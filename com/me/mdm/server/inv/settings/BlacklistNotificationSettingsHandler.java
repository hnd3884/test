package com.me.mdm.server.inv.settings;

import com.adventnet.persistence.DataObject;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.persistence.Row;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.adventnet.persistence.DataAccessException;
import org.json.JSONException;
import com.me.mdm.api.error.APIHTTPException;
import java.util.logging.Level;
import com.google.gson.Gson;
import org.json.JSONObject;
import com.me.mdm.api.APIUtil;
import com.me.mdm.api.APIRequest;
import java.util.logging.Logger;
import com.me.mdm.api.ApiRequestHandler;

public class BlacklistNotificationSettingsHandler extends ApiRequestHandler
{
    private Logger logger;
    
    public BlacklistNotificationSettingsHandler() {
        this.logger = Logger.getLogger("MDMAppMgmtLogger");
    }
    
    @Override
    public Object doGet(final APIRequest apiRequest) throws APIHTTPException {
        try {
            final Long customerID = APIUtil.getCustomerID(apiRequest.toJSONObject());
            final BlacklistNotificationSettings notificationSettings = this.getBlacklistNotificationSettings(customerID);
            final JSONObject response = new JSONObject();
            response.put("RESPONSE", (Object)new JSONObject(new Gson().toJson((Object)notificationSettings)));
            response.put("status", 200);
            return response;
        }
        catch (final JSONException | DataAccessException e) {
            this.logger.log(Level.SEVERE, "error occured in BlacklistNotificationSettingsHandler.doGet", e);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    private BlacklistNotificationSettings getBlacklistNotificationSettings(final Long customerID) throws DataAccessException, JSONException {
        BlacklistNotificationSettings settings = null;
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("BlacklistAppSettings"));
        selectQuery.setCriteria(new Criteria(Column.getColumn("BlacklistAppSettings", "CUSTOMER_ID"), (Object)customerID, 0));
        selectQuery.addSelectColumn(Column.getColumn("BlacklistAppSettings", "*"));
        final DataObject dataObject = MDMUtil.getPersistence().get(selectQuery);
        Row row = null;
        if (!dataObject.isEmpty()) {
            row = dataObject.getFirstRow("BlacklistAppSettings");
        }
        if (row == null) {
            this.logger.log(Level.INFO, "Settings not present in DB, adding default values");
            row = new Row("BlacklistAppSettings");
            row.set("CUSTOMER_ID", (Object)customerID);
            row.set("BLACKLIST_ACTION_TYPE", (Object)2);
            row.set("NOTIFY_DAYS", (Object)0);
            dataObject.addRow(row);
            MDMUtil.getPersistence().update(dataObject);
            settings = new BlacklistNotificationSettings(2L, 0, false);
        }
        else {
            settings = new BlacklistNotificationSettings((long)row.get("BLACKLIST_ACTION_TYPE"), (int)row.get("NOTIFY_DAYS"), (int)row.get("NOTIFY_DAYS") != 0);
        }
        this.logger.log(Level.INFO, "Blacklist settings Queried : {0}", settings);
        return settings;
    }
    
    @Override
    public Object doPut(final APIRequest apiRequest) throws APIHTTPException {
        try {
            final String requestBody = new String(apiRequest.requestBody);
            final BlacklistNotificationSettings notificationSettings = (BlacklistNotificationSettings)new Gson().fromJson(requestBody, (Class)BlacklistNotificationSettings.class);
            notificationSettings.customerID = APIUtil.getCustomerID(apiRequest.toJSONObject());
            this.addOrUpdateAppBlackListSettings(notificationSettings);
            final JSONObject response = new JSONObject();
            response.put("status", 202);
            return response;
        }
        catch (final JSONException | DataAccessException e) {
            this.logger.log(Level.SEVERE, "error occured in PUT /inventory_settings/blacklist_notification", e);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    private void addOrUpdateAppBlackListSettings(final BlacklistNotificationSettings notificationSettings) throws DataAccessException {
        this.logger.log(Level.INFO, "Blacklist settings being updated");
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("BlacklistAppSettings"));
        selectQuery.addSelectColumn(Column.getColumn("BlacklistAppSettings", "*"));
        selectQuery.setCriteria(new Criteria(Column.getColumn("BlacklistAppSettings", "CUSTOMER_ID"), (Object)notificationSettings.customerID, 0));
        final DataObject dataObject = MDMUtil.getPersistence().get(selectQuery);
        Long setting = notificationSettings.blacklistMode;
        Integer days = notificationSettings.daysToNotifyFor;
        if (setting == null) {
            setting = 0L;
        }
        if (days == null) {
            days = 0;
        }
        if (setting == 2L && !notificationSettings.notifyUser) {
            days = 0;
        }
        if (setting == 2L && notificationSettings.notifyUser) {
            days = 1;
        }
        Row row = dataObject.getFirstRow("BlacklistAppSettings");
        if (row != null) {
            row.set("BLACKLIST_ACTION_TYPE", (Object)setting);
            row.set("NOTIFY_DAYS", (Object)days);
            dataObject.updateRow(row);
        }
        else {
            row = new Row("BlacklistAppSettings");
            row.set("CUSTOMER_ID", (Object)notificationSettings.customerID);
            row.set("BLACKLIST_ACTION_TYPE", (Object)setting);
            row.set("NOTIFY_DAYS", (Object)days);
            dataObject.addRow(row);
        }
        MDMUtil.getPersistence().update(dataObject);
        this.logger.log(Level.INFO, "Blacklist settings updated to Notify type : {0}  notify days : {1}", new Object[] { setting, days });
    }
}
