package io.sjcdigital.orcamento.model.entity;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;

import com.fasterxml.jackson.annotation.JsonIgnore;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import io.sjcdigital.orcamento.model.pojo.DocumentosRelacionadosPojo;
import io.sjcdigital.orcamento.model.pojo.EmendasPojo;

/**
 * @author Pedro Hos <pedro-hos@outlook.com>
 *
 */

@Entity(name = "emendas")
public class Emendas extends PanacheEntity {
    
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
    public String valorRestoInscrito;
    public String valorRestoCancelado;
    public String valorRestoPago;
    public String valorRestoAPagar;
    public int quantidadeDocumentos;
    
    @JsonIgnore
    public int documentoOffset = 0;
    
    //Flags para processamento de documentos
    
    @JsonIgnore
    public Boolean processando = Boolean.FALSE;
    
    @JsonIgnore
    public Boolean processado = Boolean.FALSE;
    
    @JsonIgnore
    public Boolean erro = Boolean.FALSE;
    
    @JsonIgnore
    public boolean muitosDocumentos = Boolean.FALSE;
    
    @ManyToMany
    @JoinTable(name = "emendas_documentos")
    public List<Documentos> documentos = new ArrayList<>();
    
    public Emendas() {}
    
    public Emendas(String ano, String autor, String numeroEmenda, String localidade, String funcao,
            String subfuncao, String empenhado, String liquido, String pago, String codigoEmenda, String valorRestoInscrito, String valorRestoCancelado,
            String valorRestoPago, String valorRestoAPagar) {
        
        super();
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
        this.valorRestoInscrito = valorRestoInscrito;
        this.valorRestoAPagar = valorRestoAPagar;
        this.valorRestoCancelado = valorRestoCancelado;
        this.valorRestoPago = valorRestoPago;
    }
    
    /**
     * @param parseLong
     */
    public Emendas(long id, String codigoEmenda) {
        this.id = id;
        this.codigoEmenda = codigoEmenda;
    }

    public static List<Emendas> fromEmendaPojo(EmendasPojo pojo) {
        
        List<Emendas> emendas = new ArrayList<>();
        
        pojo.getData().forEach(d -> {
            emendas.add(new Emendas(d.getAno(), d.getAutor(), d.getNumeroEmenda(), d.getLocalidadeDoGasto(), 
                    d.getFuncao(), d.getSubfuncao(), d.getValorEmpenhado(), d.getValorLiquidado(), d.getValorPago(), d.getCodigoEmenda(), 
                    d.getValorRestoInscrito(), d.getValorRestoCancelado(), d.getValorRestoPago(), d.getValorRestoAPagar()));
        });
        
        return emendas;
        
    }
    
    public static List<Documentos> criaDocumentos (DocumentosRelacionadosPojo pojo) {
        List<Documentos> documentos = new ArrayList<>();
        pojo.getData().forEach(p -> documentos.add(Documentos.fromDocumentoDataPojo(p)));
        return documentos;
    }
    

}
