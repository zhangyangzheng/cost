package com.ctrip.hotel.cost.domain.settlement;

import com.ctrip.hotel.cost.domain.data.model.AuditOrderInfoBO;
import org.springframework.stereotype.Component;

/**
 * @author yangzhengzhang
 * @description 订单元解释器-->结算元
 * @date 2022-11-24 11:12
 */
@Component
public class OrderMetaInterpreter {

    public AuditOrderInfoBO resolverOrderFg(AuditOrderInfoBO auditOrderInfoBO) {
        /**
         * 现付审核离店：过滤掉日期不在实际住店日期的数据
         */
        /**
         * 现付审核离店：结算只需要酒店承担促销信息
         */
        return auditOrderInfoBO;
    }

}
