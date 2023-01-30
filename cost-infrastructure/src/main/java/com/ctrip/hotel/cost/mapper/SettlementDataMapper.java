package com.ctrip.hotel.cost.mapper;

import com.ctrip.hotel.cost.common.BigDecimalHelper;
import com.ctrip.hotel.cost.domain.data.model.AuditOrderInfoBO;
import com.ctrip.hotel.cost.domain.data.model.PromotionDailyInfo;
import com.ctrip.hotel.cost.domain.settlement.CancelOrderUsedBo;
import com.ctrip.soa.hotel.settlement.api.SettleDataRequest;
import com.ctrip.soa.hotel.settlement.api.SettlementPromotionDetail;
import hotel.settlement.common.DateHelper;
import hotel.settlement.common.helpers.DefaultValueHelper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.factory.Mappers;
import soa.ctrip.com.hotel.vendor.settlement.v1.Orderpromotion;
import soa.ctrip.com.hotel.vendor.settlement.v1.cancelorder.CancelorderRequesttype;
import soa.ctrip.com.hotel.vendor.settlement.v1.settlementdata.SettlementPayData;

import java.util.List;
import java.util.Optional;

/**
 * @author yangzhengzhang
 * @description
 * @date 2022-11-18 14:56
 */
@Mapper(componentModel = "spring", imports = {DefaultValueHelper.class, Optional.class})
public interface SettlementDataMapper {
    SettlementDataMapper INSTANCE = Mappers.getMapper(SettlementDataMapper.class);

    /**
     * 订单取消-->结算
     *
     * @param cancelOrderUsedBo
     * @return
     */
    CancelorderRequesttype cancelOrderUsedBoToCancelOrderRequestType(CancelOrderUsedBo cancelOrderUsedBo);

    /**
     * 新订单-->前置
     * <p>
     * copy from : hotel-settlement-api-service： src/main/java/hotel/settlement/domain/managers/HotelFGSettlementDataApplyManagement.java-->method：convertDataToPrepaidData
     * <p>
     * NullValuePropertyMappingStrategy.SET_TO_DEFAULT：
     * 源属性为null，目标属性会被赋予特定的默认值。
     * List被赋予ArrayList，Map被赋予HashMap，数组就是空数组，String是“”，基本类型或包装类是0或false，对象是空的构造方法。
     */
    @Mapping(target = "quantity", defaultValue = "0")// 需要计算
    @Mapping(target = "bidFlag", expression = "java( auditOrderInfoBO.getBidPrice() != null ? new String(\"T\") : null )")
    @Mapping(target = "clientOrderId", source = "cusOrderId", defaultValue = "")
    @Mapping(target = "orderDate", source = "auditOrderInfoBO.orderBasicInfo.orderDate")
    @Mapping(target = "vendorChannelId", source = "auditOrderInfoBO.orderBasicInfo.vendorChannelID", defaultValue = "")
    @Mapping(target = "groupOrderClass", source = "auditOrderInfoBO.orderBasicInfo.groupOrderClass", defaultValue = "")
    @Mapping(target = "orderConfirmType", source = "auditOrderInfoBO.orderBasicInfo.orderConfirmType", defaultValue = "")
    @Mapping(target = "isLadderDeduct", source = "auditOrderInfoBO.orderBasicInfo.isLadderDeductPolicy", defaultValue = "")
    @Mapping(target = "insuranceFlag", expression = "java( DefaultValueHelper.getValue(auditOrderInfoBO.getOrderBasicInfo().getInsuranceSupportType()) )")
    @Mapping(target = "rmbExchangeRate", expression = "java( DefaultValueHelper.getValue(auditOrderInfoBO.getOrderBasicInfo().getExchange()) )")
    @Mapping(target = "uid", source = "auditOrderInfoBO.orderBasicInfo.uid", defaultValue = "")
    @Mapping(target = "remarks", defaultValue = "")

