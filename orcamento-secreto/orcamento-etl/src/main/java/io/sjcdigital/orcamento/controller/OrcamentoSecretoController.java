package io.sjcdigital.orcamento.controller;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

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

    /**
     * 
     */
    private static final String PATH = "/home/pesilva/workspace/code/pessoal/data-analizes/orcamento-secreto/";
    private static final Logger LOGGER = LoggerFactory.getLogger(OrcamentoSecretoController.class);
    private static final String EMENDAS_CSV = PATH + "data/emendas.csv";

    public static void main(String[] args) {
        LOGGER.info("Iniciando CSV Parser!");
        salvaJSON(readCSV());
        
    }

    /**
     * @param readCSV
     */
    private static void salvaJSON(List<Emendas> readCSV) {
        
        for (Emendas emendas : readCSV) {
            String directoryName = PATH + "data-json/" + emendas.ano + "/";
            String arquivoNome = emendas.id + "-" + "-" + emendas.numeroEmenda + "-" + emendas.numeroEmenda;
            createJsonFile(directoryName, arquivoNome, emendas);
        }
        
    }
    
    private static void createJsonFile(String directoryName, String arquivoNome, Emendas emendas) {
        
        try {
            
            Files.write(Paths.get(directoryName + arquivoNome + ".json"), new ObjectMapper().writeValueAsString(emendas).getBytes());
            
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    

    private static List<Emendas> readCSV() {

        List<Emendas> emendas = new ArrayList<>();

        try {

            FileReader filereader = new FileReader(EMENDAS_CSV);
            CSVParser parser = new CSVParserBuilder().withSeparator(';').build();
            CSVReader csvReader = new CSVReaderBuilder(filereader).withCSVParser(parser).withSkipLines(1).build();

            List<String[]> readAll = csvReader.readAll();
            
            for(int i = 0; i < readAll.size(); i++) {
                String[] linha = readAll.get(i);
                emendas.add(criaEmenda(i, linha));
            }

        } catch (FileNotFoundException e) {
            LOGGER.error(e.getMessage());
        } catch (IOException e) {
            LOGGER.error(e.getMessage());
        } catch (CsvException e) {
            LOGGER.error(e.getMessage());
        }

        return emendas;

    }

    /**
     * @param i 
     * @param linha
     * @param contador
     * @return
     */
    private static Emendas criaEmenda(long i, String[] linha) {

        String codigoEmenda = linha[0] + "8100" + linha[2];
        
        System.out.println("Buscando dados para emenda " + codigoEmenda);
        
        DocumentoRelacionadoController docRelacionadoController = new DocumentoRelacionadoController();
        List<Documentos> relacionados = docRelacionadoController.buscaTodosDocumentosRelacionados(codigoEmenda);
        
        Emendas emenda = new Emendas(i, linha[0], linha[1],
                linha[2], linha[3], linha[4], linha[5], linha[6], linha[7], linha[8], codigoEmenda, relacionados);

        return emenda;
    }

}
