package com.ctrip.hotel.cost.domain.data.model;

import lombok.Data;
import org.apache.commons.collections.CollectionUtils;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

/**
 * @author yangzhengzhang
 * @description
 * @date 2022-11-23 11:13
 */
@Data
public class AuditOrderInfoBO {
    private OrderAuditFgMqBO orderAuditFgMqBO;

    private Long orderId;
    private Long cusOrderId;
    private OrderBasicInfo orderBasicInfo;
    private FlashOrderInfo flashOrderInfo;
    private AllianceInfo allianceInfo;
    private AssociateMemberInfo associateMember;
    private GuaranteeInfo guaranteeInfo;
    private OutTimeDeductInfo outTimeDeductInfo;
    private TechFeeInfo techFeeInfo;
    private HotelBasicInfo hotelBasicInfo;
    private List<AuditRoomInfo> auditRoomInfoList;
    private List<PromotionDailyInfo> promotionDailyInfoList;
    private List<OrderPriceInfo> orderPriceInfoList;
    private List<BidOrderInfo> bidOrderInfoList;
    private OrderNoShowInfo orderNoShowInfo;

    private SettlementCallBackInfo settlementCallBackInfo;

    private String remarks;

    private BigDecimal quantity;// 间夜数--
    private BigDecimal adjustAmount;//0--
    private BigDecimal zeroCommissionAmount;//null--
    private BigDecimal priceAmount;//0--
    private BigDecimal costAmount;//0--
    private BigDecimal bidPrice;//null--
    private BigDecimal roomAmount;;//0--
    private BigDecimal roomCost;;//0--
    private BigDecimal tripPromotionAmount;//携程承担促销 0
    private BigDecimal tripPromotionCost;
    private BigDecimal hotelPromotionAmount;
    private BigDecimal hotelPromotionCost;
    private BigDecimal buyoutDiscountAmount;//(促销)买断折扣金额 > 0 ? ~ : null
    // to 审核
    private BigDecimal promotionCashBackAmount;
    private BigDecimal promotionCashBackCost;

    private BigDecimal getNullIfZero(BigDecimal decimal){
        if(decimal == null || decimal.compareTo(BigDecimal.ZERO) == 0){
            return null;
        }
        return decimal;
    }

    public BigDecimal getBidPrice() {
        return getNullIfZero(bidPrice);
    }

    public BigDecimal getZeroCommissionAmount() {
        return getNullIfZero(zeroCommissionAmount);
    }

    public BigDecimal getBuyoutDiscountAmount() {
        return getNullIfZero(buyoutDiscountAmount);
    }

    public List<PromotionDailyInfo> getPromotionDailyInfoList() {
        if (CollectionUtils.isEmpty(promotionDailyInfoList)) {
            return Collections.emptyList();
        }
        return promotionDailyInfoList;
    }
}
