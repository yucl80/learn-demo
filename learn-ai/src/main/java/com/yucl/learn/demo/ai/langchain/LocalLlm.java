package com.yucl.learn.demo.ai.langchain;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.jlama.JlamaChatModel;


public class LocalLlm {
    public static void main(String[] args) {
        ChatLanguageModel model = JlamaChatModel.builder()
                .modelName("tjake/TinyLlama-1.1B-Chat-v1.0-Jlama-Q4")
                .build();

        String response = model.generate("Say 'Hello World'");
        System.out.println(response);
    }
}
