package com.ctrip.hotel.cost.domain.element.commission;

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
 * @date 2022-11-07 15:34
 */
@Data
public class AdjustCommissionPriceOrderInfo implements AdjustCommissionPrice {
    private BigDecimal adjustCommission;
    private Integer adjustCommissionType;
    private String adjustRemark;
    private String operatMode;// 操作模式 S闪结

    //扩展
    private Supplier<Map<String, BigDecimal>> supplier = null;

    // 计算结果
    private BigDecimal result;

    @Override
    public Factor adjustCommission() {
        return new Factor("adjustCommission", this.getAdjustCommission());
    }

    @Override
    public Factor days() {
        if (operatMode.equals("S")) { // 闪结，如果有调整服务费，计算调整服务费差额
            return new Factor("days", BigDecimal.ONE);
        } else {
            return new Factor("days", BigDecimal.ZERO);
        }
    }

    @Override
    public Factor quantity() {
        return new Factor("quantity", BigDecimal.ONE);
    }

    @Override
    public String costItemName() {
        return CostItemType.ADJUST_COMMISSION_FG.getCostItemName();
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
    public String formula() {
        return QConfigHelper.getSwitchConfigByKey(CostItemType.ADJUST_COMMISSION_FG.getFormulaQConfigKey(), CostItemType.ADJUST_COMMISSION_FG.getFormula());
    }

    @Override
    public List<Factor> factors() {
        List<Factor> fs = new ArrayList<>();
        fs.add(adjustCommission());
        fs.add(days());
        fs.add(quantity());
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
