package com.adventnet.db.adapter;

public enum RestoreErrors
{
    PROBLEM_INITIALIZING_VERSION_HANDLER("R0IN01", "Problem initializing VersionHandler."), 
    PROBLEM_INITIALIZING_METADATA("R0IN02", "Problem initializing metadata."), 
    PROBLEM_CREATING_TOUCH_FILE("R0TF01", "Problem creating touch file."), 
    PROBLEM_RENAMING_TOUCH_FILE("R0TF02", "Problem renaming touch file."), 
    PROBLEM_DELETING_TOUCH_FILE("R0TF03", "Problem deleting touch file."), 
    PROBLEM_CHECKING_DB_SERVER_STATUS("R0DB01", "Problem while checking database server status."), 
    DATABASE_ALREADY_RUNNING("R0DB02", "Database Server is already running."), 
    DATABASE_NOT_RUNNING("R0DB03", "Database Server is not running."), 
    PROBLEM_PREPARING_DB_SERVER("R0DB04", "Problem while preparing database server to accept connections."), 
    PROBLEM_FETCHING_CONNECTION("R0DB05", "Problem while getting connection."), 
    PROBLEM_REINITIALIZING_DB_SERVER("R0DB06", "Problem while reinitializing db server."), 
    NOT_ENOUGH_PERMISSION_FOR_RESTORE("R0DB07", "Not Enough Permission to execute restore."), 
    PROBLEM_RESETTING_DB_SERVER_PASSWORD("R0DB08", "Problem while resetting db server password."), 
    PROBLEM_SWITCHING_DB_MODE("R0DB09", "Problem switching database mode."), 
    PROBLEM_PRE_RESTORE_DATABASE("R0RP01", "Problem in pre restoring database."), 
    PROBLEM_GENERATING_EDT_FILE("R0RP02", "Problem while backing up EDT information."), 
    PROBLEM_GENERATING_DYNAMIC_COLUMN_FILE("R0RP03", "Problem while backing up Dynamic Columns information."), 
    PROBLEM_RESETTING_MICKEY("R0RP04", "Problem resetting mickey metadata."), 
    PROBLEM_RESETTING_DATA_SOURCE("R0RP05", "Problem resetting datasource."), 
    PROBLEM_GENERATING_DD_DIFF("R0RP06", "Problem generating data-dictionary diff."), 
    PROBLEM_TRANSFORMING_DYNAMIC_COLUMN_INFO("R0RP07", "Problem transforming dc.json file."), 
    PROBLEM_WHILE_CHECKING_COMPATIBILITY("R0RP08", "Problem while checking compatibility."), 
    PROBLEM_WHILE_COPYING_CONF("R0RP09", "Problem while copying udt conf files."), 
    INCOMPATIBLE_BACKUP("R0RP10", "Incompatible Backup File."), 
    BACKUP_FILE_NOT_FOUND("R0RP11", "Backup file not found."), 
    MISSING_DEPENDENT_BACKUPS("R0RP12", "Missing dependent backups."), 
    PROBLEM_FETCHING_VERSION("R0RP13", "Problem while getting database server version."), 
    PROBLEM_WHILE_CHECKING_FILE_IN_ZIP("R0RP14", "Problem while checking existance of file in zip."), 
    PROBLEM_WHILE_UNZIPPING("R0RP15", "Problem while unzipping."), 
    PROBLEM_WHILE_EXECUTING_COMMAND("R0RP16", "Problem while executing command."), 
    UNSUPPORTED_RESTORE_TYPE("R0RP17", "Unsupported Restore Type."), 
    PROBLEM_UPDATING_RESTORE_STATUS_IN_DB("R0RP18", "Problem while updaing restore status in database."), 
    PROBLEM_GENERATING_PASSWORD("R0RP19", "Problem while generating password"), 
    FW_SANITY_TEST_FAILED("R0ST01", "Framework Sanity Testing for restore failed."), 
    SANITY_TEST_FAILED("R0ST02", "Restore Sanity Testing failed."), 
    PROBLEM_MIGRATING_DB_MWSR("R0DM01", "Problem while migrating database in MWSR mode."), 
    RESTORE_BINARY_NOT_FOUND("R0VL01", "Binary required for restoring backup does not exists."), 
    INSTALLED_DB_CONF_FILE_NOT_FOUND("R0VL02", "Necessary conf file for installed database does not exists."), 
    WAL_DIRECTORY_MISSING("R0VL03", "WAL directory missing"), 
    PASSWORD_REQUIRED_FOR_ENCRYPTED_ZIP("R0VL04", "Password required for encrypted zip file."), 
    INCORRECT_CONTENTS_OR_PASSWORD_IN_ZIP("R0VL05", "Wrong archive file or incorrent password"), 
    ARCHITECTURE_FILE_NOT_FOUND("R0AR01", "Architecture information not found."), 
    PROBLEM_READING_ARCHITECTURE_FILE("R0AR02", "Problem while reading architecture information from file."), 
    DATA_DIRECTORY_DOES_NOT_EXIST("R0DD01", "Data directory does not exists."), 
    CHANGE_PERMISSION_SCRIPT_NOT_FOUND("R0DD02", "Change permission script not found."), 
    PROBLEM_RENAMING_DATA_DIRECTORY("R0DD03", "Problem renaming data directory."), 
    PROBLEM_ASSIGNING_PERMISSION_TO_DATA_DIRECTORY("R0DD04", "Problem assigning permission to data directory."), 
    PROGRESS_BAR_INTERRUPTED("B0PB01", "Progress Bar process was interrupted by some thread.");
    
    private final String code;
    private final String message;
    
    private RestoreErrors(final String code, final String message) {
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
