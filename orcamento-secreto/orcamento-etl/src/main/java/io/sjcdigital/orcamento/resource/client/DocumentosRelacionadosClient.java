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
    
    public static final  int TAMANHO_PAGINA = 100;
    public static final boolean PAGINACAO_SIMPLES = false;
    public static final String COLUNA_ORDENACAO = "data";
    public static final String DIRECAO_ORDENACAO = "asc";
    
    @GET
    @Path("resultado")
    @Produces({ MediaType.APPLICATION_JSON})
    public DocumentosRelacionadosPojo pegaDocs( @QueryParam("offset") int offset, 
                                                @QueryParam("codigo") String codigo, 
                                                @QueryParam("paginacaoSimples") boolean paginacaoSimples, 
                                                @QueryParam("tamanhoPagina") int tamanhoPagina, 
                                                @QueryParam("direcaoOrdenacao") String direcaoOrdenacao, 
                                                @QueryParam("colunaOrdenacao") String colunaOrdenacao );
}
