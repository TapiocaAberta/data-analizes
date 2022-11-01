package io.sjcdigital.orcamento.service;

import java.util.List;
import java.util.Objects;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.transaction.Transactional;

import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.quarkus.runtime.annotations.RegisterForReflection;
import io.sjcdigital.orcamento.model.entity.Endereco;
import io.sjcdigital.orcamento.model.entity.Estado;
import io.sjcdigital.orcamento.model.entity.Favorecido;
import io.sjcdigital.orcamento.model.entity.Municipio;
import io.sjcdigital.orcamento.model.pojo.MinhaReceitaPojo;
import io.sjcdigital.orcamento.model.repository.EnderecoRepository;
import io.sjcdigital.orcamento.model.repository.EstadoRepository;
import io.sjcdigital.orcamento.model.repository.FavorecidoRepository;
import io.sjcdigital.orcamento.model.repository.MunicipioRepository;
import io.sjcdigital.orcamento.resource.client.MinhaReceitaClient;


/**
 * @author Pedro Hos <pedro-hos@outlook.com>
 *
 */

@ApplicationScoped
@Named("favorecidoBean")
@RegisterForReflection
public class FavorecidoService extends Service {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(FavorecidoService.class);
    
    @Inject
    FavorecidoRepository favorecidoRepository;
    
    @Inject
    MunicipioRepository municipioRepository;
    
    @Inject
    EstadoRepository estadoRepository;
    
    @Inject
    EnderecoRepository enderecoRepository;
    
    /**
     * 
     */
    private static final String URL = "https://minhareceita.org";

    
    @Transactional
    public void atualizaPessoaFisicaInfos(List<Long> ids) {
        
        LOGGER.info("Iniciado processo!");
        
        List<Favorecido> favorecidos = favorecidoRepository.findByIds(ids);
        
        favorecidos.forEach(f -> {
            
            if(f.docFavorecido.length() == 18) {
                
                MinhaReceitaPojo minhaReceita = buscaDadosCnpj(f.docFavorecido);
                
                f.porte = minhaReceita.porte;
                f.razaoSocial = minhaReceita.razao_social;
                f.nomeFantasia = minhaReceita.nome_fantasia;
                f.naturezaJuridica = minhaReceita.natureza_juridica;
                f.cnaeFiscalDescricao = minhaReceita.cnae_fiscal_descricao;
                f.dataInicioAtividade = minhaReceita.data_inicio_atividade;
                
                montaEnderco(f, minhaReceita);
                
                f.processado = true;
                favorecidoRepository.persist(f);
                
            }
            
        });
        
        LOGGER.info("Finalizado processo!");
        
    }
    
    /**
     * @param f
     * @param minhaReceita
     */
    @Transactional
    public void montaEnderco(Favorecido f, MinhaReceitaPojo minhaReceita) {
        
        Endereco endereco = new Endereco();
        endereco.cep = minhaReceita.cep;
        endereco.bairro = minhaReceita.bairro;
        endereco.numero = minhaReceita.numero;
        endereco.logradouro = minhaReceita.logradouro;
        
        Municipio municipio = municipioRepository.findByCodigos(minhaReceita.codigo_municipio, minhaReceita.codigo_municipio_ibge);
        
        if(Objects.isNull(municipio)) {
            
            municipio = new Municipio();
            municipio.municipio = minhaReceita.municipio;
            municipio.codigoMunicipio = minhaReceita.codigo_municipio;
            municipio.codigoMunicipioIbge = minhaReceita.codigo_municipio_ibge;
            
            Estado estado = estadoRepository.findByUf(minhaReceita.uf);
            
            if(Objects.isNull(estado)) {
                estado = new Estado();
                estado.uf = minhaReceita.uf;
                estadoRepository.persist(estado);
            }
            
            municipio.estado = estado;
            municipioRepository.persist(municipio);
        } 
        
        endereco.municipio = municipio;
        enderecoRepository.persist(endereco);
        f.endereco = endereco;
    }

    /**
     * @param cnpj
     * @return
     */
    private MinhaReceitaPojo buscaDadosCnpj(final String cnpj) {
        ResteasyWebTarget target = getTarget(URL);
        MinhaReceitaClient proxy = target.proxy(MinhaReceitaClient.class);
        MinhaReceitaPojo pojo = proxy.getCNPJInfo(cnpj);
        target.getResteasyClient().close();
        return pojo;
    }

}
