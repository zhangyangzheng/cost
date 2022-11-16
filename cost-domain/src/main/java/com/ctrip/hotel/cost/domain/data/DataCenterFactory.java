package com.ctrip.hotel.cost.domain.data;

import com.ctrip.hotel.cost.domain.scene.Scene;

import java.util.List;

/**
 * @author yangzhengzhang
 * @description
 * @date 2022-11-02 23:19
 */
public class DataCenterFactory {

    public List<DataCenter> getDataCenter(Scene scene, List<Long> dataIds) {
        return DataSourceManager.getDataSource(scene).initData(dataIds);
    }

}
