package com.ctrip.hotel.cost.infrastructure.model.dto;

import com.ctrip.soa.hotel.settlement.api.CancelSettleData;

public class SettlementCancelListDto {
    CancelSettleData cancelSettleData;
    String referenceId;

    public SettlementCancelListDto() {
    }

    public SettlementCancelListDto(CancelSettleData cancelSettleData, String referenceId) {
        this.cancelSettleData = cancelSettleData;
        this.referenceId = referenceId;
    }

    public CancelSettleData getCancelSettleData() {
        return cancelSettleData;
    }

    public void setCancelSettleData(CancelSettleData cancelSettleData) {
        this.cancelSettleData = cancelSettleData;
    }

    public String getReferenceId() {
        return referenceId;
    }

    public void setReferenceId(String referenceId) {
        this.referenceId = referenceId;
    }
}
