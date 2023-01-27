package io.tapioca.cartao.resource.client;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import io.tapioca.cartao.model.dto.MinhaReceitaDTO;

/**
 * @author Pedro Hos <pedro-hos@outlook.com>
 *
 */

@Path("/")
@RegisterRestClient
public interface MinhaReceitaClient {
    
    @GET
    @Path("{cnpj}")
    @Produces(MediaType.APPLICATION_JSON)
    public MinhaReceitaDTO buscaCNPJInfo(@PathParam("cnpj") String cnpj);

}
