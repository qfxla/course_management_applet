package com.haotongxue.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.gargoylesoftware.htmlunit.Page;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.*;
import com.haotongxue.entity.User;
import com.haotongxue.entity.WeChatLoginResponse;
import com.haotongxue.exceptionhandler.CourseException;
import com.haotongxue.handler.WatchIsPaingHandler;
import com.haotongxue.mapper.*;
import com.haotongxue.service.EduLoginService;
import com.haotongxue.service.ICountDownService;
import com.haotongxue.service.IUserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.haotongxue.utils.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadLocalRandom;

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
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {

    @Autowired
    WeChatUtil weChatUtil;

    @Autowired
    RestTemplate restTemplate;

    @Autowired
    UserMapper userMapper;

    @Autowired
    IUserService userService;

    @Autowired
    EduLoginService eduLoginService;

    @Autowired
    WatchIsPaingHandler watchIsPaingHandler;

    @Autowired
    ICountDownService iCountDownService;

    @Override
    public WeChatLoginResponse getLoginResponse(String code) {
        String url = "https://api.weixin.qq.com/sns/jscode2session?appid="+weChatUtil.getAppid()+"" +
                "&secret="+weChatUtil.getSecret()+"" +
                "&js_code="+code+"" +
                "&grant_type=authorization_code";
        String response = restTemplate.getForObject(url,String.class);
        WeChatLoginResponse loginResponse = JSON.parseObject(response, WeChatLoginResponse.class);
        Integer errcode = loginResponse.getErrcode();
        if (errcode != null && errcode != 0){
            CourseException courseException = new CourseException();
            courseException.setCode(20001);
            if (errcode == -1){
                courseException.setMsg("系统繁忙");
            }else if (errcode == 40029){
                courseException.setMsg("code无效");
            }else if (errcode == 45011){
                courseException.setMsg("频率限制，每个用户每分钟100次");
            }else if (errcode == 40226){
                courseException.setMsg("高风险等级用户，小程序登录拦截");
            }else {
                courseException.setMsg("code有误");
            }
            throw courseException;
        }
        return loginResponse;
    }

    @Override
    public void triggerSearchCountDown(String currentOpenid,WebClient webClient) {
        QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
        userQueryWrapper.select("no","password").eq("openid",currentOpenid);
        User user = userService.getOne(userQueryWrapper);
        log.info("----->"+currentOpenid+"新用户触发了查考试倒计时和选课");
        try {
            iCountDownService.searchOptionCourse(currentOpenid,webClient);
        }catch (Exception e){

        }
        try {
            iCountDownService.searchCountDown(currentOpenid,webClient);
        }catch (Exception e){

        }

    }

    @Override
    public boolean studentEvaluate(WebClient webClient) {
        try {
            HtmlPage htmlPage = webClient.getPage("http://edu-admin.zhku.edu.cn/jsxsd/xspj/xspj_find.do");
            //System.out.println(htmlPage.asXml());
            List<HtmlElement> table = htmlPage.getByXPath("//table[@class='layui-table']");
            DomNodeList<HtmlElement> trList = table.get(0).getElementsByTagName("tr");
            for (int i=1;i<trList.size();i++){
                HtmlElement tr = trList.get(i);
                DomNodeList<HtmlElement> tdList = tr.getElementsByTagName("td");
                //获取倒数第二个
                DomNode isEvaluate = tdList.get(tdList.size() - 2);
                //System.out.println(isEvaluate.asText());
                if ("否".equals(isEvaluate.asText())){
                    System.out.println("-------->第一次点");
                    HtmlElement gotoEvaluate = tdList.get(tdList.size() - 1);
                    DomNodeList<HtmlElement> a = gotoEvaluate.getElementsByTagName("a");
                    HtmlPage evaluatePage = a.get(0).click();
                    //System.out.println(evaluatePage.asXml());
                    HtmlElement dataList = evaluatePage.getHtmlElementById("dataList");
                    DomNodeList<HtmlElement> dataListTrList = dataList.getElementsByTagName("tr");
                    for (int j=1;j<dataListTrList.size();j++){
                        HtmlElement trNode = dataListTrList.get(j);
                        DomNodeList<HtmlElement> trTdList = trNode.getElementsByTagName("td");
                        HtmlElement isCommit = trTdList.get(trTdList.size() - 2);
                        if ("否".equals(isCommit.asText())){
                            HtmlElement evaluate = trTdList.get(trTdList.size() - 1);
                            DomNodeList<HtmlElement> aTwo = evaluate.getElementsByTagName("a");
                            HtmlPage afterClick = aTwo.get(0).click();
                            //System.out.println(afterClick.asText());
                        }
                        if (j == dataListTrList.size() - 1){
                            List<HtmlElement> pageCountDiv = evaluatePage.getByXPath("//div[@class='rt edu-pagination paginationDom']");
                            DomNodeList<HtmlElement> span = pageCountDiv.get(0).getElementsByTagName("span");
                            HtmlElement pageCountText = span.get(0);
                            String[] split = pageCountText.asText().split("/");
                            String[] preSplit = split[0].split("页");
                            String[] afterSplit = split[1].split("页");
                            int preCount = Integer.valueOf(preSplit[0]);
                            int afterCount = Integer.valueOf(afterSplit[0]);
                            if (preCount != afterCount){
                                List<HtmlElement> iList = evaluatePage.getByXPath("//i[@class='rt edu-pagination paginationDom']");
                                HtmlElement nextPage = iList.get(0);
                                HtmlPage click = nextPage.click();
                                evaluatePage = click;
                                dataListTrList = click.getHtmlElementById("dataList");
                                j = 1;
                            }
                        }
                    }
                }
//                if (i == trList.size() - 1){
//                    List<HtmlElement> pageCountDiv = htmlPage.getByXPath("//div[@class='rt edu-pagination paginationDom']");
//                    DomNodeList<HtmlElement> span = pageCountDiv.get(0).getElementsByTagName("span");
//                    HtmlElement pageCountText = span.get(0);
//                    System.out.println(pageCountText.asText());
//                    String[] split = pageCountText.asText().split("/");
//                    String[] preSplit = split[0].split("页");
//                    String[] afterSplit = split[1].split("页");
//                    int preCount = Integer.valueOf(preSplit[0]);
//                    int afterCount = Integer.valueOf(afterSplit[0]);
//                    if (preCount != afterCount){
//
//                    }
//                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }


    public void beginEva(HtmlPage page){
        List<HtmlElement> radios = page.getByXPath("//label/input");
        int count = 0;
        try {
            for (int i = 0; i < radios.size() / 4; i++) {  //0~11
                int choice = ThreadLocalRandom.current().nextInt(2);    //随机选择“完全同意”或“比较同意”
                for (int j = 0; j < 4; j++) {  //0~3
                    if(j == choice){
                        System.out.println("第" + (i+1) + "道题，" + "选择了：" + choice);
                        int index = (4*(i+1)-1)-(4-(j+1));
                        HtmlElement element = radios.get(index);
                        element.click();
                        break;
                    }
                }
                count++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println(UserContext.getCurrentOpenid() + "---总共" + count + "道题");
        DomElement tj = page.getHtmlElementById("tj");
        Page submit = null;
        try {
            submit = tj.click();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
