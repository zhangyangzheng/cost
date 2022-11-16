package com.ctrip.hotel.cost.domain.data.item;

import com.ctrip.hotel.cost.domain.element.room.RoomSellingPrice;
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
 * @date 2022-11-14 19:45
 */
@Data
public class RoomSelling implements Item<RoomSellingPrice> {

    private List<RoomSellingPrice> roomSellingPrices;
    private BigDecimal total;

    public RoomSelling(List<RoomSellingPrice> roomSellingPrices) {
        this.roomSellingPrices = roomSellingPrices;
    }

    @Override
    public CostItemType costItemType() {
        return CostItemType.ROOM_SELLING_PRICE_FG;
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
        this.total = count(roomSellingPrices);
    }

    @Override
    public String formula() {
        if (CollectionUtils.isNotEmpty(roomSellingPrices)) {
            return roomSellingPrices.get(0).formula();
        }
        return null;
    }

    @Override
    public List<ItemParametersAndResultModel> parametersAndResult() {
        if (CollectionUtils.isNotEmpty(roomSellingPrices)) {
            return Collections.emptyList();
        }
        return roomSellingPrices.stream().map(e -> new ItemParametersAndResultModel(e.result(), e.computingParameter())).collect(Collectors.toList());
    }
}
