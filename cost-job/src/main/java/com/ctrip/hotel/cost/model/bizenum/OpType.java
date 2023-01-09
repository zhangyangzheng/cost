package com.ctrip.hotel.cost.model.bizenum;

import org.apache.commons.lang3.StringUtils;

public enum OpType {

    Create("C"),

    Update("U"),

    Delete("D");


    String value;

    OpType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public OpType getOpType(String value){
        if(StringUtils.equals(Create.getValue(), value)){
            return Create;
        }
        if(StringUtils.equals(Update.getValue(), value)){
            return Update;
        }
        if(StringUtils.equals(Delete.getValue(), value)){
            return Delete;
        }
        return null;
    }
}
