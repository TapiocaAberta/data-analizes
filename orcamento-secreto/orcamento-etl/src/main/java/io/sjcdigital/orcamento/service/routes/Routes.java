package io.sjcdigital.orcamento.service.routes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;

import io.sjcdigital.orcamento.model.entity.Documentos;
import io.sjcdigital.orcamento.model.entity.Emendas;

/**
 * @author Pedro Hos <pedro-hos@outlook.com>
 *
 */

@ApplicationScoped
public class Routes extends RouteBuilder {
    
    @Override
    public void configure() throws Exception {
        
        //select id, codigoEmenda from emendas where muitosDocumentos = true order by quantidadedocumentos asc;
        
        from("timer://selectDocumento?fixedRate=true&period=120000") //every 2 min
            .setBody(constant("select id, codigoEmenda from emendas where processado = false and "
                    + "processando = false and "
                    + "muitosDocumentos = false "
                    + "limit 30;"))
            .to("jdbc:default")
            .process(new Processor() {
                @Override
                public void process(Exchange exchange) throws Exception {
                    
                    @SuppressWarnings("unchecked")
                    List<HashMap<String, Object>> body = (List<HashMap<String, Object>>) exchange.getIn().getBody(List.class);
                    
                    if(!body.isEmpty()) {
                        
                        List<Emendas> emendas = new ArrayList<>();
                        
                        body.forEach(b -> {
                            String id =  String.valueOf(b.get("id"));
                            String codigoEmenda = String.valueOf(b.get("codigoemenda"));
                            emendas.add(new Emendas(Long.parseLong(id), codigoEmenda));
                        });
                        
                        exchange.getIn().setBody(emendas);
                        
                    }
                }
            })
            .to("bean:documentosRelacionadosBean?method=buscaDocumentosRelacionado(${body})")
        ;
        
        from("timer://selectDocumento?fixedRate=true&period=60000") //every 1min
            .setBody(constant("select id, fase, codigoDocumento from documentos where processado = false "
                                                                                    + "and processando = false "
                                                                                    + "and pgdetalhesnotfound = false "
                                                                                    + "limit 2000;"))
            .to("jdbc:default")
            .process(new Processor() {
                @Override
                public void process(Exchange exchange) throws Exception {
                    
                    @SuppressWarnings("unchecked")
                    List<HashMap<String, Object>> body = (List<HashMap<String, Object>>) exchange.getIn().getBody(List.class);
                    
                    if(!body.isEmpty()) {
                        
                        List<Documentos> documentos = new ArrayList<>();
                        
                        body.forEach(b -> {
                            documentos.add(new Documentos((String) b.get("fase"), (String) b.get("codigodocumento")));
                        });
                        
                        exchange.getIn().setBody(documentos);
                        
                    }
                }
            })
            .to("bean:detalhesDocumentoBean?method=salvaPaginaDetalhes(${body})")
           ;
        
    }

}
