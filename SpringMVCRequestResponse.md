**环境准备：工程：springmvc_02_request_respond**

- jar,plugins
- config
- controller

# 1 请求

## 1.1 设置映射路径

路径冲突问题：增加不同的路径前置

- 修改Controller 中的方法使用 `@RequsetMapping(".../...")` 进行设置

  ```java
  @Controller
  public class UserController {
      @RequestMapping("user/save")
      @ResponseBody
      public String save(){
          System.out.println("user save ...");
          return "{'module':'user save'}";
      }
  
      @RequestMapping("user/delete")
      @ResponseBody
      public String delete(){
          System.out.println("user delete ...");
          return "{'module':'user delete'}";
      }
  }
  ```

- 修改Controller 类使用 `@RequestMapping("/user")` 增加统一前缀

  ```java
  @Controller
  @RequestMapping("user")
  public class UserController {
      @RequestMapping("save")
      @ResponseBody
      public String save(){
          System.out.println("user save ...");
          return "{'module':'user save'}";
      }
  
      @RequestMapping("delete")
      @ResponseBody
      public String delete(){
          System.out.println("user delete ...");
          return "{'module':'user delete'}";
      }
  }
  ```

## 1.2 请求参数

1. GET 发送参数

客户端直接在 url 中发送需要传递参数：

```http
http://localhost:8081/commonParam?name=rainsun
```

服务器通过形参接收传递过来的参数：

```java
@RequestMapping("/commonParam")
@ResponseBody
public String commonParam(String name){
    System.out.println("普通参数传递 name ==> "+name);
    return "{'module':'commonParam'}";
}
```

发送多个参数的时候，就用多个形参接收

```http
http://localhost:8081/commonParam?name=rainsun&age=23
```

```java
@RequestMapping("/commonParam")
@ResponseBody
public String commonParam(String name, int age){
    System.out.println("普通参数传递 name ==> "+name);
    System.out.println("普通参数传递 name ==> "+age);
    return "{'module':'commonParam'}";
}
```

2. POST 发送参数

POST请求的参数在请求体里面：

