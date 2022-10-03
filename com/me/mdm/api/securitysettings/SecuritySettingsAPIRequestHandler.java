package com.me.mdm.api.securitysettings;

import com.me.mdm.api.error.APIHTTPException;
import org.json.JSONObject;
import com.me.mdm.api.APIRequest;
import com.adventnet.sym.server.mdm.gdpr.GDPRSettingsFacade;
import com.me.mdm.api.ApiRequestHandler;

public class SecuritySettingsAPIRequestHandler extends ApiRequestHandler
{
    GDPRSettingsFacade gdprSettingsFacade;
    
    public SecuritySettingsAPIRequestHandler() {
        this.gdprSettingsFacade = new GDPRSettingsFacade();
    }
    
    @Override
    public Object doPost(final APIRequest apiRequest) throws APIHTTPException {
        try {
            final JSONObject responseJSON = new JSONObject();
            responseJSON.put("status", 200);
            responseJSON.put("RESPONSE", (Object)this.gdprSettingsFacade.saveSecuritySettings(apiRequest.toJSONObject()));
            return responseJSON;
        }
        catch (final Exception ex) {
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    @Override
    public Object doGet(final APIRequest apiRequest) throws APIHTTPException {
        try {
            final JSONObject responseJSON = new JSONObject();
            responseJSON.put("status", 200);
            responseJSON.put("RESPONSE", (Object)this.gdprSettingsFacade.getSecuritySettings(apiRequest.toJSONObject()));
            return responseJSON;
        }
        catch (final Exception ex) {
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    @Override
    public Object doPut(final APIRequest apiRequest) throws APIHTTPException {
        try {
            final JSONObject responseJSON = new JSONObject();
            responseJSON.put("status", 200);
            responseJSON.put("RESPONSE", (Object)this.gdprSettingsFacade.toggleGdprWidget(apiRequest.toJSONObject()));
            return responseJSON;
        }
        catch (final Exception ex) {
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
}
