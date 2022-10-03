package com.me.mdm.api.stageddevices.details;

import org.json.JSONException;
import com.me.mdm.api.error.APIHTTPException;
import org.json.JSONObject;
import com.me.mdm.api.APIRequest;
import com.me.mdm.server.stageddevice.ModernMgmtStagedDeviceFacade;
import com.me.mdm.api.ApiRequestHandler;

public class ModernMgmtStagedDeviceListAPIRequestHandler extends ApiRequestHandler
{
    ModernMgmtStagedDeviceFacade modernMgmtStagedDeviceFacade;
    
    public ModernMgmtStagedDeviceListAPIRequestHandler() {
        this.modernMgmtStagedDeviceFacade = null;
    }
    
    @Override
    public Object doGet(final APIRequest apiRequest) throws APIHTTPException {
        this.initialize(apiRequest);
        JSONObject responseJSON = null;
        final JSONObject requestJSON = apiRequest.toJSONObject();
        try {
            responseJSON = new JSONObject();
            responseJSON.put("status", 200);
            responseJSON.put("RESPONSE", (Object)this.modernMgmtStagedDeviceFacade.getModernMgmtDeviceDetails(requestJSON.getJSONObject("msg_header").optJSONObject("filters")));
        }
        catch (final JSONException ex) {
            throw new APIHTTPException("COM0004", new Object[0]);
        }
        return responseJSON;
    }
    
    @Override
    public Object doPost(final APIRequest apiRequest) throws APIHTTPException {
        this.initialize(apiRequest);
        JSONObject responseJSON = null;
        try {
            responseJSON = new JSONObject();
            responseJSON.put("status", 200);
            responseJSON.put("RESPONSE", (Object)this.modernMgmtStagedDeviceFacade.addOrUpdateDeviceStagedForModernMgmt(apiRequest.toJSONObject()));
        }
        catch (final JSONException ex) {
            throw new APIHTTPException("COM0004", new Object[0]);
        }
        catch (final Exception e) {
            throw new APIHTTPException("COM0004", new Object[0]);
        }
        return responseJSON;
    }
    
    @Override
    public Object doDelete(final APIRequest apiRequest) throws APIHTTPException {
        this.initialize(apiRequest);
        JSONObject responseJSON = null;
        try {
            responseJSON = new JSONObject();
            responseJSON.put("status", 200);
            responseJSON.put("RESPONSE", (Object)this.modernMgmtStagedDeviceFacade.deleteDeviceStagedForModernMgmt(apiRequest.toJSONObject()));
        }
        catch (final JSONException ex) {
            throw new APIHTTPException("COM0004", new Object[0]);
        }
        catch (final Exception ex2) {
            throw new APIHTTPException("COM0004", new Object[0]);
        }
        return responseJSON;
    }
    
    private void initialize(final APIRequest apiRequest) {
        final Integer platformType = Integer.valueOf(apiRequest.pathInfo.substring(apiRequest.pathInfo.lastIndexOf("/") + 1));
        this.modernMgmtStagedDeviceFacade = ModernMgmtStagedDeviceFacade.getInstance(platformType);
    }
}
