package com.ctrip.hotel.cost.model.inner;

public class JobStatusStatistics {
    public int wCreateCount = 0;
    public int wUpdateCount = 0;
    public int wDeleteCount = 0;
    public int tCreateCount = 0;
    public int tUpdateCount = 0;
    public int tDeleteCount = 0;
    public int fCreateCount = 0;
    public int fUpdateCount = 0;
    public int fDeleteCount = 0;

    public int getTCreateAndUpdateCount() {
        return tCreateCount + tUpdateCount;
    }

}