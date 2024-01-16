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

# 3 继承与聚合

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

