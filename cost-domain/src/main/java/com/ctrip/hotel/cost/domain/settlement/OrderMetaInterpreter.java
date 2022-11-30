package com.ctrip.hotel.cost.domain.settlement;

import com.ctrip.hotel.cost.domain.data.model.AuditOrderInfoBO;
import com.ctrip.hotel.cost.domain.data.model.PromotionDailyInfo;
import hotel.settlement.common.helpers.DefaultValueHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import repository.SettlementRepository;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author yangzhengzhang
 * @description 订单元解释器-->结算元
 * @date 2022-11-24 11:12
 */
@Component
public class OrderMetaInterpreter {

    @Autowired
    private SettlementRepository settlementRepository;

    public Boolean resolver(AuditOrderInfoBO auditOrderInfoBO) throws Exception {
        /**
         * 现付审核离店：结算只需要酒店承担促销信息
         */
        List<PromotionDailyInfo> hotelPromotion = auditOrderInfoBO.getPromotionDailyInfoList().stream().filter(e -> DefaultValueHelper.getValue(e.getRuleGroup()) == 1).collect(Collectors.toList());
        auditOrderInfoBO.setPromotionDailyInfoList(hotelPromotion);
        return settlementRepository.callSettlementPayDataReceive(auditOrderInfoBO);
    }

}
