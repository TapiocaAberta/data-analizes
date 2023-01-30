package io.tapioca.cartao.resource.rest;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import io.tapioca.cartao.service.CartaoCorporativoService;

/**
 * @author Pedro Hos <pedro-hos@outlook.com>
 *
 */

@Path("/api/cartao-corporativo")
@ApplicationScoped
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class CartaoCorporativoRest {
    
    @Inject
    CartaoCorporativoService service;
    
    @Path("/")
    @GET
    public void test() {
        service.carregaDadosCartaoCSV();
    }
    
}
