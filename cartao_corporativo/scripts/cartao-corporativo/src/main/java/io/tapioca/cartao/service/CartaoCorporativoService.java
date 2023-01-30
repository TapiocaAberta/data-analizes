package io.tapioca.cartao.service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.enterprise.context.ApplicationScoped;
import javax.transaction.Transactional;
import javax.ws.rs.NotFoundException;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.CSVWriter;
import com.opencsv.bean.StatefulBeanToCsv;
import com.opencsv.bean.StatefulBeanToCsvBuilder;
import com.opencsv.exceptions.CsvDataTypeMismatchException;
import com.opencsv.exceptions.CsvException;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;
import com.opencsv.exceptions.CsvValidationException;

import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.tapioca.cartao.model.dto.CartaoCorporativoDTO;
import io.tapioca.cartao.model.dto.MinhaReceitaDTO;
import io.tapioca.cartao.model.entity.CartaoCoporativo;
import io.tapioca.cartao.model.entity.Endereco;
import io.tapioca.cartao.model.entity.Fornecedor;
import io.tapioca.cartao.model.entity.Municipio;
import io.tapioca.cartao.model.entity.Servidor;
import io.tapioca.cartao.model.entity.SubelementoDespesa;
import io.tapioca.cartao.model.entity.TipoDocumento;

/**
 * @author Pedro Hos <pedro-hos@outlook.com>
 *
 */

@ApplicationScoped
public class CartaoCorporativoService {

    private static final Logger LOGGER = LoggerFactory.getLogger(CartaoCorporativoService.class);

    @ConfigProperty(name = "cartao.path")
    String cartaoPath;
    
    @ConfigProperty(name = "data-etl.path")
    String dataETLPath;
    
    public void criaCSV() {
        LOGGER.info("Criando CSV com localização ...");
        
        List<CartaoCorporativoDTO> cartoesDTO = new ArrayList<>();
        PanacheQuery<CartaoCoporativo> cartoes = CartaoCoporativo.findAll().page(0, 1000);
        int paginas = cartoes.pageCount();
        
        while(paginas != 0) {
            
            cartoes.list().forEach(c -> {
                
                CartaoCorporativoDTO cartaoDTO = new CartaoCorporativoDTO();
                
                cartaoDTO.setDataPgto(c.dataPagamento.toString());
                cartaoDTO.setDiaSemanaPgto(c.dataPagamento.getDayOfWeek().getDisplayName(TextStyle.FULL, new Locale ("pt", "BR")));
                cartaoDTO.setDocumentoServidor(c.servidor.cpf);
                cartaoDTO.setDocumentoFornecedor(c.fornecedor.documento);
                cartaoDTO.setTipoDocumento(c.fornecedor.tipoDocumento.name());
                
                String nomeFornecedor = c.fornecedor.nome.isEmpty() ? c.fornecedor.razaoSocial : c.fornecedor.nome;
                
                cartaoDTO.setNomeFornecedor(nomeFornecedor);
                
                if(TipoDocumento.CNPJ.equals(c.fornecedor.tipoDocumento)) {
                
                    cartaoDTO.setPorte(validaSeExiste(c.fornecedor.porte));
                    
                    Endereco endereco = c.fornecedor.endereco;
                    
                    if(Objects.nonNull(endereco)) {
                        Municipio municipio = endereco.municipio;
                        
                        if(Objects.nonNull(municipio)) {
                            cartaoDTO.setMunicipio(municipio.nome);
                            cartaoDTO.setUf(municipio.estado.uf);
                            cartaoDTO.setLatitude(municipio.latitude);
                            cartaoDTO.setLongitude(municipio.longitude);
                        }
                    }
                }
                
                cartaoDTO.setValor(c.valor);
                cartaoDTO.setSubelementoDespesa(c.subelemento.nome);
                cartaoDTO.setMandato(c.mandato);
                
                cartoesDTO.add(cartaoDTO);
            });
            
            
            //chama prox. pg
            cartoes = cartoes.nextPage();
            paginas--;
        }
        
        salvaCartaoCSV(cartoesDTO);
        
    }
    
