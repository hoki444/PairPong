package com.algy.schedcore;

public class SchedTaskConfig {
    public long periodLowerBound = -1;
    public long periodUpperBound = -1;

    public boolean panicModeEnabled = false;
    public float panicModeTimeThreshold = 10;
    public float panicModeIncCountThreshold = 30;
    
    public Runnable onPeriodAdjusted;
}