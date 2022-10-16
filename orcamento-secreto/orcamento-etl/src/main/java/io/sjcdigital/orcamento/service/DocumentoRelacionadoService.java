package io.sjcdigital.orcamento.service;

import static io.sjcdigital.orcamento.utils.PortalTransparenciaConstantes.DOCUMENTO_URL;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;

import io.sjcdigital.orcamento.model.entity.Documentos;
import io.sjcdigital.orcamento.model.pojo.DocumentosDataPojo;
import io.sjcdigital.orcamento.model.pojo.DocumentosRelacionadosPojo;
import io.sjcdigital.orcamento.resource.client.DocumentosRelacionadosClient;

/**
 * @author Pedro Hos <pedro-hos@outlook.com>
 *
 */
public class DocumentoRelacionadoService extends PortalTransparencia {
    
    public DetalhesDocumentoService detalhesDoc;
    
    public DocumentoRelacionadoService() {
        detalhesDoc = new DetalhesDocumentoService();
    }
    
    public List <Documentos> buscaTodosDocumentosRelacionados(final String codigoEmenda) {
        
        System.out.println("Iniciando Busca de Documentos para " + codigoEmenda);
        int offset = 0;
        List <Documentos> documentos = new ArrayList<>();
        
        while(true) {
            
            DocumentosRelacionadosPojo docRelacionado = buscaDocumentos(offset, codigoEmenda);
            
            if(docRelacionado.getData().size() == 0) {
                break;
            }
            
            documentos.addAll(criaDocumentos(docRelacionado.getData()));
            offset = offset +  DocumentosRelacionadosClient.TAMANHO_PAGINA;
        }
        
        System.out.println(Thread.currentThread().getName() + " Finalizando Processo! " + codigoEmenda);
        
        return documentos;
    }
    
    /**
     * @param data
     * @return
     */
    private Collection<? extends Documentos> criaDocumentos(List<DocumentosDataPojo> data) {
        
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
        DocumentosRelacionadosPojo pojo = proxy.pegaDocs( offset, codigoEmenda, 
                                                          DocumentosRelacionadosClient.PAGINACAO_SIMPLES, 
                                                          DocumentosRelacionadosClient.TAMANHO_PAGINA, 
                                                          DocumentosRelacionadosClient.DIRECAO_ORDENACAO, 
                                                          DocumentosRelacionadosClient.COLUNA_ORDENACAO );
        target.getResteasyClient().close();
        return pojo;
    }
    
    
    private static String pegaURLDocumento(final String fase, final String codigoDocumento) {
        
        switch (fase) {
        case "Pagamento":
            return DOCUMENTO_URL + "pagamento/" + codigoDocumento;
        case "Empenho":
            return DOCUMENTO_URL + "empenho/" + codigoDocumento;
        case "Liquidação":
            return DOCUMENTO_URL + "liquidacao/" + codigoDocumento;
        default:
            return "";
        }
        
    }

}
