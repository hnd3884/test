package com.adventnet.sym.server.mdm.apps;

import java.util.Iterator;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import java.util.Map;
import com.adventnet.sym.server.mdm.config.MDMCollectionStatusUpdate;
import java.util.Collection;
import java.util.ArrayList;
import java.util.HashMap;
import com.adventnet.persistence.DataAccessException;
import com.adventnet.ds.query.UpdateQuery;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.UpdateQueryImpl;
import java.util.List;
import com.adventnet.persistence.DataObject;
import java.util.logging.Level;
import java.util.Objects;
import com.adventnet.persistence.Row;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import org.json.JSONObject;
import java.util.logging.Logger;

public class AppInstallationStatusHandler
{
    public static Logger logger;
    
    public String updateAppInstallationDetailsFromDevice(final Long resourceId, final Long appGroupId, final Long installedAppID, final int installationStatus, final String remarks, final int scope, final int platformType, final JSONObject extraParamsJSON) {
        String updatedRemarks = remarks;
        try {
            final Criteria resCri = new Criteria(Column.getColumn("MdAppCatalogToResource", "RESOURCE_ID"), (Object)resourceId, 0);
            final Criteria appGroupCri = new Criteria(Column.getColumn("MdAppCatalogToResource", "APP_GROUP_ID"), (Object)appGroupId, 0);
            final Criteria cri = resCri.and(appGroupCri);
            final DataObject dObj = MDMUtil.getPersistence().get("MdAppCatalogToResource", cri);
            final Row appRelRow = dObj.isEmpty() ? new Row("MdAppCatalogToResource") : dObj.getFirstRow("MdAppCatalogToResource");
            appRelRow.set("UPDATED_AT", (Object)System.currentTimeMillis());
            appRelRow.set("INSTALLED_APP_ID", (Object)installedAppID);
            appRelRow.set("APP_GROUP_ID", (Object)appGroupId);
            appRelRow.set("RESOURCE_ID", (Object)resourceId);
            appRelRow.set("STATUS", (Object)installationStatus);
            appRelRow.set("REMARKS", (Object)remarks);
            if (!dObj.isEmpty()) {
                Boolean updateRow = true;
                final Long publishedAppId = (Long)appRelRow.get("PUBLISHED_APP_ID");
                final AppsUtil appsUtil = new AppsUtil();
                appsUtil.addOrUpdateAppCatalogScopeRel(resourceId, appGroupId, scope);
                final int packageType = appsUtil.getAppPackageType(appGroupId);
                if (installationStatus == 2 && !Objects.equals(publishedAppId, installedAppID)) {
                    if (platformType == 2) {
                        updatedRemarks = new ManagedAppStatusHandler().getManagedAppRemarksForAndroid(appGroupId, resourceId, packageType, installedAppID, extraParamsJSON);
                        if (updatedRemarks == null) {
                            updateRow = false;
                        }
                        appRelRow.set("REMARKS", (Object)updatedRemarks);
                    }
                    else if (packageType == 2) {
                        appRelRow.set("STATUS", (Object)0);
                        appRelRow.set("REMARKS", (Object)"dc.db.mdm.apps.status.UpgradeApp");
                    }
                }
                if (updateRow) {
                    dObj.updateRow(appRelRow);
                    MDMUtil.getPersistence().update(dObj);
                }
            }
            else if (installedAppID != null) {
                appRelRow.set("PUBLISHED_APP_ID", (Object)installedAppID);
                dObj.addRow(appRelRow);
                MDMUtil.getPersistence().add(dObj);
            }
            else {
                AppInstallationStatusHandler.logger.log(Level.WARNING, "New entry row with null as app ID. AppGroupId {0}, Resource Id {1}", new Object[] { appGroupId, resourceId });
            }
        }
        catch (final Exception ex) {
            AppInstallationStatusHandler.logger.log(Level.SEVERE, "Exception in updateInstallationDetails ", ex);
        }
        return updatedRemarks;
    }
    
    public String updateAppInstallationDetailsFromDevice(final Long resourceId, final Long appGroupId, final Long installedAppID, final int installationStatus, final String remarks, final int scope) {
        return this.updateAppInstallationDetailsFromDevice(resourceId, appGroupId, installedAppID, installationStatus, remarks, scope, -1, new JSONObject());
    }
    
