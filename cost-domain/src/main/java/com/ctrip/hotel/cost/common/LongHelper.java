package com.ctrip.hotel.cost.common;

public class LongHelper {
    public static Long getNullableLong(String longStr){
        if(longStr == null){
            return null;
        }
        return Long.parseLong(longStr);
    }

    public static boolean isEffectData(Long data){
        return data != null && data.compareTo(0L) > 0;
    }

    public static boolean isNotEffectData(Long data){
        return !isEffectData(data);
    }
}
