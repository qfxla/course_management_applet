import com.haotongxue.Main9001;
import com.haotongxue.cacheUtil.LoadingRedisCache;
import com.haotongxue.entity.FreeRoom;
import com.haotongxue.entity.vo.SelectedVo;
import com.haotongxue.mapper.FreeRoomMapper;
import com.haotongxue.mapper.SelectedMapper;
import com.haotongxue.service.IFreeRoomService;
import com.haotongxue.service.impl.InfoServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Set;

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
    @Autowired
    private IFreeRoomService iFreeRoomService;

    @Autowired
    RedisTemplate redisTemplate;
    @Test
    public void test() throws InterruptedException {
        Set keys = redisTemplate.keys("freeRoom" + "*");
        redisTemplate.delete(keys);
        System.out.println("删除成功");
    }
}
