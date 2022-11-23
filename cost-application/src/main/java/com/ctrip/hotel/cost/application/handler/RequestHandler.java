package com.ctrip.hotel.cost.application.handler;

import com.ctrip.hotel.cost.application.model.vo.AuditOrderFgReqVO;
import com.ctrip.hotel.cost.domain.root.CostSupporter;
import com.ctrip.hotel.cost.application.model.AuditOrderFgCostDTO;
import com.ctrip.hotel.cost.application.model.CostDTO;
import com.ctrip.hotel.cost.application.resolver.FgOrderCostViewResolver;
import com.ctrip.hotel.cost.application.resolver.ViewResolver;
import com.ctrip.hotel.cost.domain.scene.EnumScene;
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
    public List<Long> auditOrderFg(List<AuditOrderFgReqVO> request) {
        if (CollectionUtils.isEmpty(request)) {
            return Collections.emptyList();
        }
        List<AuditOrderFgReqVO> cancelList = request.stream().filter(AuditOrderFgReqVO::getIsCancel).collect(Collectors.toList());
        List<AuditOrderFgReqVO> costList = request.stream().filter(e -> !e.getIsCancel()).collect(Collectors.toList());

        // 计费+抛单
        List<Long> costIds = costList.stream().map(AuditOrderFgReqVO::getFgId).collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(costIds)) {
            AuditOrderFgCostDTO fgReqModel = new AuditOrderFgCostDTO();
            fgReqModel.setDataIds(costIds);
            fgReqModel.setAppSceneCode(EnumScene.AUDIT_ORDER_FG.getCode());// 内部计费，向内分配
            List<Long> successIds = this.resolveResponse(new FgOrderCostViewResolver(), fgReqModel);
        }
        // 取消单 暂时一条一条调
        cancelList.stream()
                .map(e -> settlementService.callCancelOrderFg(e.getOrderId(), e.getFgId()))
                .collect(Collectors.toList());
        return null;
    }

    private <VIEW> VIEW resolveResponse(ViewResolver<VIEW, CostSupporter> viewResolver, CostDTO request) {
        CostSupporter costSupporter = new CostSupporter(request.getAppSceneCode(), request.allDataId());
        costSupporter.innerCostWorker();
        // done
        viewResolver.setModel(costSupporter);
        return viewResolver.resolveView();
    }
}
