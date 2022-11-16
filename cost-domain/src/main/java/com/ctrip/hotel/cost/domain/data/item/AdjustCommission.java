package com.ctrip.hotel.cost.domain.data.item;

import com.ctrip.hotel.cost.domain.element.commission.AdjustCommissionPrice;
import com.ctrip.hotel.cost.domain.scene.CostItemType;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * @author yangzhengzhang
 * @description
 * @date 2022-11-14 19:48
 */
@Data
public class AdjustCommission implements Item<AdjustCommissionPrice> {

    private AdjustCommissionPrice adjustCommissionPrice;
    private BigDecimal total;

    public AdjustCommission(AdjustCommissionPrice adjustCommissionPrice) {
        this.adjustCommissionPrice = adjustCommissionPrice;
    }

    @Override
    public CostItemType costItemType() {
        return CostItemType.ADJUST_COMMISSION_FG;
    }

    @Override
    public Integer level() {
        return EnumLevel.LEVEL_10.getValue();
    }

    @Override
    public BigDecimal total() {
        return this.total;
    }

    @Override
    public void countTotal() throws Exception {
        adjustCommissionPrice.listener();
        this.total = adjustCommissionPrice.result();
    }

    @Override
    public String formula() {
        return adjustCommissionPrice.formula();
    }

    @Override
    public List<ItemParametersAndResultModel> parametersAndResult() {
        return Collections.singletonList(
                new ItemParametersAndResultModel(adjustCommissionPrice.result(), adjustCommissionPrice.computingParameter())
        );
    }
}
