package com.ctrip.hotel.cost.domain.data.model;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @author yangzhengzhang
 * @description 过时扣款
 * @date 2022-11-23 11:30
 */
@Data
public class OutTimeDeductInfo {
    private BigDecimal outTimeDeductAmount;
    private BigDecimal outTimeDeductValue;
    private BigDecimal outTimeDeductCost;
    private Integer outTimeDeductType;
}
