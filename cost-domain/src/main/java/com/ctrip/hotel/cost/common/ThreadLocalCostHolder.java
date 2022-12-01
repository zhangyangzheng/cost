package com.ctrip.hotel.cost.common;

import com.alibaba.ttl.TransmittableThreadLocal;
import com.ctrip.framework.clogging.domain.thrift.LogLevel;
import hotel.settlement.common.LogHelper;

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

    public static <T> void allLinkTracingLog(T t, LogLevel level) {
        if (ThreadLocalCostHolder.getTTL().get() == null
                || t == null
                || level == null
        ) {
            return;
        }
        switch (level) {
            case INFO:
                LogHelper.logInfo(ThreadLocalCostHolder.getTTL().get().getLinkTracing(), (String) t, ThreadLocalCostHolder.getTTL().get().getTags());
                break;
            case WARN:
                LogHelper.logWarn(ThreadLocalCostHolder.getTTL().get().getLinkTracing(), (Throwable) t, ThreadLocalCostHolder.getTTL().get().getTags());
                break;
            case ERROR:
                LogHelper.logError(ThreadLocalCostHolder.getTTL().get().getLinkTracing(), (Throwable) t, ThreadLocalCostHolder.getTTL().get().getTags());
                break;
        }
    }
}
