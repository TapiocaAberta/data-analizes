package io.sjcdigital.orcamento.utils;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;

/**
 * @author Pedro Hos <pedro-hos@outlook.com>
 *
 */
public class PropertiesUtil {
    
    private static final String PATH = "/home/pesilva/workspace/code/pessoal/data-analizes/orcamento-secreto/data/offsets.properties";
    
    public static void salvaOffset(Long id, int offset) {
        
        try {

            InputStream input = new FileInputStream(PATH);
            Properties prop = new Properties();
            prop.load(input);
            prop.setProperty("emenda." + id, "" + offset);
            OutputStream output = new FileOutputStream(PATH);
            prop.store(output, null);

        } catch (IOException ex) {
            ex.printStackTrace();
        }
        
    }
    
    public static int pegaOffsetEmenda(Long id) {
        
        try (InputStream input = new FileInputStream(PATH)) {

            Properties prop = new Properties();
            prop.load(input);

            return Integer.parseInt(prop.getProperty("emenda." + id, "0"));
            
        } catch (IOException ex) {
            ex.printStackTrace();
            return 0;
        }
        
        
    }

}
