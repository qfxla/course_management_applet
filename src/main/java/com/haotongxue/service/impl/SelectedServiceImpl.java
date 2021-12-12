package com.haotongxue.service.impl;

import com.haotongxue.controller.SelectedController;
import com.haotongxue.entity.Selected;
import com.haotongxue.entity.vo.SelectedRuleVo;
import com.haotongxue.entity.vo.SelectedVo;
import com.haotongxue.entity.vo.SmallKindVo;
import com.haotongxue.mapper.CollegeBigSmallMapper;
import com.haotongxue.mapper.SelectedMapper;
import com.haotongxue.service.ISelectedService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
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

    @Override
    public List<SelectedRuleVo> getSelected(int collegeId,String openid) throws InterruptedException {
        List<SelectedVo> selectedVoList = selectedMapper.myChoice(openid);
        List<SelectedRuleVo> ruleList = selectedMapper.rule(collegeId);
        CountDownLatch countDownLatch = new CountDownLatch(ruleList.size());
        try {
            for (SelectedRuleVo rule : ruleList) {
                executorService.execute(new Runnable() {
                    @Override
                    public void run() {
                        List<SmallKindVo> smallKindVos = selectedMapper.ruleSmallKind(rule.getCollegeId(), rule.getBigId());
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
    public List<SelectedVo> getInvalidSelected(int collegeId,String openid) {
        List<Integer> invalidSmallId = collegeBigSmallMapper.getInvalidSmallId(collegeId);
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
}
