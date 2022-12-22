package com.ctrip.hotel.cost.domain.core;

import com.ctrip.hotel.cost.domain.eventbus.EventBus;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author yangzhengzhang
 * @description
 * @date 2022-10-27 15:19
 */
public interface MeasurementCenter extends ComputationCore, EventBus {

    String formula();

    List<Factor> factors();

    default Map<String, Object> computingParameter() {
        return this.factors().stream().collect(Collectors.toMap(Factor::getName, Factor::getValue, (key1, key2) -> key2));
    }

    default RoundingMode roundingMode() {
        return RoundingMode.HALF_UP;
    }

    default int scale() {
        return 4;
    }

    BigDecimal result();

}
