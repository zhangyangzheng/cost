package com.ctrip.hotel.cost.domain.data.model;

import com.ctrip.hotel.cost.domain.element.promotion.PromotionEnum;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Calendar;

/**
 * @author yangzhengzhang
 * @description 促销数据
 * @date 2022-11-23 13:59
 */
@Data
public class PromotionDailyInfo {
    private Long promotionDailyInfoID;
    private Calendar effectDate;
    private Integer quantity;
    private BigDecimal cNYAmount;
    private BigDecimal amount;
    private BigDecimal costDiscountAmount;
    private BigDecimal costDiscountCNYAmount;
    private Long typeID;
    private Integer discountDtype;
    private Integer ruleGroup;
    private Integer prepayCampaignID;
    private String prepayCampaignName;
    private Long prepayCampaignVersionID;
    private Integer costType;
    private Integer cashType;
    private Integer cashPoolID;
    private String cutCostOnly;
    private String isNeedGetCashBack;
    private Long fundId;
    private Integer fundType;
    private Integer settlementType;

    public PromotionEnum getSettlementType() {
        if (fundId != null && fundId > 0 && fundType != -1) {
            if (discountDtype != 5 && costType > 0 && settlementType == 1) {
                return PromotionEnum.HOTEL;
            } else {
                return PromotionEnum.C_TRIP;
            }
        }
        // todo 以下是老逻辑，待下线
        // 酒店承担促销金额
        // 权益类促销（DiscountDtype = 7） 计入总金额 不计入每日促销金额
        // 酒店承担（CostType > 0）
        // 虚拟资金池(CashPoolID > 0 and CashType = 1 )
        // 非  优惠类型为返现，发单、开票、抛结算不剔除返现金额（优惠金额）（DiscountDtype = 5）
        // 非  真实资金池(CashPoolID > 0 and CashType != 1 )
        // 非  集团促销（CashType = 2 )
        // 非  商家券（CashType = 5 )
        // 非  星球号优惠券（CashType = 6 )
        if (discountDtype != 5
                && costType > 0
                && cashType != 2
                && cashType != 5
                && cashType != 6
                && !(cashPoolID > 0 && cashType != 1)) {
            return PromotionEnum.HOTEL;
        } else {
            return PromotionEnum.C_TRIP;
        }
    }
}
