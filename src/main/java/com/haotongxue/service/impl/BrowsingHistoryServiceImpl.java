package com.haotongxue.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.haotongxue.entity.BrowsingHistory;
import com.haotongxue.entity.vo.BrowsingHistoryVO;
import com.haotongxue.entity.vo.BrowsingHistoryVOList;
import com.haotongxue.entity.vo.ESVO;
import com.haotongxue.mapper.BrowsingHistoryMapper;
import com.haotongxue.service.IBrowsingHistoryService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.haotongxue.service.IStudentStatusService;
import com.haotongxue.utils.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author DJT
 * @since 2022-01-21
 */
@Service
public class BrowsingHistoryServiceImpl extends ServiceImpl<BrowsingHistoryMapper, BrowsingHistory> implements IBrowsingHistoryService {

    @Autowired
    IStudentStatusService studentStatusService;

    @Override
    public BrowsingHistoryVOList sliceByCreateTime(QueryWrapper<BrowsingHistory> wrapper,boolean isMyRead) {
        List<BrowsingHistory> list = list(wrapper);
        Map<String,List<ESVO>> map = new LinkedHashMap<>();
        for (BrowsingHistory browsingHistory : list){
            String searchNo;
            if (isMyRead){
                searchNo = browsingHistory.getReadedNo();
            }else {
                searchNo = browsingHistory.getReadNo();
            }
            LocalDateTime createTime = browsingHistory.getCreateTime();
            String dateStr = createTime.toLocalDate().toString();
            List<ESVO> esvoList = map.computeIfAbsent(dateStr, k -> new ArrayList<>());
            try {
                esvoList.add(studentStatusService.getStudent(searchNo));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        BrowsingHistoryVOList browsingHistoryVOList = new BrowsingHistoryVOList();
        map.forEach((s, esvos) -> {
            BrowsingHistoryVO browsingHistoryVO = new BrowsingHistoryVO();
            browsingHistoryVO.setDate(s);
            browsingHistoryVO.setEsvoList(esvos);
            browsingHistoryVOList.getList().add(browsingHistoryVO);
        });
        return browsingHistoryVOList;
    }
}
