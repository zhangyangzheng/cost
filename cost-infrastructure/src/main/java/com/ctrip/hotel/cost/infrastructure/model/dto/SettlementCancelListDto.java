package com.ctrip.hotel.cost.infrastructure.model.dto;

import com.ctrip.soa.hotel.settlement.api.CancelSettleData;

public class SettlementCancelListDto {
    CancelSettleData cancelSettleData;
    String referenceId;

    String isThrow;

    public SettlementCancelListDto() {
    }

    public SettlementCancelListDto(CancelSettleData cancelSettleData, String referenceId, String isThrow) {
        this.cancelSettleData = cancelSettleData;
        this.referenceId = referenceId;
        this.isThrow = isThrow;
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

    public String getIsThrow() {
        return isThrow;
    }

    public void setIsThrow(String isThrow) {
        this.isThrow = isThrow;
    }
}
