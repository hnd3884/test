package com.me.mdm.server.profiles.config;

import org.json.JSONArray;
import com.adventnet.persistence.DataObject;
import com.me.mdm.api.error.APIHTTPException;
import org.json.JSONException;
import java.util.logging.Level;
import org.json.JSONObject;

public class ChromeWifiConfigHandler extends DefaultConfigHandler
{
    public static final int SECURITY_TYPE_NONE = 0;
    public static final int SECURITY_TYPE_WEP = 1;
    public static final int SECURITY_TYPE_WPA = 2;
    public static final int SECURITY_TYPE_WEP_ENTERPRISE = 4;
    public static final int SECURITY_TYPE_WPA_ENTERPRISE = 5;
    
    @Override
    public JSONObject apiJSONToServerJSON(final String configName, final JSONObject apiJSON) throws APIHTTPException {
        final JSONObject result = super.apiJSONToServerJSON(configName, apiJSON);
        try {
            final int securityType = result.getInt("security_type");
            if (securityType == 0 || securityType == 1 || securityType == 2) {
                result.put("SUB_CONFIG", (Object)"WIFI_NON_ENTERPRISE");
            }
            else if (securityType == 4 || securityType == 5) {
                result.put("SUB_CONFIG", (Object)"WIFI_ENTERPRISE");
            }
        }
        catch (final JSONException e) {
            this.logger.log(Level.SEVERE, "Exception occurred in apiJSONToServerJSON", (Throwable)e);
        }
        return result;
    }
    
    @Override
    protected JSONArray DOToAPIJSON(final DataObject dataObject, final String configName, final String tableName) throws APIHTTPException {
        try {
            final JSONArray result = super.DOToAPIJSON(dataObject, configName, tableName);
            for (int index = 0; index < result.length(); ++index) {
                final JSONObject jsonObject = result.getJSONObject(index);
                final int securityType = jsonObject.getInt("security_type");
                if (securityType == 0 || securityType == 1 || securityType == 2) {
                    if (jsonObject.has("wifi_enterprise")) {
                        jsonObject.remove("wifi_enterprise");
                    }
                }
                else if ((securityType == 4 || securityType == 5) && jsonObject.has("wifi_non_enterprise")) {
                    jsonObject.remove("wifi_non_enterprise");
                }
            }
            return result;
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Exception occurred in DOToServerJSON", e);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    @Override
    public void validateServerJSON(final JSONObject serverJSON) throws APIHTTPException {
        try {
            final int count = this.getCountWifiSSID(serverJSON);
            if (count > 0) {
                throw new APIHTTPException("COM0015", new Object[] { "WiFi with same SSID already exists in this profile" });
            }
        }
        catch (final APIHTTPException e) {
            this.logger.log(Level.SEVERE, "Exception while validation", e);
            throw e;
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, "Count Exception", ex);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
}
