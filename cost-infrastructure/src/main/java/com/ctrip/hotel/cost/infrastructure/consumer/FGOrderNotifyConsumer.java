package com.ctrip.hotel.cost.infrastructure.consumer;

import com.ctrip.hotel.cost.domain.consumer.BaseOrderNotifyConsumer;
import com.ctrip.hotel.cost.infrastructure.repository.OrderAuditFgMqRepositoryImpl;
import com.ctrip.platform.dal.dao.DalHints;
import hotel.settlement.dao.dal.htlcalculatefeetidb.entity.OrderAuditFgMqTiDBGen;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import qunar.tc.qmq.Message;

@Service
public class FGOrderNotifyConsumer extends BaseOrderNotifyConsumer<OrderAuditFgMqTiDBGen> {
    // 不可变 同一订单号需要在同一分片中
    final int sliceCount = 32;

    // 数据类型 房间id：0 订单id：1
    final Integer[] dataTypeArr = new Integer[]{0, 1};

    // 业务类型 抛结算：0 noshow自动付：1 noshow返佣：2
    final Integer[] businessTypeArr = new Integer[]{0, 1, 2};

    // 操作类型 创建：C 修改：U 删除：D
    final String[] opTypeArr = new String[]{"C", "U", "D"};


    @Autowired
    OrderAuditFgMqRepositoryImpl orderAuditFgMqRepository;

    @Override
    protected Integer getSliceIndex(Long data){
        return (int) (data % sliceCount);
    }
    @Override
    protected OrderAuditFgMqTiDBGen convertTo(Message message) throws Exception {
        Long dataId = Long.parseLong(message.getStringProperty("dataId"));
        Integer dataType = Integer.parseInt(message.getStringProperty("dataType"));
        Integer businessType = Integer.parseInt(message.getStringProperty("businessType"));
        String opType = message.getStringProperty("opType");
        String referenceId = message.getStringProperty("referenceId");
        if (dataId == null
                || dataType == null
                || businessType == null
                || opType == null
                || referenceId == null) {
            throw new Exception("field can not be null");
        }
        OrderAuditFgMqTiDBGen orderAuditFgMqTiDBGen = new OrderAuditFgMqTiDBGen();
        orderAuditFgMqTiDBGen.setDataId(dataId);
        orderAuditFgMqTiDBGen.setDataType(dataType);
        orderAuditFgMqTiDBGen.setBusinessType(businessType);
        orderAuditFgMqTiDBGen.setOpType(opType);
        orderAuditFgMqTiDBGen.setReferenceId(referenceId);
        orderAuditFgMqTiDBGen.setSliceIndex(getSliceIndex(dataId));
        return orderAuditFgMqTiDBGen;
    }
    @Override
    protected void legalCheck(OrderAuditFgMqTiDBGen item) throws Exception {
        if(!(isInLegalArr(item.getDataType(), dataTypeArr) &&
                isInLegalArr(item.getBusinessType(), businessTypeArr) &&
                isInLegalArr(item.getOpType(), opTypeArr))){
            throw new Exception("params not legal");
        }
    }
    @Override
    public void insertInto(Message message) throws Exception {
        OrderAuditFgMqTiDBGen orderAuditFgMqTiDBGen = convertTo(message);
        legalCheck(orderAuditFgMqTiDBGen);
        orderAuditFgMqRepository.insert(new DalHints(), orderAuditFgMqTiDBGen);
    }
}
