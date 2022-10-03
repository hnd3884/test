package com.adventnet.metapersistence;

public final class DBCREDENTIALSAUDIT
{
    public static final String TABLE = "DBCredentialsAudit";
    public static final String USERNAME = "USERNAME";
    public static final int USERNAME_IDX = 1;
    public static final String PASSWORD = "PASSWORD";
    public static final int PASSWORD_IDX = 2;
    public static final String LAST_MODIFIED_TIME = "LAST_MODIFIED_TIME";
    public static final int LAST_MODIFIED_TIME_IDX = 3;
    
    private DBCREDENTIALSAUDIT() {
    }
}
