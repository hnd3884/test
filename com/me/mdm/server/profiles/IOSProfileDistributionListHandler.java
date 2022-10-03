package com.me.mdm.server.profiles;

import java.util.Hashtable;
import com.me.mdm.server.apps.businessstore.MDBusinessStoreUtil;
import com.adventnet.i18n.I18N;
import com.adventnet.sym.server.mdm.apps.vpp.VPPAppMgmtHandler;
import com.adventnet.sym.server.mdm.config.ProfileAssociateHandler;
import java.util.Collection;
import java.util.Arrays;
import com.adventnet.sym.server.mdm.apps.ManagedAppDataHandler;
import com.adventnet.sym.server.mdm.apps.AppsUtil;
import com.me.mdm.server.seqcommands.ios.IOSSeqCmdUtil;
import com.me.mdm.server.config.MDMConfigUtil;
import com.me.mdm.server.profiles.kiosk.IOSKioskProfileDataHandler;
import com.me.devicemanagement.framework.server.tree.TreeNode;
import java.sql.Connection;
import com.adventnet.db.api.RelationalAPI;
import com.adventnet.ds.query.Query;
import com.adventnet.ds.query.DerivedTable;
import com.adventnet.sym.server.mdm.core.ManagedDeviceHandler;
import com.me.devicemanagement.framework.server.sql.SQLProvider;
import java.util.Properties;
import com.adventnet.ds.query.DMDataSetWrapper;
import com.adventnet.ds.query.GroupByClause;
import java.util.ArrayList;
import com.adventnet.ds.query.QueryConstructionException;
import java.sql.SQLException;
import java.util.Iterator;
import com.adventnet.persistence.DataObject;
import com.adventnet.ds.query.SelectQuery;
import java.util.logging.Level;
import org.json.JSONObject;
import com.adventnet.persistence.Row;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import java.util.HashMap;
import java.util.List;

public class IOSProfileDistributionListHandler extends ProfileDistributionListHandler
{
    public IOSProfileDistributionListHandler() {
        this.platformType = 1;
    }
    
    @Override
    public HashMap getLicensesAssociatedToGroupsMap(final List groupResourceIds, final long customerID, final List businessStoreIDList) throws Exception {
        final HashMap appLicensesUsedByGroupMembers = new HashMap();
        if (groupResourceIds != null) {
            this.getManagedDistributionLicensesAssociatedToGroupMembers(groupResourceIds, customerID, appLicensesUsedByGroupMembers, businessStoreIDList);
        }
        return appLicensesUsedByGroupMembers;
    }
    
    @Override
    public HashMap getLicensesAssociatedToResourcesMap(final List resourceIds, final long customerID, final List businessStoreIDList) throws Exception {
        final HashMap appLicensesUsedByDevices = new HashMap();
        if (resourceIds != null) {
            this.getManagedDistributionLicensesAssociatedToDevices(resourceIds, customerID, appLicensesUsedByDevices, businessStoreIDList);
        }
        return appLicensesUsedByDevices;
    }
    
    @Override
    public HashMap getRemainingLicenseCountMap(final Long customerId, final List businessStoreIDList) throws Exception {
        final HashMap appLicenseCountUnused = new HashMap();
        this.getRedemptionCodeUnusedLicenseCountMap(customerId, appLicenseCountUnused);
        this.getManagedDistributionUnusedLicenseCountMap(customerId, businessStoreIDList, appLicenseCountUnused, null);
        return appLicenseCountUnused;
    }
    
