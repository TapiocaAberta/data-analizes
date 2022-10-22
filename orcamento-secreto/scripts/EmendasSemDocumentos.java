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
import javax.json.JsonStructure;
import javax.json.JsonValue.ValueType;

public class EmendasSemDocumentos {

    record Documento(String arquivo, JsonStructure json) {
    }

    private static final String DOCUMENTOS_PROP = "documentos";

    private static final String DOCUMENTOS_DIR = "../data-json/";
    private static final Path DOCUMENTOS_PATH = Paths.get(DOCUMENTOS_DIR);

    private static final String SAIDA = "emendas_sem_documentos.txt";
    private static final Path SAIDA_PATH = Paths.get(SAIDA);

    public static void main(String... args) throws IOException {
	final var qtdeEmendas = args.length == 0 ? 0 : Integer.parseInt(args[0]);
        final var jsonReaderFactory = Json.createReaderFactory(Collections.emptyMap());
        final var emendas = Files.walk(DOCUMENTOS_PATH)
                .filter(p -> p.toString().endsWith(".json"))
                .map(p -> {
                    try (var is = Files.newInputStream(p)) {
                        var reader = jsonReaderFactory.createReader(is);
                        return new Documento(p.toString(), reader.read());
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                })
                .filter(doc -> doc.json.getValueType() == ValueType.OBJECT
                        && doc.json.asJsonObject().getJsonArray(DOCUMENTOS_PROP).size() == qtdeEmendas) 
                .peek(doc -> System.out.println(doc.json.asJsonObject().getString("codigoEmenda")))   
                .map(doc -> doc.arquivo)            
                .collect(Collectors.joining(System.lineSeparator()));
        Files.deleteIfExists(SAIDA_PATH);
        Files.writeString(SAIDA_PATH, emendas);

    }
}
