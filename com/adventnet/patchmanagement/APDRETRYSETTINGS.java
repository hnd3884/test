package com.adventnet.patchmanagement;

public final class APDRETRYSETTINGS
{
    public static final String TABLE = "APDRetrySettings";
    public static final String RETRY_SETTINGS_ID = "RETRY_SETTINGS_ID";
    public static final int RETRY_SETTINGS_ID_IDX = 1;
    public static final String ENABLE_RETRY = "ENABLE_RETRY";
    public static final int ENABLE_RETRY_IDX = 2;
    public static final String NO_OF_RETRIES = "NO_OF_RETRIES";
    public static final int NO_OF_RETRIES_IDX = 3;
    public static final String REFRESH_MIN_RETRY = "REFRESH_MIN_RETRY";
    public static final int REFRESH_MIN_RETRY_IDX = 4;
    public static final String LOGON_STARTUP_MIN_RETRY = "LOGON_STARTUP_MIN_RETRY";
    public static final int LOGON_STARTUP_MIN_RETRY_IDX = 5;
    
    private APDRETRYSETTINGS() {
    }
}
