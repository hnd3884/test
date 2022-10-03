package com.me.mdm.server.apps.ios;

import com.adventnet.sym.server.mdm.util.MDMUtil;
import org.json.JSONArray;
import org.json.JSONObject;

public class ITunesLookupAPIParserUtil
{
    public static JSONArray getResults(final JSONObject iTunesResponseJson) {
        return iTunesResponseJson.optJSONArray("results");
    }
    
    public static JSONObject getPerAppMetaData(final JSONObject iTunesResponseJson) {
        final JSONArray json = getResults(iTunesResponseJson);
        if (json != null) {
            return json.optJSONObject(0);
        }
        return null;
    }
    
    public static String getVersion(final JSONObject appLookupJson) throws Exception {
        return String.valueOf(appLookupJson.get("version"));
    }
    
    public static String getName(final JSONObject appLookupJson) throws Exception {
        return String.valueOf(appLookupJson.get("trackName"));
    }
    
    public static String getBundleId(final JSONObject appLookupJson) throws Exception {
        return String.valueOf(appLookupJson.get("bundleId"));
    }
    
    public static Long getBundleSize(final JSONObject appLookupJson) throws Exception {
        return Long.parseLong(String.valueOf(appLookupJson.get("fileSizeBytes")));
    }
    
    public static String getImageURL(final JSONObject appLookupJson) throws Exception {
        String artworkUrl = null;
        if (appLookupJson.has("artwork") && appLookupJson.getJSONObject("artwork").has("url")) {
            artworkUrl = String.valueOf(appLookupJson.getJSONObject("artwork").get("url"));
            artworkUrl = artworkUrl.replaceAll("\\{w\\}", "100").replaceAll("\\{h\\}", "100").replaceAll("\\{f\\}", "jpg");
        }
        return artworkUrl;
    }
    
    public static int getSupportedDevices(final JSONObject appMetaDataJson) throws Exception {
        final JSONArray supportedDevicesArray = appMetaDataJson.optJSONArray("supportedDevices");
        final JSONObject appMetaDetails = new JSONObject();
        appMetaDetails.put("supportedDevices", (Object)((supportedDevicesArray == null) ? new JSONArray() : supportedDevicesArray));
        appMetaDetails.put("kind", (Object)String.valueOf(appMetaDataJson.get("kind")));
        final int supportedDevice = MDMUtil.getInstance().getDeviceSupport(appMetaDetails);
        return supportedDevice;
    }
}
