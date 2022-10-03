package com.me.mdm.api.command.stageddevice;

import org.json.JSONException;
import com.me.mdm.api.error.APIHTTPException;
import java.util.logging.Level;
import org.json.JSONObject;
import com.me.mdm.api.command.CommandFacade;
import com.me.mdm.api.APIRequest;
import com.me.mdm.api.ApiRequestHandler;

public class StagedDeviceAssignUserAPIRequestHandler extends ApiRequestHandler
{
    @Override
    public Object doPut(final APIRequest apiRequest) throws APIHTTPException {
        try {
            new CommandFacade().assignUser(apiRequest.toJSONObject());
            final JSONObject responseJSON = new JSONObject();
            responseJSON.put("status", 204);
            return responseJSON;
        }
        catch (final JSONException ex) {
            this.logger.log(Level.SEVERE, "Exception occurred in assignUser", (Throwable)ex);
            throw new APIHTTPException(500, "COM0005", new Object[0]);
        }
    }
}
