package com.me.mdm.server.apps;

import java.util.logging.Level;
import com.adventnet.sym.server.mdm.util.MDMStringUtils;
import org.json.JSONObject;
import java.util.logging.Logger;

public class AndroidAppVersionChecker extends AppVersionChecker
{
    public static Logger logger;
    
    private boolean isVersionNumberGreater(final String versionNumberStr1, final String versionNumberStr2) {
        return this.versionChecker.isGreater(versionNumberStr1, versionNumberStr2);
    }
    
    private boolean isAppVersionGreaterOrEqual(final String versionNumberStr1, final String versionNumberStr2) {
        return this.versionChecker.isGreaterOrEqual(versionNumberStr1, versionNumberStr2);
    }
    
    @Override
    public boolean isAppVersionGreater(final JSONObject appDetailsJson1, final JSONObject appDetailsJson2) throws Exception {
        final String appVersion1 = appDetailsJson1.optString("APP_NAME_SHORT_VERSION", "--");
        final String appVersion2 = appDetailsJson2.optString("APP_NAME_SHORT_VERSION", "--");
        if (!MDMStringUtils.isEmpty(appVersion1) && !MDMStringUtils.isEmpty(appVersion2) && !appVersion1.equals(appVersion2)) {
            return this.isVersionNumberGreater(appVersion1, appVersion2);
        }
        final String versionName1 = appDetailsJson1.optString("APP_VERSION", "--");
        final String versionName2 = appDetailsJson2.optString("APP_VERSION", "--");
        if (MDMStringUtils.isEmpty(versionName1) || MDMStringUtils.isEmpty(versionName2)) {
            AndroidAppVersionChecker.logger.log(Level.WARNING, "Empty version when checking versions {0} {1}", new Object[] { versionName1, versionName2 });
            return true;
        }
        if (versionName1.matches("^\\d+(\\.\\d+)*$") && versionName2.matches("^\\d+(\\.\\d+)*$")) {
            return this.isVersionNumberGreater(versionName1, versionName2);
        }
        if (MDMStringUtils.isEmpty(appVersion1) && !MDMStringUtils.isEmpty(appVersion2)) {
            AndroidAppVersionChecker.logger.log(Level.WARNING, "Existing version code is empty, but new version has. Hence skipping check");
            return false;
        }
        AndroidAppVersionChecker.logger.log(Level.WARNING, "Version checking not done properly {0} {1}", new Object[] { versionName1, versionName2 });
        return !versionName1.equals(versionName2);
    }
    
    @Override
    public boolean isAppVersionGreaterOrEqual(final JSONObject mdAppDetailsJson1, final JSONObject mdAppDetailsJson2) throws Exception {
        final String appVersion1 = mdAppDetailsJson1.optString("APP_NAME_SHORT_VERSION", "--");
        final String appVersion2 = mdAppDetailsJson2.optString("APP_NAME_SHORT_VERSION", "--");
        if (!MDMStringUtils.isEmpty(appVersion1) && !MDMStringUtils.isEmpty(appVersion2) && !appVersion1.equals(appVersion2)) {
            return this.isAppVersionGreaterOrEqual(appVersion1, appVersion2);
        }
        final String versionName1 = mdAppDetailsJson1.optString("APP_VERSION", "--");
        final String versionName2 = mdAppDetailsJson2.optString("APP_VERSION", "--");
        if (MDMStringUtils.isEmpty(versionName1) || MDMStringUtils.isEmpty(versionName2)) {
            AndroidAppVersionChecker.logger.log(Level.WARNING, "Empty version when checking versions {0} {1}", new Object[] { versionName1, versionName2 });
            return true;
        }
        if (versionName1.matches("^\\d+(\\.\\d+)*$") && versionName2.matches("^\\d+(\\.\\d+)*$")) {
            return this.isAppVersionGreaterOrEqual(versionName1, versionName2);
        }
        AndroidAppVersionChecker.logger.log(Level.WARNING, "Invalid version naming conventions hence skipping validations -> {0} {1} ", new Object[] { versionName1, versionName2 });
        return true;
    }
    
    static {
        AndroidAppVersionChecker.logger = Logger.getLogger("MDMAppMgmtLogger");
    }
}
