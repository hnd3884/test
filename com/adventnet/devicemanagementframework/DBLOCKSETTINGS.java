package com.adventnet.devicemanagementframework;

public final class DBLOCKSETTINGS
{
    public static final String TABLE = "DbLockSettings";
    public static final String DBLOCKSETTINGS_ID = "DBLOCKSETTINGS_ID";
    public static final int DBLOCKSETTINGS_ID_IDX = 1;
    public static final String LOCK_TIME_LIMIT = "LOCK_TIME_LIMIT";
    public static final int LOCK_TIME_LIMIT_IDX = 2;
    public static final String IS_AUTOMATIC = "IS_AUTOMATIC";
    public static final int IS_AUTOMATIC_IDX = 3;
    public static final String NOTIFY_LIMIT = "NOTIFY_LIMIT";
    public static final int NOTIFY_LIMIT_IDX = 4;
    public static final String CLEANUP_LIMIT = "CLEANUP_LIMIT";
    public static final int CLEANUP_LIMIT_IDX = 5;
    public static final String DBLOCKS_RETAIN_COUNT = "DBLOCKS_RETAIN_COUNT";
    public static final int DBLOCKS_RETAIN_COUNT_IDX = 6;
    
    private DBLOCKSETTINGS() {
    }
}
