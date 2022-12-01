package com.ctrip.hotel.cost.infrastructure.model.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class FgBackToAuditDto {
    Long orderId;
    Long fgId;
    Integer businessType;
    Character opType;
    String referenceId;
    Character isThrow;

    Long settlementId;
    Long hwpSettlementId;// 信用住订单结算单号
    Long orderInfoId;// 前置接口返回的OrderInfoID
    String pushReferenceId;// 抛结算抛单接口调用，接口头部ReferenceID
    String hwpReferenceId;// HWP接口调用，接口头部ReferenceID
    Boolean pushWalletPay;// 是否只推送闪住结算数据

    BigDecimal amount;
    BigDecimal cost;
    BigDecimal bidPrice;
    BigDecimal roomAmount;
    BigDecimal roomCost;
    BigDecimal promotionAmountHotel;
    BigDecimal promotionCostHotel;
    BigDecimal promotionAmountTrip;
    BigDecimal promotionCostTrip;

}
