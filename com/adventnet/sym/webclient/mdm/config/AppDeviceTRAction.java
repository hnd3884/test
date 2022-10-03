package com.adventnet.sym.webclient.mdm.config;

import org.json.JSONObject;
import java.util.logging.Level;
import java.util.HashMap;
import com.me.mdm.server.apps.multiversion.AppVersionDBUtil;
import javax.servlet.http.HttpServletRequest;
import com.me.devicemanagement.framework.server.customer.CustomerInfoUtil;
import com.adventnet.sym.server.mdm.apps.AppsUtil;
import com.adventnet.sym.server.mdm.util.MDMStringUtils;
import com.adventnet.client.view.web.ViewContext;
import com.me.devicemanagement.framework.webclient.customer.CustomerSummarySqlViewController;
import java.util.logging.Logger;
import com.adventnet.sym.webclient.mdm.MDMEmberSqlViewController;

public class AppDeviceTRAction extends MDMEmberSqlViewController
{
    private Logger logger;
    
    public AppDeviceTRAction() {
        this.logger = Logger.getLogger(CustomerSummarySqlViewController.class.getName());
    }
    
    @Override
    public String getVariableValue(final ViewContext viewCtx, final String variableName) {
        final HttpServletRequest request = viewCtx.getRequest();
        final String installedIn = request.getParameter("installedIn");
        final String appIdStr = request.getParameter("appGroupId");
        final String statusStr = request.getParameter("status");
        final String appPlatform = request.getParameter("platform");
        final String showYetToUpdate = request.getParameter("showYetToUpdate");
        final String sReleaseLabelId = request.getParameter("releaseLabelId");
        final String packageIdStr = request.getParameter("packageId");
        final String groupIdStr = request.getParameter("groupId");
        final String showScheduled = request.getParameter("showScheduled");
        final String appUpdatePolicyId = request.getParameter("policyId");
        Long releaseLabelId = null;
        releaseLabelId = Long.parseLong(sReleaseLabelId);
        if (appPlatform != null) {
            request.setAttribute("appPlatform", (Object)appPlatform);
        }
        else {
            request.setAttribute("appPlatform", (Object)(-1));
        }
        if (showYetToUpdate != null) {
            request.setAttribute("showYetToUpdate", (Object)showYetToUpdate);
        }
        final String licenseType = request.getParameter("appLicenseType");
        if (licenseType != null) {
            request.setAttribute("appLicenseType", (Object)licenseType);
        }
        else {
            request.setAttribute("appLicenseType", (Object)(-1));
        }
        String cri = null;
        cri = super.getVariableValue(viewCtx, variableName);
        if (variableName.equalsIgnoreCase("SCRITERIA")) {
            if (packageIdStr != null) {
                request.setAttribute("packageId", (Object)packageIdStr);
                final Long packageId = Long.parseLong(packageIdStr);
                final String cApp = "MdPackageToAppGroup.PACKAGE_ID=" + packageId;
                if (!MDMStringUtils.isEmpty(cri)) {
                    cri = cri + " and " + cApp;
                }
                else {
                    cri = cApp;
                }
            }
            if (statusStr != null && !statusStr.equalsIgnoreCase("all")) {
                request.setAttribute("status", (Object)statusStr);
                final Integer status = Integer.parseInt(statusStr);
                String cStatus = "";
                if (status == 0) {
                    cStatus = "MdAppCatalogToResource.STATUS in ( 0 , 5, 6 )";
                }
                else {
                    cStatus = "MdAppCatalogToResource.STATUS=" + status;
                }
                if (cri != null) {
                    cri = cri + " and " + cStatus;
                }
            }
            if (installedIn != null && !installedIn.equals("-1") && !installedIn.equals("all")) {
                request.setAttribute("installedIn", (Object)installedIn);
                final Integer installedScope = Integer.parseInt(installedIn);
                String installScopeCri = null;
                if (installedIn.equals("0")) {
                    installScopeCri = "MdAppCatalogToResourceScope.SCOPE=" + installedScope;
                }
                else if (installedIn.equals("1")) {
                    installScopeCri = "MdAppCatalogToResourceScope.SCOPE=" + installedScope;
                }
                if (cri != null && installScopeCri != null) {
                    cri = cri + " and " + installScopeCri;
                }
            }
            if (packageIdStr != null && !showYetToUpdate.equals("all") && showYetToUpdate.equalsIgnoreCase("true")) {
                final String yetToUpdateDevicecri = "MdAppCatalogToResourceExtn.IS_UPDATE_AVAILABLE='" + showYetToUpdate.toLowerCase() + "'";
                final String notScheduledCriteria = "MdAppCatalogToResource.APPROVED_VERSION_STATUS=1";
                cri = cri + " and " + yetToUpdateDevicecri + " and " + notScheduledCriteria;
            }
            if (packageIdStr != null && showScheduled != null && !showScheduled.equalsIgnoreCase("all") && showScheduled.equalsIgnoreCase("true")) {
                final String yetToUpdateDeviceCri = "MdAppCatalogToResourceExtn.IS_UPDATE_AVAILABLE='" + "TRUE".toLowerCase() + "'";
                final String scheduledCriteria = "MdAppCatalogToResource.APPROVED_VERSION_STATUS=2";
                cri = cri + " and " + yetToUpdateDeviceCri + " and " + scheduledCriteria;
            }
        }
        if (variableName.equals("RELEASELABELCRITERIA")) {
            cri = " and AppGroupCollnHistory.RELEASE_LABEL_ID = " + releaseLabelId;
        }
        if (variableName.equals("GROUPIDCRITERIA") && !groupIdStr.equals("all") && groupIdStr != null) {
            final Long groupId = Long.parseLong(groupIdStr);
            cri = "AND CustomGroupMemberRel.GROUP_RESOURCE_ID = " + groupId;
        }
        if (variableName.equals("BUSINESSSTORE_JOIN")) {
            final String businessStoreIDStr = request.getParameter("businessstore_id");
            if (businessStoreIDStr != null) {
                final Long packagerID = Long.parseLong(packageIdStr);
                final Long profileId = AppsUtil.getInstance().getProfileIdForPackage(packagerID, CustomerInfoUtil.getInstance().getCustomerId());
                final Long businessStoreID = Long.parseLong(businessStoreIDStr);
                cri = "INNER JOIN (SELECT RESOURCE_ID AS \"m2.resource_id\", PROFILE_ID AS \"m2.profile_id\", BUSINESSSTORE_ID AS \"m2.businessstore_id\" FROM MDMRESOURCETODEPLOYMENTCONFIGS \"m2\"  INNER JOIN (SELECT MAX(ADDED_TIME) AS \"m1.added_time\", RESOURCE_ID AS \"m1.resource_id\", PROFILE_ID AS \"m1.profile_id\" FROM MDMRESOURCETODEPLOYMENTCONFIGS WHERE BUSINESSSTORE_ID =" + businessStoreID + " AND PROFILE_ID = " + profileId + " GROUP BY PROFILE_ID, RESOURCE_ID) \"m1\"" + "  ON RESOURCE_ID=\"m1.resource_id\" AND PROFILE_ID=\"m1.profile_id\" AND ADDED_TIME=\"m1.added_time\") \"latestdepconfig\" ON \"latestdepconfig\".\"m2.resource_id\"=RESOURCE.RESOURCE_ID";
            }
        }
        if (variableName.equals("UPDATEPOLICYCRITERIA") && appUpdatePolicyId != null && !appUpdatePolicyId.equals("all")) {
            final Long appUpdatePolicyID = Long.parseLong(appUpdatePolicyId);
            cri = "AND Profile.PROFILE_ID = " + appUpdatePolicyID;
        }
        return cri;
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
            this.logger.log(Level.SEVERE, "Exception in AppDeviceTRAction post model fetch", ex);
        }
    }
}
