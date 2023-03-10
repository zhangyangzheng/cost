package com.ctrip.hotel.cost.domain.scene;

/**
 * @author yangzhengzhang
 * @description
 * @date 2022-10-28 19:09
 */
public enum CostItemType {
    // 以下，现付的契约和订单的契约，很多字段命名不一样。现付单独写一份
    BID_PRICE_FG("bidPrice", "bidCostEffort*orgCost*days*quantity", "formula.bidPriceFg"),

    PROMOTION_SELLING_PRICE_FG("promotionSellingPrice", "amount*days*quantity", "formula.promotionSellingPriceFg"),
    PROMOTION_COST_PRICE_FG("promotionCostPrice", "costDiscountAmount*days*quantity", "formula.promotionCostPriceFg"),
    TRIP_PROMOTION_SELLING_PRICE_FG("tripPromotionSellingPrice", "amount*days*quantity", "formula.tripPromotionSellingPriceFg"),// 携程承担？
    TRIP_PROMOTION_COST_PRICE_FG("tripPromotionCostPrice", "costDiscountAmount*days*quantity", "formula.tripPromotionCostPriceFg"),
    BUYOUT_DISCOUNT_PROMOTION_COST_PRICE_FG("buyoutDiscountPromotionCostPrice", "costDiscountAmount*days*quantity", "formula.buyoutDiscountPromotionCostPrice"),// 非携程，买断折扣？
    CASH_BACK_PROMOTION_SELLING_PRICE_FG("cashBackPromotionSellingPrice", "amount*days*quantity", "formula.cashBackPromotionSellingPrice"),
    CASH_BACK_PROMOTION_COST_PRICE_FG("cashBackPromotionCostPrice", "costDiscountAmount*days*quantity", "formula.cashBackPromotionCostPrice"),

    ROOM_SELLING_PRICE_FG("roomSellingPrice", "amount*days*quantity", "formula.roomSellingPriceFg"),
    ROOM_COST_PRICE_FG("roomCostPrice", "cost*days*quantity", "formula.roomCostPriceFg"),

    AMOUNT_FG("amount", "roomSellingPrice-promotionSellingPrice", "formula.amountFg"),
    COST_FG("cost", "roomCostPrice-promotionCostPrice", "formula.costFg"),

    ADJUST_COMMISSION_FG("adjustAmount", "adjustCommission-amount+cost", "formula.adjustCommissionFg"),
    ZERO_COMMISSION_FEE_PRICE_FG("zeroCommissionFee", "cost*zeroCommissionFeeRatio", "formula.zeroCommissionFee"),

    // 以下统一
//    BID_PRICE("bidPrice", "", "formula.bidPrice"),
//    PROMOTION_SELLING_PRICE("promotionSellingPrice", "", "formula.promotionSellingPrice"),
//    PROMOTION_COST_PRICE("promotionCostPrice", "", "formula.promotionCostPrice"),
//    ROOM_SELLING_PRICE("roomSellingPrice", "", "formula.roomSellingPrice"),
//    ROOM_COST_PRICE("roomCostPrice", "", "formula.roomCostPrice"),
    ;
    private final String costItemName;
    private final String formula;
    private final String formulaQConfigKey;

    CostItemType(String costItemName, String formula, String formulaQConfigKey) {
        this.costItemName = costItemName;
        this.formula = formula;
        this.formulaQConfigKey = formulaQConfigKey;
    }

    public String getCostItemName() {
        return costItemName;
    }

    public String getFormula() {
        return formula;
    }

    public String getFormulaQConfigKey() {
        return formulaQConfigKey;
    }
}
