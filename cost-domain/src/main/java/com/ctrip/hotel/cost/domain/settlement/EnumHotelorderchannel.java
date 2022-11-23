package com.ctrip.hotel.cost.domain.settlement;

import java.util.Arrays;
import java.util.Optional;

/**
 * @author yangzhengzhang
 * @description
 * @date 2022-11-18 14:44
 */
public enum EnumHotelorderchannel {
    hpp(0),
    hfg(1),
    corpagreement(2),
    hoc(3),
    hrf(4),
    mip(5),
    walletpay(6);

    private final int value;

    EnumHotelorderchannel(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static Optional<EnumHotelorderchannel> getByValue(EnumHotelorderchannel channel) {
        return Arrays.stream(EnumHotelorderchannel.values()).filter(e -> e.equals(channel)).findFirst();
    }

}
