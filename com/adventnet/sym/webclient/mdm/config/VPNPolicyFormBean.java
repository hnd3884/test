package com.adventnet.sym.webclient.mdm.config;

import java.util.UUID;
import com.adventnet.ds.query.Join;
import com.adventnet.persistence.DataAccessException;
import org.json.JSONException;
import com.adventnet.persistence.internal.UniqueValueHolder;
import java.util.Iterator;
import java.util.List;
import com.adventnet.sym.server.mdm.util.MDMDBUtil;
import com.adventnet.sym.server.mdm.util.MDMStringUtils;
import com.adventnet.sym.webclient.mdm.config.formbean.AppLockPolicyFormBean;
import org.json.JSONArray;
import java.util.logging.Level;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.persistence.Row;
import com.me.devicemanagement.framework.server.exception.SyMException;
import com.adventnet.persistence.DataObject;
import org.json.JSONObject;
import java.util.logging.Logger;
import com.me.mdm.webclient.formbean.MDMDefaultFormBean;

public class VPNPolicyFormBean extends MDMDefaultFormBean
{
    public static Logger logger;
    
    @Override
    public DataObject getDataObject(final JSONObject multipleConfigForm, final JSONObject[] dynaActionForm, final DataObject dataObject) throws SyMException {
        this.dynaFormToDO(multipleConfigForm, dynaActionForm, dataObject);
        return dataObject;
    }
    
