package io.sjcdigital.orcamento.model.entity;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import io.quarkus.hibernate.orm.panache.PanacheEntity;

/**
 * @author Pedro Hos <pedro-hos@outlook.com>
 *
 */
@Entity(name = "municipio")
@Table(uniqueConstraints =  @UniqueConstraint(columnNames = {"codigoMunicipio", "codigoMunicipioIbge"}))
public class Municipio extends PanacheEntity {
    
    public String municipio;
    public String codigoMunicipio;
    public String codigoMunicipioIbge;
    
    @ManyToOne
    @JoinColumn(name="estado_id")
    public Estado estado;

}
