package com.haotongxue.openfeign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;

@Component
@FeignClient
public interface RemoteCalling {

}
