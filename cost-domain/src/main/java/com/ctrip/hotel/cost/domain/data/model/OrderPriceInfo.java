package com.ctrip.hotel.cost.domain.data.model;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Calendar;

/**
 * @author yangzhengzhang
 * @description 价格数据
 * @date 2022-11-23 14:09
 */
@Data
public class OrderPriceInfo {
    private Integer room;
    private Calendar eTA;
    private Calendar eTD;
    private BigDecimal amount;
    private BigDecimal cost;
    private Integer minState;
    private Integer promoteType;
    private String priceType;
    private Integer breakfast;
    private BigDecimal customPayAmount;
}
