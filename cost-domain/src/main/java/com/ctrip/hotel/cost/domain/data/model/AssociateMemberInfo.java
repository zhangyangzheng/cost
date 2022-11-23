package com.ctrip.hotel.cost.domain.data.model;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @author yangzhengzhang
 * @description 联合会员
 * @date 2022-11-23 11:28
 */
@Data
public class AssociateMemberInfo {
    private String associateMemberOrder;
    private Integer associateMemberHotelID;
    private BigDecimal associateMemberCommissionRatio;
    private String associateMemberCommissionType;
}
