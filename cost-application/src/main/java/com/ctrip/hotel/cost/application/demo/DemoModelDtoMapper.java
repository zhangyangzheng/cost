package com.ctrip.hotel.cost.application.demo;

import com.ctrip.hotel.cost.domain.demo.DemoModel;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

/**
 * @author yangzhengzhang
 * @description
 * @date 2022-09-30 13:12
 */
@Mapper(componentModel = "spring")
public interface DemoModelDtoMapper {
    DemoModelDtoMapper INSTANCE = Mappers.getMapper(DemoModelDtoMapper.class);

    @Mapping(source = "domainId", target = "id")
    DemoDto testModelToTestDto(DemoModel demoModel);

    @Mapping(source = "id", target = "domainId")
    DemoModel testDtoToTestModel(DemoDto demoDto);
}
