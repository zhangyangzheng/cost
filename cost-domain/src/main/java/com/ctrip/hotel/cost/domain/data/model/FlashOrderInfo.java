package com.ctrip.hotel.cost.domain.data.model;

import lombok.Data;

import java.util.Calendar;

/**
 * @author yangzhengzhang
 * @description 闪住
 * @date 2022-11-23 11:25
 */
@Data
public class FlashOrderInfo {
    private Boolean isFlashOrder;
    private Calendar anticipationAuditDate;
}
