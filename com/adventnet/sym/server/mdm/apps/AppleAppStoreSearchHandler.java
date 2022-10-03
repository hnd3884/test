package com.adventnet.sym.server.mdm.apps;

import java.util.Hashtable;
import org.apache.commons.lang.StringUtils;
import java.util.List;
import com.adventnet.persistence.Row;
import com.adventnet.persistence.DataObject;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.sym.server.mdm.apps.vpp.VPPAssetsHandler;
import com.me.mdm.server.apps.ios.ContentMetaDataAppDetails;
import com.adventnet.i18n.I18N;
import com.me.mdm.server.apps.ios.ITunesMetaDataParserUtil;
import com.me.mdm.server.apps.ios.ITunesLookupAPIParserUtil;
import java.util.HashMap;
import com.adventnet.sym.server.mdm.util.MDMStringUtils;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.me.devicemanagement.framework.server.httpclient.DMHttpResponse;
import com.me.devicemanagement.framework.server.httpclient.DMHttpClient;
import com.me.devicemanagement.framework.server.httpclient.DMHttpRequest;
import com.me.mdm.server.apps.ios.vpp.VPPAppAPIRequestHandler;
import com.me.devicemanagement.framework.server.downloadmgr.SSLValidationType;
import java.util.ArrayList;
import org.json.JSONArray;
import java.util.Properties;
import com.me.devicemanagement.framework.server.downloadmgr.DownloadManager;
import org.json.JSONException;
import java.util.logging.Level;
import org.json.JSONObject;
import com.me.devicemanagement.framework.server.downloadmgr.DownloadStatus;
import java.util.logging.Logger;

public class AppleAppStoreSearchHandler
{
    public static Logger logger;
    public static final int ALL_SUPPORT = 1;
    public static final int IPHONE_SUPPORT = 2;
    public static final int IPAD_SUPPORT = 3;
    public static final String SEARCH_SOURCE_FILE_PATH = "https://itunes.apple.com/search";
    private static final String ITUNES_LOOKUP_API_URL = "https://itunes.apple.com/lookup";
    public static final String DOWNLOAD_STATUS = "status";
    public static final String DOWNLOAD_SUCCESS = "success";
    public static final String DOWNLOAD_FAILED = "error";
    public static final String DOWNLOADED_DATA = "downloadedData";
    public static final String ERROR_MSG = "errorMsg";
    public static final String DOMAIN_NOT_REACHABLE = "domainNotReachable";
    public static final String NETWORK_ERROR = "networkError";
    
    private JSONObject processDownloadedResponse(final DownloadStatus status) throws JSONException {
        final JSONObject responseJson = new JSONObject();
        try {
            if (status.getStatus() == 0) {
                responseJson.put("status", (Object)"success");
                responseJson.put("downloadedData", (Object)new JSONObject(status.getUrlDataBuffer()));
            }
            else if (status.getStatus() == 10008) {
                responseJson.put("status", (Object)"error");
                final Object errorMessageArgs = status.getErrorMessageArgs();
                try {
                    final int httpStatus = (int)errorMessageArgs;
                    responseJson.put("errorMsg", (Object)"domainNotReachable");
                }
                catch (final Exception ex) {
                    responseJson.put("errorMsg", (Object)"networkError");
                }
            }
        }
        catch (final Exception ex2) {
            try {
                AppleAppStoreSearchHandler.logger.log(Level.SEVERE, "AppleAppStoreSearchHandler : downloaded data when exception in processDownloadedResponse {0}", status.getUrlDataBuffer());
            }
            catch (final Exception ex3) {
                AppleAppStoreSearchHandler.logger.log(Level.SEVERE, "AppleAppStoreSearchHandler : Exception in getting data buffer from status {0}", ex3);
            }
            AppleAppStoreSearchHandler.logger.log(Level.SEVERE, "AppleAppStoreSearchHandler : Exception in processDownloadedResponse {0}", ex2);
            responseJson.put("status", (Object)"error");
            responseJson.put("errorMsg", (Object)"networkError");
        }
        return responseJson;
    }
    
