package io.sjcdigital.orcamento.model.pojo;

/**
 * @author Pedro Hos <pedro-hos@outlook.com>
 *
 */
public class DataPojo {

    private String data;
    private String fase;
    private String codigoDocumento;
    private String codigoDocumentoResumido;
    private String especieTipo;

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

}
