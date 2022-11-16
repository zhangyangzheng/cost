package com.ctrip.hotel.cost.domain.element.room;

import com.ctrip.hotel.cost.domain.core.Factor;
import com.ctrip.hotel.cost.domain.element.CommonPrice;

/**
 * @author yangzhengzhang
 * @description
 * @date 2022-11-07 19:52
 */
public interface RoomSellingPrice extends CommonPrice {
    Factor price();
}
