package com.ctrip.hotel.cost.infrastructure.repository;

import hotel.settlement.dao.dal.htlcalculatefeetidb.entity.SettleCallbackInfoTiDBGen;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public interface SettleCallbackInfoRepository {

    void insert(SettleCallbackInfoTiDBGen settleCallbackInfoTiDBGen) throws SQLException;

    Map<String, SettleCallbackInfoTiDBGen> getMapByReferenceIdList(List<String> referenceIdList) throws SQLException;
}
