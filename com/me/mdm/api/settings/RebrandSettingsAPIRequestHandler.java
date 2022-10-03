package com.me.mdm.api.settings;

import com.me.mdm.api.error.APIHTTPException;
import java.util.logging.Level;
import org.json.JSONObject;
import com.me.mdm.api.APIRequest;
import com.me.mdm.api.ApiRequestHandler;

public class RebrandSettingsAPIRequestHandler extends ApiRequestHandler
{
    RebrandSettingsFacade rebrandSettingsFacade;
    
    public RebrandSettingsAPIRequestHandler() {
        this.rebrandSettingsFacade = new RebrandSettingsFacade();
    }
    
    @Override
    public Object doPost(final APIRequest apiRequest) throws APIHTTPException {
        try {
            final JSONObject responseJSON = new JSONObject();
            responseJSON.put("status", 200);
            responseJSON.put("RESPONSE", (Object)this.rebrandSettingsFacade.saveRebrandSettings(apiRequest));
            return responseJSON;
        }
        catch (final Exception ex) {
            this.logger.log(Level.INFO, "Exception save rebrand settings", ex);
            if (ex instanceof APIHTTPException) {
                throw (APIHTTPException)ex;
            }
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    @Override
    public Object doGet(final APIRequest apiRequest) throws APIHTTPException {
        try {
            final JSONObject responseJSON = new JSONObject();
            responseJSON.put("status", 200);
            responseJSON.put("RESPONSE", (Object)this.rebrandSettingsFacade.getRebrandSettings(apiRequest));
            return responseJSON;
        }
        catch (final Exception ex) {
            this.logger.log(Level.INFO, "Exception in fetching rebrand settings", ex);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
}
