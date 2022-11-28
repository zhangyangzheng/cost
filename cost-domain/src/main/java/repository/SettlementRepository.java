package repository;

import com.ctrip.hotel.cost.domain.data.model.AuditOrderInfoBO;

/**
 * @author yangzhengzhang
 * @description
 * @date 2022-11-18 14:37
 */
public interface SettlementRepository {
    /**
     * 抛前置取消单
     * todo 未完工
     * @param
     * @return
     */
    boolean callCancelOrder(AuditOrderInfoBO auditOrderInfoBO);

    /**
     * normal（601）结算取消
     * todo 未完工
     * @param auditOrderInfoBO
     * @return
     */
    boolean callCancelSettlementCancelList(AuditOrderInfoBO auditOrderInfoBO);

    /**
     * 闪住（606）结算取消
     * todo 未完工
     * @param auditOrderInfoBO
     * @return
     */
    boolean callCancelSettlementCancelListHWP(AuditOrderInfoBO auditOrderInfoBO);

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
