package com.ctrip.hotel.cost.infrastructure.repository.impl;

import com.ctrip.hotel.cost.domain.data.model.AuditOrderInfoBO;
import com.ctrip.hotel.cost.domain.data.model.AuditRoomInfo;
import com.ctrip.hotel.cost.domain.settlement.CancelOrderUsedBo;
import com.ctrip.hotel.cost.infrastructure.client.SettlementClient;
import com.ctrip.hotel.cost.infrastructure.mapper.SettlementDataPOMapper;
import com.ctrip.hotel.cost.infrastructure.model.dto.CancelOrderDto;
import com.ctrip.hotel.cost.infrastructure.model.dto.SettlementApplyListDto;
import com.ctrip.hotel.cost.infrastructure.model.dto.SettlementPayDataReceiveDto;
import com.ctrip.soa.hotel.settlement.api.DataItem;
import com.ctrip.soa.hotel.settlement.api.SettleDataRequest;
import com.ctrip.soa.hotel.settlement.api.SettlementApplyListRequestType;
import hotel.settlement.common.BigDecimalHelper;
import hotel.settlement.common.ConvertHelper;
import hotel.settlement.common.DateHelper;
import hotel.settlement.common.QConfigHelper;
import hotel.settlement.common.helpers.DefaultValueHelper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import repository.SettlementRepository;
import soa.ctrip.com.hotel.vendor.settlement.v1.settlementdata.SettlementPayData;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Objects;

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
        SettlementApplyListRequestType request = new SettlementApplyListRequestType();

        AuditRoomInfo auditRoomInfo = auditOrderInfoBO.getAuditRoomInfoList().get(0);



        SettleDataRequest requestData = SettlementDataPOMapper.INSTANCE.newOrderToSettlementApplyList(auditOrderInfoBO);
        requestData.setCollectionType("C");