    @Override
    public void dynaFormToDO(final JSONObject multipleConfigForm, final JSONObject[] dynaActionForm, DataObject dataObject) throws SyMException {
        final int executionOrder = dynaActionForm.length;
        try {
            for (int k = 0; k < executionOrder; ++k) {
                final JSONObject dynaJSON = dynaActionForm[k];
                final String tableName = (String)dynaJSON.get("TABLE_NAME");
                final String subConfig = (String)dynaJSON.get("SUB_CONFIG");
                Object configDataItemID = dynaJSON.optLong("CONFIG_DATA_ITEM_ID");
                if (tableName != null) {
                    final Row configData = dataObject.getRow("ConfigData");
                    final Object configDataID = configData.get("CONFIG_DATA_ID");
                    JSONObject subConfigForm = null;
                    String subTableName = null;
                    if (subConfig != null) {
                        subConfigForm = dynaJSON.getJSONObject(subConfig);
                        subTableName = (String)subConfigForm.get("TABLE_NAME");
                    }
                    boolean isAdded = false;
                    boolean isSubRowAdded = false;
                    boolean isProxyAdded = false;
                    Row payloadRow = null;
                    Row payloadSubRow = null;
                    if (configDataItemID == null || configDataItemID.equals(0L)) {
                        Row dataItemRow = null;
                        dataItemRow = new Row("ConfigDataItem");
                        dataItemRow.set("CONFIG_DATA_ID", configDataID);
                        dataItemRow.set("EXECUTION_ORDER", (Object)k);
                        dataObject.addRow(dataItemRow);
                        dynaJSON.put("CONFIG_DATA_ITEM_ID", dataItemRow.get("CONFIG_DATA_ITEM_ID"));
                        super.insertConfigDataItemExtn(dynaJSON, dataObject);
                        configDataItemID = dataItemRow.get("CONFIG_DATA_ITEM_ID");
                        isProxyAdded = true;
                    }
                    else {
                        final Criteria criteria = new Criteria(Column.getColumn(tableName, "CONFIG_DATA_ITEM_ID"), configDataItemID, 0);
                        dataObject.deleteRows(tableName, criteria);
                        if (subTableName.equals("OpenVPNPolicy")) {
                            final Criteria subCriteria = new Criteria(Column.getColumn(subTableName, "CONFIG_DATA_ITEM_ID"), configDataItemID, 0);
                            dataObject.deleteRows(subTableName, subCriteria);
                        }
                        dataObject = MDMUtil.getPersistence().update(dataObject);
                        isProxyAdded = false;
                    }
                    payloadRow = new Row(tableName);
                    payloadRow.set("CONFIG_DATA_ITEM_ID", configDataItemID);
                    if (subTableName != null) {
                        payloadSubRow = new Row(subTableName);
                        if (!subTableName.equalsIgnoreCase("VpnIKEv2")) {
                            payloadSubRow.set("CONFIG_DATA_ITEM_ID", payloadRow.get("CONFIG_DATA_ITEM_ID"));
                        }
                        isSubRowAdded = true;
                    }
                    isAdded = true;
                    String columnName = null;
                    Object columnValue = null;
                    final List columns = payloadRow.getColumns();
                    final String notToBeInsertedColumn = this.getTableDetails(subTableName);
                    for (int i = 0; i < columns.size(); ++i) {
                        columnName = columns.get(i);
                        columnValue = dynaJSON.opt(columnName);
                        if (columnName != null && columnValue != null) {
                            if (columnName.equals("CONFIG_DATA_ITEM_ID")) {
                                columnName += "";
                            }
                            else {
                                payloadRow.set(columnName, columnValue);
                            }
                        }
                    }
                    Row proxyRow = null;
                    if (dynaJSON.has("PROXY_SETTINGS")) {
                        try {
                            final JSONObject proxyJSON = dynaJSON.getJSONObject("PROXY_SETTINGS");
                            if (isProxyAdded) {
                                proxyRow = new Row("PayloadProxyConfig");
                                proxyRow.set("CONFIG_DATA_ITEM_ID", payloadRow.get("CONFIG_DATA_ITEM_ID"));
                            }
                            else {
                                final Criteria criteria2 = new Criteria(Column.getColumn("PayloadProxyConfig", "CONFIG_DATA_ITEM_ID"), configDataItemID, 0);
                                proxyRow = dataObject.getRow("PayloadProxyConfig", criteria2);
                            }
                            final List proxyColumns = proxyRow.getColumns();
                            for (int j = 0; j < proxyColumns.size(); ++j) {
                                columnName = proxyColumns.get(j);
                                if (columnName != null && !columnName.equals("CONFIG_DATA_ITEM_ID")) {
                                    columnValue = proxyJSON.opt(columnName);
                                    if (columnValue != null) {
                                        proxyRow.set(columnName, columnValue);
                                    }
                                }
                            }
                        }
                        catch (final Exception e) {
                            Logger.getLogger("MDMConfigLogger").log(Level.SEVERE, "Not able to save proxy", e);
                        }
                    }
                    if (payloadSubRow != null && subConfigForm != null) {
                        final List subColumns = payloadSubRow.getColumns();
                        for (int l = 0; l < subColumns.size(); ++l) {
                            columnName = subColumns.get(l);
                            if (columnName != null && !columnName.equals(notToBeInsertedColumn)) {
                                if (columnName.equalsIgnoreCase("IKE_SA_ID") || columnName.equalsIgnoreCase("CHILD_SA_ID")) {
                                    columnValue = this.modifySubConfigForm(dataObject, subConfigForm, columnName, isSubRowAdded);
                                }
                                else {
                                    columnValue = subConfigForm.opt(columnName);
                                }
                                if (columnValue != null) {
                                    payloadSubRow.set(columnName, columnValue);
                                }
                            }
                        }
                    }
                    if (isAdded) {
                        dataObject.addRow(payloadRow);
                    }
                    else {
                        dataObject.updateRow(payloadRow);
                    }
                    if (isSubRowAdded) {
                        dataObject.addRow(payloadSubRow);
                    }
                    else {
                        dataObject.updateRow(payloadSubRow);
                    }
                    if (dynaJSON.has("PROXY_SETTINGS")) {
                        if (isProxyAdded) {
                            dataObject.addRow(proxyRow);
                        }
                        else {
                            dataObject.updateRow(proxyRow);
                        }
                    }
                    if (subTableName.equals("VpnIKEv2")) {
                        final Object configDataItem = payloadRow.get(1);
                        final Object vpnTypePolicyId = payloadSubRow.get(1);
                        this.insertPolicyRelation(dataObject, configDataItem, vpnTypePolicyId);
                        final Long certificateIDPolicy = (Long)subConfigForm.opt("CLIENT_CERT_ID");
                        if (certificateIDPolicy != null && certificateIDPolicy != -1L) {
                            this.insertCertificateToPolicy(dataObject, certificateIDPolicy, vpnTypePolicyId);
                        }
                    }
                    if (tableName.equals("VpnPolicy") && dynaJSON.optBoolean("ENABLE_VPN_ON_DEMAND")) {
                        final String jsonString = dynaJSON.optString("ONDEMANDRULE");
                        final JSONArray jsonObj = new JSONArray(jsonString);
                        this.modifyVpnOnDemandRules(jsonObj, dataObject);
                    }
                    if (tableName.equals("VpnPolicy") && dynaJSON.has("VPN_TYPE") && dynaJSON.get("VPN_TYPE").equals(2)) {
                        final String vpnAppsStr = dynaJSON.optString("ALLOWED_APPS");
                        final JSONArray vpnAppsJSON = new JSONArray(vpnAppsStr);
                        if (String.valueOf(dynaJSON.get("CONFIG_NAME")).equals("WINDOWS_VPN_POLICY")) {
                            this.modifyAppLockPolicyApps(dataObject, vpnAppsJSON);
                        }
                        else {
                            new AppLockPolicyFormBean().modifyAppLockPolicyApps(dataObject, vpnAppsJSON, null);
                            this.modifyPerAppVPN(dataObject, dynaJSON);
                        }
                    }
                    if (tableName.equals("VpnPolicy")) {
                        final String customData = dynaJSON.optString("CUSTOM_DATA", "");
                        if (!MDMStringUtils.isEmpty(customData)) {
                            final JSONObject customDataJSON = new JSONObject(customData);
                            if (dataObject.containsTable("VpnCustomData")) {
                                dataObject.deleteRows("VpnCustomData", new Criteria(new Column("VpnCustomData", "CONFIG_DATA_ITEM_ID"), payloadRow.get("CONFIG_DATA_ITEM_ID"), 0));
                            }
                            final Iterator customDataIterator = customDataJSON.keys();
                            while (customDataIterator.hasNext()) {
                                final String customDataKey = customDataIterator.next();
                                final Row customDataRow = new Row("VpnCustomData");
                                customDataRow.set("KEY", (Object)customDataKey);
                                customDataRow.set("VALUE", customDataJSON.get(customDataKey));
                                customDataRow.set("CONFIG_DATA_ITEM_ID", payloadRow.get("CONFIG_DATA_ITEM_ID"));
                                dataObject.addRow(customDataRow);
                            }
                        }
                    }
                    if (dynaJSON.has("DISABLE_MAC_RANDOMIZE") || dynaJSON.has("SETUP_MODES")) {
                        MDMDBUtil.updateRow(dataObject, "AppleWifiPolicy", new Object[][] { { "CONFIG_DATA_ITEM_ID", configDataItemID }, { "DISABLE_MAC_RANDOMIZE", dynaJSON.optBoolean("DISABLE_MAC_RANDOMIZE", (boolean)Boolean.FALSE) }, { "SETUP_MODES", dynaJSON.optInt("SETUP_MODES") } });
                    }
                }
            }
        }
        catch (final Exception exp) {
            Logger.getLogger("MDMConfigLogger").log(Level.SEVERE, "Exception", exp);
            throw new SyMException(1002, exp.getCause());
        }
    }
    
