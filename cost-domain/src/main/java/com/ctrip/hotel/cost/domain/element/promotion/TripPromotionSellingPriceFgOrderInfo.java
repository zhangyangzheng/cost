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

/**
 * @author yangzhengzhang
 * @description
 * @date 2022-10-31 19:19
 */
@Data
public class TripPromotionSellingPriceFgOrderInfo implements PromotionSellingPrice {

    private BigDecimal amount;
    private Integer quantity;
    private Calendar effectDate;

    // 承担方
    private Integer costType;// 老逻辑待下线
    private Integer cashType;// 老逻辑待下线
    private Integer cashPoolID;// 老逻辑待下线
    private Integer discountDtype;// 老逻辑待下线
    private Long fundId;
    private Integer fundType;
    private Integer settlementType;

    // 订单属性
    private Calendar eta;
    private Calendar realETD;
    private Integer hourAdjuest;

    // 计算结果
    private BigDecimal result;

//    @Override
//    public PromotionEnum createSettlementTypeForFG() {
//        if (fundId != null && fundId > 0 && fundType != -1) {
//            if (discountDtype != 5 && costType > 0 && settlementType == 1) {
//                return PromotionEnum.HOTEL;
//            } else {
//                return PromotionEnum.C_TRIP;
//            }
//        }
//        // todo 以下是老逻辑，待下线
//        // 酒店承担促销金额
//        // 权益类促销（DiscountDtype = 7） 计入总金额 不计入每日促销金额
//        // 酒店承担（CostType > 0）
//        // 虚拟资金池(CashPoolID > 0 and CashType = 1 )
//        // 非  优惠类型为返现，发单、开票、抛结算不剔除返现金额（优惠金额）（DiscountDtype = 5）
//        // 非  真实资金池(CashPoolID > 0 and CashType != 1 )
//        // 非  集团促销（CashType = 2 )
//        // 非  商家券（CashType = 5 )
//        // 非  星球号优惠券（CashType = 6 )
//        if (discountDtype != 5
//                && costType > 0
//                && cashType != 2
//                && cashType != 5
//                && cashType != 6
//                && !(cashPoolID > 0 && cashType != 1)) {
//            return PromotionEnum.HOTEL;
//        } else {
//            return PromotionEnum.C_TRIP;
//        }
//    }

    @Override
    public Factor price() {
        return new Factor("amount", this.getAmount());
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
        if (PromotionEnum.HOTEL.equals(createSettlementTypeForFG(promotionInfo))) {
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
        return CostItemType.PROMOTION_SELLING_PRICE_FG.getCostItemName();
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
        return QConfigHelper.getSwitchConfigByKey(CostItemType.PROMOTION_SELLING_PRICE_FG.getFormulaQConfigKey(), CostItemType.PROMOTION_SELLING_PRICE_FG.getFormula());
    }

    @Override
    public List<Factor> factors() {
        return Arrays.asList(
                price(),
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
