package com.adventnet.sym.webclient.mdm.group;

import com.adventnet.sym.server.mdm.apps.AppsUtil;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import org.apache.commons.lang.StringEscapeUtils;
import org.json.JSONObject;
import java.util.HashMap;
import com.adventnet.i18n.I18N;
import java.util.logging.Level;
import javax.servlet.http.HttpServletRequest;
import com.adventnet.client.view.web.ViewContext;
import com.me.mdm.server.factory.MDMApiFactoryProvider;
import com.adventnet.client.components.web.TransformerContext;
import java.util.logging.Logger;
import com.adventnet.client.components.table.web.DefaultTransformer;

public class MDMGrouptoProfileTransformer extends DefaultTransformer
{
    private Logger logger;
    private static final int MDM_PROFILE_TYPE = 1;
    private static final int MDM_APP_PROFILE_TYPE = 2;
    
    public MDMGrouptoProfileTransformer() {
        this.logger = Logger.getLogger(MDMGrouptoProfileTransformer.class.getName());
    }
    
    protected boolean checkIfColumnRendererable(final TransformerContext tableContext) throws Exception {
        final String columnalias = tableContext.getPropertyName();
        final ViewContext vc = tableContext.getViewContext();
        final String viewname = vc.getUniqueId();
        final HttpServletRequest request = vc.getRequest();
        if (columnalias.equalsIgnoreCase("checkbox_column") || columnalias.equalsIgnoreCase("Resource.RESOURCE_ID") || columnalias.equalsIgnoreCase("checkbox")) {
            final String isExport = MDMApiFactoryProvider.getMDMTableViewAPI().getIsExport(tableContext);
            final int reportType = tableContext.getViewContext().getRenderType();
            if (reportType != 4) {
                return false;
            }
            final boolean hasConfigurationWritePrivillage = request.isUserInRole("MDM_Configurations_Write") || request.isUserInRole("ModernMgmt_Configurations_Write");
            final boolean hasAppMgmtWritePrivillage = request.isUserInRole("MDM_AppMgmt_Write") || request.isUserInRole("ModernMgmt_AppMgmt_Write");
            if (viewname.equalsIgnoreCase("mdmGroupProfiles")) {
                return hasConfigurationWritePrivillage;
            }
            if (viewname.equalsIgnoreCase("mdmGroupApps")) {
                return hasAppMgmtWritePrivillage;
            }
        }
        return super.checkIfColumnRendererable(tableContext);
    }
    
    public void renderHeader(final TransformerContext tableContext) {
        this.logger.log(Level.FINE, "Entering MDMGrouptoProfileTransformer renderHeader().....");
        super.renderHeader(tableContext);
        final ViewContext viewCtx = tableContext.getViewContext();
        final int renderType = viewCtx.getRenderType();
        final HttpServletRequest request = viewCtx.getRequest();
        final String isExport = request.getParameter("isExport");
        final HashMap headerProperties = tableContext.getRenderedAttributes();
        final String checkbox = tableContext.getDisplayName();
        try {
            if (checkbox.equals(I18N.getMsg("dc.common.CHECKBOX_COLUMN", new Object[0]))) {
                String checkAll = "";
                if (isExport == null) {
                    checkAll = "<input type=\"checkbox\" id=\"selectAll\" value=\"SelectAll\" name=\"selectcheckbox\" onclick=\"javascript:selectAllObjects(this.checked)\">";
                }
                headerProperties.put("VALUE", checkAll);
            }
        }
        catch (final Exception e) {
            this.logger.log(Level.WARNING, "Exception in MDMGroupToProfileTransformer renderHeader", e);
        }
    }
    
