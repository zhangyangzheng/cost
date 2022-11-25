package com.ctrip.hotel.cost.infrastructure.repository.impl;

import com.ctrip.hotel.cost.domain.data.model.AuditOrderInfoBO;
import com.ctrip.hotel.cost.domain.settlement.CancelOrderUsedBo;
import com.ctrip.hotel.cost.infrastructure.client.SettlementClient;
import com.ctrip.hotel.cost.infrastructure.mapper.SettlementDataPOMapper;
import com.ctrip.hotel.cost.infrastructure.model.dto.CancelOrderDto;
import com.ctrip.hotel.cost.infrastructure.model.dto.SettlementApplyListDto;
import com.ctrip.hotel.cost.infrastructure.model.dto.SettlementPayDataReceiveDto;
import com.ctrip.soa.hotel.settlement.api.DataItem;
import com.ctrip.soa.hotel.settlement.api.SettleDataRequest;
import hotel.settlement.common.BigDecimalHelper;
import hotel.settlement.common.QConfigHelper;
import hotel.settlement.common.helpers.DefaultValueHelper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import repository.SettlementRepository;
import soa.ctrip.com.hotel.vendor.settlement.v1.settlementdata.SettlementPayData;

import java.math.BigDecimal;

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
                new CancelOrderDto(SettlementDataPOMapper.INSTANCE.cancelOrderUsedBoToCancelOrderRequestType(cancelOrderUsedBo),
                        ""// todo set referenceId
                )
        );
    }

    @Override
    public boolean callSettlementPayDataReceive(AuditOrderInfoBO auditOrderInfoBO) {
        SettlementPayData settlementPayData = SettlementDataPOMapper.INSTANCE.newOrderToSettlementPayDataReceive(auditOrderInfoBO);
        // 联合会员逻辑 todo 线上开关没开，这个业务什么时候开不一定，先写在这里过渡
        String associateMemberOrder = null;
        BigDecimal associateMemberCommissionRatio = null;
        Integer associateMemberHotelID = null;
        String associateMemberCommissionType = null;
        if (auditOrderInfoBO.getAssociateMember() != null) {
            associateMemberOrder = auditOrderInfoBO.getAssociateMember().getAssociateMemberOrder();
            associateMemberCommissionRatio = auditOrderInfoBO.getAssociateMember().getAssociateMemberCommissionRatio();
            associateMemberHotelID = auditOrderInfoBO.getAssociateMember().getAssociateMemberHotelID();
            associateMemberCommissionType = auditOrderInfoBO.getAssociateMember().getAssociateMemberCommissionType();
        }
        if (!StringUtils.isEmpty(associateMemberOrder)
                && "true"
                .equalsIgnoreCase(
                        QConfigHelper.getSwitchConfigByKey(
                                "AssociateMemberIntoSettlementSwitch", "false"))) {
            if ("1".equalsIgnoreCase(associateMemberOrder)) {
                BigDecimal associateMemberCommissionRatioNew;
                if (DefaultValueHelper.getValue(associateMemberCommissionRatio).compareTo(BigDecimal.ZERO)
                        > 0) {
                    associateMemberCommissionRatioNew = associateMemberCommissionRatio;
                } else {
                    associateMemberCommissionRatioNew =
                            BigDecimalHelper.getValue(
                                    QConfigHelper.getSwitchConfigByKey(
                                            "AssociateMemberCommissionRatioDefaultValue", "0.02"));
                }

                // 免佣(按比例收取技术服务费)
                settlementPayData.setUinonMemberFlag("T");
                settlementPayData.setServiceFeeProportion(associateMemberCommissionRatioNew);

                if (DefaultValueHelper.getValue(auditOrderInfoBO.getBuyoutDiscountAmount()).compareTo(BigDecimal.ZERO) >= 0) {
                    settlementPayData.setBuyoutDiscountAmount(DefaultValueHelper.getValue(auditOrderInfoBO.getBuyoutDiscountAmount()));
                }
            } else if ("3".equalsIgnoreCase(associateMemberOrder)
                    && DefaultValueHelper.getValue(associateMemberHotelID) > 0
                    && DefaultValueHelper.getValue(associateMemberCommissionRatio).compareTo(BigDecimal.ZERO)
                    > 0
                    && "Refund".equalsIgnoreCase(DefaultValueHelper.getValue(associateMemberCommissionType))) {
                // 返佣(按比例返酒店佣金)
                // 返佣(按比例返酒店佣金)
                settlementPayData.setRakebackFlag("T");
                settlementPayData.setRakebackHotelId(associateMemberHotelID.toString());
                settlementPayData.setRakebackRate(associateMemberCommissionRatio);
                settlementPayData.setTripPromotionAmount(auditOrderInfoBO.getTripPromotionAmount());
            }
        }
        return settlementClient.callSettlementPayDataReceive(
                new SettlementPayDataReceiveDto(settlementPayData,
                        ""// todo set referenceId
                )
        );
    }

    @Override
    public boolean callSettlementApplyList(AuditOrderInfoBO auditOrderInfoBO) {
        SettleDataRequest settleDataRequest = SettlementDataPOMapper.INSTANCE.newOrderToSettlementApplyList(auditOrderInfoBO);

        // 联合会员逻辑
        String associateMemberOrder = null;
        BigDecimal associateMemberCommissionRatio = null;
        Integer associateMemberHotelID = null;
        String associateMemberCommissionType = null;
        if (auditOrderInfoBO.getAssociateMember() != null) {
            associateMemberOrder = auditOrderInfoBO.getAssociateMember().getAssociateMemberOrder();
            associateMemberCommissionRatio = auditOrderInfoBO.getAssociateMember().getAssociateMemberCommissionRatio();
            associateMemberHotelID = auditOrderInfoBO.getAssociateMember().getAssociateMemberHotelID();
            associateMemberCommissionType = auditOrderInfoBO.getAssociateMember().getAssociateMemberCommissionType();
        }
        if (!StringUtils.isEmpty(associateMemberOrder)
                && "true"
                .equalsIgnoreCase(
                        QConfigHelper.getSwitchConfigByKey(
                                "AssociateMemberIntoSettlementSwitch", "false"))) {
            if ("1".equalsIgnoreCase(associateMemberOrder)) {
                String associateMemberCommissionRatioNew = "";
                if (DefaultValueHelper.getValue(associateMemberCommissionRatio).compareTo(BigDecimal.ZERO)
                        > 0) {
                    associateMemberCommissionRatioNew = associateMemberCommissionRatio.toString();
                } else {
                    associateMemberCommissionRatioNew =
                            QConfigHelper.getSwitchConfigByKey(
                                    "AssociateMemberCommissionRatioDefaultValue", "0.02");
                }

                // 免佣(按比例收取技术服务费)
                settleDataRequest.getDataItems().add(new DataItem("UinonMemberFlag", "T", "UinonMemberFlag"));
                settleDataRequest.getDataItems().add(new DataItem("ServiceFeeProportion", associateMemberCommissionRatioNew, "ServiceFeeProportion"));

                if (DefaultValueHelper.getValue(auditOrderInfoBO.getBuyoutDiscountAmount()).compareTo(BigDecimal.ZERO) >= 0) {
                    settleDataRequest.getDataItems().add(new DataItem("BuyoutDiscountAmount", auditOrderInfoBO.getBuyoutDiscountAmount().toString(), "BuyoutDiscountAmount"));
                }
            } else if ("3".equalsIgnoreCase(associateMemberOrder)
                    && DefaultValueHelper.getValue(associateMemberHotelID) > 0
                    && DefaultValueHelper.getValue(associateMemberCommissionRatio).compareTo(BigDecimal.ZERO)
                    > 0
                    && "Refund".equalsIgnoreCase(associateMemberCommissionType)) {
                // 返佣(按比例返酒店佣金)
                settleDataRequest.getDataItems().add(new DataItem("RakebackFlag", "T", "RakebackFlag"));
                settleDataRequest.getDataItems().add(new DataItem("RakebackHotelId", associateMemberHotelID.toString(), "RakebackHotelId"));
                settleDataRequest.getDataItems().add(new DataItem("RakebackRate", associateMemberCommissionRatio.toString(), "RakebackRate"));
                settleDataRequest.getDataItems().add(new DataItem("TripPromotionAmount", auditOrderInfoBO.getTripPromotionAmount().toString(), "TripPromotionAmount"));
            }
        }
        return settlementClient.callSettlementApplyList(
                new SettlementApplyListDto(settleDataRequest,
                        ""// todo set referenceId
                )
        );
    }
}
