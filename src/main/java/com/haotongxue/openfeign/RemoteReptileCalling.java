package com.haotongxue.openfeign;

import com.haotongxue.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Component
@FeignClient(name = "reptile")
@RequestMapping("/reptile")
public interface RemoteReptileCalling {
    @PostMapping("/all")
    public R reptileAll(@RequestParam String currentOpenid);
}
