package com.adventnet.sym.webclient.mdm.config;

import java.util.HashMap;
import javax.servlet.http.HttpServletRequest;
import com.adventnet.persistence.DataAccessException;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.me.mdm.server.apps.AppTrashModeHandler;
import com.adventnet.sym.server.mdm.util.MDMStringUtils;
import com.adventnet.sym.server.mdm.inv.AppDataHandler;
import com.me.devicemanagement.framework.webclient.common.SYMClientUtil;
import com.me.mdm.server.role.RBDAUtil;
import com.me.mdm.server.apps.multiversion.AppVersionDBUtil;
import com.adventnet.ds.query.Criteria;
import com.me.devicemanagement.framework.webclient.customer.MSPWebClientUtil;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.Query;
import com.adventnet.ds.query.DerivedTable;
import java.util.List;
import com.adventnet.ds.query.GroupByClause;
import java.util.Arrays;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.client.view.web.ViewContext;
import com.adventnet.sym.webclient.mdm.MDMEmberTableRetrieverAction;

public class AppRepositoryTRAction extends MDMEmberTableRetrieverAction
{
    private static final String APP_GRP_TO_COLLN_COUNT_TAB_NAME = "AppGroupToCollection.count";
    public static final String APP_GRP_TO_COLLN_COUNT_COL_NAME = "AppGroupToCollection.RELEASE_LABEL_ID.count";
    private static final String SPLIT_APK_COUNT_TAB_NAME = "BusinessStoreAppVersion.count";
    public static final String SPLIT_APK_COUNT_COL_NAME = "BusinessStoreAppVersion.APP_GROUP_ID.count";
    
