package com.adventnet.sym.webclient.mdm.device;

import java.util.Iterator;
import com.adventnet.persistence.DataAccessException;
import com.me.mdm.server.apps.android.afw.AFWAccountErrorHandler;
import com.me.mdm.api.APIUtil;
import com.adventnet.sym.server.mdm.util.MDMStringUtils;
import com.adventnet.sym.server.mdm.apps.AppsUtil;
import com.me.mdm.webclient.transformer.TransformerUtil;
import org.apache.commons.lang.StringEscapeUtils;
import org.json.JSONObject;
import com.me.mdm.server.factory.MDMApiFactoryProvider;
import com.adventnet.i18n.I18N;
import java.util.logging.Level;
import java.util.HashMap;
import javax.servlet.http.HttpServletRequest;
import com.adventnet.client.view.web.ViewContext;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.adventnet.client.components.web.TransformerContext;
import java.util.logging.Logger;
import com.adventnet.client.components.table.web.DefaultTransformer;

public class MDMDevicetoProfileTransformer extends DefaultTransformer
{
    private Logger logger;
    private static final int MDM_PROFILE_TYPE = 1;
    private static final int MDM_APP_PROFILE_TYPE = 2;
    
    public MDMDevicetoProfileTransformer() {
        this.logger = Logger.getLogger(MDMDevicetoProfileTransformer.class.getName());
    }
    
    protected boolean checkIfColumnRendererable(final TransformerContext tableContext) throws Exception {
        final String columnalias = tableContext.getPropertyName();
        if (columnalias.equalsIgnoreCase("checkbox") || columnalias.equalsIgnoreCase("Resource.RESOURCE_ID")) {
            final ViewContext vc = tableContext.getViewContext();
            final String viewname = vc.getUniqueId();
            final HttpServletRequest request = vc.getRequest();
            final int reportType = tableContext.getViewContext().getRenderType();
            if (reportType != 4) {
                return false;
            }
            final boolean hasConfigurationWritePrivillage = request.isUserInRole("MDM_Configurations_Write") || request.isUserInRole("ModernMgmt_Configurations_Write");
            final boolean hasAppMgmtWritePrivillage = request.isUserInRole("MDM_AppMgmt_Write") || request.isUserInRole("ModernMgmt_AppMgmt_Write");
            if (viewname.equalsIgnoreCase("mdmDeviceProfiles")) {
                return hasConfigurationWritePrivillage;
            }
            if (viewname.equalsIgnoreCase("mdmDeviceApps")) {
                return hasAppMgmtWritePrivillage;
            }
        }
        if (columnalias.equalsIgnoreCase("Profile.SCOPE") || columnalias.equalsIgnoreCase("SCOPE")) {
            final ViewContext vc = tableContext.getViewContext();
            final String viewname = vc.getUniqueId();
            final HttpServletRequest request = vc.getRequest();
            final Long deviceId = Long.valueOf(request.getParameter("deviceId"));
            final HashMap groupHash = MDMUtil.getInstance().getMDMDeviceProperties(deviceId);
            return groupHash.get("PLATFORM_TYPE") == 2;
        }
        return super.checkIfColumnRendererable(tableContext);
    }
    
