package com.adventnet.sym.webclient.mdm.group;

import javax.servlet.http.HttpServletRequest;
import java.util.logging.Level;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import com.adventnet.ds.query.GroupByClause;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.me.mdm.server.apps.multiversion.AppVersionDBUtil;
import com.adventnet.ds.query.Table;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.client.view.web.ViewContext;
import java.util.logging.Logger;
import com.adventnet.sym.webclient.mdm.MDMEmberTableRetrieverAction;

public class MDMGroupProfileRetrieverAction extends MDMEmberTableRetrieverAction
{
    private Logger logger;
    
    public MDMGroupProfileRetrieverAction() {
        this.logger = Logger.getLogger(MDMGroupProfileRetrieverAction.class.getName());
    }
    
    @Override
    protected SelectQuery fetchAndCacheSelectQuery(final ViewContext viewCtx) throws Exception {
        final SelectQuery sQuery = super.fetchAndCacheSelectQuery(viewCtx);
        final List<Table> tableList = sQuery.getTableList();
        boolean getAppDetails = false;
        for (final Table table : tableList) {
            if (table.getTableName().equals("MdAppCatalogToGroup")) {
                return sQuery;
            }
            if (!table.getTableName().equals("MdPackageToAppData")) {
                continue;
            }
            getAppDetails = true;
        }
        if (getAppDetails) {
            final Join appCollnReleaseLabelToMaxJoin = AppVersionDBUtil.getInstance().getJoinForCollectionsLatestAppReleaseLabelFromHistoryTable("AppGroupCollnHistory");
            final Criteria appGroupCri = new Criteria(Column.getColumn("MdPackageToAppData", "APP_GROUP_ID"), (Object)Column.getColumn("MdAppCatalogToGroup", "APP_GROUP_ID"), 0);
            final Criteria grpResCri = new Criteria(Column.getColumn("CustomGroup", "RESOURCE_ID"), (Object)Column.getColumn("MdAppCatalogToGroup", "RESOURCE_ID"), 0);
            final Join mdAppCatalogToGrp = new Join("CustomGroup", "MdAppCatalogToGroup", grpResCri.and(appGroupCri), 1);
            final Join pubAppIdOfGrpToAppDetailsJoin = new Join("MdAppCatalogToGroup", "MdAppDetails", new String[] { "PUBLISHED_APP_ID" }, new String[] { "APP_ID" }, "MdAppCatalogToGroup", "PublishedAppId", 1);
            final Join approvedAppIdToAppDetailsJoin = new Join("MdAppCatalogToGroup", "MdAppDetails", new String[] { "APPROVED_APP_ID" }, new String[] { "APP_ID" }, "MdAppCatalogToGroup", "ApprovedAppId", 1);
            final Join AppToCollection = new Join("RecentProfileForGroup", "MdAppToCollection", new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, 1);
            final Criteria criteria = new Criteria(Column.getColumn("MdAppToCollection", "APP_ID"), (Object)Column.getColumn("LaptopLatest", "APP_ID"), 0);
            final Criteria LaptopLatest = new Criteria(Column.getColumn("LaptopLatest", "SUPPORTED_DEVICES"), (Object)16L, 0);
            final Join mdPackageJoin = new Join("MdAppToCollection", "MdAppToCollection", "MdPackageToAppData", "LaptopLatest", criteria.and(LaptopLatest), 1);
            final Join LapLateVer = new Join("LaptopLatest", "LaptopLatest", "MdAppDetails", "LaptopLatestVer", new Criteria(Column.getColumn("LaptopLatest", "APP_ID"), (Object)Column.getColumn("LaptopLatestVer", "APP_ID"), 0), 1);
            final Join AppToCollectionm = new Join("RecentProfileForGroup", "RecentProfileForGroup", "MdAppToCollection", "MdAppToColln", new Criteria(Column.getColumn("RecentProfileForGroup", "COLLECTION_ID"), (Object)Column.getColumn("MdAppToColln", "COLLECTION_ID"), 0), 1);
            final Criteria criteriam = new Criteria(Column.getColumn("MdAppToColln", "APP_ID"), (Object)Column.getColumn("MobLatest", "APP_ID"), 0);
            final Criteria LaptopLatestm = new Criteria(Column.getColumn("MobLatest", "SUPPORTED_DEVICES"), (Object)8L, 0);
            final Join mdPackageJoinm = new Join("MdAppToColln", "MdAppToColln", "MdPackageToAppData", "MobLatest", criteriam.and(LaptopLatestm), 1);
            final Join mobLateVer = new Join("MobLatest", "MobLatest", "MdAppDetails", "MobLatestVer", new Criteria(Column.getColumn("MobLatest", "APP_ID"), (Object)Column.getColumn("MobLatestVer", "APP_ID"), 0), 1);
            final Join AppToCollectionAll = new Join("RecentProfileForGroup", "RecentProfileForGroup", "MdAppToCollection", "MdAppToCollnAll", new Criteria(Column.getColumn("RecentProfileForGroup", "COLLECTION_ID"), (Object)Column.getColumn("MdAppToCollnAll", "COLLECTION_ID"), 0), 1);
            final Criteria criteriaAll = new Criteria(Column.getColumn("MdAppToCollnAll", "APP_ID"), (Object)Column.getColumn("AllLatest", "APP_ID"), 0);
            final Criteria LaptopLatestAll = new Criteria(Column.getColumn("AllLatest", "SUPPORTED_DEVICES"), (Object)24L, 0);
            final Join mdPackageJoinAll = new Join("MdAppToCollnAll", "MdAppToCollnAll", "MdPackageToAppData", "AllLatest", criteriaAll.and(LaptopLatestAll), 1);
            final Join allLateVer = new Join("AllLatest", "AllLatest", "MdAppDetails", "AllLatestVer", new Criteria(Column.getColumn("AllLatest", "APP_ID"), (Object)Column.getColumn("AllLatestVer", "APP_ID"), 0), 1);
            final Join approvedAppToColln = new Join("MdAppCatalogToGroup", "MdAppToCollection", new String[] { "APPROVED_APP_ID" }, new String[] { "APP_ID" }, "MdAppCatalogToGroup", "ApprovedAppToColln", 1);
            final Join approvedAppCollnToReleaseLabelHistory = new Join("MdAppToCollection", "AppGroupToCollection", new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, "ApprovedAppToColln", "ApprovedAppCollnHistory", 1);
            final Column approvedAppLabel = Column.getColumn("ApprovedAppCollnHistory", "RELEASE_LABEL_ID", "ApprovedAppReleaseLabel");
            final Column pubVersionAppId = Column.getColumn("MdAppCatalogToGroup", "PUBLISHED_APP_ID", "MDAPPCATALOGTOGROUP.PUBLISHED_APP_ID");
            final Column approvedAppId = Column.getColumn("MdAppCatalogToGroup", "APPROVED_APP_ID", "MDAPPCATALOGTOGROUP.APPROVED_APP_ID");
            final Column approvedVersionStatus = Column.getColumn("MdAppCatalogToGroup", "APPROVED_VERSION_STATUS", "MDAPPCATALOGTOGROUP.APPROVED_VERSION_STATUS");
            final Column isUpgrade = Column.getColumn("MdAppCatalogToGroup", "IS_UPDATE_AVAILABLE", "MDAPPCATALOGTOGROUP.IS_UPDATE_AVAILABLE");
            final Column pubVersionAppVersion = Column.getColumn("PublishedAppId", "APP_VERSION", "PublishedAppId.APP_VERSION");
            final Column pubVersionAppVersionCode = Column.getColumn("PublishedAppId", "APP_NAME_SHORT_VERSION", "PublishedAppId.APP_NAME_SHORT_VERSION");
            final Column approvedAppVersion = Column.getColumn("ApprovedAppId", "APP_VERSION", "ApprovedAppId.ApprovedVersion");
            final Column approvedAppVersionCode = Column.getColumn("ApprovedAppId", "APP_NAME_SHORT_VERSION", "ApprovedAppId.APP_NAME_SHORT_VERSION");
            final Column lapLatestColumn = Column.getColumn("LaptopLatestVer", "APP_VERSION", "LaptopLatestVer.APP_VERSION").maximum();
            lapLatestColumn.setColumnAlias("LaptopLatestVer.APP_VERSION");
            final Column mobLatestColumn = Column.getColumn("MobLatestVer", "APP_VERSION", "MobileLatestVer.APP_VERSION").maximum();
            mobLatestColumn.setColumnAlias("MobileLatestVer.APP_VERSION");
            final Column allLatestColumn = Column.getColumn("AllLatestVer", "APP_VERSION", "AllLatestVer.APP_VERSION").maximum();
            allLatestColumn.setColumnAlias("AllLatestVer.APP_VERSION");
            sQuery.addJoin(appCollnReleaseLabelToMaxJoin);
            sQuery.addJoin(mdAppCatalogToGrp);
            sQuery.addJoin(pubAppIdOfGrpToAppDetailsJoin);
            sQuery.addJoin(approvedAppIdToAppDetailsJoin);
            sQuery.addJoin(approvedAppToColln);
            sQuery.addJoin(approvedAppCollnToReleaseLabelHistory);
            sQuery.addJoin(AppToCollection);
            sQuery.addJoin(mdPackageJoin);
            sQuery.addJoin(LapLateVer);
            sQuery.addJoin(AppToCollectionm);
            sQuery.addJoin(mdPackageJoinm);
            sQuery.addJoin(mobLateVer);
            sQuery.addJoin(AppToCollectionAll);
            sQuery.addJoin(mdPackageJoinAll);
            sQuery.addJoin(allLateVer);
            sQuery.addSelectColumn(pubVersionAppId);
            sQuery.addSelectColumn(approvedAppId);
            sQuery.addSelectColumn(pubVersionAppVersion);
            sQuery.addSelectColumn(pubVersionAppVersionCode);
            sQuery.addSelectColumn(isUpgrade);
            sQuery.addSelectColumn(approvedAppVersion);
            sQuery.addSelectColumn(approvedVersionStatus);
            sQuery.addSelectColumn(approvedAppVersionCode);
            sQuery.addSelectColumn(approvedAppLabel);
            final List selectColumnList = sQuery.getSelectColumns();
            sQuery.addSelectColumn(lapLatestColumn);
            sQuery.addSelectColumn(mobLatestColumn);
            sQuery.addSelectColumn(allLatestColumn);
            sQuery.setGroupByClause(new GroupByClause(selectColumnList));
            sQuery.setDistinct(true);
        }
        return sQuery;
    }
    
