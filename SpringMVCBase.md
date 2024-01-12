# 1 SpringMVC

web 程序大都基于三层架构：Controller(web)层，Service层和Dao层

其中web层主要由servlet来处理，负责页面请求和数据的收集以及响应结果给前端。但是servlet处理请求和数据的时候，存在的问题是一个servlet只能处理一个请求

于是针对web层进行了优化，采用了MVC设计模式，将其设计为`controller`、`view`和`Model`。以便`controller`将请求分发给对应得service和dao，实现同时处理多个请求，再将返回的结果在controller进行组装为 Model 和View转发给前端

SpringMVC==主要==负责的就是

* controller如何接收请求和数据
* 如何将请求和数据转发给业务层
* 如何将响应数据转换成json发回到前端

定义：SpringMVC是一种基于Java实现MVC模型的轻量级Web框架

# 2 入门案例

1. 新建项目：

![image-20240112092523816](https://xiongyuqing-img.oss-cn-qingdao.aliyuncs.com/img/202401120925902.png)

2:补全目录结构

![image-20240112092528799](https://xiongyuqing-img.oss-cn-qingdao.aliyuncs.com/img/202401120925916.png)

3. 导入SpringMVC所需的jar包依赖

> 注意：
>
> - **此版本是基于 JDK 21 的，对应的 spring-webmvc 版本选择是 5.3.0**（其他版本会有各式各样的错误）

```xml
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/maven-v4_0_0.xsd">
    <modelVersion>4.0.0</modelVersion>
    <parent>
        <groupId>com.rainsun</groupId>
        <artifactId>SpringFramework</artifactId>
        <version>1.0-SNAPSHOT</version>
    </parent>
    <artifactId>springmvc_01_base</artifactId>
    <packaging>war</packaging>
    <name>springmvc_01_base Maven Webapp</name>
    <url>http://maven.apache.org</url>
    <dependencies>
<!--        1. 导入坐标springmvc 与 Servlet-->
        <dependency>
            <groupId>javax.servlet</groupId>
            <artifactId>javax.servlet-api</artifactId>
            <version>3.1.0</version>
            <scope>provided</scope>
        </dependency>
        <dependency>
            <groupId>org.springframework</groupId>
            <artifactId>spring-webmvc</artifactId>
            <version>5.3.0</version>
        </dependency>

        <dependency>
            <groupId>junit</groupId>
            <artifactId>junit</artifactId>
            <version>3.8.1</version>
            <scope>test</scope>
        </dependency>
    </dependencies>

    <build>
        <plugins>
            <plugin>
                <groupId>org.apache.tomcat.maven</groupId>
                <artifactId>tomcat7-maven-plugin</artifactId>
                <version>2.2</version>
                <configuration>
                    <port>8081</port>
                    <path>/</path>
                </configuration>
            </plugin>
        </plugins>
    </build>
</project>
```

> **说明：**servlet的坐标需要添加`<scope>provided</scope>`:
>
> * scope是maven中jar包依赖作用范围的描述，
> * 如果不设置默认是`compile`在在编译、运行、测试时均有效
> * 如果运行有效的话就会和tomcat中的servlet-api包发生冲突，导致启动报错
>
> * provided代表的是该包只在编译和测试的时候用，运行的时候无效直接使用tomcat中的，就避免冲突

4:创建Controller类

```java
//2. 定义Controller
// 2.1 用@Controller用于定义Bean
@Controller
public class UserController {
    // 2.2 设置当前操作的访问路径
    @RequestMapping("/save")
    // 2.3 设置当前操作的返回值类型
    @ResponseBody
    public String save(){
        System.out.println("user save ...");
        return "{'module':'springmvc'}";
    }
}
```

5：创建配置类

```java
// 3. 创建 springmvc 的配置文件，加载 controller 对应的 bean
@Configuration
@ComponentScan("com.rainsun.controller")
public class SpringMvcConfig {
}
```

6:使用配置类替换web.xml。用于配置tomcat，将配置类作为配置加载到tomcat，并设置映射路径

```java
// 4. 定义一个 servlet 容器启动的配置类。用于加载 spring的配置
public class ServletContainerInitConfig extends AbstractDispatcherServletInitializer {
    // 加载 SpringMVC 容器配置
    @Override
    protected WebApplicationContext createServletApplicationContext() {
        AnnotationConfigWebApplicationContext ctx = new AnnotationConfigWebApplicationContext();
        ctx.register(SpringMvcConfig.class);
        return ctx;
    }

    // 设置那些请求归属于 SpringMVC 处理
    @Override
    protected String[] getServletMappings() {
        return new String[]{"/"};
    }

    // 加载 spring容器配置
    @Override
    protected WebApplicationContext createRootApplicationContext() {
        return null;
    }
}
```

7:配置Tomcat环境

![image-20240112093426997](https://xiongyuqing-img.oss-cn-qingdao.aliyuncs.com/img/202401120934085.png)

双击这里也是一样的效果：

![image-20240112093510308](https://xiongyuqing-img.oss-cn-qingdao.aliyuncs.com/img/202401120935345.png)

8:启动运行项目

![image-20240112093547193](https://xiongyuqing-img.oss-cn-qingdao.aliyuncs.com/img/202401120935302.png)

9:浏览器访问

http://localhost:8081/ 这里显示的是 index.jsp里的内容

![image-20240112093829755](https://xiongyuqing-img.oss-cn-qingdao.aliyuncs.com/img/202401120938835.png)

访问自己映射的路径：http://localhost:8081/save：

![image-20240112093918206](https://xiongyuqing-img.oss-cn-qingdao.aliyuncs.com/img/202401120939242.png)

>**注意事项**
>
>* SpringMVC是基于Spring的，在pom.xml只导入了`spring-webmvc`jar包的原因是它会自动依赖spring相关坐标
>* AbstractDispatcherServletInitializer类是SpringMVC提供的快速初始化Web3.0容器的抽象类
>* AbstractDispatcherServletInitializer提供了三个接口方法供用户实现
>  * createServletApplicationContext方法，创建Servlet容器时，加载SpringMVC对应的bean并放入WebApplicationContext对象范围中，而WebApplicationContext的作用范围为ServletContext范围，即整个web容器范围
>  * getServletMappings方法，设定SpringMVC对应的请求映射路径，即SpringMVC拦截哪些请求
>  * createRootApplicationContext方法，如果创建Servlet容器时需要加载非SpringMVC对应的bean,使用当前方法进行，使用方式和createServletApplicationContext相同。
>  * createServletApplicationContext用来加载SpringMVC环境
>  * createRootApplicationContext用来加载Spring环境

## 2.1 工作流程

![image-20240112094627303](https://xiongyuqing-img.oss-cn-qingdao.aliyuncs.com/img/202401120946471.png)

### 启动服务器初始化过程

1. 服务器启动，执行ServletContainersInitConfig类，初始化web容器

   * 功能类似于以前的web.xml

2. 执行createServletApplicationContext方法，创建了WebApplicationContext对象

   * 该方法加载SpringMVC的配置类SpringMvcConfig来初始化SpringMVC的容器

3. 加载SpringMvcConfig配置类

   ![1630433335744](https://xiongyuqing-img.oss-cn-qingdao.aliyuncs.com/img/202401120946191.png)

4. 执行@ComponentScan加载对应的bean

   * 扫描指定包及其子包下所有类上的注解，如Controller类上的@Controller注解

5. 加载UserController，每个@RequestMapping的名称对应一个具体的方法

   ![1630433398932](https://xiongyuqing-img.oss-cn-qingdao.aliyuncs.com/img/202401120946196.png)

   * 此时就建立了 `/save` 和 save方法的对应关系

6. 执行getServletMappings方法，设定SpringMVC拦截请求的路径规则

   ![1630433510528](https://xiongyuqing-img.oss-cn-qingdao.aliyuncs.com/img/202401120946202.png)

   * `/`代表所拦截请求的路径规则，只有被拦截后才能交给SpringMVC来处理请求

### 单次请求过程

1. 发送请求`http://localhost/save`
2. web容器发现该请求满足SpringMVC拦截规则，将请求交给SpringMVC处理
3. 解析请求路径/save
4. 由/save匹配执行对应的方法save(）
   * 上面的第五步已经将请求路径和方法建立了对应关系，通过/save就能找到对应的save方法
5. 执行save()
6. 检测到有@ResponseBody直接将save()方法的返回值作为响应体返回给请求方

##  2.2 bean加载控制

有些bean需要给Spring控制，有些bean需要给SpringMVC控制。

项目结构为：

* config目录存入的是配置类,写过的配置类有:

  * ServletContainersInitConfig
  * SpringConfig
  * SpringMvcConfig
  * JdbcConfig
  * MybatisConfig
* controller目录存放的是SpringMVC的controller类
* service目录存放的是service接口和实现类
* dao目录存放的是dao/Mapper接口

其中controller、service和dao这些类都需要被容器管理成bean对象，那么到底是该让SpringMVC加载还是让Spring加载呢?

* SpringMVC加载其相关bean(表现层bean),也就是controller包下的类
* Spring控制的bean
  * 业务bean(Service)
  * 功能bean(DataSource,SqlSessionFactoryBean,MapperScannerConfigurer等)

接下来要解决的问题是如何让Spring和SpringMVC分开加载各自的内容。

在SpringMVC的配置类`SpringMvcConfig`中使用注解`@ComponentScan`，我们只需要将其扫描范围设置到controller即可，如

```java
@Configuration
@ComponentScan("com.rainsun.controller")
public class SpringMvcConfig {
}
```

在Spring的配置类`SpringConfig`中使用注解`@ComponentScan`,当时扫描的范围`com.rainsun` 中其实是已经包含了controller,如:

```java
@Configuration
@ComponentScan("com.rainsun")
@PropertySource("classpath:jdbc.properties")
@Import({JdbcConfig.class, MybatisConfig.class})
@EnableTransactionManagement
public class SpringConfig {
}
```

从包结构来看的话，Spring已经多把SpringMVC的controller类也给扫描到，所以需要解决**因为功能不同，如何避免Spring错误加载到SpringMVC的bean**

解决方案：

* 方式一:Spring加载的bean设定扫描范围为精准范围，例如service包、dao包等
* 方式二:Spring加载的bean设定扫描范围为com.itheima,排除掉controller包中的bean
* 方式三:不区分Spring与SpringMVC的环境，加载到同一个环境中[了解即可]

### 配置测试环境

入门案例的基础上导入使用mybatis的jar依赖：mysql, jdbc, druid, mybatis

```xml
<dependency>
    <groupId>com.alibaba</groupId>
    <artifactId>druid</artifactId>
    <version>1.2.15</version>
</dependency>

<dependency>
    <groupId>org.mybatis</groupId>
    <artifactId>mybatis</artifactId>
    <version>3.5.9</version>
</dependency>

<dependency>
    <groupId>com.mysql</groupId>
    <artifactId>mysql-connector-j</artifactId>
    <version>8.2.0</version>
</dependency>

<dependency>
    <groupId>org.springframework</groupId>
    <artifactId>spring-jdbc</artifactId>
    <version>6.1.1</version>
</dependency>
```

新增Spring配置类：

```java
@Configuration
@ComponentScan("com.rainsun")
public class SpringConfig {
}
```

新增Service，Dao,Domain类：

```java
public interface UserService {
    public void save(User user);
}

@Service
public class UserServiceImpl implements UserService {
    public void save(User user) {
        System.out.println("user service ...");
    }
}

public interface UserDao {
    @Insert("insert into tbl_user(name,age)values(#{name},#{age})")
    public void save(User user);
}
public class User {
    private Integer id;
    private String name;
    private Integer age;
    //setter..getter..toString略
}
```

### 设置bean加载控制

方式一:修改Spring配置类，设定扫描范围为精准范围。

```java
@Configuration
@ComponentScan({"com.rainsun.service", "com.rainsun.dao"})
public class SpringConfig {
}
```

> 说明: 上述只是通过例子说明可以精确指定让Spring扫描对应的包结构，真正在做开发的时候，因为Dao最终是交给`MapperScannerConfigurer`对象来进行扫描处理的，我们只需要将其扫描到service包即可。

方式二:修改Spring配置类，设定扫描范围为com.itheima,排除掉controller包中的bean

```java
@Configuration
@ComponentScan(value="com.rainsun",
excludeFilters = @ComponentScan.Filter(
        type= FilterType.ANNOTATION,
        classes = Controller.class
        )
)
public class SpringConfig {
}
```

* excludeFilters属性：设置扫描加载bean时，排除的过滤规则

* type属性：设置排除规则，当前使用按照bean定义时的注解类型进行排除

  * ANNOTATION：按照注解排除
  * ASSIGNABLE_TYPE:按照指定的类型过滤
  * ASPECTJ:按照Aspectj表达式排除，基本上不会用
  * REGEX:按照正则表达式排除
  * CUSTOM:按照自定义规则排除

  大家只需要知道第一种ANNOTATION即可

* classes属性：设置排除的具体注解类，当前设置排除@Controller定义的bean

测试：

- 注释 SpringMvcConfig中的ComponentScan
  - SpringMvcConfig上又有一个@ComponentScan，把controller类又给扫描进来了
  - 所以如果不把@ComponentScan注释掉，Spring配置类将Controller排除，但是因为扫描到SpringMVC的配置类，又将其加载回来，演示的效果就出不来

- 用SpringConfig配置容器，从容器中拿 Controller的bean，可以发现无法拿到

```java
public class App {
    public static void main(String[] args) {
        AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext(SpringConfig.class);
        System.out.println(ctx.getBean(UserController.class));
    }
}
```

![image-20240112103401810](https://xiongyuqing-img.oss-cn-qingdao.aliyuncs.com/img/202401121034912.png)

### tomcat 加载 spring 配置

有了Spring的配置类，要想在tomcat服务器启动将其加载，我们需要修改ServletContainersInitConfig

```java
// 4. 定义一个 servlet 容器启动的配置类。用于加载 spring的配置
public class ServletContainerInitConfig extends AbstractDispatcherServletInitializer {
    // 加载 SpringMVC 容器配置
    @Override
    protected WebApplicationContext createServletApplicationContext() {
        AnnotationConfigWebApplicationContext ctx = new AnnotationConfigWebApplicationContext();
        ctx.register(SpringMvcConfig.class);
        return ctx;
    }

    // 设置那些请求归属于 SpringMVC 处理
    @Override
    protected String[] getServletMappings() {
        return new String[]{"/"};
    }

    // 加载 spring容器配置
    @Override
    protected WebApplicationContext createRootApplicationContext() {
        AnnotationConfigWebApplicationContext ctx = new AnnotationConfigWebApplicationContext();
        ctx.register(SpringConfig.class);
        return ctx;
    }
}
```

对于上述的配置方式，Spring还提供了一种更简单的配置方式，可以不用再去创建`AnnotationConfigWebApplicationContext`对象，不用手动`register`对应的配置类，

```java
public class ServletContainersInitConfig extends AbstractAnnotationConfigDispatcherServletInitializer {

    protected Class<?>[] getRootConfigClasses() {
        return new Class[]{SpringConfig.class};
    }

    protected Class<?>[] getServletConfigClasses() {
        return new Class[]{SpringMvcConfig.class};
    }

    protected String[] getServletMappings() {
        return new String[]{"/"};
    }
}
```
