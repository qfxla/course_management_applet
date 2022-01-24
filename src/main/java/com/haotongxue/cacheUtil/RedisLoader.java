package com.haotongxue.cacheUtil;

public interface RedisLoader {
    Object load(String key);
}
