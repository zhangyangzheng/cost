package com.ctrip.hotel.cost.domain.common;

public class LongHelper {
    public static Long getNullableLong(String longStr){
        if(longStr == null){
            return null;
        }
        return Long.parseLong(longStr);
    }
}
