package com.ctrip.hotel.cost.domain.scene;

/**
 * @author yangzhengzhang
 * @description
 * @date 2022-11-01 0:41
 */
public class SceneFactory {
    private SceneManager sceneManager = new StaticSceneManager();

    public Scene getScene(Integer sceneCode) {
        return sceneManager.getScene(sceneCode);
    }
}
