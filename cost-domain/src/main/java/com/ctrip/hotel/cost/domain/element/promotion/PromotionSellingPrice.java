package com.ctrip.hotel.cost.domain.element.promotion;

import com.ctrip.hotel.cost.domain.core.Factor;
import com.ctrip.hotel.cost.domain.element.CommonPrice;

/**
 * @author yangzhengzhang
 * @description
 * @date 2022-11-09 11:03
 */
public interface PromotionSellingPrice extends PromotionPrice, CommonPrice {

    Long promotionDailyInfoID();

    Factor price();

}
