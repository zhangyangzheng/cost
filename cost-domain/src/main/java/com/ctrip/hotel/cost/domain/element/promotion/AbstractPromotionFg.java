package com.ctrip.hotel.cost.domain.element.promotion;

import lombok.Data;

import java.util.Calendar;

/**
 * @author yangzhengzhang
 * @description
 * @date 2022-12-12 13:35
 */
@Data
public abstract class AbstractPromotionFg {
    // 修改房型：房型修改，促销项失效
    public Integer auditRoom;// 审核房型id
    public Integer orderRoom;// 订单预定房型id

    // 承担方
    public Integer costType;// 老逻辑待下线
    public Integer cashType;// 老逻辑待下线
    public Integer cashPoolID;// 老逻辑待下线
    public Integer discountDtype;// 老逻辑待下线
    public Long fundId;
    public Integer fundType;
    public Integer settlementType;

    // 审核信息
    public Calendar eta;
    public Calendar realETD;
    public Integer hourAdjuest;

}
