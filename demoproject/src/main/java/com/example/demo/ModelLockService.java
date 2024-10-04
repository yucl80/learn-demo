package com.example.demo;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.UUID;

public class ModelLockService {
    private final String MODEL_NAE_PREFIX = "M_A-"; //Number of Active Executions
    private final String MODEL_HOLDER_PREFIX = "M_H-";
    private final String MODEL_QUEUE_PREFIX = "M_Q-"; // 实时任务队列
    private final int maxConcurrent;
    private final int retryInterval;
    private final JedisPool jedisPool;

    private static final String acquireLuaScriptRt = "if redis.call('incr', KEYS[1]) <= tonumber(ARGV[1]) then\n" +
            "  redis.call('hset', KEYS[2], ARGV[2], tonumber(ARGV[3]));\n\n" +
            "  return 1;\n" +
            "else\n" +
            "  redis.call('decr', KEYS[1]);\n" +  // 如果超过限制，减去之前增加的计数
            "  redis.call('hset', KEYS[3], ARGV[2], tonumber(ARGV[3]));\n" +  // 增加一个实时处理等待
            "  return 0;\n" +
            "end";

    private static final String acquireLuaScriptRtRetry = "if redis.call('incr', KEYS[1]) <= tonumber(ARGV[1]) then\n" +
            "  redis.call('hset', KEYS[2], ARGV[2], tonumber(ARGV[3]));\n" +
            "  redis.call('hdel', KEYS[3], ARGV[2]);\n" +  // 减少一个实时处理等待
            "  return 1;\n" +
            "else\n" +
            "  redis.call('decr', KEYS[1]);\n" +  // 如果超过限制，减去之前增加的计数
            "  return 0;\n" +
            "end";

    private static final String acquireLuaScript = "if redis.call('hlen', KEYS[3])  < 1  then\n" +
            " if redis.call('incr', KEYS[1]) <= tonumber(ARGV[1]) then\n" +
            "  redis.call('hset', KEYS[2], ARGV[2], tonumber(ARGV[3]));\n" +
            "  return 1;\n" +
            " else\n" +
            "  redis.call('decr', KEYS[1]);\n" +  // 如果超过限制，减去之前增加的计数
            "  return 0;\n" +
            " end;\n" +
            "end";

    private static final String releaseLuaScript =
            "local result = redis.call('decr', KEYS[1]) \n" +
                    "redis.call('hdel', KEYS[2], ARGV[1]);\n" +
                    "return result  \n";

    private static final String leakRecoverLuaScript =
            "local count = 0\n" +
                    "local fields = redis.call('HKEYS', KEYS[2])\n" +
                    "for i, field in ipairs(fields) do\n" +
                    " local value = tonumber(redis.call('HGET', KEYS[2], field))\n" +
                    " if value < tonumber(ARGV[2]) then\n" +
                    "  redis.call('decr', KEYS[1]) \n" +
                    "  redis.call('HDEL', KEYS[2], field)\n" +
                    "  count = count +1\n" +
                    " end\n" +
                    "end\n" +
                    "if tonumber(redis.call('GET',KEYS[1]) or 0) < tonumber(ARGV[1]) then\n" +
                    " redis.call('del',KEYS[3])\n " +
                    "end\n" +
                    "return count";

    public ModelLockService(int maxConcurrent,  int retryInterval) {
        this.jedisPool = new JedisPool("192.168.85.132", 6379);
        this.maxConcurrent = maxConcurrent;
        this.retryInterval = retryInterval;

    }

    public String acquireRt(String modelKey, long acquireTimeout) throws InterruptedException {
        Jedis jedis = null;
        try {
            jedis = this.jedisPool.getResource();
            String requestId = UUID.randomUUID().toString(); // 生成唯一标识符
            long startTime = System.currentTimeMillis();
            String modelActiveCounter = MODEL_NAE_PREFIX + modelKey;
            String modelHolderSet = MODEL_HOLDER_PREFIX + modelKey;
            String realtimeRequestQueue = MODEL_QUEUE_PREFIX + modelKey;
            Object result = jedis.eval(acquireLuaScriptRt, 3, modelActiveCounter, modelHolderSet, realtimeRequestQueue, String.valueOf(maxConcurrent), requestId, String.valueOf(System.currentTimeMillis()));
            this.jedisPool.returnResource(jedis);
            jedis = null;
            if (result != null && (Long) result == 1) {
                return requestId;
            } else {
                while (true) {

                    jedis = this.jedisPool.getResource();
                    result = jedis.eval(acquireLuaScriptRtRetry, 3, modelActiveCounter, modelHolderSet, realtimeRequestQueue, String.valueOf(maxConcurrent), requestId, String.valueOf(System.currentTimeMillis()));
                    this.jedisPool.returnResource(jedis);
                    jedis = null;
                    if (result != null && (Long) result == 1) {
                        return requestId;  // 成功获取信号量
                    }

                    // 检查超时
                    if (System.currentTimeMillis() - startTime > acquireTimeout) {
                        jedis = this.jedisPool.getResource();
                        jedis.eval("redis.call('hdel', KEYS[1], ARGV[1])", 1, realtimeRequestQueue, requestId);
                        jedis = null;
                        throw new RuntimeException("Failed to acquire semaphore within timeout");
                    }
                    Thread.sleep((long) (Math.random() * retryInterval));  // 等待重试
                }

            }
        } finally {
            if (jedis != null) {
                jedisPool.returnResource(jedis);
            }
        }
    }

