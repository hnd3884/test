package com.me.mdm.directory.service.mam;

import com.me.devicemanagement.framework.server.httpclient.DMHttpResponse;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import com.me.devicemanagement.framework.server.customer.CustomerInfoUtil;
import com.me.idps.core.api.DirectoryAPIFacade;
import java.util.List;
import org.json.JSONArray;
import java.util.Collection;
import java.util.ArrayList;
import com.me.devicemanagement.framework.server.httpclient.DMHttpRequest;
import com.me.mdm.api.error.APIHTTPException;
import org.json.JSONException;
import java.util.logging.Level;
import com.me.idps.core.IDPSlogger;
import org.json.simple.parser.JSONParser;
import java.util.Base64;
import com.me.idps.core.oauth.OauthUtil;
import com.me.idps.core.oauth.OauthException;
import org.json.JSONObject;

public class AzureMamOauthHandler
{
    private static AzureMamOauthHandler azureMamOauthHandler;
    private static final String AZURE_DOMAINS_URL = "https://graph.microsoft.com/v1.0/domains";
    private static final String INTUNE_MAM_URL = "https://graph.microsoft.com/v1.0/deviceAppManagement/managedAppPolicies";
    
    public static AzureMamOauthHandler getInstance() {
        if (AzureMamOauthHandler.azureMamOauthHandler == null) {
            AzureMamOauthHandler.azureMamOauthHandler = new AzureMamOauthHandler();
        }
        return AzureMamOauthHandler.azureMamOauthHandler;
    }
    
    public Long getAccessToken(final Long customerID, final Long userId, final JSONObject j) throws APIHTTPException {
        try {
            if (j.has("error_msg")) {
                throw new OauthException("invalid_request");
            }
            final JSONObject azureMamDetails = AzureMamDataHandler.getInstance().getAzureMamDetails(customerID);
            long oauthId = -1L;
            if (azureMamDetails != null) {
                oauthId = azureMamDetails.optLong("AUTH_TOKEN_ID", -1L);
            }
            final JSONObject oauth = new JSONObject();
            oauth.put("OAUTH_TOKEN_ID", oauthId);
            oauth.put("OAUTH_TYPE", 203);
            oauth.put("domain", j.optInt("domain_type", -1));
            if (j.has("code")) {
                oauth.put("code", j.get("code"));
                oauth.put("scope", (Object)"DeviceManagementApps.Read.All Directory.Read.All offline_access");
            }
            oauthId = OauthUtil.getInstance().register(customerID, oauth);
            final String access_token = OauthUtil.getInstance().fetchAccessTokenFromOauthId(Long.valueOf(oauthId));
            this.checkForIntuneMamSubscription(userId, customerID, oauthId, access_token);
            final String domianNmae = this.getDomainName(access_token);
            final Base64.Decoder decoder = Base64.getUrlDecoder();
            final String[] parts = access_token.split("\\.");
            final String payloadJson = new String(decoder.decode(parts[1]));
            final JSONParser parser = new JSONParser();
            final org.json.simple.JSONObject json = (org.json.simple.JSONObject)parser.parse(payloadJson);
            final String upn = (String)json.get((Object)"upn");
            final JSONObject azureMam = new JSONObject();
            azureMam.put("CUSTOMER_ID", (Object)customerID);
            azureMam.put("AUTH_TOKEN_ID", oauthId);
            azureMam.put("ADDED_BY", (Object)userId);
            azureMam.put("DOMAIN_NAME", (Object)domianNmae);
            azureMam.put("AZURE_UPN", (Object)upn);
            AzureMamDataHandler.getInstance().addOrUpdateAzureMamProps(azureMam);
            oauth.put("OAUTH_TOKEN_ID", oauthId);
            oauth.put("STATUS", 1);
            OauthUtil.getInstance().registerOauth(oauth);
            AzureMamAuditHandler.getInstance().logEvent(2088, null, "mdm.mam.integrate", "", customerID);
            return oauthId;
        }
        catch (final JSONException e) {
            IDPSlogger.ERR.log(Level.SEVERE, "getting the Authorize URL", (Throwable)e);
        }
        catch (final OauthException e2) {
            IDPSlogger.ERR.log(Level.SEVERE, "getting OAuthException the Authorize URL", (Throwable)e2);
            if (e2.getMessage().equalsIgnoreCase("unavailable")) {
                throw new APIHTTPException("AD009", new Object[0]);
            }
            if (e2.getMessage().equalsIgnoreCase("invalid_client")) {
                throw new APIHTTPException("AD021", new Object[0]);
            }
            throw new APIHTTPException("AD011", new Object[0]);
        }
        catch (final APIHTTPException e3) {
            throw e3;
        }
        catch (final Exception e4) {
            IDPSlogger.ERR.log(Level.SEVERE, "getting Exception the Authorize URL", e4);
        }
        return null;
    }
    
