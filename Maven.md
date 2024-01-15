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

## 

