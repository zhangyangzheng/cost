package com.ctrip.hotel.cost.infrastructure.model.dto;

import java.math.BigDecimal;

public class FgBackToAuditDto {
    Long orderId;
    Long fgId;
    Integer businessType;
    Character opType;
    String referenceId;
    Character isThrow;

    Long SettlementId ;
    String PushReferenceID ;
    Long HWPSettlementId;
    Boolean PushWalletPay;
    Integer ResultCode;
    String HWPReferenceID;
    Long OrderInfoID;

    BigDecimal Amount;
    BigDecimal Cost;
    BigDecimal BidPrice;
    BigDecimal RoomAmount;
    BigDecimal RoomCost;
    BigDecimal PromotionAmountHotel;
    BigDecimal PromotionCostHotel;
    BigDecimal PromotionAmountTrip;
    BigDecimal PromotionCostTrip;

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public Long getFgId() {
        return fgId;
    }

    public void setFgId(Long fgId) {
        this.fgId = fgId;
    }

    public Integer getBusinessType() {
        return businessType;
    }

    public void setBusinessType(Integer businessType) {
        this.businessType = businessType;
    }

    public Character getOpType() {
        return opType;
    }

    public void setOpType(Character opType) {
        this.opType = opType;
    }

    public String getReferenceId() {
        return referenceId;
    }

    public void setReferenceId(String referenceId) {
        this.referenceId = referenceId;
    }

    public Character getIsThrow() {
        return isThrow;
    }

    public void setIsThrow(Character isThrow) {
        this.isThrow = isThrow;
    }

    public Long getSettlementId() {
        return SettlementId;
    }

    public void setSettlementId(Long settlementId) {
        SettlementId = settlementId;
    }

    public String getPushReferenceID() {
        return PushReferenceID;
    }

    public void setPushReferenceID(String pushReferenceID) {
        PushReferenceID = pushReferenceID;
    }

    public Long getHWPSettlementId() {
        return HWPSettlementId;
    }

    public void setHWPSettlementId(Long HWPSettlementId) {
        this.HWPSettlementId = HWPSettlementId;
    }

    public Boolean getPushWalletPay() {
        return PushWalletPay;
    }

    public void setPushWalletPay(Boolean pushWalletPay) {
        PushWalletPay = pushWalletPay;
    }

    public Integer getResultCode() {
        return ResultCode;
    }

    public void setResultCode(Integer resultCode) {
        ResultCode = resultCode;
    }

    public String getHWPReferenceID() {
        return HWPReferenceID;
    }

    public void setHWPReferenceID(String HWPReferenceID) {
        this.HWPReferenceID = HWPReferenceID;
    }

    public Long getOrderInfoID() {
        return OrderInfoID;
    }

    public void setOrderInfoID(Long orderInfoID) {
        OrderInfoID = orderInfoID;
    }

    public BigDecimal getAmount() {
        return Amount;
    }

    public void setAmount(BigDecimal amount) {
        Amount = amount;
    }

    public BigDecimal getCost() {
        return Cost;
    }

    public void setCost(BigDecimal cost) {
        Cost = cost;
    }

    public BigDecimal getBidPrice() {
        return BidPrice;
    }

    public void setBidPrice(BigDecimal bidPrice) {
        BidPrice = bidPrice;
    }

    public BigDecimal getRoomAmount() {
        return RoomAmount;
    }

    public void setRoomAmount(BigDecimal roomAmount) {
        RoomAmount = roomAmount;
    }

    public BigDecimal getRoomCost() {
        return RoomCost;
    }

    public void setRoomCost(BigDecimal roomCost) {
        RoomCost = roomCost;
    }

    public BigDecimal getPromotionAmountHotel() {
        return PromotionAmountHotel;
    }

    public void setPromotionAmountHotel(BigDecimal promotionAmountHotel) {
        PromotionAmountHotel = promotionAmountHotel;
    }

    public BigDecimal getPromotionCostHotel() {
        return PromotionCostHotel;
    }

    public void setPromotionCostHotel(BigDecimal promotionCostHotel) {
        PromotionCostHotel = promotionCostHotel;
    }

    public BigDecimal getPromotionAmountTrip() {
        return PromotionAmountTrip;
    }

    public void setPromotionAmountTrip(BigDecimal promotionAmountTrip) {
        PromotionAmountTrip = promotionAmountTrip;
    }

    public BigDecimal getPromotionCostTrip() {
        return PromotionCostTrip;
    }

    public void setPromotionCostTrip(BigDecimal promotionCostTrip) {
        PromotionCostTrip = promotionCostTrip;
    }
}
