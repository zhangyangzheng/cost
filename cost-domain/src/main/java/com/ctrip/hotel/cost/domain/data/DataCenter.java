package com.ctrip.hotel.cost.domain.data;

import com.alibaba.fastjson.JSON;
import com.ctrip.framework.clogging.domain.thrift.LogLevel;
import com.ctrip.hotel.cost.common.ThreadLocalCostHolder;
import com.ctrip.hotel.cost.domain.core.MeasurementCenter;
import com.ctrip.hotel.cost.domain.data.item.*;
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
import com.ctrip.hotel.cost.domain.scene.CostItemType;
import lombok.Data;
import org.apache.commons.collections.CollectionUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author yangzhengzhang
 * @description
 * @date 2022-10-31 19:37
 */
@Data
public class  DataCenter {

    private Long dataId;
    private AuditOrderInfoBO auditOrderInfoBO;// [fg audit order]审核的数据体

    private Map<String, BigDecimal> costCollector = new HashMap<>();
    private List<Item<? extends MeasurementCenter>> itemCollector = new ArrayList<>();

    private Bid bid;
    private PromotionCost promotionCost;
    private TripPromotionCost tripPromotionCost;
    private PromotionCostBuyoutDiscount promotionCostBuyoutDiscount;
    private PromotionCostCashBack promotionCostCashBack;
    private PromotionSelling promotionSelling;
    private TripPromotionSelling tripPromotionSelling;
    private PromotionSellingCashBack promotionSellingCashBack;
    private RoomCost roomCost;
    private RoomSelling roomSelling;
    private PriceAmountFg priceAmountFg;
    private PriceCostFg priceCostFg;
    private AdjustCommission adjustCommission;
    private ZeroCommissionFee zeroCommissionFee;

    private Boolean success = false;

    private void formulaLog(Item<? extends MeasurementCenter> item) {
        if (item.formula() == null) {
            return;
        }
        StringBuilder stringLog = new StringBuilder("");
        stringLog.append(item.costItemType().getCostItemName());
        stringLog.append("=");
        stringLog.append(item.total().toString());
        stringLog.append("\n");
        stringLog.append(item.costItemType().getCostItemName());
        stringLog.append("=");
        stringLog.append(item.formula());
        stringLog.append("\n");
        stringLog.append(JSON.toJSONString(item.parametersAndResult()));
        ThreadLocalCostHolder.allLinkTracingLog(stringLog.toString(), LogLevel.INFO);
    }

    /**
     * 数据初始化后，开始单项计费
     * @throws Exception
     */
    public void compute(List<CostItemType> itemTypes) throws Exception {
        this.itemCollector.sort((p1, p2) -> p2.level().compareTo(p1.level()));// 倒排
        for (Item item : this.itemCollector) {
            if (itemTypes.contains(item.costItemType())) {// 按需计费
                item.countTotal();
                // 费用收集器，收集单项费用结果
                this.costCollector.put(item.costItemType().getCostItemName(), item.total());
                formulaLog(item);
            }
        }
    }

    // set 计费项初始化
    public void setBidPriceFgOrderInfos(List<BidPriceFgOrderInfo> bidPriceFgOrderInfos) throws Exception {
        Bid b = new Bid(new ArrayList<>(bidPriceFgOrderInfos));
        this.setBid(b);
        itemCollector.add(b);
    }

    public void setRoomSellingPriceFgOrderInfos(List<RoomSellingPriceFgOrderInfo> roomSellingPriceFgOrderInfos) throws Exception {
        RoomSelling rS = new RoomSelling(new ArrayList<>(roomSellingPriceFgOrderInfos));
        this.setRoomSelling(rS);
        itemCollector.add(rS);
    }

    public void setRoomCostPriceFgOrderInfos(List<RoomCostPriceFgOrderInfo> roomCostPriceFgOrderInfos) throws Exception {
        RoomCost rC = new RoomCost(new ArrayList<>(roomCostPriceFgOrderInfos));
        this.setRoomCost(rC);
        itemCollector.add(rC);
    }

