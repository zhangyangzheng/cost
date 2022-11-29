package com.ctrip.hotel.cost.domain.common;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

/**
 * @author yangzhengzhang
 * @description
 * @date 2022-11-29 15:11
 */
@Data
public class ThreadLocalCostContext {
    private String linkTracing = "";
    private Map<String, String> tags = new HashMap<>();
}
