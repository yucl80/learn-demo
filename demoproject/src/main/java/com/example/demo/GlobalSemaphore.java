package com.example.demo;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.UUID;

public class GlobalSemaphore {
    private final String lockKey = "semaphore";
    private final String ownerKey = "sem_owner_";
    private final int maxConcurrent;
    private final int semHoldTimeout;  // 资源使用超时设置
    private final int semGetTimeout; //资源获取超时设置
    private final JedisPool jedisPool;

    private static final String acquireLuaScript = "if redis.call('incr', KEYS[1]) <= tonumber(ARGV[1]) then " +
            "redis.call('set', KEYS[2], '1', 'EX', tonumber(ARGV[2])); " +
            "return 1; " +
            "else " +
            "redis.call('decr', KEYS[1]); " +  // 如果超过限制，减去之前增加的计数
            "return 0; " +
            "end";

    private static final String releaseLuaScript =
            "local result = redis.call('decr', KEYS[1]) \n" +
                    "redis.call('del', KEYS[2]) \n" +
                    "return result  \n";

    private static final String checkLuaScript = "local count = 0\n" +
            "for _, key in ipairs(redis.call('KEYS', ARGV[1])) do\n" +
            "    count = count + 1\n" +
            "end\n" +
            "\n" +
            "local used = tonumber(redis.call('GET', KEYS[1]) or 0) \n" +
            "\n" +
            "if used < count then\n" +
            "    redis.call('SET', KEYS[1], count)\n" +
            "    return count - used\n" +
            "else\n" +
            "    return 0\n" +
            "end\n";

    public GlobalSemaphore(int maxConcurrent, int semGetTimeout, int semHoldTimeout) {
        this.jedisPool = new JedisPool("localhost", 6379);
        this.maxConcurrent = maxConcurrent;
        this.semHoldTimeout = semHoldTimeout;  // 秒
        this.semGetTimeout = semGetTimeout;
    }

    public String acquire() throws InterruptedException {
        Jedis jedis = null;
        try {
            jedis = this.jedisPool.getResource();
            String threadId = UUID.randomUUID().toString(); // 生成唯一标识符

            long startTime = System.currentTimeMillis();
            while (true) {
                long curTime = System.currentTimeMillis();
                Object result = jedis.eval(acquireLuaScript, 2, lockKey, ownerKey + threadId, String.valueOf(maxConcurrent), String.valueOf(semHoldTimeout));
                System.out.println(System.currentTimeMillis() - curTime);
                if (result != null && (Long) result == 1) {
                    System.out.println("get ok :" + result);
                    return threadId;  // 成功获取信号量
                }
                // 检查超时
                if (System.currentTimeMillis() - startTime > semGetTimeout) {
                    check();
                    throw new RuntimeException("Failed to acquire semaphore within timeout");
                }
                Thread.sleep((long) Math.random() * 1000);  // 等待重试
            }
        } finally {
            if (jedis != null) {
                jedisPool.returnResource(jedis);
            }
        }
    }

    public void release(String threadId) {
        Jedis jedis = null;
        try {
            jedis = this.jedisPool.getResource();
            jedis.eval(releaseLuaScript, 2, lockKey, ownerKey + threadId);
        } finally {
            if (jedis != null) {
                jedisPool.returnResource(jedis);
            }
        }
    }

    public void check() {
        Jedis jedis = null;
        try {
            System.out.println(acquireLuaScript);
            jedis = this.jedisPool.getResource();
            Object result = jedis.eval(checkLuaScript, 1, lockKey, ownerKey + "*");
            System.out.println("check result " + result);
        } finally {
            if (jedis != null) {
                jedisPool.returnResource(jedis);
            }
        }
    }

    public static void main(String[] args) {
        GlobalSemaphore semaphore = new GlobalSemaphore(5, 50000, 1000);  // 设置最大并发数和超时时间
        // 创建多个线程模拟并发请求

        for (int i = 0; i < 6; i++) {
            new Thread(() -> {
                String threadId = null;
                try {
                    threadId = semaphore.acquire();
                    System.out.println(Thread.currentThread().getName() + " processing request...");
                    Thread.sleep(2000);  // 模拟处理时间
                } catch (Exception e) {
                    System.out.println(Thread.currentThread().getName() + " error: " + e.getMessage());
                } finally {
                    if (threadId != null) {
                        semaphore.release(threadId);
                    }
                }
            }).start();
        }
    }
}