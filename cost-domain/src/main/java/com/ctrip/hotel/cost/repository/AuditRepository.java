package com.ctrip.hotel.cost.repository;

import com.ctrip.hotel.cost.domain.data.model.AuditOrderInfoBO;

/**
 * @author yangzhengzhang
 * @description
 * @date 2022-12-01 19:53
 */
public interface AuditRepository {
    /**
     * 计费结果通知审核
     * @param auditOrderInfoBO
     */
    void notifyResult(AuditOrderInfoBO auditOrderInfoBO) throws Exception;
}
