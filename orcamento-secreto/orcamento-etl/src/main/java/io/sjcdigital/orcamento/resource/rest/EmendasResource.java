package io.sjcdigital.orcamento.resource.rest;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.jsoup.HttpStatusException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.sjcdigital.orcamento.model.entity.Documentos;
import io.sjcdigital.orcamento.model.repository.DocumentoRepository;
import io.sjcdigital.orcamento.service.DetalhesDocumentoService;
import io.sjcdigital.orcamento.service.DocumentoRelacionadoService;
import io.sjcdigital.orcamento.service.FavorecidoService;
import io.sjcdigital.orcamento.service.OrcamentoSecretoService;
import io.sjcdigital.orcamento.utils.Constantes;
import io.sjcdigital.orcamento.utils.FileUtil;

/**
 * @author Pedro Hos <pedro-hos@outlook.com>
 *
 */
@ApplicationScoped
@Path("/api/emendas")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class EmendasResource {

    private static final Logger LOGGER = LoggerFactory.getLogger(EmendasResource.class);

    @Inject
    OrcamentoSecretoService orcamentoService;
    
    @Inject
    DocumentoRelacionadoService docrelacionadosService;
    
    @Inject
    DetalhesDocumentoService detalhesService;
    
    @Inject
    DocumentoRepository documentRepository;

    @Inject
    FavorecidoService favorecidoService;

    @GET
    @Path("/documentos/favorecido/{id}")
    public Response buscaDocumentoPorFavorecido(@PathParam("id") Long id) {

        List<Documentos> fav = documentRepository.findByFavorecidoId(id);
        FileUtil.salvaJSON(fav, Constantes.DATA_JSON_PATH, "erro.json");

        return Response.ok().build();
    }

    /**
    @GET
    @Path("/documentos/favorecido/atualiza")
    public Response buscaDocumentoPorFavorecido() {
        ExecutorService newSingleThreadExecutor = Executors.newSingleThreadExecutor();

        newSingleThreadExecutor.submit(() -> {
            favorecidoService.atualizaPessoaFisicaInfos();
        });
        
        return Response.ok().build();
    }
**/
    
    @GET
    @Path("/documentos/fase/{fase}/codigo/{codigo}")
    @Transactional
    public Response buscaUnicoDocumento(@PathParam("fase") String fase, @PathParam("codigo") String codigo) {

        try {
            detalhesService.salvaPaginaDetalhes(Arrays.asList(new Documentos(fase, codigo)));
        } catch (HttpStatusException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return Response.ok().build();
    }

    @GET
    @Path("/documentos")
    @Transactional
    public Response buscaDocumento(@QueryParam("limit") Integer limit) throws HttpStatusException {

        detalhesService.salvaPaginaDetalhes(documentRepository.findNaoProcessado(limit));
        return Response.ok().build();
    }

    @GET
    @Path("/buscaEmendas/arquivos")
    public Response buscaPorArquivos(@QueryParam("index") Integer index) {
        LOGGER.info("Buscando Emendas por arquivos");

        ExecutorService newSingleThreadExecutor = Executors.newSingleThreadExecutor();

        newSingleThreadExecutor.submit(() -> {
            docrelacionadosService.processaMuitosDocumentosArquivo(index);
        });

        return Response.ok().build();
    }

    @GET
    @Path("/buscaEmendas/palavra-chave/{palavraChave}")
    public Response buscaPorPalavraChave(@PathParam("palavraChave") String palavraChave) {
        LOGGER.info("Buscando Emendas");
        orcamentoService.buscaPorPalavraChave(palavraChave);
        return Response.ok().build();
    }

    @GET
    @Path("/buscaEmendas")
    public Response iniciaProcesso() {
        LOGGER.info("Buscando Emendas");

        ExecutorService newSingleThreadExecutor = Executors.newSingleThreadExecutor();

        newSingleThreadExecutor.submit(() -> {
            orcamentoService.buscaTodasEmendasRelator();
        });

        return Response.ok().build();
    }

    @GET
    @Path("/salvaEmendasJson")
    public Response salvaEmendasDocumentosProcessadosJson() {
        LOGGER.info("Salvando Emendas");
        ExecutorService newSingleThreadExecutor = Executors.newSingleThreadExecutor();

        newSingleThreadExecutor.submit(() -> {
            orcamentoService.salvaTodasEmendasJson();
        });

        newSingleThreadExecutor.shutdown();

        return Response.ok().build();
    }

}
