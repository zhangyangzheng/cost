package com.ctrip.hotel.cost.infrastructure.model.bo;

import com.ctrip.hotel.cost.domain.settlement.EnumHotelorderchannel;
import hotel.settlement.common.beans.BeanHelper;
import lombok.Data;
import soa.ctrip.com.hotel.vendor.settlement.v1.Orderpromotion;
import soa.ctrip.com.hotel.vendor.settlement.v1.settlementdata.SettlementPayData;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.List;

// 原契约对象(SettlementPayData)不需要的字段注释掉了
@Data
public class SettlementPayDataUsedBo {

  public static class OrderPromotionUsed {
    public Integer promotionNumber;

    public String promotionName;

    public BigDecimal priceAmount;

    public BigDecimal costAmount;

    public Calendar beginDate;

    public Long promotionVersionID;

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

  public EnumHotelorderchannel orderchannel;

  public Calendar orderDate;

  private String currency;

  public BigDecimal quantity;

  public String sourceId;

  private String hotel;

  public Long room;//房型ID

  public String roomNo;

  public String checkInType;

  public Calendar eta;

  public Calendar etd;

  private Integer adjustCommissionType;

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

  public Calendar operateTime;

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

  private String operatMode;

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

  private BigDecimal exchange;

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

  private String cusOrderId;

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

  public SettlementPayData convertTo() {
    SettlementPayData settlementPayData = BeanHelper.convert(this, SettlementPayData.class);
    settlementPayData.setOrderpromotionlist(
        BeanHelper.convertToList(orderPromotionList, Orderpromotion.class));
    return settlementPayData;
  }
}
