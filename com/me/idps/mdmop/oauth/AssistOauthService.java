package com.me.idps.mdmop.oauth;

import java.util.Hashtable;
import com.me.mdm.api.error.APIHTTPException;
import com.adventnet.ds.query.SelectQuery;
import com.zoho.framework.utils.crypto.CryptoUtil;
import com.me.mdm.server.util.CloudAPIDataPost;
import com.me.devicemanagement.framework.server.httpclient.DMHttpResponse;
import java.util.Iterator;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import com.me.idps.core.api.IdpsAPIException;
import com.adventnet.persistence.DataAccessException;
import org.json.JSONArray;
import com.me.idps.core.oauth.OauthDataHandler;
import com.me.devicemanagement.framework.server.httpclient.DMHttpRequest;
import org.json.JSONException;
import java.util.logging.Level;
import com.me.idps.core.IDPSlogger;
import java.util.ArrayList;
import java.util.Properties;
import com.me.idps.core.oauth.OauthException;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import java.util.List;
import org.json.JSONObject;
import com.me.mdm.onpremise.remotesession.AssistAuthTokenHandler;
import com.me.devicemanagement.framework.server.customer.CustomerInfoThreadLocal;
import com.me.idps.core.oauth.OauthServiceAPI;

public class AssistOauthService implements OauthServiceAPI
{
    public static AssistOauthService assistOauthService;
    private static final String ACCOUNTS_BASE_URL = "https://accounts.zoho.com";
    private static final String ASSIST_TOKEN_URL = "https://accounts.zoho.com/oauth/v2/token";
    private static final String REDIRECT_URI = "https://www.manageengine.com/mobile-device-management/OAuth.html";
    
    public static AssistOauthService getInstance() {
        if (AssistOauthService.assistOauthService == null) {
            AssistOauthService.assistOauthService = new AssistOauthService();
        }
        return AssistOauthService.assistOauthService;
    }
    
    private String appendDomainToUrl(final String url) {
        final String custId = CustomerInfoThreadLocal.getCustomerId();
        final JSONObject assistDetails = new AssistAuthTokenHandler().getAssistAccountDetails(Long.valueOf(custId));
        final String domain = assistDetails.optString("CUSTOMER_COUNTRY_CODE", "com");
        String tokenUrl = url;
        if (!domain.equalsIgnoreCase("com")) {
            tokenUrl = url.replaceFirst("com", domain);
        }
        return tokenUrl;
    }
    
    @Override
    public List generateAccessTokenFromRefreshToken(final Long customerID, final String clientId, final String clientSecret, final String refreshToken, final String scope) throws OauthException {
        List tokensList = null;
        try {
            if (SyMUtil.isStringValid(clientId) && SyMUtil.isStringValid(clientSecret) && SyMUtil.isStringValid(refreshToken)) {
                final DMHttpRequest req = this.generateRefreshTokenRequest(clientId, clientSecret, refreshToken, scope);
                final JSONObject resp = this.executeDMHttpRequest(req);
                if (resp.has("error")) {
                    if (String.valueOf(resp.get("error")).equalsIgnoreCase("invalid_code")) {
                        throw new OauthException("invalid_grant");
                    }
                    throw new OauthException("access_denied");
                }
                else {
                    final Properties accessTokenProperties = new Properties();
                    ((Hashtable<String, Integer>)accessTokenProperties).put("TOKEN_TYPE", 2);
                    ((Hashtable<String, String>)accessTokenProperties).put("TOKEN_VALUE", resp.optString("access_token"));
                    ((Hashtable<String, Long>)accessTokenProperties).put("EXPIRES_AT", resp.optLong("expires_in", 0L) * 1000L + System.currentTimeMillis());
                    tokensList = new ArrayList();
                    tokensList.add(accessTokenProperties);
                }
            }
        }
        catch (final JSONException e) {
            IDPSlogger.OAUTH.log(Level.SEVERE, "Exception in JSON generatingAccessToken", (Throwable)e);
        }
        return tokensList;
    }
    
