-- Redis 4.0+ 才支持该命令，https://developer.aliyun.com/article/195914
redis.replicate_commands()

-- 接收并初始化一些参数
local key = KEYS[1] -- 令牌桶标识
local capacity = tonumber(ARGV[1]) -- 令牌桶容量
local period = tonumber(ARGV[2]) -- 规定一定数量令牌的单位时间，同时也是生成一批令牌的单位时间（s）
local quota = tonumber(ARGV[3]) -- 单位时间内生成令牌的数量
local quantity = tonumber(ARGV[4]) or 1 -- 每次需要的令牌数，默认为 1
local timestamp = tonumber(redis.call('time')[1]) -- 当前时间戳（s）

-- 判断令牌桶是否存在
if (redis.call('exists', key) == 0) then
    -- 初始化
    redis.call('hmset', key, 'tokens', capacity, 'timestamp', timestamp)
    -- 设置自动过期失效
    redis.call('expire', key, period)
else
    -- 计算从上一次更新到现在这段时间内应该要生成的令牌数
    local tokens = tonumber(redis.call('hget', key, 'tokens'))
    local last = tonumber(redis.call('hget', key, 'timestamp'))
    local supply = ((timestamp - last) / period) * quota
    if (supply > 0) then
        -- 重置令牌数量
        tokens = math.min(tokens + supply, capacity)
        redis.call('hmset', key, 'tokens', tokens, 'timestamp', timestamp)
        -- 设置自动过期失效
        redis.call('expire', key, period)
    end
end

local result = {}
local tokens = tonumber(redis.call('hget', key, 'tokens'))
if (tokens < quantity) then
    -- 令牌数量不足，返回0表示已超过限流，同时返回剩余令牌数
    result = {0, tokens}
else
    -- 令牌充足
    -- 重置剩余令牌数
    tokens = tokens - quantity
    redis.call('hmset', key, 'tokens', tokens, 'timestamp', timestamp)
    -- 设置自动过期失效
    redis.call('expire', key, period)
    -- 返回当前所需要的令牌数量，同时返回剩余令牌数
    result = {quantity, tokens}
end

return result