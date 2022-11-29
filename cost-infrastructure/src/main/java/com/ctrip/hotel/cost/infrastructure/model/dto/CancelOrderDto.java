package com.ctrip.hotel.cost.infrastructure.model.dto;

import soa.ctrip.com.hotel.vendor.settlement.v1.cancelorder.CancelorderRequesttype;

public class CancelOrderDto {
    CancelorderRequesttype cancelOrderRequest;

    String referenceId;

    String isThrow;

    public CancelOrderDto() {
    }

    public CancelOrderDto(CancelorderRequesttype cancelOrderRequest, String referenceId, String isThrow) {
        this.cancelOrderRequest = cancelOrderRequest;
        this.referenceId = referenceId;
        this.isThrow = isThrow;
    }

    public CancelorderRequesttype getCancelOrderRequest() {
        return cancelOrderRequest;
    }

    public void setCancelOrderRequest(CancelorderRequesttype cancelOrderRequest) {
        this.cancelOrderRequest = cancelOrderRequest;
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
