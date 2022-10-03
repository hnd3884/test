package com.me.mdm.server.adep;

import com.me.mdm.core.ios.adep.AppleDEPServerConstants;
import com.me.devicemanagement.framework.server.httpclient.DMHttpResponse;
import net.oauth.OAuthMessage;
import java.util.List;
import java.util.logging.Level;
import com.me.devicemanagement.framework.server.httpclient.DMHttpRequest;
import java.util.Collection;
import net.oauth.OAuth;
import java.util.Map;
import java.util.ArrayList;
import net.oauth.OAuthAccessor;
import net.oauth.ParameterStyle;
import net.oauth.OAuthServiceProvider;
import net.oauth.OAuthConsumer;
import com.me.devicemanagement.framework.server.exception.SyMException;
import com.adventnet.sym.server.mdm.util.JSONUtil;
import com.me.devicemanagement.framework.server.logger.DMSecurityLogger;
import org.json.JSONObject;
import java.util.logging.Logger;
import com.me.devicemanagement.framework.server.httpclient.DMHttpClient;

public class AppleDEPServerRequestHandler
{
    String sessionToken;
    DMHttpClient client;
    private static Logger logger;
    
    public AppleDEPServerRequestHandler() {
        this.sessionToken = null;
        this.client = new DMHttpClient();
    }
    
    public JSONObject processRequestForDEPToken(final JSONObject requestJSON) throws Exception {
        DMSecurityLogger.info(AppleDEPServerRequestHandler.logger, "AppleDEPServerRequestHandler", "processRequestForDEPToken", "Request JSON {0}", (Object)requestJSON.toString());
        if (this.sessionToken == null) {
            final JSONObject getTokenDetailsJson = new JSONObject();
            getTokenDetailsJson.put("CUSTOMER_ID", requestJSON.getLong("CustomerId"));
            final Long tokenId = requestJSON.optLong("DEP_TOKEN_ID");
            final JSONObject depTokenDetails = AppleDEPAuthTokenHandler.getInstance().getDEPTokenDetails(tokenId);
            JSONUtil.putAll(depTokenDetails, requestJSON);
            return this.processRequest(depTokenDetails);
        }
        return requestJSON;
    }
    
    public JSONObject processRequest(final JSONObject requestJSON) throws Exception {
        DMSecurityLogger.info(AppleDEPServerRequestHandler.logger, "AppleDEPServerRequestHandler", "processRequest", "Request JSON {0}", (Object)requestJSON.toString());
        JSONObject responseJSON = new JSONObject();
        if (this.sessionToken == null) {
            final JSONObject sessionTokenJSON = this.handleSessionTokenRequest(requestJSON);
            if (sessionTokenJSON.has("auth_session_token")) {
                this.sessionToken = String.valueOf(sessionTokenJSON.get("auth_session_token"));
            }
            else {
                if (sessionTokenJSON.has("DEPServiceError")) {
                    final Long customerID = requestJSON.getLong("CUSTOMER_ID");
                    final Long tokenId = requestJSON.getLong("DEP_TOKEN_ID");
                    ADEPServerSyncHandler.getInstance(tokenId, customerID).handleDEPServerHandshakeExceptions(new SyMException(sessionTokenJSON.getJSONObject("DEPServiceError").optInt("DEPServerErrorCode"), sessionTokenJSON.getJSONObject("DEPServiceError").optString("DEPServerErrorMsg"), (Throwable)null));
                    return sessionTokenJSON;
                }
                return sessionTokenJSON;
            }
        }
        final String depWebServiceName = requestJSON.optString("DEPServiceRequestName", "DEPServiceNotFound");
        if (depWebServiceName.equalsIgnoreCase("Account")) {
            responseJSON = this.handleAccountRequest(requestJSON, responseJSON);
        }
        else if (depWebServiceName.equalsIgnoreCase("DefineProfile")) {
            responseJSON = this.handleDefineProfileRequest(requestJSON, responseJSON);
        }
        else if (depWebServiceName.equalsIgnoreCase("AssignProfile")) {
            responseJSON = this.handleAssignProfileRequest(requestJSON, responseJSON);
        }
        else if (depWebServiceName.equalsIgnoreCase("FetchProfile")) {
            responseJSON = this.handleFetchProfileDetailsRequest(requestJSON, responseJSON);
        }
        else if (depWebServiceName.equalsIgnoreCase("RemoveProfile")) {
            responseJSON = this.handleRemoveProfileRequest(requestJSON, responseJSON);
        }
        else if (depWebServiceName.equalsIgnoreCase("FetchDevices")) {
            responseJSON = this.handleFetchDevicesRequest(requestJSON, responseJSON);
        }
        else if (depWebServiceName.equalsIgnoreCase("SyncDevices")) {
            responseJSON = this.handleSyncDevicesRequest(requestJSON, responseJSON);
        }
        else if (depWebServiceName.equalsIgnoreCase("FetchDevices")) {
            responseJSON = this.handleFetchDevicesRequest(requestJSON, responseJSON);
        }
        else if (depWebServiceName.equalsIgnoreCase("Devices")) {
            responseJSON = this.handleDeviceDetailsRequest(requestJSON, responseJSON);
        }
        DMSecurityLogger.info(AppleDEPServerRequestHandler.logger, "AppleDEPServerRequestHandler", "processRequestForDEPToken", "Response JSON {0}", (Object)responseJSON);
        final String status = String.valueOf(responseJSON.get("DEPServiceStatus"));
        if (!status.equalsIgnoreCase("Acknowledged")) {
            final Long customerID2 = requestJSON.getLong("CUSTOMER_ID");
            final Long tokenId2 = requestJSON.getLong("DEP_TOKEN_ID");
            ADEPServerSyncHandler.getInstance(tokenId2, customerID2).handleDEPServerHandshakeExceptions(new SyMException(responseJSON.optJSONObject("DEPServiceError").optInt("DEPServerErrorCode"), responseJSON.optJSONObject("DEPServiceError").optString("DEPServerErrorMsg"), (Throwable)null));
        }
        return responseJSON;
    }
    
