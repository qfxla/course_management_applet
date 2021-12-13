package com.haotongxue.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.haotongxue.entity.BigKind;
import com.haotongxue.entity.CollegeBigSmall;
import com.haotongxue.entity.Score;
import com.haotongxue.entity.SmallKind;
import com.haotongxue.excel.DemoData;
import com.haotongxue.exceptionhandler.CourseException;
import com.haotongxue.mapper.ScoreMapper;
import com.haotongxue.service.IBigKindService;
import com.haotongxue.service.ICollegeBigSmallService;
import com.haotongxue.service.IScoreService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.haotongxue.service.ISmallKindService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author DJT
 * @since 2021-12-07
 */
@Service
@Slf4j
public class ScoreServiceImpl extends ServiceImpl<ScoreMapper, Score> implements IScoreService {

    @Autowired
    IBigKindService bigKindService;

    @Autowired
    ISmallKindService smallKindService;

    @Autowired
    ICollegeBigSmallService collegeBigSmallService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveExcel(List<DemoData> cachedDataList, int collegeId,int grade) {
        for (DemoData demoData : cachedDataList){
            CollegeBigSmall collegeBigSmall = new CollegeBigSmall();
            collegeBigSmall.setCollegeId(collegeId);
            String bigKind = demoData.getBigKind();
            if ("体育类".equals(bigKind) || "小计".equals(bigKind)){
                continue;
            }
            if ("其他类".equals(bigKind)){
                demoData.setLowScore("0");
            }
            String smallKind = demoData.getSmallKind();
            QueryWrapper<BigKind> bigKindQueryWrapper = new QueryWrapper<>();
            bigKindQueryWrapper.select("big_id").eq("name",bigKind);
            BigKind one = bigKindService.getOne(bigKindQueryWrapper);
            if (one == null){
                one = new BigKind();
                one.setName(bigKind);
                if (!bigKindService.save(one)){
                    throw new CourseException(555,"保存大类表失败");
                }
            }
            collegeBigSmall.setBigId(one.getBigId());

            QueryWrapper<SmallKind> smallKindQueryWrapper = new QueryWrapper<>();
            smallKindQueryWrapper.select("samll_id").eq("name",smallKind);
            SmallKind smallKindOne = smallKindService.getOne(smallKindQueryWrapper);
            if (smallKindOne == null){
                smallKindOne =  new SmallKind();
                smallKindOne.setName(smallKind);
                if (!smallKindService.save(smallKindOne)){
                    throw new CourseException(555,"保存小类失败");
                }
            }
            collegeBigSmall.setSmallId(smallKindOne.getSamllId());
            collegeBigSmall.setGrade(grade);

            QueryWrapper<CollegeBigSmall> collegeBigSmallQueryWrapper = new QueryWrapper<>();
            collegeBigSmallQueryWrapper
                    .eq("college_id",collegeId)
                    .eq("big_id",collegeBigSmall.getBigId())
                    .eq("small_id",collegeBigSmall.getSmallId())
                    .eq("grade",grade);
            int count = collegeBigSmallService.count(collegeBigSmallQueryWrapper);
            if (count == 0){
                if (!collegeBigSmallService.save(collegeBigSmall)){
                    throw new CourseException(555,"保存CollegeBigSmall失败");
                }
            }
            Score score = new Score();
            score.setCollegeId(collegeId);
            score.setScore(demoData.getLowScore());
            score.setBigId(collegeBigSmall.getBigId());
            score.setGrade(grade);

            QueryWrapper<Score> scoreQueryWrapper = new QueryWrapper<>();
            scoreQueryWrapper.eq("college_id",collegeId)
                    .eq("big_id",collegeBigSmall.getBigId())
                    .eq("score",demoData.getLowScore())
                    .eq("grade",grade);
            int countScore = count(scoreQueryWrapper);
            if (countScore == 0){
                if (!save(score)){
                    throw new CourseException(555,"保存分数表失败");
                }
            }
        }
        log.info("插入成功----->");
    }
}