    public String acquire(String modelKey, long acquireTimeout) throws InterruptedException {
        Jedis jedis = null;
        try {
            String requestId = UUID.randomUUID().toString(); // 生成唯一标识符
            String modelActiveCounter = MODEL_NAE_PREFIX + modelKey;
            String modelHolderSet = MODEL_HOLDER_PREFIX + modelKey;
            String realtimeRequestQueue = MODEL_QUEUE_PREFIX + modelKey;
            long startTime = System.currentTimeMillis();
            while (true) {
                jedis = this.jedisPool.getResource();
                Object result = jedis.eval(acquireLuaScript, 3, modelActiveCounter, modelHolderSet, realtimeRequestQueue, String.valueOf(maxConcurrent), requestId, String.valueOf(System.currentTimeMillis()));
                this.jedisPool.returnResource(jedis);
                jedis = null;
                if (result != null && (Long) result == 1) {
                    return requestId;  // 成功获取信号量
                }
                // 检查超时
                if (System.currentTimeMillis() - startTime > acquireTimeout) {
                    throw new RuntimeException("Failed to acquire semaphore within timeout");
                }
                Thread.sleep((long) (Math.random() * retryInterval));  // 等待重试
            }
        } finally {
            if (jedis != null) {
                jedisPool.returnResource(jedis);
            }
        }
    }

    public void release(String modelKey, String requestId) {
        Jedis jedis = null;
        try {
            jedis = this.jedisPool.getResource();
            Object result = jedis.eval(releaseLuaScript, 2, MODEL_NAE_PREFIX + modelKey, MODEL_HOLDER_PREFIX + modelKey, requestId);
            System.out.println("finish :" + requestId + " ,Active: " + result);
        } catch (Exception e) {
            e.printStackTrace();

        } finally {
            if (jedis != null) {
                jedisPool.returnResource(jedis);
            }
        }
    }

    public void leakRecoverLuaScript(String modelKey, long holdTimeout) {
        Jedis jedis = null;
        try {
            jedis = this.jedisPool.getResource();
            Object result = jedis.eval(leakRecoverLuaScript, 3, MODEL_NAE_PREFIX + modelKey, MODEL_HOLDER_PREFIX + modelKey, MODEL_QUEUE_PREFIX + modelKey, String.valueOf(maxConcurrent), String.valueOf((System.currentTimeMillis() - holdTimeout)));
            System.out.println("check result " + result);
        } finally {
            if (jedis != null) {
                jedisPool.returnResource(jedis);
            }
        }
    }

    public static void main(String[] args) {
        ModelLockService lockService = new ModelLockService(5,  200);  // 设置最大并发数和超时时间
        // 创建多个线程模拟并发请求
        String modelKey = "chatglm4_130b";
        lockService.leakRecoverLuaScript(modelKey, 10000);

        long acquireTimeout = 1000000;
        for (int i = 0; i < 19; i++) {
            new Thread(() -> {
                String requestId = null;

                try {
                    requestId = lockService.acquire(modelKey, acquireTimeout);
                    System.out.println(Thread.currentThread().getName() + "  processing  batch request..." + requestId);
                    Thread.sleep((long) (2000 * Math.random() + 1000));  // 模拟处理时间
                } catch (Exception e) {
                    System.out.println(Thread.currentThread().getName() + " error: " + e.getMessage());
                } finally {
                    if (requestId != null) {
                        System.out.println("finish " + requestId);
                        lockService.release(modelKey, requestId);
                    }
                }
            }).start();
        }
        for (int i = 0; i < 12; i++) {
            new Thread(() -> {
                String requestId = null;
                try {
                    requestId = lockService.acquireRt(modelKey, acquireTimeout);
                    System.out.println(Thread.currentThread().getName() + " processing realtime request..." + requestId);
                    Thread.sleep((long) (20000 * Math.random()));   // 模拟处理时间
                } catch (Exception e) {
                    System.out.println(Thread.currentThread().getName() + " error: " + e.getMessage());
                } finally {
                    if (requestId != null) {
                        lockService.release(modelKey, requestId);
                    }
                }
            }).start();
        }
    }

}