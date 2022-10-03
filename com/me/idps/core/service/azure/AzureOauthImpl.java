package com.me.idps.core.service.azure;

import java.util.Hashtable;
import org.json.simple.parser.JSONParser;
import java.util.Base64;
import com.adventnet.ds.query.SelectQuery;
import com.me.idps.core.crud.DMDomainDataHandler;
import com.me.idps.core.crud.DomainDataProvider;
import java.util.Collection;
import java.util.Iterator;
import java.net.URLDecoder;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import com.me.devicemanagement.framework.server.httpclient.DMHttpResponse;
import java.util.Arrays;
import org.json.JSONArray;
import com.me.devicemanagement.framework.server.httpclient.DMHttpRequest;
import java.util.ArrayList;
import java.util.Properties;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import java.util.List;
import org.json.JSONException;
import com.me.idps.core.oauth.OauthException;
import com.me.idps.core.util.IdpsUtil;
import java.util.logging.Level;
import com.me.idps.core.IDPSlogger;
import org.json.JSONObject;
import com.me.idps.core.oauth.OauthServiceAPI;

public abstract class AzureOauthImpl implements OauthServiceAPI
{
    private static final String API_VERSION = "1.6";
    private static final String AZURE_DOMAINS_URL = "https://graph.windows.net/{0}/domains";
    public static final String SCOPES = "https://graph.windows.net/Directory.Read.All offline_access";
    public static final String USER_READ_SCOPE = "https://graph.windows.net/User.Read offline_access";
    private static final String AZURE_TOKEN_URL = "https://login.microsoftonline.com/common/oauth2/v2.0/token";
    private static final String REDIRECT_URI = "https://www.manageengine.com/mobile-device-management/azureADAuthorization.html";
    
    public void handleErr(final Long customerID, final JSONObject resp) throws OauthException, JSONException {
        if (resp.has("error")) {
            IDPSlogger.OAUTH.log(Level.WARNING, "response has error : {0}", new Object[] { IdpsUtil.getPrettyJSON(resp) });
            final String err = String.valueOf(resp.get("error"));
            if (err.equalsIgnoreCase("invalid_grant")) {
                throw new OauthException("invalid_grant");
            }
            if (err.equalsIgnoreCase("invalid_client")) {
                throw new OauthException("invalid_client");
            }
            throw new OauthException("access_denied");
        }
        else if (resp == null || resp.length() <= 0) {
            throw new OauthException("unavailable");
        }
    }
    
    @Override
    public List generateAccessTokenFromRefreshToken(final Long customerID, final String clientId, final String clientSecret, final String refreshToken, final String scope) throws OauthException {
        List tokensList = null;
        try {
            if (SyMUtil.isStringValid(clientId) && SyMUtil.isStringValid(clientSecret) && SyMUtil.isStringValid(refreshToken)) {
                final DMHttpRequest req = this.generateRefreshTokenRequest(clientId, clientSecret, refreshToken, scope);
                final JSONObject resp = this.executeDMHttpRequest(req);
                this.handleErr(customerID, resp);
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
                resp.remove("access_token");
                resp.remove("refresh_token");
                IDPSlogger.OAUTH.log(Level.INFO, "response is : {0}", new Object[] { IdpsUtil.getPrettyJSON(resp) });
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
                this.handleErr(customerID, resp);
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
                tokens.put("domain", (Object)this.getDomainDetails(customerID, String.valueOf(resp.get("access_token"))));
                tokens.put("scope", (Object)scopeSeperated);
                tokens.put("tokens", (Object)tokensArray);
                IDPSlogger.OAUTH.log(Level.INFO, "customerID:{0}, domain:{1} scope:{2},scopeSeperated:{3}", new Object[] { customerID, tokens.get("domain"), scopes, Arrays.toString(scopeSeperated) });
            }
        }
        catch (final JSONException e) {
            IDPSlogger.OAUTH.log(Level.SEVERE, "exception in generateTokens", (Throwable)e);
        }
        return tokens;
    }
    
    @Override
    public boolean revokeTokens(final String clientId, final String clientSecret, final String refreshToken) {
        return false;
    }
    
