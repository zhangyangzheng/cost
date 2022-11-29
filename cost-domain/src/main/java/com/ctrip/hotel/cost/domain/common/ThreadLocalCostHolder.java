package com.ctrip.hotel.cost.domain.common;

import com.alibaba.ttl.TransmittableThreadLocal;

import java.util.Map;

/**
 * @author yangzhengzhang
 * @description
 * @date 2022-11-29 15:13
 */
public class ThreadLocalCostHolder {
    private static final TransmittableThreadLocal<ThreadLocalCostContext> ttl = new TransmittableThreadLocal();

    public ThreadLocalCostHolder() {
    }

    public static TransmittableThreadLocal<ThreadLocalCostContext> getTTL() {
        return ttl;
    }

    public static void setThreadLocalCostContext(String linkTracing) {
        ThreadLocalCostContext context = new ThreadLocalCostContext();
        context.setLinkTracing(linkTracing);
        ttl.set(context);
    }
}
