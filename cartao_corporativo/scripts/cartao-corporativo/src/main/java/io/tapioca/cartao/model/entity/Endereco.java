package io.tapioca.cartao.model.entity;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import io.quarkus.hibernate.orm.panache.PanacheEntity;

/**
 * @author Pedro Hos <pedro-hos@outlook.com>
 *
 */

@Entity
public class Endereco extends PanacheEntity {
    
    @ManyToOne
    @JoinColumn(name="municipio_id")
    public Municipio municipio;
    
    public String logradouro;
    public String cep;
    public String bairro;
    public String numero;

}
