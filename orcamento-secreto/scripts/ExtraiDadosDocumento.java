
///usr/bin/env jbang "$0" "$@" ; exit $?
//JAVA 16
//DEPS com.fasterxml.jackson.core:jackson-core:2.12.3,com.fasterxml.jackson.core:jackson-databind:2.12.3
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.regex.Pattern;

import com.fasterxml.jackson.databind.ObjectMapper;

public class ExtraiDadosDocumento {

    private static final String VALOR_PAGAMENTO_INVÁLIDO = "-1";
    private static final String CAMPOS_NULOS = "Nulo";
    private static final String DOCUMENTOS_DIR = "../data-json/";
    private static final Path DOCUMENTOS_PATH = Paths.get(DOCUMENTOS_DIR);

    private static final Path SAIDA_DIR_PATH = Paths.get("../resumo_documentos");

    private static final Tipo TIPO_PADRAO = Tipo.Pagamento;

    private static final ObjectMapper MAPPER = new ObjectMapper();

    enum Tipo {
        Empenho("Empenho"),
        Pagamento("Pagamento"),
        Liquidacao("Liquidação");

        String nome;

        Tipo(String nome) {
            this.nome = nome;
        }

        static Tipo porNome(String nome) {
            return Arrays.stream(values())
                    .filter(t -> t.nome.toLowerCase().equals(nome.toLowerCase()) ||
                            t.name().toLowerCase().equals(nome.toLowerCase()))
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException(
                            "Nome do tipo não é válido: " + nome + ". Nomes válidos são: "
                                    + Arrays.toString(values())));
        }

        public String getNome() {
            return nome;
        }

