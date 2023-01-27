package io.tapioca.cartao.model.entity;

import java.time.LocalDate;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import io.quarkus.hibernate.orm.panache.PanacheEntity;

/**
 * @author Pedro Hos <pedro-hos@outlook.com>
 *
 */
@Entity
public class CartaoCoporativo extends PanacheEntity {
    
    public LocalDate dataPagamento;
    
    @ManyToOne
    @JoinColumn(name="servidor_id")
    public Servidor servidor;
    
    public Double valor;
    
    @ManyToOne
    @JoinColumn(name="fornecedor_id")
    public Fornecedor fornecedor;
    
    @ManyToOne
    @JoinColumn(name="subelemento_id")
    public SubelementoDespesa subelemento;
    
    public String mandato;
}
