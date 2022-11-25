package com.ctrip.hotel.cost.infrastructure.model.dto;

import soa.ctrip.com.hotel.vendor.settlement.v1.cancelorder.CancelorderRequesttype;

public class CancelOrderDto {
    CancelorderRequesttype cancelOrderRequest;

    String referenceId;

    public CancelOrderDto() {
    }

    public CancelOrderDto(CancelorderRequesttype cancelOrderRequest, String referenceId) {
        this.cancelOrderRequest = cancelOrderRequest;
        this.referenceId = referenceId;
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
}
