package com.me.mdm.onpremise.remotesession.oauth;

import com.adventnet.persistence.DataAccessException;
import org.json.JSONException;
import com.me.mdm.api.error.APIHTTPException;
import com.me.devicemanagement.framework.server.logger.SyMLogger;
import java.util.logging.Level;
import com.adventnet.sym.server.mdm.util.MDMEventLogHandler;
import com.me.mdm.api.APIUtil;
import com.me.idps.mdmop.oauth.AssistOauthService;
import com.me.idps.core.oauth.OauthUtil;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import com.me.mdm.onpremise.remotesession.AssistAuthTokenHandler;
import com.me.idps.core.oauth.OauthException;
import org.json.JSONObject;

public class AssistOauthUtil
{
    private static AssistOauthUtil assistOauthUtil;
    public static final int ASSIST_INTEGRATED = 10001;
    
    public static AssistOauthUtil getInstance() {
        if (AssistOauthUtil.assistOauthUtil == null) {
            AssistOauthUtil.assistOauthUtil = new AssistOauthUtil();
        }
        return AssistOauthUtil.assistOauthUtil;
    }
    
    public Long getAccessToken(final Long customerID, final JSONObject requestJSON) {
        try {
            final JSONObject jsonBody = requestJSON.optJSONObject("msg_body");
            if (jsonBody.has("error_msg")) {
                throw new OauthException("invalid_request");
            }
            final int domain = jsonBody.optInt("domain_type", -1);
            final JSONObject assistDetails = new AssistAuthTokenHandler().getAssistAccountDetails(customerID);
            long oauthId = -1L;
            if (assistDetails != null) {
                oauthId = assistDetails.optLong("AUTH_TOKEN_ID", -1L);
            }
            JSONObject oauth = new JSONObject();
            oauth.put("domain", domain);
            oauth.put("OAUTH_TYPE", 202);
            oauth.put("OAUTH_TOKEN_ID", oauthId);
            if (jsonBody.has("code")) {
                oauth.put("code", jsonBody.get("code"));
                oauth.put("scope", (Object)"ZohoAssist.userapi.READ,ZohoAssist.sessionapi.CREATE,AaaServer.profile.READ");
            }
            JSONObject assistIntegDetails = new JSONObject();
            assistIntegDetails.put("CUSTOMER_ID", (Object)customerID);
            assistIntegDetails.put("ADDED_BY", (Object)ApiFactoryProvider.getAuthUtilAccessAPI().getUserID());
            assistIntegDetails.put("TOKEN_PARAM", (Object)new Integer(1));
            String domain_tail = jsonBody.optString("domain", "com");
            assistIntegDetails.put("CUSTOMER_COUNTRY_CODE", (Object)domain_tail);
            new AssistAuthTokenHandler().addOrUpdateAssistAuthTokenDetails(assistIntegDetails);
            oauthId = OauthUtil.getInstance().register(customerID, oauth);
            final String at = OauthUtil.getInstance().fetchAccessTokenFromOauthId(Long.valueOf(oauthId));
            final String email = AssistOauthService.getInstance().getAccountsUserName(at);
            oauth = new JSONObject();
            oauth.put("OAUTH_TOKEN_ID", oauthId);
            oauth.put("STATUS", 1);
            oauth.put("REFERENCE_USER", (Object)email);
            OauthUtil.getInstance().registerOauth(oauth);
            assistIntegDetails = new JSONObject();
            assistIntegDetails.put("CUSTOMER_ID", (Object)customerID);
            assistIntegDetails.put("EMAIL_ADDRESS", (Object)email);
            assistIntegDetails.put("AUTH_TOKEN_ID", oauthId);
            assistIntegDetails.put("ADDED_BY", (Object)ApiFactoryProvider.getAuthUtilAccessAPI().getUserID());
            assistIntegDetails.put("TOKEN_PARAM", (Object)new Integer(1));
            domain_tail = jsonBody.optString("domain", "com");
            assistIntegDetails.put("CUSTOMER_COUNTRY_CODE", (Object)domain_tail);
            new AssistAuthTokenHandler().addOrUpdateAssistAuthTokenDetails(assistIntegDetails);
            final String userName = APIUtil.getUserName(requestJSON);
            MDMEventLogHandler.getInstance().MDMEventLogEntry(10001, (Long)null, userName, "mdmp.actionlog.remotecontrol.assist_integrated", (Object)userName, customerID);
            return oauthId;
        }
        catch (final JSONException e) {
            SyMLogger.log("MDMLogger", Level.SEVERE, "getting the Authorize URL", (Throwable)e);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
        catch (final OauthException e2) {
            try {
                new AssistAuthTokenHandler().resetAssistIntegDetails(customerID);
            }
            catch (final DataAccessException ex) {
                SyMLogger.log("MDMLogger", Level.SEVERE, "error in clearing assist integration", (Throwable)ex);
            }
            SyMLogger.log("MDMLogger", Level.SEVERE, "getting OAuthException the Authorize URL", (Throwable)e2);
            throw new APIHTTPException("COM0014", new Object[0]);
        }
        catch (final Exception e3) {
            SyMLogger.log("MDMLogger", Level.SEVERE, "getting Exception the Authorize URL", (Throwable)e3);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    static {
        AssistOauthUtil.assistOauthUtil = null;
    }
}
