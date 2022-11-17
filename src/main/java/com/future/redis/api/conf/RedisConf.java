package com.future.redis.api.conf;

import com.future.redis.constant.ServerMode;

import java.util.List;


/**
 * redis conf
 *
 * @author liuyunfei
 */
@SuppressWarnings({"AlibabaAbstractMethodOrInterfaceMethodMustUseJavadoc", "AlibabaCommentsMustBeJavadocFormat"})
public interface RedisConf {

    ServerMode getServerMode();

    //<editor-fold desc="cluster conf">
    List<String> getNodes();
    //</editor-fold>

    //<editor-fold desc="standalone conf">
    String getHost();

    Integer getPort();
    //</editor-fold>

    String getPassword();

    Integer getMaxRedirects();

    Integer getMinIdle();

    Integer getMaxIdle();

    Integer getMaxTotal();

    Integer getMaxWaitMillis();

    Boolean getAutoReconnect();

    Float getBufferUsageRatio();

    Boolean getCancelCommandsOnReconnectFailure();

    Boolean getPingBeforeActivateConnection();

    Integer getRequestQueueSize();

    Boolean getPublishOnScheduler();

    Boolean getTcpNoDelay();

    Integer getConnectTimeout();

    Boolean getKeepAlive();

    Boolean getSuspendReconnectOnProtocolFailure();

    Integer getFixedTimeout();

    Long getCommandTimeout();

    Long getShutdownTimeout();

    Long getShutdownQuietPeriod();

    Boolean getShareNativeConnection();

    Long getEntryTtl();

    Boolean getExposeConnection();
}