    private void getEntitySpecificJSONArray(final DownloadManager dwnManager, final Properties properties, final JSONArray childResponseArray, final JSONArray trackNameNotMatchingResponseArray, final String appName, final ArrayList addedAppsBundleIdentifiers) throws Exception {
        final DownloadStatus status = dwnManager.GetFileDataBuffer("https://itunes.apple.com/search", properties, new SSLValidationType[0]);
        final JSONObject responseJson = this.processDownloadedResponse(status);
        if (responseJson.get("status") == "success") {
            final JSONObject appstoreObj = (JSONObject)responseJson.get("downloadedData");
            final JSONArray appStoreArray = appstoreObj.getJSONArray("results");
            if (appStoreArray != null) {
                for (int i = 0; i < appStoreArray.length(); ++i) {
                    final JSONObject item = appStoreArray.getJSONObject(i);
                    if (item.has("trackName")) {
                        final String trackName = (String)item.get("trackName");
                        try {
                            if (trackName != null && trackName.toLowerCase().contains(appName.toLowerCase())) {
                                this.populateResponseArray(item, childResponseArray, addedAppsBundleIdentifiers);
                            }
                            else if (trackName != null) {
                                this.populateResponseArray(item, trackNameNotMatchingResponseArray, addedAppsBundleIdentifiers);
                            }
                        }
                        catch (final Exception e) {
                            AppleAppStoreSearchHandler.logger.log(Level.WARNING, "Exception while parsing trackName - {0}. Exception trace {1}", new Object[] { trackName, e });
                        }
                    }
                }
            }
        }
        else {
            final JSONObject errorResponseJson = responseJson;
            childResponseArray.put((Object)errorResponseJson);
        }
    }
    
    public JSONObject getITunesMetaDataJSON(final String adamId, final String vppToken, final String countryCode, final String platform) throws JSONException {
        final String lookupUrl = VPPAppAPIRequestHandler.getInstance().getServiceUrl("contentMetadataLookupUrl");
        JSONObject responseJson = new JSONObject();
        final String lookupURLWithParams = lookupUrl.concat("?version=2&p=mdm-lockup&caller=MDM&l=en").concat("&cc=").concat((countryCode != null) ? countryCode : "US").concat("&platform=").concat(platform).concat("&id=").concat(adamId);
        final JSONObject jsonHeaders = new JSONObject();
        if (vppToken != null && !vppToken.equalsIgnoreCase("")) {
            jsonHeaders.put("Cookie", (Object)("itvt=" + vppToken));
        }
        try {
            final DMHttpRequest request = new DMHttpRequest();
            request.headers = jsonHeaders;
            request.url = lookupURLWithParams;
            request.method = "GET";
            final DMHttpClient client = new DMHttpClient();
            final DMHttpResponse response = client.execute(request);
            if (response.status == 200) {
                try {
                    responseJson = new JSONObject(response.responseBodyAsString);
                }
                catch (final JSONException e) {
                    AppleAppStoreSearchHandler.logger.log(Level.SEVERE, "AppleAppStoreSearchHandler getITunesMetaDataJSON() Error response data is not Json: ", response.responseBodyAsString);
                    responseJson.put("ERROR_CODE", 10005);
                    responseJson.put("ERROR_MSG", (Object)("Invalid data: " + e.getMessage()));
                }
            }
            else {
                responseJson.put("ERROR_CODE", 10008);
                responseJson.put("ERROR_MSG", (Object)("HTTP Error " + response.status + " " + response.responseBodyAsString));
            }
        }
        catch (final Exception e2) {
            AppleAppStoreSearchHandler.logger.log(Level.SEVERE, "AppleAppStoreSearchHandler Exception in getITunesMetaDataJSON() : {0}", e2);
            responseJson.put("ERROR_CODE", 10008);
            responseJson.put("ERROR_MSG", (Object)("Error " + e2.getMessage()));
        }
        AppleAppStoreSearchHandler.logger.log(Level.INFO, "AppleAppStoreSearchHandler getITunesMetaDataJSON() responseJSON: {0}", responseJson.toString());
        return responseJson;
    }
    
