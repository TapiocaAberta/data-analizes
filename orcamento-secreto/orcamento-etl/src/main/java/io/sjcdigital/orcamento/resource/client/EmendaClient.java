package io.sjcdigital.orcamento.resource.client;

import java.util.Arrays;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import io.sjcdigital.orcamento.model.pojo.EmendasPojo;

/**
 * @author Pedro Hos <pedro-hos@outlook.com>
 *
 */
@Path("/emendas/consulta/")
@RegisterRestClient(configKey = "portaltransparencia-api")
public interface EmendaClient {

    
    public static final boolean PAGINACAO_SIMPLES = false;
    public static final int TAMANHO_PAGINA = 100;
    public static final String DIRECAO_ORDENACAO = "desc";
    public static final String ANO_DE = "2020";
    public static final String ANO_ATE = "2022";
    public static final String COLUNA_ORDENACAO = "valorEmpenhado";
    public static final String AUTOR = "8100";
    public static final List<String> COLUNAS_SELECIONADAS = Arrays.asList("valorRestoInscrito", "valorRestoCancelado", "valorRestoPago", 
                                                                                  "valorRestoAPagar", "codigoEmenda", "ano", "autor", "numeroEmenda",
                                                                                  "localidadeDoGasto", "funcao", "subfuncao", "valorEmpenhado",
                                                                                  "valorLiquidado", "valorPago");
    
    @GET
    @Path("resultado")
    @Produces({ MediaType.APPLICATION_JSON})
    public EmendasPojo getEmendas( @QueryParam("offset") int offset, 
                                   @QueryParam("paginacaoSimples") boolean paginacaoSimples, 
                                   @QueryParam("tamanhoPagina") int tamanhoPagina, 
                                   @QueryParam("direcaoOrdenacao") String direcaoOrdenacao, 
                                   @QueryParam("colunaOrdenacao") String colunaOrdenacao,
                                   @QueryParam("de") String deAno,
                                   @QueryParam("ate") String ateAno,
                                   @QueryParam("autor") String autor,
                                   @QueryParam("colunasSelecionadas") List<String> colunasSelecionadas );
}
