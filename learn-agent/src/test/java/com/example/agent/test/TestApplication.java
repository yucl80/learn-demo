package com.example.agent.test;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class TestApplication {
    public static void main(String[] args) throws Exception {
        TestApplication app = new TestApplication();
        
        // 测试正常方法调用
        app.methodA();
        
        // 测试异常情况
        try {
            app.methodWithException();
        } catch (Exception e) {
            System.out.println("Caught expected exception: " + e.getMessage());
        }
        
        // 测试线程池
        app.testThreadPool();
        
        System.out.println("Test completed.");
    }
    
    public void methodA() {
        System.out.println("Executing methodA");
        methodB();
        methodC();
    }
    
    public void methodB() {
        System.out.println("Executing methodB");
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
    
    public void methodC() {
        System.out.println("Executing methodC");
        try {
            Thread.sleep(50);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
    
    public void methodWithException() {
        System.out.println("Executing methodWithException");
        throw new RuntimeException("Test exception");
    }
    
    public void testThreadPool() throws InterruptedException {
        ExecutorService executor = Executors.newFixedThreadPool(2);
        
        for (int i = 0; i < 3; i++) {
            final int taskId = i;
            executor.submit(() -> {
                try {
                    System.out.println("Executing task " + taskId);
                    methodA();
                    if (taskId == 1) {
                        methodWithException();
                    }
                } catch (Exception e) {
                    System.out.println("Task " + taskId + " caught exception: " + e.getMessage());
                }
            });
        }
        
        executor.shutdown();
        executor.awaitTermination(5, TimeUnit.SECONDS);
    }
}
