package com.ctrip.hotel.cost.infrastructure.repository.impl;

import com.ctrip.hotel.cost.domain.settlement.CancelOrderUsedBo;
import com.ctrip.hotel.cost.infrastructure.client.SettlementClient;
import com.ctrip.hotel.cost.infrastructure.mapper.SettlementDataPOMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import repository.SettlementRepository;

/**
 * @author yangzhengzhang
 * @description
 * @date 2022-11-18 14:51
 */
@Repository
public class SettlementRepositoryImpl implements SettlementRepository {

    @Autowired
    private SettlementClient settlementClient;

    @Override
    public boolean callCancelOrder(CancelOrderUsedBo cancelOrderUsedBo) {
        return settlementClient.callCancelOrder(
                SettlementDataPOMapper.INSTANCE.cancelOrderUsedBoToCancelOrderRequestType(cancelOrderUsedBo)
        );
    }
}
