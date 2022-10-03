package com.me.mdm.api.core.misc;

import com.me.mdm.server.tracker.mics.MICSDataAPI;
import org.json.JSONException;
import com.me.mdm.api.error.APIHTTPException;
import org.json.JSONObject;
import com.me.mdm.server.factory.MDMApiFactoryProvider;
import java.util.logging.Level;
import com.me.mdm.api.APIRequest;
import java.util.logging.Logger;
import com.me.mdm.api.ApiRequestHandler;

public class MicsTrackingAPIRequestHandler extends ApiRequestHandler
{
    private Logger logger;
    
    public MicsTrackingAPIRequestHandler() {
        this.logger = Logger.getLogger("MDMLogger");
    }
    
    @Override
    public Object doPost(final APIRequest apiRequest) throws APIHTTPException {
        try {
            this.logger.log(Level.INFO, "Mics Tracking request start");
            final MICSDataAPI micsDataAPI = MDMApiFactoryProvider.getMicsTrackingAPI();
            micsDataAPI.addData(apiRequest);
            final JSONObject response = new JSONObject();
            response.put("status", 202);
            this.logger.log(Level.INFO, "Mics Tracking request end...");
            return response;
        }
        catch (final JSONException e) {
            this.logger.log(Level.SEVERE, "Exception occurred in MicsTrackingAPIRequestHandler", (Throwable)e);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
}
