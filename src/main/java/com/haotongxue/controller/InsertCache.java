package com.haotongxue.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.haotongxue.entity.StudentStatus;
import com.haotongxue.entity.User;
import com.haotongxue.entity.dto.MemberDTO;
import com.haotongxue.mapper.StudentStatusMapper;
import com.haotongxue.mapper.UserMapper;
import com.haotongxue.service.IUserService;
import io.swagger.models.auth.In;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @Author CTC
 * @Description
 * @Date 2022/2/9
 */
@Service
public class InsertCache {

    @Autowired
    UserMapper userMapper;

    @Autowired
    IUserService userService;

    @Autowired
    StudentStatusMapper studentStatusMapper;

    @Autowired
    RedisTemplate redisTemplate;

    public static final int[] secArr = new int[]{1,3,6,8,10};

//    @Scheduled(fixedDelay = 100000000)
    public void insertAllInfo(){
        List<String> noList = new ArrayList<>();
        noList.add("202010244304");
        noList.add("202010244504");
        noList.add("202010244331");
        noList.add("202010244130");
        noList.add("202010244307");
        noList.add("202010244306");
        noList.add("202010244306");
        noList.add("202010244305");
        noList.add("202010244303");
        noList.add("202010244302");
//        noList.add("202010244301");
        for (String no : noList) {
            String[][] hasCourseArr = new String[5][7];
            QueryWrapper<User> queryWrapper = new QueryWrapper<>();
            queryWrapper.select("openid").eq("no",no).last("limit 1");
            String openid = userMapper.selectOne(queryWrapper).getOpenid();
            QueryWrapper<StudentStatus> queryWrapper1 = new QueryWrapper<>();
            queryWrapper1.select("name").eq("openid",openid).last("limit 1");
            String realName = studentStatusMapper.selectOne(queryWrapper1).getName();
            for (int n = 0; n < 5; n++) {
                for (int i = 1; i < 8; i++) {
                    List<Integer> hasCourseWeekList = userService.getHasCourseWeekList(no, i, secArr[n]);
                    String weekStr;
                    if(hasCourseWeekList.size() > 0){
                        weekStr = getWeekStr(hasCourseWeekList) + "有课";
                    }else {
                        weekStr = "无课";
                    }
                    hasCourseArr[n][i-1] = weekStr;
                }
            }
            MemberDTO memberDTO = new MemberDTO();
            memberDTO.setRealName(realName);
            memberDTO.setHasCourseArr(hasCourseArr);
            System.out.println(memberDTO);
            redisTemplate.opsForValue().set(no,memberDTO);
            redisTemplate.expire(no,1, TimeUnit.HOURS);
        }
    }
//        QueryWrapper<User> noWraaper = new QueryWrapper<>();
//        noWraaper.groupBy("no");
//        noWraaper.select("no");
//        List<User> users = userMapper.selectList(noWraaper);
//        for (User user: users) {
//            for (int n = 0; n < 5; n++) {
//                QueryWrapper<User> queryWrapper = new QueryWrapper<>();
//                queryWrapper.select("openid").eq("no",user.getNo()).last("limit 1");
//                String openid = userMapper.selectOne(queryWrapper).getOpenid();
//                QueryWrapper<StudentStatus> queryWrapper1 = new QueryWrapper<>();
//                queryWrapper1.select("name").eq("openid",openid).last("limit 1");
//                String realName = studentStatusMapper.selectOne(queryWrapper1).getName();
//                for (int i = 1; i < 8; i++) {
//                    List<Integer> hasCourseWeekList = userService.getHasCourseWeekList(user.getNo(), i, secArr[n]);
//                }
//            }
//        }
public static String getWeekStr(List<Integer> weekList){
    List<String> weekStrList = new ArrayList<>();
    int[] weekArr = new int[21];
    for (Integer week : weekList) {
        weekArr[week] = week;
    }
    int begin = 0,end = 0;
    for (int i = 1; i < weekArr.length; i++) {
        if(weekArr[i] == 0){
            continue;
        }
        if(weekArr[i-1] == 0){
            if(weekArr[i+1] == 0){
                weekStrList.add(i + "");
                weekArr[i] = 0;
            }else{
                begin = i;
            }
        }else{
            if(weekArr[i+1]==0){
                end = i;
            }
        }
        if(end > begin){
            weekStrList.add(begin + "-" + end);
            for (int j = begin; j <= end; j++) {
                weekArr[j] = 0;
            }
            end = 0;
            begin = 0;
        }
    }
    String sub1 = weekStrList.toString().substring(1);
    String sub2 = sub1.substring(0,sub1.length()-1);
    String sub3 = sub2.replace(" ","") + "周";
    return sub3;
}
}
