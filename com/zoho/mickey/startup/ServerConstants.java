package com.zoho.mickey.startup;

public class ServerConstants
{
    public static final int STARTING = 1;
    public static final int STARTED = 2;
    public static final int DOWN = 3;
    public static final int COLD_START_EXCEPTION = 4;
    public static final int COLD_START_ABRUPTLY_STOPPED = 5;
    public static final int MIGRATION_INVOKED = 6;
    public static final int MIGRATION_COMPLETED = 7;
    public static final int FULLBACKUP_COMPLETED_AFTER_PPM = 8;
    
    ServerConstants() {
        throw new IllegalAccessError("Constants class");
    }
}
