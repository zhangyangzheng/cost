package com.ctrip.hotel.cost.domain.settlement;

/**
 * @author yangzhengzhang
 * @description
 * @date 2022-11-28 11:07
 */
public enum EnumOrderOpType {
    CREATE("C"),
    UPDATE("U"),
    CANCEL("D"),
    ;

    private String name;

    EnumOrderOpType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
