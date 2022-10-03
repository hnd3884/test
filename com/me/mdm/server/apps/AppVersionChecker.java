package com.me.mdm.server.apps;

import org.json.JSONObject;
import com.adventnet.sym.server.mdm.util.VersionChecker;

public class AppVersionChecker
{
    protected VersionChecker versionChecker;
    
    public AppVersionChecker() {
        this.versionChecker = new VersionChecker();
    }
    
    public static AppVersionChecker getInstance(final Integer platformType) {
        AppVersionChecker appVersionChecker = null;
        if (platformType.equals(1)) {
            appVersionChecker = new IOSAppVersionChecker();
        }
        else if (platformType.equals(2)) {
            appVersionChecker = new AndroidAppVersionChecker();
        }
        else {
            appVersionChecker = new AppVersionChecker();
        }
        return appVersionChecker;
    }
    
    public boolean isAppVersionGreater(final JSONObject mdAppDetailsJson1, final JSONObject mdAppDetailsJson2) throws Exception {
        final String versionNumberStr1 = String.valueOf(mdAppDetailsJson1.get("APP_VERSION"));
        final String versionNumberStr2 = String.valueOf(mdAppDetailsJson2.get("APP_VERSION"));
        return this.versionChecker.isGreater(versionNumberStr1, versionNumberStr2);
    }
    
    public boolean isAppVersionGreaterOrEqual(final JSONObject mdAppDetailsJson1, final JSONObject mdAppDetailsJson2) throws Exception {
        final String versionNumberStr1 = String.valueOf(mdAppDetailsJson1.get("APP_VERSION"));
        final String versionNumberStr2 = String.valueOf(mdAppDetailsJson2.get("APP_VERSION"));
        return this.versionChecker.isGreaterOrEqual(versionNumberStr1, versionNumberStr2);
    }
    
    public boolean isFilteredAppVersionGreater(final JSONObject mdAppDetailsJson1, final JSONObject mdAppDetailsJson2) throws Exception {
        final String versionNumberStr1 = String.valueOf(mdAppDetailsJson1.get("APP_VERSION"));
        final String versionNumberStr2 = String.valueOf(mdAppDetailsJson2.get("APP_VERSION"));
        return this.versionChecker.checkIfFilteredVersionGreater(versionNumberStr1, versionNumberStr2);
    }
}
