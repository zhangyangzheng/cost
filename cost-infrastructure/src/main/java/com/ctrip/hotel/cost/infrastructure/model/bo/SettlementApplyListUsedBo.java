package com.ctrip.hotel.cost.infrastructure.model.bo;

import com.ctrip.soa.hotel.settlement.api.DataItem;
import com.ctrip.soa.hotel.settlement.api.SettleDataRequest;
import com.ctrip.soa.hotel.settlement.api.SettlementOptional;
import com.ctrip.soa.hotel.settlement.api.SettlementPromotionDetail;
import hotel.settlement.common.beans.BeanHelper;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.List;

// 原契约对象(SettleDataRequest)不需要的字段注释掉了
public class SettlementApplyListUsedBo {

  public static class DataItemUsed {
    private String dataKey;

    private String dataValue;

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

    public DataItem convertTo() {
      return BeanHelper.convert(this, DataItem.class);
    }
  }

  public static class SettlementOptionalUsed {
    private int optionalNumber;

    private String optionalName;

    private BigDecimal priceAmount;

    private BigDecimal costAmount;

    private BigDecimal quantity;

    private String priceType;

    private String optionalType;

    public int getOptionalNumber() {
      return optionalNumber;
    }

    public void setOptionalNumber(int optionalNumber) {
      this.optionalNumber = optionalNumber;
    }

    public String getOptionalName() {
      return optionalName;
    }

    public void setOptionalName(String optionalName) {
      this.optionalName = optionalName;
    }

    public BigDecimal getPriceAmount() {
      return priceAmount;
    }

    public void setPriceAmount(BigDecimal priceAmount) {
      this.priceAmount = priceAmount;
    }

    public BigDecimal getCostAmount() {
      return costAmount;
    }

    public void setCostAmount(BigDecimal costAmount) {
      this.costAmount = costAmount;
    }

    public BigDecimal getQuantity() {
      return quantity;
    }

    public void setQuantity(BigDecimal quantity) {
      this.quantity = quantity;
    }

    public String getPriceType() {
      return priceType;
    }

    public void setPriceType(String priceType) {
      this.priceType = priceType;
    }

    public String getOptionalType() {
      return optionalType;
    }

    public void setOptionalType(String optionalType) {
      this.optionalType = optionalType;
    }

    public SettlementOptional convertTo() {
      return BeanHelper.convert(this, SettlementOptional.class);
    }
  }

  public static class SettlementPromotionDetailUsed {
    private Calendar beginDate;

    private Calendar endDate;

    private String promotionNumber;

    private String promotionName;

    private BigDecimal priceAmount;

    private BigDecimal costAmount;

    private long promotionVersionID;

    private int style;

    private long ruleId;

    public Calendar getBeginDate() {
      return beginDate;
    }

    public void setBeginDate(Calendar beginDate) {
      this.beginDate = beginDate;
    }

    public Calendar getEndDate() {
      return endDate;
    }

    public void setEndDate(Calendar endDate) {
      this.endDate = endDate;
    }

    public String getPromotionNumber() {
      return promotionNumber;
    }

    public void setPromotionNumber(String promotionNumber) {
      this.promotionNumber = promotionNumber;
    }

    public String getPromotionName() {
      return promotionName;
    }

    public void setPromotionName(String promotionName) {
      this.promotionName = promotionName;
    }

    public BigDecimal getPriceAmount() {
      return priceAmount;
    }

    public void setPriceAmount(BigDecimal priceAmount) {
      this.priceAmount = priceAmount;
    }

    public BigDecimal getCostAmount() {
      return costAmount;
    }

    public void setCostAmount(BigDecimal costAmount) {
      this.costAmount = costAmount;
    }

    public long getPromotionVersionID() {
      return promotionVersionID;
    }

    public void setPromotionVersionID(long promotionVersionID) {
      this.promotionVersionID = promotionVersionID;
    }

    public int getStyle() {
      return style;
    }

    public void setStyle(int style) {
      this.style = style;
    }

    public long getRuleId() {
      return ruleId;
    }

    public void setRuleId(long ruleId) {
      this.ruleId = ruleId;
    }

    public SettlementPromotionDetail convertTo() {
      return BeanHelper.convert(this, SettlementPromotionDetail.class);
    }
  }

  // 这个字段原契约对象没有 但是抛的时候需要
  private String referenceId;

  private int id;

  private long settlementId;

  private String outSettlementNo;

  private int merchantId;

  private String orderId;

  private Calendar orderDate;

  private String orderDesc;

