package com.ctrip.hotel.cost.infrastructure.repository.impl;

import com.ctrip.hotel.cost.infrastructure.repository.OrderAuditFgMqRepository;
import com.ctrip.hotel.cost.infrastructure.dao.OrderAuditFgMqDao;
import com.ctrip.platform.dal.dao.DalHints;
import hotel.settlement.dao.dal.htlcalculatefeetidb.entity.OrderAuditFgMqTiDBGen;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.sql.SQLException;
import java.util.List;

/**
 * @author yangzhengzhang
 * @description
 * @date 2022-11-17 14:29
 */
@Repository
public class OrderAuditFgMqRepositoryImpl implements OrderAuditFgMqRepository {

    @Autowired
    OrderAuditFgMqDao orderAuditFgMqDao;

    @Override
    public List<OrderAuditFgMqTiDBGen> getPendingJobs(List<Integer> sliceIndexList, Integer count) throws SQLException {
        return orderAuditFgMqDao.getPendingJobs(sliceIndexList, count);
    }

    @Override
    public List<OrderAuditFgMqTiDBGen> getJobsByOrderIdAndFgId(Long orderId, Integer fgId) throws SQLException {
        return orderAuditFgMqDao.getJobsByOrderIdAndFgId(orderId, fgId);
    }

    @Override
    public void insert(OrderAuditFgMqTiDBGen orderAuditFgMqTiDBGen) throws SQLException {
        orderAuditFgMqDao.insert(new DalHints(), orderAuditFgMqTiDBGen);
    }

    @Override
    public void batchUpdate(List<OrderAuditFgMqTiDBGen> list) throws SQLException {
        orderAuditFgMqDao.batchUpdate(new DalHints(), list);
    }
}
