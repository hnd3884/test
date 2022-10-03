package com.me.mdm.server.apps.multiversion;

public class AppVersionHandler
{
    public static AppVersionHandlerInterface getInstance(final int platformType) {
        AppVersionHandlerInterface appVersionHandlerInterface = null;
        switch (platformType) {
            case 1: {
                appVersionHandlerInterface = new IOSAppVersionHandler();
                break;
            }
            case 2: {
                appVersionHandlerInterface = new AndroidAppVersionHandler();
                break;
            }
            case 3: {
                appVersionHandlerInterface = new WindowsAppVersionHandler();
                break;
            }
            case 4: {
                appVersionHandlerInterface = new ChromeAppVersionHandler();
                break;
            }
        }
        return appVersionHandlerInterface;
    }
}
