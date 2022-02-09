package com.haotongxue.utils;

import com.haotongxue.cacheUtil.LoadingRedisCache;
import com.haotongxue.entity.Concern;
import com.haotongxue.entity.vo.ESVO;
import com.haotongxue.entity.vo.ESWithHighLightVO;
import com.haotongxue.entity.vo.IsConcernVO;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@Component
public class ESUtils {

    @Resource(name = "isConcernCache")
    LoadingRedisCache<Concern> isConcernCache;

    /**
     * 有高亮的转换
     * @param hits
     * @return
     */
    public static List<ESWithHighLightVO> transform(SearchHit[] hits){
        List<ESWithHighLightVO> list = new LinkedList<>();
        for (SearchHit hit : hits){
            list.add(transformToHighLightVO(hit));
        }
        return list;
    }

    /**
     * 普通转换
     * @param hits
     * @return
     */
    public static List<ESVO> transformNormal(SearchHit[] hits){
        List<ESVO> list = new LinkedList<>();
        for (SearchHit hit : hits){
            list.add(transformToESVO(hit));
        }
        return list;
    }

    public static ESVO transformToESVO(SearchHit hit){
        ESVO esvo = new ESVO();
        esvo.setSource(hit.getSourceAsMap());
        return esvo;
    }

    public static ESWithHighLightVO transformToHighLightVO(SearchHit hit){
        ESWithHighLightVO vo = new ESWithHighLightVO();
        vo.setSource(hit.getSourceAsMap());
        vo.setHighLight(new HashMap<>());
        Map<String, HighlightField> highlightFields = hit.getHighlightFields();
        for (Map.Entry<String,HighlightField> entry : highlightFields.entrySet()){
            String key = entry.getKey();
            HighlightField value = entry.getValue();
            Text[] fragments = value.getFragments();
            vo.getHighLight().put(key,fragments[0].string());
        }
        return vo;
    }

    /**
     * 转换为IsConcernVO
     * @param no
     * @param hits
     * @param isHighLight 是否转换为高亮
     * @return
     */
    public List<IsConcernVO> transformIsConcern(String no,SearchHit[] hits,boolean isHighLight){
        LinkedList<IsConcernVO> isConcernVOS = new LinkedList<>();
        for (SearchHit hit : hits){
            IsConcernVO isConcernVO = new IsConcernVO();
            if (isHighLight){
                isConcernVO.setESObject(transformToHighLightVO(hit));
            }else {
                isConcernVO.setESObject(transformToESVO(hit));
            }
            String targetNo = (String) hit.getSourceAsMap().get("no");
            isConcernVO.setConcern(isConcernCache.get(no+targetNo) != null);
            isConcernVOS.add(isConcernVO);
        }
        return isConcernVOS;
    }

    public static ESVO transformNormalOne(SearchHit[] hits){
        ESVO esvo = new ESVO();
        esvo.setSource(hits[0].getSourceAsMap());
        return esvo;
    }

}
