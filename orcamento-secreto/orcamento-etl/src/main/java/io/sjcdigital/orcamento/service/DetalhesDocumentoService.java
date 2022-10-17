package io.sjcdigital.orcamento.service;

import static io.sjcdigital.orcamento.utils.PortalTransparenciaConstantes.DOCUMENTO_URL;

import java.io.IOException;
import java.util.Objects;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.transaction.Transactional;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.quarkus.runtime.annotations.RegisterForReflection;
import io.sjcdigital.orcamento.model.entity.Documentos;
import io.sjcdigital.orcamento.model.entity.Favorecido;
import io.sjcdigital.orcamento.model.entity.OrgaoPagador;
import io.sjcdigital.orcamento.model.pojo.DocumentosDataPojo;
import io.sjcdigital.orcamento.model.pojo.DocumentosRelacionadosPojo;
import io.sjcdigital.orcamento.model.repository.DocumentoRepository;
import io.sjcdigital.orcamento.model.repository.FavorecidoRepository;
import io.sjcdigital.orcamento.model.repository.OrgaoPagadorRepository;
import io.sjcdigital.orcamento.utils.Constantes;
import io.sjcdigital.orcamento.utils.FileUtil;
import io.sjcdigital.orcamento.utils.PortalTransparenciaConstantes;

/**
 * @author Pedro Hos <pedro-hos@outlook.com>
 */

@ApplicationScoped
@Named("detalhesDocumentoBean")
@RegisterForReflection
public class DetalhesDocumentoService {

    private static final Logger LOGGER = LoggerFactory.getLogger(DetalhesDocumentoService.class);

    @Inject DocumentoRepository documentoRepository;
    @Inject FavorecidoRepository favorecidoRepository;
    @Inject OrgaoPagadorRepository orgaoPagadorRepository;
    
