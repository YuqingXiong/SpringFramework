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

## 2.1 Spring IoC

1. IoC 案例



