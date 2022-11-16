package com.ctrip.hotel.cost.domain.element.commission;

import com.ctrip.hotel.cost.domain.core.Factor;
import com.ctrip.hotel.cost.domain.element.CommonPrice;

/**
 * @author yangzhengzhang
 * @description
 * @date 2022-11-08 16:49
 */
public interface AdjustCommissionPrice extends CommonPrice {
    Factor adjustCommission();
}
