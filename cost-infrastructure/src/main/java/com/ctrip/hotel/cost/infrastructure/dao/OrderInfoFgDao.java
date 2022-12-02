package com.ctrip.hotel.cost.infrastructure.dao;

import hotel.settlement.dao.dal.AbstractDao;
import hotel.settlement.dao.dal.htlcalculatefeetidb.entity.OrderInfoFgTiDBGen;

import java.sql.SQLException;

public class OrderInfoFgDao extends AbstractDao<OrderInfoFgTiDBGen> {
    public OrderInfoFgDao() throws SQLException {
    }

    @Override
    public Class<OrderInfoFgTiDBGen> getDaoClass() {
        return OrderInfoFgTiDBGen.class;
    }
}
