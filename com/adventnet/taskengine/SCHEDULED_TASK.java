package com.adventnet.taskengine;

public final class SCHEDULED_TASK
{
    public static final String TABLE = "Scheduled_Task";
    public static final String SCHEDULE_ID = "SCHEDULE_ID";
    public static final int SCHEDULE_ID_IDX = 1;
    public static final String TASK_ID = "TASK_ID";
    public static final int TASK_ID_IDX = 2;
    public static final String OFFSET_MS = "OFFSET_MS";
    public static final int OFFSET_MS_IDX = 3;
    public static final String SCHEDULE_MODE = "SCHEDULE_MODE";
    public static final int SCHEDULE_MODE_IDX = 4;
    public static final String ADMIN_STATUS = "ADMIN_STATUS";
    public static final int ADMIN_STATUS_IDX = 5;
    public static final String AUDIT_FLAG = "AUDIT_FLAG";
    public static final int AUDIT_FLAG_IDX = 6;
    public static final String TRANSACTION_TIME = "TRANSACTION_TIME";
    public static final int TRANSACTION_TIME_IDX = 7;
    public static final String RETRY_SCHEDULE_ID = "RETRY_SCHEDULE_ID";
    public static final int RETRY_SCHEDULE_ID_IDX = 8;
    public static final String SKIP_MISSED_SCHEDULE = "SKIP_MISSED_SCHEDULE";
    public static final int SKIP_MISSED_SCHEDULE_IDX = 9;
    public static final String REMOVE_ON_EXPIRY = "REMOVE_ON_EXPIRY";
    public static final int REMOVE_ON_EXPIRY_IDX = 10;
    public static final String RETRY_HANDLER = "RETRY_HANDLER";
    public static final int RETRY_HANDLER_IDX = 11;
    
    private SCHEDULED_TASK() {
    }
}