    private Object modifySubConfigForm(final DataObject dataObject, final JSONObject subConfig, final String associationName, final boolean isadded) {
        try {
            final JSONObject subConfigValue = subConfig.getJSONObject(associationName);
            String tableName = (String)subConfigValue.get("TABLE_NAME");
            final String notToBeInsertedColumn = this.getTableDetails(tableName);
            Row subConfigRow;
            if (isadded) {
                subConfigRow = new Row(tableName);
            }
            else {
                if (associationName.equalsIgnoreCase("IKE_SA_ID") || associationName.equalsIgnoreCase("CHILD_SA_ID")) {
                    tableName = associationName;
                }
                subConfigRow = dataObject.getFirstRow(tableName);
            }
            final List subConfigcolumns = subConfigRow.getColumns();
            for (int i = 0; i < subConfigcolumns.size(); ++i) {
                final String columnName = subConfigcolumns.get(i);
                if (columnName != null && !columnName.equalsIgnoreCase(notToBeInsertedColumn)) {
                    final Object columnValue = subConfigValue.opt(columnName);
                    subConfigRow.set(columnName, columnValue);
                }
            }
            if (isadded) {
                dataObject.addRow(subConfigRow);
            }
            else {
                dataObject.updateRow(subConfigRow);
            }
            final Object subConfigId = subConfigRow.get(notToBeInsertedColumn);
            return subConfigId;
        }
        catch (final Exception ex) {
            Logger.getLogger("MDMConfigLogger").log(Level.SEVERE, null, ex);
            return null;
        }
    }
    
