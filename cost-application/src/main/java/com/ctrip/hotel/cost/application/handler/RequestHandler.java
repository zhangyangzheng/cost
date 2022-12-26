package com.ctrip.hotel.cost.application.handler;

import com.alibaba.fastjson.JSON;
import com.ctrip.framework.clogging.domain.thrift.LogLevel;
import com.ctrip.hotel.cost.application.model.AuditOrderFgCostDTO;
import com.ctrip.hotel.cost.application.model.CostDTO;
import com.ctrip.hotel.cost.application.model.vo.AuditOrderFgReqDTO;
import com.ctrip.hotel.cost.application.resolver.FgOrderCostViewResolver;
import com.ctrip.hotel.cost.application.resolver.ViewResolver;
import com.ctrip.hotel.cost.common.EnumLogTag;
import com.ctrip.hotel.cost.common.ThreadLocalCostHolder;
import com.ctrip.hotel.cost.domain.data.model.AuditOrderInfoBO;
import com.ctrip.hotel.cost.domain.root.CostSupporter;
import com.ctrip.hotel.cost.domain.scene.EnumScene;
import com.ctrip.hotel.cost.domain.settlement.EnumOrderOpType;
import com.ctrip.hotel.cost.domain.settlement.SettlementService;
import com.dianping.cat.annotation.CatTrace;
import hotel.settlement.common.LogHelper;
import hotel.settlement.common.entity.CatBizTypeConstant;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author yangzhengzhang
 * @description
 * @date 2022-10-28 17:08
 */
@Component
public class RequestHandler implements HandlerApi{

    @Autowired
    private SettlementService settlementService;

    /**
     * 计费，先把抛单逻辑塞到计费逻辑后
     * @param request
     * @return 【现付审核离店订单】发起计费，返回计费成功的fgId，计费失败的fgId由job发起重试
     */
    @CatTrace(type = CatBizTypeConstant.BIZ_SOA_PROCESS + ".Cost", name = "AuditOrderFg")
    @Override
    public List<String> auditOrderFg(List<AuditOrderFgReqDTO> request) {
        if (CollectionUtils.isEmpty(request)) {
            return Collections.emptyList();
        }
        List<String> successes = new ArrayList<>();

        try {
            // set TransThreadLocal
            ThreadLocalCostHolder.setThreadLocalCostContext("auditOrderFg");
            ThreadLocalCostHolder.allLinkTracingLog(JSON.toJSONString(request), LogLevel.INFO);

            List<AuditOrderFgReqDTO> costList = request.stream().filter(e -> !e.getOrderAuditFgMqBO().getOpType().equals(EnumOrderOpType.CANCEL.getName())).collect(Collectors.toList());
            // 计费+抛单
            List<Long> costIds = costList.stream().map(e -> e.getOrderAuditFgMqBO().getFgId()).collect(Collectors.toList());
            List<AuditOrderInfoBO> successCostList = auditOrderFgCollectPrice(costIds);
            Map<String, AuditOrderFgReqDTO> reqCostMap = costList.stream().collect(Collectors.toMap(a -> a.getOrderAuditFgMqBO().getOrderId().toString() + a.getOrderAuditFgMqBO().getFgId().toString(), a -> a, (k1, k2) -> k1));
            for (AuditOrderInfoBO order: successCostList) {
                order.setOrderAuditFgMqBO(RequestBodyMapper.INSTANCE.fgReqToMqBo(reqCostMap.get(order.getOrderId().toString() + order.getAuditRoomInfoList().get(0).getAuditRoomBasicInfo().getFgid().toString()).getOrderAuditFgMqBO()));
                order.setSettlementCallBackInfo(RequestBodyMapper.INSTANCE.fgReqToCallBackInfo(reqCostMap.get(order.getOrderId().toString() + order.getAuditRoomInfoList().get(0).getAuditRoomBasicInfo().getFgid().toString()).getSettlementCallBackInfo()));

                ThreadLocalCostHolder.getTTL().get().getTags().put(EnumLogTag.ORDER_ID.getValue(), order.getOrderAuditFgMqBO().getOrderId().toString());
                ThreadLocalCostHolder.getTTL().get().getTags().put(EnumLogTag.FG_ID.getValue(), order.getOrderAuditFgMqBO().getFgId().toString());
                ThreadLocalCostHolder.getTTL().get().getTags().put(EnumLogTag.REFERENCE_ID.getValue(), order.getOrderAuditFgMqBO().getReferenceId());
                ThreadLocalCostHolder.getTTL().get().getTags().put(EnumLogTag.BUSINESS_TYPE.getValue(), order.getOrderAuditFgMqBO().getBusinessType().toString());

                if (settlementService.callSettlementForFg(order)) {
                    successes.add(order.getOrderAuditFgMqBO().getReferenceId());
                }
            }
        } catch (Exception e) {
            LogHelper.logError("auditOrderFg", e);
        } finally {
            // clear threadlocal
            ThreadLocalCostHolder.getTTL().remove();
        }

        try {
            // set TransThreadLocal
            ThreadLocalCostHolder.setThreadLocalCostContext("auditOrderFg");

            List<AuditOrderFgReqDTO> cancelList = request.stream().filter(e -> e.getOrderAuditFgMqBO().getOpType().equals(EnumOrderOpType.CANCEL.getName())).collect(Collectors.toList());
            // 取消免计费抛单
            List<Long> cancelIds = cancelList.stream().map(e -> e.getOrderAuditFgMqBO().getFgId()).collect(Collectors.toList());
            List<AuditOrderInfoBO> cancelBOs = auditOrderFgNoPrice(cancelIds);
            Map<String, AuditOrderFgReqDTO> reqCancelMap = cancelList.stream().collect(Collectors.toMap(a -> a.getOrderAuditFgMqBO().getOrderId().toString() + a.getOrderAuditFgMqBO().getFgId().toString(), a -> a, (k1, k2) -> k1));
            for (AuditOrderInfoBO order : cancelBOs) {
                order.setOrderAuditFgMqBO(RequestBodyMapper.INSTANCE.fgReqToMqBo(reqCancelMap.get(order.getOrderId().toString() + order.getAuditRoomInfoList().get(0).getAuditRoomBasicInfo().getFgid().toString()).getOrderAuditFgMqBO()));
                order.setSettlementCallBackInfo(RequestBodyMapper.INSTANCE.fgReqToCallBackInfo(reqCancelMap.get(order.getOrderId().toString() + order.getAuditRoomInfoList().get(0).getAuditRoomBasicInfo().getFgid().toString()).getSettlementCallBackInfo()));

                ThreadLocalCostHolder.getTTL().get().getTags().put(EnumLogTag.ORDER_ID.getValue(), order.getOrderAuditFgMqBO().getOrderId().toString());
                ThreadLocalCostHolder.getTTL().get().getTags().put(EnumLogTag.FG_ID.getValue(), order.getOrderAuditFgMqBO().getFgId().toString());
                ThreadLocalCostHolder.getTTL().get().getTags().put(EnumLogTag.REFERENCE_ID.getValue(), order.getOrderAuditFgMqBO().getReferenceId());
                ThreadLocalCostHolder.getTTL().get().getTags().put(EnumLogTag.BUSINESS_TYPE.getValue(), order.getOrderAuditFgMqBO().getBusinessType().toString());

                if (settlementService.callCancelForFg(order)) {
                    successes.add(order.getOrderAuditFgMqBO().getReferenceId());
                }
            }
        } catch (Exception e) {
            LogHelper.logError("auditOrderFg", e);
        } finally {
            // clear threadlocal
            ThreadLocalCostHolder.getTTL().remove();
        }
        LogHelper.logInfo("auditOrderFg", "success ids：" + JSON.toJSONString(successes));
        return successes;
    }

