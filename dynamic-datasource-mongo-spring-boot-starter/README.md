# `dynamic-datasource-mongo-spring-boot-starter`

![Maven Central](https://img.shields.io/maven-central/v/com.yhyzgn.jakit/dynamic-datasource-mongo-spring-boot-starter?color=brightgreen&label=dynamic-datasource-mongo-spring-boot-starter&logo=gradle&logoColor=orange&style=flat-square)

> 动态添加和切换数据源



## 1、插件引入

> 此处以`maven`仓库`pom`引入方式为例
>
> `${latestVersion}`请参考上述徽标

```xml
<dependency>
  <groupId>com.yhyzgn.jakit</groupId>
  <artifactId>dynamic-datasource-mongo-spring-boot-starter</artifactId>
  <version>${latestVersion}</version>
</dependency>
```



## 2、动态数据源切换

> 示例中包含动态创建数据库、同步数据表结构的功能
>
> 请参考 `com.yhy.jakit.simple.dynamic.datasource.mongo.interceptor.DataSourceInterceptor` 类实现

```java
@Slf4j
@Component
public class DataSourceInterceptor implements HandlerInterceptor {
    private static final Map<String, MongoDBProperties> PROP_MAP = new HashMap<>();

    @Autowired
    private DynamicMongoTemplate mongoTemplate;

    // 构造一些测试配置
    static {
        String name;
        MongoDBProperties properties;
        for (int i = 0; i < 5; i++) {
            name = "dynamic-" + i;
            properties = MongoDBProperties.builder()
                .name(name)
                .host("localhost")
                .port(27017)
                .database(name)
                .username("root")
                .password("root")
                .authenticationDatabase("admin")
                .build();
            PROP_MAP.put(name, properties);
        }
    }

    @Override
    public boolean preHandle(HttpServletRequest request, @NotNull HttpServletResponse response, @NotNull Object handler) throws Exception {
        String db = request.getHeader("DB");
        String name = "dynamic-" + db;

        if (StringUtils.hasText(db)) {
            if (db.equals("def")) {
                // 切换到 默认数据源
                mongoTemplate.switchToDefault();
                log.info("已切换到默认数据源");
            } else {
                if (mongoTemplate.exist(name)) {
                    mongoTemplate.switchTo(name);
                    log.info("已切换到数据源：" + name);
                } else {
                    // 动态添加并切换到 其他数据源
                    MongoDBProperties properties = PROP_MAP.get(name);
                    Assert.notNull(properties, "配置不存在");
                    mongoTemplate.addAndSwitchTo(properties);
                    log.info("已动态创建并切换到新数据源：" + name);
                }
            }
        } else {
            mongoTemplate.switchToDefault();
            log.info("已切换到默认数据源");
        }
        return true;
    }

    @Override
    public void afterCompletion(@NotNull HttpServletRequest request, @NotNull HttpServletResponse response, @NotNull Object handler, Exception ex) throws Exception {
        mongoTemplate.destroy();
    }
}
```



## 3、使用动态数据源

### 3.1、`Repository`类规范

> `Repository`类需继承自特定的`com.yhy.jakit.starter.dynamic.datasource.mongo.repository.DynamicMongoRepository`类，才能支持动态数据源功能

```java
@Repository
public class UserRepository extends DynamicMongoRepository<UserEntity, String> {
}
```

### 3.2、使用

> 请参考 `com.yhy.jakit.simple.dynamic.datasource.mongo.controller.MongoController` 类

```java
@RestController
@RequestMapping("/mongo")
public class MongoController {
    private final static Random RANDOM = new Random();

    @Autowired
    private UserRepository repository;

    @GetMapping
    public Res test(String name) {
        UserEntity user = UserEntity.builder()
            .name(name)
            .age(RANDOM.nextInt(100))
            .build();
        return Res.success(repository.save(user));
    }
}
```

### 3.3、`MongoTemplate`用法

> 默认的`MongoTemplate`实例已交由`DynamicMongoTemplate`管理，需要通过其`current()`方法获取到当前数据源的`MongoTemplate`实例

```java
@Autowired
private DynamicMongoTemplate dynamicMongoTemplate;

public void test(){
    // 获取到当前数据源的 MongoTemplate 实例
    MongoTemplate template = dynamicMongoTemplate.current();
}
```



测试方法：

> 通过配置获取取消请求头`DB`来模拟动态切换数据源，取消时则使用默认数据源

```shell
curl --location --request GET 'localhost:8888/mongo?name=%E6%9D%8E%E4%B8%87%E5%A7%AC' --header 'DB: 2'
```

响应结果：

```json
{
  "ret": 0,
  "data": {
    "id": 8,
    "name": "李万姬",
    "age": 36
  },
  "msg": null,
  "errorCode": 0,
  "timestamp": 1622018486340
}
```

