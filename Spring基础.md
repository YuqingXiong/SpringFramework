# 1. Spring 基础

## 1.1 什么是Spring框架？它能带来那些好处？

Spring 是一个开源的轻量级的 Java 开发框架，可以帮助开发人员更高效的进行开发，主要优势在于简化开发和框架整合。

Spring框架整合了很多模块，这些模块可以协助我们开发。例如Spring中的两大核心技术：IoC (Inversion of Control:控制反转) 和 AOP (Aspect-Oriented Programming : 面向切面编程)，可以很方便的支持对数据库的访问，并集成第三方组件（例如调度，缓存等等），还支持单元测试。

## 1.2 Spring的组成是什么？包含哪些模块？

Spring 4.x版本：

![image-20231227195147302](https://xiongyuqing-img.oss-cn-qingdao.aliyuncs.com/img/202312271951413.png)

Spring 5.x 版本：

Spring5.x 版本中 Web 模块的 Portlet 组件已经被废弃掉，同时增加了用于异步响应式处理的 WebFlux 组件。

![Spring5.x主要模块](https://xiongyuqing-img.oss-cn-qingdao.aliyuncs.com/img/202312272018897.png)

- Core Container
- AOP
- Data Access/Integration
- Web
- Test
- ...

（每个模块的作用，和技术？）

## 1.3 Spring, Spring MVC, Spring Boot 之间的关系？

首先回答什么是 Spring，Spring MVC，Spring Boot

![在这里插入图片描述](https://xiongyuqing-img.oss-cn-qingdao.aliyuncs.com/img/202312272031012.png)

联系：

Spring MVC 是 Spring 众多模块中的一个重要模块，可以让 Spring 快速构建出一个 MVC 架构的 Web 程序。

MVC 是指模型（Model），视图（View），控制器（Controller）的简写，MVC架构的核心思想是将业务逻辑，数据，显示分离来组织代码，以简化 Web 程序的开发。

（与三层开发模型 Controller，Service，Dao 的关系是？）

![img](https://xiongyuqing-img.oss-cn-qingdao.aliyuncs.com/img/202312272031348.png)

Spring 进行开发的过程中需要用 XML 或者 Java 进行显示的配置，比较繁琐。而 Spring Boot 简化了这些配置，减少了配置文件，做到了开箱即用。

（如何做到简化配置文件的？原理是什么？）

# 2 Spring 核心技术

1. 什么是IoC/DI 思想？

- IoC（控制反转）：对象的创建权交给外部的 IoC 容器
- DI （依赖注入）：绑定对象与对象之间的依赖关系

2. 什么是 IoC 容器？

Spring 创建的用来存放所创建对象的容器

3. 什么是 Bean?

IoC 容器中存放的一个个对象就是 Bean 或者 Bean 对象

## 2.1 Spring IoC & DI

（应该要补充原理的这里）

## 2.2 案例

### IoC 的入门案例

1. 首先创建一个 Maven 工程

2. 在 pom 中添加 spring-context 依赖

3. 创建BookService,BookServiceImpl，BookDao和BookDaoImpl四个类

4. **添加 spring 配置文件：**在 resource 目录下添加 applicationContext.xml 文件 用于配置 bean 信息

   <img src="https://xiongyuqing-img.oss-cn-qingdao.aliyuncs.com/img/202312281442965.png" alt="image-20231228144257809" style="zoom:50%;" />

5. **在配置文件中对 Bean 进行配置：**设置 bean 的名字和类型

   ```xml
   <?xml version="1.0" encoding="UTF-8"?>
   <beans xmlns="http://www.springframework.org/schema/beans"
          xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
          xsi:schemaLocation="http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans.xsd">
   
   <!--    id 为 bean 的名字 class 为 bean 对象所属的类-->
       <bean id="bookDao" class="com.rainsun.Dao.Impl.BookDaoImpl"/>
       <bean id="bookService" class="com.rainsun.Service.Impl.BookServiceImpl"/>
   </beans>
   ```

6. 获取 IoC 容器：把我们配置好的 Bean 文件进行解析，获得一个存放着Bean的容器

   ```java
   public class App2 {
       public static void main(String[] args) {
           // IoC 容器获取：传入Bean的配置文件
           ApplicationContext ctx = new ClassPathXmlApplicationContext("applicationContext.xml");
       }
   }
   ```

7. 从容器中获取 Bean 对象 并进行方法调用：

   ```java
   public class App2 {
       public static void main(String[] args) {
           // IoC 容器获取
           ApplicationContext ctx = new ClassPathXmlApplicationContext("applicationContext.xml");
           // 获取 Bean
   //        BookDao bookDao = (BookDao)ctx.getBean("bookDao");
   //        bookDao.save();
           BookService bookService = (BookService)ctx.getBean("bookService");
           bookService.save();
       }
   }
   ```

问题：这里完成了控制反转，将对象的创建权交给了Spring，也可以获取对象，但是 Service 层中依然存在 Dao 层的 new 对象，这需要依赖注入(DI）来解决

### DI 入门案例

1. 去掉原先 new 创建对象的方式，改为使用 set 方法进行Bean对象赋值

   ```java
   public class BookServiceImpl implements BookService {
       // 去掉 new 的实现方式，改为 DI（依赖注入）
   //    private BookDao bookDao = new BookDaoImpl();
       private BookDao bookDao;
       @Override
       public void save() {
           System.out.println("Book service save");
           bookDao.save();
       }
   
       // 通过调用set方法进行依赖注入
       public void setBookDao(BookDao bookDao) {
           this.bookDao = bookDao;
       }
   }
   ```

2. 修改 Spring 配置文件，为 bookService 对象添加一个依赖

   把 Ioc 容器中的 bookDao 对象，绑定到 bookService 对象的 bookDao 属性上，这个绑定的实现要靠 setter 方法

   ```xml
   <?xml version="1.0" encoding="UTF-8"?>
   <beans xmlns="http://www.springframework.org/schema/beans"
          xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
          xsi:schemaLocation="http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans.xsd">
   
   <!--    id 为 bean 的名字 class 为 bean 对象所属的类-->
       <bean id="bookDao" class="com.rainsun.Dao.Impl.BookDaoImpl"/>
       <bean id="bookService" class="com.rainsun.Service.Impl.BookServiceImpl">
   <!--        name 指 BookServiceImpl 中的 bookDao 成员变量
               ref 指 IoC 容器存放的 bookDao Bean对象 -->
           <property name="bookDao" ref="bookDao"/>
       </bean>
   </beans>
   ```

   ![image-20231228152133121](https://xiongyuqing-img.oss-cn-qingdao.aliyuncs.com/img/202312281521193.png)

# 3 IoC相关 （Bean 的创建）

## 3.1 Bean 配置相关

**基础配置 id, class, name**

```xml
<bean id="" name="xxx xx xxx" class=""></bean>
```

- id 表示 Bean 的名字
- class 是bean的类型，也就是属于哪个实现类的
- name 是 bean 的别名，可以有多个，用逗号（，）分号（；）空格（ ）进行分隔。通过别名也可以获取 Bean 对象

**作用范围 scope**

```xml
<bean id="" name="xxx xx xxx" class="" scope="singleton/prototype"></bean>
```

可选范围：

- singleton：默认为单例模式，获取的同一名字的 bean 对象为同一个对象（地址相同）
- prototype：非单例

思考：

1. Bean 为什么默认为单例的？
   - 单例模式下 IoC 容器中只有该类的一个对象
   - 便于对象的复用，避免了对象的频繁创建和销毁
2. 哪些 Bean 对象适合交给容器管理？
   - 表现层对象
   - 业务层对象
   - 数据层对象
   - 工具对象
   - 它们的对象只创建一次就够了，后面可以反复使用
3. 哪些 Bean 对象不适合交给容器进行管理？
   - **封装实例的域对象**（什么意思呢？），记录了一些属性值，状态在不断的变化的对象。
   - 会引发线程安全问题，所以不适合

## 3.2 Bean 实例化

我们将对象交给 Spring 的 IoC 容器进行创建了，但是容器是如何创建的呢？

**构造方法实例化**

我们为 BookDaoImpl 添加一个 public 的构造方法：

```java
public class BookDaoImpl implements BookDao {
    public BookDaoImpl() {
        System.out.println("book dao constructor is running");
    }
    @Override
    public void save() {
        System.out.println("Book dao save");
    }
}
```

然后获取 bookDao 对象，调用save 方法，可以发现构造方法被调用了：

```java
book dao constructor is running
Book service save
Book dao save
```

如果将构造方法改为私有的 private，构造方法依然被调用：

```java
private BookDaoImpl() {
    System.out.println("book dao constructor is running");
}
```

说明无论构造函数是公开还是私有的，spring底层都能通过构造函数创建出一个bean对象。也就是说Spring底层是利用反射实现的。

如果我们在无参构造函数中添加一个参数，Spring就会报错：没有默认构造函数

进一步说明Spring底层是使用类的无参构造方法

**静态工厂实例化**

首先环境准备：（创建一个工程提供实现类的一个实例）

实现的接口：

```java
package com.rainsun.Dao;
public interface OrderDao {
    public void save();
}
```

实现类：

```java
package com.rainsun.Dao.Impl;
import com.rainsun.Dao.OrderDao;
public class OrderDaoImpl implements OrderDao {
    @Override
    public void save() {
        System.out.println("order dao save ...");
    }
}
```

工厂类提供静态方法，返回一个实例

```java
package com.rainsun.factory;
import com.rainsun.Dao.Impl.OrderDaoImpl;
import com.rainsun.Dao.OrderDao;
public class OrderDaoFactory {
    public static OrderDao getOrderDao(){
        return new OrderDaoImpl();
    }
}
```

从工厂中获取对象：

```java
package com.rainsun;
import com.rainsun.Dao.OrderDao;
import com.rainsun.factory.OrderDaoFactory;
public class AppForInstanceOrder {
    public static void main(String[] args) {
        OrderDao orderDao = OrderDaoFactory.getOrderDao();
        orderDao.save();
    }
}
```

如何将getOrderDao中创建的对象交给Spring管理呢？

这需要用到 Spring 中静态工厂实例化的知识：

在配置文件中添加 工厂方法的全类名(class) 和 工厂方法（factory-method) getOrderDao

```xml
<bean id="orderDao" class="com.rainsun.factory.OrderDaoFactory" factory-method="getOrderDao" />
```

![image-20231228162022562](https://xiongyuqing-img.oss-cn-qingdao.aliyuncs.com/img/202312281620627.png)

就可以把 getOrderDao 创建的 orderDao对象加入到容器，我们也可以通过 orderDao的名字获取到对应的bean，然后调用对应的方法了

**实例工厂实例化**

相比与静态工厂，实例工厂需要创建工厂的实例才能创建对象。也就是方法没有了 static 修饰：

```java
package com.rainsun.factory;
import com.rainsun.Dao.Impl.OrderDaoImpl;
import com.rainsun.Dao.OrderDao;
public class OrderDaoFactory {
    public OrderDao getOrderDao(){
        return new OrderDaoImpl();
    }
}
```

获取对象，需要创建工厂实例，再调用方法：

```java
public class AppForInstanceOrder {
    public static void main(String[] args) {
        //创建实例工厂对象
        OrderDaoFactory orderDaoFactory = new OrderDaoFactory();
        //创建实例工厂对象
        OrderDao orderDao = orderDaoFactory.getOrderDao();
        orderDao.save();
    }
}
```

如何将实例工厂创建的对象交给 Spring 管理呢？

我们也需要创建一个工厂的实例对象，然后用 factoryBean 指向该对象，再指明调用的工厂方法：

```xml
<bean id="orderDaoFactory" class="com.rainsun.factory.OrderDaoFactory"/>
    <bean id="orderDao" factory-method="getOrderDao" factory-bean="orderDaoFactory"/>
```

- 首先实例化工厂对象

- 调用对象中的方法创建bean

  - factory-bean：工厂的实例对象

  - factory-method：工厂中具体创建对象的方法名称

    ![image-20231228163103914](https://xiongyuqing-img.oss-cn-qingdao.aliyuncs.com/img/202312281631961.png)

**FactoryBean的使用**

实际上创建的工厂 bean 只是为了配合后面创建对象bean的使用，且每次创建对象bean的时候都需要指定 工厂bean对象。比较麻烦，Spring 提供了一个 FactoryBean接口的方式简化开发：

实例工厂实现 FactoryBean 接口，重写接口中的方法：

```java
public class OrderDaoFactoryBean implements FactoryBean<OrderDao> {
    @Override // 代替原始工厂中创建对象的方法
    public OrderDao getObject() throws Exception {
        return new OrderDaoImpl();
    }
    @Override
    public Class<?> getObjectType() {
        return OrderDao.class;
    }
}
```

Spring的bean配置中就可以只写一行配置，指明id和class就行：

```xml
<bean id="orderDao" class="com.rainsun.factory.OrderDaoFactoryBean"/>
```

继承的 FactoryBean接口中有三个方法：

```java
T getObject() throws Exception;

Class<?> getObjectType();

default boolean isSingleton() {
		return true;
}
```

- getObject ：重写返回创建的对象
- getObjectType：重写返回被创建对象的Class对象
- isSingleton：有默认值，为单例模式。如果重写返回 false 就是非单例的

## 3.3 Bean 的生命周期

Bean的生命周期是指 bean 对象从创建到销毁的整个过程。

我们主要关注 Bean 的生命周期控制，控制 bean 创建后和销毁前做一些操作。

如何将这些控制操作添加进去呢？

通过在配置文件中指定初始化方法和销毁方法的方式实现

**生命周期设置**

生命周期的控制分为两个阶段：

- bean 创建之后，添加一些操作内容，例如初始化用到的资源
- bean 销毁之前，添加一些操作内容，例如释放用到的资源

1. 添加初始化方法和销毁方法：

```java
import com.rainsun.Dao.BookDao;

public class BookDaoImpl implements BookDao {
    private BookDaoImpl() {
        System.out.println("book dao constructor is running");
    }
    @Override
    public void save() {
        System.out.println("Book dao save");
    }
    // bean 初始化对应的操作
    public void init(){
        System.out.println("init ...");
    }
    // bean销毁前的操作
    public void destroy(){
        System.out.println("destroy ...");
    }
}
```

2. 配置文件中添加生命周期控制方法

```xml
 <bean id="bookDao" class="com.rainsun.Dao.Impl.BookDaoImpl" init-method="init" destroy-method="destroy"/>
```

但是运行程序后，init 方法执行了，但是 destroy 方法是没有被执行的：

<img src="https://xiongyuqing-img.oss-cn-qingdao.aliyuncs.com/img/202312282036237.png" alt="image-20231228203633061" style="zoom:50%;" />

因为 bean 对象是交给 IoC 容器的，JVM退出后，IoC 容器没有被关闭， bean 对象还没还得及销毁，程序已经结束了。

所以我们需要关闭 IoC 容器才能销毁 bean 对象

**close 关闭IoC容器**

- ApplicationContext 接口中没有 close 方法，但是其下一个接口 ClassPathXmlApplicationContext 中有 close 方法

- 改变 IoC 容器的类型，调用 close 方法：

  ```java
  ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext("applicationContext.xml");
  ctx.close();
  ```

**注册钩子关闭IoC容器**

close 关闭容器的方法比较暴力，我们可以提前在容器没关闭前设置一个回调函数，让JVM在退出的时候回调这个函数完成容器的关闭

回调函数的设置（钩子方法）：

```java
ctx.registerShutdownHook();
```

上面控制bean生命周期的方式比较繁琐，不仅需要编写对应的控制方法，还需要编写配置文件。

Spring为了简化生命周期的控制，提供了两个接口 `InitializingBean` 和 `DisposableBean` ，重写其中的 `afterProperiesSet` 和 `destroy` 方法：

```java
package com.rainsun.Service.Impl;

import com.rainsun.Dao.BookDao;
import com.rainsun.Dao.Impl.BookDaoImpl;
import com.rainsun.Service.BookService;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

public class BookServiceImpl implements BookService, InitializingBean, DisposableBean {
    // 去掉 new 的实现方式，改为 DI（依赖注入）
//    private BookDao bookDao = new BookDaoImpl();
    private BookDao bookDao;
    @Override
    public void save() {
        System.out.println("Book service save");
        bookDao.save();
    }
    // 通过调用set方法进行依赖注入
    public void setBookDao(BookDao bookDao) {
        this.bookDao = bookDao;
    }

    @Override // 属性设置之后，也就是属性注入bean对象之后，执行
    public void afterPropertiesSet() throws Exception {
        System.out.println("service init");
    }

    @Override
    public void destroy() throws Exception {
        System.out.println("service destroy");
    }
}
```

小结：

1. bean 生命周期控制的方法：

   1. 编写对象创建后和销毁前方法，并在配置文件中为对应bean对象的标签中添加 `init-method` 和 `destroy-method` 属性
   2. 实现 `InitializingBean` 和 `DisposableBean` 接口，重写其中的 `afterPropertiesSet` 和 `destroy` 方法

2. 生命周期控制在Bean整个生命周期的位置？

   1. 初始化容器

      1. 创建对象（内存分配）
      2. 执行构造方法
      3. 执行属性注入（set操作）
      4. **执行bean的初始化方法**

   2. 使用bean

      执行业务操作

   3. 关闭/销毁容器

      1. 执行 bean的销毁方法

3. 关闭容器的方法：

   调用ConfigurableApplicationContext接口中的方法，它是ApplicationContext接口的子类

   - 调用 close() 方法
   - 调用回调函数：registerShutdownHook() 方法

# 4 DI 相关

依赖注入描述了容器中建立bean与bean之间的依赖关系的过程，bean的运行需要的数据类型有两类：引用类型，简单类型（基本数据类型与String）

向类中传递数据有两种方法：普通方法（set方法），构造方法

所以我们可以使用 setter 或者构造器完成简单类型和引用类型的bean注入

## 4.1 setter 注入

1. **setter 注入引用类型：**

- 在类中定义引用类型属性

- 提供可访问的set方法

- 在 property 标签的 ref 属性中引用该引用类型的 bean

  ```xml
  <property name="" ref=""/>
  ```

2. **setter 注入简单类型**

- 在类中定义简单类型属性

- 提供可访问的set方法

- 在 property 标签的 value 属性中注入具体的值

  ```java
  public class BookDaoImpl implements BookDao{
      private String databaseName;
      private int connectionNum;
      
      public void setDatabaseName(String databaseName) {
          this.databaseName = databaseName;
      }
  
      public void setConnectionNum(int connectionNum) {
          this.connectionNum = connectionNum;
      }
  }
  ```

  Spring 在注入时会自动转换为对应的参数类型

  ```xml
  <bean id="bookDao" class="com.rainsun.Dao.Impl.BookDaoImpl" init-method="init" destroy-method="destroy">
      <property name="databaseName" value="mysql"/>
      <property name="connectionNum" value="10"/>
  </bean>
  ```

## 4.2 构造器注入

1. **构造器注入引用类型：**

- 在类中定义引用类型属性

- 提供构造方法

- 在 constructor-arg 标签的 ref 标签引用注入的 bean

  ```java
  public class BookServiceImpl implements BookService {
      private BookDao bookDao; // 1
      public BookServiceImpl(BookDao bookDao) { // 2
          this.bookDao = bookDao;
      }
  }
  ```

  ```xml
  <bean id="bookService" class="com.rainsun.Service.Impl.BookServiceImpl">
      <constructor-arg name="bookDao" ref="bookDao"/>
  </bean>
  ```

2. **构造器注入简单类型**

同样的定义简单类型属性，提供构造方法，不同的是这里用 value 属性注入数据

```xml
<constructor-arg name="databaseName" value="mysql"/>
<constructor-arg name="connectionNum" value="10"/>
```

3. **构造器注入多个数据的耦合问题**

对应关系耦合：构造函数形参与 标签中的 name 属性对应

![image-20231229102157908](https://xiongyuqing-img.oss-cn-qingdao.aliyuncs.com/img/202312291022993.png)

解决耦合的方法：

- 方式一：按照类型注入，使用 type 属性指明类型：

  ```xml
  <constructor-arg type="java.lang.String" value="mysql"/>
  <constructor-arg type="int" value="10"/>
  ```

  如果存在类型相同的参数，这种注入方法就存在问题

- 方式二：按照索引下标注入，下标从0开始：

  ```xml
  <constructor-arg index="0" value="mysql"/>
  <constructor-arg index="1" value="10"/>
  ```

  解决了参数类型重复的问题，但是如果参数顺序发生了变化，就带来了新的耦合问题

4. **依赖注入的方式选择**

- 强制依赖使用构造器进行注入。强制依赖是指对象创建过程中必须注入的依赖参数对象，而setter注入可能会导致注入null对象
- 可选依赖使用setter注入。可选依赖指对象创建过程中注入的依赖参数对象可有可无
- Spring提倡使用构造器，第三方框架大多使用构造器注入，相对严谨
- 我们自己开发用 setter 注入比较简单

## 4.3 自动配置

上面配置bean依赖的过程很繁琐，需要编写配置文件，Spring提供了自动配置的方式。

**依赖自动装配：**  IoC 容器根据 bean 所依赖的资源自动在容器中自动查找并注入到bean的过程称为自动装配

**自动装配的方式：**

- **按类型注入** ：byType
- 按名称 ：byName
- 按构造方法
- 不启用自动装配

![image-20231229104747657](https://xiongyuqing-img.oss-cn-qingdao.aliyuncs.com/img/202312291047736.png)

**实现方式：**

- 类中定义属性

- 编写set方法

- bean标签中添加autowire属性，填写自动注入的方式

  ```java
  public class BookServiceImpl implements BookService {
      private BookDao bookDao;
  
      public void setBookDao(BookDao bookDao) {
          this.bookDao = bookDao;
      }
  }
  ```

  ```xml
  <bean id="bookService" class="com.rainsun.Service.Impl.BookServiceImpl" autowire="byType"/>
  ```

注意：

- 注入属性对应的 setter 方法不能省略
- 该属性对应的 bean 对象必须存在在 IoC 容器中被管理
- 如果存在同一类型bean对象，则会报错：`NoUniqueBeanDefinitionException`
- 此时可以按照名称注入，使用属性 `byName`，该名称是指setter方法去掉set并小写首字母的属性名

自动装配的特征：

- 自动装配只能用于引用类型，不能用于简单类型
- byType 需要保证容器中同类型的bean唯一
- byName必须保证容器中存在该名称的bean，否则注入为 null，这里存在耦合
- 自动装配的优先级低于setter注入和构造器注入，同时出现时自动装配会失效

## 4.4 集合注入

需要注入的数据类型：

```java
public class BookDaoImpl implements BookDao{
    private int[] myArray;
    private List<String> myList;
    private Set<String> mySet;
    private Map<String, String> myMap;
    private Properties myProperties;

    public void setMyArray(int[] myArray) {
        this.myArray = myArray;
    }

    public void setMyList(List<String> myList) {
        this.myList = myList;
    }

    public void setMySet(Set<String> mySet) {
        this.mySet = mySet;
    }

    public void setMyMap(Map<String, String> myMap) {
        this.myMap = myMap;
    }

    public void setMyProperties(Properties myProperties) {
        this.myProperties = myProperties;
    }

    @Override
    public void save() {
        System.out.println("Book dao save");

        System.out.println("遍历数组：" + Arrays.toString(myArray));
        System.out.println("遍历List: " + myList);
        System.out.println("遍历set: " + mySet);
        System.out.println("遍历map: " + myMap);
        System.out.println("遍历properties: " + myProperties);
    }
}
```

配置文件编写：

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xsi:schemaLocation="http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans.xsd">
    <bean id="bookDao" class="com.rainsun.Dao.Impl.BookDaoImpl">
        <property name="myArray">
            <array>
                <value>100</value>
                <value>200</value>
                <value>300</value>
            </array>
        </property>
        <property name="myList">
            <list>
                <value>rainsun</value>
                <value>xiongyuqing</value>
                <value>sdu</value>
            </list>
        </property>
        <property name="mySet">
            <set>
                <value>rainsun</value>
                <value>xiongyuqing</value>
                <value>sdu</value>
            </set>
        </property>
        <property name="myMap">
            <map>
                <entry key="name" value="xyq"/>
                <entry key="age" value="23"/>
                <entry key="city" value="qingdao"/>
            </map>
        </property>
        <property name="myProperties">
            <props>
                <prop key="name">xyq</prop>
                <prop key="age">23</prop>
                <prop key="city">qingdao</prop>
            </props>
        </property>
    </bean>

    <bean id="bookService" class="com.rainsun.Service.Impl.BookServiceImpl" autowire="byType"/>
</beans>
```

运行：

```java
public class App2 {
    public static void main(String[] args) {
        ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext("applicationContext.xml");
        BookDao bookDao = (BookDao)ctx.getBean("bookDao");
        bookDao.save();
    }
}
```

```java
Book dao save
遍历数组：[100, 200, 300]
遍历List: [rainsun, xiongyuqing, sdu]
遍历set: [rainsun, xiongyuqing, sdu]
遍历map: {name=xyq, age=23, city=qingdao}
遍历properties: {city=qingdao, name=xyq, age=23}
```

* List的底层也是通过数组实现的，所以`<list>`和`<array>`标签是可以混用
* 集合中要添加引用类型，只需要把`<value>`标签改成`<ref>`标签，这种方式用的比较少

