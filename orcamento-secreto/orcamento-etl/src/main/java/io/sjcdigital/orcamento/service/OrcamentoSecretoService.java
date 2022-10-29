package io.sjcdigital.orcamento.service;

import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.transaction.Transactional;

import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.quarkus.runtime.annotations.RegisterForReflection;
import io.sjcdigital.orcamento.model.entity.Emendas;
import io.sjcdigital.orcamento.model.pojo.EmendasPojo;
import io.sjcdigital.orcamento.model.repository.EmendasRepository;
import io.sjcdigital.orcamento.resource.client.EmendaClient;
import io.sjcdigital.orcamento.utils.ArquivoOrcamento;
import io.sjcdigital.orcamento.utils.Constantes;
import io.sjcdigital.orcamento.utils.FileUtil;

/**
 * @author Pedro Hos <pedro-hos@outlook.com>
 *
 */

@ApplicationScoped
@Named("orcamentoBean")
@RegisterForReflection
public class OrcamentoSecretoService extends PortalTransparencia {

    private static final Logger LOGGER = LoggerFactory.getLogger(OrcamentoSecretoService.class);

    @Inject
    EmendasRepository emendasRepository;
    
    @Inject
    DocumentoRelacionadoService documentosRelacionadosService;
    
    @Transactional
    public void salvaTodasEmendasJson() {
        
        emendasRepository.findAll().stream().forEach(e -> {
            String filePath = Constantes.DATA_JSON_PATH + e.ano + "/";
            String fileName = e.id + "-" + e.numeroEmenda;
            e.documentos.removeIf(d -> d.getProcessado() == false);
            FileUtil.salvaJSON(e, filePath, fileName);
        });
        
        ArquivoOrcamento.criaArquivoEmendas();
        
        LOGGER.info("Todos arquivos salvos em " + Constantes.DATA_JSON_PATH);
    }

    @Transactional
    public void processaEmenda(EmendasPojo pojo) {
        
        LOGGER.info("Processesando Emendas ...");

        try {
            
            List<Emendas> emendas = Emendas.fromEmendaPojo(pojo);
            emendasRepository.persist(emendas);

        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
        
    }
    
    public void buscaPorPalavraChave(String palavraChave) {

        try {

            int quantidade = recuperaQuantidadeDeEmendas();
            EmendasPojo emendas = buscaEmendas(0, quantidade, palavraChave);
            processaEmenda(emendas);

        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }

    }

    public void buscaTodasEmendasRelator() {

        try {
            
            int quantidade = recuperaQuantidadeDeEmendas();
            EmendasPojo emendas = buscaEmendas(0, quantidade);
            processaEmenda(emendas);
            
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }

    }
    
    /**
     * @return
     */
    private int recuperaQuantidadeDeEmendas() {
        
        try {
            
            String recordsTotal = buscaEmendas(0, 1).getRecordsTotal();
            return Integer.valueOf(recordsTotal);
            
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
        
        return EmendaClient.TAMANHO_PAGINA;
    }
    
    protected EmendasPojo buscaEmendas(final int offset, final int tamanhoPagina, final String palavraChave) {
        ResteasyWebTarget target = getTarget();
        EmendaClient proxy = target.proxy(EmendaClient.class);
        EmendasPojo emendas = proxy.getEmendas(offset, EmendaClient.PAGINACAO_SIMPLES, tamanhoPagina,
                EmendaClient.DIRECAO_ORDENACAO, EmendaClient.COLUNA_ORDENACAO, EmendaClient.ANO_DE,
                EmendaClient.ANO_ATE, EmendaClient.AUTOR, palavraChave, EmendaClient.COLUNAS_SELECIONADAS);
        target.getResteasyClient().close();
        return emendas;
    }

    protected EmendasPojo buscaEmendas(final int offset, final int tamanhoPagina) {
        ResteasyWebTarget target = getTarget();
        EmendaClient proxy = target.proxy(EmendaClient.class);
        EmendasPojo emendas = proxy.getEmendas(offset, EmendaClient.PAGINACAO_SIMPLES, tamanhoPagina,
                EmendaClient.DIRECAO_ORDENACAO, EmendaClient.COLUNA_ORDENACAO, EmendaClient.ANO_DE,
                EmendaClient.ANO_ATE, EmendaClient.AUTOR, EmendaClient.COLUNAS_SELECIONADAS);
        target.getResteasyClient().close();
        return emendas;
    }

    protected EmendasPojo buscaEmendas(final int offset) {
        ResteasyWebTarget target = getTarget();
        EmendaClient proxy = target.proxy(EmendaClient.class);
        EmendasPojo emendas = proxy.getEmendas(offset, EmendaClient.PAGINACAO_SIMPLES, EmendaClient.TAMANHO_PAGINA,
                EmendaClient.DIRECAO_ORDENACAO, EmendaClient.COLUNA_ORDENACAO, EmendaClient.ANO_DE,
                EmendaClient.ANO_ATE, EmendaClient.AUTOR, EmendaClient.COLUNAS_SELECIONADAS);
        target.getResteasyClient().close();
        return emendas;
    }

}
