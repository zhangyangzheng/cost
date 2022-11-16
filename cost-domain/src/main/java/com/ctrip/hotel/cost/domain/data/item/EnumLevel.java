package com.ctrip.hotel.cost.domain.data.item;

/**
 * @author yangzhengzhang
 * @description
 * @date 2022-11-14 19:28
 */
public enum EnumLevel {
    LEVEL_10(10),
    LEVEL_20(20),
    LEVEL_30(30),
    ;

    private Integer value;

    EnumLevel(Integer value) {
        this.value = value;
    }

    public Integer getValue() {
        return value;
    }
}
