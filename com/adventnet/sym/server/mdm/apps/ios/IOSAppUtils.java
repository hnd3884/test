package com.adventnet.sym.server.mdm.apps.ios;

import com.adventnet.persistence.DataAccessException;
import com.adventnet.sym.server.mdm.apps.AppInstallationStatusHandler;
import com.me.devicemanagement.framework.server.customer.CustomerInfoUtil;
import com.adventnet.sym.server.mdm.apps.AppsUtil;
import com.adventnet.ds.query.DMDataSetWrapper;
import com.adventnet.sym.server.mdm.apps.vpp.VppAppUpdateScheduler;
import org.json.JSONArray;
import com.me.mdm.server.apps.autoupdate.AutoAppUpdateHandler;
import com.adventnet.sym.server.mdm.util.JSONUtil;
import com.me.mdm.server.apps.AppUpdateSync;
import com.adventnet.ds.query.SortColumn;
import com.adventnet.ds.query.DerivedColumn;
import com.me.mdm.server.acp.IOSAppCatalogHandler;
import com.me.mdm.server.config.MDMCollectionUtil;
import com.adventnet.sym.server.mdm.config.MDMCollectionStatusUpdate;
import java.util.ArrayList;
import java.util.List;
import java.util.Iterator;
import com.adventnet.persistence.DataObject;
import com.adventnet.ds.query.SelectQuery;
import java.util.logging.Level;
import com.adventnet.persistence.Row;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import java.util.logging.Logger;

public class IOSAppUtils
{
    private final Logger logger;
    
    public IOSAppUtils() {
        this.logger = Logger.getLogger("MDMAppMgmtLogger");
    }
    
