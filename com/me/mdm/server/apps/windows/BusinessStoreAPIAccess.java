package com.me.mdm.server.apps.windows;

import com.me.devicemanagement.framework.server.httpclient.DMHttpResponse;
import com.me.devicemanagement.framework.server.httpclient.DMHttpRequest;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import java.util.HashMap;
import org.json.JSONException;
import java.util.logging.Level;
import org.json.JSONObject;
import java.util.logging.Logger;

public class BusinessStoreAPIAccess
{
    private static final String BSTORE_RESOURCE = "https://onestore.microsoft.com";
    private static final String BSTORE_LOGIN_AUTHORITY = "https://login.microsoftonline.com/{0}/oauth2/token";
    private static final String INVENTORY_QUERY_URL = "https://bspmts.mp.microsoft.com/V1/Inventory";
    private static final String PACKAGE_BASIC_QUERY_URL = "https://bspmts.mp.microsoft.com/V1/Products/{0}/{1}";
    private static final String PACKAGE_DETAILS_QUERY_URL = "https://bspmts.mp.microsoft.com/V1/Products/{0}/{1}/packages";
    private static final String SPECIFIC_PACKAGE_DETAILS_QUERY_URL = "https://bspmts.mp.microsoft.com/V1/Products/{0}/{1}/packages/{2}";
    private static final String PACKAGE_LOCALE_DETAILS_QUERY_URL = "https://bspmts.mp.microsoft.com/V1/Products/{0}/{1}/LocalizedDetails/{2}";
    private static final String OFFLINE_LICENSE_QUERY_URL = "https://bspmts.mp.microsoft.com/V1/Products/{0}/{1}/OfflineLicense/{2}";
    private static final String PRODUCT_ID_QUERY_URL = "https://bspmts.mp.microsoft.com/v1/public/catalog/Retail/Products/{0}/applockerdata";
    private static final String ACCESS_TOKEN = "access_token";
    private static final String EXPIRES_ON = "expires_on";
    public static final String TENANT_ID = "TenantID";
    public static final String CLIENT_ID = "ClientID";
    public static final String CLIENT_SECRET = "ClientSecret";
    public static final String TYPE = "Type";
    public static final String PACKAGE_ID = "PackageID";
    public static final String PACKAGEFULLNAME = "PackageFullName";
    public static final String SKUID = "SKUID";
    public static final String LOCALE = "Locale";
    public static final String CONTENT_ID = "ContentID";
    public static final String STORE_ID = "StoreID";
    public static final String INVENTORY = "Inventory";
    public static final String PACKAGE_BASIC_INFO = "PackageBasicInfo";
    public static final String PACKAGE_DETAILS = "PackageDetails";
    public static final String SPECIFIC_PACKAGE_DETAILS = "SpecificPackageDetails";
    public static final String LOCALISED_DETAILS = "LocalisedDetails";
    public static final String OFFLINE_LICENSE = "OfflineLicense";
    public static final String PACKAGE_ID_QUERY = "PackageIDQuery";
    public Logger logger;
    private String accessToken;
    private Long expiresOn;
    private String tenantID;
    private String clientID;
    private String clientSecret;
    
    public BusinessStoreAPIAccess() {
        this.logger = Logger.getLogger("MDMBStoreLogger");
        this.accessToken = null;
        this.expiresOn = null;
        this.tenantID = null;
        this.clientID = null;
        this.clientSecret = null;
    }
    
    public boolean initialise(final JSONObject params) throws JSONException {
        boolean success = true;
        if (this.accessToken == null || this.getRemainingTime() < 0L) {
            this.tenantID = String.valueOf(params.get("TenantID"));
            this.clientID = String.valueOf(params.get("ClientID"));
            this.clientSecret = String.valueOf(params.get("ClientSecret"));
            final JSONObject token = this.getBstoreAccessToken();
            if (token.has("access_token")) {
                this.accessToken = "Bearer " + token.get("access_token");
                this.expiresOn = token.getLong("expires_on");
            }
            else {
                this.logger.log(Level.WARNING, "getting access token has failed . Response : {0}", token);
                success = false;
            }
        }
        return success;
    }
    
    private String getAccessToken() throws JSONException {
        if (this.accessToken == null || this.getRemainingTime() < 1000L) {
            final JSONObject token = this.getBstoreAccessToken();
            this.accessToken = "Bearer " + token.get("access_token");
            this.expiresOn = token.getLong("expires_on");
        }
        return this.accessToken;
    }
    
    private JSONObject getBstoreAccessToken() throws JSONException {
        final HashMap<String, String> bodyData = new HashMap<String, String>();
        bodyData.put("client_id", this.clientID);
        bodyData.put("client_secret", this.clientSecret);
        bodyData.put("resource", "https://onestore.microsoft.com");
        bodyData.put("grant_type", "client_credentials");
        final String encodedBodyData = SyMUtil.encodeURLbodyParams((HashMap)bodyData);
        final JSONObject headers = new JSONObject().put("Content-Length", encodedBodyData.getBytes().length);
        return this.getDataFromURL(headers, "https://login.microsoftonline.com/{0}/oauth2/token".replace("{0}", this.tenantID), "POST", encodedBodyData);
    }
    
