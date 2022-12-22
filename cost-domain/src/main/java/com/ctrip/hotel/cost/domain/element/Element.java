package com.ctrip.hotel.cost.domain.element;

import com.ctrip.hotel.cost.domain.core.Factor;

import java.math.BigDecimal;

/**
 * @author yangzhengzhang
 * @description
 * @date 2022-10-31 19:13
 */
public interface Element {

    String costItemName();

    String currency();

    BigDecimal exchangeRate();

    // Factor price(); // "child class implements", 这样会更好。为了公式可配置，取消这种写法，这样计费域也就不能完全隔离外部系统特性

    Factor days();

    Factor quantity();

}
