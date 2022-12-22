package com.ctrip.hotel.cost.worker;

import com.ctrip.hotel.cost.CostJobUnitTestBase;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.InjectMocks;
import qunar.tc.schedule.MockParameter;
import qunar.tc.schedule.Parameter;

@Ignore
public class FGNotifySettlementWorkerTest extends CostJobUnitTestBase {

  @InjectMocks FGNotifySettlementWorker fgNotifySettlementWorker;

  @Test
  public void doMyWorkTest() {
    try {
      Parameter parameter = new MockParameter();
      fgNotifySettlementWorker.doMyWork(parameter);
    } catch (Exception e) {

    }
  }
}
