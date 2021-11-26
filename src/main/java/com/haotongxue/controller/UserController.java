package com.haotongxue.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.gargoylesoftware.htmlunit.*;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.github.benmanes.caffeine.cache.LoadingCache;
import com.haotongxue.entity.*;
import com.haotongxue.entity.dto.PushSettingDTO;
import com.haotongxue.entity.dto.WeChatLoginDTO;
import com.haotongxue.handler.ReptileHandler;
import com.haotongxue.handler.WatchIsPaingHandler;
import com.haotongxue.mapper.*;
import com.haotongxue.runnable.ReReptileRunnable;
import com.haotongxue.runnable.ReptileRunnable;
import com.haotongxue.service.*;
import com.haotongxue.utils.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author DJT
 * @since 2021-11-06
 */
@Api(tags = "用户管理")
@RestController
@RequestMapping("/user")
@Slf4j
public class UserController {

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
    @Autowired
    private UserMapper userMapper;

    @ApiOperation(value = "微信登录")
    @PostMapping("/login")
    public R login(@RequestBody WeChatLoginDTO loginDTO) throws IOException {
        WeChatLoginResponse loginResponse = userService.getLoginResponse(loginDTO.getCode());
        String openid = loginResponse.getOpenid();
        String unionid = loginResponse.getUnionid();
        UserContext.add(openid);
        //用来标记是否是快捷登录
        boolean isQuickLogin = false;
        //是否让用户重新刷新他的个人信息
        boolean isRefreshInfo = false;
        if (loginDTO.getPassword() == null && loginDTO.getNo() == null){
            isQuickLogin = true;
            if (!cache.asMap().containsKey(openid)){
                //每三天用户信息就要更新一次，起码确保头像是最新的
                isRefreshInfo = true;
            }
        }
        User user = (User) cache.get(openid);
        boolean isDoPa = true; //是否执行学校系统登录验证
        WebClient webClient = null;
        if (user == null){
            //快捷登录失败
            if (isQuickLogin){
                return R.error().code(ResultCode.QUICK_LOGIN_ERROR);
            }

            webClient = WebClientUtils.getWebClient();
            HtmlPage afterLogin = LoginUtils.login(webClient, loginDTO.getNo(), loginDTO.getPassword());

            if (afterLogin == null){
                return R.error().code(ResultCode.NO_OR_PASSWORD_ERROR);
            }
            user = new User();
            BeanUtils.copyProperties(loginDTO,user);
            user.setOpenid(openid);
            //记得在爬虫完以后要设置这条数据失效
            user.setIsPa(0);
            user.setIsPaing(0);

            //设置unionId
            user.setUnionId(unionid);

            userService.save(user);
            user.setSubscribe(1);
            user.setUnionId("");
            cache.put(openid,user);
        }else {
            //如果为0，则爬虫还没执行成功
            isDoPa = user.getIsPa() == 0;
            if (!user.getUnionId().equals(unionid)){
                isRefreshInfo = true;
            }
        }
        if (isDoPa && user.getIsPaing() == 0){
            log.info(openid + "开始爬虫");
            ReReptileRunnable reReptileRunnable = new ReReptileRunnable(webClient,user.getNo(),user.getPassword(),UserContext.getCurrentOpenid());
            watchIsPaingHandler.watchIsPa(reReptileRunnable);
            //////////////////////////////////////////////////////////////////////////////////////////////////////////////
            log.info(openid + "正常爬虫");
            ReptileRunnable reptileRunnable = new ReptileRunnable(webClient,user.getNo(),user.getPassword(),UserContext.getCurrentOpenid(),reReptileRunnable);
            reptileHandler.pa(reptileRunnable);
        }
        String token = JwtUtils.generate(openid);
        boolean isHaiZhu = StuNumUtils.isHaiZhu(user.getNo());
        if (isRefreshInfo){

            //设置unionId
            user.setUnionId(unionid);
            userService.updateById(user);

            return R.ok().code(ResultCode.NEED_REFRESH_INFO)
                    .data("Authority",token)
                    .data("subscribe",user.getSubscribe() == 1)
                    .data("isConcern",!user.getUnionId().equals(""))
                    .data("isHaiZhu",isHaiZhu);
        }
        return R.ok()
                .data("Authority",token)
                .data("openid",openid)
                .data("subscribe",user.getSubscribe() == 1)
                .data("isConcern",!user.getUnionId().equals(""))
                .data("avatarUrl",user.getAvatarUrl())
                .data("nickName",user.getNickName())
                .data("gender",user.getGender())
                .data("isHaiZhu",isHaiZhu);
    }

