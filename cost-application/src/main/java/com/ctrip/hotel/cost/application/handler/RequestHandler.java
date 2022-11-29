package com.ctrip.hotel.cost.application.handler;

import com.ctrip.hotel.cost.application.model.AuditOrderFgCostDTO;
import com.ctrip.hotel.cost.application.model.CostDTO;
import com.ctrip.hotel.cost.application.model.vo.AuditOrderFgReqDTO;
import com.ctrip.hotel.cost.application.resolver.FgOrderCostViewResolver;
import com.ctrip.hotel.cost.application.resolver.ViewResolver;
import com.ctrip.hotel.cost.domain.data.model.AuditOrderInfoBO;
import com.ctrip.hotel.cost.domain.data.model.OrderAuditFgMqBO;
import com.ctrip.hotel.cost.domain.root.CostSupporter;
import com.ctrip.hotel.cost.domain.scene.EnumScene;
import com.ctrip.hotel.cost.domain.settlement.EnumOrderOpType;
import com.ctrip.hotel.cost.domain.settlement.SettlementService;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
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
    @Override
    public List<Long> auditOrderFg(List<AuditOrderFgReqDTO> request) {
        if (CollectionUtils.isEmpty(request)) {
            return Collections.emptyList();
        }
        List<AuditOrderFgReqDTO> cancelList = request.stream().filter(e -> e.getOrderAuditFgMqBO().getOpType().equals(EnumOrderOpType.CANCEL.getName())).collect(Collectors.toList());
        List<AuditOrderFgReqDTO> costList = request.stream().filter(e -> !e.getOrderAuditFgMqBO().getOpType().equals(EnumOrderOpType.CANCEL.getName())).collect(Collectors.toList());

        // 计费+抛单
        List<Long> costIds = costList.stream().map(e -> e.getOrderAuditFgMqBO().getFgId()).collect(Collectors.toList());
        List<AuditOrderInfoBO> successOrders = auditOrderFgCollectPrice(costIds);
        for (AuditOrderInfoBO order: successOrders) {
            Boolean aBoolean = settlementService.callSettlementForFg(order);
        }

        // 取消抛单
        List<Long> cancelIds = cancelList.stream().map(e -> e.getOrderAuditFgMqBO().getFgId()).collect(Collectors.toList());
        List<AuditOrderInfoBO> auditOrderInfoBOS = auditOrderFgNoPrice(cancelIds);
        for (AuditOrderFgReqDTO req : cancelList) {
            AuditOrderInfoBO info = new AuditOrderInfoBO();

            OrderAuditFgMqBO bo = RequestBodyMapper.INSTANCE.fgReqToBo(req);
            info.setOrderAuditFgMqBO(bo);
            // todo 填充子表内容 + 取消单也需要查询审核接口数据
            settlementService.callCancelForFg(info);
        }
        return null;
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
        return viewResolver.resolveView();
    }
}