    public HashMap getManagedDistributionUnusedLicenseCountMap(final Long customerId, final List businessStoreIDList, final HashMap appLicenseCountUnused, final Long appGroupID) throws SQLException, QueryConstructionException {
        final SelectQuery licenseStatsForGroup = (SelectQuery)new SelectQueryImpl(Table.getTable("MdAppGroupDetails"));
        licenseStatsForGroup.addJoin(new Join("MdAppGroupDetails", "MdStoreAssetToAppGroupRel", new String[] { "APP_GROUP_ID" }, new String[] { "APP_GROUP_ID" }, 2));
        licenseStatsForGroup.addJoin(new Join("MdStoreAssetToAppGroupRel", "MdVppAsset", new String[] { "STORE_ASSET_ID" }, new String[] { "VPP_ASSET_ID" }, 2));
        licenseStatsForGroup.addJoin(new Join("MdVppAsset", "MdVPPTokenDetails", new String[] { "TOKEN_ID" }, new String[] { "TOKEN_ID" }, 2));
        licenseStatsForGroup.addJoin(new Join("MdVPPTokenDetails", "MdBusinessStoreToVppRel", new String[] { "TOKEN_ID" }, new String[] { "TOKEN_ID" }, 2));
        licenseStatsForGroup.addJoin(new Join("MdBusinessStoreToVppRel", "ManagedBusinessStore", new String[] { "BUSINESSSTORE_ID" }, new String[] { "BUSINESSSTORE_ID" }, 2));
        final Criteria businessToResourceJoin = new Criteria(Column.getColumn("ManagedBusinessStore", "BUSINESSSTORE_ID"), (Object)Column.getColumn("Resource", "RESOURCE_ID"), 0);
        businessToResourceJoin.and(new Criteria(Column.getColumn("MdAppGroupDetails", "CUSTOMER_ID"), (Object)Column.getColumn("Resource", "CUSTOMER_ID"), 0));
        licenseStatsForGroup.addJoin(new Join("ManagedBusinessStore", "Resource", businessToResourceJoin, 2));
        Criteria customerCriteria = new Criteria(new Column("MdAppGroupDetails", "CUSTOMER_ID"), (Object)customerId, 0);
        if (businessStoreIDList != null && !businessStoreIDList.isEmpty()) {
            customerCriteria = customerCriteria.and(new Criteria(new Column("MdBusinessStoreToVppRel", "BUSINESSSTORE_ID"), (Object)businessStoreIDList.toArray(), 8));
        }
        if (appGroupID != null) {
            customerCriteria = customerCriteria.and(new Criteria(new Column("MdAppGroupDetails", "APP_GROUP_ID"), (Object)appGroupID, 0));
        }
        licenseStatsForGroup.setCriteria(customerCriteria);
        licenseStatsForGroup.addSelectColumn(Column.getColumn("MdStoreAssetToAppGroupRel", "*"));
        licenseStatsForGroup.addSelectColumn(Column.getColumn("MdVppAsset", "VPP_ASSET_ID"));
        licenseStatsForGroup.addSelectColumn(Column.getColumn("MdVppAsset", "AVAILABLE_LICENSE_COUNT"));
        licenseStatsForGroup.addSelectColumn(Column.getColumn("MdVppAsset", "TOTAL_LICENSE"));
        licenseStatsForGroup.addSelectColumn(Column.getColumn("MdVppAsset", "ASSIGNED_LICENSE_COUNT"));
        DataObject dataObject = null;
        try {
            dataObject = MDMUtil.getPersistence().get(licenseStatsForGroup);
            final Iterator iterator = dataObject.getRows("MdStoreAssetToAppGroupRel");
            while (iterator.hasNext()) {
                final Row assetToAppRow = iterator.next();
                final Long appGroupIDthis = (Long)assetToAppRow.get("APP_GROUP_ID");
                final Long assetID = (Long)assetToAppRow.get("STORE_ASSET_ID");
                final Row assetRow = dataObject.getRow("MdVppAsset", new Criteria(Column.getColumn("MdVppAsset", "VPP_ASSET_ID"), (Object)assetID, 0));
                int availableCount = (int)assetRow.get("AVAILABLE_LICENSE_COUNT");
                int totalCount = (int)assetRow.get("TOTAL_LICENSE");
                int usedCount = (int)assetRow.get("ASSIGNED_LICENSE_COUNT");
                if (appLicenseCountUnused.containsKey(appGroupIDthis)) {
                    final JSONObject licenseSummary = appLicenseCountUnused.get(appGroupIDthis);
                    availableCount += licenseSummary.optInt("AVAILABLE_LICENSE_COUNT");
                    totalCount += licenseSummary.optInt("TOTAL_LICENSE");
                    usedCount += licenseSummary.optInt("ASSIGNED_LICENSE_COUNT");
                }
                final JSONObject licenseSummary = new JSONObject();
                licenseSummary.put("AVAILABLE_LICENSE_COUNT", availableCount);
                licenseSummary.put("ASSIGNED_LICENSE_COUNT", usedCount);
                licenseSummary.put("TOTAL_LICENSE", totalCount);
                appLicenseCountUnused.put(appGroupIDthis, licenseSummary);
            }
        }
        catch (final Exception exp) {
            this.logger.log(Level.SEVERE, "Exception while processing the dataset ", exp);
        }
        return appLicenseCountUnused;
    }
    
    public void getRedemptionCodeUnusedLicenseCountMap(final long customerId, final HashMap appLicenseCountUnused) throws SQLException, QueryConstructionException {
        final SelectQuery licenseStats = (SelectQuery)new SelectQueryImpl(Table.getTable("MdLicenseCodes"));
        licenseStats.addJoin(new Join("MdLicenseCodes", "MdLicenseDetails", new String[] { "LICENSE_DETAILS_ID" }, new String[] { "LICENSE_DETAILS_ID" }, 2));
        licenseStats.addJoin(new Join("MdLicenseDetails", "MdLicense", new String[] { "LICENSE_ID" }, new String[] { "LICENSE_ID" }, 2));
        final Criteria isCodeAssignedByDcCriteria = new Criteria(Column.getColumn("MdLicenseCodes", "IS_CODE_ASSIGNED_BY_DC"), (Object)false, 0);
        final Criteria isRedeemedInAppStoreCriteria = new Criteria(Column.getColumn("MdLicenseCodes", "IS_REEDEEMED_APPSTORE_CODE"), (Object)false, 0);
        final Criteria licenseTypeCri = new Criteria(Column.getColumn("MdLicense", "LICENSED_TYPE"), (Object)1, 0);
        final Criteria customerCriteria = new Criteria(Column.getColumn("MdLicense", "CUSTOMER_ID"), (Object)customerId, 0);
        final Criteria criteria = isCodeAssignedByDcCriteria.and(isRedeemedInAppStoreCriteria).and(licenseTypeCri).and(customerCriteria);
        licenseStats.setCriteria(criteria);
        licenseStats.addSelectColumn(Column.getColumn("MdLicenseCodes", "APP_GROUP_ID"));
        final Column countColumn = new Column("MdLicenseCodes", "APP_LICENSE_CODE").count();
        countColumn.setColumnAlias("APP_COUNT");
        licenseStats.addSelectColumn(countColumn);
        final List groupByColumns = new ArrayList();
        groupByColumns.add(new Column("MdLicenseCodes", "APP_GROUP_ID"));
        final GroupByClause grouping = new GroupByClause(groupByColumns);
        licenseStats.setGroupByClause(grouping);
        DMDataSetWrapper dataSet = null;
        try {
            dataSet = DMDataSetWrapper.executeQuery((Object)licenseStats);
            while (dataSet.next()) {
                final long appGroupId = (long)dataSet.getValue("APP_GROUP_ID");
                final int count = (int)dataSet.getValue("APP_COUNT");
                final JSONObject licenseSummaryJSON = new JSONObject();
                licenseSummaryJSON.put("TOTAL_LICENSE", 0);
                licenseSummaryJSON.put("ASSIGNED_LICENSE_COUNT", 0);
                licenseSummaryJSON.put("AVAILABLE_LICENSE_COUNT", count);
                appLicenseCountUnused.put(appGroupId, licenseSummaryJSON);
            }
        }
        catch (final Exception exp) {
            this.logger.log(Level.SEVERE, "Exception while processing the dataset ", exp);
        }
    }
    
