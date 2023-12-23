# Java 高并发秒杀项目

## 背景
本项目是对 B 站教学视频：《半天带你用-springBoot、Redis轻松实现Java高并发秒杀系统》的一个记录，借此学习 Java 高并发等相关知识。

教学视频地址如下

https://www.bilibili.com/video/BV1sf4y1L7KE?p=1&vd_source=4b068d7ba228906300002a95a14d4b6b

## 技术方案
TODO

## 数据库建表SQL
```sql
create table t_user(
    `id` bigint(20) not null comment  '用户ID， 手机号码',
    `nickname` varchar(255) not null,
    `password` varchar(32) default null comment 'MD5(MD5(pass 明文 + 固定salt)  + salt)',
    `slat` varchar(10) default null,
    `head` varchar(128) default null comment '头像',
    `register_date` datetime default null comment '注册时间',
    `last_login_date` datetime default null comment '最后一次登陆时间',
    `login_count` int(11) default '0' comment '登陆次数',
    primary key(`id`)
)
```

## 笔记

### maven 依赖更新
当包的版本修改后，可以运行下面的命令重新进行依赖安装构建：
```bash
mvn clean install
```
运行上述命令的效果，就等于先运行 mvn clean，然后运行 mvn install

### 登陆设计中，两次MD5加密原因
1. 第一次加密是前端到后端时进行 md5 加密，这是为了防止用户密码在网路中明文传输被截获；
2. 第二次是后端存入数据库时再进行 md5 加密，这是为了防止数据库被盗后，单次加密的 MD5 容易被碰撞解码

参考地址

https://developer.aliyun.com/article/636337

### MD5 加密的验签原理
MD5 加密是一种信息摘要技术，可以将任何长度的信息经过计算最终都得出固定的 128 位长度的字符串，并且原文有任何改动后，计算结果都会不同。使用 MD5 加密传输的原因是因为在互联网上传输数据有被第三方盗用以及篡改的风险。
MD5 加密和验签原理如下：

    a. 客户端拼接一个字符串，字符串中包含了所传的业务数据，以及密钥key，按照一定的规则进行拼接成最终的字符串，经过MD5加密后，形成最终的签名 sign。
    b. 将业务数据以及最终的 sign 都传给服务端，然后服务端使用同样的字符串拼接规则，以及密钥 key，再通过 MD5 加密后得到 sign2，如果客户端传过来的 sign 和服务端计算得到的 sign2 结果一致，就表明数据没有被篡改。

以上就是 MD5 验签的过程

### mybatis 代码自动生成
参考地址

https://baomidou.com/pages/d357af/#%E4%BD%BF%E7%94%A8%E6%95%99%E7%A8%8B

实际的实现参考 **CodeGenerator** 类

### 自定义注解参数校验
1. 先实现一个注解类
2. 然后实现一个校验类，参数就是第1点中实现的注解类，注意校验类需要实现 **ConstraintValidator** 类

完成上述两步骤后，就可以将注解类添加到需要的地方去完成校验

具体的实现可以参考注解类 **IsMobile** 和校验类 **IsMobileValidator**，以及实际应用的地方 **LoginVo** 类

### 异常处理
* 先添加 **@ControllerAdvice** 注解
* 然后添加 **@ExceptionHandler** 注解来处理所有异常

通过上述两步实现了 Controller 层的所有异常的处理，具体实现参考 **GlobalException** 和 **GlobalExceptionHandler**

### 自定义参数解析
作用：通过自定义参数解析，可以将参数直接传递给 Controller 层

具体实现：通过一个实现了 **WebMvcConfigurer** 接口的配置类，并重写 **addArgumentResolvers** 方法，添加自定义的类即可。

实例可看 **WebConfig**、**UserArgumentResolver** 这两个类，并在 /goods/toList 接口中直接使用了 User 参数

### 分布式 session
问：为什么需要分布式 session ？

答：在多机部署的环境下，不用分布式 session 会导致用户登陆态消失，需要重新登陆

问：如何实现分布式 sessin ？

答：一般通过 redis 实现，即将 session 信息存到 redis 中，并且 redis 会单独部署到其他机器。当后端需要用户信息的时候，直接从 redis 中获取即可。