    public JSONObject getDataFromBusinessStore(final JSONObject params) throws JSONException {
        final String type = String.valueOf(params.get("Type"));
        JSONObject returnObj = null;
        if (type.contains("Inventory")) {
            returnObj = this.getInventoryQuery(params);
        }
        else if (type.contains("PackageBasicInfo")) {
            returnObj = this.getPackageBasicInfo(params);
        }
        else if (type.equals("PackageDetails")) {
            returnObj = this.getPackageDetails(params);
        }
        else if (type.contains("LocalisedDetails")) {
            returnObj = this.getLocalisedProductDetails(params);
        }
        else if (type.contains("OfflineLicense")) {
            returnObj = this.getOfflineLicense(params);
        }
        else if (type.contains("SpecificPackageDetails")) {
            returnObj = this.getSpecificPackageDetails(params);
        }
        else if (type.contains("PackageIDQuery")) {
            returnObj = this.getProductIDfromStore(params);
        }
        return returnObj;
    }
    
    private JSONObject getInventoryQuery(final JSONObject param) throws JSONException {
        final JSONObject header = new JSONObject().put("Authorization", (Object)this.getAccessToken());
        final String continuationToken = param.optString("continuationToken");
        String url = "https://bspmts.mp.microsoft.com/V1/Inventory";
        if (continuationToken != null) {
            url = url + "?" + "continuationToken" + "=" + continuationToken;
        }
        return this.getDataFromURL(header, url, "GET", null);
    }
    
    private JSONObject getPackageBasicInfo(final JSONObject param) throws JSONException {
        final String packageID = String.valueOf(param.get("PackageID"));
        final String skuid = String.valueOf(param.get("SKUID"));
        final JSONObject header = new JSONObject().put("Authorization", (Object)this.getAccessToken());
        final String url = "https://bspmts.mp.microsoft.com/V1/Products/{0}/{1}".replace("{0}", packageID).replace("{1}", skuid);
        return this.getDataFromURL(header, url, "GET", null);
    }
    
    private JSONObject getPackageDetails(final JSONObject param) throws JSONException {
        final String packageID = String.valueOf(param.get("PackageID"));
        final String skuid = String.valueOf(param.get("SKUID"));
        final JSONObject header = new JSONObject().put("Authorization", (Object)this.getAccessToken());
        final String url = "https://bspmts.mp.microsoft.com/V1/Products/{0}/{1}/packages".replace("{0}", packageID).replace("{1}", skuid);
        return this.getDataFromURL(header, url, "GET", null);
    }
    
    private JSONObject getSpecificPackageDetails(final JSONObject param) throws JSONException {
        final String packageID = String.valueOf(param.get("PackageID"));
        final String skuid = String.valueOf(param.get("SKUID"));
        final String pkgFullName = String.valueOf(param.get("PackageFullName"));
        final JSONObject header = new JSONObject().put("Authorization", (Object)this.getAccessToken());
        final String url = "https://bspmts.mp.microsoft.com/V1/Products/{0}/{1}/packages/{2}".replace("{0}", packageID).replace("{1}", skuid).replace("{2}", pkgFullName);
        return this.getDataFromURL(header, url, "GET", null);
    }
    
    private JSONObject getLocalisedProductDetails(final JSONObject params) throws JSONException {
        final String packageID = String.valueOf(params.get("PackageID"));
        final String skuid = String.valueOf(params.get("SKUID"));
        final String locale = String.valueOf(params.get("Locale"));
        final JSONObject header = new JSONObject().put("Authorization", (Object)this.getAccessToken());
        final String url = "https://bspmts.mp.microsoft.com/V1/Products/{0}/{1}/LocalizedDetails/{2}".replace("{0}", packageID).replace("{1}", skuid).replace("{2}", locale);
        return this.getDataFromURL(header, url, "GET", null);
    }
    
    private JSONObject getOfflineLicense(final JSONObject params) throws JSONException {
        final String packageID = String.valueOf(params.get("PackageID"));
        final String skuid = String.valueOf(params.get("SKUID"));
        final String contentID = String.valueOf(params.get("ContentID"));
        final JSONObject header = new JSONObject().put("Authorization", (Object)this.getAccessToken());
        final String url = "https://bspmts.mp.microsoft.com/V1/Products/{0}/{1}/OfflineLicense/{2}".replace("{0}", packageID).replace("{1}", skuid).replace("{2}", contentID);
        return this.getDataFromURL(header, url, "POST", null);
    }
    
    private JSONObject getDataFromURL(final JSONObject header, final String url, final String method, final String data) throws JSONException {
        final DMHttpRequest dmHttpRequest = new DMHttpRequest();
        dmHttpRequest.url = url;
        dmHttpRequest.method = method;
        dmHttpRequest.headers = header;
        if (data != null) {
            dmHttpRequest.data = data.getBytes();
        }
        final JSONObject resultJSObject = this.executeDMHttpRequest(dmHttpRequest);
        return resultJSObject;
    }
    
    private JSONObject getProductIDfromStore(final JSONObject params) throws JSONException {
        final String storeID = String.valueOf(params.get("StoreID"));
        final String url = "https://bspmts.mp.microsoft.com/v1/public/catalog/Retail/Products/{0}/applockerdata".replace("{0}", storeID);
        final JSONObject jsonObject = this.getDataFromURL(new JSONObject(), url, "GET", null);
        return jsonObject;
    }
    
    private JSONObject executeDMHttpRequest(final DMHttpRequest dmHttpRequest) throws JSONException {
        final DMHttpResponse dmHttpResponse = SyMUtil.executeDMHttpRequest(dmHttpRequest);
        final String responseString = dmHttpResponse.responseBodyAsString;
        JSONObject resultJSObject = new JSONObject();
        if (responseString != null) {
            resultJSObject = new JSONObject(responseString);
        }
        return resultJSObject;
    }
    
    private Long getRemainingTime() {
        return this.expiresOn * 1000L - System.currentTimeMillis();
    }
}
