package com.haotongxue.service.impl;

import com.haotongxue.cacheUtil.LoadingRedisCache;
import com.haotongxue.entity.CoursePlus;
import com.haotongxue.mapper.CoursePlusMapper;
import com.haotongxue.service.ICoursePlusService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author DJT
 * @since 2022-02-18
 */
@Service
public class CoursePlusServiceImpl extends ServiceImpl<CoursePlusMapper, CoursePlus> implements ICoursePlusService {

}
