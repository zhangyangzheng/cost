package com.ctrip.hotel.cost.domain.data.model;

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
}
