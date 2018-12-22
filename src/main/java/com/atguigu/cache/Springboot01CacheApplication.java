package com.atguigu.cache;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

/**
 * 一：搭建基本环境
 * 1.导入数据库文件 创建出department表和employee表
 * 2.创建JavaBean封装数据
 * 3.整合mybatis操作数据库
 *      1.配置数据源信息
 *      2.使用注解版的mybatis
 *          1）、@MapperScan指定需要扫描的mapper接口所在的包
 *          2）、
 *  二：快速体验缓存
 *      步骤  1、开启基于注解的缓存
 *           2、标注缓存注解即可
 *           @Cacheable
 *           @CacheEvict
 *           @CachePut
 *默认使用的是ConcurrentMapCacheManager==>ConcurrentMapCache,将数据保存在ConcurrentMap<Object, Object> store中的
 * 开发中使用的是缓存中间件：redis、memcached、ehcache
 * 三：整合redis来作为缓存
 *  1、安装redis：使用docker安装
 *  2、引入redis的starter
 *  3、配置redis
 *  4、测试缓存
 *      原理：CacheManager===Cache缓存组件来实际CRUD数据    RedisCacheConfiguration会匹配 simple不匹配
 *      1) 默认保存数据k-v都是对象的时候，利用序列化来保存的，如何保存为json？
 *              1、引入了redis的starter，cacheManager变为RedisCacheManager
 *              2、默认创建的RedisCacheManager操作redis的时候RedisTemplate<Object,Object>()
 *              3、默认使用jdk序列化机制
 *      2）自定义redisCacheManager
 *
 */
@MapperScan("com.atguigu.cache.mapper")
@SpringBootApplication
@EnableCaching
public class Springboot01CacheApplication {

    public static void main(String[] args) {
        SpringApplication.run(Springboot01CacheApplication.class, args);
    }

}

