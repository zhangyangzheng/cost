package com.ctrip.hotel.cost.domain.core;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author yangzhengzhang
 * @description
 * @date 2022-11-03 16:24
 */
@Data
@AllArgsConstructor
public class Factor {
    private String name;
    private BigDecimal value;
}
