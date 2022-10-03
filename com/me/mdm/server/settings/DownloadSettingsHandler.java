package com.me.mdm.server.settings;

import java.util.List;
import java.util.logging.Level;
import com.me.mdm.server.notification.NotificationHandler;
import com.adventnet.sym.server.mdm.command.DeviceCommandRepository;
import com.adventnet.sym.server.mdm.core.ManagedDeviceHandler;
import com.adventnet.persistence.DataAccessException;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.adventnet.persistence.WritableDataObject;
import com.me.mdm.api.APIUtil;
import com.adventnet.persistence.Row;
import com.adventnet.persistence.DataObject;
import com.adventnet.ds.query.SelectQuery;
import org.json.JSONArray;
import com.adventnet.sym.server.mdm.util.JSONUtil;
import com.adventnet.persistence.DataAccess;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import org.json.JSONObject;
import java.util.logging.Logger;

public class DownloadSettingsHandler
{
    Logger logger;
    private static DownloadSettingsHandler downloadAgentSettingsHandler;
    
    public DownloadSettingsHandler() {
        this.logger = Logger.getLogger("DownloadSettingsHandler");
    }
    
    public static DownloadSettingsHandler getInstance() {
        if (DownloadSettingsHandler.downloadAgentSettingsHandler == null) {
            DownloadSettingsHandler.downloadAgentSettingsHandler = new DownloadSettingsHandler();
        }
        return DownloadSettingsHandler.downloadAgentSettingsHandler;
    }
    
