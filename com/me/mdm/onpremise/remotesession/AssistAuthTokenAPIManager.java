package com.me.mdm.onpremise.remotesession;

import com.me.devicemanagement.framework.server.api.EvaluatorAPI;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import java.io.IOException;
import org.json.JSONException;
import java.net.MalformedURLException;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import org.json.JSONObject;
import java.util.Properties;
import java.util.logging.Logger;

public class AssistAuthTokenAPIManager
{
    private static final Logger LOGGER;
    private static final String NO_USER = "NO_SUCH_USER";
    private static final String INVALID_PASSWORD = "INVALID_PASSWORD";
    private static final String INVALID_CREDENTIAL = "INVALID_CREDENTIALS";
    private static final String WEB_LOGIN_REQUIRED = "WEB_LOGIN_REQUIRED";
    private static final String MAX_LIMIT_EXCEEDED = "EXCEEDED_MAXIMUM_ALLOWED_AUTHTOKENS";
    private static final String ASSISTACCOUNTURL = "AssistAccountUrl";
    private static final String CUSTOMERDOMAIN = "CusDomain.";
    private static Properties assistProperties;
    
    public JSONObject generateAuthtoken(final String emailId, final String password, final Long customerID) throws MalformedURLException, JSONException, IOException, Exception {
        AssistAuthTokenAPIManager.assistProperties = new MDMUtil().getMDMApplicationProperties();
        JSONObject responseJSON = new JSONObject();
        Properties respProperties = null;
        final AssistAuthTokenUtil assistAuthTokenutil = new AssistAuthTokenUtil();
        final String customerDomain = assistAuthTokenutil.getCustomerDomain(customerID);
        if (customerDomain != null) {
            respProperties = assistAuthTokenutil.connectToAssist(emailId, password, AssistAuthTokenAPIManager.assistProperties.getProperty("AssistAccountUrl").replaceFirst("com", customerDomain));
            if (respProperties != null) {
                responseJSON = assistAuthTokenutil.getResponseFromProperties(respProperties);
            }
            else {
                responseJSON = assistAuthTokenutil.getAuthToken(emailId, password, customerID);
            }
        }
        else {
            respProperties = assistAuthTokenutil.connectToAssist(emailId, password, AssistAuthTokenAPIManager.assistProperties.getProperty("AssistAccountUrl"));
            if (respProperties != null) {
                responseJSON = assistAuthTokenutil.getResponseFromProperties(respProperties);
                responseJSON.put("domain", (Object)"com");
            }
            else {
                responseJSON = assistAuthTokenutil.getAuthToken(emailId, password, customerID);
            }
        }
        return responseJSON;
    }
    
    public String getRemarkForCause(final String cause) throws Exception {
        String remarks = "Failed to fetch Auth token :: " + cause;
        if (cause.equals("NO_SUCH_USER")) {
            remarks = "dc.mdm.inv.remoteTroubleshoot.no_user";
        }
        else if (cause.equals("INVALID_PASSWORD") || cause.equals("INVALID_CREDENTIALS")) {
            remarks = "dc.mdm.inv.remoteTroubleshoot.wrong_credentials";
        }
        else if (cause.equals("WEB_LOGIN_REQUIRED")) {
            remarks = "dc.mdm.inv.remoteTroubleshoot.tfa_enabled";
        }
        else if (cause.equals("EXCEEDED_MAXIMUM_ALLOWED_AUTHTOKENS")) {
            remarks = "dc.mdm.inv.remotetroubleshoot.max_limit_exceeded";
        }
        this.incrementAuthTokenFailureCause(cause);
        return remarks;
    }
    
    public void incrementAuthTokenFailureCause(final String cause) {
        final EvaluatorAPI evaluatorAPI = ApiFactoryProvider.getEvaluatorAPI();
        if (evaluatorAPI != null) {
            evaluatorAPI.addOrIncrementClickCountForTrialUsers("Remote_Module", cause);
        }
        SyMUtil.updateSyMParameter("Auth_Token_Failure_Cause", cause);
    }
    
    public void updateAssistIntegrationDetails(final JSONObject jsonObject, final Long customerID, final String emailId) throws Exception {
        final String domain = jsonObject.optString("domain", "com");
        final JSONObject assistIntegDetails = new JSONObject();
        assistIntegDetails.put("CUSTOMER_ID", (Object)customerID);
        assistIntegDetails.put("EMAIL_ADDRESS", (Object)emailId);
        assistIntegDetails.put("AUTH_TOKEN_ID", jsonObject.get("AUTH_TOKEN_ID"));
        assistIntegDetails.put("ADDED_BY", (Object)ApiFactoryProvider.getAuthUtilAccessAPI().getUserID());
        assistIntegDetails.put("CUSTOMER_COUNTRY_CODE", (Object)domain);
        new AssistAuthTokenHandler().addOrUpdateAssistAuthTokenDetails(assistIntegDetails);
    }
    
    static {
        LOGGER = Logger.getLogger("MDMRemoteControlLogger");
    }
}