    private JSONObject handleSessionTokenRequest(final JSONObject jsonObject) throws Exception {
        final String consumerkey = String.valueOf(jsonObject.get("consumer_key"));
        final String consumersecret = String.valueOf(jsonObject.get("consumer_secret"));
        final String accesssecret = String.valueOf(jsonObject.get("access_secret"));
        final String accesstoken = String.valueOf(jsonObject.get("access_token"));
        final OAuthConsumer consumer = new OAuthConsumer((String)null, consumerkey, consumersecret, (OAuthServiceProvider)null);
        consumer.setProperty("parameterStyle", (Object)ParameterStyle.AUTHORIZATION_HEADER);
        final OAuthAccessor accessor = new OAuthAccessor(consumer);
        accessor.tokenSecret = accesssecret;
        final List<Map.Entry> params = new ArrayList<Map.Entry>();
        params.add((Map.Entry)new OAuth.Parameter("oauth_token", accesstoken));
        final OAuthMessage oauthmsg = accessor.newRequestMessage("GET", "https://mdmenrollment.apple.com/session", (Collection)params);
        final JSONObject sessionTokenHeaders = new JSONObject();
        sessionTokenHeaders.put("Authorization", (Object)oauthmsg.getAuthorizationHeader((String)null));
        final DMHttpRequest dmsessionrequest = new DMHttpRequest();
        sessionTokenHeaders.put("User-Agent", (Object)"MEMDMServer");
        dmsessionrequest.headers = sessionTokenHeaders;
        dmsessionrequest.url = "https://mdmenrollment.apple.com/session";
        dmsessionrequest.method = "GET";
        final DMHttpResponse dmresponse = this.client.execute(dmsessionrequest);
        AppleDEPServerRequestHandler.logger.log(Level.SEVERE, "RESPONSE MESSAGE: STATUS: {0}", dmresponse.status);
        JSONObject sessionTokenJSON = new JSONObject();
        Boolean isJSON = false;
        final String contentType = (String)JSONUtil.optObject(dmresponse.responseHeaders, "Content-Type", false);
        if (contentType != null && contentType.toLowerCase().contains("application/json")) {
            isJSON = true;
        }
        try {
            sessionTokenJSON = new JSONObject(dmresponse.responseBodyAsString);
            isJSON = true;
        }
        catch (final Exception ex) {
            isJSON = false;
            AppleDEPServerRequestHandler.logger.log(Level.WARNING, "Response is not jsonObject :", ex);
        }
        if (dmresponse.status != 200 || !isJSON) {
            sessionTokenJSON.put("DEPServiceStatus", (Object)"Error");
            final JSONObject errorJSON = new JSONObject();
            errorJSON.put("DEPServerErrorCode", dmresponse.status);
            errorJSON.put("DEPServerErrorMsg", (Object)dmresponse.responseBodyAsString);
            sessionTokenJSON.put("DEPServiceError", (Object)errorJSON);
            AppleDEPServerRequestHandler.logger.log(Level.SEVERE, errorJSON.toString());
        }
        return sessionTokenJSON;
    }
    
