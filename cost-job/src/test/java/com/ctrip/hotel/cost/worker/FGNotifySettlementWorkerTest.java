package com.ctrip.hotel.cost.worker;

import com.ctrip.hotel.cost.CostJobUnitTestBase;
import org.junit.Test;
import org.mockito.InjectMocks;
import qunar.tc.schedule.MockParameter;
import qunar.tc.schedule.Parameter;

import static org.powermock.api.mockito.PowerMockito.when;

public class FGNotifySettlementWorkerTest extends CostJobUnitTestBase {

    @InjectMocks
    FGNotifySettlementWorker fgNotifySettlementWorker;

    @Test
    public void doMyWorkTest(){
        Parameter parameter = new MockParameter();
        fgNotifySettlementWorker.doMyWork(parameter);
    }
}
