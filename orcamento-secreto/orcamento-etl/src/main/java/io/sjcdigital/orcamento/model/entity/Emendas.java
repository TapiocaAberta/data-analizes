package io.sjcdigital.orcamento.model.entity;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;

/**
 * @author Pedro Hos <pedro-hos@outlook.com>
 *
 */
public class Emendas {
    
    public Long id;
    public String ano;
    public String autor;
    public String numeroEmenda;
    public String localidade;
    public String funcao; 
    public String subfuncao;
    public String empenhado; 
    public String liquido;
    public String pago;
    public String codigoEmenda;
    public List<Documentos> documentos = new ArrayList<>();
    
    public Emendas() {}

    @JsonCreator
    public Emendas(Long id, String ano, String autor, String numeroEmenda, String localidade, String funcao,
            String subfuncao, String empenhado, String liquido, String pago, String codigoEmenda, List<Documentos> documentos) {
        
        super();
        this.id = id;
        this.ano = ano;
        this.autor = autor;
        this.numeroEmenda = numeroEmenda;
        this.localidade = localidade;
        this.funcao = funcao;
        this.subfuncao = subfuncao;
        this.empenhado = empenhado;
        this.liquido = liquido;
        this.pago = pago;
        this.codigoEmenda = codigoEmenda;
        this.documentos = documentos;
    }
    
    
    

}
