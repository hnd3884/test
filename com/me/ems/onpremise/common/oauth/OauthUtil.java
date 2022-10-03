package com.me.ems.onpremise.common.oauth;

import com.me.ems.framework.common.api.utils.APIException;
import java.util.Map;
import com.me.devicemanagement.framework.server.httpclient.DMHttpResponse;
import java.util.Iterator;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import com.me.devicemanagement.framework.server.httpclient.DMHttpRequest;
import org.json.JSONException;
import java.util.logging.Level;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import org.json.JSONObject;
import java.util.logging.Logger;

public class OauthUtil
{
    private static Logger logger;
    private static OauthUtil oauthUtil;
    private static String REDIRECT_URL;
    private static String REFRESH_TOKEN;
    private static String CLIENT_ID;
    private static String CLIENT_SECRET;
    private static String REDIRECT_URI;
    private static String GRANT_TYPE;
    private static String ACCESS_TYPE;
    private static String SCOPE;
    private static String CODE;
    
    public static OauthUtil getInstance() {
        if (OauthUtil.oauthUtil == null) {
            OauthUtil.oauthUtil = new OauthUtil();
        }
        return OauthUtil.oauthUtil;
    }
    
    public JSONObject generateAccessTokenFromRefreshToken(final String clientId, final String clientSecret, final String refreshToken, final String scope, final String tokenUrl, final boolean useProxy) throws OauthException {
        JSONObject tokens = null;
        try {
            if (SyMUtil.isStringValid(clientId) && SyMUtil.isStringValid(clientSecret) && SyMUtil.isStringValid(refreshToken)) {
                final DMHttpRequest req = this.generateRefreshTokenRequest(clientId, clientSecret, refreshToken, scope, tokenUrl, useProxy);
                final JSONObject resp = this.executeDMHttpRequest(req);
                this.handleErr(resp);
                tokens = new JSONObject();
                tokens.put("ACCESS_TOKEN", (Object)String.valueOf(resp.get("access_token")));
                tokens.put("EXPIRES_AT", resp.getLong("expires_in") * 1000L + System.currentTimeMillis());
                final String[] scopeSeperated = scope.split(",");
                tokens.put("SCOPE", (Object)scopeSeperated);
            }
        }
        catch (final JSONException var10) {
            OauthUtil.logger.log(Level.SEVERE, "Exception in JSON generatingAccessToken", (Throwable)var10);
            throw new OauthException("INVALID_REQUEST");
        }
        return tokens;
    }
    
    private DMHttpRequest generateRefreshTokenRequest(final String clientId, final String clientSecret, final String refreshToken, final String scope, final String tokenUrl, final boolean useProxy) {
        final DMHttpRequest request = new DMHttpRequest();
        final JSONObject body = new JSONObject();
        body.put(OauthUtil.REFRESH_TOKEN, (Object)refreshToken);
        body.put(OauthUtil.CLIENT_ID, (Object)clientId);
        body.put(OauthUtil.CLIENT_SECRET, (Object)clientSecret);
        body.put(OauthUtil.REDIRECT_URI, (Object)OauthUtil.REDIRECT_URL);
        body.put(OauthUtil.GRANT_TYPE, (Object)"refresh_token");
        body.put(OauthUtil.ACCESS_TYPE, (Object)"offline");
        body.put(OauthUtil.SCOPE, (Object)scope);
        final String p = this.generateAuthUrl(body);
        request.url = tokenUrl + "?" + p;
        request.data = p.getBytes();
        request.method = "POST";
        request.useProxy = useProxy;
        final JSONObject header = new JSONObject();
        header.put("Content-Type", (Object)"application/x-www-form-urlencoded");
        request.headers = header;
        return request;
    }
    
