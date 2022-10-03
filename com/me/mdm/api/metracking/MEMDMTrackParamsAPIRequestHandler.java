package com.me.mdm.api.metracking;

import org.json.JSONException;
import com.me.mdm.api.error.APIHTTPException;
import java.util.logging.Level;
import org.json.JSONObject;
import com.me.mdm.api.APIRequest;
import com.me.mdm.server.metracker.MEMDMTrackParamsFacade;
import java.util.logging.Logger;
import com.me.mdm.api.ApiRequestHandler;

public class MEMDMTrackParamsAPIRequestHandler extends ApiRequestHandler
{
    public Logger logger;
    private MEMDMTrackParamsFacade memdmTrackParamsFacade;
    
    public MEMDMTrackParamsAPIRequestHandler() {
        this.logger = Logger.getLogger("METrackLog");
        this.memdmTrackParamsFacade = null;
        this.memdmTrackParamsFacade = new MEMDMTrackParamsFacade();
    }
    
    @Override
    public Object doPost(final APIRequest apiRequest) throws APIHTTPException {
        try {
            final JSONObject requestJSON = apiRequest.toJSONObject();
            this.memdmTrackParamsFacade.incrementTrackParams(requestJSON);
            final JSONObject successJSON = new JSONObject();
            successJSON.put("success", (Object)"success");
            final JSONObject responseJSON = new JSONObject();
            responseJSON.put("status", 200);
            responseJSON.put("RESPONSE", (Object)successJSON);
            return responseJSON;
        }
        catch (final JSONException e) {
            this.logger.log(Level.SEVERE, " -- doPost()  >   Error   ", (Throwable)e);
            throw new APIHTTPException(500, null, new Object[0]);
        }
    }
}
