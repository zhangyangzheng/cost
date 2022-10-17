package com.ctrip.hotel.cost.web;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"com.ctrip.hotel.cost"})
//@EnableDalTransaction
public class CostWebApplication {
  public static void main(String[] args) {
    SpringApplication.run(CostWebApplication.class, args);
  }
}
