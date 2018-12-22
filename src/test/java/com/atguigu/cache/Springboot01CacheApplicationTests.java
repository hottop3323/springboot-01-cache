package com.atguigu.cache;

import com.atguigu.cache.bean.Employee;
import com.atguigu.cache.mapper.EmployeeMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class Springboot01CacheApplicationTests {
    @Autowired
    EmployeeMapper employeeMapper;

    @Autowired
    StringRedisTemplate stringRedisTemplate; //操作k-v都是字符串

    @Autowired
    RedisTemplate redisTemplate;    //k-v都是对象

    @Autowired
    RedisTemplate<Object,Employee> empRedisTemplate;

    /**
     * Redis常见的5大数据类型
     * String(字符串),List(列表)，Set(集合),Hash(散列),Zset(有序集合)
     * stringRedisTemplate.opsForValue() [string (字符串)]
     * stringRedisTemplate.opsForList() [list 列表]
     */
    @Test
    public void test01() {
        //给redis中保存一个数据
        //stringRedisTemplate.opsForValue().append("msg","hello");
//        String msg = stringRedisTemplate.opsForValue().get("msg");
//        System.out.println(msg);
//        stringRedisTemplate.opsForList().leftPush("mylist","1");
//        stringRedisTemplate.opsForList().leftPush("mylist","2");
//        stringRedisTemplate.opsForList().leftPush("mylist","3");
    }

    //测试保存对象
    @Test
    public void test02() {
        Employee emp = employeeMapper.getEmpById(1);
        //默认如果保存对象，使用jdk序列化机制，序列化后的数据保存到redis中
//        redisTemplate.opsForValue().set("emp01",emp);
        //1、将数据以json形式保存
        //（1）自己将对象转为json
        //（2）redis有默认的序列化归则,改变默认的序列化规则
        empRedisTemplate.opsForValue().set("emp-01",emp);
    }

    @Test
    public void contextLoads() {

        Employee emp = employeeMapper.getEmpById(1);
        System.out.println(emp);
    }

}