    public void setPromotionSellingPriceFgOrderInfos(List<PromotionSellingPriceFgOrderInfo> promotionSellingPriceFgOrderInfos) throws Exception {
        PromotionSelling pS = new PromotionSelling(new ArrayList<>(promotionSellingPriceFgOrderInfos));
        this.setPromotionSelling(pS);
        itemCollector.add(pS);
    }
    public void setTripPromotionSellingPriceFgOrderInfos(List<TripPromotionSellingPriceFgOrderInfo> promotions) throws Exception {
        TripPromotionSelling pS = new TripPromotionSelling(new ArrayList<>(promotions));
        this.setTripPromotionSelling(pS);
        itemCollector.add(pS);
    }
    public void setPromotionSellingCashBackPriceFgOrderInfos(List<PromotionSellingCashBackPriceFgOrderInfo> promotions) throws Exception {
        PromotionSellingCashBack pS = new PromotionSellingCashBack(new ArrayList<>(promotions));
        this.setPromotionSellingCashBack(pS);
        itemCollector.add(pS);
    }
    public void setPromotionCostPriceFgOrderInfos(List<PromotionCostPriceFgOrderInfo> promotionCostPriceFgOrderInfos) throws Exception {
        PromotionCost pC = new PromotionCost(new ArrayList<>(promotionCostPriceFgOrderInfos));
        this.setPromotionCost(pC);
        itemCollector.add(pC);
    }
    public void setTripPromotionCostPriceFgOrderInfos(List<TripPromotionCostPriceFgOrderInfo> promotions) throws Exception {
        TripPromotionCost tPC = new TripPromotionCost(new ArrayList<>(promotions));
        this.setTripPromotionCost(tPC);
        itemCollector.add(tPC);
    }
    public void setBuyoutDiscountPromotionCostPriceFgOrderInfos(List<PromotionCostBuyoutDiscountPriceFgOrderInfo> promotions) throws Exception {
        PromotionCostBuyoutDiscount pC = new PromotionCostBuyoutDiscount(new ArrayList<>(promotions));
        this.setPromotionCostBuyoutDiscount(pC);
        itemCollector.add(pC);
    }
    public void setPromotionCostCashBackPriceFgOrderInfos(List<PromotionCostCashBackPriceFgOrderInfo> promotions) throws Exception {
        PromotionCostCashBack pC = new PromotionCostCashBack(new ArrayList<>(promotions));
        this.setPromotionCostCashBack(pC);
        itemCollector.add(pC);
    }

    public void setPriceAmountFgInfo(PriceAmountFgInfo priceAmountFgInfo) {
        priceAmountFgInfo.setSupplier(this::getCostCollector);
        PriceAmountFg pA = new PriceAmountFg(priceAmountFgInfo);
        this.setPriceAmountFg(pA);
        itemCollector.add(pA);
    }

    public void setPriceCostFgInfo(PriceCostFgInfo priceCostFgInfo) {
        priceCostFgInfo.setSupplier(this::getCostCollector);
        PriceCostFg pC = new PriceCostFg(priceCostFgInfo);
        this.setPriceCostFg(pC);
        itemCollector.add(pC);
    }

    public void setAdjustCommissionPriceOrderInfo(AdjustCommissionPriceOrderInfo adjustCommissionPriceOrderInfo) {
        if (adjustCommissionPriceOrderInfo != null) {
            adjustCommissionPriceOrderInfo.setSupplier(this::getCostCollector);
        }
        AdjustCommission aC = new AdjustCommission(adjustCommissionPriceOrderInfo);
        this.setAdjustCommission(aC);
        itemCollector.add(aC);
    }
    public void setZeroCommissionFeePriceOrderInfo(ZeroCommissionFeePriceOrderInfo zeroCommissionFeePriceOrderInfo) {
        if (zeroCommissionFeePriceOrderInfo != null) {
            zeroCommissionFeePriceOrderInfo.setSupplier(this::getCostCollector);
        }
        ZeroCommissionFee zC = new ZeroCommissionFee(zeroCommissionFeePriceOrderInfo);
        this.setZeroCommissionFee(zC);
        itemCollector.add(zC);
    }
    // set 计费项初始化end

}
