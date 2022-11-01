package io.sjcdigital.orcamento.model.pojo;

/**
 * @author Pedro Hos <pedro-hos@outlook.com>
 *
 */

public class MinhaReceitaPojo {

    public String uf;
    public String cep;
    public String bairro;
    public String numero;
    public String municipio;
    public String logradouro;
    public String codigo_municipio;
    public String codigo_municipio_ibge;

    public String porte;
    public String razao_social;
    public String nome_fantasia;
    public String natureza_juridica;
    public String cnae_fiscal_descricao;
    public String data_inicio_atividade;
    
    public MinhaReceitaPojo() {}

    @Override
    public String toString() {
        return "MinhaReceitaPojo [uf=" + uf + ", cep=" + cep + ", bairro=" + bairro + ", numero=" + numero
                + ", municipio=" + municipio + ", logradouro=" + logradouro + ", codigoMunicipio=" + codigo_municipio
                + ", codigoMunicipioIbge=" + codigo_municipio_ibge + ", porte=" + porte + ", razao_social="
                + razao_social + ", nome_fantasia=" + nome_fantasia + ", natureza_juridica=" + natureza_juridica
                + ", cnae_fiscal_descricao=" + cnae_fiscal_descricao + ", data_inicio_atividade="
                + data_inicio_atividade + "]";
    }

}