    private void deleteExistingVPNOnDemandRules(final DataObject dataObject) {
        try {
            dataObject.deleteRows("VpnNWRuleToPolicyRel", (Criteria)null);
            dataObject.deleteRows("VpnODRulesForConEval", (Criteria)null);
            dataObject.deleteRows("VPNOnDemandPolicy", (Criteria)null);
        }
        catch (final Exception e) {
            VPNPolicyFormBean.logger.log(Level.WARNING, "Exception occured in deleteExistingVPNOnDemandRules....", e);
        }
    }
    
    private void modifyVpnOnDemandRules(final JSONArray jsonArray, final DataObject dataObject) throws JSONException, DataAccessException {
        VPNPolicyFormBean.logger.log(Level.INFO, "jsonObj {0}", jsonArray);
        int orderOfPolicy = 1;
        final Object configId = dataObject.getValue("ConfigDataItem", "CONFIG_DATA_ITEM_ID", (Criteria)null);
        if (!(configId instanceof UniqueValueHolder)) {
            this.deleteExistingVPNOnDemandRules(dataObject);
        }
        for (int i = 0; i < jsonArray.length(); ++i) {
            final JSONObject json = (JSONObject)jsonArray.get(i);
            final Iterator keys = json.keys();
            final String actionName = keys.next();
            final Row VPNOnDemandPolicyRow = new Row("VPNOnDemandPolicy");
            VPNOnDemandPolicyRow.set("CONFIG_DATA_ITEM_ID", configId);
            VPNOnDemandPolicyRow.set("ONDEMAND_ACTION", (Object)actionName);
            VPNOnDemandPolicyRow.set("ON_DEMAND_RULE_ORDER", (Object)(orderOfPolicy++));
            dataObject.addRow(VPNOnDemandPolicyRow);
            final JSONArray array = (JSONArray)json.opt(actionName);
            for (int j = 0; j < array.length(); ++j) {
                final JSONObject TableRow = (JSONObject)array.get(j);
                final Row row = this.jsonToRow(TableRow);
                dataObject.addRow(row);
                Row ruleTableRow;
                if (actionName.equalsIgnoreCase("ConnectIfNeeded") || actionName.equalsIgnoreCase("NeverConnect")) {
                    ruleTableRow = new Row("VpnConEvalRuleToPolicyRel");
                    ruleTableRow.set("CONN_RULE_ID", row.get("CONN_RULE_ID"));
                    ruleTableRow.set("VPN_OD_POLICY_ID", VPNOnDemandPolicyRow.get("VPN_OD_POLICY_ID"));
                }
                else {
                    ruleTableRow = new Row("VpnNWRuleToPolicyRel");
                    ruleTableRow.set("NW_CHANGE_RULE_ID", row.get("NW_CHANGE_RULE_ID"));
                    ruleTableRow.set("VPN_OD_POLICY_ID", VPNOnDemandPolicyRow.get("VPN_OD_POLICY_ID"));
                }
                dataObject.addRow(ruleTableRow);
            }
        }
    }
    
