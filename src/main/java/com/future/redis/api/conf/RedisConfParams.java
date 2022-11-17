package com.future.redis.api.conf;

import com.future.redis.constant.ServerMode;

import java.util.List;

/**
 * redis params
 *
 * @author liuyunfei
 */
@SuppressWarnings({"unused", "AlibabaCommentsMustBeJavadocFormat"})
public class RedisConfParams implements RedisConf {

    protected ServerMode serverMode;

    //<editor-fold desc="cluster conf">
    protected transient List<String> nodes;
    //</editor-fold>

    //<editor-fold desc="standalone conf">
    protected transient String host;

    protected transient Integer port;
    //</editor-fold>

    protected transient String password;

    protected Integer maxRedirects;

    protected Integer minIdle;

    protected Integer maxIdle;

    protected Integer maxTotal;

    protected Integer maxWaitMillis;

    protected Boolean autoReconnect;

    protected Float bufferUsageRatio;

    protected Boolean cancelCommandsOnReconnectFailure;

    protected Boolean pingBeforeActivateConnection;

    protected Integer requestQueueSize;

    protected Boolean publishOnScheduler;

    protected Boolean tcpNoDelay;

    protected Integer connectTimeout;

    protected Boolean keepAlive;

    protected Boolean suspendReconnectOnProtocolFailure;

    protected Integer fixedTimeout;

    protected Long commandTimeout;

    protected Long shutdownTimeout;

    protected Long shutdownQuietPeriod;

    protected Boolean shareNativeConnection;

    protected Long entryTtl;

    protected Boolean exposeConnection;

    public RedisConfParams() {
    }

    @Override
    public ServerMode getServerMode() {
        return serverMode;
    }

    @Override
    public List<String> getNodes() {
        return nodes;
    }

    @Override
    public String getHost() {
        return host;
    }

