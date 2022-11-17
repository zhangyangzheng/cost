package com.ctrip.hotel.cost.infrastructure.model.bo;

import hotel.settlement.common.beans.BeanHelper;
import soa.ctrip.com.hotel.vendor.settlement.v1.Hotelorderchannel;
import soa.ctrip.com.hotel.vendor.settlement.v1.Orderpromotion;
import soa.ctrip.com.hotel.vendor.settlement.v1.settlementdata.SettlementPayData;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.List;

// 原契约对象(SettlementPayData)不需要的字段注释掉了
public class SettlementPayDataUsedBo {

  public static class OrderPromotionUsed {
    public Integer promotionNumber;

    public String promotionName;

    public BigDecimal priceAmount;

    public BigDecimal costAmount;

    public Calendar beginDate;

    public Long promotionVersionID;

    //    @JsonProperty("style")
    //    public Integer style;

    //    @JsonProperty("ruleId")
    //    public Long ruleId;

    public Integer getPromotionNumber() {
      return promotionNumber;
    }

    public void setPromotionNumber(Integer promotionNumber) {
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

    public Calendar getBeginDate() {
      return beginDate;
    }

    public void setBeginDate(Calendar beginDate) {
      this.beginDate = beginDate;
    }

    public Long getPromotionVersionID() {
      return promotionVersionID;
    }

    public void setPromotionVersionID(Long promotionVersionID) {
      this.promotionVersionID = promotionVersionID;
    }

    public Orderpromotion convertTo() {
      return BeanHelper.convert(this, Orderpromotion.class);
    }
  }

  public String outSettlementNo;

  //    @JsonProperty("serialNo")
  //    public String serialNo;

  public String orderId;

  public Hotelorderchannel orderchannel;

  public Calendar orderDate;

  public String currency;

  public BigDecimal quantity;

  public String sourceId;

  public String companyId;

  public String room;

  public String roomNo;

  public String checkInType;

  public Calendar eta;

  public Calendar etd;

  public Integer commissionType;

  public BigDecimal priceAmount;

  public BigDecimal costAmount;

  public String clientName;

  public String remarks;

  public String htlConfirmNo;

  public String roomName;

  public Calendar realETD;

  public String hotelInfo;

  public String guarantee;

  public String guaranteeWay;

  public String newFgId;

  public String isRoomDelay;

  public String recheck;

  public String allNeedGuarantee;

  //    @JsonProperty("pmsStatus")
  //    public String pmsStatus;

  public String vendorChannelId;

  //    @JsonProperty("commissionAmount")
  //    public BigDecimal commissionAmount;

  //    @JsonProperty("transBatchDateTime")
  //    public Calendar transBatchDateTime;

  public String modifyOperateType;

  public String modifyOperateSubType;

  //    @JsonProperty("modifyQuantity")
  //    public BigDecimal modifyQuantity;

  public String modifyOperateEid;

  public Calendar modifyOperateDateTime;

  public String groupOrderClass;

  public Boolean walletPay;

  public String isLadderDeduct;

  //    @JsonProperty("isCompensationOrder")
  //    public String isCompensationOrder;

  //    @JsonProperty("indemnityType")
  //    public String indemnityType;

  //    @JsonProperty("compensationConfirmed")
  //    public String compensationConfirmed;

  public String bidFlag;

  public BigDecimal bidPrice;

  //    @JsonProperty("isOverseaVendor")
  //    public String isOverseaVendor;

  public String roomNameEn;

  public String isRapidSettlement;

  public BigDecimal adjustAmount;

  public Integer fgId;

  public String orderConfirmType;

  //    @JsonProperty("adjustAmountType")
  //    public Integer adjustAmountType;

  //    @JsonProperty("adjustAmountRemark")
  //    public String adjustAmountRemark;

  //    @JsonProperty("guaranteeType")
  //    public Integer guaranteeType;

  public Integer hotelConfirmStatus;

  public Integer hotelID;

  public Integer adjustBatchId;

  //    @JsonProperty("isPlatformBuyout")
  //    public Integer isPlatformBuyout;

  //    @JsonProperty("isSettlementBuyout")
  //    public Integer isSettlementBuyout;

  public BigDecimal roomAmount;

  public BigDecimal roomCost;

  //    @JsonProperty("roomCurrency")
  //    public String roomCurrency;

  //    @JsonProperty("billsettleCurrency")
  //    public String billsettleCurrency;

  //    @JsonProperty("billHtlCurrency")
  //    public String billHtlCurrency;

  //    @JsonProperty("counterFee")
  //    public BigDecimal counterFee;

  //    @JsonProperty("counterFeeCurrency")
  //    public String counterFeeCurrency;

  //    @JsonProperty("otherCost")
  //    public BigDecimal otherCost;

