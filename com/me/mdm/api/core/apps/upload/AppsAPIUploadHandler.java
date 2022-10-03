package com.me.mdm.api.core.apps.upload;

import java.util.logging.Level;
import com.me.mdm.api.error.APIHTTPException;
import org.json.JSONObject;
import com.me.mdm.api.APIRequest;
import com.me.mdm.api.factory.MDMRestAPIFactoryProvider;
import com.me.mdm.api.APIEndpointStratergy;
import com.me.mdm.api.MickeyViewStratergy;
import com.me.mdm.server.apps.AppFacade;
import com.me.mdm.api.ApiRequestHandler;

public class AppsAPIUploadHandler extends ApiRequestHandler
{
    AppFacade app;
    
    public AppsAPIUploadHandler() {
        super(new MickeyViewStratergy());
        this.app = MDMRestAPIFactoryProvider.getAppFacade();
    }
    
    @Override
    public Object doPost(final APIRequest apiRequest) throws APIHTTPException {
        try {
            final JSONObject responseDetails = new JSONObject();
            responseDetails.put("status", 200);
            final JSONObject uploadedAppDetails = this.app.getUploadedAppDetails(apiRequest.toJSONObject());
            uploadedAppDetails.remove("msg_header");
            responseDetails.put("RESPONSE", (Object)uploadedAppDetails);
            return responseDetails;
        }
        catch (final Exception ex) {
            if (ex instanceof APIHTTPException) {
                throw (APIHTTPException)ex;
            }
            this.logger.log(Level.SEVERE, "Exception in POST /apps/blob", ex);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
}
