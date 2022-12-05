package com.ctrip.hotel.cost.worker;

import com.alibaba.fastjson.JSON;
import com.ctrip.hotel.cost.CostJobApplication;
import com.ctrip.hotel.cost.application.handler.HandlerApi;
import com.ctrip.hotel.cost.common.ThreadLocalCostHolder;
import com.ctrip.hotel.cost.domain.data.model.AuditOrderInfoBO;
import com.ctrip.hotel.cost.infrastructure.client.AuditClient;
import com.ctrip.hotel.cost.infrastructure.client.SettlementClient;
import com.ctrip.hotel.cost.infrastructure.model.bo.SettlementApplyListUsedBo;
import com.ctrip.hotel.cost.infrastructure.model.bo.SettlementCancelListUsedBo;
import com.ctrip.hotel.cost.infrastructure.model.bo.SettlementPayDataUsedBo;
import com.ctrip.hotel.cost.infrastructure.model.dto.FgBackToAuditDto;
import com.ctrip.hotel.cost.infrastructure.mq.AuditMqProducer;
import com.ctrip.hotel.cost.infrastructure.repository.OrderAuditFgMqRepository;
import com.ctrip.hotel.cost.job.FGNotifySettlementJob;
import com.ctrip.soa.hotel.settlement.api.CancelSettleData;
import com.ctrip.soa.hotel.settlement.api.SettleDataRequest;
import hotel.settlement.dao.dal.htlcalculatefeetidb.entity.OrderAuditFgMqTiDBGen;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import soa.ctrip.com.hotel.order.checkin.audit.v2.getOrderAuditRoomData.OrderAuditRoomData;
import soa.ctrip.com.hotel.vendor.settlement.v1.settlementdata.SettlementPayData;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = CostJobApplication.class) // 使用junit4进行测试
public class SpringTest {

  @Autowired
  OrderAuditFgMqRepository orderAuditFgMqRepository;

  @Autowired
  SettlementClient settlementClient;

  @Autowired
  AuditClient auditClient;

  @Autowired
  FGNotifySettlementJob fgNotifySettlementJob;

  @Autowired
  HandlerApi handlerApi;

  @Autowired
  AuditMqProducer auditMqProducer;

  @Test
  public void auditOrderFgCollectPrice() {
    try {
      // set TransThreadLocal
      ThreadLocalCostHolder.setThreadLocalCostContext("auditOrderFg");

      List<Long> ids = new ArrayList<>();
      ids.add(560025073L);
      List<AuditOrderInfoBO> orders = handlerApi.auditOrderFgCollectPrice(ids);
      System.out.println(orders.size());
    } catch (Exception e) {
      ThreadLocalCostHolder.getTTL().remove();
    }
  }

  @Test
  public void dalTest() throws Exception {
    List<OrderAuditFgMqTiDBGen> orderAuditFgMqList =
            orderAuditFgMqRepository.getPendingJobs(Arrays.asList(1, 2), 30, 1);
    System.out.println(JSON.toJSONString(orderAuditFgMqList));
  }

  @Test
  public void getDetailDataTest(){
    List<OrderAuditRoomData> fgIdRes = auditClient.getOrderAuditRoomDataByFgId(Arrays.asList(560039249l));
    System.out.println(fgIdRes);
  }

