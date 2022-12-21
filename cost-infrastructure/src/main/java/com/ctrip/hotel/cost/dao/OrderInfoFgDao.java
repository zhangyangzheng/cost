package com.ctrip.hotel.cost.dao;

import hotel.settlement.dao.dal.AbstractDao;
import hotel.settlement.dao.dal.htlcalculatefeetidb.entity.OrderInfoFgTiDBGen;
import org.springframework.stereotype.Repository;

import java.sql.SQLException;

@Repository
public class OrderInfoFgDao extends AbstractDao<OrderInfoFgTiDBGen> {
    public OrderInfoFgDao() throws SQLException {
    }

    @Override
    public Class<OrderInfoFgTiDBGen> getDaoClass() {
        return OrderInfoFgTiDBGen.class;
    }
}
