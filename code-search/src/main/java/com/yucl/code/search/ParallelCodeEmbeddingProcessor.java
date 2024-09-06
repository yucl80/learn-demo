package com.yucl.code.search;

import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ParallelCodeEmbeddingProcessor {

    private static final int THREAD_POOL_SIZE = 8; // Number of threads in the pool
    private ExecutorService executorService;
    private SmartCodeEmbeddingProcessor processor;

    public ParallelCodeEmbeddingProcessor(String apiKey) {
        this.executorService = Executors.newFixedThreadPool(THREAD_POOL_SIZE);
        this.processor = new SmartCodeEmbeddingProcessor(apiKey);
    }

    public void processDirectory(String directoryPath) throws Exception {
        File dir = new File(directoryPath);
        File[] files = dir.listFiles((d, name) -> name.endsWith(".java"));

        if (files != null) {
            for (File file : files) {
                executorService.submit(() -> {
                    try {
                        processor.processFile(file.getAbsolutePath());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
            }
        }
    }

    public void shutdown() {
        executorService.shutdown();
        while (!executorService.isTerminated()) {
        }
    }

    public static void main(String[] args) throws Exception {
        ParallelCodeEmbeddingProcessor parallelProcessor = new ParallelCodeEmbeddingProcessor("your-openai-api-key");

        // Process all Java files in the directory
        parallelProcessor.processDirectory("src/main/java/example/");

        // Shutdown the executor service after processing
        parallelProcessor.shutdown();
    }
}