//        requestData.setCompanyID(dr.getHotel() == null ? "" : dr.getHotel().toString());
//        requestData.setCurrency(DefaultValueHelper.getValue(dr.getCurrency()));
//        requestData.setId(0);
//        requestData.setMerchantId(6);
//        if (dr.getOrderDate() != null) {
//            requestData.setOrderDate(DateHelper.parseToCalendar(dr.getOrderDate()));
//        }
//
//        requestData.setOrderId(dr.getOrderID() == null ? "" : dr.getOrderID().toString());
        requestData.setChannelType("FGID_HWP");
        long FGID = DefaultValueHelper.getValue(auditOrderInfoBO.getFG);
        if (FGID > 0) {
            // RFD: Refunded，2571现付闪住退补款
            requestData.setOutSettlementNo(
                    !DefaultValueHelper.getValue(entity.getPushWalletPay())
                            ? "HWP-" + String.valueOf(FGID)
                            : "RFD-" + String.valueOf(FGID) + entity.getFGCommissionRoomID());
        } else {
            requestData.setOutSettlementNo(String.format("HWPN-%s", entity.getFGCommissionRoomID()));
        }

        if ("U".equals(auditOrderInfoBO.getOperateType())) {
            requestData.setSettlementId(entity.getHWPSettlementId());
            if (DefaultValueHelper.getValue(requestData.getSettlementId()) <= 0) {
                throw new Exception(
                        I18NMessageHelper.getMessageByLocal("HOTELFGSETTLEMENTDATAAPPLYMANAGEMENT_77"));
            }
        }

        requestData.setProductCode(DefaultValueHelper.getValue(dr.getRoomName()));
        requestData.setQuantity(DefaultValueHelper.getValue(auditOrderInfoBO.getQuantity()));
        requestData.setSettlementItemName(SettlementItemName.HotelWalletPay.getShowName());
        requestData.setSettlementPriceType("P".equals(dr.getPaymentType()) ? "P" : "C");
        requestData.setSourceId("6");

        requestData.setDataItems(new ArrayList<>());
        DataItem Item = new DataItem();
        Item.setDataKey("RoomName");
        Item.setDataValue(dr.getRoomName() == null ? "" : dr.getRoomName());
        Item.setDataDesc(
                I18NMessageHelper.getMessageByLocal("HOTELFGSETTLEMENTDATAAPPLYMANAGEMENT_78"));
        requestData.getDataItems().add(Item);

        if (ConvertHelper.getLong(dr.getCustomerOrderID()) > 0) {
            Item = new DataItem();
            Item.setDataKey("ClientOrderId");
            Item.setDataValue(ConvertHelper.getStr(dr.getCustomerOrderID()));
            Item.setDataDesc("ClientOrderId");
            requestData.getDataItems().add(Item);
        }

        Item = new DataItem();
        Item.setDataKey("BeginDate");
        Item.setDataValue(
                dr.getETA() == null
                        ? ""
                        : DateHelper.formatDate(dr.getETA(), DateHelper.SIMIPLE_DATE_FORMAT_STR));
        Item.setDataDesc(
                I18NMessageHelper.getMessageByLocal("HOTELFGSETTLEMENTDATAAPPLYMANAGEMENT_79"));
        requestData.getDataItems().add(Item);

        Item = new DataItem();
        Item.setDataKey("EndDate");
        Item.setDataValue(
                dr.getETD() == null
                        ? ""
                        : DateHelper.formatDate(dr.getETD(), DateHelper.SIMIPLE_DATE_FORMAT_STR));
        Item.setDataDesc(
                I18NMessageHelper.getMessageByLocal("HOTELFGSETTLEMENTDATAAPPLYMANAGEMENT_80"));
        requestData.getDataItems().add(Item);

        Item = new DataItem();
        Item.setDataKey("ClientName");
        Item.setDataValue(dr.getClientName() == null ? "" : dr.getClientName());
        Item.setDataDesc(
                I18NMessageHelper.getMessageByLocal("HOTELFGSETTLEMENTDATAAPPLYMANAGEMENT_81"));
        requestData.getDataItems().add(Item);

        Item = new DataItem();
        Item.setDataKey("Amount");
        Item.setDataValue(dr.getAmount() == null ? "" : dr.getAmount().toString());
        Item.setDataDesc(
                I18NMessageHelper.getMessageByLocal("HOTELFGSETTLEMENTDATAAPPLYMANAGEMENT_82"));
        requestData.getDataItems().add(Item);

        Item = new DataItem();
        Item.setDataKey("Cost");
        Item.setDataValue(dr.getCost() == null ? "" : dr.getCost().toString());
        Item.setDataDesc(
                I18NMessageHelper.getMessageByLocal("HOTELFGSETTLEMENTDATAAPPLYMANAGEMENT_83"));
        requestData.getDataItems().add(Item);

        Item = new DataItem();
        Item.setDataKey("Remark");
        Item.setDataValue("");
        Item.setDataDesc(
                I18NMessageHelper.getMessageByLocal("HOTELFGSETTLEMENTDATAAPPLYMANAGEMENT_84"));
        requestData.getDataItems().add(Item);

        Item = new DataItem();
        Item.setDataKey("OtherCost");
        Item.setDataValue(dr.getOtherCost() == null ? "" : dr.getOtherCost().toString());
        Item.setDataDesc(
                I18NMessageHelper.getMessageByLocal("HOTELFGSETTLEMENTDATAAPPLYMANAGEMENT_85"));
        requestData.getDataItems().add(Item);

        Item = new DataItem();
        Item.setDataKey("Room");
        Item.setDataValue(dr.getRoom() == null ? "" : dr.getRoom().toString());
        Item.setDataDesc(
                I18NMessageHelper.getMessageByLocal("HOTELFGSETTLEMENTDATAAPPLYMANAGEMENT_86"));
        requestData.getDataItems().add(Item);

        Item = new DataItem();
        Item.setDataKey("HtlConfirmNo");
        Item.setDataValue(dr.getHtlConfirmNo() == null ? "" : dr.getHtlConfirmNo());
        Item.setDataDesc(
                I18NMessageHelper.getMessageByLocal("HOTELFGSETTLEMENTDATAAPPLYMANAGEMENT_87"));
        requestData.getDataItems().add(Item);

        Item = new DataItem();
        Item.setDataKey("Hotel");
        Item.setDataValue(dr.getHotel() == null ? "0" : dr.getHotel().toString());
        Item.setDataDesc(
                I18NMessageHelper.getMessageByLocal("HOTELFGSETTLEMENTDATAAPPLYMANAGEMENT_88"));
        requestData.getDataItems().add(Item);

        Item = new DataItem();
        Item.setDataKey("OrderConfirmType");
        Item.setDataValue(dr.getOrderConfirmType() == null ? "" : dr.getOrderConfirmType());
        Item.setDataDesc(
                I18NMessageHelper.getMessageByLocal("HOTELFGSETTLEMENTDATAAPPLYMANAGEMENT_89"));
        requestData.getDataItems().add(Item);

        if (dr.getConfirmIncomeTime() != null) {
            Item = new DataItem();
            Item.setDataKey("ConfirmIncomeTime");
            Item.setDataValue(
                    DateHelper.formatDate(dr.getConfirmIncomeTime(), DateHelper.SIMIPLE_DATE_FORMAT_STR));
            Item.setDataDesc(
                    I18NMessageHelper.getMessageByLocal("HOTELFGSETTLEMENTDATAAPPLYMANAGEMENT_90"));
            requestData.getDataItems().add(Item);
        }

        if (dr.getInsuranceSupportType() != null) {
            Item = new DataItem();
            Item.setDataKey("InsuranceFlag");
            Item.setDataValue(dr.getInsuranceSupportType().toString());
            Item.setDataDesc(
                    I18NMessageHelper.getMessageByLocal("HOTELFGSETTLEMENTDATAAPPLYMANAGEMENT_91"));
            requestData.getDataItems().add(Item);
        }

        // 订单UID
        if (dr.getUid() != null) {
            Item = new DataItem();
            Item.setDataKey("Uid");
            Item.setDataValue(DefaultValueHelper.getValue(dr.getUid()));
            Item.setDataDesc("Uid");
            requestData.getDataItems().add(Item);
        }

        if (dr.getZeroCommissionFeeRatio() != null
                && dr.getZeroCommissionFeeRatio().compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal zeroCommissionAmount = dr.getCost().multiply(dr.getZeroCommissionFeeRatio());

            Item = new DataItem();
            Item.setDataKey("ZeroCommissionAmount");
            Item.setDataValue(zeroCommissionAmount.toString());
            Item.setDataDesc(
                    I18NMessageHelper.getMessageByLocal(
                            "hotel.settlement.ws.service.impl.hotelfgsettlementdataapplymanagement.94"));
            requestData.getDataItems().add(Item);

            Item = new DataItem();
            Item.setDataKey("ZeroCommissionDeductRate");
            Item.setDataValue(dr.getZeroCommissionFeeRatio().toString());
            Item.setDataDesc(
                    I18NMessageHelper.getMessageByLocal(
                            "hotel.settlement.ws.service.impl.hotelfgsettlementdataapplymanagement.95"));
            requestData.getDataItems().add(Item);
        }

        Item = new DataItem();
        Item.setDataKey("outTimeDeductValue");
        Item.setDataValue(dr.getOutTimeDeductValue() == null ? "" : dr.getOutTimeDeductValue().toString());
        Item.setDataDesc(I18NMessageHelper.getMessageByLocal(
                "hotel.settlement.ws.service.impl.hotelfgsettlementdataapplymanagement.96"));
        requestData.getDataItems().add(Item);

        Item = new DataItem();
        Item.setDataKey("outTimeDeductType");
        Item.setDataValue(dr.getOutTimeDeductType() == null ? "" : dr.getOutTimeDeductType().toString());
        Item.setDataDesc(I18NMessageHelper.getMessageByLocal(
                "hotel.settlement.ws.service.impl.hotelfgsettlementdataapplymanagement.97"));
        requestData.getDataItems().add(Item);



        request.setSettleDatas(new ArrayList<>());
        request.getSettleDatas().add();
    }
}