    @Mapping(target = "eta", expression = "java( " +
            "auditOrderInfoBO.getAuditRoomInfoList().get(0).getAuditRoomBasicInfo().getEta() " +
            ")")
    @Mapping(target = "etd", expression = "java( " +
            "auditOrderInfoBO.getAuditRoomInfoList().get(0).getAuditRoomBasicInfo().getRealETD() " +
            ")")
    @Mapping(target = "realETD", expression = "java( " +
            "auditOrderInfoBO.getAuditRoomInfoList().get(0).getAuditRoomBasicInfo().getRealETD() " +
            ")")
    @Mapping(target = "modifyOperateDateTime", expression = "java( " +
            "auditOrderInfoBO.getAuditRoomInfoList().get(0).getAuditRoomBasicInfo().getOperateTime()" +
            ")")
    @Mapping(target = "adjustAmount", expression = "java( " +
            "auditOrderInfoBO.getAdjustAmount() != null && !BigDecimal.ZERO.equals(auditOrderInfoBO.getAdjustAmount())  ? auditOrderInfoBO.getAdjustAmount() : new BigDecimal(\"0.0000\") " +
            ")")
    @Mapping(target = "settlementid", expression = "java( auditOrderInfoBO.getSettlementCallBackInfo().getSettlementId() != null " +
            "&& auditOrderInfoBO.getSettlementCallBackInfo().getSettlementId() != 0 " +
            "? auditOrderInfoBO.getSettlementCallBackInfo().getSettlementId() : null)")
    @Mapping(target = "fgId", expression = "java( " +
            "auditOrderInfoBO.getAuditRoomInfoList().get(0).getAuditRoomBasicInfo().getFgid() " +
            ")")
    @Mapping(target = "outSettlementNo", expression = "java( " +
            "new String(\"FG-\").concat(auditOrderInfoBO.getAuditRoomInfoList().get(0).getAuditRoomBasicInfo().getFgid().toString()) " +
            ")")
    @Mapping(target = "room", expression = "java( " +
            "auditOrderInfoBO.getAuditRoomInfoList().get(0).getAuditRoomBasicInfo().getRoom() == null ? new String(\"\") : auditOrderInfoBO.getAuditRoomInfoList().get(0).getAuditRoomBasicInfo().getRoom().toString() " +
            ")")
    @Mapping(target = "roomNo", expression = "java( " +
            "auditOrderInfoBO.getAuditRoomInfoList().get(0).getAuditRoomBasicInfo().getRoomNo() == null ? new String(\"\") : auditOrderInfoBO.getAuditRoomInfoList().get(0).getAuditRoomBasicInfo().getRoomNo() " +
            ")")
    @Mapping(target = "checkInType", expression = "java( " +
            "auditOrderInfoBO.getAuditRoomInfoList().get(0).getAuditRoomBasicInfo().getCheckInType() == null ? new String(\"\") : auditOrderInfoBO.getAuditRoomInfoList().get(0).getAuditRoomBasicInfo().getCheckInType() " +
            ")")
    @Mapping(target = "clientName", expression = "java( " +
            "auditOrderInfoBO.getAuditRoomInfoList().get(0).getAuditRoomBasicInfo().getClientName() == null ? new String(\"\") : auditOrderInfoBO.getAuditRoomInfoList().get(0).getAuditRoomBasicInfo().getClientName() " +
            ")")
    @Mapping(target = "htlConfirmNo", expression = "java( " +
            "auditOrderInfoBO.getAuditRoomInfoList().get(0).getAuditRoomBasicInfo().getHtlConfirmNo() == null ? new String(\"\") : auditOrderInfoBO.getAuditRoomInfoList().get(0).getAuditRoomBasicInfo().getHtlConfirmNo() " +
            ")")
    @Mapping(target = "roomName", expression = "java( " +
            "auditOrderInfoBO.getAuditRoomInfoList().get(0).getAuditRoomBasicInfo().getRoomName() == null ? new String(\"\") : auditOrderInfoBO.getAuditRoomInfoList().get(0).getAuditRoomBasicInfo().getRoomName() " +
            ")")
    @Mapping(target = "roomNameEn", expression = "java( " +
            "auditOrderInfoBO.getAuditRoomInfoList().get(0).getAuditRoomBasicInfo().getRoom() == null ? new String(\"\") : auditOrderInfoBO.getAuditRoomInfoList().get(0).getAuditRoomBasicInfo().getRoomNameEN() " +
            ")")
    @Mapping(target = "hotelInfo", expression = "java( " +
            "auditOrderInfoBO.getAuditRoomInfoList().get(0).getAuditRoomBasicInfo().getHotelInfo() == null ? new String(\"\") : auditOrderInfoBO.getAuditRoomInfoList().get(0).getAuditRoomBasicInfo().getHotelInfo() " +
            ")")
    @Mapping(target = "newFgId", expression = "java( " +
            "auditOrderInfoBO.getAuditRoomInfoList().get(0).getAuditRoomBasicInfo().getNewFGID() == null ? new String(\"\") : auditOrderInfoBO.getAuditRoomInfoList().get(0).getAuditRoomBasicInfo().getNewFGID().toString() " +
            ")")
    @Mapping(target = "isRoomDelay", expression = "java( " +
            "auditOrderInfoBO.getAuditRoomInfoList().get(0).getAuditRoomBasicInfo().getIsRoomDelay() == null ? new String(\"\") : auditOrderInfoBO.getAuditRoomInfoList().get(0).getAuditRoomBasicInfo().getIsRoomDelay() " +
            ")")
    @Mapping(target = "recheck", expression = "java( " +
            "auditOrderInfoBO.getAuditRoomInfoList().get(0).getAuditRoomBasicInfo().getRecheck() == null ? new String(\"\") : auditOrderInfoBO.getAuditRoomInfoList().get(0).getAuditRoomBasicInfo().getRecheck() " +
            ")")
    @Mapping(target = "modifyOperateType", expression = "java( " +
            "auditOrderInfoBO.getAuditRoomInfoList().get(0).getAuditRoomBasicInfo().getOperateType() == null ? new String(\"\") : auditOrderInfoBO.getAuditRoomInfoList().get(0).getAuditRoomBasicInfo().getOperateType() " +
            ")")
    @Mapping(target = "modifyOperateSubType", expression = "java( " +
            "auditOrderInfoBO.getAuditRoomInfoList().get(0).getAuditRoomBasicInfo().getSubOperateType() == null ? new String(\"\") : auditOrderInfoBO.getAuditRoomInfoList().get(0).getAuditRoomBasicInfo().getSubOperateType() " +
            ")")
    @Mapping(target = "modifyOperateEid", expression = "java( " +
            "auditOrderInfoBO.getAuditRoomInfoList().get(0).getAuditRoomBasicInfo().getOperator() == null ? new String(\"\") : auditOrderInfoBO.getAuditRoomInfoList().get(0).getAuditRoomBasicInfo().getOperator() " +
            ")")
    @Mapping(target = "commissionType", expression = "java( " +
            "auditOrderInfoBO.getOrderAuditFgMqBO().getBusinessType() != null" +
            "? auditOrderInfoBO.getOrderAuditFgMqBO().getBusinessType()" +
            ": 0)")
    @Mapping(target = "hotelConfirmStatus", expression = "java( " +
            "auditOrderInfoBO.getAuditRoomInfoList().get(0).getAuditRoomOtherInfo() != null " +
            "&& auditOrderInfoBO.getAuditRoomInfoList().get(0).getAuditRoomOtherInfo().getHotelConfirmStatus() != null " +
            "? auditOrderInfoBO.getAuditRoomInfoList().get(0).getAuditRoomOtherInfo().getHotelConfirmStatus() : 0" +
            ")")
    @Mapping(target = "adjustBatchId", expression = "java( " +
            "auditOrderInfoBO.getAuditRoomInfoList().get(0).getAuditRoomOtherInfo() != null " +
            "&& auditOrderInfoBO.getAuditRoomInfoList().get(0).getAuditRoomOtherInfo().getSettlementBatchID() != null " +
            "? auditOrderInfoBO.getAuditRoomInfoList().get(0).getAuditRoomOtherInfo().getSettlementBatchID().intValue() : 0" +
            ")")
    @Mapping(
            target = "currency",
            expression =
                    "java((auditOrderInfoBO.getOrderBasicInfo().getVendorChannelID() != null "
                            + "&& auditOrderInfoBO.getOrderBasicInfo().getVendorChannelID() > 0 "
                            + "&& auditOrderInfoBO.getAuditRoomInfoList().get(0).getAuditRoomOtherInfo() != null"
                            + "&& Optional.ofNullable(auditOrderInfoBO.getAuditRoomInfoList().get(0).getAuditRoomOtherInfo().getActualAmount()).orElse(new BigDecimal(\"0\"))"
                            + ".compareTo(Optional.ofNullable(auditOrderInfoBO.getAuditRoomInfoList().get(0).getAuditRoomOtherInfo().getActualCost()).orElse(new BigDecimal(\"0\"))) > 0) ? "
                            + "auditOrderInfoBO.getAuditRoomInfoList().get(0).getAuditRoomOtherInfo().getPayCurrency() : auditOrderInfoBO.getOrderBasicInfo().getCurrency())")
    @Mapping(target = "hotelID", source = "auditOrderInfoBO.hotelBasicInfo.hotel", defaultValue = "0")
    @Mapping(target = "companyId", source = "auditOrderInfoBO.hotelBasicInfo.hotel", defaultValue = "")
    @Mapping(target = "roomAmount", source = "auditOrderInfoBO.priceAmount", defaultValue = "")
    @Mapping(target = "roomCost", source = "auditOrderInfoBO.costAmount", defaultValue = "")
    @Mapping(target = "isRapidSettlement", expression = "java( auditOrderInfoBO.getHotelBasicInfo().getOperatMode() == null || !auditOrderInfoBO.getHotelBasicInfo().getOperatMode().equals(\"S\") ? new String(\"F\") : new String(\"T\") )")
    @Mapping(target = "guarantee", expression = "java( " +
            "auditOrderInfoBO.getGuaranteeInfo() != null " +
            "&& auditOrderInfoBO.getGuaranteeInfo().getGuarantee() != null " +
            "? DefaultValueHelper.getValue(auditOrderInfoBO.getGuaranteeInfo().getGuarantee()) : \"\"" +
            ")")
    @Mapping(target = "guaranteeWay", expression = "java( " +
            "auditOrderInfoBO.getGuaranteeInfo() != null " +
            "&& auditOrderInfoBO.getGuaranteeInfo().getGuaranteeWay() != null " +
            "? DefaultValueHelper.getValue(auditOrderInfoBO.getGuaranteeInfo().getGuaranteeWay()) : \"\"" +
            ")")
    @Mapping(target = "allNeedGuarantee", expression = "java( " +
            "auditOrderInfoBO.getGuaranteeInfo() != null " +
            "&& auditOrderInfoBO.getGuaranteeInfo().getAllNeedGuarantee() != null " +
            "? DefaultValueHelper.getValue(auditOrderInfoBO.getGuaranteeInfo().getAllNeedGuarantee()) : \"\"" +
            ")")
    @Mapping(target = "walletPay", expression = "java( " +
            "auditOrderInfoBO.getFlashOrderInfo() != null " +
            "&& auditOrderInfoBO.getFlashOrderInfo().getIsFlashOrder() != null " +
            "? DefaultValueHelper.getValue(auditOrderInfoBO.getFlashOrderInfo().getIsFlashOrder()) : false" +
            ")")
    @Mapping(target = "outTimeDeductType", expression = "java( " +
            "auditOrderInfoBO.getOutTimeDeductInfo() != null " +
            "&& auditOrderInfoBO.getOutTimeDeductInfo().getOutTimeDeductType() != null " +
            "? DefaultValueHelper.getValue(auditOrderInfoBO.getOutTimeDeductInfo().getOutTimeDeductType().toString()) : \"\"" +
            ")")
    @Mapping(target = "outTimeDeductValue", expression = "java( " +
            "auditOrderInfoBO.getOutTimeDeductInfo() == null || auditOrderInfoBO.getOutTimeDeductInfo().getOutTimeDeductValue() == null " +
            "? \"\" : auditOrderInfoBO.getOutTimeDeductInfo().getOutTimeDeductValue().toString() )")
    @Mapping(target = "zeroCommissionDeductRate", expression = "java( " +
            "auditOrderInfoBO.getTechFeeInfo() != null " +
            "&& auditOrderInfoBO.getTechFeeInfo().getZeroCommissionFeeRatio() != null " +
            "? DefaultValueHelper.getValue(auditOrderInfoBO.getTechFeeInfo().getZeroCommissionFeeRatio()) : null)")
    @Mapping(target = "tripPromotionAmount", expression = "java( null )" )

