package com.adventnet.sym.server.mdm.apps;

import java.util.HashMap;
import java.util.Properties;
import java.util.Collection;
import com.adventnet.persistence.DataAccess;
import com.adventnet.persistence.DataAccessException;
import java.util.Iterator;
import com.adventnet.persistence.DataObject;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.persistence.Row;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import java.util.ArrayList;
import com.adventnet.ds.query.DeleteQuery;
import java.util.logging.Level;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.DeleteQueryImpl;
import java.util.List;
import java.util.logging.Logger;

public class MDDeviceInstalledAppsHandler
{
    public Logger logger;
    
    public MDDeviceInstalledAppsHandler() {
        this.logger = Logger.getLogger("MDMConfigLogger");
    }
    
    public void removeInstalledAppResourceRelation(final List resourceIdList, final Long appGroupId) {
        try {
            final DeleteQuery delQuery = (DeleteQuery)new DeleteQueryImpl("MdInstalledAppResourceRel");
            delQuery.addJoin(new Join("MdInstalledAppResourceRel", "MdAppToGroupRel", new String[] { "APP_ID" }, new String[] { "APP_ID" }, 2));
            final Criteria cri1 = new Criteria(Column.getColumn("MdInstalledAppResourceRel", "RESOURCE_ID"), (Object)resourceIdList.toArray(), 8);
            final Criteria cri2 = new Criteria(Column.getColumn("MdAppToGroupRel", "APP_GROUP_ID"), (Object)appGroupId, 0);
            final Criteria cri3 = new Criteria(Column.getColumn("MdInstalledAppResourceRel", "USER_INSTALLED_APPS"), (Object)1, 0);
            delQuery.setCriteria(cri1.and(cri2).and(cri3));
            MDMUtil.getPersistence().delete(delQuery);
            this.logger.log(Level.INFO, "App group {0} removed from inventroy data of resource {1}", new Object[] { appGroupId, resourceIdList });
        }
        catch (final Exception ex) {
            this.logger.log(Level.WARNING, "Exception in removeInstalledAppResourceRelation...", ex);
        }
    }
    
