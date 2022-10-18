///usr/bin/env jbang "$0" "$@" ; exit $?
//JAVA 16
//DEPS javax.json:javax.json-api:1.1,org.glassfish:javax.json:1.1

import java.io.IOException;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.stream.Collectors;

import javax.json.Json;

public class EmendasSemDocumentos {

    private static final String CODIGO_EMENDA_PROP = "codigoEmenda";
    private static final String DOCUMENTOS_PROP = "documentos";

    private static final String DOCUMENTOS_DIR = "../data-json/";
    private static final Path DOCUMENTOS_PATH = Paths.get(DOCUMENTOS_DIR);

    private static final String SAIDA = "emendas_sem_documentos.txt";
    private static final Path SAIDA_PATH = Paths.get(SAIDA);

    public static void main(String... args) throws IOException {
        final var jsonReaderFactory = Json.createReaderFactory(Collections.emptyMap());
        final var emendas = Files.walk(DOCUMENTOS_PATH)
                .filter(p -> p.toFile().isFile() && p.toString().endsWith("json"))
                .map(f -> {
                    try (var is = Files.newInputStream(f)) {
                        var reader = jsonReaderFactory.createReader(is);
                        return reader.readObject();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                })
                .filter(obj -> obj.getJsonArray(DOCUMENTOS_PROP).size() == 0)
                .map(obj -> obj.getString(CODIGO_EMENDA_PROP))
                .collect(Collectors.joining(System.lineSeparator()));
        Files.deleteIfExists(SAIDA_PATH);
        Files.writeString(SAIDA_PATH, emendas);

    }
}
