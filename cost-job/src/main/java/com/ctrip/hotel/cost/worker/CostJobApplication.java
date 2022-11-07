package com.ctrip.hotel.cost.worker;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author yangzhengzhang
 * @description
 * @date 2022-09-28 16:00
 */
@SpringBootApplication(scanBasePackages = {"com.ctrip.hotel.cost.*"})
public class CostJobApplication {
    public static void main(String[] args) {
        SpringApplication.run(CostJobApplication.class, args);
    }
}
