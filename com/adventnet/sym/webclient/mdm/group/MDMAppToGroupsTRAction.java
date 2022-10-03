package com.adventnet.sym.webclient.mdm.group;

import org.json.JSONObject;
import java.util.HashMap;
import javax.servlet.http.HttpServletRequest;
import java.util.logging.Level;
import com.adventnet.sym.server.mdm.util.MDMDBUtil;
import java.util.Arrays;
import com.me.devicemanagement.framework.server.customer.CustomerInfoUtil;
import com.adventnet.sym.server.mdm.apps.AppsUtil;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Query;
import com.adventnet.ds.query.DerivedTable;
import java.util.List;
import com.adventnet.ds.query.GroupByClause;
import com.adventnet.ds.query.Column;
import java.util.ArrayList;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.me.mdm.server.apps.multiversion.AppVersionDBUtil;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.client.view.web.ViewContext;
import java.util.logging.Logger;
import com.adventnet.sym.webclient.mdm.MDMEmberTableRetrieverAction;

public class MDMAppToGroupsTRAction extends MDMEmberTableRetrieverAction
{
    private Logger logger;
    
    public MDMAppToGroupsTRAction() {
        this.logger = Logger.getLogger(MDMAppToGroupsTRAction.class.getName());
    }
    
    @Override
    protected SelectQuery fetchAndCacheSelectQuery(final ViewContext viewCtx) throws Exception {
        final SelectQuery selectQuery = super.fetchAndCacheSelectQuery(viewCtx);
        final Join appGroupCollnToReleaseLabelHistory = AppVersionDBUtil.getInstance().getJoinForCollectionsLatestAppReleaseLabelFromHistoryTable("AppGroupCollnHistory");
        selectQuery.addJoin(appGroupCollnToReleaseLabelHistory);
        final SelectQuery nextExecutionTimeSubQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("RecentProfileForGroup"));
        nextExecutionTimeSubQuery.addJoin(new Join("RecentProfileForGroup", "AutoAppUpdateConfigToCollection", new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, 2));
        nextExecutionTimeSubQuery.addJoin(new Join("AutoAppUpdateConfigToCollection", "AutoAppUpdatePackageList", new String[] { "APP_UPDATE_CONF_ID" }, new String[] { "APP_UPDATE_CONF_ID" }, 2));
        nextExecutionTimeSubQuery.addJoin(new Join("AutoAppUpdateConfigToCollection", "AppUpdatePolicyCollnToScheduleRepo", new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, 2));
        nextExecutionTimeSubQuery.addJoin(new Join("AppUpdatePolicyCollnToScheduleRepo", "ScheduleRepository", new String[] { "SCHEDULE_ID" }, new String[] { "SCHEDULE_ID" }, 2));
        final List<Column> nextExecutionTimeQueryGroupByColumn = new ArrayList<Column>();
        nextExecutionTimeQueryGroupByColumn.add(Column.getColumn("RecentProfileForGroup", "GROUP_ID"));
        nextExecutionTimeQueryGroupByColumn.add(Column.getColumn("AutoAppUpdatePackageList", "PACKAGE_ID"));
        nextExecutionTimeSubQuery.setGroupByClause(new GroupByClause((List)nextExecutionTimeQueryGroupByColumn));
        final Column nextExecutionTimeCol = Column.getColumn("ScheduleRepository", "NEXT_EXECUTION_TIME").minimum();
        nextExecutionTimeCol.setColumnAlias("next_execution_time");
        nextExecutionTimeSubQuery.addSelectColumn(nextExecutionTimeCol);
        nextExecutionTimeSubQuery.addSelectColumn(Column.getColumn("RecentProfileForGroup", "GROUP_ID"));
        nextExecutionTimeSubQuery.addSelectColumn(Column.getColumn("AutoAppUpdatePackageList", "PACKAGE_ID"));
        final DerivedTable nextExecutionTimeTable = new DerivedTable("NEXT_EXECUTION_TIME_TABLE", (Query)nextExecutionTimeSubQuery);
        final SelectQuery associatedPolicySubQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("RecentProfileForGroup"));
        associatedPolicySubQuery.addJoin(new Join("RecentProfileForGroup", "AutoAppUpdateConfigToCollection", new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, 2));
        associatedPolicySubQuery.addJoin(new Join("AutoAppUpdateConfigToCollection", "AutoAppUpdatePackageList", new String[] { "APP_UPDATE_CONF_ID" }, new String[] { "APP_UPDATE_CONF_ID" }, 2));
        associatedPolicySubQuery.addJoin(new Join("AutoAppUpdateConfigToCollection", "AppUpdatePolicyCollnToScheduleRepo", new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, 2));
        associatedPolicySubQuery.addJoin(new Join("AppUpdatePolicyCollnToScheduleRepo", "ScheduleRepository", new String[] { "SCHEDULE_ID" }, new String[] { "SCHEDULE_ID" }, 2));
        final Criteria nextExecutionTimeCriteria = new Criteria(Column.getColumn("NEXT_EXECUTION_TIME_TABLE", "next_execution_time"), (Object)Column.getColumn("ScheduleRepository", "NEXT_EXECUTION_TIME"), 0);
        final Criteria groupCriteria = new Criteria(Column.getColumn("RecentProfileForGroup", "GROUP_ID"), (Object)Column.getColumn("NEXT_EXECUTION_TIME_TABLE", "GROUP_ID"), 0);
        final Criteria packageCriteria = new Criteria(Column.getColumn("AutoAppUpdatePackageList", "PACKAGE_ID"), (Object)Column.getColumn("NEXT_EXECUTION_TIME_TABLE", "PACKAGE_ID"), 0);
        associatedPolicySubQuery.addJoin(new Join(Table.getTable("AutoAppUpdatePackageList"), (Table)nextExecutionTimeTable, groupCriteria.and(packageCriteria).and(nextExecutionTimeCriteria), 2));
        final List<Column> associatedPolicyQueryGroupByColumn = new ArrayList<Column>();
        associatedPolicyQueryGroupByColumn.add(Column.getColumn("RecentProfileForGroup", "GROUP_ID"));
        associatedPolicyQueryGroupByColumn.add(Column.getColumn("AutoAppUpdatePackageList", "PACKAGE_ID"));
        associatedPolicyQueryGroupByColumn.add(Column.getColumn("NEXT_EXECUTION_TIME_TABLE", "next_execution_time"));
        associatedPolicySubQuery.setGroupByClause(new GroupByClause((List)associatedPolicyQueryGroupByColumn));
        associatedPolicySubQuery.addSelectColumn(Column.getColumn("RecentProfileForGroup", "GROUP_ID"));
        associatedPolicySubQuery.addSelectColumn(Column.getColumn("AutoAppUpdatePackageList", "PACKAGE_ID"));
        final Column nextExecutionTimeColumn = new Column("NEXT_EXECUTION_TIME_TABLE", "next_execution_time");
        nextExecutionTimeColumn.setColumnAlias("next_execution_time");
        associatedPolicySubQuery.addSelectColumn(nextExecutionTimeColumn);
        final Column maxProfileColumn = Column.getColumn("RecentProfileForGroup", "PROFILE_ID").maximum();
        maxProfileColumn.setColumnAlias("profile_id");
        associatedPolicySubQuery.addSelectColumn(maxProfileColumn);
        final DerivedTable associatedApoUpdatePolicyTable = new DerivedTable("APP_UPDATE_POLICY_TABLE", (Query)associatedPolicySubQuery);
        final Criteria resourceCriteria = new Criteria(Column.getColumn("Resource", "RESOURCE_ID"), (Object)Column.getColumn("APP_UPDATE_POLICY_TABLE", "GROUP_ID"), 0);
        final Criteria associatedAppCriteria = new Criteria(Column.getColumn("MdPackageToAppData", "PACKAGE_ID"), (Object)Column.getColumn("APP_UPDATE_POLICY_TABLE", "PACKAGE_ID"), 0);
        selectQuery.addJoin(new Join(Table.getTable("Resource"), (Table)associatedApoUpdatePolicyTable, resourceCriteria.and(associatedAppCriteria), 1));
        selectQuery.addJoin(new Join((Table)associatedApoUpdatePolicyTable, Table.getTable("Profile"), new String[] { "profile_id" }, new String[] { "PROFILE_ID" }, 1));
        selectQuery.addSelectColumn(Column.getColumn("Profile", "PROFILE_ID"));
        selectQuery.addSelectColumn(Column.getColumn("Profile", "PROFILE_NAME", "Profile.PROFILE_NAME"));
        selectQuery.addSelectColumn(new Column("APP_UPDATE_POLICY_TABLE", "next_execution_time", "next_execution_time"));
        return selectQuery;
    }
    
    @Override
    public void setCriteria(final SelectQuery selectQuery, final ViewContext viewCtx) {
        try {
            final HttpServletRequest request = viewCtx.getRequest();
            final Long packageId = Long.parseLong(request.getParameter("packageId"));
            final Long releaseLabelId = Long.parseLong(request.getParameter("releaseLabelId"));
            final String updatePolicyIdStr = request.getParameter("policyId");
            final Criteria packageCriteria = new Criteria(new Column("MdPackageToAppData", "PACKAGE_ID"), (Object)packageId, 0);
            final Criteria labelCriteria = new Criteria(new Column("AppGroupCollnHistory", "RELEASE_LABEL_ID"), (Object)releaseLabelId, 0);
            selectQuery.setCriteria(packageCriteria.and(labelCriteria));
            if (updatePolicyIdStr != null && !updatePolicyIdStr.equalsIgnoreCase("all")) {
                final Long updatePolicyId = Long.parseLong(updatePolicyIdStr);
                final Criteria policyCriteria = new Criteria(Column.getColumn("Profile", "PROFILE_ID"), (Object)updatePolicyId, 0);
                selectQuery.setCriteria(selectQuery.getCriteria().and(policyCriteria));
            }
            final String businessStoreIDStr = request.getParameter("businessstore_id");
            if (businessStoreIDStr != null) {
                final Long profileId = AppsUtil.getInstance().getProfileIdForPackage(packageId, CustomerInfoUtil.getInstance().getCustomerId());
                final Long businessStoreID = Long.parseLong(businessStoreIDStr);
                if (businessStoreID != 0L && businessStoreID != -1L) {
                    final SelectQuery multipleVPPSubquery = (SelectQuery)new SelectQueryImpl(Table.getTable("MDMResourceToDeploymentConfigs"));
                    final Column appGroupIDCol = Column.getColumn("MDMResourceToDeploymentConfigs", "RESOURCE_ID");
                    final Column splitAppGroupIdCountCol = appGroupIDCol.count();
                    final String MULTI_VPP_TAB_NAME = "MDMResourceToDeploymentConfigs.count";
                    final String MULTI_VPP_TAB_COL_NAME = "MDMResourceToDeploymentConfigs.RESOURCE_ID.count";
                    splitAppGroupIdCountCol.setColumnAlias(MULTI_VPP_TAB_COL_NAME);
                    multipleVPPSubquery.addSelectColumn(appGroupIDCol);
                    multipleVPPSubquery.addSelectColumn(splitAppGroupIdCountCol);
                    final GroupByClause splitGroupByClause = new GroupByClause((List)Arrays.asList(appGroupIDCol));
                    multipleVPPSubquery.setGroupByClause(splitGroupByClause);
                    final Criteria bsStoreCri1 = new Criteria(new Column("MDMResourceToDeploymentConfigs", "PROFILE_ID"), (Object)profileId, 0);
                    final Criteria bsStoreCri2 = new Criteria(new Column("MDMResourceToDeploymentConfigs", "BUSINESSSTORE_ID"), (Object)businessStoreID, 0);
                    multipleVPPSubquery.setCriteria(bsStoreCri1.and(bsStoreCri2));
                    final DerivedTable splitApkCount = new DerivedTable(MULTI_VPP_TAB_NAME, (Query)multipleVPPSubquery);
                    final Join splitApkCountJoin = new Join(Table.getTable("Resource"), (Table)splitApkCount, new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 1);
                    selectQuery.addJoin(splitApkCountJoin);
                    selectQuery.addSelectColumn(Column.getColumn(MULTI_VPP_TAB_NAME, MULTI_VPP_TAB_COL_NAME));
                    selectQuery.setCriteria(MDMDBUtil.andCriteria(selectQuery.getCriteria(), new Criteria(Column.getColumn(MULTI_VPP_TAB_NAME, MULTI_VPP_TAB_COL_NAME), (Object)null, 1)));
                }
            }
            super.setCriteria(selectQuery, viewCtx);
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, "Exception in setCriteria in MDMAppToGroupsTRAction", ex);
        }
    }
    
    public void postModelFetch(final ViewContext viewContext) {
        try {
            final HttpServletRequest httpServletRequest = viewContext.getRequest();
            final String packageIdStr = httpServletRequest.getParameter("packageId");
            final String releaseLabelIdStr = httpServletRequest.getParameter("releaseLabelId");
            final Long packageId = Long.parseLong(packageIdStr);
            final Long releaseLabelId = Long.parseLong(releaseLabelIdStr);
            final JSONObject upgradeDowngradeAvailableDetails = AppVersionDBUtil.getInstance().validateIfUpgradeDowngradeAvailableForAppVersion(packageId, releaseLabelId);
            final HashMap map = new HashMap();
            map.put("is_upgrade_available", upgradeDowngradeAvailableDetails.get("is_upgrade_available"));
            map.put("is_downgrade_available", upgradeDowngradeAvailableDetails.get("is_downgrade_available"));
            map.put("is_distributable", upgradeDowngradeAvailableDetails.optBoolean("is_distributable", true));
            viewContext.getRequest().setAttribute("TRANSFORMER_PRE_DATA", (Object)map);
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, "Exception in MDMAppToGroupsTRAction postModelFetch", ex);
        }
    }
}