    public void salvaPaginaDetalhes(DocumentosRelacionadosPojo pojo) {

        LOGGER.info("Buscando detalhes da Emenda ID " + pojo.getIdEmenda());

        for (DocumentosDataPojo documentos : pojo.getData()) {

            try {

                String html = Jsoup.connect(pegaURLDocumento(documentos.getFase(), documentos.getCodigoDocumento()))
                                   .userAgent(PortalTransparenciaConstantes.AGENT)
                                   .timeout(PortalTransparenciaConstantes.TIMEOUT)
                                   .followRedirects(true)
                                   .get()
                                   .html();

                FileUtil.salvaHTML(html, Constantes.DOC_DETALHES_PATH, documentos.getFase() + "_" + documentos.getCodigoDocumento());

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    private static String pegaURLDocumento(final String fase, final String codigoDocumento) {

        String queryParam = "?ordenarPor=fase&direcao=asc";

        switch (fase) {
        case "Pagamento":
            return DOCUMENTO_URL + "pagamento/" + codigoDocumento + queryParam;
        case "Empenho":
            return DOCUMENTO_URL + "empenho/" + codigoDocumento + queryParam;
        case "Liquidação":
            return DOCUMENTO_URL + "liquidacao/" + codigoDocumento + queryParam;
        default:
            return "";
        }

    }

    /**
     * @param detalheURL
     * @param documento
     */
    @Transactional
    public void processaDetalhes(String file, String fileName) {

        String[] split = fileName.split("_");

        String fase = split[0];
        String codigoDocumento = split[1].replace(".html", "");

        LOGGER.info("Processando arquivo de Detalhes para fase " + fase + " e código Documento " + codigoDocumento);

        Documentos documento = documentoRepository.findByFaseAndCodigoDocumento(fase, codigoDocumento);

        if (Objects.nonNull(documento)) {

            Document doc = Jsoup.parse(file, PortalTransparenciaConstantes.URL);

            // Apenas Dados tabelados
            Elements dadosTabelados = doc.getElementsByClass("dados-tabelados");
            documento.descricao = dadosTabelados.select("strong:contains(Descrição)").next("span").text();
            documento.tipo = dadosTabelados.select("strong:contains(Tipo de documento)").next("span").text();
            documento.valorDocumento = dadosTabelados.select("strong:contains(Valor do documento)").next("span").text();
            documento.observacao = dadosTabelados.select("strong:contains(Observação do documento)").next("span").text();

            Elements dadosDetalhados = doc.getElementsByClass("dados-detalhados");

            // Apenas Dados favorecido
            Elements favorecidoDiv = dadosDetalhados.select("button:contains(Dados do Favorecido)").next("div");
            String documentoFavorecido = favorecidoDiv.select("strong:contains(CPF/CNPJ/Outros)").next("span").text();
            
            Favorecido favorecido = favorecidoRepository.findByDocumento(documentoFavorecido);
            
            if(Objects.isNull(favorecido)) {
                favorecido = new Favorecido();
                favorecido.docFavorecido = documentoFavorecido;
                favorecido.nome = favorecidoDiv.select("strong:contains(Nome)").next("span").text();
                favorecido.url = favorecidoDiv.select("strong:contains(CPF/CNPJ/Outros)").next("span").select("a").attr("abs:href");
                favorecido.persist();
                
                documento.favorecido = favorecido;
                
            } else {
                documento.favorecido = favorecido;
            }

            // Apenas Orgao Pagador
            Elements pagadorDiv = dadosDetalhados.select("button:contains(Dados do Órgão)").next("div");
            
            Elements orgapSuperiorDiv = pagadorDiv.select("strong:contains(Órgão Superior)");
            String orgaoSuperiorCod = orgapSuperiorDiv.next("span").text();
            
            Elements entidadeVinculadaDiv = pagadorDiv.select("strong:contains(Órgão / Entidade Vinculada)");
            String entidadeVinculadaCod = entidadeVinculadaDiv.next("span").text();
            
            Elements unidadeGestoraDiv = pagadorDiv.select("strong:contains(Unidade Gestora)");
            String unidadeGestoraCod = unidadeGestoraDiv.next("span").text();
            
            Elements gestaoDiv = pagadorDiv.select("strong:contains(Gestão)");
            String gestaoCod = gestaoDiv.next("span").text();
            
            OrgaoPagador orgaoPagador = orgaoPagadorRepository.findByCodes(orgaoSuperiorCod, entidadeVinculadaCod, unidadeGestoraCod, gestaoCod);
            
            if(Objects.isNull(orgaoPagador)) {
                orgaoPagador = new OrgaoPagador();
                orgaoPagador.orgaoSuperiorCod = orgapSuperiorDiv.next("span").text();
                orgaoPagador.orgaoSuperiorNome = orgapSuperiorDiv.next("span").next("span").text();
                orgaoPagador.url = orgapSuperiorDiv.next("span").next("span").select("a").attr("abs:href");

                orgaoPagador.entidadeVinculadaCod = entidadeVinculadaDiv.next("span").text();
                orgaoPagador.entidadeVinculadaNome = entidadeVinculadaDiv.next("span").next("span").text();

                orgaoPagador.unidadeGestoraCod = unidadeGestoraDiv.next("span").text();
                orgaoPagador.unidadeGestoraNome = unidadeGestoraDiv.next("span").next("span").text();

                orgaoPagador.gestaoCod = gestaoDiv.next("span").text();
                orgaoPagador.gestaoNome = gestaoDiv.next("span").next("span").text();
                orgaoPagador.persist();
                
                documento.orgaoPagador = orgaoPagador;
                
            } else {
                documento.orgaoPagador = orgaoPagador; 
            }
            
            documentoRepository.persistAndFlush(documento);
            
        } else {
            LOGGER.info("[RETRY] Documento não encontrado para fase " + fase + " e código Documento " + codigoDocumento);
            FileUtil.salvaHTML(file, Constantes.DOC_DETALHES_PATH + Constantes.RETRY_FOLDER, fileName);
        }
    }

}
