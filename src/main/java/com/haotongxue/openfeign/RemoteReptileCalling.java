package com.haotongxue.openfeign;

import com.haotongxue.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Component
@FeignClient(name = "reptileTwo")
@RequestMapping("/reptile")
public interface RemoteReptileCalling {
    /**
     * 爬取所有东西
     * @param currentOpenid
     * @return
     */
    @PostMapping("/all")
    public R reptileAll(@RequestParam String currentOpenid);

    /**
     * 爬取课表
     * @param currentOpenid
     * @return
     */
    @PostMapping("/course")
    public R reptileCourse(@RequestParam String currentOpenid);
}
