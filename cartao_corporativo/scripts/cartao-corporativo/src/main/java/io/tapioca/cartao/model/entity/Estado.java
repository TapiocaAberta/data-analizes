package io.tapioca.cartao.model.entity;

import javax.persistence.Entity;

import io.quarkus.hibernate.orm.panache.PanacheEntity;

/**
 * @author Pedro Hos <pedro-hos@outlook.com>
 *
 */

@Entity
public class Estado extends PanacheEntity {
    
    public String uf;
    public String codigo; //IBGE
    public String nome;
    public String nomeSemAcento;
    public String gentilico;
    public String gentilicoAlternativo;
    public String macroregiao;
    public String website;
    public String timezone;
    public String bandeira;

}
