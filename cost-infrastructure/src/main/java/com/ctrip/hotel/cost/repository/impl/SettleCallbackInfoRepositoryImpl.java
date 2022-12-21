package com.ctrip.hotel.cost.repository.impl;

import com.ctrip.hotel.cost.dao.SettleCallbackInfoDao;
import com.ctrip.hotel.cost.repository.SettleCallbackInfoRepository;
import hotel.settlement.dao.dal.htlcalculatefeetidb.entity.SettleCallbackInfoTiDBGen;
import hotel.settlement.dao.util.WhereClauseBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.sql.SQLException;
import java.sql.Types;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class SettleCallbackInfoRepositoryImpl implements SettleCallbackInfoRepository {

    @Autowired
    SettleCallbackInfoDao settleCallbackInfoDao;

    @Override
    public void insert(SettleCallbackInfoTiDBGen settleCallbackInfoTiDBGen) throws SQLException {
        settleCallbackInfoDao.insert(settleCallbackInfoTiDBGen);
    }

    @Override
    public Map<String, SettleCallbackInfoTiDBGen> getMapByReferenceIdList(List<String> referenceIdList) throws SQLException {
        WhereClauseBuilder whereClauseBuilder = new WhereClauseBuilder();
        whereClauseBuilder.in("referenceId", referenceIdList, Types.VARCHAR);
        List<SettleCallbackInfoTiDBGen> settleCallbackInfoTiDBGenList = settleCallbackInfoDao.query(whereClauseBuilder, true);
        Map<String, SettleCallbackInfoTiDBGen> res = new HashMap<>();
        for (SettleCallbackInfoTiDBGen settleCallbackInfoTiDBGen : settleCallbackInfoTiDBGenList) {
            res.put(settleCallbackInfoTiDBGen.getReferenceId(), settleCallbackInfoTiDBGen);
        }
        return res;
    }
}
