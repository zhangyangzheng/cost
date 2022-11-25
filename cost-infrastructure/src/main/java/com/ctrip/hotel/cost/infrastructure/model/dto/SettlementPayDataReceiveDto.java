package com.ctrip.hotel.cost.infrastructure.model.dto;


import soa.ctrip.com.hotel.vendor.settlement.v1.settlementdata.SettlementPayData;

public class SettlementPayDataReceiveDto {
    SettlementPayData settlementPayData;

    String referenceId;

    public SettlementPayDataReceiveDto() {
    }

    public SettlementPayDataReceiveDto(SettlementPayData settlementPayData, String referenceId) {
        this.settlementPayData = settlementPayData;
        this.referenceId = referenceId;
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
}
