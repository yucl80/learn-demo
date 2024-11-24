package com.example.agent;


import org.junit.Test;

import java.util.concurrent.*;
import java.util.ArrayList;
import java.util.List;

public class ThreadPoolTraceTest {
    
    @Test
    public void testThreadPoolExecutionChain() throws Exception {
        // Create a thread pool
        ExecutorService executorService = Executors.newFixedThreadPool(3);
        
        // Create multiple tasks that call each other
        CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> {
            return firstAsyncTask();
        }, executorService).thenApplyAsync(result -> {
            return secondAsyncTask(result);
        }, executorService).thenApplyAsync(result -> {
            return thirdAsyncTask(result);
        }, executorService);
        
        // Wait for the chain to complete
        String result = future.get(5, TimeUnit.SECONDS);
        System.out.println("Final result: " + result);
        
        executorService.shutdown();
        executorService.awaitTermination(1, TimeUnit.SECONDS);
    }
    
    @Test
    public void testParallelExecution() throws Exception {
        ExecutorService executorService = Executors.newFixedThreadPool(4);
        List<CompletableFuture<String>> futures = new ArrayList<>();
        
        // Submit multiple parallel tasks
        for (int i = 0; i < 5; i++) {
            final int taskId = i;
            CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> {
                return parallelTask(taskId);
            }, executorService);
            futures.add(future);
        }
        
        // Wait for all tasks to complete
        CompletableFuture<Void> allOf = CompletableFuture.allOf(
            futures.toArray(new CompletableFuture[0])
        );
        
        allOf.get(5, TimeUnit.SECONDS);
        
        for (CompletableFuture<String> future : futures) {
            System.out.println("Task result: " + future.get());
        }
        
        executorService.shutdown();
        executorService.awaitTermination(1, TimeUnit.SECONDS);
    }
    
    @Test
    public void testNestedThreadPools() throws Exception {
        ExecutorService outerPool = Executors.newFixedThreadPool(2);
        ExecutorService innerPool = Executors.newFixedThreadPool(2);
        
        CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> {
            return outerTask(innerPool);
        }, outerPool);
        
        String result = future.get(5, TimeUnit.SECONDS);
        System.out.println("Nested result: " + result);
        
        outerPool.shutdown();
        innerPool.shutdown();
        outerPool.awaitTermination(1, TimeUnit.SECONDS);
        innerPool.awaitTermination(1, TimeUnit.SECONDS);
    }
    
    private String firstAsyncTask() {
        sleep(100);
        return "First";
    }
    
    private String secondAsyncTask(String input) {
        sleep(100);
        return input + " -> Second";
    }
    
    private String thirdAsyncTask(String input) {
        sleep(100);
        return input + " -> Third";
    }
    
    private String parallelTask(int taskId) {
        sleep(100);
        return "Task-" + taskId + " completed";
    }
    
    private String outerTask(ExecutorService innerPool) {
        try {
            CompletableFuture<String> innerFuture = CompletableFuture.supplyAsync(() -> {
                return innerTask();
            }, innerPool);
            
            return innerFuture.get(2, TimeUnit.SECONDS);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    private String innerTask() {
        sleep(100);
        return "Inner task completed";
    }
    
    private void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
