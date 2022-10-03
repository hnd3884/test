package com.me.mdm.api.core.users;

import com.me.mdm.api.error.APIHTTPException;
import org.json.JSONObject;
import com.me.mdm.api.APIRequest;
import com.me.mdm.api.factory.MDMRestAPIFactoryProvider;
import com.me.mdm.server.user.TechniciansFacade;
import com.me.mdm.api.ApiRequestHandler;

public class IndividualTechniciansAPIRequestHandler extends ApiRequestHandler
{
    TechniciansFacade techniciansFacade;
    
    public IndividualTechniciansAPIRequestHandler() {
        this.techniciansFacade = MDMRestAPIFactoryProvider.getTechnicianFacade();
    }
    
    @Override
    public Object doDelete(final APIRequest apiRequest) throws APIHTTPException {
        try {
            this.techniciansFacade.removeTechnicians(apiRequest.toJSONObject());
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
}
