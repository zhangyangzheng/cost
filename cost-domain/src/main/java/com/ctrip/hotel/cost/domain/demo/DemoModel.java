package com.ctrip.hotel.cost.domain.demo;

import lombok.Builder;
import lombok.Data;

/**
 * @author yangzhengzhang
 * @description
 * @date 2022-09-30 13:08
 */
@Data
@Builder
public class DemoModel {
    private int domainId;

    private String name;
}
