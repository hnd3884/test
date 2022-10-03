package com.me.mdm.api.core.apps.update;

import com.me.mdm.api.error.APIHTTPException;
import com.adventnet.sym.server.mdm.util.JSONUtil;
import org.json.JSONObject;
import com.me.mdm.api.APIRequest;
import com.me.mdm.api.factory.MDMRestAPIFactoryProvider;
import com.me.mdm.server.apps.AppFacade;
import com.me.mdm.api.ApiRequestHandler;

public class IndividualAppsAutoUpdateAPIRequestHandler extends ApiRequestHandler
{
    private AppFacade app;
    
    public IndividualAppsAutoUpdateAPIRequestHandler() {
        this.app = MDMRestAPIFactoryProvider.getAppFacade();
    }
    
    @Override
    public Object doGet(final APIRequest apiRequest) throws APIHTTPException {
        try {
            JSONObject responseDetails = new JSONObject();
            final JSONObject responseObject = this.app.getAutoAppUpdateInfoForApp(apiRequest.toJSONObject());
            if (responseObject != null) {
                responseDetails.put("status", 200);
                responseDetails.put("RESPONSE", (Object)this.app.getAutoAppUpdateInfoForApp(apiRequest.toJSONObject()));
            }
            else {
                responseDetails = JSONUtil.toJSON("status", 204);
            }
            return responseDetails;
        }
        catch (final Exception ex) {
            if (ex instanceof APIHTTPException) {
                throw (APIHTTPException)ex;
            }
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
}
