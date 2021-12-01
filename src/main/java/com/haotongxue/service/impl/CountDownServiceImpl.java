package com.haotongxue.service.impl;

import com.haotongxue.entity.CountDown;
import com.haotongxue.mapper.CountDownMapper;
import com.haotongxue.service.ICountDownService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author DJT
 * @since 2021-12-01
 */
@Service
public class CountDownServiceImpl extends ServiceImpl<CountDownMapper, CountDown> implements ICountDownService {

}
