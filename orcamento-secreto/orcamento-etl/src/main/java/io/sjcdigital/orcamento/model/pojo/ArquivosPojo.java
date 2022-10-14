package io.sjcdigital.orcamento.model.pojo;

/**
 * @author Pedro Hos <pedro-hos@outlook.com>
 *
 */
public class ArquivosPojo {
        
    public String codigoEmenda;
    public String funcao;
    public String subFuncao;
    public String arquivo;

    public ArquivosPojo() { }

    /**
     * @param numeroEmenda
     * @param funcao
     * @param subFuncao
     * @param arquivo
     */
    public ArquivosPojo(String codigoEmenda, String funcao, String subFuncao, String arquivo) {
        super();
        this.codigoEmenda = codigoEmenda;
        this.funcao = funcao;
        this.subFuncao = subFuncao;
        this.arquivo = arquivo;
    }
    
}
