local concurrency_counter = KEYS[1]
local queue_name = KEYS[2]
local task_id = ARGV[1]
local max_concurrent_access = tonumber(ARGV[2])

-- 获取当前并发数
local current_concurrency = tonumber(redis.call('GET', concurrency_counter) or '0')
local available_slots = max_concurrent_access - current_concurrency
 -- 获取当前队列长度
local queue_length = tonumber(redis.call('LLEN', queue_name) or '0')

if available_slots > queue_length then
     -- 如果可用并发数大于或等于队列长度，立即处理任务，不写入队列
     redis.call('INCR', concurrency_counter)
     return {1, current_concurrency + 1}  -- 返回1，表示成功获取资源

else
    -- 没有可用的并发数，将任务加入队列
    redis.call('LPUSH', queue_name, task_id)
    return {0, current_concurrency}  -- 返回0，表示需要等待
end
