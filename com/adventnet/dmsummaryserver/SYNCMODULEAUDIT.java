package com.adventnet.dmsummaryserver;

public final class SYNCMODULEAUDIT
{
    public static final String TABLE = "SyncModuleAudit";
    public static final String MODULE_AUDIT_ID = "MODULE_AUDIT_ID";
    public static final int MODULE_AUDIT_ID_IDX = 1;
    public static final String SYNC_MODULE_ID = "SYNC_MODULE_ID";
    public static final int SYNC_MODULE_ID_IDX = 2;
    public static final String SYNC_TIME = "SYNC_TIME";
    public static final int SYNC_TIME_IDX = 3;
    public static final String PROBE_ID = "PROBE_ID";
    public static final int PROBE_ID_IDX = 4;
    public static final String FILES_POSTED_FROM_PROBE = "FILES_POSTED_FROM_PROBE";
    public static final int FILES_POSTED_FROM_PROBE_IDX = 5;
    public static final String FILES_PROCESSED_IN_SUMMARY = "FILES_PROCESSED_IN_SUMMARY";
    public static final int FILES_PROCESSED_IN_SUMMARY_IDX = 6;
    public static final String MODULE_SYNC_STATUS = "MODULE_SYNC_STATUS";
    public static final int MODULE_SYNC_STATUS_IDX = 7;
    
    private SYNCMODULEAUDIT() {
    }
}
