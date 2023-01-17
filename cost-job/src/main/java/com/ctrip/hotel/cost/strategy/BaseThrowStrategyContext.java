package com.ctrip.hotel.cost.strategy;

import java.util.List;

public abstract class BaseThrowStrategyContext<Item, MergeItem> {

    public abstract List<Item> getThrowSettleList();

    public abstract List<Item> getDoneAllList();

    public abstract List<Item> getDoNothingList();

    public abstract MergeItem processAndGetMergeItem(List<Item> identifyJobList);
}
