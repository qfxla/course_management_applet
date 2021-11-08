package com.haotongxue.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.benmanes.caffeine.cache.LoadingCache;
import com.haotongxue.entity.Info;
import com.haotongxue.entity.vo.InfoVo;
import com.haotongxue.mapper.InfoMapper;
import com.haotongxue.mapper.UserInfoMapper;
import com.haotongxue.service.ICourseService;
import com.haotongxue.service.IInfoService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.haotongxue.service.IUserInfoService;
import com.haotongxue.utils.UserContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author DJT
 * @since 2021-11-06
 */
@Service
public class InfoServiceImpl extends ServiceImpl<InfoMapper, Info> implements IInfoService {
    private static Logger logger = LoggerFactory.getLogger(InfoServiceImpl.class);

    @Autowired
    private InfoMapper infoMapper;

    @Autowired
    private LoadingCache cache;

    private static final int CORE_POOL_SIZE = 5;
    private static final int MAX_POOL_SIZE = 10;
    private static final int QUEUE_CAPACITY = 100;
    private static final Long KEEP_ALIVE_TIME = 1L;
    int i;

    public List<List> getInfo2(int week) throws InterruptedException {
        Long start = System.currentTimeMillis();

        ThreadPoolExecutor executor = new ThreadPoolExecutor(
                CORE_POOL_SIZE,
                MAX_POOL_SIZE,
                KEEP_ALIVE_TIME,
                TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(QUEUE_CAPACITY),
                new ThreadPoolExecutor.CallerRunsPolicy());

        CountDownLatch countDownLatch = new CountDownLatch(7);

        List<List> timeTables = new ArrayList<>();

        //获取登录用户
//        String openId = UserContext.getCurrentOpenid();
        String openId = "1";
        //获得用户的当前周的所有的info信息
        List<Info> infos = infoMapper.getInfoByOpenidAndWeek(openId,week);
        //把课程信息根据这周的星期几进行分类
        Map<Integer, List<Info>> xingqiMap = infos.stream().collect(Collectors.groupingBy(Info::getXingqi));
        //假设是7条，那么这7天就形成一个数组
        for (i = 1;i <= 7;i++){
            executor.execute(new Runnable() {
                @Override
                public void run() {
                    //如果这周的该星期几没课，那就都置为空
                    if (!xingqiMap.containsKey(i)){
                        List<String> oneDayVo = new ArrayList<>();
                        for (int j = 1;j <= 12;j++) {
                            oneDayVo.add("");
                        }
                        timeTables.add(oneDayVo);
                    }else {
                        //如果该天有课，那就另外根据该天的info的id去关联其他课程学习
                        List<Info> infoDay = xingqiMap.get(i);
                        String[] arr = new String[12];

                        for (Info info : infoDay) {
                            //看每个info对应的节次
                            List<Integer> sections = infoMapper.getSectionByInfoId(info.getInfoId());
                            //查该info对应的teacher，classroom，course
                            String courseName = infoMapper.getCourseNameByInfoId(info.getInfoId());
                            String classRoom = infoMapper.getClassRoomByInfoId(info.getInfoId());
                            List<String> teacherNameList = infoMapper.getTeacherListByInfoId(info.getInfoId());
                            String teacherName = "";
                            for (String s : teacherNameList) {
                                teacherName += s + " ";
                            }
                            String tableItem = courseName + "--" + classRoom + "--" + teacherName;
                            for (Integer section : sections) {
                                arr[section - 1] = tableItem;
                            }
                        }
                        for (int j = 0;j < 12;j++){
                            if (arr[j] == null || arr[j].equals("")){
                                arr[j] = "";
                            }
                        }

                        List<String> oneDayVo = Arrays.asList(arr);
                        timeTables.add(oneDayVo);
                    }
                    System.out.println(Thread.currentThread().getName() + "已经完成了");
                    countDownLatch.countDown();
                }
            });
        }

        logger.info("耗时：" + (System.currentTimeMillis() - start));
        countDownLatch.await();
        executor.shutdown();

        for (int j = 0;j < 7;j++){
            System.out.println(timeTables.get(j));
        }
//        cache.put("cour" + week,timeTables);  //循环更新缓存
        return timeTables;
    }

    @Override
    public List<List> getInfo(int week) throws InterruptedException {
        Long start = System.currentTimeMillis();

        List<List> timeTables = new ArrayList<>();

        //获取登录用户
//        String openId = UserContext.getCurrentOpenid();
        String openId = "1";
        //获得用户的当前周的所有的info信息
        List<Info> infos = infoMapper.getInfoByOpenidAndWeek(openId,week);
        //把课程信息根据这周的星期几进行分类
        Map<Integer, List<Info>> xingqiMap = infos.stream().collect(Collectors.groupingBy(Info::getXingqi));
        //假设是7条，那么这7天就形成一个数组
        for (i = 1;i <= 7;i++){
            //如果这周的该星期几没课，那就都置为空
            if (!xingqiMap.containsKey(i)){
                List<String> oneDayVo = new ArrayList<>();
                for (int j = 1;j <= 12;j++) {
                    oneDayVo.add("");
                }
                timeTables.add(oneDayVo);
            }else {
                //如果该天有课，那就另外根据该天的info的id去关联其他课程学习
                List<Info> infoDay = xingqiMap.get(i);
                String[] arr = new String[12];

                for (Info info : infoDay) {
                    //看每个info对应的节次
                    List<Integer> sections = infoMapper.getSectionByInfoId(info.getInfoId());
                    //查该info对应的teacher，classroom，course
                    String courseName = infoMapper.getCourseNameByInfoId(info.getInfoId());
                    String classRoom = infoMapper.getClassRoomByInfoId(info.getInfoId());
                    List<String> teacherNameList = infoMapper.getTeacherListByInfoId(info.getInfoId());
                    String teacherName = "";
                    for (String s : teacherNameList) {
                        teacherName += s + " ";
                    }
                    String tableItem = courseName + "--" + classRoom + "--" + teacherName;
                    for (Integer section : sections) {
                        arr[section - 1] = tableItem;
                    }
                }
                for (int j = 0;j < 12;j++){
                    if (arr[j] == null || arr[j].equals("")){
                        arr[j] = "";
                    }
                }

                List<String> oneDayVo = Arrays.asList(arr);
                timeTables.add(oneDayVo);
            }
            System.out.println(Thread.currentThread().getName() + "已经完成了");
        }

        logger.info("耗时：" + (System.currentTimeMillis() - start));
        for (int j = 0;j < 7;j++){
            System.out.println(timeTables.get(j));
        }
//        cache.put("cour" + week,timeTables);  //循环更新缓存
        return timeTables;
    }
}
