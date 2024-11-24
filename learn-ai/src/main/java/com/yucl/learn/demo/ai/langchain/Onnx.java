package com.yucl.learn.demo.ai.langchain;

import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.embedding.onnx.OnnxEmbeddingModel;
import dev.langchain4j.model.embedding.onnx.PoolingMode;
import dev.langchain4j.model.embedding.onnx.allminilml6v2.AllMiniLmL6V2EmbeddingModel;
import dev.langchain4j.model.output.Response;

public class Onnx {
    public static void main(String[] args) {
        EmbeddingModel embeddingModel = new AllMiniLmL6V2EmbeddingModel();
        Response<Embedding> response = embeddingModel.embed("test");
        Embedding embedding = response.content();
        System.out.println(embedding);

        String pathToModel = "/home/langchain4j/model.onnx";
        String pathToTokenizer = "/home/langchain4j/tokenizer.json";
        PoolingMode poolingMode = PoolingMode.MEAN;
        EmbeddingModel embeddingModel2 = new OnnxEmbeddingModel(pathToModel, pathToTokenizer, poolingMode);

        Response<Embedding> response2 = embeddingModel2.embed("test");
        Embedding embedding2 = response.content();
    }
}
