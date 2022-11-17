package com.future.redis.constant;

/**
 * redis script str
 *
 * @author liuyunfei
 */
@SuppressWarnings("SpellCheckingInspection")
public enum RedisScripts {

    /**
     * token bucket limiter script
     */
    TOKEN_BUCKET_RATE_LIMITER("redis.replicate_commands()\n" +
            "local tokens_key = KEYS[1]\n" +
            "local timestamp_key = KEYS[2]\n" +
            "local rate = tonumber(ARGV[1])\n" +
            "local capacity = tonumber(ARGV[2])\n" +
            "local now = tonumber(ARGV[3])\n" +
            "local fill_time = capacity/rate\n" +
            "local ttl = math.floor(fill_time*2)\n" +
            "local last_tokens = tonumber(redis.call(\"get\", tokens_key))\n" +
            "if last_tokens == nil then\n" +
            "  last_tokens = capacity\n" +
            "end\n" +
            "local last_refreshed = tonumber(redis.call(\"get\", timestamp_key))\n" +
            "if last_refreshed == nil then\n" +
            "  last_refreshed = 0\n" +
            "end\n" +
            "local delta = math.max(0, now-last_refreshed)\n" +
            "local tokens = math.min(capacity, last_tokens+(delta*rate))\n" +
            "local allowed = tokens >= 1\n" +
            "if allowed then\n" +
            "  tokens = tokens - 1\n" +
            "end\n" +
            "redis.call(\"setex\", tokens_key, ttl, tokens)\n" +
            "redis.call(\"setex\", timestamp_key, ttl, now)\n" +
            "return allowed"),


    /**
     * leaky bucket limiter script
     */
    LEAKY_BUCKET_RATE_LIMITER("redis.replicate_commands()\n" +
            "local tokens_key = KEYS[1]\n" +
            "local allowed_tokens = tonumber(redis.call(\"incr\", tokens_key))\n" +
            "if allowed_tokens == 1 then\n" +
            "  redis.call(\"pexpire\", tokens_key, tonumber(ARGV[2]))\n" +
            "end\n" +
            "return tonumber(ARGV[1]) >= allowed_tokens"),

    /**
     * unrepeatable validator script
     */
    UNREPEATABLE_VALIDATION("redis.replicate_commands()\n" +
            "local key = KEYS[1]\n" +
            "local v = redis.call(\"get\", key)\n" +
            "if v ~= nil then\n" +
            "  redis.call(\"del\", key)\n" +
            "end\n" +
            "return v == ARGV[1]"),


    /**
     * repeatable validator script
     */
    REPEATABLE_UNTIL_SUCCESS_OR_TIMEOUT_VALIDATION("redis.replicate_commands()\n" +
            "local key = KEYS[1]\n" +
            "local allowed = ARGV[1] == redis.call(\"get\", key)\n" +
            "if allowed then\n" +
            "  redis.call(\"del\", key)\n" +
            "end\n" +
            "return allowed"),

    /**
     * set bit and expire
     */
    SET_BIT_WITH_EXPIRE("redis.replicate_commands()\n" +
            "local k = KEYS[1]\n" +
            "local v = tonumber(redis.call(\"setbit\", k, tonumber(ARGV[1]), tonumber(ARGV[2])))\n" +
            "redis.call(\"expire\", k, tonumber(ARGV[3]))\n" +
            "return v == 0"),

    /**
     * if HyperLogLog exist, expire. or init, then expire
     */
    EXPIRE_HLL_OR_WITH_INIT("redis.replicate_commands()\n" +
            "local k = KEYS[1]\n" +
            "local b = redis.call(\"pfadd\", k, ARGV[1]) == 1\n" +
            "if b then\n" +
            "  redis.call(\"expire\", k, tonumber(ARGV[2]))\n" +
            "end\n" +
            "return b");

    public final String str;

    RedisScripts(String str) {
        this.str = str;
    }

}
