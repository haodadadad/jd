package com.jit.jd;

import com.jit.jd.mapper.AddressMapper;
import com.jit.jd.mapper.UserMapper;
import com.jit.jd.pojo.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.core.script.RedisScript;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class JdApplicationTests {

@Autowired
private RedisTemplate redisTemplate;
    @Autowired
    private RedisScript<Boolean> script;

    @Test
    public void testLock01(){
        ValueOperations valueOperations = redisTemplate.opsForValue();
        //占位，如果key不存在才可以设置成功
        //5秒后锁失效,给锁一个过期时间，防止在应用运行中抛出异常导致锁无法正常释放
        Boolean isLocked = valueOperations.setIfAbsent("k1", "v1", 5, TimeUnit.SECONDS);
        //如果占位成功，则进行正常操作
        if(isLocked){
            valueOperations.set("name", "hhh");
            String name = (String)valueOperations.get("name");
            System.out.println("name:" + name);
            //操作结束，删除锁
            redisTemplate.delete("k1");
        }else{
            System.out.println("有线程在使用，请稍后");
        }
    }

    @Test
    public void testLock02(){
        ValueOperations valueOperations = redisTemplate.opsForValue();
        String value = UUID.randomUUID().toString();
        //占位，如果key不存在才可以设置成功
        //5秒后锁失效,给锁一个过期时间，防止在应用运行中抛出异常导致锁无法正常释放
        Boolean isLocked = valueOperations.setIfAbsent("k1", value, 5, TimeUnit.SECONDS);
        //如果占位成功，则进行正常操作
        if(isLocked){
            valueOperations.set("name", "hhh");
            String name = (String)valueOperations.get("name");
            System.out.println("name:" + name);
            System.out.println(valueOperations.get("k1"));

            //操作结束，删除锁
            //执行lock.lua脚本, lua脚本是原子型的
            Boolean result = (Boolean) redisTemplate.execute(script, Collections.singletonList("k1"), value);
            System.out.println(result);
        }else{
            System.out.println("有线程在使用，请稍后");
        }
    }



}
