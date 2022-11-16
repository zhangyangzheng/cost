package com.ctrip.hotel.cost.domain.element.promotion;

import com.ctrip.hotel.cost.domain.core.Factor;
import com.ctrip.hotel.cost.domain.element.CommonPrice;

/**
 * @author yangzhengzhang
 * @description
 * @date 2022-11-10 17:08
 */
public interface PromotionCostPrice extends CommonPrice {
    /**
     * 统一后可以封装 public final int createSettlementType () { }
     * @return 承担方
     */
    PromotionEnum createSettlementType();

    Factor cost();
}
