# `dynamic-datasource-redis-spring-boot-starter`

![Maven Central](https://img.shields.io/maven-central/v/com.yhyzgn.jakit/dynamic-datasource-redis-spring-boot-starter?color=blueviolet&label=dynamic-datasource-redis-spring-boot-starter&logo=apachemaven&logoColor=c71a36&style=flat-square)
> 动态添加和切换数据源



## 1、插件引入

> 此处以`maven`仓库`pom`引入方式为例
>
> `${latestVersion}`请参考上述徽标

```xml
<dependency>
  <groupId>com.yhyzgn.jakit</groupId>
  <artifactId>dynamic-datasource-redis-spring-boot-starter</artifactId>
  <version>${latestVersion}</version>
</dependency>
```



## 2、动态数据源切换配置

> 示例中包含动态创建数据库、同步数据表结构的功能
>
> 请参考 `com.yhy.jakit.simple.dynamic.datasource.redis.interceptor.DynamicRedisTemplateInterceptor` 类实现

```java
@Component
public class DynamicRedisTemplateInterceptor implements HandlerInterceptor {
  private final static Map<String, RedisConfig> SOME_CONFIG = new HashMap<>();
  @Autowired
  private DynamicStringRedisTemplate dynamicTemplate;

  // 构造一些测试配置
  static {
    String name, prefix;
    RedisProperties properties;
    RedisConfig config;
    for (int i = 10; i < 16; i++) {
      name = "redis-" + i;
      prefix = "kf" + i;

      properties = new RedisProperties();
      properties.setClientName(name);
      properties.setHost("localhost");
      properties.setPassword("root");
      properties.setDatabase(i);

      config = new RedisConfig();
      config.setProperties(properties);
      config.setKeyPrefix(prefix);

      SOME_CONFIG.put(name, config);
    }
  }

  @Override
  public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
    String db = request.getHeader("DB");
    if (StringUtils.hasText(db)) {
      if (db.equals("def")) {
        // 切换到 默认数据源
        dynamicTemplate.switchToDefault();
      } else {
        // 动态添加并切换到 其他数据源
        RedisConfig config = SOME_CONFIG.get("redis-" + db);
        Assert.notNull(config, "配置不存在");
        dynamicTemplate.addAndSwitchTo(config);
      }
    } else {
      dynamicTemplate.switchToDefault();
    }
    return true;
  }

  @Override
  public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
    dynamicTemplate.destroy();
  }
}
```



## 3、使用动态数据源

> `Redis` 不像 `JPA` ，`Spring Redis`官方并未提供多数据源支持，所以在任何操作前需要先获取当前数据源的`StringRedisTemplate`实例
>
> 需要使用 `DynamicStringRedisTemplate` 实例来达到动态数据源操作的目的
>
> 请参考 `com.yhy.jakit.simple.dynamic.datasource.redis.controller.RedisController` 类

```java
@RestController
@RequestMapping("/redis")
public class RedisController {

  @Autowired
  private DynamicStringRedisTemplate dynamicTemplate;

  @GetMapping
  public Res test(String name) {
    StringRedisTemplate template = dynamicTemplate.current();
    String keyPrefix = dynamicTemplate.currentKeyPrefix();
    template.opsForValue().set(keyPrefix + ":dynamic-test", "Dynamic datasource test data " + name + " :: " + SystemClock.nowDate());

    Object obj = template.opsForValue().get(keyPrefix + ":dynamic-test");
    return Res.success(obj);
  }
}
```

测试方法：

> 通过配置获取取消请求头`DB`来模拟动态切换数据源，取消或指定`def`时则使用默认数据源

```shell
curl --location --request GET 'localhost:8899/redis?name=%E6%9D%8E%E4%B8%87%E5%A7%AC' --header 'DB: 11'
```

响应结果：

```json
{
  "ret": 0,
  "data": "Dynamic datasource test data 李万姬 :: 2021-05-26 16:40:27.614",
  "msg": null,
  "errorCode": 0,
  "timestamp": 1622018427629
}
```



## 4、支持

> `Redis`多数据源最终表现其实就是 `RedisConnectionFactory` 不同，所以该插件已经支持了 `RedisConnectionFactory` 多数据源管理，已实现的`DynamicStringRedisTemplate`就是由该功能实现的
>
> 这里默认只提供了针对`StringRedisTemplate`的`DynamicStringRedisTemplate`，如果需要实现针对`RedisTemplate<Object, Object>`的数据源，请直接参考`DynamicStringRedisTemplate`类即可
>
> 此部分功能看后续需求，如果有必要，可以先实现`DynamicRedisTemplate<K, V>`的管理，再衍生出`DynamicStringRedisTemplate`

核心逻辑是`public synchronized boolean add(RedisConfig config, boolean override)`方法

* `DynamicStringRedisTemplate`

  ```java
  public synchronized boolean add(RedisConfig config, boolean override) {
    RedisProperties properties = config.getProperties();
    if (templateMap.containsKey(properties.getClientName()) && redisConfigMap.containsKey(properties.getClientName())) {
      return true;
    } else if (templateMap.containsKey(properties.getClientName()) && !override) {
      return false;
    } else {
      // 此处添加连接池工厂时需要临时切换数据源，因为添加成功后要用对应的连接池工厂来创建 StringRedisTemplate
      // 否则 RedisConnectionFactory 很可能是默认数据源
      String lastName = RedisNameHolder.get();
      if (dynamicFactory.addAndSwitchTo(properties, override)) {
        RedisConnectionFactory factory = dynamicFactory.current();
        StringRedisTemplate template = new StringRedisTemplate();
        template.setConnectionFactory(factory);
        template.afterPropertiesSet();
        templateMap.put(properties.getClientName(), template);
        redisConfigMap.put(properties.getClientName(), config);
  
        // 添加成功后再需要切回去
        RedisNameHolder.set(lastName);
        return true;
      }
      return false;
    }
  }
  ```

* `DynamicRedisTemplate<K, V>`

  ```java
  public synchronized boolean add(RedisConfig config, boolean override) {
    RedisProperties properties = config.getProperties();
    if (templateMap.containsKey(properties.getClientName()) && redisConfigMap.containsKey(properties.getClientName())) {
      return true;
    } else if (templateMap.containsKey(properties.getClientName()) && !override) {
      return false;
    } else {
      // 此处添加连接池工厂时需要临时切换数据源，因为添加成功后要用对应的连接池工厂来创建 StringRedisTemplate
      // 否则 RedisConnectionFactory 很可能是默认数据源
      String lastName = RedisNameHolder.get();
      if (dynamicFactory.addAndSwitchTo(properties, override)) {
        RedisConnectionFactory factory = dynamicFactory.current();
        // here ...
        RedisTemplate<Object, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(factory);
        template.afterPropertiesSet();
        templateMap.put(properties.getClientName(), template);
        redisConfigMap.put(properties.getClientName(), config);
  
        // 添加成功后再需要切回去
        RedisNameHolder.set(lastName);
        return true;
      }
      return false;
    }
  }
  ```

  
