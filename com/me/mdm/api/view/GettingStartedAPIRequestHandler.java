package com.me.mdm.api.view;

import org.json.JSONException;
import com.me.mdm.api.error.APIHTTPException;
import java.util.logging.Level;
import org.json.JSONObject;
import com.me.mdm.api.APIRequest;
import com.me.mdm.api.ApiRequestHandler;

public class GettingStartedAPIRequestHandler extends ApiRequestHandler
{
    private GettingStartedFacade gettingStartedFacade;
    
    public GettingStartedAPIRequestHandler() {
        this.gettingStartedFacade = new GettingStartedFacade();
    }
    
    @Override
    public Object doGet(final APIRequest apiRequest) throws APIHTTPException {
        try {
            final JSONObject responseJSON = new JSONObject();
            responseJSON.put("status", 200);
            responseJSON.put("RESPONSE", (Object)this.gettingStartedFacade.isShowGettingStarted(apiRequest.toJSONObject()));
            return responseJSON;
        }
        catch (final JSONException e) {
            this.logger.log(Level.SEVERE, "error in doGet", (Throwable)e);
            throw new APIHTTPException("COM0014", new Object[0]);
        }
    }
    
    @Override
    public Object doPost(final APIRequest apiRequest) throws APIHTTPException {
        try {
            final JSONObject responseJSON = new JSONObject();
            this.gettingStartedFacade.updateGettingStarted(apiRequest.toJSONObject());
            responseJSON.put("status", 204);
            return responseJSON;
        }
        catch (final JSONException e) {
            this.logger.log(Level.SEVERE, "error in doPost", (Throwable)e);
            throw new APIHTTPException("COM0014", new Object[0]);
        }
    }
}