    private void insertPolicyRelation(final DataObject dataObject, final Object configDataItem, final Object vpnTypePolicyId) {
        try {
            if (!dataObject.containsTable("VpnToPolicyRel")) {
                final Row policyToRelRow = new Row("VpnToPolicyRel");
                policyToRelRow.set("CONFIG_DATA_ITEM_ID", configDataItem);
                policyToRelRow.set("VPN_POLICY_ID", vpnTypePolicyId);
                dataObject.addRow(policyToRelRow);
            }
            else if (dataObject.containsTable("VpnToPolicyRel")) {
                final Criteria criteria = new Criteria(Column.getColumn("VpnToPolicyRel", "CONFIG_DATA_ITEM_ID"), configDataItem, 0);
                final Row policyToRelRow = dataObject.getRow("VpnToPolicyRel", criteria);
                policyToRelRow.set("VPN_POLICY_ID", vpnTypePolicyId);
                dataObject.updateRow(policyToRelRow);
            }
        }
        catch (final DataAccessException ex) {
            Logger.getLogger("MDMConfigLogger").log(Level.SEVERE, "Error at creating vpnpolicyto Relation", (Throwable)ex);
        }
    }
    
    private void insertCertificateToPolicy(final DataObject dataObject, final Object certificatePolicyID, final Object vpnTyprPolicyId) {
        try {
            final boolean isAdded = dataObject.containsTable("VpnPolicyToCertificate");
            if (isAdded) {
                final Criteria criteria = new Criteria(Column.getColumn("VpnPolicyToCertificate", "VPN_POLICY_ID"), vpnTyprPolicyId, 0);
                final Row certificateToPolicyRow = dataObject.getRow("VpnPolicyToCertificate", criteria);
                certificateToPolicyRow.set("CLIENT_CERT_ID", certificatePolicyID);
                dataObject.updateRow(certificateToPolicyRow);
            }
            else {
                final Row certificateToPolicyRow = new Row("VpnPolicyToCertificate");
                certificateToPolicyRow.set("VPN_POLICY_ID", vpnTyprPolicyId);
                certificateToPolicyRow.set("CLIENT_CERT_ID", certificatePolicyID);
                dataObject.addRow(certificateToPolicyRow);
            }
        }
        catch (final DataAccessException ex) {
            Logger.getLogger("MDMConfigLogger").log(Level.SEVERE, "Error at creating inserting certificate policy....", (Throwable)ex);
        }
    }
    
