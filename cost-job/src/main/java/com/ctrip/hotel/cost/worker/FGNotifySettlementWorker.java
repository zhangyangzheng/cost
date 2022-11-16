package com.ctrip.hotel.cost.worker;

import com.ctrip.hotel.cost.infrastructure.job.FGNotifySettlementJob;
import hotel.settlement.common.LogHelper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import qunar.tc.qschedule.config.QSchedule;

import qunar.tc.schedule.Parameter;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class FGNotifySettlementWorker {

    static final String SLICE_INDEXES = "sliceIndexes";
    static final String COMMA = ",";


    @Autowired
    FGNotifySettlementJob fgNotifySettlementJob;

    @QSchedule("hotel.settlement.cost.fg.notifySettlement.job")
    public void doMyWork(Parameter parameter) {
        int shards = parameter.shards();
        String sliceIndexes = parameter.getProperty(SLICE_INDEXES, String.class);
        LogHelper.logInfo("FgNotifySettlementJob", String.format("shards: %d, execute sliceIndexes: %s", shards, sliceIndexes));
        if (StringUtils.isBlank(sliceIndexes)) {
            LogHelper.logError(this.getClass().getSimpleName(), "sliceIndexes is blank");
            return;
        }
        List<Integer> shardIdList = Arrays.stream(sliceIndexes.split(COMMA)).map(item -> Integer.valueOf(item)).collect(Collectors.toList());
        try {
            fgNotifySettlementJob.execute(shardIdList);
        } catch (Exception e) {
            LogHelper.logWarn("FgNotifySettlementJob", e);
        }
    }
}
