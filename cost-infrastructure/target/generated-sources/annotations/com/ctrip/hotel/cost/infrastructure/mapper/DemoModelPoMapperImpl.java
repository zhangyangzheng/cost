package com.ctrip.hotel.cost.infrastructure.mapper;

import com.ctrip.hotel.cost.domain.demo.DemoModel;
import com.ctrip.hotel.cost.infrastructure.model.DemoPo;
import javax.annotation.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2022-11-01T14:31:42+0800",
    comments = "version: 1.5.2.Final, compiler: javac, environment: Java 1.8.0_191 (Oracle Corporation)"
)
@Component
public class DemoModelPoMapperImpl implements DemoModelPoMapper {

    @Override
    public DemoPo testModelToTestPo(DemoModel demoModel) {
        if ( demoModel == null ) {
            return null;
        }

        DemoPo.DemoPoBuilder demoPo = DemoPo.builder();

        demoPo.domainId( demoModel.getDomainId() );
        demoPo.name( demoModel.getName() );

        return demoPo.build();
    }

    @Override
    public DemoModel testPoToTestModel(DemoPo demoPo) {
        if ( demoPo == null ) {
            return null;
        }

        DemoModel.DemoModelBuilder demoModel = DemoModel.builder();

        demoModel.domainId( demoPo.getDomainId() );
        demoModel.name( demoPo.getName() );

        return demoModel.build();
    }
}
