package com.haotongxue.handler;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.benmanes.caffeine.cache.LoadingCache;
import com.haotongxue.entity.OfficialUser;
import com.haotongxue.entity.User;
import com.haotongxue.service.IOfficialUserService;
import com.haotongxue.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;
import java.util.function.BiConsumer;

/**
 * 用来处理小程序和公众号之间的一些关联
 */
@Component
public class MiniAndOfficialHandler {

    @Autowired
    IUserService userService;

    @Autowired
    IOfficialUserService officialUserService;

    @Resource(name = "loginCache")
    LoadingCache<String,Object> cache;
    /**
     * 确认用户是否有订阅课程推送
     */
    @Scheduled(fixedDelay = 40*60*1000)
    public void confirmSubscribe(){
        QueryWrapper<OfficialUser> officialWrapper = new QueryWrapper<>();
        officialWrapper.select("openid");
        List<OfficialUser> officialUserList = officialUserService.list(officialWrapper);
        ConcurrentMap<String, Object> map = cache.asMap();
//        for (OfficialUser  officialUser : officialUserList){
//            officialUser
//        }
    }
}
