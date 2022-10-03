package com.me.mdm.api.technician;

import com.me.mdm.api.error.APIHTTPException;
import org.json.JSONObject;
import com.me.mdm.api.APIRequest;
import com.me.mdm.api.ApiRequestHandler;

public class ActionsForTechnicianAPIRequestHandler extends ApiRequestHandler
{
    private ActionsForTechnicianFacade technicianFacade;
    
    public ActionsForTechnicianAPIRequestHandler() {
        this.technicianFacade = new ActionsForTechnicianFacade();
    }
    
    @Override
    public Object doGet(final APIRequest apiRequest) throws APIHTTPException {
        try {
            final JSONObject responseJSON = new JSONObject();
            responseJSON.put("status", 200);
            responseJSON.put("RESPONSE", (Object)this.technicianFacade.getAllowedActionsForTechnician(apiRequest.toJSONObject()));
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
