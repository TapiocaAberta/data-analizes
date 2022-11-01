package io.sjcdigital.orcamento.model.entity;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import io.quarkus.hibernate.orm.panache.PanacheEntity;

/**
 * @author Pedro Hos <pedro-hos@outlook.com>
 *
 */

@Entity(name = "endereco")
public class Endereco extends PanacheEntity {
    
    public String cep;
    public String bairro;
    public String numero;
    public String logradouro;
    
    @ManyToOne
    @JoinColumn(name="municipio_id")
    public Municipio municipio;

}
