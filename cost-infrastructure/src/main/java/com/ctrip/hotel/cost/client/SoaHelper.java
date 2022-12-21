package com.ctrip.hotel.cost.client;

import com.ctrip.framework.clogging.domain.thrift.LogLevel;
import com.ctrip.hotel.cost.common.ThreadLocalCostHolder;
import com.ctriposs.baiji.rpc.client.ServiceClientBase;
import com.dianping.cat.Cat;
import com.dianping.cat.message.Transaction;
import com.google.common.base.Stopwatch;
import hotel.settlement.common.LogHelper;
import hotel.settlement.common.QConfigHelper;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

@Component
public class SoaHelper {
  final public <TReq, TRes> TRes callSoa(
      TReq request, Class clientClass, String requestType, Class<TRes> responseClass) {
    ServiceClientBase client = ServiceClientBase.getInstance(clientClass);
    return callSoa(request, client, requestType, responseClass);
  }

  final protected <TReq, TRes> TRes callSoa(
      TReq request, ServiceClientBase client, String requestType, Class<TRes> responseClass) {

    Objects.requireNonNull(request);
    Objects.requireNonNull(client);
    Objects.requireNonNull(requestType);
    Objects.requireNonNull(responseClass);

    int retryTimes = Integer.parseInt(QConfigHelper.getSwitchConfigByKey(requestType, "1"));
    TRes response = null;
    if (retryTimes <= 1) {
      response = callSoaOnce(request, client, requestType, responseClass);
    } else {
      int tryTime = 0;
      while (tryTime++ < retryTimes) {
        response = callSoaOnce(request, client, requestType, responseClass);
        if (response != null) {
          break;
        }
      }
    }

    return response;
  }

  @SuppressWarnings("all")
  private <TReq extends Object, TRes extends Object> TRes callSoaOnce(
      TReq request, ServiceClientBase client, String requestType, Class<TRes> responseClass) {
    Stopwatch watch = Stopwatch.createStarted();
    long beginTime = System.currentTimeMillis();
    TRes response = null;
    boolean hasError = false;
    Transaction soaTransaction = Cat.newTransaction("SOA", requestType);
    try {
      // 接口调用
      response = (TRes) client.invoke(requestType, request, responseClass);
    } catch (Exception ex) {
      hasError = true;
      soaTransaction.setStatus(ex);
      // 记录错误信息
      LogHelper.logError(requestType, ex);
      ThreadLocalCostHolder.allLinkTracingLog(ex, LogLevel.ERROR);
    } finally {
      try {
        LogHelper.logInfoES(
            requestType, watch.stop().elapsed(TimeUnit.MILLISECONDS), request, response);
      } catch (Exception ex) {
      }
      soaTransaction.complete();
    }
    return response;
  }
}