    private JSONObject handleRequest(final String url, final JSONObject requestJSON, final String method) throws Exception {
        final JSONObject responseJSON = new JSONObject();
        responseJSON.put("DEPServiceResponseName", (Object)requestJSON.optString("DEPServiceRequestName"));
        final JSONObject serviceRequestDataJSON = requestJSON.optJSONObject("DEPServiceRequestData");
        final JSONObject serviceRequestParamsJSON = requestJSON.optJSONObject("DEPServiceRequestParams");
        final DMHttpRequest dmrequest = new DMHttpRequest();
        final JSONObject requestHeaders = new JSONObject();
        requestHeaders.put("User-Agent", (Object)"MEMDMServer");
        requestHeaders.put("X-Server-Protocol-Version", (Object)AppleDEPServerConstants.DEP_SERVER_PROTOCOL_VERSION);
        requestHeaders.put("X-ADM-Auth-Session", (Object)this.sessionToken);
        if (serviceRequestDataJSON != null && serviceRequestDataJSON.length() > 0 && (method.equalsIgnoreCase("PUT") || method.equalsIgnoreCase("POST") || method.equalsIgnoreCase("DELETE"))) {
            dmrequest.data = serviceRequestDataJSON.toString().getBytes();
            requestHeaders.put("Content-Length", serviceRequestDataJSON.toString().getBytes().length);
            requestHeaders.put("Content-Type", (Object)"application/json;charset=UTF8");
        }
        if (serviceRequestParamsJSON != null && serviceRequestParamsJSON.length() > 0) {
            dmrequest.parameters = serviceRequestParamsJSON;
        }
        dmrequest.headers = requestHeaders;
        dmrequest.url = url;
        dmrequest.method = method;
        DMHttpResponse dmresponse = null;
        if (method != "DELETE") {
            dmresponse = this.client.execute(dmrequest);
        }
        else {
            dmresponse = this.client.executeDeleteMethod(dmrequest);
        }
        JSONObject responseDataJSON = new JSONObject();
        if (method == "DELETE") {
            responseJSON.put("DEPServiceStatus", (Object)"Acknowledged");
            responseJSON.put("Response", (Object)dmresponse.responseBodyAsString);
        }
        else {
            Boolean isJSON = false;
            final String contentType = (String)JSONUtil.optObject(dmresponse.responseHeaders, "Content-Type", false);
            if (contentType != null && contentType.toLowerCase().contains("application/json")) {
                isJSON = true;
            }
            try {
                responseDataJSON = new JSONObject(dmresponse.responseBodyAsString);
                responseJSON.put("DEPServiceStatus", (Object)"Acknowledged");
                responseJSON.put("DEPServiceResponseData", (Object)responseDataJSON);
                isJSON = true;
            }
            catch (final Exception ex) {
                isJSON = false;
                AppleDEPServerRequestHandler.logger.log(Level.WARNING, "Response is not jsonObject :", ex);
            }
            if (dmresponse.status != 200 || !isJSON) {
                responseJSON.put("DEPServiceStatus", (Object)"Error");
                final JSONObject errorJSON = new JSONObject();
                errorJSON.put("DEPServerErrorCode", dmresponse.status);
                errorJSON.put("DEPServerErrorMsg", (Object)dmresponse.responseBodyAsString);
                responseJSON.put("DEPServiceError", (Object)errorJSON);
            }
        }
        return responseJSON;
    }
    
    private JSONObject handleAccountRequest(final JSONObject requestJSON, final JSONObject responseJSON) throws Exception {
        return this.handleRequest("https://mdmenrollment.apple.com/account", requestJSON, "GET");
    }
    
    private JSONObject handleDefineProfileRequest(final JSONObject requestJSON, final JSONObject responseJSON) throws Exception {
        return this.handleRequest("https://mdmenrollment.apple.com/profile", requestJSON, "POST");
    }
    
    private JSONObject handleFetchProfileDetailsRequest(final JSONObject requestJSON, final JSONObject responseJSON) throws Exception {
        return this.handleRequest("https://mdmenrollment.apple.com/profile", requestJSON, "GET");
    }
    
    private JSONObject handleAssignProfileRequest(final JSONObject requestJSON, final JSONObject responseJSON) throws Exception {
        return this.handleRequest("https://mdmenrollment.apple.com/profile/devices", requestJSON, "PUT");
    }
    
    private JSONObject handleRemoveProfileRequest(final JSONObject requestJSON, final JSONObject responseJSON) throws Exception {
        return this.handleRequest("https://mdmenrollment.apple.com/profile/devices", requestJSON, "DELETE");
    }
    
    private JSONObject handleFetchDevicesRequest(final JSONObject requestJSON, final JSONObject responseJSON) throws Exception {
        return this.handleRequest("https://mdmenrollment.apple.com/server/devices", requestJSON, "POST");
    }
    
    private JSONObject handleSyncDevicesRequest(final JSONObject requestJSON, final JSONObject responseJSON) throws Exception {
        return this.handleRequest("https://mdmenrollment.apple.com/devices/sync", requestJSON, "POST");
    }
    
    private JSONObject handleDeviceDetailsRequest(final JSONObject requestJSON, final JSONObject responseJSON) throws Exception {
        return this.handleRequest("https://mdmenrollment.apple.com/devices", requestJSON, "POST");
    }
    
    static {
        AppleDEPServerRequestHandler.logger = Logger.getLogger("MDMEnrollment");
    }
}
