package com.ctrip.hotel.cost.job;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.support.SpringBootServletInitializer;

/**
 * @author yangzhengzhang
 * @description
 * @date 2022-09-28 16:00
 */
@SpringBootApplication(scanBasePackages = {"com.ctrip.hotel.cost"})
public class CostServiceApplication extends SpringBootServletInitializer {
    public static void main(String[] args) {
        SpringApplication.run(CostServiceApplication.class, args);
    }

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder springApplicationBuilder){
        return springApplicationBuilder.sources(CostServiceApplication.class);
    }
}