    private String getDomainName(final String at) throws Exception {
        final JSONObject header = new JSONObject();
        final String authorization = "Bearer " + at;
        header.put("Authorization", (Object)authorization);
        final DMHttpRequest req = new DMHttpRequest();
        req.url = "https://graph.microsoft.com/v1.0/domains";
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
                    final String domainNameObtainedFromAzure = obj.optString("id", "--");
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
            return domainsOrderByPref.get(0);
        }
        throw new OauthException("invalid_grant");
    }
    
    public void unbindAzureMamIntegration(final Long userID, final Long customerID, Long oauthID) throws Exception {
        boolean authorized = true;
        if (userID != null) {
            authorized = false;
            if (customerID != null) {
                authorized = DirectoryAPIFacade.getInstance().isUserCustomerRelevant(userID, customerID);
            }
            if (!authorized) {
                throw new APIHTTPException("COM0013", new Object[0]);
            }
        }
        if (authorized) {
            JSONObject azureMamDetails = null;
            if (oauthID == null && customerID != null) {
                azureMamDetails = AzureMamDataHandler.getInstance().getAzureMamDetails(customerID);
                if (azureMamDetails != null) {
                    oauthID = azureMamDetails.optLong("AUTH_TOKEN_ID", -1L);
                }
            }
            if (oauthID != null) {
                OauthUtil.getInstance().revokeAndRemoveOauthTokenByOauthTokenId(customerID, userID, oauthID);
                if (azureMamDetails != null) {
                    AzureMamAuditHandler.getInstance().logEvent(2089, null, "mdm.mam.delete", "", customerID);
                }
            }
        }
    }
    
    private void deleteAzureMAMintegration(final Long userID, Long customerID, final Long oauthID) throws Exception {
        if (customerID == null || customerID.equals(-1L)) {
            customerID = CustomerInfoUtil.getInstance().getCustomerId();
        }
        if (customerID != null && !customerID.equals(-1L)) {
            this.unbindAzureMamIntegration(userID, customerID, oauthID);
        }
        else {
            IDPSlogger.ERR.log(Level.SEVERE, "no intune, but because could not get customer id for {0} , can''t unbind oauth", customerID);
        }
    }
    
    public void checkForIntuneMamSubscription(final Long userID, final Long customerID, final Long oauthID, final String accessToken) throws APIHTTPException {
        try {
            final JSONObject header = new JSONObject();
            header.put("Authorization", (Object)("Bearer " + accessToken));
            final DMHttpRequest req = new DMHttpRequest();
            req.headers = header;
            req.url = "https://graph.microsoft.com/v1.0/deviceAppManagement/managedAppPolicies";
            req.method = "GET";
            final JSONObject resp = this.executeDMHttpRequest(req);
            if (resp != null) {
                if (!resp.has("error") && resp.has("value")) {
                    return;
                }
                if (resp.has("error")) {
                    throw new APIHTTPException("AD019", new Object[0]);
                }
            }
            throw new APIHTTPException("AD011", new Object[0]);
        }
        catch (final APIHTTPException ex) {
            try {
                this.deleteAzureMAMintegration(userID, customerID, oauthID);
            }
            catch (final Exception ex2) {
                IDPSlogger.ERR.log(Level.SEVERE, "exception revoking azure mam integ", ex2);
            }
            throw ex;
        }
    }
    
    private JSONObject executeDMHttpRequest(final DMHttpRequest dmHttpRequest) throws JSONException {
        IDPSlogger.SOM.log(Level.INFO, "hitting url : {0}", dmHttpRequest.url);
        final DMHttpResponse dmHttpResponse = SyMUtil.executeDMHttpRequest(dmHttpRequest);
        final String responseString = dmHttpResponse.responseBodyAsString;
        JSONObject resultJSObject = new JSONObject();
        if (responseString != null) {
            resultJSObject = new JSONObject(responseString);
        }
        return resultJSObject;
    }
    
    static {
        AzureMamOauthHandler.azureMamOauthHandler = null;
    }
}
