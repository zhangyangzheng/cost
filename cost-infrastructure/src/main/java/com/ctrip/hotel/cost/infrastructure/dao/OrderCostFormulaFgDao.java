package com.ctrip.hotel.cost.infrastructure.dao;

import hotel.settlement.dao.dal.AbstractDao;
import hotel.settlement.dao.dal.htlcalculatefeetidb.entity.OrderCostFormulaFgTiDBGen;

import java.sql.SQLException;

public class OrderCostFormulaFgDao extends AbstractDao<OrderCostFormulaFgTiDBGen> {
    public OrderCostFormulaFgDao() throws SQLException {
    }

    @Override
    public Class<OrderCostFormulaFgTiDBGen> getDaoClass() {
        return OrderCostFormulaFgTiDBGen.class;
    }
}