    @Override
    public Integer getPort() {
        return port;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public Integer getMaxRedirects() {
        return maxRedirects;
    }

    @Override
    public Integer getMinIdle() {
        return minIdle;
    }

    @Override
    public Integer getMaxIdle() {
        return maxIdle;
    }

    @Override
    public Integer getMaxTotal() {
        return maxTotal;
    }

    @Override
    public Integer getMaxWaitMillis() {
        return maxWaitMillis;
    }

    @Override
    public Boolean getAutoReconnect() {
        return autoReconnect;
    }

    @Override
    public Float getBufferUsageRatio() {
        return bufferUsageRatio;
    }

    @Override
    public Boolean getCancelCommandsOnReconnectFailure() {
        return cancelCommandsOnReconnectFailure;
    }

    @Override
    public Boolean getPingBeforeActivateConnection() {
        return pingBeforeActivateConnection;
    }

    @Override
    public Integer getRequestQueueSize() {
        return requestQueueSize;
    }

    @Override
    public Boolean getPublishOnScheduler() {
        return publishOnScheduler;
    }

    @Override
    public Boolean getTcpNoDelay() {
        return tcpNoDelay;
    }

    @Override
    public Integer getConnectTimeout() {
        return connectTimeout;
    }

    @Override
    public Boolean getKeepAlive() {
        return keepAlive;
    }

    @Override
    public Boolean getSuspendReconnectOnProtocolFailure() {
        return suspendReconnectOnProtocolFailure;
    }

    @Override
    public Integer getFixedTimeout() {
        return fixedTimeout;
    }

    @Override
    public Long getCommandTimeout() {
        return commandTimeout;
    }

    @Override
    public Long getShutdownTimeout() {
        return shutdownTimeout;
    }

    @Override
    public Long getShutdownQuietPeriod() {
        return shutdownQuietPeriod;
    }

    @Override
    public Boolean getShareNativeConnection() {
        return shareNativeConnection;
    }

    @Override
    public Long getEntryTtl() {
        return entryTtl;
    }

    @Override
    public Boolean getExposeConnection() {
        return exposeConnection;
    }

    public void setServerMode(ServerMode serverMode) {
        this.serverMode = serverMode;
    }

    public void setNodes(List<String> nodes) {
        this.nodes = nodes;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setMaxRedirects(Integer maxRedirects) {
        this.maxRedirects = maxRedirects;
    }

    public void setMinIdle(Integer minIdle) {
        this.minIdle = minIdle;
    }

    public void setMaxIdle(Integer maxIdle) {
        this.maxIdle = maxIdle;
    }

    public void setMaxTotal(Integer maxTotal) {
        this.maxTotal = maxTotal;
    }

    public void setMaxWaitMillis(Integer maxWaitMillis) {
        this.maxWaitMillis = maxWaitMillis;
    }

    public void setAutoReconnect(Boolean autoReconnect) {
        this.autoReconnect = autoReconnect;
    }

    public void setBufferUsageRatio(Float bufferUsageRatio) {
        this.bufferUsageRatio = bufferUsageRatio;
    }

    public void setCancelCommandsOnReconnectFailure(Boolean cancelCommandsOnReconnectFailure) {
        this.cancelCommandsOnReconnectFailure = cancelCommandsOnReconnectFailure;
    }

    public void setPingBeforeActivateConnection(Boolean pingBeforeActivateConnection) {
        this.pingBeforeActivateConnection = pingBeforeActivateConnection;
    }

    public void setRequestQueueSize(Integer requestQueueSize) {
        this.requestQueueSize = requestQueueSize;
    }

    public void setPublishOnScheduler(Boolean publishOnScheduler) {
        this.publishOnScheduler = publishOnScheduler;
    }

    public void setTcpNoDelay(Boolean tcpNoDelay) {
        this.tcpNoDelay = tcpNoDelay;
    }

    public void setConnectTimeout(Integer connectTimeout) {
        this.connectTimeout = connectTimeout;
    }

    public void setKeepAlive(Boolean keepAlive) {
        this.keepAlive = keepAlive;
    }

    public void setSuspendReconnectOnProtocolFailure(Boolean suspendReconnectOnProtocolFailure) {
        this.suspendReconnectOnProtocolFailure = suspendReconnectOnProtocolFailure;
    }

    public void setFixedTimeout(Integer fixedTimeout) {
        this.fixedTimeout = fixedTimeout;
    }

    public void setCommandTimeout(Long commandTimeout) {
        this.commandTimeout = commandTimeout;
    }

    public void setShutdownTimeout(Long shutdownTimeout) {
        this.shutdownTimeout = shutdownTimeout;
    }

    public void setShutdownQuietPeriod(Long shutdownQuietPeriod) {
        this.shutdownQuietPeriod = shutdownQuietPeriod;
    }

    public void setShareNativeConnection(Boolean shareNativeConnection) {
        this.shareNativeConnection = shareNativeConnection;
    }

    public void setEntryTtl(Long entryTtl) {
        this.entryTtl = entryTtl;
    }

    public void setExposeConnection(Boolean exposeConnection) {
        this.exposeConnection = exposeConnection;
    }

    @Override
    public String toString() {
        return "RedisConfParams{" +
                "serverMode=" + serverMode +
                ", nodes=" + nodes +
                ", host='" + host + '\'' +
                ", port=" + port +
                ", password='" + password + '\'' +
                ", maxRedirects=" + maxRedirects +
                ", minIdle=" + minIdle +
                ", maxIdle=" + maxIdle +
                ", maxTotal=" + maxTotal +
                ", maxWaitMillis=" + maxWaitMillis +
                ", autoReconnect=" + autoReconnect +
                ", bufferUsageRatio=" + bufferUsageRatio +
                ", cancelCommandsOnReconnectFailure=" + cancelCommandsOnReconnectFailure +
                ", pingBeforeActivateConnection=" + pingBeforeActivateConnection +
                ", requestQueueSize=" + requestQueueSize +
                ", publishOnScheduler=" + publishOnScheduler +
                ", tcpNoDelay=" + tcpNoDelay +
                ", connectTimeout=" + connectTimeout +
                ", keepAlive=" + keepAlive +
                ", suspendReconnectOnProtocolFailure=" + suspendReconnectOnProtocolFailure +
                ", fixedTimeout=" + fixedTimeout +
                ", commandTimeout=" + commandTimeout +
                ", shutdownTimeout=" + shutdownTimeout +
                ", shutdownQuietPeriod=" + shutdownQuietPeriod +
                ", shareNativeConnection=" + shareNativeConnection +
                ", entryTtl=" + entryTtl +
                ", exposeConnection=" + exposeConnection +
                '}';
    }

}
