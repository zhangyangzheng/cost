package com.ctrip.hotel.cost.domain.settlement;

import com.ctrip.hotel.cost.domain.data.model.AuditOrderInfoBO;
import hotel.settlement.common.QConfigHelper;
import hotel.settlement.common.helpers.DefaultValueHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import repository.SettlementRepository;

import java.util.Arrays;
import java.util.List;

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
//        if () {
//
//        }
//
//
//        CancelOrderUsedBo cancelOrderUsedBo = new CancelOrderUsedBo();
//        cancelOrderUsedBo.setOrderchannel(EnumHotelorderchannel.hfg);
//        cancelOrderUsedBo.setOrderid(String.valueOf(orderId));
//        cancelOrderUsedBo.setFGID(fgId);
//        return settlementRepository.callCancelOrder(cancelOrderUsedBo);
        return false;
    }

    public Boolean callCancelForFg(AuditOrderInfoBO auditOrderInfoBO) {
        if (DefaultValueHelper.getValue(auditOrderInfoBO.getSettlementCallBackInfo().getOrderInfoId()) > 0) { // 前置模块已经抛单成功
            settlementRepository.callCancelOrder(auditOrderInfoBO);
        } else {
            // todo 区分付底还是付面
            if (!DefaultValueHelper.getValue(auditOrderInfoBO.getSettlementCallBackInfo().getPushWalletPay())) { // normal（601）订单取消
                settlementRepository.callCancelSettlementCancelList(auditOrderInfoBO); // todo set 接口返回值 outSettlementId
            }
            if (DefaultValueHelper.getValue(auditOrderInfoBO.getSettlementCallBackInfo().getPushWalletPay())) {
                settlementRepository.callCancelSettlementCancelListHWP(auditOrderInfoBO); // todo set 接口返回值 outSettlementId
            }
        }
        return true;
    }

    public Boolean callSettlementForFg(AuditOrderInfoBO auditOrderInfoBO) {
        if (isToPreprocess(auditOrderInfoBO)) {
            // 抛前置
            settlementRepository.callSettlementPayDataReceive(auditOrderInfoBO); // todo set 接口返回值 orderinfoid
        } else {
            // 抛结算
            if (EnumOrderOpType.CREATE.getName().equals(auditOrderInfoBO.getOpType())) {// 如果是“C”(新订单), 没有推送过
                // 区分闪住和非闪住
                if (DefaultValueHelper.getValue(auditOrderInfoBO.getFlashOrderInfo().getIsFlashOrder())
                    && DefaultValueHelper.getValue(auditOrderInfoBO.getHotelBasicInfo().getPaymentType()).equals("P")
                    && configCanPush()
                ) {// 闪住 && 付面，只抛606
                    auditOrderInfoBO.getSettlementCallBackInfo().setPushWalletPay(true);
                    auditOrderInfoBO.setRemarks("闪住付面仅抛606");
                    settlementRepository.callSettlementApplyListHWP(auditOrderInfoBO); // todo set 接口返回值 outSettlementId + PushWalletPay
                } else if (DefaultValueHelper.getValue(auditOrderInfoBO.getFlashOrderInfo().getIsFlashOrder())
                        && !DefaultValueHelper.getValue(auditOrderInfoBO.getHotelBasicInfo().getPaymentType()).equals("P")
                        && configCanPush()
                ) {// 闪住 && 付底，抛601 + 606
                    settlementRepository.callSettlementApplyList(auditOrderInfoBO);
                    settlementRepository.callSettlementApplyListHWP(auditOrderInfoBO);
                    // todo set 接口返回值 outSettlementId + outSettlementIdHWP
                } else {// 除了606，其他新订单都要抛一次601
                    settlementRepository.callSettlementApplyList(auditOrderInfoBO); // todo set 接口返回值 outSettlementId
                }
            } else if (EnumOrderOpType.UPDATE.getName().equals(auditOrderInfoBO.getOpType())) { // 如果是“U”(修改单)
                // 区分闪住和非闪住
                if (DefaultValueHelper.getValue(auditOrderInfoBO.getFlashOrderInfo().getIsFlashOrder())
                        && DefaultValueHelper.getValue(auditOrderInfoBO.getHotelBasicInfo().getPaymentType()).equals("P")
                        && configCanPush()
                ) {// 闪住 && 付面，只抛606
                    settlementRepository.callSettlementApplyListHWP(auditOrderInfoBO); // todo set 接口返回值 outSettlementId + PushWalletPay
                } else if (DefaultValueHelper.getValue(auditOrderInfoBO.getFlashOrderInfo().getIsFlashOrder())
                        && !DefaultValueHelper.getValue(auditOrderInfoBO.getHotelBasicInfo().getPaymentType()).equals("P")
                        && configCanPush()
                ) {// 闪住 && 付底，抛601 + 606
                    settlementRepository.callSettlementApplyList(auditOrderInfoBO);
                    settlementRepository.callSettlementApplyListHWP(auditOrderInfoBO);
                    // todo set 接口返回值 outSettlementId + outSettlementIdHWP
                } else {// 除了606，其他新订单都要抛一次601
                    settlementRepository.callSettlementApplyList(auditOrderInfoBO); // todo set 接口返回值 outSettlementId
                }
            }
        }
        // mq to audit

        return true;
    }

    // 判断是否走前置模块抛结算，VendorID=253或766或136，云梯(直通车)金额>0，订单确认方式为I,或者orderinfoID>0
    protected boolean isToPreprocess(AuditOrderInfoBO auditOrderInfoBO) {
        boolean result;
        List<String> hotelPreprocessVendorIDllist =
                Arrays.asList(
                        QConfigHelper.getSwitchConfigByKey("HotelPreprocessVendorID", "").split(","));
        List<String> SplitHotellist =
                Arrays.asList(QConfigHelper.getSwitchConfigByKey("SplitHotel", "").split(","));
        String splitFlag = QConfigHelper.getSwitchConfigByKey("IsToPreprocess_SplitFlag", "F");
        int vendorChannelID = DefaultValueHelper.getValue(auditOrderInfoBO.getOrderBasicInfo().getVendorChannelID());
        int hotelId = DefaultValueHelper.getValue(auditOrderInfoBO.getHotelBasicInfo().getHotel());
        String orderConfirmType = DefaultValueHelper.getValue(auditOrderInfoBO.getOrderBasicInfo().getOrderConfirmType());
        boolean isSupportAnticipation = DefaultValueHelper.getValue(auditOrderInfoBO.getFlashOrderInfo().getIsFlashOrder());
        result = vendorChannelID > 0;
        if ("T".equalsIgnoreCase(splitFlag)
                || ","
                .concat(splitFlag)
                .concat(",")
                .contains(
                        ","
                                .concat(DefaultValueHelper.convertToString(auditOrderInfoBO.getOrderBasicInfo().getVendorChannelID()))
                                .concat(","))) {
            result =
                    result
                            && checkFGBidSplit(
                            vendorChannelID, hotelId, orderConfirmType, isSupportAnticipation);
        } else {

            result =
                    result
                            && hotelPreprocessVendorIDllist.contains(
                            DefaultValueHelper.convertToString(auditOrderInfoBO.getOrderBasicInfo().getVendorChannelID()));
            result =
                    result
                            && ((hotelId > 0
                            && SplitHotellist.contains(DefaultValueHelper.convertToString(auditOrderInfoBO.getHotelBasicInfo().getHotel())))
                            || SplitHotellist.contains("ALL"));
            result = result && "I".equalsIgnoreCase(orderConfirmType);
        }

        result = result && auditOrderInfoBO.getBidPrice() != null;
        result = result && DefaultValueHelper.getValue(auditOrderInfoBO.getAuditRoomInfoList().get(0).getAuditRoomBasicInfo().getFgid()) != 0;
        result = result && DefaultValueHelper.getValue(auditOrderInfoBO.getSettlementCallBackInfo().getSettlementId()) <= 0;
        result = result || DefaultValueHelper.getValue(auditOrderInfoBO.getSettlementCallBackInfo().getOrderInfoId()) > 0;

        return result;
    }

    protected boolean checkFGBidSplit(
            int vendorChannelID,
            int hotelId,
            String orderConfirmType,
            boolean isSupportAnticipation) {
        boolean result = false;
        // todo 查结算数据
//        try {
//            int settlementItemId = 601;
//            WhereClauseBuilder whereClauseBuilder = new WhereClauseBuilder();
//            whereClauseBuilder.equal("VendorChanneId", vendorChannelID, Types.INTEGER);
//            whereClauseBuilder.equal("SettlementItemId", settlementItemId, Types.INTEGER);
//            CSPVendorChanneHotelRelationGen relationGen =
//                    vendorChanneHotelRelationDao.queryFirst(whereClauseBuilder);
//            if (null != relationGen) {
//                String setOrderConfirmType =
//                        DefaultValueHelper.getValue(relationGen.getOrderConfirmType(), "all");
//                if ("all".equalsIgnoreCase(setOrderConfirmType)) {
//                    result = true;
//                } else if ("|"
//                        .concat(setOrderConfirmType)
//                        .concat("|")
//                        .contains("|".concat(orderConfirmType).concat("|"))) {
//                    result = true;
//                }
//                result =
//                        result && "T".equalsIgnoreCase(DefaultValueHelper.getValue(relationGen.getBidSplit()));
//                if (isSupportAnticipation) {
//                    result =
//                            result
//                                    && "F"
//                                    .equalsIgnoreCase(
//                                            DefaultValueHelper.getValue(relationGen.getSupportWalletPay(), "F"));
//                }
//                result = result && hotelId > 0 && !checkInBlacklist(relationGen, hotelId);
//            }
//        } catch (Exception ex) {
//            result = false;
//        }
        return result;
    }

    private boolean configCanPush() {
//        List<CSPConfigurationGen> list = cspConfigurationDao.getList("IsPulling606Data", null);
//        return !ListHelper.isEmpty(list)
//                && list.get(0) != null
//                && "T".equals(list.get(0).getConfigValue());
        return true;
    }

}
