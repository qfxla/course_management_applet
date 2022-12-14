package com.haotongxue.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.haotongxue.entity.*;
import com.haotongxue.entity.Class;
import com.haotongxue.entity.dto.InviteMeDTO;
import com.haotongxue.entity.dto.MemberDTO;
import com.haotongxue.entity.dto.MyOragDTO;
import com.haotongxue.entity.dto.OragDTO;
import com.haotongxue.exceptionhandler.CourseException;
import com.haotongxue.mapper.*;
import com.haotongxue.service.IUserService;
import com.haotongxue.utils.R;
import com.haotongxue.utils.SnowflakeIdWorker;
import com.haotongxue.utils.UserContext;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.util.CellRangeAddress;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.util.ArrayList;
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
    public static final String[] weekArr = new String[]{"","???","???","???","???","???","???","???"};
    public static final String[] seArr = new String[]{"1-2???","3-4???","6-7???","8-9???","10-12???"};
    public static final int[] secArr = new int[]{1,3,6,8,10};
//    public static final String pathName = "X:/";
    public static final String pathName = "/root/timeoffxls";
    public static final String suffix = "?????????????????????????????????????????????????????????????????????";

//    @RequestMapping("/downloadXls")
//    public R downloadXls(String xlsId, HttpServletResponse response) {
//    }

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
                        weekStr = getWeekStr(hasCourseWeekList) + "??????";
                    }else {
                        weekStr = "??????";
                    }
                    hasCourseArr[n][i-1] = weekStr;
                }
            }
            MemberDTO memberDTO = new MemberDTO();
            memberDTO.setRealName(realName);
            memberDTO.setHasCourseArr(hasCourseArr);
            System.out.println(memberDTO);
            redisTemplate.opsForValue().set(key,memberDTO);
            redisTemplate.expire(key, 3, TimeUnit.DAYS);
            return R.ok();
        }
    }

    @GetMapping("/getMyCreated")
    public R getMyCreated(){
        String openId = UserContext.getCurrentOpenid();
//        String openId = "ohpVk5TmJDKSy5Wm3rGAvLQnUneQ";
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
        String openId = UserContext.getCurrentOpenid();
//        String openId = "ohpVk5TmJDKSy5Wm3rGAvLQnUneQ";
//        String openId = "ohpVk5RM2Mn6d1FlKBNQA1FuWtcU";
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("openid",openId).select("no");
        User user = userMapper.selectOne(queryWrapper);
        String no = user.getNo();
        QueryWrapper<Invite> queryWrapper1 = new QueryWrapper<>();
        queryWrapper1.eq("no",no);
        List<Invite> inviteList = inviteMapper.selectList(queryWrapper1);
        List<InviteMeDTO> inviteMeDTOList = new ArrayList<>();
        for (Invite invite : inviteList) {
            String xlsId = invite.getXlsId();
            QueryWrapper<Organization> queryWrapper2 = new QueryWrapper<>();
            queryWrapper2.eq("xls_id",xlsId);
            Organization organization = organizationMapper.selectOne(queryWrapper2);
            String mainNo = organization.getNo();
            QueryWrapper<StudentStatus> queryWrapper3 = new QueryWrapper<>();
            queryWrapper3.eq("no",mainNo).last("limit 1");
            StudentStatus studentStatus = studentStatusMapper.selectOne(queryWrapper3);
            String majorId = studentStatus.getMajorId();
            String classId = studentStatus.getClassId();
            QueryWrapper<Class> queryWrapper4 = new QueryWrapper<>();
            queryWrapper4.eq("class_id",classId).eq("major_id",majorId);
            Class aClass = classMapper.selectOne(queryWrapper4);
            String className = aClass.getName() + "???";
            String realName = studentStatus.getName();
            InviteMeDTO inviteMeDTO = new InviteMeDTO(className + realName, organization.getName(), invite.getStatus());
            inviteMeDTOList.add(inviteMeDTO);
        }
        return R.ok().data("data",inviteMeDTOList);
    }

    @GetMapping("/getMyMembers")
    public R getMyMembers(@RequestParam("xlsId") String xlsId){
        List<String> memberList = new ArrayList<>();
        QueryWrapper<Invite> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("xls_id",xlsId).select("no","status");
        List<Invite> inviteList = inviteMapper.selectList(queryWrapper);
        for (Invite invite : inviteList) {
            String no = invite.getNo();
            QueryWrapper<StudentStatus> queryWrapper3 = new QueryWrapper<>();
            queryWrapper3.eq("no",no).last("limit 1");
            StudentStatus studentStatus = studentStatusMapper.selectOne(queryWrapper3);
            String majorId = studentStatus.getMajorId();
            String classId = studentStatus.getClassId();
            QueryWrapper<Class> queryWrapper4 = new QueryWrapper<>();
            queryWrapper4.eq("class_id",classId).eq("major_id",majorId);
            Class aClass = classMapper.selectOne(queryWrapper4);
            String className = aClass.getName() + "???";
            String realName = studentStatus.getName();
            memberList.add(className + realName);
        }
        QueryWrapper<Organization> queryWrapper1 = new QueryWrapper<>();
        queryWrapper1.eq("xls_id",xlsId).last("limit 1");
        Organization organization = organizationMapper.selectOne(queryWrapper1);
        int ackNum = organization.getAckNum();
        int totalNum = organization.getTotalNum();
        MyOragDTO myOragDTO = new MyOragDTO(ackNum, totalNum, memberList);
        return R.ok().data("data",myOragDTO);
    }

    @PostMapping("/agreeInvited")   //202020834307
    public R agreeInvite(@RequestParam("xlsId") String xlsId){
        String openId = UserContext.getCurrentOpenid();
//        String openId = "ohpVk5TmJDKSy5Wm3rGAvLQnUneQ";
//        String openId = "ohpVk5RM2Mn6d1FlKBNQA1FuWtcU";
//        String openId = "ohpVk5StwY5t0z0xInelQ2Yx3OT4";   //haoran
//        String openId = "ohpVk5a0KIJzvoBrZ25Hu3cDXoyE";     //lena
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("openid",openId).select("no").last("limit 1");
        User user = userMapper.selectOne(queryWrapper);
        String no = user.getNo();
        QueryWrapper<Invite> queryWrapper1 = new QueryWrapper<>();
        queryWrapper1.eq("xls_id",xlsId).eq("no",no).last("limit 1");
        Invite invite = inviteMapper.selectOne(queryWrapper1);
        if(invite.getStatus() != 2){
            invite.setStatus(1);
            System.out.println(invite);
            inviteMapper.updateById(invite);
            System.out.println(xlsId);
            QueryWrapper<Organization> queryWrapper2 = new QueryWrapper<>();
            queryWrapper2.eq("xls_id",xlsId);
            Organization organization = organizationMapper.selectOne(queryWrapper2);
            System.out.println(organization);
            if(organization.getTotalNum() - organization.getAckNum() == 1){
                organization.setStatus(1);
//                String mainer_no = organization.getNo();
//                QueryWrapper<User> queryWrapper5 = new QueryWrapper<>();
//                queryWrapper5.eq("no",mainer_no).last("limit 1");
//                User mainUser = userMapper.selectOne(queryWrapper5);
//                String mainUserUnionId = mainUser.getUnionId();
//                QueryWrapper<OfficialUser> queryWrapper6 = new QueryWrapper<>();
//                queryWrapper6.eq("unionid",mainUserUnionId).select("openid");
//                OfficialUser officialUser = officialUserMapper.selectOne(queryWrapper6);
//                String orgName = "???" + organization.getName() + "???";
//                String officialOpenId = officialUser.getOpenid();
//                String msg = "????????????" + orgName + "????????????????????????????????????????????????????????????????????????";
//                //??????????????????????????????????????????????????????????????????????????????
//                System.out.println(msg);
            }
            if(organization.getAckNum() > organization.getTotalNum()){
                throw new CourseException(555,"??????????????????");
            }
            organization.setAckNum(organization.getAckNum() + 1);
            organizationMapper.updateById(organization);
            return R.ok();
        }else {
            throw new CourseException(555,"??????????????????????????????????????????");
        }
    }

    @PostMapping("/rejectInvited")  //201810214719
    public R rejectInvited(@RequestParam("xlsId") String xlsId){
        String openId = UserContext.getCurrentOpenid();
//        String openId = "ohpVk5TmJDKSy5Wm3rGAvLQnUneQ";
//        String openId = "ohpVk5a0KIJzvoBrZ25Hu3cDXoyE";
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
//        QueryWrapper<StudentStatus> queryWrapper3 = new QueryWrapper<>();
//        queryWrapper3.eq("no",no).last("limit 1");
//        StudentStatus studentStatus = studentStatusMapper.selectOne(queryWrapper3);
//        String majorId = studentStatus.getMajorId();
//        String classId = studentStatus.getClassId();
//        QueryWrapper<Class> queryWrapper4 = new QueryWrapper<>();
//        queryWrapper4.eq("class_id",classId).eq("major_id",majorId);
//        Class aClass = classMapper.selectOne(queryWrapper4);
//        String className = aClass.getName();
//        String realName = studentStatus.getName();
//        String orgName = "???" + organization.getName() + "???";
//        String mainer_no = organization.getNo();
//        QueryWrapper<User> queryWrapper5 = new QueryWrapper<>();
//        queryWrapper5.eq("no",mainer_no).last("limit 1");
//        User mainUser = userMapper.selectOne(queryWrapper5);
//        String mainUserUnionId = mainUser.getUnionId();
//        QueryWrapper<OfficialUser> queryWrapper6 = new QueryWrapper<>();
//        queryWrapper6.eq("unionid",mainUserUnionId).select("openid");
//        OfficialUser officialUser = officialUserMapper.selectOne(queryWrapper6);
//        String officialOpenId = officialUser.getOpenid();
//        String msg = className + realName + "????????????" + orgName + "????????????????????????????????????";
//        //????????????????????????????????????????????????xxx?????????
//        if(officialOpenId == null){
//            officialOpenId = "??????????????????";
//        }
//        System.out.println(officialOpenId + "---" + msg);
        return R.ok();
    }

    @GetMapping("/getTimeOff")
    public R getTimeOff(@RequestBody OragDTO oragDTO){
        String openId = UserContext.getCurrentOpenid();
//        String openId = "ohpVk5TmJDKSy5Wm3rGAvLQnUneQ";
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
            List<String> noList = oragDTO.getNoList();
            Organization organization = new Organization();
            organization.setName(orgName);
            organization.setXlsId(String.valueOf(xlsId));
            organization.setNo(mainerNo);
            organization.setStatus(0);
            organization.setAckNum(0);
            organization.setTotalNum(noList.size());
            organizationMapper.insert(organization);
//        ArrayList<User> failedUserList = new ArrayList<>();
//            String newOrgName = "???" + orgName + "???";
//            QueryWrapper<StudentStatus> queryWrapper3 = new QueryWrapper<>();
//            queryWrapper3.eq("openid",openId);
//            StudentStatus studentStatus = studentStatusMapper.selectOne(queryWrapper3);
//            String majorId = studentStatus.getMajorId();
//            String classId = studentStatus.getClassId();
//            String reName = studentStatus.getName();
//            QueryWrapper<Class> queryWrapper4 = new QueryWrapper<>();
//            queryWrapper4.eq("class_id",classId).eq("major_id",majorId);
//            Class aClass = classMapper.selectOne(queryWrapper4);
//            String className = aClass.getName() + "???";
            for (String no : noList) {
//                String officialOpenId;
//                String msg;
                Invite invite = new Invite(String.valueOf(xlsId),no,0);
                inviteMapper.insert(invite);
//                QueryWrapper<User> queryWrapper5 = new QueryWrapper<>();
//                queryWrapper5.eq("no",no).last("limit 1");
//                User user = userMapper.selectOne(queryWrapper5);
//                String unionId = user.getUnionId();
//                QueryWrapper<OfficialUser> queryWrapper6 = new QueryWrapper<>();
//                queryWrapper6.eq("unionid",unionId).select("openid");
//                OfficialUser officialUser = officialUserMapper.selectOne(queryWrapper6);
//                if(officialUser == null){
//                    failedUserList.add(user);
////                    officialOpenId = "??????????????????";
//                }else {
////                    officialOpenId = officialUser.getOpenid();
//                }
//                assert officialUser != null;
//                String officialOpenId = officialUser.getOpenid();
//                msg = className + reName + "???????????????" + newOrgName + "???????????????????????????";
//                //?????????????????????????????????????????????????????????
//                System.out.println(msg + "---" + officialOpenId);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        log.info("~~~~~");
        if(xlsId != 0){
//            HashMap<String, Object> map = new HashMap<>();
//            map.put("xlsId",xlsId);
//            map.put("failed",failedUserList);
            return R.ok().data("data",xlsId);
        }else {
            throw new CourseException(555,"try?????????");
        }
    }

    @PostMapping("/addMember")
    public R addMember(@RequestParam("no") String no,
                       @RequestParam("xlsId") String xlsId){
        QueryWrapper<Invite> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("xls_id",xlsId);
        Invite invite = new Invite(xlsId,no,0);
        int insert = inviteMapper.insert(invite);
        return R.ok();
    }

    @PostMapping("/delMember")
    public R delMember(@RequestParam("no") String no,
                       @RequestParam("xlsId") String xlsId){
        QueryWrapper<Invite> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("xls_id",xlsId).eq("no",no);
        int delete = inviteMapper.delete(queryWrapper);
        return R.ok();
    }

    @PostMapping("/simulatePay")
    public R simulatePay(@RequestParam("xlsIdStr") String xlsIdStr, HttpServletResponse response){
        QueryWrapper<Organization> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("xls_id", xlsIdStr);
        Organization organization = organizationMapper.selectOne(queryWrapper);
        organization.setStatus(2);
        organizationMapper.updateById(organization);
        if(organizationMapper.selectOne(queryWrapper).getStatus() != 2){
            return R.ok().data("data","????????????");
        }
        //???????????????????????????????????????????????????
        QueryWrapper<Organization> queryWrapper4 = new QueryWrapper<>();
        queryWrapper4.eq("xls_id", xlsIdStr);
        Organization organization1 = organizationMapper.selectOne(queryWrapper4);
        if(organization1.getAckNum() != organization1.getTotalNum() || organization1.getStatus() != 2){
            return R.error().data("data","????????? ??? ????????????!=?????????");
        }
        String orgName = organization1.getName();
        String fileName = orgName + xlsIdStr + ".xls";
        String path = pathName + fileName;
        List<String> noList = new ArrayList<>();
        QueryWrapper<Invite> queryWrapper1 = new QueryWrapper<>();
        queryWrapper1.eq("xls_id", xlsIdStr);
        List<Invite> inviteList = inviteMapper.selectList(queryWrapper1);
        for (Invite invite : inviteList) {
            noList.add(invite.getNo());
        }
        long xlsId = Long.parseLong(xlsIdStr);
        try {
            makeXls(xlsId,orgName,noList);
        }catch (Exception e){
            e.printStackTrace();
            return R.error();
        }
        try {
            // path????????????????????????????????????
            File file = new File(path);
            log.info(file.getPath());
            // ???????????????
            String filename = orgName + suffix + ".xls";
            // ?????????????????????
//            String ext = filename.substring(filename.lastIndexOf(".") + 1).toLowerCase();
//            log.info("??????????????????" + ext);

            // ????????????????????????
            FileInputStream fileInputStream = new FileInputStream(file);
            InputStream fis = new BufferedInputStream(fileInputStream);
            byte[] buffer = new byte[fis.available()];
            int read = fis.read(buffer);
            System.out.println("read---" + read);
            fis.close();

            // ??????response
            response.reset();
            // ??????response???Header
            response.setCharacterEncoding("UTF-8");
            //Content-Disposition???????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????
            //attachment???????????????????????????   inline??????????????????   "Content-Disposition: inline; filename=?????????.mp3"
            // filename?????????????????????????????????????????????????????????URL????????????????????????????????????????????????URL?????????????????????,????????????????????????????????????????????????????????????
            response.addHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(filename, "UTF-8"));
            // ??????????????????????????????
            response.addHeader("Content-Length", "" + file.length());
            OutputStream outputStream = new BufferedOutputStream(response.getOutputStream());
            response.setContentType("application/octet-stream");
            outputStream.write(buffer);
            outputStream.flush();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return R.ok().data("data","?????????????????????????????????");
    }

    public void makeXls (long xlsId,String orgName,List<String> noList) throws Exception{
        int count = noList.size();
        int totalRow = count * 5 + totalBeginNum + 4 + 1;   //61
        HSSFWorkbook workbook = new HSSFWorkbook();
        short colorIdx1 = IndexedColors.SKY_BLUE.getIndex();
        short colorIdx2 = IndexedColors.BLACK.getIndex();
        HSSFSheet sheet = workbook.createSheet();

        //??????????????????
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
        //??????????????????
        HSSFFont titleFont = workbook.createFont();
        titleFont.setFontHeightInPoints((short) 26);
        titleFont.setFontName("????????????");
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
        cell1.setCellValue("2021-2022-2??????" + orgName + "??????????????????????????????\"???????????????\"??????????????????");
        cell1.setCellStyle(titleStyle);

        CellStyle style = workbook.createCellStyle();
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        style.setAlignment(CENTER);
        HSSFFont xingqiFont = workbook.createFont();
        xingqiFont.setFontHeightInPoints((short) 24);
        xingqiFont.setFontName("????????????");
        xingqiFont.setBold(true);
        style.setFont(xingqiFont);
        style.setFillForegroundColor(colorIdx1);// ???????????????
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setBorderTop(MEDIUM);
        style.setTopBorderColor(colorIdx2);
        style.setBorderBottom(MEDIUM);
        style.setBottomBorderColor(colorIdx2);
        style.setBorderLeft(MEDIUM);
        style.setLeftBorderColor(colorIdx2);
        style.setBorderRight(MEDIUM);
        style.setRightBorderColor(colorIdx2);
        //??????????????????
        for (int i = titleNum; i < totalBeginNum; i++) {
            HSSFRow row = sheet.createRow(i);
            for (int j = 0; j < 8; j++) {
                HSSFCell cell = row.createCell(j);
                cell.setCellStyle(style);
            }
        }
        //??????????????????
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
                cell.setCellValue("??????" + weekArr[i]);
            }
        }
        HSSFFont sectionFont = workbook.createFont();
        sectionFont.setFontHeightInPoints((short) 15);
        sectionFont.setFontName("????????????");
        sectionFont.setBold(true);
        CellStyle secStyle = workbook.createCellStyle();
        secStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        secStyle.setAlignment(CENTER);
        secStyle.setFillForegroundColor(colorIdx1);// ???????????????
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
                    System.out.print("??????" + i + "???" + "???" + secArr[n] + "??????");
                    String str;
                    if(cacheFlag){
                        str = memberDTO.getHasCourseArr()[n][i-1];
                    }else {
                        List<Integer> hasCourseWeekList = userService.getHasCourseWeekList(no, i, secArr[n]);
                        String weekStr;
                        if(hasCourseWeekList != null && hasCourseWeekList.size() > 0){
                            weekStr = getWeekStr(hasCourseWeekList) + "??????";
                        }else {
                            //????????????????????????????????????
                            weekStr = "??????";
                        }
                        hasCourseArr[n][i-1] = weekStr;
                        str = weekStr;
                    }
                    HSSFCell cell2 = seRow.createCell(i);
                    cell2.setCellValue(realName + "???" + str);
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

        //??????????????????
        String path = pathName + orgName + xlsId + ".xls";
        File file = new File(path);
        boolean newFileFlag = file.createNewFile();
        if(newFileFlag){
            System.out.println("??????????????????---"  +  file.getPath());
            FileOutputStream stream = FileUtils.openOutputStream(file);
            workbook.write(stream);
            stream.close();
        }else {
            throw new CourseException(555,"????????????????????????");
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
        String sub3 = sub2.replace(" ","") + "???";
        return sub3;
    }
}
