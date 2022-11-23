package com.ctrip.hotel.cost.infrastructure.mapper;

import com.ctrip.hotel.cost.domain.data.model.AuditOrderInfoBO;
import com.ctrip.hotel.cost.domain.settlement.CancelOrderUsedBo;
import com.ctrip.hotel.cost.domain.settlement.EnumHotelorderchannel;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.factory.Mappers;
import soa.ctrip.com.hotel.vendor.settlement.v1.Hotelorderchannel;
import soa.ctrip.com.hotel.vendor.settlement.v1.cancelorder.CancelorderRequesttype;
import soa.ctrip.com.hotel.vendor.settlement.v1.settlementdata.SettlementPayData;

/**
 * @author yangzhengzhang
 * @description
 * @date 2022-11-18 14:56
 */
@Mapper(componentModel = "spring")
public interface SettlementDataPOMapper {
    SettlementDataPOMapper INSTANCE = Mappers.getMapper(SettlementDataPOMapper.class);

    /**
     * 订单取消-->结算
     * @param cancelOrderUsedBo
     * @return
     */
    CancelorderRequesttype cancelOrderUsedBoToCancelOrderRequestType(CancelOrderUsedBo cancelOrderUsedBo);


    Hotelorderchannel enumHotelorderchannelToHotelorderchannel(EnumHotelorderchannel enumHotelorderchannel);

    /**
     * 新订单-->前置
     *
     * copy from : hotel-settlement-api-service： src/main/java/hotel/settlement/domain/managers/HotelFGSettlementDataApplyManagement.java-->method：convertDataToPrepaidData
     *
     * NullValuePropertyMappingStrategy.SET_TO_DEFAULT：
     * 源属性为null，目标属性会被赋予特定的默认值。
     * List被赋予ArrayList，Map被赋予HashMap，数组就是空数组，String是“”，基本类型或包装类是0或false，对象是空的构造方法。
     */
    @Mapping(target = "settlementid", source = "auditOrderInfoBO.settlementCallBackInfo.settlementId")
    @Mapping(target = "quantity", source = "quantity")// todo 需要计算

//    @Mapping(target = "rmbExchangeRate", source = "exchange")

