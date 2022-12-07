package com.ctrip.hotel.cost.web;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.support.SpringBootServletInitializer;

@SpringBootApplication(scanBasePackages = {"com.ctrip.hotel.cost"})
//@EnableDalTransaction
public class CostWebApplication extends SpringBootServletInitializer {
  public static void main(String[] args) {
    SpringApplication.run(CostWebApplication.class, args);
  }

  @Override
  protected SpringApplicationBuilder configure(SpringApplicationBuilder springApplicationBuilder){
    return springApplicationBuilder.sources(CostWebApplication.class);
  }
}
