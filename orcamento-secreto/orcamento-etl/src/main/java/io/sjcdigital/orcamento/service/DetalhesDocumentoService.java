package io.sjcdigital.orcamento.service;

import static io.sjcdigital.orcamento.utils.PortalTransparenciaConstantes.DOCUMENTO_URL;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

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
import io.sjcdigital.orcamento.model.entity.DetalhesErro;
import io.sjcdigital.orcamento.model.entity.Documentos;
import io.sjcdigital.orcamento.model.entity.Favorecido;
import io.sjcdigital.orcamento.model.entity.OrgaoPagador;
import io.sjcdigital.orcamento.model.repository.DocumentoRepository;
import io.sjcdigital.orcamento.model.repository.FavorecidoRepository;
import io.sjcdigital.orcamento.model.repository.OrgaoPagadorRepository;
import io.sjcdigital.orcamento.utils.Constantes;
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
    
    @Transactional
    public void salvaPaginaDetalhes(List<Documentos> value) throws HttpStatusException {
        
        LOGGER.info("[INICIO] Buscando detalhes do documento ");
        Instant start = Instant.now(); //1
        
        
        value.forEach(d -> {
            
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
                        
                        Documentos documento = documentoRepository.findByFaseAndCodigoDocumento(d.getFase(), d.getCodigoDocumento());
                        documento.setPgDetalhesNotFound(true);
                        documento.persistAndFlush();
                        
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

        Instant end = Instant.now(); //1 
        long interval = Duration.between(start, end).toSeconds(); //1
        LOGGER.info("[FIM] Buscando detalhes do documento em " + interval + "s");

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
    
    protected String pegaTipoPessoaUrl(String url) {
        if(url.contains("fisica")) {
            return "Pessoa Física";
        } else if(url.contains("juridica")) {
            return "Pessoa Jurídica";
        } else {
            return "Não Listado";
        }
    }
    
    @Transactional
    protected String verificaString(String value, String campo, String fase, String codigoDocumento, boolean folhaPagamento) {
        
        if (!folhaPagamento) {

            if (value == null || value.trim().equals("")) {
                DetalhesErro erro = new DetalhesErro();
                erro.setCodigoDocumento(codigoDocumento);
                erro.setFase(fase);
                erro.setUrl(pegaURLDocumento(fase, codigoDocumento));
                erro.setDescricao(campo.toUpperCase() + " - Erro ao ler o campo!");
                erro.persist();
            }
        }
        
        return value;
    }
    
    /**
     * @param detalheURL
     * @param documento
     */
    @Transactional
    public void processaDetalhes(String file, Documentos documentos) {
        
        Documentos documento = null;
        
        if(Objects.isNull(documentos.id)) {
            documento = documentoRepository.findByFaseAndCodigoDocumento(documentos.getFase(), documentos.getCodigoDocumento());
        } else {
            documento = documentos;
        }
        
        try {
    
            LOGGER.info("[PROCESSANDO] arquivo de Detalhes para fase " + documentos.getFase() + " e código Documento " + documentos.getCodigoDocumento());
            
    
            Document doc = Jsoup.parse(file, PortalTransparenciaConstantes.URL);
            
            documento.setProcessado(Boolean.TRUE);
            documentoRepository.persist(documento);
            
            // Apenas Dados tabelados
            Elements dadosTabelados = doc.getElementsByClass("dados-tabelados");
            documento.setDescricao(dadosTabelados.select("strong:contains(Descrição)").next("span").text());
            documento.setTipo(dadosTabelados.select("strong:contains(Tipo de documento)").next("span").text());
            documento.setValorDocumento(dadosTabelados.select("strong:contains(Valor)").next("span").text());
            documento.setObservacao(dadosTabelados.select("strong:contains(Observação do documento)").next("span").text());
    
            Elements dadosDetalhados = doc.getElementsByClass("dados-detalhados");
    
            // Apenas Dados favorecido
            Elements favorecidoDiv = dadosDetalhados.select("button:contains(Dados do Favorecido)").next("div");
            
            String documentoFavorecido = favorecidoDiv.select("strong:contains(CPF/CNPJ/Outros)").next("span").text();
    
            Favorecido favorecido = favorecidoRepository.findByDocumento(documentoFavorecido);
    
            if (Objects.isNull(favorecido)) {
                favorecido = new Favorecido();
                
                String url = favorecidoDiv.select("strong:contains(CPF/CNPJ/Outros)").next("span").select("a").attr("abs:href");
                favorecido.url = url;
                
                boolean folhaPagamento = false;
                
                String nome = favorecidoDiv.select("strong:contains(Nome)").next("span").text();
                favorecido.nome = verificaString(nome, "Nome Favorecido", documentos.getFase(), documentos.getCodigoDocumento(), folhaPagamento);
                
                if("DADO REFERENTE À FOLHA DE PAGAMENTO".toLowerCase().equals(nome.toLowerCase())) {
                    folhaPagamento = true;
                    favorecido.tipo = "Folha de Pagamento";
                } else {
                    favorecido.tipo = pegaTipoPessoaUrl(url);
                }
                
                favorecido.docFavorecido =  verificaString(documentoFavorecido, "Documento Favorecido", documentos.getFase(), 
                        documentos.getCodigoDocumento(), folhaPagamento);
                
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
