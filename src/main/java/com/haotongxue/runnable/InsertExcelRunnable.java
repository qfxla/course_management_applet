package com.haotongxue.runnable;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.haotongxue.entity.StudentStatus;
import com.haotongxue.entity.User;
import com.haotongxue.mapper.StudentStatusMapper;
import com.haotongxue.mapper.UserMapper;
import com.haotongxue.service.GradeService;
import com.haotongxue.service.IUserService;
import com.haotongxue.service.ReptileService;
import com.haotongxue.utils.GetBeanUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.util.CellRangeAddress;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * @Author CTC
 * @Description
 * @Date 2022/2/9
 */
@Slf4j
public class InsertExcelRunnable implements Runnable {


    private StudentStatusMapper studentStatusMapper = GetBeanUtil.getBean(StudentStatusMapper.class);
    private UserMapper userMapper = GetBeanUtil.getBean(UserMapper.class);
    private IUserService userService = GetBeanUtil.getBean(IUserService.class);

    private final CountDownLatch doneSignal;

    private HSSFSheet sheet;

    private int start;

    private int end;

    private List<String> noList;

    private String no;

    private int num;

    private CellStyle secStyle;

    private CellStyle detailStyle;

    private String[] seArr;

    private int[] secArr;

    private List<Integer> sepList;

    private int sepIdx;

    private int count;


    public static final int titleNum = 3;
    public static final int beginNum = 3;
    public static final int totalBeginNum = titleNum + beginNum;

    public static final Object lock = new Object();


    public InsertExcelRunnable(CountDownLatch doneSignal, HSSFSheet sheet, int start, int end, List<String> noList, String no, int num, CellStyle secStyle, CellStyle detailStyle, String[] seArr, int[] secArr, List<Integer> sepList, int sepIdx, int count) {
        this.doneSignal = doneSignal;
        this.sheet = sheet;
        this.start = start;
        this.end = end;
        this.noList = noList;
        this.no = no;
        this.num = num;
        this.secStyle = secStyle;
        this.detailStyle = detailStyle;
        this.seArr = seArr;
        this.secArr = secArr;
        this.sepList = sepList;
        this.sepIdx = sepIdx;
        this.count = count;
    }

    public void run() {
        if(noList.indexOf(no) != 0){
            start = 62;
            end = 71;
            start = totalBeginNum + 1;
        }
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("openid").eq("no",no).last("limit 1");
        String openid = userMapper.selectOne(queryWrapper).getOpenid();
        QueryWrapper<StudentStatus> queryWrapper1 = new QueryWrapper<>();
        queryWrapper1.select("name").eq("openid",openid).last("limit 1");
        String realName = studentStatusMapper.selectOne(queryWrapper1).getName();
        Thread.currentThread().setName(realName);
        for (int n = 0; n < 5; n++) {
            HSSFRow seRow = sheet.createRow(start - 1 + num);
            for (int i = 1; i < 8; i++) {
                synchronized (lock){
                    HSSFCell cell = seRow.createCell(0);
                    cell.setCellStyle(secStyle);
                    cell.setCellValue(seArr[n]);
                    if(i == 1 && noList.indexOf(no) == 0){
                        sepList.add(sepIdx);
                        sheet.addMergedRegion(CellRangeAddress.valueOf("A" + start + ":" + "A" + end));
                        sheet.addMergedRegion(CellRangeAddress.valueOf("A" + sepIdx + ":" + "H" + sepIdx));

                        start = end + 2;
                        end = start + count - 1;
                        sepIdx = sepIdx + count + 1;
                    }
                    List<Integer> hasCourseWeekList = userService.getHasCourseWeekList(no, i, secArr[n]);
                    String str;
                    if (hasCourseWeekList != null && hasCourseWeekList.size() > 0){
                        String weekStr = getWeekStr(hasCourseWeekList);
                        str = realName + "：第" + weekStr + "有课";
                    }else {
                        //当前星期的当前节次没有课
                        str = realName + "：" + "无课";
                    }
                    HSSFCell cell2 = seRow.createCell(i);
                    cell2.setCellValue(str);
                    cell2.setCellStyle(detailStyle);
                    log.info("星期" + i + "的" + "第" + secArr[n] + "节：" + str);
                }
                if(noList.indexOf(no) != 0) {
                    start = start + count + 1;
                }
            }
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
