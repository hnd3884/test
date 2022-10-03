package com.me.mdm.onpremise.remotesession;

import com.me.mdm.server.factory.MDMApiFactoryProvider;
import com.me.mdm.server.onelinelogger.MDMOneLineLogger;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.me.devicemanagement.framework.webclient.message.MessageProvider;
import com.me.idps.core.oauth.OauthUtil;
import com.me.devicemanagement.framework.server.api.EvaluatorAPI;
import java.util.logging.Level;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import com.adventnet.sym.server.mdm.util.MDMEventLogHandler;
import com.me.mdm.api.error.APIHTTPException;
import java.util.Base64;
import com.me.mdm.api.APIUtil;
import org.json.JSONObject;
import com.me.mdm.api.APIRequest;
import java.util.logging.Logger;
import com.me.mdm.api.ApiRequestHandler;

public class AssistAuthenticateHandler extends ApiRequestHandler
{
    private static final Logger LOGGER;
    public static final int ASSIST_INTEGRATED = 10001;
    public static final int ASSIST_DISINTEGRATED = 10002;
    public static final int ASSIST_INTEGRATION_FAILED = 10003;
    public static final String INVALID_USERNAME = "ASSIST001";
    public static final String INVALID_PASSWORD = "ASSIST002";
    public static final String AUTH_TOKEN_LIMIT_EXCEEDED = "ASSIST003";
    public static final String ALREADY_LOGGED_IN = "ASSIST004";
    
    @Deprecated
    public Object doPost(final APIRequest apiRequest) throws APIHTTPException {
        final JSONObject response = new JSONObject();
        Long customerID = null;
        try {
            final JSONObject responseJSON = new JSONObject();
            final JSONObject requestJSON = apiRequest.toJSONObject();
            final JSONObject msgBody = requestJSON.optJSONObject("msg_body");
            final String userName = APIUtil.getUserName(requestJSON);
            final String emailId = msgBody.optString("username");
            final String password = new String(Base64.getDecoder().decode(msgBody.optString("password")));
            customerID = APIUtil.getCustomerID(requestJSON);
            if (new AssistAuthTokenHandler().isAssistIntegrated(customerID)) {
                throw new APIHTTPException("ASSIST004", new Object[0]);
            }
            final JSONObject respJSON = new AssistAuthTokenAPIManager().generateAuthtoken(emailId, password, customerID);
            final String authStatus = String.valueOf(respJSON.get("Status"));
            if (authStatus.equalsIgnoreCase("Success")) {
                new AssistAuthTokenAPIManager().updateAssistIntegrationDetails(respJSON, customerID, emailId);
                MDMEventLogHandler.getInstance().MDMEventLogEntry(10001, (Long)null, userName, "mdmp.actionlog.remotecontrol.assist_integrated", (Object)userName, customerID);
                final int status = 200;
                response.put("status", status);
                responseJSON.put("Status", (Object)"Success");
                response.put("RESPONSE", (Object)responseJSON);
                return response;
            }
            final EvaluatorAPI evaluatorApi = ApiFactoryProvider.getEvaluatorAPI();
            if (evaluatorApi != null) {
                evaluatorApi.addOrIncrementClickCountForTrialUsers("Inv_Module", "Assist_Login_Failed_Attempt");
            }
            if (respJSON.optString("Remarks").equals("dc.mdm.inv.remoteTroubleshoot.no_user")) {
                MDMEventLogHandler.getInstance().MDMEventLogEntry(10003, (Long)null, userName, "mdmp.actionlog.remotecontrol.assist_fail_username", (Object)null, customerID);
                this.logger.log(Level.INFO, "Invalid Username - Assist Integration Failed");
                throw new APIHTTPException("ASSIST001", new Object[0]);
            }
            if (respJSON.optString("Remarks").equals("dc.mdm.inv.remotetroubleshoot.max_limit_exceeded")) {
                MDMEventLogHandler.getInstance().MDMEventLogEntry(10003, (Long)null, userName, "mdmp.actionlog.remotecontrol.assist_fail_authtokn", (Object)null, customerID);
                this.logger.log(Level.INFO, "Max Authtoken limit reached - Assist Integration Failed");
                throw new APIHTTPException("ASSIST003", new Object[0]);
            }
            MDMEventLogHandler.getInstance().MDMEventLogEntry(10003, (Long)null, userName, "mdmp.actionlog.remotecontrol.assist_fail_password", (Object)null, customerID);
            this.logger.log(Level.INFO, "{0} - Assist Integration Failed", respJSON.optString("Remarks"));
            throw new APIHTTPException("ASSIST002", new Object[0]);
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, "error in doPost - settings/assistIntegration", ex);
            if (ex instanceof APIHTTPException) {
                throw (APIHTTPException)ex;
            }
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    public Object doDelete(final APIRequest apiRequest) throws APIHTTPException {
        String remarks = "zoho assist disintegrate failed";
        try {
            final JSONObject response = new JSONObject();
            final JSONObject requestJSON = apiRequest.toJSONObject();
            final Long customerID = APIUtil.getCustomerID(requestJSON);
            final String userName = APIUtil.getUserName(requestJSON);
            final JSONObject assistAccDetails = new AssistAuthTokenHandler().getAssistAccountDetails(customerID);
            final Long oauthId = assistAccDetails.optLong("AUTH_TOKEN_ID", -1L);
            OauthUtil.getInstance().revokeAndRemoveOauthTokenByOauthTokenId(customerID, APIUtil.getUserID(requestJSON), oauthId);
            new AssistAuthTokenHandler().resetAssistIntegDetails(customerID);
            MDMEventLogHandler.getInstance().MDMEventLogEntry(10002, (Long)null, userName, "mdmp.actionlog.remotecontrol.assist_disintegrated", (Object)userName, customerID);
            AssistAuthenticateHandler.LOGGER.log(Level.INFO, "logged out Assist Account for customer Id {0}", customerID);
            remarks = "zoho assist disintegrate success";
            MessageProvider.getInstance().hideMessage("ASSIST_AUTH_FAILED", customerID);
            MDMUtil.updateUserParameter(ApiFactoryProvider.getAuthUtilAccessAPI().getUserID(), "skipRemoteAccSettings", "true");
            response.put("status", 204);
            return response;
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "error in doDelete - settings/assistIntegration", e);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
        finally {
            MDMOneLineLogger.log(Level.INFO, "DISINTEGRATE_ASSIST", remarks);
        }
    }
    
    public Object doGet(final APIRequest apiRequest) throws APIHTTPException {
        final JSONObject response = new JSONObject();
        try {
            final Long customerID = APIUtil.getCustomerID(apiRequest.toJSONObject());
            final JSONObject resultJSON = new JSONObject();
            if (MDMApiFactoryProvider.getAssistAuthTokenHandler().isAssistIntegrated(customerID)) {
                final JSONObject assistAccDetails = new AssistAuthTokenHandler().getAssistAccountDetails(customerID);
                resultJSON.put("is_integrated", true);
                final JSONObject integratedDetails = new JSONObject();
                integratedDetails.put("username", (Object)String.valueOf(assistAccDetails.get("EMAIL_ADDRESS")));
                resultJSON.put("integration_details", (Object)integratedDetails);
            }
            else {
                resultJSON.put("is_integrated", false);
                resultJSON.put("integration_details", (Object)new JSONObject());
            }
            response.put("status", 200);
            response.put("RESPONSE", (Object)resultJSON);
            return response;
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "error in doGet - settings/assistIntegration", e);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    static {
        LOGGER = Logger.getLogger("MDMRemoteControlLogger");
    }
}
