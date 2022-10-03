package com.me.mdm.server.apps.ios;

import com.adventnet.persistence.Row;
import com.adventnet.persistence.DataObject;
import com.me.mdm.server.apps.ios.vpp.VPPTokenDataHandler;
import java.util.Iterator;
import java.util.HashMap;
import com.me.devicemanagement.framework.server.httpclient.DMHttpResponse;
import org.json.JSONException;
import java.util.logging.Level;
import com.me.devicemanagement.framework.server.httpclient.DMHttpClient;
import com.me.devicemanagement.framework.server.httpclient.DMHttpRequest;
import com.me.mdm.server.apps.ios.vpp.VPPAppAPIRequestHandler;
import org.json.JSONObject;
import java.util.List;
import java.util.logging.Logger;

public class ContentMetaDataAPIHandler
{
    public static Logger logger;
    
    private JSONObject getContentMetaDataJSON(final List<String> adamId, final String vppToken, final String countryCode, final String platform) {
        final String contentMetaDataURL = VPPAppAPIRequestHandler.getInstance().getServiceUrl("contentMetadataLookupUrl");
        final String contentMetaDataURLWithParams = contentMetaDataURL.concat("?version=2&p=mdm-lockup&caller=MDM&l=en").concat("&cc=").concat((countryCode != null) ? countryCode : "US").concat("&platform=").concat(platform).concat("&id=").concat(String.join(",", adamId));
        final JSONObject jsonHeaders = new JSONObject();
        if (vppToken != null && !vppToken.equalsIgnoreCase("")) {
            jsonHeaders.put("Cookie", (Object)("itvt=" + vppToken));
        }
        JSONObject responseJson = new JSONObject();
        try {
            final DMHttpRequest request = new DMHttpRequest();
            request.headers = jsonHeaders;
            request.url = contentMetaDataURLWithParams;
            request.method = "GET";
            final DMHttpClient client = new DMHttpClient();
            final DMHttpResponse response = client.execute(request);
            if (response.status == 200) {
                try {
                    responseJson = new JSONObject(response.responseBodyAsString);
                }
                catch (final JSONException e) {
                    ContentMetaDataAPIHandler.logger.log(Level.SEVERE, "ContentMetaDataAPIHandler getContentMetaDataAPIJSON Error response data is not Json: ", response.responseBodyAsString);
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
            ContentMetaDataAPIHandler.logger.log(Level.SEVERE, "ContentMetaDataAPIHandler Exception in getContentMetaDataAPIJSON() : {0}", e2);
            responseJson.put("ERROR_CODE", 10008);
            responseJson.put("ERROR_MSG", (Object)("Error " + e2.getMessage()));
        }
        ContentMetaDataAPIHandler.logger.log(Level.INFO, "Request completed Successfully getContentMetaDataAPIJSON() responseJSON: {0}", responseJson.toString());
        return responseJson;
    }
    
    private HashMap getContentMetaDataHashMap(final JSONObject responseObject) {
        final HashMap allAppDetailsHashMap = new HashMap();
        try {
            final JSONObject appsDetailResponse = responseObject.optJSONObject("results");
            if (appsDetailResponse != null) {
                final Iterator<String> key = appsDetailResponse.keys();
                while (key.hasNext()) {
                    final String adamId = key.next();
                    final JSONObject singleAppDetailsResponse = appsDetailResponse.getJSONObject(adamId);
                    final ContentMetaDataAppDetails appDetailsObject = new ContentMetaDataAppDetails();
                    appDetailsObject.setAppName(ContentMetaDataLookUpAPIParserUtil.getAppName(singleAppDetailsResponse));
                    appDetailsObject.setAdamId(ContentMetaDataLookUpAPIParserUtil.getAdamId(singleAppDetailsResponse));
                    appDetailsObject.setBundleId(ContentMetaDataLookUpAPIParserUtil.getBundleId(singleAppDetailsResponse));
                    appDetailsObject.setReleaseDate(ContentMetaDataLookUpAPIParserUtil.getReleaseData(singleAppDetailsResponse));
                    appDetailsObject.setSellerName(ContentMetaDataLookUpAPIParserUtil.getSellerName(singleAppDetailsResponse));
                    appDetailsObject.setPrimaryGenreName(ContentMetaDataLookUpAPIParserUtil.getPrimaryGenreName(singleAppDetailsResponse));
                    appDetailsObject.setAppIconImageURL(ContentMetaDataLookUpAPIParserUtil.getAppIconImage(singleAppDetailsResponse));
                    appDetailsObject.setAppStoreURL(ContentMetaDataLookUpAPIParserUtil.getAppStoreURL(singleAppDetailsResponse));
                    appDetailsObject.setAppDescription(ContentMetaDataLookUpAPIParserUtil.getAppDescription(singleAppDetailsResponse));
                    appDetailsObject.setAppPrice(ContentMetaDataLookUpAPIParserUtil.getAppPrice(singleAppDetailsResponse));
                    appDetailsObject.setIsPaidApp(ContentMetaDataLookUpAPIParserUtil.getIsPaidApp(singleAppDetailsResponse));
                    appDetailsObject.setAppVersion(ContentMetaDataLookUpAPIParserUtil.getVersion(singleAppDetailsResponse));
                    appDetailsObject.setExternalAppVersionID(ContentMetaDataLookUpAPIParserUtil.getExternalId(singleAppDetailsResponse));
                    appDetailsObject.setMinimumOSVersion(ContentMetaDataLookUpAPIParserUtil.getMinimumOSVersion(singleAppDetailsResponse));
                    appDetailsObject.setSupportDevice(ContentMetaDataLookUpAPIParserUtil.getSupportDevice(singleAppDetailsResponse));
                    allAppDetailsHashMap.put(Integer.parseInt(adamId), appDetailsObject);
                }
                ContentMetaDataAPIHandler.logger.log(Level.INFO, "ResponseJSON Parsed successfully getContentMetaDataHashMap()");
            }
        }
        catch (final Exception e) {
            ContentMetaDataAPIHandler.logger.log(Level.SEVERE, "Exception in getContentMetaDataHashMap {0}", e);
        }
        return allAppDetailsHashMap;
    }
    
    public HashMap getVPPAppDetails(final List adamId, final Long businessStoreId) throws Exception {
        final DataObject vppAssetDO = VPPTokenDataHandler.getInstance().getVPPTokenDO(businessStoreId);
        String vppToken = null;
        String platform = null;
        String countryCode = null;
        if (vppAssetDO != null) {
            final Row tokenRow = vppAssetDO.getFirstRow("MdVPPTokenDetails");
            countryCode = (String)tokenRow.get("COUNTRY_CODE");
            vppToken = (String)tokenRow.get("S_TOKEN");
            final int defaultPlatformInt = (int)tokenRow.get("DEFAULT_PLATFORM");
            if (defaultPlatformInt == 0) {
                platform = "enterprisestore";
            }
            else {
                platform = "volumestore";
            }
        }
        ContentMetaDataAPIHandler.logger.log(Level.INFO, "AppDetails collected in getContentMetaDataHashMap() for BusinessStore{0}", businessStoreId);
        return this.getAllAppDetails(adamId, vppToken, countryCode, platform);
    }
    
    public HashMap getNonVPPAppDetails(final List adamId, final String countryCode) {
        final String sToken = "";
        return this.getAllAppDetails(adamId, sToken, countryCode, "enterprisestore");
    }
    
    private HashMap getAllAppDetails(final List adamId, final String sToken, final String countryCode, final String platform) {
        HashMap allAppDetailsHashMap = new HashMap();
        try {
            final JSONObject response = this.getContentMetaDataJSON(adamId, sToken, countryCode, platform);
            allAppDetailsHashMap = this.getContentMetaDataHashMap(response);
        }
        catch (final Exception e) {
            ContentMetaDataAPIHandler.logger.log(Level.SEVERE, "Exception in getAllAppDetails {0}", e);
        }
        return allAppDetailsHashMap;
    }
    
    static {
        ContentMetaDataAPIHandler.logger = Logger.getLogger("MDMConfigLogger");
    }
}
