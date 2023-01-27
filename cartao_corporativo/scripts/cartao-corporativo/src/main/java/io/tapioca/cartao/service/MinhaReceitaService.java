package io.tapioca.cartao.service;

import javax.ws.rs.core.UriBuilder;

import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;
import org.jboss.resteasy.client.jaxrs.internal.ResteasyClientBuilderImpl;

import io.tapioca.cartao.model.dto.MinhaReceitaDTO;
import io.tapioca.cartao.resource.client.MinhaReceitaClient;

/**
 * @author Pedro Hos <pedro-hos@outlook.com>
 *
 */
public class MinhaReceitaService {
    
    static String URL = "https://minhareceita.org";
    
    public static MinhaReceitaDTO buscaDadosCnpj(final String cnpj) {
        ResteasyWebTarget target = getTarget();
        MinhaReceitaClient proxy = target.proxy(MinhaReceitaClient.class);
        MinhaReceitaDTO pojo = proxy.buscaCNPJInfo(cnpj);
        target.getResteasyClient().close();
        return pojo;
    }
    
    protected static ResteasyWebTarget getTarget() {
        ResteasyClient client = new ResteasyClientBuilderImpl().build();
        ResteasyWebTarget target = client.target(UriBuilder.fromPath(URL));
        return target;
    }

}
