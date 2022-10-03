package com.me.mdm.api.core.users;

import com.me.mdm.api.error.APIHTTPException;
import java.util.logging.Level;
import org.json.JSONObject;
import java.util.logging.Logger;
import com.me.mdm.api.APIRequest;
import com.me.mdm.api.factory.MDMRestAPIFactoryProvider;
import com.me.mdm.api.APIEndpointStratergy;
import com.me.mdm.api.MickeyViewStratergy;
import com.me.mdm.server.user.TechniciansFacade;
import com.me.mdm.api.ApiRequestHandler;

public class TechniciansAPIRequestHandler extends ApiRequestHandler
{
    private TechniciansFacade techniciansFacade;
    
    public TechniciansAPIRequestHandler() {
        super(new MickeyViewStratergy());
        this.techniciansFacade = MDMRestAPIFactoryProvider.getTechnicianFacade();
    }
    
    @Override
    public Object doGet(final APIRequest apiRequest) throws APIHTTPException {
        final Logger LOGGER = Logger.getLogger(TechniciansAPIRequestHandler.class.getName());
        try {
            final JSONObject message = apiRequest.toJSONObject();
            final JSONObject res = this.techniciansFacade.getTechnicians(message);
            final JSONObject response = new JSONObject();
            response.put("status", 200);
            response.put("RESPONSE", (Object)res);
            return response;
        }
        catch (final Exception e) {
            LOGGER.log(Level.SEVERE, "Exception while get users :", e);
            if (e instanceof APIHTTPException) {
                throw (APIHTTPException)e;
            }
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    @Override
    public Object doPost(final APIRequest apiRequest) throws APIHTTPException {
        final Logger LOGGER = Logger.getLogger(TechniciansAPIRequestHandler.class.getName());
        try {
            this.techniciansFacade.addTechnicians(apiRequest.toJSONObject());
            final JSONObject responseJSON = new JSONObject();
            responseJSON.put("status", 202);
            return responseJSON;
        }
        catch (final APIHTTPException ex) {
            LOGGER.log(Level.SEVERE, "Exception while processing addUser :", ex);
            throw ex;
        }
        catch (final Exception ex2) {
            LOGGER.log(Level.SEVERE, "Exception while processing addUser :", ex2);
            throw new APIHTTPException(400, null, new Object[0]);
        }
    }
    
    @Override
    public Object doPut(final APIRequest apiRequest) throws APIHTTPException {
        try {
            this.techniciansFacade.updateTechnicians(apiRequest.toJSONObject());
            final JSONObject responseJSON = new JSONObject();
            responseJSON.put("status", 202);
            return responseJSON;
        }
        catch (final APIHTTPException ex) {
            throw ex;
        }
        catch (final Exception ex2) {
            throw new APIHTTPException(400, null, new Object[0]);
        }
    }
    
    @Override
    public Object doDelete(final APIRequest apiRequest) throws APIHTTPException {
        try {
            this.techniciansFacade.removeTechnicians(apiRequest.toJSONObject());
            final JSONObject responseJSON = new JSONObject();
            responseJSON.put("status", 204);
            return responseJSON;
        }
        catch (final APIHTTPException ex) {
            throw ex;
        }
        catch (final Exception ex2) {
            throw new APIHTTPException(400, null, new Object[0]);
        }
    }
}
