package com.ctrip.hotel.cost.model.bo;

import hotel.settlement.dao.dal.htlcalculatefeetidb.entity.OrderAuditFgMqTiDBGen;
import hotel.settlement.dao.dal.htlcalculatefeetidb.entity.SettleCallbackInfoTiDBGen;

public class FgOrderAuditMqDataBo {
    OrderAuditFgMqTiDBGen orderAuditFgMqTiDBGen;

    SettleCallbackInfoTiDBGen settleCallbackInfoTiDBGen;

    public FgOrderAuditMqDataBo() {
    }

    public FgOrderAuditMqDataBo(OrderAuditFgMqTiDBGen orderAuditFgMqTiDBGen, SettleCallbackInfoTiDBGen settleCallbackInfoTiDBGen) {
        this.orderAuditFgMqTiDBGen = orderAuditFgMqTiDBGen;
        this.settleCallbackInfoTiDBGen = settleCallbackInfoTiDBGen;
    }

    public OrderAuditFgMqTiDBGen getOrderAuditFgMqTiDBGen() {
        return orderAuditFgMqTiDBGen;
    }

    public void setOrderAuditFgMqTiDBGen(OrderAuditFgMqTiDBGen orderAuditFgMqTiDBGen) {
        this.orderAuditFgMqTiDBGen = orderAuditFgMqTiDBGen;
    }

    public SettleCallbackInfoTiDBGen getSettleCallbackInfoTiDBGen() {
        return settleCallbackInfoTiDBGen;
    }

    public void setSettleCallbackInfoTiDBGen(SettleCallbackInfoTiDBGen settleCallbackInfoTiDBGen) {
        this.settleCallbackInfoTiDBGen = settleCallbackInfoTiDBGen;
    }
}
