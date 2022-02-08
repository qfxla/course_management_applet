package com.haotongxue.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.haotongxue.entity.StudentStatus;
import com.haotongxue.entity.User;
import com.haotongxue.mapper.StudentStatusMapper;
import com.haotongxue.mapper.UserMapper;
import com.haotongxue.service.IUserService;
import com.haotongxue.utils.R;
import io.swagger.models.auth.In;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @Author CTC
 * @Description
 * @Date 2022/2/8
 */
@RestController
@RequestMapping("/timeOff")
public class GetTimeOffController {

    @Autowired
    UserMapper userMapper;

    @Autowired
    IUserService userService;

    @Autowired
    StudentStatusMapper studentStatusMapper;

    @GetMapping("/getTimeOff")
    public R getTimeOff(@RequestParam("noList") List<String> noList){
        int[] secArr = new int[]{1,4,6,8,10};
        for (String no : noList) {
            QueryWrapper<User> queryWrapper = new QueryWrapper<>();
            queryWrapper.select("openid").eq("no",no).last("limit 1");
            String openid = userMapper.selectOne(queryWrapper).getOpenid();
            QueryWrapper<StudentStatus> queryWrapper1 = new QueryWrapper<>();
            queryWrapper1.select("name").eq("openid",openid).last("limit 1");
            String realName = studentStatusMapper.selectOne(queryWrapper1).getName();
            for (int i = 1; i < 8; i++) {
                for (int j = 0; j < 5; j++) {
                    List<Integer> hasCourseWeekList = userService.getHasCourseWeekList(no, i, secArr[j]);
                    if (hasCourseWeekList != null && hasCourseWeekList.size() > 0){
                        String weekStr = getWeekStr(hasCourseWeekList);
                        String str = realName + "：" + weekStr + "有课";
                        System.out.println(str);
                    }else {
                        //当前星期的当前节次没有课
                        String str = realName + "：" + "无课";
                        System.out.println(str);
                    }
                }
            }
        }
        return R.ok();
    }

    public static String getWeekStr(List<Integer> weekList){
        System.out.println("周次集合。。。。");
        System.out.println(weekList);
        List<String> weekStrList = new ArrayList<>();
        int[] weekArr = new int[21];
        for (Integer week : weekList) {
            weekArr[week] = week;
        }
        System.out.println(Arrays.toString(weekArr));
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
        System.out.println(sub3);
        return sub3;
    }
}
