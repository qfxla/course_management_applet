package com.haotongxue.handler;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.haotongxue.cacheUtil.LoadingRedisCache;
import com.haotongxue.entity.OfficialUser;
import com.haotongxue.entity.User;
import com.haotongxue.exceptionhandler.CourseException;
import com.haotongxue.service.IOfficialUserService;
import com.haotongxue.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.UUID;

/**
 * 用来处理小程序和公众号之间的一些关联
 */
@Component
@Deprecated
public class MiniAndOfficialHandler {

    @Autowired
    IUserService userService;

    @Autowired
    IOfficialUserService officialUserService;

//    @Resource(name = "loginCache")
//    LoadingCache<String,Object> cache;

    @Resource(name = "loginCache")
    LoadingRedisCache cache;

    /**
     * 确认用户是否有订阅课程推送
     */
    //@Scheduled(fixedDelay = 20*60*1000)
    //@Transactional(rollbackFor = Exception.class)
    public void confirmSubscribe(){
        QueryWrapper<OfficialUser> officialWrapper = new QueryWrapper<>();
        officialWrapper.select("openid","nickname","sex","unionid");
        List<OfficialUser> officialUserList = officialUserService.list(officialWrapper);
        for (OfficialUser officialUser : officialUserList){
            String officialOpenid = officialUser.getOpenid();
            String nickname = officialUser.getNickname();
            String sex = officialUser.getSex();
            QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
            userQueryWrapper.select("openid")
                    .eq("nick_name",nickname)
                    .eq("gender",sex);
            List<User> userList = userService.list(userQueryWrapper);
            if (userList == null || userList.size() == 0){
                continue;
            }
            User user = userList.get(0);
            String userOpenid = user.getOpenid();
            if (user.getUnionId() == null || user.getUnionId().equals("")){
                String uuid;
                if (!officialUser.getUnionid().equals("")){
                    uuid = officialUser.getUnionid();
                }else {
                    uuid = UUID.randomUUID().toString();
                }
                UpdateWrapper<OfficialUser> officialWrapperTwo = new UpdateWrapper<>();
                officialWrapperTwo.set("unionid",uuid).eq("openid",officialOpenid);
                if (!officialUserService.update(officialWrapperTwo)){
                    CourseException courseException = new CourseException();
                    courseException.setMsg("设置official_uuid失败");
                    throw courseException;
                }
                UpdateWrapper<User> userUpdateWrapper = new UpdateWrapper<>();
                userUpdateWrapper.set("union_id",uuid)
                        .eq("openid",userOpenid);
                if (!userService.update(userUpdateWrapper)){
                    CourseException courseException = new CourseException();
                    courseException.setMsg("设置user_uuid失败");
                    throw courseException;
                }
                cache.invalidate(userOpenid);
            }
        }
    }
}
