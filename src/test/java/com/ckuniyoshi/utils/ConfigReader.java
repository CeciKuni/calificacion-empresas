package com.ckuniyoshi.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ConfigReader {
    private static Properties properties;

    static {
        properties = new Properties();
        try (InputStream inputStream = ConfigReader.class.getResourceAsStream("/config.properties")) {
            if (inputStream != null) {
                properties.load(inputStream);
            } else {
                System.err.println("No se pudo encontrar el archivo de configuración config.properties");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Obtiene una propiedad, dando prioridad a las configuraciones desde la línea de comandos.
     * 
     * @param key Clave de la propiedad
     * @return Valor de la propiedad
     */
    public static String getProperty(String key) {

        String commandLineValue = System.getProperty(key);
        if (commandLineValue != null && !commandLineValue.isEmpty()) {
            return commandLineValue;
        }

        return properties.getProperty(key);
    }
}

