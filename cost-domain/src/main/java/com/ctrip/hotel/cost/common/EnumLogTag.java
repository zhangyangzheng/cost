package com.ctrip.hotel.cost.common;

/**
 * @author yangzhengzhang
 * @description
 * @date 2022-12-09 17:52
 */
public enum EnumLogTag {
    ORDER_ID("orderId"),
    FG_ID("fgId"),
    REFERENCE_ID("referenceId"),
    BUSINESS_TYPE("businessType"),
    ;

    private final String value;

    EnumLogTag(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