![image-20240112153623691](https://xiongyuqing-img.oss-cn-qingdao.aliyuncs.com/img/202401121536857.png)

3. GET 中文乱码：

由于GET的参数在访问路径里面，所以可以设置访问路径编解码字符集：

```xml
<build>
    <plugins>
      <plugin>
        <groupId>org.apache.tomcat.maven</groupId>
        <artifactId>tomcat7-maven-plugin</artifactId>
        <version>2.1</version>
        <configuration>
          <port>8081</port><!--tomcat端口号-->
          <path>/</path> <!--虚拟目录-->
          <uriEncoding>UTF-8</uriEncoding><!--访问路径编解码字符集-->
        </configuration>
      </plugin>
    </plugins>
  </build>
```

4. POST 中文乱码

设置过滤器

```java
public class ServletContainerInitConfig extends AbstractAnnotationConfigDispatcherServletInitializer {

    @Override
    protected Class<?>[] getRootConfigClasses() {
        return new Class[0];
    }

    @Override
    protected Class<?>[] getServletConfigClasses() {
        return new Class[]{SpringMvcConfig.class};
    }

    @Override
    protected String[] getServletMappings() {
        return new String[]{"/"};
    }

    // 设置过滤器进行乱码处理
    @Override
    protected Filter[] getServletFilters() {
        CharacterEncodingFilter filter = new CharacterEncodingFilter();
        filter.setEncoding("UTF-8");
        return new Filter[]{filter};
    }
}
```

CharacterEncodingFilter是在spring-web(已经包含在spring-webmvc中了）包中，所以用之前需要导入对应的jar包。

## 1.3 五种类型的参数传递

1. 普通参数

   * 形参接收地址参数

   * 设置形参接收指定地址参数的值：@RequestParam

     ```java
     @RequestMapping("/commonParamDifferentName")
     @ResponseBody
     public String commonParamDifferentName(@RequestParam("name") String userName, int age){
         System.out.println("普通参数传递 name ==> "+userName);
         System.out.println("普通参数传递 name ==> "+age);
         return "{'module':'commonParamDifferentName'}";
     }
     ```

   * **注意:写上@RequestParam注解框架就不需要自己去解析注入，能提升框架处理性能**

2. POJO类型参数
   - 如果参数比较多，那么后台接收参数的时候就比较复杂，这个时候我们可以考虑使用POJO数据类型
   - POJO参数：请求参数名与形参对象属性名相同，定义POJO类型形参即可接收参数

3. 嵌套POJO类型参数
   - 嵌套POJO参数：请求参数名与形参对象属性名相同，按照对象层次结构关系即可接收嵌套POJO属性参数

4. 数组类型参数

   - 数组参数：请求参数名与形参对象属性名相同且请求参数为多个，定义数组类型即可接收参数

   - ```http
     http://localhost:8081/user/arrayParam?likes=game&likes=music
     ```

   - ```java
     //数组参数：同名请求参数可以直接映射到对应名称的形参数组对象中
     @RequestMapping("/arrayParam")
     @ResponseBody
     public String arrayParam(String[] likes){
         System.out.println("数组参数传递 likes ==> "+ Arrays.toString(likes));
         return "{'module':'array param'}";
     }
     ```

5. 集合类型参数

```java
//集合参数：同名请求参数可以使用@RequestParam注解映射到对应名称的集合对象中作为数据
@RequestMapping("/listParam")
@ResponseBody
public String listParam(@RequestParam List<String> likes){
    System.out.println("集合参数传递 likes ==> "+ likes);
    return "{'module':'list param'}";
}
```

不加 `@RequestParam` 时，SpringMVC会将List看作一个POJO参数处理，将其创建一个对象并准备把前端的数据封装到对象中，但是List是一个接口无法创建对象，然后报错。

## 1.4 JSON类型的参数传递

对于JSON数据类型，我们常见的有三种:

- json普通数组（["value1","value2","value3",...]）
- json对象（{key1:value1,key2:value2,...}）
- json对象数组（[{key1:value1,...},{key2:value2,...}]）

### 环境配置

1. 首先SpringMVC默认使用的是jackson来处理json的转换，所以需要在pom.xml添加jackson依赖

```xml
<dependency>
    <groupId>com.fasterxml.jackson.core</groupId>
    <artifactId>jackson-databind</artifactId>
    <version>2.15.3</version>
</dependency>
```

2. 开启SpringMVC注解支持：在SpringMVC的配置类中开启SpringMVC的注解支持，这里面就包含了将JSON转换成对象的功能。

```java
@Configuration
@ComponentScan("com.rainsun.controller")
// 开启 json 数据类型自动转换
@EnableWebMvc
public class SpringMvcConfig {
}
```

3. 参数前添加 `@RequestBody` ：表明以Json的格式解析获取的数据映射到形参的集合对象中

```java
@RequestMapping("/listParamForJson")
@ResponseBody
public String arrayParam(@RequestBody List<String> likes){
    System.out.println("list common json 参数传递 likes ==> "+ likes);
    return "{'module':'list common json'}";
}
```

-  `@RequestBody` 约束请求参数是按照 json 解析
-  `@ResponseBody` 约束返回的响应参数为 json 格式

4. PostMan发送JSON数据

### JSON数组数据

![image-20240112163544420](https://xiongyuqing-img.oss-cn-qingdao.aliyuncs.com/img/202401121635482.png)

### JSON对象数据

请求方传递的格式：

![image-20240112164636160](https://xiongyuqing-img.oss-cn-qingdao.aliyuncs.com/img/202401121646205.png)

接收方接收的格式：

```java
@RequestMapping("/pojoParamForJson")
@ResponseBody
public String pojoParamForJson(@RequestBody User user){
    System.out.println("pojoParamForJson 参数传递 user ==> "+ user);
    return "{'module':'pojoParamForJson'}";
}
```

json中的`key: value`与对象中的`属性 值`一一对应

对象中包含另一个对象的情况：

```java
{
	"name":"itcast",
	"age":15,
    "address":{
        "province":"beijing",
        "city":"beijing"
    }
}
```

### JSON对象数组

数组里面包含对象，数组是用 `[...]` 表示的，对象用 `{...}` 表示的，所以是：

```java
[
    {...},
    {...},
    ...
]
```

用 `List<ObjectName> `进行接收，不要忘了 `@RequestBody` 

```java
@RequestMapping("/listPojoParamForJson")
@ResponseBody
public String listPojoParamForJson(@RequestBody List<User> list){
    System.out.println("list pojo(json)参数传递 list ==> "+list);
    return "{'module':'list pojo for json param'}";
}
```

## 1.5 日期类型参数传递

日期类型比较特殊，因为对于日期的格式有N多中输入方式，比如:

* 2088-08-18
* 2088/08/18
* 08/18/2088
* ......

SpringMVC默认支持的字符串转日期的格式为`yyyy/MM/dd`

```java
@RequestMapping("/dataParam")
@ResponseBody
public String dataParam(Date date,Date date1)
    System.out.println("参数传递 date ==> "+date);
    return "{'module':'data param'}";
}
```

SpringMVC进行格式转换需要使用`@DateTimeFormat`

```java
@RequestMapping("/dataParam")
@ResponseBody
public String dataParam(Date date,
                        @DateTimeFormat(pattern="yyyy-MM-dd") Date date1)
    System.out.println("参数传递 date ==> "+date);
	System.out.println("参数传递 date1(yyyy-MM-dd) ==> "+date1);
    return "{'module':'data param'}";
}
```

**携带时间的日期**

```java
@RequestMapping("/dataParam")
@ResponseBody
public String dataParam(Date date,
                        @DateTimeFormat(pattern="yyyy-MM-dd") Date date1,
                        @DateTimeFormat(pattern="yyyy/MM/dd HH:mm:ss") Date date2)
    System.out.println("参数传递 date ==> "+date);
	System.out.println("参数传递 date1(yyyy-MM-dd) ==> "+date1);
	System.out.println("参数传递 date2(yyyy/MM/dd HH:mm:ss) ==> "+date2);
    return "{'module':'data param'}";
}
```

## 1.6 参数转换的内部原理

* 前端传递字符串，后端使用日期Date接收
* 前端传递JSON数据，后端使用对象接收
* 前端传递字符串，后端使用Integer接收
* 后台需要的数据类型有很多中
* 在数据的传递过程中存在很多类型的转换

SpringMVC来做这个类型转换，SpringMVC中提供了很多类型转换接口和实现类，

在框架中，有一些类型转换接口，其中有:

* (1) Converter接口

```java
/**
*	S: the source type
*	T: the target type
*/
public interface Converter<S, T> {
    @Nullable
    //该方法就是将从页面上接收的数据(S)转换成我们想要的数据类型(T)返回
    T convert(S source);
}
```

**注意:Converter所属的包为`org.springframework.core.convert.converter`**

Converter接口的实现类

![1630496385398](https://xiongyuqing-img.oss-cn-qingdao.aliyuncs.com/img/202401121656973.png)

框架中有提供很多对应Converter接口的实现类，用来实现不同数据类型之间的转换,如:

请求参数年龄数据（String→Integer）

日期格式转换（String → Date）

* (2) HttpMessageConverter接口

该接口是实现对象与JSON之间的转换工作

**==注意:SpringMVC的配置类把@EnableWebMvc当做标配配置上去，不要省略==**

# 2 响应

## 2.1 响应页面

1. 不能添加@ResponseBody,如果加了该注入，会直接将page.jsp当字符串返回前端
2. 方法需要返回String

```java
@RequestMapping("/toJumpPage")
public String toJumpPage(){
    System.out.println("跳转页面");
    return "page.jsp";
}
```

## 2.2 返回文本数据

```java
@Controller
public class UserController {
    
   	@RequestMapping("/toText")
	//注意此处该注解就不能省略，如果省略了,会把response text当前页面名称去查找，如果没有回报404错误
    @ResponseBody
    public String toText(){
        System.out.println("返回纯文本数据");
        return "response text";
    }
    
}
```

## 2.3 响应JSON数据

### 响应POJO对象

返回值为实体类对象，设置返回值为实体类类型，即可实现返回对应对象的json数据，需要依赖==@ResponseBody==注解和==@EnableWebMvc==注解

```java
@RequestMapping("/toJsonPOJO")
@ResponseBody
public User toJsonPOJO(){
    System.out.println("toJsonPOJO");
    User user = new User();
    user.setName("rainsun");
    user.setAge(23);
    return user;
}
```

### 响应POJO集合对象

```java
@RequestMapping("/toJsonList")
@ResponseBody
public List<User> toJsonList(){
    System.out.println("toJsonList");
    ArrayList<User> users = new ArrayList<>();
    User user = new User();
    user.setName("rainsun");
    user.setAge(23);
    users.add(user);
    users.add(user);
    return users;
}
```

知识点1：@ResponseBody

**说明:**

* 该注解可以写在类上或者方法上
* 写在类上就是该类下的所有方法都有@ReponseBody功能
* 当方法上有@ReponseBody注解后
  * 方法的返回值为字符串，会将其作为文本内容直接响应给前端
  * 方法的返回值为对象，会将对象转换成JSON响应给前端

此处又使用到了类型转换，内部还是通过Converter接口的实现类完成的，所以Converter除了前面所说的功能外，它还可以实现:

* 对象转Json数据(POJO -> json)
* 集合转Json数据(Collection -> json)