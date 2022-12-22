package com.ctrip.hotel.cost.domain.data.item;

import com.ctrip.hotel.cost.domain.element.bid.BidPrice;
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
 * @date 2022-11-14 19:18
 */
@Data
public class Bid implements Item<BidPrice> {

    private List<BidPrice> bidPrices;
    private BigDecimal total;

    public Bid(List<BidPrice> bidPrices) {
        this.bidPrices = bidPrices.stream().filter(e -> !e.days().getValue().equals(BigDecimal.ZERO)).collect(Collectors.toList());
    }

    @Override
    public CostItemType costItemType() {
        return CostItemType.BID_PRICE_FG;// todo 上游契约定义不统一，后续的迭代希望可以统一为 BID_PRICE
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
        this.total = count(bidPrices);
    }

    @Override
    public String formula() {
        if (CollectionUtils.isNotEmpty(bidPrices)) {
            return bidPrices.get(0).formula();
        }
        return null;
    }

    @Override
    public List<ItemParametersAndResultModel> parametersAndResult() {
        if (CollectionUtils.isEmpty(bidPrices)) {
            return Collections.emptyList();
        }
        return bidPrices.stream().map(e -> new ItemParametersAndResultModel(e.result(), e.computingParameter())).collect(Collectors.toList());
    }
}
