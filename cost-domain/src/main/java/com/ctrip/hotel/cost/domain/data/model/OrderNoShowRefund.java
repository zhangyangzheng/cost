package com.ctrip.hotel.cost.domain.data.model;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Calendar;

/**
 * @author yangzhengzhang
 * @description NoShow扣客人明细
 * @date 2022-11-23 13:47
 */
@Data
public class OrderNoShowRefund {
    private Integer fgid;
    private Calendar refundDay;
    private BigDecimal amount;
    private BigDecimal cost;
    private Integer refundType;
}
