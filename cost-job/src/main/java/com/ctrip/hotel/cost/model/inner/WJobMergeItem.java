package com.ctrip.hotel.cost.model.inner;

import com.ctrip.hotel.cost.model.bo.FgOrderAuditMqDataBo;
import hotel.settlement.dao.dal.htlcalculatefeetidb.entity.OrderAuditFgMqTiDBGen;

import java.util.List;

// 合并后的Job对象 同生共死
public class WJobMergeItem {
  // 主单
  public FgOrderAuditMqDataBo leader;
  // 副单
  public List<FgOrderAuditMqDataBo> followers;

  public WJobMergeItem(FgOrderAuditMqDataBo leader, List<FgOrderAuditMqDataBo> followers) {
    this.leader = leader;
    this.followers = followers;
  }

  public void setLeaderRemark(String remark) {
    leader.getOrderAuditFgMqTiDBGen().setRemark(remark);
  }

  public void setFollowersRemark(String remark) {
    followers.stream().forEach(job -> job.getOrderAuditFgMqTiDBGen().setRemark(remark));
  }
}
