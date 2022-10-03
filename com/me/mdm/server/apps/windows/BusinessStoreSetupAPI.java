package com.me.mdm.server.apps.windows;

import com.me.devicemanagement.framework.server.httpclient.DMHttpResponse;
import org.json.JSONArray;
import com.me.idps.core.oauth.OauthException;
import com.me.mdm.api.error.APIHTTPException;
import java.util.logging.Level;
import com.me.mdm.server.windows.apps.WpAppSettingsHandler;
import com.me.idps.core.factory.IdpsFactoryProvider;
import com.me.idps.core.service.azure.AzureOauthImpl;
import com.me.devicemanagement.framework.server.httpclient.DMHttpRequest;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import java.util.HashMap;
import org.json.JSONException;
import org.json.JSONObject;

public class BusinessStoreSetupAPI
{
    public static final String CODE = "Code";
    private static final String OBJECT_ID = "objectId";
    private static final String AUTH_URL = "https://login.microsoftonline.com/common/oauth2/token";
    private static final String TENANT_DETAILS_URL = "https://graph.windows.net/myorganization/tenantDetails?api-version=1.6";
    public static final String CLIENT_ID = "ClientID";
    public static final String ACCESS_TOKEN = "access_token";
    public static final String CLIENT_SECRET = "ClientSecret";
    public static final String TENANT_ID = "TenantID";
    public static final String DISPLAY_NAME = "displayName";
    public static final String GRAPH_RESOURCE = "https://graph.windows.net";
    private static final String VERIFIED_DOMIANS = "verifiedDomains";
    private static final String NAME = "name";
    public static final String DOMAIN_NAME = "DomainName";
    
    public JSONObject getBasicTenantInfo(final JSONObject params) throws JSONException {
        final JSONObject token = this.getAccessToken(params);
        final JSONObject tenantDetails = this.getTenantDetails(token);
        return tenantDetails;
    }
    
    private JSONObject getAccessToken(final JSONObject params) throws JSONException {
        final String code = params.optString("Code");
        final String clientID = params.optString("ClientID");
        final String clientSecret = params.optString("ClientSecret");
        final HashMap<String, String> bodyData = new HashMap<String, String>();
        bodyData.put("client_id", clientID);
        bodyData.put("code", code);
        bodyData.put("client_secret", clientSecret);
        bodyData.put("resource", "https://graph.windows.net");
        bodyData.put("grant_type", "authorization_code");
        final String encodedBodyData = SyMUtil.encodeURLbodyParams((HashMap)bodyData);
        final JSONObject headers = new JSONObject().put("Content-Length", encodedBodyData.getBytes().length);
        final DMHttpRequest dmHttpRequest = new DMHttpRequest();
        dmHttpRequest.url = "https://login.microsoftonline.com/common/oauth2/token";
        dmHttpRequest.method = "POST";
        dmHttpRequest.headers = headers;
        dmHttpRequest.data = encodedBodyData.getBytes();
        final JSONObject resultJSObject = this.executeDMHttpRequest(dmHttpRequest);
        try {
            ((AzureOauthImpl)IdpsFactoryProvider.getOauthImpl(201)).handleErr((Long)null, resultJSObject);
        }
        catch (final OauthException e) {
            final String eMsg = e.getMessage();
            WpAppSettingsHandler.logger.log(Level.SEVERE, "exception in getting access token", (Throwable)e);
            if (!SyMUtil.isStringEmpty(eMsg) && eMsg.contains("invalid_client")) {
                throw new APIHTTPException("AD021", new Object[0]);
            }
        }
        return resultJSObject;
    }
    
    private JSONObject getTenantDetails(final JSONObject accessTokenParams) throws JSONException {
        final String accessToken = String.valueOf(accessTokenParams.get("access_token"));
        final JSONObject header = new JSONObject().put("Authorization", (Object)("Bearer " + accessToken));
        final DMHttpRequest dmHttpRequest = new DMHttpRequest();
        dmHttpRequest.url = "https://graph.windows.net/myorganization/tenantDetails?api-version=1.6";
        dmHttpRequest.method = "GET";
        dmHttpRequest.headers = header;
        final JSONObject resultJSObject = this.executeDMHttpRequest(dmHttpRequest);
        final JSONObject tenantDetails = new JSONObject();
        final JSONObject allTenantDetails = resultJSObject.getJSONArray("value").getJSONObject(0);
        tenantDetails.put("TenantID", (Object)String.valueOf(allTenantDetails.get("objectId")));
        tenantDetails.put("displayName", (Object)String.valueOf(allTenantDetails.get("displayName")));
        final JSONArray domains = allTenantDetails.getJSONArray("verifiedDomains");
        for (int i = 0; i < domains.length(); ++i) {
            final JSONObject domain = domains.getJSONObject(i);
            if (String.valueOf(domain.get("default")).contains("true")) {
                tenantDetails.put("DomainName", (Object)String.valueOf(domain.get("name")));
            }
        }
        return tenantDetails;
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
}
