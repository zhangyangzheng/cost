package com.ctrip.hotel.cost.infrastructure.mapper;

import com.ctrip.hotel.cost.domain.element.bid.BidPriceFgOrderInfo;
import com.ctrip.hotel.cost.domain.element.commission.AdjustCommissionPriceOrderInfo;
import com.ctrip.hotel.cost.domain.element.room.fg.RoomCostPriceFgOrderInfo;
import com.ctrip.hotel.cost.domain.element.room.fg.RoomSellingPriceFgOrderInfo;
import com.ctrip.hotel.cost.domain.element.promotion.PromotionCostPriceFgOrderInfo;
import com.ctrip.hotel.cost.domain.element.promotion.PromotionSellingPriceFgOrderInfo;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import soa.ctrip.com.hotel.order.checkin.audit.v2.getOrderAuditRoomData.*;

/**
 * @author yangzhengzhang
 * @description
 * @date 2022-11-02 15:47
 */
@Mapper(componentModel = "spring")
public interface OrderAuditRoomDataPoMapper {
    OrderAuditRoomDataPoMapper INSTANCE = Mappers.getMapper(OrderAuditRoomDataPoMapper.class);

    BidPriceFgOrderInfo auditOrderToBid(BidOrderInfo bidOrderInfo, AuditRoomBasicInfo roomBasicInfo);

    RoomSellingPriceFgOrderInfo auditOrderToRoomPrice(OrderPriceInfo orderPriceInfo, AuditRoomBasicInfo roomBasicInfo);
    RoomCostPriceFgOrderInfo auditOrderToRoomCost(OrderPriceInfo orderPriceInfo, AuditRoomBasicInfo roomBasicInfo);

    PromotionSellingPriceFgOrderInfo auditOrderToPromotion(PromotionDailyInfo promotionInfo, AuditRoomBasicInfo roomBasicInfo);
    PromotionCostPriceFgOrderInfo auditOrderToPromotionCost(PromotionDailyInfo promotionInfo, AuditRoomBasicInfo roomBasicInfo);

    AdjustCommissionPriceOrderInfo auditOrderToAdjustCommission(AuditRoomOtherInfo auditRoomOtherInfo, HotelBasicInfo hotelBasicInfo);

}
