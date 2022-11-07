package com.ctrip.hotel.cost.domain.repository;

import hotel.settlement.dao.dal.htlcalculatefeetidb.entity.OrderAuditFgMqTiDBGen;

import java.sql.SQLException;
import java.util.List;

public interface OrderAuditFgMqRepository {
    List<OrderAuditFgMqTiDBGen> getPendingJobs(List<Integer> sliceIndexList, Integer count) throws SQLException;
    List<OrderAuditFgMqTiDBGen> getJobsByDataId(Long dataId) throws SQLException;
}