    private DMHttpRequest generateRefreshTokenRequest(final String clientId, final String clientSecret, final String refreshToken, final String scope) throws JSONException {
        final DMHttpRequest request = new DMHttpRequest();
        final JSONObject body = new JSONObject();
        final String tokenUrl = this.appendDomainToUrl("https://accounts.zoho.com/oauth/v2/token");
        body.put("url", (Object)tokenUrl);
        body.put("refresh_token", (Object)refreshToken);
        body.put("client_id", (Object)clientId);
        body.put("client_secret", (Object)clientSecret);
        body.put("redirect_uri", (Object)"https://www.manageengine.com/mobile-device-management/OAuth.html");
        body.put("grant_type", (Object)"refresh_token");
        body.put("access_type", (Object)"offline");
        body.put("scope", (Object)scope);
        request.url = this.generateAuthUrl(body);
        request.method = "POST";
        return request;
    }
    
    @Override
    public JSONObject generateTokens(final Long customerID, final String clientId, final String clientSecret, final String code, final String scope) throws OauthException {
        JSONObject tokens = null;
        try {
            if (SyMUtil.isStringValid(clientId) && SyMUtil.isStringValid(clientSecret)) {
                if (!SyMUtil.isStringValid(code)) {
                    throw new OauthException("invalid_request");
                }
                final DMHttpRequest req = this.generateAccessTokenRequest(clientId, clientSecret, code);
                final JSONObject resp = this.executeDMHttpRequest(req);
                if (resp.has("error")) {
                    if (String.valueOf(resp.get("error")).equalsIgnoreCase("invalid_code")) {
                        throw new OauthException("invalid_grant");
                    }
                    if (String.valueOf(resp.get("error")).equalsIgnoreCase("invalid_client") || String.valueOf(resp.get("error")).equalsIgnoreCase("unauthorized_client")) {
                        final Properties metadata = this.fetchMetadata(null, null);
                        OauthDataHandler.getInstance().addOrUpdateOauthMetadata(null, null, null, metadata.getProperty("OAUTH_CLIENT_ID"), metadata.getProperty("OAUTH_CLIENT_SECRET"), 202);
                        throw new OauthException("invalid_grant");
                    }
                    throw new OauthException("access_denied");
                }
                else {
                    final JSONObject access_token = new JSONObject();
                    access_token.put("TOKEN_TYPE", 2);
                    access_token.put("TOKEN_VALUE", (Object)String.valueOf(resp.get("access_token")));
                    access_token.put("EXPIRES_AT", resp.getLong("expires_in") * 1000L + System.currentTimeMillis());
                    final JSONObject refresh_token = new JSONObject();
                    refresh_token.put("TOKEN_TYPE", 1);
                    refresh_token.put("TOKEN_VALUE", (Object)String.valueOf(resp.get("refresh_token")));
                    refresh_token.put("EXPIRES_AT", Long.MAX_VALUE);
                    tokens = new JSONObject();
                    final JSONArray tokensArray = new JSONArray();
                    tokensArray.put((Object)access_token);
                    tokensArray.put((Object)refresh_token);
                    tokens.put("tokens", (Object)tokensArray);
                    final String[] scopeSeperated = scope.split(",");
                    tokens.put("scope", (Object)scopeSeperated);
                }
            }
        }
        catch (final JSONException | DataAccessException | IdpsAPIException e) {
            IDPSlogger.OAUTH.log(Level.SEVERE, "Json exception while generateTokens for assist oauth service", e);
        }
        return tokens;
    }
    
