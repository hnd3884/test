package com.adventnet.sym.webclient.mdm.config;

import com.adventnet.sym.server.mdm.apps.AppsUtil;
import com.me.devicemanagement.framework.server.util.Utils;
import com.me.mdm.webclient.transformer.TransformerUtil;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.me.mdm.server.apps.multiversion.AppVersionDBUtil;
import com.me.devicemanagement.framework.server.authentication.DMUserHandler;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.logging.Level;
import com.adventnet.i18n.I18N;
import javax.servlet.http.HttpServletRequest;
import com.adventnet.client.view.web.ViewContext;
import com.me.devicemanagement.framework.server.util.DBUtil;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import com.me.mdm.server.util.MDMFeatureParamsHandler;
import com.adventnet.client.components.web.TransformerContext;
import java.util.logging.Logger;
import com.adventnet.client.components.table.web.DefaultTransformer;

public class AppRepositoryTransformer extends DefaultTransformer
{
    private static Logger logger;
    
    protected boolean checkIfColumnRendererable(final TransformerContext tableContext) throws Exception {
        final String columnalias = tableContext.getPropertyName();
        final int reportType = tableContext.getViewContext().getRenderType();
        boolean isExport = Boolean.FALSE;
        if (reportType != 4) {
            isExport = Boolean.TRUE;
        }
        if (columnalias.equalsIgnoreCase("checkbox_column") || columnalias.equalsIgnoreCase("MdAppGroupDetails.APP_GROUP_ID") || columnalias.equalsIgnoreCase("Checkbox")) {
            final ViewContext vc = tableContext.getViewContext();
            final String viewname = vc.getUniqueId();
            final HttpServletRequest request = vc.getRequest();
            final boolean hasAppMgmtAdminPrivillage = request.isUserInRole("MDM_AppMgmt_Admin") || request.isUserInRole("ModernMgmt_AppMgmt_Admin");
            if (viewname.equalsIgnoreCase("appRepository")) {
                boolean isRestrictedAppListAppWriteRole = Boolean.FALSE;
                if (!hasAppMgmtAdminPrivillage) {
                    final Boolean showOnlyUserCreatedProfilesApps = MDMFeatureParamsHandler.getInstance().isFeatureEnabled("showOnlyUserCreatedProfilesApps");
                    if (showOnlyUserCreatedProfilesApps != null && showOnlyUserCreatedProfilesApps) {
                        isRestrictedAppListAppWriteRole = ((request.isUserInRole("MDM_AppMgmt_Write") || request.isUserInRole("ModernMgmt_AppMgmt_Write")) && showOnlyUserCreatedProfilesApps);
                    }
                }
                return hasAppMgmtAdminPrivillage || isRestrictedAppListAppWriteRole;
            }
            final boolean hasAppMgmtWritePrivillage = ApiFactoryProvider.getAuthUtilAccessAPI().getRoles().contains("MDM_AppMgmt_Write") || ApiFactoryProvider.getAuthUtilAccessAPI().getRoles().contains("ModernMgmt_AppMgmt_Write");
            return hasAppMgmtWritePrivillage;
        }
        else if (columnalias.equalsIgnoreCase("Resource.RESOURCE_ID")) {
            final ViewContext vc = tableContext.getViewContext();
            final HttpServletRequest request2 = vc.getRequest();
            if (isExport) {
                return false;
            }
            final boolean hasAppMgmtAdminPrivillage2 = request2.isUserInRole("MDM_AppMgmt_Admin") || request2.isUserInRole("ModernMgmt_AppMgmt_Admin");
            final boolean hasAppMgmtWritePrivillage2 = request2.isUserInRole("MDM_AppMgmt_Write") || request2.isUserInRole("ModernMgmt_AppMgmt_Write");
            return hasAppMgmtAdminPrivillage2 || hasAppMgmtWritePrivillage2;
        }
        else {
            if (columnalias.equalsIgnoreCase("MdAppCatalogToResourceScope.SCOPE")) {
                final ViewContext vc = tableContext.getViewContext();
                final String viewname = vc.getUniqueId();
                final HttpServletRequest request = vc.getRequest();
                final Long packageId = Long.valueOf(request.getParameter("packageId"));
                final Integer platform = (Integer)DBUtil.getValueFromDB("MdPackage", "PACKAGE_ID", (Object)packageId, "PLATFORM_TYPE");
                return platform == 2;
            }
            if (columnalias.equalsIgnoreCase("Profile.POLICY_NAME")) {
                return MDMFeatureParamsHandler.getInstance().isFeatureEnabled("EnableScheduleAppUpdates");
            }
            return super.checkIfColumnRendererable(tableContext);
        }
    }
    