    private List getLatestAppIDsFromCollectionID(final Long collectionID) throws DataAccessException {
        final List appIds = new ArrayList();
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("MdAppToCollection"));
        selectQuery.addJoin(new Join("MdAppToCollection", "AppGroupToCollection", new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, 1));
        final Criteria collnCriteria = new Criteria(Column.getColumn("MdAppToCollection", "COLLECTION_ID"), (Object)collectionID, 0);
        selectQuery.addSelectColumn(Column.getColumn("MdAppToCollection", "*"));
        selectQuery.setCriteria(collnCriteria);
        final DataObject dataObject = MDMUtil.getPersistence().get(selectQuery);
        if (!dataObject.isEmpty()) {
            final Iterator iterator = dataObject.getRows("MdAppToCollection");
            while (iterator.hasNext()) {
                final Row row = iterator.next();
                appIds.add(row.get("APP_ID"));
            }
        }
        return appIds;
    }
    
    public ArrayList removeInstalledAppResourceFromList(final List resourceList, final Long collectionId) {
        final ArrayList installedAppResourceList = new ArrayList();
        final ArrayList removedInstalledAppResourceList = new ArrayList();
        try {
            if (!resourceList.isEmpty() && collectionId != null) {
                final List appIds = this.getLatestAppIDsFromCollectionID(collectionId);
                final Criteria appIdCri = new Criteria(Column.getColumn("MdAppCatalogToResource", "PUBLISHED_APP_ID"), (Object)appIds.toArray(), 8);
                final Criteria resourceListCri = new Criteria(Column.getColumn("MdAppCatalogToResource", "RESOURCE_ID"), (Object)resourceList.toArray(), 8);
                final Criteria installedStatusCri = new Criteria(Column.getColumn("MdAppCatalogToResource", "STATUS"), (Object)2, 0);
                final Criteria cri = appIdCri.and(resourceListCri).and(installedStatusCri);
                final DataObject dObj = DataAccess.get("MdAppCatalogToResource", cri);
                removedInstalledAppResourceList.addAll(resourceList);
                if (!dObj.isEmpty()) {
                    final Iterator itr = dObj.getRows("MdAppCatalogToResource");
                    Row row = null;
                    while (itr.hasNext()) {
                        row = itr.next();
                        final Long resourceId = (Long)row.get("RESOURCE_ID");
                        if (!installedAppResourceList.contains(resourceId)) {
                            removedInstalledAppResourceList.remove(resourceId);
                            installedAppResourceList.add(resourceId);
                        }
                    }
                }
            }
            this.logger.log(Level.INFO, "Collection {0} removed from MDAPPCATALOGTORESOURCE resource {1}", new Object[] { collectionId, resourceList });
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, "Exception in getInstalledAppsResourceList...", ex);
        }
        return removedInstalledAppResourceList;
    }
    
    public void removeInstalledAppResourceRelation(final Long resourceId, final Long appGroupId) {
        try {
            final DeleteQuery delQuery = (DeleteQuery)new DeleteQueryImpl("MdInstalledAppResourceRel");
            delQuery.addJoin(new Join("MdInstalledAppResourceRel", "MdAppToGroupRel", new String[] { "APP_ID" }, new String[] { "APP_ID" }, 2));
            final Criteria cri1 = new Criteria(Column.getColumn("MdInstalledAppResourceRel", "RESOURCE_ID"), (Object)resourceId, 0);
            final Criteria cri2 = new Criteria(Column.getColumn("MdAppToGroupRel", "APP_GROUP_ID"), (Object)appGroupId, 0);
            final Criteria cri3 = new Criteria(Column.getColumn("MdInstalledAppResourceRel", "USER_INSTALLED_APPS"), (Object)1, 0);
            delQuery.setCriteria(cri1.and(cri2).and(cri3));
            MDMUtil.getPersistence().delete(delQuery);
            this.logger.log(Level.INFO, "App group {0} removed from inventory data of resource {1}", new Object[] { appGroupId, resourceId });
        }
        catch (final Exception ex) {
            this.logger.log(Level.WARNING, "Exception in removeInstalledAppResourceRelation...", ex);
        }
    }
    
    public void addOrUpdateWpMSIInstalledAppRel(final Long resourceID, final Long appID, final Properties props) throws Exception {
        final int scope = Integer.valueOf(props.getProperty("SCOPE"));
        final DataObject mdInstallAppResRelDO = MDMUtil.getPersistence().get("MdInstalledAppResourceRel", new Criteria(Column.getColumn("MdInstalledAppResourceRel", "RESOURCE_ID"), (Object)resourceID, 0).and(Column.getColumn("MdInstalledAppResourceRel", "APP_ID"), (Object)appID, 0));
        Row mdInstallAppResRelRow;
        if (!mdInstallAppResRelDO.isEmpty()) {
            mdInstallAppResRelRow = mdInstallAppResRelDO.getFirstRow("MdInstalledAppResourceRel");
        }
        else {
            mdInstallAppResRelRow = new Row("MdInstalledAppResourceRel");
        }
        mdInstallAppResRelRow.set("RESOURCE_ID", (Object)resourceID);
        mdInstallAppResRelRow.set("APP_ID", (Object)appID);
        mdInstallAppResRelRow.set("SCOPE", (Object)scope);
        final String userInstalled = Integer.toString(1);
        final int installationType = Integer.valueOf(props.getProperty("USER_INSTALLED_APPS", userInstalled));
        mdInstallAppResRelRow.set("USER_INSTALLED_APPS", (Object)installationType);
        mdInstallAppResRelRow.set("UPDATED_AT", (Object)System.currentTimeMillis());
        if (!mdInstallAppResRelDO.isEmpty()) {
            mdInstallAppResRelDO.updateRow(mdInstallAppResRelRow);
        }
        else {
            mdInstallAppResRelDO.addRow(mdInstallAppResRelRow);
        }
        MDMUtil.getPersistence().update(mdInstallAppResRelDO);
        this.logger.log(Level.INFO, "Inventory data of app {0} updated for resource {1} with {2}", new Object[] { appID, resourceID, props });
    }
    
    public void addOrUpdateInstalledAppResourceRel(final Long resourceID, final HashMap appMap, final int scope, final int installationType) throws DataAccessException {
        final Long appId = appMap.getOrDefault("APP_ID", null);
        final Long appGroupId = appMap.get("APP_GROUP_ID");
        final SelectQuery query = (SelectQuery)new SelectQueryImpl(Table.getTable("MdInstalledAppResourceRel"));
        final Join appGroup = new Join("MdInstalledAppResourceRel", "MdAppToGroupRel", new String[] { "APP_ID" }, new String[] { "APP_ID" }, 2);
        query.addJoin(appGroup);
        final Criteria resCri = new Criteria(Column.getColumn("MdInstalledAppResourceRel", "RESOURCE_ID"), (Object)resourceID, 0);
        final Criteria appGroupCri = new Criteria(Column.getColumn("MdAppToGroupRel", "APP_GROUP_ID"), (Object)appGroupId, 0);
        query.setCriteria(resCri.and(appGroupCri));
        query.addSelectColumn(Column.getColumn("MdInstalledAppResourceRel", "APP_ID"));
        query.addSelectColumn(Column.getColumn("MdInstalledAppResourceRel", "RESOURCE_ID"));
        query.addSelectColumn(Column.getColumn("MdInstalledAppResourceRel", "SCOPE"));
        query.addSelectColumn(Column.getColumn("MdAppToGroupRel", "APP_ID"));
        query.addSelectColumn(Column.getColumn("MdAppToGroupRel", "APP_GROUP_ID"));
        final DataObject dO = MDMUtil.getPersistence().get(query);
        Row mdInstallAppResRelRow;
        if (!dO.isEmpty()) {
            mdInstallAppResRelRow = dO.getFirstRow("MdInstalledAppResourceRel");
        }
        else {
            mdInstallAppResRelRow = new Row("MdInstalledAppResourceRel");
        }
        mdInstallAppResRelRow.set("RESOURCE_ID", (Object)resourceID);
        mdInstallAppResRelRow.set("APP_ID", (Object)appId);
        mdInstallAppResRelRow.set("SCOPE", (Object)scope);
        mdInstallAppResRelRow.set("USER_INSTALLED_APPS", (Object)installationType);
        mdInstallAppResRelRow.set("UPDATED_AT", (Object)System.currentTimeMillis());
        if (!dO.isEmpty()) {
            dO.updateRow(mdInstallAppResRelRow);
        }
        else {
            dO.addRow(mdInstallAppResRelRow);
        }
        MDMUtil.getPersistence().update(dO);
        this.logger.log(Level.INFO, "App {0}:{1} updated in inventory data of resource {2}:{3}", new Object[] { appGroupId, appId, resourceID, (scope == 1) ? "container" : "device" });
    }
}
