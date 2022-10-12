package io.sjcdigital.orcamento.controller;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.ws.rs.core.UriBuilder;

import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;
import org.jboss.resteasy.client.jaxrs.internal.ResteasyClientBuilderImpl;

import io.sjcdigital.orcamento.model.entity.Documentos;
import io.sjcdigital.orcamento.model.pojo.DataPojo;
import io.sjcdigital.orcamento.model.pojo.DocumentosRelacionadosPojo;
import io.sjcdigital.orcamento.resource.client.DocumentosRelacionadosClient;

/**
 * @author Pedro Hos <pedro-hos@outlook.com>
 *
 */
public class DocumentoRelacionadoController {
    
    public static final String PATH = "https://www.portaltransparencia.gov.br";
    public static final String DOCUMENTO_PATH = PATH + "/despesas/documento/";
    public DetalhesDocumentoController detalhesDoc;
    
    public DocumentoRelacionadoController() {
        super();
        detalhesDoc = new DetalhesDocumentoController();
    }

    public static void main(String[] args) {
        DocumentoRelacionadoController c = new DocumentoRelacionadoController();
        DocumentosRelacionadosPojo docsRelacionads = c.buscaDocumentos(200, "202181000674");
        System.out.println(docsRelacionads.getData().size());
    }
    
    public List <Documentos> buscaTodosDocumentosRelacionados(final String codigoEmenda) {
        
        int offset = 0;
        int qntPag = 100;
        
        List <Documentos> documentos = new ArrayList<>();
        
        while(true) {
            
            DocumentosRelacionadosPojo docRelacionado = buscaDocumentos(offset, codigoEmenda);
            
            if(docRelacionado.getData().size() == 0) {
                break;
            }
            
            documentos.addAll(criaDocumentos(docRelacionado.getData()));
            
            offset = offset + qntPag;
            
        }
        
        return documentos;
    }
    
    /**
     * @param data
     * @return
     */
    private Collection<? extends Documentos> criaDocumentos(List<DataPojo> data) {
        
        List <Documentos> documentos = new ArrayList<>();
        
        data.forEach(d -> {
            
            Documentos documento = new Documentos();
            documento.data = d.getData();
            documento.fase = d.getFase();
            documento.codigoDocumento = d.getCodigoDocumento();
            documento.codigoDocumentoResumido = d.getCodigoDocumentoResumido();
            documento.especieTipo = d.getEspecieTipo();
            
            String detalheURL = pegaURLDocumento(documento.fase, documento.codigoDocumento);
            
            detalhesDoc.preencheDetalhes(detalheURL, documento);
            
            documentos.add(documento);
        });
        
        return documentos;
    }

    public DocumentosRelacionadosPojo buscaDocumentos(final int offset, final String codigoEmenda) {
        DocumentosRelacionadosClient proxy = getTarget().proxy(DocumentosRelacionadosClient.class);
        return proxy.pegaDocs(offset, codigoEmenda, false, 100, "asc", "data");
    }
    
    private static ResteasyWebTarget getTarget() {
        ResteasyClient client = new ResteasyClientBuilderImpl().build();
        ResteasyWebTarget target = client.target(UriBuilder.fromPath(PATH));
        return target;
    }
    
    private static String pegaURLDocumento(final String fase, final String codigoDocumento) {
        
        switch (fase) {
        case "Pagamento":
            return DOCUMENTO_PATH + "pagamento/" + codigoDocumento;
        case "Empenho":
            return DOCUMENTO_PATH + "empenho/" + codigoDocumento;
        case "Liquidação":
            return DOCUMENTO_PATH + "liquidacao/" + codigoDocumento;
        default:
            return "";
        }
        
    }

}
