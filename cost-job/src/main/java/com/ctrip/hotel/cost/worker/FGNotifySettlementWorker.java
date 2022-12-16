package com.ctrip.hotel.cost.worker;

import com.ctrip.hotel.cost.job.BaseNotifySettlementJob;
import com.dianping.cat.annotation.CatTrace;
import hotel.settlement.common.LogHelper;
import hotel.settlement.common.entity.CatBizTypeConstant;
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
    BaseNotifySettlementJob fgNotifySettlementJob;

    @QSchedule("hotel.settlement.cost.job.fg.notifySettlement")
    @CatTrace(type = CatBizTypeConstant.BIZ_QSCHEDULE_EXECUTE + ".Cost", name = "hotel.settlement.cost.job.fg.notifySettlement")
    public void doMyWork(Parameter parameter) {
        int shards = parameter.shards();
        String sliceIndexes = parameter.getProperty(SLICE_INDEXES, String.class);
        LogHelper.logInfo(
                "FgNotifySettlementJob",
                String.format("shards: %d, execute sliceIndexes: %s", shards, sliceIndexes));
        if (StringUtils.isBlank(sliceIndexes)) {
            LogHelper.logError(this.getClass().getSimpleName(), "sliceIndexes is blank");
            return;
        }
        List<Integer> shardIdList =
                Arrays.stream(sliceIndexes.split(COMMA))
                        .map(item -> Integer.valueOf(item))
                        .collect(Collectors.toList());
        try {
            fgNotifySettlementJob.execute(shardIdList);
        } catch (Exception e) {
            LogHelper.logWarn("FgNotifySettlementJob", e);
        }
    }
}
