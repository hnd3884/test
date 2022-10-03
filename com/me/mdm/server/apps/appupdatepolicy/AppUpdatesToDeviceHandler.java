package com.me.mdm.server.apps.appupdatepolicy;

import com.adventnet.ds.query.UpdateQuery;
import com.adventnet.ds.query.UpdateQueryImpl;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.me.mdm.server.apps.multiversion.AppVersionDBUtil;
import java.util.logging.Level;
import com.adventnet.persistence.DataAccessException;
import com.adventnet.persistence.Row;
import com.adventnet.persistence.DataObject;
import com.adventnet.ds.query.SelectQuery;
import java.util.ArrayList;
import java.util.Iterator;
import com.me.devicemanagement.framework.server.util.DBUtil;
import com.adventnet.persistence.DataAccess;
import com.adventnet.ds.query.DerivedColumn;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import java.util.List;

public class AppUpdatesToDeviceHandler extends BaseAppUpdateToResourceHandler
{
    @Override
    public List<Long> getResourceListForWhichTheAppToBeScheduled(final List deviceList, final Long profileId, final Long collectionId) throws DataAccessException {
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("MdAppCatalogToResource"));
        selectQuery.addJoin(new Join("MdAppCatalogToResource", "MdAppToCollection", new String[] { "PUBLISHED_APP_ID" }, new String[] { "APP_ID" }, 2));
        selectQuery.addJoin(new Join("MdAppToCollection", "ProfileToCollection", new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, 2));
        selectQuery.addJoin(new Join("MdAppCatalogToResource", "RecentProfileForResource", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2));
        selectQuery.addJoin(new Join("RecentProfileForResource", "AutoAppUpdateConfigToCollection", new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, 2));
        selectQuery.addJoin(new Join("AutoAppUpdateConfigToCollection", "AutoAppUpdateConfigDetails", new String[] { "APP_UPDATE_CONF_ID" }, new String[] { "APP_UPDATE_CONF_ID" }, 2));
        selectQuery.addJoin(new Join("AutoAppUpdateConfigDetails", "AutoAppUpdatePackageConfig", new String[] { "APP_UPDATE_CONF_ID" }, new String[] { "APP_UPDATE_CONF_ID" }, 2));
        selectQuery.addJoin(new Join("AutoAppUpdateConfigDetails", "AutoAppUpdatePackageList", new String[] { "APP_UPDATE_CONF_ID" }, new String[] { "APP_UPDATE_CONF_ID" }, 1));
        selectQuery.addJoin(new Join("AutoAppUpdatePackageList", "MdPackageToAppData", new String[] { "PACKAGE_ID" }, new String[] { "PACKAGE_ID" }, 1));
        selectQuery.addJoin(new Join("MdPackageToAppData", "MdAppToCollection", new String[] { "APP_ID" }, new String[] { "APP_ID" }, "MdPackageToAppData", "PolicyAppCollection", 1));
        final Criteria profileCriteria = new Criteria(Column.getColumn("ProfileToCollection", "PROFILE_ID"), (Object)profileId, 0);
        final Criteria resourceCriteria = new Criteria(Column.getColumn("MdAppCatalogToResource", "RESOURCE_ID"), (Object)deviceList.toArray(), 8);
        final Criteria collectionCriteria = new Criteria(Column.getColumn("MdAppToCollection", "COLLECTION_ID"), (Object)collectionId, 1);
        final Criteria policyAppCollectionCriteria = new Criteria(Column.getColumn("PolicyAppCollection", "COLLECTION_ID"), (Object)collectionId, 0);
        final Criteria policyNotMarkedForDeleteCriteria = new Criteria(Column.getColumn("RecentProfileForResource", "MARKED_FOR_DELETE"), (Object)false, 0);
        final SelectQuery subQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("RecentProfileForResource"));
        final Criteria recentProfileCriteria = new Criteria(Column.getColumn("RecentProfileForResource", "PROFILE_ID"), (Object)profileId, 0);
        final Criteria deviceListCriteria = new Criteria(Column.getColumn("RecentProfileForResource", "RESOURCE_ID"), (Object)deviceList.toArray(), 8);
        final Criteria markedForDeleteCriteria = new Criteria(Column.getColumn("RecentProfileForResource", "MARKED_FOR_DELETE"), (Object)true, 0);
        subQuery.setCriteria(recentProfileCriteria.and(deviceListCriteria).and(markedForDeleteCriteria));
        subQuery.addSelectColumn(Column.getColumn("RecentProfileForResource", "RESOURCE_ID"));
        final DerivedColumn derivedColumn = new DerivedColumn("DC", subQuery);
        final Criteria notInDeviceCriteria = new Criteria(Column.getColumn("MdAppCatalogToResource", "RESOURCE_ID"), (Object)derivedColumn, 9);
        selectQuery.setCriteria(profileCriteria.and(resourceCriteria).and(collectionCriteria).and(notInDeviceCriteria).and(policyAppCollectionCriteria).and(policyNotMarkedForDeleteCriteria));
        selectQuery.addSelectColumn(Column.getColumn("MdAppCatalogToResource", "RESOURCE_ID"));
        selectQuery.addSelectColumn(Column.getColumn("MdAppCatalogToResource", "APP_GROUP_ID"));
        final DataObject dataObject = DataAccess.get(selectQuery);
        if (!dataObject.isEmpty()) {
            final Iterator<Row> iterator = dataObject.getRows("MdAppCatalogToResource");
            return DBUtil.getColumnValuesAsList((Iterator)iterator, "RESOURCE_ID");
        }
        return new ArrayList<Long>();
    }
    
    @Override
    public void updateApprovedAppIdAndItsStatus(final Criteria criteria, final Long approvedAppId, final Integer associatedAppStatus) throws DataAccessException {
        this.configLogger.log(Level.INFO, "Setting approved app Id {0} Status {1} criteria {2} for device", new Object[] { approvedAppId, associatedAppStatus, criteria });
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("MdAppCatalogToResource"));
        selectQuery.addJoin(new Join("MdAppCatalogToResource", "MdAppDetails", new String[] { "APPROVED_APP_ID" }, new String[] { "APP_ID" }, 2));
        selectQuery.addJoin(new Join("MdAppDetails", "MdAppToCollection", new String[] { "APP_ID" }, new String[] { "APP_ID" }, 2));
        selectQuery.addJoin(new Join("MdAppCatalogToResource", "MdAppCatalogToResourceExtn", new String[] { "APP_GROUP_ID", "RESOURCE_ID" }, new String[] { "APP_GROUP_ID", "RESOURCE_ID" }, 2));
        selectQuery.addJoin(new Join("MdAppToCollection", "AppCollnToReleaseLabelHistory", new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, 2));
        selectQuery.addJoin(AppVersionDBUtil.getInstance().getJoinForCollectionsLatestAppReleaseLabelFromHistoryTable());
        selectQuery.addJoin(new Join("AppCollnToReleaseLabelHistory", "AppReleaseLabel", new String[] { "RELEASE_LABEL_ID" }, new String[] { "RELEASE_LABEL_ID" }, 2));
        selectQuery.setCriteria(criteria);
        selectQuery.addSelectColumn(new Column("MdAppCatalogToResource", "*"));
        final DataObject dataObject = DataAccess.get(selectQuery);
        if (!dataObject.isEmpty()) {
            final Iterator<Row> iterator = dataObject.getRows("MdAppCatalogToResource");
            while (iterator.hasNext()) {
                final Row row = iterator.next();
                if (approvedAppId != null) {
                    row.set("APPROVED_APP_ID", (Object)approvedAppId);
                }
                if (associatedAppStatus != null) {
                    row.set("APPROVED_VERSION_STATUS", (Object)associatedAppStatus);
                }
                dataObject.updateRow(row);
            }
        }
        MDMUtil.getPersistence().update(dataObject);
    }
    
    @Override
    public DerivedColumn getResourcesForWhichAppUpdatePolicyIsConfiguredForGivenApp(final Long appGroupId) {
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("RecentProfileForResource"));
        selectQuery.addJoin(new Join("RecentProfileForResource", "AutoAppUpdateConfigToCollection", new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, 2));
        selectQuery.addJoin(new Join("AutoAppUpdateConfigToCollection", "AutoAppUpdateConfigDetails", new String[] { "APP_UPDATE_CONF_ID" }, new String[] { "APP_UPDATE_CONF_ID" }, 2));
        selectQuery.addJoin(new Join("AutoAppUpdateConfigDetails", "AutoAppUpdatePackageConfig", new String[] { "APP_UPDATE_CONF_ID" }, new String[] { "APP_UPDATE_CONF_ID" }, 2));
        selectQuery.addJoin(new Join("AutoAppUpdateConfigDetails", "AutoAppUpdatePackageList", new String[] { "APP_UPDATE_CONF_ID" }, new String[] { "APP_UPDATE_CONF_ID" }, 1));
        selectQuery.addJoin(new Join("AutoAppUpdatePackageList", "MdPackageToAppGroup", new String[] { "PACKAGE_ID" }, new String[] { "PACKAGE_ID" }, 1));
        selectQuery.setCriteria(new Criteria(Column.getColumn("MdPackageToAppGroup", "APP_GROUP_ID"), (Object)appGroupId, 0).and(new Criteria(Column.getColumn("RecentProfileForResource", "MARKED_FOR_DELETE"), (Object)false, 0)));
        selectQuery.addSelectColumn(Column.getColumn("RecentProfileForResource", "RESOURCE_ID").distinct());
        return new DerivedColumn("DC", selectQuery);
    }
    
    private Criteria getAppGroupCriteria(final Long appGroupId) {
        return new Criteria(Column.getColumn("MdAppCatalogToResource", "APP_GROUP_ID"), (Object)appGroupId, 0);
    }
    
    private Criteria getReleaseLabelCriteria(final List labelIds) {
        return new Criteria(Column.getColumn("AppReleaseLabel", "RELEASE_LABEL_ID"), (Object)labelIds.toArray(), 8);
    }
    
    private Criteria getDeviceIdCriteria(final List deviceList) {
        return new Criteria(Column.getColumn("MdAppCatalogToResource", "RESOURCE_ID"), (Object)deviceList.toArray(), 8);
    }
    
    @Override
    public void setIsUpdateAvailable(final Long appGroupId, final List resourceIds) throws Exception {
        this.profileDistLogger.log(Level.INFO, "[Setting App Update True] for device {0} appGroup {1}", new Object[] { resourceIds, appGroupId });
        final UpdateQuery updateQuery = (UpdateQuery)new UpdateQueryImpl("MdAppCatalogToResourceExtn");
        updateQuery.setCriteria(new Criteria(Column.getColumn("MdAppCatalogToResourceExtn", "APP_GROUP_ID"), (Object)appGroupId, 0).and(Column.getColumn("MdAppCatalogToResourceExtn", "RESOURCE_ID"), (Object)resourceIds.toArray(), 8));
        updateQuery.setUpdateColumn("IS_UPDATE_AVAILABLE", (Object)true);
        MDMUtil.getPersistence().update(updateQuery);
    }
    
    @Override
    public Criteria getCriteria(final Long appGroupId, final List resourceIds) {
        return this.getAppGroupCriteria(appGroupId).and(this.getDeviceIdCriteria(resourceIds));
    }
    
    @Override
    public Criteria getCriteria(final DerivedColumn resourcesWithPolicy, final Long appGroupId, final List releaseLabelIds, final Boolean includeResourcesWithPolicy) {
        final Criteria resourceWithPolicy = new Criteria(Column.getColumn("MdAppCatalogToResource", "RESOURCE_ID"), (Object)resourcesWithPolicy, ((boolean)includeResourcesWithPolicy) ? 8 : 9);
        return resourceWithPolicy.and(this.getAppGroupCriteria(appGroupId)).and(this.getReleaseLabelCriteria(releaseLabelIds));
    }
    
    @Override
    public Criteria getCriteria(final List appGroupIds, final List resourceIds, final Boolean isUpdateAvailable) {
        final Criteria appGroupCriteria = new Criteria(Column.getColumn("MdAppCatalogToResource", "APP_GROUP_ID"), (Object)appGroupIds.toArray(), 8);
        final Criteria resourceCriteria = new Criteria(Column.getColumn("MdAppCatalogToResource", "RESOURCE_ID"), (Object)resourceIds.toArray(), 8);
        final Criteria isUpdateAvailableCriteria = new Criteria(Column.getColumn("MdAppCatalogToResourceExtn", "IS_UPDATE_AVAILABLE"), (Object)isUpdateAvailable, 0);
        return appGroupCriteria.and(resourceCriteria.and(isUpdateAvailableCriteria));
    }
    
    @Override
    public Criteria getCriteria(final Long appGroupId, final List resourceIds, final DerivedColumn resourcesWithPolicy, final Boolean isUpdateAvailable) throws Exception {
        final Criteria appGroupCriteria = new Criteria(Column.getColumn("MdAppCatalogToResource", "APP_GROUP_ID"), (Object)appGroupId, 0);
        final Criteria resourceCriteria = new Criteria(Column.getColumn("MdAppCatalogToResource", "RESOURCE_ID"), (Object)resourceIds.toArray(), 8);
        final Criteria isUpdateAvailableCriteria = new Criteria(Column.getColumn("MdAppCatalogToResourceExtn", "IS_UPDATE_AVAILABLE"), (Object)isUpdateAvailable, 0);
        final Criteria resourcesWithPolicyCriteria = new Criteria(Column.getColumn("MdAppCatalogToResource", "RESOURCE_ID"), (Object)resourcesWithPolicy, 9);
        return appGroupCriteria.and(resourceCriteria.and(isUpdateAvailableCriteria).and(resourcesWithPolicyCriteria));
    }
}
