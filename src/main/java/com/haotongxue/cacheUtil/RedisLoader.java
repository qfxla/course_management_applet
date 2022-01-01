package com.haotongxue.cacheUtil;

@FunctionalInterface
public interface RedisLoader {
    Object load(String key);
}
