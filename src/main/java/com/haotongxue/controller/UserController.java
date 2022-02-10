package com.haotongxue.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.gargoylesoftware.htmlunit.*;
import com.haotongxue.cacheUtil.LoadingRedisCache;
import com.haotongxue.entity.*;
import com.haotongxue.entity.dto.PushSettingDTO;
import com.haotongxue.entity.dto.WeChatLoginDTO;
import com.haotongxue.exceptionhandler.CourseException;
import com.haotongxue.handler.ReptileHandler;
import com.haotongxue.handler.WatchIsPaingHandler;
import com.haotongxue.mapper.*;
import com.haotongxue.openfeign.RemoteReptileCalling;
import com.haotongxue.service.*;
import com.haotongxue.utils.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.ArrayList;
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
@RequestMapping("/zkCourse/user")
@Slf4j
public class UserController {

    @Autowired
    IUserService userService;

    @Resource(name = "loginCache")
    LoadingRedisCache cache;

    @Resource(name = "courseCache")
    LoadingRedisCache courseCache;

    @Resource(name = "studentStatusCache")
    LoadingRedisCache<StudentStatus> studentStatusCache;

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
    @Autowired
    private CountDownMapper countDownMapper;
    @Autowired
    private UserSelectedMapper userSelectedMapper;

    @Autowired
    private IOfficialUserService officialUserService;

    @Autowired
    private IStudentStatusService studentStatusService;

    @Autowired
    RemoteReptileCalling remoteReptileCalling;

    @Autowired
    private GradeService gradeService;


