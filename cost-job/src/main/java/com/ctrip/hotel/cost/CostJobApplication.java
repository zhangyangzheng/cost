package com.ctrip.hotel.cost;

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
public class CostJobApplication extends SpringBootServletInitializer {
    public static void main(String[] args) {
        SpringApplication.run(CostJobApplication.class, args);
    }

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder springApplicationBuilder){
        return springApplicationBuilder.sources(CostJobApplication.class);
    }
}
