package com.ctrip.hotel.cost.domain.scene;

import com.ctrip.hotel.cost.common.util.I18NMessageUtil;

import java.util.Arrays;
import java.util.List;

/**
 * @author yangzhengzhang
 * @description
 * @date 2022-11-01 0:12
 */
public enum EnumScene {
    AUDIT_ORDER_FG(0,
            Arrays.asList(CostItemType.BID_PRICE_FG,
                    CostItemType.PROMOTION_SELLING_PRICE_FG,
                    CostItemType.PROMOTION_COST_PRICE_FG,
                    CostItemType.TRIP_PROMOTION_SELLING_PRICE_FG,
                    CostItemType.TRIP_PROMOTION_COST_PRICE_FG,
                    CostItemType.BUYOUT_DISCOUNT_PROMOTION_COST_PRICE_FG,
                    CostItemType.ROOM_SELLING_PRICE_FG,
                    CostItemType.ROOM_COST_PRICE_FG,
                    CostItemType.AMOUNT_FG,
                    CostItemType.COST_FG,
                    CostItemType.ADJUST_COMMISSION_FG,
                    CostItemType.ZERO_COMMISSION_FEE_PRICE_FG,
                    CostItemType.CASH_BACK_PROMOTION_SELLING_PRICE_FG,
                    CostItemType.CASH_BACK_PROMOTION_COST_PRICE_FG),
            "audit normal mode"
    ),

    ;

    private int code;
    private List<CostItemType> costItemTypes;
    private String name;

    EnumScene(int code, List<CostItemType> costItemTypes, String name) {
        this.code = code;
        this.costItemTypes = costItemTypes;
        this.name = name;
    }

    public int getCode() {
        return code;
    }

    public List<CostItemType> getCostItemTypes() {
        return costItemTypes;
    }

    public String getName() {
        return name;
    }

    public static EnumScene getScene(int value) {
        for (EnumScene innerScene : EnumScene.values()) {
            if (innerScene.getCode() == value) {
                return innerScene;
            }
        }
        throw new EnumNotFoundException(EnumScene.class, value);
    }
}
