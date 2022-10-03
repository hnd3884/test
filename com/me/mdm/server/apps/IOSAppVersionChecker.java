package com.me.mdm.server.apps;

import org.json.JSONObject;
import java.util.logging.Level;
import java.util.logging.Logger;

public class IOSAppVersionChecker extends AppVersionChecker
{
    Logger logger;
    
    public IOSAppVersionChecker() {
        this.logger = Logger.getLogger("MDMVPPAppsMgmtLogger");
    }
    
    public boolean isVersionNumberGreater(final String versionNumberStr1, final String versionNumberStr2) {
        try {
            return this.versionChecker.isGreater(versionNumberStr1, versionNumberStr2);
        }
        catch (final NumberFormatException ex) {
            this.logger.log(Level.SEVERE, ex, () -> "Exception while parsing version old:" + s + " and new:" + s2 + " . NumberFormatException has occured, So safely assuming uploaded version is greater than the old version as Enterprise app in IOS would contain special and alphanumeric characters . Allowing to upload");
            return true;
        }
    }
    
    public boolean isBuildNumberGreater(final String buildStr1, final String buildStr2) {
        return this.versionChecker.isGreater(buildStr1, buildStr2);
    }
    
    @Override
    public boolean isAppVersionGreater(final JSONObject appDetailsJson1, final JSONObject appDetailsJson2) throws Exception {
        final Long externalIdFromAppDetails1 = appDetailsJson1.optLong("EXTERNAL_APP_VERSION_ID");
        final Long externalIdFromAppDetails2 = appDetailsJson2.optLong("EXTERNAL_APP_VERSION_ID");
        boolean isNewVersion = false;
        if (externalIdFromAppDetails1.equals(0L)) {
            return this.isVersionNumberGreater(String.valueOf(appDetailsJson1.get("APP_VERSION")), appDetailsJson2.getString("APP_VERSION"));
        }
        if (!externalIdFromAppDetails1.equals(externalIdFromAppDetails2)) {
            isNewVersion = true;
        }
        return isNewVersion;
    }
}
