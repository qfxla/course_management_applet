package com.haotongxue.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.benmanes.caffeine.cache.LoadingCache;
import com.haotongxue.entity.User;
import com.haotongxue.entity.UserInfo;
import com.haotongxue.entity.WeChatLoginResponse;
import com.haotongxue.exceptionhandler.CourseException;
import com.haotongxue.handler.ReptileHandler;
import com.haotongxue.handler.WatchIsPaingHandler;
import com.haotongxue.mapper.*;
import com.haotongxue.service.EduLoginService;
import com.haotongxue.service.IUserInfoService;
import com.haotongxue.service.IUserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.haotongxue.utils.R;
import com.haotongxue.utils.WeChatUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author DJT
 * @since 2021-11-06
 */
@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {

    @Autowired
    WeChatUtil weChatUtil;

    @Autowired
    RestTemplate restTemplate;

    @Autowired
    UserMapper userMapper;

    @Autowired
    IUserService userService;

    @Resource(name = "loginCache")
    LoadingCache<String,Object> cache;
    @Resource(name = "courseCache")
    LoadingCache<String,Object> courseCache;

    @Autowired
    EduLoginService eduLoginService;

    @Autowired
    ReptileHandler reptileHandler;

    @Autowired
    WatchIsPaingHandler watchIsPaingHandler;

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
    private InfoMapper infoMapper;
    @Autowired
    private IUserInfoService iUserInfoService;

    @Override
    public WeChatLoginResponse getLoginResponse(String code) {
        String url = "https://api.weixin.qq.com/sns/jscode2session?appid="+weChatUtil.getAppid()+"" +
                "&secret="+weChatUtil.getSecret()+"" +
                "&js_code="+code+"" +
                "&grant_type=authorization_code";
        String response = restTemplate.getForObject(url,String.class);
        WeChatLoginResponse loginResponse = JSON.parseObject(response, WeChatLoginResponse.class);
        Integer errcode = loginResponse.getErrcode();
        if (errcode != null && errcode != 0){
            CourseException courseException = new CourseException();
            courseException.setCode(20001);
            if (errcode == -1){
                courseException.setMsg("系统繁忙");
            }else if (errcode == 40029){
                courseException.setMsg("code无效");
            }else if (errcode == 45011){
                courseException.setMsg("频率限制，每个用户每分钟100次");
            }else if (errcode == 40226){
                courseException.setMsg("高风险等级用户，小程序登录拦截");
            }else {
                courseException.setMsg("code有误");
            }
            throw courseException;
        }
        return loginResponse;
    }

    @Override
    public R deleteAllZeroPa() {
        List<String> openIdList = userMapper.selectZeroPa();
        log.info("删除全部0课且isPa为1的用户");
        for (String openId : openIdList) {
            userMapper.deleteByInfoId(openId);
            cache.invalidate(openId);
            for (int i = 1;i <= 20;i++){
                courseCache.invalidate("cour" + openId + ":" + i);
            }
            QueryWrapper<UserInfo> wrapper = new QueryWrapper<>();
            wrapper.eq("openid",openId);
            List<String> infoList = iUserInfoService.list(wrapper).stream().map(UserInfo::getInfoId).collect(Collectors.toList());
            if(infoList.size() != 0){
                infoSectionMapper.deleteByInfoId(infoList);
                infoWeekMapper.deleteByInfoId(infoList);
                infoCourseMapper.deleteByInfoId(infoList);
                infoClassroomMapper.deleteByInfoId(infoList);
                infoTeacherMapper.deleteByInfoId(infoList);
                infoMapper.deleteByInfoId(infoList);
                userInfoMapper.deleteByInfoId(infoList);
            }
        }
        return R.ok().data("msg","清除成功，共" + openIdList.size() + "个");
    }

    @Override
    public R deleteAllPaing() {
        List<String> openIdList = userMapper.selectPaing();
        log.info("删除全部paing");
        for (String openId : openIdList) {
            userMapper.deleteByInfoId(openId);
            cache.invalidate(openId);
            for (int i = 1;i <= 20;i++){
                courseCache.invalidate("cour" + openId + ":" + i);
            }
            QueryWrapper<UserInfo> wrapper = new QueryWrapper<>();
            wrapper.eq("openid",openId);
            List<String> infoList = iUserInfoService.list(wrapper).stream().map(UserInfo::getInfoId).collect(Collectors.toList());
            if(infoList.size() != 0){
                infoSectionMapper.deleteByInfoId(infoList);
                infoWeekMapper.deleteByInfoId(infoList);
                infoCourseMapper.deleteByInfoId(infoList);
                infoClassroomMapper.deleteByInfoId(infoList);
                infoTeacherMapper.deleteByInfoId(infoList);
                infoMapper.deleteByInfoId(infoList);
                userInfoMapper.deleteByInfoId(infoList);
            }
        }
        return R.ok().data("msg","清除成功，共" + openIdList.size() + "个");
    }
}
