package com.future.redis.component;

import com.future.base.model.exps.ProException;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;

import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.function.UnaryOperator;

import static com.future.base.constant.common.ResponseElement.EMPTY_PARAM;
import static com.future.base.util.base.ProChecker.isBlank;
import static com.future.base.util.base.ProChecker.isNull;
import static com.future.redis.api.generator.ProRedisScriptGenerator.generateScriptByScriptStr;
import static com.future.redis.constant.RedisScripts.REPEATABLE_UNTIL_SUCCESS_OR_TIMEOUT_VALIDATION;
import static com.future.redis.constant.RedisScripts.UNREPEATABLE_VALIDATION;
import static java.util.Collections.singletonList;
import static java.util.Optional.ofNullable;

/**
 * validator
 *
 * @author liuyunfei
 */
@SuppressWarnings({"AliControlFlowStatementWithoutBraces", "JavaDoc", "unused"})
public final class ProValidator {

    private final StringRedisTemplate stringRedisTemplate;

    public ProValidator(StringRedisTemplate stringRedisTemplate) {
        if (isNull(stringRedisTemplate))
            throw new RuntimeException("stringRedisTemplate can't be null");

        this.stringRedisTemplate = stringRedisTemplate;
    }

    private static final String KEY_PREFIX = "VK_";

    private static final UnaryOperator<String> KEY_WRAPPER = key -> KEY_PREFIX + key;

    private static final Function<String, List<String>> SCRIPT_KEYS_WRAPPER = key ->
            singletonList(KEY_WRAPPER.apply(key));

    private final Function<String, List<String>> SCRIPT_ARGS_WRAPPER = Collections::singletonList;

    private static final RedisScript<Boolean> UNREPEATABLE_VALIDATION_SCRIPT = generateScriptByScriptStr(UNREPEATABLE_VALIDATION.str, Boolean.class);

    private static final RedisScript<Boolean> REPEATABLE_UNTIL_SUCCESS_OR_TIMEOUT_VALIDATION_SCRIPT =
            generateScriptByScriptStr(REPEATABLE_UNTIL_SUCCESS_OR_TIMEOUT_VALIDATION.str, Boolean.class);

    /**
     * set k-v with expire
     *
     * @param key
     * @param value
     * @param expire
     */
    public void setKeyValueWithExpire(String key, String value, Duration expire) {
        assertParam(key, value);
        if (isNull(expire))
            throw new RuntimeException("expire can't be null");

        stringRedisTemplate.opsForValue().set(KEY_WRAPPER.apply(key), value, expire);
    }

    /**
     * unrepeatable validate
     *
     * @param key
     * @param value
     * @return
     */
    public boolean unRepeatableValidate(String key, String value) {
        assertParam(key, value);

        return ofNullable(stringRedisTemplate.execute(UNREPEATABLE_VALIDATION_SCRIPT,
                SCRIPT_KEYS_WRAPPER.apply(key), SCRIPT_ARGS_WRAPPER.apply(value))).orElse(false);
    }

    /**
     * repeatable validate until success or timeout
     *
     * @param key
     * @param value
     * @return
     */
    public boolean repeatableValidateUntilSuccessOrTimeout(String key, String value) {
        assertParam(key, value);

        return ofNullable(stringRedisTemplate.execute(REPEATABLE_UNTIL_SUCCESS_OR_TIMEOUT_VALIDATION_SCRIPT,
                SCRIPT_KEYS_WRAPPER.apply(key), SCRIPT_ARGS_WRAPPER.apply(value))).orElse(false);
    }

    /**
     * repeatable validate until timeout
     *
     * @param key
     * @param value
     * @return
     */
    public boolean repeatableValidateUntilTimeout(String key, String value) {
        assertParam(key, value);

        return ofNullable(stringRedisTemplate.opsForValue().get(KEY_WRAPPER.apply(key)))
                .map(value::equals).orElse(false);
    }

    /**
     * delete key
     *
     * @param key
     * @return
     */
    public boolean delete(String key) {
        if (isBlank(key))
            throw new ProException(EMPTY_PARAM);

        return ofNullable(stringRedisTemplate.delete(key)).orElse(false);
    }

    /**
     * assert params
     *
     * @param key
     * @param value
     */
    private void assertParam(String key, String value) {
        if (isNull(key))
            throw new ProException(EMPTY_PARAM);
        if (isNull(value))
            throw new ProException(EMPTY_PARAM);
    }

}