        @Override
        public String toString() {
            return nome;
        }

    }

    record Favorecido(String docFavorecido, String nome, String url) {
    }

    record OrgaoPagador(String orgaoSuperiorCod, String orgaoSuperiorNome, String entidadeVinculadaCod,
            String entidadeVinculadaNome, String unidadeGestoraCod, String unidadeGestoraNome,
            String gestaoCod,
            String gestaoNome,
            String url) {
    }

    record Documento(String data, String fase, String codigoDocumento, String codigoDocumentoResumido,
            String especieTipo, String observacao, String tipo, String valorDocumento, String descricao,
            Favorecido favorecido, OrgaoPagador orgaopagador) {
    }

    record Emenda(String id, String ano, String autor, String numeroEmenda, String localidade, String funcao,
            String subfuncao, String empenhado, String liquido, String pago, String codigoEmenda,
            List<Documento> documentos) {
    }

    public static void main(String... args) throws IOException {
        final var tipo = args.length > 0 ? Tipo.porNome(args[0]) : TIPO_PADRAO;

        Predicate<Documento> filtroFavorecidoValido = doc -> doc.favorecido != null
                && doc.favorecido.docFavorecido != null;

        Predicate<Documento> filtroPrefeitura = doc -> (doc.favorecido.nome.startsWith("PREFEITURA")
                || doc.favorecido.nome.startsWith("MUNICIPIO"));

        Predicate<Documento> filtroEstado = doc -> doc.favorecido.nome.startsWith("ESTADO D");

        Predicate<Documento> filtroPessoaFisica = doc -> doc.favorecido.docFavorecido.startsWith("*") &&
                doc.favorecido.docFavorecido.endsWith("*");

        Predicate<Documento> filtroPessoaJuridica = doc -> (!filtroPrefeitura.test(doc)) && (!filtroEstado.test(doc))
                && Pattern
                        .matches("^\\d{2}\\.\\d{3}\\.\\d{3}\\/\\d{4}\\-\\d{2}$",
                                doc.favorecido.docFavorecido);
        Predicate<Documento> filtroOutros = doc -> !filtroFavorecidoValido.test(doc) ||
                !(filtroPessoaFisica.test(doc) ||
                        filtroPrefeitura.test(doc) ||
                        filtroPessoaJuridica.test(doc));

        final var todosDocumentos = Files.walk(DOCUMENTOS_PATH)
                .filter(p -> p.toString().endsWith(".json"))
                .map(p -> {
                    try (var is = Files.newInputStream(p)) {
                        return MAPPER.readValue(is, Emenda.class);
                    } catch (Exception e) {
                        System.out.println("Ignorando " + p.toFile().getName());
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .flatMap(e -> e.documentos().stream())
                .filter(e -> tipo.getNome().equals(e.fase))
                .distinct()
                .toList();
        final var documentosPrefeituras = todosDocumentos.stream()
                .filter(filtroFavorecidoValido.and(filtroPrefeitura))
                .toList();
        final var documentosEstados = todosDocumentos.stream()
                .filter(filtroFavorecidoValido.and(filtroEstado))
                .toList();
        final var documentosPessoaFisica = todosDocumentos.stream()
                .filter(filtroFavorecidoValido.and(filtroPessoaFisica))
                .toList();
        final var documentosPessoaJuridica = todosDocumentos.stream()
                .filter(filtroFavorecidoValido.and(filtroPessoaJuridica))
                .toList();
        final var documentosOutros = todosDocumentos.stream()
                .filter(filtroOutros)
                .toList();
        System.out.println("Há " + todosDocumentos.size() + " documentos");
        System.out.println("Há " + documentosEstados.size() + " documentos de estados");
        System.out.println("Há " + documentosPrefeituras.size() + " documentos de prefeituras");
        System.out.println("Há " + documentosPessoaFisica.size() + " documentos de pessoas físicas");
        System.out.println("Há " + documentosPessoaJuridica.size() + " documentos de pessoas jurídicas");
        System.out.println("Há " + documentosOutros.size() + " documentos de outros");

        Files.createDirectories(SAIDA_DIR_PATH);
        Map.of(
                "prefeituras", documentosPrefeituras,
                "estados", documentosEstados,
                "pessoas_fisicas", documentosPessoaFisica,
                "pessoas_juridicas", documentosPessoaJuridica,
                "outros", documentosOutros)
                .forEach((particao, lista) -> {
                    if (lista.size() > 10000) {

                    }
                    var arquivoSaida = SAIDA_DIR_PATH
                            .resolve("documentos_" + tipo.name().toLowerCase() + "_" + particao + ".json");
                    try {
                        var listaArray = toArray(lista);
                        var json = MAPPER.writeValueAsString(listaArray);
                        Files.deleteIfExists(arquivoSaida);
                        Files.writeString(arquivoSaida, json);
                        System.out.println("Arquivo " + arquivoSaida + " salvo com sucesso!");
                    } catch (Exception e) {
                        System.out.println(
                                "Erro escrevendo documento " + arquivoSaida.toFile().getName() + ": " + e.getMessage());
                    }
                });

    }

    final static String[] CAMPOS_SAIDA = {
            "",
    };

    private static String[][] toArray(List<Documento> lista) {
        final var saida = new String[lista.size()][];
        for (int i = 0; i < lista.size(); i++) {
            var doc = lista.get(i);
            for (int j = 0; j < CAMPOS_SAIDA.length; j++) {
                saida[i] = new String[] {
                        doc.data,
                        doc.codigoDocumento,
                        doc.especieTipo,
                        doc.observacao,
                        doc.tipo,
                        doc.valorDocumento,
                        doc.valorDocumento == null || doc.valorDocumento.length() == 0 ? VALOR_PAGAMENTO_INVÁLIDO
                                : doc.valorDocumento.replace("R$", "")
                                        .replace(".", "")
                                        .replace(" ", "")
                                        .replace(",", ".")
                                        .replaceAll("-$", VALOR_PAGAMENTO_INVÁLIDO),
                        doc.descricao,
                        doc.favorecido == null ? CAMPOS_NULOS : doc.favorecido.docFavorecido,
                        doc.favorecido == null ? CAMPOS_NULOS : doc.favorecido.nome,
                        doc.orgaopagador == null ? CAMPOS_NULOS : doc.orgaopagador.entidadeVinculadaNome,
                        doc.orgaopagador == null ? CAMPOS_NULOS : doc.orgaopagador.gestaoNome,
                        doc.orgaopagador == null ? CAMPOS_NULOS : doc.orgaopagador.orgaoSuperiorNome,
                        doc.orgaopagador == null ? CAMPOS_NULOS : doc.orgaopagador.unidadeGestoraNome
                };
            }
        }
        return saida;
    }

}
