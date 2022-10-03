package com.adventnet.sym.webclient.mdm.group;

import com.adventnet.sym.server.mdm.util.MDMUtil;
import org.json.JSONObject;
import com.me.mdm.server.factory.MDMApiFactoryProvider;
import com.adventnet.i18n.I18N;
import java.util.logging.Level;
import java.util.HashMap;
import javax.servlet.http.HttpServletRequest;
import com.adventnet.client.view.web.ViewContext;
import com.me.mdm.webclient.transformer.TransformerUtil;
import com.me.mdm.server.role.RBDAUtil;
import com.me.devicemanagement.framework.server.authentication.DMUserHandler;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import com.adventnet.sym.server.mdm.group.MDMGroupHandler;
import com.adventnet.client.components.web.TransformerContext;
import java.util.logging.Logger;
import com.adventnet.client.components.table.web.DefaultTransformer;

public class MDMGroupMemberTransformer extends DefaultTransformer
{
    private Logger logger;
    
    public MDMGroupMemberTransformer() {
        this.logger = Logger.getLogger(MDMGroupMemberTransformer.class.getName());
    }
    
    protected boolean checkIfColumnRendererable(final TransformerContext tableContext) throws Exception {
        final String columnalias = tableContext.getPropertyName();
        final ViewContext vc = tableContext.getViewContext();
        final HttpServletRequest request = vc.getRequest();
        final String isExport = request.getParameter("isExport");
        Boolean isExportForEmebrView = false;
        final String viewName = tableContext.getViewContext().getUniqueId();
        final int reportType = tableContext.getViewContext().getRenderType();
        if (reportType != 4) {
            isExportForEmebrView = true;
        }
        final String groupIdStr = request.getParameter("groupId");
        final String groupDomainName = MDMGroupHandler.getInstance().domainNameForGroupID(groupIdStr);
        if (columnalias.equalsIgnoreCase("checkbox_column") || columnalias.equalsIgnoreCase("checkbox") || columnalias.equalsIgnoreCase("Resource.RESOURCE_ID")) {
            if (reportType != 4) {
                return Boolean.FALSE;
            }
            if (groupDomainName != null && !groupDomainName.equalsIgnoreCase("MDM")) {
                return false;
            }
            if (isExport != null && isExport.equalsIgnoreCase("true")) {
                return false;
            }
            if (isExportForEmebrView) {
                return false;
            }
            String isEditable = request.getParameter("isEditable");
            if (isEditable == null || isEditable.trim() == "") {
                isEditable = String.valueOf(request.getAttribute("isEditable"));
            }
            if (isEditable == null || isEditable.trim() == "" || isEditable.trim() == "null") {
                if (groupIdStr != null && !groupIdStr.trim().equals("") && !groupIdStr.equals("-1")) {
                    final Long groupId = Long.valueOf(groupIdStr);
                    final HashMap mdmGroupDetails = MDMGroupHandler.getInstance().getGroupDetails(groupId);
                    final Long createdByUserId = mdmGroupDetails.get("CREATED_BY");
                    final Long loggedInUserId = ApiFactoryProvider.getAuthUtilAccessAPI().getUserID();
                    final Long loggedInLoginId = ApiFactoryProvider.getAuthUtilAccessAPI().getLoginID();
                    final Long createdByLoginId = DMUserHandler.getLoginIdForUserId(createdByUserId);
                    final boolean hasGroupWritePrivillage = request.isUserInRole("MDM_GroupMgmt_Write") || request.isUserInRole("ModernMgmt_MDMGroupMgmt_Write");
                    final boolean hasGroupAdminPrivillage = request.isUserInRole("MDM_GroupMgmt_Admin") || request.isUserInRole("ModernMgmt_MDMGroupMgmt_Admin");
                    if (hasGroupWritePrivillage && createdByUserId.equals(loggedInUserId)) {
                        isEditable = "true";
                    }
                    else if (hasGroupAdminPrivillage && (createdByUserId.equals(loggedInUserId) || createdByLoginId == null || (RBDAUtil.getInstance().hasUserAllDeviceScopeGroup(createdByLoginId, true) && TransformerUtil.hasUserAllDeviceScopeGroup(vc, true)))) {
                        isEditable = "true";
                    }
                    else {
                        isEditable = "false";
                    }
                }
                else if (groupIdStr.equals("-1")) {
                    isEditable = "true";
                }
            }
            return (isEditable != null) ? Boolean.valueOf(isEditable) : Boolean.FALSE;
        }
        else {
            if (columnalias.equalsIgnoreCase("Resource.RESOURCE_TYPE") || columnalias.equalsIgnoreCase("Resource.RESOURCE_TYPE")) {
                return groupDomainName != null && !groupDomainName.equalsIgnoreCase("MDM") && (isExport != null && isExport.equalsIgnoreCase("true")) && false;
            }
            return super.checkIfColumnRendererable(tableContext);
        }
    }
    
