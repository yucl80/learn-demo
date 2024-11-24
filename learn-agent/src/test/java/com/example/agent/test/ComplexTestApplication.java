package com.example.agent.test;

import java.util.concurrent.*;
import java.util.Random;

public class ComplexTestApplication {
    private final ExecutorService executorService;
    private final Random random;

    public ComplexTestApplication() {
        this.executorService = Executors.newFixedThreadPool(3);
        this.random = new Random();
    }

    public static void main(String[] args) throws Exception {
        ComplexTestApplication app = new ComplexTestApplication();
        try {
            app.runComplexTest();
        } finally {
            app.shutdown();
        }
    }

    public void runComplexTest() throws Exception {
        // 1. 提交一些正常的任务
        CompletableFuture<String> future1 = submitNormalTask("Task1");
        CompletableFuture<String> future2 = submitNormalTask("Task2");

        // 2. 提交一个会抛出异常的任务
        CompletableFuture<String> futureWithException = submitTaskWithException();

        // 3. 提交一个包含嵌套调用的任务
        CompletableFuture<String> nestedFuture = submitNestedTask();

        // 4. 提交一个混合任务（正常调用和异常）
        CompletableFuture<String> mixedFuture = submitMixedTask();

        // 等待所有任务完成
        try {
            CompletableFuture.allOf(
                future1, future2, futureWithException, nestedFuture, mixedFuture
            ).get(10, TimeUnit.SECONDS);
        } catch (Exception e) {
            System.out.println("Some tasks failed as expected: " + e.getMessage());
        }
    }

    private CompletableFuture<String> submitNormalTask(String taskName) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return normalOperation(taskName);
            } catch (Exception e) {
                throw new CompletionException(e);
            }
        }, executorService);
    }

    private CompletableFuture<String> submitTaskWithException() {
        return CompletableFuture.supplyAsync(() -> {
            return operationWithException();
        }, executorService);
    }

    private CompletableFuture<String> submitNestedTask() {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return nestedOperation();
            } catch (Exception e) {
                throw new CompletionException(e);
            }
        }, executorService);
    }

    private CompletableFuture<String> submitMixedTask() {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return mixedOperation();
            } catch (Exception e) {
                throw new CompletionException(e);
            }
        }, executorService);
    }

    private String normalOperation(String taskName) throws InterruptedException {
        System.out.println("Executing " + taskName);
        simulateWork(100);
        return "Result from " + taskName;
    }

    private String operationWithException() {
        System.out.println("Executing operation that will fail");
        simulateWork(50);
        throw new RuntimeException("Simulated failure in operation");
    }

    private String nestedOperation() throws InterruptedException {
        System.out.println("Starting nested operation");
        simulateWork(100);
        
        String result1 = innerOperation1();
        String result2 = innerOperation2();
        
        return "Nested: " + result1 + " - " + result2;
    }

    private String innerOperation1() throws InterruptedException {
        System.out.println("Inner operation 1");
        simulateWork(50);
        return "Inner1";
    }

    private String innerOperation2() throws InterruptedException {
        System.out.println("Inner operation 2");
        simulateWork(50);
        return "Inner2";
    }

    private String mixedOperation() throws InterruptedException {
        System.out.println("Starting mixed operation");
        simulateWork(50);
        
        try {
            return innerOperationWithException();
        } catch (Exception e) {
            return recoveryOperation();
        }
    }

    private String innerOperationWithException() {
        System.out.println("Inner operation that will fail");
        simulateWork(30);
        throw new RuntimeException("Simulated failure in inner operation");
    }

    private String recoveryOperation() throws InterruptedException {
        System.out.println("Executing recovery operation");
        simulateWork(50);
        return "Recovered";
    }

    private void simulateWork(int maxMillis) {
        try {
            Thread.sleep(random.nextInt(maxMillis));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private void shutdown() {
        executorService.shutdown();
        try {
            if (!executorService.awaitTermination(5, TimeUnit.SECONDS)) {
                executorService.shutdownNow();
            }
        } catch (InterruptedException e) {
            executorService.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
}
