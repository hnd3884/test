package com.me.mdm.server.profiles.config;

import java.util.Iterator;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.persistence.Row;
import java.io.InputStream;
import java.io.IOException;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
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

public class IOSVPNConfigHandler extends VPNConfigHandler
{
    public static final String IOS_API_VPN_TYPE_CISCO = "cisco";
    public static final String IOS_API_VPN_TYPE_JUNIPERSSL = "juniperssl";
    public static final String IOS_API_VPN_TYPE_F5SSL = "f5ssl";
    public static final String IOS_API_VPN_TYPE_CUSTOMSSL = "customssl";
    public static final String IOS_API_VPN_TYPE_PULSESECURE = "pulsesecure";
    public static final String IOS_API_VPN_TYPE_CISCOANYCONNECT = "ciscoanyconnect";
    public static final String IOS_API_VPN_TYPE_SONICWALL = "sonicwall";
    public static final String IOS_API_VPN_TYPE_ARUBAVIA = "arubavia";
    public static final String IOS_API_VPN_TYPE_CHECKPOINT = "checkpoint";
    public static final String IOS_VPN_TYPE_CISCO = "Cisco";
    public static final String IOS_VPN_TYPE_JUNIPERSSL = "JuniperSSL";
    public static final String IOS_VPN_TYPE_F5SSL = "F5SSL";
    public static final String IOS_VPN_TYPE_CISCOANYCONNECT = "CiscoAnyConnect";
    public static final String IOS_VPN_TYPE_SONICWALL = "SonicWall";
    public static final String IOS_VPN_TYPE_ARUBAVIA = "ArubaVia";
    public static final String IOS_VPN_TYPE_CHECKPOINT = "Checkpoint";
    public static final String IOS_OPEN_VPN_IDENTIFIER = "net.openvpn.connect.app";
    
    @Override
    protected void checkAndAddInnerJSON(final JSONObject configJSON, final DataObject dataObject, final String configName) throws Exception {
        if (configJSON.has("payload_id")) {
            final JSONArray templateConfigProperties = ProfileConfigurationUtil.getInstance().getPayloadConfigurationProperties(configName);
            this.constructInnerJsonValue(templateConfigProperties, configJSON, dataObject, configName);
            if (configJSON.optInt("vpn_type") == 2) {
                new IOSAppLockConfigHandler().addKioskApps(dataObject, configJSON, templateConfigProperties);
            }
        }
    }
    
