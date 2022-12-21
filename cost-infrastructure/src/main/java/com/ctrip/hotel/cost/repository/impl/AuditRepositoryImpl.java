package com.ctrip.hotel.cost.repository.impl;

import com.ctrip.hotel.cost.domain.data.model.AuditOrderInfoBO;
import com.ctrip.hotel.cost.mapper.AuditMapper;
import com.ctrip.hotel.cost.model.dto.FgBackToAuditDto;
import com.ctrip.hotel.cost.mq.AuditMqProducer;
import com.ctrip.hotel.cost.repository.AuditRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

/**
 * @author yangzhengzhang
 * @description
 * @date 2022-12-01 19:55
 */
@Repository
public class AuditRepositoryImpl implements AuditRepository {

    @Autowired
    private AuditMqProducer auditMqProducer;

    @Override
    public void notifyResult(AuditOrderInfoBO auditOrderInfoBO) throws Exception {
        FgBackToAuditDto fgBackToAuditDto = AuditMapper.INSTANCE.AuditOrderInfoBOToFgBackToAuditDto(auditOrderInfoBO, auditOrderInfoBO.getOrderAuditFgMqBO(), auditOrderInfoBO.getSettlementCallBackInfo());
        auditMqProducer.fgBackToAudit(fgBackToAuditDto);
    }
}
