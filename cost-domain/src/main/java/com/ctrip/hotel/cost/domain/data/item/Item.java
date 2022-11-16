package com.ctrip.hotel.cost.domain.data.item;

import com.ctrip.hotel.cost.domain.core.MeasurementCenter;
import com.ctrip.hotel.cost.domain.scene.CostItemType;
import org.apache.commons.collections.CollectionUtils;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author yangzhengzhang
 * @description todo 链式计算过渡到等级计算，之后完善到图计算
 * @date 2022-11-14 19:27
 */
public interface Item<T extends MeasurementCenter> {
    CostItemType costItemType();
    Integer level();
    BigDecimal total();
    void countTotal() throws Exception;

    String formula();

    List<ItemParametersAndResultModel> parametersAndResult();

    default BigDecimal count(List<T> list) throws Exception {
        if (CollectionUtils.isEmpty(list)) {
            return BigDecimal.ZERO;
        }
        for (T t : list) {
            t.listener();
        }
        return list.stream().map(MeasurementCenter::result).reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
