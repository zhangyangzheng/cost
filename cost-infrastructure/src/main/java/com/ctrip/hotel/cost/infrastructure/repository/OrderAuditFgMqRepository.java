package com.ctrip.hotel.cost.infrastructure.repository;

import hotel.settlement.dao.dal.htlcalculatefeetidb.entity.OrderAuditFgMqTiDBGen;

import java.sql.SQLException;
import java.util.List;

/**
 * @author yangzhengzhang
 * @description
 * @date 2022-11-17 14:28
 */
public interface OrderAuditFgMqRepository {

    List<OrderAuditFgMqTiDBGen> getPendingJobs(List<Integer> sliceIndexList, Integer count) throws SQLException;

    List<OrderAuditFgMqTiDBGen> getJobsByOrderIdAndFgId(Long orderId, Integer fgId) throws SQLException;

    void insert(OrderAuditFgMqTiDBGen orderAuditFgMqTiDBGen) throws SQLException;

    void batchUpdate(List<OrderAuditFgMqTiDBGen> list) throws SQLException;

}