    private void populateResponseArray(final JSONObject item, final JSONArray responseArray, final ArrayList addedAppsBundleIdentifiers) throws Exception {
        boolean isPaidApp = false;
        final String sBundleID = (String)item.get("bundleId");
        if (!addedAppsBundleIdentifiers.contains(sBundleID)) {
            final JSONObject childResponseObject = new JSONObject();
            String appName = (String)item.get("trackName");
            if (appName.length() > 250) {
                appName = appName.substring(0, 249);
            }
            childResponseObject.put("trackName", (Object)appName);
            childResponseObject.put("trackId", item.get("trackId"));
            childResponseObject.put("bundleId", item.get("bundleId"));
            childResponseObject.put("appIcon", item.get("artworkUrl100"));
            childResponseObject.put("appCategory", item.get("primaryGenreName"));
            childResponseObject.put("version", item.get("version"));
            childResponseObject.put("price", item.get("price"));
            childResponseObject.put("trackViewUrl", item.get("trackViewUrl"));
            final Double sPrice = (Double)item.get("price");
            isPaidApp = (sPrice != 0.0);
            childResponseObject.put("isPaidApp", isPaidApp);
            final JSONArray supportedDevicesArray = (JSONArray)item.get("supportedDevices");
            final JSONObject appDetailsJson = new JSONObject();
            appDetailsJson.put("supportedDevices", (Object)supportedDevicesArray);
            appDetailsJson.put("kind", (item.opt("kind") == null) ? "" : item.opt("kind"));
            final int data = MDMUtil.getInstance().getDeviceSupport(appDetailsJson);
            childResponseObject.put("deviceSupport", data);
            childResponseObject.put("supportedDevices", (Object)AppsUtil.getInstance().getiOSSupportedDevicesString(data));
            responseArray.put((Object)childResponseObject);
            addedAppsBundleIdentifiers.add(sBundleID);
        }
    }
    
    public JSONArray getAppSuggestData(final String appName, final String params) {
        final JSONArray childResponseArray = new JSONArray();
        final JSONArray trackNameNotMatchingResponseArray = new JSONArray();
        final ArrayList addedAppsBundleIdentifiers = new ArrayList();
        try {
            final DownloadManager dwnManager = DownloadManager.getInstance();
            final Properties proxyDetails = ApiFactoryProvider.getServerSettingsAPI().getProxyConfiguration();
            final int proxyType = DownloadManager.proxyType;
            if (proxyType == 4) {
                final Properties pacProps = ApiFactoryProvider.getServerSettingsAPI().getProxyConfiguration("https://itunes.apple.com/search", proxyDetails);
                dwnManager.setProxyConfiguration(pacProps);
            }
            else {
                dwnManager.setProxyConfiguration(proxyDetails);
            }
            final Properties properties = new Properties();
            ((Hashtable<String, String>)properties).put("term", appName);
            if (params != null) {
                ((Hashtable<String, String>)properties).put("country", params);
            }
            else {
                ((Hashtable<String, String>)properties).put("country", "US");
            }
            ((Hashtable<String, String>)properties).put("entity", "software");
            ((Hashtable<String, String>)properties).put("limit", "25");
            this.getEntitySpecificJSONArray(dwnManager, properties, childResponseArray, trackNameNotMatchingResponseArray, appName, addedAppsBundleIdentifiers);
            ((Hashtable<String, String>)properties).put("entity", "iPadSoftware");
            this.getEntitySpecificJSONArray(dwnManager, properties, childResponseArray, trackNameNotMatchingResponseArray, appName, addedAppsBundleIdentifiers);
            ((Hashtable<String, String>)properties).put("entity", "tvSoftware");
            this.getEntitySpecificJSONArray(dwnManager, properties, childResponseArray, trackNameNotMatchingResponseArray, appName, addedAppsBundleIdentifiers);
            for (int iArrayIndex = 0; iArrayIndex < trackNameNotMatchingResponseArray.length(); ++iArrayIndex) {
                final JSONObject obj = trackNameNotMatchingResponseArray.getJSONObject(iArrayIndex);
                childResponseArray.put((Object)obj);
            }
        }
        catch (final Exception exp) {
            AppleAppStoreSearchHandler.logger.log(Level.WARNING, "Exception in getAppSuggestData {0}", exp);
            final JSONObject childResponseObject = new JSONObject();
            try {
                childResponseObject.put("error", true);
            }
            catch (final JSONException ex) {
                AppleAppStoreSearchHandler.logger.log(Level.WARNING, "Exception while adding in JSONObject-childResponseObject {0}", (Throwable)ex);
            }
            childResponseArray.put((Object)childResponseObject);
        }
        AppleAppStoreSearchHandler.logger.log(Level.FINE, "Final JSON Array {0}", childResponseArray);
        return childResponseArray;
    }
    
