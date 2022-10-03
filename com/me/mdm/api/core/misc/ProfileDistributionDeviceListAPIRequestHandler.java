package com.me.mdm.api.core.misc;

import com.me.mdm.api.error.APIHTTPException;
import java.util.logging.Level;
import com.me.mdm.server.device.DeviceFacade;
import org.json.JSONObject;
import com.me.mdm.api.APIRequest;
import java.util.logging.Logger;
import com.me.mdm.api.ApiRequestHandler;

public class ProfileDistributionDeviceListAPIRequestHandler extends ApiRequestHandler
{
    public Logger logger;
    
    public ProfileDistributionDeviceListAPIRequestHandler() {
        this.logger = Logger.getLogger("MDMAPILogger");
    }
    
    @Override
    public Object doGet(final APIRequest apiRequest) throws APIHTTPException {
        try {
            final JSONObject requestJSON = apiRequest.toJSONObject();
            final JSONObject responseJSON = new JSONObject();
            responseJSON.put("status", 200);
            final JSONObject distributionJSON = new DeviceFacade().getDeviceDistributionListForProfile(requestJSON);
            responseJSON.put("RESPONSE", (Object)distributionJSON);
            return responseJSON;
        }
        catch (final APIHTTPException ex) {
            this.logger.log(Level.SEVERE, " -- doGet()   >   Error", ex);
            throw ex;
        }
        catch (final Exception ex2) {
            this.logger.log(Level.SEVERE, " -- doGet()   >   Error", ex2);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
}
