package com.haotongxue.handler;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.github.benmanes.caffeine.cache.LoadingCache;
import com.haotongxue.entity.OfficialUser;
import com.haotongxue.entity.User;
import com.haotongxue.exceptionhandler.CourseException;
import com.haotongxue.service.IOfficialUserService;
import com.haotongxue.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentMap;

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
    @Transactional(rollbackFor = Exception.class)
    public void confirmSubscribe(){
        QueryWrapper<OfficialUser> officialWrapper = new QueryWrapper<>();
        officialWrapper.select("openid");
        List<OfficialUser> officialUserList = officialUserService.list(officialWrapper);
        for (OfficialUser officialUser : officialUserList){
            String openid = officialUser.getOpenid();
            User user = (User) cache.get(openid);
            if (user == null){
                continue;
            }
            if (user.getUnionId() == null){
                String uuid = UUID.randomUUID().toString();
                UpdateWrapper<OfficialUser> officialWrapperTwo = new UpdateWrapper<>();
                officialWrapperTwo.set("unionid",uuid).eq("openid",openid);
                if (!officialUserService.update(officialWrapperTwo)){
                    CourseException courseException = new CourseException();
                    courseException.setMsg("设置official_uuid失败");
                    throw courseException;
                }
                UpdateWrapper<User> userUpdateWrapper = new UpdateWrapper<>();
                userUpdateWrapper.set("union_id",uuid)
                        .set("subscribe",1)
                        .eq("openid",openid);
                if (!userService.update(userUpdateWrapper)){
                    CourseException courseException = new CourseException();
                    courseException.setMsg("设置user_uuid失败");
                    throw courseException;
                }
            }
        }
    }
}
