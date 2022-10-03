package com.me.devicemanagement.onpremise.server.dbtuning;

public class DBPostgresOptimizationConstants
{
    private static DBPostgresOptimizationConstants dBPostgresOptimizationConsInstance;
    public static final String DB_NAME = "postgres";
    public static final Float DEFAULT_MEMORY_VALUE;
    public static final Float POSTGRES_MAX_MEMORY_VALUE;
    public static final Float MAX_MEMORY_PERCENT;
    public static final String LISTEN_ADDRESSES = "listen_addresses";
    public static final String SHARED_BUFFERS = "shared_buffers";
    public static final String TEMP_BUFFERS = "temp_buffers";
    public static final String WORK_MEM = "work_mem";
    public static final String EFFECTIVE_CACHE_SIZE = "effective_cache_size";
    public static final String MAINTENANCE_WORK_MEM = "maintenance_work_mem";
    public static final String WAL_BUFFERS = "wal_buffers";
    public static final String CHECKPOINT_COMPLETION_TARGET = "checkpoint_completion_target";
    public static final String CHECKPOINT_TIMEOUT = "checkpoint_timeout";
    public static final String MAX_FILES_PER_PROCESS = "max_files_per_process";
    public static final String LOGGING_COLLECTOR = "logging_collector";
    public static final String LOG_DIRECTORY = "log_directory";
    public static final String LOG_FILENAME = "log_filename";
    public static final String LOG_TRUNCATE_ON_ROTATION = "log_truncate_on_rotation";
    public static final String CLIENT_MIN_MESSAGES = "client_min_messages";
    public static final String LOG_MIN_MESSAGES = "log_min_messages";
    public static final String LOG_MIN_ERROR_STATEMENT = "log_min_error_statement";
    public static final String LOG_MIN_DURATION_STATEMENT = "log_min_duration_statement";
    public static final String LOG_CHECKPOINTS = "log_checkpoints";
    public static final String LOG_LOCK_WAITS = "log_lock_waits";
    public static final String LOG_CONNECTIONS = "log_connections";
    public static final String LOG_DISCONNECTIONS = "log_disconnections";
    public static final String LOG_HOSTNAME = "log_hostname";
    public static final String LOG_LINE_PREFIX = "log_line_prefix";
    public static final String LOG_STATEMENT = "log_statement";
    public static final String LOG_TEMP_FILES = "log_temp_files";
    public static final String DEADLOCK_TIMEOUT = "deadlock_timeout";
    public static final String RANDOM_PAGE_COST = "random_page_cost";
    public static final String SYNCHRONOUS_COMMIT = "synchronous_commit";
    public static final String VACUUM_FREEZE_MIN_AGE = "vacuum_freeze_min_age";
    public static final String WAL_SYNC_METHOD = "wal_sync_method";
    public static final String MAX_WAL_SIZE = "max_wal_size";
    public static final String MIN_WAL_SIZE = "min_wal_size";
    public static final String MB_SUFFIX = "MB";
    public static final String MIN_SUFFIX = "min";
    public static final String SECOND_SUFFIX = "s";
    public static final String ON_VALUE = "on";
    public static final String OFF_VALUE = "off";
    public static final String LOG_DIRECTORY_VALUE = "'pg_log'";
    public static final String LOG_FILENAME_VALUE = "'postgresql-%d.log'";
    public static final String CLIENT_MIN_MESSAGES_VALUE = "notice";
    public static final String LOG_MIN_MESSAGES_VALUE = "info";
    public static final String LOG_MIN_ERROR_STATEMENT_VALUE = "error";
    public static final String LOG_LINE_PREFIX_VALUE = "'%m [%p] [%e] '";
    public static final String LOG_STATEMENT_VALUE = "'ddl'";
    public static final String WAL_SYNC_METHOD_VALUE = "fsync_writethrough";
    public static final String SHARED_BUFERS_COMMENTS = "  \n#Shared Buffers - 1/4 of RAM MEMORY IN MB, Maximum 512MB for Windows";
    public static final String TEMP_BUFERS_COMMENTS = "  \n#Temp Buffers - RAM MEMORY IN MB /32*4";
    public static final String WORK_MEM_COMMENTS = "  \n#Work Memory - (RAM MEMORY IN MB-Shared_Buffers) /(2*Max_Used_Connections*3)";
    public static final String EFFECTIVE_CACHE_SIZE_COMMENTS = "  \n#Effective Cache Size - RAM MEMORY IN MB * 0.75";
    public static final String MAINTENANCE_WORK_MEM_COMMENTS = "  \n#Maintenance Work Memory - RAM MEMORY IN MB/16 ";
    public static final String WAL_BUFFERS_COMMENTS = "  \n#Wal_buffers - 1/32 of the size of shared_buffers, with an upper limit of 16MB";
    public static final String OLD_LOG_LINE_PREFIX_COMMENTS = " \n#log_line_prefix -  %t - Time stamp without milliseconds  %p - Process ID";
    public static final String LOG_LINE_PREFIX_COMMENTS = "  \n#log_line_prefix -  %m - Time stamp with milliseconds  %p - Process ID   %e - SQLSTATE error code";
    public static final String WAL_SYNC_METHOD_COMMENTS = "  \n#wal_sync_method - Will ensure that the data sent from WAL buffers to Disk are actually persisted";
    public static final Float EFFECTIVE_CACHE_SIZE_VALUE;
    public static final Long MAX_EFFECTIVE_CACHE_SIZE_VALUE;
    public static final Long MAX_WORK_MEM_VALUE;
    public static final Long MAX_MAINTENANCE_WORK_MEM_VALUE;
    public static final Long MAX_TEMP_BUFFERS_VALUE;
    public static final String POSTGRESQL_CONF_RESET = "postgresql_conf_reset";
    
    private DBPostgresOptimizationConstants() {
    }
    
    public static synchronized DBPostgresOptimizationConstants getInstance() {
        if (DBPostgresOptimizationConstants.dBPostgresOptimizationConsInstance == null) {
            DBPostgresOptimizationConstants.dBPostgresOptimizationConsInstance = new DBPostgresOptimizationConstants();
        }
        return DBPostgresOptimizationConstants.dBPostgresOptimizationConsInstance;
    }
    
    static {
        DBPostgresOptimizationConstants.dBPostgresOptimizationConsInstance = null;
        DEFAULT_MEMORY_VALUE = 4.0f;
        POSTGRES_MAX_MEMORY_VALUE = 27.0f;
        MAX_MEMORY_PERCENT = 80.0f;
        EFFECTIVE_CACHE_SIZE_VALUE = 1338.0f;
        MAX_EFFECTIVE_CACHE_SIZE_VALUE = 375000L;
        MAX_WORK_MEM_VALUE = 1792L;
        MAX_MAINTENANCE_WORK_MEM_VALUE = 1792L;
        MAX_TEMP_BUFFERS_VALUE = 1792L;
    }
}