    public JSONObject generateTokens(final String clientId, final String clientSecret, final String code, final String scope, final String tokenUrl, final boolean useProxy) throws OauthException {
        JSONObject tokens = null;
        try {
            if (SyMUtil.isStringValid(clientId) && SyMUtil.isStringValid(clientSecret)) {
                if (!SyMUtil.isStringValid(code)) {
                    throw new OauthException("INVALID_REQUEST");
                }
                final DMHttpRequest req = this.generateAccessTokenRequest(clientId, clientSecret, code, tokenUrl, useProxy);
                final JSONObject resp = this.executeDMHttpRequest(req);
                this.handleErr(resp);
                tokens = new JSONObject();
                tokens.put("ACCESS_TOKEN", (Object)String.valueOf(resp.get("access_token")));
                tokens.put("REFRESH_TOKEN", (Object)String.valueOf(resp.get("refresh_token")));
                tokens.put("EXPIRES_AT", resp.getLong("expires_in") * 1000L + System.currentTimeMillis());
                final String[] scopeSeperated = scope.split(",");
                tokens.put("SCOPE", (Object)scopeSeperated);
            }
        }
        catch (final JSONException e) {
            OauthUtil.logger.log(Level.SEVERE, "Json exception while generateTokens in OauthUtil", (Throwable)e);
            throw new OauthException("INVALID_REQUEST");
        }
        return tokens;
    }
    
    public void handleErr(final JSONObject resp) throws OauthException, JSONException {
        if (resp.has("error")) {
            OauthUtil.logger.log(Level.WARNING, "Token response has error");
            final String err = String.valueOf(resp.get("error"));
            if (err.equalsIgnoreCase("invalid_grant")) {
                throw new OauthException("INVALID_GRANT");
            }
            if (err.equalsIgnoreCase("invalid_client")) {
                throw new OauthException("INVALID_CLIENT");
            }
            if (err.equalsIgnoreCase("invalid_request")) {
                throw new OauthException("INVALID_REQUEST");
            }
            if (!err.equalsIgnoreCase("redirect_uri_mismatch")) {
                throw new OauthException("ACCESS_DENIED");
            }
            OauthUtil.logger.log(Level.SEVERE, "Exception in OAuth: Redirect URL mismatch");
        }
        else if (resp == null || resp.length() <= 0) {
            throw new OauthException("UNAVAILABLE");
        }
    }
    
