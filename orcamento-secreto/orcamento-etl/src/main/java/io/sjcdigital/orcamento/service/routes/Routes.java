package io.sjcdigital.orcamento.service.routes;

import javax.enterprise.context.ApplicationScoped;

import org.apache.camel.builder.RouteBuilder;

import io.sjcdigital.orcamento.model.pojo.DocumentosRelacionadosPojo;
import io.sjcdigital.orcamento.model.pojo.EmendasPojo;
import io.sjcdigital.orcamento.utils.Constantes;

/**
 * @author Pedro Hos <pedro-hos@outlook.com>
 *
 */

@ApplicationScoped
public class Routes extends RouteBuilder {
    
    @Override
    public void configure() throws Exception {
        
        from("file:" + Constantes.EMENDAS_PATH + "?delay=100")
            .log("Lendo Emendas de ${header.CamelFileName}")
            .unmarshal().json(EmendasPojo.class)
            .to("bean:orcamentoBean?method=processaEmenda")
            .marshal().json(EmendasPojo.class)
            .to("file:" + Constantes.EMENDAS_PATH + Constantes.EXECUTADO_FOLDER);
        
        from("file:" + Constantes.DOC_RELACIONADOS_PATH + "?delay=5000")
            .log("Lendo Documentos Relacionados de ${header.CamelFileName}")
            .unmarshal().json(DocumentosRelacionadosPojo.class)
            .to("bean:documentosRelacionadosBean?method=processaDocumentosRelacionados(${body}, ${header.CamelFileName})")
            .marshal().json(DocumentosRelacionadosPojo.class)
            .to("file:" + Constantes.DOC_RELACIONADOS_PATH + Constantes.EXECUTADO_FOLDER);
        
        //Retry
        from("file:" + Constantes.DOC_RELACIONADOS_PATH + Constantes.RETRY_FOLDER + "?delay=100000")
            .log("Lendo Documentos Relacionados (Retry) de ${header.CamelFileName}")
            .unmarshal().json(DocumentosRelacionadosPojo.class)
            .to("bean:documentosRelacionadosBean?method=processaDocumentosRelacionados(${body}, ${header.CamelFileName})")
            .marshal().json(DocumentosRelacionadosPojo.class)
            .to("file:" + Constantes.DOC_RELACIONADOS_PATH + Constantes.EXECUTADO_FOLDER);
        
        from("file:" + Constantes.DOC_DETALHES_PATH + "?delay=20000")
            .log("Lendo Documentos Detalhes de ${header.CamelFileName}")
            .to("bean:detalhesDocumentoBean?method=processaDetalhes(${body}, ${header.CamelFileName})")
            .to("file:" + Constantes.DOC_DETALHES_PATH + Constantes.EXECUTADO_FOLDER);
        
        //Retry
        from("file:" + Constantes.DOC_DETALHES_PATH + Constantes.RETRY_FOLDER + "?delay=200000")
            .log("Lendo Documentos Detalhes de ${header.CamelFileName}")
            .to("bean:detalhesDocumentoBean?method=processaDetalhes(${body}, ${header.CamelFileName})")
            .to("file:" + Constantes.DOC_DETALHES_PATH + Constantes.EXECUTADO_FOLDER);
        
    }

}
