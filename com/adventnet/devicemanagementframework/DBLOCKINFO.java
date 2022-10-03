package com.adventnet.devicemanagementframework;

public final class DBLOCKINFO
{
    public static final String TABLE = "DbLockInfo";
    public static final String DBLOCKINFO_ID = "DBLOCKINFO_ID";
    public static final int DBLOCKINFO_ID_IDX = 1;
    public static final String FILE_NAME = "FILE_NAME";
    public static final int FILE_NAME_IDX = 2;
    public static final String CREATED_TIME = "CREATED_TIME";
    public static final int CREATED_TIME_IDX = 3;
    public static final String NO_OF_QUERIES_LOCKED = "NO_OF_QUERIES_LOCKED";
    public static final int NO_OF_QUERIES_LOCKED_IDX = 4;
    public static final String LOCKED_QUERY_MAX_TIME = "LOCKED_QUERY_MAX_TIME";
    public static final int LOCKED_QUERY_MAX_TIME_IDX = 5;
    public static final String THREADS_BLOCKED = "THREADS_BLOCKED";
    public static final int THREADS_BLOCKED_IDX = 6;
    public static final String IS_DELETED = "IS_DELETED";
    public static final int IS_DELETED_IDX = 7;
    
    private DBLOCKINFO() {
    }
}
