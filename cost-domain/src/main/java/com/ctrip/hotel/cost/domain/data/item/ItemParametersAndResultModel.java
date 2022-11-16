package com.ctrip.hotel.cost.domain.data.item;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Map;

/**
 * @author yangzhengzhang
 * @description
 * @date 2022-11-16 15:35
 */
@Data
@AllArgsConstructor
public class ItemParametersAndResultModel {
    private BigDecimal result;
    private Map<String, Object> parameters;
}
