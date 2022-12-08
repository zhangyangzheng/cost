package com.ctrip.hotel.cost.application.resolver;

import com.alibaba.fastjson.JSON;
import com.ctrip.framework.clogging.domain.thrift.LogLevel;
import com.ctrip.hotel.cost.common.ThreadLocalCostHolder;
import com.ctrip.hotel.cost.domain.data.model.AuditOrderInfoBO;
import com.ctrip.hotel.cost.domain.root.CostSupporter;

import java.util.List;

/**
 * @author yangzhengzhang
 * @description
 * @date 2022-10-30 23:32
 */
public class FgOrderCostViewResolver extends AbstractViewResolver<List<AuditOrderInfoBO>, CostSupporter>{

    @Override
    public List<AuditOrderInfoBO> resolveView() {
        // model provide all data
        List<AuditOrderInfoBO> bos = model.getFgAuditCostSuccessResult();
        ThreadLocalCostHolder.allLinkTracingLog("AuditOrderInfoBO=" + JSON.toJSONString(bos), LogLevel.INFO);
        return bos;
    }

    /**
     * todo 未来和结算解耦开后，下掉该接口
     * @return
     */
    @Override
    public List<AuditOrderInfoBO> resolveViewExtend() {
        return model.getFgAuditResult();
    }
}
