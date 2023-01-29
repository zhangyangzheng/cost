package com.ctrip.hotel.cost.common;

import java.math.BigDecimal;

public class BigDecimalHelper {

  public static BigDecimal getNullIfZero(BigDecimal decimal) {
    if (decimal == null || decimal.compareTo(BigDecimal.ZERO) == 0) {
      return null;
    }
    return decimal;
  }
}
