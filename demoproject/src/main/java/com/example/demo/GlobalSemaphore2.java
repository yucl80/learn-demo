package com.example.demo;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.UUID;

public class GlobalSemaphore2 {
    private final String MODEL_NAE_PREFIX = "M_A-"; //Number of Active Executions
    private final String MODEL_HOLDER_PREFIX = "M_H-";
    private final String MODEL_QUEUE_PREFIX = "M_Q-"; // 实时任务队列
    private final int maxConcurrent;
    private final int semHoldTimeout;  // 资源使用超时设置
    private final JedisPool jedisPool;

    private static final String acquireLuaScriptRt = "if redis.call('incr', KEYS[1]) <= tonumber(ARGV[1]) then\n" +
            "  redis.call('set', KEYS[2], '1', 'EX', tonumber(ARGV[2]));\n" +
            "  return 1;\n" +
            "else\n" +
            "  redis.call('decr', KEYS[1]);\n" +  // 如果超过限制，减去之前增加的计数
            "  redis.call('hset', KEYS[3], ARGV[3], ARGV[4]);\n" +  // 增加一个实时处理等待
            "  return 0;\n" +
            "end";

    private static final String acquireLuaScriptRtRetry = "if redis.call('incr', KEYS[1]) <= tonumber(ARGV[1]) then\n" +
            "  redis.call('set', KEYS[2], '1', 'EX', tonumber(ARGV[2]));\n" +
            "  redis.call('hdel', KEYS[3], ARGV[3]);\n" +  // 减少一个实时处理等待
            "  return 1;\n" +
            "else\n" +
            "  redis.call('decr', KEYS[1]);\n" +  // 如果超过限制，减去之前增加的计数
            "  return 0;\n" +
            "end";

    private static final String acquireLuaScript = "if redis.call('hlen', KEYS[3])  < 1  then\n" +
            " if redis.call('incr', KEYS[1]) <= tonumber(ARGV[1]) then\n" +
            "  redis.call('set', KEYS[2], '1', 'EX', tonumber(ARGV[2]));\n" +
            "  return 1;\n" +
            " else\n" +
            "  redis.call('decr', KEYS[1]);\n" +  // 如果超过限制，减去之前增加的计数
            "  return 0;\n" +
            " end;\n" +
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

    public GlobalSemaphore2(int maxConcurrent, int semGetTimeout, int semHoldTimeout) {
        this.jedisPool = new JedisPool("localhost", 6379);
        this.maxConcurrent = maxConcurrent;
        this.semHoldTimeout = semHoldTimeout; // 秒

    }

    public String acquireRt(String modelKey,long acquireTimeout) throws InterruptedException {
        Jedis jedis = null;
        try {
            jedis = this.jedisPool.getResource();
            String requestId = UUID.randomUUID().toString(); // 生成唯一标识符
            long startTime = System.currentTimeMillis();
            Object result = jedis.eval(acquireLuaScriptRt, 3, MODEL_NAE_PREFIX + modelKey, MODEL_HOLDER_PREFIX + modelKey + requestId,MODEL_QUEUE_PREFIX+modelKey, String.valueOf(maxConcurrent), String.valueOf(semHoldTimeout),requestId,String.valueOf(System.currentTimeMillis()));
            if (result != null && (Long) result == 1) {
                return requestId;
            } else {
                boolean needRemoveQueueItem = false;
                try {
                    while (true) {
                        long curTime = System.currentTimeMillis();
                        result = jedis.eval(acquireLuaScriptRtRetry, 3, MODEL_NAE_PREFIX + modelKey, MODEL_HOLDER_PREFIX + modelKey + requestId,MODEL_QUEUE_PREFIX+modelKey, String.valueOf(maxConcurrent), String.valueOf(semHoldTimeout),requestId);
                        System.out.println(System.currentTimeMillis() - curTime);
                        if (result != null && (Long) result == 1) {
                            needRemoveQueueItem = true;
                            return requestId;  // 成功获取信号量
                        }
                        needRemoveQueueItem = true;
                        // 检查超时
                        if (System.currentTimeMillis() - startTime > acquireTimeout) {
                           return null;
                        }
                        Thread.sleep((long) (Math.random() * 200));  // 等待重试
                    }
                } finally {
                    if(needRemoveQueueItem) {
                        jedis.eval("redis.call('hdel', KEYS[1], ARGV[1])", 1, MODEL_QUEUE_PREFIX + modelKey, requestId);
                    }
                }
            }
        } finally {
            if (jedis != null) {
                jedisPool.returnResource(jedis);
            }
        }
    }

