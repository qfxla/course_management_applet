package com.haotongxue.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.haotongxue.cacheUtil.LoadingRedisCache;
import com.haotongxue.entity.Class;
import com.haotongxue.entity.StudentStatus;
import com.haotongxue.entity.vo.ESVO;
import com.haotongxue.entity.vo.IsConcernVO;
import com.haotongxue.entity.vo.StudentVOTwo;
import com.haotongxue.mapper.StudentStatusMapper;
import com.haotongxue.service.IStudentStatusService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.haotongxue.utils.ESUtils;
import com.haotongxue.utils.GradeUtils;
import com.haotongxue.utils.UserContext;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.ActionListener;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.*;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author DJT
 * @since 2022-01-19
 */
@Slf4j
@Service
public class StudentStatusServiceImpl extends ServiceImpl<StudentStatusMapper, StudentStatus> implements IStudentStatusService {


    @Resource(name = "classCache")
    LoadingRedisCache<Class> classCache;

    public int count = 0;

    @Autowired
    private RestHighLevelClient client;

    @Autowired
    private ESUtils esUtils;

    @Override
    public void prepareES() {
        QueryWrapper<StudentStatus> studentStatusQueryWrapper = new QueryWrapper<>();
        studentStatusQueryWrapper.select("college_id", "class_id", "major_id", "name", "sex", "no");
        studentStatusQueryWrapper.groupBy(Arrays.asList("college_id", "class_id", "major_id", "name", "sex", "no"));
        List<StudentStatus> list = list(studentStatusQueryWrapper);

//        CreateIndexRequest request = new CreateIndexRequest("studentstatus");
//        try {
//            CreateIndexResponse response = client.indices().create(request, RequestOptions.DEFAULT);
//            log.info("创建索引==>" + response);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
        for (StudentStatus item : list) {
            StudentVOTwo studentVOTwo = new StudentVOTwo();

            //解决有一个人学号为空的下策
            if (item.getNo() == null) {
                item.setNo("");
            }
            studentVOTwo.setNo(item.getNo());
            studentVOTwo.setName(item.getName());
            studentVOTwo.setGrade(GradeUtils.getGrade(item.getNo()));
            studentVOTwo.setCollegeId(String.valueOf(item.getCollegeId()));
            studentVOTwo.setMajorId(item.getMajorId());
            studentVOTwo.setClassId(item.getClassId());
            studentVOTwo.setSex(item.getSex());
            //获得班级名字
            Class aClass = classCache.get(item.getClassId());
            studentVOTwo.setClassName(aClass.getName());
            addStudentToES(studentVOTwo);
            log.info("处理完：" + (count++) + "个人");
        }
    }

    @Override
    public List<IsConcernVO> getStudent(String grade, String collegeId, String majorId, String classId, Integer page,String no) throws IOException {
        SearchRequest request = new SearchRequest("studentstatus");
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        //设置分页
        searchSourceBuilder.from((page-1)*25);
        searchSourceBuilder.size(25);
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        if (!StringUtils.isEmpty(grade)){
            boolQueryBuilder.must(QueryBuilders.termQuery("grade",grade));
        }
        if (!StringUtils.isEmpty(collegeId)){
            boolQueryBuilder.must(QueryBuilders.termQuery("collegeId",collegeId));
        }
        if (!StringUtils.isEmpty(majorId)){
            boolQueryBuilder.must(QueryBuilders.termQuery("majorId",majorId));
        }
        if (!StringUtils.isEmpty(classId)){
            boolQueryBuilder.must(QueryBuilders.termQuery("classId",classId));
        }
        searchSourceBuilder.query(boolQueryBuilder);
        request.source(searchSourceBuilder);

        //发请求
        SearchResponse search = client.search(request, RequestOptions.DEFAULT);

        return esUtils.transformIsConcern(no,search.getHits().getHits(),false);
    }

    @Override
    public List<ESVO> getStudent(String[] nos) throws IOException {
        SearchRequest request = new SearchRequest("studentstatus");
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        //设置分页
        searchSourceBuilder.from(0);
        searchSourceBuilder.size(1000);
        searchSourceBuilder.query(QueryBuilders.termsQuery("no",nos));
        request.source(searchSourceBuilder);

        //发请求
        SearchResponse search = client.search(request, RequestOptions.DEFAULT);
        return ESUtils.transformNormal(search.getHits().getHits());
    }

    @Override
    public ESVO getStudent(String no) throws IOException {
        //发请求得到响应
        SearchResponse search = getStudentRes(no);
        return ESUtils.transformNormalOne(search.getHits().getHits());
    }

    @Override
    public SearchResponse getStudentRes(String no) throws IOException {
        SearchRequest request = new SearchRequest("studentstatus");
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        //设置分页
        searchSourceBuilder.from(0);
        searchSourceBuilder.size(1);
        searchSourceBuilder.query(QueryBuilders.termQuery("no",no));
        request.source(searchSourceBuilder);
        //发请求
        return client.search(request, RequestOptions.DEFAULT);
    }

    @Override
    public List<IsConcernVO> getStudentByFuzzySearch(String content, Integer page, String no) throws IOException {
        SearchRequest request = new SearchRequest("studentstatus");
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        //设置分页
        searchSourceBuilder.from((page-1)*25);
        searchSourceBuilder.size(25);
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        boolQueryBuilder
                .should(QueryBuilders.matchQuery("className",content))
                .should(QueryBuilders.matchQuery("name",content));
        searchSourceBuilder.query(boolQueryBuilder);

        //设置高亮显示
        HighlightBuilder highlightBuilder = new HighlightBuilder().field("*");
        highlightBuilder.preTags("<span style='color:red'>");
        highlightBuilder.postTags("</span>");

        searchSourceBuilder.highlighter(highlightBuilder);
        request.source(searchSourceBuilder);

        //发请求
        SearchResponse search = client.search(request, RequestOptions.DEFAULT);
        return esUtils.transformIsConcern(no,search.getHits().getHits(),true);
    }

    @Override
    public void addStudentToES(StudentVOTwo studentVOTwo){
        esUtils.esAddAsync("studentstatus",studentVOTwo,new ActionListener<IndexResponse>() {
            @Override
            public void onResponse(IndexResponse indexResponse) {

            }

            @Override
            public void onFailure(Exception e) {
                log.info(studentVOTwo.getNo() + "-->失败");
                e.printStackTrace();
            }
        });
    }

    @Override
    public void deleteStudentToES(String no) throws Exception {
        SearchResponse studentRes = getStudentRes(no);
        String id = studentRes.getHits().getHits()[0].getId();
        DeleteRequest deleteRequest = new DeleteRequest("studentstatus","_doc",id);
        client.delete(deleteRequest, RequestOptions.DEFAULT);
    }
}
