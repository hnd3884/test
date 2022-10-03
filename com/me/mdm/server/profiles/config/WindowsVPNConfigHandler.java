package com.me.mdm.server.profiles.config;

import com.adventnet.persistence.DataAccessException;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.persistence.Row;
import java.util.Iterator;
import java.util.List;
import java.util.Collection;
import java.util.ArrayList;
import java.util.Arrays;
import com.me.mdm.api.error.APIHTTPException;
import org.json.JSONException;
import java.util.logging.Level;
import com.adventnet.persistence.DataObject;
import org.json.JSONObject;
import org.json.JSONArray;

public class WindowsVPNConfigHandler extends VPNConfigHandler
{
    private static final String IKE_SA = "ike_sa_id";
    private static final String IKE_SA_CHILD = "child_sa_id";
    private static final String IKEV2 = "ikev2";
    private static final String IKEV2_REMOVAL = "redirect";
    private static final String IKEV2_LOCAL_ID = "local_id";
    private static final String IKEV2_REMOTE_ID = "remote_id";
    private static final String IKEV2_EAP_PASSWORD = "eap_password";
    private static final String IKEV2_SHARED_SECRET = "shared_secret";
    private static final String IKEV2_INTERNAL_IP_SUBNET = "internal_ip_subnet";
    private static final String IKEV2_MOBIKE = "mobike";
    private static final String IKEV2_CHILD_SA_ID = "child_sa_id";
    private static final String IKEV2_EAP_ENABLING = "eap_enabling";
    private static final String IKEV2_EAP_USERNAME = "eap_username";
    private static final String IKEV2_NAT_ALIVE_INTERVAL = "nat_alive_interval";
    private static final String IKEV2_IKE_SA_ID = "ike_sa_id";
    private static final String IKEV2_DEAD_PER_DETECTION = "dead_per_detection";
    private static final String IKEV2_CERTIFICATE_REVOCATION_CHECK = "certificate_revocation_check";
    private static final String IKEV2_PFS = "pfs";
    private static final String IKEV2_NAT_ALIVE_OFFLOAD_ENABLE = "nat_alive_offload_enable";
    String allowedAppAlias;
    JSONArray allowedApp;
    
    public WindowsVPNConfigHandler() {
        this.allowedAppAlias = "";
    }
    
    @Override
    protected void checkAndAddInnerJSON(final JSONObject configJSON, final DataObject dataObject, final String configName) throws Exception {
        if (configJSON.has("payload_id")) {
            final JSONArray templateConfigProperties = ProfileConfigurationUtil.getInstance().getPayloadConfigurationProperties(configName);
            this.constructInnerJsonValue(templateConfigProperties, configJSON, dataObject, configName);
            if (configJSON.optInt("vpn_type") == 2) {
                this.handlePerAppVPNData(dataObject, configJSON, configName);
            }
        }
    }
    
