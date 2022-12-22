package com.ctrip.hotel.cost.domain.element.bid;

import com.ctrip.hotel.cost.domain.core.Factor;
import com.ctrip.hotel.cost.domain.element.AbstractElementFg;
import com.ctrip.hotel.cost.domain.scene.CostItemType;
import hotel.settlement.common.QConfigHelper;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

/**
 * @author yangzhengzhang
 * @description
 * @date 2022-11-01 19:22
 */
@Data
public class BidPriceFgOrderInfo extends AbstractElementFg implements BidPrice {
    private Long bidOrderID;
    private Calendar beginDate;
    private Calendar endDate;
    private BigDecimal orgCost;
    private BigDecimal bidCostEffort;
    private String tradNo;
    private String platform;
    private String ztcNo;
    private BigDecimal rate;
    private BigDecimal biddenCost;
    private Integer bidPayType;

    // 订单属性
    private Calendar eta;
    private Calendar realETD;

    // 计算结果
    private BigDecimal result;

    @Override
    public Factor orgCost() {
        return new Factor("orgCost", this.getOrgCost());
    }

    @Override
    public Factor bidCostEffort() {
        return new Factor("bidCostEffort", this.getBidCostEffort());
    }

    /**
     * 订单时间内的所有云梯费
     * @return
     */
    @Override
    public Factor days() {
        if (bidPayType != null && bidPayType == 1) {
            return new Factor("days", BigDecimal.ZERO);
        }
        return new Factor("days", new BigDecimal(countDays()));
    }

    @Override
    public Factor quantity() {
        return new Factor("quantity", BigDecimal.ONE);// 审核离店是房间维度，所以数量是1间
    }

    @Override
    public Calendar orderBeginDate() {
        return this.getEta();
    }

    @Override
    public Calendar orderEndDate() {
        return this.getRealETD();
    }

    @Override
    public Calendar elementBeginDate() {
        return this.getBeginDate();
    }

    @Override
    public Calendar elementEndDate() {
        return this.getEndDate();
    }

    @Override
    public String costItemName() {
        return CostItemType.BID_PRICE_FG.getCostItemName();
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
        return QConfigHelper.getSwitchConfigByKey(CostItemType.BID_PRICE_FG.getFormulaQConfigKey(), CostItemType.BID_PRICE_FG.getFormula());
    }

    @Override
    public List<Factor> factors() {
        return Arrays.asList(
                orgCost(),
                bidCostEffort(),
                days(),
                quantity()
        );
    }

    @Override
    public BigDecimal result() {
        return this.getResult();
    }

    @Override
    public void listener() throws Exception{
        this.setResult(computing(this).multiply(new BigDecimal("-1")));
    }
}
