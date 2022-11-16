package com.ctrip.hotel.cost.application.handler;

import java.util.List;

/**
 * @author yangzhengzhang
 * @description
 * @date 2022-11-03 1:54
 */
public interface HandlerApi {
    List<Long> auditOrderFg(List<Long> dataIds);
}
