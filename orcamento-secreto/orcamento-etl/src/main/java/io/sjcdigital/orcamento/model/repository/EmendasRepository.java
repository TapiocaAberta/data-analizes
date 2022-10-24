package io.sjcdigital.orcamento.model.repository;

import java.util.List;

import javax.enterprise.context.ApplicationScoped;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import io.sjcdigital.orcamento.model.entity.Emendas;

/**
 * @author Pedro Hos <pedro-hos@outlook.com>
 *
 */

@ApplicationScoped
public class EmendasRepository implements PanacheRepository<Emendas> {
    
    public List<Emendas> buscaArquivosGrandesNaoProcesados() {
        return list("muitosdocumentos = true and processado = false order by quantidadedocumentos asc");
    }
    
}
