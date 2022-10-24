package io.sjcdigital.orcamento.resource.rest;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.sjcdigital.orcamento.service.DocumentoRelacionadoService;
import io.sjcdigital.orcamento.service.OrcamentoSecretoService;

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
    
    @Inject OrcamentoSecretoService orcamentoService;
    @Inject DocumentoRelacionadoService docrelacionadosService;
    
    @GET
    @Path("/buscaEmendas/arquivos")
    public Response buscaPorArquivos() {
        LOGGER.info("Buscando Emendas por arquivos");
        
        ExecutorService newSingleThreadExecutor = Executors.newSingleThreadExecutor();
        
        newSingleThreadExecutor.submit(() -> {
            docrelacionadosService.processaMuitosDocumentosArquivo();
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
    public Response salvaEmendasJson() {
        LOGGER.info("Salvando Emendas");
        orcamentoService.salvaTodasEmendasJson();
        return Response.ok().build();
    }

}
