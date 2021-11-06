package com.haotongxue.course_service.service.impl;

import com.haotongxue.course_service.entity.Info;
import com.haotongxue.course_service.mapper.InfoMapper;
import com.haotongxue.course_service.service.IInfoService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

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

}
