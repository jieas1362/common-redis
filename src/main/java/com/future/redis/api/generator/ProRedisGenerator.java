package com.future.redis.api.generator;

import com.future.redis.api.conf.RedisConf;
import com.future.redis.component.ProValidator;
import com.future.redis.constant.ServerMode;
import io.lettuce.core.ClientOptions;
import io.lettuce.core.SocketOptions;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.springframework.cache.CacheManager;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.cache.RedisCacheWriter;
import org.springframework.data.redis.connection.*;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettucePoolingClientConfiguration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.io.Serializable;
import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Stream;

import static com.future.base.util.base.ProChecker.isNull;
import static com.future.redis.constant.ServerMode.CLUSTER;
import static com.future.redis.constant.ServerMode.SINGLE;
import static io.lettuce.core.protocol.DecodeBufferPolicies.ratio;
import static java.lang.Integer.parseInt;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.time.Duration.of;
import static java.time.temporal.ChronoUnit.MILLIS;
import static java.time.temporal.ChronoUnit.SECONDS;
import static java.util.Optional.ofNullable;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.springframework.util.CollectionUtils.isEmpty;

/**
 * redis components generator
 *
 * @author liuyunfei
 */
@SuppressWarnings({"JavaDoc", "unused", "AliControlFlowStatementWithoutBraces", "DuplicatedCode"})
public final class ProRedisGenerator {

    private static final StringRedisSerializer STRING_REDIS_SERIALIZER = new StringRedisSerializer(UTF_8);

    private static final RedisSerializer<Object> JDK_REDIS_SERIALIZER = new JdkSerializationRedisSerializer();

    private static final String KEY_VALUE_SEPARATOR = ":";

    private static final String CACHE_MANAGER_PRE = "CH_M:";

    private static final Map<ServerMode, Consumer<RedisConf>> SERVER_MODE_ASSERTERS = new HashMap<>(4, 1.0f);

    private static final Map<ServerMode, Function<RedisConf, RedisConfiguration>> CONF_GENERATORS = new HashMap<>(4, 1.0f);

    static {
        SERVER_MODE_ASSERTERS.put(CLUSTER, conf -> {
            if (isNull(conf))
                throw new RuntimeException("conf can't be null");

            List<String> nodes = conf.getNodes();
            if (isEmpty(nodes))
                throw new RuntimeException("nodes can't be null or empty");
        });

        SERVER_MODE_ASSERTERS.put(SINGLE, conf -> {
            if (isNull(conf))
                throw new RuntimeException("conf can't be null");

            Integer port = conf.getPort();
            if (isBlank(conf.getHost()) || isNull(port) || port < 1)
                throw new RuntimeException("host can't be null or '', port can't be null or less than 1");
        });

        CONF_GENERATORS.put(CLUSTER, ProRedisGenerator::generateClusterConfiguration);
        CONF_GENERATORS.put(SINGLE, ProRedisGenerator::generateStandConfiguration);
    }

    private static final Consumer<RedisConf> SERVER_MODE_ASSERTER = conf -> {
        if (isNull(conf))
            throw new RuntimeException("conf can't be null");

        ServerMode serverMode = conf.getServerMode();
        if (isNull(serverMode))
            throw new RuntimeException("serverMode can't be null");

        Consumer<RedisConf> asserter = SERVER_MODE_ASSERTERS.get(serverMode);
        if (isNull(asserter))
            throw new RuntimeException("unknown serverMode -> " + serverMode);

        asserter.accept(conf);
    };

    private static final Function<RedisConf, RedisConfiguration> CONF_GENERATOR = conf -> {
        if (isNull(conf))
            throw new RuntimeException("conf can't be null");

        ServerMode serverMode = conf.getServerMode();
        if (isNull(serverMode))
            throw new RuntimeException("serverMode can't be null");

        Function<RedisConf, RedisConfiguration> generator = CONF_GENERATORS.get(serverMode);
        if (isNull(generator))
            throw new RuntimeException("unknown serverMode -> " + serverMode);

        return generator.apply(conf);
    };

    /**
     * assert params
     *
     * @param conf
     */
    private static void confAsserter(RedisConf conf) {
        if (isNull(conf))
            throw new RuntimeException("conf can't be null");

        SERVER_MODE_ASSERTER.accept(conf);
    }

    /**
     * generate redis configuration
     *
     * @param redisConf
     * @return
     */
    public static RedisConfiguration generateConfiguration(RedisConf redisConf) {
        confAsserter(redisConf);

        return CONF_GENERATOR.apply(redisConf);
    }

