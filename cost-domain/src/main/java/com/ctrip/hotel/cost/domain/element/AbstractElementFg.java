package com.ctrip.hotel.cost.domain.element;

import hotel.settlement.common.DateHelper;
import hotel.settlement.common.enums.DateDiffType;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author yangzhengzhang
 * @description
 * @date 2022-11-10 11:24
 */
public abstract class AbstractElementFg {
    public abstract Calendar orderBeginDate();
    public abstract Calendar orderEndDate();

    public abstract Calendar elementBeginDate();
    public abstract Calendar elementEndDate();

    /**
     * for fg
     * @return
     */
    public final int countDays() {
        List<String> orderCycleList = getCycleList(1, orderBeginDate(), orderEndDate());
        List<String> elementCycleList = getCycleList(1, elementBeginDate(), elementEndDate());
        List<String> intersectionSet = elementCycleList.stream().filter(orderCycleList::contains).collect(Collectors.toList());
        return intersectionSet.size();
    }

    /**
     * for fg
     * @return
     */
    public static List<String> getCycleList(int stepSize, Calendar beginTime, Calendar endTime) {
        if (stepSize < 1) {
            return Arrays.asList(DateHelper.format(beginTime) + "~" + DateHelper.format(endTime));
        }
        long gapDay = DateHelper.dateDiff(DateDiffType.Day, beginTime, endTime);
        if (gapDay < 1) {
            return Collections.emptyList();
        }
        long cycleForNum = gapDay % stepSize == 0 ? (gapDay / stepSize) : (gapDay / stepSize) + 1;

        List<String> cycleTimeList=new ArrayList<>();
        Calendar cycleBegin = beginTime;
        for (int i = 1; i <= cycleForNum; i++) {
            String begin = DateHelper.format(cycleBegin);
            String end = DateHelper.format(endTime);
            cycleBegin = DateHelper.parseCalendar(DateHelper.dateAdd(DateDiffType.Day, DateHelper.parseToTimestamp(cycleBegin), stepSize));
            if (DateHelper.dateDiff(DateDiffType.Day, DateHelper.parseToTimestamp(cycleBegin), DateHelper.parseToTimestamp(endTime)) <= 0) {
                end = DateHelper.format(cycleBegin);
            }
            String dateString = begin
                    + "~"
                    + end;
            cycleTimeList.add(dateString);
        }
        return cycleTimeList;
    }

}
