package com.me.mdm.api.core.apps.addition;

import java.util.logging.Level;
import com.me.mdm.api.error.APIHTTPException;
import org.json.JSONObject;
import com.me.mdm.api.APIRequest;
import com.me.mdm.api.factory.MDMRestAPIFactoryProvider;
import com.me.mdm.server.apps.AppFacade;
import com.me.mdm.api.ApiRequestHandler;

public class AppSearchSuggestionAPIHandler extends ApiRequestHandler
{
    AppFacade appFacade;
    
    public AppSearchSuggestionAPIHandler() {
        this.appFacade = MDMRestAPIFactoryProvider.getAppFacade();
    }
    
    @Override
    public Object doGet(final APIRequest apiRequest) throws APIHTTPException {
        try {
            final JSONObject responseDetails = new JSONObject();
            responseDetails.put("RESPONSE", (Object)this.appFacade.getAppSuggestion(apiRequest.toJSONObject()));
            responseDetails.put("status", 200);
            return responseDetails;
        }
        catch (final Exception ex) {
            if (ex instanceof APIHTTPException) {
                this.logger.log(Level.SEVERE, "API exception in app search suggestion ", ex);
                throw (APIHTTPException)ex;
            }
            this.logger.log(Level.SEVERE, "Exception in app search suggestion ", ex);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
}
