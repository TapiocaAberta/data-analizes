package io.sjcdigital.orcamento.utils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author Pedro Hos <pedro-hos@outlook.com>
 *
 */
public class FileUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(FileUtil.class);

    /**
     * @param object
     */
    public static void salvaJSON(Object object, String directoryName, String filename) {

        try {
            createDirectoryIfDoesntExists(directoryName);
            Files.write(Paths.get(directoryName + filename + ".json"), new ObjectMapper().writeValueAsString(object).getBytes());
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    protected static void createDirectoryIfDoesntExists(String directoryName) {

        LOGGER.info("Files will be save into " + directoryName
                + " if you need change it, replace the 'file.path' argument on application.properties file");

        var directory = new File(directoryName);

        if (!directory.exists()) {
            directory.mkdirs();
        }
    }

}
