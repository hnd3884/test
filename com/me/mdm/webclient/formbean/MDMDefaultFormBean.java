package com.me.mdm.webclient.formbean;

import org.apache.commons.lang3.RandomStringUtils;
import com.me.mdm.server.util.MDMFeatureParamsHandler;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import java.util.ArrayList;
import com.me.mdm.server.profiles.config.ProfileConfigurationUtil;
import com.me.mdm.server.config.MDMConfigUtil;
import org.json.JSONException;
import com.adventnet.persistence.DataAccessException;
import com.me.mdm.server.payload.PayloadException;
import java.util.List;
import java.util.Iterator;
import java.util.logging.Level;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.persistence.Row;
import java.util.HashMap;
import java.util.Map;
import com.me.mdm.server.config.PayloadProperty;
import com.me.devicemanagement.framework.server.exception.SyMException;
import com.adventnet.persistence.DataObject;
import org.json.JSONObject;
import java.util.logging.Logger;

public class MDMDefaultFormBean
{
    public static Logger logger;
    public boolean isAdded;
    
    public MDMDefaultFormBean() {
        this.isAdded = false;
    }
    
    public DataObject getDataObject(final JSONObject multipleConfigForm, final JSONObject[] dynaActionForm, final DataObject dataObject) throws SyMException {
        this.dynaFormToDO(multipleConfigForm, dynaActionForm, dataObject);
        return dataObject;
    }
    
    public DataObject getModifiedDO(final JSONObject multipleConfigForm, final JSONObject[] dynaActionForm, final DataObject dataObject) throws SyMException {
        this.dynaFormToDO(multipleConfigForm, dynaActionForm, dataObject);
        return dataObject;
    }
    
    protected boolean getTransformedFormPropertyValue(final JSONObject mutlipleConfigForm, final JSONObject dynaForm, final PayloadProperty policyRowData) throws Exception {
        return true;
    }
    
