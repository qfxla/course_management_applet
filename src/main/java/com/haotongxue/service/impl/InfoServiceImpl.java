package com.haotongxue.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.benmanes.caffeine.cache.LoadingCache;
import com.haotongxue.entity.*;
import com.haotongxue.entity.vo.CourseVo;
import com.haotongxue.entity.vo.TodayCourseVo;
import com.haotongxue.exceptionhandler.CourseException;
import com.haotongxue.mapper.*;
import com.haotongxue.service.*;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.haotongxue.utils.UserContext;
import lombok.extern.slf4j.Slf4j;
import org.checkerframework.checker.units.qual.C;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.RoundingMode;
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

    @Resource(name = "courseInfo")
    LoadingCache<String,Object> cache;
    @Autowired
    private IInfoCourseService iInfoCourseService;
    @Autowired
    private IInfoClassroomService iInfoClassroomService;
    @Autowired
    private IInfoTeacherService iInfoTeacherService;
    @Resource(name = "weekCache")
    LoadingCache<String,Object> weekCache;
    @Autowired
    private InfoSectionMapper infoSectionMapper;
    @Autowired
    private InfoWeekMapper infoWeekMapper;
    @Autowired
    private InfoCourseMapper infoCourseMapper;
    @Autowired
    private InfoClassroomMapper infoClassroomMapper;
    @Autowired
    private IInfoService iInfoService;
    @Autowired
    private IUserInfoService iUserInfoService;


    private static final int CORE_POOL_SIZE = Runtime.getRuntime ().availableProcessors () + 1;
    private static final int MAX_POOL_SIZE = (Runtime.getRuntime ().availableProcessors () + 1) * 2;
    private static final int QUEUE_CAPACITY = 100;
    private static final Long KEEP_ALIVE_TIME = 1L;

    @Override
    public List<List> getInfo(String openId,int week) {
        if (week == 0){
//            week = infoMapper.getWeekByToday();
            week = (Integer)weekCache.get("week");
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

        //获得用户的当前周的所有的info信息并按星期一到星期七
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

                        //如果这周的该星期几没课，那就都置为空
                        if (!xingqiMap.containsKey(day)){
                            List<CourseVo> oneDayVo = new ArrayList<>();
                            for (int j = 1;j <= 12;j++) {
                                CourseVo courseVo = new CourseVo("","","","","");
                                oneDayVo.add(courseVo);
                            }
                            map.put(day - 1,oneDayVo);
                        }else {
                            //如果该天有课，那就另外根据该天的info的id去关联其他课程学习
                            List<Info> infoDay = xingqiMap.get(day);
                            CourseVo[] arr = new CourseVo[12];

                            for (Info info : infoDay) {
                                //System.out.println("info为" + info.getXingqi() + ":" + info.getSectionStr());
                                //看每个info对应的节次
                                List<Integer> sections = infoMapper.getSectionByInfoId(info.getInfoId());
                                //查该info对应的teacher，classroom，course
                                String str1 = "";
                                String str2 = "";
                                String str3 = "";
                                String courseId = iInfoCourseService.list(new QueryWrapper<InfoCourse>().eq("info_id", info.getInfoId())).get(0).getCourseId();
                                Integer classRoomId = iInfoClassroomService.list(new QueryWrapper<InfoClassroom>().eq("info_id", info.getInfoId())).get(0).getClassroomId();
                                Integer teacherId = iInfoTeacherService.list(new QueryWrapper<InfoTeacher>().eq("info_id", info.getInfoId())).get(0).getTeacherId();
                                String courseName = ((str1 = (String)cache.get("course-" + courseId)).equals("无")?"":str1);
                                String classRoom = ((str2 = (String)cache.get("classroom-" + classRoomId))).equals("无")?"": "@" + str2.substring(5);
                                String teacher = ((str3 = (String)cache.get("teacher-" + teacherId)).equals("无")?"":str3);
                                CourseVo courseVo = new CourseVo();
                                courseVo.setTeacher(teacher).setName(courseName).setClassRoom(classRoom).setWeekStr(info.getWeekStr()).setSectionStr(info.getSectionStr());
//                                String tableItem = courseName + "  " + classRoom + "  " + teacherName;
//                                String tableItem = courseName + "  @" + classRoom;
                                for (Integer section : sections) {
                                    arr[section - 1] = courseVo;
                                }
                            }

                            for (int j = 0;j < 12;j++){
                                if (arr[j] == null){
                                    CourseVo courseVo = new CourseVo();
                                    courseVo.setName("").setClassRoom("").setTeacher("").setSectionStr("").setWeekStr("");
                                    arr[j] = courseVo;
                                }
                            }
                            List<CourseVo> oneDayVo = Arrays.asList(arr);
                            map.put(day - 1,oneDayVo);
                        }

                        countDownLatch.countDown();
                    }
                });
            }
            countDownLatch.await();
            logger.info("耗时：" + (System.currentTimeMillis() - start));
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
        Calendar calendar = Calendar.getInstance();
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

    @Override
    public int insertInfo(Info info) {
        int res = infoMapper.insert(info);
        if(res == 1){
            return 1;
        }else{
            throw new CourseException(555,"插入t_info失败。");
        }
    }

    @Override
    public boolean updateCourseData() {
        String openId = UserContext.getCurrentOpenid();
        //查找当前用户的所有info
        QueryWrapper<UserInfo> wrapper = new QueryWrapper<>();
        wrapper.eq("openid",openId);
        List<String> infoList = iUserInfoService.list(wrapper).stream().map(UserInfo::getInfoId).collect(Collectors.toList());
        for (String infoId : infoList) {
            //删除当前用户的数据
            int i1 = infoSectionMapper.deleteByInfoId(infoId);
            int i2 = infoWeekMapper.deleteByInfoId(infoId);
            int i3 = infoCourseMapper.deleteByInfoId(infoId);
            int i4 = infoClassroomMapper.deleteByInfoId(infoId);
        }

return true;

    }

    @Override
    public String addCourseInfo(int week,String weekStr,String sectionStr) {
        UUID infoUUID = UUID.randomUUID();
        String infoId = infoUUID.toString();
        Info info = new Info();
        info.setInfoId(infoId);
        info.setWeekStr(weekStr);
        info.setSectionStr(sectionStr);
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
