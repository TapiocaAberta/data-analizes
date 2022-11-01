package io.sjcdigital.orcamento.model.repository;

import java.util.List;

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
    
    public List<Favorecido> findAllByTipo(String tipo) {
        return list("tipo = ?1 and processado = false", tipo).subList(0, 300);
    }
    

    public List<Favorecido> findByIds(List<Long> ids) {
        return list("id in (?1)", ids);
    }

}