    @Override
    public void cloneConfigDO(final Integer configID, final DataObject configDOFromDB, final DataObject cloneConfigDO) throws DataAccessException {
        super.cloneConfigDO(configID, configDOFromDB, cloneConfigDO);
        final Object configDataItemId = cloneConfigDO.getValue("ConfigDataItem", "CONFIG_DATA_ITEM_ID", new Criteria(Column.getColumn("ConfigData", "CONFIG_ID"), (Object)new Integer(configID), 0));
        VPNPolicyFormBean.logger.log(Level.INFO, " configDOFromDB {0}", configDOFromDB);
        if (configID == 176 || configID == 521 || configID == 766) {
            if (this.isVPNIKEV2(configDOFromDB, "VpnIKEv2")) {
                this.cloneSubVPNType("VpnIKEv2", configDOFromDB, cloneConfigDO, configDataItemId);
            }
            final Iterator customDatas = configDOFromDB.getRows("VpnCustomData");
            while (customDatas.hasNext()) {
                final Row customDataRow = customDatas.next();
                final Row clonedRow = new Row("VpnCustomData");
                final Object newPolicyId = this.cloneRow(customDataRow, clonedRow, "CUSTOM_DATA_ID");
                clonedRow.set("CONFIG_DATA_ITEM_ID", configDataItemId);
                VPNPolicyFormBean.logger.log(Level.INFO, "custom data clonedRow {0}", clonedRow);
                cloneConfigDO.addRow(clonedRow);
            }
            VPNPolicyFormBean.logger.log(Level.INFO, "configDOFromDB {0}", configDOFromDB);
            final Iterator it = configDOFromDB.getRows("VPNOnDemandPolicy");
            while (it.hasNext()) {
                final Row vpnOnDemanRow = it.next();
                final Row clonedRow2 = new Row("VPNOnDemandPolicy");
                final Object newPolicyId2 = this.cloneRow(vpnOnDemanRow, clonedRow2, "VPN_OD_POLICY_ID");
                clonedRow2.set("CONFIG_DATA_ITEM_ID", configDataItemId);
                VPNPolicyFormBean.logger.log(Level.INFO, "clonedRow {0}", clonedRow2);
                cloneConfigDO.addRow(clonedRow2);
                final Criteria nwRulecriteria = new Criteria(new Column("VpnNWRuleToPolicyRel", "VPN_OD_POLICY_ID"), vpnOnDemanRow.get("VPN_OD_POLICY_ID"), 0);
                final Join nwRulejoin = new Join("VpnNWRuleToPolicyRel", "VpnODRulesForNWChange", new String[] { "NW_CHANGE_RULE_ID" }, new String[] { "NW_CHANGE_RULE_ID" }, 2);
                final Iterator nwRuleTableIterator = configDOFromDB.getRows("VpnODRulesForNWChange", nwRulecriteria, nwRulejoin);
                while (nwRuleTableIterator.hasNext()) {
                    final Row ruleTableRow = nwRuleTableIterator.next();
                    final Row clonedRuleTableRow = new Row("VpnODRulesForNWChange");
                    final Object newRuleID = this.cloneRow(ruleTableRow, clonedRuleTableRow, "NW_CHANGE_RULE_ID");
                    cloneConfigDO.addRow(clonedRuleTableRow);
                    final Row vpnNwRuleToDictRow = new Row("VpnNWRuleToPolicyRel");
                    vpnNwRuleToDictRow.set("NW_CHANGE_RULE_ID", newRuleID);
                    vpnNwRuleToDictRow.set("VPN_OD_POLICY_ID", newPolicyId2);
                    cloneConfigDO.addRow(vpnNwRuleToDictRow);
                }
                final Criteria connAttemptcriteria = new Criteria(new Column("VpnConEvalRuleToPolicyRel", "VPN_OD_POLICY_ID"), vpnOnDemanRow.get("VPN_OD_POLICY_ID"), 0);
                final Join connAttemptjoin = new Join("VpnConEvalRuleToPolicyRel", "VpnODRulesForConEval", new String[] { "CONN_RULE_ID" }, new String[] { "CONN_RULE_ID" }, 2);
                final Iterator connAttemptTblIterator = configDOFromDB.getRows("VpnODRulesForConEval", connAttemptcriteria, connAttemptjoin);
                while (connAttemptTblIterator.hasNext()) {
                    final Row ruleTableRow2 = connAttemptTblIterator.next();
                    final Row clonedRuleTableRow2 = new Row("VpnODRulesForConEval");
                    final Object newRuleID2 = this.cloneRow(ruleTableRow2, clonedRuleTableRow2, "CONN_RULE_ID");
                    cloneConfigDO.addRow(clonedRuleTableRow2);
                    final Row connAttemptRelRow = new Row("VpnConEvalRuleToPolicyRel");
                    connAttemptRelRow.set("CONN_RULE_ID", newRuleID2);
                    connAttemptRelRow.set("VPN_OD_POLICY_ID", newPolicyId2);
                    cloneConfigDO.addRow(connAttemptRelRow);
                }
            }
        }
        VPNPolicyFormBean.logger.log(Level.INFO, " cloneConfigDO {0}", cloneConfigDO);
    }
    
