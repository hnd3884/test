package com.me.mdm.server.updates.osupdates;

import org.json.JSONException;
import com.adventnet.persistence.DataAccessException;
import com.adventnet.ds.query.SortColumn;
import java.util.Iterator;
import com.adventnet.sym.server.mdm.util.VersionChecker;
import com.adventnet.ds.query.DeleteQuery;
import com.adventnet.ds.query.DeleteQueryImpl;
import com.adventnet.sym.server.mdm.util.MDMStringUtils;
import com.adventnet.persistence.Row;
import org.json.JSONObject;
import com.adventnet.ds.query.DMDataSetWrapper;
import java.util.logging.Level;
import java.util.List;
import com.me.devicemanagement.framework.server.util.DBUtil;
import java.util.Collection;
import java.util.ArrayList;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.adventnet.persistence.DataObject;
import java.util.logging.Logger;

public class ResourceOSUpdateDataHandler
{
    private final Logger logger;
    
    public ResourceOSUpdateDataHandler() {
        this.logger = Logger.getLogger("MDMLogger");
    }
    
    public DataObject getAvailableUpdatesForResource(final Long resourceID) throws Exception {
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("DeviceAvailableOSUpdates"));
        final Join osUpdateJoin = new Join("DeviceAvailableOSUpdates", "OSUpdates", new String[] { "UPDATE_ID" }, new String[] { "UPDATE_ID" }, 2);
        final Join managedUpdateJoin = new Join("OSUpdates", "ManagedUpdates", new String[] { "UPDATE_ID" }, new String[] { "UPDATE_ID" }, 2);
        selectQuery.addJoin(osUpdateJoin);
        selectQuery.addJoin(managedUpdateJoin);
        final Criteria criteria = new Criteria(Column.getColumn("DeviceAvailableOSUpdates", "RESOURCE_ID"), (Object)resourceID, 0);
        selectQuery.setCriteria(criteria);
        selectQuery.addSelectColumn(Column.getColumn((String)null, "*"));
        return MDMUtil.getPersistence().get(selectQuery);
    }
    
    public ArrayList<Long> getResourcesMissingUpdate(final ArrayList<Long> resourcesToCheck, final Long updateID) throws Exception {
        final ArrayList<Long> resMissing = new ArrayList<Long>(resourcesToCheck);
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("DeviceAvailableOSUpdates"));
        if (!resourcesToCheck.isEmpty()) {
            final Criteria resCriteria = new Criteria(Column.getColumn("DeviceAvailableOSUpdates", "RESOURCE_ID"), (Object)resourcesToCheck.toArray(), 8);
            selectQuery.setCriteria(resCriteria);
        }
        final Criteria updateCriteria = new Criteria(Column.getColumn("DeviceAvailableOSUpdates", "UPDATE_ID"), (Object)updateID, 0);
        selectQuery.setCriteria(updateCriteria);
        selectQuery.addSelectColumn(Column.getColumn("DeviceAvailableOSUpdates", "RESOURCE_ID").distinct());
        final DataObject dO = MDMUtil.getPersistence().get(selectQuery);
        if (!dO.isEmpty()) {
            final List existingRes = DBUtil.getColumnValuesAsList(dO.getRows("DeviceAvailableOSUpdates"), "RESOURCE_ID");
            resMissing.removeAll(existingRes);
        }
        return resMissing;
    }
    
    public ArrayList<Long> getAffectedResourcesFromTarget(final ArrayList<Long> targetResourceList, final Long detectedTimeInMillis) {
        this.logger.log(Level.INFO, "DATA-IN: Resources to check: {0}, Time Period : {1}", new Object[] { targetResourceList, detectedTimeInMillis });
        final ArrayList<Long> affectedTargets = new ArrayList<Long>();
        try {
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("DeviceAvailableOSUpdates"));
            final Join osUpdatesJoin = new Join("DeviceAvailableOSUpdates", "OSUpdates", new String[] { "UPDATE_ID" }, new String[] { "UPDATE_ID" }, 2);
            final Join managedUpdateJoin = new Join("OSUpdates", "ManagedUpdates", new String[] { "UPDATE_ID" }, new String[] { "UPDATE_ID" }, 2);
            selectQuery.addJoin(osUpdatesJoin);
            selectQuery.addJoin(managedUpdateJoin);
            final Criteria resC = new Criteria(Column.getColumn("DeviceAvailableOSUpdates", "RESOURCE_ID"), (Object)targetResourceList.toArray(), 8);
            final Criteria detectedTimeC = new Criteria(Column.getColumn("ManagedUpdates", "ADDED_AT"), (Object)detectedTimeInMillis, 6);
            selectQuery.setCriteria(resC.and(detectedTimeC));
            final Column resourceColumn = Column.getColumn("DeviceAvailableOSUpdates", "RESOURCE_ID");
            final Column distinctResourceColumn = resourceColumn.distinct();
            distinctResourceColumn.setColumnAlias("RESOURCE_ID");
            selectQuery.addSelectColumn(distinctResourceColumn);
            final DMDataSetWrapper dataSetWrapper = DMDataSetWrapper.executeQuery((Object)selectQuery);
            while (dataSetWrapper.next()) {
                final Long resourceId = (Long)dataSetWrapper.getValue("RESOURCE_ID");
                affectedTargets.add(resourceId);
            }
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Exception while checking whether any resource is due for an OS update: ", e);
        }
        this.logger.log(Level.INFO, "Resources due for an OS update: {0}, Time: {1}", new Object[] { affectedTargets, detectedTimeInMillis });
        return affectedTargets;
    }
    
    protected void addOrModifyDeviceAvailableUpdate(final JSONObject dataJson) throws Exception {
        final Long resourceID = dataJson.getLong("RESOURCE_ID");
        final Long detectedUpdateID = dataJson.getLong("UPDATE_ID");
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("DeviceAvailableOSUpdates"));
        final Criteria resC = new Criteria(Column.getColumn("DeviceAvailableOSUpdates", "RESOURCE_ID"), (Object)resourceID, 0);
        final Criteria updateIDC = new Criteria(Column.getColumn("DeviceAvailableOSUpdates", "UPDATE_ID"), (Object)detectedUpdateID, 0);
        selectQuery.addSelectColumn(Column.getColumn((String)null, "*"));
        selectQuery.setCriteria(resC.and(updateIDC));
        final DataObject dO = MDMUtil.getPersistence().get(selectQuery);
        Row deviceRow = dO.getRow("DeviceAvailableOSUpdates");
        if (deviceRow == null) {
            this.logger.log(Level.INFO, "Adding Device available Update: {0} for resource: {1}", new Object[] { detectedUpdateID, resourceID });
            deviceRow = new Row("DeviceAvailableOSUpdates");
            deviceRow.set("RESOURCE_ID", (Object)resourceID);
            deviceRow.set("UPDATE_ID", (Object)detectedUpdateID);
            deviceRow.set("STATUS", (Object)dataJson.getInt("STATUS"));
            deviceRow.set("I18N_REMARKS", (Object)dataJson.optString("I18N_REMARKS", "--"));
            deviceRow.set("UPDATE_STARTED_AT", (Object)dataJson.optLong("UPDATE_STARTED_AT", -1L));
            dO.addRow(deviceRow);
        }
        else {
            if (dataJson.opt("STATUS") != null) {
                deviceRow.set("STATUS", (Object)dataJson.getInt("STATUS"));
            }
            if (dataJson.opt("I18N_REMARKS") != null) {
                deviceRow.set("I18N_REMARKS", (Object)String.valueOf(dataJson.get("I18N_REMARKS")));
            }
            dO.updateRow(deviceRow);
        }
        MDMUtil.getPersistence().update(dO);
    }
    
    public void modifyDeviceAvailableUpdateDetails(final JSONObject dataJson) throws Exception {
        this.logger.log(Level.INFO, "Modifying device available update:{0}", new Object[] { dataJson });
        final Long resourceID = dataJson.getLong("RESOURCE_ID");
        final Long detectedUpdateID = dataJson.getLong("UPDATE_ID");
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("DeviceAvailableOSUpdates"));
        final Criteria resC = new Criteria(Column.getColumn("DeviceAvailableOSUpdates", "RESOURCE_ID"), (Object)resourceID, 0);
        final Criteria updateIDC = new Criteria(Column.getColumn("DeviceAvailableOSUpdates", "UPDATE_ID"), (Object)detectedUpdateID, 0);
        selectQuery.addSelectColumn(Column.getColumn((String)null, "*"));
        selectQuery.setCriteria(resC.and(updateIDC));
        final DataObject dO = MDMUtil.getPersistence().get(selectQuery);
        final Row deviceRow = dO.getRow("DeviceAvailableOSUpdates");
        if (deviceRow != null) {
            if (dataJson.opt("STATUS") != null) {
                deviceRow.set("STATUS", (Object)dataJson.getInt("STATUS"));
            }
            if (dataJson.opt("I18N_REMARKS") != null) {
                deviceRow.set("I18N_REMARKS", (Object)dataJson.optString("I18N_REMARKS"));
            }
            final Long updateStartedAt = dataJson.optLong("UPDATE_STARTED_AT", -1L);
            if (updateStartedAt != -1L) {
                deviceRow.set("UPDATE_STARTED_AT", (Object)updateStartedAt);
            }
            final String downloadPercent = dataJson.optString("DOWNLOAD_PERCENT", "");
            if (!MDMStringUtils.isEmpty(downloadPercent)) {
                deviceRow.set("DOWNLOAD_PERCENT", (Object)downloadPercent);
            }
            final Long lastUpdateTime = dataJson.optLong("DOWNLOAD_PERCENT_UPDATED_AT", -1L);
            if (lastUpdateTime != -1L) {
                deviceRow.set("DOWNLOAD_PERCENT_UPDATED_AT", (Object)lastUpdateTime);
            }
            dO.updateRow(deviceRow);
            this.logger.log(Level.INFO, "Updated the device available update");
            MDMUtil.getPersistence().update(dO);
        }
    }
    
    public void deleteAvailableUpdatesForResource(final Long resourceID, final List<String> versionList) throws Exception {
        this.logger.log(Level.INFO, "Going to delete the lowerOSVersion:{0} for resource:{1}", new Object[] { versionList, resourceID });
        final DeleteQuery deleteQuery = (DeleteQuery)new DeleteQueryImpl("DeviceAvailableOSUpdates");
        deleteQuery.addJoin(new Join("DeviceAvailableOSUpdates", "OSUpdates", new String[] { "UPDATE_ID" }, new String[] { "UPDATE_ID" }, 1));
        Criteria resourceCriteria = new Criteria(new Column("DeviceAvailableOSUpdates", "RESOURCE_ID"), (Object)resourceID, 0);
        if (!versionList.isEmpty()) {
            resourceCriteria = resourceCriteria.and(new Criteria(new Column("OSUpdates", "VERSION"), (Object)versionList.toArray(), 8));
        }
        deleteQuery.setCriteria(resourceCriteria);
        MDMUtil.getPersistenceLite().delete(deleteQuery);
    }
    
    public Long getLatestOSVersionUpdateIDForResource(final VersionChecker versionChecker, final Long resourceId) throws Exception {
        String OSVersion = "0.1";
        try {
            final DataObject dataObject = this.getAvailableUpdatesForResource(resourceId);
            if (!dataObject.isEmpty()) {
                final Iterator iterator = dataObject.getRows("OSUpdates");
                while (iterator.hasNext()) {
                    final Row deviceAvailableOSRow = iterator.next();
                    final String version = (String)deviceAvailableOSRow.get("VERSION");
                    if (versionChecker.isGreater(version, OSVersion)) {
                        OSVersion = version;
                    }
                }
                if (OSVersion.equals("0.1")) {
                    OSVersion = (String)dataObject.getFirstRow("OSUpdates").get("VERSION");
                }
                final Criteria updateCriteria = new Criteria(new Column("OSUpdates", "VERSION"), (Object)OSVersion, 0);
                final Row updateIDRow = dataObject.getRow("OSUpdates", updateCriteria);
                return (Long)updateIDRow.get("UPDATE_ID");
            }
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, "Exception:", ex);
            throw ex;
        }
        return null;
    }
    
    public JSONObject getApplicableOSVersionForResource(final Long detectedTimeInMillis, final Long resourceId) {
        final JSONObject jsonObject = new JSONObject();
        try {
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("DeviceAvailableOSUpdates"));
            final Join osUpdatesJoin = new Join("DeviceAvailableOSUpdates", "OSUpdates", new String[] { "UPDATE_ID" }, new String[] { "UPDATE_ID" }, 2);
            final Join managedUpdateJoin = new Join("OSUpdates", "ManagedUpdates", new String[] { "UPDATE_ID" }, new String[] { "UPDATE_ID" }, 2);
            selectQuery.addJoin(osUpdatesJoin);
            selectQuery.addJoin(managedUpdateJoin);
            selectQuery.addJoin(new Join("OSUpdates", "IOSUpdates", new String[] { "UPDATE_ID" }, new String[] { "UPDATE_ID" }, 2));
            final Criteria resC = new Criteria(Column.getColumn("DeviceAvailableOSUpdates", "RESOURCE_ID"), (Object)resourceId, 0);
            final Criteria detectedTimeC = new Criteria(Column.getColumn("ManagedUpdates", "ADDED_AT"), (Object)detectedTimeInMillis, 6);
            selectQuery.setCriteria(resC.and(detectedTimeC));
            selectQuery.addSelectColumn(new Column("ManagedUpdates", "UPDATE_ID"));
            selectQuery.addSelectColumn(new Column("ManagedUpdates", "ADDED_AT"));
            selectQuery.addSelectColumn(new Column("OSUpdates", "UPDATE_ID"));
            selectQuery.addSelectColumn(new Column("OSUpdates", "VERSION"));
            selectQuery.addSelectColumn(new Column("IOSUpdates", "UPDATE_ID"));
            selectQuery.addSelectColumn(new Column("IOSUpdates", "PRODUCT_KEY"));
            final DataObject dataObject = MDMUtil.getPersistenceLite().get(selectQuery);
            if (!dataObject.isEmpty()) {
                final SortColumn sortColumn = new SortColumn(new Column("ManagedUpdates", "ADDED_AT"), false);
                dataObject.sortRows("ManagedUpdates", new SortColumn[] { sortColumn });
                final Row managedUpdateRow = dataObject.getFirstRow("ManagedUpdates");
                final Long updateId = (Long)managedUpdateRow.get("UPDATE_ID");
                final Row OSUpdateRow = dataObject.getRow("OSUpdates", new Criteria(new Column("OSUpdates", "UPDATE_ID"), (Object)updateId, 0));
                final String version = (String)OSUpdateRow.get("VERSION");
                final Row iOSUpdateRow = dataObject.getRow("IOSUpdates", new Criteria(new Column("IOSUpdates", "UPDATE_ID"), (Object)updateId, 0));
                final String productKey = (String)iOSUpdateRow.get("PRODUCT_KEY");
                jsonObject.put("VERSION", (Object)version);
                jsonObject.put("PRODUCT_KEY", (Object)productKey);
            }
        }
        catch (final DataAccessException | JSONException e) {
            this.logger.log(Level.SEVERE, "Exception in getApplicable os", e);
        }
        return jsonObject;
    }
    
    public SelectQuery getDeviceManagedUpdateQuery() {
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("OSUpdates"));
        selectQuery.addJoin(new Join("OSUpdates", "DeviceAvailableOSUpdates", new String[] { "UPDATE_ID" }, new String[] { "UPDATE_ID" }, 2));
        return selectQuery;
    }
    
    public void updateResourceOSUpdateStatus(final Long resourceId, final int statusCode, final String remarks, final Long updateStartedAt, final String productKey, final String downloadPercent, final Long lastUpdateTime) {
        try {
            final ResourceOSUpdateDataHandler dataHandler = new ResourceOSUpdateDataHandler();
            DataObject dataObject = null;
            if (!MDMStringUtils.isEmpty(productKey)) {
                dataObject = this.getResourceUpdateFromProductKey(resourceId, productKey);
            }
            else {
                dataObject = this.getAvailableUpdatesForResource(resourceId);
            }
            final Long detectedUpdateID = (Long)dataObject.getFirstRow("DeviceAvailableOSUpdates").get("UPDATE_ID");
            if (detectedUpdateID != null) {
                final JSONObject dataJson = new JSONObject();
                dataJson.put("UPDATE_ID", (Object)detectedUpdateID);
                dataJson.put("RESOURCE_ID", (Object)resourceId);
                dataJson.put("STATUS", statusCode);
                dataJson.put("I18N_REMARKS", (Object)remarks);
                dataJson.put("UPDATE_STARTED_AT", (Object)updateStartedAt);
                dataJson.put("DOWNLOAD_PERCENT", (Object)downloadPercent);
                dataJson.put("DOWNLOAD_PERCENT_UPDATED_AT", (Object)lastUpdateTime);
                dataHandler.modifyDeviceAvailableUpdateDetails(dataJson);
            }
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, "Exception in updateResourceOSUpdateStatus ", ex);
        }
    }
    
    private DataObject getResourceUpdateFromProductKey(final Long resourceId, final String productKey) {
        try {
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("DeviceAvailableOSUpdates"));
            selectQuery.addJoin(new Join("DeviceAvailableOSUpdates", "IOSUpdates", new String[] { "UPDATE_ID" }, new String[] { "UPDATE_ID" }, 2));
            final Criteria productKeyCriteria = new Criteria(new Column("IOSUpdates", "PRODUCT_KEY"), (Object)productKey, 0);
            final Criteria resourceCriteria = new Criteria(new Column("DeviceAvailableOSUpdates", "RESOURCE_ID"), (Object)resourceId, 0);
            selectQuery.setCriteria(productKeyCriteria.and(resourceCriteria));
            selectQuery.addSelectColumn(new Column("DeviceAvailableOSUpdates", "*"));
            final DataObject dataObject = MDMUtil.getPersistenceLite().get(selectQuery);
            return dataObject;
        }
        catch (final Exception e) {
            this.logger.log(Level.INFO, "Exception in getting resourceUpdate from product key", e);
            return null;
        }
    }
    
    public Row getDeviceAvailableRowFromProductKey(final Long resourceId, final String productKey) {
        try {
            final DataObject dataObject = this.getResourceUpdateFromProductKey(resourceId, productKey);
            if (dataObject != null && !dataObject.isEmpty()) {
                final Row deviceRow = dataObject.getRow("DeviceAvailableOSUpdates");
                return deviceRow;
            }
        }
        catch (final Exception e) {
            this.logger.log(Level.INFO, "Exception in getDeviceAvailableRowFromProductKey", e);
        }
        return null;
    }
    
    public Long getResourceUpdateStartedAtTime(final Long resourceId, final String productKey) {
        try {
            final DataObject dataObject = this.getResourceUpdateFromProductKey(resourceId, productKey);
            if (dataObject != null && !dataObject.isEmpty()) {
                final Row deviceRow = dataObject.getRow("DeviceAvailableOSUpdates");
                return (Long)deviceRow.get("UPDATE_STARTED_AT");
            }
        }
        catch (final Exception e) {
            this.logger.log(Level.INFO, "Exception in getResourceUpdateStartedAtTime from product key", e);
        }
        return null;
    }
    
    public boolean isAnyUpdateStartedForResource(final Long resourceId) {
        try {
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("DeviceAvailableOSUpdates"));
            final Criteria resourceCriteria = new Criteria(new Column("DeviceAvailableOSUpdates", "RESOURCE_ID"), (Object)resourceId, 0);
            final Criteria updateStartedAtCriteria = new Criteria(new Column("DeviceAvailableOSUpdates", "UPDATE_STARTED_AT"), (Object)(-1L), 1);
            selectQuery.addSelectColumn(new Column("*", (String)null));
            selectQuery.setCriteria(resourceCriteria.and(updateStartedAtCriteria));
            final DataObject dataObject = MDMUtil.getPersistenceLite().get(selectQuery);
            if (!dataObject.isEmpty()) {
                final Row updateRow = dataObject.getRow("DeviceAvailableOSUpdates");
                final Long updateStartedAt = (Long)updateRow.get("UPDATE_STARTED_AT");
                if (!updateStartedAt.equals(-1L)) {
                    return true;
                }
            }
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, "Error in isAnyUpdateStartedForResource", ex);
        }
        return false;
    }
    
    public void deleteDeviceAvailableUpdate(final Long resourceId, final String osVersion) {
        try {
            final DataObject dataObject = this.getAvailableUpdatesForResource(resourceId);
            if (!dataObject.isEmpty()) {
                final List<String> availableUpdates = new ArrayList<String>();
                final Iterator iterator = dataObject.getRows("OSUpdates");
                while (iterator.hasNext()) {
                    final Row osupdateRow = iterator.next();
                    final String version = (String)osupdateRow.get("VERSION");
                    availableUpdates.add(version);
                }
                final List<String> lowerVersion = this.getLowerOSVersion(new VersionChecker(), availableUpdates, osVersion);
                if (!lowerVersion.isEmpty()) {
                    this.deleteAvailableUpdatesForResource(resourceId, lowerVersion);
                }
            }
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, "Exception in deleteDeviceAvailableUpdate", ex);
        }
    }
    
    private List<String> getLowerOSVersion(final VersionChecker versionChecker, final List<String> osVersionList, final String version) {
        final List<String> lowerVersions = new ArrayList<String>();
        for (final String osVersion : osVersionList) {
            if (!versionChecker.isGreater(osVersion, version)) {
                lowerVersions.add(osVersion);
            }
        }
        return lowerVersions;
    }
}
