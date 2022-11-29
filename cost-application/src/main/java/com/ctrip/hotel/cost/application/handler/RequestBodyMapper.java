package com.ctrip.hotel.cost.application.handler;

import com.ctrip.hotel.cost.application.model.vo.AuditOrderFgReqDTO;
import com.ctrip.hotel.cost.domain.data.model.OrderAuditFgMqBO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * @author yangzhengzhang
 * @description
 * @date 2022-11-28 19:18
 */
@Mapper(componentModel = "spring")
public interface RequestBodyMapper {
    RequestBodyMapper INSTANCE = Mappers.getMapper(RequestBodyMapper.class);

    OrderAuditFgMqBO fgReqToBo(AuditOrderFgReqDTO req);
}
