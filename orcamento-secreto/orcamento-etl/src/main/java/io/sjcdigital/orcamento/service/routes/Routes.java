package io.sjcdigital.orcamento.service.routes;

import javax.enterprise.context.ApplicationScoped;

import org.apache.camel.builder.RouteBuilder;

import io.sjcdigital.orcamento.model.entity.Favorecido;
import io.sjcdigital.orcamento.service.routes.processors.DocumentosProcessor;
import io.sjcdigital.orcamento.service.routes.processors.EmendasProcessor;
import io.sjcdigital.orcamento.service.routes.processors.FavorecidoProcessor;

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
        
        from("timer://selectDocumento?fixedRate=true&period=40000")
            .setBody(constant("select id, fase, codigoDocumento from documentos where processado = false "
                                                                                    + "and processando = false "
                                                                                    + "and pgDetalhesNotFound = false "
                                                                                    + "limit 300;"))
            .to("jdbc:default")
            .process(new DocumentosProcessor())
            .to("bean:detalhesDocumentoBean?method=salvaPaginaDetalhes(${body})")
           ;
        
        from("timer://updateFavorecido?fixedRate=true&period=40000")
        .setBody(constant("select id from favorecido where tipo = '" + Favorecido.PESSOA_JURIDICA + "' "
                + "and processado = false "
                + "and LENGTH(docfavorecido) = 18 "
                + "limit 100"))
        .to("jdbc:default")
        .process(new FavorecidoProcessor())
        .to("bean:favorecidoBean?method=atualizaPessoaFisicaInfos(${body})")
       ;
       **/
        
    }

}
