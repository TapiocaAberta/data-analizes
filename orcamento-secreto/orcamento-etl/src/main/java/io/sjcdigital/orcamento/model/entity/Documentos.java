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
@Table(uniqueConstraints =  @UniqueConstraint(columnNames = {"codigoDocumento"}))
public class Documentos extends PanacheEntity {
    
    private String data;
    private String fase;
    private String codigoDocumento;
    private String codigoDocumentoResumido;
    private String especieTipo;
    
    @Column(length = 1000)
    private String observacao;
    
    private String tipo;
    private String valorDocumento;
    
    @Column(length = 1000)
    private String descricao;
    
    //Flags para processo de detalhes
    @JsonIgnore
    private Boolean processando = Boolean.FALSE;
    
    @JsonIgnore
    private Boolean processado  = Boolean.FALSE;
    
    @JsonIgnore
    private Boolean erro = Boolean.FALSE;
    
    @JsonIgnore
    private Boolean pgDetalhesNotFound = Boolean.FALSE;
    
    @ManyToOne
    @JoinColumn(name="favorecido_id")
    private Favorecido favorecido;
    
    @ManyToOne
    @JoinColumn(name="orgaoPagador_id")
    private OrgaoPagador orgaoPagador;
    
    @JsonIgnore
    @ManyToMany(mappedBy = "documentos")
    private List<Emendas> emenda = new ArrayList<>();
    
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

    /**
     * @return the data
     */
    public String getData() {
        return data;
    }

    /**
     * @param data the data to set
     */
    public void setData(String data) {
        this.data = data;
    }

    /**
     * @return the fase
     */
    public String getFase() {
        return fase;
    }

    /**
     * @param fase the fase to set
     */
    public void setFase(String fase) {
        this.fase = fase;
    }

    /**
     * @return the codigoDocumento
     */
    public String getCodigoDocumento() {
        return codigoDocumento;
    }

    /**
     * @param codigoDocumento the codigoDocumento to set
     */
    public void setCodigoDocumento(String codigoDocumento) {
        this.codigoDocumento = codigoDocumento;
    }

    /**
     * @return the codigoDocumentoResumido
     */
    public String getCodigoDocumentoResumido() {
        return codigoDocumentoResumido;
    }

    /**
     * @param codigoDocumentoResumido the codigoDocumentoResumido to set
     */
    public void setCodigoDocumentoResumido(String codigoDocumentoResumido) {
        this.codigoDocumentoResumido = codigoDocumentoResumido;
    }

    /**
     * @return the especieTipo
     */
    public String getEspecieTipo() {
        return especieTipo;
    }

    /**
     * @param especieTipo the especieTipo to set
     */
    public void setEspecieTipo(String especieTipo) {
        this.especieTipo = especieTipo;
    }

    /**
     * @return the observacao
     */
    public String getObservacao() {
        return observacao;
    }

    /**
     * @param observacao the observacao to set
     */
    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }

    /**
     * @return the tipo
     */
    public String getTipo() {
        return tipo;
    }

    /**
     * @param tipo the tipo to set
     */
    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    /**
     * @return the valorDocumento
     */
    public String getValorDocumento() {
        return valorDocumento;
    }

    /**
     * @param valorDocumento the valorDocumento to set
     */
    public void setValorDocumento(String valorDocumento) {
        this.valorDocumento = valorDocumento;
    }

    /**
     * @return the descricao
     */
    public String getDescricao() {
        return descricao;
    }

    /**
     * @param descricao the descricao to set
     */
    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    /**
     * @return the processando
     */
    public Boolean getProcessando() {
        return processando;
    }

    /**
     * @param processando the processando to set
     */
    public void setProcessando(Boolean processando) {
        this.processando = processando;
    }

    /**
     * @return the processado
     */
    public Boolean getProcessado() {
        return processado;
    }

    /**
     * @param processado the processado to set
     */
    public void setProcessado(Boolean processado) {
        this.processado = processado;
    }

    /**
     * @return the erro
     */
    public Boolean getErro() {
        return erro;
    }

    /**
     * @param erro the erro to set
     */
    public void setErro(Boolean erro) {
        this.erro = erro;
    }

    /**
     * @return the pgDetalhesNotFound
     */
    public Boolean getPgDetalhesNotFound() {
        return pgDetalhesNotFound;
    }

    /**
     * @param pgDetalhesNotFound the pgDetalhesNotFound to set
     */
    public void setPgDetalhesNotFound(Boolean pgDetalhesNotFound) {
        this.pgDetalhesNotFound = pgDetalhesNotFound;
    }

    /**
     * @return the favorecido
     */
    public Favorecido getFavorecido() {
        return favorecido;
    }

    /**
     * @param favorecido the favorecido to set
     */
    public void setFavorecido(Favorecido favorecido) {
        this.favorecido = favorecido;
    }

    /**
     * @return the orgaoPagador
     */
    public OrgaoPagador getOrgaoPagador() {
        return orgaoPagador;
    }

    /**
     * @param orgaoPagador the orgaoPagador to set
     */
    public void setOrgaoPagador(OrgaoPagador orgaoPagador) {
        this.orgaoPagador = orgaoPagador;
    }

    /**
     * @return the emenda
     */
    public List<Emendas> getEmenda() {
        return emenda;
    }

    /**
     * @param emenda the emenda to set
     */
    public void setEmenda(List<Emendas> emenda) {
        this.emenda = emenda;
    }
    
    

}
