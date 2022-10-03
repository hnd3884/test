package com.me.ems.summaryserver.common.sync;

public class SyncConstants
{
    public static final int REGULAR_SYNC = 1;
    public static final int FULL_SYNC = 2;
    public static final int PRIORITY_SYNC = 3;
    public static final String SYNCMETADATA_CACHEKEY = "SyncMetaData";
    public static final String CONFLICTRESOLUTIONMETADETA_CACHEKEY = "ConflictResolutionMetaData";
    public static final String SYNCMODULEMETADATA_CACHEKEY = "SyncModuleMeta";
    public static final String SUMMARYSYNCPARAMS_CACHEKEY = "SummarySyncParams";
    public static final int CSV = 1;
    public static final int JSON = 2;
    public static final String DELETION_INFO = "Deletion_Info";
    public static final String CSV_FILE_EXTENSION = ".csv";
    public static final String JSON_FILE_EXTENSION = ".json";
    public static final String GZIP_FILE_EXTENSION = ".gz";
    public static final String LAST_FILE_IDENTIFIER = "(last)";
    public static final String NULL_STRING = "null";
    public static final String STAR_NULL_STAR = "*null*";
    public static final String SYNC_FILE_COUNT_SEPARATOR = "--";
    public static final String CSV_ESCAPE_CHAR_REGEX = "\"";
    public static final String CSV_ESCAPE_CHAR_DOUBLE_REGEX = "\"\"";
    public static final String SS_FILED = "SS_Field";
    public static final String SS_FIELDVALUES = "SS_FieldValues";
    public static final String IS_CONFLICT_DATA = "IS_CONFLICT_DATA";
    public static final String FIELDS = "fields";
    public static final String VALUES = "values";
    public static final String CONFLICT = "Conflict";
    public static final String SYNC_COLUMN_SEPARATOR = "*";
    public static final String SYNC_COLUMN_SEPARATOR_REGEX = "\\*";
    public static final String DEFAULT_TIME_FILED = "DB_UPDATED_TIME";
    public static final String DB_ADDED_TIME = "DB_ADDED_TIME";
    public static final int DEFAULT_BATCH_SELECTION_LIMIT = 1000;
    public static final int DEFAULT_RECORD_COUNT_LIMIT = 10000;
    public static final String DEFAULT_TIME_STRING = "0";
    public static final long DEFAULT_MODULE = -1L;
    public static final long DEFAULT_TIME_VALUE = -1L;
    public static final String DEFAULT_RECORD_LIMIT = "10000";
    public static final String DEFAULT_INTEGER = "0";
    public static final String DEFAULT_STRING = "-";
    public static final String DEFAULT_UNKNOWN = "Unknown";
    public static final String DEFAULT_BATCH_VALUE = "1000";
    public static final String DEFAULT_RETRY_THRESHOLD = "5";
    public static final String LAST_SYNC_TIME = "LAST_SYNC_TIME";
    public static final String LAST_SUCCESSFUL_SYNC_TIME = "LAST_SUCCESSFUL_SYNC_TIME";
    public static final String PROBE_ID = "PROBE_ID";
    public static final String MIN_RECORD_LIMIT = "MIN_RECORD_LIMIT";
    public static final String MAX_RECORD_LIMIT = "MAX_RECORD_LIMIT";
    public static final String SYNC_AUDIT_CLEANUP_DAYS = "SYNC_AUDIT_CLEANUP_DAYS";
    public static final String AUDIT_DELETION_CHUNK = "AUDIT_DELETION_CHUNK";
    public static final String ENABLE_SPLIT_SYNC = "ENABLE_SPLIT_SYNC";
    public static final String SPLIT_SYNC_MINUTES = "SPLIT_SYNC_MINUTES";
    public static final String MAPPING_FIELD_SEPARATOR_REGEX = "\\.";
    public static final String UNIQUE_FIELDS_SEPARATOR_REGEX = ",";
    public static final String PROBE_ID_KEY = "probe_id";
    public static final String SYNC_MODULE_NAME_KEY = "sync_module_name";
    public static final String SYNC_TIME_KEY = "sync_time";
    public static final String TOTAL_FILE_COUNT_KEY = "total_file_count";
    public static final String FILES_POSTED_COUNT_KEY = "files_posted_from_probe_count";
    public static final String FILE_NAMES_KEY = "file_names";
    public static final String FILES_PROCESSED_IN_SUMMARY_COUNT_KEY = "files_processed_in_summary_count";
    public static final String FILE_DETAILS_KEY = "file_details";
    public static final String FILE_STATUS_KEY = "file_status";
    public static final String RETRY_COUNT_KEY = "retry_count";
    public static final String MODULE_SYNC_STATUS_KEY = "module_sync_status";
    public static final String STATUS_UPDATED = "StatusUpdated";
    public static final String QFILENAME_SEPARATOR = "-";
    
    public enum SyncType
    {
        REGULAR_SYNC("Regular Sync", 1), 
        FULL_SYNC("Full Sync", 2);
        
        private final String value;
        private final int code;
        
        private SyncType(final String value, final int code) {
            this.value = value;
            this.code = code;
        }
        
        public String getValue() {
            return this.value;
        }
        
        public int getCode() {
            return this.code;
        }
    }
}
