package io.tapioca.cartao.model.dto;

import com.opencsv.bean.CsvBindByName;

/**
 * @author Pedro Hos <pedro-hos@outlook.com>
 *
 */
public class CartaoCorporativoDTO {
    
    @CsvBindByName
    private String dataPgto;
    
    @CsvBindByName
    private String diaSemanaPgto;
    
    @CsvBindByName
    private String documentoServidor;
    
    @CsvBindByName
    private String documentoFornecedor;
    
    @CsvBindByName
    private String tipoDocumento;
    
    @CsvBindByName
    private String nomeFornecedor;
    
    @CsvBindByName
    private String porte;
    
    @CsvBindByName
    private String municipio;
    
    @CsvBindByName
    private String uf;
    
    @CsvBindByName
    private String latitude;
    
    @CsvBindByName
    private String longitude;
    
    @CsvBindByName
    private Double valor;
    
    @CsvBindByName
    private String subelementoDespesa;
    
    @CsvBindByName
    private String mandato;

    /**
     * @return the dataPgto
     */
    public String getDataPgto() {
        return dataPgto;
    }

    /**
     * @param dataPgto the dataPgto to set
     */
    public void setDataPgto(String dataPgto) {
        this.dataPgto = dataPgto;
    }

    /**
     * @return the diaSemanaPgto
     */
    public String getDiaSemanaPgto() {
        return diaSemanaPgto;
    }

    /**
     * @param diaSemanaPgto the diaSemanaPgto to set
     */
    public void setDiaSemanaPgto(String diaSemanaPgto) {
        this.diaSemanaPgto = diaSemanaPgto;
    }

    /**
     * @return the documentoServidor
     */
    public String getDocumentoServidor() {
        return documentoServidor;
    }

    /**
     * @param documentoServidor the documentoServidor to set
     */
    public void setDocumentoServidor(String documentoServidor) {
        this.documentoServidor = documentoServidor;
    }

    /**
     * @return the documentoFornecedor
     */
    public String getDocumentoFornecedor() {
        return documentoFornecedor;
    }

    /**
     * @param documentoFornecedor the documentoFornecedor to set
     */
    public void setDocumentoFornecedor(String documentoFornecedor) {
        this.documentoFornecedor = documentoFornecedor;
    }

    /**
     * @return the tipoDocumento
     */
    public String getTipoDocumento() {
        return tipoDocumento;
    }

    /**
     * @param tipoDocumento the tipoDocumento to set
     */
    public void setTipoDocumento(String tipoDocumento) {
        this.tipoDocumento = tipoDocumento;
    }

    /**
     * @return the nomeFornecedor
     */
    public String getNomeFornecedor() {
        return nomeFornecedor;
    }

    /**
     * @param nomeFornecedor the nomeFornecedor to set
     */
    public void setNomeFornecedor(String nomeFornecedor) {
        this.nomeFornecedor = nomeFornecedor;
    }

    /**
     * @return the porte
     */
    public String getPorte() {
        return porte;
    }

    /**
     * @param porte the porte to set
     */
    public void setPorte(String porte) {
        this.porte = porte;
    }

    /**
     * @return the municipio
     */
    public String getMunicipio() {
        return municipio;
    }

    /**
     * @param municipio the municipio to set
     */
    public void setMunicipio(String municipio) {
        this.municipio = municipio;
    }

    /**
     * @return the uf
     */
    public String getUf() {
        return uf;
    }

    /**
     * @param uf the uf to set
     */
    public void setUf(String uf) {
        this.uf = uf;
    }

    /**
     * @return the latitude
     */
    public String getLatitude() {
        return latitude;
    }

    /**
     * @param latitude the latitude to set
     */
    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    /**
     * @return the longitude
     */
    public String getLongitude() {
        return longitude;
    }

    /**
     * @param longitude the longitude to set
     */
    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    /**
     * @return the valor
     */
    public Double getValor() {
        return valor;
    }

    /**
     * @param valor the valor to set
     */
    public void setValor(Double valor) {
        this.valor = valor;
    }

    /**
     * @return the subelementoDespesa
     */
    public String getSubelementoDespesa() {
        return subelementoDespesa;
    }

    /**
     * @param subelementoDespesa the subelementoDespesa to set
     */
    public void setSubelementoDespesa(String subelementoDespesa) {
        this.subelementoDespesa = subelementoDespesa;
    }

    /**
     * @return the mandato
     */
    public String getMandato() {
        return mandato;
    }

    /**
     * @param mandato the mandato to set
     */
    public void setMandato(String mandato) {
        this.mandato = mandato;
    }
}
