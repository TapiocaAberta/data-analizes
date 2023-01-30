package io.tapioca.cartao.model.entity;

import javax.persistence.Column;
import javax.persistence.Entity;

import io.quarkus.hibernate.orm.panache.PanacheEntity;

/**
 * @author Pedro Hos <pedro-hos@outlook.com>
 *
 */

@Entity
public class Estado extends PanacheEntity {
    
    @Column(unique = true)
    public String uf;
    
    @Column(unique = true)
    public String codigo; //IBGE
    
    @Column(unique = true)
    public String nome;
    
    public String nomeSemAcento;
    public String gentilico;
    public String gentilicoAlternativo;
    public String macroregiao;
    public String website;
    public String timezone;
    public String bandeira;

}
