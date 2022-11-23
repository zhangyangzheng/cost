package com.ctrip.hotel.cost.application.model;

import java.util.List;

/**
 * @author yangzhengzhang
 * @description
 * @date 2022-11-03 0:49
 */
public interface CostDTO {
    List<Long> allDataId();

    Integer getAppSceneCode();
}
