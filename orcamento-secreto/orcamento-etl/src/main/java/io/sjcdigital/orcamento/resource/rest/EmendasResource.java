package io.sjcdigital.orcamento.resource.rest;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
    
    @GET
    @Path("/buscaTodasEmendas")
    public Response iniciaProcesso() {
        LOGGER.info("Buscando Emendas");
        orcamentoService.buscaTodasEmendasRelator();
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
