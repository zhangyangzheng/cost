package com.ctrip.hotel.cost.web.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;

/**
 * @author yangzhengzhang
 * @description
 * @date 2022-09-29 13:59
 */
@ApiModel("例子1-request")
@Data
@Builder
public class DemoRequest {
    @ApiModelProperty(value = "例子1-requestId", required = true)
    private Long requestId;

    @ApiModelProperty(value = "例子1-requestName", required = true)
    private String requestName;
}
