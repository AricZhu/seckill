# Java 高并发秒杀项目

## 背景
通过手撸一个实际项目来学习 Java 高并发相关知识。这里会记录项目开发过程中的各种问题

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

其中密码是两次 md5 加密，理由如下：
1. 第一次是用户端传到后端时进行 md5 加密，防止用户密码在网路中明文传输被截获；
2. 第二次是后端存入数据库时再进行 md5 加密，防止数据库被盗后，根据 salt 字段反编译出密码

## 细节记录
1. 当包的版本修改后，可以运行下面的命令重新进行依赖安装构建：
```bash
mvn clean install
```
2. MD5 加密和验签相关原理
MD5 加密是一种信息摘要技术，可以将任何长度的信息经过计算最终都得出固定的 128 位长度的字符串，并且原文有任何改动后，计算结果都会不同。使用 MD5 加密传输的原因是因为在互联网上传输数据有被第三方盗用以及篡改的风险。
MD5 加密和验签原理如下：

    a. 客户端拼接一个字符串，字符串中包含了所传的业务数据，以及密钥key，按照一定的规则进行拼接成最终的字符串，经过MD5加密后，形成最终的签名 sign。

    b. 将业务数据以及最终的 sign 都传给服务端，然后服务端使用同样的字符串拼接规则，以及密钥 key，再通过 MD5 加密后得到 sign2，如果客户端传过来的 sign 和服务端计算得到的 sign2 结果一致，就表明数据没有被篡改。

以上就是 MD5 验签的过程

3. mybatis 代码自动生成，参考地址：https://baomidou.com/pages/d357af/#%E4%BD%BF%E7%94%A8%E6%95%99%E7%A8%8B

有一些改动，具体看代码：CodeGenerator