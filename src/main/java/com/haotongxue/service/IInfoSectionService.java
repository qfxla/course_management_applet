package com.haotongxue.service;

import com.haotongxue.entity.InfoSection;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author DJT
 * @since 2021-11-06
 */
public interface IInfoSectionService extends IService<InfoSection> {
    boolean insertInfoSection(String infoId,Integer sectionId);
}
