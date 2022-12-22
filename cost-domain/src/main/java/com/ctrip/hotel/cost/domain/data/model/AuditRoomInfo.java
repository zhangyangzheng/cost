package com.ctrip.hotel.cost.domain.data.model;

import lombok.Data;

/**
 * @author yangzhengzhang
 * @description 审核房间数据
 * @date 2022-11-23 13:50
 */
@Data
public class AuditRoomInfo {
    private AuditRoomBasicInfo auditRoomBasicInfo;
    private AuditRoomOtherInfo auditRoomOtherInfo;
}
