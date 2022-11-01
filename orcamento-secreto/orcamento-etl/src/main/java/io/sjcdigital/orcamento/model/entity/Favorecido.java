package io.sjcdigital.orcamento.model.entity;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

import com.fasterxml.jackson.annotation.JsonIgnore;

import io.quarkus.hibernate.orm.panache.PanacheEntity;

/**
 * @author Pedro Hos <pedro-hos@outlook.com>
 *
 */
@Entity(name = "favorecido")
public class Favorecido extends PanacheEntity {
    
    public final static String PESSOA_FISICA = "Pessoa Física";
    public final static String PESSOA_JURIDICA = "Pessoa Jurídica";
    public final static String NAO_LISTADO = "Não Listado";
    
    @Column(unique = true)
    public String docFavorecido;
    public String nome;
    public String tipo;
    public String url;
    
    @JsonIgnore
    @OneToMany(mappedBy = "favorecido")
    public List<Documentos> documentos;
    
    public String porte;
    public String razaoSocial;
    public String nomeFantasia;
    public String naturezaJuridica;
    public String cnaeFiscalDescricao;
    public String dataInicioAtividade;
    
    @OneToOne
    public Endereco endereco;
    
    @JsonIgnore
    public boolean processado = false;
}
