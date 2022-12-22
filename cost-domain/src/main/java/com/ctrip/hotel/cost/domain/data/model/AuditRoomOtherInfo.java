package com.ctrip.hotel.cost.domain.data.model;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @author yangzhengzhang
 * @description 审核房间其它信息
 * @date 2022-11-23 13:58
 */
@Data
public class AuditRoomOtherInfo {
    private Integer fgid;
    private Integer hotelConfirmStatus;
    private BigDecimal adjustCommission;
    private Integer adjustCommissionType;
    private String adjustRemark;
    private Long settlementBatchID;
    private BigDecimal actualAmount;
    private BigDecimal actualCost;
    private BigDecimal counterFee;
    private String counterFeeCurrency;
    private String payCurrency;
    private String htlCurrency;
    private BigDecimal otherCost;
}