    public void updateAppInstallationStatus(final Long resourceId, final Long appGroupId, final Long newlyPublishedAppId, final int installationStatus, final String remarks, final int scope) {
        try {
            final Criteria resCri = new Criteria(Column.getColumn("MdAppCatalogToResource", "RESOURCE_ID"), (Object)resourceId, 0);
            final Criteria appGroupCri = new Criteria(Column.getColumn("MdAppCatalogToResource", "APP_GROUP_ID"), (Object)appGroupId, 0);
            final Criteria cri = resCri.and(appGroupCri);
            final DataObject dObj = MDMUtil.getPersistence().get("MdAppCatalogToResource", cri);
            final Row appRelRow = dObj.isEmpty() ? new Row("MdAppCatalogToResource") : dObj.getFirstRow("MdAppCatalogToResource");
            appRelRow.set("UPDATED_AT", (Object)System.currentTimeMillis());
            appRelRow.set("REMARKS", (Object)remarks);
            appRelRow.set("STATUS", (Object)installationStatus);
            appRelRow.set("APP_GROUP_ID", (Object)appGroupId);
            appRelRow.set("RESOURCE_ID", (Object)resourceId);
            if (!dObj.isEmpty()) {
                final Long oldPublishedId = (Long)appRelRow.get("PUBLISHED_APP_ID");
                final AppsUtil appsUtil = AppsUtil.getInstance();
                appsUtil.addOrUpdateAppCatalogScopeRel(resourceId, appGroupId, scope);
                final int packageType = appsUtil.getAppPackageType(appGroupId);
                if (packageType == 2 && installationStatus == 2 && !Objects.equals(oldPublishedId, newlyPublishedAppId)) {
                    appRelRow.set("STATUS", (Object)0);
                    appRelRow.set("REMARKS", (Object)"dc.db.mdm.apps.status.UpgradeApp");
                }
                dObj.updateRow(appRelRow);
                MDMUtil.getPersistence().update(dObj);
            }
            else {
                appRelRow.set("PUBLISHED_APP_ID", (Object)newlyPublishedAppId);
                dObj.addRow(appRelRow);
                MDMUtil.getPersistence().add(dObj);
            }
        }
        catch (final Exception ex2) {
            AppInstallationStatusHandler.logger.log(Level.SEVERE, "Exception in updateInstallationStatus...", ex2);
        }
    }
    
    public void updateAppInstallationStatusFromDevice(final Long resourceId, final Long appGroupId, final Long installedAppId, final int installationStatus, final String remarks, final int scope) {
        try {
            final Criteria resCri = new Criteria(Column.getColumn("MdAppCatalogToResource", "RESOURCE_ID"), (Object)resourceId, 0);
            final Criteria appGroupCri = new Criteria(Column.getColumn("MdAppCatalogToResource", "APP_GROUP_ID"), (Object)appGroupId, 0);
            final Criteria cri = resCri.and(appGroupCri);
            final DataObject dObj = MDMUtil.getPersistence().get("MdAppCatalogToResource", cri);
            final Row appRelRow = dObj.isEmpty() ? new Row("MdAppCatalogToResource") : dObj.getFirstRow("MdAppCatalogToResource");
            appRelRow.set("UPDATED_AT", (Object)System.currentTimeMillis());
            appRelRow.set("REMARKS", (Object)remarks);
            appRelRow.set("STATUS", (Object)installationStatus);
            appRelRow.set("APP_GROUP_ID", (Object)appGroupId);
            appRelRow.set("RESOURCE_ID", (Object)resourceId);
            if (!dObj.isEmpty()) {
                final Long publishedAppId = (Long)appRelRow.get("PUBLISHED_APP_ID");
                final AppsUtil appsUtil = AppsUtil.getInstance();
                appsUtil.addOrUpdateAppCatalogScopeRel(resourceId, appGroupId, scope);
                final int packageType = appsUtil.getAppPackageType(appGroupId);
                if (packageType == 2 && installationStatus == 2 && !Objects.equals(publishedAppId, installedAppId)) {
                    appRelRow.set("STATUS", (Object)0);
                    appRelRow.set("REMARKS", (Object)"dc.db.mdm.apps.status.UpgradeApp");
                }
                dObj.updateRow(appRelRow);
                MDMUtil.getPersistence().update(dObj);
            }
            else if (installedAppId != null) {
                appRelRow.set("PUBLISHED_APP_ID", (Object)installedAppId);
                dObj.addRow(appRelRow);
                MDMUtil.getPersistence().add(dObj);
            }
            else {
                AppInstallationStatusHandler.logger.log(Level.WARNING, "New entry row with null as app ID. AppGroupId {0}, Resource Id {1}", new Object[] { appGroupId, resourceId });
            }
        }
        catch (final Exception ex) {
            AppInstallationStatusHandler.logger.log(Level.SEVERE, "Exception in updateInstallationStatus...", ex);
        }
    }
    
