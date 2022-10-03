package com.adventnet.remotedesktopsharing;

public final class RCIDLESESSIONSETTINGS
{
    public static final String TABLE = "RCIdleSessionSettings";
    public static final String SETTINGS_ID = "SETTINGS_ID";
    public static final int SETTINGS_ID_IDX = 1;
    public static final String CUSTOMER_ID = "CUSTOMER_ID";
    public static final int CUSTOMER_ID_IDX = 2;
    public static final String IS_IDLE_SESSION_TIMEOUT = "IS_IDLE_SESSION_TIMEOUT";
    public static final int IS_IDLE_SESSION_TIMEOUT_IDX = 3;
    public static final String IDLE_TIMEOUT = "IDLE_TIMEOUT";
    public static final int IDLE_TIMEOUT_IDX = 4;
    public static final String TIMEOUT_ACTION = "TIMEOUT_ACTION";
    public static final int TIMEOUT_ACTION_IDX = 5;
    public static final String IDLE_TIME_POLLING_INTERVAL = "IDLE_TIME_POLLING_INTERVAL";
    public static final int IDLE_TIME_POLLING_INTERVAL_IDX = 6;
    
    private RCIDLESESSIONSETTINGS() {
    }
}
