package com.atguigu.cache.service;

import com.atguigu.cache.bean.Employee;
import com.atguigu.cache.mapper.EmployeeMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.*;
import org.springframework.stereotype.Service;

@CacheConfig(cacheNames = "emp",cacheManager = "employeeCacheManager") //抽取缓存的公共配置
@Service
public class EmployeeService {

    @Autowired
    EmployeeMapper employeeMapper;

    /**
     * 将方法的运行结果进行缓存，以后再要相同的数据就从缓存中获取，不再调用方法
     *
     * CacheManager管理多个缓存组件的，对缓存真正的CRUD操作在Cache组件中，每个缓存组建有自己唯一的名字
     *几个属性：
     *      cacheNames/value：指定缓存组建的名字,将方法的返回结果放在那个缓存中，是数组的方式，可以指定多个
     *      key：缓存数据使用的key，默认使用方法参数的值   1-方法的返回值    { 可以多个 }
     *          编写SpEL #a0 #p0 #root.args[0]
     *      keyGenerator:key的生成器，可以自己制定key的生成器组件id（和key二选一，只能制定一个）
     *      cacheManager：制定缓存管理器
     *      condition：制定符合条件情况下才缓存  condition="#id>0" 才进行缓存
     *          condition = "#a0>1" 第一个参数的值大于1才进行缓存
     *      unless:否定缓存，当unless指定的条件为true，方法的返回值就不会缓存；可以获取到结果 unless="#result == null"
     *      sync:是否使用异步模式   此时不支持unless
     *
     * 原理:
     *  1、自动配置类 CacheAutoConfiguration
     *  2、缓存的配置类
     *      11个
     *  3、那个配置类生效 SimpleCacheConfiguration
     *  4、给容器中注册了一个CacheManager：ConcurrentMapCacheManager
     *  5、可以获取和创建ConcurrentMapCache类型的缓存组件，作用将数据保存在ConcurrentMap中
     *
     * 运行流程：
     *  @Cacheable
     *  1、方法运行之前，先去查询Cache(缓存组件),按照cacheName指定的名字获取:
     *      (CacheManager先获取相应缓存)，第一次获取缓存如果没有Cache组件，会自动创建出来
     *  2、去Cache中查找缓存的内容，使用一个key，默认就是方法的参数：
     *      key是按照某种策略生成，默认使用keyGenerator生成的，默认使用SimpleKeyGenerator生成
     *          SimpleKeyGenerator生成key的默认策略
     *              没有参数：key = new SimpleKey()
     *              如果有一个参数: key = 参数的值
     *              如果有多个参数: key = SimpeKey(params);
     *  3、没有查到缓存旧调用目标方法;
     *  4、将目标方法的结果，放进缓存中
     *
     * @Cacheable标注的方法执行之前先来检查缓存中有没有这个数据，默认按照参数的值作为key去查询缓存，如果没有旧运行方法，并将结果放入缓存
     *
     * 核心：
     *  1）、使用CacheManager【ConcurrentMapCacheManager】按照名字得到cache【ConcurrentMapCache】组件
     *  2）、key使用keyGenerator生成的，默认是SimpleKeyGenerator生成
     *
     * @param id
     * @return
     */
    @Cacheable(value = {"emp"}/*,keyGenerator = "myKeyGenerator",condition = "#a0>1"*/)  //相当于"#root.args[0]"
    public Employee getEmp(Integer id) {
        System.out.println("查询"+id+"号员工");
        Employee emp = employeeMapper.getEmpById(id);
        return  emp;
    }

    /**
     * @CachePut: 既调用方法，又同步更新缓存数据：
     * 修改了数据库的某个数据，同时更新缓存
     *  运行时机：
     *  1、先调用目标方法
     *  2、将目标方法的结果缓存起来
     *
     *  测试步骤：
     *      1、查询一号员工，结果放在缓存中
     *          key：1  value： lastName：张三
     *      2、以后查询还是之前的结果
     *      3、更新1号员工：【lastName：zhangsan gender：0】
     *          将方法的返回值也放进缓存了；
     *          key：传入的employee对象   值：返回的employee对象
     *      4、查询1号员工
     *          应该是更新后的员工
     *              key = "#employee.id"  等同于#result.id
     *              @Cacheable的key是不能用#result的
     */
    @CachePut(value = "emp",key = "#employee.id")
    public Employee updateEmp(Employee employee) {
        System.out.println("updateEmp: "+employee);
        employeeMapper.updateEmp(employee);
        return employee;
    }

    /**
     * @CacheEvict
     *  key：指定要清除的数据
     *  allEntries = true 删掉缓存中所有数据，默认为false
     *  beforeInvocation  是否在方法之前执行，默认是false，在方法之后执行;  方法之前执行，即使方法错误也能删除缓存
     */
    @CacheEvict(value = "emp",key = "#id")
    public void deleteEmp(Integer id) {
        System.out.println("删除一个员工：" +id);
        employeeMapper.deleteEmpById(id);
    }

    //@Caching 定义复杂的缓存规则
    @Caching(
            cacheable = {
                    @Cacheable(value = "emp",key = "#lastName")
            },
            put = {
                    @CachePut(value = "emp",key = "#result.id"),
                    @CachePut(value = "emp",key = "#result.email")
            }
    )
    public Employee getEmpByLastName(String lastName) {
        return  employeeMapper.getEmpByLastName(lastName);
    }
}
