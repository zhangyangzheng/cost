package com.ctrip.hotel.cost.mapper;

import com.ctrip.hotel.cost.domain.data.model.AuditOrderInfoBO;
import com.ctrip.hotel.cost.domain.data.model.OrderAuditFgMqBO;
import com.ctrip.hotel.cost.domain.data.model.SettlementCallBackInfo;
import com.ctrip.hotel.cost.model.dto.FgBackToAuditDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

/**
 * @author yangzhengzhang
 * @description
 * @date 2022-12-01 19:58
 */
@Mapper(componentModel = "spring")
public interface AuditMapper {
    AuditMapper INSTANCE = Mappers.getMapper(AuditMapper.class);

    @Mapping(target = "orderId", source = "orderAuditFgMqBO.orderId")
    @Mapping(target = "fgId", source = "orderAuditFgMqBO.fgId")
    @Mapping(target = "businessType", source = "orderAuditFgMqBO.businessType")
    @Mapping(target = "opType", source = "orderAuditFgMqBO.opType")
    @Mapping(target = "referenceId", source = "orderAuditFgMqBO.referenceId")
    @Mapping(target = "isThrow", source = "orderAuditFgMqBO.isThrow")
    @Mapping(target = "settlementId", source = "settlementCallBackInfo.settlementId")
    @Mapping(target = "hwpSettlementId", source = "settlementCallBackInfo.hwpSettlementId")
    @Mapping(target = "orderInfoId", source = "settlementCallBackInfo.orderInfoId")
    @Mapping(target = "pushReferenceId", source = "settlementCallBackInfo.pushReferenceId")
    @Mapping(target = "hwpReferenceId", source = "settlementCallBackInfo.hwpReferenceId")
    @Mapping(target = "pushWalletPay", source = "settlementCallBackInfo.pushWalletPay")
    @Mapping(target = "amount", source = "auditOrderInfoBO.priceAmount")
    @Mapping(target = "cost", source = "auditOrderInfoBO.costAmount")
    @Mapping(target = "promotionAmountHotel", source = "auditOrderInfoBO.hotelPromotionAmount")
    @Mapping(target = "promotionCostHotel", source = "auditOrderInfoBO.hotelPromotionCost")
    @Mapping(target = "promotionAmountTrip", source = "auditOrderInfoBO.tripPromotionAmount")
    @Mapping(target = "promotionCostTrip", source = "auditOrderInfoBO.tripPromotionCost")
    @Mapping(target = "promotionCashBackAmount", source = "auditOrderInfoBO.promotionCashBackAmount")
    @Mapping(target = "promotionCashBackCost", source = "auditOrderInfoBO.promotionCashBackCost")
    FgBackToAuditDto AuditOrderInfoBOToFgBackToAuditDto(AuditOrderInfoBO auditOrderInfoBO, OrderAuditFgMqBO orderAuditFgMqBO, SettlementCallBackInfo settlementCallBackInfo);
}
