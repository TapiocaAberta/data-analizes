package io.tapioca.cartao.model.entity;

import java.util.Optional;

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
    
    public String codigo; //IBGE
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