    public Map getTableListDetails(final Integer configID) {
        final Map tableDetails = new HashMap();
        if (configID != null) {
            switch (configID) {
                case 172:
                case 757: {
                    tableDetails.put("PasscodePolicy", "CONFIG_DATA_ITEM_ID");
                    break;
                }
                case 765: {
                    tableDetails.put("ADCertPolicy", "CONFIG_DATA_ITEM_ID");
                    break;
                }
                case 173:
                case 751:
                case 951: {
                    tableDetails.put("RestrictionsPolicy", "CONFIG_DATA_ITEM_ID");
                    break;
                }
                case 754: {
                    tableDetails.put("MacPPPCPolicy", "CONFIG_DATA_ITEM_ID");
                    break;
                }
                case 174: {
                    tableDetails.put("EMailPolicy", "CONFIG_DATA_ITEM_ID");
                    break;
                }
                case 175: {
                    tableDetails.put("ExchangeActiveSyncPolicy", "CONFIG_DATA_ITEM_ID");
                    break;
                }
                case 176:
                case 521:
                case 756:
                case 766: {
                    tableDetails.put("VpnPolicy", "CONFIG_DATA_ITEM_ID");
                    tableDetails.put("VpnL2TP", "CONFIG_DATA_ITEM_ID");
                    tableDetails.put("VpnPPTP", "CONFIG_DATA_ITEM_ID");
                    tableDetails.put("VpnIPSec", "CONFIG_DATA_ITEM_ID");
                    tableDetails.put("VpnCisco", "CONFIG_DATA_ITEM_ID");
                    tableDetails.put("VpnJuniperSSL", "CONFIG_DATA_ITEM_ID");
                    tableDetails.put("VpnF5SSL", "CONFIG_DATA_ITEM_ID");
                    tableDetails.put("VpnCustomSSL", "CONFIG_DATA_ITEM_ID");
                    tableDetails.put("AppLockPolicyApps", "CONFIG_DATA_ITEM_ID");
                    break;
                }
                case 177:
                case 774: {
                    tableDetails.put("WifiPolicy", "CONFIG_DATA_ITEM_ID");
                    tableDetails.put("WifiNonEnterprise", "CONFIG_DATA_ITEM_ID");
                    tableDetails.put("WifiEnterprise", "CONFIG_DATA_ITEM_ID");
                    break;
                }
                case 178: {
                    tableDetails.put("LdapPolicy", "CONFIG_DATA_ITEM_ID");
                    break;
                }
                case 179: {
                    tableDetails.put("CalDAVPolicy", "CONFIG_DATA_ITEM_ID");
                    break;
                }
                case 180: {
                    tableDetails.put("SubscibedCalendarPolicy", "CONFIG_DATA_ITEM_ID");
                    break;
                }
                case 181: {
                    tableDetails.put("CardDAVPolicy", "CONFIG_DATA_ITEM_ID");
                    break;
                }
                case 182:
                case 560: {
                    tableDetails.put("WebClipToConfigRel", "CONFIG_DATA_ITEM_ID");
                    break;
                }
                case 183: {
                    tableDetails.put("AppLockPolicy", "CONFIG_DATA_ITEM_ID");
                    tableDetails.put("AppLockPolicyApps", "CONFIG_DATA_ITEM_ID");
                    tableDetails.put("ScreenLayoutSettings", "CONFIG_DATA_ITEM_ID");
                    tableDetails.put("WebClipToConfigRel", "CONFIG_DATA_ITEM_ID");
                    break;
                }
                case 184:
                case 559:
                case 768: {
                    tableDetails.put("GlobalHttpProxyPolicy", "CONFIG_DATA_ITEM_ID");
                    break;
                }
                case 187: {
                    tableDetails.put("ApnPolicy", "CONFIG_DATA_ITEM_ID");
                    break;
                }
                case 188:
                case 561:
                case 707:
                case 758: {
                    tableDetails.put("IOSWebContentPolicy", "CONFIG_DATA_ITEM_ID");
                    break;
                }
                case 515:
                case 555:
                case 607:
                case 703:
                case 772: {
                    tableDetails.put("CertificatePolicy", "CONFIG_DATA_ITEM_ID");
                    break;
                }
                case 516:
                case 566:
                case 606:
                case 773: {
                    tableDetails.put("SCEPPolicy", "CONFIG_DATA_ITEM_ID");
                    break;
                }
                case 185: {
                    tableDetails.put("AndroidPasscodePolicy", "CONFIG_DATA_ITEM_ID");
                    break;
                }
                case 186: {
                    tableDetails.put("AndroidRestrictionsPolicy", "CONFIG_DATA_ITEM_ID");
                    break;
                }
                case 556:
                case 605: {
                    tableDetails.put("WifiPolicy", "CONFIG_DATA_ITEM_ID");
                    tableDetails.put("WifiNonEnterprise", "CONFIG_DATA_ITEM_ID");
                    tableDetails.put("WifiEnterprise", "CONFIG_DATA_ITEM_ID");
                    break;
                }
                case 553: {
                    tableDetails.put("AndroidEMailPolicy", "CONFIG_DATA_ITEM_ID");
                    break;
                }
                case 554: {
                    tableDetails.put("AndroidActiveSyncPolicy", "CONFIG_DATA_ITEM_ID");
                    break;
                }
                case 557: {
                    tableDetails.put("AndroidKioskPolicy", "CONFIG_DATA_ITEM_ID");
                    tableDetails.put("AndroidKioskPolicyApps", "CONFIG_DATA_ITEM_ID");
                    tableDetails.put("KioskCustomSettings", "CONFIG_DATA_ITEM_ID");
                    tableDetails.put("AndroidKioskPolicyBackgroundApps", "CONFIG_DATA_ITEM_ID");
                    tableDetails.put("ScreenLayoutSettings", "CONFIG_DATA_ITEM_ID");
                    tableDetails.put("WebClipToConfigRel", "CONFIG_DATA_ITEM_ID");
                    break;
                }
                case 518:
                case 558: {
                    tableDetails.put("MDMWallpaperPolicy", "CONFIG_DATA_ITEM_ID");
                    break;
                }
                case 562: {
                    tableDetails.put("ApnPolicy", "CONFIG_DATA_ITEM_ID");
                    tableDetails.put("AndroidApnPolicyExtn", "CONFIG_DATA_ITEM_ID");
                    break;
                }
                case 563: {
                    tableDetails.put("AgentMigrationDetails", "CONFIG_DATA_ITEM_ID");
                    break;
                }
                case 565: {
                    tableDetails.put("AndroidEFRPPolicy", "CONFIG_DATA_ITEM_ID");
                    break;
                }
                case 301: {
                    tableDetails.put("InstallAppPolicy", "CONFIG_DATA_ITEM_ID");
                    break;
                }
                case 601: {
                    tableDetails.put("WpPasscodePolicy", "CONFIG_DATA_ITEM_ID");
                    break;
                }
                case 602: {
                    tableDetails.put("WpEmailPolicy", "CONFIG_DATA_ITEM_ID");
                    break;
                }
                case 603: {
                    tableDetails.put("WpExchangeActiveSyncPolicy", "CONFIG_DATA_ITEM_ID");
                    break;
                }
                case 604: {
                    tableDetails.put("WpRestrictionsPolicy", "CONFIG_DATA_ITEM_ID");
                    break;
                }
                case 517: {
                    tableDetails.put("ManagedWebDomainPolicy", "CONFIG_DATA_ITEM_ID");
                    break;
                }
                case 519:
                case 769: {
                    tableDetails.put("AirPrintPolicy", "CONFIG_DATA_ITEM_ID");
                    break;
                }
                case 771: {
                    tableDetails.put("DirectoryBindConfig", "CONFIG_DATA_ITEM_ID");
                    break;
                }
                case 752: {
                    tableDetails.put("MdMacAccountConfigPolicy", "CONFIG_DATA_ITEM_ID");
                    break;
                }
                case 770: {
                    tableDetails.put("MacFileVault2Policy", "CONFIG_DATA_ITEM_ID");
                    break;
                }
                case 753: {
                    tableDetails.put("MacFirmwarePolicy", "CONFIG_DATA_ITEM_ID");
                    break;
                }
                case 755: {
                    tableDetails.put("MacSystemExtnPolicy", "CONFIG_DATA_ITEM_ID");
                    break;
                }
                case 520: {
                    tableDetails.put("SSOAccountPolicy", "CONFIG_DATA_ITEM_ID");
                    tableDetails.put("SSOKerberosAccount", "CONFIG_DATA_ITEM_ID");
                    tableDetails.put("SSOApps", "CONFIG_DATA_ITEM_ID");
                    tableDetails.put("SSOToCertificateRel", "CONFIG_DATA_ITEM_ID");
                    break;
                }
                case 564: {
                    tableDetails.put("VpnPolicy", "CONFIG_DATA_ITEM_ID");
                    tableDetails.put("VpnL2TP", "CONFIG_DATA_ITEM_ID");
                    tableDetails.put("VpnPPTP", "CONFIG_DATA_ITEM_ID");
                    tableDetails.put("VpnIPSec", "CONFIG_DATA_ITEM_ID");
                    tableDetails.put("VpnCisco", "CONFIG_DATA_ITEM_ID");
                    tableDetails.put("VpnJuniperSSL", "CONFIG_DATA_ITEM_ID");
                    tableDetails.put("VpnF5SSL", "CONFIG_DATA_ITEM_ID");
                    tableDetails.put("VpnPaloAlto", "CONFIG_DATA_ITEM_ID");
                }
                case 702: {
                    tableDetails.put("EthernetConfig", "CONFIG_DATA_ITEM_ID");
                    tableDetails.put("PayloadProxyConfig", "CONFIG_DATA_ITEM_ID");
                    tableDetails.put("PayloadWifiEnterprise", "CONFIG_DATA_ITEM_ID");
                    break;
                }
                case 705: {
                    tableDetails.put("ChromeKioskPolicy", "CONFIG_DATA_ITEM_ID");
                    tableDetails.put("ChromeKioskPolicyApps", "CONFIG_DATA_ITEM_ID");
                    break;
                }
                case 706: {
                    tableDetails.put("ChromeRestrictionPolicy", "CONFIG_DATA_ITEM_ID");
                    break;
                }
                case 709: {
                    tableDetails.put("ManagedBookmarksPolicy", "CONFIG_DATA_ITEM_ID");
                    break;
                }
                case 708: {
                    tableDetails.put("PowerManagementSettings", "CONFIG_DATA_ITEM_ID");
                    break;
                }
                case 710: {
                    tableDetails.put("ChromeUserRestrictions", "CONFIG_DATA_ITEM_ID");
                    break;
                }
                case 608: {
                    tableDetails.put("WindowsKioskPolicy", "CONFIG_DATA_ITEM_ID");
                    tableDetails.put("WindowsKioskPolicyApps", "CONFIG_DATA_ITEM_ID");
                    tableDetails.put("WindowsKioskPolicySystemApps", "CONFIG_DATA_ITEM_ID");
                }
                case 701: {
                    tableDetails.put("WifiPolicy", "CONFIG_DATA_ITEM_ID");
                    tableDetails.put("WifiNonEnterprise", "CONFIG_DATA_ITEM_ID");
                    tableDetails.put("WifiEnterprise", "CONFIG_DATA_ITEM_ID");
                    tableDetails.put("PayloadProxyConfig", "CONFIG_DATA_ITEM_ID");
                    break;
                }
                case 704: {
                    tableDetails.put("VpnPolicy", "CONFIG_DATA_ITEM_ID");
                    tableDetails.put("VpnL2TP", "CONFIG_DATA_ITEM_ID");
                    tableDetails.put("OpenVPNPolicy", "CONFIG_DATA_ITEM_ID");
                    tableDetails.put("PayloadProxyConfig", "CONFIG_DATA_ITEM_ID");
                    break;
                }
                case 711: {
                    tableDetails.put("VerifyAccessAPIConfig", "CONFIG_DATA_ITEM_ID");
                    break;
                }
                case 712: {
                    tableDetails.put("BrowserConfiguration", "CONFIG_DATA_ITEM_ID");
                    break;
                }
                case 713: {
                    tableDetails.put("ApplicationPolicyConfig", "CONFIG_DATA_ITEM_ID");
                    break;
                }
                case 714: {
                    tableDetails.put("ManagedGuestSession", "CONFIG_DATA_ITEM_ID");
                    break;
                }
                case 609: {
                    tableDetails.put("VpnPolicy", "CONFIG_DATA_ITEM_ID");
                    tableDetails.put("VpnL2TP", "CONFIG_DATA_ITEM_ID");
                    tableDetails.put("VpnPPTP", "CONFIG_DATA_ITEM_ID");
                    tableDetails.put("VpnIPSec", "CONFIG_DATA_ITEM_ID");
                    tableDetails.put("VpnCisco", "CONFIG_DATA_ITEM_ID");
                    tableDetails.put("VpnJuniperSSL", "CONFIG_DATA_ITEM_ID");
                    tableDetails.put("VpnF5SSL", "CONFIG_DATA_ITEM_ID");
                    tableDetails.put("VpnCustomSSL", "CONFIG_DATA_ITEM_ID");
                    tableDetails.put("WindowsKioskPolicy", "CONFIG_DATA_ITEM_ID");
                    tableDetails.put("WindowsKioskPolicySystemApps", "CONFIG_DATA_ITEM_ID");
                    break;
                }
                case 610: {
                    tableDetails.put("DataProtectionPolicy", "CONFIG_DATA_ITEM_ID");
                    break;
                }
                case 525:
                case 612:
                case 767: {
                    tableDetails.put("CustomProfileToCfgDataItem", "CONFIG_DATA_ITEM_ID");
                    break;
                }
                case 611: {
                    tableDetails.put("WindowsLockdownPolicy", "CONFIG_DATA_ITEM_ID");
                    break;
                }
                case 901: {
                    tableDetails.put("DataTrackingPolicy", "CONFIG_DATA_ITEM_ID");
                    tableDetails.put("DataTrackingSSID", "SSID_TRACKING_ID");
                    break;
                }
                case 902: {
                    tableDetails.put("DataUsageActions", "CONFIG_DATA_ITEM_ID");
                    tableDetails.put("DataTrackingSSID", "SSID_TRACKING_ID");
                    break;
                }
                case 903: {
                    tableDetails.put("DataUsageLevels", "CONFIG_DATA_ITEM_ID");
                    tableDetails.put("DataTrackingSSID", "SSID_TRACKING_ID");
                    break;
                }
                case 613: {
                    tableDetails.put("BitlockerPolicyToCfgDataItem", "CONFIG_DATA_ITEM_ID");
                    break;
                }
                case 761: {
                    tableDetails.put("MacLoginWindow", "CONFIG_DATA_ITEM_ID");
                    tableDetails.put("MacLoginWindowSettings", "CONFIG_DATA_ITEM_ID");
                    tableDetails.put("MacScreenSaverSettings", "CONFIG_DATA_ITEM_ID");
                    break;
                }
                case 762: {
                    tableDetails.put("MacLoginWindowItemSettings", "CONFIG_DATA_ITEM_ID");
                    break;
                }
                case 526:
                case 763: {
                    tableDetails.put("CfgDataItemToFontRel", "CONFIG_DATA_ITEM_ID");
                    break;
                }
                case 764: {
                    tableDetails.put("MacGatekeeperPolicy", "CONFIG_DATA_ITEM_ID");
                    break;
                }
                case 759: {
                    tableDetails.put("MacSystemPreferencePolicy", "CONFIG_DATA_ITEM_ID");
                    break;
                }
                case 760: {
                    tableDetails.put("MacEnergySettingsPolicy", "CONFIG_DATA_ITEM_ID");
                    break;
                }
                case 302: {
                    tableDetails.put("ManagedAppConfigurationPolicy", "CONFIG_DATA_ITEM_ID");
                    tableDetails.put("AppConfigPolicy", "CONFIG_DATA_ITEM_ID");
                    break;
                }
                case 568: {
                    tableDetails.put("WorkDataSecurityPolicy", "CONFIG_DATA_ITEM_ID");
                    break;
                }
                case 527: {
                    tableDetails.put("SharedDeviceConfiguration", "CONFIG_DATA_ITEM_ID");
                    break;
                }
                case 529: {
                    tableDetails.put("IOSAccessibilitySettings", "CONFIG_DATA_ITEM_ID");
                    break;
                }
            }
        }
        return tableDetails;
    }
    
