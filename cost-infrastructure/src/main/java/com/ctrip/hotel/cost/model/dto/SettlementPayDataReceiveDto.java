package com.ctrip.hotel.cost.model.dto;


import soa.ctrip.com.hotel.vendor.settlement.v1.settlementdata.SettlementPayData;

public class SettlementPayDataReceiveDto {
    SettlementPayData settlementPayData;

    String referenceId;

    String isThrow;

    public SettlementPayDataReceiveDto() {
    }

    public SettlementPayDataReceiveDto(SettlementPayData settlementPayData, String referenceId, String isThrow) {
        this.settlementPayData = settlementPayData;
        this.referenceId = referenceId;
        this.isThrow = isThrow;
    }

    public SettlementPayData getSettlementPayData() {
        return settlementPayData;
    }

    public void setSettlementPayData(SettlementPayData settlementPayData) {
        this.settlementPayData = settlementPayData;
    }

    public String getReferenceId() {
        return referenceId;
    }

    public void setReferenceId(String referenceId) {
        this.referenceId = referenceId;
    }

    public String getIsThrow() {
        return isThrow;
    }

    public void setIsThrow(String isThrow) {
        this.isThrow = isThrow;
    }
}
