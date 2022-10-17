package com.ctrip.hotel.cost.infrastructure.model;

import lombok.Builder;
import lombok.Data;

/**
 * @author yangzhengzhang
 * @description
 * @date 2022-09-30 14:59
 */
@Data
@Builder
public class DemoPo {
    private int domainId;

    private String name;
}
