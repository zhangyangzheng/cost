package com.ctrip.hotel.cost.application.resolver;

/**
 * @author yangzhengzhang
 * @description
 * @date 2022-10-30 23:28
 */
public interface ViewResolver<VIEW, MODEL> {

    VIEW resolveView();

    void setModel(MODEL model);

}
