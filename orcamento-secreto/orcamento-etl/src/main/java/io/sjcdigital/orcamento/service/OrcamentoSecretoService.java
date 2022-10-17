package io.sjcdigital.orcamento.service;

import static io.sjcdigital.orcamento.utils.Constantes.EMENDAS_PATH;

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
            FileUtil.salvaJSON(e, filePath, fileName);
        });
    }

    @Transactional
    public void processaEmenda(EmendasPojo pojo) {
        
        LOGGER.info("Processesando Emendas ..." + pojo.getData().size());

        try {
            List<Emendas> emendas = Emendas.fromEmendaPojo(pojo);
            emendasRepository.persist(emendas);
            documentosRelacionadosService.buscaTodosDocumentosRelacionados(emendas);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void buscaTodasEmendasRelator() {

        int offset = 0;

        while (offset < 10) {

            EmendasPojo emendas = buscaEmendas(offset);

            if (emendas.getData().isEmpty()) {
                System.out.println("Lista de emendas Vazia");
                break;
            }

            FileUtil.salvaJSON(emendas, EMENDAS_PATH, offset + "-" + EmendaClient.TAMANHO_PAGINA);
            offset = offset + EmendaClient.TAMANHO_PAGINA;
        }

    }

    private EmendasPojo buscaEmendas(final int offset) {
        ResteasyWebTarget target = getTarget();
        EmendaClient proxy = target.proxy(EmendaClient.class);
        EmendasPojo emendas = proxy.getEmendas(offset, EmendaClient.PAGINACAO_SIMPLES, EmendaClient.TAMANHO_PAGINA,
                EmendaClient.DIRECAO_ORDENACAO, EmendaClient.COLUNA_ORDENACAO, EmendaClient.ANO_DE,
                EmendaClient.ANO_ATE, EmendaClient.AUTOR, EmendaClient.COLUNAS_SELECIONADAS);
        target.getResteasyClient().close();
        return emendas;
    }

}
