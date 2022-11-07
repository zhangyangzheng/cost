package com.ctrip.hotel.cost.infrastructure.repository;

import com.ctrip.hotel.cost.domain.repository.OrderAuditFgMqRepository;
import com.ctrip.platform.dal.dao.DalHints;
import hotel.settlement.dao.dal.htlcalculatefeetidb.dao.OrderAuditFgMqTiDBGenDao;
import hotel.settlement.dao.dal.htlcalculatefeetidb.entity.OrderAuditFgMqTiDBGen;
import hotel.settlement.dao.util.WhereClauseBuilder;
import org.springframework.stereotype.Repository;

import java.sql.SQLException;
import java.sql.Types;
import java.util.List;

@Repository
public class OrderAuditFgMqRepositoryImpl extends OrderAuditFgMqTiDBGenDao
    implements OrderAuditFgMqRepository {
  OrderAuditFgMqRepositoryImpl() {}

  private String getSliceIndexStr(List<Integer> sliceIndexList){
    StringBuilder sliceIndexSb = new StringBuilder();
    String split = "";
    for (Integer sliceIndex : sliceIndexList) {
      sliceIndexSb.append(split + sliceIndex);
      split = ", ";
    }
    return sliceIndexSb.toString();
  }

  // 获取待执行且在指定分片中的job并依据时间排序
  @Override
  public List<OrderAuditFgMqTiDBGen> getPendingJobs(List<Integer> sliceIndexList, Integer count) throws SQLException {
    String sliceIndexStr = getSliceIndexStr(sliceIndexList);
    // 实测比直球查询:
    // SELECT * FROM ORDER_AUDIT_FG_MQ WHERE jobStatus = 'W' AND sliceIndex IN (1,2,3) ORDER BY datachange_createtime
    // 快4～5倍
    String sql = String.format("SELECT * FROM ORDER_AUDIT_FG_MQ WHERE id IN" +
    "(SELECT id FROM ORDER_AUDIT_FG_MQ WHERE jobStatus = 'W' ORDER BY datachange_createtime)" +
    "AND sliceIndex IN (%s) LIMIT %d", sliceIndexStr, count);
    return this.query(sql, new DalHints());
  }

  // 获取指定dataId 执行成功或者待执行的job
  @Override
  public List<OrderAuditFgMqTiDBGen> getJobsByDataId(Long dataId) throws SQLException {
    String sql = String.format("SELECT * FROM ORDER_AUDIT_FG_MQ WHERE dataId = %d", dataId);
    return this.query(sql, new DalHints());
  }
}
