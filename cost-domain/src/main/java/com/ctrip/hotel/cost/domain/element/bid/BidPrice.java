package com.ctrip.hotel.cost.domain.element.bid;

import com.ctrip.hotel.cost.domain.core.Factor;
import com.ctrip.hotel.cost.domain.element.CommonPrice;

/**
 * @author yangzhengzhang
 * @description
 * @date 2022-10-31 17:46
 */
public interface BidPrice extends CommonPrice {
    Factor orgCost();
    Factor bidCostEffort();
}
