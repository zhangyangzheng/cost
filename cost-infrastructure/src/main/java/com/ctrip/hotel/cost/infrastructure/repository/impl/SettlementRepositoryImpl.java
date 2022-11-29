package com.ctrip.hotel.cost.infrastructure.repository.impl;

import com.ctrip.hotel.cost.domain.data.model.*;
import com.ctrip.hotel.cost.domain.settlement.CancelOrderUsedBo;
import com.ctrip.hotel.cost.domain.settlement.ChannelType;
import com.ctrip.hotel.cost.domain.settlement.SettlementItemName;
import com.ctrip.hotel.cost.infrastructure.client.SettlementClient;
import com.ctrip.hotel.cost.infrastructure.mapper.SettlementDataPOMapper;
import com.ctrip.hotel.cost.infrastructure.model.dto.CancelOrderDto;
import com.ctrip.hotel.cost.infrastructure.model.dto.SettlementApplyListDto;
import com.ctrip.hotel.cost.infrastructure.model.dto.SettlementPayDataReceiveDto;
import com.ctrip.soa.hotel.settlement.api.DataItem;
import com.ctrip.soa.hotel.settlement.api.SettleDataRequest;
import com.ctrip.soa.hotel.settlement.api.SettlementApplyListRequestType;
import hotel.settlement.common.BigDecimalHelper;
import hotel.settlement.common.ConvertHelper;
import hotel.settlement.common.DateHelper;
import hotel.settlement.common.QConfigHelper;
import hotel.settlement.common.helpers.DefaultValueHelper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import repository.SettlementRepository;
import soa.ctrip.com.hotel.vendor.settlement.v1.settlementdata.SettlementPayData;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Optional;

/**
 * @author yangzhengzhang
 * @description
 * @date 2022-11-18 14:51
 */
@Repository
public class SettlementRepositoryImpl implements SettlementRepository {

    @Autowired
    private SettlementClient settlementClient;


    @Override
    public boolean callCancelOrder(AuditOrderInfoBO auditOrderInfoBO) {
        return false;
    }

    @Override
    public boolean callCancelSettlementCancelList(AuditOrderInfoBO auditOrderInfoBO) {
        return false;
    }

    @Override
    public boolean callCancelSettlementCancelListHWP(AuditOrderInfoBO auditOrderInfoBO) {
        return false;
    }

    @Override
    public boolean callSettlementPayDataReceive(AuditOrderInfoBO auditOrderInfoBO) {
        SettlementPayData settlementPayData = SettlementDataPOMapper.INSTANCE.newOrderToSettlementPayDataReceive(auditOrderInfoBO);
        // 联合会员逻辑 todo 线上开关没开，这个业务什么时候开不一定，先写在这里过渡
        String associateMemberOrder = null;
        BigDecimal associateMemberCommissionRatio = null;
        Integer associateMemberHotelID = null;
        String associateMemberCommissionType = null;
        if (auditOrderInfoBO.getAssociateMember() != null) {
            associateMemberOrder = auditOrderInfoBO.getAssociateMember().getAssociateMemberOrder();
            associateMemberCommissionRatio = auditOrderInfoBO.getAssociateMember().getAssociateMemberCommissionRatio();
            associateMemberHotelID = auditOrderInfoBO.getAssociateMember().getAssociateMemberHotelID();
            associateMemberCommissionType = auditOrderInfoBO.getAssociateMember().getAssociateMemberCommissionType();
        }
        if (!StringUtils.isEmpty(associateMemberOrder)
                && "true"
                .equalsIgnoreCase(
                        QConfigHelper.getSwitchConfigByKey(
                                "AssociateMemberIntoSettlementSwitch", "false"))) {
            if ("1".equalsIgnoreCase(associateMemberOrder)) {
                BigDecimal associateMemberCommissionRatioNew;
                if (DefaultValueHelper.getValue(associateMemberCommissionRatio).compareTo(BigDecimal.ZERO)
                        > 0) {
                    associateMemberCommissionRatioNew = associateMemberCommissionRatio;
                } else {
                    associateMemberCommissionRatioNew =
                            BigDecimalHelper.getValue(
                                    QConfigHelper.getSwitchConfigByKey(
                                            "AssociateMemberCommissionRatioDefaultValue", "0.02"));
                }

                // 免佣(按比例收取技术服务费)
                settlementPayData.setUinonMemberFlag("T");
                settlementPayData.setServiceFeeProportion(associateMemberCommissionRatioNew);

                if (DefaultValueHelper.getValue(auditOrderInfoBO.getBuyoutDiscountAmount()).compareTo(BigDecimal.ZERO) >= 0) {
                    settlementPayData.setBuyoutDiscountAmount(DefaultValueHelper.getValue(auditOrderInfoBO.getBuyoutDiscountAmount()));
                }
            } else if ("3".equalsIgnoreCase(associateMemberOrder)
                    && DefaultValueHelper.getValue(associateMemberHotelID) > 0
                    && DefaultValueHelper.getValue(associateMemberCommissionRatio).compareTo(BigDecimal.ZERO)
                    > 0
                    && "Refund".equalsIgnoreCase(DefaultValueHelper.getValue(associateMemberCommissionType))) {
                // 返佣(按比例返酒店佣金)
                // 返佣(按比例返酒店佣金)
                settlementPayData.setRakebackFlag("T");
                settlementPayData.setRakebackHotelId(associateMemberHotelID.toString());
                settlementPayData.setRakebackRate(associateMemberCommissionRatio);
                settlementPayData.setTripPromotionAmount(auditOrderInfoBO.getTripPromotionAmount());
            }
        }
        return settlementClient.callSettlementPayDataReceive(
                new SettlementPayDataReceiveDto(settlementPayData,
                        ""// todo set referenceId
                )
        );
    }

