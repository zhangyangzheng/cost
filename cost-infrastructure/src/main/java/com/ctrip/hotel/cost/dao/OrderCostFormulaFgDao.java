package com.ctrip.hotel.cost.dao;

import hotel.settlement.dao.dal.AbstractDao;
import hotel.settlement.dao.dal.htlcalculatefeetidb.entity.OrderCostFormulaFgTiDBGen;
import org.springframework.stereotype.Repository;

import java.sql.SQLException;

@Repository
public class OrderCostFormulaFgDao extends AbstractDao<OrderCostFormulaFgTiDBGen> {
    public OrderCostFormulaFgDao() throws SQLException {
    }

    @Override
    public Class<OrderCostFormulaFgTiDBGen> getDaoClass() {
        return OrderCostFormulaFgTiDBGen.class;
    }
}
