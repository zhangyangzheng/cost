package com.ctrip.hotel.cost.domain.data.item;

import com.ctrip.hotel.cost.domain.element.price.PriceAmount;
import com.ctrip.hotel.cost.domain.scene.CostItemType;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

/**
 * @author yangzhengzhang
 * @description
 * @date 2022-11-14 19:50
 */
public class PriceAmountFg implements Item<PriceAmount> {

    private PriceAmount priceAmount;
    private BigDecimal total;

    public PriceAmountFg(PriceAmount priceAmount) {
        this.priceAmount = priceAmount;
    }

    @Override
    public CostItemType costItemType() {
        return CostItemType.AMOUNT_FG;
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
        priceAmount.listener();
        this.total = priceAmount.result();
    }

    @Override
    public String formula() {
        return priceAmount.formula();
    }

    @Override
    public List<ItemParametersAndResultModel> parametersAndResult() {
        return Collections.singletonList(
                new ItemParametersAndResultModel(priceAmount.result(), priceAmount.computingParameter())
        );
    }
}
