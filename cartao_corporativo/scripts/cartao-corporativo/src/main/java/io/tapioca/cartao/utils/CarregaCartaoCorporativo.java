package io.tapioca.cartao.utils;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.quarkus.runtime.ShutdownEvent;
import io.quarkus.runtime.StartupEvent;
import io.tapioca.cartao.service.CartaoCorporativoService;

/**
 * @author Pedro Hos <pedro-hos@outlook.com>
 *
 */

@ApplicationScoped
public class CarregaCartaoCorporativo {
    
    @Inject
    CartaoCorporativoService cartaoCoporativoService;
    
    private static final Logger LOGGER = LoggerFactory.getLogger(CartaoCorporativoService.class);

    void onStart(@Observes StartupEvent ev) {               
        LOGGER.info("A aplicação está sendo iniciada...");
        cartaoCoporativoService.carregaDadosCSV();
    }

    void onStop(@Observes ShutdownEvent ev) {               
        LOGGER.info("A aplicação está sendo parada...");
    }


}
