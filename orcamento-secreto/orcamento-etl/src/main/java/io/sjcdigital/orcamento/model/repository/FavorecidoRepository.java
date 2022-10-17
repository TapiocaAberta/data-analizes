package io.sjcdigital.orcamento.model.repository;

import javax.enterprise.context.ApplicationScoped;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import io.sjcdigital.orcamento.model.entity.Favorecido;

/**
 * @author Pedro Hos <pedro-hos@outlook.com>
 *
 */

@ApplicationScoped
public class FavorecidoRepository implements PanacheRepository<Favorecido> {
    
    public Favorecido findByDocumento(String documento) {
        return find("docFavorecido", documento).firstResult();
    }

}
