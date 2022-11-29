package com.ctrip.hotel.cost.domain.element.techfee;

import com.ctrip.hotel.cost.domain.core.Factor;
import com.ctrip.hotel.cost.domain.element.CommonPrice;

/**
 * @author yangzhengzhang
 * @description
 * @date 2022-11-28 16:40
 */
public interface ZeroCommissionFeePrice extends CommonPrice {
    Factor zeroCommissionFeeRatio();
}
