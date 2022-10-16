package io.sjcdigital.orcamento.service;

import static io.sjcdigital.orcamento.utils.Constantes.*;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.opencsv.CSVParser;
import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.exceptions.CsvException;

import io.sjcdigital.orcamento.model.entity.Documentos;
import io.sjcdigital.orcamento.model.entity.Emendas;
import io.sjcdigital.orcamento.model.pojo.EmendasPojo;
import io.sjcdigital.orcamento.resource.client.EmendaClient;
import io.sjcdigital.orcamento.utils.Constantes;
import io.sjcdigital.orcamento.utils.FileUtil;

/**
 * @author Pedro Hos <pedro-hos@outlook.com>
 *
 */
public class OrcamentoSecretoService extends PortalTransparencia {

    private static final Logger LOGGER = LoggerFactory.getLogger(OrcamentoSecretoService.class);
    
    public static void main(String[] args) {
        System.out.println("Iniciando ...");
        
        OrcamentoSecretoService service = new OrcamentoSecretoService();
        service.buscaTodasEmendasRelator();
        
    }
    
    public void buscaTodasEmendasRelator() {
        
        int offset = 0;
        
        while(offset < 10) {
            
            EmendasPojo emendas = buscaEmendas(offset);
            
            if(emendas.getData().isEmpty()) {
                break;
            }
            
            FileUtil.salvaJSON(emendas, ORIGINAIS_PATH + "emendas/", offset + "-" + EmendaClient.TAMANHO_PAGINA);
            offset = offset + EmendaClient.TAMANHO_PAGINA;
        }
        
    }
    
    private EmendasPojo buscaEmendas(final int offset) {
        ResteasyWebTarget target = getTarget();
        EmendaClient proxy = target.proxy(EmendaClient.class);
        EmendasPojo emendas = proxy.getEmendas( offset, EmendaClient.PAGINACAO_SIMPLES, EmendaClient.TAMANHO_PAGINA, 
                                                EmendaClient.DIRECAO_ORDENACAO, EmendaClient.COLUNA_ORDENACAO, 
                                                EmendaClient.ANO_DE, EmendaClient.ANO_ATE,  EmendaClient.AUTOR, 
                                                EmendaClient.COLUNAS_SELECIONADAS);
        target.getResteasyClient().close();
        return emendas;
    }
    
    private static void montaEmendasDeRelator() {
        
        try {

            FileReader filereader = new FileReader(EMENDAS_CSV);
            CSVParser parser = new CSVParserBuilder().withSeparator(';').build();
            CSVReader csvReader = new CSVReaderBuilder(filereader).withCSVParser(parser).withSkipLines(1).build();

            List<String[]> readAll = csvReader.readAll();
            
            ThreadPoolExecutor executor =  (ThreadPoolExecutor) Executors.newFixedThreadPool(readAll.size());
            
            for(int i = 0; i < readAll.size(); i++) {
                String[] linha = readAll.get(i);
                
                executor.submit(() -> {
                    criaEmenda(readAll.indexOf(linha), linha);
                });
                
            }

        } catch (FileNotFoundException e) {
            LOGGER.error(e.getMessage());
        } catch (IOException e) {
            LOGGER.error(e.getMessage());
        } catch (CsvException e) {
            LOGGER.error(e.getMessage());
        }

    }

    /**
     * @param id 
     * @param linha
     * @param contador
     * @return
     */
    private static void criaEmenda(long id, String[] linha) {

        String codigoEmenda = linha[0] + "8100" + linha[2];
        
        System.out.println("[" + Thread.currentThread().getName() +  "] Buscando dados para emenda " + codigoEmenda + " id " + id);
        
        DocumentoRelacionadoService docRelacionadoController = new DocumentoRelacionadoService();
        List<Documentos> relacionados = docRelacionadoController.buscaTodosDocumentosRelacionados(codigoEmenda);
        
        Emendas emenda = new Emendas(id, linha[0], linha[1], linha[2], linha[3], linha[4], linha[5], linha[6], linha[7], 
                                     linha[8], codigoEmenda, relacionados);
        
        String directoryName = PATH + "data-json-new/" + emenda.ano + "/";
        String fileName = emenda.id + "-" + emenda.numeroEmenda;
        
        FileUtil.salvaJSON(emenda, directoryName, fileName);

    }

}
