package com.ctrip.hotel.cost.application.handler;

import com.ctrip.hotel.cost.domain.root.CostSupporter;
import com.ctrip.hotel.cost.application.fg.request.AuditOrderFgReqModel;
import com.ctrip.hotel.cost.application.fg.request.CostDTO;
import com.ctrip.hotel.cost.application.resolver.FgOrderCostViewResolver;
import com.ctrip.hotel.cost.application.resolver.ViewResolver;
import com.ctrip.hotel.cost.domain.scene.EnumScene;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

/**
 * @author yangzhengzhang
 * @description
 * @date 2022-10-28 17:08
 */
@Component
public class RequestHandler implements HandlerApi{

    /**
     *
     * @param dataIds
     * @return 【现付审核离店订单】发起计费，返回计费成功的fgId，计费失败的fgId由job发起重试
     */
    @Override
    public List<Long> auditOrderFg(List<Long> dataIds) {
        if (CollectionUtils.isEmpty(dataIds)) {
            return Collections.emptyList();
        }
        AuditOrderFgReqModel fgReqModel = new AuditOrderFgReqModel();
        fgReqModel.setDataIds(dataIds);
        fgReqModel.setAppSceneCode(EnumScene.AUDIT_ORDER_FG.getCode());// 内部计费，固定分配
        List<Long> successIds = this.resolveResponse(new FgOrderCostViewResolver(), fgReqModel);
        // do other 抛单

        return successIds;
    }

    private <VIEW> VIEW resolveResponse(ViewResolver<VIEW, CostSupporter> viewResolver, CostDTO request) {
        CostSupporter costSupporter = new CostSupporter(request.getAppSceneCode(), request.allDataId());
        costSupporter.innerCostWorker();
        // done
        viewResolver.setModel(costSupporter);
        return viewResolver.resolveView();
    }
}