  //    @JsonProperty("realAmount")
  //    public BigDecimal realAmount;

  //    @JsonProperty("orderGenerateSource")
  //    public Integer orderGenerateSource;

  //    @JsonProperty("makeInvoiceType")
  //    public Integer makeInvoiceType;

  public String settlementPriceType;

  //    @JsonProperty("latestCancelTime")
  //    public Calendar latestCancelTime;

  public String splitOrder;

  public Long settlementid;

  //    @JsonProperty("orgcostAmount")
  //    public BigDecimal orgcostAmount;

  //    @JsonProperty("orderoptionallist")
  //    public List<Orderoptional> orderoptionallist;

  //    @JsonProperty("roomresourcelist")
  //    public List<Roomresource> roomresourcelist;

  public List<OrderPromotionUsed> orderPromotionList;

  public Integer insuranceFlag;

  public String uid;

  //    @JsonProperty("allianceID")
  //    public Integer allianceID;

  //    @JsonProperty("sID")
  //    public Integer sID;

  //    @JsonProperty("allianceOrderID")
  //    public String allianceOrderID;

  //    @JsonProperty("allianceflag")
  //    public Integer allianceflag;

  //    @JsonProperty("billID")
  //    public Integer billID;

  //    @JsonProperty("childrenPrice")
  //    public BigDecimal childrenPrice;

  //    @JsonProperty("childrenCost")
  //    public BigDecimal childrenCost;

  public BigDecimal rmbExchangeRate;

  //    @JsonProperty("orderInvoiceType")
  //    public String orderInvoiceType;

  //    @JsonProperty("orderSource")
  //    public String orderSource;

  public BigDecimal zeroCommissionAmount;

  public BigDecimal zeroCommissionDeductRate;

  public String uinonMemberFlag;

  public BigDecimal serviceFeeProportion;

  public BigDecimal buyoutDiscountAmount;

  public String rakebackFlag;

  public String rakebackHotelId;

  public BigDecimal rakebackRate;

  public String clientOrderId;

  public BigDecimal tripPromotionAmount;

  //    @JsonProperty("empQrcodeFlag")
  //    public String empQrcodeFlag;

  //    @JsonProperty("merchantExtraSubsidyRatio")
  //    public BigDecimal merchantExtraSubsidyRatio;

  //    @JsonProperty("merchantExtraSubsidyAmount")
  //    public BigDecimal merchantExtraSubsidyAmount;

  public String outTimeDeductType;

  public String outTimeDeductValue;

  //    @JsonProperty("recuperationLimitAmount")
  //    public BigDecimal recuperationLimitAmount;

  public String getOutSettlementNo() {
    return outSettlementNo;
  }

  public void setOutSettlementNo(String outSettlementNo) {
    this.outSettlementNo = outSettlementNo;
  }

  public String getOrderId() {
    return orderId;
  }

  public void setOrderId(String orderId) {
    this.orderId = orderId;
  }

  public Hotelorderchannel getOrderchannel() {
    return orderchannel;
  }

  public void setOrderchannel(Hotelorderchannel orderchannel) {
    this.orderchannel = orderchannel;
  }

  public Calendar getOrderDate() {
    return orderDate;
  }

