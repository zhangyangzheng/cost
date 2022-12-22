package com.ctrip.hotel.cost.domain.data.item;

import com.ctrip.hotel.cost.domain.element.promotion.PromotionSellingPrice;
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
public class PromotionSelling implements Item<PromotionSellingPrice> {

    private List<PromotionSellingPrice> promotionSellingPrices;
    private BigDecimal total;

    public PromotionSelling(List<PromotionSellingPrice> promotionSellingPrices) {
        this.promotionSellingPrices = promotionSellingPrices.stream().filter(e -> !e.days().getValue().equals(BigDecimal.ZERO)).collect(Collectors.toList());
    }

    @Override
    public CostItemType costItemType() {
        return CostItemType.PROMOTION_SELLING_PRICE_FG;
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
        this.total = count(promotionSellingPrices);
    }

    @Override
    public String formula() {
        if (CollectionUtils.isNotEmpty(promotionSellingPrices)) {
            return promotionSellingPrices.get(0).formula();
        }
        return null;
    }

    @Override
    public List<ItemParametersAndResultModel> parametersAndResult() {
        if (CollectionUtils.isEmpty(promotionSellingPrices)) {
            return Collections.emptyList();
        }
        return promotionSellingPrices.stream().map(e -> new ItemParametersAndResultModel(e.result(), e.computingParameter())).collect(Collectors.toList());
    }
}
