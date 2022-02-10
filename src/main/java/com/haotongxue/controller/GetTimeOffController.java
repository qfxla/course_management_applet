package com.haotongxue.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.haotongxue.entity.Organization;
import com.haotongxue.entity.StudentStatus;
import com.haotongxue.entity.User;
import com.haotongxue.entity.dto.MemberDTO;
import com.haotongxue.entity.dto.OragDTO;
import com.haotongxue.exceptionhandler.CourseException;
import com.haotongxue.mapper.OrganizationMapper;
import com.haotongxue.mapper.StudentStatusMapper;
import com.haotongxue.mapper.UserMapper;
import com.haotongxue.service.IUserService;
import com.haotongxue.utils.R;
import com.haotongxue.utils.SnowflakeIdWorker;
import io.swagger.models.auth.In;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.util.CellRangeAddress;
import org.elasticsearch.monitor.os.OsStats;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.*;

import static org.apache.poi.ss.usermodel.BorderStyle.MEDIUM;
import static org.apache.poi.ss.usermodel.HorizontalAlignment.CENTER;

/**
 * @Author CTC
 * @Description
 * @Date 2022/2/8
 */
@Slf4j
@RestController
@RequestMapping("/zkCourse/timeoff/authority")
//@RequestMapping("/zkCourse/timeoff")
public class GetTimeOffController {

    @Autowired
    UserMapper userMapper;

    @Autowired
    IUserService userService;

    @Autowired
    StudentStatusMapper studentStatusMapper;

    @Autowired
    OrganizationMapper organizationMapper;

    @Autowired
    RedisTemplate redisTemplate;

    public static final int titleNum = 3;
    public static final int beginNum = 3;
    public static final int totalBeginNum = titleNum + beginNum;
    public static final String[] arr = new String[]{"A","B","C","D","E","F","G","H"};
    public static final String[] weekArr = new String[]{"","一","二","三","四","五","六","日"};
    public static final String[] seArr = new String[]{"1-2节","3-4节","6-7节","8-9节","10-12节"};
    public static final int[] secArr = new int[]{1,3,6,8,10};

    private final ExecutorService executorService = new ThreadPoolExecutor(16,16,0,
            TimeUnit.MILLISECONDS,
            new LinkedBlockingQueue<>());


    @PostMapping("/preHeat")
    public R preHeat(@RequestParam("no") String no){
        String key = "xls" + no;
        if(redisTemplate.hasKey(key)){
            redisTemplate.expire(no,1,TimeUnit.HOURS);
            return R.ok();
        }else {
            String[][] hasCourseArr = new String[5][7];
            QueryWrapper<User> queryWrapper = new QueryWrapper<>();
            queryWrapper.select("openid").eq("no",no).last("limit 1");
            String openid = userMapper.selectOne(queryWrapper).getOpenid();
            QueryWrapper<StudentStatus> queryWrapper1 = new QueryWrapper<>();
            queryWrapper1.select("name").eq("openid",openid).last("limit 1");
            String realName = studentStatusMapper.selectOne(queryWrapper1).getName();
            for (int n = 0; n < 5; n++) {
                for (int i = 1; i < 8; i++) {
                    List<Integer> hasCourseWeekList = userService.getHasCourseWeekList(no, i, secArr[n]);
                    String weekStr;
                    if(hasCourseWeekList.size() > 0){
                        weekStr = getWeekStr(hasCourseWeekList) + "有课";
                    }else {
                        weekStr = "无课";
                    }
                    hasCourseArr[n][i-1] = weekStr;
                }
            }
            MemberDTO memberDTO = new MemberDTO();
            memberDTO.setRealName(realName);
            memberDTO.setHasCourseArr(hasCourseArr);
            System.out.println(memberDTO);
            redisTemplate.opsForValue().set(key,memberDTO);
            redisTemplate.expire(key, 1, TimeUnit.HOURS);
            return R.ok();
        }
    }

    @GetMapping("/getTimeOff")
    public R getTimeOff(@RequestBody OragDTO oragDTO){
        log.info("~~~~~");
        try {
            SnowflakeIdWorker idWorker = new SnowflakeIdWorker(1, 1);
            long id = idWorker.nextId();
            String orgName = oragDTO.getOrgName();
            Organization organization = new Organization();
            organization.setName(orgName);
            organization.setXlsId(String.valueOf(id));
            organizationMapper.insert(organization);
            List<String> noList = oragDTO.getNoList();
            boolean flag = makeXls(id,orgName,noList);
        } catch (Exception e) {
            e.printStackTrace();
        }
        log.info("~~~~~");
        return R.ok();
    }

