package com.ctrip.hotel.cost.application.resolver;

/**
 * @author yangzhengzhang
 * @description
 * @date 2022-10-30 23:29
 */
public abstract class AbstractViewResolver<VIEW, MODEL> implements ViewResolver<VIEW, MODEL> {

    protected MODEL model;

    @Override
    public void setModel(MODEL model) {
        this.model = model;
    }
}