  @Test
  public void throwOrderTest() {
//    CancelOrderUsedBo cancelOrderUsedBo = new CancelOrderUsedBo();
//    cancelOrderUsedBo.setOrderid("1l");
//    cancelOrderUsedBo.setOrderchannel(EnumHotelorderchannel.hpp);
//    cancelOrderUsedBo.setFGID(1l);
//    CancelOrderUsedBo.ToCancelDataUsed toCancelDataUsed = new CancelOrderUsedBo.ToCancelDataUsed();
//    toCancelDataUsed.setSettlementid(1l);
//    toCancelDataUsed.setOutsettlementno("1111111");
//    cancelOrderUsedBo.setCancelDataList(Arrays.asList(toCancelDataUsed));
//    CancelorderRequesttype cancelorderRequesttype = cancelOrderUsedBo.convertTo();
//    System.out.println(cancelorderRequesttype);
//    settlementClient.callCancelOrder(new CancelOrderDto(cancelorderRequesttype, "ashdjk"));



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
//    settlementClient.callSettlementApplyList(new SettlementApplyListDto(settleDataRequest, "12312"));




    SettlementCancelListUsedBo settlementCancelListUsedBo = new SettlementCancelListUsedBo();
    settlementCancelListUsedBo.setSettlementId(1l);
    settlementCancelListUsedBo.setOutSettlementNo("123");
    SettlementCancelListUsedBo.CancelDataItemUsed cancelDataItemUsed = new SettlementCancelListUsedBo.CancelDataItemUsed();
    cancelDataItemUsed.setDataDesc("123");
    settlementCancelListUsedBo.setCancelItems(Arrays.asList(cancelDataItemUsed));
    CancelSettleData cancelSettleData = settlementCancelListUsedBo.convertTo();
    System.out.println(cancelSettleData);
//    settlementClient.callSettlementCancelList(new SettlementCancelListDto(cancelSettleData, "5672318tiw"));



    SettlementPayDataUsedBo settlementPayDataUsedBo = new SettlementPayDataUsedBo();
    settlementPayDataUsedBo.setBidFlag("aaasssddd");
    SettlementPayDataUsedBo.OrderPromotionUsed orderPromotionUsed = new SettlementPayDataUsedBo.OrderPromotionUsed();
    orderPromotionUsed.setBeginDate(Calendar.getInstance());
    settlementPayDataUsedBo.setOrderPromotionList(Arrays.asList(orderPromotionUsed));
    SettlementPayData settlementPayData = settlementPayDataUsedBo.convertTo();
//    settlementClient.callSettlementPayDataReceive(new SettlementPayDataReceiveDto(settlementPayData, "123789gujhi"));
    System.out.println(settlementPayData);
  }


  @Test
  public void fgJudgeTest(){
    try {
      settlementClient.callConfigCanPush();
    } catch (Exception e) {
      throw new RuntimeException(e);
    }

    try {
      settlementClient.callCheckFGBidSplit(123, 123, "ss", true);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }


  @Test
  public void jobTest(){
    List<Integer> sliceList = new ArrayList<>();
    for (int i = 0; i < 32; i++) {
      sliceList.add(i);
    }
    try {
      fgNotifySettlementJob.execute(sliceList);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }


  @Test
  public void auditMqProducerTest(){
    FgBackToAuditDto fgBackToAuditDto = new FgBackToAuditDto();

    fgBackToAuditDto.setOrderId(1l);
    fgBackToAuditDto.setFgId(1l);
    fgBackToAuditDto.setBusinessType(1);
    fgBackToAuditDto.setOpType('c');
    fgBackToAuditDto.setReferenceId("111");
    fgBackToAuditDto.setIsThrow('1');

    fgBackToAuditDto.setSettlementId(1l);
    fgBackToAuditDto.setPushReferenceId("asd");
    fgBackToAuditDto.setHwpSettlementId(1l);
    fgBackToAuditDto.setPushWalletPay(true);
    fgBackToAuditDto.setHwpReferenceId("111");
    fgBackToAuditDto.setOrderInfoId(1l);

    fgBackToAuditDto.setAmount(new BigDecimal(1));
    fgBackToAuditDto.setCost(new BigDecimal(1));
    fgBackToAuditDto.setBidPrice(new BigDecimal(1));
    fgBackToAuditDto.setRoomAmount(new BigDecimal(1));
    fgBackToAuditDto.setRoomCost(new BigDecimal(1));
    fgBackToAuditDto.setPromotionAmountHotel(new BigDecimal(1));
    fgBackToAuditDto.setPromotionCostHotel(new BigDecimal(1));
    fgBackToAuditDto.setPromotionAmountTrip(new BigDecimal(1));
    fgBackToAuditDto.setPromotionCostTrip(new BigDecimal(1));

    try {
      auditMqProducer.fgBackToAudit(fgBackToAuditDto);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
    int i = 0;
  }

}