    public boolean makeXls (long id,String orgName,List<String> noList) throws Exception{
        int count = noList.size();
        int totalRow = count * 5 + totalBeginNum + 4 + 1;   //61
        HSSFWorkbook workbook = new HSSFWorkbook();
        short colorIdx1 = IndexedColors.SKY_BLUE.getIndex();
        short colorIdx2 = IndexedColors.BLACK.getIndex();
        HSSFSheet sheet = workbook.createSheet();

        //设置标题边框
        CellStyle titleStyle = workbook.createCellStyle();
        CellStyle titleBorderStyle = workbook.createCellStyle();
        titleBorderStyle.setBorderTop(MEDIUM);
        titleBorderStyle.setTopBorderColor(colorIdx2);
        titleBorderStyle.setBorderBottom(MEDIUM);
        titleBorderStyle.setBottomBorderColor(colorIdx2);
        titleBorderStyle.setBorderLeft(MEDIUM);
        titleBorderStyle.setLeftBorderColor(colorIdx2);
        titleBorderStyle.setBorderRight(MEDIUM);
        titleBorderStyle.setRightBorderColor(colorIdx2);
        //设置标题样式
        HSSFFont titleFont = workbook.createFont();
        titleFont.setFontHeightInPoints((short) 26);
        titleFont.setFontName("微软雅黑");
        titleFont.setBold(true);
        titleStyle.setFont(titleFont);
        titleStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        titleStyle.setAlignment(CENTER);
        for (int i = 0; i < titleNum; i++) {
            HSSFRow row = sheet.createRow(i);
            for (int j = 0; j < 8; j++) {
                HSSFCell cell = row.createCell(j);
                cell.setCellStyle(titleBorderStyle);
            }
        }
        sheet.addMergedRegion(CellRangeAddress.valueOf("A1:" + "H3"));

        HSSFRow row2 = sheet.createRow(0);
        HSSFCell cell1 = row2.createCell(0);
        cell1.setCellValue("2021-2022-2学期" + orgName + "成员无课时间一览表（\"仲园课程表\"小程序提供）");
        cell1.setCellStyle(titleStyle);

        CellStyle style = workbook.createCellStyle();
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        style.setAlignment(CENTER);
        HSSFFont xingqiFont = workbook.createFont();
        xingqiFont.setFontHeightInPoints((short) 24);
        xingqiFont.setFontName("微软雅黑");
        xingqiFont.setBold(true);
        style.setFont(xingqiFont);
        style.setFillForegroundColor(colorIdx1);// 设置背景色
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setBorderTop(MEDIUM);
        style.setTopBorderColor(colorIdx2);
        style.setBorderBottom(MEDIUM);
        style.setBottomBorderColor(colorIdx2);
        style.setBorderLeft(MEDIUM);
        style.setLeftBorderColor(colorIdx2);
        style.setBorderRight(MEDIUM);
        style.setRightBorderColor(colorIdx2);
        //设置星期边框
        for (int i = titleNum; i < totalBeginNum; i++) {
            HSSFRow row = sheet.createRow(i);
            for (int j = 0; j < 8; j++) {
                HSSFCell cell = row.createCell(j);
                cell.setCellStyle(style);
            }
        }
        //设置节次边框
        for (int i = totalBeginNum; i < totalRow; i++) {
            HSSFRow row = sheet.createRow(i);
            HSSFCell cell = row.createCell(0);
            cell.setCellStyle(style);
        }

        HSSFRow row = sheet.createRow(beginNum);
        for (int i = 0; i < 8; i++) {
            sheet.addMergedRegion(CellRangeAddress.valueOf(arr[i] + (beginNum + 1) + ":" + arr[i] + (beginNum + 3)));
            if(i != 0){
                sheet.setColumnWidth(i, 33 * 256);
                HSSFCell cell = row.createCell(i);
                cell.setCellStyle(style);
                cell.setCellValue("星期" + weekArr[i]);
            }
        }
        HSSFFont sectionFont = workbook.createFont();
        sectionFont.setFontHeightInPoints((short) 15);
        sectionFont.setFontName("微软雅黑");
        sectionFont.setBold(true);
        CellStyle secStyle = workbook.createCellStyle();
        secStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        secStyle.setAlignment(CENTER);
        secStyle.setFillForegroundColor(colorIdx1);// 设置背景色
        secStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        secStyle.setBorderTop(MEDIUM);
        secStyle.setTopBorderColor(colorIdx2);
        secStyle.setBorderBottom(MEDIUM);
        secStyle.setBottomBorderColor(colorIdx2);
        secStyle.setBorderLeft(MEDIUM);
        secStyle.setLeftBorderColor(colorIdx2);
        secStyle.setBorderRight(MEDIUM);
        secStyle.setRightBorderColor(colorIdx2);
        secStyle.setFont(sectionFont);

        CellStyle detailStyle = workbook.createCellStyle();
        detailStyle.setBorderLeft(MEDIUM);
        detailStyle.setLeftBorderColor(colorIdx2);
        detailStyle.setBorderRight(MEDIUM);
        detailStyle.setRightBorderColor(colorIdx2);

        int start = totalBeginNum + 1;
        int end = start + count - 1;
        List<Integer> sepList = new ArrayList<>();
        int sepIdx = count + totalBeginNum + 1;
        sheet.setColumnWidth(0, 12 * 256);
        int num = -1;
        for (String no : noList) {
            String key = "xls" + no;
            String[][] hasCourseArr = new String[5][7];
            boolean cacheFlag = true;
            MemberDTO memberDTO;
            if(redisTemplate.hasKey(key)){
                memberDTO = (MemberDTO) redisTemplate.opsForValue().get(key);
            }else {
                cacheFlag = false;
                QueryWrapper<StudentStatus> queryWrapper1 = new QueryWrapper<>();
                QueryWrapper<User> queryWrapper = new QueryWrapper<>();
                queryWrapper.select("openid").eq("no",no).last("limit 1");
                String openid = userMapper.selectOne(queryWrapper).getOpenid();
                queryWrapper1.select("name").eq("openid",openid).last("limit 1");
                String realName = studentStatusMapper.selectOne(queryWrapper1).getName();
                memberDTO = new MemberDTO();
                memberDTO.setRealName(realName);
            }
            if(noList.indexOf(no) != 0){
                start = totalBeginNum + 1;
            }
            num++;
            assert memberDTO != null;
            String realName = memberDTO.getRealName();
            for (int n = 0; n < 5; n++) {
                HSSFRow seRow = sheet.createRow(start - 1 + num);
                for (int i = 1; i < 8; i++) {
                    HSSFCell cell = seRow.createCell(0);
                    cell.setCellStyle(secStyle);
                    cell.setCellValue(seArr[n]);
                    if(i == 1 && noList.indexOf(no) == 0){
                        sepList.add(sepIdx);
                        if(noList.size() > 1){
                            sheet.addMergedRegion(CellRangeAddress.valueOf("A" + start + ":" + "A" + end));
                        }
                        sheet.addMergedRegion(CellRangeAddress.valueOf("A" + sepIdx + ":" + "H" + sepIdx));

                        start = end + 2;
                        end = start + count - 1;
                        sepIdx = sepIdx + count + 1;
                    }
                    System.out.print("星期" + i + "的" + "第" + secArr[n] + "节：");
                    String str;
                    if(cacheFlag){
                        str = memberDTO.getHasCourseArr()[n][i-1];
                    }else {
                        List<Integer> hasCourseWeekList = userService.getHasCourseWeekList(no, i, secArr[n]);
                        String weekStr;
                        if(hasCourseWeekList != null && hasCourseWeekList.size() > 0){
                            weekStr = getWeekStr(hasCourseWeekList) + "有课";
                        }else {
                            //当前星期的当前节次没有课
                            weekStr = "无课";
                        }
                        hasCourseArr[n][i-1] = weekStr;
                        str = weekStr;
                    }
                    HSSFCell cell2 = seRow.createCell(i);
                    cell2.setCellValue(realName + "：" + str);
                    cell2.setCellStyle(detailStyle);
                    System.out.println(realName + "---" + str);
                }
                if(noList.indexOf(no) != 0) {
                    start = start + count + 1;
                }
            }
            if(!cacheFlag){
                memberDTO.setHasCourseArr(hasCourseArr);
                redisTemplate.opsForValue().set(key,memberDTO);
            }
        }
        CellStyle sepStyle = workbook.createCellStyle();
        short colorIdx3 = IndexedColors.LIGHT_CORNFLOWER_BLUE.getIndex();
        sepStyle.setFillForegroundColor(colorIdx3);
        sepStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        for (int sepIndex : sepList) {
            HSSFRow row1 = sheet.createRow(sepIndex - 1);
            HSSFCell cell = row1.createCell(0);
            cell.setCellStyle(sepStyle);
        }

        //创建一个文件
        File file = new File("X:/" + id + ".xls");
//        File file = new File("/root/timeoffxls/" + orgName + "成员无课时间一览表(\"仲园课程表\"小程序提供)" + id + ".xls");
        boolean newFileFlag = file.createNewFile();
        if(newFileFlag){
            System.out.println("文件成功创建---"  +  file.getPath());
            FileOutputStream stream = FileUtils.openOutputStream(file);
            workbook.write(stream);
            stream.close();
            return true;
        }else {
            throw new CourseException(555,"文件居然已经存在");
        }
    }

    public static String getWeekStr(List<Integer> weekList){
        List<String> weekStrList = new ArrayList<>();
        int[] weekArr = new int[21];
        for (Integer week : weekList) {
            weekArr[week] = week;
        }
        int begin = 0,end = 0;
        for (int i = 1; i < weekArr.length; i++) {
            if(weekArr[i] == 0){
                continue;
            }
            if(weekArr[i-1] == 0){
                if(weekArr[i+1] == 0){
                    weekStrList.add(i + "");
                    weekArr[i] = 0;
                }else{
                    begin = i;
                }
            }else{
                if(weekArr[i+1]==0){
                    end = i;
                }
            }
            if(end > begin){
                weekStrList.add(begin + "-" + end);
                for (int j = begin; j <= end; j++) {
                    weekArr[j] = 0;
                }
                end = 0;
                begin = 0;
            }
        }
        String sub1 = weekStrList.toString().substring(1);
        String sub2 = sub1.substring(0,sub1.length()-1);
        String sub3 = sub2.replace(" ","") + "周";
        return sub3;
    }
}
