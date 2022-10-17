package io.sjcdigital.orcamento.model.entity;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToMany;

import com.fasterxml.jackson.annotation.JsonIgnore;

import io.quarkus.hibernate.orm.panache.PanacheEntity;

/**
 * @author Pedro Hos <pedro-hos@outlook.com>
 *
 */
@Entity(name = "favorecido")
public class Favorecido extends PanacheEntity {
    
    @Column(unique = true)
    public String docFavorecido;
    public String nome;
    public String url;
    
    @JsonIgnore
    @OneToMany(mappedBy = "favorecido")
    public List<Documentos> documentos;
}
