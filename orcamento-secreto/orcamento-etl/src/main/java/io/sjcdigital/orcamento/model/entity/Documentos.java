package io.sjcdigital.orcamento.model.entity;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import com.fasterxml.jackson.annotation.JsonIgnore;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import io.sjcdigital.orcamento.model.pojo.DocumentosDataPojo;

/**
 * @author Pedro Hos <pedro-hos@outlook.com>
 *
 */

@Entity(name = "documentos")
public class Documentos extends PanacheEntity {
    
    public String data;
    public String fase;
    public String codigoDocumento;
    public String codigoDocumentoResumido;
    public String especieTipo;
    public String observacao;
    public String tipo;
    public String valorDocumento;
    public String descricao;
    
    @ManyToOne
    @JoinColumn(name="favorecido_id")
    public Favorecido favorecido;
    
    @ManyToOne
    @JoinColumn(name="orgaoPagador_id")
    public OrgaoPagador orgaoPagador;
    
    @ManyToOne
    @JoinColumn(name="emenda_id")
    @JsonIgnore
    public Emendas emenda;
    
    
    public Documentos() { }


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
                        Favorecido favorecido, OrgaoPagador orgaopagador, Emendas emendas) {
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
    
    public static Documentos fromDocumentoDataPojo(DocumentosDataPojo pojo, Emendas emenda) {
        return new Documentos(  pojo.getData(), 
                                pojo.getFase(), 
                                pojo.getCodigoDocumento(), 
                                pojo.getCodigoDocumentoResumido(), 
                                pojo.getEspecieTipo(), 
                                null, null, null, null, null, null, emenda);
    }
    
    

}
