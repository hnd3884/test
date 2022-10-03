package com.me.mdm.api.settings;

import com.me.mdm.server.factory.MDMRebrandAPI;
import com.me.mdm.api.error.APIHTTPException;
import java.util.logging.Level;
import com.me.mdm.server.factory.MDMApiFactoryProvider;
import org.json.JSONObject;
import com.me.mdm.api.APIRequest;
import java.util.logging.Logger;

public class RebrandSettingsFacade
{
    public Logger logger;
    
    public RebrandSettingsFacade() {
        this.logger = Logger.getLogger(RebrandSettingsFacade.class.getName());
    }
    
    public JSONObject getRebrandSettings(final APIRequest apiRequest) {
        try {
            final MDMRebrandAPI rebrandAPI = MDMApiFactoryProvider.getRebrandAPI();
            JSONObject rebrandSettings = new JSONObject();
            rebrandSettings = rebrandAPI.getRebrandSettings(apiRequest);
            if (String.valueOf(rebrandSettings.get("company_name")).equals("ManageEngine")) {
                rebrandSettings.put("company_name", (Object)" ");
            }
            return rebrandSettings;
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, "error in getting RebrandSettings details...", ex);
            if (ex instanceof APIHTTPException) {
                throw (APIHTTPException)ex;
            }
            throw new APIHTTPException("REBRAND001", new Object[0]);
        }
    }
    
    public JSONObject saveRebrandSettings(final APIRequest apiRequest) {
        JSONObject rebrandSettings = new JSONObject();
        try {
            final MDMRebrandAPI rebrandAPI = MDMApiFactoryProvider.getRebrandAPI();
            rebrandSettings = rebrandAPI.saveRebrandSettings(apiRequest);
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, "error in Save RebrandSettings", ex);
            if (ex instanceof APIHTTPException) {
                throw (APIHTTPException)ex;
            }
            throw new APIHTTPException("REBRAND002", new Object[0]);
        }
        return rebrandSettings;
    }
}