    public void updateAppInstallationStatusForDevice(final List resourceIds, final List collectionIds, final Long publishedAppID, final int installationStatus) throws DataAccessException {
        final UpdateQuery updateQuery = (UpdateQuery)new UpdateQueryImpl("MdAppCatalogToResource");
        updateQuery.addJoin(new Join("MdAppCatalogToResource", "AppGroupToCollection", new String[] { "APP_GROUP_ID" }, new String[] { "APP_GROUP_ID" }, 2));
        final Criteria collnCriteria = new Criteria(Column.getColumn("AppGroupToCollection", "COLLECTION_ID"), (Object)collectionIds.toArray(), 8);
        final Criteria resCriteria = new Criteria(Column.getColumn("MdAppCatalogToResource", "RESOURCE_ID"), (Object)resourceIds.toArray(), 8);
        updateQuery.setCriteria(collnCriteria.and(resCriteria));
        updateQuery.setUpdateColumn("PUBLISHED_APP_ID", (Object)publishedAppID);
        updateQuery.setUpdateColumn("STATUS", (Object)installationStatus);
        MDMUtil.getPersistenceLite().update(updateQuery);
    }
    
    public int getAppCatalogStatusConstant(final int mdmStatusConstant) {
        int appCatalogConstants = mdmStatusConstant;
        if (mdmStatusConstant == 12) {
            appCatalogConstants = 0;
        }
        else if (mdmStatusConstant == 7 || mdmStatusConstant == 8) {
            appCatalogConstants = 6;
        }
        else if (mdmStatusConstant == 3 || mdmStatusConstant == 18) {
            appCatalogConstants = 1;
        }
        else if (mdmStatusConstant == 200) {
            appCatalogConstants = 0;
        }
        return appCatalogConstants;
    }
    
    public void addOrUpdateAppStatusToAppCatalogResource(final HashMap<String, List> remarksToResMap, final Long collectionID, final int appInstallStatus, final boolean removal) throws Exception {
        final List<String> remarksList = new ArrayList<String>(remarksToResMap.keySet());
        List resourceIdList = new ArrayList();
        final List resourceListCri = new ArrayList();
        String remarks = null;
        if (appInstallStatus != 0 && appInstallStatus != 1 && appInstallStatus != 2 && appInstallStatus != 3 && appInstallStatus != 4 && appInstallStatus != 5 && appInstallStatus != 6) {
            throw new RuntimeException("Status given is " + appInstallStatus + " not in allowed in AppCatalogStatus.");
        }
        for (int i = 0; i < remarksList.size(); ++i) {
            remarks = remarksList.get(i);
            resourceIdList = remarksToResMap.get(remarks);
            resourceListCri.addAll(resourceIdList);
        }
        if (!resourceListCri.isEmpty() && collectionID != null) {
            final Long latestVersionAppId = MDMUtil.getInstance().getAppIDFromCollection(collectionID);
            final Long appGroupId = MDMUtil.getInstance().getAppGroupIDFromCollection(collectionID);
            final Criteria resListCri = new Criteria(Column.getColumn("MdAppCatalogToResource", "RESOURCE_ID"), (Object)resourceListCri.toArray(), 8);
            final Criteria appGroupCri = new Criteria(Column.getColumn("MdAppCatalogToResource", "APP_GROUP_ID"), (Object)appGroupId, 0);
            final Criteria criteria = resListCri.and(appGroupCri);
            final DataObject appCatalogDO = MDMUtil.getPersistence().get("MdAppCatalogToResource", criteria);
            for (int j = 0; j < remarksList.size(); ++j) {
                remarks = remarksList.get(j);
                resourceIdList = remarksToResMap.get(remarks);
                if (!resourceIdList.isEmpty()) {
                    Row mdAppCatalogRow = null;
                    for (int k = 0; k < resourceIdList.size(); ++k) {
                        final Long resId = resourceIdList.get(k);
                        final Criteria resIdCri = new Criteria(Column.getColumn("MdAppCatalogToResource", "RESOURCE_ID"), (Object)resId, 0);
                        mdAppCatalogRow = appCatalogDO.getRow("MdAppCatalogToResource", resIdCri);
                        if (mdAppCatalogRow == null && !removal) {
                            mdAppCatalogRow = new Row("MdAppCatalogToResource");
                            mdAppCatalogRow.set("UPDATED_AT", (Object)System.currentTimeMillis());
                            mdAppCatalogRow.set("REMARKS", (Object)remarks);
                            mdAppCatalogRow.set("STATUS", (Object)appInstallStatus);
                            mdAppCatalogRow.set("APP_GROUP_ID", (Object)appGroupId);
                            mdAppCatalogRow.set("RESOURCE_ID", (Object)resId);
                            mdAppCatalogRow.set("PUBLISHED_APP_ID", (Object)latestVersionAppId);
                            mdAppCatalogRow.set("APPROVED_APP_ID", (Object)latestVersionAppId);
                            appCatalogDO.addRow(mdAppCatalogRow);
                        }
                        else {
                            mdAppCatalogRow.set("UPDATED_AT", (Object)System.currentTimeMillis());
                            mdAppCatalogRow.set("REMARKS", (Object)remarks);
                            mdAppCatalogRow.set("STATUS", (Object)appInstallStatus);
                            mdAppCatalogRow.set("PUBLISHED_APP_ID", (Object)latestVersionAppId);
                            mdAppCatalogRow.set("APPROVED_APP_ID", (Object)latestVersionAppId);
                            appCatalogDO.updateRow(mdAppCatalogRow);
                        }
                    }
                }
            }
            MDMUtil.getPersistence().update(appCatalogDO);
        }
    }
    
