package com.adventnet.sym.webclient.mdm.device;

import javax.servlet.http.HttpServletRequest;
import java.util.logging.Level;
import com.me.mdm.server.role.RBDAUtil;
import com.me.devicemanagement.framework.server.authentication.DMUserHandler;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import com.adventnet.sym.server.mdm.core.ManagedDeviceHandler;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import java.util.HashMap;
import java.util.List;
import com.me.mdm.server.apps.businessstore.MDBusinessStoreUtil;
import com.adventnet.ds.query.Join;
import com.me.mdm.server.apps.multiversion.AppVersionDBUtil;
import com.adventnet.ds.query.Table;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.client.view.web.ViewContext;
import java.util.logging.Logger;
import com.adventnet.sym.webclient.mdm.MDMEmberTableRetrieverAction;

public class MDMDeviceProfileRetrieverAction extends MDMEmberTableRetrieverAction
{
    private Logger logger;
    
    public MDMDeviceProfileRetrieverAction() {
        this.logger = Logger.getLogger(MDMDeviceProfileRetrieverAction.class.getName());
    }
    
    @Override
    protected SelectQuery fetchAndCacheSelectQuery(final ViewContext viewCtx) throws Exception {
        final SelectQuery sQuery = super.fetchAndCacheSelectQuery(viewCtx);
        final Criteria profileTrashCriteria = new Criteria(Column.getColumn("Profile", "IS_MOVED_TO_TRASH"), (Object)Boolean.FALSE, 0, (boolean)Boolean.FALSE);
        final Criteria criteria = (sQuery.getCriteria() != null) ? sQuery.getCriteria().and(profileTrashCriteria) : profileTrashCriteria;
        sQuery.setCriteria(criteria);
        final String viewName = viewCtx.getUniqueId();
        final List<Table> tableList = sQuery.getTableList();
        if (!tableList.contains(Table.getTable("MdAppCatalogToResource")) && viewName.equals("mdmDeviceApps")) {
            final Join appCollnReleaseLabelToMaxJoin = AppVersionDBUtil.getInstance().getJoinForCollectionsLatestAppReleaseLabelFromHistoryTable("AppGroupCollnHistory");
            final Join mdAppCollnJoin = new Join("CollnToResources", "MdAppToCollection", new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, 1);
            final Join mdAppToGrpRel = new Join("MdAppToCollection", "MdAppToGroupRel", new String[] { "APP_ID" }, new String[] { "APP_ID" }, 1);
            final Criteria appGroupCri = new Criteria(Column.getColumn("MdAppToGroupRel", "APP_GROUP_ID"), (Object)Column.getColumn("MdAppCatalogToResource", "APP_GROUP_ID"), 0);
            final Criteria appResCri = new Criteria(Column.getColumn("CollnToResources", "RESOURCE_ID"), (Object)Column.getColumn("MdAppCatalogToResource", "RESOURCE_ID"), 0);
            final Join mdAppCatalogToResource = new Join("MdAppToGroupRel", "MdAppCatalogToResource", appGroupCri.and(appResCri), 1);
            final Join mdAppDetailsPublishedVersionJoin = new Join("MdAppCatalogToResource", "MdAppDetails", new String[] { "PUBLISHED_APP_ID" }, new String[] { "APP_ID" }, "MdAppCatalogToResource", "PublishedAppId", 1);
            final Join mdAppCatalogToResourceScope = new Join("MdAppCatalogToResource", "MdAppCatalogToResourceScope", new String[] { "RESOURCE_ID", "APP_GROUP_ID" }, new String[] { "RESOURCE_ID", "APP_GROUP_ID" }, 1);
            final Join mdAppGroupToResInstalledVersionJoin = new Join("MdAppCatalogToResource", "MdAppDetails", new String[] { "INSTALLED_APP_ID" }, new String[] { "APP_ID" }, "MdAppCatalogToResource", "InstalledAppId", 1);
            final Join mdAppGroupToResApprovedVersionJoin = new Join("MdAppCatalogToResource", "MdAppDetails", new String[] { "APPROVED_APP_ID" }, new String[] { "APP_ID" }, "MdAppCatalogToResource", "ApprovedAppId", 1);
            final Join approvedAppIdToCollnJoin = new Join("MdAppCatalogToResource", "MdAppToCollection", new String[] { "APPROVED_APP_ID" }, new String[] { "APP_ID" }, "MdAppCatalogToResource", "ApprovedAppToColln", 1);
            final Join approvedAppCollnToReleaseLabelHistory = new Join("MdAppToCollection", "AppGroupToCollection", new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, "ApprovedAppToColln", "ApprovedAppCollnHistory", 1);
            final Join ExtnJoin = new Join("MdAppCatalogToResource", "MdAppCatalogToResourceExtn", new String[] { "APP_GROUP_ID", "RESOURCE_ID" }, new String[] { "APP_GROUP_ID", "RESOURCE_ID" }, 1);
            final Column scopeColumn = Column.getColumn("MdAppCatalogToResourceScope", "SCOPE", "SCOPE");
            final Column pubVersionAppId = Column.getColumn("MdAppCatalogToResource", "PUBLISHED_APP_ID", "MDAPPCATALOGTORESOURCE.PUBLISHED_APP_ID");
            final Column installedVersionAppId = Column.getColumn("MdAppCatalogToResource", "INSTALLED_APP_ID", "MDAPPCATALOGTORESOURCE.INSTALLED_APP_ID");
            final Column approvedVersionAppId = Column.getColumn("MdAppCatalogToResource", "APPROVED_APP_ID", "MDAPPCATALOGTORESOURE.APPROVED_APP_ID");
            final Column approvedAppVersionStatus = Column.getColumn("MdAppCatalogToResource", "APPROVED_VERSION_STATUS", "MDAPPCATALOGTORESOURCE.APPROVED_VERSION_STATUS");
            final Column installedVersionColumn = Column.getColumn("InstalledAppId", "APP_VERSION", "InstalledAppId.InstalledAppVersion");
            final Column installedVersionCode = Column.getColumn("InstalledAppId", "APP_NAME_SHORT_VERSION", "InstalledAppId.InstalledAppVersionCode");
            final Column publishedVersionColumn = Column.getColumn("PublishedAppId", "APP_VERSION", "PublishedAppId.PubAppVersion");
            final Column publishedVersionCode = Column.getColumn("PublishedAppId", "APP_NAME_SHORT_VERSION", "PublishedAppId.PubAppVersionCode");
            final Column approvedVersionColumn = Column.getColumn("ApprovedAppId", "APP_VERSION", "ApprovedAppId.ApprovedVersion");
            final Column approvedVersionCode = Column.getColumn("ApprovedAppId", "APP_NAME_SHORT_VERSION", "ApprovedAppId.ApprovedVersionCode");
            final Column isUpdateColumn = Column.getColumn("MdAppCatalogToResourceExtn", "IS_UPDATE_AVAILABLE", "MDAPPCATALOGTORESOURCEEXTN.IS_UPDATE_AVAILABLE");
            final Column approvedAppLabel = Column.getColumn("ApprovedAppCollnHistory", "RELEASE_LABEL_ID", "ApprovedAppReleaseLabel");
            sQuery.addJoin(appCollnReleaseLabelToMaxJoin);
            final Join resToDeploymentConfig = MDBusinessStoreUtil.getLatestDeploymentConfigJoinForRecentProfileForResource();
            final Join reploymentConfigToVPP = new Join(Table.getTable("MDMResourceToDeploymentConfigs"), Table.getTable("MdBusinessStoreToVppRel"), new String[] { "BUSINESSSTORE_ID" }, new String[] { "BUSINESSSTORE_ID" }, 1);
            final Join vppToBSStore = new Join("MdBusinessStoreToVppRel", "MdVPPTokenDetails", new String[] { "TOKEN_ID" }, new String[] { "TOKEN_ID" }, 1);
            sQuery.addJoin(resToDeploymentConfig);
            sQuery.addJoin(reploymentConfigToVPP);
            sQuery.addJoin(vppToBSStore);
            sQuery.addJoin(mdAppCollnJoin);
            sQuery.addJoin(mdAppToGrpRel);
            sQuery.addJoin(mdAppCatalogToResource);
            sQuery.addJoin(mdAppCatalogToResourceScope);
            sQuery.addJoin(mdAppGroupToResInstalledVersionJoin);
            sQuery.addJoin(mdAppDetailsPublishedVersionJoin);
            sQuery.addJoin(mdAppGroupToResApprovedVersionJoin);
            sQuery.addJoin(approvedAppIdToCollnJoin);
            sQuery.addJoin(approvedAppCollnToReleaseLabelHistory);
            sQuery.addJoin(ExtnJoin);
            sQuery.addSelectColumn(scopeColumn);
            sQuery.addSelectColumn(pubVersionAppId);
            sQuery.addSelectColumn(installedVersionAppId);
            sQuery.addSelectColumn(approvedVersionAppId);
            sQuery.addSelectColumn(approvedAppVersionStatus);
            sQuery.addSelectColumn(installedVersionColumn);
            sQuery.addSelectColumn(publishedVersionColumn);
            sQuery.addSelectColumn(approvedVersionColumn);
            sQuery.addSelectColumn(publishedVersionCode);
            sQuery.addSelectColumn(installedVersionCode);
            sQuery.addSelectColumn(approvedVersionCode);
            sQuery.addSelectColumn(isUpdateColumn);
            sQuery.addSelectColumn(approvedAppLabel);
            sQuery.addSelectColumn(new Column("MdVPPTokenDetails", "LOCATION_NAME", "MdVPPTokenDetails.LOCATION_NAME"));
            sQuery.setDistinct(true);
        }
        return sQuery;
    }
    
