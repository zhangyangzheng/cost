package com.ctrip.hotel.cost.domain.data.item;

import com.ctrip.hotel.cost.domain.element.price.PriceCost;
import com.ctrip.hotel.cost.domain.scene.CostItemType;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

/**
 * @author yangzhengzhang
 * @description
 * @date 2022-11-14 19:51
 */
public class PriceCostFg implements Item<PriceCost> {

    private PriceCost priceCost;
    private BigDecimal total;

    public PriceCostFg(PriceCost priceCost) {
        this.priceCost = priceCost;
    }

    @Override
    public CostItemType costItemType() {
        return CostItemType.COST_FG;
    }

    @Override
    public Integer level() {
        return EnumLevel.LEVEL_20.getValue();
    }

    @Override
    public BigDecimal total() {
        return this.total;
    }

    @Override
    public void countTotal() throws Exception {
        priceCost.listener();
        this.total = priceCost.result();
    }

    @Override
    public String formula() {
        return priceCost.formula();
    }

    @Override
    public List<ItemParametersAndResultModel> parametersAndResult() {
        return Collections.singletonList(
                new ItemParametersAndResultModel(priceCost.result(), priceCost.computingParameter())
        );
    }
}
