package com.ctrip.hotel.cost.application.handler;

import com.ctrip.hotel.cost.application.model.vo.AuditOrderFgReqVO;

import java.util.List;

/**
 * @author yangzhengzhang
 * @description
 * @date 2022-11-03 1:54
 */
public interface HandlerApi {
    List<Long> auditOrderFg(List<AuditOrderFgReqVO> request);
}
