# 1 分模块开发

将原始模块按照功能拆分成若干个子模块，方便模块间的相互调用，接口共享：

![image-20240115201902128](https://xiongyuqing-img.oss-cn-qingdao.aliyuncs.com/img/202401152019207.png)

环境准备：maven_01_ssm

## 抽取domain层

1. 创建一个新Module：maven_02_pojo

**注意模块的 parent 选择要时 None**

![image-20240115202113126](https://xiongyuqing-img.oss-cn-qingdao.aliyuncs.com/img/202401152021168.png)

2. 将domain中的代码拷贝到新Module的java目录下：

![image-20240115202227757](https://xiongyuqing-img.oss-cn-qingdao.aliyuncs.com/img/202401152022779.png)

3. 删除原项目中的domain包，此时项目中用到`Book`的类中都会有红色提示

4. 建立依赖关系: maven_01_ssm 项目pom.xml 导入 maven_02_pojo 的坐标：

```xml
<dependency>
    <groupId>com.rainsun</groupId>
    <artifactId>maven_02_pojo</artifactId>
    <version>1.0-SNAPSHOT</version>
</dependency>
```

因为添加了依赖，所以在`maven_01_ssm`中就已经能找到Book类，所以刚才的报红提示就会消失。

5. 将项目安装本地仓库

> 直接编译 `maven_01_ssm` 会报错，因为找不到 `maven_02_pojo` 这个jar包。
>
> 因为Maven会从本地仓库找对应的jar包，但是本地仓库又不存在该jar包所以会报错。所以需要将`maven_02_pojo`项目安装到本地仓库

使用maven的install命令，把其安装到Maven的本地仓库中。

![image-20240115203153206](https://xiongyuqing-img.oss-cn-qingdao.aliyuncs.com/img/202401152031255.png)

![image-20240115203229724](https://xiongyuqing-img.oss-cn-qingdao.aliyuncs.com/img/202401152032751.png)

执行`maven_01_ssm`的compile的命令后，就已经能够成功编译：

![image-20240115203308876](https://xiongyuqing-img.oss-cn-qingdao.aliyuncs.com/img/202401152033965.png)

## 抽取Dao层

1. 创建一个新模块maven_03_dao
2. 拷贝01中的dao包到新项目的dao下，删除原项目的dao包
3. maven_03_dao 的pom中引入Mybatis操作需要的包
4. maven_03_dao 依赖 maven_02_pojo，在pom文件中引入
5. maven_01_ssm 依赖 maven_03_dao，在pom文件中引入
6. 执行maven_03_dao 项目的 maven的 install 命令，将项目 `maven_03_dao` 到本地仓库
7. 执行 maven_01_ssm 项目的 maven的 compile 命令进行编译，测试是否成功

# 2 依赖管理

依赖指当前项目运行所需的jar，一个项目可以设置多个依赖

当在其他项目中想要使用独立出来的这些模块，只需要在其pom.xml使用`<dependency>` 标签来进行jar包的引入即可

## 依赖传递与冲突问题

刚才的项目案例中，打开Maven的面板，会发现：

展开具有箭头的 jar 包会发现，jar包下面还包含有其他的jar包

![image-20240115205651567](https://xiongyuqing-img.oss-cn-qingdao.aliyuncs.com/img/202401152056631.png)

依赖传递：

- 由于 maven_03_dao 依赖 maven_02_pojo，所以 maven_02_pojo 被加载到了 maven_01_ssm 项目中了

- 但是 maven_02_pojo 之前已经被加载过了
- 删除 maven_02_pojo 依赖，只加载 maven_03_dao 后，会发现 maven_02_pojo 中的数据依然可以被使用

![image-20240115210033023](https://xiongyuqing-img.oss-cn-qingdao.aliyuncs.com/img/202401152100093.png)

**说明:**A代表自己的项目；B,C,D,E,F,G代表的是项目所依赖的jar包；D1和D2 E1和E2代表是相同jar包的不同版本

(1) A依赖了B和C,B和C有分别依赖了其他jar包，所以在A项目中就可以使用上面所有jar包，这就是所说的依赖传递

(2) 依赖传递有直接依赖和间接依赖

* 相对于A来说，A直接依赖B和C,间接依赖了D1,E1,G，F,D2和E2
* 相对于B来说，B直接依赖了D1和E1,间接依赖了G
* 直接依赖和间接依赖是一个相对的概念

(3)因为有依赖传递的存在，就会导致jar包在依赖的过程中出现冲突问题，具体什么是冲突?Maven是如何解决冲突的?

这里所说的==依赖冲突==是指项目依赖的某一个jar包，有多个不同的版本，因而造成类包版本冲突。

情况一: 在`maven_02_ssm`的pom.xml中添加两个不同版本的Junit依赖:

```xml
<dependencies>
    <dependency>
      <groupId>junit</groupId>
      <artifactId>junit</artifactId>
      <version>4.12</version>
      <scope>test</scope>
    </dependency>

    <dependency>
      <groupId>junit</groupId>
      <artifactId>junit</artifactId>
      <version>4.11</version>
      <scope>test</scope>
    </dependency>
</dependencies>
```

通过对比，会发现一个结论

* 特殊优先：当同级配置了相同资源的不同版本，后配置的覆盖先配置的。

情况二: 路径优先：当依赖中出现相同的资源时，层级越深，优先级越低，层级越浅，优先级越高

* A通过B间接依赖到E1
* A通过C间接依赖到E2
* A就会间接依赖到E1和E2,Maven会按照层级来选择，E1是2度，E2是3度，所以最终会选择E1

情况三: 声明优先：当资源在相同层级被依赖时，配置顺序靠前的覆盖配置顺序靠后的

* A通过B间接依赖到D1
* A通过C间接依赖到D2
* D1和D2都是两度，这个时候就不能按照层级来选择，需要按照声明来，谁先声明用谁，也就是说B在C之前声明，这个时候使用的是D1，反之则为D2

但是对应上面这些结果，大家不需要刻意去记它。因为不管Maven怎么选，最终的结果都会在Maven的`Dependencies`面板中展示出来，展示的是哪个版本，也就是说它选择的就是哪个版本，

如果想更全面的查看Maven中各个坐标的依赖关系，可以点击Maven面板中的`show Dependencies`

## 可选依赖和排除依赖

需求：不想让当前依赖一个jar包的工程，去使用jar包自己的依赖

例如：01_ssm依赖_03_dao，03_dao依赖02_pojo，于是由于依赖传递01_ssm就可以直接使用02_pojo了。但是现在不想让 01_ssm 使用 02_pojo。

方案一:可选依赖

- 可选依赖指对外隐藏当前所依赖的资源---不透明
- optional 标签

```xml
<dependency>
    <groupId>com.rainsun</groupId>
    <artifactId>maven_02_pojo</artifactId>
    <version>1.0-SNAPSHOT</version>
    <!--可选依赖是隐藏当前工程所依赖的资源，隐藏后对应资源将不具有依赖传递-->
    <optional>true</optional>
</dependency>
```

方案二:排除依赖

- 排除依赖指主动断开依赖的资源，被排除的资源无需指定版本---不需要

- exclusions 标签，且不指定版本

```xml
<dependency>
    <groupId>com.itheima</groupId>
    <artifactId>maven_04_dao</artifactId>
    <version>1.0-SNAPSHOT</version>
    <!--排除依赖是隐藏当前资源对应的依赖关系-->
    <exclusions>
        <exclusion>
            <groupId>com.rainsun</groupId>
            <artifactId>maven_02_pojo</artifactId>
        </exclusion>
    </exclusions>
</dependency>
```

两种方式：

* `A依赖B,B依赖C`,`C`通过依赖传递会被`A`使用到，现在要想办法让`A`不去依赖`C`
* 可选依赖是在B上设置`<optional>`,`A`不知道有`C`的存在，
* 排除依赖是在A上设置`<exclusions>`,`A`知道有`C`的存在，主动将其排除掉。

# 3 聚合与继承

## 聚合

![image-20240115211446732](https://xiongyuqing-img.oss-cn-qingdao.aliyuncs.com/img/202401152114786.png)

问题：

- 当ssm_pojo发生变化后，为了确保我们对ssm_pojo的修改不会影响到其他项目模块，我们需要对所有的模块进行重新编译

解决：

- 能抽取一个项目，把所有的项目管理起来，以后我们要想操作这些项目，只需要操作这一个项目

  ![image-20240115212318176](https://xiongyuqing-img.oss-cn-qingdao.aliyuncs.com/img/202401152123225.png)

聚合：

* 所谓聚合:将多个模块组织成一个整体，同时进行项目构建的过程称为聚合
* 聚合工程：通常是一个不具有业务功能的"空"工程（有且仅有一个pom文件）
* 作用：使用聚合工程可以将多个工程编组，通过对聚合工程进行构建，实现对所包含的模块进行同步构建
  * 当工程中某个模块发生更新（变更）时，必须保障工程中与已更新模块关联的模块同步更新，此时可以使用聚合工程来解决批量模块同步构建的问题。

实现步骤：

1. 创建一个空项目 maven_00_parent

2. 将项目的打包方式改为pom

   项目的打包方式，我们接触到的有三种，分别是

   * jar:默认情况，说明该项目为java项目
   * war:说明该项目为web项目
   * pom:说明该项目为聚合或继承(后面会讲)项目

3. pom.xml添加所要管理的项目

   ```xml
   <project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
     xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
     <modelVersion>4.0.0</modelVersion>
   
     <groupId>com.rainsun</groupId>
     <artifactId>maven_00_parent</artifactId>
     <version>1.0-SNAPSHOT</version>
     <packaging>pom</packaging>
   
     <name>maven_00_parent</name>
     <url>http://maven.apache.org</url>
   
     <properties>
       <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
     </properties>
   
     <modules>
       <module>../maven_01_ssm</module>
       <module>../maven_02_pojo</module>
       <module>../maven_03_dao</module>
     </modules>
   
     <dependencies>
       <dependency>
         <groupId>junit</groupId>
         <artifactId>junit</artifactId>
         <version>3.8.1</version>
         <scope>test</scope>
       </dependency>
     </dependencies>
   </project>
   ```

4. 使用聚合统一管理项目:测试发现，当`maven_00_parent`的`compile`被点击后，所有被其管理的项目都会被执行编译操作。这就是聚合工程的作用。

   ![image-20240115212602851](https://xiongyuqing-img.oss-cn-qingdao.aliyuncs.com/img/202401152126886.png)

**说明：**聚合工程管理的项目在进行运行的时候，会按照项目与项目之间的依赖关系来自动决定执行的顺序和配置的顺序无关。

## 继承

多模块开发存在重复配置问题，例如下面的三个模块，有部分配置相同：

![image-20240116100937375](https://xiongyuqing-img.oss-cn-qingdao.aliyuncs.com/img/202401161009442.png)

子工程继承父工程中的配置信息，可以减少重复配置，统一管理版本

* 所谓继承:描述的是两个工程间的关系，与java中的继承相似，子工程可以继承父工程中的配置信息，常见于依赖关系的继承。
* 作用：
  - 简化配置
  - 减少版本冲突

实现方式：

1. 创建父工程，pom打包形式

2. 子工程中设置引用的父工程：

   ```xml
   <!--配置当前工程继承自parent工程-->
   <parent>
       <groupId>com.rainsun</groupId>
       <artifactId>maven_00_parent</artifactId>
       <version>1.0-RELEASE</version>
       <!--设置父项目pom.xml位置路径-->
       <relativePath>../maven_00_parent/pom.xml</relativePath>
   </parent>
   ```

3. 优化子项目共有依赖导入问题：将子项目共同使用的jar包都抽取出来，维护在父项目的pom.xml中

4. 删除子项目中已经被抽取到父项目的pom.xml中的jar包

5. 优化子项目依赖版本问题

   - 如果把所有用到的jar包都管理在父项目的pom.xml，看上去更简单些，但是这样就会导致有很多项目引入了过多自己不需要的jar包

   - 针对于这种部分项目有的jar包，可以在父工程mavne_00_parent的pom.xml来定义依赖管理：

   - ```xml
     <!--定义依赖管理-->
     <dependencyManagement>
         <dependencies>
             <dependency>
                 <groupId>junit</groupId>
                 <artifactId>junit</artifactId>
                 <version>4.12</version>
                 <scope>test</scope>
             </dependency>
         </dependencies>
     </dependencyManagement>
     ```

   - `<dependencyManagement>`标签不真正引入jar包，而是配置可供子项目选择的jar包依赖

6. 在maven_01_ssm的pom.xml添加junit的依赖:

   ```xml
   <dependency>
       <groupId>junit</groupId>
       <artifactId>junit</artifactId>
       <scope>test</scope>
   </dependency>
   ```

   **注意：这里就不需要添加版本了，这样做的好处就是当父工程dependencyManagement标签中的版本发生变化后，子项目中的依赖版本也会跟着发生变化**

### 聚合与继承的区别

两种之间的作用:

* 聚合用于快速构建项目，对项目进行管理
* 继承用于快速配置和管理子项目中所使用jar包的版本

聚合和继承的相同点:

* 聚合与继承的pom.xml文件打包方式均为pom，可以将两种关系制作到同一个pom文件中
* 聚合与继承均属于设计型模块，并无实际的模块内容

聚合和继承的不同点:

* 聚合是在当前模块中配置关系，聚合可以感知到参与聚合的模块有哪些
* 继承是在子模块中配置关系，父模块无法感知哪些子模块继承了自己

# 属性

## jar 包加载属性

多个 jar 包的版本相同，需要统一更改

定义一个属性变量，设置版本时引用该变量；以后变量更改值得时候，版本也一起变更了

实现方式：

1. 父工程定义属性：

```xml
<properties>
<project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
<spring.version>5.3.29</spring.version>
<junit.version>4.13.2</junit.version>
<mybatis-spring.version>2.0.7</mybatis-spring.version>
</properties>
```

2. 修改依赖的version

```xml
<dependency>
    <groupId>org.springframework</groupId>
    <artifactId>spring-core</artifactId>
    <version>${spring.version}</version>
</dependency>
<dependency>
    <groupId>org.springframework</groupId>
    <artifactId>spring-webmvc</artifactId>
    <version>${spring.version}</version>
</dependency>
<dependency>
    <groupId>org.springframework</groupId>
    <artifactId>spring-jdbc</artifactId>
    <version>${spring.version}</version>
</dependency>
```

## 配置文件加载属性

1:父工程定义属性

```xml
<properties>
   <jdbc.url>jdbc:mysql://127.1.1.1:3306/ssm_db</jdbc.url>
</properties>
```

2:jdbc.properties文件中引用属性

在jdbc.properties，将jdbc.url的值直接获取Maven配置的属性

```properties
jdbc.driver=com.mysql.jdbc.Driver
jdbc.url=${jdbc.url}
jdbc.username=root
jdbc.password=1234
```

3:设置maven过滤文件范围

```xml
<build>
    <resources>
        <!--设置资源目录-->
        <resource>
            <directory>../maven_01_ssm/src/main/resources</directory>
            <!--设置能够解析${}，默认是false -->
            <filtering>true</filtering>
        </resource>
    </resources>
</build>
```

```xml
<build>
    <resources>
        <!--
			${project.basedir}: 当前项目所在目录,子项目继承了父项目，
			相当于所有的子项目都添加了资源目录的过滤
		-->
        <resource>
            <directory>${project.basedir}/src/main/resources</directory>
            <filtering>true</filtering>
        </resource>
    </resources>
</build>
```

**说明:**打包的过程中如果报如下错误:

![1630948929828](https://xiongyuqing-img.oss-cn-qingdao.aliyuncs.com/img/202401161043206.png)

原因就是Maven发现你的项目为web项目，就会去找web项目的入口web.xml[配置文件配置的方式]，发现没有找到，就会报错。

解决方案1：在maven_02_ssm项目的`src\main\webapp\WEB-INF\`添加一个web.xml文件

```xml
<?xml version="1.0" encoding="UTF-8"?>
<web-app xmlns="http://xmlns.jcp.org/xml/ns/javaee"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://xmlns.jcp.org/xml/ns/javaee http://xmlns.jcp.org/xml/ns/javaee/web-app_3_1.xsd"
         version="3.1">
</web-app>
```

解决方案2: 配置maven打包war时，忽略web.xml检查

```xml
<build>
    <plugins>
        <plugin>
            <groupId>org.apache.maven.plugins</groupId>
            <artifactId>maven-war-plugin</artifactId>
            <version>3.2.3</version>
            <configuration>
                <failOnMissingWebXml>false</failOnMissingWebXml>
            </configuration>
        </plugin>
    </plugins>
</build>
```

上面我们所使用的都是Maven的自定义属性，除了${project.basedir},它属于Maven的内置系统属性。

在Maven中的属性分为:

- 自定义属性（常用）
- 内置属性
- Setting属性
- Java系统属性
- 环境变量属性

![1630981519370](https://xiongyuqing-img.oss-cn-qingdao.aliyuncs.com/img/202401161040645.png)

## 版本管理

![image-20240116104120066](https://xiongyuqing-img.oss-cn-qingdao.aliyuncs.com/img/202401161041097.png)

里面有两个单词，SNAPSHOT和RELEASE，它们所代表的含义是什么呢?

在我们jar包的版本定义中，有两个工程版本用的比较多:

- SNAPSHOT（快照版本）
  - 项目开发过程中临时输出的版本，称为快照版本
  - 快照版本会随着开发的进展不断更新
- RELEASE（发布版本）
  - 项目开发到一定阶段里程碑后，向团队外部发布较为稳定的版本，这种版本所对应的构件文件是稳定的
  - 即便进行功能的后续开发，也不会改变当前发布版本内容，这种版本称为发布版本

除了上面的工程版本，我们还经常能看到一些发布版本:

* alpha版:内测版，bug多不稳定内部版本不断添加新功能
* beta版:公测版，不稳定(比alpha稳定些)，bug相对较多不断添加新功能
* 纯数字版