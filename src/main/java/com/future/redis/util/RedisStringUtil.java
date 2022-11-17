package com.future.redis.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.PostConstruct;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import static java.util.Optional.ofNullable;

@Component
@AutoConfigureAfter({StringRedisTemplate.class})
@SuppressWarnings({"unused"})
public class RedisStringUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(RedisStringUtil.class);
    private final StringRedisTemplate stringRedisTemplate;

    private static StringRedisTemplate staticStringRedisTemplate;


    public RedisStringUtil(StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
    }

    @PostConstruct
    public void init() {
        staticStringRedisTemplate = this.stringRedisTemplate;
    }

    // =============================common============================

    /**
     * 指定缓存失效时间
     *
     * @param key  键
     * @param time 时间(秒)
     */
    public static boolean expire(String key, long time) {
        try {
            if (time > 0) {
                staticStringRedisTemplate.expire(key, time, TimeUnit.SECONDS);
            }
            return true;
        } catch (Exception e) {
            LOGGER.error("expire", e);
            return false;
        }
    }

    /**
     * 根据key 获取过期时间
     *
     * @param key 键 不能为null
     * @return 时间(秒) 返回0代表为永久有效
     */
    public static Long getExpire(String key) {
        return staticStringRedisTemplate.getExpire(key, TimeUnit.SECONDS);
    }


    /**
     * 判断key是否存在
     *
     * @param key 键
     * @return true 存在 false不存在
     */
    public static boolean hasKey(String key) {
        try {
            return Boolean.TRUE.equals(staticStringRedisTemplate.hasKey(key));
        } catch (Exception e) {
            LOGGER.error("hasKey", e);
            return false;
        }
    }


    /**
     * 删除缓存
     *
     * @param key 可以传一个值 或多个
     */
    @SuppressWarnings("unchecked")
    public static void del(String... key) {
        if (key != null && key.length > 0) {
            if (key.length == 1) {
                staticStringRedisTemplate.delete(key[0]);
            } else {
                staticStringRedisTemplate.delete((Collection<String>) CollectionUtils.arrayToList(key));
            }
        }
    }


    // ============================String=============================

    /**
     * 普通缓存获取
     *
     * @param key 键
     * @return 值
     */
    public static String get(String key) {
        return key == null ? null : staticStringRedisTemplate.opsForValue().get(key);
    }


    /**
     * 普通缓存获取
     *
     * @param key 键
     * @return 值
     */
    public static String getStr(String key) {
        return key == null ? null : staticStringRedisTemplate.opsForValue().get(key);
    }

    /**
     * 批量普通缓存获取
     *
     * @param keys 键
     * @return 值
     */
    public static List<String> mget(List<String> keys) {
        return staticStringRedisTemplate.opsForValue().multiGet(keys);
    }

    /**
     * 普通缓存放入
     *
     * @param key   键
     * @param value 值
     * @return true成功 false失败
     */

    public static boolean set(String key, String value) {
        try {
            staticStringRedisTemplate.opsForValue().set(key, value);
            return true;
        } catch (Exception e) {
            LOGGER.error("set", e);
            return false;
        }
    }


    /**
     * 普通缓存放入并设置时间
     *
     * @param key   键
     * @param value 值
     * @param time  时间(秒) time要大于0 如果time小于等于0 将设置无限期
     * @return true成功 false 失败
     */
    public static boolean set(String key, String value, long time) {
        try {
            if (time > 0) {
                staticStringRedisTemplate.opsForValue().set(key, value, time, TimeUnit.SECONDS);
            } else {
                set(key, value);
            }
            return true;
        } catch (Exception e) {
            LOGGER.error("set", e);
            return false;
        }
    }


    /**
     * 递增
     *
     * @param key   键
     * @param delta 要增加几(大于0)
     */
    public static Long incr(String key, long delta) {
        if (delta < 0) {
            throw new RuntimeException("递增因子必须大于0");
        }
        return staticStringRedisTemplate.opsForValue().increment(key, delta);
    }


    /**
     * 递减
     *
     * @param key   键
     * @param delta 要减少几(小于0)
     */
    public static Long decr(String key, long delta) {
        if (delta < 0) {
            throw new RuntimeException("递减因子必须大于0");
        }
        return staticStringRedisTemplate.opsForValue().increment(key, -delta);
    }


    // ================================Map=================================

    /**
     * HashGet
     *
     * @param key  键 不能为null
     * @param item 项 不能为null
     */
    public static Object hget(String key, Object item) {
        return staticStringRedisTemplate.opsForHash().get(key, item);
    }

    /**
     * 获取hashKey对应的所有键值
     *
     * @param key 键
     * @return 对应的多个键值
     */
    public static Map<Object, Object> hmget(String key) {
        return staticStringRedisTemplate.opsForHash().entries(key);
    }

    /**
     * HashSet
     *
     * @param key 键
     * @param map 对应多个键值
     */
    public static boolean hmset(String key, Map<String, String> map) {
        try {
            staticStringRedisTemplate.opsForHash().putAll(key, map);
            return true;
        } catch (Exception e) {
            LOGGER.error("hmset", e);
            return false;
        }
    }


    /**
     * HashSet 并设置时间
     *
     * @param key  键
     * @param map  对应多个键值
     * @param time 时间(秒)
     * @return true成功 false失败
     */
    public static boolean hmset(String key, Map<String, String> map, long time) {
        try {
            staticStringRedisTemplate.opsForHash().putAll(key, map);
            if (time > 0) {
                return expire(key, time);
            }
            return true;
        } catch (Exception e) {
            LOGGER.error("hmset", e);
            return false;
        }
    }


    /**
     * 向一张hash表中放入数据,如果不存在将创建
     *
     * @param key   键
     * @param item  项
     * @param value 值
     * @return true 成功 false失败
     */
    public static boolean hset(String key, String item, String value) {
        try {
            staticStringRedisTemplate.opsForHash().put(key, item, value);
            return true;
        } catch (Exception e) {
            LOGGER.error("hset", e);
            return false;
        }
    }

    /**
     * 向一张hash表中放入数据,如果不存在将创建
     *
     * @param key   键
     * @param item  项
     * @param value 值
     * @param time  时间(秒) 注意:如果已存在的hash表有时间,这里将会替换原有的时间
     * @return true 成功 false失败
     */
    public static boolean hset(String key, String item, String value, long time) {
        try {
            staticStringRedisTemplate.opsForHash().put(key, item, value);
            if (time > 0) {
                return expire(key, time);
            }
            return true;
        } catch (Exception e) {
            LOGGER.error("hset", e);
            return false;
        }
    }


    /**
     * 删除hash表中的值
     *
     * @param key  键 不能为null
     * @param item 项 可以使多个 不能为null
     */
    public static void hdel(String key, Object... item) {
        staticStringRedisTemplate.opsForHash().delete(key, item);
    }


    /**
     * 判断hash表中是否有该项的值
     *
     * @param key  键 不能为null
     * @param item 项 不能为null
     * @return true 存在 false不存在
     */
    public static boolean hHasKey(String key, String item) {
        return staticStringRedisTemplate.opsForHash().hasKey(key, item);
    }


    /**
     * hash递增 如果不存在,就会创建一个 并把新增后的值返回
     *
     * @param key  键
     * @param item 项
     * @param by   要增加几(大于0)
     */
    public static double hincr(String key, String item, double by) {
        return staticStringRedisTemplate.opsForHash().increment(key, item, by);
    }


    /**
     * hash递减
     *
     * @param key  键
     * @param item 项
     * @param by   要减少记(小于0)
     */
    public static double hdecr(String key, String item, double by) {
        return staticStringRedisTemplate.opsForHash().increment(key, item, -by);
    }


    // ============================set=============================

    /**
     * 根据key获取Set中的所有值
     *
     * @param key 键
     */
    public static Set<String> sGet(String key) {
        try {
            return staticStringRedisTemplate.opsForSet().members(key);
        } catch (Exception e) {
            LOGGER.error("sGet", e);
            return null;
        }
    }


    /**
     * 根据value从一个set中查询,是否存在
     *
     * @param key   键
     * @param value 值
     * @return true 存在 false不存在
     */
    public static boolean sHasKey(String key, String value) {
        try {
            return Boolean.TRUE.equals(staticStringRedisTemplate.opsForSet().isMember(key, value));
        } catch (Exception e) {
            LOGGER.error("sHasKey", e);
            return false;
        }
    }


    /**
     * 将数据放入set缓存
     *
     * @param key    键
     * @param values 值 可以是多个
     * @return 成功个数
     */
    public static long sSet(String key, String... values) {
        try {
            return ofNullable(staticStringRedisTemplate.opsForSet().add(key, values)).orElse(0L);
        } catch (Exception e) {
            LOGGER.error("sSet", e);
            return 0;
        }
    }


    /**
     * 将set数据放入缓存
     *
     * @param key    键
     * @param time   时间(秒)
     * @param values 值 可以是多个
     * @return 成功个数
     */
    public static long sSetAndTime(String key, long time, String... values) {
        try {
            Long count = staticStringRedisTemplate.opsForSet().add(key, values);
            if (time > 0)
                expire(key, time);
            return ofNullable(count).orElse(0L);
        } catch (Exception e) {
            LOGGER.error("sSetAndTime", e);
            return 0;
        }
    }


    /**
     * 获取set缓存的长度
     *
     * @param key 键
     */
    public static long sGetSetSize(String key) {
        try {
            return ofNullable(staticStringRedisTemplate.opsForSet().size(key)).orElse(0L);
        } catch (Exception e) {
            LOGGER.error("sGetSetSize", e);
            return 0;
        }
    }


    /**
     * 移除值为value的
     *
     * @param key    键
     * @param values 值 可以是多个
     * @return 移除的个数
     */

    public static long setRemove(String key, Object... values) {
        try {
            return ofNullable(staticStringRedisTemplate.opsForSet().remove(key, values)).orElse(0L);
        } catch (Exception e) {
            LOGGER.error("setRemove", e);
            return 0;
        }
    }

    // ===============================list=================================

    /**
     * 获取list缓存的内容
     *
     * @param key   键
     * @param start 开始
     * @param end   结束 0 到 -1代表所有值
     */
    public static List<String> lGet(String key, long start, long end) {
        try {
            return staticStringRedisTemplate.opsForList().range(key, start, end);
        } catch (Exception e) {
            LOGGER.error("lGet", e);
            return null;
        }
    }


    /**
     * 获取list缓存的长度
     *
     * @param key 键
     */
    public static long lGetListSize(String key) {
        try {
            return ofNullable(staticStringRedisTemplate.opsForList().size(key)).orElse(0L);
        } catch (Exception e) {
            LOGGER.error("lGetListSize", e);
            return 0;
        }
    }


    /**
     * 通过索引 获取list中的值
     *
     * @param key   键
     * @param index 索引 index>=0时， 0 表头，1 第二个元素，依次类推；index<0时，-1，表尾，-2倒数第二个元素，依次类推
     */
    public static String lGetIndex(String key, long index) {
        try {
            return staticStringRedisTemplate.opsForList().index(key, index);
        } catch (Exception e) {
            LOGGER.error("lGetIndex", e);
            return null;
        }
    }


    /**
     * 将list放入缓存
     *
     * @param key   键
     * @param value 值
     */
    public static boolean lSet(String key, String value) {
        try {
            staticStringRedisTemplate.opsForList().rightPush(key, value);
            return true;
        } catch (Exception e) {
            LOGGER.error("lSet", e);
            return false;
        }
    }


    /**
     * 将list放入缓存
     *
     * @param key   键
     * @param value 值
     * @param time  时间(秒)
     */
    public static boolean lSet(String key, String value, long time) {
        try {
            staticStringRedisTemplate.opsForList().rightPush(key, value);
            if (time > 0)
                return expire(key, time);
            return true;
        } catch (Exception e) {
            LOGGER.error("lSet", e);
            return false;
        }

    }


    /**
     * 将list放入缓存
     *
     * @param key   键
     * @param value 值
     * @return boolean
     */
    public static boolean lSet(String key, List<String> value) {
        try {
            staticStringRedisTemplate.opsForList().rightPushAll(key, value);
            return true;
        } catch (Exception e) {
            LOGGER.error("lSet", e);
            return false;
        }

    }


    /**
     * 将list放入缓存
     *
     * @param key   键
     * @param value 值
     * @param time  时间(秒)
     * @return boolean
     */
    public static boolean lSet(String key, List<String> value, long time) {
        try {
            staticStringRedisTemplate.opsForList().rightPushAll(key, value);
            if (time > 0)
                return expire(key, time);
            return true;
        } catch (Exception e) {
            LOGGER.error("lSet", e);
            return false;
        }
    }


    /**
     * 根据索引修改list中的某条数据
     *
     * @param key   键
     * @param index 索引
     * @param value 值
     * @return boolean
     */
    public static boolean lUpdateIndex(String key, long index, String value) {
        try {
            staticStringRedisTemplate.opsForList().set(key, index, value);
            return true;
        } catch (Exception e) {
            LOGGER.error("lUpdateIndex", e);
            return false;
        }
    }


    /**
     * 移除N个值为value
     *
     * @param key   键
     * @param count 移除多少个
     * @param value 值
     * @return 移除的个数
     */
    public static long lRemove(String key, long count, String value) {
        try {
            return ofNullable(staticStringRedisTemplate.opsForList().remove(key, count, value)).orElse(0L);
        } catch (Exception e) {
            LOGGER.error("lRemove", e);
            return 0;
        }

    }


    // ===============================zSet=================================

    /**
     * zset 添加
     *
     * @param key   键
     * @param value 值
     * @param score 分数
     * @return boolean
     */
    public static boolean zAdd(String key, String value, double score) {
        return Boolean.TRUE.equals(staticStringRedisTemplate.opsForZSet().add(key, value, score));
    }
}
