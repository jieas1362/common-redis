package com.future.redis.common;

import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.lang.NonNull;

import static java.util.Objects.isNull;
import static org.apache.logging.log4j.util.Strings.isBlank;
import static org.springframework.data.redis.core.script.DigestUtils.sha1DigestAsHex;

/**
 * redis script
 *
 * @author liuyunfei
 */
@SuppressWarnings({"FieldCanBeLocal", "AliControlFlowStatementWithoutBraces"})
public final class ProRedisScript<T> implements RedisScript<T> {

    private final String SCRIPT;
    private final String SHA1;
    private final Class<T> TYPE;

    public ProRedisScript(String script, Class<T> type) {
        if (isBlank(script) || isNull(type))
            throw new RuntimeException("script can't be blank, type can't be null");

        this.SCRIPT = script;
        this.SHA1 = sha1DigestAsHex(script);
        this.TYPE = type;
    }

    @Override
    public @NonNull
    String getSha1() {
        return SHA1;
    }

    @Override
    public Class<T> getResultType() {
        return TYPE;
    }

    @Override
    public @NonNull
    String getScriptAsString() {
        return SCRIPT;
    }

    @Override
    public boolean returnsRawValue() {
        return isNull(TYPE);
    }
}
