import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.haotongxue.cacheUtil.LoadingRedisCache;
import com.haotongxue.entity.Class;
import com.haotongxue.entity.StudentStatus;
import com.haotongxue.entity.vo.StudentVOTwo;
import com.haotongxue.service.IStudentStatusService;
import com.haotongxue.utils.GradeUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpHost;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.CreateIndexResponse;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@SpringBootTest
@Slf4j
public class ESTest {

//    @Autowired
//    IStudentStatusService studentStatusService;
//
//    @Resource(name = "classCache")
//    LoadingRedisCache<Class> classCache;

    @Test
    public void prepareES() {
//        QueryWrapper<StudentStatus> studentStatusQueryWrapper = new QueryWrapper<>();
//        studentStatusQueryWrapper.groupBy(Arrays.asList("college_id","class_id","major_id","name","sex","no"));
//        List<StudentStatus> list = studentStatusService.list(studentStatusQueryWrapper);
//
//        RestHighLevelClient client = new RestHighLevelClient(RestClient.builder(
//                new HttpHost("123.57.90.27", 9200)
//        ));
//
//        CreateIndexRequest request = new CreateIndexRequest("student");
//        try {
//            CreateIndexResponse response = client.indices().create(request, RequestOptions.DEFAULT);
//            log.info("创建索引==>"+response);
//        } catch (IOException e) {
//            e.printStackTrace();
//        } finally {
//            try {
//                client.close();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//        for (StudentStatus item : list){
//            StudentVOTwo studentVOTwo = new StudentVOTwo();
//
//            //解决有一个人学号为空的下策
//            if (item.getNo() == null){
//                item.setNo("");
//            }
//            studentVOTwo.setNo(item.getNo());
//            studentVOTwo.setName(item.getName());
//            studentVOTwo.setGrade(GradeUtils.getGrade(item.getNo()));
//            studentVOTwo.setCollegeId(String.valueOf(item.getCollegeId()));
//            studentVOTwo.setMajorId(item.getMajorId());
//            studentVOTwo.setClassId(item.getClassId());
//            //获得班级名字
//            Class aClass = classCache.get(item.getClassId());
//            studentVOTwo.setClassName(aClass.getName());
//            try {
//
//                log.info("处理完："+(count++)+"个人");
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
    }
}
