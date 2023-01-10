package com.ctrip.hotel.cost.helper;

import com.ctrip.hotel.cost.application.model.vo.AuditOrderFgReqDTO;
import com.ctrip.hotel.cost.domain.data.model.OrderAuditFgMqBO;
import com.ctrip.hotel.cost.domain.data.model.SettlementCallBackInfo;
import com.ctrip.hotel.cost.model.bo.FgOrderAuditMqDataBo;
import hotel.settlement.common.beans.BeanHelper;
import hotel.settlement.dao.dal.htlcalculatefeetidb.entity.OrderAuditFgMqTiDBGen;
import hotel.settlement.dao.dal.htlcalculatefeetidb.entity.SettleCallbackInfoTiDBGen;
import org.apache.commons.lang.BooleanUtils;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class ConvertHelper {

    public static AuditOrderFgReqDTO fgOrderAuditMqDataBoToAuditOrderFgReqDTO(FgOrderAuditMqDataBo fgOrderAuditMqDataBo) {

        Objects.requireNonNull(fgOrderAuditMqDataBo);

        OrderAuditFgMqTiDBGen orderAuditFgMqTiDBGen = fgOrderAuditMqDataBo.getOrderAuditFgMqTiDBGen();
        SettleCallbackInfoTiDBGen settleCallbackInfoTiDBGen = fgOrderAuditMqDataBo.getSettleCallbackInfoTiDBGen();

        Objects.requireNonNull(orderAuditFgMqTiDBGen);
        Objects.requireNonNull(settleCallbackInfoTiDBGen);


        OrderAuditFgMqBO orderAuditFgMqBO =
                BeanHelper.convert(orderAuditFgMqTiDBGen, OrderAuditFgMqBO.class);
        SettlementCallBackInfo settlementCallBackInfo =
                BeanHelper.convert(settleCallbackInfoTiDBGen, SettlementCallBackInfo.class);
        settlementCallBackInfo.setPushWalletPay(
                BooleanUtils.toBooleanObject(settleCallbackInfoTiDBGen.getPushWalletPay()));

        AuditOrderFgReqDTO auditOrderFgReqDTO =
                new AuditOrderFgReqDTO(orderAuditFgMqBO, settlementCallBackInfo);

        return auditOrderFgReqDTO;
    }

    public static List<AuditOrderFgReqDTO> fgOrderAuditMqDataBoListToAuditOrderFgReqDTOList(List<FgOrderAuditMqDataBo> fgOrderAuditMqDataBoList){
        return fgOrderAuditMqDataBoList.stream().map(ConvertHelper::fgOrderAuditMqDataBoToAuditOrderFgReqDTO).collect(Collectors.toList());
    }


    public static List<OrderAuditFgMqTiDBGen> fgOrderAuditMqDataBoListToOrderAuditFgMqTiDBGenList (List<FgOrderAuditMqDataBo> fgOrderAuditMqDataBoList){
        return fgOrderAuditMqDataBoList.stream().map(FgOrderAuditMqDataBo::getOrderAuditFgMqTiDBGen).collect(Collectors.toList());
    }

}
