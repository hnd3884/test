package com.adventnet.dmsummaryserver;

public final class SUMMARYEVENTDATA
{
    public static final String TABLE = "SummaryEventData";
    public static final String SUMMARY_EVENT_ID = "SUMMARY_EVENT_ID";
    public static final int SUMMARY_EVENT_ID_IDX = 1;
    public static final String EVENT_ID = "EVENT_ID";
    public static final int EVENT_ID_IDX = 2;
    public static final String EVENT_CREATED_TIME = "EVENT_CREATED_TIME";
    public static final int EVENT_CREATED_TIME_IDX = 3;
    public static final String EVENT_CREATED_BY = "EVENT_CREATED_BY";
    public static final int EVENT_CREATED_BY_IDX = 4;
    public static final String CUSTOMER_ID = "CUSTOMER_ID";
    public static final int CUSTOMER_ID_IDX = 5;
    public static final String EVENT_FILE_PATH = "EVENT_FILE_PATH";
    public static final int EVENT_FILE_PATH_IDX = 6;
    public static final String FILE_CHECKSUM = "FILE_CHECKSUM";
    public static final int FILE_CHECKSUM_IDX = 7;
    public static final String IS_APPLICABLE_TO_ALL_PROBES = "IS_APPLICABLE_TO_ALL_PROBES";
    public static final int IS_APPLICABLE_TO_ALL_PROBES_IDX = 8;
    public static final String IS_REQUIRED_FOR_NEW_PROBE = "IS_REQUIRED_FOR_NEW_PROBE";
    public static final int IS_REQUIRED_FOR_NEW_PROBE_IDX = 9;
    public static final String EVENT_UNIQUE_ID = "EVENT_UNIQUE_ID";
    public static final int EVENT_UNIQUE_ID_IDX = 10;
    
    private SUMMARYEVENTDATA() {
    }
}
