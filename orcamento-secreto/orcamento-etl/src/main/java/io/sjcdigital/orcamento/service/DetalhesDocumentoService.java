package io.sjcdigital.orcamento.service;

import static io.sjcdigital.orcamento.utils.PortalTransparenciaConstantes.DOCUMENTO_URL;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.transaction.Transactional;

import org.jsoup.Connection.Response;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.quarkus.runtime.annotations.RegisterForReflection;
import io.sjcdigital.orcamento.model.entity.Documentos;
import io.sjcdigital.orcamento.model.entity.Favorecido;
import io.sjcdigital.orcamento.model.entity.OrgaoPagador;
import io.sjcdigital.orcamento.model.repository.DocumentoRepository;
import io.sjcdigital.orcamento.model.repository.FavorecidoRepository;
import io.sjcdigital.orcamento.model.repository.OrgaoPagadorRepository;
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
    
    public void salvaPaginaDetalhes(List<Documentos> value) throws HttpStatusException {

        ExecutorService executor = Executors.newCachedThreadPool();
        
        LOGGER.info("[INICIO] Buscando detalhes do documento");

        value.forEach(d -> {
            
            
            executor.submit(() -> {
                
                try {
                    
                    Response execute = Jsoup.connect(pegaURLDocumento(d.getFase(), d.getCodigoDocumento()))
                                       .userAgent(PortalTransparenciaConstantes.AGENT)
                                       .timeout(PortalTransparenciaConstantes.TIMEOUT)
                                       .followRedirects(true)
                                       .execute();
          
                    String html = execute.parse().html();
                    processaDetalhes(html, d);

                } catch (HttpStatusException e) {
                   
                    if(e.getStatusCode() == 404) {
                        
                        LOGGER.error("[ERRO] ao pegar pagina de detalhes não encontrada. "
                                + "Fase:" + d.getFase() + ", Código Documento: " + d.getCodigoDocumento() + " [ " + e.getMessage() + " ]");
                       
                    } else {
                        LOGGER.error("[ERRO] ao pegar pagina de detalhe. "
                                + "Fase:" + d.getFase() + ", Código Documento: " + d.getCodigoDocumento() + " [ " + e.getMessage() + " ]");
                    }
                   
                } catch (IOException e) {
                    LOGGER.error("[ERRO] ao pegar pagina de detalhe. "
                            + "Fase:" + d.getFase() + ", Código Documento: " + d.getCodigoDocumento() + " [ " + e.getMessage() + " ]");
                }

            });

        });

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
    
    protected String pegaTipoPessoa(String url) {
        if(url.contains("fisica")) {
            return "Pessoa Física";
        } else if(url.contains("juridica")) {
            return "Pessoa Jurídica";
        } else {
            return "Não Listado";
        }
    }

    /**
     * @param detalheURL
     * @param documento
     */
    @Transactional
    public void processaDetalhes(String file, Documentos documentos) {
        
        Documentos documento = documentoRepository.findByFaseAndCodigoDocumento(documentos.getFase(), documentos.getCodigoDocumento());
        
        try {
    
            LOGGER.info("[PROCESSANDO] arquivo de Detalhes para fase " + documentos.getFase() + " e código Documento " + documentos.getCodigoDocumento());
            
    
            Document doc = Jsoup.parse(file, PortalTransparenciaConstantes.URL);
            
            documento.setProcessado(Boolean.TRUE);
            documentoRepository.persist(documento);
            
            // Apenas Dados tabelados
            Elements dadosTabelados = doc.getElementsByClass("dados-tabelados");
            documento.setDescricao(dadosTabelados.select("strong:contains(Descrição)").next("span").text());
            documento.setTipo(dadosTabelados.select("strong:contains(Tipo de documento)").next("span").text());
            documento.setValorDocumento(dadosTabelados.select("strong:contains(Valor do documento)").next("span").text());
            documento.setObservacao(dadosTabelados.select("strong:contains(Observação do documento)").next("span").text());
    
            Elements dadosDetalhados = doc.getElementsByClass("dados-detalhados");
    
            // Apenas Dados favorecido
            Elements favorecidoDiv = dadosDetalhados.select("button:contains(Dados do Favorecido)").next("div");
            String documentoFavorecido = favorecidoDiv.select("strong:contains(CPF/CNPJ/Outros)").next("span").text();
    
            Favorecido favorecido = favorecidoRepository.findByDocumento(documentoFavorecido);
    
            if (Objects.isNull(favorecido)) {
                favorecido = new Favorecido();
                favorecido.docFavorecido = documentoFavorecido;
                favorecido.nome = favorecidoDiv.select("strong:contains(Nome)").next("span").text();
                
                String url = favorecidoDiv.select("strong:contains(CPF/CNPJ/Outros)").next("span").select("a").attr("abs:href");
                favorecido.url = url;
                favorecido.tipo = pegaTipoPessoa(url);
                
                favorecidoRepository.persist(favorecido);
    
                documento.setFavorecido(favorecido);
    
            } else {
                documento.setFavorecido(favorecido);
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
    
            if (Objects.isNull(orgaoPagador)) {
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
                orgaoPagadorRepository.persist(orgaoPagador);
    
                documento.setOrgaoPagador(orgaoPagador);
    
            } else {
                documento.setOrgaoPagador(orgaoPagador);
            }
    
            documento.setProcessado(Boolean.TRUE);
            documento.setErro(Boolean.FALSE);
            documento.setProcessando(Boolean.FALSE);
            
            documentoRepository.persist(documento);
            
            LOGGER.info("[FINALIZADO] Detalhes salvos com sucesso!");
            
         } catch (Exception e) {
            LOGGER.error("Erro ao processar detalhes de " + documento.getCodigoDocumento() + " - " + e.getMessage());
            documento.setProcessado(Boolean.FALSE);
            documento.setErro(Boolean.TRUE);
            documento.setProcessando(Boolean.FALSE);
            documentoRepository.persist(documento);
        }
    }

}
