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

## 2.1 Spring IoC

（应该要补充原理的这里）

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

   把 Ioc 容器中的 bookDao 对象，绑定到 bookService 对象的 bookDao 属性上

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

   

