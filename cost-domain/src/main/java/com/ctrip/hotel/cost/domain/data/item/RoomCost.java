package com.ctrip.hotel.cost.domain.data.item;

import com.ctrip.hotel.cost.domain.element.room.RoomCostPrice;
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
public class RoomCost implements Item<RoomCostPrice> {

    private List<RoomCostPrice> roomCostPrices;
    private BigDecimal total;

    public RoomCost(List<RoomCostPrice> roomCostPrices) {
        this.roomCostPrices = roomCostPrices;
    }

    @Override
    public CostItemType costItemType() {
        return CostItemType.ROOM_COST_PRICE_FG;
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
        this.total = count(roomCostPrices);
    }

    @Override
    public String formula() {
        if (CollectionUtils.isNotEmpty(roomCostPrices)) {
            return roomCostPrices.get(0).formula();
        }
        return null;
    }

    @Override
    public List<ItemParametersAndResultModel> parametersAndResult() {
        if (CollectionUtils.isEmpty(roomCostPrices)) {
            return Collections.emptyList();
        }
        return roomCostPrices.stream().map(e -> new ItemParametersAndResultModel(e.result(), e.computingParameter())).collect(Collectors.toList());
    }
}
