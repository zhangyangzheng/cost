package com.ctrip.hotel.cost.domain.root;

import com.ctrip.hotel.cost.domain.data.DataCenter;
import com.ctrip.hotel.cost.domain.data.DataCenterFactory;
import com.ctrip.hotel.cost.domain.scene.Scene;
import hotel.settlement.common.LogHelper;
import org.apache.commons.collections.CollectionUtils;

import java.util.List;

/**
 * @author yangzhengzhang
 * @description
 * @date 2022-11-03 1:07
 */
public class CostContext {

    private static final DataCenterFactory dataCenterFactory = new DataCenterFactory();
    private Scene scene;
    private List<Long> dataIds;
    private List<DataCenter> dataCenters;

    public CostContext(Scene scene, List<Long> dataIds) {
        this.scene = scene;
        this.dataIds = dataIds;
        this.dataCenters = dataCenterFactory.getDataCenter(scene, dataIds);
    }

    public void computeCostItem() {
        if (CollectionUtils.isEmpty(this.getDataCenters())) {
            return;
        }
        for (DataCenter dataCenter : this.getDataCenters()) {
            try {
                dataCenter.compute(this.getScene().getCostItemTypes());
                dataCenter.setSuccess(true);
            } catch (Exception e) {
                // 一笔订单计费失败的异常，在此处理。计费失败被丢弃，外部重试
                LogHelper.logError(this.getClass().getSimpleName(), e);// todo 优化日志
                System.out.println(e);
                System.out.println(e.getMessage());
            }
        }
    }

    public Scene getScene() {
        return scene;
    }

    public List<Long> getDataIds() {
        return dataIds;
    }

    public List<DataCenter> getDataCenters() {
        return dataCenters;
    }
}
