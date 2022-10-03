package com.adventnet.persistence;

public final class BACKUPDETAILS
{
    public static final String TABLE = "BackupDetails";
    public static final String BACKUP_ID = "BACKUP_ID";
    public static final int BACKUP_ID_IDX = 1;
    public static final String BACKUP_TYPE = "BACKUP_TYPE";
    public static final int BACKUP_TYPE_IDX = 2;
    public static final String BACKUP_STARTTIME = "BACKUP_STARTTIME";
    public static final int BACKUP_STARTTIME_IDX = 3;
    public static final String BACKUP_ENDTIME = "BACKUP_ENDTIME";
    public static final int BACKUP_ENDTIME_IDX = 4;
    public static final String BACKUP_STATUS = "BACKUP_STATUS";
    public static final int BACKUP_STATUS_IDX = 5;
    public static final String BACKUP_ZIPNAME = "BACKUP_ZIPNAME";
    public static final int BACKUP_ZIPNAME_IDX = 6;
    public static final String BACKUP_ZIP_CLEANED = "BACKUP_ZIP_CLEANED";
    public static final int BACKUP_ZIP_CLEANED_IDX = 7;
    public static final String LAST_DATAFILE_NAME = "LAST_DATAFILE_NAME";
    public static final int LAST_DATAFILE_NAME_IDX = 8;
    public static final String LAST_DATAFILE_MODIFIEDTIME = "LAST_DATAFILE_MODIFIEDTIME";
    public static final int LAST_DATAFILE_MODIFIEDTIME_IDX = 9;
    public static final String DATAFILE_COUNT = "DATAFILE_COUNT";
    public static final int DATAFILE_COUNT_IDX = 10;
    public static final String FIRST_BACKUP_AFTER_PPM = "FIRST_BACKUP_AFTER_PPM";
    public static final int FIRST_BACKUP_AFTER_PPM_IDX = 11;
    public static final String DATABASE_SIZE = "DATABASE_SIZE";
    public static final int DATABASE_SIZE_IDX = 12;
    public static final String BACKUP_ZIPSIZE = "BACKUP_ZIPSIZE";
    public static final int BACKUP_ZIPSIZE_IDX = 13;
    
    private BACKUPDETAILS() {
    }
}
