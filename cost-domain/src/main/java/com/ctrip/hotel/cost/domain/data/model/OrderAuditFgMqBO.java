package com.ctrip.hotel.cost.domain.data.model;

import lombok.Data;

/**
 * @author yangzhengzhang
 * @description
 * @date 2022-11-28 16:02
 */
@Data
public class OrderAuditFgMqBO {
    private Long orderId;
    private Long fgId;
    private Integer businessType;
    private String opType;
    private String referenceId;
    private String isThrow;
}