    @Mapping(target = "orderchannel", expression = "java( Hotelorderchannel.hfg )")
    @Mapping(target = "sourceId", expression = "java( new String(\"6\") )")
    @Mapping(target = "splitOrder", expression = "java( new String(\"T\") )")
    @Mapping(target = "settlementPriceType", expression = "java( new String(\"C\") )")
    @Mapping(target = "bidPrice", expression = "java( auditOrderInfoBO.getBidPrice() == null ? null : DefaultValueHelper.getValue(auditOrderInfoBO.getBidPrice()))")
    @Mapping(target = "orderpromotionlist", source = "promotionDailyInfoList", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.SET_TO_DEFAULT)
    SettlementPayData newOrderToSettlementPayDataReceive(AuditOrderInfoBO auditOrderInfoBO);

    List<Orderpromotion> promotionConvert(List<PromotionDailyInfo> promotion);

    @Mappings({
            @Mapping(target = "beginDate", source = "promotionDailyInfo.effectDate"),
            @Mapping(target = "priceAmount", source = "promotionDailyInfo.amount"),
            @Mapping(target = "costAmount", source = "promotionDailyInfo.costDiscountAmount"),
            @Mapping(target = "promotionName", source = "promotionDailyInfo.prepayCampaignName"),
            @Mapping(target = "promotionNumber", source = "promotionDailyInfo.prepayCampaignID"),
            @Mapping(target = "promotionVersionID", source = "promotionDailyInfo.prepayCampaignVersionID")
    })
    Orderpromotion createPromotion(PromotionDailyInfo promotionDailyInfo);

