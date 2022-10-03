package com.me.mdm.api.core.azuremam;

import com.me.mdm.api.APIUtil;
import com.me.mdm.directory.service.mam.AzureMamOauthHandler;
import com.me.idps.core.oauth.OauthException;
import com.me.mdm.api.error.APIHTTPException;
import java.util.logging.Level;
import com.me.idps.core.oauth.OauthUtil;
import com.me.mdm.directory.service.mam.AzureMamDataHandler;
import org.json.JSONObject;
import com.me.mdm.api.APIRequest;
import com.me.mdm.api.ApiRequestHandler;

public class AzureMamApi extends ApiRequestHandler
{
    @Override
    public Object doGet(final APIRequest apiRequest) throws APIHTTPException {
        try {
            final JSONObject response = new JSONObject();
            response.put("status", 200);
            final Long customerID = apiRequest.getParameterList().get("customer_id");
            JSONObject azureMamaPorps = AzureMamDataHandler.getInstance().getAzureMamDetails(customerID);
            if (azureMamaPorps != null) {
                final String oauth_token = OauthUtil.getInstance().fetchAccessTokenFromOauthId(Long.valueOf(azureMamaPorps.getLong("AUTH_TOKEN_ID")));
                azureMamaPorps.put("access_token", (Object)oauth_token);
            }
            else {
                azureMamaPorps = new JSONObject();
            }
            response.put("RESPONSE", (Object)azureMamaPorps);
            return response;
        }
        catch (final APIHTTPException e) {
            this.logger.log(Level.SEVERE, "Error APIHTTPException Occured in doGet /azuremam", e);
            throw e;
        }
        catch (final OauthException e2) {
            this.logger.log(Level.SEVERE, "Error Exception Occured in doGet /azuremam", (Throwable)e2);
            throw new APIHTTPException("AD011", new Object[0]);
        }
        catch (final Exception e3) {
            this.logger.log(Level.SEVERE, "Error Exception Occured in doGet /azuremam", e3);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    @Override
    public Object doDelete(final APIRequest apiRequest) throws APIHTTPException {
        try {
            final JSONObject response = new JSONObject();
            response.put("status", 204);
            final Long customerID = apiRequest.getParameterList().get("customer_id");
            final JSONObject azureMamaPorps = AzureMamDataHandler.getInstance().getAzureMamDetails(customerID);
            if (azureMamaPorps == null) {
                return new APIHTTPException("MAM0001", new Object[0]);
            }
            final Long oauthID = azureMamaPorps.getLong("AUTH_TOKEN_ID");
            AzureMamOauthHandler.getInstance().unbindAzureMamIntegration(APIUtil.getUserID(apiRequest.toJSONObject()), customerID, oauthID);
            return response;
        }
        catch (final APIHTTPException e) {
            this.logger.log(Level.SEVERE, "Error APIHTTPException Occured in dodelete /azuremam", e);
            throw e;
        }
        catch (final Exception e2) {
            this.logger.log(Level.SEVERE, "Error Exception Occured in dodelete /azuremam", e2);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
}
