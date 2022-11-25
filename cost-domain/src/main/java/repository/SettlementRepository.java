package repository;

import com.ctrip.hotel.cost.domain.data.model.AuditOrderInfoBO;
import com.ctrip.hotel.cost.domain.settlement.CancelOrderUsedBo;

/**
 * @author yangzhengzhang
 * @description
 * @date 2022-11-18 14:37
 */
public interface SettlementRepository {
    boolean callCancelOrder(CancelOrderUsedBo cancelOrderUsedBo);

    boolean callSettlementPayDataReceive(AuditOrderInfoBO auditOrderInfoBO);

    boolean callSettlementApplyList(AuditOrderInfoBO auditOrderInfoBO);

}
