package com.haotongxue.config;

import com.haotongxue.cacheUtil.LoadingRedisCache;
import com.haotongxue.cacheUtil.MyRedis;
import com.haotongxue.entity.PrivacySetting;
import com.haotongxue.entity.User;
import com.haotongxue.service.IPrivacySettingService;
import com.haotongxue.service.IPrivacyTargetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

@Configuration
public class PrivacySettingCacheConfig {
    @Autowired
    MyRedis<PrivacySetting> myRedis;

    @Autowired
    IPrivacySettingService settingService;

    /**
     * 用于觅他隐私查询
     * @return
     */
    @Bean("privacySettingCache")
    public LoadingRedisCache<PrivacySetting> getCache(){
        return myRedis.newBuilder()
                .expireAfterWrite(1, TimeUnit.DAYS)
                .setPrefix("privacySetting")
                .build(key -> settingService.getById(key));
    }
}
