package com.me.mdm.api.settings;

import org.json.JSONException;
import com.me.mdm.api.error.APIHTTPException;
import java.util.logging.Level;
import com.me.mdm.api.APIUtil;
import org.json.JSONObject;
import com.me.mdm.server.settings.DownloadSettingsHandler;
import java.util.logging.Logger;

public class DownloadSettingsFacade
{
    Logger logger;
    private static DownloadSettingsFacade downloadAgentSettingsFacade;
    private static DownloadSettingsHandler downloadAgentSettingsHandler;
    
    public DownloadSettingsFacade() {
        this.logger = Logger.getLogger("DownloadSettingsFacade");
    }
    
    public static DownloadSettingsFacade getInstance() {
        if (DownloadSettingsFacade.downloadAgentSettingsFacade == null) {
            DownloadSettingsFacade.downloadAgentSettingsFacade = new DownloadSettingsFacade();
        }
        return DownloadSettingsFacade.downloadAgentSettingsFacade;
    }
    
    public JSONObject getDownloadSettingsForAgent(final JSONObject apiRequest) throws APIHTTPException {
        try {
            final Long customerID = APIUtil.getCustomerID(apiRequest);
            return DownloadSettingsFacade.downloadAgentSettingsHandler.getDownloadSettingsForAgent(customerID);
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, "Exception in getDownloadAgentSettings ", ex);
            if (ex instanceof APIHTTPException) {
                throw (APIHTTPException)ex;
            }
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    public void updateDownloadSettingsForAgent(final JSONObject apiRequest) throws APIHTTPException {
        try {
            JSONObject requestJSON;
            try {
                requestJSON = apiRequest.getJSONObject("msg_body");
            }
            catch (final JSONException e) {
                throw new APIHTTPException("COM0006", new Object[0]);
            }
            requestJSON.put("msg_header", (Object)apiRequest.getJSONObject("msg_header"));
            this.preFillData(requestJSON);
            DownloadSettingsFacade.downloadAgentSettingsHandler.addOrUpdateDownloadSettingsForAgent(requestJSON);
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, "Exception in updateDownloadAgentSettings", ex);
            if (ex instanceof APIHTTPException) {
                throw (APIHTTPException)ex;
            }
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    private void preFillData(final JSONObject requestJSON) throws Exception {
        final Long customerID = APIUtil.getCustomerID(requestJSON);
        final JSONObject existingJSON = DownloadSettingsHandler.getInstance().getDownloadSettingsForAgent(customerID);
        requestJSON.put("max_retry_count", requestJSON.optInt("max_retry_count", existingJSON.getInt("MAX_RETRY_COUNT")));
        requestJSON.put("min_retry_delay", requestJSON.optLong("min_retry_delay", existingJSON.getLong("MIN_RETRY_DELAY")));
        requestJSON.put("max_retry_delay", requestJSON.optLong("max_retry_delay", existingJSON.getLong("MAX_RETRY_DELAY")));
        requestJSON.put("delay_random", requestJSON.optLong("delay_random", existingJSON.getLong("DELAY_RANDOM")));
        if (!requestJSON.has("excluded_domain")) {
            requestJSON.put("excluded_domain", (Object)existingJSON.getJSONArray("EXCLUDED_DOMAIN"));
        }
        requestJSON.put("custom_retry_delay", requestJSON.optLong("custom_retry_delay", existingJSON.getLong("CUSTOM_RETRY_DELAY")));
    }
    
    static {
        DownloadSettingsFacade.downloadAgentSettingsFacade = null;
        DownloadSettingsFacade.downloadAgentSettingsHandler = DownloadSettingsHandler.getInstance();
    }
}
