package com.me.mdm.server.profiles.config;

import com.adventnet.persistence.Row;
import com.me.mdm.api.error.APIHTTPException;
import java.util.logging.Level;
import org.json.JSONObject;
import org.json.JSONArray;
import com.adventnet.persistence.DataObject;

public class DataUsageActionConfigHandler extends DefaultConfigHandler
{
    @Override
    protected JSONArray DOToAPIJSON(final DataObject dataObject, final String configName, final String tableName) throws APIHTTPException {
        try {
            if (!dataObject.isEmpty() && tableName != null && !tableName.isEmpty()) {
                final JSONArray result = super.DOToAPIJSON(dataObject, configName, tableName);
                if (result != null && result.length() != 0) {
                    final JSONObject jsonObject = result.getJSONObject(0);
                    final Row row = dataObject.getRow("DataTrackingSSID");
                    final String ssid = (String)row.get("SSID");
                    final Integer type = (Integer)row.get("TYPE");
                    final JSONObject ssidJSON = new JSONObject();
                    ssidJSON.put("ssid", (Object)ssid);
                    ssidJSON.put("type", (Object)type);
                    jsonObject.put("tracking_ssid", (Object)ssidJSON);
                }
                return result;
            }
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "exception occurred in DOtoAPIJSON WindowsKioskConfigHandler", e);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
        return null;
    }
    
    @Override
    protected void checkAndAddInnerJSON(final JSONObject configJSON, final DataObject dataObject, final String configName) throws Exception {
    }
}