  private String collectionType;

  private String settlementItemName;

  //    @JsonProperty("SettlementItemDesc")
  //    private String settlementItemDesc;

  private String companyID;

  private String productCode;

  private String currency;

  private BigDecimal quantity;

  private String settlementPriceType;

  private String sourceId;

  private String channelType;

  private List<DataItemUsed> dataItems;

  //    @JsonProperty("SourceExtraDatas")
  //    private List<DataItem> sourceExtraDatas;

  private List<SettlementOptionalUsed> settlementOptionalList;

  //  @JsonProperty("SettlementRoomResourceLists")
  //  private List<SettlementRoomResource> settlementRoomResourceLists;

  private List<SettlementPromotionDetailUsed> settlementPromotionDetailList;

  public String getReferenceId() {
    return referenceId;
  }

  public void setReferenceId(String referenceId) {
    this.referenceId = referenceId;
  }

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public long getSettlementId() {
    return settlementId;
  }

  public void setSettlementId(long settlementId) {
    this.settlementId = settlementId;
  }

  public String getOutSettlementNo() {
    return outSettlementNo;
  }

  public void setOutSettlementNo(String outSettlementNo) {
    this.outSettlementNo = outSettlementNo;
  }

  public int getMerchantId() {
    return merchantId;
  }

  public void setMerchantId(int merchantId) {
    this.merchantId = merchantId;
  }

  public String getOrderId() {
    return orderId;
  }

  public void setOrderId(String orderId) {
    this.orderId = orderId;
  }

  public Calendar getOrderDate() {
    return orderDate;
  }

  public void setOrderDate(Calendar orderDate) {
    this.orderDate = orderDate;
  }

  public String getOrderDesc() {
    return orderDesc;
  }

  public void setOrderDesc(String orderDesc) {
    this.orderDesc = orderDesc;
  }

  public String getCollectionType() {
    return collectionType;
  }

  public void setCollectionType(String collectionType) {
    this.collectionType = collectionType;
  }

  public String getSettlementItemName() {
    return settlementItemName;
  }

  public void setSettlementItemName(String settlementItemName) {
    this.settlementItemName = settlementItemName;
  }

  public String getCompanyID() {
    return companyID;
  }

  public void setCompanyID(String companyID) {
    this.companyID = companyID;
  }

  public String getProductCode() {
    return productCode;
  }

  public void setProductCode(String productCode) {
    this.productCode = productCode;
  }

  public String getCurrency() {
    return currency;
  }

  public void setCurrency(String currency) {
    this.currency = currency;
  }

  public BigDecimal getQuantity() {
    return quantity;
  }

  public void setQuantity(BigDecimal quantity) {
    this.quantity = quantity;
  }

  public String getSettlementPriceType() {
    return settlementPriceType;
  }

  public void setSettlementPriceType(String settlementPriceType) {
    this.settlementPriceType = settlementPriceType;
  }

  public String getSourceId() {
    return sourceId;
  }

  public void setSourceId(String sourceId) {
    this.sourceId = sourceId;
  }

  public String getChannelType() {
    return channelType;
  }

  public void setChannelType(String channelType) {
    this.channelType = channelType;
  }

  public List<DataItemUsed> getDataItems() {
    return dataItems;
  }

  public void setDataItems(List<DataItemUsed> dataItems) {
    this.dataItems = dataItems;
  }

  public List<SettlementOptionalUsed> getSettlementOptionalList() {
    return settlementOptionalList;
  }

  public void setSettlementOptionalList(List<SettlementOptionalUsed> settlementOptionalList) {
    this.settlementOptionalList = settlementOptionalList;
  }

  public List<SettlementPromotionDetailUsed> getSettlementPromotionDetailList() {
    return settlementPromotionDetailList;
  }

  public void setSettlementPromotionDetailList(
      List<SettlementPromotionDetailUsed> settlementPromotionDetailList) {
    this.settlementPromotionDetailList = settlementPromotionDetailList;
  }

  public SettleDataRequest convertTo() {
    SettleDataRequest request = BeanHelper.convert(this, SettleDataRequest.class);// todo 反射copy改成值拷贝
    request.setDataItems(BeanHelper.convertToList(dataItems, DataItem.class));
    request.setSettlementOptionalLists(
        BeanHelper.convertToList(settlementOptionalList, SettlementOptional.class));
    request.setSettlementPromotionDetailLists(
        BeanHelper.convertToList(settlementPromotionDetailList, SettlementPromotionDetail.class));
    return request;
  }
}
