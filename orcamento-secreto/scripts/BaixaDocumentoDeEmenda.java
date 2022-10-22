///usr/bin/env jbang "$0" "$@" ; exit $?
//JAVA 16
//DEPS org.jsoup:jsoup:1.15.3,com.fasterxml.jackson.core:jackson-core:2.12.3,com.fasterxml.jackson.core:jackson-databind:2.12.3

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse.BodyHandlers;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.jsoup.Jsoup;

import com.fasterxml.jackson.databind.ObjectMapper;

// CÓDIGO de autoria do Pedro HOS, modificado para rodar em um script JBANG - no futuro deveria ser consolidado no código principal
public class BaixaDocumentoDeEmenda {

        private static int QUANTIDADE_POR_PAGINA = 1000;
        private static final String AGENT = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_9_2) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/33.0.1750.152 Safari/537.36";

        private static final String BASE_URL = "https://www.portaltransparencia.gov.br";
        private static final String LISTA_DOCUMENTOS_PATH = "/emendas/documentos-relacionados/resultado";
        private static final String DOCUMENTO_PATH = "/despesas/documento/";

        private static final ObjectMapper MAPPER = new ObjectMapper();

        record Favorecido(String docFavorecido, String nome, String url) {
        }

        record OrgaoPagador(String orgaoSuperiorCod, String orgaoSuperiorNome, String entidadeVinculadaCod,
                        String entidadeVinculadaNome, String unidadeGestoraCod, String unidadeGestoraNome,
                        String gestaoCod,
                        String gestaoNome,
                        String url) {
        }

        record DocumentoBusca(String data, String fase, String codigoDocumento, String codigoDocumentoResumido,
                        String especieTipo) {
        }

        record Documento(String data, String fase, String codigoDocumento, String codigoDocumentoResumido,
                        String especieTipo, String observacao, String tipo, String valorDocumento, String descricao,
                        Favorecido favorecido, OrgaoPagador orgaopagador) {
        }

        record Emenda(String id, String ano, String autor, String numeroEmenda, String localidade, String funcao,
                        String subfuncao, String empenhado, String liquido, String pago, String codigoEmenda,
                        List<Documento> documentos) {
        }

        public static void main(String... args) throws Exception {
                if (args.length < 1) {
                        System.out.println("o caminho para o arquivo precisa ser passado!");
                        return;
                }
                if (args.length > 1) {
                        QUANTIDADE_POR_PAGINA = Integer.parseInt(args[1]);
                }
                var arquivo = Paths.get(args[0]);
                System.out.println("Iniciando download de documentos para " + arquivo);
                var emenda = MAPPER.readValue(arquivo.toFile(), Emenda.class);
                var documentos = buscaDocumentos(emenda.codigoEmenda);
                if (documentos.size() > 0) {
                        var novaEmenda = new Emenda(
                                        emenda.id, emenda.ano, emenda.autor, emenda.numeroEmenda, emenda.localidade,
                                        emenda.funcao, emenda.subfuncao, emenda.empenhado, emenda.liquido, emenda.pago,
                                        emenda.codigoEmenda, documentos);

                        Files.writeString(arquivo, MAPPER.writeValueAsString(novaEmenda));
                }
                System.out.printf("Finalizado download de documentos para  %s. Documentos baixados: %d\n", arquivo,
                                documentos.size());
        }

        private static List<Documento> buscaDocumentos(String codigoEmenda) throws Exception {
                int offset = 0;
                var documentos = new ArrayList<Documento>();
                List<Documento> _documentos;
                while ((_documentos = buscaDocumentos(offset, codigoEmenda)).size() > 0) {                        
                        documentos.addAll(_documentos);
                        offset = offset + QUANTIDADE_POR_PAGINA;
                }
                return documentos;
        }

        private static List<Documento> buscaDocumentos(final int offset, final String codigoEmenda) throws Exception {
                // busca documentos

                var response = _buscaDocumentosStr(offset, codigoEmenda);

                var data = MAPPER.readTree(response).get("data");
                var listaDocumentos = data == null ? new DocumentoBusca[0]
                                : MAPPER.readValue(data.toString(), DocumentoBusca[].class);
                return Arrays.stream(listaDocumentos)
                                .parallel()
                                .map(BaixaDocumentoDeEmenda::scrapDetalhesDoDocumento)
                                .toList();

        }

        private static Documento scrapDetalhesDoDocumento(DocumentoBusca documentoBusca) {
                try {
                        var urlDoc = pegaURLDocumento(documentoBusca.fase(), documentoBusca.codigoDocumento());
                        var doc = Jsoup.connect(urlDoc)
                                        .userAgent(AGENT)
                                        .get();

                        // Apenas Dados tabelados
                        var dadosTabelados = doc.getElementsByClass("dados-tabelados");
                        var dadosDetalhados = doc.getElementsByClass("dados-detalhados");

                        // Apenas Dados favorecido
                        var favorecidoDiv = dadosDetalhados.select("button:contains(Dados do Favorecido)").next("div");

                        // Apenas Orgao Pagador
                        var pagadorDiv = dadosDetalhados.select("button:contains(Dados do Órgão)").next("div");
                        var orgaoSuperiorDiv = pagadorDiv.select("strong:contains(Órgão Superior)");
                        var entidadeVinculadaDiv = pagadorDiv.select("strong:contains(Órgão / Entidade Vinculada)");
                        var unidadeGestoraDiv = pagadorDiv.select("strong:contains(Unidade Gestora)");
                        var gestaoDiv = pagadorDiv.select("strong:contains(Gestão)");

                        var orgaoSuperiorCod = orgaoSuperiorDiv.next("span").text();
                        var orgaoSuperiorNome = orgaoSuperiorDiv.next("span").next("span").text();
                        var url = orgaoSuperiorDiv.next("span").next("span").select("a").attr("abs:href");

                        var entidadeVinculadaCod = entidadeVinculadaDiv.next("span").text();
                        var entidadeVinculadaNome = entidadeVinculadaDiv.next("span").next("span").text();

                        var unidadeGestoraCod = unidadeGestoraDiv.next("span").text();
                        var unidadeGestoraNome = unidadeGestoraDiv.next("span").next("span").text();

                        var gestaoCod = gestaoDiv.next("span").text();
                        var gestaoNome = gestaoDiv.next("span").next("span").text();

                        return new Documento(documentoBusca.data,
                                        documentoBusca.fase,
                                        documentoBusca.codigoDocumento,
                                        documentoBusca.codigoDocumentoResumido,
                                        documentoBusca.especieTipo,
                                        dadosTabelados.select("strong:contains(Observação do documento)").next("span")
                                                        .text(),
                                        dadosTabelados.select("strong:contains(Tipo de documento)").next("span").text(),
                                        dadosTabelados.select("strong:contains(Valor do documento)").next("span")
                                                        .text(),
                                        dadosTabelados.select("strong:contains(Descrição)").next("span").text(),
                                        new Favorecido(
                                                        favorecidoDiv.select("strong:contains(CPF/CNPJ/Outros)")
                                                                        .next("span").text(),
                                                        favorecidoDiv.select("strong:contains(Nome)").next("span")
                                                                        .text(),
                                                        favorecidoDiv.select("strong:contains(CPF/CNPJ/Outros)")
                                                                        .next("span").select("a")
                                                                        .attr("abs:href")),
                                        new OrgaoPagador(orgaoSuperiorCod,
                                                        orgaoSuperiorNome,
                                                        entidadeVinculadaCod,
                                                        entidadeVinculadaNome,
                                                        unidadeGestoraCod,
                                                        unidadeGestoraNome,
                                                        gestaoCod,
                                                        gestaoNome,
                                                        url));

                } catch (IOException e) {
                        throw new RuntimeException(e);
                }
        }

        private static String pegaURLDocumento(final String fase, final String codigoDocumento) {
                var segmento = switch (fase) {
                        case "Pagamento" -> "pagamento";
                        case "Empenho" -> "empenho";
                        case "Liquidação" -> "liquidacao";
                        default -> "";
                };
                return BASE_URL + DOCUMENTO_PATH + segmento + "/" + codigoDocumento;
        }

        private static String buildQueryParams(Map<String, Object> queryParams) {
                return queryParams.entrySet().stream()
                                .map(e -> e.getKey() + "=" + e.getValue())
                                .collect(Collectors.joining("&", "?", ""));
        }

        private static String _buscaDocumentosStr(final int offset, final String codigoEmenda) throws Exception {
                Map<String, Object> queryParams = Map.of(
                                "offset", offset,
                                "codigo", codigoEmenda,
                                "paginacaoSimples", false,
                                "tamanhoPagina", QUANTIDADE_POR_PAGINA,
                                "direcaoOrdenacao", "asc",
                                "colunaOrdenacao", "data");

                final var query = buildQueryParams(queryParams);
                final var uriDocumentosParaEmenda = BASE_URL + LISTA_DOCUMENTOS_PATH + query;
                System.out.println(uriDocumentosParaEmenda);
                return reqDocumentos(uriDocumentosParaEmenda);
        }

        private static String reqDocumentos(final String uriDocumentosParaEmenda) throws Exception {
                var request = HttpRequest.newBuilder()
                                .uri(new URI(uriDocumentosParaEmenda))
                                .GET()
                                .build();

                return HttpClient.newHttpClient().send(request, BodyHandlers.ofString()).body();
        }
}
