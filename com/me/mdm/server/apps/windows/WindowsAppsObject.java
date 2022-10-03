package com.me.mdm.server.apps.windows;

import com.adventnet.sym.server.mdm.util.VersionChecker;
import org.json.JSONArray;
import org.json.JSONException;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.me.mdm.server.windows.apps.WindowsAppExtractor;
import com.adventnet.sym.server.mdm.apps.AppsUtil;
import org.json.JSONObject;

public class WindowsAppsObject implements Comparable<WindowsAppsObject>
{
    private String platFormType;
    private String version;
    private String minVersion;
    private String[] architecuture;
    private JSONObject packageJson;
    
    public void setMinVersion(final String minVersion) {
        this.minVersion = minVersion;
    }
    
    public String getMinVersion() {
        return this.minVersion;
    }
    
    public String getPlatFormType() {
        return this.platFormType;
    }
    
    public String getVersion() {
        return this.version;
    }
    
    public String[] getArchitecuture() {
        return this.architecuture;
    }
    
    public void setPlatFormType(final String platFormType) {
        this.platFormType = platFormType;
    }
    
    public JSONObject getPackageJson() {
        return this.packageJson;
    }
    
    public void setPackageJson(final JSONObject packageJson) {
        this.packageJson = packageJson;
    }
    
    public void setVersion(final String version) {
        this.version = version;
    }
    
    public void setArchitecuture(final String[] architecuture) {
        this.architecuture = architecuture;
    }
    
    @Override
    public String toString() {
        final JSONObject appJSON = new JSONObject();
        try {
            final JSONObject windowsAppData = new JSONObject();
            final JSONObject MDPackagetoAppData = new JSONObject();
            final JSONObject packageData = this.getPackageJson();
            windowsAppData.put("packageId", (Object)String.valueOf(packageData.get("packageId")));
            final JSONArray aumids = packageData.optJSONArray("aumids");
            if (aumids != null && aumids.length() != 0) {
                windowsAppData.put("AUMID", (Object)String.valueOf(aumids.get(0)));
            }
            appJSON.put("packageIdentifier", (Object)String.valueOf(packageData.get("packageFullName")));
            appJSON.put("BUNDLE_SIZE", (Object)String.valueOf(packageData.get("fileSize")));
            windowsAppData.put("fileHash", (Object)String.valueOf(packageData.get("fileHash")));
            windowsAppData.put("contentId", (Object)String.valueOf(packageData.get("contentId")));
            appJSON.put("APP_VERSION", (Object)this.getVersion());
            MDPackagetoAppData.put("SUPPORTED_ARCH", (Object)AppsUtil.getInstance().getSupportedArchCode(this.getArchitecuture()));
            MDPackagetoAppData.put("SUPPORTED_DEVICES", (Object)WindowsAppExtractor.getSupportedDeviceCode(this.getPlatFormType()));
            MDPackagetoAppData.put("MIN_OS", (Object)this.getMinVersion());
            appJSON.put("MdPackageToAppDataFrom", (Object)MDPackagetoAppData);
            appJSON.put("WindowsAppData", (Object)windowsAppData);
        }
        catch (final JSONException e) {
            final Logger logger = Logger.getLogger("MDMLogger");
            logger.log(Level.WARNING, "Exception in converting Windows Package data to string");
        }
        return appJSON.toString();
    }
    
    @Override
    public int compareTo(final WindowsAppsObject o) {
        final VersionChecker versionChecker = new VersionChecker();
        if (versionChecker.isEqual(o.getVersion(), this.getVersion())) {
            return this.getArchitecuture().length - o.getArchitecuture().length;
        }
        return versionChecker.isGreater(this.getVersion(), o.getVersion()) ? 1 : -1;
    }
}