    public JSONObject getAppDetails(final String trackId, final String countryCode) {
        JSONObject responseObj = new JSONObject();
        final DMHttpClient dmHttpClient = new DMHttpClient();
        dmHttpClient.setUseProxyIfConfigured(true);
        final DMHttpRequest request = new DMHttpRequest();
        request.url = "https://itunes.apple.com/lookup";
        request.method = "GET";
        final JSONObject requestObject = new JSONObject();
        requestObject.put("id", (Object)trackId);
        if (!MDMStringUtils.isEmpty(countryCode)) {
            requestObject.put("country", (Object)countryCode);
        }
        else {
            requestObject.put("country", (Object)"US");
        }
        request.parameters = requestObject;
        try {
            final DMHttpResponse response = dmHttpClient.execute(request);
            if (response.status == 200) {
                responseObj = new JSONObject(response.responseBodyAsString);
            }
        }
        catch (final Exception e) {
            AppleAppStoreSearchHandler.logger.log(Level.SEVERE, "Exception in getAppDetailsFromAppFile {0}", e);
        }
        return responseObj;
    }
    
    private Boolean isPaidApp(final Object price) {
        final Double sPrice = (Double)price;
        if (sPrice == 0.0) {
            return false;
        }
        return true;
    }
    
    private HashMap getITunesLookupAPIHashMap(final JSONObject responseJson) {
        final HashMap allAppDetails = new HashMap();
        try {
            final JSONArray appStoreArray = responseJson.getJSONArray("results");
            for (int i = 0; i < appStoreArray.length(); ++i) {
                final HashMap hash = new HashMap();
                final JSONObject item = appStoreArray.getJSONObject(i);
                String appName = (String)item.get("trackName");
                if (appName.length() > 250) {
                    appName = appName.substring(0, 249);
                }
                if (appName != null) {
                    hash.put("appName", appName);
                    hash.put("trackId", item.get("trackId"));
                    hash.put("releaseDate", item.get("releaseDate"));
                    hash.put("sellerName", item.get("sellerName"));
                    hash.put("primaryGenreName", item.get("primaryGenreName"));
                    hash.put("artworkUrl100", item.get("artworkUrl100"));
                    hash.put("trackViewUrl", item.get("trackViewUrl"));
                    hash.put("description", item.get("description"));
                    hash.put("price", item.get("price"));
                    hash.put("isPaidApp", this.isPaidApp(item.get("price")));
                    hash.put("version", item.get("version"));
                    hash.put("bundleId", item.get("bundleId"));
                    hash.put("minimumOsVersion", item.optString("minimumOsVersion", ""));
                    final int supportedDevices = ITunesLookupAPIParserUtil.getSupportedDevices(item);
                    hash.put("supportedDevices", supportedDevices);
                    allAppDetails.put(item.get("trackId"), hash);
                }
            }
        }
        catch (final Exception ex) {
            AppleAppStoreSearchHandler.logger.log(Level.SEVERE, "Exception in getITunesLookupAPIHashMap {0}", ex);
        }
        return allAppDetails;
    }
    
