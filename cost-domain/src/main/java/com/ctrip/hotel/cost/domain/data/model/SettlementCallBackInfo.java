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
    private Long orderInfoId;// 前置接口返回的OrderInfoID
    private String pushReferenceID;// 抛结算抛单接口调用，接口头部ReferenceID
    private Long hwpSettlementId;// 信用住订单结算单号
    private Boolean pushWalletPay;// 是否只推送闪住结算数据
    private String hwpReferenceID;// HWP接口调用，接口头部ReferenceID
}
