package com.ctrip.hotel.cost.mq;

import com.ctrip.hotel.cost.model.dto.FgBackToAuditDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AuditMqProducer {
    @Autowired
    QmqHelper qmqHelper;

    public void fgBackToAudit(FgBackToAuditDto fgBackToAuditDto) throws Exception {
        qmqHelper.sendMessageSync("hotel.cost.notifybackaudit", fgBackToAuditDto);
    }
}
