package com.yucl.code.search.config;

import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class SearchConfig {
    private static final String CONFIG_FILE = "search-config.properties";
    private static SearchConfig instance;
    
    private final String elasticsearchHost;
    private final int elasticsearchPort;
    private final String openAIKey;
    private final String chromaDBUrl;
    
    private SearchConfig() {
        Properties props = loadProperties();
        this.elasticsearchHost = props.getProperty("elasticsearch.host", "localhost");
        this.elasticsearchPort = Integer.parseInt(props.getProperty("elasticsearch.port", "9200"));
        this.openAIKey = props.getProperty("openai.key");
        this.chromaDBUrl = props.getProperty("chromadb.url", "http://localhost:8000");
    }
    
    public static SearchConfig getInstance() {
        if (instance == null) {
            instance = new SearchConfig();
        }
        return instance;
    }
    
    private Properties loadProperties() {
        Properties props = new Properties();
        try (InputStream input = getClass().getClassLoader().getResourceAsStream(CONFIG_FILE)) {
            if (input != null) {
                props.load(input);
            }
        } catch (IOException e) {
            System.err.println("Warning: Could not load " + CONFIG_FILE + ". Using default values.");
        }
        return props;
    }
    
    public RestHighLevelClient createElasticsearchClient() {
        return new RestHighLevelClient(
            RestClient.builder(new HttpHost(elasticsearchHost, elasticsearchPort, "http"))
        );
    }
    
    public String getOpenAIKey() {
        return openAIKey;
    }
    
    public String getChromaDBUrl() {
        return chromaDBUrl;
    }
}
