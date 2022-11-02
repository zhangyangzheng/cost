package com.ctrip.hotel.cost.domain.scene;

import java.util.List;

/**
 * @author yangzhengzhang
 * @description
 * @date 2022-11-01 0:37
 */
public class StaticSceneManager implements SceneManager {
    @Override
    public Scene getScene(Integer sceneCode) {
        return new SceneEnumWrapper(EnumScene.getScene(sceneCode));
    }

    static class SceneEnumWrapper implements Scene {

        private EnumScene innerScene;

        private SceneEnumWrapper(EnumScene innerScene) {
            this.innerScene = innerScene;
        }

        @Override
        public List<CostItemType> getCostItemTypes() {
            return innerScene.getCostItemTypes();
        }

        @Override
        public Integer getSceneCode() {
            return innerScene.getCode();
        }

    }
}
