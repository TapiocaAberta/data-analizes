package io.sjcdigital.orcamento.model.entity;

/**
 * @author Pedro Hos <pedro-hos@outlook.com>
 *
 */
public class Documentos {
    
    public String data;
    public String fase;
    public String codigoDocumento;
    public String codigoDocumentoResumido;
    public String especieTipo;
    public String observacao;
    public String tipo;
    public String valorDocumento;
    public String descricao;
    public Favorecido favorecido;
    public OrgaoPagador orgaopagador;
    
    
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
     * @param orgaopagador
     */
    public Documentos(  String data, String fase, String codigoDocumento, String codigoDocumentoResumido,
                        String especieTipo, String observacao, String tipo, String valorDocumento, String descricao,
                        Favorecido favorecido, OrgaoPagador orgaopagador) {
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
        this.orgaopagador = orgaopagador;
    }
    
    

}