    public JSONObject getDownloadSettingsForAgent(final Long customerID) throws Exception {
        final JSONObject downloadSettings = new JSONObject();
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("DownloadSettingsForAgent"));
        selectQuery.addSelectColumn(Column.getColumn((String)null, "*"));
        selectQuery.setCriteria(new Criteria(Column.getColumn("DownloadSettingsForAgent", "CUSTOMER_ID"), (Object)customerID, 0));
        final DataObject dataObject = DataAccess.get(selectQuery);
        if (!dataObject.isEmpty()) {
            final Row row = dataObject.getFirstRow("DownloadSettingsForAgent");
            downloadSettings.put("MAX_RETRY_COUNT", row.get("MAX_RETRY_COUNT"));
            downloadSettings.put("MIN_RETRY_DELAY", row.get("MIN_RETRY_DELAY"));
            downloadSettings.put("MAX_RETRY_DELAY", row.get("MAX_RETRY_DELAY"));
            downloadSettings.put("DELAY_RANDOM", row.get("DELAY_RANDOM"));
            final String excludedDomainsStr = String.valueOf(row.get("EXCLUDED_DOMAIN"));
            JSONArray convertCommaSeparatedStringToJSONArray;
            if (!excludedDomainsStr.equals("")) {
                JSONUtil.getInstance();
                convertCommaSeparatedStringToJSONArray = JSONUtil.convertCommaSeparatedStringToJSONArray(excludedDomainsStr);
            }
            else {
                convertCommaSeparatedStringToJSONArray = new JSONArray();
            }
            final JSONArray excludedDomains = convertCommaSeparatedStringToJSONArray;
            downloadSettings.put("EXCLUDED_DOMAIN", (Object)excludedDomains);
            downloadSettings.put("CUSTOM_RETRY_DELAY", row.get("CUSTOM_RETRY_DELAY"));
        }
        return downloadSettings;
    }
    
    public void addOrUpdateDownloadSettingsForAgent(final JSONObject requestJSON) throws Exception {
        final Long customerID = APIUtil.getCustomerID(requestJSON);
        final DataObject finalDO = (DataObject)new WritableDataObject();
        final JSONArray excludedDomains = requestJSON.getJSONArray("excluded_domain");
        final String excludedDomainsStr = (excludedDomains.length() != 0) ? JSONUtil.getInstance().convertJSONArrayToString(excludedDomains) : "";
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("DownloadSettingsForAgent"));
        selectQuery.setCriteria(new Criteria(Column.getColumn("DownloadSettingsForAgent", "CUSTOMER_ID"), (Object)customerID, 0));
        selectQuery.addSelectColumn(Column.getColumn((String)null, "*"));
        final DataObject existingDO = DataAccess.get(selectQuery);
        if (existingDO.isEmpty()) {
            final Row row = new Row("DownloadSettingsForAgent");
            row.set("MAX_RETRY_COUNT", requestJSON.get("max_retry_count"));
            row.set("MIN_RETRY_DELAY", requestJSON.get("min_retry_delay"));
            row.set("MAX_RETRY_DELAY", requestJSON.get(""));
            row.set("DELAY_RANDOM", requestJSON.get("delay_random"));
            row.set("EXCLUDED_DOMAIN", (Object)excludedDomainsStr);
            row.set("CUSTOM_RETRY_DELAY", requestJSON.get("custom_retry_delay"));
            row.set("CUSTOMER_ID", (Object)customerID);
            finalDO.addRow(row);
        }
        else {
            final Row row = existingDO.getFirstRow("DownloadSettingsForAgent");
            row.set("MAX_RETRY_COUNT", requestJSON.get("max_retry_count"));
            row.set("MIN_RETRY_DELAY", requestJSON.get("min_retry_delay"));
            row.set("MAX_RETRY_DELAY", requestJSON.get("max_retry_delay"));
            row.set("DELAY_RANDOM", requestJSON.get("delay_random"));
            row.set("EXCLUDED_DOMAIN", (Object)excludedDomainsStr);
            row.set("CUSTOM_RETRY_DELAY", requestJSON.get("custom_retry_delay"));
            finalDO.updateBlindly(row);
        }
        MDMUtil.getPersistence().update(finalDO);
        this.assignSyncDownloadSettingsCommand(customerID, 2);
    }
    
    public void addDefaultDownloadSettingsForAgent(final Long customerID) throws DataAccessException {
        final DataObject downloadSettingsDao = (DataObject)new WritableDataObject();
        final Row downloadSettingsRow = new Row("DownloadSettingsForAgent");
        downloadSettingsRow.set("MAX_RETRY_COUNT", (Object)3);
        downloadSettingsRow.set("MIN_RETRY_DELAY", (Object)DownloadSettingsConstants.DefaultValues.MIN_RETRY_DELAY);
        downloadSettingsRow.set("MAX_RETRY_DELAY", (Object)DownloadSettingsConstants.DefaultValues.MAX_RETRY_DELAY);
        downloadSettingsRow.set("DELAY_RANDOM", (Object)DownloadSettingsConstants.DefaultValues.DELAY_RANDOM);
        downloadSettingsRow.set("EXCLUDED_DOMAIN", (Object)"");
        downloadSettingsRow.set("CUSTOMER_ID", (Object)customerID);
        downloadSettingsRow.set("CUSTOM_RETRY_DELAY", (Object)DownloadSettingsConstants.DefaultValues.CUSTOM_RETRY_DELAY);
        downloadSettingsDao.addRow(downloadSettingsRow);
        DataAccess.add(downloadSettingsDao);
    }
    
    public void assignSyncDownloadSettingsCommand(final Long customerID, final int platformType) {
        try {
            final List managedResourceList = ManagedDeviceHandler.getInstance().getManagedDevicesForCustomer(customerID, platformType);
            if (platformType == 2) {
                DeviceCommandRepository.getInstance().addSyncDownloadSettingsCommand(managedResourceList, 1);
                NotificationHandler.getInstance().SendNotification(managedResourceList, 201);
            }
            else if (platformType == 3) {
                DeviceCommandRepository.getInstance().addSyncDownloadSettingsCommand(managedResourceList, 2);
                NotificationHandler.getInstance().SendNotification(managedResourceList, 1);
            }
            else if (platformType == 1) {
                DeviceCommandRepository.getInstance().addSyncDownloadSettingsCommand(managedResourceList, 2);
                NotificationHandler.getInstance().SendNotification(managedResourceList, 303);
            }
            else {
                DeviceCommandRepository.getInstance().addSyncDownloadSettingsCommand(managedResourceList, 2);
            }
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, "Exception in assignSyncDownloadSettingsCommand ", ex);
        }
    }
    
    public void assignSyncDownloadSettingsCommand(final Long customerID) {
        try {
            final List managedAndroidResourceList = ManagedDeviceHandler.getInstance().getManagedDevicesForCustomer(customerID, 2);
            if (!managedAndroidResourceList.isEmpty()) {
                DeviceCommandRepository.getInstance().addSyncDownloadSettingsCommand(managedAndroidResourceList, 1);
                NotificationHandler.getInstance().SendNotification(managedAndroidResourceList, 201);
            }
            final List managedResourceList = ManagedDeviceHandler.getInstance().getManagedDeviceResourceIDs(null);
            managedResourceList.remove(managedAndroidResourceList);
            if (!managedResourceList.isEmpty()) {
                DeviceCommandRepository.getInstance().addSyncDownloadSettingsCommand(managedAndroidResourceList, 2);
                NotificationHandler.getInstance().SendNotification(managedResourceList);
            }
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, "Exception in assignSyncDownloadSettingsCommand()", ex);
        }
    }
    
    static {
        DownloadSettingsHandler.downloadAgentSettingsHandler = null;
    }
}
