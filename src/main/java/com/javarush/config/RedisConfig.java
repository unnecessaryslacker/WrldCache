package com.javarush.config;

import redis.clients.jedis.Jedis;

public class RedisConfig {
    private static final Jedis jedis = new Jedis("localhost", 6379);

    static {
        jedis.connect();
    }

    public static Jedis getJedis() {
        return jedis;
    }
}
