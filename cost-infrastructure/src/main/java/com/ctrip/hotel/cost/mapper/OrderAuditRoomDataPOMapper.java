package com.ctrip.hotel.cost.mapper;

import com.ctrip.hotel.cost.domain.data.model.AuditOrderInfoBO;
import com.ctrip.hotel.cost.domain.element.bid.BidPriceFgOrderInfo;
import com.ctrip.hotel.cost.domain.element.commission.AdjustCommissionPriceOrderInfo;
import com.ctrip.hotel.cost.domain.element.promotion.*;
import com.ctrip.hotel.cost.domain.element.promotion.cashBack.PromotionCostCashBackPriceFgOrderInfo;
import com.ctrip.hotel.cost.domain.element.promotion.cashBack.PromotionSellingCashBackPriceFgOrderInfo;
import com.ctrip.hotel.cost.domain.element.room.fg.RoomCostPriceFgOrderInfo;
import com.ctrip.hotel.cost.domain.element.room.fg.RoomSellingPriceFgOrderInfo;
import com.ctrip.hotel.cost.domain.element.techfee.ZeroCommissionFeePriceOrderInfo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import soa.ctrip.com.hotel.order.checkin.audit.v2.getOrderAuditRoomData.*;

/**
 * @author yangzhengzhang
 * @description
 * @date 2022-11-02 15:47
 */
@Mapper(componentModel = "spring")
public interface OrderAuditRoomDataPOMapper {
    OrderAuditRoomDataPOMapper INSTANCE = Mappers.getMapper(OrderAuditRoomDataPOMapper.class);

    BidPriceFgOrderInfo auditOrderToBid(BidOrderInfo bidOrderInfo, AuditRoomBasicInfo roomBasicInfo);

    @Mapping(target = "orderETA", source = "roomBasicInfo.eta")
    RoomSellingPriceFgOrderInfo auditOrderToRoomPrice(OrderPriceInfo orderPriceInfo, AuditRoomBasicInfo roomBasicInfo);
    @Mapping(target = "orderETA", source = "roomBasicInfo.eta")
    RoomCostPriceFgOrderInfo auditOrderToRoomCost(OrderPriceInfo orderPriceInfo, AuditRoomBasicInfo roomBasicInfo);

    @Mapping(target = "auditRoom", source = "roomBasicInfo.room")
    @Mapping(target = "orderRoom", source = "orderBasicInfo.room")
    @Mapping(target = "eta", source = "roomBasicInfo.eta")
    @Mapping(target = "promotionDailyInfoID", source = "promotionInfo.promotionDailyInfoID")
    PromotionSellingPriceFgOrderInfo auditOrderToPromotion(PromotionDailyInfo promotionInfo, AuditRoomBasicInfo roomBasicInfo, OrderBasicInfo orderBasicInfo);
    @Mapping(target = "auditRoom", source = "roomBasicInfo.room")
    @Mapping(target = "orderRoom", source = "orderBasicInfo.room")
    @Mapping(target = "eta", source = "roomBasicInfo.eta")
    @Mapping(target = "promotionDailyInfoID", source = "promotionInfo.promotionDailyInfoID")
    PromotionCostPriceFgOrderInfo auditOrderToPromotionCost(PromotionDailyInfo promotionInfo, AuditRoomBasicInfo roomBasicInfo, OrderBasicInfo orderBasicInfo);
    @Mapping(target = "auditRoom", source = "roomBasicInfo.room")
    @Mapping(target = "orderRoom", source = "orderBasicInfo.room")
    @Mapping(target = "eta", source = "roomBasicInfo.eta")
    @Mapping(target = "promotionDailyInfoID", source = "promotionInfo.promotionDailyInfoID")
    TripPromotionSellingPriceFgOrderInfo auditOrderToTripPromotion(PromotionDailyInfo promotionInfo, AuditRoomBasicInfo roomBasicInfo, OrderBasicInfo orderBasicInfo);
    @Mapping(target = "auditRoom", source = "roomBasicInfo.room")
    @Mapping(target = "orderRoom", source = "orderBasicInfo.room")
    @Mapping(target = "eta", source = "roomBasicInfo.eta")
    @Mapping(target = "promotionDailyInfoID", source = "promotionInfo.promotionDailyInfoID")
    TripPromotionCostPriceFgOrderInfo auditOrderToTripPromotionCost(PromotionDailyInfo promotionInfo, AuditRoomBasicInfo roomBasicInfo, OrderBasicInfo orderBasicInfo);
    @Mapping(target = "auditRoom", source = "roomBasicInfo.room")
    @Mapping(target = "orderRoom", source = "orderBasicInfo.room")
    @Mapping(target = "eta", source = "roomBasicInfo.eta")
    @Mapping(target = "promotionDailyInfoID", source = "promotionInfo.promotionDailyInfoID")
    PromotionCostCashBackPriceFgOrderInfo auditOrderToCashBackPromotionCost(PromotionDailyInfo promotionInfo, AuditRoomBasicInfo roomBasicInfo, OrderBasicInfo orderBasicInfo);
    @Mapping(target = "auditRoom", source = "roomBasicInfo.room")
    @Mapping(target = "orderRoom", source = "orderBasicInfo.room")
    @Mapping(target = "eta", source = "roomBasicInfo.eta")
    @Mapping(target = "promotionDailyInfoID", source = "promotionInfo.promotionDailyInfoID")
    PromotionSellingCashBackPriceFgOrderInfo auditOrderToCashBackPromotion(PromotionDailyInfo promotionInfo, AuditRoomBasicInfo roomBasicInfo, OrderBasicInfo orderBasicInfo);
    @Mapping(target = "auditRoom", source = "roomBasicInfo.room")
    @Mapping(target = "orderRoom", source = "orderBasicInfo.room")
    @Mapping(target = "eta", source = "roomBasicInfo.eta")
    @Mapping(target = "promotionDailyInfoID", source = "promotionInfo.promotionDailyInfoID")
    PromotionCostBuyoutDiscountPriceFgOrderInfo auditOrderToBuyoutDiscountPromotion(PromotionDailyInfo promotionInfo, AuditRoomBasicInfo roomBasicInfo, OrderBasicInfo orderBasicInfo);

    AdjustCommissionPriceOrderInfo auditOrderToAdjustCommission(AuditRoomOtherInfo auditRoomOtherInfo, HotelBasicInfo hotelBasicInfo);

    ZeroCommissionFeePriceOrderInfo auditOrderToZeroCommissionFee(TechFeeInfo techFeeInfo);

    AuditOrderInfoBO auditOrderToBO(OrderAuditRoomData auditRoomData);
}
