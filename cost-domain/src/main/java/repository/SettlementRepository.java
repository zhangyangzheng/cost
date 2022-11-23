package repository;

import com.ctrip.hotel.cost.domain.settlement.CancelOrderUsedBo;

/**
 * @author yangzhengzhang
 * @description
 * @date 2022-11-18 14:37
 */
public interface SettlementRepository {
    boolean callCancelOrder(CancelOrderUsedBo cancelOrderUsedBo);
}
