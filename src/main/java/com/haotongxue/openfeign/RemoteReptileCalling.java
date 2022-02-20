package com.haotongxue.openfeign;

import com.gargoylesoftware.htmlunit.WebClient;
import com.haotongxue.entity.User;
import com.haotongxue.utils.LoginUtils;
import com.haotongxue.utils.R;
import com.haotongxue.utils.WebClientUtils;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Component
@FeignClient(name = "reptile")
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
    @PutMapping("/course")
    public R getCourseBySchoolWebsite(@RequestParam String currentOpenid);
}
