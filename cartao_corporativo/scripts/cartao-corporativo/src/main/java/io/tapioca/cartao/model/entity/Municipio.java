package io.tapioca.cartao.model.entity;

import java.util.Optional;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import io.quarkus.hibernate.orm.panache.PanacheEntity;

/**
 * @author Pedro Hos <pedro-hos@outlook.com>
 *
 */

@Entity
public class Municipio extends PanacheEntity {
    
    @ManyToOne
    @JoinColumn(name="estado_id")
    public Estado estado;
    
    @Column(unique = true)
    public String codigo; //IBGE
    
    @Column(unique = true)
    public String nome;
    
    public Boolean capital;
    public String latitude;
    public String longitude;
    public String nomeSemAcento;
    public Integer populacao;
    
    public static Optional<Municipio> comCodigoIbge(final String codigo) {
        return find("codigo", codigo).firstResultOptional();
    }
    
}
