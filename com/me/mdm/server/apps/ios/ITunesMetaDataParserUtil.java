package com.me.mdm.server.apps.ios;

import com.adventnet.sym.server.mdm.util.MDMUtil;
import org.json.JSONArray;
import org.json.JSONObject;

public class ITunesMetaDataParserUtil
{
    public static JSONObject getPerAppMetaData(final JSONObject iTunesResponseJson, final String adamID) {
        JSONObject json = getResults(iTunesResponseJson);
        if (json != null) {
            json = json.optJSONObject(adamID);
        }
        return json;
    }
    
    public static JSONObject getResults(final JSONObject iTunesResponseJson) {
        return iTunesResponseJson.optJSONObject("results");
    }
    
    public static JSONObject getOffersJson(final JSONObject appMetaDataJson) throws Exception {
        return appMetaDataJson.getJSONArray("offers").getJSONObject(0);
    }
    
    public static JSONObject getVersionJson(final JSONObject appMetaDataJson) throws Exception {
        return getOffersJson(appMetaDataJson).getJSONObject("version");
    }
    
    public static String getDisplayVersion(final JSONObject appMetaDataJson) throws Exception {
        return String.valueOf(getVersionJson(appMetaDataJson).get("display"));
    }
    
    public static String getName(final JSONObject appMetaDataJson) throws Exception {
        return String.valueOf(appMetaDataJson.get("name"));
    }
    
    public static String getBundleId(final JSONObject appMetaDataJson) throws Exception {
        return String.valueOf(appMetaDataJson.get("bundleId"));
    }
    
    public static JSONObject getAssetsJson(final JSONObject appMetaDataJson) throws Exception {
        return getOffersJson(appMetaDataJson).getJSONArray("assets").getJSONObject(0);
    }
    
    public static Long getBundleSize(final JSONObject appMetaDataJson) throws Exception {
        final JSONObject assetsJSON = getAssetsJson(appMetaDataJson);
        if (assetsJSON.has("size")) {
            return Long.parseLong(String.valueOf(assetsJSON.get("size")));
        }
        return null;
    }
    
    public static String getAppKind(final JSONObject appMetaDataJson) throws Exception {
        return String.valueOf(appMetaDataJson.get("kind"));
    }
    
    public static int getSupportedDevices(final JSONObject appMetaDataJson) throws Exception {
        final JSONArray deviceFamiliesArray = appMetaDataJson.optJSONArray("deviceFamilies");
        final JSONObject appMetaDetails = new JSONObject();
        appMetaDetails.put("supportedDevices", (Object)((deviceFamiliesArray == null) ? new JSONArray() : deviceFamiliesArray));
        appMetaDetails.put("kind", (Object)getAppKind(appMetaDataJson));
        return MDMUtil.getInstance().getDeviceSupport(appMetaDetails);
    }
    
    public static Integer parseB2BAppType(final JSONObject appMetaDataJson) throws Exception {
        final Boolean privateAppType = getIsB2BApp(appMetaDataJson);
        return (int)(Object)privateAppType;
    }
    
    public static Boolean getIsB2BApp(final JSONObject appMetaDataJson) throws Exception {
        if (appMetaDataJson == null) {
            return false;
        }
        return appMetaDataJson.optBoolean("isB2BCustomApp", false);
    }
}
