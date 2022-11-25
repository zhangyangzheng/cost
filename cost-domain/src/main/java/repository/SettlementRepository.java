package repository;

import com.ctrip.hotel.cost.domain.data.model.AuditOrderInfoBO;
import com.ctrip.hotel.cost.domain.settlement.CancelOrderUsedBo;

/**
 * @author yangzhengzhang
 * @description
 * @date 2022-11-18 14:37
 */
public interface SettlementRepository {
    /**
     * 取消单
     * @param cancelOrderUsedBo
     * @return
     */
    boolean callCancelOrder(CancelOrderUsedBo cancelOrderUsedBo);

    /**
     * 抛前置 done
     * @param auditOrderInfoBO
     * @return
     */
    boolean callSettlementPayDataReceive(AuditOrderInfoBO auditOrderInfoBO);

    /**
     * 新单或修改单抛结算
     * todo 未完工
     * @param auditOrderInfoBO
     * @return
     */
    boolean callSettlementApplyList(AuditOrderInfoBO auditOrderInfoBO);

    /**
     * 新单或修改单抛HWP
     * todo 未完工
     * @param auditOrderInfoBO
     * @return
     */
    boolean callSettlementApplyListHWP(AuditOrderInfoBO auditOrderInfoBO);

}
