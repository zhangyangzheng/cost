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
    private String opType;

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

    private BigDecimal quantity;// 间夜数
    private BigDecimal adjustAmount;//0
    private BigDecimal zeroCommissionAmount;//null
    private BigDecimal priceAmount;//0
    private BigDecimal costAmount;//0
    private BigDecimal bidPrice;//null
    private BigDecimal roomAmount;;//0
    private BigDecimal roomCost;;//0
    private BigDecimal tripPromotionAmount;//携程承担促销 0                   ruleGroup != 1  叠加amount
    private BigDecimal buyoutDiscountAmount;//(促销)买断折扣金额 > 0 ? ~ : null     ruleGroup == 1 && discountDtype == 8 叠加costDiscountAmount

    public List<PromotionDailyInfo> getPromotionDailyInfoList() {
        if (CollectionUtils.isEmpty(promotionDailyInfoList)) {
            return Collections.emptyList();
        }
        return promotionDailyInfoList;
    }
}
