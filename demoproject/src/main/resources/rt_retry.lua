local concurrency_counter = KEYS[1]
local queue_name = KEYS[2]
local task_id = ARGV[1]
local max_concurrent_access = tonumber(ARGV[2])

-- 获取当前并发数
local current_concurrency = tonumber(redis.call('GET', concurrency_counter) or '0')
local available_slots = max_concurrent_access - current_concurrency

if available_slots > 0 then
    -- 获取队列中的前 available_slots 个任务
    local queue_front = redis.call('LRANGE', queue_name, -available_slots, -1)
    
    -- 检查当前任务是否在队列的前 available_slots 个元素中
    local found_in_queue = false
    for _, v in ipairs(queue_front) do
        if v == task_id then
            found_in_queue = true
            break
        end
    end

    if found_in_queue then
        -- 任务在队列的前 available_slots 中，允许执行并从队列中移除
        redis.call('LREM', queue_name, 1, task_id)
        redis.call('INCR', concurrency_counter)
        return {1, current_concurrency + 1}  -- 返回1，表示任务成功获取资源
    else
        -- 任务不在队列的前 available_slots 中，需要继续等待
        return {0, current_concurrency}  -- 返回0，表示任务需要继续等待
    end
else
    -- 没有可用并发数，任务需要继续等待
    return {0, current_concurrency}  -- 返回0，表示任务需要继续等待
end