    @ApiOperation("修改用户密码")
    @PostMapping("/authority")
    public R editPassword(@RequestBody PasswordEditEntity passwordEditEntity) throws IOException {
        String no = passwordEditEntity.getNo();
        String password = passwordEditEntity.getPassword();
        if (StringUtils.isEmpty(no) || StringUtils.isEmpty(password)){
            return R.error();
        }
        //验证账号和密码对不对
        WebClient webClient = WebClientUtils.getWebClient();
        HtmlPage afterLogin = LoginUtils.login(webClient, no, password);
        if (afterLogin == null){
            return R.error().code(ResultCode.NO_OR_PASSWORD_ERROR);
        }
        String currentOpenid = UserContext.getCurrentOpenid();
        //使缓存失效
        cache.invalidate(currentOpenid);
        UpdateWrapper<User> userUpdateWrapper = new UpdateWrapper<>();
        userUpdateWrapper.set("no",no).set("password",password).eq("openid",currentOpenid);
        userService.update(userUpdateWrapper);
        return R.ok();
    }

    @ApiOperation("查看用户推送设置")
    @GetMapping("/authority")
    public R isSubscribe(){
        String currentOpenid = UserContext.getCurrentOpenid();
        User user = (User) cache.get(currentOpenid);
        if (user == null){
            return R.error();
        }
        return R.ok().data("isSubscribe",user.getSubscribe());
    }

    @ApiOperation("查看用户是否关注了公众号")
    @GetMapping("/authority/confirmOfficial")
    public R confirmConcern(){
        String currentOpenid = UserContext.getCurrentOpenid();
        User user = (User) cache.get(currentOpenid);
        return user.getUnionId().equals("") ? R.error() : R.ok();
    }

    @ApiOperation("重新设置信息推送规则")
    @PostMapping("/authority/setting")
    public R pushSetting(@RequestBody PushSettingDTO pushSettingDTO){
        String currentOpenid = UserContext.getCurrentOpenid();
        cache.invalidate(currentOpenid);
        UpdateWrapper<User> userUpdateWrapper = new UpdateWrapper<>();
        userUpdateWrapper.set("subscribe",pushSettingDTO.getSubscribe());
        userUpdateWrapper.eq("openid",currentOpenid);
        userService.update(userUpdateWrapper);
        return R.ok();
    }

    @ApiOperation("获取用户的学号")
    @GetMapping("/authority/no")
    public R getNo(@RequestHeader("Authority") @ApiParam("放在请求头的token") String authority){
        String currentOpenid = UserContext.getCurrentOpenid();
        User user = (User) cache.get(currentOpenid);
        String no = user.getNo();
        return R.ok().data("no",no);
    }

    @ApiOperation("删除某个人的数据及缓存")
    @GetMapping("/deleteLoginCache")
    public R deleteLoginCache(@RequestParam("openid")String openid){
        User user = userService.getById(openid);
        if (user == null){
            return R.error().data("msg","无该openid");
        }

        String openId = user.getOpenid();
        QueryWrapper<UserInfo> wrapper = new QueryWrapper<>();
        wrapper.eq("openid",openId);
        List<String> infoList = iUserInfoService.list(wrapper).stream().map(UserInfo::getInfoId).collect(Collectors.toList());
        userMapper.deleteByInfoId(openid);
        cache.invalidate(openid);
        for (int i = 1;i <= 20;i++){
            courseCache.invalidate("cour" + openId + ":" + i);
        }
        log.info("查找用户的所有info数量"+ infoList.size());
        if(infoList.size() != 0){
            infoSectionMapper.deleteByInfoId(infoList);
            infoWeekMapper.deleteByInfoId(infoList);
            infoCourseMapper.deleteByInfoId(infoList);
            infoClassroomMapper.deleteByInfoId(infoList);
            infoTeacherMapper.deleteByInfoId(infoList);
            infoMapper.deleteByInfoId(infoList);
            userInfoMapper.deleteByInfoId(infoList);
        }
        return R.ok().data("msg","清除成功");
    }

}

