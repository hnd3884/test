package com.me.mdm.server.command.kiosk;

public class PauseResumeKioskConstants
{
    public static final String KIOSK_RUNNING_REMARK = "profile_applied";
    public static final String KIOSK_REMOVED_REMARK = "profile_removed";
    public static final String KIOSK_NOT_APPLIED_REMARK = "none";
    public static final Integer KIOSK_RUNNING;
    public static final Integer KIOSK_PAUSED;
    public static final Integer KIOSK_NOT_APPLIED_OR_REMOVED;
    
    static {
        KIOSK_RUNNING = 1;
        KIOSK_PAUSED = 2;
        KIOSK_NOT_APPLIED_OR_REMOVED = 3;
    }
}
