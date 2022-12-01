package com.ctrip.hotel.cost.domain.common;

public class StringHelper {

    public static String valueOf(Object obj){
        return obj == null ? null : obj.toString();
    }
}
