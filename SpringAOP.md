# 1 AOP 简介

## 什么是AOP？

AOP(Aspect Oriented Programming) 面向切面编程，是一种编程思想，用于指导开发者组织程序结构，可以在不改变原有代码的前提下对原有功能进行增强。

## AOP的核心概念

![image-20240103194629495](https://xiongyuqing-img.oss-cn-qingdao.aliyuncs.com/img/202401031946578.png)

* 连接点(JoinPoint)：程序执行过程中的任意位置，粒度为执行方法、抛出异常、设置变量等
  * 在SpringAOP中，理解为方法的执行
* 切入点(Pointcut):匹配连接点的式子
  * 在SpringAOP中，一个切入点可以描述一个具体方法，也可也匹配多个方法
    * 一个具体的方法:如com.itheima.dao包下的BookDao接口中的无形参无返回值的save方法
    * 匹配多个方法:所有的save方法，所有的get开头的方法，所有以Dao结尾的接口中的任意方法，所有带有一个参数的方法
  * 连接点范围要比切入点范围大，是切入点的方法也一定是连接点，但是是连接点的方法就不一定要被增强，所以可能不是切入点。
* 通知(Advice):在切入点处执行的操作，也就是共性功能
  * 在SpringAOP中，功能最终以方法的形式呈现
* 通知类：定义通知的类
* 切面(Aspect):描述通知与切入点的对应关系。

## 案例：打印方法调用时的时间

1. 添加依赖：spring和AspectJ(AOP思想的具体实现，spring实现的比较麻烦，这里用Spring整合AspectJ的方式进行开发)

```xml
<dependency>
    <groupId>org.aspectj</groupId>
    <artifactId>aspectjweaver</artifactId>
    <version>1.9.20.1</version>
</dependency>
<dependency>
    <groupId>org.springframework</groupId>
    <artifactId>spring-context</artifactId>
    <version>6.1.2</version>
</dependency>
```

2. 定义通知类和通知：将共性功能抽取成方法

```java
public class MyAdvice {
    public void method(){
        System.out.println(System.currentTimeMillis());
    }
}
```

3. 定义切入点，这里的切入点时 update 方法：

```java
public class MyAdvice {
    // 切入点
    @Pointcut("execution(void com.rainsun.dao.BookDao.update())")
    private void pt(){}
    
    public void method(){
        System.out.println(System.currentTimeMillis());
    }
}
```

4. 制作切面：描述通知和切入点之间的关系

```java
public class MyAdvice {
    @Pointcut("execution(void com.rainsun.dao.BookDao.update())")
    private void pt(){}
    
    // @Before是说通知会在切入点方法执行之前执行
    @Before("pt()")
    public void method(){
        System.out.println(System.currentTimeMillis());
    }
}
```

5. 通知类用Component注解作为bean配给容器，并标识为切面类

```java
@Component
@Aspect
public class MyAdvice {
    // 切入点
    @Pointcut("execution(void com.rainsun.dao.BookDao.update())")
    public void pt(){}
    // 切面，定义通知和切入点的连接方式
    @Before("pt()")
    public void method(){
        System.out.println(System.currentTimeMillis());
    }
}
```

6. SpringConfig 配置类中，开启注解格式的 AOP 功能

```java
@Configuration
@ComponentScan("com.rainsun")
@EnableAspectJAutoProxy
public class SpringConfig {
}
```

# 2. AOP 工作流程

1. Spring容器启动

启动过程中需要加载那些需要被增强的类，例如BookSerciceImpl，通知类（MyAdvice），这时 bean 对象还未创建成功

2. 读取所有切面中已经被配置或者说被使用的切入点

切入点可能有多个，但是使用的可能是其中几个，这里只读使用了的切入点

3. 初始化 bean，判定 bean 对应的类中的方法是否匹配到任意切入点

- 匹配失败，创建原始的对象

- 匹配成功，创建原始对象（**目标对象**）的**代理**对象

![image-20240103202025349](https://xiongyuqing-img.oss-cn-qingdao.aliyuncs.com/img/202401032020416.png)

4. 获取 bean 执行方法

- 获取bean原始对象，调用方法执行，完成操作
- 获取bean的代理对象，根据代理对象的运行模式运行原始方法与增强的内容，完成操作

# 3 AOP 配置管理

## 3.1 AOP 切入点表达式

- 切入点：要增强的方法
- 切入点表达式：要进行增强的方法的描述方式

- 切入点表达式的标准格式：

  - `动作关键字(访问修饰符 返回值 包名.类型/接口名.方法名(参数) 异常名)`

  - ```java
    execution(public User com.itheima.service.UserService.findById(int))
    ```

  - execution：动作关键字，描述切入点的行为动作，例如execution表示执行到指定切入点

  - public:访问修饰符,还可以是public，private等，可以省略

  - User：返回值，写返回值类型

  - com.itheima.service：包名，多级包使用点连接

  - UserService:类/接口名称

  - findById：方法名

  - int:参数，直接写参数的类型，多个类型用逗号隔开

  - 异常名：方法定义中抛出指定异常，可以省略

### 匹配多个切入点：通配符

* `*`:单个独立的任意符号，可以独立出现，也可以作为前缀或者后缀的匹配符出现

  ```
  execution（public * com.rainsun.*.UserService.find*(*))
  ```

  匹配com.itheimarainsun下的任意包中的UserService类或接口中所有find开头的带有一个参数的方法

* `..`：多个连续的任意符号，可以独立出现，常用于简化包名与参数的书写

  ```
  execution（public User com..UserService.findById(..))
  ```

  匹配com包下的任意包中的UserService类或接口中所有名称为findById的方法

* `+`：专用于匹配子类类型

  ```
  execution(* *..*Service+.*(..))
  ```

  这个使用率较低，描述子类的，咱们做JavaEE开发，继承机会就一次，使用都很慎重，所以很少用它。*Service+，表示所有以Service结尾的接口的子类。

**书写技巧**

对于切入点表达式的编写其实是很灵活的，那么在编写的时候，有没有什么好的技巧让我们用用:

- 所有代码按照标准规范开发，否则以下技巧全部失效
- 描述切入点通**==常描述接口==**，而不描述实现类,如果描述到实现类，就出现紧耦合了
- 访问控制修饰符针对接口开发均采用public描述（**==可省略访问控制修饰符描述==**）
- 返回值类型对于增删改类使用精准类型加速匹配，对于查询类使用\*通配快速描述
- **==包名==**书写**==尽量不使用..匹配==**，效率过低，常用\*做单个包描述匹配，或精准匹配
- **==接口名/类名==**书写名称与模块相关的**==采用\*匹配==**，例如UserService书写成\*Service，绑定业务层接口名
- **==方法名==**书写以**==动词==**进行**==精准匹配==**，名词采用*匹配，例如getById书写成getBy*,selectAll书写成selectAll
- 参数规则较为复杂，根据业务方法灵活调整
- 通常**==不使用异常==**作为**==匹配==**规则

## 3.2 AOP 通知类型

1. 前置通知：追加功能到方法执行前

```java
@Component
@Aspect
public class MyAdvice {
    @Pointcut("execution(void com.rainsun.dao.BookDao.update())")
    private void pt(){}
    
    @Before("pt()")
    //此处也可以写成 @Before("MyAdvice.pt()"),不建议
    public void before() {
        System.out.println("before advice ...");
    }
}
```

2. 后置通知,追加功能到方法执行后

```java
@After("pt()")
public void after() {
    System.out.println("after advice ...");
}
```

3. 返回后通知,追加功能到方法执行后，只有方法正常执行结束后才进行；return了才会通知，中间出异常了就不会执行通知

```java
@AfterReturning("pt2()")
public void afterReturning() {
    System.out.println("afterReturning advice ...");
}
```

4. 抛出异常后通知,追加功能到方法抛出异常后，只有方法执行出异常才进行,

```java
@AfterThrowing("pt2()")
public void afterThrowing() {
    System.out.println("afterThrowing advice ...");
}
```

5. **环绕通知**：环绕通知功能比较强大，它可以追加功能到方法执行的前后

```java
@Component
@Aspect
public class MyAdvice {
    @Pointcut("execution(void com.rainsun.dao.BookDao.update())")
    private void pt(){}
    
    @Pointcut("execution(int com.rainsun.dao.BookDao.select())")
    private void pt2(){}
    
    @Around("pt2()")
    public Object aroundSelect(ProceedingJoinPoint pjp) throws Throwable {
        System.out.println("around before advice ...");
        //表示对原始操作的调用
        Object ret = pjp.proceed();
        System.out.println("around after advice ...");
        return ret;
    }
}
```

- 因为环绕通知需要在原始方法的前后进行增强，所以环绕通知就必须要能对原始操作进行调用，这里通过传递一个 `ProceedingJoinPoint` 的形参进行，调用 proceed()方法就是对原始方法的调用。

- 原始方法有返回值时，还需要根据原始方法的返回值来设置环绕通知的返回值

## 3.3 AOP通知获取数据

有时候需要根据切入点的方法信息进行特殊操作。

这时候需要获取该方法的 `参数`，`返回值`，`异常` 这三个方面

获取方式：

1. 获取切入点方法的参数，所有的通知类型都可以获取参数

   * JoinPoint：适用于前置、后置、返回后、抛出异常后通知

   * ProceedingJoinPoint：适用于环绕通知

2. 获取切入点方法返回值，前置和抛出异常后通知是没有返回值，后置通知可有可无，所以不做研究

   * 返回后通知

   * 环绕通知

3. 获取切入点方法运行异常信息，前置和返回后通知是不会有，后置通知可有可无，所以不做研究

   * 抛出异常后通知
   * 环绕通知

----

1. 获取切入点的方法参数：

**非环绕通知获取方式**：在方法上添加JoinPoint,通过JoinPoint来获取参数

```java
@Before("pt()")
public void before(JoinPoint jp){
    Object[] args = jp.getArgs();
    System.out.println(Arrays.toString(args));
    System.out.println("before advice...");
}
```

**环绕通知获取方式**：

环绕通知使用的是ProceedingJoinPoint，因为ProceedingJoinPoint是JoinPoint类的子类，所以对于ProceedingJoinPoint类中应该也会有对应的`getArgs()`方法

pjp.proceed()方法是有两个构造方法，分别是:

![1630234756123](https://xiongyuqing-img.oss-cn-qingdao.aliyuncs.com/img/202401042055587.png)

* 调用无参数的proceed，当原始方法有参数，会在调用的过程中自动传入参数

* 所以调用这两个方法的任意一个都可以完成功能

* 但是当需要修改原始方法的参数时，就只能采用带有参数的方法,如下:

```java
@Around("pt()")
public Object around(ProceedingJoinPoint pjp) throws Throwable{
    Object[] args = pjp.getArgs();
    System.out.println(Arrays.toString(args));
    args[0] = 666;
    Object ret = pjp.proceed(args);
    return ret;
}
```

2. 获取返回值

只有返回后`AfterReturing`和环绕`Around`这两个通知类型可以获取

**环绕通知获取返回值**：

`Object ret = pjp.proceed(args);` 中的 ret 就是返回值

**返回后通知获取返回值**：

需要先定义一个获取返回值的形参

```java
@AfterReturning(value = "pt()",returning = "ret")
public void afterReturning(Object ret) {
    System.out.println("afterReturning advice ..."+ret);
}
```

(1)参数名的问题

![1630237320870](https://xiongyuqing-img.oss-cn-qingdao.aliyuncs.com/img/202401052116124.png)

(2)afterReturning方法参数类型的问题

参数类型可以写成String，但是为了能匹配更多的参数类型，建议写成Object类型

(3)afterReturning方法参数的顺序问题

![1630237586682](https://xiongyuqing-img.oss-cn-qingdao.aliyuncs.com/img/202401052116162.png)

3. 获取异常

对于获取抛出的异常，只有抛出异常后`AfterThrowing`和环绕`Around`这两个通知类型可以获取

**环绕通知获取异常**：

在catch方法中就可以获取到异常，至于获取到异常以后该如何处理，这个就和业务需求有关了。

```java
@Around("pt()")
public Object around(ProceedingJoinPoint pjp){
    Object[] args = pjp.getArgs();
    System.out.println(Arrays.toString(args));
    args[0] = 666;
    Object ret = null;
    try{
        ret = pjp.proceed(args);
    }catch(Throwable throwable){
        t.printStackTrace();
    }
    return ret;
}
```

**抛出异常后通知获取异常:**

```java
@AfterThrowing(value = "pt()",throwing = "t")
public void afterThrowing(Throwable t) {
    System.out.println("afterThrowing advice ..."+t);
}
```

# 4 AOP 事务管理

- 事务作用：在数据层保障一系列的数据库操作同成功同失败
- Spring事务作用：在数据层或**==业务层==**保障一系列的数据库操作同成功同失败

1. 添加依赖：

```xml
<dependencies>
    <dependency>
        <groupId>org.springframework</groupId>
        <artifactId>spring-context</artifactId>
        <version>6.1.2</version>
    </dependency>

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

    <dependency>
        <groupId>org.mybatis</groupId>
        <artifactId>mybatis-spring</artifactId>
        <version>3.0.3</version>
    </dependency>

    <dependency>
        <groupId>org.springframework</groupId>
        <artifactId>spring-test</artifactId>
        <version>6.1.2</version>
    </dependency>

    <dependency>
        <groupId>junit</groupId>
        <artifactId>junit</artifactId>
        <version>3.8.1</version>
        <scope>test</scope>
    </dependency>
</dependencies>
```

2. 创建原型类 Account

```java
public class Account implements Serializable {

    private Integer id;
    private String name;
    private Double money;
	//setter...getter...toString...方法略    
}
```

3. 创建 Dao 接口

```java
public interface AccountDao {

    @Update("update tbl_account set money = money + #{money} where name = #{name}")
    void inMoney(@Param("name") String name, @Param("money") Double money);

    @Update("update tbl_account set money = money - #{money} where name = #{name}")
    void outMoney(@Param("name") String name, @Param("money") Double money);
}
```

4. Service接口&实现类

```java
public interface AccountService {
    /**
     * 转账操作
     * @param out 传出方
     * @param in 转入方
     * @param money 金额
     */
    public void transfer(String out,String in ,Double money) ;
}

@Service
public class AccountServiceImpl implements AccountService {

    @Autowired
    private AccountDao accountDao;

    public void transfer(String out,String in ,Double money) {
        accountDao.outMoney(out,money);
        accountDao.inMoney(in,money);
    }

}
```

5. 配置JDBC形成一个数据库连接池用于获取数据库连接对象

添加jdbc.properties文件

```java
jdbc.driver=com.mysql.jdbc.Driver
jdbc.url=jdbc:mysql://localhost:3306/spring_db?useSSL=false
jdbc.username=root
jdbc.password=root
```

创建JdbcConfig配置类

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

6. 创建MybatisConfig配置类，用于创建操纵 SQL 语句的对象，并定义扫描的范围

```java
public class MybatisConfig {
    @Bean
    public SqlSessionFactoryBean sqlSessionFactory(DataSource dataSource){
        SqlSessionFactoryBean ssfb = new SqlSessionFactoryBean();
        ssfb.setTypeAliasesPackage("com.rainsun.domain");
        ssfb.setDataSource(dataSource);
        return ssfb;
    }

    @Bean
    public MapperScannerConfigurer mapperScannerConfigurer(){
        MapperScannerConfigurer msc = new MapperScannerConfigurer();
        msc.setBasePackage("com.rainsun.dao");
        return msc;
    }
}
```

7. 创建SpringConfig配置类，定义IoC容器扫描获得Bean的范围，引入其他的配置

```java
@Configuration
@ComponentScan("com.rainsun")
@PropertySource("classpath:jdbc.properties")
@Import({JdbcConfig.class, MybatisConfig.class})
public class SpringConfig {
}
```

8. 测试类，表示执行上下文用的配置来自SpringConfig类，以及用测试的方式执行

```java
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = SpringConfig.class)
public class AccountServiceTest {
    @Autowired
    private AccountService accountService;

    @Test
    public void testTransfer() throws IOException {
        accountService.transfer("Tom","Jerry",100D);
    }
}
```

![image-20240111163610301](https://xiongyuqing-img.oss-cn-qingdao.aliyuncs.com/img/202401111636385.png)

## 4.1 事务管理

1. 在需要事务管理的方法上添加注解

```java
public interface AccountService {
    @Transactional
    public void transfer(String out, String in, Double money);
}
```

==注意:==

@Transactional可以写在接口类上、接口方法上、实现类上和实现类方法上

* 写在接口类上，该接口的所有实现类的所有方法都会有事务
* 写在接口方法上，该接口的所有实现类的该方法都会有事务
* 写在实现类上，该类中的所有方法都会有事务
* 写在实现类方法上，该方法上有事务
* 建议写在实现类或实现类的方法上

2. 在Jdbc中配置事务管理器,mybatis使用的是jdbc事务

```java
@Bean
public PlatformTransactionManager transactionManager(DataSource dataSource){
    DataSourceTransactionManager transactionManager = new DataSourceTransactionManager();
    transactionManager.setDataSource(dataSource);
    return transactionManager;
}
```

3. 在SpringConfig的配置类中开启事务注解

```java
@Configuration
@ComponentScan("com.rainsun")
@PropertySource("classpath:jdbc.properties")
@Import({JdbcConfig.class, MybatisConfig.class})
@EnableTransactionManagement
public class SpringConfig {
}
```

## 4.2 事务角色

1. 未开启事务前：有两个事务 T1,T2
   - AccountDao的outMoney因为是修改操作，会开启一个事务T1
   - AccountDao的inMoney因为是修改操作，会开启一个事务T2
   - AccountService的transfer没有事务，
     * 运行过程中如果没有抛出异常，则T1和T2都正常提交，数据正确
     * 如果在两个方法中间抛出异常，T1因为执行成功提交事务，T2因为抛异常不会被执行
     * 就会导致数据出现错误
2. 开启Spring的事务管理后：只有一个事务 T
   - transfer上添加了@Transactional注解，在该方法上就会有一个事务T
   - AccountDao的outMoney方法的事务T1加入到transfer的事务T中
   - AccountDao的inMoney方法的事务T2加入到transfer的事务T中
   - 这样就保证他们在同一个事务中，当业务层中出现异常，整个事务就会回滚，保证数据的准确性。

![image-20240111164316649](https://xiongyuqing-img.oss-cn-qingdao.aliyuncs.com/img/202401111643744.png)

- 事务管理员：发起事务方，在Spring中通常指代业务层开启事务的方法
- 事务协调员：加入事务方，在Spring中通常指代数据层方法，也可以是业务层方法

## 4.3 事务属性

### 事务配置

![image-20240111164629953](https://xiongyuqing-img.oss-cn-qingdao.aliyuncs.com/img/202401111646007.png)

并不是所有的异常都会回滚事务，比如下面的代码就不会回滚：

```java
public interface AccountService {
    /**
     * 转账操作
     * @param out 传出方
     * @param in 转入方
     * @param money 金额
     */
    //配置当前接口方法具有事务
    public void transfer(String out,String in ,Double money) throws IOException;
}

@Service
public class AccountServiceImpl implements AccountService {

    @Autowired
    private AccountDao accountDao;
	@Transactional
    public void transfer(String out,String in ,Double money) throws IOException{
        accountDao.outMoney(out,money);
        //int i = 1/0; //这个异常事务会回滚
        if(true){
            throw new IOException(); //这个异常事务就不会回滚
        }
        accountDao.inMoney(in,money);
    }

}
```

出现这个问题的原因是，Spring的事务只会对`Error异常`和`RuntimeException异常`及其子类进行事务回顾，其他的异常类型是不会回滚的，对应IOException不符合上述条件所以不回滚

- 此时就可以使用rollbackFor属性来设置出现IOException异常不回滚

````java
@Service
public class AccountServiceImpl implements AccountService {

    @Autowired
    private AccountDao accountDao;
	 @Transactional(rollbackFor = {IOException.class})
    public void transfer(String out,String in ,Double money) throws IOException{
        accountDao.outMoney(out,money);
        //int i = 1/0; //这个异常事务会回滚
        if(true){
            throw new IOException(); //这个异常事务就不会回滚
        }
        accountDao.inMoney(in,money);
    }

}
````

isolation设置事务的隔离级别

* DEFAULT   :默认隔离级别, 会采用数据库的隔离级别
* READ_UNCOMMITTED : 读未提交
* READ_COMMITTED : 读已提交
* REPEATABLE_READ : 重复读取
* SERIALIZABLE: 串行化

`propagation属性`：

当一个事务里包含多个事务时，设置新事务是否加入到当前事务

![image-20240111165639622](https://xiongyuqing-img.oss-cn-qingdao.aliyuncs.com/img/202401111656684.png)

事务传播行为：事务协调员对事务管理员所携带事务的处理态度。

![image-20240111165830370](https://xiongyuqing-img.oss-cn-qingdao.aliyuncs.com/img/202401111658425.png)
