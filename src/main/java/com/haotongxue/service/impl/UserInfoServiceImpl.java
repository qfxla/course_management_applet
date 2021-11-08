package com.haotongxue.service.impl;

import com.haotongxue.entity.Info;
import com.haotongxue.entity.UserInfo;
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

}