    @Mapping(target = "clientOrderId", source = "cusOrderId", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.SET_TO_DEFAULT)
    @Mapping(target = "companyId", source = "auditOrderInfoBO.hotelBasicInfo.hotel", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.SET_TO_DEFAULT)
    @Mapping(target = "currency", source = "auditOrderInfoBO.orderBasicInfo.currency", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.SET_TO_DEFAULT)
    @Mapping(target = "orderDate", source = "auditOrderInfoBO.orderBasicInfo.orderDate", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.SET_TO_DEFAULT)
//    @Mapping(target = "room", source = "auditOrderInfoBO.orderBasicInfo.room", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.SET_TO_DEFAULT)
//    @Mapping(target = "roomNo", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.SET_TO_DEFAULT)
//    @Mapping(target = "checkInType", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.SET_TO_DEFAULT)
//    @Mapping(target = "clientName", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.SET_TO_DEFAULT)
//    @Mapping(target = "htlConfirmNo", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.SET_TO_DEFAULT)
//    @Mapping(target = "roomName", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.SET_TO_DEFAULT)
//    @Mapping(target = "hotelID", source = "hotel", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.SET_TO_DEFAULT)
//    @Mapping(target = "hotelInfo", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.SET_TO_DEFAULT)
//    @Mapping(target = "guarantee", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.SET_TO_DEFAULT)
//    @Mapping(target = "guaranteeWay", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.SET_TO_DEFAULT)
//    @Mapping(target = "newFgId", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.SET_TO_DEFAULT)
//    @Mapping(target = "isRoomDelay", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.SET_TO_DEFAULT)
//    @Mapping(target = "recheck", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.SET_TO_DEFAULT)
//    @Mapping(target = "allNeedGuarantee", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.SET_TO_DEFAULT)
//    @Mapping(target = "vendorChannelID", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.SET_TO_DEFAULT)
//    @Mapping(target = "modifyOperateType", source = "operateType", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.SET_TO_DEFAULT)
//    @Mapping(target = "modifyOperateSubType", source = "subOperateType", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.SET_TO_DEFAULT)
//    @Mapping(target = "modifyOperateEid", source = "operator", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.SET_TO_DEFAULT)
//    @Mapping(target = "groupOrderClass", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.SET_TO_DEFAULT)
//    @Mapping(target = "walletPay", source = "isFlashOrder", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.SET_TO_DEFAULT)
//    @Mapping(target = "hotelConfirmStatus", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.SET_TO_DEFAULT)
//    @Mapping(target = "orderConfirmType", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.SET_TO_DEFAULT)
//    @Mapping(target = "isLadderDeduct", source = "isLadderDeductPolicy", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.SET_TO_DEFAULT)
//    @Mapping(target = "adjustAmount", source = "adjustCommission", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.SET_TO_DEFAULT)
//    @Mapping(target = "adjustBatchId", source = "settlementBatchID", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.SET_TO_DEFAULT)
//    @Mapping(target = "roomAmount", source = "roomAmount", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.SET_TO_DEFAULT)// todo
//    @Mapping(target = "roomCost", source = "roomCost", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.SET_TO_DEFAULT)// todo
//    @Mapping(target = "uid", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.SET_TO_DEFAULT)
//    @Mapping(target = "insuranceFlag", source = "insuranceSupportType", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.SET_TO_DEFAULT)
//    @Mapping(target = "outTimeDeductType", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.SET_TO_DEFAULT)
//    @Mapping(target = "outTimeDeductValue", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.SET_TO_DEFAULT)
//    @Mapping(target = "fgId", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.SET_TO_DEFAULT)
//    @Mapping(target = "remarks", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.SET_TO_DEFAULT)
    @Mapping(target = "outSettlementNo", expression = "java( new java.lang.String(\"FG-\").concat(auditOrderInfoBO.getAuditRoomInfoList().get(0).getAuditRoomBasicInfo().getFgid().toString()) )")
    @Mapping(target = "commissionType", expression = "java( " +
            "auditOrderInfoBO.getAuditRoomInfoList().get(0).getAuditRoomOtherInfo() != null " +
            "&& auditOrderInfoBO.getAuditRoomInfoList().get(0).getAuditRoomOtherInfo().getAdjustCommissionType() != null " +
            "? auditOrderInfoBO.getAuditRoomInfoList().get(0).getAuditRoomOtherInfo().getAdjustCommissionType() : 0" +
            ")")
    @Mapping(target = "orderchannel", expression = "java( Hotelorderchannel.hfg )") // todo 写到SettlementPayDataUsedBo init?
//    @Mapping(target = "sourceId", expression = "java( new java.lang.String(\"6\") )") // todo 写到SettlementPayDataUsedBo init?
//    @Mapping(target = "modifyOperateDateTime", expression = "java( settlementPayDataUsedBo.getOperateTime() == null ? DateHelper.getCSharpMinDate() : settlementPayDataUsedBo.getOperateTime() )")
//    @Mapping(target = "bidFlag", expression = "java( settlementPayDataUsedBo.getBidPrice() != null ? new String(\"T\") : null )")
//    @Mapping(target = "isRapidSettlement", expression = "java( settlementPayDataUsedBo.getOperatMode() == null || !settlementPayDataUsedBo.getOperatMode().equals(\"S\") ? new String(\"F\") : new String(\"T\") )")
//    @Mapping(target = "roomNameEn", expression = "java( settlementPayDataUsedBo.getRoom() == null ? new String(\"\") : settlementPayDataUsedBo.getRoomNameEn() )")
//    @Mapping(target = "splitOrder", expression = "java( new String(\"T\") )")
    @Mapping(target = "settlementPriceType", expression = "java( new String(\"C\") )")
    SettlementPayData newOrderToSettlementPayDataReceive(AuditOrderInfoBO auditOrderInfoBO);

}
