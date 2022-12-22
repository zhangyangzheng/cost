package com.ctrip.hotel.cost.domain.data.model;

import lombok.Data;

import java.util.Calendar;

/**
 * @author yangzhengzhang
 * @description 审核房间基本数据
 * @date 2022-11-23 13:52
 */
@Data
public class AuditRoomBasicInfo {
    private Integer fgid;
    private Integer room;
    private Calendar eta;
    private Calendar etd;
    private Calendar realETD;
    private String roomNo;
    private String clientName;
    private String checkInType;
    private String hotelInfo;
    private String htlConfirmNo;
    private String groupConfirmNo;
    private String roomName;
    private String roomNameEN;
    private Integer newFGID;
    private String isRoomDelay;
    private Integer hourAdjuest;
    private String recheck;
    private String operateType;
    private String subOperateType;
    private String operator;
    private Calendar operateTime;
    private String noticeSettlementType;
}
