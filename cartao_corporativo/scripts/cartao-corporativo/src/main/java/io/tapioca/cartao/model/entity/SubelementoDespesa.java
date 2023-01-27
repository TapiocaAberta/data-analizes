package io.tapioca.cartao.model.entity;

import java.util.Optional;

import javax.persistence.Column;
import javax.persistence.Entity;

import io.quarkus.hibernate.orm.panache.PanacheEntity;

/**
 * @author Pedro Hos <pedro-hos@outlook.com>
 *
 */

@Entity
public class SubelementoDespesa extends PanacheEntity {
    
    @Column(unique = true)
    public String nome;
    
    public static Optional<SubelementoDespesa> comNome(final String nome) {
        return find("nome", nome).firstResultOptional();
    }
}
