package com.me.mdm.api.command;

import org.json.JSONException;
import com.me.mdm.api.error.APIHTTPException;
import org.json.JSONObject;
import com.me.mdm.api.APIRequest;

public abstract class CommandWrapper
{
    public final JSONObject toJSONWithCommand(final APIRequest apiRequest) throws APIHTTPException {
        try {
            final String temp = apiRequest.pathInfo.substring(apiRequest.pathInfo.lastIndexOf("devices/") + "devices/".length());
            String commandName = temp.substring(temp.indexOf("/") + 1);
            if (commandName.contains("actions")) {
                commandName = commandName.substring(commandName.indexOf("/") + 1);
            }
            commandName = this.getEquivalentCommandName(commandName);
            if (commandName == null) {
                throw new APIHTTPException("CMD0001", new Object[0]);
            }
            final JSONObject requestJSON = apiRequest.toJSONObject();
            final JSONObject headerJSON = requestJSON.getJSONObject("msg_header");
            final JSONObject idJSON = headerJSON.getJSONObject("resource_identifier");
            idJSON.put("command_name", (Object)commandName);
            headerJSON.put("resource_identifier", (Object)idJSON);
            requestJSON.put("msg_header", (Object)headerJSON);
            return requestJSON;
        }
        catch (final JSONException e) {
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    public abstract String getEquivalentCommandName(final String p0);
}