    /**
     * generate pool
     *
     * @param redisConf
     * @return
     */
    public static GenericObjectPoolConfig<RedisConnection> generateGenericObjectPoolConfig(RedisConf redisConf) {
        confAsserter(redisConf);

        GenericObjectPoolConfig<RedisConnection> genericObjectPoolConfig = new GenericObjectPoolConfig<>();

        ofNullable(redisConf.getMinIdle())
                .ifPresent(genericObjectPoolConfig::setMinIdle);
        ofNullable(redisConf.getMaxIdle())
                .ifPresent(genericObjectPoolConfig::setMaxIdle);
        ofNullable(redisConf.getMaxTotal())
                .ifPresent(genericObjectPoolConfig::setMaxTotal);
        ofNullable(redisConf.getMaxWaitMillis())
                .ifPresent(mwm -> genericObjectPoolConfig.setMaxWait(of(mwm, MILLIS)));

        return genericObjectPoolConfig;
    }

    /**
     * generate client options
     *
     * @param redisConf
     * @return
     */
    public static ClientOptions generateClientOptions(RedisConf redisConf) {
        confAsserter(redisConf);

        ClientOptions.Builder coBuilder = ClientOptions.builder()
                .disconnectedBehavior(ClientOptions.DisconnectedBehavior.REJECT_COMMANDS);

        ofNullable(redisConf.getAutoReconnect())
                .ifPresent(coBuilder::autoReconnect);
        ofNullable(redisConf.getBufferUsageRatio())
                .ifPresent(bur -> coBuilder.decodeBufferPolicy(ratio(bur)));
        ofNullable(redisConf.getCancelCommandsOnReconnectFailure())
                .ifPresent(coBuilder::cancelCommandsOnReconnectFailure);
        ofNullable(redisConf.getPingBeforeActivateConnection())
                .ifPresent(coBuilder::pingBeforeActivateConnection);
        ofNullable(redisConf.getRequestQueueSize())
                .ifPresent(coBuilder::requestQueueSize);
        ofNullable(redisConf.getPublishOnScheduler())
                .ifPresent(coBuilder::publishOnScheduler);
        ofNullable(redisConf.getAutoReconnect())
                .ifPresent(coBuilder::autoReconnect);
        ofNullable(redisConf.getAutoReconnect())
                .ifPresent(coBuilder::autoReconnect);

        SocketOptions.Builder soBuilder = SocketOptions.builder();
        ofNullable(redisConf.getTcpNoDelay())
                .ifPresent(soBuilder::tcpNoDelay);
        ofNullable(redisConf.getConnectTimeout())
                .ifPresent(cto -> soBuilder.connectTimeout(of(cto, SECONDS)));
        ofNullable(redisConf.getKeepAlive())
                .ifPresent(soBuilder::keepAlive);

        coBuilder.socketOptions(soBuilder.build());

        return coBuilder.build();
    }

    /**
     * generate client configuration
     *
     * @param redisConf
     * @return
     */
    public static LettuceClientConfiguration generateLettuceClientConfiguration(RedisConf redisConf, GenericObjectPoolConfig<RedisConnection> genericObjectPoolConfig, ClientOptions clientOptions) {
        confAsserter(redisConf);
        if (isNull(genericObjectPoolConfig))
            throw new RuntimeException("genericObjectPoolConfig can't be null");
        if (isNull(clientOptions))
            throw new RuntimeException("clientOptions can't be null");

        LettucePoolingClientConfiguration.LettucePoolingClientConfigurationBuilder builder = LettucePoolingClientConfiguration.builder()
                .poolConfig(genericObjectPoolConfig).clientOptions(clientOptions);

        ofNullable(redisConf.getCommandTimeout())
                .ifPresent(cto -> builder.commandTimeout(of(cto, SECONDS)));
        ofNullable(redisConf.getShutdownTimeout())
                .ifPresent(sto -> builder.shutdownTimeout(of(sto, SECONDS)));
        ofNullable(redisConf.getShutdownQuietPeriod())
                .ifPresent(sqp -> builder.shutdownQuietPeriod(of(sqp, SECONDS)));

        return builder.build();
    }

    /**
     * generate connection factory
     *
     * @param redisConfiguration
     * @param lettuceClientConfiguration
     * @return
     */
    public static LettuceConnectionFactory generateConnectionFactory(RedisConf redisConf, RedisConfiguration redisConfiguration, LettuceClientConfiguration lettuceClientConfiguration) {
        confAsserter(redisConf);
        if (isNull(redisConfiguration))
            throw new RuntimeException("redisConfiguration can't be null");
        if (isNull(lettuceClientConfiguration))
            throw new RuntimeException("lettuceClientConfiguration can't be null");

        LettuceConnectionFactory lettuceConnectionFactory = new LettuceConnectionFactory(redisConfiguration, lettuceClientConfiguration);
        ofNullable(redisConf.getShareNativeConnection())
                .ifPresent(lettuceConnectionFactory::setShareNativeConnection);

        return lettuceConnectionFactory;
    }


