package com.ctrip.hotel.cost.web.controller;

import com.ctrip.hotel.cost.application.demo.DemoService;
import com.ctrip.hotel.cost.web.model.DemoRequest;
import com.ctrip.hotel.cost.web.model.DemoResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author yangzhengzhang
 * @description
 * @date 2022-09-29 13:56
 */
@Api(tags="例子")
@RestController
@RequestMapping("/test")
public class TestDemoController {

    @Autowired
    private DemoService demoService;

    @ApiOperation("接口1")
    @RequestMapping(value = "/client", method = RequestMethod.POST)
    public DemoResponse testClient(@RequestBody DemoRequest request) {
        return DemoResponse.builder()
                .responseId(1L)
                .responseName("test名称")
                .build();
    }

}
