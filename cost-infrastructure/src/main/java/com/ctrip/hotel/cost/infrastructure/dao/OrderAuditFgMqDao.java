package com.ctrip.hotel.cost.infrastructure.dao;

import com.ctrip.platform.dal.dao.DalHints;
import hotel.settlement.dao.dal.AbstractDao;
import hotel.settlement.dao.dal.htlcalculatefeetidb.entity.OrderAuditFgMqTiDBGen;
import hotel.settlement.dao.util.WhereClauseBuilder;
import org.springframework.stereotype.Repository;

import java.sql.SQLException;
import java.sql.Types;
import java.util.List;

@Repository
public class OrderAuditFgMqDao extends AbstractDao<OrderAuditFgMqTiDBGen> {

  public OrderAuditFgMqDao() throws SQLException {}

  @Override
  public Class<OrderAuditFgMqTiDBGen> getDaoClass() {
    return OrderAuditFgMqTiDBGen.class;
  }

  private String getSliceIndexStr(List<Integer> sliceIndexList) throws Exception {
    StringBuilder sliceIndexSb = new StringBuilder();
    String split = "";
    for (Integer sliceIndex : sliceIndexList) {
      sliceIndexSb.append(split + sliceIndex);
      split = ", ";
    }
    return sliceIndexSb.toString();
  }

  // 获取待执行且在指定分片中的job并依据时间排序
  public List<OrderAuditFgMqTiDBGen> getPendingJobs(List<Integer> sliceIndexList, Integer minBetween, Integer count)
      throws Exception {
    String sliceIndexStr = getSliceIndexStr(sliceIndexList);
    // 实测比直球查询:
    // SELECT * FROM ORDER_AUDIT_FG_MQ WHERE jobStatus = 'W' AND sliceIndex IN (1,2,3) ORDER BY
    // datachange_createtime
    // 快4～5倍
    String sql =
        String.format(
            "SELECT * FROM ORDER_AUDIT_FG_MQ WHERE id IN"
                + "(SELECT id FROM ORDER_AUDIT_FG_MQ WHERE jobStatus = 'W' ORDER BY datachange_createtime)"
                + "AND sliceIndex IN (%s) AND timestampdiff(MINUTE, datachange_lasttime, CURRENT_TIMESTAMP(3)) > %d LIMIT %d",
            sliceIndexStr, minBetween, count);
    // 需要读到最新的
    return client.query(sql, new DalHints().masterOnly());
  }

  // 获取指定dataId 执行成功或者待执行的job
  public List<OrderAuditFgMqTiDBGen> getJobsByOrderIdAndFgId(Long orderId, Long fgId)
      throws Exception {
    WhereClauseBuilder whereClauseBuilder = new WhereClauseBuilder();
    whereClauseBuilder.equal("orderId", orderId, Types.BIGINT);
    whereClauseBuilder.equal("fgId", fgId, Types.INTEGER);
    // 需要读到最新的
    return query(whereClauseBuilder, true);
  }
}