    @ApiOperation(value = "微信登录")
    @PostMapping("/login")
    public R login(@RequestBody WeChatLoginDTO loginDTO) {
        WeChatLoginResponse loginResponse = userService.getLoginResponse(loginDTO.getCode());
        String openid = loginResponse.getOpenid();
        String unionid = loginResponse.getUnionid();
        UserContext.add(openid);
        //用来标记是否是快捷登录
        boolean isQuickLogin = false;
        //是否让用户重新刷新他的个人信息
        //boolean isRefreshInfo = false;
        if (loginDTO.getPassword() == null && loginDTO.getNo() == null){
            isQuickLogin = true;
        }
        User user = (User) cache.get(openid);
        boolean isDoPa = false; //是否执行学校系统登录验证
        WebClient webClient = null;
        if (user == null){
            isDoPa =  true;
            //快捷登录失败
            if (isQuickLogin){
                return R.error().code(ResultCode.QUICK_LOGIN_ERROR);
            }
            webClient = WebClientUtils.getWebClient();
            try {
                LoginUtils.login(webClient, loginDTO.getNo(), loginDTO.getPassword());

            }catch (Exception e){
                e.printStackTrace();
                if (e instanceof CourseException){
                    CourseException courseException = (CourseException) e;
                    if (courseException.getCode().equals(400)){
                        return R.error().code(ResultCode.NO_OR_PASSWORD_ERROR);
                    }
                }
                return R.error().code(ResultCode.EDU_PROBLEM);
            }finally {
                webClient.close();
            }
            user = new User();
            BeanUtils.copyProperties(loginDTO,user);
            user.setOpenid(openid);
            //记得在爬虫完以后要设置这条数据失效
            user.setIsPa(0);
            user.setIsPaing(0);
            if (StuNumUtils.isHaiZhu(user.getNo())){
                user.setIsHaizhu(1);
            }else {
                user.setIsHaizhu(2);
            }
            //设置unionId
            user.setUnionId(unionid);
            user.setSubscribe(1);
            userService.save(user);
            cache.put(openid,user);
        }
        else {
            //如果为0，则爬虫还没执行成功
            //isDoPa = user.getIsPa() == 0;
            if (!user.getUnionId().equals(unionid)){
                user.setUnionId(unionid);
                userService.updateById(user);
            }
        }
        if (isDoPa && user.getIsPaing() == 0){
            //调用远程爬的接口
            remoteReptileCalling.reptileAll(openid);
        }
        boolean isHaiZhu = user.getIsHaizhu().equals(1);
        String token = JwtUtils.generate(openid);
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
    public R editPassword(@RequestBody PasswordEditEntity passwordEditEntity) {
        String no = passwordEditEntity.getNo();
        String password = passwordEditEntity.getPassword();
        if (StringUtils.isEmpty(no) || StringUtils.isEmpty(password)){
            return R.error();
        }
        //验证账号和密码对不对
        WebClient webClient = WebClientUtils.getWebClient();
        try {
            LoginUtils.login(webClient, no, password);
        } catch (Exception e) {
            //e.printStackTrace();
            if (e instanceof CourseException){
                CourseException courseException = (CourseException) e;
                if (courseException.getCode().equals(400)){
                    return R.error().code(ResultCode.NO_OR_PASSWORD_ERROR);
                }else {
                    return R.error().code(ResultCode.EDU_PROBLEM);
                }
            }
        }finally {
            webClient.close();
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
        QueryWrapper<OfficialUser> officialUserQueryWrapper = new QueryWrapper<>();
        officialUserQueryWrapper.eq("unionid",user.getUnionId());
        int count = officialUserService.count(officialUserQueryWrapper);
        if (count == 1 && user.getSubscribe().equals(1)){
            return R.ok();
        }else {
            return R.error();
        }
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
    public R deleteLoginCache(@RequestParam(value = "openid",defaultValue = "",required = false)String openid){
        if (openid.equals("")){
            openid = UserContext.getCurrentOpenid();
        }
        User user = userService.getById(openid);
        if (user == null){
            return R.error().data("msg","无该openid");
        }
        String openId = user.getOpenid();
        userMapper.deleteByInfoId(openid);
        cache.invalidate(openid);
        for (int i = 1;i <= 20;i++){
            courseCache.invalidate("cour" + openId + ":" + i);
        }
        QueryWrapper<UserInfo> wrapper = new QueryWrapper<>();
        wrapper.eq("openid",openId);
        List<String> infoList = iUserInfoService.list(wrapper).stream().map(UserInfo::getInfoId).collect(Collectors.toList());

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

//    @ApiOperation("删除所有人的登录缓存")
//    @GetMapping("/deleteAllLoginCache")
//    public R deleteAllLoginCache(){
//        ConcurrentMap<@NonNull String, @NonNull Object> map = cache.asMap();
//        Set<Map.Entry<@NonNull String, @NonNull Object>> set = map.entrySet();
//        for (Map.Entry<String, Object> entry : set) {
//            cache.invalidate(entry.getKey());
//        }
//
//        return R.ok();
//    }

    @ApiOperation("只删某个人的登录缓存")
    @GetMapping("/authority/justDeleteCache")
    public R justDeleteCache(){
        String openid = UserContext.getCurrentOpenid();
        User user = userService.getById(openid);
        if (user == null){
            return R.error().data("msg","无该openid");
        }
        cache.invalidate(openid);
        return R.ok().data("msg","删除成功");
    }


    @ApiOperation("检测密码是否正确")
    @PostMapping("/passwordCheck/authority")
    public R passwordCheck(){
        String currentOpenid = UserContext.getCurrentOpenid();
        User user = (User) cache.get(currentOpenid);
        if (user == null || user.getIsPaing().equals(1)){
            return R.error();
        }
        try (WebClient webClient = WebClientUtils.getWebClient()) {
            LoginUtils.login(webClient, user.getNo(), user.getPassword());
        } catch (Exception e) {
            //e.printStackTrace();
            if (e instanceof CourseException) {
                CourseException courseException = (CourseException) e;
                if (courseException.getCode().equals(400)) {
                    return R.error().code(ResultCode.NO_OR_PASSWORD_ERROR);
                } else {
                    return R.error().code(ResultCode.EDU_PROBLEM);
                }
            }
        }
        return R.ok();
    }

    @ApiOperation("删除某个人的数据，包括选课和倒计时")
    @DeleteMapping("/delSomeOneData")
    public R delSomeOneData(@RequestParam("openid")String openid){
        cache.invalidate(String.valueOf(openid));
        User user = userService.getById(openid);
        if (user == null){
            return R.error().data("msg","无该openid");
        }
        String openId = user.getOpenid();
        for (int i = 1;i <= 20;i++){
            courseCache.invalidate("cour" + openId + ":" + i);
        }
        QueryWrapper<UserInfo> wrapper = new QueryWrapper<>();
        wrapper.eq("openid",openId);
        List<String> infoList = iUserInfoService.list(wrapper).stream().map(UserInfo::getInfoId).collect(Collectors.toList());

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


        countDownMapper.deleteByOpenId(openId);
        userSelectedMapper.deleteByOpenId(openId);

        userMapper.deleteByInfoId(openid);

        StudentStatus studentStatus = studentStatusCache.get(openid);
        try {
            studentStatusService.deleteStudentToES(studentStatus.getNo());
        } catch (Exception e) {
            e.printStackTrace();
            log.info("删除ES的student失败了-->"+openId);
        }

        return R.ok().data("msg","清除成功");
    }

    @ApiOperation("学生评教")
    @PostMapping("/authority/evaluate")
    public R studentEvaluate(@RequestHeader("Authority") @ApiParam("放在请求头的token") String authority){
        String currentOpenid = UserContext.getCurrentOpenid();
        User user = (User) cache.get(currentOpenid);
//        String no = "202010244304";
//        String password = "Ctc779684470...";
        String no = user.getNo();
        String password = user.getPassword();
        for (int i=0;i<20;i++){
            try (WebClient webClient = WebClientUtils.getWebClient()) {
                LoginUtils.login(webClient, no, password);
                if (userService.studentEvaluate(webClient)) {
                    return R.ok();
                }
            } catch (Exception e) {
                e.printStackTrace();
                continue;
            }
            break;
        }
        return R.error();
    }


    @ApiOperation("删除所有Paing，返回所有nickname")
    @GetMapping("/delAllPaing")
    public List<String> delAllPaing(){
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        QueryWrapper<User> paingUsers = queryWrapper.eq("is_paing", 1);
        List<String> nickNames = new ArrayList<>();
        List<User> userList = userMapper.selectList(paingUsers);
        System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@paing数：" + userList.size());
        for (User user : userList) {
            nickNames.add(user.getNickName());
            nickNames.add(user.getNo());
            String openId = user.getOpenid();
            System.out.println("删除所有缓存和数据-----" + openId);
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
            countDownMapper.deleteByOpenId(openId);
            userSelectedMapper.deleteByOpenId(openId);
            userMapper.deleteByInfoId(openId);
        }
        if(nickNames.size()!=0){
            return nickNames;
        }else {
            nickNames.add("没有paing");
            return nickNames;
        }
    }

//    @PostMapping("/insertGrade")
//    public int insertGrade(){
//        return gradeService.paGrade();
//    }
}

