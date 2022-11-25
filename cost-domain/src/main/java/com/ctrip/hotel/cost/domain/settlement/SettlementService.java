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
        CancelOrderUsedBo cancelOrderUsedBo = new CancelOrderUsedBo();
        cancelOrderUsedBo.setOrderchannel(EnumHotelorderchannel.hfg);
        cancelOrderUsedBo.setOrderid(String.valueOf(orderId));
        cancelOrderUsedBo.setFGID(fgId);
        return settlementRepository.callCancelOrder(cancelOrderUsedBo);
    }

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

}
