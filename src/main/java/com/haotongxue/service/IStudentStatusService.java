package com.haotongxue.service;

import com.haotongxue.entity.StudentStatus;
import com.baomidou.mybatisplus.extension.service.IService;
import com.haotongxue.entity.vo.ESVO;
import com.haotongxue.entity.vo.ESWithHighLightVO;
import com.haotongxue.entity.vo.IsConcernVO;
import com.haotongxue.entity.vo.StudentVOTwo;
import org.elasticsearch.action.search.SearchResponse;
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

    List<IsConcernVO> getStudent(String grade, String collegeId, String majorId, String classId, Integer page, String no) throws IOException;

    List<ESVO> getStudent(String[] nos) throws IOException;

    ESVO getStudent(String no) throws IOException;

    List<IsConcernVO> getStudentByFuzzySearch(String content, Integer page, String no) throws IOException;

    void addStudentToES(StudentVOTwo studentVOTwo);

    SearchResponse getStudentRes(String no) throws IOException;

    void deleteStudentToES(String no) throws Exception;
}
