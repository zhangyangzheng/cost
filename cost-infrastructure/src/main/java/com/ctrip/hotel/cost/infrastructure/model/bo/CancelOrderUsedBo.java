package com.ctrip.hotel.cost.infrastructure.model.bo;

import com.fasterxml.jackson.annotation.JsonProperty;
import hotel.settlement.common.ListHelper;
import hotel.settlement.common.beans.BeanHelper;
import soa.ctrip.com.hotel.vendor.settlement.v1.Hotelorderchannel;
import soa.ctrip.com.hotel.vendor.settlement.v1.cancelorder.CancelorderRequesttype;
import soa.ctrip.com.hotel.vendor.settlement.v1.cancelorder.Tocanceldata;

import java.util.List;

public class CancelOrderUsedBo {

  public static class ToCancelDataUsed {
    @JsonProperty("outsettlementno")
    public String outsettlementno;

    @JsonProperty("settlementid")
    public Long settlementid;

    public String getOutsettlementno() {
      return outsettlementno;
    }

    public void setOutsettlementno(String outsettlementno) {
      this.outsettlementno = outsettlementno;
    }

    public Long getSettlementid() {
      return settlementid;
    }

    public void setSettlementid(Long settlementid) {
      this.settlementid = settlementid;
    }

    public Tocanceldata convertTo() {
      return BeanHelper.convert(this, Tocanceldata.class);
    }
  }

  @JsonProperty("orderchannel")
  public Hotelorderchannel orderchannel;

  @JsonProperty("orderid")
  public String orderid;

  @JsonProperty("fGID")
  public Long fGID;

  @JsonProperty("toCancelDataList")
  public List<ToCancelDataUsed> cancelDataList;

  //  @JsonProperty("version")
  //  public String version;

  public Hotelorderchannel getOrderchannel() {
    return orderchannel;
  }

  public void setOrderchannel(Hotelorderchannel orderchannel) {
    this.orderchannel = orderchannel;
  }

  public String getOrderid() {
    return orderid;
  }

  public void setOrderid(String orderid) {
    this.orderid = orderid;
  }

  public Long getFGID() {
    return fGID;
  }

  public void setFGID(Long fGID) {
    this.fGID = fGID;
  }

  public List<ToCancelDataUsed> getCancelDataList() {
    return cancelDataList;
  }

  public void setCancelDataList(List<ToCancelDataUsed> cancelDataList) {
    this.cancelDataList = cancelDataList;
  }

  public CancelorderRequesttype convertTo() {
    CancelorderRequesttype cancelorderRequesttype =
        BeanHelper.convert(this, CancelorderRequesttype.class);
    cancelorderRequesttype.setCanceldatalist(
        BeanHelper.convertToList(cancelDataList, Tocanceldata.class));
    return cancelorderRequesttype;
  }
}