    @Override
    public void setCriteria(final SelectQuery selectQuery, final ViewContext viewCtx) {
        try {
            final HttpServletRequest request = viewCtx.getRequest();
            final String viewName = viewCtx.getUniqueId();
            final String deviceIdStr = request.getParameter("deviceId");
            final String resourceCollnStatus = request.getParameter("resourceCollnStatus");
            final String packageTypeStr = request.getParameter("packageType");
            int[] profileType = { 1, 10 };
            int packageType = -1;
            Criteria cri = selectQuery.getCriteria();
            Long deviceId = null;
            final HashMap deviceProfileAppViewDetails = new HashMap();
            if (deviceIdStr != null) {
                deviceId = Long.valueOf(deviceIdStr);
                deviceProfileAppViewDetails.put("deviceId", deviceIdStr);
                final Criteria resCri = new Criteria(Column.getColumn("Resource", "RESOURCE_ID"), (Object)deviceId, 0);
                if (cri == null) {
                    cri = resCri;
                }
                else {
                    cri = cri.and(resCri);
                }
            }
            if (resourceCollnStatus != null && !resourceCollnStatus.equals("all")) {
                deviceProfileAppViewDetails.put("statusFilter", resourceCollnStatus);
                final Criteria collnResCri = new Criteria(Column.getColumn("CollnToResources", "STATUS"), (Object)resourceCollnStatus, 0);
                if (cri == null) {
                    cri = collnResCri;
                }
                else {
                    cri = cri.and(collnResCri);
                }
            }
            if (packageTypeStr != null && !packageTypeStr.equalsIgnoreCase("all")) {
                deviceProfileAppViewDetails.put("packageType", packageTypeStr);
                packageType = Integer.valueOf(packageTypeStr);
                final Criteria packageTypeCri = MDMUtil.getInstance().getPackageTypeCriteria(packageType);
                cri = cri.and(packageTypeCri);
            }
            if (viewName.equals("mdmDeviceApps")) {
                profileType = new int[] { 2 };
            }
            final int platformType = ManagedDeviceHandler.getInstance().getPlatformType(deviceId);
            final Criteria typeCri = new Criteria(Column.getColumn("Profile", "PROFILE_TYPE"), (Object)profileType, 8);
            Criteria platformCri;
            if (platformType == 1) {
                platformCri = new Criteria(Column.getColumn("Profile", "PLATFORM_TYPE"), (Object)new Integer[] { 7, 6, 1 }, 8);
            }
            else {
                platformCri = new Criteria(Column.getColumn("Profile", "PLATFORM_TYPE"), (Object)platformType, 0);
            }
            cri = cri.and(typeCri).and(platformCri);
            final Long loginId = ApiFactoryProvider.getAuthUtilAccessAPI().getLoginID();
            if (!DMUserHandler.isUserInRole(loginId, "All_Managed_Mobile_Devices")) {
                selectQuery.addJoin(RBDAUtil.getInstance().getUserDeviceMappingJoin("Resource", "RESOURCE_ID"));
                final Criteria userDeviceCriteria = RBDAUtil.getInstance().getUserDeviceMappingCriteria(loginId);
                cri = cri.and(userDeviceCriteria);
            }
            selectQuery.setCriteria(cri);
            super.setCriteria(selectQuery, viewCtx);
            final HashMap packageTypeMap = MDMUtil.getInstance().getAppPackageTypeMap(platformType);
            deviceProfileAppViewDetails.put("platformType", platformType);
            deviceProfileAppViewDetails.put("packageTypeMap", packageTypeMap);
            request.setAttribute("deviceProfileAppViewDetails", (Object)deviceProfileAppViewDetails);
        }
        catch (final Exception e) {
            this.logger.log(Level.WARNING, "Exception occoured in MDMDeviceProfileRetrieverAction...{0}", e);
        }
    }
}
