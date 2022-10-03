package com.me.mdm.api.command.bulk;

import org.json.JSONException;
import com.me.mdm.api.error.APIHTTPException;
import org.json.JSONObject;
import com.me.mdm.api.command.CommandFacade;
import com.me.mdm.api.APIRequest;
import com.me.mdm.api.ApiRequestHandler;

public class BulkCommandAPIRequestHandler extends ApiRequestHandler
{
    @Override
    public Object doPost(final APIRequest apiRequest) throws APIHTTPException {
        try {
            final String commandName = new CommandFacade().getCommandNameFromAPIRequest(apiRequest);
            final JSONObject requestJSON = apiRequest.toJSONObject();
            requestJSON.put("Command", (Object)commandName);
            new CommandFacade().executeBulkDeviceCommand(requestJSON);
            final JSONObject responseJSON = new JSONObject();
            responseJSON.put("status", 202);
            return responseJSON;
        }
        catch (final JSONException ex) {
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
}