    public boolean isVPNIKEV2(final DataObject configDOFromDB, final String vpnType) {
        boolean isMatch = false;
        try {
            final Iterator payloadIterator = configDOFromDB.getRows(vpnType);
            isMatch = payloadIterator.hasNext();
        }
        catch (final DataAccessException ex) {
            Logger.getLogger(VPNPolicyFormBean.class.getName()).log(Level.SEVERE, null, (Throwable)ex);
        }
        return isMatch;
    }
    
    public String getTableDetails(final String tableName) {
        String tableColumn = null;
        if (tableName.equals("VpnIKEv2") || tableName.equals("VpnPolicyToCertificate")) {
            tableColumn = "VPN_POLICY_ID";
        }
        else if (tableName.equals("IKESAParams") || tableName.equals("CHILD_SA_ID") || tableName.equals("IKE_SA_ID")) {
            tableColumn = "SECURITY_ASSOCIATION_ID";
        }
        else {
            tableColumn = "CONFIG_DATA_ITEM_ID";
        }
        return tableColumn;
    }
    
    private void cloneSubVPNType(final String tableName, final DataObject configDOFromDB, final DataObject cloneConfigDO, final Object configDataItemId) {
        try {
            final String notToInsertColumn = this.getTableDetails(tableName);
            final Iterator iKEVIterator = configDOFromDB.getRows(tableName);
            while (iKEVIterator.hasNext()) {
                final Row iKEVRow = iKEVIterator.next();
                final Row clonediKEVRow = new Row("VpnIKEv2");
                final List columns = iKEVRow.getColumns();
                String columnName = null;
                for (int i = 0; i < columns.size(); ++i) {
                    columnName = columns.get(i);
                    if (columnName != null && !columnName.equals(notToInsertColumn)) {
                        if (columnName.equalsIgnoreCase("IKE_SA_ID") || columnName.equalsIgnoreCase("CHILD_SA_ID")) {
                            final Object columnValue = this.cloneSubConfig(configDOFromDB, cloneConfigDO, columnName);
                            clonediKEVRow.set(columnName, columnValue);
                        }
                        else {
                            final Object columnValue = iKEVRow.get(columnName);
                            clonediKEVRow.set(columnName, columnValue);
                        }
                    }
                }
                final Row clonedVpnToPolicyRel = new Row("VpnToPolicyRel");
                clonedVpnToPolicyRel.set("CONFIG_DATA_ITEM_ID", configDataItemId);
                clonedVpnToPolicyRel.set("VPN_POLICY_ID", clonediKEVRow.get("VPN_POLICY_ID"));
                if (configDOFromDB.containsTable("VpnPolicyToCertificate")) {
                    final Row policyToCertificateRow = configDOFromDB.getFirstRow("VpnPolicyToCertificate");
                    final Row clonedPolicyToCertificateRow = new Row("VpnPolicyToCertificate");
                    clonedPolicyToCertificateRow.set("CLIENT_CERT_ID", policyToCertificateRow.get("CLIENT_CERT_ID"));
                    clonedPolicyToCertificateRow.set("VPN_POLICY_ID", clonediKEVRow.get("VPN_POLICY_ID"));
                    cloneConfigDO.addRow(clonedPolicyToCertificateRow);
                }
                cloneConfigDO.addRow(clonedVpnToPolicyRel);
                cloneConfigDO.addRow(clonediKEVRow);
            }
        }
        catch (final Exception ex) {
            Logger.getLogger("MDMConfigLogger").log(Level.SEVERE, "Error while cloning IKEV2", ex);
        }
    }
    