    @Override
    protected SelectQuery fetchAndCacheSelectQuery(final ViewContext viewCtx) throws Exception {
        final SelectQuery selectQuery = super.fetchAndCacheSelectQuery(viewCtx);
        if (!selectQuery.containsSubQuery()) {
            final SelectQuery subQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("AppGroupToCollection"));
            final Column appGroupIdCol = Column.getColumn("AppGroupToCollection", "APP_GROUP_ID");
            final Column appGroupIdCountCol = appGroupIdCol.count();
            appGroupIdCountCol.setColumnAlias("AppGroupToCollection.RELEASE_LABEL_ID.count");
            subQuery.addSelectColumn(appGroupIdCol);
            subQuery.addSelectColumn(appGroupIdCountCol);
            final GroupByClause groupByClause = new GroupByClause((List)Arrays.asList(appGroupIdCol));
            subQuery.setGroupByClause(groupByClause);
            final DerivedTable appGroupToCollectionCount = new DerivedTable("AppGroupToCollection.count", (Query)subQuery);
            final Join appGroupToCollectionCountJoin = new Join(Table.getTable("AppGroupToCollection"), (Table)appGroupToCollectionCount, new String[] { "APP_GROUP_ID" }, new String[] { "APP_GROUP_ID" }, 2);
            selectQuery.addJoin(appGroupToCollectionCountJoin);
            selectQuery.addSelectColumn(Column.getColumn("AppGroupToCollection.count", "AppGroupToCollection.RELEASE_LABEL_ID.count"));
            final SelectQuery splitApkQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("BusinessStoreAppVersion"));
            final Column splitAppGroupIdCol = Column.getColumn("BusinessStoreAppVersion", "APP_GROUP_ID");
            final Column splitAppGroupIdCountCol = splitAppGroupIdCol.count();
            splitAppGroupIdCountCol.setColumnAlias("BusinessStoreAppVersion.APP_GROUP_ID.count");
            splitApkQuery.addSelectColumn(splitAppGroupIdCol);
            splitApkQuery.addSelectColumn(splitAppGroupIdCountCol);
            final GroupByClause splitGroupByClause = new GroupByClause((List)Arrays.asList(splitAppGroupIdCol));
            splitApkQuery.setGroupByClause(splitGroupByClause);
            final DerivedTable splitApkCount = new DerivedTable("BusinessStoreAppVersion.count", (Query)splitApkQuery);
            final Join splitApkCountJoin = new Join(Table.getTable("AppGroupToCollection"), (Table)splitApkCount, new String[] { "APP_GROUP_ID" }, new String[] { "APP_GROUP_ID" }, 1);
            selectQuery.addJoin(splitApkCountJoin);
            selectQuery.addSelectColumn(Column.getColumn("BusinessStoreAppVersion.count", "BusinessStoreAppVersion.APP_GROUP_ID.count"));
        }
        return selectQuery;
    }
    
    @Override
    public void setCriteria(final SelectQuery query, final ViewContext viewCtx) {
        try {
            final HttpServletRequest request = viewCtx.getRequest();
            final String viewname = viewCtx.getUniqueId();
            final String appTypeStr = request.getParameter("appType");
            final String platformTypeStr = request.getParameter("platformType");
            final String isForAllCustomersStr = request.getParameter("isForAllCustomers");
            final String nonVppApp = request.getParameter("nonVppAppOnly");
            final String businessStoreIDStr = request.getParameter("businessStoreID");
            final HashMap packageTypeMap = MDMUtil.getInstance().getAppPackageTypeMap();
            request.setAttribute("packageTypeMap", (Object)packageTypeMap);
            final String afwConfigured = request.getParameter("afwConfigured");
            if (afwConfigured != null) {
                request.setAttribute("afwConfigured", (Object)afwConfigured);
            }
            final String showAppUpdatesAvailable = request.getParameter("showAppUpdatesAvailable");
            if (showAppUpdatesAvailable != null) {
                request.setAttribute("showAppUpdatesAvailable", (Object)showAppUpdatesAvailable);
            }
            int appType = -1;
            final Long customerID = MSPWebClientUtil.getCustomerID(request);
            final Criteria nativeAgentCriteria = new Criteria(Column.getColumn("Profile", "PROFILE_TYPE"), (Object)7, 1);
            final Criteria customerCriteria = new Criteria(Column.getColumn("ProfileToCustomerRel", "CUSTOMER_ID"), (Object)customerID, 0);
            final Criteria collectionCriteria = AppVersionDBUtil.getInstance().getCriteriaForCollectionIdWithProfileToCollection();
            final Criteria approvedAppLabelCriteria = AppVersionDBUtil.getInstance().getApprovedAppVersionCriteria();
            Criteria criteria = customerCriteria.and(collectionCriteria.and(approvedAppLabelCriteria)).and(nativeAgentCriteria);
            if (appTypeStr != null && !appTypeStr.equalsIgnoreCase("all")) {
                request.setAttribute("appType", (Object)appTypeStr);
                appType = Integer.valueOf(appTypeStr);
                final Criteria packageTypeCri = MDMUtil.getInstance().getPackageTypeCriteria(appType);
                criteria = criteria.and(packageTypeCri);
            }
            else if (platformTypeStr != null && !platformTypeStr.equalsIgnoreCase("all")) {
                final Criteria platformTypeCriteria = new Criteria(Column.getColumn("Profile", "PLATFORM_TYPE"), (Object)Integer.valueOf(platformTypeStr), 0);
                criteria = criteria.and(platformTypeCriteria);
            }
            Criteria userIdCrit = null;
            userIdCrit = RBDAUtil.getInstance().getProfileCreatedOrModifiedByCriteria(SYMClientUtil.getLoginId(request));
            if (userIdCrit != null) {
                criteria = criteria.and(userIdCrit);
            }
            final String showVppApp = request.getParameter("showVppApp");
            if (showVppApp != null && showVppApp.equalsIgnoreCase("true")) {
                final Criteria isPortalPurchased = new Criteria(Column.getColumn("MdPackageToAppGroup", "IS_PURCHASED_FROM_PORTAL"), (Object)true, 0);
                final Criteria platformType = new Criteria(Column.getColumn("MdPackage", "PLATFORM_TYPE"), (Object)1, 0);
                criteria = criteria.and(isPortalPurchased.and(platformType));
            }
            final String showBstoreApp = request.getParameter("showBstoreApp");
            if (showBstoreApp != null && showBstoreApp.equalsIgnoreCase("true")) {
                final Criteria platformType = new Criteria(Column.getColumn("MdPackage", "PLATFORM_TYPE"), (Object)3, 0);
                final Criteria isPortalPurchased2 = new Criteria(Column.getColumn("MdPackageToAppGroup", "IS_PURCHASED_FROM_PORTAL"), (Object)true, 0);
                final Criteria bstoreAppCriteria = platformType.and(isPortalPurchased2);
                criteria = criteria.and(bstoreAppCriteria);
            }
            final String showPfwApp = request.getParameter("showPfwApp");
            if (showPfwApp != null && showPfwApp.equalsIgnoreCase("true")) {
                final Criteria platformType2 = new Criteria(Column.getColumn("MdPackage", "PLATFORM_TYPE"), (Object)2, 0);
                final Criteria isPortalPurchased3 = new Criteria(Column.getColumn("MdPackageToAppGroup", "IS_PURCHASED_FROM_PORTAL"), (Object)true, 0);
                final Criteria bstoreAppCriteria2 = platformType2.and(isPortalPurchased3);
                criteria = criteria.and(bstoreAppCriteria2);
            }
            if (showAppUpdatesAvailable != null && showAppUpdatesAvailable.equalsIgnoreCase("true")) {
                final List appUpdatesAvailableList = new AppDataHandler().getListofAppsWithUpdate(customerID);
                final Criteria appUpdateAvailableCri = new Criteria(Column.getColumn("MdAppGroupDetails", "APP_GROUP_ID"), (Object)appUpdatesAvailableList.toArray(), 8);
                criteria = criteria.and(appUpdateAvailableCri);
            }
            final String businessAppFilter = request.getParameter("businessAppFilter");
            if (!"all".equals(businessAppFilter) && businessAppFilter != null) {
                int appPlatformType = -1;
                boolean isPurchasedfromPortal = true;
                if (businessAppFilter.equals("10") || businessAppFilter.equals("11")) {
                    appPlatformType = 1;
                }
                else if (businessAppFilter.equals("12") || businessAppFilter.equals("13")) {
                    appPlatformType = 2;
                }
                if (businessAppFilter.equals("11") || businessAppFilter.equals("13")) {
                    isPurchasedfromPortal = false;
                }
                if (appPlatformType != -1) {
                    final Criteria platformType3 = new Criteria(Column.getColumn("MdPackage", "PLATFORM_TYPE"), (Object)appPlatformType, 0);
                    final Criteria isPortalPurchased4 = new Criteria(Column.getColumn("MdPackageToAppGroup", "IS_PURCHASED_FROM_PORTAL"), (Object)isPurchasedfromPortal, 0);
                    final Criteria appCriteria = platformType3.and(isPortalPurchased4);
                    criteria = criteria.and(appCriteria);
                }
            }
            request.setAttribute("businessAppFilter", (Object)businessAppFilter);
            final String appGroupIdFilter = request.getParameter("appGroupId");
            if (!"all".equals(appGroupIdFilter) && !MDMStringUtils.isEmpty(appGroupIdFilter)) {
                final Long appGroupId = Long.valueOf(appGroupIdFilter);
                final Criteria appGroupCriteria = new Criteria(Column.getColumn("MdAppGroupDetails", "APP_GROUP_ID"), (Object)appGroupId, 0);
                criteria = criteria.and(appGroupCriteria);
            }
            request.setAttribute("trashAppCount", (Object)new AppTrashModeHandler().getAppTrashCount(customerID));
            if (viewname.equalsIgnoreCase("TrashAppList")) {
                final Criteria trashList = new Criteria(Column.getColumn("Profile", "IS_MOVED_TO_TRASH"), (Object)true, 0);
                criteria = criteria.and(trashList);
            }
            else {
                final Criteria appList = new Criteria(Column.getColumn("Profile", "IS_MOVED_TO_TRASH"), (Object)false, 0);
                criteria = criteria.and(appList);
            }
            if (!MDMStringUtils.isEmpty(isForAllCustomersStr)) {
                final Boolean isForAllCustomers = Boolean.parseBoolean(isForAllCustomersStr);
                if (isForAllCustomers) {
                    criteria = criteria.and(new Criteria(Column.getColumn("MdPackage", "APP_SHARED_SCOPE"), (Object)1, 0));
                }
                else {
                    criteria = criteria.and(new Criteria(Column.getColumn("MdPackage", "APP_SHARED_SCOPE"), (Object)0, 0));
                }
            }
            if (nonVppApp != null || businessStoreIDStr != null) {
                final SelectQuery multipleVPPSubquery = (SelectQuery)new SelectQueryImpl(Table.getTable("MdStoreAssetToAppGroupRel"));
                final Column appGroupIDCol = Column.getColumn("MdStoreAssetToAppGroupRel", "APP_GROUP_ID");
                final Column splitAppGroupIdCountCol = appGroupIDCol.count();
                final String MULTI_VPP_TAB_NAME = "MdStoreAssetToAppGroupRel.count";
                final String MULTI_VPP_TAB_COL_NAME = "MdStoreAssetToAppGroupRel.APP_GROUP_ID.count";
                splitAppGroupIdCountCol.setColumnAlias(MULTI_VPP_TAB_COL_NAME);
                multipleVPPSubquery.addSelectColumn(appGroupIDCol);
                multipleVPPSubquery.addSelectColumn(splitAppGroupIdCountCol);
                final Join vppAssetTableJoin = new Join("MdStoreAssetToAppGroupRel", "MdVppAsset", new String[] { "STORE_ASSET_ID" }, new String[] { "VPP_ASSET_ID" }, 2);
                final Join assetToToken = new Join("MdVppAsset", "MdBusinessStoreToVppRel", new String[] { "TOKEN_ID" }, new String[] { "TOKEN_ID" }, 2);
                Long businessStoreID = -1L;
                if (businessStoreIDStr != null) {
                    businessStoreID = Long.parseLong(businessStoreIDStr);
                    if (businessStoreID != -1L) {
                        final Criteria criteria2 = new Criteria(Column.getColumn("MdBusinessStoreToVppRel", "BUSINESSSTORE_ID"), (Object)businessStoreID, 0);
                        multipleVPPSubquery.setCriteria(criteria2);
                    }
                }
                multipleVPPSubquery.addJoin(vppAssetTableJoin);
                multipleVPPSubquery.addJoin(assetToToken);
                final GroupByClause splitGroupByClause = new GroupByClause((List)Arrays.asList(appGroupIDCol));
                multipleVPPSubquery.setGroupByClause(splitGroupByClause);
                final DerivedTable splitApkCount = new DerivedTable(MULTI_VPP_TAB_NAME, (Query)multipleVPPSubquery);
                final Join splitApkCountJoin = new Join(Table.getTable("MdAppGroupDetails"), (Table)splitApkCount, new String[] { "APP_GROUP_ID" }, new String[] { "APP_GROUP_ID" }, 1);
                query.addJoin(splitApkCountJoin);
                query.addSelectColumn(Column.getColumn(MULTI_VPP_TAB_NAME, MULTI_VPP_TAB_COL_NAME));
                if (nonVppApp != null && nonVppApp.equals("true")) {
                    criteria = criteria.and(new Criteria(Column.getColumn(MULTI_VPP_TAB_NAME, MULTI_VPP_TAB_COL_NAME), (Object)null, 0));
                }
                if (businessStoreID != -1L) {
                    criteria = criteria.and(new Criteria(Column.getColumn(MULTI_VPP_TAB_NAME, MULTI_VPP_TAB_COL_NAME), (Object)null, 1));
                }
            }
            if (query.getCriteria() != null) {
                criteria = criteria.and(query.getCriteria());
            }
            query.setCriteria(criteria);
            super.setCriteria(query, viewCtx);
        }
        catch (final DataAccessException ex) {
            Logger.getLogger(AppRepositoryTRAction.class.getName()).log(Level.SEVERE, null, (Throwable)ex);
        }
    }
}
