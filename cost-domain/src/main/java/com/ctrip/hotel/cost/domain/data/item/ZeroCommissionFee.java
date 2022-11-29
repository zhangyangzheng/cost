package com.ctrip.hotel.cost.domain.data.item;

import com.ctrip.hotel.cost.domain.element.techfee.ZeroCommissionFeePrice;
import com.ctrip.hotel.cost.domain.scene.CostItemType;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

/**
 * @author yangzhengzhang
 * @description
 * @date 2022-11-28 16:52
 */
@Data
public class ZeroCommissionFee implements Item<ZeroCommissionFeePrice> {

    private ZeroCommissionFeePrice zeroCommissionFeePrice;
    private BigDecimal total;

    public ZeroCommissionFee(ZeroCommissionFeePrice zeroCommissionFeePrice) {
        this.zeroCommissionFeePrice = zeroCommissionFeePrice;
    }

    @Override
    public CostItemType costItemType() {
        return CostItemType.ZERO_COMMISSION_FEE_PRICE_FG;
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
        zeroCommissionFeePrice.listener();
        this.total = zeroCommissionFeePrice.result();
    }

    @Override
    public String formula() {
        return zeroCommissionFeePrice.formula();
    }

    @Override
    public List<ItemParametersAndResultModel> parametersAndResult() {
        return Collections.singletonList(
                new ItemParametersAndResultModel(zeroCommissionFeePrice.result(), zeroCommissionFeePrice.computingParameter())
        );
    }
}
