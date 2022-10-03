package com.me.mdm.api.core.csv;

import java.util.Map;
import java.util.logging.Level;
import org.json.JSONObject;
import com.me.mdm.server.device.DeviceImportFacade;
import com.me.mdm.api.APIUtil;
import com.me.mdm.api.error.APIHTTPException;
import com.me.mdm.api.APIRequest;
import java.util.logging.Logger;
import com.me.mdm.api.ApiRequestHandler;

public class DeviceImportCSVAPIRequestHandler extends ApiRequestHandler
{
    private Logger logger;
    
    public DeviceImportCSVAPIRequestHandler() {
        this.logger = Logger.getLogger("MDMLogger");
    }
    
    @Override
    public Object doPost(final APIRequest apiRequest) throws APIHTTPException {
        try {
            final JSONObject requestJSON = apiRequest.toJSONObject();
            final JSONObject body = requestJSON.getJSONObject("msg_body");
            if (!body.has("csv_file")) {
                throw new APIHTTPException("COM0005", new Object[] { "csv_file" });
            }
            final Long userID = APIUtil.getUserID(requestJSON);
            final Map responseMap = new DeviceImportFacade().validateDevices(body, userID);
            final JSONObject response = new JSONObject();
            response.put("RESPONSE", responseMap);
            response.put("status", 200);
            return response;
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, "Exception in doPost of DeviceImportCSVAPIRequestHandler", ex);
            if (ex instanceof APIHTTPException) {
                throw (APIHTTPException)ex;
            }
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
}