    public void renderCell(final TransformerContext tableContext) throws Exception {
        super.renderCell(tableContext);
        final HashMap columnProperties = tableContext.getRenderedAttributes();
        final ViewContext viewCtx = tableContext.getViewContext();
        final String viewname = viewCtx.getUniqueId();
        Object data = tableContext.getPropertyValue();
        final String columnalais = tableContext.getPropertyName();
        boolean isApp = false;
        String statusStr = "";
        final String isExportString = MDMApiFactoryProvider.getMDMTableViewAPI().getIsExport(tableContext);
        final int reportType = tableContext.getViewContext().getRenderType();
        boolean isExport = false;
        if (isExportString != null && !isExportString.equals("")) {
            isExport = true;
        }
        this.logger.log(Level.FINE, "Columnalais : {0}", columnalais);
        final long profileIdforCheck = (long)tableContext.getAssociatedPropertyValue("Profile.PROFILE_ID");
        final long collectionIdforCheck = (long)tableContext.getAssociatedPropertyValue("ProfileToCollection.COLLECTION_ID");
        final String profileNameforCheck = (String)tableContext.getAssociatedPropertyValue("Profile.PROFILE_NAME");
        final int profileTypeforCheck = (int)tableContext.getAssociatedPropertyValue("Profile.PROFILE_TYPE");
        final int latestVer = (int)tableContext.getAssociatedPropertyValue("ProfileToCollection.PROFILE_VERSION");
        if (profileTypeforCheck == 2) {
            isApp = true;
        }
        else if (profileTypeforCheck == 1) {
            isApp = false;
        }
        final Integer version = (Integer)tableContext.getAssociatedPropertyValue("ProfileColln.PROFILE_VERSION");
        if (columnalais.equals("GroupToProfileHistory.COLLECTION_STATUS")) {
            final long collectionIdforTable = (long)tableContext.getAssociatedPropertyValue("RecentProfileForGroup.COLLECTION_ID");
            if (reportType == 4) {
                final JSONObject payload = new JSONObject();
                payload.put("statusColumn", true);
                payload.put("profileId", (Object)String.valueOf(profileIdforCheck));
                payload.put("collectionId", (Object)String.valueOf(collectionIdforTable));
                columnProperties.put("PAYLOAD", payload);
            }
            else {
                final String statusLabel = (String)tableContext.getAssociatedPropertyValue("ConfigStatusDefn.Label");
                columnProperties.put("VALUE", I18N.getMsg(statusLabel, new Object[0]));
            }
        }
        if (columnalais.equals("Profile.PROFILE_NAME")) {
            final long collectionIdforTable = (long)tableContext.getAssociatedPropertyValue("RecentProfileForGroup.COLLECTION_ID");
            final JSONObject payload = new JSONObject();
            payload.put("statusColumn", false);
            payload.put("profileId", (Object)String.valueOf(profileIdforCheck));
            payload.put("collectionId", (Object)String.valueOf(collectionIdforTable));
            columnProperties.put("PAYLOAD", payload);
        }
        if (columnalais.equals("RecentProfileForGroup.MARKED_FOR_DELETE")) {
            if (data != null) {
                if (viewname.equalsIgnoreCase("mdmGroupProfiles")) {
                    if (data == Boolean.TRUE) {
                        statusStr = I18N.getMsg("dc.mdm.group.view.profile_disassociataion", new Object[0]);
                    }
                    else {
                        statusStr = I18N.getMsg("dc.mdm.group.view.profile_associataion", new Object[0]);
                    }
                }
                else if (data == Boolean.TRUE) {
                    statusStr = I18N.getMsg("dc.mdm.group.view.app_removal", new Object[0]);
                }
                else {
                    statusStr = I18N.getMsg("dc.mdm.group.view.app_installation", new Object[0]);
                }
            }
            columnProperties.put("VALUE", statusStr);
        }
        if (reportType != 4 && columnalais.equals("Profile.PLATFORM_TYPE")) {
            String platformName = I18N.getMsg("dc.common.UNKNOWN", new Object[0]);
            final Integer platformType = (Integer)data;
            if (platformType == 1) {
                platformName = I18N.getMsg("dc.mdm.ios", new Object[0]);
            }
            else if (platformType == 2) {
                platformName = I18N.getMsg("dc.mdm.android", new Object[0]);
            }
            else if (platformType == 3) {
                platformName = I18N.getMsg("dc.common.WINDOWS", new Object[0]);
            }
            else if (platformType == 4) {
                platformName = I18N.getMsg("mdm.common.chrome", new Object[0]);
            }
            else if (platformType == 6) {
                platformName = I18N.getMsg("mdm.os.MacOS", new Object[0]);
            }
            else if (platformType == 7) {
                platformName = I18N.getMsg("mdm.os.TVos", new Object[0]);
            }
            columnProperties.put("VALUE", platformName);
        }
        if (columnalais.equals("Resource.RESOURCE_ID")) {
            final Boolean markedForDelete = (Boolean)tableContext.getAssociatedPropertyValue("RecentProfileForGroup.MARKED_FOR_DELETE");
            final long resourceId = (long)data;
            final JSONObject value = new JSONObject();
            value.put("resourceId", resourceId);
            value.put("collectionId", (Object)new Long(collectionIdforCheck).toString());
            if (isApp) {
                final Long releaseLabelId = (Long)tableContext.getAssociatedPropertyValue("DistAppReleaseLabel.RELEASE_LABEL_ID");
                value.put("appId", (Object)tableContext.getAssociatedPropertyValue("MdPackageToAppData.PACKAGE_ID").toString());
                value.put("releaseLabelId", (Object)String.valueOf(releaseLabelId));
                value.put("appName", (Object)StringEscapeUtils.escapeJavaScript(profileNameforCheck));
            }
            else {
                if (!markedForDelete && latestVer > version) {
                    value.put("upgradeProfile", true);
                }
                value.put("profileId", (Object)new Long(profileIdforCheck).toString());
                value.put("profileName", (Object)StringEscapeUtils.escapeJavaScript(profileNameforCheck));
            }
            columnProperties.put("PAYLOAD", value);
        }
        if (columnalais.equals("Profile.SCOPE")) {
            final Integer platform = (Integer)tableContext.getAssociatedPropertyValue("Profile.PLATFORM_TYPE");
            String pType = "--";
            final Integer type = (Integer)data;
            if (type != null) {
                if (type == 1) {
                    pType = "Knox";
                }
                else {
                    pType = MDMUtil.getInstance().getPlatformName(platform);
                }
            }
            columnProperties.put("VALUE", pType);
        }
        if (columnalais.equals("AppDeploymentPolicy.FORCE_APP_INSTALL")) {
            final Object isForceAppInstallObj = tableContext.getAssociatedPropertyValue("AppDeploymentPolicy.FORCE_APP_INSTALL");
            if (isForceAppInstallObj != null) {
                final Boolean isForceInstall = (Boolean)isForceAppInstallObj;
                if (isForceInstall) {
                    columnProperties.put("VALUE", I18N.getMsg("dc.mdm.group.install_app_automatically", new Object[0]));
                }
                else {
                    columnProperties.put("VALUE", I18N.getMsg("dc.mdm.actionlog.appmgmt.appInstallType.manual", new Object[0]));
                }
            }
        }
        if (columnalais.equals("ProfileColln.PROFILE_VERSION") && latestVer > version) {
            columnProperties.put("VALUE", version);
        }
        if (columnalais.equals("GroupToProfileHistory.REMARKS") || columnalais.equals("AppCategory.APP_CATEGORY_LABEL") || columnalais.equals("DistAppReleaseLabel.RELEASE_LABEL_DISPLAY_NAME")) {
            columnProperties.put("VALUE", I18N.getMsg((String)data, new Object[0]));
        }
        if (columnalais.equalsIgnoreCase("PublishedAppId.APP_VERSION")) {
            final String allLatestVersion = (String)tableContext.getAssociatedPropertyValue("AllLatestVer.APP_VERSION");
            final String mobLatestVersion = (String)tableContext.getAssociatedPropertyValue("MobileLatestVer.APP_VERSION");
            final String lapLatestVersion = (String)tableContext.getAssociatedPropertyValue("LaptopLatestVer.APP_VERSION");
            final Boolean markedForDelete2 = (Boolean)tableContext.getAssociatedPropertyValue("RecentProfileForGroup.MARKED_FOR_DELETE");
            if (data == null || (data != null && data.equals(""))) {
                data = "--";
            }
            data = AppsUtil.getValidVersion(data.toString());
            if (reportType == 4) {
                final JSONObject payloadJson = new JSONObject();
                boolean updateApp = false;
                if (!markedForDelete2 && this.isAppUpgradeAvailable(tableContext)) {
                    updateApp = true;
                }
                final JSONObject approvedDetails = new JSONObject();
                approvedDetails.put("app_id", (Object)tableContext.getAssociatedPropertyValue("MdPackageToAppData.PACKAGE_ID").toString());
                final Long approvedAppReleaseLabel = (Long)tableContext.getAssociatedPropertyValue("ApprovedAppReleaseLabel");
                approvedDetails.put("release_label_id", (Object)String.valueOf(approvedAppReleaseLabel));
                payloadJson.put("approvedDetails", (Object)approvedDetails);
                final String approvedAppVersion = (String)tableContext.getAssociatedPropertyValue("ApprovedAppId.ApprovedVersion");
                final Integer approvedVersionStatus = (Integer)tableContext.getAssociatedPropertyValue("MDAPPCATALOGTOGROUP.APPROVED_VERSION_STATUS");
                payloadJson.put("approvedAppVersion", (Object)AppsUtil.getValidVersion(approvedAppVersion));
                payloadJson.put("approvedVersionStatus", (Object)approvedVersionStatus);
                payloadJson.put("appName", (Object)StringEscapeUtils.escapeJavaScript(profileNameforCheck));
                payloadJson.put("updateApp", updateApp);
                payloadJson.put("allLatestVersion", (Object)allLatestVersion);
                payloadJson.put("mobLatestVersion", (Object)mobLatestVersion);
                payloadJson.put("lapLatestVersion", (Object)lapLatestVersion);
                payloadJson.put("version", data);
                columnProperties.put("PAYLOAD", payloadJson);
            }
            else {
                columnProperties.put("VALUE", data);
            }
        }
    }
    
    private boolean isAppUpgradeAvailable(final TransformerContext tableContext) throws Exception {
        final Object isUpgrade = tableContext.getAssociatedPropertyValue("MDAPPCATALOGTOGROUP.IS_UPDATE_AVAILABLE");
        if (isUpgrade != null) {
            return (boolean)isUpgrade;
        }
        return Boolean.FALSE;
    }
}
