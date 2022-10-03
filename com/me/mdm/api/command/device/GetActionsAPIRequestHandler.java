package com.me.mdm.api.command.device;

import com.me.mdm.server.inv.actions.resource.InventoryActionList;
import org.json.JSONException;
import com.me.mdm.api.error.APIHTTPException;
import java.util.logging.Level;
import org.json.JSONObject;
import com.me.mdm.api.APIUtil;
import com.me.mdm.server.inv.actions.InvActionUtilProvider;
import com.me.mdm.server.device.resource.Device;
import com.me.mdm.server.device.DeviceFacade;
import com.me.mdm.api.APIRequest;
import com.google.gson.Gson;
import java.util.logging.Logger;
import com.me.mdm.api.ApiRequestHandler;

public class GetActionsAPIRequestHandler extends ApiRequestHandler
{
    private Logger logger;
    private Gson gson;
    
    public GetActionsAPIRequestHandler() {
        this.logger = Logger.getLogger("InventoryLogger");
        this.gson = new Gson();
    }
    
    @Override
    public Object doGet(final APIRequest apiRequest) throws APIHTTPException {
        try {
            final JSONObject request = apiRequest.toJSONObject();
            final String json = new DeviceFacade().getDevice(request).toString();
            final Device device = (Device)this.gson.fromJson(json, (Class)Device.class);
            final InventoryActionList inventoryActionList = InvActionUtilProvider.getInvActionUtil(device.getPlatformType()).getApplicableActions(device, APIUtil.getCustomerID(request));
            final JSONObject response = new JSONObject();
            response.put("RESPONSE", (Object)new JSONObject(new Gson().toJson((Object)inventoryActionList)));
            response.put("status", 200);
            return response;
        }
        catch (final JSONException e) {
            this.logger.log(Level.SEVERE, "Exception occurred in GetActionsAPIRequestHandler.doGet()", (Throwable)e);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
}
