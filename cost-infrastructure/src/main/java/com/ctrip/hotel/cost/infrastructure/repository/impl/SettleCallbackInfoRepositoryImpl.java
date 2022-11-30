package com.ctrip.hotel.cost.infrastructure.repository.impl;

import com.ctrip.hotel.cost.infrastructure.dao.SettleCallbackInfoDao;
import com.ctrip.hotel.cost.infrastructure.repository.SettleCallbackInfoRepository;
import hotel.settlement.dao.dal.htlcalculatefeetidb.entity.SettleCallbackInfoTiDBGen;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.sql.SQLException;

@Repository
public class SettleCallbackInfoRepositoryImpl implements SettleCallbackInfoRepository {

    @Autowired
    SettleCallbackInfoDao settleCallbackInfoDao;

    @Override
    public void insert(SettleCallbackInfoTiDBGen settleCallbackInfoTiDBGen) throws SQLException {
        settleCallbackInfoDao.insert(settleCallbackInfoTiDBGen);
    }
}