    private static String encode(final String url) {
        String encodeURL = null;
        try {
            encodeURL = URLEncoder.encode(url, "UTF-8");
        }
        catch (final UnsupportedEncodingException e) {
            OauthUtil.logger.log(Level.INFO, "Exception occurred : " + e);
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
    
    private DMHttpRequest generateAccessTokenRequest(final String clientId, final String clientSecret, final String code, final String tokenUrl, final boolean useProxy) throws JSONException {
        final DMHttpRequest request = new DMHttpRequest();
        final JSONObject body = new JSONObject();
        body.put(OauthUtil.CODE, (Object)code);
        body.put(OauthUtil.CLIENT_ID, (Object)clientId);
        body.put(OauthUtil.CLIENT_SECRET, (Object)clientSecret);
        body.put(OauthUtil.REDIRECT_URI, (Object)OauthUtil.REDIRECT_URL);
        body.put(OauthUtil.GRANT_TYPE, (Object)"authorization_code");
        body.put(OauthUtil.ACCESS_TYPE, (Object)"offline");
        final String p = this.generateAuthUrl(body);
        request.url = tokenUrl + "?" + p;
        request.data = p.getBytes();
        request.method = "POST";
        request.useProxy = useProxy;
        final JSONObject header = new JSONObject();
        header.put("Content-Type", (Object)"application/x-www-form-urlencoded");
        request.headers = header;
        return request;
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
    
    public Map<String, Object> attachAccessToken(final Map<String, Object> smtpSettingsMap, final boolean compareValuesInDb) throws OauthException {
        JSONObject oauthCredential = null;
        if (compareValuesInDb) {
            oauthCredential = OauthDataHandler.getInstance().getOauthCredential(null);
        }
        final String newClientId = smtpSettingsMap.get("clientId");
        final String newClientSecret = smtpSettingsMap.get("clientSecret");
        final String code = smtpSettingsMap.get("code");
        String scope = smtpSettingsMap.get("scope");
        String tokenUrl = smtpSettingsMap.get("tokenUrl");
        final boolean useProxy = smtpSettingsMap.get("proxyEnabled");
        if (oauthCredential != null) {
            final String existingClientId = (String)oauthCredential.get("CLIENT_ID");
            final String existingClientSecret = (String)oauthCredential.get("CLIENT_SECRET");
            final long expiresAt = oauthCredential.getLong("EXPIRES_AT");
            if (existingClientId.equalsIgnoreCase(newClientId) && existingClientSecret.equalsIgnoreCase(newClientSecret)) {
                if (expiresAt > System.currentTimeMillis()) {
                    smtpSettingsMap.put("smtpPassword", oauthCredential.getString("ACCESS_TOKEN"));
                }
                else {
                    final String refreshToken = oauthCredential.getString("REFRESH_TOKEN");
                    scope = oauthCredential.getString("SCOPE");
                    tokenUrl = oauthCredential.getString("TOKEN_URL");
                    final JSONObject tokens = this.generateAccessTokenFromRefreshToken(existingClientId, existingClientSecret, refreshToken, scope, tokenUrl, useProxy);
                    smtpSettingsMap.put("smtpPassword", tokens.getString("ACCESS_TOKEN"));
                }
            }
            else {
                final JSONObject tokens2 = this.generateTokens(newClientId, newClientSecret, code, scope, tokenUrl, useProxy);
                smtpSettingsMap.put("smtpPassword", tokens2.getString("ACCESS_TOKEN"));
            }
        }
        else {
            final JSONObject tokens3 = this.generateTokens(newClientId, newClientSecret, code, scope, tokenUrl, useProxy);
            smtpSettingsMap.put("smtpPassword", tokens3.getString("ACCESS_TOKEN"));
            smtpSettingsMap.put("REFRESH_TOKEN", tokens3.getString("REFRESH_TOKEN"));
            smtpSettingsMap.put("EXPIRES_AT", tokens3.get("EXPIRES_AT"));
        }
        return smtpSettingsMap;
    }
    
    public String getAccessTokenFromDb(final Long credentialId, final boolean useProxy) throws APIException, OauthException {
        if (credentialId == null) {
            throw new APIException("GENERIC0005");
        }
        final JSONObject oauthCredential = OauthDataHandler.getInstance().getOauthCredential(credentialId);
        long expiresAt = oauthCredential.getLong("EXPIRES_AT");
        String accessToken = "";
        if (expiresAt > System.currentTimeMillis()) {
            accessToken = oauthCredential.getString("ACCESS_TOKEN");
        }
        else {
            final String clientId = oauthCredential.getString("CLIENT_ID");
            final String clientSecret = oauthCredential.getString("CLIENT_SECRET");
            final String refreshToken = oauthCredential.getString("REFRESH_TOKEN");
            final String scope = oauthCredential.getString("SCOPE");
            final String tokenUrl = oauthCredential.getString("TOKEN_URL");
            try {
                final JSONObject tokens = this.generateAccessTokenFromRefreshToken(clientId, clientSecret, refreshToken, scope, tokenUrl, useProxy);
                accessToken = tokens.getString("ACCESS_TOKEN");
                expiresAt = tokens.getLong("EXPIRES_AT");
                OauthDataHandler.getInstance().updateAccessTokenAndExpiry(credentialId, accessToken, expiresAt);
            }
            catch (final OauthException e) {
                OauthUtil.logger.log(Level.SEVERE, "Exception in JSON getAccessTokenFromDb", e);
                throw new OauthException("INVALID_REQUEST");
            }
        }
        return accessToken;
    }
    
    static {
        OauthUtil.logger = Logger.getLogger(OauthUtil.class.getName());
        OauthUtil.oauthUtil = null;
        OauthUtil.REDIRECT_URL = "https://www.manageengine.com/ems/OAuthAuthorization.html";
        OauthUtil.REFRESH_TOKEN = "refresh_token";
        OauthUtil.CLIENT_ID = "client_id";
        OauthUtil.CLIENT_SECRET = "client_secret";
        OauthUtil.REDIRECT_URI = "redirect_uri";
        OauthUtil.GRANT_TYPE = "grant_type";
        OauthUtil.ACCESS_TYPE = "access_type";
        OauthUtil.SCOPE = "scope";
        OauthUtil.CODE = "code";
    }
}
