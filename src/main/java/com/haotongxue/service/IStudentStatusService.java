package com.haotongxue.service;

import com.haotongxue.entity.StudentStatus;
import com.baomidou.mybatisplus.extension.service.IService;
import com.haotongxue.entity.vo.ESVO;
import com.haotongxue.entity.vo.ESWithHighLightVO;
import org.elasticsearch.search.SearchHit;

import java.io.IOException;
import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author DJT
 * @since 2022-01-19
 */
public interface IStudentStatusService extends IService<StudentStatus> {
    void prepareES();

    List<ESVO> getStudent(String grade, String collegeId, String majorId, String classId, Integer page) throws IOException;

    List<ESVO> getStudent(String[] nos) throws IOException;

    ESVO getStudent(String no) throws IOException;

    List<ESWithHighLightVO> getStudentByFuzzySearch(String content,Integer page) throws IOException;
}
