package io.sjcdigital.orcamento.model.entity;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import com.fasterxml.jackson.annotation.JsonIgnore;

import io.quarkus.hibernate.orm.panache.PanacheEntity;

/**
 * @author Pedro Hos <pedro-hos@outlook.com>
 *
 */

@Entity(name = "orgaoPagador")
@Table(uniqueConstraints =  @UniqueConstraint(columnNames = {"orgaoSuperiorCod", "entidadeVinculadaCod", "unidadeGestoraCod", "gestaoCod"}))
public class OrgaoPagador extends PanacheEntity {
    
    public String orgaoSuperiorCod;
    public String orgaoSuperiorNome;
    
    public String entidadeVinculadaCod;
    public String entidadeVinculadaNome;
    
    public String unidadeGestoraCod;
    public String unidadeGestoraNome;
    
    public String gestaoCod;
    public String gestaoNome;
    
    public String url;
    
    @JsonIgnore
    @OneToMany(mappedBy = "orgaoPagador")
    public List<Documentos> documentos;

}
