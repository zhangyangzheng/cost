package com.ctrip.hotel.cost.domain.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author yangzhengzhang
 * @description
 * @date 2022-09-29 20:30
 */
@Component
public class DemoDO {

    @Autowired
    private DemoModelRepository testModelRepository;

    public DemoModel creatTest() {
        return testModelRepository.getTestModel();
    }

}
