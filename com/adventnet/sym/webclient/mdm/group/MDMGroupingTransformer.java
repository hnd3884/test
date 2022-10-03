package com.adventnet.sym.webclient.mdm.group;

import java.util.List;
import java.net.URLDecoder;
import com.me.mdm.webclient.transformer.TransformerUtil;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import java.net.URLEncoder;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.me.idps.core.util.DirectoryUtil;
import org.json.JSONObject;
import com.adventnet.sym.server.mdm.group.MDMGroupHandler;
import java.util.HashMap;
import com.adventnet.i18n.I18N;
import java.util.logging.Level;
import javax.servlet.http.HttpServletRequest;
import com.adventnet.client.view.web.ViewContext;
import com.me.devicemanagement.framework.server.license.LicenseProvider;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import com.me.mdm.server.factory.MDMApiFactoryProvider;
import com.adventnet.client.components.web.TransformerContext;
import java.util.logging.Logger;
import com.me.devicemanagement.framework.webclient.authorization.RolecheckerTransformer;

public class MDMGroupingTransformer extends RolecheckerTransformer
{
    private Logger logger;
    
    public MDMGroupingTransformer() {
        this.logger = Logger.getLogger(MDMGroupingTransformer.class.getName());
    }
    
    protected boolean checkIfColumnRendererable(final TransformerContext tableContext) throws Exception {
        final String columnalias = tableContext.getPropertyName();
        final ViewContext vc = tableContext.getViewContext();
        final HttpServletRequest request = vc.getRequest();
        final String isExport = MDMApiFactoryProvider.getMDMTableViewAPI().getIsExport(tableContext);
        final int reportType = tableContext.getViewContext().getRenderType();
        if (columnalias.equalsIgnoreCase("checkbox_column") || columnalias.equalsIgnoreCase("CustomGroup.RESOURCE_ID") || columnalias.equalsIgnoreCase("Checkbox")) {
            if ((isExport != null && isExport.equalsIgnoreCase("true")) || reportType != 4) {
                return false;
            }
            final String viewname = vc.getUniqueId();
            final boolean hasContentWritePrivillage = request.isUserInRole("MDM_ContentMgmt_Write");
            if (!SyMUtil.isStringEmpty(viewname) && viewname.equalsIgnoreCase("mdmDocGroups") && columnalias.equalsIgnoreCase("CustomGroup.RESOURCE_ID")) {
                return hasContentWritePrivillage;
            }
            final boolean hasSettingsWritePrivillage = request.isUserInRole("MDM_Settings_Write") || request.isUserInRole("ModernMgmt_Settings_Write");
            final boolean hasConfigurationWritePrivillage = request.isUserInRole("MDM_Configurations_Write") || request.isUserInRole("ModernMgmt_Configurations_Write");
            final boolean hasAppMgmtWritePrivillage = request.isUserInRole("MDM_AppMgmt_Write") || request.isUserInRole("ModernMgmt_AppMgmt_Write");
            return hasSettingsWritePrivillage || hasConfigurationWritePrivillage || hasAppMgmtWritePrivillage || hasContentWritePrivillage;
        }
        else {
            if (columnalias.equalsIgnoreCase("ResourceToProfileSummary.APP_COUNT")) {
                return request.isUserInRole("MDM_AppMgmt_Read") || request.isUserInRole("ModernMgmt_AppMgmt_Read");
            }
            if (columnalias.equalsIgnoreCase("ResourceToProfileSummary.PROFILE_COUNT")) {
                return request.isUserInRole("MDM_Configurations_Read") || request.isUserInRole("ModernMgmt_Configurations_Read");
            }
            if (columnalias.equalsIgnoreCase("ResourceToProfileSummary.DOC_COUNT")) {
                return request.isUserInRole("MDM_ContentMgmt_Read") && LicenseProvider.getInstance().getMDMLicenseAPI().isProfessionalLicenseEdition();
            }
            return super.checkIfColumnRendererable(tableContext);
        }
    }
    
