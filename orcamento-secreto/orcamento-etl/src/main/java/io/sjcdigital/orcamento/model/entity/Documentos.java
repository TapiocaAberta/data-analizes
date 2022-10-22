package io.sjcdigital.orcamento.model.entity;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import com.fasterxml.jackson.annotation.JsonIgnore;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import io.sjcdigital.orcamento.model.pojo.DocumentosDataPojo;

/**
 * @author Pedro Hos <pedro-hos@outlook.com>
 *
 */

@Entity(name = "documentos")
@Table(uniqueConstraints =  @UniqueConstraint(columnNames = {"fase", "codigoDocumento"}))
public class Documentos extends PanacheEntity {
    
    public String data;
    public String fase;
    public String codigoDocumento;
    public String codigoDocumentoResumido;
    public String especieTipo;
    
    @Column(length = 1000)
    public String observacao;
    
    public String tipo;
    public String valorDocumento;
    
    @Column(length = 1000)
    public String descricao;
    
    //Flags para processo de detalhes
    @JsonIgnore
    public Boolean processando = Boolean.FALSE;
    
    @JsonIgnore
    public Boolean processado  = Boolean.FALSE;
    
    @JsonIgnore
    public Boolean erro = Boolean.FALSE;
    
    @JsonIgnore
    public Boolean pgDetalhesNotFound = Boolean.FALSE;
    
    @ManyToOne
    @JoinColumn(name="favorecido_id")
    public Favorecido favorecido;
    
    @ManyToOne
    @JoinColumn(name="orgaoPagador_id")
    public OrgaoPagador orgaoPagador;
    
    @JsonIgnore
    @ManyToMany(mappedBy = "documentos")
    public List<Emendas> emenda = new ArrayList<>();
    
    public Documentos() { }
    
    public Documentos(String fase, String codigoDocumento) { 
        this.codigoDocumento = codigoDocumento;
        this.fase = fase;
    }

    /**
     * @param data
     * @param fase
     * @param codigoDocumento
     * @param codigoDocumentoResumido
     * @param especieTipo
     * @param observacao
     * @param tipo
     * @param valorDocumento
     * @param descricao
     * @param favorecido
     * @param orgaoPagador
     */
    public Documentos(  String data, String fase, String codigoDocumento, String codigoDocumentoResumido,
                        String especieTipo, String observacao, String tipo, String valorDocumento, String descricao,
                        Favorecido favorecido, OrgaoPagador orgaopagador, List<Emendas> emendas) {
        super();
        this.data = data;
        this.fase = fase;
        this.codigoDocumento = codigoDocumento;
        this.codigoDocumentoResumido = codigoDocumentoResumido;
        this.especieTipo = especieTipo;
        this.observacao = observacao;
        this.tipo = tipo;
        this.valorDocumento = valorDocumento;
        this.descricao = descricao;
        this.favorecido = favorecido;
        this.orgaoPagador = orgaopagador;
        this.emenda = emendas;
    }
    
    public static Documentos fromDocumentoDataPojo(DocumentosDataPojo pojo) {
        return new Documentos(  pojo.getData(), 
                                pojo.getFase(), 
                                pojo.getCodigoDocumento(), 
                                pojo.getCodigoDocumentoResumido(), 
                                pojo.getEspecieTipo(), 
                                null, null, null, null, null, null, new ArrayList<>());
    }
    
    

}
