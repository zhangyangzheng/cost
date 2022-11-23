package com.ctrip.hotel.cost.domain.data.model;

import lombok.Data;

import java.util.Calendar;

/**
 * @author yangzhengzhang
 * @description 酒店基本信息
 * @date 2022-11-23 11:33
 */
@Data
public class HotelBasicInfo {
    private Integer hotel;
    private String hotelName;
    private String hotelNameEN;
    private String hotelBelongTo;
    private Integer supplierID;
    private String supplierName;
    private String operatMode;
    private Calendar openDate;
    private String paymentType;
}