    private DMHttpRequest generateAccessTokenRequest(final String clientId, final String clientSecret, final String code) throws JSONException {
        final DMHttpRequest request = new DMHttpRequest();
        final JSONObject body = new JSONObject();
        final String tokenUrl = this.appendDomainToUrl("https://accounts.zoho.com/oauth/v2/token");
        body.put("url", (Object)tokenUrl);
        body.put("code", (Object)code);
        body.put("client_id", (Object)clientId);
        body.put("client_secret", (Object)clientSecret);
        body.put("redirect_uri", (Object)"https://www.manageengine.com/mobile-device-management/OAuth.html");
        body.put("grant_type", (Object)"authorization_code");
        body.put("access_type", (Object)"offline");
        request.url = this.generateAuthUrl(body);
        request.method = "POST";
        return request;
    }
    
    private DMHttpRequest revokeRequest(final String refreshtoken) throws JSONException {
        final DMHttpRequest request = new DMHttpRequest();
        final JSONObject body = new JSONObject();
        final String url = this.appendDomainToUrl("https://accounts.zoho.com/oauth/v2/token");
        body.put("url", (Object)url);
        body.put("token", (Object)refreshtoken);
        request.url = this.generateAuthUrl(body);
        request.method = "POST";
        return request;
    }
    
    private static String encode(final String url) {
        String encodeURL = null;
        try {
            encodeURL = URLEncoder.encode(url, "UTF-8");
        }
        catch (final UnsupportedEncodingException e) {
            IDPSlogger.OAUTH.log(Level.SEVERE, "Exception occurred : " + e);
        }
        return encodeURL;
    }
    
