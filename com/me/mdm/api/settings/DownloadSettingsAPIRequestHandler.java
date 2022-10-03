package com.me.mdm.api.settings;

import org.json.JSONException;
import com.me.mdm.api.error.APIHTTPException;
import java.util.logging.Level;
import org.json.JSONObject;
import com.me.mdm.api.APIRequest;
import com.me.mdm.api.ApiRequestHandler;

public class DownloadSettingsAPIRequestHandler extends ApiRequestHandler
{
    DownloadSettingsFacade downloadAgentSettingsFacade;
    
    public DownloadSettingsAPIRequestHandler() {
        this.downloadAgentSettingsFacade = DownloadSettingsFacade.getInstance();
    }
    
    @Override
    public JSONObject doGet(final APIRequest apiRequest) throws APIHTTPException {
        try {
            final JSONObject response = new JSONObject();
            response.put("status", 200);
            response.put("RESPONSE", (Object)this.downloadAgentSettingsFacade.getDownloadSettingsForAgent(apiRequest.toJSONObject()));
            return response;
        }
        catch (final JSONException ex) {
            this.logger.log(Level.SEVERE, "Exception in getting download agent settings", (Throwable)ex);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    @Override
    public JSONObject doPut(final APIRequest apiRequest) throws APIHTTPException {
        try {
            final JSONObject response = new JSONObject();
            response.put("status", 204);
            this.downloadAgentSettingsFacade.updateDownloadSettingsForAgent(apiRequest.toJSONObject());
            return response;
        }
        catch (final JSONException ex) {
            this.logger.log(Level.SEVERE, "Exception in updating download agent settings", (Throwable)ex);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
}
