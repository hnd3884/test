package com.adventnet.dmsummaryserver;

public final class PUSHTOPROBESEVENTLOG
{
    public static final String TABLE = "PushToProbesEventLog";
    public static final String PUSH_EVENT_LOG_ID = "PUSH_EVENT_LOG_ID";
    public static final int PUSH_EVENT_LOG_ID_IDX = 1;
    public static final String SUMMARY_EVENT_ID = "SUMMARY_EVENT_ID";
    public static final int SUMMARY_EVENT_ID_IDX = 2;
    public static final String PROBE_ID = "PROBE_ID";
    public static final int PROBE_ID_IDX = 3;
    public static final String PUSH_EVENT_TIME = "PUSH_EVENT_TIME";
    public static final int PUSH_EVENT_TIME_IDX = 4;
    public static final String PUSH_EVENT_STATUS = "PUSH_EVENT_STATUS";
    public static final int PUSH_EVENT_STATUS_IDX = 5;
    public static final String RETRY_COUNT = "RETRY_COUNT";
    public static final int RETRY_COUNT_IDX = 6;
    
    private PUSHTOPROBESEVENTLOG() {
    }
}
