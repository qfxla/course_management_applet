package com.haotongxue.utils;

import com.haotongxue.entity.vo.ESVO;
import com.haotongxue.entity.vo.ESWithHighLightVO;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class ESUtils {

    /**
     * 有高亮的转换
     * @param hits
     * @return
     */
    public static List<ESWithHighLightVO> transform(SearchHit[] hits){
        List<ESWithHighLightVO> list = new LinkedList<>();
        for (SearchHit hit : hits){
            ESWithHighLightVO studentSearchVO = new ESWithHighLightVO();
            studentSearchVO.setSource(hit.getSourceAsMap());
            studentSearchVO.setHighLight(new HashMap<>());
            Map<String, HighlightField> highlightFields = hit.getHighlightFields();
            for (Map.Entry<String,HighlightField> entry : highlightFields.entrySet()){
                String key = entry.getKey();
                HighlightField value = entry.getValue();
                Text[] fragments = value.getFragments();
                studentSearchVO.getHighLight().put(key,fragments[0].string());
            }
            list.add(studentSearchVO);
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
            ESVO esvo = new ESVO();
            esvo.setSource(hit.getSourceAsMap());
            list.add(esvo);
        }
        return list;
    }

    public static ESVO transformNormalOne(SearchHit[] hits){
        ESVO esvo = new ESVO();
        esvo.setSource(hits[0].getSourceAsMap());
        return esvo;
    }

}
