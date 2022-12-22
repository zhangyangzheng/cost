package com.ctrip.hotel.cost.model;

import com.ctrip.hotel.cost.common.util.I18NMessageUtil;

public enum JobStatus {

    Pending("W", I18NMessageUtil.getMessage("JobStatus.desc.1")),
    Success("T", I18NMessageUtil.getMessage("JobStatus.desc.2")),
    Fail("F", I18NMessageUtil.getMessage("JobStatus.desc.3")),
    Invalid("I", I18NMessageUtil.getMessage("JobStatus.desc.4"));

    String value;
    String desc;

    JobStatus(String value, String desc) {
        this.value = value;
        this.desc = desc;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
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