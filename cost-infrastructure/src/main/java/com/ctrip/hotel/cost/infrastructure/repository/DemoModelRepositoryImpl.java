package com.ctrip.hotel.cost.infrastructure.repository;

import com.ctrip.hotel.cost.domain.demo.DemoModel;
import com.ctrip.hotel.cost.domain.demo.DemoModelRepository;
import com.ctrip.hotel.cost.infrastructure.mapper.DemoModelPoMapper;
import com.ctrip.hotel.cost.infrastructure.model.DemoPo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

/**
 * @author yangzhengzhang
 * @description
 * @date 2022-09-30 15:10
 */
@Repository
public class DemoModelRepositoryImpl implements DemoModelRepository {

    @Autowired private DemoModelPoMapper testModelPoMapper;

    public DemoModel getTestModel() {
        DemoPo demoPo = DemoPo.builder()
                .domainId(111)
                .name("test")
                .build();
        return testModelPoMapper.testPoToTestModel(demoPo);
    }

}