    /**
     * generate template
     *
     * @param redisConnectionFactory
     * @return
     */
    public static StringRedisTemplate generateStringRedisTemplate(RedisConnectionFactory redisConnectionFactory) {
        if (isNull(redisConnectionFactory))
            throw new RuntimeException("redisConnectionFactory can't be null");

        return new StringRedisTemplate(redisConnectionFactory);
    }

    /**
     * generate validator
     *
     * @param stringRedisTemplate
     * @return
     */
    public static ProValidator generateValidator(StringRedisTemplate stringRedisTemplate) {
        return new ProValidator(stringRedisTemplate);
    }

    /**
     * generate template
     *
     * @param redisConnectionFactory
     * @return
     */
    public static RedisTemplate<String, Object> generateObjectRedisTemplate(RedisConf redisConf, RedisConnectionFactory redisConnectionFactory) {
        confAsserter(redisConf);
        if (isNull(redisConnectionFactory))
            throw new RuntimeException("redisConnectionFactory can't be null");

        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(redisConnectionFactory);

        redisTemplate.setKeySerializer(RedisSerializer.string());
        redisTemplate.setValueSerializer(JDK_REDIS_SERIALIZER);
        redisTemplate.setKeySerializer(RedisSerializer.string());
        redisTemplate.setHashValueSerializer(JDK_REDIS_SERIALIZER);
        redisTemplate.setDefaultSerializer(JDK_REDIS_SERIALIZER);

        redisTemplate.setExposeConnection(ofNullable(redisConf.getExposeConnection()).orElse(false));

        return redisTemplate;
    }


    /**
     * generate template
     *
     * @param redisConnectionFactory
     * @return
     */
    public static <T> CacheManager generateCacheManager(RedisConf redisConf, RedisConnectionFactory redisConnectionFactory) {
        confAsserter(redisConf);
        if (isNull(redisConnectionFactory))
            throw new RuntimeException("redisConnectionFactory can't be null");

        RedisSerializationContext<String, T> objectRedisSerializationContext = generateObjectRedisSerializationContext();

        return RedisCacheManager.builder(RedisCacheWriter.nonLockingRedisCacheWriter(redisConnectionFactory))
                .cacheDefaults(RedisCacheConfiguration.defaultCacheConfig()
                        .prefixCacheNameWith(CACHE_MANAGER_PRE)
                        .entryTtl(Duration.of(redisConf.getEntryTtl(), SECONDS))
                        .serializeKeysWith(objectRedisSerializationContext.getStringSerializationPair())
                        .serializeValuesWith(objectRedisSerializationContext.getValueSerializationPair())
                ).build();
    }

    /**
     * generate cluster configuration
     *
     * @param redisConf
     * @return
     */
    private static RedisConfiguration generateClusterConfiguration(RedisConf redisConf) {
        confAsserter(redisConf);

        RedisClusterConfiguration redisClusterConfiguration = new RedisClusterConfiguration();
        try {
            redisConf.getNodes().stream().map(node -> {
                String[] hostAndPort = node.split(KEY_VALUE_SEPARATOR);
                return new RedisNode(hostAndPort[0], parseInt(hostAndPort[1]));
            }).forEach(redisClusterConfiguration::addClusterNode);
        } catch (Exception e) {
            throw new RuntimeException("redis init error, check args, e = " + e);
        }

        ofNullable(redisConf.getMaxRedirects())
                .ifPresent(redisClusterConfiguration::setMaxRedirects);
        ofNullable(redisConf.getPassword())
                .ifPresent(redisClusterConfiguration::setPassword);

        return redisClusterConfiguration;
    }

    /**
     * generate standalone configuration
     *
     * @param redisConf
     * @return
     */
    private static RedisConfiguration generateStandConfiguration(RedisConf redisConf) {
        confAsserter(redisConf);

        RedisStandaloneConfiguration redisStandaloneConfiguration = new RedisStandaloneConfiguration();

        ofNullable(redisConf.getHost())
                .ifPresent(redisStandaloneConfiguration::setHostName);
        ofNullable(redisConf.getPort())
                .ifPresent(redisStandaloneConfiguration::setPort);
        ofNullable(redisConf.getPassword())
                .ifPresent(redisStandaloneConfiguration::setPassword);

        return redisStandaloneConfiguration;
    }

    /**
     * generate object redis serialization context
     *
     * @param <T>
     * @return
     */
    private static <T> RedisSerializationContext<String, T> generateObjectRedisSerializationContext() {
        @SuppressWarnings("unchecked")
        RedisSerializer<T> jdkSerializationRedisSerializer = (RedisSerializer<T>) new JdkSerializationRedisSerializer();

        RedisSerializationContext.RedisSerializationContextBuilder<String, T> contextBuilder =
                RedisSerializationContext.newSerializationContext();

        return contextBuilder
                .key(STRING_REDIS_SERIALIZER).value(jdkSerializationRedisSerializer)
                .hashKey(STRING_REDIS_SERIALIZER).hashValue(jdkSerializationRedisSerializer)
                .build();
    }

}