    private String getQueryToComputeAppsCount(final List resourceIds, final int sqlQueryIdentifiers, final String placeholder) {
        String query = "";
        try {
            final Properties variableProps = new Properties();
            ((Hashtable<String, String>)variableProps).put(placeholder, resourceIds.toString().replace("[", "(").replace("]", ")"));
            ((Hashtable<String, String>)variableProps).put("%APP_COUNT_STRING%", "\"APP_COUNT\"");
            query = SQLProvider.getInstance().getSQLString(sqlQueryIdentifiers, variableProps);
        }
        catch (final Exception ex) {
            this.logger.log(Level.WARNING, "Exception in getQueryToComputeAppsCount", ex);
        }
        return query;
    }
    
    private Criteria getIos9AndAboveDevicesCriteria() {
        final Criteria osVersion4Criteria = new Criteria(Column.getColumn("MdDeviceInfo", "OS_VERSION"), (Object)"4.*", 3);
        final Criteria osVersion5Criteria = new Criteria(Column.getColumn("MdDeviceInfo", "OS_VERSION"), (Object)"5.*", 3);
        final Criteria osVersion6Criteria = new Criteria(Column.getColumn("MdDeviceInfo", "OS_VERSION"), (Object)"6.*", 3);
        final Criteria osVersion7Criteria = new Criteria(Column.getColumn("MdDeviceInfo", "OS_VERSION"), (Object)"7.*", 3);
        final Criteria osVersion8Criteria = new Criteria(Column.getColumn("MdDeviceInfo", "OS_VERSION"), (Object)"8.*", 3);
        final Criteria osVersionCriteria = osVersion4Criteria.and(osVersion5Criteria).and(osVersion6Criteria).and(osVersion7Criteria).and(osVersion8Criteria);
        return osVersionCriteria;
    }
    
    private Criteria getIos8AndBelowDevicesCriteria() {
        final Criteria osVersion4Criteria = new Criteria(Column.getColumn("MdDeviceInfo", "OS_VERSION"), (Object)"4.*", 2);
        final Criteria osVersion5Criteria = new Criteria(Column.getColumn("MdDeviceInfo", "OS_VERSION"), (Object)"5.*", 2);
        final Criteria osVersion6Criteria = new Criteria(Column.getColumn("MdDeviceInfo", "OS_VERSION"), (Object)"6.*", 2);
        final Criteria osVersion7Criteria = new Criteria(Column.getColumn("MdDeviceInfo", "OS_VERSION"), (Object)"7.*", 2);
        final Criteria osVersion8Criteria = new Criteria(Column.getColumn("MdDeviceInfo", "OS_VERSION"), (Object)"8.*", 2);
        final Criteria osVersionCriteria = osVersion4Criteria.or(osVersion5Criteria).or(osVersion6Criteria).or(osVersion7Criteria).or(osVersion8Criteria);
        return osVersionCriteria;
    }
    
