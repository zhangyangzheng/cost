package com.ctrip.hotel.cost.domain.element.promotion;

import com.ctrip.hotel.cost.domain.core.Factor;
import com.ctrip.hotel.cost.domain.scene.CostItemType;
import hotel.settlement.common.DateHelper;
import hotel.settlement.common.QConfigHelper;
import hotel.settlement.common.enums.DateDiffType;
import lombok.Data;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;

/**
 * @author yangzhengzhang
 * @description
 * @date 2022-11-09 20:10
 */
@Data
public class PromotionCostPriceFgOrderInfo extends AbstractPromotionFg implements PromotionCostPrice {
    private BigDecimal costDiscountAmount;
    private Integer quantity;
    private Calendar effectDate;

    // 计算结果
    private BigDecimal result;

    @Override
    public Long promotionDailyInfoID() {
        return this.getPromotionDailyInfoID();
    }

    @Override
    public Factor cost() {
        return new Factor("costDiscountAmount", this.getCostDiscountAmount());
    }

    /**
     * hourAdjuest代表房间审核是否提前离店, 如果提前离店，最后一天的促销只能算一半（-12/24）
     * 最后一天的判断条件：realETD - effectDate = 1
     *
     * @return
     */
    @Override
    public Factor days() {
        AbstractPromotion.SettlementType promotionInfo = AbstractPromotion.SettlementType
                .builder()
                .costType(this.costType)
                .cashType(this.cashType)
                .cashPoolID(this.cashPoolID)
                .discountDtype(this.discountDtype)
                .fundId(this.fundId)
                .fundType(this.fundType)
                .settlementType(this.settlementType)
                .build();
        if (!Objects.equals(auditRoom, orderRoom) || PromotionEnum.C_TRIP.equals(createSettlementTypeForFG(promotionInfo))) {
            return new Factor("days", BigDecimal.ZERO);
        }

        if (DateHelper.dateDiff(DateDiffType.Day, this.getEffectDate(), this.getEta()) >= 0
                && DateHelper.dateDiff(DateDiffType.Day, this.getEffectDate(), this.getRealETD()) < 0) {
            BigDecimal days = BigDecimal.ONE;
            if (this.getHourAdjuest() != null && this.getHourAdjuest() != 0
                    && DateHelper.dateDiff(DateDiffType.Day, this.getRealETD(), this.getEffectDate()) == 1) {
                days = days.add(
                        BigDecimal.valueOf(this.getHourAdjuest()).divide(BigDecimal.valueOf(24), 4, RoundingMode.HALF_UP)
                );
            }

            return new Factor("days", days);
        }
        return new Factor("days", BigDecimal.ZERO);
    }

    @Override
    public Factor quantity() {
        return new Factor("quantity", BigDecimal.ONE);// 审核的计算里没有使用这个字段，默认1
    }

    @Override
    public String costItemName() {
        return CostItemType.PROMOTION_COST_PRICE_FG.getCostItemName();
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
        return QConfigHelper.getSwitchConfigByKey(CostItemType.PROMOTION_COST_PRICE_FG.getFormulaQConfigKey(), CostItemType.PROMOTION_COST_PRICE_FG.getFormula());
    }

    @Override
    public List<Factor> factors() {
        return Arrays.asList(
                cost(),
                days(),
                quantity()
        );
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
