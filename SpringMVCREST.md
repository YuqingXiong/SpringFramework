# REST 风格

**REST**（Representational State Transfer），表现形式状态转换,它是一种软件架构风格

REST的优点有:

- 隐藏资源的访问行为，无法通过地址得知对资源是何种操作
- 书写简化

按照REST风格访问资源时使**行为动作**区分对资源进行了何种操作

* `http://localhost/users`	查询全部用户信息 GET（查询）
* `http://localhost/users/1`  查询指定用户信息 GET（查询）
* `http://localhost/users`    添加用户信息    POST（新增/保存）
* `http://localhost/users`    修改用户信息    PUT（修改/更新）
* `http://localhost/users/1`  删除用户信息    DELETE（删除）

根据REST风格对资源进行访问称为**RESTful**。

**环境准备：springmvc_03_REST 项目**

## 新增 POST

- 删掉了形参
- 将请求路径更改为`/users`

* 访问该方法使用 POST
* 使用method属性限定该方法的访问方式为`POST`

```java
@RequestMapping(value = "/users", method = RequestMethod.POST)
@ResponseBody
public String save() {
    System.out.println("user save...");
    return "{'module':'user save'}";
}
```

## 删除 DELETE

将请求路径更改为`/users`

- 访问该方法使用 DELETE

传递路径参数：`http://localhost/users/1`

后端获取参数，需要做如下修改:

* 修改@RequestMapping的value属性，将其中修改为`/users/{id}`，目的是和路径匹配
* 在方法的形参前添加@PathVariable注解

```java
@RequestMapping(value = "/user/{id}", method = RequestMethod.DELETE)
@ResponseBody
public String delete(@PathVariable Integer id) {
    System.out.println("user delete..." + id);
    return "{'module':'user delete'}";
}
```

> 当新参与路径中的参数代号不一致时，可以在注解 `@PathVariable` 中加上参数表示对应路径中的哪个参数，例如：
>
> ```java
> @PathVariable("id")
> ```

当传递多个参数时，就用多个形参接收，路径里多加几个分号：

```java
@RequestMapping(value = "/user/{id}/{name}", method = RequestMethod.DELETE)
@ResponseBody
public String delete(@PathVariable Integer id, @PathVariable String name) {
    System.out.println("user delete..." + id);
    return "{'module':'user delete'}";
}
```

## 修改 PUT

将请求路径更改为`/users`

- 访问该方法使用 PUT

```java
@RequestMapping(value = "/users", method = RequestMethod.PUT)
@ResponseBody
public String update(@RequestBody User user) {
    System.out.println("user update..." + user);
    return "{'module':'user update'}";
}
```

## 查询 GET

将请求路径更改为`/users`

- 访问该方法使用 GET

```java
@RequestMapping(value = "/user/{id}", method = RequestMethod.GET)
@ResponseBody
public String getById(@PathVariable Integer id) {
    System.out.println("user getById..." + id);
    return "{'module':'user getById'}";
}
```

查询所有：

将请求路径更改为`/users`

- 访问该方法使用 GET

```java
@RequestMapping(value = "/users", method = RequestMethod.GET)
@ResponseBody
public String getAll() {
    System.out.println("user getAll...");
    return "{'module':'user getAll'}";
}
```

## 总结

关于接收参数，我们学过三个注解`@RequestBody`、`@RequestParam`、`@PathVariable`,这三个注解之间的区别和应用分别是什么?

* 区别
  * @RequestParam用于接收url地址传参或表单传参
  * @RequestBody用于接收json数据
  * @PathVariable用于接收路径参数，使用{参数名称}描述路径参数
* 应用
  * 后期开发中，发送请求参数超过1个时，以json格式为主，@RequestBody应用较广
  * 如果发送非json格式数据，选用@RequestParam接收请求参数
  * 采用RESTful进行开发，当参数数量较少时，例如1个，可以采用@PathVariable接收请求路径变量，通常用于传递id值

# RESTful快速开发

- 简化 `method = RequestMethod.xxx`  的写法
- 减少每个方法上的 `@ResponseBody` 注解

1. `/books` 路径放在类上面，作为所有方法的前缀，方法内部只指明对应的Mapping类型
2. `@RestController = @Controller + ReponseBody` : 一个注解用于声明这时一个Bean的Controller对象，以及表示该类的所有函数的返回值都是 json 类型的
3.  `@PostMapping = @RequestMapping(method = RequestMethod.POST)`

简化后：

```java
@RestController // @Controller + @ResponseBody
@RequestMapping("/books")
public class BookController {
//    @RequestMapping(method = RequestMethod.POST)
    @PostMapping
    public String save(@RequestBody Book book){
        System.out.println("book save..." + book);
        return "{'module':'book save'}";
    }

//    @RequestMapping(value = "/{id}",method = RequestMethod.DELETE)
    @DeleteMapping("/{id}")
    public String delete(@PathVariable Integer id){
        System.out.println("book delete..." + id);
        return "{'module':'book delete'}";
    }

//    @RequestMapping(method = RequestMethod.PUT)
    @PutMapping
    public String update(@RequestBody Book book){
        System.out.println("book update..." + book);
        return "{'module':'book update'}";
    }

//    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    @GetMapping("/{id}")
    public String getById(@PathVariable Integer id){
        System.out.println("book getById..." + id);
        return "{'module':'book getById'}";
    }

//    @RequestMapping(method = RequestMethod.GET)
    @GetMapping
    public String getAll(){
        System.out.println("book getAll...");
        return "{'module':'book getAll'}";
    }
}
```

