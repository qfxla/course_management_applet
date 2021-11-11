package com.haotongxue.service.impl;

import com.haotongxue.entity.InfoWeek;
import com.haotongxue.exceptionhandler.CourseException;
import com.haotongxue.mapper.InfoWeekMapper;
import com.haotongxue.service.IInfoWeekService;
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
public class InfoWeekServiceImpl extends ServiceImpl<InfoWeekMapper, InfoWeek> implements IInfoWeekService {

    @Override
    public boolean insertInfoWeek(String infoId, Integer weekId) {
        InfoWeek infoWeek = new InfoWeek();
        infoWeek.setInfoId(infoId);
        infoWeek.setWeekId(weekId);
        boolean flag = save(infoWeek);
        if(flag){
            return true;
        }else{
            CourseException courseException = new CourseException();
            courseException.setCode(505);
            courseException.setMsg("插入对象到t_info_week失败。");
            return false;
        }
    }
}
