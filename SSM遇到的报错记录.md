# 前提

建立在 JDK21上：

```xml
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/maven-v4_0_0.xsd">
    <modelVersion>4.0.0</modelVersion>
    <parent>
        <groupId>com.rainsun</groupId>
        <artifactId>SpringFramework</artifactId>
        <version>1.0-SNAPSHOT</version>
    </parent>
    <artifactId>springmvc_04_ssm</artifactId>
    <packaging>war</packaging>
    <name>springmvc_04_ssm Maven Webapp</name>
    <url>http://maven.apache.org</url>
    <dependencies>
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
            <groupId>com.alibaba</groupId>
            <artifactId>druid</artifactId>
            <version>1.2.15</version>
        </dependency>

        <dependency>
            <groupId>org.mybatis</groupId>
            <artifactId>mybatis</artifactId>
            <version>3.5.9</version>
        </dependency>

<!--        这里 mybatis-spring 是 2.0.7 版本, 如果是 3.0.x 会报 aot 的错-->
        <dependency>
            <groupId>org.mybatis</groupId>
            <artifactId>mybatis-spring</artifactId>
            <version>2.0.7</version>
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
            <groupId>com.fasterxml.jackson.core</groupId>
            <artifactId>jackson-databind</artifactId>
            <version>2.15.3</version>
        </dependency>

<!--        spring-test 和 springmvc的版本要相同 -->
<!--        驳回上面这句，不相同好像也行-->
        <dependency>
            <groupId>org.springframework</groupId>
            <artifactId>spring-test</artifactId>
            <version>6.1.2</version>
<!--            <version>5.3.0</version>-->
        </dependency>

        <dependency>
            <groupId>junit</groupId>
            <artifactId>junit</artifactId>
            <version>4.13.2</version>
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
                    <uriEncoding>UTF-8</uriEncoding>
                </configuration>
            </plugin>
        </plugins>
    </build>
</project>
```

# tomcat 报错：A child container failed during start

![image-20240113204807370](https://xiongyuqing-img.oss-cn-qingdao.aliyuncs.com/img/202401132048430.png)

## 1.mybatis-spring 版本太高

3.0.x 版本的 mybatis-spring 会出现上面的错误

```xml
<dependency>
    <groupId>org.mybatis</groupId>
    <artifactId>mybatis-spring</artifactId>
    <version>3.0.1</version>
</dependency>
```

### 解决方案

下调成 2.0.7:

```xml
<dependency>
    <groupId>org.mybatis</groupId>
    <artifactId>mybatis-spring</artifactId>
    <version>2.0.7</version>
</dependency>
```

## 2.注解没加 classpath 来引入属性文件

`@PropertySource("jdbc.properties")` 这样写也会报上述错误

```java
@Configuration
@ComponentScan({"com.rainsun.service"})
@PropertySource("jdbc.properties")
@Import({JdbcConfig.class, MyBatisConfig.class})
public class SpringConfig {
}
```

### 解决方案

加上 `classpath:` ，这样spring就能找到这个文件在哪了

```java
@Configuration
@ComponentScan({"com.rainsun.service"})
@PropertySource("classpath:jdbc.properties")
@Import({JdbcConfig.class, MyBatisConfig.class})
public class SpringConfig {
}
```

## 3.路径前没加斜杠 “/”，或者 pom 里 jar 包版本问题

> 路径前没加斜杆“/”：[maven使用tomcat启动报错A child container failed during start_maven-CSDN博客](https://blog.csdn.net/weixin_51405802/article/details/124607812)
>
> pom.xml里 servlet-api 没写  `<scope>provided</scope>`[Tomcat启动报A child container failed during start问题解决-CSDN博客](https://blog.csdn.net/qq_26599807/article/details/107694063)
>
> [A child container failed during start-阿里云开发者社区 (aliyun.com)](https://developer.aliyun.com/article/342754)

