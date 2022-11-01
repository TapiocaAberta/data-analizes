package io.sjcdigital.orcamento.resource.client;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import io.sjcdigital.orcamento.model.pojo.MinhaReceitaPojo;

/**
 * @author Pedro Hos <pedro-hos@outlook.com>
 *
 */

@Path("/")
@RegisterRestClient(configKey="minhareceita-api")
public interface MinhaReceitaClient {
    
    @GET
    @Path("{cnpj}")
    @Produces({ MediaType.APPLICATION_JSON})
    public MinhaReceitaPojo getCNPJInfo(@PathParam("cnpj") String cnpj);

}