    private String generateAuthUrl(final JSONObject urlParms) throws JSONException {
        final StringBuilder url = new StringBuilder(urlParms.optString("url", ""));
        if (urlParms.has("url")) {
            url.append("?");
            urlParms.remove("url");
        }
        final Iterator<String> keys = urlParms.keys();
        while (keys.hasNext()) {
            final String key = keys.next();
            final String value = String.valueOf(urlParms.get(key));
            final String param = key + "=" + encode(value);
            url.append(param);
            if (keys.hasNext()) {
                url.append("&");
            }
        }
        return url.toString();
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
    
    @Override
    public Properties fetchMetadata(final Long customerID, final Long userID) {
        Properties properties = null;
        try {
            Properties existingMetadata = null;
            try {
                existingMetadata = OauthDataHandler.getInstance().getMetadataFromDomainType(null, 202, null);
                if (existingMetadata != null) {
                    return existingMetadata;
                }
            }
            catch (final Exception ex) {
                IDPSlogger.OAUTH.log(Level.WARNING, null, ex);
            }
            final JSONObject j = this.fetchZohoAppDetailsFromCreator();
            properties = new Properties();
            final String clientID = j.getString("OAUTH_CLIENT_ID");
            final String clientSecret = j.getString("OAUTH_CLIENT_SECRET");
            properties.setProperty("OAUTH_CLIENT_ID", clientID);
            properties.setProperty("OAUTH_CLIENT_SECRET", clientSecret);
            final Long metadataId = OauthDataHandler.getInstance().addOrUpdateOauthMetadata(null, null, null, clientID, clientSecret, 202);
            ((Hashtable<String, Long>)properties).put("OAUTH_METADATA_ID", metadataId);
        }
        catch (final Exception e) {
            IDPSlogger.OAUTH.log(Level.SEVERE, "Exception while fetching Metadata for assist_remote", e);
        }
        return properties;
    }
    
    private JSONObject fetchZohoAppDetailsFromCreator() {
        final JSONObject jsObject = new JSONObject();
        try {
            final String postUrl = "https://mdm.manageengine.com/api/v1/mdm/secretkeys";
            final CloudAPIDataPost postData = new CloudAPIDataPost();
            final JSONObject submitJSONObject = new JSONObject();
            submitJSONObject.put("keys", (Object)"RC_ASSIST_KEYS");
            postData.encryptAndPostDataToCloud(postUrl, submitJSONObject, "keys");
            if (postData.status.toString().startsWith("20")) {
                final String responseContent = postData.response;
                IDPSlogger.OAUTH.log(Level.INFO, "got the data successfully.");
                if (SyMUtil.isValidJSON(responseContent)) {
                    final JSONObject viewJSONobject = new JSONObject(responseContent);
                    final String client_id = String.valueOf(viewJSONobject.get("RC_CLIENT_ID"));
                    final String client_secret = String.valueOf(viewJSONobject.get("RC_CLIENT_SECRET"));
                    jsObject.put("OAUTH_CLIENT_ID", (Object)client_id);
                    jsObject.put("OAUTH_CLIENT_SECRET", (Object)CryptoUtil.decrypt(client_secret, "1592532495243"));
                }
                else {
                    IDPSlogger.OAUTH.log(Level.INFO, "Failed due to Some Error in Creator");
                }
            }
            else {
                IDPSlogger.OAUTH.log(Level.INFO, "Failed due to Some Error in Creator");
            }
        }
        catch (final Exception e) {
            IDPSlogger.OAUTH.log(Level.INFO, "Exception occurred : " + e);
        }
        return jsObject;
    }
    
    @Override
    public boolean revokeTokens(final String clientId, final String clientSecret, final String refreshToken) {
        boolean isrevoked = false;
        final DMHttpRequest req = this.revokeRequest(refreshToken);
        final JSONObject resp = this.executeDMHttpRequest(req);
        if (resp.has("status") && resp.getString("status").equalsIgnoreCase("success")) {
            isrevoked = true;
        }
        return isrevoked;
    }
    
    @Override
    public String getAuthorizeUrl(final Long customerID, final Long userID, final String[] scopes, final String state) {
        String url = null;
        final JSONObject assistParam = new JSONObject();
        try {
            assistParam.put("url", (Object)"https://accounts.zoho.com/oauth/v2/auth");
            final Properties p = this.fetchMetadata(customerID, userID);
            assistParam.put("client_id", (Object)p.getProperty("OAUTH_CLIENT_ID"));
            assistParam.put("response_type", (Object)"code");
            assistParam.put("redirect_uri", (Object)"https://www.manageengine.com/mobile-device-management/OAuth.html");
            assistParam.put("state", (Object)state);
            assistParam.put("prompt", (Object)"consent");
            final StringBuilder sb = new StringBuilder();
            for (int i = 0; i < scopes.length; ++i) {
                sb.append(scopes[i]);
                if (i != scopes.length - 1) {
                    sb.append(",");
                }
            }
            assistParam.put("scope", (Object)sb.toString());
            assistParam.put("access_type", (Object)"offline");
            url = this.generateAuthUrl(assistParam);
        }
        catch (final JSONException e) {
            IDPSlogger.OAUTH.log(Level.SEVERE, "getting the for Assist Remote Authorize URL", (Throwable)e);
        }
        return url;
    }
    
    @Override
    public SelectQuery configureSelectQuery(final SelectQuery selectQuery, final Long customerID, final Long userID) {
        return selectQuery;
    }
    
    @Override
    public int getDomainType() {
        return 4;
    }
    
    public String getAccountsUserName(final String accessToken) throws Exception {
        String username = "--";
        if (!SyMUtil.isStringValid(accessToken)) {
            throw new APIHTTPException("COM0004", new Object[0]);
        }
        final String accUrl = this.appendDomainToUrl("https://accounts.zoho.com");
        final String userNameUrl = accUrl + "/oauth/user/info";
        final DMHttpRequest request = new DMHttpRequest();
        final JSONObject header = new JSONObject();
        final String authorization = "Zoho-oauthtoken " + accessToken;
        header.put("Authorization", (Object)authorization);
        request.headers = header;
        request.method = "GET";
        request.url = userNameUrl;
        final JSONObject resp = this.executeDMHttpRequest(request);
        if (!resp.has("error") && resp.has("Email")) {
            username = resp.getString("Email");
        }
        return username;
    }
    
    static {
        AssistOauthService.assistOauthService = null;
    }
}
