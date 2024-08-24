package com.example.analyzer;

import java.util.Properties;
import java.io.InputStream;
import java.io.IOException;

public class AnalyzerConfig {

    private Properties properties;

    public AnalyzerConfig() {
        properties = new Properties();
        try (InputStream input = getClass().getClassLoader().getResourceAsStream("config.properties")) {
            if (input == null) {
                System.out.println("Sorry, unable to find config.properties");
                return;
            }
            properties.load(input);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public String getProperty(String key) {
        return properties.getProperty(key);
    }
}
