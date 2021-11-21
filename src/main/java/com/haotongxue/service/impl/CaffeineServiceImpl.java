package com.haotongxue.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.github.benmanes.caffeine.cache.LoadingCache;
import com.haotongxue.entity.User;
import com.haotongxue.entity.UserInfo;
import com.haotongxue.handler.ReptileHandler;
import com.haotongxue.mapper.*;
import com.haotongxue.service.*;
import com.haotongxue.utils.LoginUtils;
import com.haotongxue.utils.UserContext;
import com.haotongxue.utils.WebClientUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author zcj
 * @creat 2021-11-21-22:46
 */
@Component
public class CaffeineServiceImpl {
    @Autowired
    private InfoMapper infoMapper;

    @Resource(name = "courseInfo")
    LoadingCache<String,Object> cache;
    @Resource(name = "courseCache")
    LoadingCache<String,Object> courseCache;
    @Autowired
    private IInfoCourseService iInfoCourseService;
    @Autowired
    private IInfoClassroomService iInfoClassroomService;
    @Autowired
    private IInfoTeacherService iInfoTeacherService;
    @Resource(name = "weekCache")
    LoadingCache<String,Object> weekCache;
    @Autowired
    private InfoSectionMapper infoSectionMapper;
    @Autowired
    private InfoWeekMapper infoWeekMapper;
    @Autowired
    private InfoCourseMapper infoCourseMapper;
    @Autowired
    private InfoClassroomMapper infoClassroomMapper;
    @Autowired
    private InfoTeacherMapper infoTeacherMapper;
    @Autowired
    private UserInfoMapper userInfoMapper;
    @Autowired
    private IInfoService iInfoService;
    @Autowired
    private IUserInfoService iUserInfoService;
    @Autowired
    private ReptileService reptileService;
    @Autowired
    private ReptileHandler reptileHandler;
    @Resource(name = "loginCache")
    LoadingCache<String,Object> loginCache;
    @Autowired
    private IUserService iUserService;


    @Transactional(rollbackFor = Exception.class)
    public boolean updateCourseData() throws IOException {
        String openId = UserContext.getCurrentOpenid();
        UpdateWrapper<User> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("openid",openId).set("is_pa",0);
        iUserService.update(updateWrapper);
        //查找当前用户的所有info
        QueryWrapper<UserInfo> wrapper = new QueryWrapper<>();
        wrapper.eq("openid",openId);
        List<String> infoList = iUserInfoService.list(wrapper).stream().map(UserInfo::getInfoId).collect(Collectors.toList());


        int i1 = 0,i2 =0,i3 = 0,i4 = 0,i5 = 0,i6 = 0,i7 = 0;
        i1 = infoSectionMapper.deleteByInfoId(infoList);
        i2 = infoWeekMapper.deleteByInfoId(infoList);
        i3 = infoCourseMapper.deleteByInfoId(infoList);
        i4 = infoClassroomMapper.deleteByInfoId(infoList);
        i5 = infoTeacherMapper.deleteByInfoId(infoList);
        i6 = infoMapper.deleteByInfoId(infoList);
        i7 = userInfoMapper.deleteByInfoId(infoList);

        //删除成功，开始爬
        if (i1 > 0 && i2 > 0 && i3 > 0 && i4 > 0 && i5 > 0 && i6 > 0 && i7 > 0){
            WebClient webClient = WebClientUtils.getWebClient();
            User user = (User)loginCache.get(openId);
            HtmlPage afterLogin = LoginUtils.login(webClient, user.getNo(), user.getPassword());
            reptileHandler.pa(webClient,user.getNo(),user.getPassword());

            //删除缓存
            for (int i = 1;i <= 20;i++){
                courseCache.invalidate("cour" + openId + ":" + i);
            }
            return true;
        }
        return false;
    }
}
