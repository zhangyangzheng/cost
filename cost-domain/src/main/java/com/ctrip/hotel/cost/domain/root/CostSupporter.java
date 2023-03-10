package com.ctrip.hotel.cost.domain.root;

import com.ctrip.hotel.cost.domain.data.DataCenter;
import com.ctrip.hotel.cost.domain.data.model.AuditOrderInfoBO;
import com.ctrip.hotel.cost.domain.element.promotion.PromotionCostPrice;
import com.ctrip.hotel.cost.domain.element.promotion.PromotionSellingPrice;
import com.ctrip.hotel.cost.domain.scene.Scene;
import com.ctrip.hotel.cost.domain.scene.SceneFactory;
import hotel.settlement.common.DateHelper;
import org.apache.commons.collections.CollectionUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author yangzhengzhang
 * @description
 * @date 2022-10-30 23:56
 */
public class CostSupporter {

    private static final SceneFactory sceneFactory = new SceneFactory();
    private CostContext costContext;

    public CostSupporter(Integer sceneCode, List<Long> dataIds) {
        createCostContext(sceneCode, dataIds);
    }

    /**
     * 创建计费上下文
     */
    private void createCostContext(Integer sceneCode, List<Long> dataIds) {
        Scene scene = sceneFactory.getScene(sceneCode);
        costContext = new CostContext(scene, dataIds);
    }

    /**
     * 处理计费
     */
    public void innerCostWorker() {
        costContext.computeCostItem();
    }

