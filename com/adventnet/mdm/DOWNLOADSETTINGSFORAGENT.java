package com.adventnet.mdm;

public final class DOWNLOADSETTINGSFORAGENT
{
    public static final String TABLE = "DownloadSettingsForAgent";
    public static final String SETTINGS_ID = "SETTINGS_ID";
    public static final int SETTINGS_ID_IDX = 1;
    public static final String MAX_RETRY_COUNT = "MAX_RETRY_COUNT";
    public static final int MAX_RETRY_COUNT_IDX = 2;
    public static final String MIN_RETRY_DELAY = "MIN_RETRY_DELAY";
    public static final int MIN_RETRY_DELAY_IDX = 3;
    public static final String MAX_RETRY_DELAY = "MAX_RETRY_DELAY";
    public static final int MAX_RETRY_DELAY_IDX = 4;
    public static final String DELAY_RANDOM = "DELAY_RANDOM";
    public static final int DELAY_RANDOM_IDX = 5;
    public static final String EXCLUDED_DOMAIN = "EXCLUDED_DOMAIN";
    public static final int EXCLUDED_DOMAIN_IDX = 6;
    public static final String CUSTOM_RETRY_DELAY = "CUSTOM_RETRY_DELAY";
    public static final int CUSTOM_RETRY_DELAY_IDX = 7;
    public static final String CUSTOMER_ID = "CUSTOMER_ID";
    public static final int CUSTOMER_ID_IDX = 8;
    
    private DOWNLOADSETTINGSFORAGENT() {
    }
}
