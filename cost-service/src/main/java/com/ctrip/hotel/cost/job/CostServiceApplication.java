package com.ctrip.hotel.cost.job;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author yangzhengzhang
 * @description
 * @date 2022-09-28 16:00
 */
@SpringBootApplication(scanBasePackages = {"com.ctrip.hotel.cost"})
public class CostServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(CostServiceApplication.class, args);
    }
}
