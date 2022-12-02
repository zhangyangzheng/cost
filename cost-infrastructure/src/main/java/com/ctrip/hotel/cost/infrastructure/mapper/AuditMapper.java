package com.ctrip.hotel.cost.infrastructure.mapper;

import com.ctrip.hotel.cost.domain.data.model.AuditOrderInfoBO;
import com.ctrip.hotel.cost.domain.data.model.OrderAuditFgMqBO;
import com.ctrip.hotel.cost.domain.data.model.SettlementCallBackInfo;
import com.ctrip.hotel.cost.infrastructure.model.dto.FgBackToAuditDto;
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

    @Mapping(target = "amount", source = "priceAmount")
    @Mapping(target = "cost", source = "costAmount")
    @Mapping(target = "promotionAmountHotel", source = "hotelPromotionAmount")
    @Mapping(target = "promotionCostHotel", source = "hotelPromotionCost")
    @Mapping(target = "promotionAmountTrip", source = "tripPromotionAmount")
    @Mapping(target = "promotionCostTrip", source = "tripPromotionCost")
    FgBackToAuditDto AuditOrderInfoBOToFgBackToAuditDto(AuditOrderInfoBO auditOrderInfoBO, OrderAuditFgMqBO orderAuditFgMqBO, SettlementCallBackInfo settlementCallBackInfo);
}
