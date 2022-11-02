package com.ctrip.hotel.cost.domain.scene;

import java.util.List;

/**
 * @author yangzhengzhang
 * @description
 * @date 2022-11-01 0:22
 */
public interface Scene {

    List<CostItemType> getCostItemTypes();

    Integer getSceneCode();
}
