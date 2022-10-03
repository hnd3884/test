package com.me.mdm.server.inv.settings;

import java.util.Hashtable;
import java.util.Properties;
import com.adventnet.persistence.ReadOnlyPersistence;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.persistence.Row;
import com.adventnet.persistence.DataObject;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.me.devicemanagement.framework.server.alerts.AlertMailGeneratorUtil;
import com.me.mdm.server.util.MDMFeatureParamsHandler;
import org.json.JSONException;
import com.me.mdm.api.error.APIHTTPException;
import java.util.logging.Level;
import com.google.gson.Gson;
import org.json.JSONObject;
import com.me.mdm.api.APIUtil;
import com.me.mdm.api.APIRequest;
import java.util.logging.Logger;
import com.me.mdm.api.ApiRequestHandler;

public class AppScanSettingsHandler extends ApiRequestHandler
{
    private Logger logger;
    
    public AppScanSettingsHandler() {
        this.logger = Logger.getLogger("MDMAppMgmtLogger");
    }
    
    @Override
    public Object doGet(final APIRequest apiRequest) throws APIHTTPException {
        try {
            final Long customerID = APIUtil.getCustomerID(apiRequest.toJSONObject());
            final AppScanSettings scanSettings = this.getAppScanSettings(customerID);
            this.getLegacyAppScanSettings(scanSettings, customerID);
            final JSONObject response = new JSONObject();
            response.put("RESPONSE", (Object)new JSONObject(new Gson().toJson((Object)scanSettings)));
            response.put("status", 200);
            return response;
        }
        catch (final JSONException e) {
            this.logger.log(Level.SEVERE, "Exception in GET /inventory_settings/app_scan_settings", (Throwable)e);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    private void getLegacyAppScanSettings(final AppScanSettings scanSettings, final Long customerID) throws APIHTTPException {
        try {
            if (MDMFeatureParamsHandler.getInstance().isFeatureEnabled("showBlacklistAdminSettings")) {
                final AlertMailGeneratorUtil mailGenerator = new AlertMailGeneratorUtil(this.logger);
                final DataObject dataObject = MDMUtil.getInstance().getAppSettingsDO(customerID);
                final Row row = dataObject.getFirstRow("MdAppBlackListSetting");
                scanSettings.enableAppDiscoveryAlert = (Boolean)row.get("ENABLE_APP_DISCOVERY_ALERT");
                scanSettings.enableSummaryAlert = (Boolean)row.get("ENABLE_SUMMARY_ALERT");
                scanSettings.enableBlacklistAlert = (Boolean)row.get("ENABLE_BLACKLIST_ALERT");
                if (!ApiFactoryProvider.getMailSettingAPI().isMailServerConfigured()) {
                    this.logger.log(Level.WARNING, "mail not configured for legacyAppScanSettings");
                    throw new APIHTTPException("MAS001", new Object[0]);
                }
                scanSettings.adminEmail = mailGenerator.getCustomerEMailAddress(customerID, "MdM-BlackListApp");
            }
        }
        catch (final Exception e) {
            if (e instanceof APIHTTPException) {
                throw (APIHTTPException)e;
            }
            this.logger.log(Level.SEVERE, "exception occured in getLegacyAppScanSettings", e);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    public AppScanSettings getAppScanSettings(final Long customerID) throws APIHTTPException {
        AppScanSettings scanSettings = null;
        try {
            final ReadOnlyPersistence cachedPersistence = MDMUtil.getCachedPersistence();
            final DataObject appViewDO = cachedPersistence.get("AppViewSetting", new Criteria(Column.getColumn("AppViewSetting", "CUSTOMER_ID"), (Object)customerID, 0));
            if (!appViewDO.isEmpty()) {
                final Row appViewDataRow = appViewDO.getFirstRow("AppViewSetting");
                scanSettings = new AppScanSettings((boolean)appViewDataRow.get("SHOW_USER_INSTALLED_APPS"), (boolean)appViewDataRow.get("SHOW_SYSTEM_APPS"), (boolean)appViewDataRow.get("SHOW_MANAGED_APPS"));
            }
            else {
                scanSettings = new AppScanSettings(true, false, true);
            }
            scanSettings.customerID = customerID;
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, "Exception occurred while AppScanSettingsHandler.getAppScanSettings", ex);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
        this.logger.log(Level.INFO, "getAppScanSettings Data : {0}", scanSettings);
        return scanSettings;
    }
    
    @Override
    public Object doPut(final APIRequest apiRequest) throws APIHTTPException {
        try {
            final String requestBody = new String(apiRequest.requestBody);
            final AppScanSettings appScanSettings = (AppScanSettings)new Gson().fromJson(requestBody, (Class)AppScanSettings.class);
            appScanSettings.customerID = APIUtil.getCustomerID(apiRequest.toJSONObject());
            this.addOrUpdateAppScanSettings(appScanSettings);
            this.addOrUpdateLegacyAppScanSettings(appScanSettings);
            final JSONObject response = new JSONObject();
            response.put("status", 202);
            return response;
        }
        catch (final JSONException e) {
            this.logger.log(Level.SEVERE, "error occured in PUT /inventory_settings/app_scan_settings", (Throwable)e);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    private void addOrUpdateAppScanSettings(final AppScanSettings scanSettings) throws APIHTTPException {
        try {
            final Long customerID = scanSettings.customerID;
            final Criteria custCrit = new Criteria(new Column("AppViewSetting", "CUSTOMER_ID"), (Object)customerID, 0);
            final DataObject appViewSettingDO = MDMUtil.getPersistence().get("AppViewSetting", custCrit);
            this.logger.log(Level.INFO, "AppView Settings : {0}", scanSettings);
            if (appViewSettingDO.isEmpty()) {
                final Row row = new Row("AppViewSetting");
                row.set("CUSTOMER_ID", (Object)scanSettings.customerID);
                row.set("SHOW_MANAGED_APPS", (Object)scanSettings.showManagedApps);
                row.set("SHOW_USER_INSTALLED_APPS", (Object)scanSettings.showUserInstalledApps);
                row.set("SHOW_SYSTEM_APPS", (Object)scanSettings.showSystemApps);
                appViewSettingDO.addRow(row);
                MDMUtil.getPersistence().add(appViewSettingDO);
                this.logger.log(Level.INFO, "AppViewSetting row Added");
            }
            else {
                final Row row = appViewSettingDO.getFirstRow("AppViewSetting");
                row.set("SHOW_MANAGED_APPS", (Object)scanSettings.showManagedApps);
                row.set("SHOW_USER_INSTALLED_APPS", (Object)scanSettings.showUserInstalledApps);
                row.set("SHOW_SYSTEM_APPS", (Object)scanSettings.showSystemApps);
                appViewSettingDO.updateRow(row);
                MDMUtil.getPersistence().update(appViewSettingDO);
                this.logger.log(Level.INFO, "AppViewSetting row Modified");
            }
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, "Exception occurred in addOrUpdateAppScanSettings", ex);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    private void addOrUpdateLegacyAppScanSettings(final AppScanSettings scanSettings) throws APIHTTPException {
        if (MDMFeatureParamsHandler.getInstance().isFeatureEnabled("showBlacklistAdminSettings")) {
            final Properties properties = new Properties();
            ((Hashtable<String, Long>)properties).put("CUSTOMER_ID", scanSettings.customerID);
            ((Hashtable<String, Boolean>)properties).put("ENABLE_BLACKLIST_ALERT", scanSettings.enableBlacklistAlert);
            ((Hashtable<String, Boolean>)properties).put("ENABLE_APP_DISCOVERY_ALERT", scanSettings.enableAppDiscoveryAlert);
            ((Hashtable<String, Boolean>)properties).put("ENABLE_SUMMARY_ALERT", scanSettings.enableSummaryAlert);
            if (!ApiFactoryProvider.getMailSettingAPI().isMailServerConfigured()) {
                throw new APIHTTPException("MAS001", new Object[0]);
            }
            final AlertMailGeneratorUtil mailGenerator = new AlertMailGeneratorUtil();
            try {
                if (scanSettings.enableAppDiscoveryAlert || scanSettings.enableBlacklistAlert || scanSettings.enableSummaryAlert) {
                    mailGenerator.setCustomerEMailAddress((long)scanSettings.customerID, scanSettings.adminEmail, "MdM-BlackListApp");
                }
                else {
                    final DataObject emailAddressDO = mailGenerator.getCustomerEMailAddressDO(scanSettings.customerID, "MdM-BlackListApp");
                    if (!emailAddressDO.isEmpty()) {
                        final Row row = emailAddressDO.getFirstRow("EMailAddr");
                        row.set("SEND_MAIL", (Object)false);
                        emailAddressDO.updateRow(row);
                        MDMUtil.getPersistence().update(emailAddressDO);
                    }
                }
            }
            catch (final Exception e) {
                this.logger.log(Level.SEVERE, "Exception occured while adding mail id for blacklist notification");
            }
            MDMUtil.getInstance().addorUpdateAppSettings(properties);
        }
    }
}
