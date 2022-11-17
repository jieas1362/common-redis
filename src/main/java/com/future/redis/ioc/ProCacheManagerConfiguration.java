package com.future.redis.ioc;

import com.future.redis.api.conf.RedisConf;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;

import static com.future.redis.api.generator.ProRedisGenerator.generateCacheManager;

/**
 * cache manager configuration
 *
 * @author liuyunfei
 */
@ConditionalOnBean(value = {RedisConf.class})
public class ProCacheManagerConfiguration {

    @Bean
    CacheManager cacheManager(RedisConf redisConf, LettuceConnectionFactory lettuceConnectionFactory) {
        return generateCacheManager(redisConf, lettuceConnectionFactory);
    }

}
