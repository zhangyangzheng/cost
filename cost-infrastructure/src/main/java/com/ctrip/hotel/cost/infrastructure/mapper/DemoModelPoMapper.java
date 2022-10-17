package com.ctrip.hotel.cost.infrastructure.mapper;

import com.ctrip.hotel.cost.domain.demo.DemoModel;
import com.ctrip.hotel.cost.infrastructure.model.DemoPo;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * @author yangzhengzhang
 * @description
 * @date 2022-09-30 13:12
 */
@Mapper(componentModel = "spring")
public interface DemoModelPoMapper {
    DemoModelPoMapper INSTANCE = Mappers.getMapper(DemoModelPoMapper.class);

    DemoPo testModelToTestPo(DemoModel demoModel);

    DemoModel testPoToTestModel(DemoPo demoPo);
}
