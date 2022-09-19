package com.itheima.test;

import org.junit.Test;
import redis.clients.jedis.Jedis;

/**
 * 使用Jedis 操作 Redis
 */
public class JedisTest {

    @Test
    public void testRedis(){
        /**
         * 1. 链接
         * 2. 执行操作
         * 3. 关闭连接
         */

        Jedis jedis = new Jedis("localhost", 6379);
        // 有密码 使用 jedis.auth()方法

        jedis.set("username", "xiaoming");

        jedis.close();

    }
}
