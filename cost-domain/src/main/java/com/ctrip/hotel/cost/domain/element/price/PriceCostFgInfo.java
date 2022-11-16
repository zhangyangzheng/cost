package com.ctrip.hotel.cost.domain.element.price;

import com.ctrip.hotel.cost.domain.core.Factor;
import com.ctrip.hotel.cost.domain.scene.CostItemType;
import hotel.settlement.common.QConfigHelper;
import lombok.Data;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

/**
 * @author yangzhengzhang
 * @description
 * @date 2022-11-15 21:44
 */
@Data
public class PriceCostFgInfo implements PriceCost{

    //扩展
    private Supplier<Map<String, BigDecimal>> supplier = null;

    // 计算结果
    private BigDecimal result;

    @Override
    public String costItemName() {
        return null;
    }

    @Override
    public String currency() {
        return null;
    }

    @Override
    public BigDecimal exchangeRate() {
        return null;
    }

    @Override
    public Factor days() {
        return null;
    }

    @Override
    public Factor quantity() {
        return null;
    }

    @Override
    public String formula() {
        return QConfigHelper.getSwitchConfigByKey(CostItemType.COST_FG.getFormulaQConfigKey(), CostItemType.COST_FG.getFormula());
    }

    @Override
    public List<Factor> factors() {
        List<Factor> fs = new ArrayList<>();
        if (this.supplier != null && this.supplier.get() != null) {
            for (Map.Entry<String, BigDecimal> entry : this.supplier.get().entrySet()) {
                fs.add(new Factor(entry.getKey(), entry.getValue()));
            }
        }
        return fs;
    }

    @Override
    public BigDecimal result() {
        return this.getResult();
    }

    @Override
    public void listener() throws Exception {
        this.setResult(computing(this));
    }
}
