package com.haotongxue.quartz;

import com.haotongxue.entity.FailedUser;
import com.haotongxue.entity.User;
import com.haotongxue.mapper.FailedUserMapper;
import com.haotongxue.mapper.UserMapper;
import com.haotongxue.utils.HttpUtil;
import lombok.extern.slf4j.Slf4j;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import javax.annotation.Resource;
import java.util.List;

/**
 * @Description TODO
 * @date 2021/12/10 15:40
 */

@Slf4j
public class FailedPaJob implements Job {

    @Resource
    FailedUserMapper failedUserMapper;


    @Resource
    UserMapper userMapper;
    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        log.info("清除failedUser开始了");
        String deleteUrl = "https://course-1383871-1308162715.ap-shanghai.run.tcloudbase.com/user/deleteLoginCache?openid=";
        List<User> failedUserList = userMapper.selectPaing();
        for (User user : failedUserList) {
            FailedUser failedUser = new FailedUser();
            failedUser.setOpenid(user.getOpenid());
            failedUser.setNickName(user.getNickName());
            failedUser.setNo(user.getNo());
            failedUser.setPassword(user.getPassword());
            failedUserMapper.insert(failedUser);
            String res = HttpUtil.sendGetRequest(deleteUrl + user.getOpenid());
            log.info(user.getOpenid() + "删除了用户的缓存及所有数据");
        }
    }
}
