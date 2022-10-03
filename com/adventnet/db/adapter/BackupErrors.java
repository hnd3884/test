package com.adventnet.db.adapter;

public enum BackupErrors
{
    ALREADY_BACKUP_RUNNING("B0BP01", "Already a backup DB process is running."), 
    UNABLE_TO_CREATE_BACKUP_DIRECTORY("B0BP02", "Unable to create folder for backup."), 
    NOT_ENOUGH_PERMISSION_FOR_BACKUP("B0BP03", "Not Enough Permission to execute backup."), 
    INSUFFICIENT_STORAGE_SPACE("B0BP04", "Insufficient Storage Space for backup."), 
    PROBLEM_FETCHING_TABLE_NAMES("B0BP05", "Problem fetching table names for backup."), 
    PROBLEM_FLUSHING_LOGS("B0BP06", "Problem while executing flush logs statement."), 
    PROBLEM_FETCHING_BACKUP_DIRECTORY_PATH("B0BP07", "Problem while fetching canonical path of backup directory."), 
    PROBLEM_FETCHING_CONNECTION("B0BP08", "Problem while getting connection."), 
    PROBLEM_WHILE_EXECUTING_COMMAND("B0BP09", "Problem while executing command."), 
    PROBLEM_WHILE_ZIPPING("B0BP10", "Problem while zipping."), 
    PROBLEM_GENERATING_BACKUP_INDEX_PROPS("B0BP11", "Problem while writing backup information (index/full_index/incremental_index.props)."), 
    PROBLEM_GENERATING_BACKUPRESTORE_PROPS("B0BP12", "Problem while writing backuprestore.conf or spec.xml."), 
    PROBLEM_WRITING_VERSION_PROPERTIES("B0BP13", "Problem while writing version information."), 
    PROBLEM_FETCHING_DB_LOG_FILE("B0BP14", "Problem fetching starting log file."), 
    PROBLEM_FETCHING_BACKUP_STATUS_FILE("B0BP15", "Problem fetching backup status file."), 
    PROBLEM_RESOLVING_HOSTNAME("B0BP16", "Problem resolving hostname."), 
    PROBLEM_CREATING_TOUCH_FILE("B0BP17", "Problem creating touch file."), 
    PROBLEM_RENAMING_TOUCH_FILE("B0BP18", "Problem renaming touch file."), 
    PROBLEM_DELETING_TOUCH_FILE("B0BP19", "Problem deleting touch file."), 
    PROBLEM_WRITING_SQL_FILE("B0BP20", "Problem writing sql file."), 
    PROBLEM_ENABLING_REPLICATION("B0BP21", "Problem enabling replication."), 
    PROBLEM_DISABLING_REPLICATION("B0BP22", "Problem disabling replication."), 
    PROBLEM_GENERATING_PASSWORD("B0BP23", "Problem while generating password"), 
    REMOTE_BACKUP_PATH_ERROR("B0BP24", "Problem in writing to remote directory"), 
    SANITY_TEST_FAILED("B0ST01", "Backup Sanity Testing failed."), 
    PROGRESS_BAR_INTERRUPTED("B0PB01", "Progress Bar process was interrupted by some thread."), 
    BACKUP_DIRECTORY_NOT_SPECIFIED("B0VP01", "Backup directory is not specified."), 
    ZIPFILE_NAME_NOT_SPECIFIED("B0VP02", "Zip File Name is not specified."), 
    BACKUP_CONTENT_TYPE_NOT_SPECIFIED("B0VP03", "Backup Content Type is not specified."), 
    BACKUP_DIRECTORY_DOES_NOT_EXIST("B0VP04", "Backup directory does not exists."), 
    DATABASE_BACKUP_MISCONFIGURED("B0VP05", "Database misconfiguration."), 
    UNSUPPORTED_BACKUP_TYPE("B0VP06", "Unsupported Backup Type."), 
    PROBLEM_ENABLING_BACKUP("B0VP07", "Problem enabling backup."), 
    BACKUP_FILE_ALREADY_EXISTS("B0VP08", "Given backup file already exists."), 
    PROBLEM_PUBLISHING_BACKUP_STATUS("B0MN01", "Problem while publishing backup status."), 
    PROBLEM_COPYING_CONF_FILES("B0BC01", "Problem while copying conf files."), 
    PROBLEM_GENERATING_DYNAMIC_COLUMN_FILE("B0BC02", "Problem while backing up Dynamic Columns information."), 
    INCREMENTAL_BINARY_NOT_FOUND("B0BI01", "Binary required for incremental backup does not exists."), 
    FULL_BINARY_NOT_FOUND("B0BI02", "Binary required for full backup does not exists."), 
    DUMP_BINARY_NOT_FOUND("B0BI03", "Binary required for dump backup does not exists."), 
    INSTALLED_DB_CONF_FILE_NOT_FOUND("B0BI04", "Necessary conf file for installed database does not exists."), 
    ARCHIVE_SCRIPT_NOT_FOUND("B0BI05", "Archive script does not exists."), 
    PROBLEM_INITIALIZING_VERSION_HANDLER("B0IN01", "Problem initializing VersionHandler.");
    
    private final String code;
    private final String message;
    
    private BackupErrors(final String code, final String message) {
        this.code = code;
        this.message = message;
    }
    
    public String getCode() {
        return this.code;
    }
    
    public String getMessage() {
        return this.message;
    }
    
    @Override
    public String toString() {
        return this.code + ": " + this.message;
    }
}
