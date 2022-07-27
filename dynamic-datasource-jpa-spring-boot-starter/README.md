# `dynamic-datasource-jpa-spring-boot-starter`

![Maven Central](https://img.shields.io/maven-central/v/com.yhyzgn.jakit/dynamic-datasource-jpa-spring-boot-starter?color=brightgreen&label=dynamic-datasource-jpa-spring-boot-starter&logo=gradle&logoColor=orange&style=flat-square)

> 动态添加和切换数据源



## 1、插件引入

> 此处以`maven`仓库`pom`引入方式为例
>
> `${latestVersion}`请参考上述徽标

```xml
<dependency>
  <groupId>com.yhyzgn.jakit</groupId>
  <artifactId>dynamic-datasource-jpa-spring-boot-starter</artifactId>
  <version>${latestVersion}</version>
</dependency>
```



## 2、动态数据源切换

> 示例中包含动态创建数据库、同步数据表结构的功能
>
> 请参考 `com.yhy.jakit.simple.dynamic.datasource.jpa.interceptor.DataSourceInterceptor` 类实现

```java
@Component
public class DataSourceInterceptor implements HandlerInterceptor {
  @Autowired
  private DynamicDataSource dataSource;

  @Override
  public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
    /*
        所有配置可自己定义来源，比如配置文件，或者配置中心服务
        注意：
            1/2 两步为动态创建数据库和数据表，如果不需要则直接略过即可
     */
    String dsName = request.getHeader("DS");
    if (StringUtils.hasText(dsName)) {
      String dbName = "db_" + dsName;

      // 1、检查并创建数据库
      String dbServerUrl = "jdbc:mysql://localhost:3306/";
      Assert.isTrue(DBUtils.createDatabase(dbServerUrl, "root", "root", dbName) > 0, "数据库创建失败");

      // 2、建库后生成所有的表
      DataSourceConfig config = DataSourceConfig.builder()
        .dialect(MySQL8Dialect.class.getCanonicalName())
        .driver(Driver.class.getCanonicalName())
        .url("jdbc:mysql://localhost:3306/" + dbName + "?useUnicode=true&allowPublicKeyRetrieval=true&characterEncoding=UTF-8&useSSL=false&zeroDateTimeBehavior=convertToNull&serverTimezone=Asia/Shanghai")
        .username("root")
        .password("root")
        .build();
      DBUtils.updateSchema(config);

      ///3、动态数据源
      DataSourceProperties properties = new DataSourceProperties();
      properties.setName(dsName);
      properties.setDriverClassName(Driver.class.getCanonicalName());
      properties.setUrl("jdbc:mysql://localhost:3306/" + dbName + "?useUnicode=true&characterEncoding=UTF-8&useSSL=false&zeroDateTimeBehavior=convertToNull&serverTimezone=Asia/Shanghai");
      properties.setUsername("root");
      properties.setPassword("root");

      // 动态添加并切换
      dataSource.addAndSwitchTo(properties);
    } else {
      dataSource.switchToDefault();
    }
    return true;
  }

  @Override
  public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
    dataSource.destroy();
  }
}
```



## 3、使用动态数据源

> 以配置的数据源已经交由`Spring JPA`管理，在做数据读写操作时将`JPA`将自动获取使用当前数据源
>
> 请参考 `com.yhy.jakit.simple.dynamic.datasource.jpa.controller.JPAController` 类

```java
@RestController
@RequestMapping("/jpa")
public class JPAController {
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

测试方法：

> 通过配置获取取消请求头`DS`来模拟动态切换数据源，取消时则使用默认数据源

```shell
curl --location --request GET 'localhost:8888/jpa?name=%E6%9D%8E%E4%B8%87%E5%A7%AC' --header 'DS: 02'
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

