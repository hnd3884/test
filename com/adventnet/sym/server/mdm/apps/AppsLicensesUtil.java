package com.adventnet.sym.server.mdm.apps;

import com.adventnet.sym.server.mdm.apps.android.AndroidAppLicenseMgmtHandler;
import com.adventnet.sym.server.mdm.apps.ios.AppleAppLicenseMgmtHandler;

public class AppsLicensesUtil
{
    public static AppLicenseMgmtHandler getInstance(final int platformType) {
        AppLicenseMgmtHandler appLicenseMgmtHandler = null;
        if (platformType == 1) {
            appLicenseMgmtHandler = new AppleAppLicenseMgmtHandler();
        }
        else if (platformType == 2) {
            appLicenseMgmtHandler = new AndroidAppLicenseMgmtHandler();
        }
        else {
            appLicenseMgmtHandler = new AppLicenseMgmtHandler();
        }
        return appLicenseMgmtHandler;
    }
}
