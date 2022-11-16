package com.ctrip.hotel.cost.domain.core;

import hotel.settlement.common.interfaces.IExpComputer;

import java.math.BigDecimal;

/**
 * @author yangzhengzhang
 * @description
 * @date 2022-10-27 17:35
 */
public interface ComputationCore {

    default BigDecimal computing(MeasurementCenter measurementCenter) throws Exception {
        // todo: default BigDecimal.ROUND_HALF_UP, 其他模式需要扩展
        return IExpComputer.Calculator.express(measurementCenter.formula(), measurementCenter.computingParameter()).setScale(measurementCenter.scale()).bigDecimal();
    }

}
