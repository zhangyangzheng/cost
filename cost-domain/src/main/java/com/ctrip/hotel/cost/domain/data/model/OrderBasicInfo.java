package com.ctrip.hotel.cost.domain.data.model;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Calendar;

/**
 * @author yangzhengzhang
 * @description
 * @date 2022-11-23 11:15
 */
@Data
public class OrderBasicInfo {
    private Integer room;
    private Calendar eta;
    private Calendar etd;
    private Calendar orderDate;
    private String status;
    private String uid;
    private String currency;
    private BigDecimal exchange;
    private Integer roomNum;
    private Integer vendorChannelID;
    private String groupOrderClass;
    private String hotelGroupVipID;
    private String orderConfirmType;
    private Calendar confirmIncomeTime;
    private String ownedBU;
    private String quickPaymentGuaranteePoly;
    private String isReturnCash;
    private Integer insuranceSupportType;
    private String isLadderDeductPolicy;
    private String hourRoom;
}
