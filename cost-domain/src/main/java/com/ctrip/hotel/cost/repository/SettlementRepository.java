package com.ctrip.hotel.cost.repository;

import com.ctrip.hotel.cost.domain.data.model.AuditOrderInfoBO;

/**
 * @author yangzhengzhang
 * @description
 * @date 2022-11-18 14:37
 */
public interface SettlementRepository {
    /**
     * 抛前置取消单
     * @param
     * @return
     */
    boolean callCancelOrder(AuditOrderInfoBO auditOrderInfoBO) throws Exception;

    /**
     * normal（601）结算取消
     * @param auditOrderInfoBO
     * @return
     */
    boolean callCancelSettlementCancelList(AuditOrderInfoBO auditOrderInfoBO) throws Exception;

    /**
     * 闪住（606）结算取消
     * @param auditOrderInfoBO
     * @return
     */
    boolean callCancelSettlementCancelListHWP(AuditOrderInfoBO auditOrderInfoBO) throws Exception;

    /**
     * 抛前置
     * @param auditOrderInfoBO
     * @return
     */
    Long callSettlementPayDataReceive(AuditOrderInfoBO auditOrderInfoBO) throws Exception;

    /**
     * 新单或修改单抛结算
     * @param auditOrderInfoBO
     * @return
     */
    Long callSettlementApplyList(AuditOrderInfoBO auditOrderInfoBO) throws Exception;

    /**
     * 新单或修改单抛HWP
     * @param auditOrderInfoBO
     * @return
     */
    Long callSettlementApplyListHWP(AuditOrderInfoBO auditOrderInfoBO) throws Exception;

    boolean callConfigCanPush() throws Exception;

    boolean callCheckFGBidSplit(
            Integer vendorChannelID,
            Integer hotelId,
            String orderConfirmType,
            Boolean isSupportAnticipation)
            throws Exception;
}
