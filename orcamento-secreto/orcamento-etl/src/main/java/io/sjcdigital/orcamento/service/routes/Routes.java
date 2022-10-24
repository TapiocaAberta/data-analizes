package io.sjcdigital.orcamento.service.routes;

import javax.enterprise.context.ApplicationScoped;

import org.apache.camel.builder.RouteBuilder;

import io.sjcdigital.orcamento.service.routes.processors.DocumentosProcessor;
import io.sjcdigital.orcamento.service.routes.processors.EmendasProcessor;

/**
 * @author Pedro Hos <pedro-hos@outlook.com>
 *
 */

@ApplicationScoped
public class Routes extends RouteBuilder {
    
    @Override
    public void configure() throws Exception {
        
        
        /**
        from("timer://selectMuitosDocumento?fixedRate=true&period=120000")
        .setBody(constant("select id, codigoEmenda from emendas where processado = false and "
                + "processando = false and "
                + "muitosDocumentos = true "
                + "order by quantidadedocumentos asc "
                + "limit 1;"))
        .to("jdbc:default")
        .process(new EmendasProcessor())
        .to("bean:documentosRelacionadosBean?method=salvaMuitosDocumentosRelacionado");
        
        from("timer://selectDocumento?fixedRate=true&period=120000") //every 2 min
            .setBody(constant("select id, codigoEmenda from emendas where processado = false and "
                    + "processando = false and "
                    + "muitosDocumentos = false "
                    + "limit 30;"))
            .to("jdbc:default")
            .process(new EmendasProcessor())
            .to("bean:documentosRelacionadosBean?method=buscaDocumentosRelacionado(${body})")
        ;
        
       
        
        from("timer://selectDocumento?fixedRate=true&period=60000") //every 1min
            .setBody(constant("select id, fase, codigoDocumento from documentos where processado = false "
                                                                                    + "and processando = false "
                                                                                    + "and pgdetalhesnotfound = false "
                                                                                    + "limit 1000;"))
            .to("jdbc:default")
            .process(new DocumentosProcessor())
            .to("bean:detalhesDocumentoBean?method=salvaPaginaDetalhes(${body})")
           ;
         **/
    }

}
