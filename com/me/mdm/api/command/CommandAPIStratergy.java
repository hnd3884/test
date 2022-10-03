package com.me.mdm.api.command;

import org.json.JSONArray;
import org.json.JSONException;
import com.me.mdm.api.error.APIHTTPException;
import org.json.JSONObject;
import com.me.mdm.api.APIRequest;
import com.me.mdm.api.APIEndpointStratergy;

public class CommandAPIStratergy extends APIEndpointStratergy
{
    public final CommandWrapper wrapper;
    
    public CommandAPIStratergy(final CommandWrapper wrapper) {
        this.wrapper = wrapper;
    }
    
    @Override
    public Object doPost(final APIRequest apiRequest) throws APIHTTPException {
        try {
            final JSONObject responseJSON = new JSONObject();
            final JSONObject json = new CommandFacade().executeDeviceCommand(this.wrapper.toJSONWithCommand(apiRequest));
            responseJSON.put("status", (json.length() > 0) ? 200 : 202);
            responseJSON.put("RESPONSE", (Object)json);
            return responseJSON;
        }
        catch (final JSONException ex) {
            throw new APIHTTPException(500, "COM0005", new Object[0]);
        }
    }
    
    @Override
    public Object doGet(final APIRequest apiRequest) throws APIHTTPException {
        try {
            final JSONObject responseJSON = new JSONObject();
            responseJSON.put("status", 200);
            final JSONArray temp = new CommandFacade().getDeviceCommandStatus(this.wrapper.toJSONWithCommand(apiRequest));
            if (temp != null && temp.length() != 0) {
                final JSONObject status = temp.getJSONObject(0);
                switch (temp.getJSONObject(0).optInt("status")) {
                    case 2: {
                        status.put("status_code", 2);
                        status.put("status_description", (Object)"Command Success");
                        break;
                    }
                    case 1: {
                        status.put("status_code", 1);
                        status.put("status_description", (Object)"Command Initiated");
                        break;
                    }
                    case 3: {
                        status.put("status_code", 3);
                        status.put("status_description", (Object)"Command Not Initiated");
                        break;
                    }
                    case -1: {
                        status.put("status_code", -1);
                        status.put("status_description", (Object)"Command Timed Out");
                        break;
                    }
                    default: {
                        status.put("status_code", 0);
                        status.put("status_description", (Object)"Command Failed");
                        status.put("kb_url", (Object)temp.getJSONObject(0).optString("kb_url"));
                        break;
                    }
                }
                responseJSON.put("RESPONSE", (Object)status);
            }
            return responseJSON;
        }
        catch (final JSONException e) {
            throw new APIHTTPException(500, "COM0005", new Object[0]);
        }
    }
    
    @Override
    public Object doDelete(final APIRequest apiRequest) throws APIHTTPException {
        try {
            new CommandFacade().suspendDeviceCommand(this.wrapper.toJSONWithCommand(apiRequest));
            final JSONObject responseJSON = new JSONObject();
            responseJSON.put("status", 204);
            return responseJSON;
        }
        catch (final JSONException ex) {
            throw new APIHTTPException(500, "COM0005", new Object[0]);
        }
    }
}
