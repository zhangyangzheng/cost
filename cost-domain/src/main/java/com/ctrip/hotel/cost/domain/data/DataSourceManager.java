package com.ctrip.hotel.cost.domain.data;

import com.ctrip.hotel.cost.domain.scene.Scene;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author yangzhengzhang
 * @description
 * @date 2022-11-01 14:17
 */
@Component
public class DataSourceManager {

    private static final Map<Integer, DataSource> dataSource = new ConcurrentHashMap();

    @Autowired
    public DataSourceManager(Map<String, DataSource> map) {
        for (Map.Entry<String, DataSource> entry : map.entrySet()) {
            dataSource.put(entry.getValue().sceneType(), entry.getValue());
        }
    }

    public static DataSource getDataSource(Scene scene) {
        return dataSource.get(scene.getSceneCode());
    }

}
