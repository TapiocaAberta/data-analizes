package io.tapioca.cartao.utils;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.quarkus.runtime.ShutdownEvent;
import io.quarkus.runtime.StartupEvent;
import io.tapioca.cartao.model.entity.CartaoCoporativo;
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
        
        if(CartaoCoporativo.count() == 0) {
            LOGGER.info("Não existem dados, carga será iniciada");
            cartaoCoporativoService.carregaDadosCartaoCSV();
        } else {
            LOGGER.info("Base de dados preenchida");
            cartaoCoporativoService.criaCSV();
        }
    }

    void onStop(@Observes ShutdownEvent ev) {               
        LOGGER.info("A aplicação está sendo parada...");
    }


}
