package io.tapioca.cartao.model.entity;

import java.util.Optional;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;

import io.quarkus.hibernate.orm.panache.PanacheEntity;

/**
 * @author Pedro Hos <pedro-hos@outlook.com>
 *
 */

@Entity
public class Fornecedor extends PanacheEntity {
    
    @Column(unique = true)
    public String documento;
    public String porte;
    public String razaoSocial;
    public String nome; //ou nome fantasia
    public String naturezaJuridica;
    public String cnaeFiscalDescricao;
    public String dataInicioAtividade;
    
    @Enumerated(EnumType.STRING)
    public TipoDocumento tipoDocumento;
    
    @OneToOne
    @JoinColumn(name="endereco_id")
    public Endereco endereco;
    
    public static Optional<Fornecedor> comDocumento(final String documento) {
        return find("documento", documento).firstResultOptional();
    }

}