    public void updateAppStatus(final HashMap<String, List> remarksToResMap, final Long collectionID, final int collnResStatus) throws DataAccessException {
        this.updateAppStatus(remarksToResMap, collectionID, collnResStatus, false);
    }
    
    public void updateAppStatus(final HashMap<String, List> remarksToResMap, final Long collectionID, final int collnResStatus, final boolean removal) throws DataAccessException {
        try {
            final int appInstallStatus = this.getAppCatalogStatusConstant(collnResStatus);
            this.addOrUpdateAppStatusToAppCatalogResource(remarksToResMap, collectionID, appInstallStatus, removal);
        }
        catch (final Exception ex) {
            AppInstallationStatusHandler.logger.log(Level.SEVERE, "Exception in updateAppStatus...", ex);
        }
        MDMCollectionStatusUpdate.getInstance().updateAppStatusToCollnToResources(remarksToResMap, collectionID, collnResStatus);
    }
    
    public void updateAppStatus(final List<Long> resourceIds, final List<Long> collectionIds, final Map<Long, Long> appGroupIdsToAppIds, final Map<Long, Long> collMap, final Map<Long, Integer> appPackageMap, final int collnResStatus, final String remarks, final int associatedAppSource, final int platformType, final Boolean isSilentInstall) throws DataAccessException {
        final int appInstallStatus = this.getAppCatalogStatusConstant(collnResStatus);
        new AppLicenseMgmtHandler().addOrUpdateStatusInAppCatalogResource(platformType, resourceIds, collectionIds, appGroupIdsToAppIds, collMap, appPackageMap, associatedAppSource, isSilentInstall, appInstallStatus, remarks);
        MDMCollectionStatusUpdate.getInstance().updateStatusForCollntoResDO(resourceIds, collectionIds, collnResStatus, remarks, Boolean.TRUE);
    }
    
    public List<Long> getAppInstalledDevices(final Long appgroupId, final List<Long> deviceList) throws DataAccessException {
        final List<Long> installedDevices = new ArrayList<Long>();
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("MdAppCatalogToResource"));
        final Criteria appgroupCriteria = new Criteria(new Column("MdAppCatalogToResource", "APP_GROUP_ID"), (Object)appgroupId, 0);
        final Criteria deviceListCriteria = new Criteria(new Column("MdAppCatalogToResource", "RESOURCE_ID"), (Object)deviceList.toArray(), 8);
        final Criteria installedStatusCriteria = new Criteria(new Column("MdAppCatalogToResource", "INSTALLED_APP_ID"), (Object)null, 1);
        selectQuery.setCriteria(appgroupCriteria.and(deviceListCriteria).and(installedStatusCriteria));
        selectQuery.addSelectColumn(new Column("MdAppCatalogToResource", "APP_GROUP_ID"));
        selectQuery.addSelectColumn(new Column("MdAppCatalogToResource", "RESOURCE_ID"));
        final DataObject dataObject = MDMUtil.getPersistence().get(selectQuery);
        if (!dataObject.isEmpty()) {
            final Iterator<Row> iterator = dataObject.getRows("MdAppCatalogToResource");
            while (iterator.hasNext()) {
                final Row row = iterator.next();
                installedDevices.add((Long)row.get("RESOURCE_ID"));
            }
        }
        return installedDevices;
    }
    
    static {
        AppInstallationStatusHandler.logger = Logger.getLogger("MDMConfigLogger");
    }
}
