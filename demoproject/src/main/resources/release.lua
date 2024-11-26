local concurrency_counter = KEYS[1]


-- 释放资源，减少并发计数器
local new_concurrency = redis.call('DECR', concurrency_counter)
return new_concurrency