    public void dynaFormToDO(final JSONObject multipleConfigForm, final JSONObject[] dynaActionForm, final DataObject dataObject) throws SyMException, PayloadException {
        final int executionOrder = dynaActionForm.length;
        try {
            for (int k = 0; k < executionOrder; ++k) {
                final JSONObject dynaForm = dynaActionForm[k];
                final String tableName = (String)dynaForm.get("TABLE_NAME");
                final Long configDataItemID = dynaForm.optLong("CONFIG_DATA_ITEM_ID");
                if (tableName != null) {
                    if (this.populateMainTable(tableName)) {
                        final Row configData = dataObject.getRow("ConfigData");
                        final Object configDataID = configData.get("CONFIG_DATA_ID");
                        boolean isAdded = false;
                        Row payloadRow = null;
                        if (configDataItemID <= 0L) {
                            this.insertConfigDataItem(dynaForm, dataObject, k);
                            final Row dataItemRow = dataObject.getRow("ConfigDataItem");
                            payloadRow = new Row(tableName);
                            payloadRow.set("CONFIG_DATA_ITEM_ID", dataItemRow.get("CONFIG_DATA_ITEM_ID"));
                            isAdded = true;
                        }
                        else {
                            final Iterator configDataItemExtnRow = dataObject.getRows("MdConfigDataItemExtn");
                            if (!configDataItemExtnRow.hasNext()) {
                                this.insertConfigDataItemExtn(dynaForm, dataObject);
                            }
                            final Criteria criteria = new Criteria(Column.getColumn(tableName, "CONFIG_DATA_ITEM_ID"), (Object)configDataItemID, 0);
                            payloadRow = dataObject.getRow(tableName, criteria);
                        }
                        final PayloadProperty payloadProperty = new PayloadProperty();
                        dynaForm.put("_IS_CONF_ADDED", isAdded);
                        final List columns = payloadRow.getColumns();
                        for (int i = 0; i < columns.size(); ++i) {
                            payloadProperty.name = columns.get(i);
                            String columnName = payloadProperty.name;
                            payloadProperty.value = dynaForm.opt(columnName);
                            if (columnName != null && (payloadProperty.value != null || this.checkIfNullableColumn(columnName, dynaForm))) {
                                if (columnName.equals("CONFIG_DATA_ITEM_ID")) {
                                    columnName += "";
                                }
                                else {
                                    final boolean checkState = this.getTransformedFormPropertyValue(multipleConfigForm, dynaForm, payloadProperty);
                                    if (checkState) {
                                        payloadRow.set(columnName, payloadProperty.value);
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
                    }
                    else {
                        this.insertConfigDataItem(dynaForm, dataObject, k);
                    }
                }
            }
        }
        catch (final Exception exp) {
            MDMDefaultFormBean.logger.log(Level.SEVERE, "Exception while saving the config data item DO", exp);
            throw new SyMException(1002, exp.getCause());
        }
    }
    
    public void cloneConfigDO(final Integer configID, final DataObject configDOFromDB, final DataObject cloneConfigDO) throws DataAccessException {
        final Map tableDetails = this.getTableListDetails(configID);
        if (!configDOFromDB.isEmpty() && !cloneConfigDO.isEmpty() && tableDetails != null) {
            final Criteria criteria = new Criteria(Column.getColumn("ConfigData", "CONFIG_ID"), (Object)new Integer(configID), 0);
            final Object clonedConfigDataID = cloneConfigDO.getValue("ConfigData", "CONFIG_DATA_ID", criteria);
            final Iterator dataItemIterator = configDOFromDB.getRows("ConfigDataItem");
            while (dataItemIterator.hasNext()) {
                final Row configDataItemRow = dataItemIterator.next();
                if (configDataItemRow != null) {
                    final Row clonedConfigDataItemRow = this.insertClonedConfigDataItem(configID, configDOFromDB, cloneConfigDO);
                    if (clonedConfigDataItemRow == null) {
                        continue;
                    }
                    List columns = configDataItemRow.getColumns();
                    for (final String tableName : tableDetails.keySet()) {
                        if (this.populateMainTable(tableName)) {
                            final String notToBeclonedColumn = tableDetails.get(tableName);
                            if (!configDOFromDB.containsTable(tableName)) {
                                continue;
                            }
                            final Iterator payloadRowIteraot = configDOFromDB.getRows(tableName);
                            while (payloadRowIteraot.hasNext()) {
                                final Row payloadRow = payloadRowIteraot.next();
                                if (payloadRow != null) {
                                    final Row clonedPayloadRow = new Row(tableName);
                                    columns = payloadRow.getColumns();
                                    for (int i = 0; i < columns.size(); ++i) {
                                        final String columnName = columns.get(i);
                                        if (columnName.equals(notToBeclonedColumn)) {
                                            clonedPayloadRow.set(columnName, clonedConfigDataItemRow.get(columnName));
                                        }
                                        else if (payloadRow.get(columnName) != null) {
                                            clonedPayloadRow.set(columnName, payloadRow.get(columnName));
                                        }
                                    }
                                    cloneConfigDO.addRow(clonedPayloadRow);
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    
    protected Row jsonToRow(final JSONObject tableRow) throws JSONException, DataAccessException {
        final Iterator keys = tableRow.keys();
        final String key = keys.next();
        final Row row = new Row(key);
        final JSONObject TableColumns = (JSONObject)tableRow.get(key);
        final List columns = row.getColumns();
        for (int i = 1; i < columns.size(); ++i) {
            final String columnName = columns.get(i);
            final String columnValue = TableColumns.opt(columnName).toString();
            if (columnName != null && columnValue != null) {
                row.set(columnName, (Object)columnValue);
            }
        }
        return row;
    }
    
    protected Object cloneRow(final Row oldRow, final Row clonedRow, final String notToBeCloned) {
        final List columns = oldRow.getColumns();
        for (int i = 0; i < columns.size(); ++i) {
            String columnName = columns.get(i);
            if (!columnName.equals(notToBeCloned)) {
                columnName += "";
                clonedRow.set(columnName, oldRow.get(columnName));
            }
        }
        return clonedRow.get(notToBeCloned);
    }
    
    public void insertConfigDataItem(final JSONObject dynaForm, final DataObject dataObject, final int k) throws SyMException {
        try {
            final Long configDataItemID = dynaForm.optLong("CONFIG_DATA_ITEM_ID");
            final Row configData = dataObject.getRow("ConfigData");
            final Object configDataID = configData.get("CONFIG_DATA_ID");
            if (configDataItemID == null || configDataItemID <= 0L) {
                Row dataItemRow = null;
                dataItemRow = new Row("ConfigDataItem");
                dataItemRow.set("CONFIG_DATA_ID", configDataID);
                dataItemRow.set("EXECUTION_ORDER", (Object)k);
                dataObject.addRow(dataItemRow);
                dynaForm.put("CONFIG_DATA_ITEM_ID", dataItemRow.get("CONFIG_DATA_ITEM_ID"));
                this.insertConfigDataItemExtn(dynaForm, dataObject);
            }
        }
        catch (final Exception exp) {
            Logger.getLogger("MDMConfigLogger").log(Level.WARNING, "Exception while inserting configdata item", exp);
            throw new SyMException(1002, exp.getCause());
        }
    }
    
    public Row insertClonedConfigDataItem(final Integer configID, final DataObject configDOFromDB, DataObject cloneConfigDO) throws DataAccessException {
        final Map tableDetails = this.getTableListDetails(configID);
        if (!configDOFromDB.isEmpty() && !cloneConfigDO.isEmpty() && tableDetails != null) {
            final Criteria criteria = new Criteria(Column.getColumn("ConfigData", "CONFIG_ID"), (Object)new Integer(configID), 0);
            final Object clonedConfigDataID = cloneConfigDO.getValue("ConfigData", "CONFIG_DATA_ID", criteria);
            final Iterator dataItemIterator = configDOFromDB.getRows("ConfigDataItem");
            while (dataItemIterator.hasNext()) {
                final Row configDataItemRow = dataItemIterator.next();
                if (configDataItemRow != null) {
                    final Row clonedConfigDataItemRow = new Row("ConfigDataItem");
                    final List columns = configDataItemRow.getColumns();
                    for (int i = 0; i < columns.size(); ++i) {
                        String columnName = columns.get(i);
                        if (columnName.equals("CONFIG_DATA_ITEM_ID")) {
                            columnName += "";
                        }
                        else if (columnName.equals("CONFIG_DATA_ID")) {
                            clonedConfigDataItemRow.set(columnName, clonedConfigDataID);
                        }
                        else {
                            clonedConfigDataItemRow.set(columnName, configDataItemRow.get(columnName));
                        }
                    }
                    cloneConfigDO.addRow(clonedConfigDataItemRow);
                    final Row configDataItemExtRow = configDOFromDB.getRow("MdConfigDataItemExtn", new Criteria(Column.getColumn("MdConfigDataItemExtn", "CONFIG_DATA_ITEM_ID"), configDataItemRow.get("CONFIG_DATA_ITEM_ID"), 0));
                    final Row clonedProfileRow = cloneConfigDO.getFirstRow("Profile");
                    if (configDataItemExtRow != null) {
                        final Row clonedConfigDataItemExtRow = new Row("MdConfigDataItemExtn");
                        final List configDataItemExtRowColumns = clonedConfigDataItemExtRow.getColumns();
                        for (int j = 0; j < configDataItemExtRowColumns.size(); ++j) {
                            String columnName2 = configDataItemExtRowColumns.get(j);
                            if (columnName2.equals("CONFIG_DATA_ITEM_ID")) {
                                columnName2 += "";
                            }
                            else {
                                clonedConfigDataItemExtRow.set(columnName2, configDataItemExtRow.get(columnName2));
                            }
                        }
                        clonedConfigDataItemExtRow.set("CONFIG_DATA_ITEM_ID", clonedConfigDataItemRow.get("CONFIG_DATA_ITEM_ID"));
                        cloneConfigDO.addRow(clonedConfigDataItemExtRow);
                    }
                    else if (clonedProfileRow.get("PROFILE_PAYLOAD_IDENTIFIER") != null) {
                        try {
                            final String configName = MDMConfigUtil.getConfigLabel(configID);
                            final String payloadName = ProfileConfigurationUtil.getInstance().getPayloadName(configName);
                            final String configDataIdentifier = ProfileConfigurationUtil.getInstance().getConfigDataIdentifier(payloadName);
                            final String payloadIdentifier = (String)clonedProfileRow.get("PROFILE_PAYLOAD_IDENTIFIER") + "." + configDataIdentifier;
                            final JSONObject jsonObject = new JSONObject();
                            jsonObject.put("CONFIG_DATA_ITEM_ID", clonedConfigDataItemRow.get("CONFIG_DATA_ITEM_ID"));
                            jsonObject.put("PROFILE_PAYLOAD_IDENTIFIER", (Object)payloadIdentifier);
                            jsonObject.put("EXISTING_CONFIG_DATA_ITEM", true);
                            cloneConfigDO = this.insertConfigDataItemExtn(jsonObject, cloneConfigDO);
                        }
                        catch (final Exception ex) {
                            ex.printStackTrace();
                        }
                    }
                    return clonedConfigDataItemRow;
                }
            }
        }
        return null;
    }
    
    protected Object[] getSpecificColumnValue(final DataObject dataObject, final String tableName, final String columnName, final Object configDataItem) throws DataAccessException {
        final List<Object> instanceList = new ArrayList<Object>();
        final Criteria criteria = new Criteria(new Column(tableName, "CONFIG_DATA_ITEM_ID"), configDataItem, 0);
        final Iterator iterator = dataObject.getRows(tableName, criteria);
        while (iterator.hasNext()) {
            final Row row = iterator.next();
            final Object instance = row.get(columnName);
            instanceList.add(instance);
        }
        final Object[] urlInstanceArray = new Object[instanceList.size()];
        instanceList.toArray(urlInstanceArray);
        return urlInstanceArray;
    }
    
    protected boolean getTransformedPropertyValue(final DataObject dataObject, final PayloadProperty policyRowData) {
        return true;
    }
    
    protected Object constructFileUrl(Object columnValue) {
        final HashMap hm = new HashMap();
        hm.put("path", columnValue);
        hm.put("IS_SERVER", true);
        hm.put("IS_AUTHTOKEN", false);
        columnValue = ApiFactoryProvider.getFileAccessAPI().constructFileURL(hm);
        return columnValue;
    }
    
    public DataObject insertConfigDataItemExtn(final JSONObject jsonObject, final DataObject dataObject) throws DataAccessException, JSONException {
        if (MDMFeatureParamsHandler.getInstance().isFeatureEnabled("MigrationTarget") && jsonObject.has("CONFIG_PAYLOAD_IDENTIFIER")) {
            jsonObject.put("PROFILE_PAYLOAD_IDENTIFIER", jsonObject.get("CONFIG_PAYLOAD_IDENTIFIER"));
            jsonObject.put("EXISTING_CONFIG_DATA_ITEM", true);
        }
        if (jsonObject.has("PROFILE_PAYLOAD_IDENTIFIER")) {
            final StringBuilder payloadIdentifier = new StringBuilder(String.valueOf(jsonObject.get("PROFILE_PAYLOAD_IDENTIFIER")));
            if (!jsonObject.has("EXISTING_CONFIG_DATA_ITEM")) {
                payloadIdentifier.append(".").append(RandomStringUtils.randomAlphanumeric(3));
            }
            final Row configDataItemExtnRow = new Row("MdConfigDataItemExtn");
            configDataItemExtnRow.set("CONFIG_DATA_ITEM_ID", jsonObject.get("CONFIG_DATA_ITEM_ID"));
            configDataItemExtnRow.set("CONFIG_PAYLOAD_IDENTIFIER", (Object)payloadIdentifier.toString());
            dataObject.addRow(configDataItemExtnRow);
        }
        return dataObject;
    }
    
    protected void modifyDetails(final DataObject dataObject, final JSONObject dynaForm, final JSONObject multiConfigForm, final String tableName, final String columnName) throws Exception {
        boolean isAdded = false;
        Row payloadRow = null;
        if (!dataObject.containsTable(tableName)) {
            payloadRow = new Row(tableName);
            isAdded = true;
        }
        else {
            payloadRow = dataObject.getFirstRow(tableName);
        }
        final PayloadProperty payloadProperty = new PayloadProperty();
        final List columnList = payloadRow.getColumns();
        for (int i = 0; i < columnList.size(); ++i) {
            payloadProperty.name = columnList.get(i);
            payloadProperty.value = dynaForm.opt(payloadProperty.name);
            payloadProperty.value = ((payloadProperty.value == null) ? payloadRow.get(payloadProperty.name) : payloadProperty.value);
            if (!payloadProperty.name.equals(columnName)) {
                final boolean checkState = this.getTransformedFormPropertyValue(multiConfigForm, dynaForm, payloadProperty);
                if (checkState) {
                    payloadRow.set(payloadProperty.name, payloadProperty.value);
                }
            }
            else if (payloadProperty.name.contains("CONFIG_DATA_ITEM_ID") && isAdded) {
                final Object configDataItemId = dataObject.getValue("ConfigDataItem", "CONFIG_DATA_ITEM_ID", (Criteria)null);
                payloadRow.set(payloadProperty.name, configDataItemId);
            }
        }
        if (isAdded) {
            dataObject.addRow(payloadRow);
        }
        else {
            dataObject.updateRow(payloadRow);
        }
    }
    
    protected boolean populateMainTable(final String table) {
        return !table.equals("AndroidEFRPPolicy");
    }
    
    protected boolean checkIfNullableColumn(final String columnName, final JSONObject dynaForm) {
        final String nullableColumnName = columnName + "_NULLABLE";
        return dynaForm.has(nullableColumnName) && dynaForm.optBoolean(nullableColumnName, false);
    }
    
    static {
        MDMDefaultFormBean.logger = Logger.getLogger("MDMConfigLogger");
    }
}
