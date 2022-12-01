package com.ctrip.hotel.cost.domain.data.model;

import lombok.Data;

/**
 * @author yangzhengzhang
 * @description
 * @date 2022-11-23 14:48
 */
@Data
public class SettlementCallBackInfo {
    private Long settlementId;
    private Long hwpSettlementId;// 信用住订单结算单号
    private Long orderInfoId;// 前置接口返回的OrderInfoID
    private String pushReferenceId;// 抛结算抛单接口调用，接口头部ReferenceID
    private String hwpReferenceId;// HWP接口调用，接口头部ReferenceID
    private Boolean pushWalletPay;// 是否只推送闪住结算数据
}
