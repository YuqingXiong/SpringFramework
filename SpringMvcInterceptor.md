# 拦截器

## 拦截器概念

讲解拦截器的概念之前，先看一张图:

![1630676280170](https://xiongyuqing-img.oss-cn-qingdao.aliyuncs.com/img/202401151619865.png)

(1)浏览器发送一个请求会先到Tomcat的web服务器

(2)Tomcat服务器接收到请求以后，会去判断请求的是静态资源还是动态资源

(3)如果是静态资源，会直接到Tomcat的项目部署目录下去直接访问

(4)如果是动态资源，就需要交给项目的后台代码进行处理

(5)在找到具体的方法之前，我们可以去配置过滤器(可以配置多个)，按照顺序进行执行

(6)然后进入到到中央处理器(SpringMVC中的内容)，SpringMVC会根据配置的规则进行拦截

(7)如果满足规则，则进行处理，找到其对应的controller类中的方法进行执行,完成后返回结果

(8)如果不满足规则，则不进行处理

(9)这个时候，如果我们需要在每个Controller方法执行的前后添加业务，具体该如何来实现?

这个就是拦截器要做的事。

* 拦截器（Interceptor）是一种动态拦截方法调用的机制，在SpringMVC中动态拦截控制器方法的执行
* 作用:
  * 在指定的方法调用前后执行预先设定的代码
  * 阻止原始方法的执行
  * 总结：拦截器就是用来做增强

看完以后，大家会发现

* 拦截器和过滤器在作用和执行顺序上也很相似

所以这个时候，就有一个问题需要思考:拦截器和过滤器之间的区别是什么?

- 归属不同：Filter属于Servlet技术，Interceptor属于SpringMVC技术
- 拦截内容不同：Filter对所有访问进行增强，Interceptor仅针对SpringMVC的访问进行增强

![1630676903190](https://xiongyuqing-img.oss-cn-qingdao.aliyuncs.com/img/202401151619882.png)

## 环境准备

REST 项目复制，作为项目环境 `springmvc_05_interceptor`

## 拦截器开发

1. 创建拦截器类

让类实现HandlerInterceptor接口，重写接口中的三个方法。

```java
@Component
//定义拦截器类，实现HandlerInterceptor接口
//注意当前类必须受Spring容器控制
public class ProjectInterceptor implements HandlerInterceptor {
    @Override
    //原始方法调用前执行的内容
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        System.out.println("preHandle");
        return true;
    }

    @Override
    //原始方法调用后执行的内容
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        System.out.println("postHandle");
    }

    @Override
    //原始方法调用完成后执行的内容
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        System.out.println("afterCompletion");
    }
}
```

- 拦截器类要被SpringMVC容器扫描到，因为定义到了 controller 包下，已经在ComponentScan中被扫描到了

- 拦截器中的`preHandler`方法，如果返回true,则代表放行，会执行原始Controller类中要请求的方法，如果返回false，则代表拦截，后面的就不会再执行了。

2. :配置拦截器类

实现 `WebMvcConfigurationSupport` 类中的 `addInterceptors` 方法

```java
@Configuration
public class SpringMvcSupport extends WebMvcConfigurationSupport {
    @Autowired
    private ProjectInterceptor projectInterceptor;

    @Override
    protected void addInterceptors(InterceptorRegistry registry) {
        //配置拦截器
        registry.addInterceptor(projectInterceptor).addPathPatterns("/books" ,"/books/*" );
    }
}
```

只写 ”/books"的话 `http://localhost/books/100` 不会被拦截，原因是拦截器的`addPathPatterns`方法配置的拦截路径是`/books`,我们现在发送的是`/books/100`，所以没有匹配上，所以需要加上 `"/books/*"` 

上面的代码可以简化，SpringMvcSupport 和 SpringMvcConfig 处于同一层级下，而且 `WebMvcConfigurer` 接口中也有 `addInterceptors` 方法，所以可以直接写在 SpringMvcConfig 类中，就不用多写一个 SpringMvcSupport 类，也不用添加 "com.rainsun.config"扫描路径了：

```java
@Configuration
@ComponentScan({"com.rainsun.controller"})
@EnableWebMvc
public class SpringMvcConfig implements WebMvcConfigurer {

    @Autowired
    private ProjectInterceptor projectInterceptor;

    // 添加拦截器
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 添加拦截器：里面写了拦截时我们想做啥
        // 添加拦截路径：表示拦截哪些访问路径
        registry.addInterceptor(projectInterceptor)
                .addPathPatterns("/books","/books/*");

    }
}
```

拦截器的执行流程:

![1630679464294](https://xiongyuqing-img.oss-cn-qingdao.aliyuncs.com/img/202401151721433.png)

当有拦截器后，请求会先进入preHandle方法，

​	如果方法返回true，则放行继续执行后面的handle[controller的方法]和后面的方法

​	如果返回false，则直接跳过后面方法的执行。

## 拦截器中的参数

### 前置处理方法

原始方法之前运行preHandle

```java
@Override
public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
    String acceptEncoding = request.getHeader("Accept-Encoding"); // 获取请求头的某个key对应的value
    System.out.println(handler.getClass());// = HandlerMethod
    HandlerMethod hm = (HandlerMethod)handler; // 暴力反射获取执行的函数
    System.out.println("preHandle..." + acceptEncoding);
    return true;
}
```

* request:请求对象
* response:响应对象
* handler:被调用的处理器对象，本质上是一个方法对象，对反射中的Method对象进行了再包装

使用request对象可以获取请求数据中的内容，如获取请求头的`Content-Type`

```java
public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
    String contentType = request.getHeader("Content-Type");
    System.out.println("preHandle..."+contentType);
    return true;
}
```

使用handler参数，可以获取方法的相关信息

```java
public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
    HandlerMethod hm = (HandlerMethod)handler;
    String methodName = hm.getMethod().getName();//可以获取方法的名称
    System.out.println("preHandle..."+methodName);
    return true;
}
```

### 后置处理方法

原始方法运行后运行，如果原始方法被拦截，则不执行  

```java
public void postHandle(HttpServletRequest request,
                       HttpServletResponse response,
                       Object handler,
                       ModelAndView modelAndView) throws Exception {
    System.out.println("postHandle");
}
```

前三个参数和上面的是一致的。

modelAndView:如果处理器执行完成具有返回结果，可以读取到对应数据与页面信息，并进行调整

因为咱们现在都是返回json数据，所以该参数的使用率不高。

### 完成处理方法

拦截器最后执行的方法，无论原始方法是否执行

```java
public void afterCompletion(HttpServletRequest request,
                            HttpServletResponse response,
                            Object handler,
                            Exception ex) throws Exception {
    System.out.println("afterCompletion");
}
```

前三个参数与上面的是一致的。

ex:如果处理器执行过程中出现异常对象，可以针对异常情况进行单独处理  

因为我们现在已经有全局异常处理器类，所以该参数的使用率也不高。

这三个方法中，最常用的是==preHandle==,在这个方法中可以通过返回值来决定是否要进行放行，我们可以把业务逻辑放在该方法中，如果满足业务则返回true放行，不满足则返回false拦截。

## 拦截器链

多个拦截器时，就组成了拦截器链

配置方式：

1. 定义多个拦截器，它们都实现了 HandlerInterceptor 接口

例如下面新定义了一个拦截器类 ProjectInterceptor2:

```java
@Component
public class ProjectInterceptor2 implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        System.out.println("preHandle...222");
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request,
                           HttpServletResponse response,
                           Object handler,
                           ModelAndView modelAndView) throws Exception {

        System.out.println("postHandle...222");
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        System.out.println("afterCompletion...222");
    }
}
```

2. 将拦截器配置到SpringMVC中：

```java
@Configuration
@ComponentScan({"com.rainsun.controller"})
@EnableWebMvc
public class SpringMvcConfig implements WebMvcConfigurer {

    @Autowired
    private ProjectInterceptor projectInterceptor;

    @Autowired
    private ProjectInterceptor2 projectInterceptor2;

    // 添加拦截器
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 添加拦截器：里面写了拦截时我们想做啥
        // 添加拦截路径：表示拦截哪些访问路径
        registry.addInterceptor(projectInterceptor)
                .addPathPatterns("/books","/books/*");

        registry.addInterceptor(projectInterceptor2)
                .addPathPatterns("/books","/books/*");

    }
}
```

3. 拦截器的执行顺序和配置顺序有关：

![image-20240115194621790](https://xiongyuqing-img.oss-cn-qingdao.aliyuncs.com/img/202401151946825.png)

pre1->per2->post2->post1->after2->after1

拦截器链的运行规则：

* 当配置多个拦截器时，形成拦截器链
* 拦截器链的运行顺序参照拦截器添加顺序为准
* 当拦截器中出现对原始处理器的拦截，后面的拦截器均终止运行
* 当拦截器运行中断，仅运行配置在前面的拦截器的afterCompletion操作

![1630680579735](https://xiongyuqing-img.oss-cn-qingdao.aliyuncs.com/img/202401151947749.png)