package com.haotongxue.service;

import com.haotongxue.entity.StudentStatus;
import com.baomidou.mybatisplus.extension.service.IService;
import org.elasticsearch.search.SearchHit;

import java.io.IOException;

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

    SearchHit[] getStudent(String grade, String collegeId, String majorId, String classId) throws IOException;

    SearchHit[] getStudentByFuzzySearch(String content) throws IOException;
}
