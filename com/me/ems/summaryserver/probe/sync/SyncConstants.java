package com.me.ems.summaryserver.probe.sync;

public class SyncConstants
{
    public static final String PROBESYNCMODULEMETA_CACHE = "ProbeSyncModuleMeta";
    public static final String SYNC_CSV_SEPARATOR = ",";
    public static final String NEXT_LINE = "\n";
    public static final String EMPTY_FIELD_VALUE = "-";
    public static final int EMPTY_SQL_ID = -1;
    public static final String CURR_SYNC_LOCKED_TIME = "CURR_SYNC_LOCKED_TIME";
    public static final String LAST_UPDATE_FROM_SUMMARY = "LAST_UPDATE_FROM_SUMMARY";
    public static final String SYNC_RETRY_COUNT = "SYNC_RETRY_COUNT";
    public static final String IS_SPLIT_SYNC = "IS_SPLIT_SYNC";
    public static final String IS_EMPTY_SYNC = "IS_EMPTY_SYNC";
    public static final String PROBE_TABLE_NAME = "PROBE_TABLE_NAME";
    public static final String PROBE_TABLE_PK_VALUE = "PROBE_TABLE_PK_VALUE";
    public static final String LAST_UPDATED_TIME = "LAST_UPDATED_TIME";
    public static final String IS_COMPRESSED_FILE_POST = "IS_COMPRESSED_FILE_POST";
    public static final String POST_RETRY_INTERVAL = "POST_RETRY_INTERVAL";
    public static final String FAILURE_RETRY_THRESHOLD = "FAILURE_RETRY_THRESHOLD";
    public static final String SKIP_SYNC_THRESHOLD = "SKIP_SYNC_THRESHOLD";
    public static final String SYNC_BEFORE_MINUTES = "SYNC_BEFORE_MINUTES";
    public static final int DEFAULT_SYNC_BEFORE_MINUTES = 5;
    public static final int DEFAULT_SKIP_THRESHOLD = 0;
    public static final long DEFAULT_SYNC_TIME = 0L;
    public static final String PK_COLUMN = "PK_COLUMN";
    public static final String MODULE_ID = "MODULE_ID";
    public static final String SQL_ID = "SQL_ID";
    public static final String MODULE_SYNC = "_SYNC";
    public static final String SYNC_ENABLED = "ENABLED";
    public static final String SYNC_DISABLED_ON_SS_DOWN = "DISABLED_ON_SS_DOWN";
    public static final String SYNC_DISABLED_ON_PROBE_FAILURE = "DISABLED_ON_PROBE_FAILURE";
    public static final String SYNC_DISABLED_ON_PARENT_MODULE_FAILURE = "DISABLED_ON_PARENT_MODULE_FAILURE";
    public static final String SYNC_DISABLED_ON_SS_STATUS_NOT_RECEIVED = "DISABLED_ON_NO_STATUS_FROM_SS";
    public static final String SYNC_DISABLED_ON_SS_LONG_PROCESSING = "DISABLED_ON_SS_LONG_PROCESSING";
    public static final String SYNC_DISABLED_ON_SS_FAILURE = "DISABLED_ON_SS_FAILURE";
    public static final String ENABLE_SYNC_ON_SS_LIVE = "ENABLE_ON_SS_LIVE";
    public static final String ENABLE_SYNC_ON_SS_STATUS_UPDATE = "ENABLE_ON_SS_STATUS";
    public static final String ENABLE_SYNC_ON_MAINTENANCE_RETRY = "ENABLE_ON_MAINTENANCE_RETRY";
    public static final String DELETE_INSERT_TABLES = "deleteInsertTables";
    
    public enum SyncFailureStatus
    {
        THRESHOLD_NOT_REACHED(0), 
        CRITICAL_LIMIT_REACHED(1), 
        CRITICAL_LIMIT_EXCEEDED(2);
        
        private final int statusCode;
        private int failedCount;
        
        private SyncFailureStatus(final int statusCode) {
            this.failedCount = 0;
            this.statusCode = statusCode;
        }
        
        public int getValue() {
            return this.statusCode;
        }
        
        @Override
        public String toString() {
            return String.valueOf(this.statusCode);
        }
        
        public void setFailedCount(final int failedCount) {
            this.failedCount = failedCount;
        }
        
        public int getFailedCount() {
            return this.failedCount;
        }
    }
}
