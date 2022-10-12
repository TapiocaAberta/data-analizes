package io.sjcdigital.orcamento.resource.client;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import io.sjcdigital.orcamento.model.pojo.DocumentosRelacionadosPojo;

/**
 * @author Pedro Hos <pedro-hos@outlook.com>
 *
 */
@Path("/emendas/documentos-relacionados/")
@RegisterRestClient(configKey="portaltransparencia-api")
public interface DocumentosRelacionadosClient {
    
    @GET
    @Path("resultado")
    @Produces({ MediaType.APPLICATION_JSON})
    public DocumentosRelacionadosPojo pegaDocs(   @QueryParam("offset") int offset, 
                                @QueryParam("codigo") String codigo, 
                                @QueryParam("paginacaoSimples") boolean paginacaoSimples, 
                                @QueryParam("tamanhoPagina") int tamanhoPagina, 
                                @QueryParam("direcaoOrdenacao") String direcaoOrdenacao, 
                                @QueryParam("colunaOrdenacao") String colunaOrdenacao );
}
