package io.sjcdigital.orcamento.model.entity;

import javax.persistence.Entity;

import io.quarkus.hibernate.orm.panache.PanacheEntity;

/**
 * @author Pedro Hos <pedro-hos@outlook.com>
 *
 */
@Entity(name = "estado")
public class Estado extends PanacheEntity {
    public String uf;
}
