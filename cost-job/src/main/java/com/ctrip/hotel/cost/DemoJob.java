package com.ctrip.hotel.cost.job;

import org.springframework.stereotype.Component;
import qunar.tc.qschedule.config.QSchedule;
import qunar.tc.qschedule.config.QScheduleByClass;

/**
 * @author yangzhengzhang
 * @description
 * @date 2022-10-27 21:14
 */
@Component
public class DemoJob {

    @QScheduleByClass
    public void demo() {

    }

}