    public int getEnterpriseAppProvisionSignedType(final Long appID) {
        int provType = -1;
        try {
            final SelectQuery adHocCheckQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("AppleProvProfilesExtn"));
            adHocCheckQuery.addJoin(new Join("AppleProvProfilesExtn", "MdAppToProvProfileRel", new String[] { "PROV_ID" }, new String[] { "PROV_ID" }, 2));
            adHocCheckQuery.addJoin(new Join("MdAppToProvProfileRel", "MdAppDetails", new String[] { "APP_ID" }, new String[] { "APP_ID" }, 2));
            adHocCheckQuery.addSelectColumn(new Column("AppleProvProfilesExtn", "PROV_ID"));
            adHocCheckQuery.addSelectColumn(new Column("AppleProvProfilesExtn", "PROV_PROV_SIGNED_TYPE"));
            final Criteria appCriteria = new Criteria(new Column("MdAppDetails", "APP_ID"), (Object)appID, 0);
            adHocCheckQuery.setCriteria(appCriteria);
            final DataObject adHocDo = MDMUtil.getPersistence().get(adHocCheckQuery);
            final Iterator adHocItr = adHocDo.getRows("AppleProvProfilesExtn", (Criteria)null);
            if (adHocItr.hasNext()) {
                final Row provExtn = adHocItr.next();
                provType = (int)provExtn.get("PROV_PROV_SIGNED_TYPE");
            }
            else {
                this.logger.log(Level.SEVERE, "App provision details is not available for APP_ID = {0} ", appID);
            }
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, "Exception in getEnterpriseAppProvisionSignedType", ex);
        }
        return provType;
    }
    
    public List<Long> getAdhocProvisionedDevicesForAppleEnterpriseApp(final List<Long> resourceList, final Long appID, final Long customerID) {
        final List<Long> adhocRegisteredDevicesList = new ArrayList<Long>();
        try {
            final SelectQuery udidQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("ManagedDevice"));
            udidQuery.addJoin(new Join("ManagedDevice", "Resource", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2));
            udidQuery.addJoin(new Join("Resource", "AppleProvProfilesDetails", new String[] { "CUSTOMER_ID" }, new String[] { "CUSTOMER_ID" }, 2));
            udidQuery.addJoin(new Join("AppleProvProfilesDetails", "MdAppToProvProfileRel", new String[] { "PROV_ID" }, new String[] { "PROV_ID" }, 2));
            Criteria udidCrit = new Criteria(Column.getColumn("AppleProvProfilesDetails", "PROV_ID"), (Object)Column.getColumn("AppleProvProfilesToUDID", "PROV_ID"), 0);
            udidCrit = udidCrit.and(Column.getColumn("AppleProvProfilesToUDID", "UDID"), (Object)Column.getColumn("ManagedDevice", "UDID"), 0);
            final Join udidJoin = new Join(Table.getTable("ManagedDevice"), Table.getTable("AppleProvProfilesToUDID"), udidCrit, 2);
            udidQuery.addJoin(udidJoin);
            udidQuery.addSelectColumn(new Column("ManagedDevice", "RESOURCE_ID"));
            final Criteria resCriteria = new Criteria(new Column("ManagedDevice", "RESOURCE_ID"), (Object)resourceList.toArray(), 8);
            final Criteria appCriteria = new Criteria(new Column("MdAppToProvProfileRel", "APP_ID"), (Object)appID, 0);
            final Criteria custCriteria = new Criteria(new Column("AppleProvProfilesDetails", "CUSTOMER_ID"), (Object)customerID, 0);
            udidQuery.setCriteria(resCriteria.and(appCriteria).and(custCriteria));
            final DataObject resDo = MDMUtil.getPersistence().get(udidQuery);
            if (resDo.isEmpty()) {
                this.logger.log(Level.FINE, "No selected device have been registered for the app with app_id : {0}", new Object[] { appID });
            }
            final Iterator regDevicesItr = resDo.getRows("ManagedDevice", (Criteria)null);
            while (regDevicesItr.hasNext()) {
                final Row regDevice = regDevicesItr.next();
                final Long resourceID = (Long)regDevice.get("RESOURCE_ID");
                adhocRegisteredDevicesList.add(resourceID);
            }
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, "Exception in getAdhocProvisionedDevicesForAppleEnterpriseApp", ex);
        }
        return adhocRegisteredDevicesList;
    }
    
    public void removeExpiredEnterpriseAppFromAppCatalog(final Long customerID) {
        try {
            final SelectQuery sQuery = (SelectQuery)new SelectQueryImpl(new Table("MdPackage"));
            sQuery.addJoin(new Join("MdPackage", "MdPackageToAppData", new String[] { "PACKAGE_ID" }, new String[] { "PACKAGE_ID" }, 2));
            sQuery.addJoin(new Join("MdPackageToAppData", "MdPackageToAppGroup", new String[] { "PACKAGE_ID" }, new String[] { "PACKAGE_ID" }, 2));
            sQuery.addJoin(new Join("MdPackageToAppGroup", "MdAppGroupDetails", new String[] { "APP_GROUP_ID" }, new String[] { "APP_GROUP_ID" }, 2));
            sQuery.addJoin(new Join("MdPackageToAppData", "MdAppToProvProfileRel", new String[] { "APP_ID" }, new String[] { "APP_ID" }, 2));
            sQuery.addJoin(new Join("MdAppToProvProfileRel", "AppleProvProfilesDetails", new String[] { "PROV_ID" }, new String[] { "PROV_ID" }, 2));
            sQuery.addJoin(new Join("MdPackageToAppData", "MdAppToCollection", new String[] { "APP_ID" }, new String[] { "APP_ID" }, 2));
            sQuery.addJoin(new Join("MdAppToCollection", "CollnToResources", new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, 2));
            sQuery.addJoin(new Join("CollnToResources", "MDMCollnToResErrorCode", new String[] { "RESOURCE_ID", "COLLECTION_ID" }, new String[] { "RESOURCE_ID", "COLLECTION_ID" }, 1));
            sQuery.addSelectColumn(Column.getColumn("MdAppToCollection", "*"));
            sQuery.addSelectColumn(Column.getColumn("CollnToResources", "COLLECTION_ID"));
            sQuery.addSelectColumn(Column.getColumn("CollnToResources", "RESOURCE_ID"));
            final Criteria enterprisePkgTypeCri = new Criteria(new Column("MdPackageToAppGroup", "PACKAGE_TYPE"), (Object)2, 0);
            final Criteria platformCri = new Criteria(new Column("MdAppGroupDetails", "PLATFORM_TYPE"), (Object)1, 0);
            final Criteria notMacOnlyCrit = new Criteria(new Column("MdPackageToAppData", "SUPPORTED_DEVICES"), (Object)16, 1);
            final Criteria custCrit = new Criteria(new Column("MdPackage", "CUSTOMER_ID"), (Object)customerID, 0);
            final Criteria appExpiryCrit = new Criteria(new Column("AppleProvProfilesDetails", "PROV_EXPIRY_DATE"), (Object)System.currentTimeMillis(), 7);
            final Criteria errCrit = new Criteria(Column.getColumn("MDMCollnToResErrorCode", "ERROR_CODE"), (Object)null, 0);
            Criteria criteria = enterprisePkgTypeCri.and(platformCri).and(notMacOnlyCrit).and(errCrit);
            criteria = criteria.and(custCrit).and(appExpiryCrit);
            sQuery.setCriteria(criteria);
            final DataObject appDO = MDMUtil.getReadOnlyPersistence().get(sQuery);
            final Iterator collItr = appDO.getRows("MdAppToCollection");
            if (appDO.isEmpty()) {
                this.logger.log(Level.FINE, "No expired apps for Processing");
            }
            while (collItr.hasNext()) {
                final Row collRow = collItr.next();
                final Long collectionID = (Long)collRow.get("COLLECTION_ID");
                final Long appID = (Long)collRow.get("APP_ID");
                final Iterator resItr = appDO.getRows("CollnToResources", new Criteria(Column.getColumn("CollnToResources", "COLLECTION_ID"), (Object)collectionID, 0));
                final List resourceList = new ArrayList();
                while (resItr.hasNext()) {
                    final Row resourceRow = resItr.next();
                    final Long resourceID = (Long)resourceRow.get("RESOURCE_ID");
                    resourceList.add(resourceID);
                }
                if (!resourceList.isEmpty() && collectionID != null) {
                    final Long expiryDate = this.getAppExpiryDate(appID);
                    MDMCollectionStatusUpdate.getInstance().updateStatusForCollntoRes(resourceList, collectionID, 8, "dc.mdm.devicemgmt.app_expired_distribution_error@@@LongTime:" + expiryDate);
                    MDMCollectionUtil.addOrUpdateCollnToResErrorCode(resourceList, collectionID, 21012);
                    this.logger.log(Level.INFO, "The distribution details is changed for the resources with resource_ids = {0} since the app with app_id = {1} is expired.", new Object[] { resourceList, collectionID });
                    new IOSAppCatalogHandler().deleteAppCatalogForDevices(appID, resourceList);
                    this.logger.log(Level.INFO, "The app catalog details for the resources with ids : {0} is removed since the app with app_id = {1} is expired", new Object[] { resourceList, appID });
                }
            }
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Exception in removeExpiredEnterpriseAppFromAppCatalog", e);
        }
    }
    
    public void updateAlreadyDistributedAdhocAppDetails(final Long appID, final Long customerID) {
        try {
            final SelectQuery udidQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("AppleProvProfilesToUDID"));
            udidQuery.addJoin(new Join("AppleProvProfilesToUDID", "AppleProvProfilesDetails", new String[] { "PROV_ID" }, new String[] { "PROV_ID" }, 2));
            udidQuery.addJoin(new Join("AppleProvProfilesDetails", "MdAppToProvProfileRel", new String[] { "PROV_ID" }, new String[] { "PROV_ID" }, 2));
            final Criteria appCriteria = new Criteria(Column.getColumn("MdAppToProvProfileRel", "APP_ID"), (Object)appID, 0);
            final Criteria custCriteria = new Criteria(Column.getColumn("AppleProvProfilesDetails", "CUSTOMER_ID"), (Object)customerID, 0);
            udidQuery.addSelectColumn(Column.getColumn("AppleProvProfilesToUDID", "UDID"));
            udidQuery.setCriteria(appCriteria.and(custCriteria));
            final SelectQuery resourceQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("CollnToResources"));
            resourceQuery.addJoin(new Join("CollnToResources", "MdAppToCollection", new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, 2));
            resourceQuery.addJoin(new Join("CollnToResources", "Resource", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2));
            resourceQuery.addJoin(new Join("Resource", "ManagedDevice", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2));
            final Criteria appCriteria2 = new Criteria(Column.getColumn("MdAppToCollection", "APP_ID"), (Object)appID, 0);
            final Criteria custCriteria2 = new Criteria(Column.getColumn("Resource", "CUSTOMER_ID"), (Object)customerID, 0);
            final Criteria udidCriteria = new Criteria(Column.getColumn("ManagedDevice", "UDID"), (Object)new DerivedColumn("UDID", udidQuery), 9);
            resourceQuery.addSelectColumn(Column.getColumn("CollnToResources", "COLLECTION_ID"));
            resourceQuery.addSelectColumn(Column.getColumn("CollnToResources", "RESOURCE_ID"));
            resourceQuery.setCriteria(appCriteria2.and(custCriteria2.and(udidCriteria)));
            final DataObject resDo = MDMUtil.getPersistence().get(resourceQuery);
            final List adhocUnRegisteredDevicesList = new ArrayList();
            long collectionId = -1L;
            if (resDo.isEmpty()) {
                this.logger.log(Level.FINE, "No unregistered devices have been distributed with the Ad-Hoc app with app_id = {0}", new Object[] { appID });
            }
            final Iterator unRegDevicesItr = resDo.getRows("CollnToResources", (Criteria)null);
            while (unRegDevicesItr.hasNext()) {
                final Row regDevice = unRegDevicesItr.next();
                collectionId = (long)regDevice.get("COLLECTION_ID");
                final Long resourceID = (Long)regDevice.get("RESOURCE_ID");
                adhocUnRegisteredDevicesList.add(resourceID);
            }
            if (!adhocUnRegisteredDevicesList.isEmpty() && collectionId != 1L) {
                MDMCollectionStatusUpdate.getInstance().updateStatusForCollntoRes(adhocUnRegisteredDevicesList, collectionId, 8, "dc.mdm.devicemgmt.device_not_registered_for_adhoc");
                this.logger.log(Level.INFO, "The distribution details is changed for the resources with resource_ids : {0} for app with app_id = {1} since the resources are not registered for the app.", new Object[] { adhocUnRegisteredDevicesList, appID });
                MDMCollectionUtil.addOrUpdateCollnToResErrorCode(adhocUnRegisteredDevicesList, collectionId, 21013);
                new IOSAppCatalogHandler().deleteAppCatalogForDevices(appID, adhocUnRegisteredDevicesList);
                this.logger.log(Level.INFO, "The app catalog details for the resources with ids : {0} is removed for the app with app_id = {1} since the resources are not registered for the app", new Object[] { adhocUnRegisteredDevicesList, appID });
            }
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, "Exception in updateAlreadyDistributedAppDetails", ex);
        }
    }
    
    public long getAppExpiryDate(final Long appID) {
        long expiryDate = -1L;
        try {
            final SelectQuery appExpQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("AppleProvProfilesDetails"));
            appExpQuery.addJoin(new Join("AppleProvProfilesDetails", "MdAppToProvProfileRel", new String[] { "PROV_ID" }, new String[] { "PROV_ID" }, 2));
            final Criteria appCriteria = new Criteria(new Column("MdAppToProvProfileRel", "APP_ID"), (Object)appID, 0);
            appExpQuery.addSelectColumn(Column.getColumn("AppleProvProfilesDetails", "PROV_ID"));
            appExpQuery.addSelectColumn(Column.getColumn("AppleProvProfilesDetails", "PROV_EXPIRY_DATE"));
            appExpQuery.setCriteria(appCriteria);
            final SortColumn sortColumn = new SortColumn(Column.getColumn("AppleProvProfilesDetails", "PROV_EXPIRY_DATE"), false);
            appExpQuery.addSortColumn(sortColumn);
            final DataObject appProvDO = MDMUtil.getPersistence().get(appExpQuery);
            if (appProvDO != null && !appProvDO.isEmpty()) {
                final Row appProvRow = appProvDO.getFirstRow("AppleProvProfilesDetails");
                expiryDate = (long)appProvRow.get("PROV_EXPIRY_DATE");
            }
            else {
                this.logger.log(Level.SEVERE, "App provision details is not found for app with id = {0} ", appID);
            }
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Exception in getAppExpiryDate", e);
        }
        return expiryDate;
    }
    
    public void updateAndPushIOSAppsToDevices(final Long customerID) {
        try {
            final AppUpdateSync upgradeSync = new AppUpdateSync(customerID, 1);
            upgradeSync.syncNonPortalApps();
            final JSONArray updatedAppGroupList = upgradeSync.getUpdateAppGroupList();
            if (updatedAppGroupList != null && updatedAppGroupList.length() > 0) {
                final List<Long> appGroupList = new JSONUtil().convertLongJSONArrayTOList(updatedAppGroupList);
                AutoAppUpdateHandler.getInstance().handleAutoAppUpdate(customerID, appGroupList);
            }
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Exception in updateAndPushIOSAppsToDevices", e);
        }
    }
    
    public void syncAndUpdateIOSApps(final Long customerID) {
        try {
            final VppAppUpdateScheduler vppSchedule = new VppAppUpdateScheduler();
            vppSchedule.executeVPPTask(customerID);
            this.updateAndPushIOSAppsToDevices(customerID);
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Exception in syncAndUpdateIOSApps", e);
        }
    }
    
    public Long getMeMdmAppBusinessStoreID(final Long customerID) {
        Long businessStoreID = null;
        try {
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("MdAppGroupDetails"));
            selectQuery.addJoin(new Join("MdAppGroupDetails", "MdStoreAssetToAppGroupRel", new String[] { "APP_GROUP_ID" }, new String[] { "APP_GROUP_ID" }, 2));
            selectQuery.addJoin(new Join("MdStoreAssetToAppGroupRel", "MdVppAsset", new String[] { "STORE_ASSET_ID" }, new String[] { "VPP_ASSET_ID" }, 2));
            selectQuery.addJoin(new Join("MdStoreAssetToAppGroupRel", "MdBusinessStoreToAssetRel", new String[] { "STORE_ASSET_ID" }, new String[] { "STORE_ASSET_ID" }, 2));
            selectQuery.addJoin(new Join("MdBusinessStoreToAssetRel", "MdBusinessStoreToVppRel", new String[] { "BUSINESSSTORE_ID" }, new String[] { "BUSINESSSTORE_ID" }, 2));
            selectQuery.addJoin(new Join("MdBusinessStoreToVppRel", "MdVPPTokenDetails", new String[] { "TOKEN_ID" }, new String[] { "TOKEN_ID" }, 2));
            final SortColumn sortColumn = new SortColumn(Column.getColumn("MdVPPTokenDetails", "VPP_TOKEN_ADDED_TIME"), false);
            selectQuery.addSortColumn(sortColumn);
            selectQuery.addSelectColumn(Column.getColumn("MdBusinessStoreToVppRel", "BUSINESSSTORE_ID"));
            final Criteria availableLicenseCriteria = new Criteria(Column.getColumn("MdVppAsset", "AVAILABLE_LICENSE_COUNT"), (Object)0, 5);
            final Criteria meMDMAppCriteria = new Criteria(Column.getColumn("MdAppGroupDetails", "IDENTIFIER"), (Object)"com.manageengine.mdm.iosagent", 0);
            final Criteria customerCriteria = new Criteria(Column.getColumn("MdAppGroupDetails", "CUSTOMER_ID"), (Object)customerID, 0);
            selectQuery.setCriteria(meMDMAppCriteria.and(customerCriteria).and(availableLicenseCriteria));
            final DMDataSetWrapper ds = DMDataSetWrapper.executeQuery((Object)selectQuery);
            if (ds.next()) {
                businessStoreID = (Long)ds.getValue("BUSINESSSTORE_ID");
            }
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Exception in getMeMdmAppBusinessStoreID", e);
        }
        return businessStoreID;
    }
    
    public boolean isNativeAppInstalledInDevice(final Long resourceID, final boolean isMac) throws DataAccessException {
        boolean isNativeAppInstalled = false;
        final List resList = new ArrayList();
        resList.add(resourceID);
        String identifier = "com.manageengine.mdm.iosagent";
        if (isMac) {
            identifier = "com.manageengine.mdm.mac";
        }
        final Long appGroupID = AppsUtil.getInstance().getAppGroupIDFromIdentifier(identifier, 1, CustomerInfoUtil.getInstance().getCustomerIDForResID(Long.valueOf(resList.get(0))));
        final List appAlreadyInstalledResList = new AppInstallationStatusHandler().getAppInstalledDevices(appGroupID, resList);
        if (appAlreadyInstalledResList != null && !appAlreadyInstalledResList.isEmpty()) {
            isNativeAppInstalled = true;
        }
        return isNativeAppInstalled;
    }
}
