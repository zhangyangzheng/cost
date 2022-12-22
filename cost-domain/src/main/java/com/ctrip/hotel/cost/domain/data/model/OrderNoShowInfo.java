package com.ctrip.hotel.cost.domain.data.model;

import lombok.Data;

import java.util.List;

/**
 * @author yangzhengzhang
 * @description NoShow信息
 * @date 2022-11-23 13:45
 */
@Data
public class OrderNoShowInfo {
    private List<OrderNoShowRefund> orderNoShowRefundList;
}
