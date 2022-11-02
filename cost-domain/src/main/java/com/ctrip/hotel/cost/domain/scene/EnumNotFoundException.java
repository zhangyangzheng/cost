package com.ctrip.hotel.cost.domain.scene;

/**
 * @author yangzhengzhang
 * @description
 * @date 2022-11-01 0:33
 */
public class EnumNotFoundException  extends RuntimeException {

    public EnumNotFoundException(Class type, Object key) {
        super(
                String.format("enum %s, key %s", type.getName(), key)
        );
    }

}
