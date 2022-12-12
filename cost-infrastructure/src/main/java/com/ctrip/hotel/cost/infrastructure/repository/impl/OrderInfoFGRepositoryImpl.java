package com.ctrip.hotel.cost.infrastructure.repository.impl;

import com.ctrip.framework.clogging.domain.thrift.LogLevel;
import com.ctrip.hotel.cost.common.EnumLogTag;
import com.ctrip.hotel.cost.common.ThreadLocalCostHolder;
import com.ctrip.hotel.cost.domain.data.DataCenter;
import com.ctrip.hotel.cost.domain.data.OrderInfoFGRepository;
import com.ctrip.hotel.cost.domain.data.model.AuditOrderInfoBO;
import com.ctrip.hotel.cost.domain.element.bid.BidPriceFgOrderInfo;
import com.ctrip.hotel.cost.domain.element.commission.AdjustCommissionPriceOrderInfo;
import com.ctrip.hotel.cost.domain.element.price.PriceAmountFgInfo;
import com.ctrip.hotel.cost.domain.element.price.PriceCostFgInfo;
import com.ctrip.hotel.cost.domain.element.promotion.*;
import com.ctrip.hotel.cost.domain.element.promotion.cashBack.PromotionCostCashBackPriceFgOrderInfo;
import com.ctrip.hotel.cost.domain.element.promotion.cashBack.PromotionSellingCashBackPriceFgOrderInfo;
import com.ctrip.hotel.cost.domain.element.room.fg.RoomCostPriceFgOrderInfo;
import com.ctrip.hotel.cost.domain.element.room.fg.RoomSellingPriceFgOrderInfo;
import com.ctrip.hotel.cost.domain.element.techfee.ZeroCommissionFeePriceOrderInfo;
import com.ctrip.hotel.cost.infrastructure.client.AuditClient;
import com.ctrip.hotel.cost.infrastructure.mapper.OrderAuditRoomDataPOMapper;
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
     * 数据项可以做成类型扩展
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
                    promotionPriceBuild(order.getPromotionDailyInfoList(), order.getAuditRoomInfoList().get(0).getAuditRoomBasicInfo(), order.getOrderBasicInfo())
            );
            dataCenter.setTripPromotionSellingPriceFgOrderInfos(
                    promotionTripPriceBuild(order.getPromotionDailyInfoList(), order.getAuditRoomInfoList().get(0).getAuditRoomBasicInfo(), order.getOrderBasicInfo())
            );
            dataCenter.setPromotionSellingCashBackPriceFgOrderInfos(
                    promotionCashBackPriceBuild(order.getPromotionDailyInfoList(), order.getAuditRoomInfoList().get(0).getAuditRoomBasicInfo(), order.getOrderBasicInfo())
            );
            dataCenter.setPromotionCostPriceFgOrderInfos(
                    promotionCostBuild(order.getPromotionDailyInfoList(), order.getAuditRoomInfoList().get(0).getAuditRoomBasicInfo(), order.getOrderBasicInfo())
            );
            dataCenter.setTripPromotionCostPriceFgOrderInfos(
                    promotionTripCostBuild(order.getPromotionDailyInfoList(), order.getAuditRoomInfoList().get(0).getAuditRoomBasicInfo(), order.getOrderBasicInfo())
            );
            dataCenter.setBuyoutDiscountPromotionCostPriceFgOrderInfos(
                    promotionBuyoutDiscountPriceBuild(order.getPromotionDailyInfoList(), order.getAuditRoomInfoList().get(0).getAuditRoomBasicInfo(), order.getOrderBasicInfo())
            );
            dataCenter.setPromotionCostCashBackPriceFgOrderInfos(
                    promotionCashBackCostBuild(order.getPromotionDailyInfoList(), order.getAuditRoomInfoList().get(0).getAuditRoomBasicInfo(), order.getOrderBasicInfo())
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
            ThreadLocalCostHolder.allLinkTracingLog(e, LogLevel.ERROR);
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
                .filter(Objects::nonNull)
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

    private List<PromotionSellingPriceFgOrderInfo> promotionPriceBuild(List<PromotionDailyInfo> promotionDailyInfoList, AuditRoomBasicInfo auditRoomInfo, OrderBasicInfo orderBasicInfo) {
        if (CollectionUtils.isEmpty(promotionDailyInfoList)) {
            return Collections.emptyList();
        }
        return promotionDailyInfoList
                .stream()
                .filter(Objects::nonNull)
                .map(promotion -> OrderAuditRoomDataPOMapper.INSTANCE.auditOrderToPromotion(promotion, auditRoomInfo, orderBasicInfo))
                .collect(Collectors.toList());
    }
    private List<PromotionCostPriceFgOrderInfo> promotionCostBuild(List<PromotionDailyInfo> promotionDailyInfoList, AuditRoomBasicInfo auditRoomInfo, OrderBasicInfo orderBasicInfo) {
        if (CollectionUtils.isEmpty(promotionDailyInfoList)) {
            return Collections.emptyList();
        }
        return promotionDailyInfoList
                .stream()
                .filter(Objects::nonNull)
                .map(promotion -> OrderAuditRoomDataPOMapper.INSTANCE.auditOrderToPromotionCost(promotion, auditRoomInfo, orderBasicInfo))
                .collect(Collectors.toList());
    }
    private List<TripPromotionSellingPriceFgOrderInfo> promotionTripPriceBuild(List<PromotionDailyInfo> promotionDailyInfoList, AuditRoomBasicInfo auditRoomInfo, OrderBasicInfo orderBasicInfo) {
        if (CollectionUtils.isEmpty(promotionDailyInfoList)) {
            return Collections.emptyList();
        }
        return promotionDailyInfoList
                .stream()
                .filter(Objects::nonNull)
                .map(promotion -> OrderAuditRoomDataPOMapper.INSTANCE.auditOrderToTripPromotion(promotion, auditRoomInfo, orderBasicInfo))
                .collect(Collectors.toList());
    }
    private List<TripPromotionCostPriceFgOrderInfo> promotionTripCostBuild(List<PromotionDailyInfo> promotionDailyInfoList, AuditRoomBasicInfo auditRoomInfo, OrderBasicInfo orderBasicInfo) {
        if (CollectionUtils.isEmpty(promotionDailyInfoList)) {
            return Collections.emptyList();
        }
        return promotionDailyInfoList
                .stream()
                .filter(Objects::nonNull)
                .map(promotion -> OrderAuditRoomDataPOMapper.INSTANCE.auditOrderToTripPromotionCost(promotion, auditRoomInfo, orderBasicInfo))
                .collect(Collectors.toList());
    }
    private List<PromotionCostCashBackPriceFgOrderInfo> promotionCashBackCostBuild(List<PromotionDailyInfo> promotionDailyInfoList, AuditRoomBasicInfo auditRoomInfo, OrderBasicInfo orderBasicInfo) {
        if (CollectionUtils.isEmpty(promotionDailyInfoList)) {
            return Collections.emptyList();
        }
        return promotionDailyInfoList
                .stream()
                .filter(Objects::nonNull)
                .map(promotion -> OrderAuditRoomDataPOMapper.INSTANCE.auditOrderToCashBackPromotionCost(promotion, auditRoomInfo, orderBasicInfo))
                .collect(Collectors.toList());
    }
    private List<PromotionSellingCashBackPriceFgOrderInfo> promotionCashBackPriceBuild(List<PromotionDailyInfo> promotionDailyInfoList, AuditRoomBasicInfo auditRoomInfo, OrderBasicInfo orderBasicInfo) {
        if (CollectionUtils.isEmpty(promotionDailyInfoList)) {
            return Collections.emptyList();
        }
        return promotionDailyInfoList
                .stream()
                .filter(Objects::nonNull)
                .map(promotion -> OrderAuditRoomDataPOMapper.INSTANCE.auditOrderToCashBackPromotion(promotion, auditRoomInfo, orderBasicInfo))
                .collect(Collectors.toList());
    }
    private List<PromotionCostBuyoutDiscountPriceFgOrderInfo> promotionBuyoutDiscountPriceBuild(List<PromotionDailyInfo> promotionDailyInfoList, AuditRoomBasicInfo auditRoomInfo, OrderBasicInfo orderBasicInfo) {
        if (CollectionUtils.isEmpty(promotionDailyInfoList)) {
            return Collections.emptyList();
        }
        return promotionDailyInfoList
                .stream()
                .filter(Objects::nonNull)
                .map(promotion -> OrderAuditRoomDataPOMapper.INSTANCE.auditOrderToBuyoutDiscountPromotion(promotion, auditRoomInfo, orderBasicInfo))
                .collect(Collectors.toList());
    }

    private AdjustCommissionPriceOrderInfo adjustCommissionBuild(AuditRoomOtherInfo auditRoomOtherInfo, HotelBasicInfo hotelBasicInfo) {
        if (hotelBasicInfo != null
                && hotelBasicInfo.getOperatMode() != null
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

    // 增加检查项
    private Boolean orderCheckPass(OrderAuditRoomData order) {
        return !orderCheckFail(order);
    }

    private Boolean orderCheckFail(OrderAuditRoomData order) {
        boolean isFail = order == null
                || order.getOrderId() == null
                || order.getCusOrderId() == null
                || order.getOrderBasicInfo() == null
                || order.getOrderBasicInfo().getEta() == null
                || order.getOrderBasicInfo().getHourRoom() == null
                || CollectionUtils.isEmpty(order.getAuditRoomInfoList())
                || auditRoomCheckFail(order.getAuditRoomInfoList())
                || order.getHotelBasicInfo() == null;
        if (isFail) {
            StringBuilder stringBuilder = new StringBuilder("");
            stringBuilder.append("getOrderAuditRoomData client error data is unpass, possible:\n");
            stringBuilder.append("order is null\n");
            stringBuilder.append("order.cusOrderId is null\n");
            stringBuilder.append("order.orderBasicInfo is null\n");
            stringBuilder.append("order.orderBasicInfo.eta is null\n");
            stringBuilder.append("order.orderBasicInfo.hourRoom is null\n");
            stringBuilder.append("order.auditRoomInfoList is null\n");
            stringBuilder.append("order.auditRoomInfoList.auditRoomBasicInfo is null\n");
            stringBuilder.append("order.auditRoomInfoList.auditRoomBasicInfo.realETD is null\n");
            stringBuilder.append("order.auditRoomInfoList.auditRoomBasicInfo.fgid is null\n");
            stringBuilder.append("order.hotelBasicInfo is null\n");
            if (order != null && order.getOrderId() != null) {
                ThreadLocalCostHolder.getTTL().get().getTags().put(EnumLogTag.ORDER_ID.getValue(), order.getOrderId().toString());
            }
            ThreadLocalCostHolder.allLinkTracingLog(stringBuilder.toString(), LogLevel.ERROR);
        }
        return isFail;
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
