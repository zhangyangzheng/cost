package com.ctrip.hotel.cost.domain.data;

import java.util.List;

/**
 * @author yangzhengzhang
 * @description data source implement, fg or pp or ebk...
 * @date 2022-11-01 11:15
 */
public interface DataSource<R, P> {

    List<R> initData(List<P> ps);

    int sceneType();
}
