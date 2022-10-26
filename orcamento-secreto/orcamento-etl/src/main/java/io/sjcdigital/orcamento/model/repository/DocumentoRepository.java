package io.sjcdigital.orcamento.model.repository;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

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
    
    public List<Documentos> findByFavorecidoId(Long id) {
        return find("favorecido.id = ?1", id).list();
    }
    
    public Map<String, Documentos> findByCodigoDocumento(List<String> codigos) {
        return find("codigoDocumento in (?1)", codigos).stream().collect(Collectors.toMap(Documentos::getCodigoDocumento, Function.identity()));
    }
    
    public Map<String, Documentos> findAllMap() {
        return findAll().stream().collect(Collectors.toMap(Documentos::getCodigoDocumento, Function.identity()));
    }

}
