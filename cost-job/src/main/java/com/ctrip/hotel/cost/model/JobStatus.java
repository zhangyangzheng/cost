package com.ctrip.hotel.cost.model;

import com.ctrip.hotel.cost.common.util.I18NMessageUtil;

public enum JobStatus {

    Pending("W", "To be executed"),
    Success("T", "Successful execution"),
    Fail("F", "Failed to execute"),
    Invalid("I", "Invalid data");

    String value;
    String desc;

    JobStatus(String value, String desc) {
        this.value = value;
        this.desc = desc;
    }

    public String getValue() {
        return value;
    }

    public String getDesc() {
        return desc;
    }

    public static JobStatus getJobStatus(String value){
        if(Pending.getValue().equals(value)){
            return Pending;
        }
        if(Success.getValue().equals(value)){
            return Success;
        }
        if(Fail.getValue().equals(value)){
            return Fail;
        }
        if(Invalid.getValue().equals(value)){
            return Invalid;
        }
        return null;
    }
}