  /**
   * 新订单-->结算
   *
   * @param auditOrderInfoBO
   * @return
   */
  //    @Mapping(target = "quantity", defaultValue = "0")// 需要计算
  @Mapping(target = "id", expression = "java( Integer.valueOf(0) )")
  @Mapping(target = "merchantId", expression = "java( Integer.valueOf(6) )")
  //    @Mapping(target = "settlementItemName", expression = "java(
  // SettlementItemName.FGHotel.getShowName() )")
  @Mapping(target = "settlementPriceType", expression = "java( new String(\"P\") )")
  @Mapping(target = "collectionType", expression = "java( new String(\"P\") )") // pushHWP 为 C
  @Mapping(target = "sourceId", expression = "java( new String(\"6\") )")
  //    @Mapping(target = "channelType", expression = "java( ChannelType.FGID.name() )")

  @Mapping(
      target = "settlementId",
      source = "auditOrderInfoBO.settlementCallBackInfo.settlementId") // todo 修改单传这个，取消单不传
  @Mapping(target = "orderId", source = "orderId", defaultValue = "")
  @Mapping(
      target = "currency",
      expression =
          "java((auditOrderInfoBO.getOrderBasicInfo().getVendorChannelID() != null "
              + "&& auditOrderInfoBO.getOrderBasicInfo().getVendorChannelID() > 0 "
              + "&& auditOrderInfoBO.getAuditRoomInfoList().get(0).getAuditRoomOtherInfo() != null"
              + "&& Optional.ofNullable(auditOrderInfoBO.getAuditRoomInfoList().get(0).getAuditRoomOtherInfo().getActualAmount()).orElse(new BigDecimal(\"0\"))"
              + ".compareTo(Optional.ofNullable(auditOrderInfoBO.getAuditRoomInfoList().get(0).getAuditRoomOtherInfo().getActualCost()).orElse(new BigDecimal(\"0\"))) > 0) ? "
              + "auditOrderInfoBO.getAuditRoomInfoList().get(0).getAuditRoomOtherInfo().getPayCurrency() : auditOrderInfoBO.getOrderBasicInfo().getCurrency())")
  @Mapping(target = "orderDate", source = "auditOrderInfoBO.orderBasicInfo.orderDate")
  @Mapping(
      target = "companyID",
      source = "auditOrderInfoBO.hotelBasicInfo.hotel",
      defaultValue = "")

