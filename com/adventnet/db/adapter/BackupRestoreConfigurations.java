package com.adventnet.db.adapter;

public class BackupRestoreConfigurations
{
    public enum BACKUP_CONTENT_TYPE
    {
        BINARY(1), 
        DUMP(2);
        
        private int contentType;
        
        private BACKUP_CONTENT_TYPE(final int t) {
            this.contentType = t;
        }
        
        public int getValue() {
            return this.contentType;
        }
        
        @Override
        public String toString() {
            return String.valueOf(this.contentType);
        }
    }
    
    public enum BACKUP_STATUS
    {
        BACKUP_PROCESS_STARTED(1), 
        GOING_TO_PERFORM_PG_START_BACKUP(2), 
        PG_START_BACKUP_COMPLETED(3), 
        FULLBACKUP_STATUSFILE_CREATED(4), 
        INDEXFILE_GENERATED_FOR_BACKUP(5), 
        GOING_TO_CREATE_BACKUPZIP(6), 
        BACKUPZIP_CREATION_COMPLETED(7), 
        GOING_TO_PERFORM_PG_STOP_BACKUP(8), 
        PG_STOP_BACKUP_COMPLETED(9), 
        GOING_TO_APPEND_WALFILES_IN_FULLBACKUPZIP(10), 
        APPENDING_WALFILES_IN_FULLBACKUPZIP_COMPLETED(11), 
        BACKUP_SUCCESSFULLY_COMPLETED(12), 
        BACKUP_PROCESS_FAILED(13), 
        BACKUP_PROCESS_TERMINATED(14), 
        BACKUP_PROCESS_IN_PROGRESS(15), 
        RESTORED_BACKUP(16);
        
        private int status;
        
        private BACKUP_STATUS(final int s) {
            this.status = s;
        }
        
        public int getValue() {
            return this.status;
        }
        
        @Override
        public String toString() {
            return String.valueOf(this.status);
        }
    }
    
    public enum RESTORE_STATUS
    {
        RESTORE_PROCESS_STARTED(1), 
        MOVING_DATABASE(2), 
        GOING_TO_UNZIP_BACKUPZIP(3), 
        UNZIP_BACKUPZIP_COMPLETED(4), 
        DROPPED_TABLES(5), 
        RENAMING_DATA_DIRECTORY(6), 
        RESTORE_SUCCESSFULLY_COMPLETED(7), 
        RESTORE_PROCESS_IN_PROGRESS(8), 
        RESTORE_PROCESS_TERMINATED(9), 
        RESTORE_PROCESS_FAILED(10);
        
        private int status;
        
        private RESTORE_STATUS(final int s) {
            this.status = s;
        }
        
        public int getValue() {
            return this.status;
        }
        
        @Override
        public String toString() {
            return String.valueOf(this.status);
        }
    }
    
    public enum BACKUP_MODE
    {
        ONLINE_BACKUP(1), 
        FILE_BACKUP(2), 
        OFFLINE_BACKUP(3);
        
        private int type;
        
        private BACKUP_MODE(final int t) {
            this.type = t;
        }
        
        public int getValue() {
            return this.type;
        }
        
        @Override
        public String toString() {
            return String.valueOf(this.type);
        }
    }
    
    public enum DB_START_MODE
    {
        PRE_START_DB(1), 
        POST_START_DB(2);
        
        private int mode;
        
        private DB_START_MODE(final int m) {
            this.mode = m;
        }
        
        public int getValue() {
            return this.mode;
        }
        
        @Override
        public String toString() {
            return String.valueOf(this.mode);
        }
    }
    
    public enum INIT_PERSISTENCE
    {
        BEFORE_RESTORE(1), 
        AFTER_RESTORE(2);
        
        private int mode;
        
        private INIT_PERSISTENCE(final int m) {
            this.mode = m;
        }
        
        public int getValue() {
            return this.mode;
        }
        
        @Override
        public String toString() {
            return String.valueOf(this.mode);
        }
    }
    
    public enum RESET_MICKEY
    {
        RESET(1), 
        NOT_APPLICABLE(2);
        
        private int mode;
        
        private RESET_MICKEY(final int m) {
            this.mode = m;
        }
        
        public int getValue() {
            return this.mode;
        }
        
        @Override
        public String toString() {
            return String.valueOf(this.mode);
        }
    }
    
    public enum BACKUP_TYPE
    {
        INCREMENTAL_BACKUP(1), 
        FULL_BACKUP(2);
        
        private int mode;
        
        private BACKUP_TYPE(final int t) {
            this.mode = t;
        }
        
        public int getValue() {
            return this.mode;
        }
        
        @Override
        public String toString() {
            return String.valueOf(this.mode);
        }
    }
}
