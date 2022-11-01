package io.sjcdigital.orcamento.model.repository;

import javax.enterprise.context.ApplicationScoped;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import io.sjcdigital.orcamento.model.entity.Municipio;

/**
 * @author Pedro Hos <pedro-hos@outlook.com>
 *
 */
@ApplicationScoped
public class MunicipioRepository implements PanacheRepository<Municipio> {
    
    public Municipio findByCodigos(String codigo, String codigoIbge) {
        return find("codigoMunicipio = ?1 and codigoMunicipioIbge = ?2", codigo, codigoIbge).firstResult();
    }

}
