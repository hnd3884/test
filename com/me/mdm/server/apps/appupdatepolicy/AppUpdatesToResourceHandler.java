package com.me.mdm.server.apps.appupdatepolicy;

public class AppUpdatesToResourceHandler
{
    public static AppUpdatesToResourceInterface getInstance(final int resourceType) {
        switch (resourceType) {
            case 101: {
                return new AppUpdateToCGHandler();
            }
            case 120: {
                return new AppUpdatesToDeviceHandler();
            }
            case 2: {
                return new AppUpdatesToUserHandler();
            }
            default: {
                return null;
            }
        }
    }
}
