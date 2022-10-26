package io.sjcdigital.orcamento.model.entity;

import javax.persistence.Entity;

import io.quarkus.hibernate.orm.panache.PanacheEntity;

/**
 * @author Pedro Hos <pedro-hos@outlook.com>
 *
 */

@Entity
public class DetalhesErro extends PanacheEntity {
    
    private String codigoDocumento;
    private String fase;
    private String descricao;
    private String url;
    
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
     * @return the url
     */
    public String getUrl() {
        return url;
    }
    /**
     * @param url the url to set
     */
    public void setUrl(String url) {
        this.url = url;
    }

}
