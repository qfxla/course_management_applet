package com.haotongxue.mapper;

import com.haotongxue.entity.Selected;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.haotongxue.entity.vo.SelectedRuleVo;
import com.haotongxue.entity.vo.SelectedVo;
import com.haotongxue.entity.vo.SmallKindVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;

import java.sql.ParameterMetaData;
import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author DJT
 * @since 2021-12-06
 */
@Mapper
public interface SelectedMapper extends BaseMapper<Selected> {
    List<SelectedVo> myChoice(@Param("openid")String openid);

    List<SelectedRuleVo> rule(@Param("collegeId")int collegeId);

    List<SmallKindVo> ruleSmallKind(@Param("collegeId") int collegeId, @Param("bigId")int bigId);
}
