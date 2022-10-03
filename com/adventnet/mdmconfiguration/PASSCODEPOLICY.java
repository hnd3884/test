package com.adventnet.mdmconfiguration;

public final class PASSCODEPOLICY
{
    public static final String TABLE = "PasscodePolicy";
    public static final String CONFIG_DATA_ITEM_ID = "CONFIG_DATA_ITEM_ID";
    public static final int CONFIG_DATA_ITEM_ID_IDX = 1;
    public static final String ALLOW_SIMPLE_VALUE = "ALLOW_SIMPLE_VALUE";
    public static final int ALLOW_SIMPLE_VALUE_IDX = 2;
    public static final String REQUIRE_ALPHANUMERIC = "REQUIRE_ALPHANUMERIC";
    public static final int REQUIRE_ALPHANUMERIC_IDX = 3;
    public static final String MIN_PASSCODE_LENGTH = "MIN_PASSCODE_LENGTH";
    public static final int MIN_PASSCODE_LENGTH_IDX = 4;
    public static final String MIN_COMPLEX_CHARS = "MIN_COMPLEX_CHARS";
    public static final int MIN_COMPLEX_CHARS_IDX = 5;
    public static final String MAX_PASSCODE_AGE = "MAX_PASSCODE_AGE";
    public static final int MAX_PASSCODE_AGE_IDX = 6;
    public static final String AUTO_LOCK_IDLE_FOR = "AUTO_LOCK_IDLE_FOR";
    public static final int AUTO_LOCK_IDLE_FOR_IDX = 7;
    public static final String NO_OF_PASSCODE_MAINTAINED = "NO_OF_PASSCODE_MAINTAINED";
    public static final int NO_OF_PASSCODE_MAINTAINED_IDX = 8;
    public static final String MAX_GRACE_PERIOD = "MAX_GRACE_PERIOD";
    public static final int MAX_GRACE_PERIOD_IDX = 9;
    public static final String MAX_FAILED_ATTEMPTS = "MAX_FAILED_ATTEMPTS";
    public static final int MAX_FAILED_ATTEMPTS_IDX = 10;
    public static final String RESTRICT_PASSCODE = "RESTRICT_PASSCODE";
    public static final int RESTRICT_PASSCODE_IDX = 11;
    public static final String FORCE_PASSCODE = "FORCE_PASSCODE";
    public static final int FORCE_PASSCODE_IDX = 12;
    public static final String CHANGE_AT_NEXT_AUTH = "CHANGE_AT_NEXT_AUTH";
    public static final int CHANGE_AT_NEXT_AUTH_IDX = 13;
    public static final String MINS_FAILED_LOGIN_RESET = "MINS_FAILED_LOGIN_RESET";
    public static final int MINS_FAILED_LOGIN_RESET_IDX = 14;
    
    private PASSCODEPOLICY() {
    }
}
