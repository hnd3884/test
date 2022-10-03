package com.me.mdm.onpremise.api.settings;

import com.me.mdm.api.error.APIHTTPException;
import org.json.JSONObject;
import com.me.mdm.api.APIRequest;
import com.me.mdm.api.ApiRequestHandler;

public class NATSettingsAPIRequestHandler extends ApiRequestHandler
{
    NATSettingsFacade natSettingsFacade;
    
    public NATSettingsAPIRequestHandler() {
        this.natSettingsFacade = new NATSettingsFacade();
    }
    
    public Object doPost(final APIRequest apiRequest) throws APIHTTPException {
        try {
            final JSONObject responseJSON = new JSONObject();
            responseJSON.put("status", 200);
            responseJSON.put("RESPONSE", (Object)this.natSettingsFacade.saveNATSettings(apiRequest.toJSONObject()));
            return responseJSON;
        }
        catch (final APIHTTPException ex) {
            throw ex;
        }
        catch (final Exception ex2) {
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    public Object doGet(final APIRequest apiRequest) throws APIHTTPException {
        try {
            final JSONObject responseJSON = new JSONObject();
            responseJSON.put("status", 200);
            responseJSON.put("RESPONSE", (Object)this.natSettingsFacade.getNATSettings());
            return responseJSON;
        }
        catch (final APIHTTPException ex) {
            throw ex;
        }
        catch (final Exception ex2) {
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
}