    public void renderHeader(final TransformerContext tableContext) {
        this.logger.log(Level.FINE, "Entering CustomeGroupingTransformer renderHeader().....");
        super.renderHeader(tableContext);
        final ViewContext viewCtx = tableContext.getViewContext();
        final int renderType = viewCtx.getRenderType();
        final String isExport = MDMApiFactoryProvider.getMDMTableViewAPI().getIsExport(tableContext);
        final HashMap headerProperties = tableContext.getRenderedAttributes();
        final String head = tableContext.getDisplayName();
        if (head.equals("Action") && isExport != null) {
            headerProperties.put("VALUE", "");
        }
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
            this.logger.log(Level.WARNING, "Exception in MDMGroupingTransformer renderHeader", e);
        }
    }
    
    public void renderCell(final TransformerContext tableContext) {
        this.logger.log(Level.FINE, "Entering CustomeGroupingTransformer...");
        try {
            super.renderCell(tableContext);
            final HashMap columnProperties = tableContext.getRenderedAttributes();
            final Object data = tableContext.getPropertyValue();
            final String columnalais = tableContext.getPropertyName();
            this.logger.log(Level.FINE, "Columnalais : ", columnalais);
            final long resourceIdforCheck = (long)tableContext.getAssociatedPropertyValue("Resource.RESOURCE_ID");
            final String groupNameforCheck = (String)tableContext.getAssociatedPropertyValue("Resource.NAME");
            final String domainName = (String)tableContext.getAssociatedPropertyValue("Resource.DOMAIN_NETBIOS_NAME");
            final Long createdByUserId = (Long)tableContext.getAssociatedPropertyValue("CreatedUser.USER_ID");
            final int reportType = tableContext.getViewContext().getRenderType();
            final boolean isDefaultGroup = MDMGroupHandler.getInstance().isDefaultGroup(groupNameforCheck);
            final ViewContext vc = tableContext.getViewContext();
            final HttpServletRequest srvRequest = vc.getRequest();
            boolean isExport = Boolean.FALSE;
            if (reportType != 4) {
                isExport = Boolean.TRUE;
            }
            if (columnalais.equals("Checkbox")) {
                final Integer groupType = (Integer)tableContext.getAssociatedPropertyValue("CustomGroup.GROUP_TYPE");
                Integer syncStatus = 0;
                if (groupType == 7 && domainName != null && !domainName.equalsIgnoreCase("MDM")) {
                    syncStatus = ((this.getDomainFetchStatus(vc, domainName) == null) ? 0 : this.getDomainFetchStatus(vc, domainName));
                }
                boolean isDisabled = false;
                if (syncStatus == 300 || syncStatus == 3) {
                    isDisabled = true;
                }
                if (tableContext.getAssociatedPropertyValue("ImmutableCustomGroups.CUSTOM_GROUP_ID") != null && tableContext.getAssociatedPropertyValue("ImmutableCustomGroups.CUSTOM_GROUP_ID").equals(tableContext.getAssociatedPropertyValue("CustomGroup.RESOURCE_ID"))) {
                    isDisabled = true;
                }
                final JSONObject payloadData = new JSONObject();
                payloadData.put("isDisabled", isDisabled);
                columnProperties.put("PAYLOAD", payloadData);
            }
            if (columnalais.equals("Resource.NAME")) {
                final String groupname = (String)data;
                final int dirObjStatus = DirectoryUtil.getInstance().getDirObjStatus(tableContext);
                final Integer groupType2 = (Integer)tableContext.getAssociatedPropertyValue("CustomGroup.GROUP_TYPE");
                if (!isExport) {
                    final JSONObject payloadData = new JSONObject();
                    payloadData.put("cellValue", (Object)groupname);
                    payloadData.put("groupType", (Object)groupType2);
                    payloadData.put("viewContent", (Object)"members");
                    payloadData.put("dirObjStatus", dirObjStatus);
                    payloadData.put("groupId", (Object)String.valueOf(resourceIdforCheck));
                    payloadData.put("syncing", this.isGroupSyncing(groupType2, domainName, vc));
                    columnProperties.put("PAYLOAD", payloadData);
                }
                else {
                    columnProperties.put("VALUE", groupname);
                }
            }
            if (columnalais.equals("CustomGroup.RESOURCE_ID")) {
                String groupName = (String)tableContext.getAssociatedPropertyValue("Resource.NAME");
                groupName = MDMUtil.getInstance().encodeURIComponentEquivalent(groupName);
                groupName = URLEncoder.encode(groupName, "UTF-8");
                if (!isExport) {
                    final boolean groupWriteAccess = srvRequest.isUserInRole("MDM_GroupMgmt_Write") || srvRequest.isUserInRole("ModernMgmt_MDMGroupMgmt_Write");
                    final boolean groupAdminAccess = srvRequest.isUserInRole("MDM_GroupMgmt_Admin") || srvRequest.isUserInRole("ModernMgmt_MDMGroupMgmt_Admin");
                    final JSONObject payloadData = new JSONObject();
                    final Integer groupType3 = (Integer)tableContext.getAssociatedPropertyValue("CustomGroup.GROUP_TYPE");
                    if (groupWriteAccess || groupAdminAccess) {
                        final Boolean edit = (Boolean)tableContext.getAssociatedPropertyValue("CustomGroup.IS_EDITABLE");
                        final Long loggedInUserId = ApiFactoryProvider.getAuthUtilAccessAPI().getUserID();
                        final Long loggedInLoginId = ApiFactoryProvider.getAuthUtilAccessAPI().getLoginID();
                        final Long createdByLoginId = this.getLoginIdForUser(vc, createdByUserId);
                        boolean addDelAction = false;
                        final String s = domainName;
                        MDMUtil.getInstance();
                        if (s.equalsIgnoreCase("MDM")) {
                            final boolean adminAccesscreatedByLogIn = this.hasAdminAccessForCreatedLogIn(vc, createdByLoginId);
                            if (groupAdminAccess && ((createdByLoginId == null && TransformerUtil.hasUserAllDeviceScopeGroup(vc, true)) || createdByUserId.equals(loggedInUserId) || (adminAccesscreatedByLogIn && TransformerUtil.hasUserAllDeviceScopeGroup(vc, true)))) {
                                addDelAction = true;
                            }
                            else if ((!addDelAction && groupWriteAccess && createdByLoginId == null && srvRequest.isUserInRole("All_Managed_Mobile_Devices")) || createdByUserId.equals(loggedInUserId)) {
                                addDelAction = true;
                            }
                        }
                        else {
                            final int dirObjStatus2 = DirectoryUtil.getInstance().getDirObjStatus(tableContext);
                            if (dirObjStatus2 == 2 || dirObjStatus2 == 4 || (edit && (createdByUserId.equals(loggedInUserId) || TransformerUtil.hasUserAllDeviceScopeGroup(vc, true)) && !isDefaultGroup)) {
                                groupName = URLDecoder.decode(groupName, "UTF-8");
                                groupName = SyMUtil.getInstance().decodeURIComponentEquivalent(groupName);
                                addDelAction = true;
                            }
                        }
                        if (tableContext.getAssociatedPropertyValue("ImmutableCustomGroups.CUSTOM_GROUP_ID") != null && tableContext.getAssociatedPropertyValue("ImmutableCustomGroups.CUSTOM_GROUP_ID").equals(tableContext.getAssociatedPropertyValue("CustomGroup.RESOURCE_ID"))) {
                            addDelAction = false;
                        }
                        payloadData.put("addDelAction", addDelAction);
                        if (addDelAction) {
                            boolean modifyGroup = false;
                            final String s2 = domainName;
                            MDMUtil.getInstance();
                            if (s2.equalsIgnoreCase("MDM")) {
                                modifyGroup = true;
                            }
                            payloadData.put("modifyGroup", modifyGroup);
                        }
                    }
                    payloadData.put("groupName", (Object)groupName);
                    payloadData.put("groupId", (Object)data.toString());
                    payloadData.put("syncing", this.isGroupSyncing(groupType3, domainName, vc));
                    payloadData.put("groupType", tableContext.getAssociatedPropertyValue("CustomGroup.GROUP_TYPE"));
                    payloadData.put("createdUser", tableContext.getAssociatedPropertyValue("CreatedUser.FIRST_NAME"));
                    columnProperties.put("PAYLOAD", payloadData);
                }
            }
            if (columnalais.equals("CustomGroup.GROUP_TYPE") && data != null) {
                String groupType4 = "";
                if (data.equals(7)) {
                    if (domainName == null || domainName.equalsIgnoreCase("MDM")) {
                        groupType4 = I18N.getMsg("mdm.common.userGroup", new Object[0]);
                    }
                    else {
                        groupType4 = I18N.getMsg("mdm.ad.synced.group", new Object[0]);
                    }
                }
                else {
                    groupType4 = I18N.getMsg("dc.mdm.knox.device.group", new Object[0]);
                }
                columnProperties.put("VALUE", groupType4);
            }
            if (columnalais.equalsIgnoreCase("ResourceToProfileSummary.MEMBER_COUNT") || columnalais.equalsIgnoreCase("ResourceToProfileSummary.PROFILE_COUNT") || columnalais.equalsIgnoreCase("ResourceToProfileSummary.APP_COUNT") || columnalais.equalsIgnoreCase("ResourceToProfileSummary.DOC_COUNT")) {
                final String value = (data == null) ? "0" : data.toString();
                if (!isExport) {
                    final Integer groupType5 = (Integer)tableContext.getAssociatedPropertyValue("CustomGroup.GROUP_TYPE");
                    final boolean syncing = this.isGroupSyncing(groupType5, domainName, vc);
                    String viewContent = "";
                    if (columnalais.equalsIgnoreCase("ResourceToProfileSummary.MEMBER_COUNT")) {
                        viewContent = "members";
                    }
                    else if (columnalais.equalsIgnoreCase("ResourceToProfileSummary.PROFILE_COUNT")) {
                        viewContent = "profile";
                    }
                    else if (columnalais.equalsIgnoreCase("ResourceToProfileSummary.APP_COUNT")) {
                        viewContent = "app";
                    }
                    else if (columnalais.equalsIgnoreCase("ResourceToProfileSummary.DOC_COUNT")) {
                        viewContent = "content";
                    }
                    final JSONObject payloadData2 = new JSONObject();
                    payloadData2.put("cellValue", (Object)value);
                    payloadData2.put("syncing", syncing);
                    payloadData2.put("viewContent", (Object)viewContent);
                    payloadData2.put("dirObjStatus", 1);
                    payloadData2.put("groupId", (Object)String.valueOf(resourceIdforCheck));
                    payloadData2.put("groupType", tableContext.getAssociatedPropertyValue("CustomGroup.GROUP_TYPE"));
                    columnProperties.put("PAYLOAD", payloadData2);
                }
                else {
                    columnProperties.put("VALUE", value);
                }
            }
        }
        catch (final Exception e) {
            this.logger.log(Level.WARNING, "Exception occoured in renderCell", e);
        }
    }
    
    private boolean isGroupSyncing(final int groupType, final String domainName, final ViewContext vc) throws Exception {
        boolean isSyncing = false;
        if (groupType == 7 && domainName != null && !domainName.equalsIgnoreCase("MDM")) {
            final int syncStatus = (this.getDomainFetchStatus(vc, domainName) == null) ? 0 : this.getDomainFetchStatus(vc, domainName);
            if (syncStatus == 300 || syncStatus == 3) {
                isSyncing = true;
            }
        }
        return isSyncing;
    }
    
    private boolean isSelfEnrollDefaultGroup(final ViewContext vc, final Long resourceID) throws Exception {
        final List selfEnrollGroup = (List)TransformerUtil.getPreValuesForTransformer(vc, "SELF_ENROLL_DEFAULT_GROUP");
        return selfEnrollGroup != null && selfEnrollGroup.contains(resourceID);
    }
    
    private Long getLoginIdForUser(final ViewContext vc, final Long userID) throws Exception {
        final HashMap hashMap = (HashMap)TransformerUtil.getPreValuesForTransformer(vc, "USER_LOGIN_MAP");
        return (hashMap != null && hashMap.get(userID) != null) ? hashMap.get(userID) : null;
    }
    
    private boolean hasAdminAccessForCreatedLogIn(final ViewContext vc, final Long logInID) throws Exception {
        final List adminLoginIds = (List)TransformerUtil.getPreValuesForTransformer(vc, "ADMIN_LOGIN_ID");
        return adminLoginIds != null && adminLoginIds.contains(logInID);
    }
    
    private Integer getDomainFetchStatus(final ViewContext vc, final String domainName) throws Exception {
        final HashMap hashMap = (HashMap)TransformerUtil.getPreValuesForTransformer(vc, "DOMAIN_FETCH_STATUS_MAP");
        return (hashMap != null && hashMap.get(domainName) != null) ? hashMap.get(domainName) : null;
    }
}