    public String acquire(String modelKey,long acquireTimeout) throws InterruptedException {
        Jedis jedis = null;
        try {
            jedis = this.jedisPool.getResource();
            String threadId = UUID.randomUUID().toString(); // 生成唯一标识符

            long startTime = System.currentTimeMillis();
            while (true) {
                long curTime = System.currentTimeMillis();
                Object result = jedis.eval(acquireLuaScript, 3, MODEL_NAE_PREFIX + modelKey, MODEL_HOLDER_PREFIX + modelKey + threadId, MODEL_QUEUE_PREFIX + modelKey, String.valueOf(maxConcurrent), String.valueOf(semHoldTimeout));
                System.out.println(System.currentTimeMillis() - curTime);
                if (result != null && (Long) result == 1) {
                    System.out.println("get ok :" + result);
                    return threadId;  // 成功获取信号量
                }
                // 检查超时
                if (System.currentTimeMillis() - startTime > acquireTimeout) {
                    throw new RuntimeException("Failed to acquire semaphore within timeout");
                }
                Thread.sleep((long) (Math.random() * 200));  // 等待重试
            }
        } finally {
            if (jedis != null) {
                jedisPool.returnResource(jedis);
            }
        }
    }

    public void release(String modelKey, String threadId) {
        Jedis jedis = null;
        try {
            jedis = this.jedisPool.getResource();
            jedis.eval(releaseLuaScript, 2, MODEL_NAE_PREFIX + modelKey, MODEL_HOLDER_PREFIX + modelKey + threadId);
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
            Object result = jedis.eval(checkLuaScript, 1, MODEL_NAE_PREFIX, MODEL_HOLDER_PREFIX + "*");
            System.out.println("check result " + result);
        } finally {
            if (jedis != null) {
                jedisPool.returnResource(jedis);
            }
        }
    }

    public static void main(String[] args) {
        GlobalSemaphore2 semaphore = new GlobalSemaphore2(5, 50000, 1000);  // 设置最大并发数和超时时间
        // 创建多个线程模拟并发请求
        long acquireTimeout = 1000000;
        for (int i = 0; i < 10; i++) {
            new Thread(() -> {
                String threadId = null;
                String modelKey = "chatglm4_130b";
                try {
                    threadId = semaphore.acquire(modelKey,acquireTimeout);
                    System.out.println(Thread.currentThread().getName() + " processing request...");
                    Thread.sleep(2000);  // 模拟处理时间
                } catch (Exception e) {
                    System.out.println(Thread.currentThread().getName() + " error: " + e.getMessage());
                } finally {
                    if (threadId != null) {
                        semaphore.release(modelKey,threadId);
                    }
                }
            }).start();
        }
        for (int i = 0; i < 6; i++) {
            new Thread(() -> {
                String threadId = null;
                String modelKey = "chatglm4_130b";
                try {
                    threadId = semaphore.acquireRt(modelKey,acquireTimeout);
                    System.out.println(Thread.currentThread().getName() + " processing request...");
                    Thread.sleep(10000);  // 模拟处理时间
                } catch (Exception e) {
                    System.out.println(Thread.currentThread().getName() + " error: " + e.getMessage());
                } finally {
                    if (threadId != null) {
                        semaphore.release(modelKey,threadId);
                    }
                }
            }).start();
        }
    }

}