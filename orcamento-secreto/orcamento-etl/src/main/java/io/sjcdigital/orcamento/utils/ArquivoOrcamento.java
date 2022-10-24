package io.sjcdigital.orcamento.utils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.sjcdigital.orcamento.model.entity.Emendas;
import io.sjcdigital.orcamento.model.pojo.ArquivosPojo;

public class ArquivoOrcamento {

    private static final String PATH = "/home/pesilva/workspace/code/pessoal/data-analizes/orcamento-secreto/data-json/";

    public static void main(String[] args) {

        List<String> anos = Arrays.asList("2020", "2021", "2022");
        List<ArquivosPojo> arquivosPojo = new ArrayList<>();
        ObjectMapper mapper = new ObjectMapper();

        anos.forEach(ano -> {

            System.out.println("Coletando " + ano + "\n");

            Path path = Paths.get(PATH + ano);

            try {

                listFiles(path).forEach(arquivo -> {

                    try {

                        Emendas emenda = mapper.readValue(arquivo.toFile(), Emendas.class);
                        String nomeArquivo = "/" + emenda.ano + "/" + arquivo.getFileName().toString();
                        arquivosPojo.add(
                                new ArquivosPojo(emenda.codigoEmenda, emenda.funcao, emenda.subfuncao, nomeArquivo));

                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                });

            } catch (IOException e) {
                e.printStackTrace();
            }

        });

        System.out.println(arquivosPojo.size());

        salvaJSON(arquivosPojo);
    }

    private static void salvaJSON(List<ArquivosPojo> arquivosPojo) {
        String fileName = "arquivos";
        createJsonFile(PATH, fileName, arquivosPojo);
    }

    private static void createJsonFile(String directoryName, String arquivoNome, List<ArquivosPojo> arquivosPojo) {

        try {
            Files.write(Paths.get(directoryName + arquivoNome + ".json"),
                    new ObjectMapper().writeValueAsString(arquivosPojo).getBytes());
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static List<Path> listFiles(Path path) throws IOException {

        List<Path> result;
        try (Stream<Path> walk = Files.walk(path)) {
            result = walk.filter(Files::isRegularFile).collect(Collectors.toList());
        }
        return result;

    }
}