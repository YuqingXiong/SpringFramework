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