    @Override
    public JSONObject apiJSONToServerJSON(final String configName, final JSONObject apiJSON) throws APIHTTPException {
        this.logger.log(Level.INFO, "Started converting API JSON to Server JSON...");
        try {
            final JSONObject result = super.apiJSONToServerJSON(configName, apiJSON);
            int connectionType = result.getInt("connection_type");
            ++connectionType;
            boolean needCustomData = true;
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
                case 4: {
                    result.put("SUB_CONFIG", (Object)"Cisco");
                    break;
                }
                case 5: {
                    result.put("SUB_CONFIG", (Object)"JuniperSSL");
                    break;
                }
                case 6: {
                    result.put("SUB_CONFIG", (Object)"F5SSL");
                    break;
                }
                case 7: {
                    result.put("SUB_CONFIG", (Object)"CustomSSL");
                    final String identifier = result.getJSONObject("CustomSSL").getString("IDENTIFIER");
                    if (identifier.equalsIgnoreCase("net.openvpn.connect.app") && result.getJSONObject("CustomSSL").has("CUSTOM_FILE")) {
                        this.setCustomDataForOpenVPN(result);
                        needCustomData = false;
                        break;
                    }
                    break;
                }
                case 8: {
                    result.put("SUB_CONFIG", (Object)"JuniperSSL");
                    break;
                }
                case 9: {
                    result.put("SUB_CONFIG", (Object)"IKEv2");
                    break;
                }
                case 10: {
                    result.put("SUB_CONFIG", (Object)"CiscoAnyConnect");
                    break;
                }
                case 11: {
                    result.put("SUB_CONFIG", (Object)"SonicWall");
                    break;
                }
                case 12: {
                    result.put("SUB_CONFIG", (Object)"ArubaVia");
                    break;
                }
                case 13: {
                    result.put("SUB_CONFIG", (Object)"Checkpoint");
                    break;
                }
            }
            if (result.has("CUSTOM_DATA") && needCustomData) {
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
                final String[] vpnTypes = { "l2tp", "pptp", "ipsec", "cisco", "juniperssl", "f5ssl", "customssl", "pulsesecure", "ikev2", "ciscoanyconnect", "sonicwall", "arubavia", "checkpoint", "sub_config" };
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
                    case 4: {
                        vpnTypeList.remove("cisco");
                        break;
                    }
                    case 5: {
                        vpnTypeList.remove("juniperssl");
                        break;
                    }
                    case 6: {
                        vpnTypeList.remove("f5ssl");
                        break;
                    }
                    case 7: {
                        vpnTypeList.remove("customssl");
                        break;
                    }
                    case 8: {
                        vpnTypeList.remove("pulsesecure");
                        break;
                    }
                    case 9: {
                        vpnTypeList.remove("ikev2");
                        break;
                    }
                    case 10: {
                        vpnTypeList.remove("ciscoanyconnect");
                        break;
                    }
                    case 11: {
                        vpnTypeList.remove("sonicwall");
                        break;
                    }
                    case 12: {
                        vpnTypeList.remove("arubavia");
                        break;
                    }
                    case 13: {
                        vpnTypeList.remove("checkpoint");
                        break;
                    }
                }
                this.removeKeyFromJSON(jsonObject, vpnTypeList);
                final JSONObject property = this.getDetailsForColName(configName, "ONDEMANDRULE");
                final String onDemandAlias = String.valueOf(property.get("alias"));
                final JSONArray vpnOnDemand = this.setVPNOnDemand(dataObject, property);
                jsonObject.put(onDemandAlias, (Object)vpnOnDemand);
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
    
    private void setCustomDataForOpenVPN(final JSONObject result) throws APIHTTPException {
        BufferedReader reader = null;
        try {
            final JSONObject customSSL = result.getJSONObject("CustomSSL");
            if (customSSL.has("CUSTOM_FILE")) {
                final String customFile = customSSL.getString("CUSTOM_FILE");
                final InputStream stream = ApiFactoryProvider.getFileAccessAPI().readFile(customFile);
                reader = new BufferedReader(new InputStreamReader(stream));
                String line = null;
                String currentCertKey = "";
                final JSONObject customData = new JSONObject();
                customData.put("dhcp-option", (Object)"DNS 8.8.8.8");
                customData.put("vpn-on-demand", 0);
                while ((line = reader.readLine()) != null) {
                    line = line.trim();
                    final String[] spiltedtext = line.split(" ");
                    final String key = spiltedtext[0].trim();
                    if (key.equalsIgnoreCase("<ca>") || key.equalsIgnoreCase("<cert>") || key.equalsIgnoreCase("<key>") || key.equalsIgnoreCase("<tls-crypt>")) {
                        final int start = key.indexOf("<");
                        final int end = key.indexOf(">");
                        final String actualKey = currentCertKey = key.substring(start + 1, end);
                        customData.put(currentCertKey, (Object)"");
                    }
                    else if (key.equalsIgnoreCase("</ca>") || key.equalsIgnoreCase("</cert>") || key.equalsIgnoreCase("</key>") || key.equalsIgnoreCase("</tls-crypt>")) {
                        currentCertKey = "";
                    }
                    else if (currentCertKey.equalsIgnoreCase("ca") || currentCertKey.equalsIgnoreCase("cert") || currentCertKey.equalsIgnoreCase("key") || currentCertKey.equalsIgnoreCase("tls-crypt")) {
                        String certString = customData.getString(currentCertKey);
                        certString = certString + line + "\\n";
                        customData.put(currentCertKey, (Object)certString);
                    }
                    else if (spiltedtext.length > 1) {
                        String value = "";
                        for (int i = 1; i < spiltedtext.length; ++i) {
                            if (value != "") {
                                value = value + " " + spiltedtext[i].trim();
                            }
                            else {
                                value = spiltedtext[i].trim();
                            }
                        }
                        customData.put(key, (Object)value);
                    }
                    else {
                        customData.put(key, (Object)"NOARGS");
                    }
                }
                if (customData.has("proto")) {
                    final String proto = customData.getString("proto");
                    String remote = customData.getString("remote");
                    remote = remote + " " + proto;
                    customData.put("remote", (Object)remote);
                }
                result.put("CUSTOM_DATA", (Object)customData);
            }
        }
        catch (final JSONException e) {
            throw new APIHTTPException("COM0005", new Object[0]);
        }
        catch (final Exception e2) {
            this.logger.log(Level.SEVERE, "Exception in custom data for openvpn", e2);
        }
        finally {
            try {
                if (reader != null) {
                    reader.close();
                }
            }
            catch (final IOException ioe) {
                this.logger.log(Level.SEVERE, "Error while closing stream: ", ioe);
            }
        }
    }
    
    protected JSONArray setVPNOnDemand(final DataObject dataObject, final JSONObject property) throws Exception {
        final JSONArray onDemandPoliciesArray = new JSONArray();
        this.logger.log(Level.INFO, "configDOFromDB {0}", dataObject);
        final Iterator it = dataObject.getRows("VPNOnDemandPolicy");
        while (it.hasNext()) {
            final JSONObject onDemandPolicy = new JSONObject();
            final Row vpnOnDemandRow = it.next();
            final String action = vpnOnDemandRow.get("ONDEMAND_ACTION").toString();
            final JSONObject onDemandProperty = super.getSubConfigProperties(property.getJSONArray("properties"), action);
            final String onDemandname = String.valueOf(onDemandProperty.get("alias"));
            final JSONArray NwRulesJson = new JSONArray();
            final Criteria nwRulecriteria = new Criteria(new Column("VpnNWRuleToPolicyRel", "VPN_OD_POLICY_ID"), vpnOnDemandRow.get("VPN_OD_POLICY_ID"), 0);
            final Join nwRulejoin = new Join("VpnNWRuleToPolicyRel", "VpnODRulesForNWChange", new String[] { "NW_CHANGE_RULE_ID" }, new String[] { "NW_CHANGE_RULE_ID" }, 2);
            final Iterator nwRuleTableIterator = dataObject.getRows("VpnODRulesForNWChange", nwRulecriteria, nwRulejoin);
            while (nwRuleTableIterator.hasNext()) {
                final JSONObject nwRule = new JSONObject();
                final Row ruleTableRow = nwRuleTableIterator.next();
                final String rulename = ruleTableRow.get("NETWORKS_RULE_TYPE").toString();
                final String ruleValue = ruleTableRow.get("NETWORKS_VALUE").toString();
                nwRule.put("NETWORKS_RULE_TYPE", (Object)rulename);
                nwRule.put("NETWORKS_VALUE", (Object)ruleValue);
                NwRulesJson.put((Object)nwRule);
            }
            final JSONArray connAttamptRulesJson = new JSONArray();
            final Criteria connAttemptcriteria = new Criteria(new Column("VpnConEvalRuleToPolicyRel", "VPN_OD_POLICY_ID"), vpnOnDemandRow.get("VPN_OD_POLICY_ID"), 0);
            final Join connAttemptjoin = new Join("VpnConEvalRuleToPolicyRel", "VpnODRulesForConEval", new String[] { "CONN_RULE_ID" }, new String[] { "CONN_RULE_ID" }, 2);
            final Iterator connAttemptTblIterator = dataObject.getRows("VpnODRulesForConEval", connAttemptcriteria, connAttemptjoin);
            while (connAttemptTblIterator.hasNext()) {
                final JSONObject connRule = new JSONObject();
                final Row ruleTableRow2 = connAttemptTblIterator.next();
                final String domainName = ruleTableRow2.get("DOMAIN_NAME").toString();
                final String dnsServer = ruleTableRow2.get("DNS_SERVER_ADDRESS").toString();
                final String urlProbe = ruleTableRow2.get("URL_PROBE").toString();
                connRule.put("domain_name", (Object)domainName);
                connRule.put("dns_server_address", (Object)dnsServer);
                connRule.put("url_probe", (Object)urlProbe);
                final JSONObject jsonObject = new JSONObject();
                jsonObject.put("vpnodrulesforconeval", (Object)connRule);
                connAttamptRulesJson.put((Object)jsonObject);
            }
            if (NwRulesJson.length() > 0) {
                onDemandPolicy.put(onDemandname, (Object)NwRulesJson);
            }
            if (connAttamptRulesJson.length() > 0) {
                onDemandPolicy.put(onDemandname, (Object)connAttamptRulesJson);
            }
            onDemandPoliciesArray.put((Object)onDemandPolicy);
        }
        return onDemandPoliciesArray;
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
    
    @Override
    public void validateServerJSON(final JSONObject serverJSON) throws APIHTTPException {
        try {
            super.validateServerJSON(serverJSON);
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
        catch (final JSONException e) {
            this.logger.log(Level.SEVERE, "Invalid json", (Throwable)e);
            throw new APIHTTPException("COM0005", new Object[0]);
        }
    }
}
