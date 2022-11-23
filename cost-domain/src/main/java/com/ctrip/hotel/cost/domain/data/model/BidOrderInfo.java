package com.ctrip.hotel.cost.domain.data.model;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Calendar;

/**
 * @author yangzhengzhang
 * @description 云梯数据
 * @date 2022-11-23 14:11
 */
@Data
public class BidOrderInfo {
    private Long bidOrderID;
    private Calendar beginDate;
    private Calendar endDate;
    private BigDecimal orgCost;
    private BigDecimal bidCostEffort;
    private String tradNo;
    private String platform;
    private String ztcNo;
    private BigDecimal rate;
    private BigDecimal biddenCost;
    private Integer bidPayType;
}
