package com.me.idps.mdm.oauth;

import java.util.Hashtable;
import com.me.devicemanagement.framework.server.httpclient.DMHttpResponse;
import java.net.URLDecoder;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Iterator;
import com.adventnet.ds.query.SelectQuery;
import org.json.JSONArray;
import com.me.idps.core.oauth.OauthException;
import org.json.JSONObject;
import com.me.devicemanagement.framework.server.httpclient.DMHttpRequest;
import org.json.JSONException;
import java.util.logging.Level;
import com.me.idps.core.IDPSlogger;
import java.util.ArrayList;
import java.util.Properties;
import com.me.mdm.directory.service.mam.AzureMamOauthHandler;
import com.me.idps.core.factory.IdpsFactoryProvider;
import com.me.idps.core.service.azure.AzureOauthImpl;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import java.util.List;
import com.me.idps.core.oauth.OauthServiceAPI;

public class AzureMamOauthImpl implements OauthServiceAPI
{
    private static final String AZURE_TOKEN_URL = "https://login.microsoftonline.com/common/oauth2/v2.0/token";
    public static final String AZURE_MAM_SCOPE = "DeviceManagementApps.Read.All Directory.Read.All offline_access";
    private static final String REDIRECT_URI = "https://www.manageengine.com/mobile-device-management/azureADAuthorization.html";
    
    @Override
    public List generateAccessTokenFromRefreshToken(final Long customerID, final String clientId, final String clientSecret, final String refreshToken, final String scope) throws OauthException {
        List tokensList = null;
        try {
            if (SyMUtil.isStringValid(clientId) && SyMUtil.isStringValid(clientSecret) && SyMUtil.isStringValid(refreshToken)) {
                final DMHttpRequest req = this.generateRefreshTokenRequest(clientId, clientSecret, refreshToken, scope);
                final JSONObject resp = this.executeDMHttpRequest(req);
                ((AzureOauthImpl)IdpsFactoryProvider.getOauthImpl(201)).handleErr(customerID, resp);
                AzureMamOauthHandler.getInstance().checkForIntuneMamSubscription((Long)null, (Long)null, (Long)null, resp.optString("access_token"));
                final Properties accessTokenProperties = new Properties();
                ((Hashtable<String, Integer>)accessTokenProperties).put("TOKEN_TYPE", 2);
                ((Hashtable<String, String>)accessTokenProperties).put("TOKEN_VALUE", resp.optString("access_token"));
                ((Hashtable<String, Long>)accessTokenProperties).put("EXPIRES_AT", resp.optLong("expires_in", 0L) * 1000L + System.currentTimeMillis());
                final Properties refrshTokenProperties = new Properties();
                ((Hashtable<String, Integer>)refrshTokenProperties).put("TOKEN_TYPE", 1);
                ((Hashtable<String, String>)refrshTokenProperties).put("TOKEN_VALUE", String.valueOf(resp.get("refresh_token")));
                ((Hashtable<String, Long>)refrshTokenProperties).put("EXPIRES_AT", Long.MAX_VALUE);
                tokensList = new ArrayList();
                tokensList.add(accessTokenProperties);
                tokensList.add(refrshTokenProperties);
            }
        }
        catch (final JSONException e) {
            IDPSlogger.OAUTH.log(Level.SEVERE, "Exception in JSON generatingAccessToken", (Throwable)e);
        }
        return tokensList;
    }
    
    @Override
    public JSONObject generateTokens(final Long customerID, final String clientId, final String clientSecret, final String code, final String scope) throws OauthException {
        JSONObject tokens = null;
        try {
            if (SyMUtil.isStringValid(clientId) && SyMUtil.isStringValid(clientSecret)) {
                if (!SyMUtil.isStringValid(code)) {
                    throw new OauthException("invalid_request");
                }
                final DMHttpRequest req = this.generateAccessTokenRequest(clientId, clientSecret, code, scope);
                final JSONObject resp = this.executeDMHttpRequest(req);
                ((AzureOauthImpl)IdpsFactoryProvider.getOauthImpl(201)).handleErr(customerID, resp);
                final JSONObject access_token = new JSONObject();
                access_token.put("TOKEN_TYPE", 2);
                access_token.put("TOKEN_VALUE", (Object)String.valueOf(resp.get("access_token")));
                access_token.put("EXPIRES_AT", resp.getLong("expires_in") * 1000L + System.currentTimeMillis());
                final JSONObject refresh_token = new JSONObject();
                refresh_token.put("TOKEN_TYPE", 1);
                refresh_token.put("TOKEN_VALUE", (Object)String.valueOf(resp.get("refresh_token")));
                refresh_token.put("EXPIRES_AT", Long.MAX_VALUE);
                final String scopes = decode(resp.optString("scope"));
                final String[] scopeSeperated = scopes.split(" ");
                tokens = new JSONObject();
                final JSONArray tokensArray = new JSONArray();
                tokensArray.put((Object)access_token);
                tokensArray.put((Object)refresh_token);
                tokens.put("scope", (Object)scopeSeperated);
                tokens.put("tokens", (Object)tokensArray);
            }
        }
        catch (final JSONException e) {
            IDPSlogger.OAUTH.log(Level.SEVERE, "Json exception in generateTokens for azure mam oauth impl", (Throwable)e);
        }
        return tokens;
    }
    