    /**
     * @param cartoesDTO
     */
    private void salvaCartaoCSV(List<CartaoCorporativoDTO> cartoesDTO) {
        
        cartoesDTO.stream()
                  .collect(Collectors.groupingBy(CartaoCorporativoDTO::getMandato))
                  .forEach((mandato, gastos) -> {
                      String filename = mandato.replace(" ", "").replace("/", "_").toLowerCase().concat(".csv");
                      String dir = dataETLPath + "/" + selecionaPresidentePorMandato(mandato) + "/";
                      createDirectoryIfDoesntExists(dir);
                      Path path = Paths.get( dir + filename);
                      
                      try (var writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {

                          StatefulBeanToCsv<CartaoCorporativoDTO> beanToCsv = new StatefulBeanToCsvBuilder<CartaoCorporativoDTO>(writer)
                                  .withQuotechar(CSVWriter.DEFAULT_QUOTE_CHARACTER)
                                  .withSeparator(CSVWriter.DEFAULT_SEPARATOR)
                                  .build();

                          beanToCsv.write(gastos);

                      } catch (CsvDataTypeMismatchException | CsvRequiredFieldEmptyException | IOException e) {
                          LOGGER.error("Erro ao criar CSV " + filename, e);
                      }
                      
                  });
        
    }
    
    protected void createDirectoryIfDoesntExists(String directoryName) {
        LOGGER.info("Files will be save into " + directoryName + " if you need change it, replace the 'file.path' argument on application.properties file");

        var directory = new File(directoryName);
        
        if (!directory.exists()) {
            directory.mkdirs();
        }
    }

    public String validaSeExiste(String value) {
        
        if(Objects.isNull(value)) {
            return "";
        }
        
        return value;
        
    }
    
    public void carregaDadosCartaoCSV() {
        LOGGER.info("Iniciando carregamento dos dados do cartão corporativo");
        criaGastosComCartao(leCSV());
    }

    @Transactional
    protected void criaGastosComCartao(final List<String[]> linhas) {
        List<CartaoCoporativo> gastos = new ArrayList<>();
        linhas.forEach(l -> gastos.add(criaGastoComCartao(l)));
        CartaoCoporativo.persist(gastos);
    }

    protected CartaoCoporativo criaGastoComCartao(String[] linha) {

        String dataPgtoStr = linha[0];
        String cpfServidorStr = linha[1];
        String documentoStr = linha[2];
        String nomeFornecedorStr = linha[3];
        String valorStr = linha[4];
        String subelemento = linha[6];
        
        LOGGER.info("Gasto do dia " + dataPgtoStr + " para o fornecedor " + nomeFornecedorStr + " sendo processado!");

        CartaoCoporativo cartao = new CartaoCoporativo();
        
        cartao.dataPagamento = formataData(dataPgtoStr);
        cartao.mandato = selecionaMandato(cartao.dataPagamento.getYear());
        cartao.valor = formataValor(valorStr);
        cartao.servidor = criaOuRecuperaServidor(cpfServidorStr);
        cartao.subelemento = criaOuRecuperaSubelemento(subelemento);
        cartao.fornecedor = criaOuRecuperaFornecedor(documentoStr, nomeFornecedorStr);
        
        return cartao;
        
    }

    private Fornecedor criaOuRecuperaFornecedor(String documentoStr, String nomeFornecedorStr) {
        return Fornecedor.comDocumento(documentoStr).orElseGet(() -> criaFornecedor(documentoStr, nomeFornecedorStr));
    }

    private Fornecedor criaFornecedor(String documentoStr, String nomeFornecedorStr) {

        Fornecedor fornecedor = new Fornecedor();
        fornecedor.documento = documentoStr;
        fornecedor.tipoDocumento = selecionaTipo(documentoStr);

        if (TipoDocumento.CNPJ.equals(fornecedor.tipoDocumento)) {
            
            try {
                
                MinhaReceitaDTO dadosCnpj = MinhaReceitaService.buscaDadosCnpj(documentoStr);
                fornecedor.cnaeFiscalDescricao = dadosCnpj.cnae_fiscal_descricao;
                fornecedor.dataInicioAtividade = dadosCnpj.data_inicio_atividade;
                fornecedor.naturezaJuridica = dadosCnpj.natureza_juridica;
                fornecedor.nome = dadosCnpj.nome_fantasia;
                fornecedor.porte = dadosCnpj.porte;
                fornecedor.razaoSocial = dadosCnpj.razao_social;
                
                Endereco endereco = new Endereco();
                endereco.logradouro = dadosCnpj.logradouro;
                endereco.bairro = dadosCnpj.bairro;
                endereco.cep = dadosCnpj.cep;
                endereco.numero = dadosCnpj.numero;
                endereco.municipio = Municipio.comCodigoIbge(dadosCnpj.codigo_municipio_ibge).orElseGet(() -> null);
                endereco.persist();
                
                fornecedor.endereco = endereco;
                fornecedor.persistAndFlush();
                
            } catch (NotFoundException e) {
                LOGGER.error("CNPJ " + documentoStr + " não encontrado em Minha Receita", e);
                fornecedor.nome = nomeFornecedorStr;
                fornecedor.persistAndFlush();
            }

        } else {
            fornecedor.nome = nomeFornecedorStr;
            fornecedor.persistAndFlush();
            return fornecedor;
        }
        
        return fornecedor;
    }

    private TipoDocumento selecionaTipo(String documentoStr) {

        if (documentoStr.length() == 0) {
            return TipoDocumento.SEM_DOCUMENTO;
        } else if (documentoStr.length() == 14) {
            return TipoDocumento.CNPJ;
        } else if (documentoStr.length() == 11) {
            return TipoDocumento.CPF;
        }

        return TipoDocumento.SEM_Tipo;
    }

    private SubelementoDespesa criaOuRecuperaSubelemento(String nome) {
        return SubelementoDespesa.comNome(nome).orElseGet(() -> criaSubelemento(nome));
    }

    private SubelementoDespesa criaSubelemento(String nome) {
        SubelementoDespesa subelemento = new SubelementoDespesa();
        subelemento.nome = nome;
        subelemento.persistAndFlush();
        return subelemento;
    }

    private Servidor criaOuRecuperaServidor(String cpfServidorStr) {
        return Servidor.comCPF(cpfServidorStr).orElseGet(() -> novoServidor(cpfServidorStr));
    }

    private Servidor novoServidor(String cpfServidorStr) {
        Servidor servidor = new Servidor();
        servidor.cpf = cpfServidorStr;
        servidor.persistAndFlush();
        return servidor;
    }

    private Double formataValor(String valorStr) {
        Double valor;
        
        try {
            valor = Double.valueOf(valorStr.replace("R$", "").replace(".", "").replace(",", "."));
        } catch (NumberFormatException e) {
            LOGGER.error("Não foi possível realizar o parse do valor " + valorStr, e);
            valor = Double.valueOf(0);
        }
        
        return valor;
    }
    
    public String selecionaPresidentePorMandato(String mandato) {
        
        switch (mandato) {
        
        case "LULA 1", "LULA 2" -> {return "lula";}
        case "DILMA 1", "DILMA 2" -> {return "dilma";}
        case "DILMA 2/TEMER" -> {return "dilma-temer";}
        case "TEMER" -> {return "temer";}
        case "BOLSONARO" -> {return "bolsonaro";}
        case "NA" -> {return "na";}
        
        default ->
            throw new IllegalArgumentException("Valor não esperado de mandato: " + mandato);
        }
    }

    private String selecionaMandato(int ano) {

        if (ano >= 2003 && ano <= 2006) {
            return "LULA 1";
        } else if (ano >= 2007 && ano <= 2010) {
            return "LULA 2";
        } else if (ano >= 2011 && ano <= 2014) {
            return "DILMA 1";
        } else if (ano == 2015) {
            return "DILMA 2";
        } else if (ano == 2016) {
            return "DILMA 2/TEMER";
        } else if (ano >= 2017 && ano <= 2018) {
            return "TEMER";
        } else if (ano >= 2019 && ano <= 2022) {
            return "BOLSONARO";
        }

        return "NA";
    }

    private LocalDate formataData(String dataPgtoStr) {
        return LocalDate.parse(dataPgtoStr, DateTimeFormatter.ofPattern("dd/MM/yyyy"));
    }

    protected List<String[]> leCSV() {

        try (CSVReader reader = new CSVReaderBuilder(new FileReader(cartaoPath))
                .withCSVParser(new CSVParserBuilder().withSeparator(';').build()).withSkipLines(1).build()) {

            return reader.readAll();

        } catch (FileNotFoundException e) {
            LOGGER.error("Erro durante a leitura do CSV: ", e);
        } catch (IOException e) {
            LOGGER.error("Erro durante a leitura do CSV: ", e);
        } catch (CsvValidationException e) {
            LOGGER.error("Erro durante a leitura do CSV: ", e);
        } catch (CsvException e) {
            LOGGER.error("Erro durante a leitura do CSV: ", e);
        }

        return Arrays.asList();

    }

}
