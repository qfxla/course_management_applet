package com.haotongxue.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.haotongxue.entity.Info;
import com.haotongxue.entity.InfoTeacher;
import com.haotongxue.entity.UserInfo;
import com.haotongxue.exceptionhandler.CourseException;
import com.haotongxue.mapper.UserInfoMapper;
import com.haotongxue.service.IUserInfoService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author DJT
 * @since 2021-11-07
 */
@Service
public class UserInfoServiceImpl extends ServiceImpl<UserInfoMapper, UserInfo> implements IUserInfoService {

    @Override
    public boolean insertUserInfo(String openId, String infoId) {
        UserInfo userInfo = new UserInfo();
        userInfo.setOpenid(openId);
        userInfo.setInfoId(infoId);
        boolean flag = save(userInfo);
        if(flag){
            return true;
        }else{
            CourseException courseException = new CourseException();
            courseException.setCode(505);
            courseException.setMsg("插入对象到t_user_info失败。");
            return false;
        }
    }
}
