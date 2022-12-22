package com.ctrip.hotel.cost.domain.settlement;


import hotel.settlement.common.helpers.StringHelper;
import org.apache.commons.validator.routines.IntegerValidator;

/** Created by qjzhuo on 2017-11-07. */
public enum ChannelType {
    /** 订单号(5780抛送入口) */
    OrderId(0),

    /** PPID(2571预付抛送入口) */
    PPID(1),

    /** FGID(2835现付抛送入口) */
    FGID(2),

    /** 追赔ID(追赔系统抛送入口) */
    CompensationID(3),

    /** 现付集团(现付集团抛送入口) */
    FGGroupID(4),

    /** FGID(2835现付闪住抛送入口) */
    FGID_HWP(5),

    /** 付面返佣 */
    PPTOFG(6),

    /** 应收调整数据(佣金、对账批次应收调整抛送入口) */
    RecAdj(7),

    /** 收款单退款(6612收款单退款抛送入口) */
    CollRef(8),

    /** 导入轧差(预付账单导入产生的轧差数据抛送入口) */
    ImprotGX(9),

    /** 预付过时取消 */
    PP_Offline(10),

    /** 现付过时取消 */
    FG_Offline(11),

    /** 超扣 */
    PP_AD(12),

    /** 自动退补款 */
    RID(13),

    /** 补偿费 */
    CompensationSettlement(14);

    private Integer index;

    public int getVal() {
        return index;
    }

    ChannelType(Integer index) {
        this.index = index;
    }

    public static ChannelType findChannelTypeByName(String enumName) {
        if (StringHelper.isNullOrWhiteSpace(enumName)) {
            return null;
        }

        for (ChannelType oneType : ChannelType.values()) {
            if (oneType.name().equalsIgnoreCase(enumName)) {
                return oneType;
            }
        }

        return null;
    }

    public static ChannelType findChannelTypeByValue(int enumVal) {
        for (ChannelType oneType : ChannelType.values()) {
            if (oneType.getVal() == enumVal) {
                return oneType;
            }
        }

        return null;
    }

    public static ChannelType findChannelTypeByAnyString(String anyStr) {
        if (StringHelper.isNullOrWhiteSpace(anyStr)) {
            return null;
        }

        if (IntegerValidator.getInstance().isValid(anyStr)) {
            return findChannelTypeByValue(Integer.parseInt(anyStr));
        } else {
            return findChannelTypeByName(anyStr);
        }
    }
}
