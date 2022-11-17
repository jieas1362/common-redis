package com.future.redis.component;

import com.future.base.model.exps.ProException;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;

import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

import static com.future.base.constant.common.ResponseElement.BAD_REQUEST;
import static com.future.base.util.base.ProChecker.isBlank;
import static com.future.base.util.base.ProChecker.isNull;
import static com.future.redis.api.generator.ProRedisScriptGenerator.generateScriptByScriptStr;
import static com.future.redis.constant.RedisScripts.TOKEN_BUCKET_RATE_LIMITER;
import static java.lang.String.valueOf;
import static java.time.Instant.now;
import static java.util.Arrays.asList;
import static java.util.Optional.ofNullable;

/**
 * token bucket rate limiter
 *
 * @author liuyunfei
 */
@SuppressWarnings({"JavaDoc", "AliControlFlowStatementWithoutBraces", "unused"})
public final class ProFixedTokenBucketRateLimiter {

    private StringRedisTemplate stringRedisTemplate;

    private String replenishRate, burstCapacity;

    public ProFixedTokenBucketRateLimiter(StringRedisTemplate stringRedisTemplate, Integer replenishRate, Integer burstCapacity) {
        assertParam(stringRedisTemplate, replenishRate, burstCapacity);

        this.stringRedisTemplate = stringRedisTemplate;
        this.replenishRate = valueOf(replenishRate);
        this.burstCapacity = valueOf(burstCapacity);
    }

    private static final Supplier<String> CURRENT_SEC_STAMP_SUP = () -> now().getEpochSecond() + "";

    private static final RedisScript<Boolean> SCRIPT = generateScriptByScriptStr(TOKEN_BUCKET_RATE_LIMITER.str, Boolean.class);

    private static final String KEY_PREFIX = "TB_RLI_";
    private static final String TOKEN_SUFFIX = "_TKS", STAMP_SUFFIX = "_TST";

    private static final Function<String, List<String>> SCRIPT_KEYS_WRAPPER = id -> {
        String prefix = KEY_PREFIX + id;
        return asList(prefix + TOKEN_SUFFIX, prefix + STAMP_SUFFIX);
    };

    private final Supplier<List<String>> SCRIPT_ARGS_SUP = () ->
            asList(replenishRate, burstCapacity, CURRENT_SEC_STAMP_SUP.get());

    private final Function<String, Boolean> ALLOWED_GETTER = limitKey ->
            stringRedisTemplate.execute(SCRIPT, SCRIPT_KEYS_WRAPPER.apply(limitKey),
                    SCRIPT_ARGS_SUP.get());

    /**
     * key allowed?
     *
     * @param limitKey
     * @return
     */
    public boolean isAllowed(String limitKey) {
        return ofNullable(ALLOWED_GETTER.apply(limitKey)).orElse(false);
    }

    /**
     * delete key
     *
     * @param key
     * @return
     */
    public boolean delete(String key) {
        if (isBlank(key))
            throw new ProException(BAD_REQUEST);

        return ofNullable(stringRedisTemplate.delete(key)).orElse(false);
    }

    /**
     * assert params
     *
     * @param stringRedisTemplate
     * @param replenishRate
     * @param burstCapacity
     */
    private void assertParam(StringRedisTemplate stringRedisTemplate, Integer replenishRate, Integer burstCapacity) {
        if (isNull(stringRedisTemplate))
            throw new RuntimeException("stringRedisTemplate can't be null");

        if (isNull(replenishRate) || isNull(burstCapacity) || replenishRate < 1 || burstCapacity < replenishRate)
            throw new RuntimeException("replenishRate and burstCapacity can't be null or less than 1, burstCapacity can't be less than replenishRate");
    }

}
