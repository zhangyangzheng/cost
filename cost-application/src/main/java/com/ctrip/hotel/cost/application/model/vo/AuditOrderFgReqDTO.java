package com.ctrip.hotel.cost.application.model.vo;

import com.ctrip.hotel.cost.domain.data.model.OrderAuditFgMqBO;
import com.ctrip.hotel.cost.domain.data.model.SettlementCallBackInfo;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author yangzhengzhang
 * @description 【订单审核离店】计费请求参数
 * @date 2022-11-18 12:20
 */
@Data
@AllArgsConstructor
public class AuditOrderFgReqDTO {
    private OrderAuditFgMqBO orderAuditFgMqBO;

    private SettlementCallBackInfo settlementCallBackInfo;
}
