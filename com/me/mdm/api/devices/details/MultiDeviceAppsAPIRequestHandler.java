package com.me.mdm.api.devices.details;

import org.json.JSONObject;
import org.json.JSONException;
import com.me.mdm.api.error.APIHTTPException;
import java.util.logging.Level;
import com.adventnet.sym.server.mdm.util.JSONUtil;
import com.me.mdm.server.apps.AppFacade;
import com.me.mdm.api.APIRequest;
import com.me.mdm.api.ApiRequestHandler;

public class MultiDeviceAppsAPIRequestHandler extends ApiRequestHandler
{
    @Override
    public Object doPost(final APIRequest apiRequest) throws APIHTTPException {
        try {
            final JSONObject request = apiRequest.toJSONObject();
            request.getJSONObject("msg_header").getJSONObject("resource_identifier").remove("device_id");
            new AppFacade().associateAppsToDevices(request);
            return JSONUtil.toJSON("status", 204);
        }
        catch (final JSONException ex) {
            this.logger.log(Level.SEVERE, "Exception in POST /devices/apps", (Throwable)ex);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
}
