package io.sjcdigital.orcamento.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.transaction.Transactional;

import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.quarkus.runtime.annotations.RegisterForReflection;
import io.sjcdigital.orcamento.model.entity.Documentos;
import io.sjcdigital.orcamento.model.entity.Emendas;
import io.sjcdigital.orcamento.model.pojo.DocumentosRelacionadosPojo;
import io.sjcdigital.orcamento.model.repository.DocumentoRepository;
import io.sjcdigital.orcamento.model.repository.EmendasRepository;
import io.sjcdigital.orcamento.resource.client.DocumentosRelacionadosClient;

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
    @Inject DocumentoRepository documentoRepository;
    
    public void buscaDocumentosRelacionado(List<Emendas> emendas) {
        
        ExecutorService executor = Executors.newFixedThreadPool(30);
        emendas.forEach(emenda -> 
            executor.submit(() -> buscaDocumentosRelacionado(emenda))
        );
        
    }
    
    /**
     * @param id
     * @param codigoEmenda
     */
    @Transactional
    public void buscaDocumentosRelacionado(Emendas emendas) {
        
        Emendas emenda = emendaReporitory.findById(emendas.id);
        
        try {
            
            LOGGER.info("[INICIANDO] Busca de Documentos para " + emenda.codigoEmenda);
            
            int offset = 0;
            int quantidadeDocumentos = recuperaQuantidadeDeDocumentos(emendas.codigoEmenda);
            
            emenda.quantidadeDocumentos = quantidadeDocumentos;
            
            if(quantidadeDocumentos > 5000) {
                
                emenda.muitosDocumentos = true;
                emendaReporitory.persist(emenda);
                
            } else {
                
                emenda.processando = Boolean.TRUE;
                emendaReporitory.persist(emenda);
                
                int maxAttempt = Integer.MAX_VALUE;
                
                DocumentosRelacionadosPojo docRelacionado = null;
                
                while(offset < maxAttempt) {
                    
                    if(Objects.isNull(docRelacionado)) {
                        docRelacionado = buscaDocumentos(offset, emenda.codigoEmenda);
                        maxAttempt = Integer.valueOf(docRelacionado.getRecordsTotal());
                    } else {
                        docRelacionado.getData().addAll(buscaDocumentos(offset, emenda.codigoEmenda).getData());
                    }
                    
                    offset = offset +  DocumentosRelacionadosClient.TAMANHO_PAGINA;
                }
                
                LOGGER.info("Quantidade disponível x recuperada " + docRelacionado.getRecordsTotal() + " x " + docRelacionado.getData().size());
                processaDocumentosRelacionados(docRelacionado, emenda);
            }
            
        } catch (Exception e) {
            emenda.erro = Boolean.TRUE;
            emenda.processando = Boolean.FALSE;
            emendaReporitory.persist(emenda);
            LOGGER.error(e.getMessage());
        }
    }
    
    /**
     * @return
     */
    protected int recuperaQuantidadeDeDocumentos(final String codigoEmenda) {
        
        try {
            
            String recordsTotal = buscaDocumentos(0, 1, codigoEmenda).getRecordsTotal();
            LOGGER.info(recordsTotal + " Documentos para Emenda");
            return Integer.valueOf(recordsTotal);
            
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
        
        return DocumentosRelacionadosClient.TAMANHO_PAGINA;
    }
    
    @Transactional
    public void processaDocumentosRelacionados(DocumentosRelacionadosPojo pojo, Emendas emenda) throws Exception {
        LOGGER.info("[PROCESSANDO] Documentos Relacionados - " + emenda.codigoEmenda);
        
        List<Documentos> docs = new ArrayList<>();
        
        pojo.getData().forEach(data -> {
            
            Documentos documentos = documentoRepository.findByFaseAndCodigoDocumento(data.getFase(), data.getCodigoDocumento());
            
            if(Objects.isNull(documentos)) {
                documentos = Documentos.fromDocumentoDataPojo(data);
                documentos.emenda.add(emenda);
            }
            
            docs.add(documentos);
            documentoRepository.persist(documentos);
            
        });
        
        emenda.documentos.addAll(docs);
        emenda.processado = Boolean.TRUE;
        emenda.erro = Boolean.FALSE;
        emenda.processando = Boolean.FALSE;
        emendaReporitory.persist(emenda);
        LOGGER.info("[FINALIZADO] " + docs.size() + " documentos salvos.");
    }
    
    public DocumentosRelacionadosPojo buscaDocumentos(final int offset, final int tamanhoPagina, final String codigoEmenda) {
        ResteasyWebTarget target = getTarget();
        DocumentosRelacionadosClient proxy = target.proxy(DocumentosRelacionadosClient.class);
        DocumentosRelacionadosPojo pojo = proxy.pegaDocs( offset, codigoEmenda, 
                                                          DocumentosRelacionadosClient.PAGINACAO_SIMPLES, 
                                                          tamanhoPagina, 
                                                          DocumentosRelacionadosClient.DIRECAO_ORDENACAO, 
                                                          DocumentosRelacionadosClient.COLUNA_ORDENACAO );
        target.getResteasyClient().close();
        return pojo;
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
