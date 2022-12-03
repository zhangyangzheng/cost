package com.ctrip.hotel.cost.application.resolver;

/**
 * @author yangzhengzhang
 * @description
 * @date 2022-10-30 23:28
 */
public interface ViewResolver<VIEW, MODEL> {

    VIEW resolveView();

    /**
     * 未来和结算解耦开后，下掉该接口
     * @return
     */
    VIEW resolveViewExtend();

    void setModel(MODEL model);

}
