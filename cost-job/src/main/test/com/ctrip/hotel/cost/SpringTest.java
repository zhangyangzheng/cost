package com.ctrip.hotel.cost;

import com.ctrip.hotel.cost.infrastructure.helper.GetDetailDataSoaHelper;
import com.ctrip.hotel.cost.infrastructure.helper.ThrowOrderSoaHelper;
import com.ctrip.hotel.cost.infrastructure.job.FGNotifySettlementJob;
import com.ctrip.hotel.cost.infrastructure.model.bo.CancelOrderUsedBo;
import com.ctrip.hotel.cost.infrastructure.model.bo.SettlementApplyListUsedBo;
import com.ctrip.hotel.cost.infrastructure.model.bo.SettlementCancelListUsedBo;
import com.ctrip.hotel.cost.infrastructure.model.bo.SettlementPayDataUsedBo;
import com.ctrip.hotel.cost.infrastructure.repository.OrderAuditFgMqRepository;
import com.ctrip.hotel.cost.worker.CostJobApplication;
import com.ctrip.soa.hotel.settlement.api.CancelSettleData;
import com.ctrip.soa.hotel.settlement.api.SettleDataRequest;
import hotel.settlement.dao.dal.htlcalculatefeetidb.entity.OrderAuditFgMqTiDBGen;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import soa.ctrip.com.hotel.order.checkin.audit.v2.getOrderAuditRoomData.OrderAuditRoomData;
import soa.ctrip.com.hotel.vendor.settlement.v1.Hotelorderchannel;
import soa.ctrip.com.hotel.vendor.settlement.v1.cancelorder.CancelorderRequesttype;
import soa.ctrip.com.hotel.vendor.settlement.v1.settlementdata.SettlementPayData;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = CostJobApplication.class) // 使用junit4进行测试
public class SpringTest {

  @Autowired
  OrderAuditFgMqRepository orderAuditFgMqRepository;

  @Autowired
  ThrowOrderSoaHelper throwOrderSoaHelper;

  @Autowired
  GetDetailDataSoaHelper getDetailDataSoaHelper;

  @Autowired
  FGNotifySettlementJob fgNotifySettlementJob;

  @Test
  public void dalTest() throws SQLException {
    List<OrderAuditFgMqTiDBGen> orderAuditFgMqList =
        orderAuditFgMqRepository.getPendingJobs(Arrays.asList(1, 2), 1);
    System.out.println(orderAuditFgMqList);
  }

  @Test
  public void getDetailDataTest(){
    List<OrderAuditRoomData> fgIdRes = getDetailDataSoaHelper.getOrderAuditRoomDataByFgId(Arrays.asList(560039249l));
    System.out.println(fgIdRes);
  }

  @Test
  public void throwOrderTest() {
    CancelOrderUsedBo cancelOrderUsedBo = new CancelOrderUsedBo();
    cancelOrderUsedBo.setOrderid("1l");
    cancelOrderUsedBo.setOrderchannel(Hotelorderchannel.hpp);
    cancelOrderUsedBo.setFGID(1l);
    CancelOrderUsedBo.ToCancelDataUsed toCancelDataUsed = new CancelOrderUsedBo.ToCancelDataUsed();
    toCancelDataUsed.setSettlementid(1l);
    toCancelDataUsed.setOutsettlementno("1111111");
    cancelOrderUsedBo.setCancelDataList(Arrays.asList(toCancelDataUsed));
    CancelorderRequesttype cancelorderRequesttype = cancelOrderUsedBo.convertTo();
    System.out.println(cancelorderRequesttype);
    throwOrderSoaHelper.batchCallCancelOrder(Arrays.asList(cancelOrderUsedBo));



    SettlementApplyListUsedBo settlementApplyListUsedBo = new SettlementApplyListUsedBo();
    settlementApplyListUsedBo.setOrderId(" sss");
    settlementApplyListUsedBo.setSettlementId(1l);
    settlementApplyListUsedBo.setOutSettlementNo(" 122 ");

    SettlementApplyListUsedBo.DataItemUsed dataItemUsed = new SettlementApplyListUsedBo.DataItemUsed();
    dataItemUsed.setDataKey("111");
    settlementApplyListUsedBo.setDataItems(Arrays.asList(dataItemUsed));

    SettlementApplyListUsedBo.SettlementOptionalUsed settlementOptionalUsed = new SettlementApplyListUsedBo.SettlementOptionalUsed();
    settlementOptionalUsed.setOptionalName("111222");
    settlementApplyListUsedBo.setSettlementOptionalList(Arrays.asList(settlementOptionalUsed));

    SettlementApplyListUsedBo.SettlementPromotionDetailUsed settlementPromotionDetailUsed = new SettlementApplyListUsedBo.SettlementPromotionDetailUsed();
    settlementPromotionDetailUsed.setPromotionName("111323113");
    settlementPromotionDetailUsed.setBeginDate(Calendar.getInstance());
    settlementApplyListUsedBo.setSettlementPromotionDetailList(Arrays.asList(settlementPromotionDetailUsed));

    SettleDataRequest settleDataRequest = settlementApplyListUsedBo.convertTo();
    System.out.println(settleDataRequest);
    throwOrderSoaHelper.batchCallSettlementApplyList(Arrays.asList(settlementApplyListUsedBo));




    SettlementCancelListUsedBo settlementCancelListUsedBo = new SettlementCancelListUsedBo();
    settlementCancelListUsedBo.setSettlementId(1l);
    settlementCancelListUsedBo.setOutSettlementNo("123");
    SettlementCancelListUsedBo.CancelDataItemUsed cancelDataItemUsed = new SettlementCancelListUsedBo.CancelDataItemUsed();
    cancelDataItemUsed.setDataDesc("123");
    settlementCancelListUsedBo.setCancelItems(Arrays.asList(cancelDataItemUsed));
    CancelSettleData cancelSettleData = settlementCancelListUsedBo.convertTo();
    System.out.println(cancelSettleData);
    throwOrderSoaHelper.batchCallSettlementCancelList(Arrays.asList(settlementCancelListUsedBo));



    SettlementPayDataUsedBo settlementPayDataUsedBo = new SettlementPayDataUsedBo();
    settlementPayDataUsedBo.setBidFlag("aaasssddd");
    SettlementPayDataUsedBo.OrderPromotionUsed orderPromotionUsed = new SettlementPayDataUsedBo.OrderPromotionUsed();
    orderPromotionUsed.setBeginDate(Calendar.getInstance());
    settlementPayDataUsedBo.setOrderPromotionList(Arrays.asList(orderPromotionUsed));
    SettlementPayData settlementPayData = settlementPayDataUsedBo.convertTo();
    throwOrderSoaHelper.batchCallSettlementPayDataReceive(Arrays.asList(settlementPayDataUsedBo));
    System.out.println(settlementPayData);
  }


  @Test
  public void jobTest(){
    try {
      fgNotifySettlementJob.execute(Arrays.asList(19));
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }
}
