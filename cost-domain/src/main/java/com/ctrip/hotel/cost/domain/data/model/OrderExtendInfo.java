package com.ctrip.hotel.cost.domain.data.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.Calendar;

/**
 * @author yangzhengzhang
 * @description
 * @date 2022-11-23 11:19
 */
@Data
public class OrderExtendInfo {
    /**
     * 闪住
     */
    public Boolean isFlashOrder;
    public Calendar anticipationAuditDate;

    /**
     * 分销
     */
    public Integer allianceID;
    public Integer sID;
    public String allianceOrderID;
}
