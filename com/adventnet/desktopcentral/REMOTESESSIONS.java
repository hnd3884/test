package com.adventnet.desktopcentral;

public final class REMOTESESSIONS
{
    public static final String TABLE = "RemoteSessions";
    public static final String UNIQUE_ID = "UNIQUE_ID";
    public static final int UNIQUE_ID_IDX = 1;
    public static final String RESOURCE_ID = "RESOURCE_ID";
    public static final int RESOURCE_ID_IDX = 2;
    public static final String COMPUTER_NAME = "COMPUTER_NAME";
    public static final int COMPUTER_NAME_IDX = 3;
    public static final String USER_NAME = "USER_NAME";
    public static final int USER_NAME_IDX = 4;
    public static final String OPENED_SESSION_COUNT = "OPENED_SESSION_COUNT";
    public static final int OPENED_SESSION_COUNT_IDX = 5;
    public static final String ACTIVE_TIME = "ACTIVE_TIME";
    public static final int ACTIVE_TIME_IDX = 6;
    public static final String IDLE_TIME = "IDLE_TIME";
    public static final int IDLE_TIME_IDX = 7;
    public static final String USER_FLAGS = "USER_FLAGS";
    public static final int USER_FLAGS_IDX = 8;
    public static final String CLIENT_TYPE_NAME = "CLIENT_TYPE_NAME";
    public static final int CLIENT_TYPE_NAME_IDX = 9;
    public static final String TRANSPORT_NAME = "TRANSPORT_NAME";
    public static final int TRANSPORT_NAME_IDX = 10;
    
    private REMOTESESSIONS() {
    }
}
