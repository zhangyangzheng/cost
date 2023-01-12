package com.ctrip.hotel.cost.caller;

import com.ctrip.hotel.cost.application.model.vo.AuditOrderFgReqDTO;
import com.ctrip.hotel.cost.model.bo.FgOrderAuditMqDataBo;
import hotel.settlement.common.tuples.Tuple;

import java.util.List;

public abstract class BaseHandlerCaller {

    public abstract Tuple<List<FgOrderAuditMqDataBo>, List<FgOrderAuditMqDataBo>> batchCallAndGetSuccessAndFailList(List<FgOrderAuditMqDataBo> throwSettleList);
}
