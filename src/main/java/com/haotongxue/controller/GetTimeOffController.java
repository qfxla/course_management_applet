package com.haotongxue.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.haotongxue.entity.*;
import com.haotongxue.entity.Class;
import com.haotongxue.entity.dto.MemberDTO;
import com.haotongxue.entity.dto.OragDTO;
import com.haotongxue.exceptionhandler.CourseException;
import com.haotongxue.mapper.*;
import com.haotongxue.service.IUserService;
import com.haotongxue.utils.R;
import com.haotongxue.utils.SnowflakeIdWorker;
import com.haotongxue.utils.UserContext;
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

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
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
//@RequestMapping("/zkCourse/timeoff/authority")
@RequestMapping("/zkCourse/timeoff")
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
    InviteMapper inviteMapper;

    @Autowired
    ClassMapper classMapper;

    @Autowired
    OfficialUserMapper officialUserMapper;

    @Autowired
    RedisTemplate redisTemplate;

    public static final int titleNum = 3;
    public static final int beginNum = 3;
    public static final int totalBeginNum = titleNum + beginNum;
    public static final String[] arr = new String[]{"A","B","C","D","E","F","G","H"};
    public static final String[] weekArr = new String[]{"","一","二","三","四","五","六","日"};
    public static final String[] seArr = new String[]{"1-2节","3-4节","6-7节","8-9节","10-12节"};
    public static final int[] secArr = new int[]{1,3,6,8,10};
