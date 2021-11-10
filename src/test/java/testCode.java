import com.haotongxue.Main9001;
import com.haotongxue.service.impl.InfoServiceImpl;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author zcj
 * @creat 2021-11-07-14:52
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = Main9001.class)
public class testCode {
    @Autowired
    private InfoServiceImpl infoServiceImpl;
    @Test
    public void test() throws InterruptedException {
        infoServiceImpl.getTodayCourse("1");
    }
}
