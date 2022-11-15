package com.ctrip.hotel.cost.infrastructure.model.bo;

import com.ctrip.soa.hotel.settlement.api.CancelDataItem;
import com.ctrip.soa.hotel.settlement.api.CancelSettleData;
import com.fasterxml.jackson.annotation.JsonProperty;
import hotel.settlement.common.ListHelper;
import hotel.settlement.common.beans.BeanHelper;

import java.util.ArrayList;
import java.util.List;

public class SettlementCancelListUsedBo {

  public static class CancelDataItemUsed {
    @JsonProperty("dataKey")
    private String dataKey;

    @JsonProperty("dataValue")
    private String dataValue;

    @JsonProperty("dataDesc")
    private String dataDesc;

    public String getDataKey() {
      return dataKey;
    }

    public void setDataKey(String dataKey) {
      this.dataKey = dataKey;
    }

    public String getDataValue() {
      return dataValue;
    }

    public void setDataValue(String dataValue) {
      this.dataValue = dataValue;
    }

    public String getDataDesc() {
      return dataDesc;
    }

    public void setDataDesc(String dataDesc) {
      this.dataDesc = dataDesc;
    }

    public CancelDataItem convertTo() {
      return BeanHelper.convert(this, CancelDataItem.class);
    }
  }

  @JsonProperty("id")
  private Integer id;

  @JsonProperty("settlementId")
  private Long settlementId;

  @JsonProperty("outSettlementNo")
  private String outSettlementNo;

  @JsonProperty("merchantId")
  private Integer merchantId;

  @JsonProperty("cancelItem")
  private List<CancelDataItemUsed> cancelItems;

  //  @JsonProperty("RetCode")
  //  private Integer RetCode;

  //  @JsonProperty("Message")
  //  private String Message;

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public Long getSettlementId() {
    return settlementId;
  }

  public void setSettlementId(Long settlementId) {
    this.settlementId = settlementId;
  }

  public String getOutSettlementNo() {
    return outSettlementNo;
  }

  public void setOutSettlementNo(String outSettlementNo) {
    this.outSettlementNo = outSettlementNo;
  }

  public Integer getMerchantId() {
    return merchantId;
  }

  public void setMerchantId(Integer merchantId) {
    this.merchantId = merchantId;
  }

  public List<CancelDataItemUsed> getCancelItems() {
    return cancelItems;
  }

  public void setCancelItems(List<CancelDataItemUsed> cancelItems) {
    this.cancelItems = cancelItems;
  }

  public CancelSettleData convertTo() {
    CancelSettleData cancelSettleData = BeanHelper.convert(this, CancelSettleData.class);
    cancelSettleData.setDataItems(BeanHelper.convertToList(cancelItems, CancelDataItem.class));
    return cancelSettleData;
  }
}