    private HashMap getITunesMetaDataHashMap(final JSONObject responseJson, final int adamId) {
        final HashMap appDetailsMap = new HashMap();
        try {
            if (!responseJson.has("ERROR_CODE")) {
                final JSONObject resultsJson = responseJson.getJSONObject("results");
                if (resultsJson.length() != 0) {
                    final JSONObject appMetadataJSON = resultsJson.getJSONObject(adamId + "");
                    appDetailsMap.put("trackName", String.valueOf(appMetadataJSON.get("name")));
                    appDetailsMap.put("trackId", String.valueOf(appMetadataJSON.optString("id")));
                    appDetailsMap.put("bundleId", String.valueOf(appMetadataJSON.optString("bundleId")));
                    appDetailsMap.put("minimumOsVersion", String.valueOf(appMetadataJSON.optString("minimumOsVersion", "")));
                    final JSONArray offersJsonArray = appMetadataJSON.getJSONArray("offers");
                    if (offersJsonArray.length() > 0) {
                        final JSONObject offersJson = offersJsonArray.getJSONObject(0);
                        appDetailsMap.put("version", offersJson.has("version") ? offersJson.getJSONObject("version").get("display") : "1.0");
                        final double price = offersJson.has("price") ? offersJson.getDouble("price") : 0.0;
                        appDetailsMap.put("price", price);
                        appDetailsMap.put("isPaidApp", this.isPaidApp(price));
                    }
                    else {
                        appDetailsMap.put("version", "1.0");
                        appDetailsMap.put("price", 0.0);
                        appDetailsMap.put("isPaidApp", false);
                    }
                    appDetailsMap.put("primaryGenreName", String.valueOf(appMetadataJSON.getJSONArray("genres").getJSONObject(0).get("name")));
                    String artworkUrl = String.valueOf(appMetadataJSON.getJSONObject("artwork").get("url"));
                    artworkUrl = artworkUrl.replaceAll("\\{w\\}", "100").replaceAll("\\{h\\}", "100").replaceAll("\\{f\\}", "jpg");
                    appDetailsMap.put("artworkUrl100", artworkUrl);
                    appDetailsMap.put("trackViewUrl", String.valueOf(appMetadataJSON.get("url")));
                    appDetailsMap.put("description", (appMetadataJSON.getJSONObject("description") != null) ? String.valueOf(appMetadataJSON.getJSONObject("description").get("standard")) : "");
                    final int supportedDevices = ITunesMetaDataParserUtil.getSupportedDevices(appMetadataJSON);
                    appDetailsMap.put("supportedDevices", supportedDevices);
                    final Integer b2bAppType = ITunesMetaDataParserUtil.parseB2BAppType(appMetadataJSON);
                    appDetailsMap.put("privateApp", b2bAppType);
                }
                else {
                    appDetailsMap.put("errorCode", -505);
                    appDetailsMap.put("errorMessage", "APP NOT FOUND");
                }
            }
            else {
                appDetailsMap.put("errorCode", responseJson.get("ERROR_CODE"));
                final String errorMsg = I18N.getMsg("dc.mdm.vpp.apps_request_error_message", new Object[0]);
                appDetailsMap.put("errorMessage", errorMsg);
            }
        }
        catch (final Exception ex) {
            AppleAppStoreSearchHandler.logger.log(Level.SEVERE, "Exception in getITunesMetaDataHashMap {0}", ex);
        }
        return appDetailsMap;
    }
    
    public HashMap getCompleteAppDetails(final int adamId, final Long customerId) {
        return this.getCompleteAppDetails(adamId, customerId, false, null);
    }
    
    public HashMap getCompleteAppDetails(final int adamId, final Long customerId, final Boolean checkiTunes, String countryCode) {
        AppleAppStoreSearchHandler.logger.log(Level.INFO, "getCompleteAppDetails: appID/trackID: {0}", new Object[] { adamId });
        HashMap appDetailsMap = new HashMap();
        try {
            JSONObject responseJson = null;
            JSONArray appStoreArray = null;
            if (countryCode == null) {
                countryCode = AppsUtil.getInstance().getAppStoreRegionValue();
            }
            if (checkiTunes) {
                responseJson = this.getAppDetails(adamId + "", countryCode);
                appStoreArray = responseJson.optJSONArray("results");
            }
            if (appStoreArray != null && appStoreArray.length() > 0) {
                appDetailsMap = this.getITunesLookupAPIHashMap(responseJson).get(adamId);
            }
            else {
                AppleAppStoreSearchHandler.logger.log(Level.INFO, "No results found for adamID[{0}] in itunesstore, Possible reason B2B , going to git BusinessStore with VPP token", adamId);
                responseJson = this.getAppDataFromITunesMetaData(adamId + "", countryCode, customerId);
                appDetailsMap = this.getITunesMetaDataHashMap(responseJson, adamId);
            }
            if (!appDetailsMap.isEmpty()) {
                appDetailsMap.put("COUNTRY_CODE", countryCode);
            }
        }
        catch (final Exception ex) {
            AppleAppStoreSearchHandler.logger.log(Level.SEVERE, "Exception in getCompleteAppDetails {0}", ex);
        }
        return appDetailsMap;
    }
    
    public ContentMetaDataAppDetails getCompleteAppDetailsObject(final int adamId, final Boolean checkiTunes, String countryCode) {
        AppleAppStoreSearchHandler.logger.log(Level.INFO, "getCompleteAppDetails: appID/trackID: {0}", new Object[] { String.valueOf(adamId) });
        ContentMetaDataAppDetails contentMetaDataAppDetails = null;
        try {
            JSONObject responseJson = null;
            JSONArray appStoreArray = null;
            if (countryCode == null) {
                countryCode = AppsUtil.getInstance().getAppStoreRegionValue();
            }
            if (checkiTunes) {
                responseJson = this.getAppDetails(adamId + "", countryCode);
                appStoreArray = responseJson.optJSONArray("results");
            }
            if (appStoreArray != null && appStoreArray.length() > 0) {
                contentMetaDataAppDetails = this.getITunesLookupAPIObject(responseJson);
            }
            else {
                AppleAppStoreSearchHandler.logger.log(Level.INFO, "No results found for adamId {0}", adamId);
            }
        }
        catch (final Exception ex) {
            AppleAppStoreSearchHandler.logger.log(Level.SEVERE, "Exception in getCompleteAppDetails {0}", ex);
        }
        return contentMetaDataAppDetails;
    }
    
