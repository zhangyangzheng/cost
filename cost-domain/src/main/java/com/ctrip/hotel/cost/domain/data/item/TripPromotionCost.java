package com.ctrip.hotel.cost.domain.data.item;

import com.ctrip.hotel.cost.domain.element.promotion.PromotionCostPrice;
import com.ctrip.hotel.cost.domain.scene.CostItemType;
import lombok.Data;
import org.apache.commons.collections.CollectionUtils;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author yangzhengzhang
 * @description
 * @date 2022-11-14 19:44
 */
@Data
public class TripPromotionCost implements Item<PromotionCostPrice> {

    private List<PromotionCostPrice> promotionCostPrices;
    private BigDecimal total;

    public TripPromotionCost(List<PromotionCostPrice> promotionCostPrices) {
        this.promotionCostPrices = promotionCostPrices.stream().filter(e -> !e.days().getValue().equals(BigDecimal.ZERO)).collect(Collectors.toList());
    }

    @Override
    public CostItemType costItemType() {
        return CostItemType.TRIP_PROMOTION_COST_PRICE_FG;
    }

    @Override
    public Integer level() {
        return EnumLevel.LEVEL_30.getValue();
    }

    @Override
    public BigDecimal total() {
        return this.total;
    }

    @Override
    public void countTotal() throws Exception {
        this.total = count(promotionCostPrices);
    }

    @Override
    public String formula() {
        if (CollectionUtils.isNotEmpty(promotionCostPrices)) {
            return promotionCostPrices.get(0).formula();
        }
        return null;
    }

    @Override
    public List<ItemParametersAndResultModel> parametersAndResult() {
        if (CollectionUtils.isEmpty(promotionCostPrices)) {
            return Collections.emptyList();
        }
        return promotionCostPrices.stream().map(e -> new ItemParametersAndResultModel(e.result(), e.computingParameter())).collect(Collectors.toList());
    }
}
