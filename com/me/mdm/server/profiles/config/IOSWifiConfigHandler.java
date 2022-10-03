package com.me.mdm.server.profiles.config;

import com.dd.plist.NSString;
import org.apache.commons.lang.StringUtils;
import com.dd.plist.NSArray;
import com.adventnet.persistence.Row;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import org.json.JSONArray;
import com.adventnet.persistence.DataObject;
import org.json.JSONException;
import java.util.logging.Level;
import com.me.mdm.api.error.APIHTTPException;
import org.json.JSONObject;

public class IOSWifiConfigHandler extends DefaultConfigHandler
{
    public static final int SECURITY_TYPE_NONE = 0;
    public static final int SECURITY_TYPE_WEP = 1;
    public static final int SECURITY_TYPE_WPA = 2;
    public static final int SECURITY_TYPE_ANY_PERSONAL = 3;
    public static final int SECURITY_TYPE_WEP_ENTERPRISE = 4;
    public static final int SECURITY_TYPE_WPA_ENTERPRISE = 5;
    public static final int SECURITY_TYPE_ANY_ENTERPRISE = 6;
    public static final int SECURITY_TYPE_WPA3_PERSONAL = 7;
    public static final String SETUP_MODE_SYSTEM = "System";
    public static final String SETUP_MODE_LOGINWINDOW = "Loginwindow";
    
    @Override
    public JSONObject apiJSONToServerJSON(final String configName, final JSONObject apiJSON) throws APIHTTPException {
        final JSONObject result = super.apiJSONToServerJSON(configName, apiJSON);
        try {
            final int securityType = result.getInt("security_type");
            if (securityType == 0 || securityType == 1 || securityType == 2 || securityType == 3 || securityType == 7) {
                result.put("SUB_CONFIG", (Object)"WIFI_NON_ENTERPRISE");
                if (!result.has("WIFI_NON_ENTERPRISE") && securityType != 0) {
                    throw new APIHTTPException("COM0005", new Object[0]);
                }
            }
            else if (securityType == 4 || securityType == 5 || securityType == 6) {
                result.put("SUB_CONFIG", (Object)"WIFI_ENTERPRISE");
                if (!result.has("WIFI_ENTERPRISE")) {
                    throw new APIHTTPException("COM0005", new Object[0]);
                }
            }
        }
        catch (final JSONException e) {
            this.logger.log(Level.SEVERE, "Exception occurred in apiJSONToServerJSON", (Throwable)e);
            throw new APIHTTPException("COM0005", new Object[0]);
        }
        return result;
    }
    
    @Override
    protected JSONArray DOToAPIJSON(final DataObject dataObject, final String configName, final String tableName) throws APIHTTPException {
        try {
            final JSONArray result = super.DOToAPIJSON(dataObject, configName, tableName);
            for (int index = 0; index < result.length(); ++index) {
                final JSONObject jsonObject = result.getJSONObject(index);
                final Long policyID = jsonObject.getLong("payload_id");
                final int securityType = jsonObject.getInt("security_type");
                if (securityType == 0 || securityType == 1 || securityType == 2 || securityType == 3) {
                    if (jsonObject.has("wifi_enterprise")) {
                        jsonObject.remove("wifi_enterprise");
                    }
                }
                else if ((securityType == 4 || securityType == 5 || securityType == 6) && jsonObject.has("wifi_non_enterprise")) {
                    jsonObject.remove("wifi_non_enterprise");
                }
                this.addAppleSpecificDetails(dataObject, jsonObject, policyID);
            }
            return result;
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Exception occurred in DOToServerJSON", e);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    private void addAppleSpecificDetails(final DataObject dataObject, final JSONObject jsonObject, final Long policyID) throws Exception {
        if (!dataObject.containsTable("AppleWifiPolicy")) {
            return;
        }
        final Row row = dataObject.getRow("AppleWifiPolicy", new Criteria(Column.getColumn("AppleWifiPolicy", "CONFIG_DATA_ITEM_ID"), (Object)policyID, 0));
        if (row == null) {
            return;
        }
        final Integer setupModes = (Integer)row.get("SETUP_MODES");
        final Boolean disableMACRandomization = (Boolean)row.get("DISABLE_MAC_RANDOMIZE");
        jsonObject.put("DISABLE_MAC_RANDOMIZE", (Object)disableMACRandomization);
        if (setupModes != null) {
            jsonObject.put("SETUP_MODES", (Object)setupModes);
        }
    }
    
    @Override
    public void validateServerJSON(final JSONObject serverJSON) throws APIHTTPException {
        try {
            final int count = this.getCountWifiSSID(serverJSON);
            if (count > 0) {
                throw new APIHTTPException("COM0015", new Object[] { "WiFi with same SSID already exists in this profile" });
            }
            if (serverJSON.has("PROXY_TYPE")) {
                final Integer proxyType = serverJSON.getInt("PROXY_TYPE");
                if (proxyType == 1) {
                    serverJSON.get("PROXY_SERVER");
                    serverJSON.get("PROXY_SERVER_PORT");
                }
                else if (proxyType == 2) {
                    serverJSON.get("PROXY_PAC_URL");
                }
            }
        }
        catch (final APIHTTPException e) {
            this.logger.log(Level.SEVERE, "Exception while validation", e);
            throw e;
        }
        catch (final JSONException e2) {
            this.logger.log(Level.SEVERE, "Invalid json", (Throwable)e2);
            throw new APIHTTPException("COM0005", new Object[0]);
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, "Count Exception", ex);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    public NSArray getSetupModesNSArray(final Integer setupModes) {
        if (setupModes == null || setupModes == 0) {
            return null;
        }
        final String binary = Integer.toBinaryString(setupModes);
        final NSArray array = new NSArray(StringUtils.countMatches(binary, "1"));
        final int length = binary.length();
        if (binary.charAt(length - 1) == '1') {
            array.setValue(0, (Object)new NSString("System"));
        }
        if (binary.length() > 1 && binary.charAt(length - 2) == '1') {
            array.setValue(1, (Object)new NSString("Loginwindow"));
        }
        return array;
    }
}