    private ContentMetaDataAppDetails getITunesLookupAPIObject(final JSONObject responseJson) {
        try {
            final JSONArray appStoreArray = responseJson.getJSONArray("results");
            final int i = 0;
            if (i < appStoreArray.length()) {
                final ContentMetaDataAppDetails appDetails = new ContentMetaDataAppDetails();
                final JSONObject item = appStoreArray.getJSONObject(i);
                String appName = (String)item.get("trackName");
                if (appName.length() > 250) {
                    appName = appName.substring(0, 249);
                }
                if (appName != null) {
                    appDetails.setAppName(appName);
                    appDetails.setAdamId((String)item.get("trackId"));
                    appDetails.setReleaseDate((String)item.get("releaseDate"));
                    appDetails.setSellerName((String)item.get("releaseDate"));
                    appDetails.setPrimaryGenreName((String)item.get("primaryGenreName"));
                    appDetails.setAppIconImageURL((String)item.get("artworkUrl100"));
                    appDetails.setAppStoreURL((String)item.get("trackViewUrl"));
                    appDetails.setAppDescription((String)item.get("description"));
                    appDetails.setAppPrice((Double)item.get("price"));
                    appDetails.setIsPaidApp(this.isPaidApp(item.get("price")));
                    appDetails.setAppVersion((String)item.get("version"));
                    appDetails.setBundleId((String)item.get("bundleId"));
                    appDetails.setMinimumOSVersion(item.optString("minimumOsVersion", ""));
                    final int supportedDevices = ITunesLookupAPIParserUtil.getSupportedDevices(item);
                    appDetails.setSupportDevice(supportedDevices);
                }
                return appDetails;
            }
        }
        catch (final Exception ex) {
            AppleAppStoreSearchHandler.logger.log(Level.SEVERE, "Exception in getITunesLookupAPIHashMap {0}", ex);
        }
        return null;
    }
    
    private JSONObject getMdPackageAndAppJsonFromITunesMetaData(final JSONObject iTunesMetaJson) throws Exception {
        JSONObject json = null;
        try {
            json = new JSONObject();
            final String latestVersion = ITunesMetaDataParserUtil.getDisplayVersion(iTunesMetaJson);
            final String name = ITunesMetaDataParserUtil.getName(iTunesMetaJson);
            final String bundleId = ITunesMetaDataParserUtil.getBundleId(iTunesMetaJson);
            final Long bundleSize = ITunesMetaDataParserUtil.getBundleSize(iTunesMetaJson);
            final int supportedDevices = ITunesMetaDataParserUtil.getSupportedDevices(iTunesMetaJson);
            final Integer b2bAppType = ITunesMetaDataParserUtil.parseB2BAppType(iTunesMetaJson);
            final String imageURL = ITunesLookupAPIParserUtil.getImageURL(iTunesMetaJson);
            json.put("APP_NAME", (Object)name);
            json.put("APP_TITLE", (Object)name);
            json.put("APP_VERSION", (Object)latestVersion);
            json.put("APP_NAME_SHORT_VERSION", (Object)"--");
            json.put("IDENTIFIER", (Object)bundleId);
            json.put("BUNDLE_SIZE", (Object)bundleSize);
            json.put("PLATFORM_TYPE", 1);
            json.put("SUPPORTED_DEVICES", supportedDevices);
            json.put("PRIVATE_APP_TYPE", (Object)b2bAppType);
            if (imageURL != null) {
                json.put("DISPLAY_IMAGE_LOC", (Object)imageURL);
            }
        }
        catch (final Exception ex) {
            AppleAppStoreSearchHandler.logger.log(Level.SEVERE, "Exception in processing the response jon in getMdPackageAndAppJsonFromITunesMetaData {0} for  iTunesMetaJson:{1}", new Object[] { ex.getMessage(), iTunesMetaJson });
            return null;
        }
        return json;
    }
    
