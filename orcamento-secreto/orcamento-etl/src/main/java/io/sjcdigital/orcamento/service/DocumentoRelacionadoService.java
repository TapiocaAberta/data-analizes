package io.sjcdigital.orcamento.service;

import static io.sjcdigital.orcamento.utils.Constantes.DOC_RELACIONADOS_PATH;

import java.util.List;
import java.util.Objects;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.transaction.Transactional;

import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.quarkus.runtime.annotations.RegisterForReflection;
import io.sjcdigital.orcamento.model.entity.Emendas;
import io.sjcdigital.orcamento.model.pojo.DocumentosRelacionadosPojo;
import io.sjcdigital.orcamento.model.repository.EmendasRepository;
import io.sjcdigital.orcamento.resource.client.DocumentosRelacionadosClient;
import io.sjcdigital.orcamento.utils.Constantes;
import io.sjcdigital.orcamento.utils.FileUtil;

/**
 * @author Pedro Hos <pedro-hos@outlook.com>
 *
 */
@ApplicationScoped
@Named("documentosRelacionadosBean")
@RegisterForReflection
public class DocumentoRelacionadoService extends PortalTransparencia {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(DocumentoRelacionadoService.class);
    
    @Inject DetalhesDocumentoService detalhesDoc;
    @Inject EmendasRepository emendaReporitory;
    
    public void buscaTodosDocumentosRelacionados(final List<Emendas> emendas) {
        emendas.forEach(e -> buscaDocumentosRelacionado(e.id, e.codigoEmenda));
    }
    
    /**
     * @param id
     * @param codigoEmenda
     */
    private void buscaDocumentosRelacionado(Long idEmenda, String codigoEmenda) {
        
        LOGGER.info("Iniciando Busca de Documentos para " + codigoEmenda + " id Emenda: " + idEmenda);
        
        int offset = 0;
        
        while(true) {
            
            DocumentosRelacionadosPojo docRelacionado = buscaDocumentos(offset, codigoEmenda);
            docRelacionado.setIdEmenda(idEmenda);
            
            if(docRelacionado.getData().size() == 0) {
                break;
            }
            
            FileUtil.salvaJSON(docRelacionado, DOC_RELACIONADOS_PATH, idEmenda + "-" + codigoEmenda);
            offset = offset +  DocumentosRelacionadosClient.TAMANHO_PAGINA;
        }
    }
    
    @Transactional
    public void processaDocumentosRelacionados(DocumentosRelacionadosPojo pojo, String fileName) {
        LOGGER.info("Processando Documentos Relacionados");
        
        Emendas emenda = emendaReporitory.findById(pojo.getIdEmenda());
        
        if(Objects.isNull(emenda)) {
            LOGGER.info("[RETRY] Emenda não encontrada - " + pojo.getIdEmenda());
            FileUtil.salvaJSON(pojo, DOC_RELACIONADOS_PATH + Constantes.RETRY_FOLDER, fileName);
        } else {
            emenda.documentos.addAll(Emendas.criaDocumentos(pojo, emenda));
            emendaReporitory.persistAndFlush(emenda);
            detalhesDoc.salvaPaginaDetalhes(pojo);
        }
        
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
    
    
    

}
