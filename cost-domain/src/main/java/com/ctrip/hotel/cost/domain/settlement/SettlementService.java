package com.ctrip.hotel.cost.domain.settlement;

import com.ctrip.framework.clogging.domain.thrift.LogLevel;
import com.ctrip.hotel.cost.common.ThreadLocalCostHolder;
import com.ctrip.hotel.cost.domain.data.model.AuditOrderInfoBO;
import com.ctrip.hotel.cost.repository.AuditRepository;
import hotel.settlement.common.QConfigHelper;
import hotel.settlement.common.helpers.DefaultValueHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ctrip.hotel.cost.repository.SettlementRepository;

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

    @Autowired
    private OrderMetaInterpreter orderMetaInterpreter;

    @Autowired
    private AuditRepository auditRepository;

    /**
     * 整体成功/失败
     * @param auditOrderInfoBO
     * @return
     */
    public Boolean callCancelForFg(AuditOrderInfoBO auditOrderInfoBO) {
        try {
            if (DefaultValueHelper.getValue(auditOrderInfoBO.getSettlementCallBackInfo().getOrderInfoId()) > 0) { // 前置模块已经抛单成功
                settlementRepository.callCancelOrder(auditOrderInfoBO);
            }
            if (DefaultValueHelper.getValue(auditOrderInfoBO.getSettlementCallBackInfo().getSettlementId()) > 0) {
                settlementRepository.callCancelSettlementCancelList(auditOrderInfoBO);
            }
            if (DefaultValueHelper.getValue(auditOrderInfoBO.getSettlementCallBackInfo().getHwpSettlementId()) > 0) {
                settlementRepository.callCancelSettlementCancelListHWP(auditOrderInfoBO);
            }
            auditRepository.notifyResult(auditOrderInfoBO);
        } catch (Exception e) {
            ThreadLocalCostHolder.allLinkTracingLog(e, LogLevel.ERROR);
            return false;
        }
        return true;
    }

    public Boolean callSettlementForFg(AuditOrderInfoBO auditOrderInfoBO) {
        try {
            auditOrderInfoBO = orderMetaInterpreter.resolverOrderFg(auditOrderInfoBO);
            if (isToPreprocess(auditOrderInfoBO)) {
                // 抛前置
                Long orderInfoId = settlementRepository.callSettlementPayDataReceive(auditOrderInfoBO);
                auditOrderInfoBO.getSettlementCallBackInfo().setOrderInfoId(orderInfoId);
            } else {
                // 抛结算
                if (EnumOrderOpType.CREATE.getName().equals(auditOrderInfoBO.getOrderAuditFgMqBO().getOpType())) {// 如果是“C”(新订单), 没有推送过
                    // 区分闪住和非闪住
                    if (DefaultValueHelper.getValue(auditOrderInfoBO.getFlashOrderInfo().getIsFlashOrder())
                        && DefaultValueHelper.getValue(auditOrderInfoBO.getHotelBasicInfo().getPaymentType()).equals("P")
                        && configCanPush()
                    ) {// 闪住 && 付面，只抛606
                        auditOrderInfoBO.getSettlementCallBackInfo().setPushWalletPay(true);
                        auditOrderInfoBO.setRemarks("闪住付面仅抛606");
                        Long hwpSettlementId = settlementRepository.callSettlementApplyListHWP(auditOrderInfoBO);
                        auditOrderInfoBO.getSettlementCallBackInfo().setHwpSettlementId(hwpSettlementId);
                        auditOrderInfoBO.getSettlementCallBackInfo().setHwpReferenceId(auditOrderInfoBO.getOrderAuditFgMqBO().getReferenceId());
                    } else if (DefaultValueHelper.getValue(auditOrderInfoBO.getFlashOrderInfo().getIsFlashOrder())
                            && !DefaultValueHelper.getValue(auditOrderInfoBO.getHotelBasicInfo().getPaymentType()).equals("P")
                            && configCanPush()
                    ) {// 闪住 && 付底，抛601 + 606
                        Long settlementId = settlementRepository.callSettlementApplyList(auditOrderInfoBO);
                        Long hwpSettlementId = settlementRepository.callSettlementApplyListHWP(auditOrderInfoBO);
                        auditOrderInfoBO.getSettlementCallBackInfo().setSettlementId(settlementId);
                        auditOrderInfoBO.getSettlementCallBackInfo().setHwpSettlementId(hwpSettlementId);
                        auditOrderInfoBO.getSettlementCallBackInfo().setPushReferenceId(auditOrderInfoBO.getOrderAuditFgMqBO().getReferenceId());
                        auditOrderInfoBO.getSettlementCallBackInfo().setHwpReferenceId(auditOrderInfoBO.getOrderAuditFgMqBO().getReferenceId());
                    } else {// 除了606，其他新订单都要抛一次601
                        Long settlementId = settlementRepository.callSettlementApplyList(auditOrderInfoBO);
                        auditOrderInfoBO.getSettlementCallBackInfo().setSettlementId(settlementId);
                        auditOrderInfoBO.getSettlementCallBackInfo().setPushReferenceId(auditOrderInfoBO.getOrderAuditFgMqBO().getReferenceId());
                    }
                } else if (EnumOrderOpType.UPDATE.getName().equals(auditOrderInfoBO.getOrderAuditFgMqBO().getOpType())) { // 如果是“U”(修改单)
                    // 区分闪住和非闪住
                    if (DefaultValueHelper.getValue(auditOrderInfoBO.getFlashOrderInfo().getIsFlashOrder())
                            && DefaultValueHelper.getValue(auditOrderInfoBO.getHotelBasicInfo().getPaymentType()).equals("P")
                            && configCanPush()
                    ) {// 闪住 && 付面，只抛606
                        Long hwpSettlementId = settlementRepository.callSettlementApplyListHWP(auditOrderInfoBO);
                        auditOrderInfoBO.getSettlementCallBackInfo().setHwpSettlementId(hwpSettlementId);
                        auditOrderInfoBO.getSettlementCallBackInfo().setHwpReferenceId(auditOrderInfoBO.getOrderAuditFgMqBO().getReferenceId());
                    } else if (DefaultValueHelper.getValue(auditOrderInfoBO.getFlashOrderInfo().getIsFlashOrder())
                            && !DefaultValueHelper.getValue(auditOrderInfoBO.getHotelBasicInfo().getPaymentType()).equals("P")
                            && configCanPush()
                    ) {// 闪住 && 付底，抛601 + 606
                        Long settlementId = settlementRepository.callSettlementApplyList(auditOrderInfoBO);
                        Long hwpSettlementId = settlementRepository.callSettlementApplyListHWP(auditOrderInfoBO);
                        auditOrderInfoBO.getSettlementCallBackInfo().setSettlementId(settlementId);
                        auditOrderInfoBO.getSettlementCallBackInfo().setHwpSettlementId(hwpSettlementId);
                        auditOrderInfoBO.getSettlementCallBackInfo().setPushReferenceId(auditOrderInfoBO.getOrderAuditFgMqBO().getReferenceId());
                        auditOrderInfoBO.getSettlementCallBackInfo().setHwpReferenceId(auditOrderInfoBO.getOrderAuditFgMqBO().getReferenceId());
                    } else {// 除了606，其他新订单都要抛一次601
                        Long settlementId = settlementRepository.callSettlementApplyList(auditOrderInfoBO);
                        auditOrderInfoBO.getSettlementCallBackInfo().setSettlementId(settlementId);
                        auditOrderInfoBO.getSettlementCallBackInfo().setPushReferenceId(auditOrderInfoBO.getOrderAuditFgMqBO().getReferenceId());
                    }
                }
            }
            auditRepository.notifyResult(auditOrderInfoBO);
        } catch (Exception e) {
            ThreadLocalCostHolder.allLinkTracingLog(e, LogLevel.ERROR);
            return false;
        }

        return true;
    }

    // 判断是否走前置模块抛结算，VendorID=253或766或136，云梯(直通车)金额>0，订单确认方式为I,或者orderinfoID>0
    protected boolean isToPreprocess(AuditOrderInfoBO auditOrderInfoBO) throws Exception {
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
            boolean isSupportAnticipation) throws Exception {
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
        return settlementRepository.callCheckFGBidSplit(vendorChannelID, hotelId, orderConfirmType, isSupportAnticipation);
    }

    private boolean configCanPush() throws Exception {
//        List<CSPConfigurationGen> list = cspConfigurationDao.getList("IsPulling606Data", null);
//        return !ListHelper.isEmpty(list)
//                && list.get(0) != null
//                && "T".equals(list.get(0).getConfigValue());
        return settlementRepository.callConfigCanPush();
    }

}
