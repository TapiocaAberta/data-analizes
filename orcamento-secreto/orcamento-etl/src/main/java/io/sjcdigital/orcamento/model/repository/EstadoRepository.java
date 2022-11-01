package io.sjcdigital.orcamento.model.repository;

import javax.enterprise.context.ApplicationScoped;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import io.sjcdigital.orcamento.model.entity.Estado;

/**
 * @author Pedro Hos <pedro-hos@outlook.com>
 *
 */

@ApplicationScoped
public class EstadoRepository implements PanacheRepository<Estado> {
    
    public Estado findByUf(final String uf) {
        return find("uf", uf).firstResult();
    }

}
