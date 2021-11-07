package com.haotongxue.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.haotongxue.entity.Info;
import com.haotongxue.entity.vo.InfoVo;
import com.haotongxue.mapper.InfoMapper;
import com.haotongxue.service.ICourseService;
import com.haotongxue.service.IInfoService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.haotongxue.utils.UserContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author DJT
 * @since 2021-11-06
 */
@Service
public class InfoServiceImpl extends ServiceImpl<InfoMapper, Info> implements IInfoService {

    @Autowired
    private IInfoService iInfoService;

    @Autowired
    private ICourseService iCourseService;


    @Override
    public List<InfoVo> getInfo(int week) {
        //获取登录用户
        String openId = UserContext.getCurrentOpenid();
        QueryWrapper<Info> wrapper = new QueryWrapper<>();
        wrapper.eq("openid", wrapper);
//        iCourseService.
        //添加info关联的表信息



        return null;
    }
}
