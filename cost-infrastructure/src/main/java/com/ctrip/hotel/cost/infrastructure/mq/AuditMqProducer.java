package com.ctrip.hotel.cost.infrastructure.mq;

import com.ctrip.hotel.cost.infrastructure.model.dto.FgBackToAuditDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AuditMqProducer {
    @Autowired
    QmqHelper qmqHelper;

    public void fgBackToAudit(FgBackToAuditDto fgBackToAuditDto) throws Exception {
        qmqHelper.sendMessage("hotel.cost.notifybackaudit", fgBackToAuditDto);
    }
}
