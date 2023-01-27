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
public class Servidor extends PanacheEntity {
    
    @Column(unique = true)
    public String cpf;
    
    public static Optional<Servidor> comCPF(final String cpf) {
        return find("cpf", cpf).firstResultOptional();
    }
}
