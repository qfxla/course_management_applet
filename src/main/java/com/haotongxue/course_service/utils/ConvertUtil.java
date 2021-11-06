package com.haotongxue.course_service.utils;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.BeanUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author run
 * @since 2021/3/21 10:57
 */
public class ConvertUtil {

    public static <T,S> List<T> convert(List<S> source, Class<? extends T> targetClass){
        List<T> voList = new ArrayList<>();
        source.forEach(po -> {
            T vo = null;
            try {
                vo = convert(po,targetClass);
            } catch (Exception e) {
                e.printStackTrace();
            }
            voList.add(vo);
        });
        return voList;
    }

    public static <T,S> IPage<T> convert(IPage<S> source, Class<? extends T> targetClass){
        IPage<T> voiPage = new Page<>();
        BeanUtils.copyProperties(source,voiPage);
        List<T> records = new ArrayList<>();
        source.getRecords().forEach(po -> {
            T vo = null;
            try {
                vo = convert(po,targetClass);
            } catch (Exception e) {
                e.printStackTrace();
            }
            records.add(vo);
        });
        return voiPage.setRecords(records);
    }

    public static <T,S> T convert(S source,Class<? extends T> targetClass) throws Exception {
        if (source == null){
            return null;
        }
        T vo = create(targetClass);
        BeanUtils.copyProperties(source,vo);
        return vo;
    }

    private static <T> T create(Class<? extends T> targetClass) throws Exception {
        T vo = null;
        try {
            vo = targetClass.newInstance();
        } catch (Exception e) {
            throw new Exception("数据转换异常");
        }
        return vo;
    }


}