    private JSONObject getMdPackageAndAppJsonFromITunesLookupAPI(final JSONObject iTunesLookUpAPIJson) throws Exception {
        JSONObject json = null;
        try {
            json = new JSONObject();
            final String latestVersion = ITunesLookupAPIParserUtil.getVersion(iTunesLookUpAPIJson);
            final String name = ITunesLookupAPIParserUtil.getName(iTunesLookUpAPIJson);
            final String bundleId = ITunesLookupAPIParserUtil.getBundleId(iTunesLookUpAPIJson);
            final Long bundleSize = ITunesLookupAPIParserUtil.getBundleSize(iTunesLookUpAPIJson);
            final int supportedDevices = ITunesLookupAPIParserUtil.getSupportedDevices(iTunesLookUpAPIJson);
            json.put("APP_NAME", (Object)name);
            json.put("APP_TITLE", (Object)name);
            json.put("APP_VERSION", (Object)latestVersion);
            json.put("APP_NAME_SHORT_VERSION", (Object)"--");
            json.put("IDENTIFIER", (Object)bundleId);
            json.put("BUNDLE_SIZE", (Object)bundleSize);
            json.put("PLATFORM_TYPE", 1);
            json.put("SUPPORTED_DEVICES", supportedDevices);
        }
        catch (final Exception ex) {
            AppleAppStoreSearchHandler.logger.log(Level.SEVERE, "Exception in processing the response jon in getMdPackageAndAppJsonFromITunesLookupAPI {0}", ex.getMessage());
            return null;
        }
        return json;
    }
    
    public JSONObject getiTunesDataForAppAsJson(final String adamId, final String countryCode, final Long customerID) throws JSONException, Exception {
        JSONObject appJson = null;
        final JSONObject iTunesResponse = this.getAppDataFromITunesMetaData(adamId, countryCode, customerID);
        appJson = ITunesMetaDataParserUtil.getPerAppMetaData(iTunesResponse, adamId);
        if (iTunesResponse != null) {
            appJson = this.getMdPackageAndAppJsonFromITunesMetaData(appJson);
        }
        if (appJson == null) {
            AppleAppStoreSearchHandler.logger.log(Level.SEVERE, "Parsed appJSON from Itunes is empty . ItunesResponse :{0}", iTunesResponse);
        }
        return appJson;
    }
    
    public HashMap getAPIAppDetails(final String bundleID, String countryCode) throws Exception {
        final DMHttpClient dmHttpClient = new DMHttpClient();
        dmHttpClient.setUseProxyIfConfigured(true);
        HashMap appDetails = new HashMap();
        final DMHttpRequest request = new DMHttpRequest();
        request.url = "https://itunes.apple.com/lookup";
        request.method = "GET";
        JSONObject responseObj = new JSONObject();
        final JSONObject requestObject = new JSONObject();
        requestObject.put("bundleId", (Object)bundleID);
        if (countryCode == null) {
            countryCode = AppsUtil.getInstance().getAppStoreRegionValue();
        }
        if (!MDMStringUtils.isEmpty(countryCode)) {
            requestObject.put("country", (Object)countryCode);
        }
        else {
            requestObject.put("country", (Object)"US");
        }
        request.parameters = requestObject;
        try {
            final DMHttpResponse response = dmHttpClient.execute(request);
            if (response.status == 200) {
                responseObj = new JSONObject(response.responseBodyAsString);
                if (responseObj.optInt("resultCount") > 0) {
                    final JSONObject item = responseObj.getJSONArray("results").getJSONObject(0);
                    final int trackID = (int)item.get("trackId");
                    appDetails = this.getITunesLookupAPIHashMap(responseObj).get(trackID);
                }
            }
            else {
                appDetails.put("errorMessage", response.status);
                appDetails.put("errorCode", 10008);
            }
        }
        catch (final Exception e) {
            AppleAppStoreSearchHandler.logger.log(Level.SEVERE, "Exception in getAPIAppDetails {0}", e);
        }
        return appDetails;
    }
    
