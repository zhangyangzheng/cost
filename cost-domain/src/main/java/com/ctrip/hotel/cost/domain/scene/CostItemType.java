package com.ctrip.hotel.cost.domain.scene;

/**
 * @author yangzhengzhang
 * @description
 * @date 2022-10-28 19:09
 */
public enum CostItemType {
    BID_PRICE("bidPrice", "bidCostEffort*orgCost*days*quantity", "formula.bidPrice"),
    COST("cost", "", ""),
    ;
    private String costItem;
    private String formula;
    private String formulaQConfigKey;

    CostItemType(String costItem, String formula, String formulaQConfigKey) {
        this.costItem = costItem;
        this.formula = formula;
        this.formulaQConfigKey = formulaQConfigKey;
    }

    public String getCostItem() {
        return costItem;
    }

    public String getFormula() {
        return formula;
    }

    public String getFormulaQConfigKey() {
        return formulaQConfigKey;
    }
}
