# 1.IOC/DI 注解开发

## 1.1 注解开发定义 Bean

1. 在实现类上添加 `@Component` 注解：

   ```java
   @Component("bookDao")
   public class BookDaoImpl implements BookDao{
       @Override
       public void save() {
           System.out.println("Book dao save");
       }
   }
   ```

2. 在 XML 文件中配置 Spring 注解包扫描

   ```xml
   <?xml version="1.0" encoding="UTF-8"?>
   <beans xmlns="http://www.springframework.org/schema/beans"
          xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
          xmlns:context="http://www.springframework.org/schema/context"
          xsi:schemaLocation="http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans.xsd http://www.springframework.org/schema/context https://www.springframework.org/schema/context/spring-context.xsd">
   
       <context:component-scan base-package="com.rainsun"/>
   
   </beans>
   ```

   component-scan 表示组件扫描

   base-package 属性用于配置扫描的包路径

3. Serveric 上也添加注解：

   不起名字默认类名首字母小写：bookServiceImpl

   ```java
   @Component
   public class BookServiceImpl implements BookService{
       private BookDao bookDao;
   
       public void setBookDao(BookDao bookDao) {
           this.bookDao = bookDao;
       }
   
       @Override
       public void save() {
           System.out.println("Book service save");
           bookDao.save();
       }
   }
   ```

@Component 注解衍生出了其他三个注解：@Controller，@Service，@Repository

用于区分出这个类是属于 表现层，业务层 还是 数据层

