package io.sjcdigital.orcamento.controller;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.opencsv.CSVParser;
import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.exceptions.CsvException;

import io.sjcdigital.orcamento.model.entity.Documentos;
import io.sjcdigital.orcamento.model.entity.Emendas;

/**
 * @author Pedro Hos <pedro-hos@outlook.com>
 *
 */
public class OrcamentoSecretoController {

    private static final Logger LOGGER = LoggerFactory.getLogger(OrcamentoSecretoController.class);
    
    private static final String PATH = "/home/pesilva/workspace/code/pessoal/data-analizes/orcamento-secreto/";
    private static final String EMENDAS_CSV = PATH + "data/emendas.csv";

    public static void main(String[] args) {
        System.out.println("Iniciando ...");
        montaEmendasDeRelator();
    }

    /**
     * @param emenda
     */
    private static void salvaJSON(Emendas emenda) {
        String directoryName = PATH + "data-json-new/" + emenda.ano + "/";
        String fileName = emenda.id + "-" + emenda.numeroEmenda;
        createJsonFile(directoryName, fileName, emenda);
    }
    
    private static void createJsonFile(String directoryName, String arquivoNome, Emendas emendas) {
        
        try {
            createDirectoryIfDoesntExists(directoryName);
            Files.write(Paths.get(directoryName + arquivoNome + ".json"), new ObjectMapper().writeValueAsString(emendas).getBytes());
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    protected static void createDirectoryIfDoesntExists(String directoryName) {
        
        LOGGER.info("Files will be save into " + directoryName + " if you need change it, replace the 'file.path' argument on application.properties file");

        var directory = new File(directoryName);
        
        if (!directory.exists()) {
            directory.mkdirs();
        }
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
        
        DocumentoRelacionadoController docRelacionadoController = new DocumentoRelacionadoController();
        List<Documentos> relacionados = docRelacionadoController.buscaTodosDocumentosRelacionados(codigoEmenda);
        
        Emendas emenda = new Emendas(id, linha[0], linha[1], linha[2], linha[3], linha[4], linha[5], linha[6], linha[7], 
                                     linha[8], codigoEmenda, relacionados);
        
        salvaJSON(emenda);

    }

}
