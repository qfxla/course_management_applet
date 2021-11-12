package com.haotongxue.service.impl;

import com.haotongxue.entity.InfoSection;
import com.haotongxue.entity.InfoWeek;
import com.haotongxue.exceptionhandler.CourseException;
import com.haotongxue.mapper.InfoSectionMapper;
import com.haotongxue.service.IInfoSectionService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author DJT
 * @since 2021-11-06
 */
@Service
public class InfoSectionServiceImpl extends ServiceImpl<InfoSectionMapper, InfoSection> implements IInfoSectionService {

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean insertInfoSection(String infoId, Integer sectionId) {
        InfoSection infoSection = new InfoSection();
        infoSection.setInfoId(infoId);
        infoSection.setSectionId(sectionId);
        infoSection.setId(UUID.randomUUID().toString());
        boolean flag = save(infoSection);
        if(flag){
            return true;
        }else{
            CourseException courseException = new CourseException();
            courseException.setCode(505);
            courseException.setMsg("插入对象到t_info_section失败。");
            return false;
        }
    }
}
