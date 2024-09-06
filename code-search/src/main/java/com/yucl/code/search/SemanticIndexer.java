package com.yucl.code.search;


import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import org.json.JSONObject;

public class SemanticIndexer {
    private String openAIKey;
    private String chromaDBUrl;
    private HttpClient httpClient;

    public SemanticIndexer(String openAIKey, String chromaDBUrl) {
        this.openAIKey = openAIKey;
        this.chromaDBUrl = chromaDBUrl;
        this.httpClient = HttpClient.newHttpClient();
    }

    // 创建索引
    public void createIndex(String document) throws Exception {
        String embedding = getEmbedding(document);
        addDocumentToChromaDB(embedding, document);
    }

    // 查询索引
    public List<String> queryIndex(String query) throws Exception {
        String queryEmbedding = getEmbedding(query);
        return queryDocumentsFromChromaDB(queryEmbedding);
    }

    private String getEmbedding(String text) throws Exception {
        String apiUrl = "https://api.openai.com/v1/embeddings";
        JSONObject requestBody = new JSONObject()
            .put("input", text)
            .put("model", "text-embedding-ada-002");

        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(apiUrl))
            .header("Content-Type", "application/json")
            .header("Authorization", "Bearer " + openAIKey)
            .POST(HttpRequest.BodyPublishers.ofString(requestBody.toString()))
            .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        JSONObject responseBody = new JSONObject(response.body());
        return responseBody.getJSONArray("data").getJSONObject(0).getJSONArray("embedding").toString();
    }

    private void addDocumentToChromaDB(String embedding, String document) throws Exception {
        String apiUrl = chromaDBUrl + "/addDocument";
        JSONObject requestBody = new JSONObject()
            .put("embedding", embedding)
            .put("document", document);

        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(apiUrl))
            .header("Content-Type", "application/json")
            .POST(HttpRequest.BodyPublishers.ofString(requestBody.toString()))
            .build();

        httpClient.send(request, HttpResponse.BodyHandlers.ofString());
    }

    private List<String> queryDocumentsFromChromaDB(String embedding) throws Exception {
        String apiUrl = chromaDBUrl + "/queryDocuments";
        JSONObject requestBody = new JSONObject()
            .put("embedding", embedding);

        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(apiUrl))
            .header("Content-Type", "application/json")
            .POST(HttpRequest.BodyPublishers.ofString(requestBody.toString()))
            .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        JSONObject responseBody = new JSONObject(response.body());
        return responseBody.getJSONArray("documents").toList();
    }
}