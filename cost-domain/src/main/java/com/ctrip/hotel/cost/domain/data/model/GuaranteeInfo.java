package com.ctrip.hotel.cost.domain.data.model;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @author yangzhengzhang
 * @description 担保
 * @date 2022-11-23 11:29
 */
@Data
public class GuaranteeInfo {
    private Integer isGuarantee;
    private Integer guaranteeType;
    private String paymentGuaranteePoly;
    private BigDecimal guaranteeCNYAmount;
    private BigDecimal guaranteeAmountCus;
    private String guarantee;
    private String allNeedGuarantee;
    private String guaranteeWay;
}
