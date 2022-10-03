package com.me.mdm.api.command.device;

import org.json.JSONArray;
import com.adventnet.sym.server.mdm.inv.MDMInvDataPopulator;
import com.me.devicemanagement.framework.server.common.ErrorCodeHandler;
import com.me.mdm.api.APIUtil;
import org.json.JSONException;
import com.me.mdm.api.error.APIHTTPException;
import org.json.JSONObject;
import com.me.mdm.api.command.CommandFacade;
import com.me.mdm.api.APIRequest;
import com.me.mdm.api.ApiRequestHandler;

public class DeviceScanAPIRequestHandler extends ApiRequestHandler
{
    DeviceCommandWrapper wrapper;
    
    public DeviceScanAPIRequestHandler() {
        this.wrapper = new DeviceCommandWrapper();
    }
    
    @Override
    public Object doPost(final APIRequest apiRequest) throws APIHTTPException {
        try {
            new CommandFacade().scanDevices(this.wrapper.toJSONWithCommand(apiRequest));
            final JSONObject responseJSON = new JSONObject();
            responseJSON.put("status", 202);
            return responseJSON;
        }
        catch (final JSONException ex) {
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    @Override
    public Object doGet(final APIRequest apiRequest) throws APIHTTPException {
        try {
            final JSONObject responseJSON = new JSONObject();
            responseJSON.put("status", 200);
            final Long deviceId = APIUtil.getResourceID(apiRequest.toJSONObject(), "device_id");
            final JSONArray temp = new CommandFacade().getDeviceScanStatus(this.wrapper.toJSONWithCommand(apiRequest));
            if (temp != null && temp.length() != 0) {
                final JSONObject status = new JSONObject();
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
                    case 4: {
                        status.put("status_code", 4);
                        status.put("status_description", (Object)"Command In Progress");
                        break;
                    }
                    default: {
                        status.put("status_code", 0);
                        status.put("status_description", (Object)"Command Failed");
                        status.put("kb_url", (Object)ErrorCodeHandler.getInstance().getKBURL((long)MDMInvDataPopulator.getInstance().getDeviceScanToErrCode(deviceId)));
                        break;
                    }
                }
                responseJSON.put("RESPONSE", (Object)status);
            }
            return responseJSON;
        }
        catch (final JSONException ex) {
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
}
