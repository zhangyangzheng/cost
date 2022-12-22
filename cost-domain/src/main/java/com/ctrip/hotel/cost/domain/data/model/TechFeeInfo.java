package com.ctrip.hotel.cost.domain.data.model;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @author yangzhengzhang
 * @description 技术服务费
 * @date 2022-11-23 11:32
 */
@Data
public class TechFeeInfo {
    private String techFeeType;
    private BigDecimal zeroCommissionFeeRatio;
}
