package com.adventnet.taskengine;

public final class SCHEDULEDTASK_RETRY
{
    public static final String TABLE = "ScheduledTask_Retry";
    public static final String SCHEDULE_ID = "SCHEDULE_ID";
    public static final int SCHEDULE_ID_IDX = 1;
    public static final String TASK_ID = "TASK_ID";
    public static final int TASK_ID_IDX = 2;
    public static final String RETRY_COUNT = "RETRY_COUNT";
    public static final int RETRY_COUNT_IDX = 3;
    public static final String RETRY_TIME_PERIOD = "RETRY_TIME_PERIOD";
    public static final int RETRY_TIME_PERIOD_IDX = 4;
    public static final String RETRY_UNIT_OF_TIME = "RETRY_UNIT_OF_TIME";
    public static final int RETRY_UNIT_OF_TIME_IDX = 5;
    public static final String RETRY_FACTOR = "RETRY_FACTOR";
    public static final int RETRY_FACTOR_IDX = 6;
    
    private SCHEDULEDTASK_RETRY() {
    }
}
