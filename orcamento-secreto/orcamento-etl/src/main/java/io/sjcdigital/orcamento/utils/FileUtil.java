package io.sjcdigital.orcamento.utils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author Pedro Hos <pedro-hos@outlook.com>
 *
 */
public class FileUtil {
    
    public static void salvaHTML(String page, String directoryName, String filename) {
        
        try {
            filename = filename.contains(".html") ? filename : filename + ".html";
            createDirectoryIfDoesntExists(directoryName);
            Files.write(Paths.get(directoryName + filename), page.getBytes());
            
        } catch (IOException e) {
            e.printStackTrace();
        }
        
    }

    /**
     * @param object
     */
    public static void salvaJSON(Object object, String directoryName, String filename) {

        try {
            createDirectoryIfDoesntExists(directoryName);
            filename = filename.contains(".json") ? filename : filename + ".json";
            Files.write(Paths.get(directoryName + filename), new ObjectMapper().writeValueAsString(object).getBytes());
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * @param directoryName
     */
    protected static void createDirectoryIfDoesntExists(String directoryName) {

        var directory = new File(directoryName);

        if (!directory.exists()) {
            directory.mkdirs();
        }
    }

}
