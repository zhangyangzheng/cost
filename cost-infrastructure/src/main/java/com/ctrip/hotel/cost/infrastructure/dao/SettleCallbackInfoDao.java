package com.ctrip.hotel.cost.infrastructure.dao;

import hotel.settlement.dao.dal.AbstractDao;
import hotel.settlement.dao.dal.htlcalculatefeetidb.entity.SettleCallbackInfoTiDBGen;
import org.springframework.stereotype.Repository;

import java.sql.SQLException;

@Repository
public class SettleCallbackInfoDao extends AbstractDao<SettleCallbackInfoTiDBGen> {
    public SettleCallbackInfoDao() throws SQLException {
    }

    @Override
    public Class<SettleCallbackInfoTiDBGen> getDaoClass() {
        return SettleCallbackInfoTiDBGen.class;
    }
}
