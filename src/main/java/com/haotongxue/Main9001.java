package com.haotongxue;

import com.haotongxue.utils.GetBeanUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.ConfigurableApplicationContext;

@EnableDiscoveryClient
@SpringBootApplication
public class Main9001 {

    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(Main9001.class, args);
        GetBeanUtil getBeanUtil = new GetBeanUtil();
        getBeanUtil.setApplicationContext(context);
    }

}
