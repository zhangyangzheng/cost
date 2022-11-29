package com.ctrip.hotel.cost.infrastructure.repository.impl;

import com.ctrip.hotel.cost.domain.data.model.AuditOrderInfoBO;
import com.ctrip.hotel.cost.infrastructure.client.SettlementClient;
import com.ctrip.hotel.cost.infrastructure.mapper.SettlementDataPOMapper;
import com.ctrip.hotel.cost.infrastructure.model.dto.CancelOrderDto;
import com.ctrip.hotel.cost.infrastructure.model.dto.SettlementApplyListDto;
import com.ctrip.hotel.cost.infrastructure.model.dto.SettlementCancelListDto;
import com.ctrip.hotel.cost.infrastructure.model.dto.SettlementPayDataReceiveDto;
import com.ctrip.soa.hotel.settlement.api.CancelDataItem;
import com.ctrip.soa.hotel.settlement.api.CancelSettleData;
import com.ctrip.soa.hotel.settlement.api.DataItem;
import com.ctrip.soa.hotel.settlement.api.SettleDataRequest;
import hotel.settlement.common.BigDecimalHelper;
import hotel.settlement.common.DateHelper;
import hotel.settlement.common.QConfigHelper;
import hotel.settlement.common.helpers.DefaultValueHelper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import repository.SettlementRepository;
import soa.ctrip.com.hotel.vendor.settlement.v1.Hotelorderchannel;
import soa.ctrip.com.hotel.vendor.settlement.v1.cancelorder.CancelorderRequesttype;
import soa.ctrip.com.hotel.vendor.settlement.v1.settlementdata.SettlementPayData;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

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
    public boolean callCancelOrder(AuditOrderInfoBO auditOrderInfoBO) {
        CancelorderRequesttype cancelOrderRequest = new CancelorderRequesttype();
        cancelOrderRequest.setOrderchannel(Hotelorderchannel.hfg);
        cancelOrderRequest.setOrderid(auditOrderInfoBO.getOrderAuditFgMqBO().getOrderId().toString());
        cancelOrderRequest.setFGID(auditOrderInfoBO.getOrderAuditFgMqBO().getFgId());
        CancelOrderDto orderDto = new CancelOrderDto();
        orderDto.setCancelOrderRequest(cancelOrderRequest);
        orderDto.setReferenceId(auditOrderInfoBO.getOrderAuditFgMqBO().getReferenceId());
        settlementClient.callCancelOrder(orderDto);
        return true;
    }

    @Override
    public boolean callCancelSettlementCancelList(AuditOrderInfoBO auditOrderInfoBO) {
        CancelSettleData request = new CancelSettleData();
        request.setId(0);
        request.setMerchantId(6);
        request.setSettlementId(auditOrderInfoBO.getSettlementCallBackInfo().getSettlementId());
        if ("true".equals(QConfigHelper.getSwitchConfigByKey("FgOutsettlementNo", "true"))) {
            if (DefaultValueHelper.getValue(auditOrderInfoBO.getOrderAuditFgMqBO().getFgId()) > 0) { // fgId必须大于0，否则是无效数据
                request.setOutSettlementNo(String.valueOf(auditOrderInfoBO.getOrderAuditFgMqBO().getFgId()));
            }
//            else {
//                request.setOutSettlementNo(
//                        "FM-"
//                                + (entity.getFGCommissionRoomID() == null
//                                ? null
//                                : String.valueOf(entity.getFGCommissionRoomID())));
//            }
        } else {
            request.setOutSettlementNo(String.valueOf(auditOrderInfoBO.getOrderAuditFgMqBO().getFgId()));
        }
        List<CancelDataItem> list = new ArrayList<>();
        if (auditOrderInfoBO.getAuditRoomInfoList().get(0).getAuditRoomBasicInfo().getOperateType() != null) {
            list.add(new CancelDataItem("ModifyOperateType", auditOrderInfoBO.getAuditRoomInfoList().get(0).getAuditRoomBasicInfo().getOperateType(), "修改类型"));
            list.add(new CancelDataItem("ModifyOperateSubType", auditOrderInfoBO.getAuditRoomInfoList().get(0).getAuditRoomBasicInfo().getSubOperateType(), "修改子类型"));
            list.add(new CancelDataItem("ModifyOperateDateTime", DateHelper.format(auditOrderInfoBO.getAuditRoomInfoList().get(0).getAuditRoomBasicInfo().getOperateTime(), DateHelper.SIMIPLE_DATE_FORMAT_STR), "操作时间"));
            list.add(new CancelDataItem("ModifyOperateEid", auditOrderInfoBO.getAuditRoomInfoList().get(0).getAuditRoomBasicInfo().getOperator(), "操作人"));
        }
        request.setDataItems(list);
        SettlementCancelListDto dto = new SettlementCancelListDto();
        dto.setReferenceId(auditOrderInfoBO.getOrderAuditFgMqBO().getReferenceId());
        dto.setCancelSettleData(request);
        settlementClient.callSettlementCancelList(dto);
        return true;
    }

    @Override
    public boolean callCancelSettlementCancelListHWP(AuditOrderInfoBO auditOrderInfoBO) {
        CancelSettleData request = new CancelSettleData();
        request.setId(0);
        request.setMerchantId(6);
        // RFD: Refunded，2571现付闪住退补款
        request.setOutSettlementNo(
                !DefaultValueHelper.getValue(auditOrderInfoBO.getSettlementCallBackInfo().getPushWalletPay())
                        ? "HWP-" + auditOrderInfoBO.getOrderAuditFgMqBO().getFgId()
                        : "RFD-" + auditOrderInfoBO.getOrderAuditFgMqBO().getFgId());
        request.setSettlementId(auditOrderInfoBO.getSettlementCallBackInfo().getHwpSettlementId());
        SettlementCancelListDto dto = new SettlementCancelListDto();
        dto.setReferenceId(auditOrderInfoBO.getOrderAuditFgMqBO().getReferenceId());
        dto.setCancelSettleData(request);
        settlementClient.callSettlementCancelList(dto);
        return true;
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

        // todo copy过来改

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

    @Override
    public boolean callSettlementApplyListHWP(AuditOrderInfoBO auditOrderInfoBO) {
        return false;
    }
}
