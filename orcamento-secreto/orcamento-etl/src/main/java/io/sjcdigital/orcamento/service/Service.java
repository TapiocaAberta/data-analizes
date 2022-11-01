package io.sjcdigital.orcamento.service;

import javax.ws.rs.core.UriBuilder;

import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;
import org.jboss.resteasy.client.jaxrs.internal.ResteasyClientBuilderImpl;

/**
 * @author Pedro Hos <pedro-hos@outlook.com>
 *
 */
public abstract class Service {
    
    protected static ResteasyWebTarget getTarget(final String URL) {
        ResteasyClient client = new ResteasyClientBuilderImpl().build();
        ResteasyWebTarget target = client.target(UriBuilder.fromPath(URL));
        return target;
    }

}
