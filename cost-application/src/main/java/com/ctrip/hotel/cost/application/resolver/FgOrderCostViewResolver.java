package com.ctrip.hotel.cost.application.resolver;

import com.ctrip.hotel.cost.domain.root.CostSupporter;

import java.util.List;

/**
 * @author yangzhengzhang
 * @description
 * @date 2022-10-30 23:32
 */
public class FgOrderCostViewResolver extends AbstractViewResolver<List<Long>, CostSupporter>{

    @Override
    public List<Long> resolveView() {
        // model provide all data
        return model.getCostSuccessData();
    }
}
