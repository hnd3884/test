package com.me.mdm.api.core.azuremam;

import com.me.idps.core.api.IdpsAPIException;
import com.me.idps.core.IDPSlogger;
import com.me.idps.core.factory.IdpsFactoryProvider;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import java.util.logging.Level;
import org.json.JSONObject;
import com.me.mdm.directory.service.mam.AzureMamOauthHandler;
import com.me.mdm.api.error.APIHTTPException;
import com.me.mdm.api.APIUtil;
import com.me.mdm.api.APIRequest;
import com.me.mdm.api.ApiRequestHandler;

public class AzureMamOauth extends ApiRequestHandler
{
    @Override
    public Object doPost(final APIRequest apiRequest) throws APIHTTPException {
        try {
            final Long customerID = APIUtil.getCustomerID(apiRequest.toJSONObject());
            final Long userId = APIUtil.getUserID(apiRequest.toJSONObject());
            if (customerID == null) {
                throw new APIHTTPException("COM0014", new Object[0]);
            }
            final Long oauthId = AzureMamOauthHandler.getInstance().getAccessToken(customerID, userId, apiRequest.toJSONObject().getJSONObject("msg_body"));
            if (oauthId == null) {
                throw new APIHTTPException("COM0004", new Object[0]);
            }
            final JSONObject body = new JSONObject();
            body.put("OAUTH_TOKEN_ID", (Object)oauthId);
            final JSONObject response = new JSONObject();
            response.put("RESPONSE", (Object)body);
            response.put("status", 201);
            return response;
        }
        catch (final APIHTTPException e) {
            this.logger.log(Level.SEVERE, "Error APIHTTPException Occured in doPost /directory/azuremam/", e);
            throw e;
        }
        catch (final Exception e2) {
            this.logger.log(Level.SEVERE, "Error Exception Occured in doPost /directory/azuremam/", e2);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    @Override
    public Object doGet(final APIRequest apiRequest) throws APIHTTPException {
        try {
            final JSONObject response = new JSONObject();
            response.put("status", 200);
            final JSONObject request = apiRequest.toJSONObject();
            final String state = APIUtil.getStringFilter(request, "state");
            if (SyMUtil.isStringEmpty(state)) {
                throw new APIHTTPException("COM0024", new Object[0]);
            }
            final String url = IdpsFactoryProvider.getOauthImpl(203).getAuthorizeUrl(APIUtil.getCustomerID(request), APIUtil.getUserID(request), (String[])null, state);
            final JSONObject urlObject = new JSONObject();
            urlObject.put("url", (Object)url);
            response.put("RESPONSE", (Object)urlObject);
            return response;
        }
        catch (final IdpsAPIException e) {
            IDPSlogger.ERR.log(Level.SEVERE, "IdpsAPIException ", (Throwable)e);
            throw new APIHTTPException(e.getMessage(), new Object[0]);
        }
        catch (final APIHTTPException e2) {
            this.logger.log(Level.SEVERE, "Error APIHTTPException Occured in doGet /directory/azuremam", e2);
            throw e2;
        }
        catch (final Exception e3) {
            this.logger.log(Level.SEVERE, "Error Exception Occured in doGet /directory/azuremam", e3);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
}
