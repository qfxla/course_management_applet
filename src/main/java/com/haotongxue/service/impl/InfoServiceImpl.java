package com.haotongxue.service.impl;

import com.haotongxue.entity.Info;
import com.haotongxue.entity.vo.TodayCourseVo;
import com.haotongxue.exceptionhandler.CourseException;
import com.haotongxue.mapper.InfoMapper;
import com.haotongxue.service.IInfoService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author DJT
 * @since 2021-11-06
 */
@Service
@Slf4j
public class InfoServiceImpl extends ServiceImpl<InfoMapper, Info> implements IInfoService {
    private static Logger logger = LoggerFactory.getLogger(InfoServiceImpl.class);

    @Autowired
    private InfoMapper infoMapper;


    private static final int CORE_POOL_SIZE = 5;
    private static final int MAX_POOL_SIZE = 10;
    private static final int QUEUE_CAPACITY = 100;
    private static final Long KEEP_ALIVE_TIME = 1L;

    @Override
    public List<List> getInfo(String openId,int week) {
        if (week == 0){
            week = infoMapper.getWeekByToday();
        }
        Long start = System.currentTimeMillis();
        ThreadPoolExecutor executor = new ThreadPoolExecutor(
                CORE_POOL_SIZE,
                MAX_POOL_SIZE,
                KEEP_ALIVE_TIME,
                TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(QUEUE_CAPACITY),
                new ThreadPoolExecutor.CallerRunsPolicy());

        CountDownLatch countDownLatch = new CountDownLatch(7);

        List<List> timeTables = new ArrayList<>(7);

        //获得用户的当前周的所有的info信息
        List<Info> infos = infoMapper.getInfoByOpenidAndWeek(openId,week);
        //把课程信息根据这周的星期几进行分类
        Map<Integer, List<Info>> xingqiMap = infos.stream().collect(Collectors.groupingBy(Info::getXingqi));
        ConcurrentHashMap<Integer, List> map = new ConcurrentHashMap<>();
        //假设是7条，那么这7天就形成一个数组
        try {
            for (int o = 1;o <= 7;o++){
                int day = o;
                executor.execute(new Runnable() {
                    @Override
                    public void run() {
                        logger.info("调用课表用的线程为：" + Thread.currentThread().getName());
                        //如果这周的该星期几没课，那就都置为空
                        if (!xingqiMap.containsKey(day)){
                            List<String> oneDayVo = new ArrayList<>();
                            for (int j = 1;j <= 12;j++) {
                                oneDayVo.add("");
                            }
                            map.put(day - 1,oneDayVo);
                        }else {
                            //如果该天有课，那就另外根据该天的info的id去关联其他课程学习
                            List<Info> infoDay = xingqiMap.get(day);
                            String[] arr = new String[12];

                            for (Info info : infoDay) {
                                //看每个info对应的节次
                                List<Integer> sections = infoMapper.getSectionByInfoId(info.getInfoId());
                                //查该info对应的teacher，classroom，course
                                String str1 = "";
                                String str2 = "";
                                String courseName = ((str1 = infoMapper.getCourseNameByInfoId(info.getInfoId())).equals("无")?"":str1);
                                String classRoom = ((str2 = infoMapper.getClassRoomByInfoId(info.getInfoId())).equals("无")?"":str2.substring(5));
//                                List<String> teacherNameList = infoMapper.getTeacherListByInfoId(info.getInfoId());
//                                String teacherName = "";
//                                if (teacherNameList.size() != 0){
//                                    for (String s : teacherNameList) {
//                                        teacherName += s + " ";
//                                    }
//                                }
//                                String tableItem = courseName + "  " + classRoom + "  " + teacherName;
                                String tableItem = courseName + "  @" + classRoom;
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
                            map.put(day - 1,oneDayVo);
                        }

                        countDownLatch.countDown();
                    }
                });
            }

            logger.info("耗时：" + (System.currentTimeMillis() - start));
            countDownLatch.await();
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            executor.shutdown();
        }
        for (Map.Entry<Integer, List> entry : map.entrySet()) {
            timeTables.add(entry.getKey(),entry.getValue());
        }
        return timeTables;
    }



    @Override
    public List getTodayCourse(String openId) {
        //查今天是第几周
        Integer week = infoMapper.getWeekByToday();
        //获取这周所有的infos
        List<Info> infos = infoMapper.getInfoByOpenidAndWeek(openId, week);
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT+:08:00"));
        calendar.setTime(new Date());
        int xingqi = calendar.get(Calendar.DAY_OF_WEEK) - 1;
        //过滤出今天的infos
        infos.stream().filter(s -> s.getXingqi() == xingqi).collect(Collectors.toList());
        ArrayList<TodayCourseVo> list = new ArrayList<>();

        for (Info info : infos) {
            TodayCourseVo pushCourseVo = new TodayCourseVo();
            //看每个info对应的节次
            pushCourseVo.setOpenId(openId);
            List<Integer> sections = infoMapper.getSectionByInfoId(info.getInfoId());
            pushCourseVo.setSections(sections);
            String str1;
            String str2;
            String courseName = ((str1 = infoMapper.getCourseNameByInfoId(info.getInfoId())).equals("无")?"":str1);
            String classRoom = ((str2 = infoMapper.getClassRoomByInfoId(info.getInfoId())).equals("无")?"":str2);
            List<String> teacherNameList = infoMapper.getTeacherListByInfoId(info.getInfoId());
            String teacherName = "";
            if (teacherNameList.size() != 0){
                for (String s : teacherNameList) {
                    teacherName += s + " ";
                }
            }
            pushCourseVo.setCourseName(courseName);
            pushCourseVo.setClassroom(classRoom);
            pushCourseVo.setTeacher(teacherName);

            list.add(pushCourseVo);
        }

        return list;
    }


    //没优化之前的

//    @Override
//    public List<List> getInfo(int week) throws InterruptedException {
//        Long start = System.currentTimeMillis();
//
//        List<List> timeTables = new ArrayList<>();
//
//        //获取登录用户
////        String openId = UserContext.getCurrentOpenid();
//        String openId = "1";
//        //获得用户的当前周的所有的info信息
//        List<Info> infos = infoMapper.getInfoByOpenidAndWeek(openId,week);
//        //把课程信息根据这周的星期几进行分类
//        Map<Integer, List<Info>> xingqiMap = infos.stream().collect(Collectors.groupingBy(Info::getXingqi));
//        //假设是7条，那么这7天就形成一个数组
//        for (i = 1;i <= 7;i++){
//            //如果这周的该星期几没课，那就都置为空
//            if (!xingqiMap.containsKey(i)){
//                List<String> oneDayVo = new ArrayList<>();
//                for (int j = 1;j <= 12;j++) {
//                    oneDayVo.add("");
//                }
//                timeTables.add(oneDayVo);
//            }else {
//                //如果该天有课，那就另外根据该天的info的id去关联其他课程学习
//                List<Info> infoDay = xingqiMap.get(i);
//                String[] arr = new String[12];
//
//                for (Info info : infoDay) {
//                    //看每个info对应的节次
//                    List<Integer> sections = infoMapper.getSectionByInfoId(info.getInfoId());
//                    //查该info对应的teacher，classroom，course
//                    String courseName = infoMapper.getCourseNameByInfoId(info.getInfoId());
//                    String classRoom = infoMapper.getClassRoomByInfoId(info.getInfoId());
//                    List<String> teacherNameList = infoMapper.getTeacherListByInfoId(info.getInfoId());
//                    String teacherName = "";
//                    for (String s : teacherNameList) {
//                        teacherName += s + " ";
//                    }
//                    String tableItem = courseName + "--" + classRoom + "--" + teacherName;
//                    for (Integer section : sections) {
//                        arr[section - 1] = tableItem;
//                    }
//                }
//                for (int j = 0;j < 12;j++){
//                    if (arr[j] == null || arr[j].equals("")){
//                        arr[j] = "";
//                    }
//                }
//
//                List<String> oneDayVo = Arrays.asList(arr);
//                timeTables.add(oneDayVo);
//            }
//        }
//
//        logger.info("耗时：" + (System.currentTimeMillis() - start));
//        for (int j = 0;j < 7;j++){
//            System.out.println(timeTables.get(j));
//        }
////        cache.put("cour" + week,timeTables);  //循环更新缓存
//        return timeTables;
//        return null;
//    }


    @Override
    public String addCourseInfo(int week) {
        UUID infoUUID = UUID.randomUUID();
        String infoId = infoUUID.toString();
        Info info = new Info();
        info.setInfoId(infoId);
        if(week >= 1 && week <= 7){
            info.setXingqi(week);
        }else {
            CourseException courseException = new CourseException();
            courseException.setCode(555);
            courseException.setMsg("星期数超过1~7范围，插入t_info失败。");
            throw courseException;
        }
        boolean flag = save(info);
        if(flag){
            return infoId;
        }else{
            CourseException courseException = new CourseException();
            courseException.setCode(505);
            courseException.setMsg("插入对象到t_info失败。");
            throw courseException;
        }
    }
}