  //    @Mapping(target = "roomName", expression = "java( " +
  //
  // "auditOrderInfoBO.getAuditRoomInfoList().get(0).getAuditRoomBasicInfo().getRoomName() == null ?
  // new String(\"\") :
  // auditOrderInfoBO.getAuditRoomInfoList().get(0).getAuditRoomBasicInfo().getRoomName() " +
  //            ")")
  @Mapping(
      target = "outSettlementNo",
      expression =
          "java( "
              + "auditOrderInfoBO.getAuditRoomInfoList().get(0).getAuditRoomBasicInfo().getFgid() > 0 ? "
              + "new String(\"FG-\").concat(auditOrderInfoBO.getAuditRoomInfoList().get(0).getAuditRoomBasicInfo().getFgid().toString()) : "
              + "new String(\"FM-\").concat(auditOrderInfoBO.getAuditRoomInfoList().get(0).getAuditRoomBasicInfo().getFgid().toString()) "
              + ")")
  @Mapping(
      target = "settlementPromotionDetailLists",
      source = "promotionDailyInfoList",
      nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.SET_TO_DEFAULT)
  SettleDataRequest newOrderToSettlementApplyList(AuditOrderInfoBO auditOrderInfoBO);

    @Mappings({
            @Mapping(target = "beginDate", source = "promotionDailyInfo.effectDate"),
            @Mapping(target = "priceAmount", source = "promotionDailyInfo.amount"),
            @Mapping(target = "costAmount", source = "promotionDailyInfo.costDiscountAmount"),
            @Mapping(target = "promotionName", source = "promotionDailyInfo.prepayCampaignName"),
            @Mapping(target = "promotionNumber", source = "promotionDailyInfo.prepayCampaignID"),
            @Mapping(target = "promotionVersionID", source = "promotionDailyInfo.prepayCampaignVersionID")
    })
    SettlementPromotionDetail createSettlementPromotionDetail(PromotionDailyInfo promotionDailyInfo);
}
