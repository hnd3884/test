package com.me.mdm.api.core.groups;

import com.me.mdm.api.error.APIHTTPException;
import java.util.logging.Level;
import org.json.JSONObject;
import com.me.mdm.api.APIRequest;
import com.me.mdm.server.customgroup.GroupFacade;
import com.me.mdm.api.ApiRequestHandler;

public class IndividualGroupAppStatusRefreshAPIRequestHandler extends ApiRequestHandler
{
    GroupFacade groupFacade;
    
    public IndividualGroupAppStatusRefreshAPIRequestHandler() {
        this.groupFacade = new GroupFacade();
    }
    
    @Override
    public Object doPost(final APIRequest apiRequest) throws APIHTTPException {
        try {
            final JSONObject responseObject = new JSONObject();
            this.groupFacade.refreshAppStatusForDeviceGroup(apiRequest.toJSONObject());
            responseObject.put("status", 204);
            return responseObject;
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Exception in IndividualGroupAppStatusRefreshAPIRequestHandler", e);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
}