![image-20240102150450891](https://xiongyuqing-img.oss-cn-qingdao.aliyuncs.com/img/202401021504975.png)

## 1.2 注解开发模式

上面的 Bean 定义用了注解，但是包扫描依然用到了配置文件，Spring 3.0 已经支持了纯注解开发，用Java类代替配置文件

1. **创建配置类并用注解 `@Configuration` 标识为配置类**

2. **用注解 `@ComponentScan` 替换包扫描范围的配置**

   ```java
   @ComponentScan("com.rainsun")
   @Configuration
   public class SpringConfig {
   }
   ```

3. 创建运行类 AppForAnnotation ：

   其中的配置是从配置类中加载的，而不是 XML 文件中加载的

   ```java
   public class AppForAnnotation {
       public static void main(String[] args) {
           ApplicationContext ctx = new AnnotationConfigApplicationContext(SpringConfig.class);
   
           BookDao bookDao = (BookDao)ctx.getBean("bookDao");
           bookDao.save();
           BookService bookService = (BookService)ctx.getBean("bookServiceImpl");
           bookService.save();
       }
   }
   ```

4.  使用 `@Scope` 注解配置Bean的作用范围：

   ```java
   @Repository
   @Scope("prototype")
   public class BookDaoImpl implements BookDao{
       @Override
       public void save() {
           System.out.println("Book dao save");
       }
   }
   ```

5. **使用疏解配置Bean的生命周期控制：**

   导入包含下面注解的依赖：

   ```xml
   <dependency>
       <groupId>javax.annotation</groupId>
       <artifactId>javax.annotation-api</artifactId>
       <version>1.2</version>
   </dependency>
   ```

   使用方法注解 `@PostConstruct` 配置bean初始化后需要执行的一些操作

   使用方法注解 `@PreDestroy` 配置容器关闭销毁 bean 前需要执行的一些操作

   ```java
   @Repository
   public class BookDaoImpl implements BookDao{
       @Override
       public void save() {
           System.out.println("Book dao save");
       }
   
       @PostConstruct
       public void init() {
           System.out.println("Book dao init");
       }
   
       @PreDestroy
       public void destroy() {
           System.out.println("Book dao destroy");
       }
   }
   ```

6. 获取 Bean 观察结果：

   ```java
   public class AppForAnnotation {
       public static void main(String[] args) {
           AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext(SpringConfig.class);
           BookDao bookDao = (BookDao)ctx.getBean("bookDaoImpl");
           bookDao.save();
           ctx.close(); //关闭容器
       }
   }
   /**
    * 输出：
    * Book dao init
    * Book dao save
    * Book dao destroy
    */
   ```

## 1.3 注解开发依赖注入

使用注解 `@Autowired` 进行自动装配，按照类型自动注入依赖

`@Autowired` 是用暴力反射的方式实现依赖注入的，所以不需要使用Setter方法，但需要提供无参构造方法

```java
@Component
public class BookServiceImpl implements BookService{
    @Autowired
    private BookDao bookDao;

    @Override
    public void save() {
        System.out.println("Book service save");
        bookDao.save();
    }
}
```

多个实现类时，按照类型进行自动装配就会报错，这时可以使用 `@Qualifier` 注解实现按照名称注入

### 简单类型注入

使用 `@Value` 注解注入简单类型的值：

```java
@Repository
public class BookDaoImpl implements BookDao{
    @Value("rainsun")
    private String name;
    
    @Override
    public void save() {
        System.out.println("Book dao save");
    }
}
```

### 注入 properties 配置文件中的值

1. 在配置类上用 `@PropertySource` 注解加载配置文件

   ```java
   @PropertySource("jdbc.properties")
   @ComponentScan("com.rainsun")
   @Configuration
   public class SpringConfig {
   }
   ```

2. 用 `@Value` 注解和 `$` 引用配置文件的 key ，获取对应的 value 值

   ```java
   @Repository
   public class BookDaoImpl implements BookDao{
   
       @Value("${name}")
       private String name;
   
       @Override
       public void save() {
           System.out.println("Book dao save");
       }
   }
   ```

## 1.4 注解开发管理第三方 Bean

1. 导入对应的 Jar 包：

   ```xml
   <dependency>
       <groupId>com.alibaba</groupId>
       <artifactId>druid</artifactId>
       <version>1.1.16</version>
   </dependency>
   ```

2. 在配置类添加一个方法用于返回要创建的 Bean 对象，并使用 `@Bean` 注解把返回值给Spring管理：

   ```java
   @Configuration
   public class SpringConfig {
   	@Bean
       public DataSource dataSource(){
           DruidDataSource ds = new DruidDataSource();
           ds.setDriverClassName("com.mysql.jdbc.Driver");
           ds.setUrl("jdbc:mysql://localhost:3306/spring_db");
           ds.setUsername("root");
           ds.setPassword("1234");
           return ds;
       }
   }
   ```

## 1.5 引入外部配置类 

所有的第三方 Bean都配置到Spring配置类 SpringConfig 中不便于管理，所以我们可以按照按照类别将这些bean配置到不同的配置类中

1. 对于数据源 Bean，新建一个 JdbcConfig 配置类，把配置写到该类下：

```java
public class JdbcConfig {
	@Bean
    public DataSource dataSource(){
        DruidDataSource ds = new DruidDataSource();
        ds.setDriverClassName("com.mysql.jdbc.Driver");
        ds.setUrl("jdbc:mysql://localhost:3306/spring_db");
        ds.setUsername("root");
        ds.setPassword("1234");
        return ds;
    }
}
```

2. 使用 `@Import` 注解引入需要加载的配置类到Spring配置类中：

```java
@Configuration
//@ComponentScan("com.itheima.config")
@Import({JdbcConfig.class})
public class SpringConfig {
}
```

## 1.6 为第三方 Bean 注入资源

### 简单数据类型

使用`@Value`注解引入值

```java
public class JdbcConfig {
    @Value("com.mysql.jdbc.Driver")
    private String driver;
    @Value("jdbc:mysql://localhost:3306/spring_db")
    private String url;
    @Value("root")
    private String userName;
    @Value("password")
    private String password;
	@Bean
    public DataSource dataSource(){
        DruidDataSource ds = new DruidDataSource();
        ds.setDriverClassName(driver);
        ds.setUrl(url);
        ds.setUsername(userName);
        ds.setPassword(password);
        return ds;
    }
}
```

### 引用数据类型

1. 设置扫描的范围，扫描范围需要包含需要的 bean

   例如这里需要用到 BookDao 的Bean:

   ```java
   @Configuration
   @ComponentScan("com.rainsun.dao")
   @Import({JdbcConfig.class})
   public class SpringConfig {
   }
   ```

2. 在JdbcConfig类的方法上添加需要用到的形参 Bean

   ```java
   @Bean
   public DataSource dataSource(BookDao bookDao){
       System.out.println(bookDao);
       DruidDataSource ds = new DruidDataSource();
       ds.setDriverClassName(driver);
       ds.setUrl(url);
       ds.setUsername(userName);
       ds.setPassword(password);
       return ds;
   }
   ```

   ==引用类型注入只需要为bean定义方法设置形参即可，容器会根据类型自动装配对象。==

# 2 Spring 整合第三方工具

## 2.1 Spring 整合 Mybatis

1. 导入需要整合的 jar 包

   ```xml
   <dependency>
       <groupId>org.springframework</groupId>
       <artifactId>spring-jdbc</artifactId>
       <version>6.1.1</version>
   </dependency>
   <dependency>
       <groupId>org.mybatis</groupId>
       <artifactId>mybatis-spring</artifactId>
       <version>3.0.3</version>
   </dependency>
   ```

2. 使用 `@Configuration` 注解创建 Spring 配置类管理 Bean 

   - `@ComponentScan` 设置bean的包扫描范围
   - `@PropertySource` 设置 properties 文件名，导入配置信息

   ```java
   @Configuration
   @ComponentScan("com.rainsun")
   public class SpringConfig {
   }
   ```

3. 创建数据源配置类

   ```java
   public class JdbcConfig {
       @Value("${jdbc.driver}")
       private String driver;
       @Value("${jdbc.url}")
       private String url;
       @Value("${jdbc.username}")
       private String userName;
       @Value("${jdbc.password}")
       private String password;
   
       @Bean
       public DataSource dataSource(){
           DruidDataSource ds = new DruidDataSource();
           ds.setDriverClassName(driver);
           ds.setUrl(url);
           ds.setUsername(userName);
           ds.setPassword(password);
           return ds;
       }
   }
   ```

   `$` 中的内容从 properties 文件中读取，所以需要在Spring配置类中用 `@PropertySource` 注解导入 properties 文件路径

4. 主配置类中读properties并引入数据源配置类

   ```java
   @Configuration
   @ComponentScan("com.rainsun")
   @PropertySource("jdbc.properties")
   @Import(JdbcConfig.class)
   public class SpringConfig {
   }
   ```

5. **创建 Mybaties 配置类并配置 SqlSessionFactory**

   ```java
   public class MybatisConfig {
       //定义bean，SqlSessionFactoryBean，用于产生SqlSessionFactory对象
       @Bean
       public SqlSessionFactoryBean sqlSessionFactory(DataSource dataSource){
           SqlSessionFactoryBean ssfb = new SqlSessionFactoryBean();
           //设置模型类的别名扫描
           ssfb.setTypeAliasesPackage("com.rainsun.pojo");
           //设置 JdbcConfig 数据源
           ssfb.setDataSource(dataSource);
           return ssfb;
       }
       //定义bean，返回MapperScannerConfigurer对象
       @Bean
       public MapperScannerConfigurer mapperScannerConfigurer(){
           MapperScannerConfigurer msc = new MapperScannerConfigurer();
           msc.setBasePackage("com.rainsun.dao");
           return msc;
       }
   }
   ```

6. 主配置中再把Mybatis的配置类引入：

   ```java
   @Configuration
   @ComponentScan("com.rainsun")
   @PropertySource("jdbc.properties")
   @Import({JdbcConfig.class, MybatisConfig.class})
   public class SpringConfig {
   }
   ```

7. 重新编写运行类

   ```java
   public class App2 {
       public static void main(String[] args) {
           ApplicationContext ctx = new AnnotationConfigApplicationContext(SpringConfig.class);
   
           AccountService accountService = ctx.getBean(AccountService.class);
   
           Account ac = accountService.findById(1);
           System.out.println(ac);
       }
   }
   ```



* 使用SqlSessionFactoryBean封装SqlSessionFactory需要的环境信息

  ![1630138835057](https://xiongyuqing-img.oss-cn-qingdao.aliyuncs.com/img/202401022016708.png)

  * SqlSessionFactoryBean是前面我们讲解FactoryBean的一个子类，在该类中将SqlSessionFactory的创建进行了封装，简化对象的创建，我们只需要将其需要的内容设置即可。
  * 方法中有一个参数为dataSource,当前Spring容器中已经创建了Druid数据源，类型刚好是DataSource类型，此时在初始化SqlSessionFactoryBean这个对象的时候，发现需要使用DataSource对象，而容器中刚好有这么一个对象，就自动加载了DruidDataSource对象。

* 使用MapperScannerConfigurer加载Dao接口，创建代理对象保存到IOC容器中

  ![1630138916939](https://xiongyuqing-img.oss-cn-qingdao.aliyuncs.com/img/202401022016422.png)

  * 这个MapperScannerConfigurer对象也是MyBatis提供的专用于整合的jar包中的类，用来处理原始配置文件中的mappers相关配置，加载数据层的Mapper接口类
  * MapperScannerConfigurer有一个核心属性basePackage，就是用来设置所扫描的包路径

## 2.2 Spring 整合 Junit

1. 引入依赖：Junit 和 spring-test

```xml
<dependency>
    <groupId>junit</groupId>
    <artifactId>junit</artifactId>
    <version>3.8.1</version>
    <scope>test</scope>
</dependency>
<dependency>
    <groupId>org.springframework</groupId>
    <artifactId>spring-test</artifactId>
    <version>6.1.2</version>
</dependency>
```

2. 编写测试类，在test/java下得service包下创建AccountServiceTest

   ```java
   //设置类运行器
   @RunWith(SpringJUnit4ClassRunner.class)
   //设置Spring环境对应的配置类
   @ContextConfiguration(classes = {SpringConfiguration.class}) //加载配置类
   //@ContextConfiguration(locations={"classpath:applicationContext.xml"})//加载配置文件
   public class AccountServiceTest {
       //支持自动装配注入bean
       @Autowired
       private AccountService accountService;
       @Test
       public void testFindById(){
           System.out.println(accountService.findById(1));
   
       }
       @Test
       public void testFindAll(){
           System.out.println(accountService.findAll());
       }
   }
   ```

   **注意:**

   * 单元测试，如果测试的是注解配置类，则使用`@ContextConfiguration(classes = 配置类.class)`
   * 单元测试，如果测试的是配置文件，则使用`@ContextConfiguration(locations={配置文件名,...})`
   * Junit运行后是基于Spring环境运行的，所以Spring提供了一个专用的类运行器，这个务必要设置，这个类运行器就在Spring的测试专用包中提供的，导入的坐标就是这个东西`SpringJUnit4ClassRunner`
   * 上面两个配置都是固定格式，当需要测试哪个bean时，使用自动装配加载对应的对象，下面的工作就和以前做Junit单元测试完全一样了
