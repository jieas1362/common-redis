package com.future.redis.api.generator;

import com.future.redis.component.ProFixedTokenBucketRateLimiter;
import org.springframework.data.redis.core.StringRedisTemplate;

/**
 * rate limiter generator
 *
 * @author liuyunfei
 */
@SuppressWarnings({"JavaDoc", "unused"})
public final class ProRateLimiterGenerator {

    /**
     * generate token bucket rate limiter
     *
     * @param stringRedisTemplate
     * @param replenishRate
     * @param burstCapacity
     * @return
     */
    public static ProFixedTokenBucketRateLimiter generateFixedTokenBucketRateLimiter(StringRedisTemplate stringRedisTemplate, Integer replenishRate, Integer burstCapacity) {
        return new ProFixedTokenBucketRateLimiter(stringRedisTemplate, replenishRate, burstCapacity);
    }

}
