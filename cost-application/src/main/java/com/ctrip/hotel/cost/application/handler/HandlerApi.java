package com.ctrip.hotel.cost.application.handler;

import com.ctrip.hotel.cost.application.model.vo.AuditOrderFgReqDTO;
import com.ctrip.hotel.cost.domain.data.model.AuditOrderInfoBO;

import java.util.List;

/**
 * @author yangzhengzhang
 * @description
 * @date 2022-11-03 1:54
 */
public interface HandlerApi {
    List<String> auditOrderFg(List<AuditOrderFgReqDTO> request);

    List<AuditOrderInfoBO> auditOrderFgCollectPrice(List<Long> costIds);
}
