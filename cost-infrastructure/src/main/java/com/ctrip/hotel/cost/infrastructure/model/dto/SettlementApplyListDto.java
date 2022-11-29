package com.ctrip.hotel.cost.infrastructure.model.dto;

import com.ctrip.soa.hotel.settlement.api.SettleDataRequest;

public class SettlementApplyListDto {
    SettleDataRequest settleDataRequest;
    String referenceId;

    String isThrow;

    public SettlementApplyListDto() {
    }

    public SettlementApplyListDto(SettleDataRequest settleDataRequest, String referenceId, String isThrow) {
        this.settleDataRequest = settleDataRequest;
        this.referenceId = referenceId;
        this.isThrow = isThrow;
    }

    public SettleDataRequest getSettleDataRequest() {
        return settleDataRequest;
    }

    public void setSettleDataRequest(SettleDataRequest settleDataRequest) {
        this.settleDataRequest = settleDataRequest;
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