    public void renderHeader(final TransformerContext tableContext) {
        super.renderHeader(tableContext);
        final HashMap headerProperties = tableContext.getRenderedAttributes();
        final String columnalais = tableContext.getPropertyName();
        final ViewContext viewCtx = tableContext.getViewContext();
        final int renderType = viewCtx.getRenderType();
        final int reportType = tableContext.getViewContext().getRenderType();
        boolean isExport = Boolean.FALSE;
        if (reportType != 4) {
            isExport = Boolean.TRUE;
        }
        final String checkbox = tableContext.getDisplayName();
        try {
            if (checkbox.equals(I18N.getMsg("dc.common.CHECKBOX_COLUMN", new Object[0]))) {
                String checkAll = "";
                if (!isExport) {
                    checkAll = "<input type=\"checkbox\" id=\"selectAll\" value=\"SelectAll\" name=\"selectcheckbox\" onclick=\"javascript:selectAllObjects(this.checked)\">";
                }
                headerProperties.put("VALUE", checkAll);
            }
        }
        catch (final Exception e) {
            AppRepositoryTransformer.logger.log(Level.WARNING, "Exception in AppRepositoryTransformer renderHeader", e);
        }
    }
    
    public void renderCell(final TransformerContext tableContext) throws Exception {
        super.renderCell(tableContext);
        final ViewContext vc = tableContext.getViewContext();
        final String viewname = vc.getUniqueId();
        final HashMap columnProperties = tableContext.getRenderedAttributes();
        final String columnalais = tableContext.getPropertyName();
        final int reportType = tableContext.getViewContext().getRenderType();
        boolean isExport = Boolean.FALSE;
        if (reportType != 4) {
            isExport = Boolean.TRUE;
        }
        final Long packageId = (Long)tableContext.getAssociatedPropertyValue("MdPackage.PACKAGE_ID");
        final Long publishedAppId = (Long)tableContext.getAssociatedPropertyValue("MdAppCatalogToResource.PUBLISHED_APP_ID");
        final Long installedAppId = (Long)tableContext.getAssociatedPropertyValue("MdAppCatalogToResource.INSTALLED_APP_ID");
        final String installedAppVersion = (String)tableContext.getAssociatedPropertyValue("InstalledAppDetails.APP_VERSION");
        final Boolean isUpdateAvailable = (Boolean)tableContext.getAssociatedPropertyValue("MdAppCatalogToResourceExtn.IS_UPDATE_AVAILABLE");
        final Integer countOfAppReleaseLabels = (Integer)tableContext.getAssociatedPropertyValue("AppGroupToCollection.RELEASE_LABEL_ID.count");
        final Integer splitApkCount = (Integer)tableContext.getAssociatedPropertyValue("BusinessStoreAppVersion.APP_GROUP_ID.count");
        Object data = tableContext.getPropertyValue();
        if (columnalais.equalsIgnoreCase("Checkbox")) {
            final Integer appSharedScope = (Integer)tableContext.getAssociatedPropertyValue("MdPackage.APP_SHARED_SCOPE");
            final Boolean isForAllCustomers = appSharedScope != null && appSharedScope == 1;
            final JSONObject payloadData = new JSONObject();
            final Long loginId = ApiFactoryProvider.getAuthUtilAccessAPI().getLoginID();
            if (isForAllCustomers) {
                payloadData.put("isDisabled", !DMUserHandler.isUserInAdminRole(loginId));
            }
            else {
                payloadData.put("isDisabled", false);
            }
            if (MDMFeatureParamsHandler.getInstance().isFeatureEnabled("SyncConfigurationsForAllCustomers")) {
                payloadData.put("isUserInRole", DMUserHandler.isUserInAdminRole(loginId));
            }
            else {
                payloadData.put("isUserInRole", true);
            }
            payloadData.put("is_for_all_customers", (Object)isForAllCustomers);
            columnProperties.put("PAYLOAD", payloadData);
        }
        if (columnalais.equals("MdAppGroupDetails.APP_GROUP_ID")) {
            final Long appGroupId = (Long)data;
            final int packageType = (int)tableContext.getAssociatedPropertyValue("MdPackageToAppGroup.PACKAGE_TYPE");
            final int privateApp = (int)tableContext.getAssociatedPropertyValue("MdPackageToAppGroup.PRIVATE_APP_TYPE");
            final int platformType = (int)tableContext.getAssociatedPropertyValue("MdPackage.PLATFORM_TYPE");
            final String appName = (String)tableContext.getAssociatedPropertyValue("MdAppGroupDetails.GROUP_DISPLAY_NAME");
            final Boolean isAppPurchasedFromPortal = (Boolean)tableContext.getAssociatedPropertyValue("MdPackageToAppGroup.IS_PURCHASED_FROM_PORTAL");
            final Integer appSharedScope2 = (Integer)tableContext.getAssociatedPropertyValue("MdPackage.APP_SHARED_SCOPE");
            final Boolean isForAllCustomers2 = appSharedScope2 == 1;
            int appType = 0;
            if (platformType == 1 && (packageType == 0 || packageType == 1)) {
                appType = 0;
            }
            else if (platformType == 1 && packageType == 2) {
                appType = 1;
            }
            else if (platformType == 2 && privateApp == 1) {
                appType = 10;
            }
            else if (platformType == 2 && (packageType == 0 || packageType == 1)) {
                appType = 2;
            }
            else if (platformType == 2 && packageType == 2) {
                appType = 3;
            }
            else if (platformType == 3 && (packageType == 0 || packageType == 1)) {
                appType = 4;
            }
            else if (platformType == 3 && packageType == 2) {
                final String appFileLoc = (String)tableContext.getAssociatedPropertyValue("MdPackageToAppData.APP_FILE_LOC");
                if (appFileLoc != null && appFileLoc.toLowerCase().endsWith(".msi")) {
                    appType = 6;
                }
                else {
                    appType = 5;
                }
            }
            else if (platformType == 4 && packageType == 2) {
                appType = 9;
            }
            else if (platformType == 4 && (packageType == 0 || packageType == 1)) {
                appType = 8;
            }
            final JSONObject payloadData2 = new JSONObject();
            if (platformType == 3 && packageType == 2) {
                final String appFileLoc2 = (String)tableContext.getAssociatedPropertyValue("MdPackageToAppData.APP_FILE_LOC");
                if (appFileLoc2 != null && appFileLoc2.toLowerCase().endsWith(".msi")) {
                    payloadData2.put("msiApp", true);
                }
            }
            payloadData2.put("appType", appType);
            payloadData2.put("appGroupId", (Object)appGroupId.toString());
            payloadData2.put("appId", (Object)packageId.toString());
            payloadData2.put("packageType", packageType);
            payloadData2.put("platformType", platformType);
            payloadData2.put("appName", (Object)appName);
            payloadData2.put("countOfAppReleaseLabels", (Object)countOfAppReleaseLabels);
            payloadData2.put("isAppPurchasedFromPortal", (Object)isAppPurchasedFromPortal);
            payloadData2.put("releaseLabels", (Object)AppVersionDBUtil.getInstance().convertMapOfReleaseLabelToJSONArray(AppVersionDBUtil.getInstance().getAvailableReleaseLabelsForSpecificPackage(packageId)));
            if (MDMFeatureParamsHandler.getInstance().isFeatureEnabled("SyncConfigurationsForAllCustomers")) {
                final Long loginId2 = ApiFactoryProvider.getAuthUtilAccessAPI().getLoginID();
                payloadData2.put("isUserInRole", DMUserHandler.isUserInAdminRole(loginId2));
            }
            else {
                payloadData2.put("isUserInRole", true);
            }
            payloadData2.put("is_for_all_customers", (Object)isForAllCustomers2);
            columnProperties.put("PAYLOAD", payloadData2);
        }
        if (columnalais.equals("MdPackageToAppGroup.PACKAGE_TYPE")) {
            final int platformType2 = (int)tableContext.getAssociatedPropertyValue("MdPackage.PLATFORM_TYPE");
            final int privateApp2 = (int)tableContext.getAssociatedPropertyValue("MdPackageToAppGroup.PRIVATE_APP_TYPE");
            if (platformType2 == 1 && ((int)data == 0 || (int)data == 1)) {
                columnProperties.put("VALUE", I18N.getMsg("dc.mdm.actionlog.appmgmt.appStoreApp", new Object[0]));
            }
            else if (platformType2 == 1 && (int)data == 2) {
                columnProperties.put("VALUE", I18N.getMsg("dc.mdm.actionlog.appmgmt.enterpriseApp", new Object[0]));
            }
            else if (platformType2 == 2 && privateApp2 == 1) {
                columnProperties.put("VALUE", I18N.getMsg("mdm.apps.android.store_private_app", new Object[0]));
            }
            else if (platformType2 == 2 && ((int)data == 0 || (int)data == 1)) {
                columnProperties.put("VALUE", I18N.getMsg("mdm.apps.android.store_app", new Object[0]));
            }
            else if (platformType2 == 2 && (int)data == 2) {
                columnProperties.put("VALUE", I18N.getMsg("dc.mdm.actionlog.appmgmt.android_enterpriseApp", new Object[0]));
            }
            else if (platformType2 == 3 && ((int)data == 0 || (int)data == 1)) {
                columnProperties.put("VALUE", I18N.getMsg("dc.mdm.actionlog.appmgmt.windows_businessStoreApp", new Object[0]));
            }
            else if (platformType2 == 3 && (int)data == 2) {
                final String appFileLoc3 = (String)tableContext.getAssociatedPropertyValue("MdPackageToAppData.APP_FILE_LOC");
                String pkgTypeValue = "dc.mdm.actionlog.appmgmt.windows_enterpriseApp";
                if (appFileLoc3 != null && appFileLoc3.toLowerCase().endsWith(".msi")) {
                    pkgTypeValue = "mdm.appmgmt.windows.msi_apps";
                }
                columnProperties.put("VALUE", I18N.getMsg(pkgTypeValue, new Object[0]));
            }
            else if (platformType2 == 4 && ((int)data == 0 || (int)data == 1)) {
                columnProperties.put("VALUE", I18N.getMsg("Chrome Store App", new Object[0]));
            }
            else if (platformType2 == 4 && (int)data == 2) {
                columnProperties.put("VALUE", I18N.getMsg("Chrome Custom App", new Object[0]));
            }
        }
        if (reportType != 4 && columnalais.equals("Profile.PLATFORM_TYPE")) {
            String platformName = I18N.getMsg("dc.common.UNKNOWN", new Object[0]);
            final Integer platformType3 = (Integer)data;
            if (platformType3 == 1) {
                platformName = I18N.getMsg("dc.mdm.ios", new Object[0]);
            }
            else if (platformType3 == 2) {
                platformName = I18N.getMsg("dc.mdm.android", new Object[0]);
            }
            else if (platformType3 == 3) {
                platformName = I18N.getMsg("dc.common.WINDOWS", new Object[0]);
            }
            else if (platformType3 == 4) {
                platformName = I18N.getMsg("mdm.common.chrome", new Object[0]);
            }
            columnProperties.put("VALUE", platformName);
        }
        if (columnalais.equals("MdPackageToAppGroup.IS_PAID_APP") && data != null) {
            if (data) {
                columnProperties.put("VALUE", I18N.getMsg("dc.inv.common.Paid", new Object[0]));
            }
            else if (!(boolean)data) {
                columnProperties.put("VALUE", I18N.getMsg("dc.inv.common.Free", new Object[0]));
            }
        }
        if (columnalais.equals("MdPackageToAppData.SUPPORTED_DEVICES") && data != null) {
            final int platformType2 = (int)tableContext.getAssociatedPropertyValue("MdPackage.PLATFORM_TYPE");
            final String value = AppRepositoryUtil.getInstance().getSupportedDevices(platformType2, data);
            columnProperties.put("VALUE", value);
        }
        if (columnalais.equals("AppCategory.APP_CATEGORY_LABEL")) {
            columnProperties.put("VALUE", I18N.getMsg((String)data, new Object[0]));
        }
        if (columnalais.equals("MdPackageToAppGroup.IS_PURCHASED_FROM_PORTAL") && data != null) {
            final int platformType2 = (int)tableContext.getAssociatedPropertyValue("MdPackage.PLATFORM_TYPE");
            final int packageType = (int)tableContext.getAssociatedPropertyValue("MdPackageToAppGroup.PACKAGE_TYPE");
            if (platformType2 == 1) {
                if (data) {
                    columnProperties.put("VALUE", I18N.getMsg("mdm.common.vppApps", new Object[0]));
                }
                else if (!(boolean)data && packageType == 2) {
                    columnProperties.put("VALUE", I18N.getMsg("mdm.app.inHouseApps", new Object[0]));
                }
                else if (!(boolean)data) {
                    columnProperties.put("VALUE", I18N.getMsg("dc.conf.syspref.app_Store", new Object[0]));
                }
            }
            else if (platformType2 == 2) {
                if (data) {
                    columnProperties.put("VALUE", I18N.getMsg("dc.mdm.android.managed_google_play", new Object[0]));
                }
                else if (!(boolean)data && packageType == 2) {
                    columnProperties.put("VALUE", I18N.getMsg("mdm.app.inHouseApps", new Object[0]));
                }
                else if (!(boolean)data) {
                    columnProperties.put("VALUE", I18N.getMsg("dc.mdm.inv.allow_android_market", new Object[0]));
                }
            }
            else if (platformType2 == 3) {
                if (data) {
                    columnProperties.put("VALUE", I18N.getMsg("mdm.app.bstoreApps", new Object[0]));
                }
                else if (!(boolean)data) {
                    columnProperties.put("VALUE", I18N.getMsg("mdm.app.inHouseApps", new Object[0]));
                }
            }
            else if (platformType2 == 4) {
                if (!(boolean)data && packageType == 2) {
                    columnProperties.put("VALUE", I18N.getMsg("mdm.app.inHouseApps", new Object[0]));
                }
                else if (!(boolean)data) {
                    columnProperties.put("VALUE", I18N.getMsg("mdm.app.chromeStoreApps", new Object[0]));
                }
            }
        }
        if (columnalais.equals("MdAppCatalogToResource.STATUS")) {
            final JSONObject payloadData3 = new JSONObject();
            String statusLabel = "";
            String statusStr = "";
            String className = "";
            if (!isExport && isUpdateAvailable) {
                final Integer associatedAppVersionStatus = (Integer)tableContext.getAssociatedPropertyValue("MdAppCatalogToResource.APPROVED_VERSION_STATUS");
                if (associatedAppVersionStatus == 2) {
                    statusLabel = I18N.getMsg("dc.mdm.appmgmt.app_update_scheduled", new Object[0]);
                    className = "ucs-table-status-text__ready";
                    payloadData3.put("statusLabel", (Object)statusLabel);
                    payloadData3.put("styleClass", (Object)className);
                    columnProperties.put("PAYLOAD", payloadData3);
                }
                else {
                    statusLabel = I18N.getMsg("dc.mdm.appmgmt.yet_to_distribute_updates", new Object[0]);
                    className = "ucs-table-status-text__ready";
                    payloadData3.put("statusLabel", (Object)statusLabel);
                    payloadData3.put("styleClass", (Object)className);
                    columnProperties.put("PAYLOAD", payloadData3);
                }
            }
            else if (data != null) {
                switch ((int)data) {
                    case 0: {
                        statusLabel = "dc.db.som.status.yet_to_install";
                        if (installedAppId != null && !publishedAppId.equals(installedAppId)) {
                            statusLabel = "dc.common.YET_TO_UPDATE";
                        }
                        className = "ucs-table-status-text__ready";
                        break;
                    }
                    case 1: {
                        statusLabel = "dc.db.config.status.in_progress";
                        className = "ucs-table-status-text__in-progress";
                        break;
                    }
                    case 2: {
                        statusLabel = "dc.common.INSTALLED";
                        className = "ucs-table-status-text__success";
                        break;
                    }
                    case 3: {
                        statusLabel = "desktopcentral.admin.som.allSomComputersRead.Uninstalled";
                        className = "ucs-table-status-text__failed";
                        break;
                    }
                    case 4: {
                        statusLabel = "dc.db.som.status.uninstallation_failed";
                        className = "ucs-table-status-text__failed";
                        break;
                    }
                    case 5:
                    case 6: {
                        statusLabel = "dc.db.som.status.yet_to_install";
                        className = "ucs-table-status-text__ready";
                        break;
                    }
                    case 7: {
                        statusLabel = "dc.db.config.status.failed";
                        className = "ucs-table-status-text__failed";
                        break;
                    }
                }
                statusStr = I18N.getMsg(statusLabel, new Object[0]);
                if (!isExport) {
                    payloadData3.put("statusLabel", (Object)statusStr);
                    payloadData3.put("styleClass", (Object)className);
                    columnProperties.put("PAYLOAD", payloadData3);
                }
                else {
                    columnProperties.put("VALUE", statusStr);
                }
            }
        }
        if (columnalais.equalsIgnoreCase("MdModelInfo.MODEL_TYPE") && data != null) {
            final int modelType = (int)data;
            final String modelTypeName = MDMUtil.getInstance().getModelTypeName(modelType);
            columnProperties.put("VALUE", modelTypeName);
        }
        if (columnalais.equalsIgnoreCase("MdAppCatalogToResourceScope.SCOPE")) {
            if (data == null) {
                columnProperties.put("VALUE", I18N.getMsg("dc.common.DEVICE", new Object[0]));
            }
            else {
                final int installedIn = (int)data;
                columnProperties.put("VALUE", (installedIn == 1) ? I18N.getMsg("dc.mdm.android.knox.container", new Object[0]) : I18N.getMsg("dc.common.DEVICE", new Object[0]));
            }
        }
        if (columnalais.equals("Resource.RESOURCE_ID")) {
            final Long resourceId = (Long)data;
            final JSONObject payloadData4 = new JSONObject();
            payloadData4.put("updateAvailable", (Object)((isUpdateAvailable != null) ? isUpdateAvailable : Boolean.FALSE));
            payloadData4.put("RESOURCE_ID", (Object)resourceId.toString());
            columnProperties.put("PAYLOAD", payloadData4);
        }
        if (columnalais.equals("Profile.PROFILE_NAME") || columnalais.equals("MdAppDetails.APP_VERSION")) {
            final HashMap hm = new HashMap();
            final JSONObject payloadData4 = new JSONObject();
            final String imageLoc = (String)tableContext.getAssociatedPropertyValue("MdPackageToAppData.DISPLAY_IMAGE_LOC");
            final Integer appSharedScope3 = (Integer)tableContext.getAssociatedPropertyValue("MdPackage.APP_SHARED_SCOPE");
            String icon = null;
            if (imageLoc != null) {
                hm.put("path", tableContext.getAssociatedPropertyValue("MdPackageToAppData.DISPLAY_IMAGE_LOC"));
                hm.put("IS_SERVER", true);
                hm.put("IS_AUTHTOKEN", false);
                hm.put("isApi", true);
                icon = ApiFactoryProvider.getFileAccessAPI().constructFileURL(hm);
            }
            final int platformType4 = (int)tableContext.getAssociatedPropertyValue("MdPackage.PLATFORM_TYPE");
            final Long appGroupId2 = (Long)tableContext.getAssociatedPropertyValue("MdAppGroupDetails.APP_GROUP_ID");
            if (platformType4 == 3) {
                final boolean isPortalPurchased = (boolean)tableContext.getAssociatedPropertyValue("MdPackageToAppGroup.IS_PURCHASED_FROM_PORTAL");
                if (isPortalPurchased) {
                    String backgroundColor = (String)tableContext.getAssociatedPropertyValue("WindowsAppDetails.IMG_BG");
                    if (backgroundColor == null || backgroundColor.equals("transparent")) {
                        backgroundColor = "#0078d7";
                    }
                    payloadData4.put("backgroundColor", (Object)backgroundColor);
                }
            }
            String cellContent = "apps";
            if (columnalais.equals("MdAppDetails.APP_VERSION")) {
                if (data == null || (data != null && data.equals(""))) {
                    columnProperties.put("VALUE", "--");
                }
                else {
                    columnProperties.put("VALUE", data);
                }
                cellContent = "version";
            }
            final Boolean isForAllCustomers3 = appSharedScope3 == 1;
            if (MDMFeatureParamsHandler.getInstance().isFeatureEnabled("SyncConfigurationsForAllCustomers")) {
                final Long loginId3 = ApiFactoryProvider.getAuthUtilAccessAPI().getLoginID();
                payloadData4.put("isUserInRole", DMUserHandler.isUserInAdminRole(loginId3));
            }
            else {
                payloadData4.put("isUserInRole", true);
            }
            payloadData4.put("cellContent", (Object)cellContent);
            payloadData4.put("appGroupId", (Object)appGroupId2.toString());
            payloadData4.put("appId", (Object)packageId.toString());
            payloadData4.put("icon", (Object)icon);
            payloadData4.put("platformType", platformType4);
            payloadData4.put("countOfAppReleaseLabels", (Object)countOfAppReleaseLabels);
            payloadData4.put("releaseLabels", (Object)AppVersionDBUtil.getInstance().convertMapOfReleaseLabelToJSONArray(AppVersionDBUtil.getInstance().getAvailableReleaseLabelsForSpecificPackage(packageId)));
            payloadData4.put("isSplitApk", splitApkCount != null && splitApkCount > 1);
            payloadData4.put("is_for_all_customers", (Object)isForAllCustomers3);
            if (reportType != 4) {
                columnProperties.put("VALUE", data);
            }
            else {
                columnProperties.put("PAYLOAD", payloadData4);
            }
        }
        if (columnalais.equals("CollnToResources.STATUS")) {
            final String statusLabel2 = (String)tableContext.getAssociatedPropertyValue("ConfigStatusDefn.STATUS_LABEL");
            if (!isUpdateAvailable) {
                final String statusStr2 = I18N.getMsg(statusLabel2, new Object[0]);
                columnProperties.put("VALUE", statusStr2);
            }
            else {
                final String statusStr2 = I18N.getMsg("dc.common.YET_TO_UPDATE", new Object[0]);
                columnProperties.put("VALUE", statusStr2);
            }
        }
        if (columnalais.equals("MdAppCatalogToResource.REMARKS") || columnalais.equals("CollnToResources.REMARKS")) {
            if (!isUpdateAvailable) {
                final Integer statusID = (Integer)tableContext.getAssociatedPropertyValue("CollnToResources.STATUS");
                final Boolean isErr = statusID != null && (statusID == 7 || statusID == 11 || statusID == 8);
                TransformerUtil.renderRemarksAsText(tableContext, columnProperties, (String)data, isErr, isExport);
                if (columnProperties.containsKey("PAYLOAD")) {
                    final JSONObject jsonObject = columnProperties.get("PAYLOAD");
                    if (jsonObject.has("VALUE")) {
                        final String value2 = jsonObject.getString("VALUE");
                        if (value2.contains("@@@<l>")) {
                            jsonObject.put("VALUE", (Object)MDMUtil.replaceProductUrlLoaderValuesinText(value2, "AppDistribtionPage"));
                        }
                    }
                    if (jsonObject.has("READKB")) {
                        final String readKB = jsonObject.getString("READKB");
                        if (readKB != null) {
                            jsonObject.put("READKB", (Object)MDMUtil.replaceProductUrlLoaderValuesinText(readKB, "AppDistribtionPage"));
                        }
                    }
                    jsonObject.put("isUpdateAvailable", (Object)isUpdateAvailable);
                }
            }
            else if (isExport) {
                columnProperties.put("VALUE", I18N.getMsg("mdm.apps.status.dist_ondemand", new Object[0]));
            }
            else {
                final Integer associatedAppStatus = (Integer)tableContext.getAssociatedPropertyValue("MdAppCatalogToResource.APPROVED_VERSION_STATUS");
                final JSONObject payloadData4 = new JSONObject();
                if (associatedAppStatus == 2) {
                    final Object nextUpdateTimeObject = (tableContext.getAssociatedPropertyValue("AppUpdatePolicy.NEXT_EXECUTION_TIME") == null) ? Long.valueOf(-1L) : tableContext.getAssociatedPropertyValue("AppUpdatePolicy.NEXT_EXECUTION_TIME");
                    payloadData4.put("VALUE", (Object)I18N.getMsg("dc.mdm.appmgtm.app_updated_scheduled_time", new Object[] { Utils.getTime((Long)nextUpdateTimeObject) }));
                }
                else {
                    payloadData4.put("VALUE", (Object)I18N.getMsg("mdm.apps.status.dist_ondemand", new Object[0]));
                }
                payloadData4.put("isUpdateAvailable", (Object)isUpdateAvailable);
                columnProperties.put("PAYLOAD", payloadData4);
            }
        }
        if (columnalais.equals("RecentProfileForResource.MARKED_FOR_DELETE")) {
            String statusStr3 = "";
            if (data != null) {
                if (data == Boolean.TRUE) {
                    statusStr3 = I18N.getMsg("dc.mdm.group.view.app_removal", new Object[0]);
                }
                else {
                    statusStr3 = I18N.getMsg("dc.mdm.group.view.app_installation", new Object[0]);
                }
            }
            columnProperties.put("VALUE", statusStr3);
        }
        if (columnalais.equals("Resource.RESOURCE_ID")) {
            Boolean isUpgradeAvailable = (Boolean)TransformerUtil.getPreValuesForTransformer(vc, "is_upgrade_available");
            isUpgradeAvailable = ((isUpgradeAvailable == null) ? Boolean.FALSE : isUpgradeAvailable);
            Boolean isDowngradeAvailable = (Boolean)TransformerUtil.getPreValuesForTransformer(vc, "is_downgrade_available");
            isDowngradeAvailable = ((isDowngradeAvailable == null) ? Boolean.FALSE : isDowngradeAvailable);
            final JSONObject payloadData = new JSONObject();
            payloadData.put("resource_id", (Object)data.toString());
            payloadData.put("is_upgrade_available", (Object)isUpgradeAvailable);
            payloadData.put("is_downgrade_available", (Object)isDowngradeAvailable);
            columnProperties.put("PAYLOAD", payloadData);
        }
        if (columnalais.equals("PublishedAppDetails.APP_VERSION")) {
            if (data != null) {
                data = AppsUtil.getValidVersion(data.toString());
            }
            if (isExport) {
                columnProperties.put("VALUE", data);
            }
            else {
                JSONObject payloadJSON = new JSONObject();
                final Integer appCatalogStatus = (Integer)tableContext.getAssociatedPropertyValue("MdAppCatalogToResource.STATUS");
                if (isUpdateAvailable && (appCatalogStatus == 1 || appCatalogStatus == 0 || appCatalogStatus == 5 || appCatalogStatus == 6 || appCatalogStatus == 4)) {
                    final Boolean isErr2 = appCatalogStatus == 7 || appCatalogStatus == 11;
                    final String remarks = (String)tableContext.getAssociatedPropertyValue("MdAppCatalogToResource.REMARKS");
                    TransformerUtil.renderRemarksAsText(tableContext, columnProperties, remarks, isErr2, isExport);
                    payloadJSON = (columnProperties.containsKey("PAYLOAD") ? columnProperties.get("PAYLOAD") : payloadJSON);
                    String statusLabel3 = "";
                    String type = "";
                    switch (appCatalogStatus) {
                        case 0: {
                            statusLabel3 = "dc.db.som.status.yet_to_install";
                            if (installedAppId != null && !publishedAppId.equals(installedAppId)) {
                                statusLabel3 = "dc.common.YET_TO_UPDATE";
                            }
                            type = "Warning";
                            break;
                        }
                        case 1: {
                            statusLabel3 = "dc.db.config.status.in_progress";
                            type = "InProgress";
                            break;
                        }
                        case 4: {
                            statusLabel3 = "dc.db.som.status.uninstallation_failed";
                            type = "Failed";
                            break;
                        }
                        case 5:
                        case 6: {
                            statusLabel3 = "dc.db.som.status.yet_to_install";
                            type = "Warning";
                            break;
                        }
                    }
                    payloadJSON.put("remarks", (Object)payloadJSON.optString("VALUE", I18N.getMsg(statusLabel3, new Object[0])));
                    payloadJSON.put("type", (Object)type);
                }
                payloadJSON.put("last_distributed_version", data);
                columnProperties.put("PAYLOAD", payloadJSON);
            }
        }
    }
    
    static {
        AppRepositoryTransformer.logger = Logger.getLogger(AppRepositoryTransformer.class.getName());
    }
}
