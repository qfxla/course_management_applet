import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.haotongxue.Main9001;
import com.haotongxue.entity.College;
import com.haotongxue.service.ICollegeService;
import com.haotongxue.utils.WhichCollege;
import org.junit.jupiter.api.TestTemplate;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Main9001.class)
public class Test {
    @Autowired
    ICollegeService iCollegeService;

    @org.junit.Test
    public void test(){
        int i = Integer.valueOf("202010244130".substring(2,4));
        System.out.println(i);
    }
}
