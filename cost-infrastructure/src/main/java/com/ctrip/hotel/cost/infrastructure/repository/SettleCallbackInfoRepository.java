package com.ctrip.hotel.cost.infrastructure.repository;

import hotel.settlement.dao.dal.htlcalculatefeetidb.entity.SettleCallbackInfoTiDBGen;

import java.sql.SQLException;

public interface SettleCallbackInfoRepository {

    void insert(SettleCallbackInfoTiDBGen settleCallbackInfoTiDBGen) throws SQLException;
}