    /**
     * 封装[fg audit order cost success]需要的返回值
     * @return
     */
    public List<AuditOrderInfoBO> getFgAuditCostSuccessResult() {
        if (CollectionUtils.isEmpty(costContext.getDataCenters())) {
            return Collections.emptyList();
        }

        return costContext.getDataCenters().stream().filter(DataCenter::getSuccess).map(e -> {
            // 结算只要满足条件的“酒店承担”促销（非0（底价或卖价）的数据才有效）
            Set<Long> promotionIDs = e.getPromotionSelling().getPromotionSellingPrices().stream()
                    .filter(promotionSellingPrice -> promotionSellingPrice.result().compareTo(BigDecimal.ZERO) > 0)
                    .map(PromotionSellingPrice::promotionDailyInfoID)
                    .collect(Collectors.toSet());
            Set<Long> promotionCostIDs = e.getPromotionCost().getPromotionCostPrices().stream()
                    .filter(promotionCostPrice -> promotionCostPrice.result().compareTo(BigDecimal.ZERO) > 0)
                    .map(PromotionCostPrice::promotionDailyInfoID)
                    .collect(Collectors.toSet());
            promotionIDs.addAll(promotionCostIDs);
            e.getAuditOrderInfoBO().setPromotionDailyInfoList(
                    e.getAuditOrderInfoBO().getPromotionDailyInfoList().stream().filter(p -> promotionIDs.contains(p.getPromotionDailyInfoID())).collect(Collectors.toList())
            );
            Map<Long, PromotionSellingPrice> promotionSellingMap = e.getPromotionSelling().getPromotionSellingPrices().stream().collect(Collectors.toMap(PromotionSellingPrice::promotionDailyInfoID, promotionSellingPrice -> promotionSellingPrice, (key1, key2) -> key2));
            Map<Long, PromotionCostPrice> promotionCostMap = e.getPromotionCost().getPromotionCostPrices().stream().collect(Collectors.toMap(PromotionCostPrice::promotionDailyInfoID, promotionCostPrice -> promotionCostPrice, (k1, k2) -> k2));
            e.getAuditOrderInfoBO().getPromotionDailyInfoList().forEach(promotionDailyInfo -> {
                promotionDailyInfo.setAmount(promotionSellingMap.get(promotionDailyInfo.getPromotionDailyInfoID()).result());
                promotionDailyInfo.setCostDiscountAmount(promotionCostMap.get(promotionDailyInfo.getPromotionDailyInfoID()).result());
            });

            e.getAuditOrderInfoBO().setAdjustAmount(e.getAdjustCommission() == null ? null : e.getAdjustCommission().total());
            e.getAuditOrderInfoBO().setPriceAmount(e.getPriceAmountFg() == null ? null : e.getPriceAmountFg().total());
            e.getAuditOrderInfoBO().setCostAmount(e.getPriceCostFg() == null ? null : e.getPriceCostFg().total());
            e.getAuditOrderInfoBO().setBidPrice(e.getBid() == null ? null : e.getBid().total());
            e.getAuditOrderInfoBO().setRoomAmount(e.getRoomSelling() == null ? null : e.getRoomSelling().total());
            e.getAuditOrderInfoBO().setRoomCost(e.getRoomCost() == null ? null : e.getRoomCost().total());
            e.getAuditOrderInfoBO().setZeroCommissionAmount(e.getZeroCommissionFee() == null ? null : e.getZeroCommissionFee().total());
            e.getAuditOrderInfoBO().setTripPromotionAmount(e.getTripPromotionSelling() == null ? null : e.getTripPromotionSelling().total());
            e.getAuditOrderInfoBO().setTripPromotionCost(e.getTripPromotionCost() == null ? null : e.getTripPromotionCost().total());
            e.getAuditOrderInfoBO().setHotelPromotionAmount(e.getPromotionSelling() == null ? null : e.getPromotionSelling().total());
            e.getAuditOrderInfoBO().setHotelPromotionCost(e.getPromotionCost() == null ? null : e.getPromotionCost().total());
            e.getAuditOrderInfoBO().setBuyoutDiscountAmount(e.getPromotionCostBuyoutDiscount() == null ? null : e.getPromotionCostBuyoutDiscount().total());
            e.getAuditOrderInfoBO().setPromotionCashBackAmount(e.getPromotionSellingCashBack() == null ? null : e.getPromotionSellingCashBack().total());
            e.getAuditOrderInfoBO().setPromotionCashBackCost(e.getPromotionCostCashBack() == null ? null : e.getPromotionCostCashBack().total());
            e.getAuditOrderInfoBO().setQuantity(
                    BigDecimal.valueOf(
                            DateHelper.getDiffDays(
                                    e.getAuditOrderInfoBO().getAuditRoomInfoList().get(0).getAuditRoomBasicInfo().getRealETD(),
                                    e.getAuditOrderInfoBO().getAuditRoomInfoList().get(0).getAuditRoomBasicInfo().getEta())
                    ).add(
                            (
                                    e.getAuditOrderInfoBO().getAuditRoomInfoList().get(0).getAuditRoomBasicInfo().getHourAdjuest() == null
                                    ? BigDecimal.ZERO
                                    : BigDecimal.valueOf(e.getAuditOrderInfoBO().getAuditRoomInfoList().get(0).getAuditRoomBasicInfo().getHourAdjuest())
                            ).divide(new BigDecimal("24"), 4, RoundingMode.HALF_UP)
                        )
                    );
            return e.getAuditOrderInfoBO();
        }).collect(Collectors.toList());
    }

    /**
     * 封装[fg audit order]的返回值
     * @return
     */
    public List<AuditOrderInfoBO> getFgAuditResult() {
        if (CollectionUtils.isEmpty(costContext.getDataCenters())) {
            return Collections.emptyList();
        }
        return costContext.getDataCenters().stream().map(DataCenter::getAuditOrderInfoBO).collect(Collectors.toList());
    }

    public List<Long> getCostSuccessData() {
        if (CollectionUtils.isEmpty(costContext.getDataCenters())) {
            return Collections.emptyList();
        }
        return costContext.getDataCenters().stream().filter(DataCenter::getSuccess).map(DataCenter::getDataId).collect(Collectors.toList());
    }

}