    private Object cloneSubConfig(final DataObject configDOFromDB, final DataObject cloneConfigDO, String tableName) {
        Object clonedSubConfigId = null;
        try {
            final Iterator clonedSubConfigIterator = configDOFromDB.getRows(tableName);
            while (clonedSubConfigIterator.hasNext()) {
                final Row SubConfigRow = clonedSubConfigIterator.next();
                tableName = this.checkTableName(tableName);
                final Row clonedSubConfigRow = new Row(tableName);
                final String notToInsertColumn = this.getTableDetails(tableName);
                clonedSubConfigId = this.cloneRow(SubConfigRow, clonedSubConfigRow, notToInsertColumn);
                cloneConfigDO.addRow(clonedSubConfigRow);
            }
        }
        catch (final DataAccessException ex) {
            Logger.getLogger(VPNPolicyFormBean.class.getName()).log(Level.SEVERE, null, (Throwable)ex);
        }
        return clonedSubConfigId;
    }
    
    private String checkTableName(String tableName) {
        if (tableName.equalsIgnoreCase("CHILD_SA_ID") || tableName.equalsIgnoreCase("IKE_SA_ID")) {
            tableName = "IKESAParams";
        }
        return tableName;
    }
    
    private void modifyPerAppVPN(final DataObject dataObject, final JSONObject dynaJSON) throws DataAccessException {
        final Iterator vpnPolicyRowItr = dataObject.getRows("VpnPolicy");
        while (vpnPolicyRowItr.hasNext()) {
            final Row vpnPolicyRow = vpnPolicyRowItr.next();
            vpnPolicyRow.set("VPNUUID", (Object)UUID.randomUUID().toString());
            dataObject.updateRow(vpnPolicyRow);
        }
    }
    
    private Row createPolicyAppRow(final Object configId, final Long appId) throws Exception {
        final Row policyToAppRow = new Row("WindowsKioskPolicyApps");
        policyToAppRow.set("CONFIG_DATA_ITEM_ID", configId);
        policyToAppRow.set("APP_GROUP_ID", (Object)appId);
        return policyToAppRow;
    }
    
    private Row createPolicySystemAppRow(final Object configId, final Long appId) throws Exception {
        final Row policyToAppRow = new Row("WindowsKioskPolicySystemApps");
        policyToAppRow.set("CONFIG_DATA_ITEM_ID", configId);
        policyToAppRow.set("APP_ID", (Object)appId);
        return policyToAppRow;
    }
    
    public void modifyAppLockPolicyApps(final DataObject dataObject, final JSONArray allowedAppsJson) throws Exception {
        final Object configId = dataObject.getValue("ConfigDataItem", "CONFIG_DATA_ITEM_ID", (Criteria)null);
        if (!(configId instanceof UniqueValueHolder)) {
            this.deleteExistingAppLockPolicyApps(dataObject);
        }
        if (allowedAppsJson != null) {
            for (int i = 0; i < allowedAppsJson.length(); ++i) {
                final JSONObject appJson = new JSONObject(allowedAppsJson.get(i).toString());
                final Boolean isSystemApp = appJson.optBoolean("IS_SYSTEM_APP", false);
                if (isSystemApp) {
                    final Long appId = Long.parseLong(appJson.optString("APP_ID"));
                    dataObject.addRow(this.createPolicySystemAppRow(configId, appId));
                }
                else {
                    final Long appId = Long.parseLong(appJson.optString("APP_ID"));
                    dataObject.addRow(this.createPolicyAppRow(configId, appId));
                }
            }
        }
    }
    
    private void deleteExistingAppLockPolicyApps(final DataObject dataObject) {
        try {
            dataObject.deleteRows("WindowsKioskPolicySystemApps", (Criteria)null);
            dataObject.deleteRows("WindowsKioskPolicyApps", (Criteria)null);
        }
        catch (final Exception e) {
            VPNPolicyFormBean.logger.log(Level.SEVERE, "AppLockPolicyFormBean: Error while deleteExistingAppLockPolicyApps() ", e);
        }
    }
    
    static {
        VPNPolicyFormBean.logger = Logger.getLogger(VPNPolicyFormBean.class.getName());
    }
}
