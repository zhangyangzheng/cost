package com.ctrip.hotel.cost.domain.settlement;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import repository.SettlementRepository;

/**
 * @author yangzhengzhang
 * @description
 * @date 2022-11-18 15:09
 */
@Service
public class SettlementService {

    @Autowired
    private SettlementRepository settlementRepository;

    public Boolean callCancelOrderFg(Long orderId, Long fgId) {
        CancelOrderUsedBo cancelOrderUsedBo = new CancelOrderUsedBo();
        cancelOrderUsedBo.setOrderchannel(EnumHotelorderchannel.hfg);
        cancelOrderUsedBo.setOrderid(String.valueOf(orderId));
        cancelOrderUsedBo.setFGID(fgId);
        return settlementRepository.callCancelOrder(cancelOrderUsedBo);
    }

}