    private JSONObject getAppDataFromITunesMetaData(final String adamId, String countryCode, final Long customerID) throws JSONException, Exception {
        final DataObject vppAssetDO = VPPAssetsHandler.getInstance().getVPPAssetDo(adamId, customerID);
        String defaultPlatform = null;
        String sToken = null;
        if (!vppAssetDO.isEmpty()) {
            final Row assetRow = vppAssetDO.getFirstRow("MdVppAsset");
            final Long assetID = (Long)assetRow.get("VPP_ASSET_ID");
            final Row tokenRow = vppAssetDO.getRow("MdVPPTokenDetails", new Criteria(Column.getColumn("MdVppAsset", "VPP_ASSET_ID"), (Object)assetID, 0));
            countryCode = (String)tokenRow.get("COUNTRY_CODE");
            sToken = (String)tokenRow.get("S_TOKEN");
            final int defaultPlatformInt = (int)tokenRow.get("DEFAULT_PLATFORM");
            if (defaultPlatformInt == 0) {
                defaultPlatform = "enterprisestore";
            }
            else {
                defaultPlatform = "volumestore";
            }
        }
        JSONObject parsedResponse = null;
        JSONObject rawResponse = null;
        if (sToken != null) {
            if (defaultPlatform == null) {
                defaultPlatform = "enterprisestore";
            }
            rawResponse = this.getITunesMetaDataJSON(adamId, sToken, countryCode, defaultPlatform);
            AppleAppStoreSearchHandler.logger.log(Level.INFO, "VPP APP Hit for {0} with vpp token: adamID: {1}  -> ", new Object[] { defaultPlatform, adamId });
            parsedResponse = ITunesMetaDataParserUtil.getPerAppMetaData(rawResponse, adamId);
            if (parsedResponse == null || parsedResponse.length() == 0) {
                String alternatePlatform = null;
                if (defaultPlatform.equalsIgnoreCase("enterprisestore")) {
                    alternatePlatform = "volumestore";
                }
                else {
                    alternatePlatform = "enterprisestore";
                }
                AppleAppStoreSearchHandler.logger.log(Level.WARNING, "No response from defaultPlatform - {0}. Hence hitting with alternate platform - {1}", new Object[] { defaultPlatform, alternatePlatform });
                rawResponse = this.getITunesMetaDataJSON(adamId, sToken, countryCode, alternatePlatform);
                parsedResponse = ITunesMetaDataParserUtil.getPerAppMetaData(rawResponse, adamId);
                AppleAppStoreSearchHandler.logger.log(Level.INFO, "VPP APP Hit for {0} with vpp token: adamID: {1} -> ", new Object[] { alternatePlatform, adamId });
            }
        }
        if (sToken == null || parsedResponse == null || parsedResponse.length() == 0) {
            AppleAppStoreSearchHandler.logger.log(Level.WARNING, "No response for adamID - {0} in both platforms. Hence hitting with iTunes as platform", adamId);
            rawResponse = this.getITunesMetaDataJSON(adamId, sToken, countryCode, "itunes");
            AppleAppStoreSearchHandler.logger.log(Level.INFO, "[Non VPP or Empty VPP response] Hitting itunesstore without vpp token:[{0}] -> {1}", new Object[] { adamId, rawResponse });
        }
        return rawResponse;
    }
    
    public HashMap getBulkAppDetails(final List storeIdList, String countryCode) throws Exception {
        final DMHttpClient dmHttpClient = new DMHttpClient();
        dmHttpClient.setUseProxyIfConfigured(true);
        final DMHttpRequest request = new DMHttpRequest();
        request.url = "https://itunes.apple.com/lookup";
        request.method = "GET";
        JSONObject responseObj = new JSONObject();
        final JSONObject requestObject = new JSONObject();
        HashMap bulkAppsResponse = new HashMap();
        final JSONArray outputObject = null;
        requestObject.put("id", (Object)StringUtils.join(storeIdList.toArray(), ","));
        if (countryCode == null) {
            countryCode = AppsUtil.getInstance().getAppStoreRegionValue();
        }
        if (!MDMStringUtils.isEmpty(countryCode)) {
            requestObject.put("country", (Object)countryCode);
        }
        else {
            requestObject.put("country", (Object)"US");
        }
        request.parameters = requestObject;
        try {
            final DMHttpResponse response = dmHttpClient.execute(request);
            if (response.status == 200) {
                responseObj = new JSONObject(response.responseBodyAsString);
                bulkAppsResponse = this.getITunesLookupAPIHashMap(responseObj);
            }
        }
        catch (final Exception e) {
            AppleAppStoreSearchHandler.logger.log(Level.SEVERE, "Exception in getBulkAppDetails {0}", e);
        }
        return bulkAppsResponse;
    }
    
    static {
        AppleAppStoreSearchHandler.logger = Logger.getLogger("MDMConfigLogger");
    }
}
