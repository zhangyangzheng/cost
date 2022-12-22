package com.ctrip.hotel.cost.domain.settlement;

import lombok.Data;

import java.util.List;

@Data
public class CancelOrderUsedBo {

  @Data
  public static class ToCancelDataUsed {
    private String outsettlementno;

    private Long settlementid;

  }

  private EnumHotelorderchannel orderchannel;

  private String orderid;

  private Long fGID;

  private List<ToCancelDataUsed> cancelDataList;

  //  public String version;

}
