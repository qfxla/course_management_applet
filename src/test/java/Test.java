import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.haotongxue.Main9001;
import com.haotongxue.entity.College;
import com.haotongxue.service.ICollegeService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.TestTemplate;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Main9001.class)
@Slf4j
public class Test {
    @Autowired
    ICollegeService iCollegeService;

    @org.junit.Test
    public void test(){
        String i = "202010244130".substring(5,8);
        log.info("iii" + i);
    }
}