//    public static final String pathName = "X:/";
    public static final String pathName = "/root/timeoffxls";
    public static final String suffix = "成员无课时间一览表（“仲园课程表”小程序提供）";

    @RequestMapping("/downloadXls")
    public R downloadXls(String orgName, String xlsId, HttpServletResponse response) {
        //首先判断是否支付成功
        QueryWrapper<Organization> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("xls_id",xlsId).select("ack_num","total_num","status");
        Organization organization = organizationMapper.selectOne(queryWrapper);
        if(organization.getAck_num() != organization.getTotal_num() || organization.getStatus() != 2){
            return R.error().data("data","未支付 或 确认人数!=总人数");
        }
        String fileName = orgName + xlsId + ".xls";
        String path = pathName + fileName;
        try {
            // path是指想要下载的文件的路径
            File file = new File(path);
            log.info(file.getPath());
            // 获取文件名
            String filename = orgName + suffix + ".xls";
            // 获取文件后缀名
//            String ext = filename.substring(filename.lastIndexOf(".") + 1).toLowerCase();
//            log.info("文件后缀名：" + ext);

            // 将文件写入输入流
            FileInputStream fileInputStream = new FileInputStream(file);
            InputStream fis = new BufferedInputStream(fileInputStream);
            byte[] buffer = new byte[fis.available()];
            int read = fis.read(buffer);
            System.out.println("read---" + read);
            fis.close();

            // 清空response
            response.reset();
            // 设置response的Header
            response.setCharacterEncoding("UTF-8");
            //Content-Disposition的作用：告知浏览器以何种方式显示响应返回的文件，用浏览器打开还是以附件的形式下载到本地保存
            //attachment表示以附件方式下载   inline表示在线打开   "Content-Disposition: inline; filename=文件名.mp3"
            // filename表示文件的默认名称，因为网络传输只支持URL编码的相关支付，因此需要将文件名URL编码后进行传输,前端收到后需要反编码才能获取到真正的名称
            response.addHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(filename, "UTF-8"));
            // 告知浏览器文件的大小
            response.addHeader("Content-Length", "" + file.length());
            OutputStream outputStream = new BufferedOutputStream(response.getOutputStream());
            response.setContentType("application/octet-stream");
            outputStream.write(buffer);
            outputStream.flush();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return R.ok();
    }

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

    @GetMapping("/getMyCreated")
    public R getMyCreated(){
//        String openId = UserContext.getCurrentOpenid();
        String openId = "ohpVk5TmJDKSy5Wm3rGAvLQnUneQ";
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("openid",openId).select("no");
        User user = userMapper.selectOne(queryWrapper);
        String no = user.getNo();
        QueryWrapper<Organization> queryWrapper1 = new QueryWrapper<>();
        queryWrapper1.eq("no",no);
        List<Organization> organizationList = organizationMapper.selectList(queryWrapper1);
        return R.ok().data("data",organizationList);
    }

    @GetMapping("/getMyInvited")
    public R getMyInvited(){
//        String openId = UserContext.getCurrentOpenid();
        String openId = "ohpVk5TmJDKSy5Wm3rGAvLQnUneQ";
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("openid",openId).select("no");
        User user = userMapper.selectOne(queryWrapper);
        String no = user.getNo();
        QueryWrapper<Invite> queryWrapper1 = new QueryWrapper<>();
        queryWrapper1.eq("no",no);
        List<Invite> inviteList = inviteMapper.selectList(queryWrapper1);
        return R.ok().data("data",inviteList);
    }

    @GetMapping("/getMyMembers")
    public R getMyMembers(@RequestParam("xlsId") String xlsId){
        QueryWrapper<Invite> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("xls_id",xlsId).select("no","status");
        List<Invite> inviteList = inviteMapper.selectList(queryWrapper);
        return R.ok().data("data",inviteList);
    }

    @PostMapping("/agreeInvited")
    public R agreeInvite(@RequestParam("xlsId") String xlsId){
//        String openId = UserContext.getCurrentOpenid();
        String openId = "ohpVk5TmJDKSy5Wm3rGAvLQnUneQ";
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("openid",openId).select("no");
        User user = userMapper.selectOne(queryWrapper);
        String no = user.getNo();
        QueryWrapper<Invite> queryWrapper1 = new QueryWrapper<>();
        queryWrapper1.eq("xls_id",xlsId).eq("no",no).select("status");
        Invite invite = inviteMapper.selectOne(queryWrapper1);
        if(invite.getStatus() != 2){
            invite.setStatus(1);
            QueryWrapper<Organization> queryWrapper2 = new QueryWrapper<>();
            queryWrapper2.eq("xls_id",xlsId);
            Organization organization = organizationMapper.selectOne(queryWrapper2);
            if(organization.getTotal_num() - organization.getAck_num() == 1){
                organization.setStatus(1);
                String mainer_no = organization.getNo();
                QueryWrapper<User> queryWrapper5 = new QueryWrapper<>();
                queryWrapper5.eq("no",mainer_no).last("limit 1");
                User mainUser = userMapper.selectOne(queryWrapper5);
                String mainUserUnionId = mainUser.getUnionId();
                QueryWrapper<OfficialUser> queryWrapper6 = new QueryWrapper<>();
                queryWrapper6.eq("unionid",mainUserUnionId).select("openid");
                OfficialUser officialUser = officialUserMapper.selectOne(queryWrapper6);
                String orgName = "“" + organization.getName() + "”";
                String officialOpenId = officialUser.getOpenid();
                String msg = "您创建的" + orgName + "成员无课时间一览表所邀请的所有成员已全部接收邀请";
                //此处要发送到消息队列，通知负责人所有成员已经全部确认
                return R.ok().data("data",officialOpenId + "--" + msg);
            }
            if(organization.getAck_num() > organization.getTotal_num()){
                throw new CourseException(555,"数据出现异常");
            }
            organization.setAck_num(organization.getAck_num() + 1);
            organizationMapper.updateById(organization);
            return R.ok();
        }else {
            throw new CourseException(555,"邀请状态为拒绝，不能再同意！");
        }
    }

    @PostMapping("/rejectInvited")
    public R rejectInvited(@RequestParam("xlsId") String xlsId){
//        String openId = UserContext.getCurrentOpenid();
        String openId = "ohpVk5TmJDKSy5Wm3rGAvLQnUneQ";
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("openid",openId).select("no");
        User user = userMapper.selectOne(queryWrapper);
        String no = user.getNo();
        QueryWrapper<Invite> queryWrapper1 = new QueryWrapper<>();
        queryWrapper1.eq("no",no).eq("xls_id",xlsId);
        Invite invite = inviteMapper.selectOne(queryWrapper1);
        invite.setStatus(2);
        inviteMapper.updateById(invite);
        QueryWrapper<Organization> queryWrapper2 = new QueryWrapper<>();
        queryWrapper2.eq("xls_id",xlsId);
        Organization organization = organizationMapper.selectOne(queryWrapper2);
        organization.setStatus(3);
        organizationMapper.updateById(organization);
        QueryWrapper<StudentStatus> queryWrapper3 = new QueryWrapper<>();
        queryWrapper3.eq("no",no);
        StudentStatus studentStatus = studentStatusMapper.selectOne(queryWrapper3);
        String majorId = studentStatus.getMajorId();
        String classId = studentStatus.getClassId();
        QueryWrapper<Class> queryWrapper4 = new QueryWrapper<>();
        queryWrapper4.eq("class_id",classId).eq("major_id",majorId);
        Class aClass = classMapper.selectOne(queryWrapper4);
        String className = aClass.getName();
        String realName = studentStatus.getName();
        String orgName = "“" + organization.getName() + "”";
        String mainer_no = organization.getNo();
        QueryWrapper<User> queryWrapper5 = new QueryWrapper<>();
        queryWrapper5.eq("no",mainer_no).last("limit 1");
        User mainUser = userMapper.selectOne(queryWrapper5);
        String mainUserUnionId = mainUser.getUnionId();
        QueryWrapper<OfficialUser> queryWrapper6 = new QueryWrapper<>();
        queryWrapper6.eq("unionid",mainUserUnionId).select("openid");
        OfficialUser officialUser = officialUserMapper.selectOne(queryWrapper6);
        String officialOpenId = officialUser.getOpenid();
        String msg = className + realName + "拒绝了您" + orgName + "成员无课时间一览表的邀请";
        //此处要发送到消息队列，通知负责人xxx拒绝了
        return R.ok().data("data",officialOpenId + "--" + msg);
    }

    @GetMapping("/getTimeOff")
    public R getTimeOff(@RequestBody OragDTO oragDTO){
//        String openId = UserContext.getCurrentOpenid();
        String openId = "ohpVk5TmJDKSy5Wm3rGAvLQnUneQ";
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("openid",openId).select("no");
        User mainerUser = userMapper.selectOne(queryWrapper);
        String mainerNo = mainerUser.getNo();
        log.info("~~~~~");
        long xlsId = 0;
        try {
            SnowflakeIdWorker idWorker = new SnowflakeIdWorker(1, 1);
            xlsId = idWorker.nextId();
            String orgName = oragDTO.getOrgName();
            String newOrgName = "“" + orgName + "”";
            List<String> noList = oragDTO.getNoList();
            Organization organization = new Organization();
            organization.setName(orgName);
            organization.setXlsId(String.valueOf(xlsId));
            organization.setNo(mainerNo);
            organization.setStatus(0);
            organization.setAck_num(0);
            organization.setTotal_num(noList.size());
            organizationMapper.insert(organization);

            QueryWrapper<StudentStatus> queryWrapper3 = new QueryWrapper<>();
            queryWrapper3.eq("no",mainerNo);
            StudentStatus studentStatus = studentStatusMapper.selectOne(queryWrapper3);
            String majorId = studentStatus.getMajorId();
            String classId = studentStatus.getClassId();
            String reName = studentStatus.getName();
            QueryWrapper<Class> queryWrapper4 = new QueryWrapper<>();
            queryWrapper4.eq("class_id",classId).eq("major_id",majorId);
            Class aClass = classMapper.selectOne(queryWrapper4);
            String className = aClass.getName();
            for (String no : noList) {
                Invite invite = new Invite(String.valueOf(xlsId),no,0);
                inviteMapper.insert(invite);
                QueryWrapper<User> queryWrapper5 = new QueryWrapper<>();
                queryWrapper5.eq("no",no).last("limit 1");
                User user = userMapper.selectOne(queryWrapper5);
                String unionId = user.getUnionId();
                QueryWrapper<OfficialUser> queryWrapper6 = new QueryWrapper<>();
                queryWrapper6.eq("unionid",unionId).select("openid");
                OfficialUser officialUser = officialUserMapper.selectOne(queryWrapper6);
                String officialOpenId = officialUser.getOpenid();
                String msg = className + reName + "邀请你加入" + newOrgName + "成员无课时间一览表";
                //此处要发送到消息队列，通知成员前往确认
                return R.ok().data("data",officialOpenId + "--" + msg);
            }

            try {
                makeXls(mainerNo,xlsId,orgName,noList);
            }catch (Exception e){
                e.printStackTrace();
                return R.error();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        log.info("~~~~~");
        if(xlsId != 0){
            return R.ok().data("data",xlsId);
        }else {
            throw new CourseException(555,"try失败了");
        }
    }

    public void makeXls (String mainNo,long xlsId,String orgName,List<String> noList) throws Exception{
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
        String path = pathName + orgName + xlsId + ".xls";
        File file = new File(path);
        boolean newFileFlag = file.createNewFile();
        if(newFileFlag){
            System.out.println("文件成功创建---"  +  file.getPath());
            FileOutputStream stream = FileUtils.openOutputStream(file);
            workbook.write(stream);
            stream.close();
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
