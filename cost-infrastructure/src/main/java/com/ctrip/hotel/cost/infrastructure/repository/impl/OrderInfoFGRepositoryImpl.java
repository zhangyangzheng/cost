package com.ctrip.hotel.cost.infrastructure.repository.impl;

import com.ctrip.hotel.cost.domain.data.DataCenter;
import com.ctrip.hotel.cost.domain.data.OrderInfoFGRepository;
import com.ctrip.hotel.cost.domain.data.model.AuditOrderInfoBO;
import com.ctrip.hotel.cost.domain.element.bid.BidPriceFgOrderInfo;
import com.ctrip.hotel.cost.domain.element.commission.AdjustCommissionPriceOrderInfo;
import com.ctrip.hotel.cost.domain.element.price.PriceAmountFgInfo;
import com.ctrip.hotel.cost.domain.element.price.PriceCostFgInfo;
import com.ctrip.hotel.cost.domain.element.promotion.PromotionCostPriceFgOrderInfo;
import com.ctrip.hotel.cost.domain.element.promotion.PromotionSellingPriceFgOrderInfo;
import com.ctrip.hotel.cost.domain.element.room.fg.RoomCostPriceFgOrderInfo;
import com.ctrip.hotel.cost.domain.element.room.fg.RoomSellingPriceFgOrderInfo;
import com.ctrip.hotel.cost.domain.element.techfee.ZeroCommissionFeePriceOrderInfo;
import com.ctrip.hotel.cost.infrastructure.client.AuditClient;
import com.ctrip.hotel.cost.infrastructure.mapper.OrderAuditRoomDataPOMapper;
import hotel.settlement.common.LogHelper;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import soa.ctrip.com.hotel.order.checkin.audit.v2.getOrderAuditRoomData.*;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author yangzhengzhang
 * @description todo: build detail description for check unpass order ?
 * @date 2022-10-26 17:06
 */
@Repository
public class OrderInfoFGRepositoryImpl implements OrderInfoFGRepository {

    @Autowired
    private AuditClient auditClient;

    private List<OrderAuditRoomData> getOrders(List<Long> dataIds) {
        List<OrderAuditRoomData> auditRoomOrders = auditClient.getOrderAuditRoomDataByFgId(dataIds);
        // todo insert table ORDER_INFO_FG, 写入失败捕获异常，不要影响计费主流程，如果失败，可以通过接口日志查询
        return auditRoomOrders;
    }

    /**
     * todo 数据项需要支持扩展
     *
     * @param dataIds
     * @return
     */
    @Override
    public List<DataCenter> initData(List<Long> dataIds) {
        List<OrderAuditRoomData> orders = getOrders(dataIds).stream().filter(this::orderCheckPass).collect(Collectors.toList());
        return orders.stream().map(this::dataCenterBuild).collect(Collectors.toList())
                .stream().filter(Objects::nonNull).collect(Collectors.toList());
    }

    private DataCenter dataCenterBuild(OrderAuditRoomData order) {
        DataCenter dataCenter = new DataCenter();
        try {
            dataCenter.setDataId(order.getAuditRoomInfoList().get(0).getAuditRoomBasicInfo().getFgid().longValue());
            dataCenter.setAuditOrderInfoBO(
                    auditOrderInfoBOBuild(order)
            );
            dataCenter.setBidPriceFgOrderInfos(
                    bidBuild(order.getBidOrderInfoList(), order.getAuditRoomInfoList().get(0).getAuditRoomBasicInfo())// 审核离店：房间维度,只有get(0)
            );
            dataCenter.setRoomSellingPriceFgOrderInfos(
                    roomPriceBuild(order.getOrderPriceInfoList(), order.getAuditRoomInfoList().get(0).getAuditRoomBasicInfo())
            );
            dataCenter.setRoomCostPriceFgOrderInfos(
                    roomCostBuild(order.getOrderPriceInfoList(), order.getAuditRoomInfoList().get(0).getAuditRoomBasicInfo())
            );
            dataCenter.setPromotionSellingPriceFgOrderInfos(
                    promotionPriceBuild(order.getPromotionDailyInfoList(), order.getAuditRoomInfoList().get(0).getAuditRoomBasicInfo())
            );
            dataCenter.setTripPromotionSellingPriceFgOrderInfos(
                    promotionPriceBuild(order.getPromotionDailyInfoList(), order.getAuditRoomInfoList().get(0).getAuditRoomBasicInfo())
            );
            dataCenter.setPromotionCostPriceFgOrderInfos(
                    promotionCostBuild(order.getPromotionDailyInfoList(), order.getAuditRoomInfoList().get(0).getAuditRoomBasicInfo())
            );
            dataCenter.setBuyoutDiscountPromotionCostPriceFgOrderInfos(
                    promotionCostBuild(order.getPromotionDailyInfoList(), order.getAuditRoomInfoList().get(0).getAuditRoomBasicInfo())
            );
            dataCenter.setPriceAmountFgInfo(new PriceAmountFgInfo());
            dataCenter.setPriceCostFgInfo(new PriceCostFgInfo());
            dataCenter.setAdjustCommissionPriceOrderInfo(
                    adjustCommissionBuild(order.getAuditRoomInfoList().get(0).getAuditRoomOtherInfo(), order.getHotelBasicInfo())
            );
            dataCenter.setZeroCommissionFeePriceOrderInfo(
                    zeroCommissionFeeBuild(order.getTechFeeInfo())
            );
            // 增加数据项

        } catch (Exception e) {
            LogHelper.logError(this.getClass().getSimpleName(), e);// todo 优化日志，这里的异常属于数据检查异常
            return null;
        }
        return dataCenter;
    }

