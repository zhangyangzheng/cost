package com.ctrip.hotel.cost.application.model.vo;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author yangzhengzhang
 * @description 【订单审核离店】计费请求参数
 * @date 2022-11-18 12:20
 */
@Data
@AllArgsConstructor
public class AuditOrderFgReqVO {
    private Long orderId;
    private Long fgId;
    private Boolean isCancel;
}
