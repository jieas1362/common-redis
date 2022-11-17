package com.future.redis.anno;

import com.future.redis.ioc.ProCacheManagerConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * enable redis cache manager
 *
 * @author liuyunfei
 */
@SuppressWarnings("unused")
@Target(TYPE)
@Retention(RUNTIME)
@Configuration
@Import(ProCacheManagerConfiguration.class)
public @interface EnableProCacheManager {
}