    public void renderHeader(final TransformerContext tableContext) {
        this.logger.log(Level.FINE, "Entering MDMGrouptoProfileTransformer renderHeader().....");
        super.renderHeader(tableContext);
        final int reportType = tableContext.getViewContext().getRenderType();
        String isExport = "false";
        if (reportType != 4) {
            isExport = "true";
        }
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
            this.logger.log(Level.WARNING, "Exception in MDMDeviceToProfileTransformer renderHeader", e);
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
        this.logger.log(Level.FINE, "Columnalais : {0}", columnalais);
        final Long resourceIdforCheck = (Long)tableContext.getAssociatedPropertyValue("Resource.RESOURCE_ID");
        final Long profileIdforCheck = (Long)tableContext.getAssociatedPropertyValue("Profile.PROFILE_ID");
        final Long collectionIdforCheck = (Long)tableContext.getAssociatedPropertyValue("ProfileToCollection.COLLECTION_ID");
        final String profileNameforCheck = (String)tableContext.getAssociatedPropertyValue("Profile.PROFILE_NAME");
        final Integer profileTypeforCheck = (Integer)tableContext.getAssociatedPropertyValue("Profile.PROFILE_TYPE");
        final Integer version = (Integer)tableContext.getAssociatedPropertyValue("ProfileColln.PROFILE_VERSION");
        final Integer lastestVersion = (Integer)tableContext.getAssociatedPropertyValue("ProfileToCollection.PROFILE_VERSION");
        final Long associatedCollnID = (Long)tableContext.getAssociatedPropertyValue("ProfileColln.COLLECTION_ID");
        final String isExportString = MDMApiFactoryProvider.getMDMTableViewAPI().getIsExport(tableContext);
        Boolean isExport = false;
        final int reportType = tableContext.getViewContext().getRenderType();
        if (reportType != 4) {
            isExport = true;
        }
        else if (isExportString != null && !isExportString.equals("")) {
            isExport = true;
        }
        if (profileTypeforCheck == 2) {
            isApp = true;
        }
        else if (profileTypeforCheck == 1 || profileTypeforCheck == 10) {
            isApp = false;
        }
        if (columnalais.equals("checkbox_column")) {
            final String collectionCheckId = profileIdforCheck + "collnId";
            final String profileNameCheckId = profileIdforCheck + "collnName";
            final String isAppCheckId = profileIdforCheck + "isApp";
            final Long collnID = (associatedCollnID == null) ? collectionIdforCheck : associatedCollnID;
            final String collectionUpgradeCheckId = profileIdforCheck + "collnUpgradeId";
            final String isUpgradeId = profileIdforCheck + "isUpgrade";
            final boolean isUpgrade = this.isAppUpgradeAvailable(tableContext);
            final String check = "<input type=\"checkbox\" value=\"" + profileIdforCheck + "\" name=\"object_list\" onclick=\"\"><input type=\"hidden\" id=\"" + collectionCheckId + "\"  value=\"" + collnID + "\"><input type=\"hidden\" id=\"" + isUpgradeId + "\"  value=\"" + isUpgrade + "\"><input type=\"hidden\" id=\"" + collectionUpgradeCheckId + "\"  value=\"" + collectionIdforCheck + "\"><input type=\"hidden\" id=\"" + profileNameCheckId + "\"  value=\"" + profileNameforCheck + "\"><input type=\"hidden\" id=\"" + isAppCheckId + "\"  value=\"" + isApp + "\">";
            columnProperties.put("VALUE", check);
        }
        if (columnalais.equals("CollnToResources.STATUS")) {
            final String statusLabel = (String)tableContext.getAssociatedPropertyValue("ConfigStatusDefn.StatusLabel");
            statusStr = I18N.getMsg(statusLabel, new Object[0]);
            columnProperties.put("VALUE", statusStr);
        }
        if (columnalais.equals("RecentProfileForResource.MARKED_FOR_DELETE")) {
            if (data != null) {
                if (viewname.equalsIgnoreCase("mdmDeviceProfiles")) {
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
        if (columnalais.equals("Resource.RESOURCE_ID")) {
            final Boolean markedForDelete = (Boolean)tableContext.getAssociatedPropertyValue("RecentProfileForResource.MARKED_FOR_DELETE");
            final long resourceId = (long)data;
            String action = "";
            if (isApp) {
                final JSONObject value = new JSONObject();
                final Long releaseLabelId = (Long)tableContext.getAssociatedPropertyValue("DistAppReleaseLabel.RELEASE_LABEL_ID");
                value.put("resourceId", resourceId);
                value.put("appId", (Object)tableContext.getAssociatedPropertyValue("MdPackageToAppData.PACKAGE_ID").toString());
                value.put("releaseLabelId", (Object)String.valueOf(releaseLabelId));
                value.put("appName", (Object)StringEscapeUtils.escapeJavaScript(profileNameforCheck));
                columnProperties.put("PAYLOAD", value);
            }
            else {
                action = "<a href=\"#\" onclick=\"comfirmAndDissociateFromGroup('" + resourceId + "' , '" + profileIdforCheck + "' , '" + associatedCollnID + "' , " + "'" + StringEscapeUtils.escapeJavaScript(profileNameforCheck) + "'" + "," + false + " , " + isApp + ")\"><img src=\"/images/disconnect.gif\" width=\"22\" alt=\"" + I18N.getMsg("dc.mdm.groups.Disassociate_Profile", new Object[0]) + "\" title=\"" + I18N.getMsg("dc.mdm.groups.Disassociate_Profile", new Object[0]) + "\" height=\"10\" hspace=\"3\" vspace=\"0\" border=\"0\" align=\"absmiddle\" ></a>";
                if (!markedForDelete && lastestVersion > version) {
                    action = action + "<a href=\"#\" onclick=\"comfirmAndUpgrade('" + resourceId + "' , '" + profileIdforCheck + "' , '" + collectionIdforCheck + "' , " + "'" + StringEscapeUtils.escapeJavaScript(profileNameforCheck) + "'" + "," + false + " , " + isApp + ")\"><img src=\"/images/profile_upgrade.png\" width=\"17\" alt=\"" + I18N.getMsg("dc.mdm.groups.Upgrade_Profile", new Object[0]) + "\" title=\"" + I18N.getMsg("dc.mdm.groups.Upgrade_Profile", new Object[0]) + "\" height=\"16\" hspace=\"3\" vspace=\"0\" border=\"0\" align=\"absmiddle\" ></a>";
                }
                columnProperties.put("VALUE", action);
            }
        }
        if (columnalais.equalsIgnoreCase("ProfileColln.PROFILE_VERSION") && lastestVersion > version) {
            columnProperties.put("VALUE", version);
        }
        if (columnalais.equals("CollnToResources.REMARKS")) {
            final Integer statusID = (Integer)tableContext.getAssociatedPropertyValue("CollnToResources.STATUS");
            final Boolean isErr = statusID == 7 || statusID == 11 || statusID == 8;
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
            }
        }
        if (columnalais.equalsIgnoreCase("MdModelInfo.MODEL_TYPE")) {
            final int modelType = (int)data;
            final String modelTypeName = MDMUtil.getInstance().getModelTypeName(modelType);
            columnProperties.put("VALUE", modelTypeName);
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
        if (columnalais.equalsIgnoreCase("SCOPE")) {
            if (data == null) {
                columnProperties.put("VALUE", I18N.getMsg("dc.common.DEVICE", new Object[0]));
            }
            else {
                final int installedIn = (int)data;
                columnProperties.put("VALUE", (installedIn == 1) ? I18N.getMsg("dc.mdm.android.knox.container", new Object[0]) : I18N.getMsg("dc.common.DEVICE", new Object[0]));
            }
        }
        if (columnalais.equalsIgnoreCase("PublishedAppId.PubAppVersion")) {
            final Boolean markedForDelete = (Boolean)tableContext.getAssociatedPropertyValue("RecentProfileForResource.MARKED_FOR_DELETE");
            if (data == null || (data != null && data.equals(""))) {
                data = "--";
            }
            if (reportType == 4) {
                final JSONObject payloadJson = new JSONObject();
                boolean updateApp = false;
                if (!markedForDelete && this.isAppUpgradeAvailable(tableContext)) {
                    updateApp = true;
                }
                final JSONObject approvedDetails = new JSONObject();
                approvedDetails.put("app_id", (Object)tableContext.getAssociatedPropertyValue("MdPackageToAppData.PACKAGE_ID").toString());
                final Long approvedReleaseLabel = (Long)tableContext.getAssociatedPropertyValue("ApprovedAppReleaseLabel");
                approvedDetails.put("release_label_id", (Object)String.valueOf(approvedReleaseLabel));
                payloadJson.put("approvedDetails", (Object)approvedDetails);
                final String approvedAppVersion = (String)tableContext.getAssociatedPropertyValue("ApprovedAppId.ApprovedVersion");
                final Integer approvedAppVersionStatus = (Integer)tableContext.getAssociatedPropertyValue("MDAPPCATALOGTORESOURCE.APPROVED_VERSION_STATUS");
                payloadJson.put("approvedAppVersion", (Object)AppsUtil.getValidVersion(approvedAppVersion));
                payloadJson.put("approvedVersionStatus", (approvedAppVersionStatus == null) ? 1 : ((int)approvedAppVersionStatus));
                payloadJson.put("appName", (Object)StringEscapeUtils.escapeJavaScript(profileNameforCheck));
                payloadJson.put("updateApp", updateApp);
                payloadJson.put("version", data);
                columnProperties.put("PAYLOAD", payloadJson);
            }
            else {
                columnProperties.put("VALUE", data);
            }
        }
        if (columnalais.equalsIgnoreCase("InstalledAppId.InstalledAppVersion") && (data == null || (data != null && data.equals("")))) {
            columnProperties.put("VALUE", "--");
        }
        if (columnalais.equalsIgnoreCase("DistAppReleaseLabel.RELEASE_LABEL_DISPLAY_NAME")) {
            final String key = (String)data;
            if (MDMStringUtils.isEmpty(key)) {
                columnProperties.put("VALUE", "--");
            }
            else {
                columnProperties.put("VALUE", I18N.getMsg(key, new Object[0]));
            }
        }
    }
    
    private boolean isAppUpgradeAvailable(final TransformerContext tableContext) throws Exception {
        final Boolean isUpdateAvailable = (Boolean)tableContext.getAssociatedPropertyValue("MDAPPCATALOGTORESOURCEEXTN.IS_UPDATE_AVAILABLE");
        return isUpdateAvailable != null && isUpdateAvailable;
    }
    
    public static JSONObject addAccountTroubleshootDataForApp(final TransformerContext context, final JSONObject payloadData, final String keyWithArgs) throws Exception {
        try {
            if (isPfWAccountError(context, keyWithArgs)) {
                final APIUtil apiUtil = APIUtil.getNewInstance();
                if (apiUtil.checkRolesForCurrentUser(new String[] { "MDM_AppMgmt_Admin", "ModernMgmt_AppMgmt_Admin" }) && retryRedirectionNeeded(context)) {
                    payloadData.put("resolveRedirection", true);
                }
                else {
                    payloadData.put("resolveRedirection", false);
                    payloadData.put("ManagedAccountLearnMore", true);
                }
            }
        }
        catch (final Exception e) {
            AppsUtil.logger.log(Level.WARNING, "Exception in populating troubleshooting steps for apps", e);
        }
        return payloadData;
    }
    
    public static boolean retryRedirectionNeeded(final TransformerContext context) throws DataAccessException {
        final Long resourceId = (Long)context.getAssociatedPropertyValue("Resource.RESOURCE_ID");
        return new AFWAccountErrorHandler().isRetryNeeded(resourceId);
    }
    
    public static boolean isPfWAccountError(final TransformerContext context, final String keyWithArgs) {
        final int profileTypeforCheck = (int)context.getAssociatedPropertyValue("Profile.PROFILE_TYPE");
        final Integer platform = (Integer)context.getAssociatedPropertyValue("MdAppDetails.PLATFORM_TYPE");
        if (profileTypeforCheck == 2 && platform.equals(2)) {
            if (keyWithArgs.startsWith("mdm.appmgmt.afw.account_addition_initiated") || keyWithArgs.startsWith("mdm.appmgmt.afw.account_addition_failed") || keyWithArgs.startsWith("mdm.appmgmt.afw.no_device_contact_support") || keyWithArgs.startsWith("mdm.appmgmt.afw.device_owner_for_install") || keyWithArgs.startsWith("mdm.appmgmt.afw.accept_terms_remarks")) {
                return true;
            }
            final HashMap accountErrors = AFWAccountErrorHandler.getErrorCodeReasons();
            final Iterator accountErrorItr = accountErrors.values().iterator();
            while (accountErrorItr.hasNext()) {
                if (keyWithArgs.startsWith(accountErrorItr.next())) {
                    return true;
                }
            }
        }
        return false;
    }
}