    @Override
    public JSONObject apiJSONToServerJSON(final String configName, final JSONObject apiJSON) throws APIHTTPException {
        this.logger.log(Level.INFO, "Started converting API JSON to Server JSON...");
        try {
            int connectionType = apiJSON.getInt("connection_type");
            ++connectionType;
            if (9 == connectionType) {
                final JSONObject IKEV2Json = apiJSON.getJSONObject("ikev2");
                IKEV2Json.put("ike_sa_id", (Object)new JSONObject());
                IKEV2Json.put("child_sa_id", (Object)new JSONObject());
            }
            final JSONObject result = super.apiJSONToServerJSON(configName, apiJSON);
            switch (connectionType) {
                case 1: {
                    result.put("SUB_CONFIG", (Object)"L2TP");
                    break;
                }
                case 2: {
                    result.put("SUB_CONFIG", (Object)"PPTP");
                    break;
                }
                case 3: {
                    result.put("SUB_CONFIG", (Object)"IPSec");
                    break;
                }
                case 9: {
                    result.put("SUB_CONFIG", (Object)"IKEv2");
                    break;
                }
                case 7: {
                    result.put("SUB_CONFIG", (Object)"CustomSSL");
                    break;
                }
            }
            if (result.has("CUSTOM_DATA")) {
                this.setCustomDataInServerJson(result);
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
                int connectionType = jsonObject.getInt("connection_type");
                final String[] vpnTypes = { "l2tp", "pptp", "ipsec", "ikev2", "sub_config" };
                final List<String> vpnTypeList = new ArrayList<String>(Arrays.asList(vpnTypes));
                ++connectionType;
                switch (connectionType) {
                    case 1: {
                        vpnTypeList.remove("l2tp");
                        break;
                    }
                    case 2: {
                        vpnTypeList.remove("pptp");
                        break;
                    }
                    case 3: {
                        vpnTypeList.remove("ipsec");
                        break;
                    }
                    case 7: {
                        vpnTypeList.remove("customssl");
                        break;
                    }
                    case 9: {
                        vpnTypeList.remove("ikev2");
                        final List<String> ikev2RemovalList = new ArrayList<String>(Arrays.asList("redirect", "child_sa_id", "ike_sa_id"));
                        this.removeInnerKeysFromApiJSON(jsonObject, ikev2RemovalList, "ikev2");
                        break;
                    }
                }
                this.removeKeyFromJSON(jsonObject, vpnTypeList);
                final JSONArray customData = this.setCustomDataInAPIJson(dataObject);
                final JSONObject customDataProperty = this.getDetailsForColName(configName, "CUSTOM_DATA");
                final String customDataAlias = String.valueOf(customDataProperty.get("alias"));
                jsonObject.put(customDataAlias, (Object)customData);
            }
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "exception occurred in DOToAPIJSON");
            throw new APIHTTPException("COM0004", new Object[0]);
        }
        return jsonArray;
    }
    
    private void removeInnerKeysFromApiJSON(final JSONObject jsonObject, final List<String> keys, final String removalKey) throws Exception {
        final JSONObject innerJson = jsonObject.getJSONObject(removalKey);
        for (final String key : keys) {
            if (innerJson.has(key)) {
                innerJson.remove(key);
            }
        }
    }
    
    protected void setCustomDataInServerJson(final JSONObject serverJson) throws JSONException {
        final JSONArray jsonArray = serverJson.optJSONArray("CUSTOM_DATA");
        final JSONObject customDataJson = new JSONObject();
        for (int i = 0; i < jsonArray.length(); ++i) {
            final JSONObject jsonObject = jsonArray.getJSONObject(i);
            final String customKey = String.valueOf(jsonObject.get("CUSTOM_KEY"));
            final String value = String.valueOf(jsonObject.get("CUSTOM_VALUE"));
            customDataJson.put(customKey, (Object)value);
        }
        serverJson.put("CUSTOM_DATA", (Object)customDataJson);
    }
    
    protected JSONArray setCustomDataInAPIJson(final DataObject dataObject) throws Exception {
        final JSONArray customDataJSON = new JSONArray();
        if (dataObject.containsTable("VpnCustomData")) {
            final Iterator customDataIterator = dataObject.getRows("VpnCustomData");
            while (customDataIterator.hasNext()) {
                final JSONObject jsonObject = new JSONObject();
                final Row customDataRow = customDataIterator.next();
                final String customDataKey = (String)customDataRow.get("KEY");
                final String customDataValue = (String)customDataRow.get("VALUE");
                jsonObject.put("custom_key", (Object)customDataKey);
                jsonObject.put("custom_value", (Object)customDataValue);
                customDataJSON.put((Object)jsonObject);
            }
        }
        return customDataJSON;
    }
    
    private void handlePerAppVPNData(final DataObject dataObject, final JSONObject configJSON, final String configName) throws Exception {
        try {
            if (!dataObject.isEmpty() && configJSON.has("payload_id")) {
                final JSONArray configProperties = ProfileConfigurationUtil.getInstance().getPayloadConfigurationProperties(configName);
                this.setAliasName(configProperties, configJSON);
                this.addKioskApps(dataObject, configJSON);
                this.addKioskSystemApps(dataObject, configJSON);
            }
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Exception in check and add inner json", e);
            throw e;
        }
    }
    
    private void setAliasName(final JSONArray configProperties, final JSONObject configJSON) {
        try {
            for (int i = 0; i < configProperties.length(); ++i) {
                final JSONObject configObject = configProperties.getJSONObject(i);
                final String name = String.valueOf(configObject.get("name"));
                if (name.equalsIgnoreCase("ALLOWED_APPS")) {
                    configJSON.put(this.allowedAppAlias = String.valueOf(configObject.get("alias")), (Object)new JSONArray());
                    this.allowedApp = configObject.getJSONArray("properties");
                }
            }
        }
        catch (final JSONException e) {
            this.logger.log(Level.SEVERE, "Exception occurred in apiJSONToServerJSON", (Throwable)e);
        }
    }
    
    private void addKioskApps(final DataObject dataObject, final JSONObject configJSON) throws DataAccessException, JSONException {
        try {
            final String tableName = "WindowsKioskPolicyApps";
            if (dataObject.containsTable(tableName)) {
                final Long configDataItemId = configJSON.getLong("payload_id");
                final Criteria payloadCriteria = new Criteria(new Column(tableName, "CONFIG_DATA_ITEM_ID"), (Object)configDataItemId, 0);
                final Iterator iterator = dataObject.getRows(tableName, payloadCriteria);
                while (iterator.hasNext()) {
                    final Row appsRow = iterator.next();
                    final JSONObject innerArrayJSON = new JSONObject();
                    if (appsRow != null) {
                        final Long appGroupId = (Long)appsRow.get("APP_GROUP_ID");
                        final Row appDetailRow = dataObject.getRow("MdAppGroupDetails", new Criteria(new Column("MdAppGroupDetails", "APP_GROUP_ID"), (Object)appGroupId, 0));
                        final String displayName = (String)appDetailRow.get("GROUP_DISPLAY_NAME");
                        innerArrayJSON.put(this.getAliasName("APP_ID", this.allowedApp), (Object)appGroupId);
                        innerArrayJSON.put(this.getAliasName("IS_SYSTEM_APP", this.allowedApp), false);
                        innerArrayJSON.put(this.getAliasName("GROUP_DISPLAY_NAME", this.allowedApp), (Object)displayName);
                        configJSON.getJSONArray(this.allowedAppAlias).put((Object)innerArrayJSON);
                    }
                }
            }
        }
        catch (final DataAccessException e) {
            throw e;
        }
        catch (final JSONException e2) {
            throw e2;
        }
    }
    
    private void addKioskSystemApps(final DataObject dataObject, final JSONObject configJSON) throws JSONException, DataAccessException {
        try {
            final String tableName = "WindowsKioskPolicySystemApps";
            if (dataObject.containsTable(tableName)) {
                final Long configDataItemId = configJSON.getLong("payload_id");
                final Criteria payloadCriteria = new Criteria(new Column(tableName, "CONFIG_DATA_ITEM_ID"), (Object)configDataItemId, 0);
                final Iterator iterator = dataObject.getRows(tableName, payloadCriteria);
                while (iterator.hasNext()) {
                    final Row systemAppRow = iterator.next();
                    final JSONObject innerArrayJSON = new JSONObject();
                    final Long appId = (Long)systemAppRow.get("APP_ID");
                    final Row appDetailRow = dataObject.getRow("WindowsSystemApps", new Criteria(new Column("WindowsSystemApps", "APP_ID"), (Object)appId, 0));
                    final String displayName = (String)appDetailRow.get("APP_NAME");
                    innerArrayJSON.put(this.getAliasName("APP_ID", this.allowedApp), (Object)appId);
                    innerArrayJSON.put(this.getAliasName("IS_SYSTEM_APP", this.allowedApp), true);
                    innerArrayJSON.put(this.getAliasName("GROUP_DISPLAY_NAME", this.allowedApp), (Object)displayName);
                    configJSON.getJSONArray(this.allowedAppAlias).put((Object)innerArrayJSON);
                }
            }
        }
        catch (final DataAccessException e) {
            throw e;
        }
        catch (final JSONException e2) {
            throw e2;
        }
    }
    
    private String getAliasName(final String name, final JSONArray arrayObject) {
        try {
            for (int i = 0; i < arrayObject.length(); ++i) {
                final JSONObject property = arrayObject.getJSONObject(i);
                if (String.valueOf(property.get("name")).equals(name) && property.has("alias")) {
                    return String.valueOf(property.get("alias"));
                }
            }
        }
        catch (final JSONException e) {
            this.logger.log(Level.SEVERE, "Exception occurred in apiJSONToServerJSON", (Throwable)e);
        }
        return "";
    }
}