    private AuditOrderInfoBO auditOrderInfoBOBuild(OrderAuditRoomData order) {
        return OrderAuditRoomDataPOMapper.INSTANCE.auditOrderToBO(order);
    }

    /**
     * BidPayType，0表示后付，1表示先付。现付获取直通车金额明细时，不获取BidPayType=1的数据
     *
     * @param bidOrderInfoList
     * @param auditRoomInfo
     * @return
     */
    private List<BidPriceFgOrderInfo> bidBuild(List<BidOrderInfo> bidOrderInfoList, AuditRoomBasicInfo auditRoomInfo) {
        if (CollectionUtils.isEmpty(bidOrderInfoList)) {
            return Collections.emptyList();
        }
        return bidOrderInfoList
                .stream()
                .filter(bid -> Objects.nonNull(bid) && bid.getBidPayType() != 1) // BidPayType must not null
                .map(bid -> OrderAuditRoomDataPOMapper.INSTANCE.auditOrderToBid(bid, auditRoomInfo))
                .collect(Collectors.toList());
    }

    private List<RoomSellingPriceFgOrderInfo> roomPriceBuild(List<OrderPriceInfo> orderPriceInfoList, AuditRoomBasicInfo auditRoomInfo) {
        if (CollectionUtils.isEmpty(orderPriceInfoList)) {
            return Collections.emptyList();
        }
        return orderPriceInfoList
                .stream()
                .filter(Objects::nonNull)
                .map(orderPrice -> OrderAuditRoomDataPOMapper.INSTANCE.auditOrderToRoomPrice(orderPrice, auditRoomInfo))
                .collect(Collectors.toList());
    }

    private List<RoomCostPriceFgOrderInfo> roomCostBuild(List<OrderPriceInfo> orderPriceInfoList, AuditRoomBasicInfo auditRoomInfo) {
        if (CollectionUtils.isEmpty(orderPriceInfoList)) {
            return Collections.emptyList();
        }
        return orderPriceInfoList
                .stream()
                .filter(Objects::nonNull)
                .map(orderPrice -> OrderAuditRoomDataPOMapper.INSTANCE.auditOrderToRoomCost(orderPrice, auditRoomInfo))
                .collect(Collectors.toList());
    }

    private List<PromotionSellingPriceFgOrderInfo> promotionPriceBuild(List<PromotionDailyInfo> promotionDailyInfoList, AuditRoomBasicInfo auditRoomInfo) {
        if (CollectionUtils.isEmpty(promotionDailyInfoList)) {
            return Collections.emptyList();
        }
        return promotionDailyInfoList
                .stream()
                .filter(Objects::nonNull)
                .map(promotion -> OrderAuditRoomDataPOMapper.INSTANCE.auditOrderToPromotion(promotion, auditRoomInfo))
                .collect(Collectors.toList());
    }

    private List<PromotionCostPriceFgOrderInfo> promotionCostBuild(List<PromotionDailyInfo> promotionDailyInfoList, AuditRoomBasicInfo auditRoomInfo) {
        if (CollectionUtils.isEmpty(promotionDailyInfoList)) {
            return Collections.emptyList();
        }
        return promotionDailyInfoList
                .stream()
                .filter(Objects::nonNull)
                .map(promotion -> OrderAuditRoomDataPOMapper.INSTANCE.auditOrderToPromotionCost(promotion, auditRoomInfo))
                .collect(Collectors.toList());
    }

    private AdjustCommissionPriceOrderInfo adjustCommissionBuild(AuditRoomOtherInfo auditRoomOtherInfo, HotelBasicInfo hotelBasicInfo) {
        if (hotelBasicInfo != null
                && hotelBasicInfo.getOperatMode() != null
                && hotelBasicInfo.getOperatMode().equals("S") // 闪结，如果有调整服务费，计算调整服务费差额
                && auditRoomOtherInfo != null
                && auditRoomOtherInfo.getAdjustCommission() != null
        ) {
            return OrderAuditRoomDataPOMapper.INSTANCE.auditOrderToAdjustCommission(auditRoomOtherInfo, hotelBasicInfo);
        }
        return null;
    }
    private ZeroCommissionFeePriceOrderInfo zeroCommissionFeeBuild(TechFeeInfo techFeeInfo) {
        if (techFeeInfo != null && techFeeInfo.getZeroCommissionFeeRatio() != null) {
            return OrderAuditRoomDataPOMapper.INSTANCE.auditOrderToZeroCommissionFee(techFeeInfo);
        }
        return null;
    }

    // todo 增加检查项
    private Boolean orderCheckPass(OrderAuditRoomData order) {
        return !orderCheckFail(order);
    }

    private Boolean orderCheckFail(OrderAuditRoomData order) {
        return order == null
                || order.getOrderId() == null
                || order.getCusOrderId() == null
                || order.getOrderBasicInfo() == null
                || order.getOrderBasicInfo().getEta() == null
                || order.getOrderBasicInfo().getHourRoom() == null
                || CollectionUtils.isEmpty(order.getAuditRoomInfoList())
                || auditRoomCheckFail(order.getAuditRoomInfoList())
                || order.getHotelBasicInfo() == null
//                || order.getHotelBasicInfo().getOperatMode() == null
                ;
    }

    private Boolean auditRoomCheckFail(List<AuditRoomInfo> rooms) {
        for (AuditRoomInfo room : rooms) {
            if (room == null
                    || room.getAuditRoomBasicInfo() == null
                    || room.getAuditRoomBasicInfo().getRealETD() == null
                    || room.getAuditRoomBasicInfo().getFgid() == null
            ) {
                return true;
            }
        }
        return false;
    }

}
