package com.me.mdm.api.core.groups;

import org.json.JSONException;
import com.me.mdm.api.error.APIHTTPException;
import java.util.logging.Level;
import org.json.JSONObject;
import com.me.mdm.api.APIRequest;
import com.me.mdm.server.apps.AppFacade;
import java.util.logging.Logger;
import com.me.mdm.api.ApiRequestHandler;

public class IndividualGroupAppAPIRequestHandler extends ApiRequestHandler
{
    private Logger logger;
    protected AppFacade appFacade;
    
    public IndividualGroupAppAPIRequestHandler() {
        this.logger = Logger.getLogger("MDMConfigLogger");
        this.appFacade = new AppFacade();
    }
    
    @Override
    public Object doGet(final APIRequest apiRequest) throws APIHTTPException {
        final JSONObject request = apiRequest.toJSONObject();
        try {
            final JSONObject response = new JSONObject();
            response.put("status", 200);
            response.put("RESPONSE", (Object)this.appFacade.getGroupAppDetail(request));
            return response;
        }
        catch (final JSONException e) {
            this.logger.log(Level.SEVERE, "jsonexception", (Throwable)e);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
}
