package io.tapioca.cartao.utils;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.exceptions.CsvException;
import com.opencsv.exceptions.CsvValidationException;

public class EstadoMunicipioSQLUtil {

    public static void main(String[] args) {
        
        criaSQLEstado();
        criaSQLMunicipio();
        
    }

    private static void criaSQLMunicipio() {
        
        Map<String, Integer> estados = new HashMap<>();
                
        estados.put("AC", 1); 
        estados.put("AL", 2); 
        estados.put("AM", 3); 
        estados.put("AP", 4); 
        estados.put("BA", 5);
        estados.put("CE", 6); 
        estados.put("DF", 7); 
        estados.put("ES", 8);
        estados.put("GO", 9); 
        estados.put("MA", 10);
        estados.put("MG", 11);
        estados.put("MS", 12);
        estados.put("MT", 13);
        estados.put("PA", 14);
        estados.put("PB", 15);
        estados.put("PE", 16);
        estados.put("PI", 17);
        estados.put("PR", 18);
        estados.put("RJ", 19);
        estados.put("RN", 20);
        estados.put("RO", 21);
        estados.put("RR", 22);
        estados.put("RS", 23);
        estados.put("SC", 24);
        estados.put("SE", 25);
        estados.put("SP", 26);
        estados.put("TO", 27);
        
        String arquivo = "/home/pesilva/workspace/code/pessoal/municipios-br/tabelas/municipios.csv";
        
        try (CSVReader reader = new CSVReaderBuilder(new FileReader(arquivo)).withSkipLines(1).build()) {

            List<String[]> documento = reader.readAll();
            
            for(int i = 0; i < documento.size(); i++) {
                
                String[] linha = documento.get(i);
                int id = i+1;
                
                StringBuilder sql = new StringBuilder("INSERT INTO Municipio "
                        + "(id, estado_id, codigo, nome, capital, latitude, longitude, nomeSemAcento, populacao) "
                        + "VALUES(");
                
                sql.append(id + ", ");
                sql.append(estados.get(linha[2]) + ", ");
                sql.append("\"" + linha[0] + "\", ");
                sql.append("\"" + linha[3] + "\", ");
                
                Boolean ehCapital = !linha[10].isBlank();
                
                sql.append(ehCapital + ", ");
                
                sql.append("\"" + linha[12] + "\", ");
                sql.append("\"" + linha[13] + "\", ");
                sql.append("\"" + linha[14] + "\", ");
                sql.append(linha[17]);
                sql.append(");");
                
                System.out.println(sql);
            }
                        

        } catch (FileNotFoundException e) {
        } catch (IOException e) {
        } catch (CsvValidationException e) {
        } catch (CsvException e) {
        }
        

    }

    private static void criaSQLEstado() {
        String arquivo = "/home/pesilva/workspace/code/pessoal/municipios-br/tabelas/ufs.csv";
        
        try (CSVReader reader = new CSVReaderBuilder(new FileReader(arquivo)).withSkipLines(1).build()) {

            List<String[]> documento = reader.readAll();
            
            for(int i = 0; i < documento.size(); i++) {
                
                String[] linha = documento.get(i);
                int id = i+1;
                
                StringBuilder sql = new StringBuilder("INSERT INTO Estado "
                        + "(id, ufCode, uf, nome, nomeSemAcento, gentilico, gentilicoAlternativo, macroregiao,timezone, website, bandeira) "
                        + "VALUES(");
                sql.append(id + ", ");
                sql.append("'" + linha[0] + "', ");
                sql.append("'" + linha[1] + "', ");
                sql.append("'" + linha[5] + "', ");
                sql.append("'" + linha[6] + "', ");
                sql.append("'" + linha[7] + "', ");
                sql.append("'" + linha[8] + "', ");
                sql.append("'" + linha[9] + "', ");
                sql.append("'" + linha[11] + "', ");
                sql.append("'" + linha[12] + "', ");
                sql.append("'" + linha[13] + "'");
                sql.append(");");
                
                System.out.println(sql);
            }
                        

        } catch (FileNotFoundException e) {
        } catch (IOException e) {
        } catch (CsvValidationException e) {
        } catch (CsvException e) {
        }
    }
    
}