    public void renderHeader(final TransformerContext tableContext) {
        this.logger.log(Level.FINE, "Entering CustomeGroupingTransformer renderHeader().....");
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
            this.logger.log(Level.WARNING, "Exception in MDMGroupMemberTransformer renderHeader", e);
        }
    }
    
    public void renderCell(final TransformerContext tableContext) throws Exception {
        super.renderCell(tableContext);
        Boolean isExportForEmebrView = false;
        final String viewName = tableContext.getViewContext().getUniqueId();
        final HashMap columnProperties = tableContext.getRenderedAttributes();
        final Object data = tableContext.getPropertyValue();
        final String columnalais = tableContext.getPropertyName();
        final String statusStr = "";
        this.logger.log(Level.FINE, "Columnalais : {0}", columnalais);
        final String isExportString = MDMApiFactoryProvider.getMDMTableViewAPI().getIsExport(tableContext);
        boolean isExport = false;
        if (isExportString != null && !isExportString.equals("")) {
            isExport = true;
        }
        final int reportType = tableContext.getViewContext().getRenderType();
        if (reportType != 4) {
            isExportForEmebrView = true;
        }
        if (reportType != 4) {
            isExport = true;
        }
        final String resourceName = (String)tableContext.getAssociatedPropertyValue("ManagedDeviceExtn.NAME");
        if (columnalais.equals("Resource.RESOURCE_TYPE")) {
            String membertype = "";
            if ((int)data == 101) {
                membertype = "";
            }
            else {
                membertype = "";
            }
            columnProperties.put("VALUE", membertype);
        }
        if (columnalais.equals("Resource.NAME")) {
            final long resourceIdforCheck = (long)tableContext.getAssociatedPropertyValue("Resource.RESOURCE_ID");
            final Integer resourceType = (Integer)tableContext.getAssociatedPropertyValue("Resource.RESOURCE_TYPE");
            String groupNameValue = "";
            Boolean subGroup = false;
            if (viewName.equalsIgnoreCase("mdmUserGroups") || viewName.equalsIgnoreCase("mdmTempUserGroups")) {
                if (isExportForEmebrView) {
                    columnProperties.put("VALUE", data);
                }
                else {
                    groupNameValue = (String)data;
                    if (resourceType == 101) {
                        subGroup = true;
                    }
                    final JSONObject payload = new JSONObject();
                    payload.put("subGroup", (Object)subGroup);
                    payload.put("groupNameValue", (Object)groupNameValue);
                    columnProperties.put("PAYLOAD", payload);
                }
            }
            else if (isExport) {
                columnProperties.put("VALUE", data);
            }
            else {
                columnProperties.put("VALUE", groupNameValue);
            }
        }
        if (columnalais.equals("USER_DOMAIN")) {
            String domainName = (String)tableContext.getAssociatedPropertyValue("Resource.DOMAIN_NETBIOS_NAME");
            if (domainName == null || domainName.toString().equalsIgnoreCase("MDM")) {
                domainName = I18N.getMsg("dc.mdm.enroll.local_user", new Object[0]);
            }
            columnProperties.put("VALUE", domainName);
        }
        if (columnalais.equalsIgnoreCase("ManagedDeviceExtn.NAME")) {
            final int managedStatus = (int)tableContext.getAssociatedPropertyValue("ManagedDevice.MANAGED_STATUS");
            String value = "--";
            value = resourceName;
            columnProperties.put("VALUE", value);
            final JSONObject payload2 = new JSONObject();
            payload2.put("managedStatus", managedStatus);
            payload2.put("resourceName", (Object)value);
            columnProperties.put("PAYLOAD", payload2);
        }
        if (columnalais.equalsIgnoreCase("MdModelInfo.MODEL_TYPE") && data != null) {
            final int modelType = (int)data;
            final String modelTypeName = MDMUtil.getInstance().getModelTypeName(modelType);
            columnProperties.put("VALUE", modelTypeName);
        }
        if (columnalais.equalsIgnoreCase("MdDeviceInfo.BATTERY_LEVEL") && data != null) {
            final float batteryLevel = (float)data;
            if (batteryLevel == -1.0) {
                columnProperties.put("VALUE", "--");
            }
        }
    }
}
