package com.ctrip.hotel.cost.application.model;

import com.ctrip.hotel.cost.application.model.CostDTO;
import lombok.Data;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author yangzhengzhang
 * @description
 * @date 2022-11-03 0:47
 */
@Data
public class AuditOrderFgCostDTO implements CostDTO {

    private Integer appSceneCode;

    private List<Long> dataIds;

    @Override
    public List<Long> allDataId() {
        if (CollectionUtils.isEmpty(dataIds)) {
            return Collections.emptyList();
        }
        return dataIds.stream().filter(Objects::nonNull).distinct().collect(Collectors.toList());
    }

    @Override
    public Integer getAppSceneCode() {
        return appSceneCode;
    }
}
