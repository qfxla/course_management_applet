package com.haotongxue.service.impl;

import com.baomidou.mybatisplus.core.MybatisConfiguration;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.haotongxue.controller.SelectedController;
import com.haotongxue.entity.Selected;
import com.haotongxue.entity.SmallKind;
import com.haotongxue.entity.vo.SelectedRuleVo;
import com.haotongxue.entity.vo.SelectedVo;
import com.haotongxue.entity.vo.SmallKindVo;
import com.haotongxue.mapper.CollegeBigSmallMapper;
import com.haotongxue.mapper.SelectedMapper;
import com.haotongxue.service.ISelectedService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.haotongxue.service.ISmallKindService;
import com.haotongxue.utils.UserContext;
import com.haotongxue.utils.WhichGrade;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.swing.plaf.TableUI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author DJT
 * @since 2021-12-06
 */
@Service
public class SelectedServiceImpl extends ServiceImpl<SelectedMapper, Selected> implements ISelectedService {
    private static Logger logger = LoggerFactory.getLogger(SelectedServiceImpl.class);

    @Autowired
    SelectedMapper selectedMapper;

    ExecutorService executorService = Executors.newCachedThreadPool();

    @Autowired
    CollegeBigSmallMapper collegeBigSmallMapper;
    @Autowired
    ISmallKindService iSmallKindService;

    @Override
    public List<SelectedRuleVo> getSelected(int collegeId,String openid,int grade,String no) throws InterruptedException {
        List<SelectedVo> selectedVoList = selectedMapper.myChoice(openid);


        //16版的轻工分专业
        if (no.substring(5,8).equals("072") || no.substring(5,8).equals("073")){
            List<SelectedRuleVo> qingGongRule = isQingGong(openid);
            return qingGongRule;
        }

        List<SelectedRuleVo> ruleList = selectedMapper.rule(collegeId,grade);
        CountDownLatch countDownLatch = new CountDownLatch(ruleList.size());
        try {
            for (SelectedRuleVo rule : ruleList) {
                executorService.execute(new Runnable() {
                    @Override
                    public void run() {
                        List<SmallKindVo> smallKindVos = selectedMapper.ruleSmallKind(rule.getCollegeId(), rule.getBigId(),grade);
                        for (SmallKindVo smallKindVo : smallKindVos) {
                            List<SelectedVo> selectList = new ArrayList<>();
                            for (SelectedVo myChoice : selectedVoList) {
                                if (smallKindVo.getSmallId() == myChoice.getSmallId()){
                                    selectList.add(myChoice);
                                }
                            }
                            smallKindVo.setSelectedVoList(selectList);
                        }
                        rule.setSmallVo(smallKindVos);

                        float iHave = 0;
                        //根据小类判断自己的所选科目是不是归在这里面的,是的话这个大类的得分就加
                        for (SmallKindVo smallKindVo : smallKindVos) {
                            for (SelectedVo selectedVo : selectedVoList) {
                                if (smallKindVo.getSmallId() == selectedVo.getSmallId()){
                                    iHave += selectedVo.getSelectedScore();
                                }
                            }
                        }
                        rule.setIHave(iHave);
                        countDownLatch.countDown();
                    }
                });
            }
        }finally {
//            executorService.shutdown();
        }
        countDownLatch.await();
        return ruleList;
    }

    @Override
    public List<SelectedVo> getInvalidSelected(int collegeId,String openid,int grade) {
        List<Integer> invalidSmallId = collegeBigSmallMapper.getInvalidSmallId(collegeId,grade);
        List<SelectedVo> result = new ArrayList<>();
        List<SelectedVo> selectedVoList = selectedMapper.myChoice(openid);

        for (Integer smallId : invalidSmallId) {
            for (SelectedVo selectedVo : selectedVoList) {
                if (selectedVo.getSmallId() == smallId){
                    result.add(selectedVo);
                }
            }
        }
        return result;
    }


    //轻工特殊判断
    public List<SelectedRuleVo> isQingGong(String openId){
        List<SelectedVo> selectedVoList = selectedMapper.myChoice(openId);

        List<SelectedRuleVo> ruleList = new ArrayList<>();
        SelectedRuleVo object1 = new SelectedRuleVo();
        object1.setCollegeId(07);
        object1.setBigName("创新创业教育类");
        object1.setBigId(20);
        object1.setScore("2");

        SelectedRuleVo object2 = new SelectedRuleVo();
        object2.setCollegeId(07);
        object2.setBigName("人文社科类");
        object2.setBigId(21);
        object2.setScore("6");

        SelectedRuleVo object3 = new SelectedRuleVo();
        object3.setCollegeId(07);
        object3.setBigName("艺术类");
        object3.setBigId(22);
        object3.setScore("1");

        SelectedRuleVo object4 = new SelectedRuleVo();
        object4.setCollegeId(07);
        object4.setBigName("心理健康类");
        object4.setBigId(34);
        object4.setScore("1");

        SelectedRuleVo object5 = new SelectedRuleVo();
        object5.setCollegeId(07);
        object5.setBigName("其他类");
        object5.setBigId(25);
        object5.setScore("0");

        ConcurrentHashMap<String, List<Integer>> map = new ConcurrentHashMap<>();
        map.put("20",new ArrayList<>(Arrays.asList(2)));
        map.put("21",new ArrayList<>(Arrays.asList(8,3,7)));
        map.put("22",new ArrayList<>(Arrays.asList(1)));
        map.put("34",new ArrayList<>(Arrays.asList(26)));
        map.put("25",new ArrayList<>(Arrays.asList(5,9)));


        ruleList.add(object1);
        ruleList.add(object2);
        ruleList.add(object3);
        ruleList.add(object4);
        ruleList.add(object5);

        for (SelectedRuleVo rule : ruleList) {
            List<Integer> smallIds = map.get(rule.getBigId().toString());
            List<SmallKindVo> smallKindVos = new ArrayList<>();
            for (Integer smallId : smallIds) {
                SmallKind smallKind = iSmallKindService.getById(smallId);
                SmallKindVo smallKindVo = new SmallKindVo();
                smallKindVo.setSmallId(smallKind.getSamllId());
                smallKindVo.setSmallName(smallKind.getName());
                smallKindVos.add(smallKindVo);
            }

            for (SmallKindVo smallKindVo : smallKindVos) {
                List<SelectedVo> selectList = new ArrayList<>();
                for (SelectedVo myChoice : selectedVoList) {
                    if (smallKindVo.getSmallId() == myChoice.getSmallId()){
                        selectList.add(myChoice);
                    }
                }
                smallKindVo.setSelectedVoList(selectList);
            }
            rule.setSmallVo(smallKindVos);

            float iHave = 0;
            for (SmallKindVo smallKindVo : smallKindVos) {
                for (SelectedVo selectedVo : selectedVoList) {
                    if (smallKindVo.getSmallId() == selectedVo.getSmallId()){
                        iHave += selectedVo.getSelectedScore();
                    }
                }
            }
            rule.setIHave(iHave);
        }
        return ruleList;

    }
}

