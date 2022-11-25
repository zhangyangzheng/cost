package com.ctrip.hotel.cost.infrastructure.dao;

import hotel.settlement.dao.dal.AbstractDao;
import hotel.settlement.dao.dal.htlorderaccount.entity.CompareSettleDataCommonGen;
import org.springframework.stereotype.Repository;

import java.sql.SQLException;

@Repository
public class CompareSettleDataCommonDao extends AbstractDao<CompareSettleDataCommonGen> {
    public CompareSettleDataCommonDao() throws SQLException {
    }

    @Override
    public Class<CompareSettleDataCommonGen> getDaoClass() {
        return CompareSettleDataCommonGen.class;
    }
}
