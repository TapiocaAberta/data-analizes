package io.sjcdigital.orcamento.model.repository;

import javax.enterprise.context.ApplicationScoped;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import io.sjcdigital.orcamento.model.entity.Documentos;

/**
 * @author Pedro Hos <pedro-hos@outlook.com>
 *
 */
@ApplicationScoped
public class DocumentoRepository implements PanacheRepository<Documentos> {
    
    public Documentos findByFaseAndCodigoDocumento(String fase, String codigoDocumento) {
        return find("fase = ?1 and codigoDocumento = ?2", fase, codigoDocumento).firstResult();
    }

}
