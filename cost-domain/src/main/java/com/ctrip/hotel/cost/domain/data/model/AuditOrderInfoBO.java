package com.ctrip.hotel.cost.domain.data.model;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author yangzhengzhang
 * @description
 * @date 2022-11-23 11:13
 */
@Data
public class AuditOrderInfoBO {
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

    private BigDecimal quantity;// 间夜数
}