    @Override
    public List<AuditOrderInfoBO> auditOrderFgCollectPrice(List<Long> costIds) {
        if (CollectionUtils.isNotEmpty(costIds)) {
            AuditOrderFgCostDTO fgReqModel = new AuditOrderFgCostDTO();
            fgReqModel.setDataIds(costIds);
            fgReqModel.setAppSceneCode(EnumScene.AUDIT_ORDER_FG.getCode());// 内部计费，向内分配
            return this.resolveResponse(new FgOrderCostViewResolver(), fgReqModel);
        }
        return Collections.emptyList();
    }

    private <VIEW> VIEW resolveResponse(ViewResolver<VIEW, CostSupporter> viewResolver, CostDTO request) {
        CostSupporter costSupporter = new CostSupporter(request.getAppSceneCode(), request.allDataId());
        costSupporter.innerCostWorker();
        // done
        viewResolver.setModel(costSupporter);
        return viewResolver.resolveView();
    }

    private List<AuditOrderInfoBO> auditOrderFgNoPrice(List<Long> costIds) {
        if (CollectionUtils.isNotEmpty(costIds)) {
            AuditOrderFgCostDTO fgReqModel = new AuditOrderFgCostDTO();
            fgReqModel.setDataIds(costIds);
            fgReqModel.setAppSceneCode(EnumScene.AUDIT_ORDER_FG.getCode());// 内部计费，向内分配
            return this.resolveNoPriceResponse(new FgOrderCostViewResolver(), fgReqModel);
        }
        return Collections.emptyList();
    }

    private <VIEW> VIEW resolveNoPriceResponse(ViewResolver<VIEW, CostSupporter> viewResolver, CostDTO request) {
        CostSupporter costSupporter = new CostSupporter(request.getAppSceneCode(), request.allDataId());
        // done
        viewResolver.setModel(costSupporter);
        return viewResolver.resolveViewExtend();
    }
}
