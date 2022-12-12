package com.ctrip.hotel.cost.domain.element.promotion;

import lombok.Builder;
import lombok.Data;

/**
 * @author yangzhengzhang
 * @description
 * @date 2022-12-06 15:35
 */
public interface PromotionPrice {

    abstract class AbstractPromotion{
        @Data
        @Builder
        static class SettlementType {
            private Integer costType;// 老逻辑待下线
            private Integer cashType;// 老逻辑待下线
            private Integer cashPoolID;// 老逻辑待下线
            private Integer discountDtype;// 老逻辑待下线
            private Long fundId;
            private Integer fundType;
            private Integer settlementType;
        }
    }

    /**
     * 统一后可以封装 public final PromotionEnum createSettlementType () { }
     * @return 承担方
     */
    default PromotionEnum createSettlementTypeForFG(AbstractPromotion.SettlementType promotionInfo) {
        if (promotionInfo.fundId != null && promotionInfo.fundId > 0 && promotionInfo.fundType != -1) {
            if (promotionInfo.discountDtype != 5 && promotionInfo.costType > 0 && promotionInfo.settlementType == 1) {
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
        if (promotionInfo.discountDtype != 5
                && promotionInfo.costType > 0
                && promotionInfo.cashType != 2
                && promotionInfo.cashType != 5
                && promotionInfo.cashType != 6
                && !(promotionInfo.cashPoolID > 0 && promotionInfo.cashType != 1)) {
            return PromotionEnum.HOTEL;
        } else {
            return PromotionEnum.C_TRIP;
        }
    }

}
