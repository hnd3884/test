package com.me.mdm.server.apps.appupdatepolicy;

import com.adventnet.persistence.Row;
import java.util.Iterator;
import com.me.devicemanagement.framework.server.util.DBUtil;
import java.util.ArrayList;
import java.util.List;
import com.adventnet.persistence.DataAccessException;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.persistence.DataAccess;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.adventnet.persistence.DataObject;

public class AppUpdatePolicyDBHandler
{
    private static AppUpdatePolicyDBHandler appUpdatePolicyDBHandler;
    
    public static AppUpdatePolicyDBHandler getInstance() {
        if (AppUpdatePolicyDBHandler.appUpdatePolicyDBHandler == null) {
            AppUpdatePolicyDBHandler.appUpdatePolicyDBHandler = new AppUpdatePolicyDBHandler();
        }
        return AppUpdatePolicyDBHandler.appUpdatePolicyDBHandler;
    }
    
    public DataObject getAppUpdatePolicyDO(final Long profileId) throws DataAccessException {
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("Profile"));
        selectQuery.addJoin(new Join("Profile", "AaaUser", new String[] { "CREATED_BY" }, new String[] { "USER_ID" }, "Profile", "CREATED_BY_USER", 1));
        selectQuery.addJoin(new Join("Profile", "AaaUser", new String[] { "LAST_MODIFIED_BY" }, new String[] { "USER_ID" }, "Profile", "LAST_MODIFIED_BY_USER", 1));
        selectQuery.addJoin(new Join("Profile", "RecentPubProfileToColln", new String[] { "PROFILE_ID" }, new String[] { "PROFILE_ID" }, 2));
        selectQuery.addJoin(new Join("RecentPubProfileToColln", "AutoAppUpdateConfigToCollection", new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, 2));
        selectQuery.addJoin(new Join("AutoAppUpdateConfigToCollection", "AutoAppUpdateConfigDetails", new String[] { "APP_UPDATE_CONF_ID" }, new String[] { "APP_UPDATE_CONF_ID" }, 2));
        selectQuery.addJoin(new Join("AutoAppUpdateConfigDetails", "AutoAppUpdatePackageConfig", new String[] { "APP_UPDATE_CONF_ID" }, new String[] { "APP_UPDATE_CONF_ID" }, 2));
        selectQuery.addJoin(new Join("AutoAppUpdateConfigDetails", "AutoAppUpdatePackageList", new String[] { "APP_UPDATE_CONF_ID" }, new String[] { "APP_UPDATE_CONF_ID" }, 1));
        selectQuery.addJoin(new Join("AutoAppUpdatePackageList", "MdPackageToAppGroup", new String[] { "PACKAGE_ID" }, new String[] { "PACKAGE_ID" }, 1));
        selectQuery.addJoin(new Join("MdPackageToAppGroup", "MdAppGroupDetails", new String[] { "APP_GROUP_ID" }, new String[] { "APP_GROUP_ID" }, 1));
        selectQuery.addJoin(new Join("AutoAppUpdateConfigToCollection", "AppUpdateDeploymentPolicy", new String[] { "DEPLOYMENT_TEMPLATE_ID" }, new String[] { "DEPLOYMENT_TEMPLATE_ID" }, 2));
        selectQuery.addJoin(new Join("AutoAppUpdateConfigToCollection", "DeploymentWindowTemplate", new String[] { "DEPLOYMENT_TEMPLATE_ID" }, new String[] { "DEPLOYMENT_TEMPLATE_ID" }, 1));
        selectQuery.addJoin(new Join("AutoAppUpdateConfigToCollection", "AppUpdatePolicyCollnToScheduleRepo", new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, 1));
        selectQuery.addJoin(new Join("AppUpdatePolicyCollnToScheduleRepo", "ScheduleRepository", new String[] { "SCHEDULE_ID" }, new String[] { "SCHEDULE_ID" }, 1));
        selectQuery.setCriteria(new Criteria(Column.getColumn("Profile", "PROFILE_ID"), (Object)profileId, 0));
        selectQuery.addSelectColumn(Column.getColumn((String)null, "*"));
        return DataAccess.get(selectQuery);
    }
    
    public List getListOfCollectionsForGivenPolicy(final Long profileId) throws DataAccessException {
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("ProfileToCollection"));
        selectQuery.setCriteria(new Criteria(Column.getColumn("ProfileToCollection", "PROFILE_ID"), (Object)profileId, 0));
        selectQuery.addSelectColumn(Column.getColumn((String)null, "*"));
        final DataObject dataObject = DataAccess.get(selectQuery);
        if (dataObject.isEmpty()) {
            return new ArrayList();
        }
        final Iterator<Row> iterator = dataObject.getRows("ProfileToCollection");
        return DBUtil.getColumnValuesAsList((Iterator)iterator, "COLLECTION_ID");
    }
    
    public List getListOfPoliciesForGivenProfile(final Long profileId) throws DataAccessException {
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("ProfileToCollection"));
        selectQuery.addJoin(new Join("ProfileToCollection", "AutoAppUpdateConfigToCollection", new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, 2));
        selectQuery.setCriteria(new Criteria(Column.getColumn("ProfileToCollection", "PROFILE_ID"), (Object)profileId, 0));
        selectQuery.addSelectColumn(Column.getColumn("AutoAppUpdateConfigToCollection", "*"));
        final DataObject dataObject = DataAccess.get(selectQuery);
        if (dataObject.isEmpty()) {
            return new ArrayList();
        }
        final Iterator<Row> iterator = dataObject.getRows("AutoAppUpdateConfigToCollection");
        return DBUtil.getColumnValuesAsList((Iterator)iterator, "APP_UPDATE_CONF_ID");
    }
    
    public List<Long> getListOfAppsInGivenPolicy(final Long collectionId) throws DataAccessException {
        List<Long> appsList = new ArrayList<Long>();
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("AutoAppUpdateConfigToCollection"));
        selectQuery.addJoin(new Join("AutoAppUpdateConfigToCollection", "AutoAppUpdatePackageList", new String[] { "APP_UPDATE_CONF_ID" }, new String[] { "APP_UPDATE_CONF_ID" }, 2));
        selectQuery.addJoin(new Join("AutoAppUpdatePackageList", "MdPackageToAppGroup", new String[] { "PACKAGE_ID" }, new String[] { "PACKAGE_ID" }, 2));
        selectQuery.addSelectColumn(Column.getColumn("MdPackageToAppGroup", "APP_GROUP_ID"));
        selectQuery.addSelectColumn(Column.getColumn("MdPackageToAppGroup", "PACKAGE_ID"));
        selectQuery.setCriteria(new Criteria(Column.getColumn("AutoAppUpdateConfigToCollection", "COLLECTION_ID"), (Object)collectionId, 0));
        final DataObject dataObject = DataAccess.get(selectQuery);
        if (!dataObject.isEmpty()) {
            final Iterator<Row> iterator = dataObject.getRows("MdPackageToAppGroup");
            appsList = DBUtil.getColumnValuesAsList((Iterator)iterator, "APP_GROUP_ID");
        }
        return appsList;
    }
    
    public List<Long> getAssociatedGroupIds(final Long collectionId) throws DataAccessException {
        List<Long> associatedGroupIds = new ArrayList<Long>();
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("RecentProfileForGroup"));
        final Criteria collectionCriteria = new Criteria(Column.getColumn("RecentProfileForGroup", "COLLECTION_ID"), (Object)collectionId, 0);
        final Criteria markedForDeleteCriteria = new Criteria(Column.getColumn("RecentProfileForGroup", "MARKED_FOR_DELETE"), (Object)Boolean.FALSE, 0);
        selectQuery.setCriteria(collectionCriteria.and(markedForDeleteCriteria));
        selectQuery.addSelectColumn(Column.getColumn("RecentProfileForGroup", "GROUP_ID"));
        selectQuery.addSelectColumn(Column.getColumn("RecentProfileForGroup", "PROFILE_ID"));
        final DataObject dataObject = DataAccess.get(selectQuery);
        if (!dataObject.isEmpty()) {
            final Iterator<Row> iterator = dataObject.getRows("RecentProfileForGroup");
            associatedGroupIds = DBUtil.getColumnValuesAsList((Iterator)iterator, "GROUP_ID");
        }
        return associatedGroupIds;
    }
    
    public Boolean checkIfGivenPolicyIsStoreAppPolicy(final Long policyId) throws DataAccessException {
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("Profile"));
        final Criteria policyCriteria = new Criteria(Column.getColumn("Profile", "PROFILE_ID"), (Object)policyId, 0);
        final Criteria storeAppNameCriteria = new Criteria(Column.getColumn("Profile", "PROFILE_NAME"), (Object)"Store Apps - update policy", 0, false);
        selectQuery.addSelectColumn(new Column("Profile", "PROFILE_ID"));
        selectQuery.setCriteria(policyCriteria.and(storeAppNameCriteria));
        final DataObject dataObject = DataAccess.get(selectQuery);
        return !dataObject.isEmpty();
    }
    
    public List<Long> getExistingStoreAppPolicies(final Long customerId) throws DataAccessException {
        List<Long> storeAppPolicyIds = new ArrayList<Long>();
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("AutoAppUpdateConfigDetails"));
        selectQuery.addJoin(new Join("AutoAppUpdateConfigDetails", "AutoAppUpdateConfigToCollection", new String[] { "APP_UPDATE_CONF_ID" }, new String[] { "APP_UPDATE_CONF_ID" }, 1));
        final Criteria customerCriteria = new Criteria(Column.getColumn("AutoAppUpdateConfigDetails", "CUSTOMER_ID"), (Object)customerId, 0);
        final Criteria storeAppCriteria = new Criteria(Column.getColumn("AutoAppUpdateConfigToCollection", "COLLECTION_ID"), (Object)null, 0);
        selectQuery.setCriteria(customerCriteria.and(storeAppCriteria));
        selectQuery.addSelectColumn(new Column("AutoAppUpdateConfigDetails", "APP_UPDATE_CONF_ID"));
        final DataObject dataObject = DataAccess.get(selectQuery);
        if (!dataObject.isEmpty()) {
            final Iterator<Row> iterator = dataObject.getRows("AutoAppUpdateConfigDetails");
            storeAppPolicyIds = DBUtil.getColumnValuesAsList((Iterator)iterator, "APP_UPDATE_CONF_ID");
        }
        return storeAppPolicyIds;
    }
    
    public SelectQuery getPackageToAppUpdateConfigQuery(final Long autoUpdatePolicyId) {
        final SelectQuery appScheduledUpdateQuery = (SelectQuery)new SelectQueryImpl(new Table("AutoAppUpdateConfigDetails"));
        appScheduledUpdateQuery.addJoin(new Join("AutoAppUpdateConfigDetails", "AutoAppUpdateConfigToCollection", new String[] { "APP_UPDATE_CONF_ID" }, new String[] { "APP_UPDATE_CONF_ID" }, 2));
        appScheduledUpdateQuery.addJoin(new Join("AutoAppUpdateConfigToCollection", "MdmDeploymentTemplate", new String[] { "DEPLOYMENT_TEMPLATE_ID" }, new String[] { "DEPLOYMENT_TEMPLATE_ID" }, 2));
        appScheduledUpdateQuery.addJoin(new Join("MdmDeploymentTemplate", "AppUpdateDeploymentPolicy", new String[] { "DEPLOYMENT_TEMPLATE_ID" }, new String[] { "DEPLOYMENT_TEMPLATE_ID" }, 2));
        appScheduledUpdateQuery.addJoin(new Join("AutoAppUpdateConfigDetails", "AutoAppUpdatePackageConfig", new String[] { "APP_UPDATE_CONF_ID" }, new String[] { "APP_UPDATE_CONF_ID" }, 2));
        appScheduledUpdateQuery.addJoin(new Join("AutoAppUpdatePackageConfig", "AutoAppUpdatePackageList", new String[] { "APP_UPDATE_CONF_ID" }, new String[] { "APP_UPDATE_CONF_ID" }, 1));
        appScheduledUpdateQuery.addJoin(new Join("AutoAppUpdatePackageList", "MdPackageToAppGroup", new String[] { "PACKAGE_ID" }, new String[] { "PACKAGE_ID" }, 1));
        appScheduledUpdateQuery.setCriteria(new Criteria(new Column("AutoAppUpdateConfigDetails", "APP_UPDATE_CONF_ID"), (Object)autoUpdatePolicyId, 0));
        appScheduledUpdateQuery.addSelectColumn(new Column("AppUpdateDeploymentPolicy", "*"));
        appScheduledUpdateQuery.addSelectColumn(new Column("AutoAppUpdatePackageConfig", "*"));
        appScheduledUpdateQuery.addSelectColumn(new Column("AutoAppUpdatePackageList", "*"));
        appScheduledUpdateQuery.addSelectColumn(new Column("MdPackageToAppGroup", "APP_GROUP_ID"));
        appScheduledUpdateQuery.addSelectColumn(new Column("MdPackageToAppGroup", "PACKAGE_ID"));
        return appScheduledUpdateQuery;
    }
    
    static {
        AppUpdatePolicyDBHandler.appUpdatePolicyDBHandler = null;
    }
}
