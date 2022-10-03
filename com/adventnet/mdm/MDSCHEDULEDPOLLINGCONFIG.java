package com.adventnet.mdm;

public final class MDSCHEDULEDPOLLINGCONFIG
{
    public static final String TABLE = "MDScheduledPollingConfig";
    public static final String SCHEDULED_CONFIG_ID = "SCHEDULED_CONFIG_ID";
    public static final int SCHEDULED_CONFIG_ID_IDX = 1;
    public static final String POLLING_INTERVAL = "POLLING_INTERVAL";
    public static final int POLLING_INTERVAL_IDX = 2;
    public static final String INITIAL_POLLING_INTERVAL = "INITIAL_POLLING_INTERVAL";
    public static final int INITIAL_POLLING_INTERVAL_IDX = 3;
    public static final String MAX_INITIAL_RETRIES = "MAX_INITIAL_RETRIES";
    public static final int MAX_INITIAL_RETRIES_IDX = 4;
    
    private MDSCHEDULEDPOLLINGCONFIG() {
    }
}
