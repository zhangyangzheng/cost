package com.ctrip.hotel.cost.domain.data;

import com.ctrip.hotel.cost.domain.scene.EnumScene;

/**
 * @author yangzhengzhang
 * @description
 * @date 2022-10-26 14:24
 */
public interface OrderInfoFGRepository extends DataSource<DataCenter, Long> {

    default int sceneType() {
        return EnumScene.AUDIT_ORDER_FG.getCode();
    }
}