    @Override
    public Properties fetchMetadata(final Long customerID, final Long userID) {
        return IdpsFactoryProvider.getOauthImpl(201).fetchMetadata(customerID, userID);
    }
    
    @Override
    public boolean revokeTokens(final String clientId, final String clientSecret, final String refreshToken) {
        return false;
    }
    
    @Override
    public String getAuthorizeUrl(final Long customerID, final Long userID, final String[] scopes, final String state) {
        return this.getAzureAuthorizeUrl(customerID, userID, state);
    }
    
    @Override
    public SelectQuery configureSelectQuery(final SelectQuery selectQuery, final Long customerID, final Long userID) throws Exception {
        return IdpsFactoryProvider.getOauthImpl(201).configureSelectQuery(selectQuery, customerID, userID);
    }
    
    @Override
    public int getDomainType() {
        return 3;
    }
    
    private String getAzureAuthorizeUrl(final Long customerID, final Long userID, final String state) {
        String url = null;
        final JSONObject azureParam = new JSONObject();
        try {
            final Properties azureOAuthMeta = this.fetchMetadata(customerID, userID);
            azureParam.put("url", (Object)"https://login.microsoftonline.com/common/oauth2/v2.0/authorize");
            azureParam.put("client_id", (Object)azureOAuthMeta.getProperty("OAUTH_CLIENT_ID"));
            azureParam.put("response_type", (Object)"code");
            azureParam.put("redirect_uri", (Object)"https://www.manageengine.com/mobile-device-management/azureADAuthorization.html");
            azureParam.put("state", (Object)state);
            azureParam.put("scope", (Object)"DeviceManagementApps.Read.All Directory.Read.All offline_access");
            azureParam.put("prompt", (Object)"select_account");
            url = this.generateAuthUrl(azureParam);
        }
        catch (final JSONException e) {
            IDPSlogger.SOM.log(Level.SEVERE, "getting the Authorize URL", (Throwable)e);
        }
        return url;
    }
    
    private DMHttpRequest generateAccessTokenRequest(final String clientId, final String clientSecret, final String code, final String scope) throws JSONException {
        final DMHttpRequest request = new DMHttpRequest();
        request.url = "https://login.microsoftonline.com/common/oauth2/v2.0/token";
        final JSONObject body = new JSONObject();
        body.put("client_id", (Object)clientId);
        body.put("grant_type", (Object)"authorization_code");
        body.put("code", (Object)code);
        body.put("redirect_uri", (Object)"https://www.manageengine.com/mobile-device-management/azureADAuthorization.html");
        body.put("client_secret", (Object)clientSecret);
        if (SyMUtil.isStringValid(scope)) {
            body.put("scope", (Object)scope);
        }
        else {
            body.put("scope", (Object)"DeviceManagementApps.Read.All Directory.Read.All offline_access");
        }
        final String p = this.generateAuthUrl(body);
        request.data = p.getBytes();
        request.method = "POST";
        final JSONObject header = new JSONObject();
        header.put("Content-Type", (Object)"application/x-www-form-urlencoded");
        request.headers = header;
        return request;
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
    
    private static String encode(final String url) {
        String encodeURL = null;
        try {
            encodeURL = URLEncoder.encode(url, "UTF-8");
        }
        catch (final UnsupportedEncodingException e) {
            IDPSlogger.OAUTH.log(Level.INFO, "Exception occurred : " + e);
        }
        return encodeURL;
    }
    
    private static String decode(final String url) {
        String decodeURL = null;
        try {
            decodeURL = URLDecoder.decode(url, "UTF-8");
        }
        catch (final UnsupportedEncodingException e) {
            IDPSlogger.OAUTH.log(Level.INFO, "Exception occurred : " + e);
        }
        return decodeURL;
    }
    
    private JSONObject executeDMHttpRequest(final DMHttpRequest dmHttpRequest) throws JSONException {
        IDPSlogger.OAUTH.log(Level.INFO, "hitting url : " + dmHttpRequest.url);
        final DMHttpResponse dmHttpResponse = SyMUtil.executeDMHttpRequest(dmHttpRequest);
        final String responseString = dmHttpResponse.responseBodyAsString;
        JSONObject resultJSObject = new JSONObject();
        if (responseString != null) {
            resultJSObject = new JSONObject(responseString);
        }
        return resultJSObject;
    }
    
    private DMHttpRequest generateRefreshTokenRequest(final String clientId, final String clientSecret, final String refreshToken, final String scope) throws JSONException {
        final DMHttpRequest request = new DMHttpRequest();
        request.url = "https://login.microsoftonline.com/common/oauth2/v2.0/token";
        final JSONObject body = new JSONObject();
        body.put("client_id", (Object)clientId);
        body.put("grant_type", (Object)"refresh_token");
        body.put("refresh_token", (Object)refreshToken);
        body.put("client_secret", (Object)clientSecret);
        if (SyMUtil.isStringValid(scope)) {
            body.put("scope", (Object)scope);
        }
        else {
            body.put("scope", (Object)"DeviceManagementApps.Read.All Directory.Read.All offline_access");
        }
        final String p = this.generateAuthUrl(body);
        request.data = p.getBytes();
        request.method = "POST";
        final JSONObject header = new JSONObject();
        header.put("Content-Type", (Object)"application/x-www-form-urlencoded");
        request.headers = header;
        return request;
    }
}
