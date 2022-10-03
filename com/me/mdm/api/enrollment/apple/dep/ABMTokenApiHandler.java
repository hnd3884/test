package com.me.mdm.api.enrollment.apple.dep;

import com.me.mdm.api.error.APIHTTPException;
import java.util.logging.Level;
import org.json.JSONObject;
import com.me.mdm.server.adep.ABMAuthTokenFacade;
import com.me.mdm.api.APIRequest;
import com.me.mdm.api.ApiRequestHandler;

public class ABMTokenApiHandler extends ApiRequestHandler
{
    @Override
    public Object doGet(final APIRequest apiRequest) throws APIHTTPException {
        try {
            final JSONObject success = ABMAuthTokenFacade.getInstance().getNotifyEmailAddr(apiRequest.toJSONObject());
            final JSONObject response = new JSONObject();
            response.put("status", 200);
            response.put("RESPONSE", (Object)success);
            return response;
        }
        catch (final APIHTTPException e) {
            this.logger.log(Level.WARNING, "Exception while getting DEP token expiry addr Api..", e);
            throw e;
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, "Exception while getting DEP token expiry addr Api..", ex);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    @Override
    public Object doPost(final APIRequest apiRequest) throws APIHTTPException {
        try {
            final JSONObject success = ABMAuthTokenFacade.getInstance().saveDEPToken(apiRequest.toJSONObject());
            final JSONObject response = new JSONObject();
            response.put("status", 200);
            response.put("RESPONSE", (Object)success);
            return response;
        }
        catch (final APIHTTPException e) {
            this.logger.log(Level.WARNING, "Exception while uploading DEP token through Api..", e);
            throw e;
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, "Exception while uploading DEP token through Api..", ex);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    @Override
    public Object doPut(final APIRequest apiRequest) throws APIHTTPException {
        try {
            final JSONObject success = ABMAuthTokenFacade.getInstance().saveDEPToken(apiRequest.toJSONObject());
            final JSONObject response = new JSONObject();
            response.put("status", 200);
            response.put("RESPONSE", (Object)success);
            return response;
        }
        catch (final APIHTTPException e) {
            this.logger.log(Level.WARNING, "Exception while replacing DEP token through Api..", e);
            throw e;
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, "Exception while replacing DEP token through Api..", ex);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    @Override
    public Object doDelete(final APIRequest apiRequest) throws APIHTTPException {
        try {
            final JSONObject removeResponse = ABMAuthTokenFacade.getInstance().removeDEPToken(apiRequest.toJSONObject());
            final JSONObject response = new JSONObject();
            response.put("status", 200);
            response.put("RESPONSE", (Object)removeResponse);
            return response;
        }
        catch (final APIHTTPException e) {
            this.logger.log(Level.WARNING, "Exception while deleting DEP token through Api..", e);
            throw e;
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, "Exception while deleting DEP token through Api..", ex);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
}