  @Override
  public boolean callSettlementApplyList(AuditOrderInfoBO auditOrderInfoBO) {
    try{
      OrderBasicInfo orderBasicInfo = auditOrderInfoBO.getOrderBasicInfo();
      SettlementCallBackInfo settlementCallBackInfo = auditOrderInfoBO.getSettlementCallBackInfo();
      AuditRoomInfo auditRoomInfo = auditOrderInfoBO.getAuditRoomInfoList().get(0);
      AuditRoomBasicInfo auditRoomBasicInfo = auditRoomInfo.getAuditRoomBasicInfo();
      AuditRoomOtherInfo auditRoomOtherInfo = auditRoomInfo.getAuditRoomOtherInfo();
      GuaranteeInfo guaranteeInfo = auditOrderInfoBO.getGuaranteeInfo();
      FlashOrderInfo flashOrderInfo = auditOrderInfoBO.getFlashOrderInfo();
      HotelBasicInfo hotelBasicInfo = auditOrderInfoBO.getHotelBasicInfo();
      AllianceInfo allianceInfo = auditOrderInfoBO.getAllianceInfo();
      TechFeeInfo techFeeInfo = auditOrderInfoBO.getTechFeeInfo();
      OutTimeDeductInfo outTimeDeductInfo = auditOrderInfoBO.getOutTimeDeductInfo();

      SettleDataRequest requestData =
          SettlementDataPOMapper.INSTANCE.newOrderToSettlementApplyList(auditOrderInfoBO);
      //        requestData.setCollectionType("P");
      //        requestData.setCompanyID(getSettlementProvider(dr));
      //        requestData.setCurrency(dr.getCurrency() == null ? "" : dr.getCurrency());
      //        requestData.setId(0);
      //        requestData.setMerchantId(6);
      if (orderBasicInfo.getOrderDate() != null) {
        requestData.setOrderDate(orderBasicInfo.getOrderDate());
      }
      //
      //        requestData.setOrderId(dr.getOrderID() == null ? "" :
      // String.valueOf(dr.getOrderID()));
      if (DefaultValueHelper.getValue(auditRoomBasicInfo.getFgid()) > 0) {
        requestData.setOutSettlementNo("FG-" + auditRoomBasicInfo.getFgid());
      } else {
        new Exception("tmp stage fgId should greater than zero");
      }

      requestData.setProductCode(
          auditRoomBasicInfo.getRoomName() == null ? "" : auditRoomBasicInfo.getRoomName());
      requestData.setQuantity(
          auditOrderInfoBO.getQuantity() == null ? BigDecimal.ZERO : auditOrderInfoBO.getQuantity());
      requestData.setSettlementItemName(SettlementItemName.FGHotel.getShowName());
      //        requestData.setSettlementPriceType("P");
      //        requestData.setSourceId("6");
      requestData.setChannelType(ChannelType.FGID.name());

      // 如果为修改数据则传SettlementId
      if ("U".equals(auditOrderInfoBO.getOpType())
          && DefaultValueHelper.getValue(settlementCallBackInfo.getSettlementId()) > 0) {
        requestData.setSettlementId(settlementCallBackInfo.getSettlementId());
      }

      requestData.setDataItems(new ArrayList<>());
      DataItem Item = new DataItem();

      Item.setDataKey("Room");
      Item.setDataValue(
          auditRoomBasicInfo.getRoom() == null ? "" : auditRoomBasicInfo.getRoom().toString());
      Item.setDataDesc("房型代码");
      requestData.getDataItems().add(Item);

      Item = new DataItem();
      Item.setDataKey("RoomNo");
      Item.setDataValue(auditRoomBasicInfo.getRoomNo() == null ? "" : auditRoomBasicInfo.getRoomNo());
      Item.setDataDesc("房间号");
      requestData.getDataItems().add(Item);

      if (ConvertHelper.getLong(auditOrderInfoBO.getCusOrderId()) > 0) {
        Item = new DataItem();
        Item.setDataKey("ClientOrderId");
        Item.setDataValue(ConvertHelper.getStr(auditOrderInfoBO.getCusOrderId()));
        Item.setDataDesc("ClientOrderId");
        requestData.getDataItems().add(Item);
      }

      Item = new DataItem();
      Item.setDataKey("CheckInType");
      Item.setDataValue(
          auditRoomBasicInfo.getCheckInType() == null ? "" : auditRoomBasicInfo.getCheckInType());
      Item.setDataDesc("入住审核");
      requestData.getDataItems().add(Item);

      Item = new DataItem();
      Item.setDataKey("ETA");
      Item.setDataValue(
          orderBasicInfo.getEta() == null
              ? ""
              : DateHelper.formatDate(
                  new Timestamp(orderBasicInfo.getEta().getTimeInMillis()),
                  DateHelper.SIMIPLE_DATE_FORMAT_STR));
      Item.setDataDesc("入住时间");
      requestData.getDataItems().add(Item);

      Item = new DataItem();
      Item.setDataKey("ETD");
      Item.setDataValue(
          orderBasicInfo.getEtd() == null
              ? ""
              : DateHelper.formatDate(
                  new Timestamp(orderBasicInfo.getEtd().getTimeInMillis()),
                  DateHelper.SIMIPLE_DATE_FORMAT_STR));
      Item.setDataDesc("离店时间");
      requestData.getDataItems().add(Item);

      Item = new DataItem();
      Item.setDataKey("CommissionType");
      Item.setDataValue(
          auditRoomOtherInfo.getAdjustCommissionType() == null
              ? ""
              : auditRoomOtherInfo.getAdjustCommissionType().toString());
      Item.setDataDesc("CommissionType    0 正常订单，1 NS付卖价收佣金，2 酒店扣款，3 NoShow收酒店佣金，4 赔款订单，5 应收调整订单");
      requestData.getDataItems().add(Item);

      Item = new DataItem();
      Item.setDataKey("PriceAmount");
      Item.setDataValue(
          auditOrderInfoBO.getPriceAmount() == null
              ? ""
              : auditOrderInfoBO.getPriceAmount().toString());
      Item.setDataDesc("订单面价");
      requestData.getDataItems().add(Item);

      Item = new DataItem();
      Item.setDataKey("CostAmount");
      Item.setDataValue(
          auditOrderInfoBO.getCostAmount() == null
              ? ""
              : auditOrderInfoBO.getCostAmount().toString());
      Item.setDataDesc("订单底价");
      requestData.getDataItems().add(Item);

      Item = new DataItem();
      Item.setDataKey("RMBExchangeRate");
      Item.setDataValue(
          orderBasicInfo.getExchange() == null ? "" : orderBasicInfo.getExchange().toString());
      Item.setDataDesc("人民币汇率");
      requestData.getDataItems().add(Item);

      Item = new DataItem();
      Item.setDataKey("ClientName");
      Item.setDataValue(
          auditRoomBasicInfo.getClientName() == null ? "" : auditRoomBasicInfo.getClientName());
      Item.setDataDesc("入住者");
      requestData.getDataItems().add(Item);

      Item = new DataItem();
      Item.setDataKey("Remarks");
      Item.setDataValue("");
      Item.setDataDesc("备注");
      requestData.getDataItems().add(Item);

      Item = new DataItem();
      Item.setDataKey("HtlConfirmNo");
      Item.setDataValue(
          auditRoomBasicInfo.getHtlConfirmNo() == null ? "" : auditRoomBasicInfo.getHtlConfirmNo());
      Item.setDataDesc("确认号");
      requestData.getDataItems().add(Item);

      Item = new DataItem();
      Item.setDataKey("RoomName");
      Item.setDataValue(
          auditRoomBasicInfo.getRoomName() == null ? "" : auditRoomBasicInfo.getRoomName());
      Item.setDataDesc("房型名称");
      requestData.getDataItems().add(Item);

      Item = new DataItem();
      Item.setDataKey("RealETD");
      Item.setDataValue(
          auditRoomBasicInfo.getRealETD() == null
              ? ""
              : DateHelper.formatDate(
                  new Timestamp(auditRoomBasicInfo.getRealETD().getTimeInMillis()),
                  DateHelper.SIMIPLE_DATE_FORMAT_STR));
      Item.setDataDesc("审核实际离店日期");
      requestData.getDataItems().add(Item);

      Item = new DataItem();
      Item.setDataKey("HotelInfo");
      Item.setDataValue(
          auditRoomBasicInfo.getHotelInfo() == null ? "" : auditRoomBasicInfo.getHotelInfo());
      Item.setDataDesc("宾馆信息");
      requestData.getDataItems().add(Item);

      Item = new DataItem();
      Item.setDataKey("Guarantee");
      Item.setDataValue(guaranteeInfo.getGuarantee() == null ? "" : guaranteeInfo.getGuarantee());
      Item.setDataDesc("是否担保(G首日/F)");
      requestData.getDataItems().add(Item);

      Item = new DataItem();
      Item.setDataKey("GuaranteeWay");
      Item.setDataValue(
          guaranteeInfo.getGuaranteeWay() == null ? "" : guaranteeInfo.getGuaranteeWay());
      Item.setDataDesc("担保类型C信用卡担保，E积分担保");
      requestData.getDataItems().add(Item);

      Item = new DataItem();
      Item.setDataKey("NewFGID");
      Item.setDataValue(
          auditRoomBasicInfo.getNewFGID() == null ? "" : auditRoomBasicInfo.getNewFGID().toString());
      Item.setDataDesc("延住后对应房间的FGID(FGID > 0 存在延住）");
      requestData.getDataItems().add(Item);

      Item = new DataItem();
      Item.setDataKey("IsRoomDelay");
      Item.setDataValue(
          auditRoomBasicInfo.getIsRoomDelay() == null ? "" : auditRoomBasicInfo.getIsRoomDelay());
      Item.setDataDesc("是否为延住房间(R预定延住/A审核延住/F非延住)");
      requestData.getDataItems().add(Item);

      Item = new DataItem();
      Item.setDataKey("Recheck");
      Item.setDataValue(
          auditRoomBasicInfo.getRecheck() == null ? "" : auditRoomBasicInfo.getRecheck());
      Item.setDataDesc("复审标记(T复审)");
      requestData.getDataItems().add(Item);

      Item = new DataItem();
      Item.setDataKey("AllNeedGuarantee");
      Item.setDataValue(
          guaranteeInfo.getAllNeedGuarantee() == null ? "" : guaranteeInfo.getAllNeedGuarantee());
      Item.setDataDesc("担保类型(T全程/B峰时/F)");
      requestData.getDataItems().add(Item);

      Item = new DataItem();
      Item.setDataKey("VendorChannelID");
      Item.setDataValue(
          orderBasicInfo.getVendorChannelID() == null
              ? ""
              : orderBasicInfo.getVendorChannelID().toString());
      Item.setDataDesc("集团酒店Code");
      requestData.getDataItems().add(Item);

      Item = new DataItem();
      Item.setDataKey("ModifyOperateType");
      Item.setDataValue(
          auditRoomBasicInfo.getOperateType() == null ? "" : auditRoomBasicInfo.getOperateType());
      Item.setDataDesc("修改类型");
      requestData.getDataItems().add(Item);

      Item = new DataItem();
      Item.setDataKey("ModifyOperateSubType");
      Item.setDataValue(
          auditRoomBasicInfo.getSubOperateType() == null
              ? ""
              : auditRoomBasicInfo.getSubOperateType());
      Item.setDataDesc("修改子类型");
      requestData.getDataItems().add(Item);

      Item = new DataItem();
      Item.setDataKey("ModifyOperateDateTime");
      Item.setDataValue(
          auditRoomBasicInfo.getOperateTime() == null
              ? ""
              : DateHelper.formatDate(
                  new Timestamp(auditRoomBasicInfo.getOperateTime().getTimeInMillis()),
                  DateHelper.SIMIPLE_DATE_FORMAT_STR));
      Item.setDataDesc("操作时间");
      requestData.getDataItems().add(Item);

      Item = new DataItem();
      Item.setDataKey("ModifyOperateEid");
      Item.setDataValue(
          auditRoomBasicInfo.getOperator() == null ? "" : auditRoomBasicInfo.getOperator());
      Item.setDataDesc("操作人");
      requestData.getDataItems().add(Item);

      Item = new DataItem();
      Item.setDataKey("GroupOrderClass");
      Item.setDataValue(
          orderBasicInfo.getGroupOrderClass() == null ? "" : orderBasicInfo.getGroupOrderClass());
      Item.setDataDesc("系统外订单属性");
      requestData.getDataItems().add(Item);

      Item = new DataItem();
      Item.setDataKey("IsWalletPay");
      Item.setDataValue(
          flashOrderInfo.getIsFlashOrder() == null
              ? ""
              : flashOrderInfo.getIsFlashOrder().toString());
      Item.setDataDesc("是否闪住");
      requestData.getDataItems().add(Item);

      Item = new DataItem();
      Item.setDataKey("HotelConfirmStatus");
      Item.setDataValue(
          auditRoomOtherInfo.getHotelConfirmStatus() == null
              ? "0"
              : auditRoomOtherInfo.getHotelConfirmStatus().toString());
      Item.setDataDesc("酒店确认状态");
      requestData.getDataItems().add(Item);

      Item = new DataItem();
      Item.setDataKey("OrderConfirmType");
      Item.setDataValue(
          orderBasicInfo.getOrderConfirmType() == null ? "" : orderBasicInfo.getOrderConfirmType());
      Item.setDataDesc("订单确认类型");
      requestData.getDataItems().add(Item);

      // 是否为阶梯扣款
      Item = new DataItem();
      Item.setDataKey("IsLadderDeduct");
      Item.setDataValue(
          orderBasicInfo.getIsLadderDeductPolicy() == null
              ? ""
              : orderBasicInfo.getIsLadderDeductPolicy());
      Item.setDataDesc("是否为阶梯扣款");
      requestData.getDataItems().add(Item);

      if (auditOrderInfoBO.getBidPrice() != null) {
        Item = new DataItem();
        Item.setDataKey("BidFlag");
        Item.setDataValue("T");
        Item.setDataDesc("是否为云梯订单");
        requestData.getDataItems().add(Item);

        Item = new DataItem();
        Item.setDataKey("BidPrice");
        Item.setDataValue(auditOrderInfoBO.getBidPrice().toString());
        Item.setDataDesc("云梯费用");
        requestData.getDataItems().add(Item);
      }

      Item = new DataItem();
      Item.setDataKey("FGID");
      Item.setDataValue(
          auditRoomBasicInfo.getFgid() == null ? "0" : auditRoomBasicInfo.getFgid().toString());
      Item.setDataDesc("FGID");
      requestData.getDataItems().add(Item);

      Item = new DataItem();
      Item.setDataKey("IsRapidSettlement");
      Item.setDataValue(
          hotelBasicInfo.getOperatMode() == null || !"S".equals(hotelBasicInfo.getOperatMode())
              ? "F"
              : "T");
      Item.setDataDesc("是否闪结订单");
      requestData.getDataItems().add(Item);

      Item = new DataItem();
      Item.setDataKey("AdjustAmount");
      Item.setDataValue(
          auditRoomOtherInfo.getAdjustCommission() == null
              ? "0"
              : auditRoomOtherInfo.getAdjustCommission().toString());
      Item.setDataDesc("应收调整金额");
      requestData.getDataItems().add(Item);

      Item = new DataItem();
      Item.setDataKey("AdjustAmountType");
      Item.setDataValue(
          auditRoomOtherInfo.getAdjustCommissionType() == null
              ? ""
              : auditRoomOtherInfo.getAdjustCommissionType().toString());
      Item.setDataDesc("应收调整类型");
      requestData.getDataItems().add(Item);

      Item = new DataItem();
      Item.setDataKey("AdjustAmountRemark");
      Item.setDataValue(
          auditRoomOtherInfo.getAdjustRemark() == null ? "" : auditRoomOtherInfo.getAdjustRemark());
      Item.setDataDesc("应收调整原因");
      requestData.getDataItems().add(Item);

      Item = new DataItem();
      Item.setDataKey("GuaranteeType");
      Item.setDataValue(
          guaranteeInfo.getGuaranteeType() == null
              ? ""
              : guaranteeInfo.getGuaranteeType().toString());
      Item.setDataDesc("担保类型");
      requestData.getDataItems().add(Item);

      Item = new DataItem();
      Item.setDataKey("RoomNameEn");
      Item.setDataValue(
          auditRoomBasicInfo.getRoomNameEN() == null ? "" : auditRoomBasicInfo.getRoomNameEN());
      Item.setDataDesc("英文房型名称");
      requestData.getDataItems().add(Item);

      Item = new DataItem();
      Item.setDataKey("HotelID");
      Item.setDataValue(
          hotelBasicInfo.getHotel() == null ? "0" : hotelBasicInfo.getHotel().toString());
      Item.setDataDesc("子酒店ID");
      requestData.getDataItems().add(Item);

      Item = new DataItem();
      Item.setDataKey("AdjustBatchId");
      Item.setDataValue(
          auditRoomOtherInfo.getSettlementBatchID() == null
              ? "0"
              : auditRoomOtherInfo.getSettlementBatchID().toString());
      Item.setDataDesc("批次ID");
      requestData.getDataItems().add(Item);

      Item = new DataItem();
      Item.setDataKey("RoomAmount");
      Item.setDataValue(
          auditOrderInfoBO.getRoomAmount() == null
              ? "0"
              : auditOrderInfoBO.getRoomAmount().toString());
      Item.setDataDesc("我司面价");
      requestData.getDataItems().add(Item);

      Item = new DataItem();
      Item.setDataKey("RoomCost");
      Item.setDataValue(
          auditOrderInfoBO.getRoomCost() == null ? "0" : auditOrderInfoBO.getRoomCost().toString());
      Item.setDataDesc("我司底价");
      requestData.getDataItems().add(Item);

      if (orderBasicInfo.getCurrency() != null) {
        Item = new DataItem();
        Item.setDataKey("RoomCurrency");
        Item.setDataValue(orderBasicInfo.getCurrency() == null ? "" : orderBasicInfo.getCurrency());
        Item.setDataDesc("我司币种");
        requestData.getDataItems().add(Item);
      }

      if (auditRoomOtherInfo.getPayCurrency() != null) {
        Item = new DataItem();
        Item.setDataKey("BillsettleCurrency");
        Item.setDataValue(
            auditRoomOtherInfo.getPayCurrency() == null ? "" : auditRoomOtherInfo.getPayCurrency());
        Item.setDataDesc("账单结算币种");
        requestData.getDataItems().add(Item);
      }

      if (auditRoomOtherInfo.getHtlCurrency() != null) {
        Item = new DataItem();
        Item.setDataKey("BillHtlCurrency");
        Item.setDataValue(
            auditRoomOtherInfo.getHtlCurrency() == null ? "" : auditRoomOtherInfo.getHtlCurrency());
        Item.setDataDesc("账单酒店币种");
        requestData.getDataItems().add(Item);
      }

      Item = new DataItem();
      Item.setDataKey("CounterFee");
      Item.setDataValue(
          auditRoomOtherInfo.getCounterFee() == null
              ? "0"
              : auditRoomOtherInfo.getCounterFee().toString());
      Item.setDataDesc("手续费");
      requestData.getDataItems().add(Item);

      if (auditRoomOtherInfo.getCounterFeeCurrency() != null) {
        Item = new DataItem();
        Item.setDataKey("CounterFeeCurrency");
        Item.setDataValue(
            auditRoomOtherInfo.getCounterFeeCurrency() == null
                ? ""
                : auditRoomOtherInfo.getCounterFeeCurrency());
        Item.setDataDesc("手续费币种");
        requestData.getDataItems().add(Item);
      }

      Item = new DataItem();
      Item.setDataKey("PaidCommissionFlag");
      Item.setDataValue(DefaultValueHelper.getValue(flashOrderInfo.getIsFlashOrder()) ? "1" : "0");
      Item.setDataDesc("收佣标识");
      requestData.getDataItems().add(Item);

      if (orderBasicInfo.getConfirmIncomeTime() != null) {
        Item = new DataItem();
        Item.setDataKey("ConfirmIncomeTime");
        Item.setDataValue(
            orderBasicInfo.getConfirmIncomeTime() == null
                ? ""
                : DateHelper.formatDate(
                    new Timestamp(orderBasicInfo.getConfirmIncomeTime().getTimeInMillis()),
                    DateHelper.SIMIPLE_DATE_FORMAT_STR));
        Item.setDataDesc("收入确认时间");
        requestData.getDataItems().add(Item);
      }

      if (orderBasicInfo.getInsuranceSupportType() != null) {
        Item = new DataItem();
        Item.setDataKey("InsuranceFlag");
        Item.setDataValue(orderBasicInfo.getInsuranceSupportType().toString());
        Item.setDataDesc("保险标识");
        requestData.getDataItems().add(Item);
      }

      // 订单UID
      if (orderBasicInfo.getUid() != null) {
        Item = new DataItem();
        Item.setDataKey("Uid");
        Item.setDataValue(orderBasicInfo.getUid());
        Item.setDataDesc("Uid");
        requestData.getDataItems().add(Item);
      }

      Item = new DataItem();
      Item.setDataKey("AllianceID");
      Item.setDataValue(
          allianceInfo.getAllianceID() == null ? "" : allianceInfo.getAllianceID().toString());
      Item.setDataDesc("分销ID");
      requestData.getDataItems().add(Item);

      Item = new DataItem();
      Item.setDataKey("Allianceflag");
      Item.setDataValue(DefaultValueHelper.getValue(allianceInfo.getAllianceID()) > 0 ? "1" : "0");
      Item.setDataDesc("分销标识");
      requestData.getDataItems().add(Item);

      Item = new DataItem();
      Item.setDataKey("SID");
      Item.setDataValue(allianceInfo.getSID() == null ? "" : allianceInfo.getSID().toString());
      Item.setDataDesc("分销站点ID");
      requestData.getDataItems().add(Item);

      Item = new DataItem();
      Item.setDataKey("AllianceOrderID");
      Item.setDataValue(
          allianceInfo.getAllianceOrderID() == null ? "" : allianceInfo.getAllianceOrderID());
      Item.setDataDesc("分销订单号");
      requestData.getDataItems().add(Item);

      if (techFeeInfo.getZeroCommissionFeeRatio() != null
          && techFeeInfo.getZeroCommissionFeeRatio().compareTo(BigDecimal.ZERO) > 0) {
        BigDecimal zeroCommissionAmount = auditOrderInfoBO.getZeroCommissionAmount();

        Item = new DataItem();
        Item.setDataKey("ZeroCommissionAmount");
        Item.setDataValue(zeroCommissionAmount.toString());
        Item.setDataDesc("零佣技术服务费");
        requestData.getDataItems().add(Item);

        Item = new DataItem();
        Item.setDataKey("ZeroCommissionDeductRate");
        Item.setDataValue(techFeeInfo.getZeroCommissionFeeRatio().toString());
        Item.setDataDesc("零佣技术服务费比例");
        requestData.getDataItems().add(Item);
      }

      Item = new DataItem();
      Item.setDataKey("outTimeDeductValue");
      Item.setDataValue(
          outTimeDeductInfo.getOutTimeDeductValue() == null
              ? ""
              : outTimeDeductInfo.getOutTimeDeductValue().toString());
      Item.setDataDesc("过时扣款类型对应的值");
      requestData.getDataItems().add(Item);

      Item = new DataItem();
      Item.setDataKey("outTimeDeductType");
      Item.setDataValue(
          outTimeDeductInfo.getOutTimeDeductType() == null
              ? ""
              : outTimeDeductInfo.getOutTimeDeductType().toString());
      Item.setDataDesc("过时扣款类型");
      requestData.getDataItems().add(Item);

      return settlementClient.callSettlementApplyList(new SettlementApplyListDto(requestData, "jksda"));
    }catch (Exception e){
      return false;
    }

  }

