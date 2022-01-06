import com.haotongxue.Main9001;
import com.haotongxue.entity.vo.SelectedVo;
import com.haotongxue.mapper.SelectedMapper;
import com.haotongxue.service.impl.InfoServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.File;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * @author zcj
 * @creat 2021-11-07-14:52
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = Main9001.class)
@Slf4j
public class testCode {
    @Autowired
    private InfoServiceImpl infoServiceImpl;
    @Autowired
    private SelectedMapper selectedMapper;
    @Test
    public void test() throws InterruptedException {
    }
}
