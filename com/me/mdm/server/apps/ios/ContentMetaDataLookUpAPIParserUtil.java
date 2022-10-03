package com.me.mdm.server.apps.ios;

import org.json.JSONArray;
import org.json.JSONObject;

public class ContentMetaDataLookUpAPIParserUtil
{
    public static String getAppName(final JSONObject response) {
        return response.optString("name");
    }
    
    public static String getAdamId(final JSONObject response) {
        return response.optString("id");
    }
    
    public static String getReleaseData(final JSONObject response) {
        return response.optString("latestVersionReleaseDate");
    }
    
    public static String getSellerName(final JSONObject response) {
        return response.optString("artistName");
    }
    
    public static String getPrimaryGenreName(final JSONObject response) {
        final JSONArray genreNames = response.optJSONArray("genreNames");
        return genreNames.optString(0, "Not available");
    }
    
    public static String getAppIconImage(final JSONObject response) {
        final JSONObject artwork = response.getJSONObject("artwork");
        String iconUrl = artwork.optString("url");
        iconUrl = iconUrl.replace("{w}", "100");
        iconUrl = iconUrl.replace("{h}", "100");
        iconUrl = iconUrl.replace("{f}", "jpg");
        return iconUrl;
    }
    
    public static String getAppStoreURL(final JSONObject response) {
        return response.optString("url");
    }
    
    public static String getAppDescription(final JSONObject response) {
        final JSONObject description = response.getJSONObject("description");
        return description.optString("standard");
    }
    
    public static Double getAppPrice(final JSONObject response) {
        final JSONArray offerArray = response.optJSONArray("offers");
        final JSONObject offerJSON = offerArray.optJSONObject(0);
        if (offerJSON != null) {
            return offerJSON.optDouble("price");
        }
        return ContentMetaDataAPIConstants.KEY_DEFAULT_PRICE;
    }
    
    public static Boolean getIsPaidApp(final JSONObject response) {
        final Double appPrice = getAppPrice(response);
        if (appPrice == 0.0) {
            return false;
        }
        return true;
    }
    
    public static String getVersion(final JSONObject response) {
        final JSONArray offerArray = response.getJSONArray("offers");
        final JSONObject offerJSON = offerArray.optJSONObject(0);
        if (offerJSON != null) {
            final JSONObject versionJSON = offerJSON.optJSONObject("version");
            return versionJSON.optString("display", "1");
        }
        return "1";
    }
    
    public static Long getExternalId(final JSONObject response) {
        final JSONArray offerArray = response.getJSONArray("offers");
        final JSONObject offerJSON = offerArray.optJSONObject(0);
        if (offerJSON != null) {
            final JSONObject versionJSON = offerJSON.optJSONObject("version");
            return versionJSON.optLong("externalId");
        }
        return ContentMetaDataAPIConstants.KEY_DEFAULT_EXTERNAL_APP_VERSION_ID;
    }
    
    public static String getBundleId(final JSONObject response) {
        return response.optString("bundleId");
    }
    
    public static String getMinimumOSVersion(final JSONObject response) {
        return response.optString("minimumOSVersion");
    }
    
    public static int getSupportDevice(final JSONObject response) {
        int deviceSupport = 0;
        boolean iPadSupport = false;
        boolean iPhoneSupport = false;
        boolean appleTVSupport = false;
        boolean macOSSupport = false;
        boolean ipodSupport = false;
        final JSONArray deviceFamiliesArray = response.optJSONArray("deviceFamilies");
        if (deviceFamiliesArray != null) {
            for (int i = 0; i < deviceFamiliesArray.length(); ++i) {
                final String deviceType = deviceFamiliesArray.getString(i);
                if (deviceType.equalsIgnoreCase("ipad")) {
                    iPadSupport = true;
                }
                else if (deviceType.equalsIgnoreCase("iphone")) {
                    iPhoneSupport = true;
                }
                else if (deviceType.equalsIgnoreCase("tvos")) {
                    appleTVSupport = true;
                }
                else if (deviceType.equalsIgnoreCase("ipod")) {
                    ipodSupport = true;
                }
                else if (deviceType.equalsIgnoreCase("mac")) {
                    macOSSupport = true;
                }
            }
        }
        if (iPhoneSupport) {
            deviceSupport |= 0x2;
        }
        if (iPadSupport) {
            deviceSupport |= 0x1;
        }
        if (appleTVSupport) {
            deviceSupport |= 0x8;
        }
        if (macOSSupport) {
            deviceSupport |= 0x10;
        }
        if (ipodSupport) {
            deviceSupport |= 0x4;
        }
        return deviceSupport;
    }
}