  @Override
  public boolean callSettlementApplyListHWP(AuditOrderInfoBO auditOrderInfoBO) {
    try {

      AuditRoomInfo auditRoomInfo = auditOrderInfoBO.getAuditRoomInfoList().get(0);
      AuditRoomBasicInfo auditRoomBasicInfo = auditRoomInfo.getAuditRoomBasicInfo();
      AuditRoomOtherInfo auditRoomOtherInfo = auditRoomInfo.getAuditRoomOtherInfo();
      HotelBasicInfo hotelBasicInfo = auditOrderInfoBO.getHotelBasicInfo();
      SettlementCallBackInfo settlementCallBackInfo = auditOrderInfoBO.getSettlementCallBackInfo();
      OrderBasicInfo orderBasicInfo = auditOrderInfoBO.getOrderBasicInfo();
      TechFeeInfo techFeeInfo = auditOrderInfoBO.getTechFeeInfo();
      OutTimeDeductInfo outTimeDeductInfo = auditOrderInfoBO.getOutTimeDeductInfo();

      SettleDataRequest requestData =
          SettlementDataPOMapper.INSTANCE.newOrderToSettlementApplyList(auditOrderInfoBO);
      requestData.setCollectionType("C");
      //        requestData.setCompanyID(dr.getHotel() == null ? "" : dr.getHotel().toString());
      //        requestData.setCurrency(DefaultValueHelper.getValue(dr.getCurrency()));
      //        requestData.setId(0);
      //        requestData.setMerchantId(6);
      //        if (dr.getOrderDate() != null) {
      //            requestData.setOrderDate(DateHelper.parseToCalendar(dr.getOrderDate()));
      //        }
      //
      //        requestData.setOrderId(dr.getOrderID() == null ? "" : dr.getOrderID().toString());
      requestData.setChannelType(ChannelType.FGID_HWP.name());
      long FGID = DefaultValueHelper.getValue(auditRoomBasicInfo.getFgid());
      if (FGID > 0) {
        // RFD: Refunded，2571现付闪住退补款
        requestData.setOutSettlementNo(
            !DefaultValueHelper.getValue(settlementCallBackInfo.getPushWalletPay())
                ? "HWP-" + String.valueOf(FGID)
                : "RFD-" + String.valueOf(FGID));
      } else {
        new Exception("tmp stage fgId should greater than zero");
      }

      if ("U".equals(auditOrderInfoBO.getOpType())) {
        requestData.setSettlementId(settlementCallBackInfo.getSettlementId());
        if (DefaultValueHelper.getValue(requestData.getSettlementId()) <= 0) {
          throw new Exception("Abnormal HWPSettlementId parameter modification");
        }
      }

      requestData.setProductCode(DefaultValueHelper.getValue(auditRoomBasicInfo.getRoomName()));
      requestData.setQuantity(DefaultValueHelper.getValue(auditOrderInfoBO.getQuantity()));
      requestData.setSettlementItemName(SettlementItemName.HotelWalletPay.getShowName());
      requestData.setSettlementPriceType("P".equals(hotelBasicInfo.getPaymentType()) ? "P" : "C");
      requestData.setSourceId("6");

      requestData.setDataItems(new ArrayList<>());
      DataItem Item = new DataItem();
      Item.setDataKey("RoomName");
      Item.setDataValue(DefaultValueHelper.getValue(auditRoomBasicInfo.getRoomName()));
      Item.setDataDesc("房型名称");
      requestData.getDataItems().add(Item);

      if (ConvertHelper.getLong(auditOrderInfoBO.getCusOrderId()) > 0) {
        Item = new DataItem();
        Item.setDataKey("ClientOrderId");
        Item.setDataValue(ConvertHelper.getStr(auditOrderInfoBO.getCusOrderId()));
        Item.setDataDesc("ClientOrderId");
        requestData.getDataItems().add(Item);
      }

      Item = new DataItem();
      Item.setDataKey("BeginDate");
      Item.setDataValue(
          orderBasicInfo.getEta() == null
              ? ""
              : DateHelper.formatDate(
                  new Timestamp(orderBasicInfo.getEta().getTimeInMillis()),
                  DateHelper.SIMIPLE_DATE_FORMAT_STR));
      Item.setDataDesc("入住时间");
      requestData.getDataItems().add(Item);

      Item = new DataItem();
      Item.setDataKey("EndDate");
      Item.setDataValue(
          orderBasicInfo.getEtd() == null
              ? ""
              : DateHelper.formatDate(
                  new Timestamp(orderBasicInfo.getEtd().getTimeInMillis()),
                  DateHelper.SIMIPLE_DATE_FORMAT_STR));
      Item.setDataDesc("离店时间");
      requestData.getDataItems().add(Item);

      Item = new DataItem();
      Item.setDataKey("ClientName");
      Item.setDataValue(DefaultValueHelper.getValue(auditRoomBasicInfo.getClientName()));
      Item.setDataDesc("客人姓名");
      requestData.getDataItems().add(Item);

      Item = new DataItem();
      Item.setDataKey("Amount");
      Item.setDataValue(
          auditOrderInfoBO.getPriceAmount() == null
              ? ""
              : auditOrderInfoBO.getPriceAmount().toString());
      Item.setDataDesc("面价");
      requestData.getDataItems().add(Item);

      Item = new DataItem();
      Item.setDataKey("Cost");
      Item.setDataValue(
          auditOrderInfoBO.getCostAmount() == null
              ? ""
              : auditOrderInfoBO.getCostAmount().toString());
      Item.setDataDesc("底价");
      requestData.getDataItems().add(Item);

      Item = new DataItem();
      Item.setDataKey("Remark");
      Item.setDataValue("");
      Item.setDataDesc("备注");
      requestData.getDataItems().add(Item);

      Item = new DataItem();
      Item.setDataKey("OtherCost");
      Item.setDataValue(
          auditRoomOtherInfo.getOtherCost() == null
              ? ""
              : auditRoomOtherInfo.getOtherCost().toString());
      Item.setDataDesc("其他金额");
      requestData.getDataItems().add(Item);

      Item = new DataItem();
      Item.setDataKey("Room");
      Item.setDataValue(
          auditRoomBasicInfo.getRoom() == null ? "" : auditRoomBasicInfo.getRoom().toString());
      Item.setDataDesc("房型代码");
      requestData.getDataItems().add(Item);

      Item = new DataItem();
      Item.setDataKey("HtlConfirmNo");
      Item.setDataValue(
          auditRoomBasicInfo.getHtlConfirmNo() == null ? "" : auditRoomBasicInfo.getHtlConfirmNo());
      Item.setDataDesc("确认号");
      requestData.getDataItems().add(Item);

      Item = new DataItem();
      Item.setDataKey("Hotel");
      Item.setDataValue(
          hotelBasicInfo.getHotel() == null ? "0" : hotelBasicInfo.getHotel().toString());
      Item.setDataDesc("子酒店ID");
      requestData.getDataItems().add(Item);

      Item = new DataItem();
      Item.setDataKey("OrderConfirmType");
      Item.setDataValue(
          orderBasicInfo.getOrderConfirmType() == null ? "" : orderBasicInfo.getOrderConfirmType());
      Item.setDataDesc("订单确认类型");
      requestData.getDataItems().add(Item);

      if (orderBasicInfo.getConfirmIncomeTime() != null) {
        Item = new DataItem();
        Item.setDataKey("ConfirmIncomeTime");
        Item.setDataValue(
            DateHelper.formatDate(
                new Timestamp(orderBasicInfo.getConfirmIncomeTime().getTimeInMillis()),
                DateHelper.SIMIPLE_DATE_FORMAT_STR));
        Item.setDataDesc("收入确认时间");
        requestData.getDataItems().add(Item);
      }

      if (orderBasicInfo.getInsuranceSupportType() != null) {
        Item = new DataItem();
        Item.setDataKey("InsuranceFlag");
        Item.setDataValue(orderBasicInfo.getInsuranceSupportType().toString());
        Item.setDataDesc("保险标识");
        requestData.getDataItems().add(Item);
      }

      // 订单UID
      if (orderBasicInfo.getUid() != null) {
        Item = new DataItem();
        Item.setDataKey("Uid");
        Item.setDataValue(DefaultValueHelper.getValue(orderBasicInfo.getUid()));
        Item.setDataDesc("Uid");
        requestData.getDataItems().add(Item);
      }

      if (techFeeInfo.getZeroCommissionFeeRatio() != null
          && techFeeInfo.getZeroCommissionFeeRatio().compareTo(BigDecimal.ZERO) > 0) {
        BigDecimal zeroCommissionAmount =
            auditOrderInfoBO.getCostAmount().multiply(techFeeInfo.getZeroCommissionFeeRatio());

        Item = new DataItem();
        Item.setDataKey("ZeroCommissionAmount");
        Item.setDataValue(zeroCommissionAmount.toString());
        Item.setDataDesc("零佣技术服务费");
        requestData.getDataItems().add(Item);

        Item = new DataItem();
        Item.setDataKey("ZeroCommissionDeductRate");
        Item.setDataValue(techFeeInfo.getZeroCommissionFeeRatio().toString());
        Item.setDataDesc("零佣技术服务费比例");
        requestData.getDataItems().add(Item);
      }

      Item = new DataItem();
      Item.setDataKey("outTimeDeductValue");
      Item.setDataValue(
          outTimeDeductInfo.getOutTimeDeductValue() == null
              ? ""
              : outTimeDeductInfo.getOutTimeDeductValue().toString());
      Item.setDataDesc("过时扣款类型对应的值");
      requestData.getDataItems().add(Item);

      Item = new DataItem();
      Item.setDataKey("outTimeDeductType");
      Item.setDataValue(
          outTimeDeductInfo.getOutTimeDeductType() == null
              ? ""
              : outTimeDeductInfo.getOutTimeDeductType().toString());
      Item.setDataDesc("过时扣款类型");
      requestData.getDataItems().add(Item);

      return settlementClient.callSettlementApplyList(new SettlementApplyListDto(requestData, "ssa"));
    } catch (Exception e) {
      return false;
    }
  }
}
