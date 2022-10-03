package com.me.mdm.server.profiles.config;

import java.util.List;
import java.util.Collection;
import java.util.ArrayList;
import java.util.Arrays;
import com.me.mdm.api.error.APIHTTPException;
import org.json.JSONException;
import java.util.logging.Level;
import org.json.JSONArray;
import com.adventnet.persistence.DataObject;
import org.json.JSONObject;

public class ChromeVPNConfigHandler extends VPNConfigHandler
{
    public static final int CHROME_L2TP_VPN_TYPE = 0;
    public static final int CHROME_OPEN_VPN_TYPE = 13;
    public static final String CHROME_API_VPN_TYPE_L2TP = "l2tp";
    public static final String CHROME_API_VPN_TYPE_OPEN_VPN_POLICY = "open_vpn_policy";
    public static final String CHROME_VPN_TYPE_OPEN_VPN_POLICY = "OPEN_VPN_POLICY";
    
    @Override
    protected void checkAndAddInnerJSON(final JSONObject configJSON, final DataObject dataObject, final String configName) throws Exception {
        if (configJSON.has("payload_id")) {
            final JSONArray templateConfigProperties = ProfileConfigurationUtil.getInstance().getPayloadConfigurationProperties(configName);
            this.constructInnerJsonValue(templateConfigProperties, configJSON, dataObject, configName);
        }
    }
    
    @Override
    public JSONObject apiJSONToServerJSON(final String configName, final JSONObject apiJSON) throws APIHTTPException {
        this.logger.log(Level.INFO, "Started converting API JSON to Server JSON...");
        try {
            final JSONObject result = super.apiJSONToServerJSON(configName, apiJSON);
            final int connectionType = result.getInt("connection_type");
            switch (connectionType) {
                case 0: {
                    result.put("SUB_CONFIG", (Object)"L2TP");
                    break;
                }
                case 13: {
                    result.put("SUB_CONFIG", (Object)"OPEN_VPN_POLICY");
                    break;
                }
            }
            return result;
        }
        catch (final JSONException e) {
            this.logger.log(Level.SEVERE, "Exception occurred in apiJSONToServerJSON", (Throwable)e);
            return null;
        }
    }
    
    @Override
    public JSONArray DOToAPIJSON(final DataObject dataObject, final String configName) throws APIHTTPException {
        final JSONArray jsonArray = super.DOToAPIJSON(dataObject, configName);
        try {
            for (int index = 0; index < jsonArray.length(); ++index) {
                final JSONObject jsonObject = jsonArray.getJSONObject(index);
                final int connectionType = jsonObject.getInt("connection_type");
                final String[] vpnTypes = { "l2tp", "open_vpn_policy", "sub_config" };
                final List<String> vpnTypeList = new ArrayList<String>(Arrays.asList(vpnTypes));
                switch (connectionType) {
                    case 0: {
                        vpnTypeList.remove("l2tp");
                        break;
                    }
                    case 13: {
                        vpnTypeList.remove("open_vpn_policy");
                        break;
                    }
                }
                this.removeKeyFromJSON(jsonObject, vpnTypeList);
            }
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "exception occurred in DOToAPIJSON");
            throw new APIHTTPException("COM0004", new Object[0]);
        }
        return jsonArray;
    }
}