  public void setOrderDate(Calendar orderDate) {
    this.orderDate = orderDate;
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

  public String getSourceId() {
    return sourceId;
  }

  public void setSourceId(String sourceId) {
    this.sourceId = sourceId;
  }

  public String getCompanyId() {
    return companyId;
  }

  public void setCompanyId(String companyId) {
    this.companyId = companyId;
  }

  public String getRoom() {
    return room;
  }

  public void setRoom(String room) {
    this.room = room;
  }

  public String getRoomNo() {
    return roomNo;
  }

  public void setRoomNo(String roomNo) {
    this.roomNo = roomNo;
  }

  public String getCheckInType() {
    return checkInType;
  }

  public void setCheckInType(String checkInType) {
    this.checkInType = checkInType;
  }

  public Calendar getEta() {
    return eta;
  }

  public void setEta(Calendar eta) {
    this.eta = eta;
  }

  public Calendar getEtd() {
    return etd;
  }

  public void setEtd(Calendar etd) {
    this.etd = etd;
  }

  public Integer getCommissionType() {
    return commissionType;
  }

  public void setCommissionType(Integer commissionType) {
    this.commissionType = commissionType;
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

  public String getClientName() {
    return clientName;
  }

  public void setClientName(String clientName) {
    this.clientName = clientName;
  }

  public String getRemarks() {
    return remarks;
  }

  public void setRemarks(String remarks) {
    this.remarks = remarks;
  }

  public String getHtlConfirmNo() {
    return htlConfirmNo;
  }

  public void setHtlConfirmNo(String htlConfirmNo) {
    this.htlConfirmNo = htlConfirmNo;
  }

  public String getRoomName() {
    return roomName;
  }

  public void setRoomName(String roomName) {
    this.roomName = roomName;
  }

  public Calendar getRealETD() {
    return realETD;
  }

  public void setRealETD(Calendar realETD) {
    this.realETD = realETD;
  }

  public String getHotelInfo() {
    return hotelInfo;
  }

  public void setHotelInfo(String hotelInfo) {
    this.hotelInfo = hotelInfo;
  }

  public String getGuarantee() {
    return guarantee;
  }

  public void setGuarantee(String guarantee) {
    this.guarantee = guarantee;
  }

  public String getGuaranteeWay() {
    return guaranteeWay;
  }

  public void setGuaranteeWay(String guaranteeWay) {
    this.guaranteeWay = guaranteeWay;
  }

  public String getNewFgId() {
    return newFgId;
  }

  public void setNewFgId(String newFgId) {
    this.newFgId = newFgId;
  }

  public String getIsRoomDelay() {
    return isRoomDelay;
  }

  public void setIsRoomDelay(String isRoomDelay) {
    this.isRoomDelay = isRoomDelay;
  }

  public String getRecheck() {
    return recheck;
  }

  public void setRecheck(String recheck) {
    this.recheck = recheck;
  }

  public String getAllNeedGuarantee() {
    return allNeedGuarantee;
  }

  public void setAllNeedGuarantee(String allNeedGuarantee) {
    this.allNeedGuarantee = allNeedGuarantee;
  }

  public String getVendorChannelId() {
    return vendorChannelId;
  }

  public void setVendorChannelId(String vendorChannelId) {
    this.vendorChannelId = vendorChannelId;
  }

  public String getModifyOperateType() {
    return modifyOperateType;
  }

  public void setModifyOperateType(String modifyOperateType) {
    this.modifyOperateType = modifyOperateType;
  }

  public String getModifyOperateSubType() {
    return modifyOperateSubType;
  }

  public void setModifyOperateSubType(String modifyOperateSubType) {
    this.modifyOperateSubType = modifyOperateSubType;
  }

  public String getModifyOperateEid() {
    return modifyOperateEid;
  }

  public void setModifyOperateEid(String modifyOperateEid) {
    this.modifyOperateEid = modifyOperateEid;
  }

  public Calendar getModifyOperateDateTime() {
    return modifyOperateDateTime;
  }

  public void setModifyOperateDateTime(Calendar modifyOperateDateTime) {
    this.modifyOperateDateTime = modifyOperateDateTime;
  }

  public String getGroupOrderClass() {
    return groupOrderClass;
  }

  public void setGroupOrderClass(String groupOrderClass) {
    this.groupOrderClass = groupOrderClass;
  }

  public Boolean getWalletPay() {
    return walletPay;
  }

  public void setWalletPay(Boolean walletPay) {
    this.walletPay = walletPay;
  }

  public String getIsLadderDeduct() {
    return isLadderDeduct;
  }

  public void setIsLadderDeduct(String isLadderDeduct) {
    this.isLadderDeduct = isLadderDeduct;
  }

  public String getBidFlag() {
    return bidFlag;
  }

  public void setBidFlag(String bidFlag) {
    this.bidFlag = bidFlag;
  }

  public BigDecimal getBidPrice() {
    return bidPrice;
  }

  public void setBidPrice(BigDecimal bidPrice) {
    this.bidPrice = bidPrice;
  }

  public String getRoomNameEn() {
    return roomNameEn;
  }

  public void setRoomNameEn(String roomNameEn) {
    this.roomNameEn = roomNameEn;
  }

  public String getIsRapidSettlement() {
    return isRapidSettlement;
  }

  public void setIsRapidSettlement(String isRapidSettlement) {
    this.isRapidSettlement = isRapidSettlement;
  }

  public BigDecimal getAdjustAmount() {
    return adjustAmount;
  }

  public void setAdjustAmount(BigDecimal adjustAmount) {
    this.adjustAmount = adjustAmount;
  }

  public Integer getFgId() {
    return fgId;
  }

  public void setFgId(Integer fgId) {
    this.fgId = fgId;
  }

  public String getOrderConfirmType() {
    return orderConfirmType;
  }

  public void setOrderConfirmType(String orderConfirmType) {
    this.orderConfirmType = orderConfirmType;
  }

  public Integer getHotelConfirmStatus() {
    return hotelConfirmStatus;
  }

  public void setHotelConfirmStatus(Integer hotelConfirmStatus) {
    this.hotelConfirmStatus = hotelConfirmStatus;
  }

  public Integer getHotelID() {
    return hotelID;
  }

  public void setHotelID(Integer hotelID) {
    this.hotelID = hotelID;
  }

  public Integer getAdjustBatchId() {
    return adjustBatchId;
  }

  public void setAdjustBatchId(Integer adjustBatchId) {
    this.adjustBatchId = adjustBatchId;
  }

  public BigDecimal getRoomAmount() {
    return roomAmount;
  }

  public void setRoomAmount(BigDecimal roomAmount) {
    this.roomAmount = roomAmount;
  }

  public BigDecimal getRoomCost() {
    return roomCost;
  }

  public void setRoomCost(BigDecimal roomCost) {
    this.roomCost = roomCost;
  }

  public String getSettlementPriceType() {
    return settlementPriceType;
  }

  public void setSettlementPriceType(String settlementPriceType) {
    this.settlementPriceType = settlementPriceType;
  }

  public String getSplitOrder() {
    return splitOrder;
  }

  public void setSplitOrder(String splitOrder) {
    this.splitOrder = splitOrder;
  }

  public Long getSettlementid() {
    return settlementid;
  }

  public void setSettlementid(Long settlementid) {
    this.settlementid = settlementid;
  }

  public List<OrderPromotionUsed> getOrderPromotionList() {
    return orderPromotionList;
  }

  public void setOrderPromotionList(List<OrderPromotionUsed> orderPromotionList) {
    this.orderPromotionList = orderPromotionList;
  }

  public Integer getInsuranceFlag() {
    return insuranceFlag;
  }

  public void setInsuranceFlag(Integer insuranceFlag) {
    this.insuranceFlag = insuranceFlag;
  }

  public String getUid() {
    return uid;
  }

  public void setUid(String uid) {
    this.uid = uid;
  }

  public BigDecimal getRmbExchangeRate() {
    return rmbExchangeRate;
  }

  public void setRmbExchangeRate(BigDecimal rmbExchangeRate) {
    this.rmbExchangeRate = rmbExchangeRate;
  }

  public BigDecimal getZeroCommissionAmount() {
    return zeroCommissionAmount;
  }

  public void setZeroCommissionAmount(BigDecimal zeroCommissionAmount) {
    this.zeroCommissionAmount = zeroCommissionAmount;
  }

  public BigDecimal getZeroCommissionDeductRate() {
    return zeroCommissionDeductRate;
  }

  public void setZeroCommissionDeductRate(BigDecimal zeroCommissionDeductRate) {
    this.zeroCommissionDeductRate = zeroCommissionDeductRate;
  }

  public String getUinonMemberFlag() {
    return uinonMemberFlag;
  }

  public void setUinonMemberFlag(String uinonMemberFlag) {
    this.uinonMemberFlag = uinonMemberFlag;
  }

  public BigDecimal getServiceFeeProportion() {
    return serviceFeeProportion;
  }

  public void setServiceFeeProportion(BigDecimal serviceFeeProportion) {
    this.serviceFeeProportion = serviceFeeProportion;
  }

  public BigDecimal getBuyoutDiscountAmount() {
    return buyoutDiscountAmount;
  }

  public void setBuyoutDiscountAmount(BigDecimal buyoutDiscountAmount) {
    this.buyoutDiscountAmount = buyoutDiscountAmount;
  }

  public String getRakebackFlag() {
    return rakebackFlag;
  }

  public void setRakebackFlag(String rakebackFlag) {
    this.rakebackFlag = rakebackFlag;
  }

  public String getRakebackHotelId() {
    return rakebackHotelId;
  }

  public void setRakebackHotelId(String rakebackHotelId) {
    this.rakebackHotelId = rakebackHotelId;
  }

  public BigDecimal getRakebackRate() {
    return rakebackRate;
  }

  public void setRakebackRate(BigDecimal rakebackRate) {
    this.rakebackRate = rakebackRate;
  }

  public String getClientOrderId() {
    return clientOrderId;
  }

  public void setClientOrderId(String clientOrderId) {
    this.clientOrderId = clientOrderId;
  }

  public BigDecimal getTripPromotionAmount() {
    return tripPromotionAmount;
  }

  public void setTripPromotionAmount(BigDecimal tripPromotionAmount) {
    this.tripPromotionAmount = tripPromotionAmount;
  }

  public String getOutTimeDeductType() {
    return outTimeDeductType;
  }

  public void setOutTimeDeductType(String outTimeDeductType) {
    this.outTimeDeductType = outTimeDeductType;
  }

  public String getOutTimeDeductValue() {
    return outTimeDeductValue;
  }

  public void setOutTimeDeductValue(String outTimeDeductValue) {
    this.outTimeDeductValue = outTimeDeductValue;
  }

  public SettlementPayData convertTo() {
    SettlementPayData settlementPayData = BeanHelper.convert(this, SettlementPayData.class);
    settlementPayData.setOrderpromotionlist(
        BeanHelper.convertToList(orderPromotionList, Orderpromotion.class));
    return settlementPayData;
  }
}
