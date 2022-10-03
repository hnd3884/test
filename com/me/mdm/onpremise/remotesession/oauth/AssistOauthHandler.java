package com.me.mdm.onpremise.remotesession.oauth;

import com.me.mdm.server.onelinelogger.MDMOneLineLogger;
import com.me.mdm.api.error.APIHTTPException;
import java.util.logging.Level;
import com.me.idps.mdmop.oauth.AssistOauthService;
import com.me.mdm.api.APIUtil;
import org.json.JSONObject;
import com.me.mdm.api.APIRequest;
import com.me.mdm.api.ApiRequestHandler;

public class AssistOauthHandler extends ApiRequestHandler
{
    public Object doGet(final APIRequest apiRequest) throws APIHTTPException {
        final JSONObject response = new JSONObject();
        try {
            final JSONObject request = apiRequest.toJSONObject();
            final Long userID = APIUtil.getUserID(request);
            final Long customerID = APIUtil.getCustomerID(request);
            final JSONObject resultJSON = new JSONObject();
            final Long oauthid = -1L;
            final String[] scopes = { "ZohoAssist.userapi.READ", "ZohoAssist.sessionapi.CREATE", "AaaServer.profile.READ" };
            String state = APIUtil.getStringFilter(apiRequest.toJSONObject(), "state");
            state = state.replaceAll("%oauthid%", String.valueOf(oauthid));
            final String url = AssistOauthService.getInstance().getAuthorizeUrl(customerID, userID, scopes, state);
            resultJSON.put("url", (Object)url);
            response.put("status", 200);
            response.put("RESPONSE", (Object)resultJSON);
            return response;
        }
        catch (final APIHTTPException e) {
            this.logger.log(Level.SEVERE, "error in doGet - settings/assistIntegration", (Throwable)e);
            throw e;
        }
        catch (final Exception e2) {
            this.logger.log(Level.SEVERE, "error in doGet - settings/assistIntegration", e2);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    public Object doPost(final APIRequest apiRequest) throws APIHTTPException {
        String remarks = "zoho assist integration failed";
        try {
            final Long customerID = apiRequest.getParameterList().get("customer_id");
            if (customerID == null) {
                throw new APIHTTPException("COM0014", new Object[0]);
            }
            final Long oauthId = AssistOauthUtil.getInstance().getAccessToken(customerID, apiRequest.toJSONObject());
            if (oauthId == null) {
                throw new APIHTTPException("COM0004", new Object[0]);
            }
            final JSONObject body = new JSONObject();
            body.put("OAUTH_TOKEN_ID", (Object)oauthId);
            final JSONObject response = new JSONObject();
            response.put("RESPONSE", (Object)body);
            response.put("status", 201);
            remarks = "zoho assist integration success";
            return response;
        }
        catch (final APIHTTPException e) {
            this.logger.log(Level.SEVERE, "Error APIHTTPException Occured in doGet /directory/oauth/:id", (Throwable)e);
            throw e;
        }
        catch (final Exception e2) {
            this.logger.log(Level.SEVERE, "Error Exception Occured in doGet /directory/oauth/:id", e2);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
        finally {
            MDMOneLineLogger.log(Level.INFO, "INTEGRATE_ASSIST", remarks);
        }
    }
}