    @Override
    public void setCriteria(final SelectQuery selectQuery, final ViewContext viewCtx) {
        try {
            final HttpServletRequest request = viewCtx.getRequest();
            final String viewName = viewCtx.getUniqueId();
            final long groupId = Long.parseLong(request.getParameter("groupId"));
            final String profileStatusStr = request.getParameter("profileStatus");
            final String packageTypeStr = request.getParameter("packageType");
            int profileStatus = -1;
            int[] profileType = { 1, 10 };
            int packageType = -1;
            int platform = -1;
            final HashMap groupProfileAppViewDetails = new HashMap();
            Criteria cri = new Criteria(Column.getColumn("CustomGroup", "RESOURCE_ID"), (Object)groupId, 0);
            cri = cri.and(new Criteria(Column.getColumn("Profile", "IS_MOVED_TO_TRASH"), (Object)Boolean.FALSE, 0, (boolean)Boolean.FALSE));
            if (profileStatusStr != null && !profileStatusStr.equalsIgnoreCase("all") && !profileStatusStr.equalsIgnoreCase("")) {
                groupProfileAppViewDetails.put("profileStatus", profileStatusStr);
                profileStatus = Integer.valueOf(profileStatusStr);
                final Criteria statusCri = new Criteria(Column.getColumn("GroupToProfileHistory", "COLLECTION_STATUS"), (Object)profileStatus, 0);
                cri = cri.and(statusCri);
            }
            if (packageTypeStr != null && !packageTypeStr.equalsIgnoreCase("all") && !packageTypeStr.equalsIgnoreCase("")) {
                groupProfileAppViewDetails.put("packageType", packageTypeStr);
                packageType = Integer.valueOf(packageTypeStr);
                final Criteria packageTypeCri = MDMUtil.getInstance().getPackageTypeCriteria(packageType);
                cri = cri.and(packageTypeCri);
            }
            final String platformTypeStr = request.getParameter("platform");
            if (platformTypeStr != null && !platformTypeStr.equalsIgnoreCase("all")) {
                platform = Integer.valueOf(platformTypeStr);
                groupProfileAppViewDetails.put("platform", platform);
                final Criteria cPlatform = new Criteria(Column.getColumn("Profile", "PLATFORM_TYPE"), (Object)platform, 0);
                cri = cri.and(cPlatform);
            }
            Criteria typeCri = null;
            if (viewName.equals("mdmGroupApps")) {
                profileType = new int[] { 2 };
            }
            else if (viewName.equals("mdmGroupuOSUpdatePolicy")) {
                profileType = new int[] { 3 };
            }
            typeCri = new Criteria(Column.getColumn("Profile", "PROFILE_TYPE"), (Object)profileType, 8);
            cri = cri.and(typeCri);
            selectQuery.setCriteria(cri);
            super.setCriteria(selectQuery, viewCtx);
            final HashMap packageTypeMap = MDMUtil.getInstance().getAppPackageTypeMap(0);
            groupProfileAppViewDetails.put("groupId", groupId);
            groupProfileAppViewDetails.put("groupType", 6);
            groupProfileAppViewDetails.put("packageTypeMap", packageTypeMap);
            request.setAttribute("groupProfileAppViewDetails", (Object)groupProfileAppViewDetails);
        }
        catch (final Exception e) {
            this.logger.log(Level.WARNING, "Exception occoured in MDMGroupProfileRetrieverAction...", e);
        }
    }
}
