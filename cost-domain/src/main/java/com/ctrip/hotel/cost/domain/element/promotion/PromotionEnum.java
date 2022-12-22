package com.ctrip.hotel.cost.domain.element.promotion;

/**
 * @author yangzhengzhang
 * @description
 * @date 2022-11-09 19:23
 */
public enum PromotionEnum {
    /** 商家承担 */
    HOTEL(1),
    /** 携程承担 */
    C_TRIP(2);

    private final int value;

    PromotionEnum(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
