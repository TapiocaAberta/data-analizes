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
    public static final  int QUANTIDADE_POR_PAGINA = 1000;
    
    public DetalhesDocumentoController detalhesDoc;
    
    public DocumentoRelacionadoController() {
        detalhesDoc = new DetalhesDocumentoController();
    }
    
    public List <Documentos> buscaTodosDocumentosRelacionados(final String codigoEmenda) {
        
        int offset = 0;
        
        List <Documentos> documentos = new ArrayList<>();
        
        
        while(true) {
            
            DocumentosRelacionadosPojo docRelacionado = buscaDocumentos(offset, codigoEmenda);
            
            if(docRelacionado.getData().size() == 0) {
                break;
            }
            
            documentos.addAll(criaDocumentos(docRelacionado.getData()));
            
            offset = offset + QUANTIDADE_POR_PAGINA;
            
        }
        
        System.out.println(Thread.currentThread().getName() + " Finalizando Processo!");
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
            
            detalhesDoc.preencheDetalhes(pegaURLDocumento(documento.fase, documento.codigoDocumento), documento);
            
            documentos.add(documento);
        });
        
        return documentos;
    }

    public DocumentosRelacionadosPojo buscaDocumentos(final int offset, final String codigoEmenda) {
        ResteasyWebTarget target = getTarget();
        DocumentosRelacionadosClient proxy = target.proxy(DocumentosRelacionadosClient.class);
        DocumentosRelacionadosPojo pojo = proxy.pegaDocs(offset, codigoEmenda, false, QUANTIDADE_POR_PAGINA, "asc", "data");
        target.getResteasyClient().close();
        return pojo;
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
