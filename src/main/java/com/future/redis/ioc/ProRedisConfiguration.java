package com.future.redis.ioc;

import com.future.redis.api.conf.RedisConf;
import com.future.redis.component.ProValidator;
import com.future.redis.util.RedisStringUtil;
import com.future.redis.util.RedisUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.connection.RedisConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;

import static com.future.redis.api.generator.ProRedisGenerator.*;

/**
 * redis configuration
 *
 * @author liuyunfei
 */
@SuppressWarnings({"AlibabaRemoveCommentedCode"})
@ConditionalOnBean(value = {RedisConf.class})
@AutoConfiguration
public class ProRedisConfiguration {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProRedisConfiguration.class);

    @Bean
    RedisConfiguration redisConfiguration(RedisConf redisConf) {
        LOGGER.info("RedisConfiguration redisConfiguration(), redisConf = {}", redisConf);
        return generateConfiguration(redisConf);
    }

    @Bean
    LettuceConnectionFactory lettuceConnectionFactory(RedisConf redisConf, RedisConfiguration redisConfiguration) {
        LOGGER.info("LettuceConnectionFactory lettuceConnectionFactory(RedisConfiguration redisConfiguration), redisConf = {}", redisConf);
        return generateConnectionFactory(redisConf, redisConfiguration, generateLettuceClientConfiguration(redisConf, generateGenericObjectPoolConfig(redisConf), generateClientOptions(redisConf)));
    }

    @Bean
    RedisTemplate<String, Object> redisTemplate(RedisConf redisConf, LettuceConnectionFactory lettuceConnectionFactory) {
        return generateObjectRedisTemplate(redisConf, lettuceConnectionFactory);
    }

    @Bean
    StringRedisTemplate stringRedisTemplate(LettuceConnectionFactory lettuceConnectionFactory) {
        return generateStringRedisTemplate(lettuceConnectionFactory);
    }

    @Bean
    RedisUtil redisUtil(RedisTemplate<String, Object> redisTemplate) {
        return new RedisUtil(redisTemplate);
    }

    @Bean
    RedisStringUtil redisStringUtil(StringRedisTemplate stringRedisTemplate) {
        return new RedisStringUtil(stringRedisTemplate);
    }

    @Bean
    ProValidator proValidator(StringRedisTemplate stringRedisTemplate) {
        return generateValidator(stringRedisTemplate);
    }

}