    @Override
    public int getEnrolledDeviceCount(final List resourceList) {
        int ios9AndAboveDevicesForResources = 0;
        int ios8AndBelowUsersForResources = 0;
        int totalCount = 0;
        try {
            ios9AndAboveDevicesForResources = ManagedDeviceHandler.getInstance().getManagedDeviceCountForResources(resourceList, this.getIos9AndAboveDevicesCriteria());
            ios8AndBelowUsersForResources = ManagedDeviceHandler.getInstance().getManagedUserCountForResources(resourceList, this.getIos8AndBelowDevicesCriteria());
            totalCount = ios9AndAboveDevicesForResources + ios8AndBelowUsersForResources;
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, "Exception in getEnrolledDeviceCount in ios ", ex);
        }
        return totalCount;
    }
    
    @Override
    protected int getEnrolledDeviceCountInGroups(final List groupResourceIds) {
        int ios9AndAboveDevicesInGroups = 0;
        int ios8AndBelowUsersInGroups = 0;
        int totalCount = 0;
        try {
            ios9AndAboveDevicesInGroups = ManagedDeviceHandler.getInstance().getManagedDeviceCountInGroups(groupResourceIds, this.getIos9AndAboveDevicesCriteria(), 1);
            ios8AndBelowUsersInGroups = ManagedDeviceHandler.getInstance().getManagedUserCountInGroups(groupResourceIds, this.getIos8AndBelowDevicesCriteria(), 1);
            totalCount = ios9AndAboveDevicesInGroups + ios8AndBelowUsersInGroups;
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, "Exception in getEnrolledDeviceCountInGroups in ios ", ex);
        }
        return totalCount;
    }
    
    private void getManagedDistributionLicensesAssociatedToGroupMembers(final List groupResourceIds, final long customerId, final HashMap appLicensesUsedByGroupMembers, final List businessStoreIDList) throws Exception {
        this.getLicenseCountForVppUserBasedApp(groupResourceIds, appLicensesUsedByGroupMembers, businessStoreIDList);
        this.getLicenseCountForVppDeviceBasedApp(groupResourceIds, appLicensesUsedByGroupMembers, businessStoreIDList);
        this.logger.log(Level.INFO, "Query execution completed without exceptions!");
    }
    
    private void getLicenseCountForVppUserBasedApp(final List groupResourceIds, final HashMap appLicensesUsedByGroupMembers, final List businessStoreIDlist) throws SQLException {
        DMDataSetWrapper dataSetForUserAssignment = null;
        try {
            final SelectQuery subSQ = (SelectQuery)new SelectQueryImpl(Table.getTable("ManagedDevice"));
            subSQ.addJoin(new Join("ManagedDevice", "ManagedUserToDevice", new String[] { "RESOURCE_ID" }, new String[] { "MANAGED_DEVICE_ID" }, 2));
            subSQ.addJoin(new Join("ManagedUserToDevice", "CustomGroupMemberRel", new String[] { "MANAGED_DEVICE_ID" }, new String[] { "MEMBER_RESOURCE_ID" }, 2));
            final Criteria appGroupIdCri = new Criteria(Column.getColumn("CustomGroupMemberRel", "GROUP_RESOURCE_ID"), (Object)groupResourceIds.toArray(), 8);
            final Criteria platformCriteria = new Criteria(Column.getColumn("ManagedDevice", "PLATFORM_TYPE"), (Object)1, 0);
            subSQ.setCriteria(appGroupIdCri.and(platformCriteria));
            final Column managedUserToDeviceCol = Column.getColumn("ManagedUserToDevice", "MANAGED_USER_ID").distinct();
            managedUserToDeviceCol.setColumnAlias("manageduserid");
            subSQ.addSelectColumn(managedUserToDeviceCol);
            final DerivedTable groupDerievedTable = new DerivedTable("CustomGroupTableAliasName", (Query)subSQ);
            final Table mdVPPUserToManagedUserTable = Table.getTable("MdManagedUserToVppUserRel");
            final SelectQuery baseSelectQuery = (SelectQuery)new SelectQueryImpl(mdVPPUserToManagedUserTable);
            baseSelectQuery.addJoin(new Join(mdVPPUserToManagedUserTable, (Table)groupDerievedTable, new String[] { "MANAGED_USER_ID" }, new String[] { "manageduserid" }, 2));
            baseSelectQuery.addJoin(new Join("MdManagedUserToVppUserRel", "MdVppAssetToVppUserRel", new String[] { "VPP_USER_ID" }, new String[] { "VPP_USER_ID" }, 2));
            baseSelectQuery.addJoin(new Join("MdVppAssetToVppUserRel", "MdVppAsset", new String[] { "VPP_ASSET_ID" }, new String[] { "VPP_ASSET_ID" }, 2));
            baseSelectQuery.addJoin(new Join("MdVppAsset", "MdVPPTokenDetails", new String[] { "TOKEN_ID" }, new String[] { "TOKEN_ID" }, 2));
            baseSelectQuery.addJoin(new Join("MdVppAsset", "MdStoreAssetToAppGroupRel", new String[] { "VPP_ASSET_ID" }, new String[] { "STORE_ASSET_ID" }, 2));
            baseSelectQuery.addJoin(new Join("MdStoreAssetToAppGroupRel", "MdBusinessStoreToAssetRel", new String[] { "STORE_ASSET_ID" }, new String[] { "STORE_ASSET_ID" }, 2));
            Criteria licenseTypeCri = new Criteria(Column.getColumn("MdVPPTokenDetails", "LICENSE_ASSIGN_TYPE"), (Object)1, 0);
            if (businessStoreIDlist != null && !businessStoreIDlist.isEmpty()) {
                final Criteria businessStoreCriteria = new Criteria(Column.getColumn("MdBusinessStoreToAssetRel", "BUSINESSSTORE_ID"), (Object)businessStoreIDlist.toArray(), 8);
                licenseTypeCri = licenseTypeCri.and(businessStoreCriteria);
            }
            baseSelectQuery.setCriteria(licenseTypeCri);
            final Column managedUserToDeviceCount = Column.getColumn("MdVppAssetToVppUserRel", "VPP_USER_ID").count();
            managedUserToDeviceCount.setColumnAlias("APP_COUNT");
            baseSelectQuery.addSelectColumn(managedUserToDeviceCount);
            baseSelectQuery.addSelectColumn(Column.getColumn("MdStoreAssetToAppGroupRel", "APP_GROUP_ID"));
            final List list = new ArrayList();
            final Column groupByCol = Column.getColumn("MdStoreAssetToAppGroupRel", "APP_GROUP_ID");
            list.add(groupByCol);
            final GroupByClause appGroupBy = new GroupByClause(list);
            baseSelectQuery.setGroupByClause(appGroupBy);
            final RelationalAPI relapi = RelationalAPI.getInstance();
            final String sql = relapi.getSelectSQL((Query)baseSelectQuery);
            this.logger.log(Level.SEVERE, "SQL for  getLicenseCountForVppUserBasedApp {0}", sql);
            dataSetForUserAssignment = DMDataSetWrapper.executeQuery((Object)baseSelectQuery);
            if (dataSetForUserAssignment != null) {
                while (dataSetForUserAssignment.next()) {
                    final long appGroupId = (long)dataSetForUserAssignment.getValue("APP_GROUP_ID");
                    int count = (int)dataSetForUserAssignment.getValue("APP_COUNT");
                    final Integer prevCount = appLicensesUsedByGroupMembers.get(appGroupId);
                    if (prevCount != null) {
                        count += prevCount;
                    }
                    appLicensesUsedByGroupMembers.put(appGroupId, count);
                }
            }
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, "Exception in getLicenseCountForVppUserBasedApp {0}", ex);
        }
    }
    
    private void getLicenseCountForVppDeviceBasedApp(final List groupResourceIds, final HashMap appLicensesUsedByGroupMembers, final List businessStoreIDlist) throws SQLException {
        DMDataSetWrapper dataSetForDeviceAssignment = null;
        try {
            final SelectQuery subSQ = (SelectQuery)new SelectQueryImpl(Table.getTable("CustomGroupMemberRel"));
            final Criteria appGroupIdCri = new Criteria(Column.getColumn("CustomGroupMemberRel", "GROUP_RESOURCE_ID"), (Object)groupResourceIds.toArray(), 8);
            subSQ.setCriteria(appGroupIdCri);
            final Column customGroupMemResId = Column.getColumn("CustomGroupMemberRel", "MEMBER_RESOURCE_ID").distinct();
            customGroupMemResId.setColumnAlias("memberresourceid");
            subSQ.addSelectColumn(customGroupMemResId);
            final DerivedTable groupDerievedTable = new DerivedTable("CustomGroupTableAliasName", (Query)subSQ);
            final Table mdVPPLicenseToDeviceTable = Table.getTable("MdVppAssetToManagedDeviceRel");
            final SelectQuery baseSelectQuery = (SelectQuery)new SelectQueryImpl(mdVPPLicenseToDeviceTable);
            baseSelectQuery.addJoin(new Join(mdVPPLicenseToDeviceTable, (Table)groupDerievedTable, new String[] { "MANAGED_DEVICE_ID" }, new String[] { "memberresourceid" }, 2));
            baseSelectQuery.addJoin(new Join("MdVppAssetToManagedDeviceRel", "MdStoreAssetToAppGroupRel", new String[] { "VPP_ASSET_ID" }, new String[] { "STORE_ASSET_ID" }, 2));
            baseSelectQuery.addJoin(new Join("MdStoreAssetToAppGroupRel", "MdVppAsset", new String[] { "STORE_ASSET_ID" }, new String[] { "VPP_ASSET_ID" }, 2));
            baseSelectQuery.addJoin(new Join("MdVppAsset", "MdVPPTokenDetails", new String[] { "TOKEN_ID" }, new String[] { "TOKEN_ID" }, 2));
            baseSelectQuery.addJoin(new Join("MdStoreAssetToAppGroupRel", "MdBusinessStoreToAssetRel", new String[] { "STORE_ASSET_ID" }, new String[] { "STORE_ASSET_ID" }, 2));
            final Column managedDeviceCount = Column.getColumn("MdVppAssetToManagedDeviceRel", "MANAGED_DEVICE_ID").count();
            managedDeviceCount.setColumnAlias("APP_COUNT");
            baseSelectQuery.addSelectColumn(managedDeviceCount);
            baseSelectQuery.addSelectColumn(Column.getColumn("MdStoreAssetToAppGroupRel", "APP_GROUP_ID"));
            Criteria licenseTypeCri = new Criteria(Column.getColumn("MdVPPTokenDetails", "LICENSE_ASSIGN_TYPE"), (Object)2, 0);
            if (businessStoreIDlist != null && !businessStoreIDlist.isEmpty()) {
                final Criteria businessStoreCriteria = new Criteria(Column.getColumn("MdBusinessStoreToAssetRel", "BUSINESSSTORE_ID"), (Object)businessStoreIDlist.toArray(), 8);
                licenseTypeCri = licenseTypeCri.and(businessStoreCriteria);
            }
            baseSelectQuery.setCriteria(licenseTypeCri);
            final List list = new ArrayList();
            final Column groupByCol = Column.getColumn("MdStoreAssetToAppGroupRel", "APP_GROUP_ID");
            list.add(groupByCol);
            final GroupByClause appGroupBy = new GroupByClause(list);
            baseSelectQuery.setGroupByClause(appGroupBy);
            dataSetForDeviceAssignment = DMDataSetWrapper.executeQuery((Object)baseSelectQuery);
            if (dataSetForDeviceAssignment != null) {
                while (dataSetForDeviceAssignment.next()) {
                    final long appGroupId = (long)dataSetForDeviceAssignment.getValue("APP_GROUP_ID");
                    int count = (int)dataSetForDeviceAssignment.getValue("APP_COUNT");
                    final Integer prevCount = appLicensesUsedByGroupMembers.get(appGroupId);
                    if (prevCount != null) {
                        count += prevCount;
                    }
                    appLicensesUsedByGroupMembers.put(appGroupId, count);
                }
            }
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, "Exception in  {0}", ex);
        }
    }
    
    private void getRedemptionCodeLicensesAssociatedToGroupMembers(final List groupResourceIds, final long customerId, final HashMap appLicensesUsedByGroupMembers) throws Exception {
        this.getLicenseCountForVppRedemtionCodeApp(groupResourceIds, appLicensesUsedByGroupMembers);
        this.logger.log(Level.INFO, "Query execution completed without exceptions!");
    }
    
    private void getLicenseCountForVppRedemtionCodeApp(final List groupResourceIds, final HashMap appLicensesUsedByGroupMembers) throws SQLException {
        DMDataSetWrapper dataSetForVppRedeemptionCode = null;
        final Connection connection = null;
        try {
            final SelectQuery subSQ = (SelectQuery)new SelectQueryImpl(Table.getTable("CustomGroupMemberRel"));
            final Criteria appGroupIdCri = new Criteria(Column.getColumn("CustomGroupMemberRel", "GROUP_RESOURCE_ID"), (Object)groupResourceIds.toArray(), 8);
            subSQ.setCriteria(appGroupIdCri);
            final Column customGroupMemResId = Column.getColumn("CustomGroupMemberRel", "MEMBER_RESOURCE_ID").distinct();
            customGroupMemResId.setColumnAlias("memberresourceid");
            subSQ.addSelectColumn(customGroupMemResId);
            final DerivedTable groupDerievedTable = new DerivedTable("CustomGroupTableAliasName", (Query)subSQ);
            final Table mdAppLicenseToResources = Table.getTable("MdAppLicenseToResources");
            final SelectQuery baseSelectQuery = (SelectQuery)new SelectQueryImpl(mdAppLicenseToResources);
            baseSelectQuery.addJoin(new Join(mdAppLicenseToResources, (Table)groupDerievedTable, new String[] { "RESOURCE_ID" }, new String[] { "memberresourceid" }, 2));
            baseSelectQuery.addJoin(new Join("MdAppLicenseToResources", "MdLicenseToAppGroupRel", new String[] { "APP_GROUP_ID" }, new String[] { "APP_GROUP_ID" }, 2));
            baseSelectQuery.addJoin(new Join("MdLicenseToAppGroupRel", "MdLicense", new String[] { "LICENSE_ID" }, new String[] { "LICENSE_ID" }, 2));
            final Criteria licenseTypeCri = new Criteria(Column.getColumn("MdLicense", "LICENSED_TYPE"), (Object)1, 0);
            baseSelectQuery.setCriteria(licenseTypeCri);
            final Column managedUserToDeviceCount = Column.getColumn("MdAppLicenseToResources", "RESOURCE_ID").count();
            managedUserToDeviceCount.setColumnAlias("APP_COUNT");
            baseSelectQuery.addSelectColumn(managedUserToDeviceCount);
            baseSelectQuery.addSelectColumn(Column.getColumn("MdAppLicenseToResources", "APP_GROUP_ID"));
            final List list = new ArrayList();
            final Column groupByCol = Column.getColumn("MdAppLicenseToResources", "APP_GROUP_ID");
            list.add(groupByCol);
            final GroupByClause appGroupBy = new GroupByClause(list);
            baseSelectQuery.setGroupByClause(appGroupBy);
            dataSetForVppRedeemptionCode = DMDataSetWrapper.executeQuery((Object)baseSelectQuery);
            if (dataSetForVppRedeemptionCode != null) {
                while (dataSetForVppRedeemptionCode.next()) {
                    final long appGroupId = (long)dataSetForVppRedeemptionCode.getValue("APP_GROUP_ID");
                    final int count = (int)dataSetForVppRedeemptionCode.getValue("APP_COUNT");
                    appLicensesUsedByGroupMembers.put(appGroupId, count);
                }
            }
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, "Exception in   {0}", ex);
        }
    }
    
    private void getManagedDistributionLicensesAssociatedToDevices(final List resourceList, final long customerId, final HashMap appLicensesUsedByDevices, final List businessStoreIDList) throws Exception {
        this.getLicenseCountToDevicesForVppUserBasedApp(resourceList, appLicensesUsedByDevices, businessStoreIDList);
        this.getLicenseCountToDevicesForVppDeviceBasedApp(resourceList, appLicensesUsedByDevices, businessStoreIDList);
        this.logger.log(Level.INFO, "Query execution completed without exceptions!");
    }
    
    private void getLicenseCountToDevicesForVppUserBasedApp(final List resourceList, final HashMap appLicensesUsedByDevices, final List businessStoreIDList) throws SQLException {
        DMDataSetWrapper dataSetForUserAssignment = null;
        try {
            final Table mdVPPUserToManagedUserTable = Table.getTable("ManagedUserToDevice");
            final SelectQuery baseSelectQuery = (SelectQuery)new SelectQueryImpl(mdVPPUserToManagedUserTable);
            baseSelectQuery.addJoin(new Join("ManagedUserToDevice", "MdManagedUserToVppUserRel", new String[] { "MANAGED_USER_ID" }, new String[] { "MANAGED_USER_ID" }, 2));
            baseSelectQuery.addJoin(new Join("MdManagedUserToVppUserRel", "MdVppAssetToVppUserRel", new String[] { "VPP_USER_ID" }, new String[] { "VPP_USER_ID" }, 2));
            baseSelectQuery.addJoin(new Join("MdVppAssetToVppUserRel", "MdVppAsset", new String[] { "VPP_ASSET_ID" }, new String[] { "VPP_ASSET_ID" }, 2));
            baseSelectQuery.addJoin(new Join("MdVppAsset", "MdStoreAssetToAppGroupRel", new String[] { "VPP_ASSET_ID" }, new String[] { "STORE_ASSET_ID" }, 2));
            baseSelectQuery.addJoin(new Join("MdVppAsset", "MdVPPTokenDetails", new String[] { "TOKEN_ID" }, new String[] { "TOKEN_ID" }, 2));
            baseSelectQuery.addJoin(new Join("MdStoreAssetToAppGroupRel", "MdBusinessStoreToAssetRel", new String[] { "STORE_ASSET_ID" }, new String[] { "STORE_ASSET_ID" }, 2));
            Criteria criteria = new Criteria(Column.getColumn("ManagedUserToDevice", "MANAGED_DEVICE_ID"), (Object)resourceList.toArray(), 8);
            final Column managedUserToDeviceCount = Column.getColumn("MdVppAssetToVppUserRel", "VPP_USER_ID").count();
            managedUserToDeviceCount.setColumnAlias("APP_COUNT");
            baseSelectQuery.addSelectColumn(managedUserToDeviceCount);
            baseSelectQuery.addSelectColumn(Column.getColumn("MdStoreAssetToAppGroupRel", "APP_GROUP_ID"));
            criteria = criteria.and(new Criteria(Column.getColumn("MdVPPTokenDetails", "LICENSE_ASSIGN_TYPE"), (Object)1, 0));
            if (businessStoreIDList != null && !businessStoreIDList.isEmpty()) {
                final Criteria businessStoreCriteria = new Criteria(Column.getColumn("MdBusinessStoreToAssetRel", "BUSINESSSTORE_ID"), (Object)businessStoreIDList.toArray(), 8);
                criteria = criteria.and(businessStoreCriteria);
            }
            baseSelectQuery.setCriteria(criteria);
            final List list = new ArrayList();
            final Column groupByCol = Column.getColumn("MdStoreAssetToAppGroupRel", "APP_GROUP_ID");
            list.add(groupByCol);
            final GroupByClause appGroupBy = new GroupByClause(list);
            baseSelectQuery.setGroupByClause(appGroupBy);
            dataSetForUserAssignment = DMDataSetWrapper.executeQuery((Object)baseSelectQuery);
            if (dataSetForUserAssignment != null) {
                while (dataSetForUserAssignment.next()) {
                    final long appGroupId = (long)dataSetForUserAssignment.getValue("APP_GROUP_ID");
                    int count = (int)dataSetForUserAssignment.getValue("APP_COUNT");
                    final Integer prevCount = appLicensesUsedByDevices.get(appGroupId);
                    if (prevCount != null) {
                        count += prevCount;
                    }
                    appLicensesUsedByDevices.put(appGroupId, count);
                }
            }
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, "Exception in  {0}", ex);
        }
    }
    
    private void getLicenseCountToDevicesForVppDeviceBasedApp(final List resourceIds, final HashMap appLicensesUsedByDevices, final List businessStoreIDList) throws SQLException {
        DMDataSetWrapper dataSetForDeviceAssignment = null;
        try {
            final SelectQuery baseSelectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("MdVppAssetToManagedDeviceRel"));
            baseSelectQuery.addJoin(new Join("MdVppAssetToManagedDeviceRel", "MdStoreAssetToAppGroupRel", new String[] { "VPP_ASSET_ID" }, new String[] { "STORE_ASSET_ID" }, 2));
            baseSelectQuery.addJoin(new Join("MdStoreAssetToAppGroupRel", "MdVppAsset", new String[] { "STORE_ASSET_ID" }, new String[] { "VPP_ASSET_ID" }, 2));
            baseSelectQuery.addJoin(new Join("MdVppAsset", "MdVPPTokenDetails", new String[] { "TOKEN_ID" }, new String[] { "TOKEN_ID" }, 2));
            baseSelectQuery.addJoin(new Join("MdStoreAssetToAppGroupRel", "MdBusinessStoreToAssetRel", new String[] { "STORE_ASSET_ID" }, new String[] { "STORE_ASSET_ID" }, 2));
            final Column managedUserToDeviceCount = Column.getColumn("MdVppAssetToManagedDeviceRel", "MANAGED_DEVICE_ID").count();
            managedUserToDeviceCount.setColumnAlias("APP_COUNT");
            baseSelectQuery.addSelectColumn(managedUserToDeviceCount);
            baseSelectQuery.addSelectColumn(Column.getColumn("MdStoreAssetToAppGroupRel", "APP_GROUP_ID"));
            Criteria criteria = new Criteria(Column.getColumn("MdVppAssetToManagedDeviceRel", "MANAGED_DEVICE_ID"), (Object)resourceIds.toArray(), 8);
            criteria = criteria.and(new Criteria(Column.getColumn("MdVPPTokenDetails", "LICENSE_ASSIGN_TYPE"), (Object)2, 0));
            if (businessStoreIDList != null && !businessStoreIDList.isEmpty()) {
                final Criteria businessStoreCriteria = new Criteria(Column.getColumn("MdBusinessStoreToAssetRel", "BUSINESSSTORE_ID"), (Object)businessStoreIDList.toArray(), 8);
                criteria = criteria.and(businessStoreCriteria);
            }
            baseSelectQuery.setCriteria(criteria);
            final List list = new ArrayList();
            final Column groupByCol = Column.getColumn("MdStoreAssetToAppGroupRel", "APP_GROUP_ID");
            list.add(groupByCol);
            final GroupByClause appGroupBy = new GroupByClause(list);
            baseSelectQuery.setGroupByClause(appGroupBy);
            dataSetForDeviceAssignment = DMDataSetWrapper.executeQuery((Object)baseSelectQuery);
            if (dataSetForDeviceAssignment != null) {
                while (dataSetForDeviceAssignment.next()) {
                    final long appGroupId = (long)dataSetForDeviceAssignment.getValue("APP_GROUP_ID");
                    int count = (int)dataSetForDeviceAssignment.getValue("APP_COUNT");
                    final Integer prevCount = appLicensesUsedByDevices.get(appGroupId);
                    if (prevCount != null) {
                        count += prevCount;
                    }
                    appLicensesUsedByDevices.put(appGroupId, count);
                }
            }
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, "Exception in  {0}", ex);
        }
    }
    
    private void getRedemptionCodeLicensesAssociatedToDevices(final List groupResourceIds, final long customerId, final HashMap appLicensesUsedByGroupMembers) throws Exception {
        this.getLicenseCountToDeviceForVppRedemtionCodeApp(groupResourceIds, appLicensesUsedByGroupMembers);
        this.logger.log(Level.INFO, "Query execution completed without exceptions!");
    }
    
    private void getLicenseCountToDeviceForVppRedemtionCodeApp(final List resourceIds, final HashMap appLicensesUsedByDevices) throws SQLException {
        DMDataSetWrapper dataSetForVppRedeemptionCode = null;
        final Connection connection = null;
        try {
            final SelectQuery baseSelectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("MdAppLicenseToResources"));
            baseSelectQuery.addJoin(new Join("MdAppLicenseToResources", "MdLicenseToAppGroupRel", new String[] { "APP_GROUP_ID" }, new String[] { "APP_GROUP_ID" }, 2));
            baseSelectQuery.addJoin(new Join("MdLicenseToAppGroupRel", "MdLicense", new String[] { "LICENSE_ID" }, new String[] { "LICENSE_ID" }, 2));
            final Column managedUserToDeviceCount = Column.getColumn("MdAppLicenseToResources", "RESOURCE_ID").count();
            managedUserToDeviceCount.setColumnAlias("APP_COUNT");
            baseSelectQuery.addSelectColumn(managedUserToDeviceCount);
            baseSelectQuery.addSelectColumn(Column.getColumn("MdAppLicenseToResources", "APP_GROUP_ID"));
            final Criteria licenseTypeCri = new Criteria(Column.getColumn("MdLicense", "LICENSED_TYPE"), (Object)1, 0);
            final Criteria resourceIdCri = new Criteria(Column.getColumn("MdAppLicenseToResources", "RESOURCE_ID"), (Object)resourceIds.toArray(), 8);
            baseSelectQuery.setCriteria(resourceIdCri.and(licenseTypeCri));
            final List list = new ArrayList();
            final Column groupByCol = Column.getColumn("MdAppLicenseToResources", "APP_GROUP_ID");
            list.add(groupByCol);
            final GroupByClause appGroupBy = new GroupByClause(list);
            baseSelectQuery.setGroupByClause(appGroupBy);
            dataSetForVppRedeemptionCode = DMDataSetWrapper.executeQuery((Object)baseSelectQuery);
            if (dataSetForVppRedeemptionCode != null) {
                while (dataSetForVppRedeemptionCode.next()) {
                    final long appGroupId = (long)dataSetForVppRedeemptionCode.getValue("APP_GROUP_ID");
                    final int count = (int)dataSetForVppRedeemptionCode.getValue("APP_COUNT");
                    appLicensesUsedByDevices.put(appGroupId, count);
                }
            }
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, "Exception in  getLicenseCountToDeviceForVppRedemtionCodeApp {0}", ex);
        }
    }
    
    @Override
    public void getCollectionSpecificUserProperties(final JSONObject collectionProperties, final Properties userProperties, final TreeNode childNode) throws Exception {
        final Long collectionID = collectionProperties.optLong("collectionID");
        final Long customerId = collectionProperties.optLong("customerID");
        try {
            final IOSKioskProfileDataHandler kioskHandler = new IOSKioskProfileDataHandler();
            if (MDMConfigUtil.getConfigIds(collectionID).contains(183) && kioskHandler.isIOSKioskAppAutomation(collectionID, customerId)) {
                this.getKioskAutomateUserProperties(collectionProperties, userProperties, childNode);
            }
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Exception in getting profile specific properties", e);
        }
    }
    
    private void getKioskAutomateUserProperties(final JSONObject collectionProperties, final Properties userDataProperties, final TreeNode childNode) throws Exception {
        final Long collectionId = collectionProperties.optLong("collectionID");
        final boolean isGroup = collectionProperties.optBoolean("isGroup");
        final Long[] groupIds = (Long[])collectionProperties.opt("groupIDs");
        final Long customerId = collectionProperties.optLong("customerID");
        final Long[] deviceIds = (Long[])collectionProperties.opt("deviceIDs");
        int totalDeviceInGroup = 0;
        final IOSKioskProfileDataHandler kioskHandler = new IOSKioskProfileDataHandler();
        final JSONObject appDetails = kioskHandler.isProfileApplicableForIOSKioskAutomation(collectionId, customerId);
        final boolean isAutomatePossible = appDetails.optInt("KIOSK_MODE") == 1;
        if (isAutomatePossible) {
            final Long appGroupID = appDetails.optLong("APP_GROUP_ID");
            final JSONObject appLicenseDetails = IOSSeqCmdUtil.getInstance().getAppDetails(appGroupID);
            final boolean isEnterprise = appLicenseDetails.optInt("PACKAGE_TYPE") == 2;
            final Long appProfileID = AppsUtil.getInstance().getProfileIDFromAppGroupID(appGroupID);
            final List appProfileList = new ArrayList();
            appProfileList.add(appProfileID);
            Properties preferredBusinessStoreMap = null;
            List businessStoreList = null;
            Long businessStoreID = null;
            final Boolean isVppApp = new ManagedAppDataHandler().isAppPurchasedFromPortal(appGroupID);
            if (!isEnterprise && isVppApp) {
                HashMap licenseAssocitedTodevice = new HashMap();
                if (isGroup) {
                    final List groupResourceIdsList = new ArrayList(Arrays.asList(groupIds));
                    preferredBusinessStoreMap = new ProfileAssociateHandler().getPreferredProfileToBusinessStoreMap(preferredBusinessStoreMap, 1, appProfileList, Arrays.asList(groupIds));
                    businessStoreID = null;
                    if (preferredBusinessStoreMap != null) {
                        businessStoreID = ((Hashtable<K, Long>)preferredBusinessStoreMap).get(appProfileID);
                    }
                    if (businessStoreID != null) {
                        businessStoreList = new ArrayList();
                        businessStoreList.add(businessStoreID);
                    }
                    totalDeviceInGroup = ManagedDeviceHandler.getInstance().getManagedDeviceCountInGroups(groupResourceIdsList, null, 1);
                    licenseAssocitedTodevice = this.getLicensesAssociatedToGroupsMap(groupResourceIdsList, customerId, businessStoreList);
                }
                else {
                    final List groupResourceIdsList = new ArrayList(Arrays.asList(deviceIds));
                    totalDeviceInGroup = ManagedDeviceHandler.getInstance().getManagedDeviceCountForResources(groupResourceIdsList, null);
                    preferredBusinessStoreMap = new ProfileAssociateHandler().getPreferredProfileToBusinessStoreMap(preferredBusinessStoreMap, 1, appProfileList, groupResourceIdsList);
                    businessStoreID = null;
                    if (preferredBusinessStoreMap != null) {
                        businessStoreID = ((Hashtable<K, Long>)preferredBusinessStoreMap).get(appProfileID);
                    }
                    if (businessStoreID != null) {
                        businessStoreList = new ArrayList();
                        businessStoreList.add(businessStoreID);
                    }
                    licenseAssocitedTodevice = this.getLicensesAssociatedToResourcesMap(groupResourceIdsList, customerId, businessStoreList);
                }
                int licenseAssignmentType = 0;
                if (businessStoreID != null) {
                    licenseAssignmentType = VPPAppMgmtHandler.getInstance().getVppGlobalAssignmentType(customerId, businessStoreID);
                }
                final HashMap remainingLicenseCountMap = this.getRemainingLicenseCountMap(customerId, businessStoreList);
                int remainingLicense = 0;
                if (remainingLicenseCountMap.containsKey(appGroupID)) {
                    final JSONObject licenseSummaryJSON = remainingLicenseCountMap.get(appGroupID);
                    remainingLicense = licenseSummaryJSON.optInt("AVAILABLE_LICENSE_COUNT");
                }
                final boolean isLicenseAvailable = this.getIfAppHasEnoughLicensesForGroup(appGroupID, remainingLicense, totalDeviceInGroup, licenseAssocitedTodevice);
                if (licenseAssignmentType == 2 && !isLicenseAvailable) {
                    ((Hashtable<String, Boolean>)userDataProperties).put("isEnabled", isLicenseAvailable);
                    final String appName = AppsUtil.getInstance().getAppName(appGroupID);
                    String insufficientLicenseMsg = I18N.getMsg("mdm.profile.ios.kiosk.license", new Object[] { appName });
                    if (businessStoreID != null) {
                        insufficientLicenseMsg = I18N.getMsg("mdm.profile.ios.kiosk_license_not_available", new Object[] { appName, MDBusinessStoreUtil.getBusinessStoreName(businessStoreID) });
                    }
                    ((Hashtable<String, String>)userDataProperties).put("remarks", insufficientLicenseMsg);
                    ((Hashtable<String, Boolean>)userDataProperties).put("insufficientLicense", true);
                    childNode.style = "background-image: url(/images/profile.png) !important; background-color: rgb(249, 249, 249) !important; filter: opacity(80%); border: 1px solid rgb(226, 226, 226) !important; color: black !important; cursor:not-allowed";
                }
            }
        }
    }
}
