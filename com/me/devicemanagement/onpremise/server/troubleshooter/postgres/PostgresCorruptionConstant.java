package com.me.devicemanagement.onpremise.server.troubleshooter.postgres;

import java.io.File;

public class PostgresCorruptionConstant
{
    public static final String SERVER_HOME;
    public static final String EXCEPTION_SORTER_FILE;
    public static final String CORRUPTION_FILE;
    public static final String CORRUPTION_LOCK_FILE;
    public static final String REGEX = "regex";
    public static final String EXCEPTION_MSG = "exceptionMsg";
    public static final String NOTIFY_CUST_THRU_MAIL = "notify.cust.thru.mail";
    public static final String DB_BACKUP_LOCATION = "db.backup.location";
    public static final String TABLE_INFO_REQUIRED = "TableInfoRequired";
    public static final String USING_RELFILENODE = "using_relfilenode";
    public static final String QUERY = "query";
    public static final String RESTORE_SUCCESSFUL = "restore.successful";
    public static final String CLEAR_CORRUPTION_PROPS = "clear.corruption.props";
    public static final String DISABLE_CORRUPTION_DETECTION = "DisableCorruptionDetection";
    public static final String CREATE_LOCK_FILE = "CreateLockFile";
    public static final String NOTIFY_CUSTOMERTHR_MAIL = "notifyCustomerthrMail";
    public static final String EXCEPTIONS = "Exceptions";
    public static final String CORRUPTION_ID = "CorruptionID";
    public static final String SCHEDULED_DBBACKUP = "ScheduledDBBackup";
    public static final String HISTORY = "history";
    public static final String CURRENT = "current";
    public static final String ID = "id";
    public static final String MSG = "msg";
    public static final String COUNT = "count";
    public static final String TABLE = "table";
    public static final String DETECTED = "detected";
    public static final String RECENT = "recent";
    public static final String IS_ENABLE = "isEnable";
    public static final String TOTAL_CURRENT = "totalCurrent";
    public static final String TOTAL_HISTORY = "totalHistory";
    public static final String LAST_DUMP = "lastDump";
    public static final String LAST_BINARY = "lastBinary";
    public static final String CORRUPTION_LOG_FILE;
    public static final String ERROR_MSG_1 = "errorMessage1";
    public static final String ERROR_MSG_2 = "errorMessage2";
    
    static {
        SERVER_HOME = System.getProperty("server.home");
        EXCEPTION_SORTER_FILE = PostgresCorruptionConstant.SERVER_HOME + File.separator + "conf" + File.separator + "postgresCorruptionExceptionTemplate.json";
        CORRUPTION_FILE = PostgresCorruptionConstant.SERVER_HOME + File.separator + "logs" + File.separator + "corruptionInfo.json";
        CORRUPTION_LOCK_FILE = PostgresCorruptionConstant.SERVER_HOME + File.separator + "bin" + File.separator + "corruption.lock";
        CORRUPTION_LOG_FILE = PostgresCorruptionConstant.SERVER_HOME + File.separator + "logs" + File.separator + "corruption.log";
    }
}
