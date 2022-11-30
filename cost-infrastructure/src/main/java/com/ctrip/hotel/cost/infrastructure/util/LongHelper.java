package com.ctrip.hotel.cost.infrastructure.util;

public class LongHelper {
    public static Long getNullableLong(String longStr){
        if(longStr == null){
            return null;
        }
        return Long.parseLong(longStr);
    }
}
