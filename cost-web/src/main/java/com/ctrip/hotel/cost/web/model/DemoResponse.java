package com.ctrip.hotel.cost.web.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;

/**
 * @author yangzhengzhang
 * @description
 * @date 2022-09-29 14:00
 */
@ApiModel("例子1-response")
@Data
@Builder
public class DemoResponse {
    @ApiModelProperty("例子1-responseId")
    private Long responseId;

    @ApiModelProperty("例子1-responseName")
    private String responseName;
}
