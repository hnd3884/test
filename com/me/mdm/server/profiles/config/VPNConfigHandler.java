package com.me.mdm.server.profiles.config;

import java.util.logging.Level;
import java.util.logging.Logger;
import com.adventnet.persistence.Row;
import com.me.mdm.server.security.profile.PayloadSecretFieldsHandler;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.Join;
import com.adventnet.persistence.DataObject;
import org.json.JSONArray;
import java.util.Iterator;
import java.util.List;
import org.json.JSONObject;

public class VPNConfigHandler extends DefaultConfigHandler
{
    public static final String CONNECTION_TYPE = "connection_type";
    public static final String API_SUB_CONFIG = "sub_config";
    public static final String VPN_TYPE_CUSTOMSSL = "CustomSSL";
    public static final String API_VPN_TYPE_L2TP = "l2tp";
    public static final String API_VPN_TYPE_PPTP = "pptp";
    public static final String API_VPN_TYPE_IPSEC = "ipsec";
    public static final String API_VPN_TYPE_CUSTOMSSL = "customssl";
    public static final String API_VPN_TYPE_IKEV2 = "ikev2";
    
    public JSONObject removeKeyFromJSON(final JSONObject jsonObject, final List<String> keys) {
        for (final String key : keys) {
            if (jsonObject.has(key)) {
                jsonObject.remove(key);
            }
        }
        return jsonObject;
    }
    
    public void constructInnerJsonValue(final JSONArray templateConfigProperties, final JSONObject configJSON, final DataObject dataObject, final String configName) throws Exception {
        if (configJSON.has("payload_id")) {
            final long configDataItemId = configJSON.getLong("payload_id");
            String subConfigName = "";
            for (int index = 0; index < templateConfigProperties.length(); ++index) {
                if (String.valueOf(templateConfigProperties.getJSONObject(index).get("type")).equals("org.json.JSONObject")) {
                    final JSONObject subConfig = templateConfigProperties.getJSONObject(index);
                    final String tableName = String.valueOf(subConfig.get("table_name"));
                    if (dataObject.containsTable(tableName)) {
                        subConfigName = String.valueOf(subConfig.get("alias"));
                        if (subConfig.has("properties")) {
                            final JSONArray jsonArray = subConfig.getJSONArray("properties");
                            Row row = null;
                            if (tableName.equals("VpnIKEv2")) {
                                final Join vpnPolicyJoin = new Join(tableName, "VpnToPolicyRel", new String[] { "VPN_POLICY_ID" }, new String[] { "VPN_POLICY_ID" }, 1);
                                final Criteria criteria = new Criteria(Column.getColumn("VpnToPolicyRel", "CONFIG_DATA_ITEM_ID"), (Object)configDataItemId, 0);
                                row = dataObject.getRow(tableName, criteria, vpnPolicyJoin);
                            }
                            else {
                                final Criteria criteria2 = new Criteria(Column.getColumn(tableName, "CONFIG_DATA_ITEM_ID"), (Object)configDataItemId, 0);
                                row = dataObject.getRow(tableName, criteria2);
                            }
                            JSONObject config = new JSONObject();
                            String columnName = null;
                            Object columnValue = null;
                            JSONObject property = null;
                            if (row != null) {
                                final List columns = row.getColumns();
                                for (int i = 0; i < columns.size(); ++i) {
                                    columnName = columns.get(i);
                                    property = super.getSubConfigProperties(jsonArray, columnName);
                                    columnValue = row.get(columnName);
                                    columnValue = this.transformTableValueToApiValue(dataObject, columnName, columnValue, tableName, configName);
                                    if (property != null && property.has("alias") && columnValue != null) {
                                        if (columnName.equalsIgnoreCase("IKE_SA_ID") || columnName.equalsIgnoreCase("CHILD_SA_ID")) {
                                            config.put(String.valueOf(property.get("alias")), (Object)this.getSubConfigForm(dataObject, columnName, property.getJSONArray("properties")));
                                        }
                                        else if (property.has("return_secret_field_value")) {
                                            config = PayloadSecretFieldsHandler.getInstance().replaceSecretFieldIdInDoToApi(property, columnValue, config);
                                        }
                                        else {
                                            config.put(String.valueOf(property.get("alias")), columnValue);
                                        }
                                    }
                                }
                                if (tableName.equals("VpnIKEv2") && dataObject.containsTable("VpnPolicyToCertificate")) {
                                    final Join vpnPolicyJoin2 = new Join("VpnPolicyToCertificate", "VpnToPolicyRel", new String[] { "VPN_POLICY_ID" }, new String[] { "VPN_POLICY_ID" }, 1);
                                    final Criteria criteria3 = new Criteria(Column.getColumn("VpnToPolicyRel", "CONFIG_DATA_ITEM_ID"), (Object)configDataItemId, 0);
                                    final Row certificateIDRow = dataObject.getRow("VpnPolicyToCertificate", criteria3, vpnPolicyJoin2);
                                    property = super.getSubConfigProperties(jsonArray, "CLIENT_CERT_ID");
                                    config.put(String.valueOf(property.get("alias")), certificateIDRow.get("CLIENT_CERT_ID"));
                                }
                                configJSON.put("sub_config", (Object)String.valueOf(subConfig.get("name")));
                                configJSON.put(subConfigName, (Object)config);
                            }
                        }
                    }
                }
            }
        }
    }
    
    private JSONObject getSubConfigForm(final DataObject dataObject, final String tableName, final JSONArray properties) {
        final JSONObject subForm = new JSONObject();
        try {
            final Iterator subConfigIterator = dataObject.getRows(tableName);
            while (subConfigIterator.hasNext()) {
                JSONObject property = null;
                final Row subConfigRow = subConfigIterator.next();
                final List columns = subConfigRow.getColumns();
                for (int i = 0; i < columns.size(); ++i) {
                    final String columnName = columns.get(i);
                    property = super.getSubConfigProperties(properties, columnName);
                    if (property != null && String.valueOf(property.get("alias")) != null) {
                        subForm.put(String.valueOf(property.get("alias")), subConfigRow.get(columnName));
                    }
                }
            }
        }
        catch (final Exception ex) {
            Logger.getLogger("MDMConfigLogger").log(Level.SEVERE, "Exception at retriving vpnformbean", ex);
        }
        return subForm;
    }
}
