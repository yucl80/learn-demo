package com.example.demo;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;

import java.util.Arrays;
import java.util.List;

public class RedisLock {

    private RedisTemplate redisTemplate;

    public  void test(String counterKey, String uniqueRequestKey, char[] maxConcurrentAccess, char[] requestExpireTime){
        String luaScriptLock =
                "if redis.call('incr', KEYS[1]) <= tonumber(ARGV[1]) then " +
                        "redis.call('set', KEYS[2], '1', 'EX', ARGV[2]); " +
                        "return 1; " +
                        "else " +
                        "redis.call('decr', KEYS[1]); " +  // 如果超过限制，减去之前增加的计数
                        "return 0; " +
                        "end";

        String luaScriptRelease =
                "if redis.call('del', KEYS[1]) == 1 then " +
                        "return redis.call('decr', KEYS[2]); " +
                        "else " +
                        "return 0; " +
                        "end";

        List<String> keys = Arrays.asList(counterKey, uniqueRequestKey);
        List<String> args = Arrays.asList(String.valueOf(maxConcurrentAccess), String.valueOf(requestExpireTime));

        Long result = (Long)redisTemplate.execute(new DefaultRedisScript<>(luaScriptLock, Long.class), keys, args);

        if (result == 1) {
            try {
                // 访问资源的逻辑
                // ...
            } catch (Exception e) {

                // 处理异常
            } finally {
                Long result1 = (Long)redisTemplate.execute(new DefaultRedisScript<>(luaScriptRelease, Long.class), keys);
                // result = 1 表示操作成功
                if (result1 == 1) {
                    // 成功删除并减少计数器
                } else {
                    // 删除失败或其他问题
                }
            }
        } else {
            // 超过最大并发数，拒绝访问
        }

    }
}
