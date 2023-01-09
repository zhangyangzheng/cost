package com.ctrip.hotel.cost.model.inner;

import java.util.Objects;

public class Identify {
    public Long orderId;
    public Long fgId;


    public Identify(Long orderId, Long fgId) {
        this.orderId = orderId;
        this.fgId = fgId;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Identify identify = (Identify) o;
        return Objects.equals(orderId, identify.orderId) && Objects.equals(fgId, identify.fgId);
    }


    @Override
    public int hashCode() {
        return Objects.hash(orderId, fgId);
    }
}