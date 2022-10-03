package com.me.mdm.api.core.apps.config.feedback;

import com.me.mdm.api.error.APIHTTPException;
import java.util.logging.Level;
import org.json.JSONObject;
import com.me.mdm.api.APIRequest;
import com.me.mdm.server.apps.config.AppConfigFacade;
import com.me.mdm.api.ApiRequestHandler;

public class IndividualAppToDeviceFeedbackAPIRequestHandler extends ApiRequestHandler
{
    AppConfigFacade appConfigFacade;
    
    public IndividualAppToDeviceFeedbackAPIRequestHandler() {
        this.appConfigFacade = AppConfigFacade.getInstance();
    }
    
    @Override
    public JSONObject doGet(final APIRequest apiRequest) throws APIHTTPException {
        try {
            final JSONObject response = new JSONObject();
            response.put("status", 200);
            response.put("RESPONSE", (Object)this.appConfigFacade.getSpecificAppConfigForDevice(apiRequest.toJSONObject()));
            return response;
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, "Exception getting individual app feedback");
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
}
