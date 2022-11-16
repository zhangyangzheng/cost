package com.ctrip.hotel.cost.domain.root;

import com.ctrip.hotel.cost.domain.data.DataCenter;
import com.ctrip.hotel.cost.domain.scene.Scene;
import com.ctrip.hotel.cost.domain.scene.SceneFactory;
import org.apache.commons.collections.CollectionUtils;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author yangzhengzhang
 * @description
 * @date 2022-10-30 23:56
 */
public class CostSupporter {

    private static final SceneFactory sceneFactory = new SceneFactory();
    private CostContext costContext;

    public CostSupporter(Integer sceneCode, List<Long> dataIds) {
        createCostContext(sceneCode, dataIds);
    }

    /**
     * 创建计费上下文
     */
    private void createCostContext(Integer sceneCode, List<Long> dataIds) {
        Scene scene = sceneFactory.getScene(sceneCode);
        costContext = new CostContext(scene, dataIds);
    }

    /**
     * 处理计费
     */
    public void innerCostWorker() {
        costContext.computeCostItem();
    }

    /**
     * 返回计费结果
     * @return
     */
    public List<Long> getCostSuccessData() {
        if (CollectionUtils.isEmpty(costContext.getDataCenters())) {
            return Collections.emptyList();
        }
        return costContext.getDataCenters().stream().filter(DataCenter::getSuccess).map(DataCenter::getDataId).collect(Collectors.toList());
    }

}