    @Override
    public String getAuthorizeUrl(final Long customerID, final Long userID, final String[] scopes, final String state) {
        return this.getAzureAuthorizeUrl(customerID, userID, "https://graph.windows.net/Directory.Read.All offline_access", state);
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
            body.put("scope", (Object)"https://graph.windows.net/Directory.Read.All offline_access");
        }
        final String p = this.generateAuthUrl(body);
        request.data = p.getBytes();
        request.method = "POST";
        final JSONObject header = new JSONObject();
        header.put("Content-Type", (Object)"application/x-www-form-urlencoded");
        request.headers = header;
        return request;
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
            body.put("scope", (Object)"https://graph.windows.net/Directory.Read.All offline_access");
        }
        final String p = this.generateAuthUrl(body);
        request.data = p.getBytes();
        request.method = "POST";
        final JSONObject header = new JSONObject();
        header.put("Content-Type", (Object)"application/x-www-form-urlencoded");
        request.headers = header;
        return request;
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
    
    private String getDomainName(final Long customerID, final String tenant_id, final String at) throws Exception {
        final String domainsUrl = "https://graph.windows.net/{0}/domains".replace("{0}", tenant_id);
        final JSONObject params = new JSONObject();
        params.put("api-version", (Object)"1.6");
        final JSONObject header = new JSONObject();
        final String authorization = "Bearer " + at;
        header.put("Authorization", (Object)authorization);
        final DMHttpRequest req = new DMHttpRequest();
        req.url = domainsUrl;
        req.parameters = params;
        req.method = "GET";
        req.headers = header;
        final JSONObject resp = this.executeDMHttpRequest(req);
        if (!resp.has("error") && resp.has("value")) {
            final JSONArray value = resp.getJSONArray("value");
            final List<String> initialDomains = new ArrayList<String>();
            final List<String> rootAndAdminManagedDomains = new ArrayList<String>();
            final List<String> defaultAndAdminManagedDomains = new ArrayList<String>();
            final List<String> otherDomains = new ArrayList<String>();
            if (value != null && value.length() > 0) {
                for (int i = 0; i < value.length(); ++i) {
                    final JSONObject obj = value.getJSONObject(i);
                    final String domainNameObtainedFromAzure = obj.optString("name", "--");
                    if (obj.getBoolean("isDefault") && obj.getBoolean("isAdminManaged")) {
                        defaultAndAdminManagedDomains.add(domainNameObtainedFromAzure);
                    }
                    else if (obj.getBoolean("isRoot") && obj.getBoolean("isAdminManaged")) {
                        rootAndAdminManagedDomains.add(domainNameObtainedFromAzure);
                    }
                    else if (obj.getBoolean("isInitial")) {
                        initialDomains.add(domainNameObtainedFromAzure);
                    }
                    else {
                        otherDomains.add(domainNameObtainedFromAzure);
                    }
                }
            }
            final List<String> domainsOrderByPref = new ArrayList<String>();
            domainsOrderByPref.addAll(defaultAndAdminManagedDomains);
            domainsOrderByPref.addAll(rootAndAdminManagedDomains);
            domainsOrderByPref.addAll(initialDomains);
            domainsOrderByPref.addAll(otherDomains);
            final SelectQuery query = DomainDataProvider.getDMManagedDomainQuery(customerID, null, null, 3);
            final List<Properties> azureDomainProps = DMDomainDataHandler.getInstance().getDomains(query);
            final List<String> azuredomainNames = new ArrayList<String>();
            for (int j = 0; j < azureDomainProps.size(); ++j) {
                azuredomainNames.add(azureDomainProps.get(j).getProperty("NAME"));
            }
            for (int j = 0; j < domainsOrderByPref.size(); ++j) {
                final String curDomainName = domainsOrderByPref.get(j);
                if (!SyMUtil.isStringEmpty(curDomainName) && azuredomainNames.contains(curDomainName)) {
                    return curDomainName;
                }
            }
            final String curDomainName2 = domainsOrderByPref.get(0);
            if (!SyMUtil.isStringEmpty(curDomainName2)) {
                return curDomainName2;
            }
        }
        throw new OauthException("invalid_grant");
    }
    
    private JSONObject getDomainDetails(final Long customerID, final String at) throws OauthException {
        JSONObject domainObject = null;
        try {
            final Base64.Decoder decoder = Base64.getUrlDecoder();
            final String[] parts = at.split("\\.");
            final String payloadJson = new String(decoder.decode(parts[1]));
            final JSONParser parser = new JSONParser();
            final org.json.simple.JSONObject json = (org.json.simple.JSONObject)parser.parse(payloadJson);
            final String tenant_id = (String)json.get((Object)"tid");
            final String upn = (String)json.get((Object)"upn");
            final String domain_name = this.getDomainName(customerID, tenant_id, at);
            domainObject = this.getDMDomainProps(customerID, domain_name, upn);
            domainObject.put("tid", (Object)tenant_id);
            domainObject.put("oid", json.getOrDefault((Object)"oid", (Object)null));
        }
        catch (final OauthException e) {
            IDPSlogger.SOM.log(Level.SEVERE, "exception while adding to DMDOMAIN", e);
            throw e;
        }
        catch (final Exception e2) {
            IDPSlogger.SOM.log(Level.WARNING, "exception while adding to DMDOMAIN", e2);
        }
        return domainObject;
    }
    
    private JSONObject getDMDomainProps(final Long customerID, final String domain_name, final String userName) throws JSONException {
        JSONObject props = null;
        if (SyMUtil.isStringValid(domain_name)) {
            props = new JSONObject();
            props.put("NAME", (Object)domain_name);
            props.put("CUSTOMER_ID", (Object)customerID);
            if (SyMUtil.isStringValid(userName)) {
                props.put("CRD_USERNAME", (Object)userName);
            }
            else {
                props.put("CRD_USERNAME", (Object)"NA");
            }
        }
        return props;
    }
    
    public String getAzureAuthorizeUrl(final Long customerID, final Long userID, final String scopes, final String state) {
        String url = null;
        final JSONObject azureParam = new JSONObject();
        try {
            final Properties azureOAuthMeta = this.fetchMetadata(customerID, userID);
            azureParam.put("state", (Object)state);
            azureParam.put("scope", (Object)scopes);
            azureParam.put("response_type", (Object)"code");
            azureParam.put("prompt", (Object)"select_account");
            azureParam.put("url", (Object)"https://login.microsoftonline.com/common/oauth2/v2.0/authorize");
            azureParam.put("client_id", (Object)azureOAuthMeta.getProperty("OAUTH_CLIENT_ID"));
            azureParam.put("redirect_uri", (Object)"https://www.manageengine.com/mobile-device-management/azureADAuthorization.html");
            url = this.generateAuthUrl(azureParam);
        }
        catch (final JSONException e) {
            IDPSlogger.ERR.log(Level.SEVERE, "getting the Authorize URL", (Throwable)e);
        }
        return url;
    }
    
    @Override
    public int getDomainType() {
        return 3;
    }
    
    public void handleAzureOAuth() {
    }
}
