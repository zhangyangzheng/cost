package com.ctrip.hotel.cost.application.demo;

import com.ctrip.hotel.cost.domain.demo.DemoModel;
import javax.annotation.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2022-11-01T14:31:32+0800",
    comments = "version: 1.5.2.Final, compiler: javac, environment: Java 1.8.0_191 (Oracle Corporation)"
)
@Component
public class DemoModelDtoMapperImpl implements DemoModelDtoMapper {

    @Override
    public DemoDto testModelToTestDto(DemoModel demoModel) {
        if ( demoModel == null ) {
            return null;
        }

        DemoDto demoDto = new DemoDto();

        demoDto.setId( demoModel.getDomainId() );
        demoDto.setName( demoModel.getName() );

        return demoDto;
    }

    @Override
    public DemoModel testDtoToTestModel(DemoDto demoDto) {
        if ( demoDto == null ) {
            return null;
        }

        DemoModel.DemoModelBuilder demoModel = DemoModel.builder();

        demoModel.domainId( demoDto.getId() );
        demoModel.name( demoDto.getName() );

        return demoModel.build();
    }
}
