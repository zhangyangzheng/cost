package com.ctrip.hotel.cost;


import com.ctrip.hotel.cost.util.I18NMessageUtil;
import com.ctrip.hotel.cost.util.SpringUtil;
import hotel.settlement.common.LogHelper;
import hotel.settlement.common.QConfigHelper;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.mockito.ArgumentMatchers.anyString;
import static org.powermock.api.mockito.PowerMockito.when;

@PowerMockIgnore({
  "org.xml.*",
  "javax.xml.*",
  "org.w3c.*",
  "javax.net.ssl.*",
  "com.sun.*",
  "javax.crypto.*",
  "javax.management.*"
})
@RunWith(PowerMockRunner.class)
@PrepareForTest(value = {LogHelper.class, I18NMessageUtil.class, SpringUtil.class, QConfigHelper.class,
        LoggerFactory.class})
public class CostJobUnitTestBase {

  @Before
  public void prepare() throws Exception {
    PowerMockito.mockStatic(I18NMessageUtil.class);
    PowerMockito.mockStatic(LogHelper.class);
    PowerMockito.mockStatic(QConfigHelper.class);
    PowerMockito.mockStatic(SpringUtil.class);

    Logger logger = PowerMockito.mock(Logger.class);
    PowerMockito.mockStatic(LoggerFactory.class);
    PowerMockito.when(LoggerFactory.getLogger(anyString())).thenReturn(logger);
  }
}
