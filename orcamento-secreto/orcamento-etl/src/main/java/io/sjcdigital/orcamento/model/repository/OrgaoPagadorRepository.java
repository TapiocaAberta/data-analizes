package io.sjcdigital.orcamento.model.repository;

import javax.enterprise.context.ApplicationScoped;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import io.sjcdigital.orcamento.model.entity.OrgaoPagador;

/**
 * @author Pedro Hos <pedro-hos@outlook.com>
 *
 */

@ApplicationScoped
public class OrgaoPagadorRepository implements PanacheRepository<OrgaoPagador> {
    
    public OrgaoPagador findByCodes(String orgaoSuperiorCod, String entidadeVinculadaCod, String unidadeGestoraCod, String gestaoCod) {
        String query = "orgaoSuperiorCod = ?1 and entidadeVinculadaCod = ?2 and unidadeGestoraCod = ?3 and gestaoCod = ?4";
        return find(query, orgaoSuperiorCod, entidadeVinculadaCod, unidadeGestoraCod, gestaoCod).firstResult();
    }

}
