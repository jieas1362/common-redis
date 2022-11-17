# redis

### application.yml
```
cache:
  serverMode: SINGLE
  host: ******
  port: ******
  password:
  maxRedirects: 4
  minIdle: 4
  maxIdle: 64
  maxTotal: 128
  maxWaitMillis: 16
  autoReconnect: true
  bufferUsageRatio: 32.0
  cancelCommandsOnReconnectFailure: false
  pingBeforeActivateConnection: false
  requestQueueSize: 64
  publishOnScheduler: true
  tcpNoDelay: true
  connectTimeout: 10
  keepAlive: false
  suspendReconnectOnProtocolFailure: false
  fixedTimeout: 4
  commandTimeout: 4
  shutdownTimeout: 8
  shutdownQuietPeriod: 4
  entryTtl: 60
```




### project
#### config class
```
@Component
@ConfigurationProperties(prefix = "cache")
public class ProRedisConfig extends RedisConfParams {
}
```


#### use
```
    RedisUtil.set(k,v);
    RedisStringUtil.set(k,v);

```