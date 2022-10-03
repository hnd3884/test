package com.adventnet.sym.webclient.mdm.device;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import org.json.JSONObject;
import java.text.DecimalFormat;
import java.util.HashMap;
import com.adventnet.i18n.I18N;
import java.util.logging.Level;
import javax.servlet.http.HttpServletRequest;
import com.adventnet.client.view.web.ViewContext;
import com.me.devicemanagement.framework.server.license.LicenseProvider;
import com.me.mdm.server.factory.MDMApiFactoryProvider;
import com.adventnet.client.components.web.TransformerContext;
import java.util.logging.Logger;
import com.me.devicemanagement.framework.webclient.authorization.RolecheckerTransformer;

public class MDMDeviceTransformer extends RolecheckerTransformer
{
    private Logger logger;
    
    public MDMDeviceTransformer() {
        this.logger = Logger.getLogger(MDMDeviceTransformer.class.getName());
    }
    
    protected boolean checkIfColumnRendererable(final TransformerContext tableContext) throws Exception {
        final String columnalias = tableContext.getPropertyName();
        final ViewContext vc = tableContext.getViewContext();
        final HttpServletRequest request = vc.getRequest();
        final String isExport = MDMApiFactoryProvider.getMDMTableViewAPI().getIsExport(tableContext);
        final int reportType = tableContext.getViewContext().getRenderType();
        if (columnalias.equalsIgnoreCase("checkbox_column") || columnalias.equalsIgnoreCase("Resource.RESOURCE_ID") || columnalias.equalsIgnoreCase("Checkbox")) {
            if ((isExport != null && isExport.equalsIgnoreCase("true")) || reportType != 4) {
                return false;
            }
            final boolean hasSettingsWritePrivillage = request.isUserInRole("MDM_Settings_Write");
            final boolean hasGroupReadPrivillage = request.isUserInRole("MDM_GroupMgmt_Read");
            final boolean hasSettingsWriteModernMgmtPrivillage = request.isUserInRole("ModernMgmt_Settings_Write");
            final boolean hasGrouptReadModernMgmtPrivillage = request.isUserInRole("ModernMgmt_MDMGroupMgmt_Read");
            return hasSettingsWritePrivillage || hasGroupReadPrivillage || hasGrouptReadModernMgmtPrivillage || hasSettingsWriteModernMgmtPrivillage;
        }
        else {
            if (columnalias.equalsIgnoreCase("ResourceToProfileSummary.APP_COUNT")) {
                final boolean isUserInRole = request.isUserInRole("MDM_AppMgmt_Read");
                final boolean isUserInModernMgmtRole = request.isUserInRole("ModernMgmt_AppMgmt_Read");
                return isUserInModernMgmtRole || isUserInRole;
            }
            if (columnalias.equalsIgnoreCase("ResourceToProfileSummary.PROFILE_COUNT")) {
                final boolean isUserInRole = request.isUserInRole("MDM_Configurations_Read");
                final boolean isUserInModernMgmtRole = request.isUserInRole("ModernMgmt_Configurations_Read");
                return isUserInModernMgmtRole || isUserInRole;
            }
            if (columnalias.equalsIgnoreCase("ResourceToProfileSummary.DOC_COUNT")) {
                return request.isUserInRole("MDM_ContentMgmt_Read") && LicenseProvider.getInstance().getMDMLicenseAPI().isProfessionalLicenseEdition();
            }
            return super.checkIfColumnRendererable(tableContext);
        }
    }
    
    public void renderHeader(final TransformerContext tableContext) {
        this.logger.log(Level.FINE, "Entering MDMDeviceTransformer renderHeader().....");
        super.renderHeader(tableContext);
        final ViewContext viewCtx = tableContext.getViewContext();
        final int renderType = viewCtx.getRenderType();
        final HttpServletRequest request = viewCtx.getRequest();
        final String isExport = request.getParameter("isExport");
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
            this.logger.log(Level.WARNING, "Exception in MDMDeviceTransformer renderHeader", e);
        }
    }
    
    public void renderCell(final TransformerContext tableContext) {
        this.logger.log(Level.FINE, "Entering MDMDeviceTransformer renderCell...");
        try {
            super.renderCell(tableContext);
            final HashMap columnProperties = tableContext.getRenderedAttributes();
            final Object data = tableContext.getPropertyValue();
            final String columnalais = tableContext.getPropertyName();
            this.logger.log(Level.FINE, "Columnalais : ", columnalais);
            final long resourceIdforCheck = (long)tableContext.getAssociatedPropertyValue("Resource.RESOURCE_ID");
            final int platformTypeforCheck = (int)tableContext.getAssociatedPropertyValue("ManagedDevice.PLATFORM_TYPE");
            final ViewContext vc = tableContext.getViewContext();
            final HttpServletRequest request = vc.getRequest();
            final String isExportString = MDMApiFactoryProvider.getMDMTableViewAPI().getIsExport(tableContext);
            boolean isExport = false;
            final DecimalFormat dec = new DecimalFormat("###.##");
            final String nullValue = "<span style=\"cursor:text;color: black;\">0</span>";
            final int reportType = tableContext.getViewContext().getRenderType();
            if (isExportString != null && !isExportString.equals("")) {
                isExport = true;
            }
            if (isExport && columnalais.equals("ManagedDevice.PLATFORM_TYPE")) {
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
                columnProperties.put("VALUE", platformName);
            }
            if (columnalais.equals("Resource.RESOURCE_ID")) {
                final JSONObject payloadData = new JSONObject();
                payloadData.put("deviceId", (Object)data.toString());
                payloadData.put("platformType", platformTypeforCheck);
                payloadData.put("deviceName", tableContext.getAssociatedPropertyValue("ManagedDeviceExtn.NAME"));
                columnProperties.put("PAYLOAD", payloadData);
            }
            if (columnalais.equals("checkbox_column")) {
                final String deviceTypeCheckId = resourceIdforCheck + "rType";
                final String deviceModelTypeCheckId = resourceIdforCheck + "modelTypes";
                Object modelType = tableContext.getAssociatedPropertyValue("MdModelInfo.MODEL_TYPE");
                String check = "<input type='checkbox' value='" + resourceIdforCheck + "' name='object_list' onclick=\"setSelectHead(this.checked)\"><input type='hidden' id='" + deviceTypeCheckId + "'  value='" + platformTypeforCheck + "'>";
                if (modelType == null) {
                    modelType = "";
                }
                check = check + "<input type='hidden' id='" + deviceModelTypeCheckId + "' value='" + modelType.toString() + "'>";
                columnProperties.put("VALUE", check);
            }
            if (columnalais.equalsIgnoreCase("MdModelInfo.MODEL_TYPE") && data != null) {
                final int modelType2 = (int)data;
                final String modelTypeName = MDMUtil.getInstance().getModelTypeName(modelType2);
                columnProperties.put("VALUE", modelTypeName);
            }
            if (columnalais.equalsIgnoreCase("ManagedDeviceExtn.NAME")) {
                final boolean hasGroupReadPrivillage = request.isUserInRole("MDM_GroupMgmt_Read");
                final boolean hasGroupReadModernMgmtPrivillage = request.isUserInRole("ModernMgmt_MDMGroupMgmt_Read");
                final boolean hasConfigurationWritePrivillage = request.isUserInRole("MDM_Configurations_Read");
                final boolean hasAppMgmtWritePrivillage = request.isUserInRole("MDM_AppMgmt_Read");
                final boolean hasConfigurationWriteModernMgmtPrivillage = request.isUserInRole("ModernMgmt_Configurations_Read");
                final boolean hasAppMgmtWriteModernMgmtPrivillage = request.isUserInRole("ModernMgmt_AppMgmt_Read");
                final boolean hasContentWritePrivillage = request.isUserInRole("MDM_ContentMgmt_Read");
                final boolean hasInvWritePrivillage = request.isUserInRole("MDM_Inventory_Write") || request.isUserInRole("ModernMgmt_Inventory_Write");
                final String deviceResourceName = (String)tableContext.getAssociatedPropertyValue("ManagedDeviceExtn.NAME");
                String viewContent = "";
                if (hasGroupReadPrivillage || hasGroupReadModernMgmtPrivillage) {
                    viewContent = "groups";
                }
                else if (hasConfigurationWritePrivillage || hasConfigurationWriteModernMgmtPrivillage) {
                    viewContent = "profile";
                }
                else if (hasAppMgmtWritePrivillage || hasAppMgmtWriteModernMgmtPrivillage) {
                    viewContent = "app";
                }
                else if (hasContentWritePrivillage) {
                    viewContent = "content";
                }
                else if (hasInvWritePrivillage) {
                    viewContent = "action";
                }
                if (reportType == 4) {
                    final JSONObject payloadData2 = new JSONObject();
                    payloadData2.put("viewContent", (Object)viewContent);
                    payloadData2.put("deviceId", (Object)String.valueOf(resourceIdforCheck));
                    payloadData2.put("cellValue", (Object)deviceResourceName);
                    columnProperties.put("PAYLOAD", payloadData2);
                }
                else {
                    columnProperties.put("VALUE", deviceResourceName);
                }
            }
            if (columnalais.equalsIgnoreCase("ResourceToProfileSummary.PROFILE_COUNT") || columnalais.equalsIgnoreCase("ResourceToProfileSummary.APP_COUNT") || columnalais.equalsIgnoreCase("ResourceToProfileSummary.DOC_COUNT") || columnalais.equalsIgnoreCase("GROUP_COUNT")) {
                final String value = (data == null) ? "0" : data.toString();
                if (reportType == 4) {
                    String viewContent2 = "";
                    final JSONObject payloadData3 = new JSONObject();
                    if (columnalais.equalsIgnoreCase("ResourceToProfileSummary.PROFILE_COUNT")) {
                        viewContent2 = "profile";
                    }
                    else if (columnalais.equalsIgnoreCase("ResourceToProfileSummary.APP_COUNT")) {
                        viewContent2 = "app";
                    }
                    else if (columnalais.equalsIgnoreCase("ResourceToProfileSummary.DOC_COUNT")) {
                        viewContent2 = "content";
                    }
                    else if (columnalais.equalsIgnoreCase("GROUP_COUNT")) {
                        viewContent2 = "groups";
                        final List groupList = this.getAssociatedGroupNames(vc, resourceIdforCheck);
                        String groupName = "";
                        if (groupList.size() != 0) {
                            final Iterator item = groupList.iterator();
                            groupName = item.next();
                            while (item.hasNext()) {
                                groupName = groupName + " , " + item.next();
                            }
                        }
                        payloadData3.put("hoverText", (Object)groupName);
                    }
                    payloadData3.put("viewContent", (Object)viewContent2);
                    payloadData3.put("deviceId", (Object)String.valueOf(resourceIdforCheck));
                    payloadData3.put("cellValue", (Object)value);
                    columnProperties.put("PAYLOAD", payloadData3);
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
    
    private List getAssociatedGroupNames(final ViewContext vc, final Long resourceID) throws Exception {
        final HashMap hashMap = (HashMap)vc.getRequest().getAttribute("ASSOCIATED_GROUP_NAMES");
        return (hashMap.get(resourceID) != null) ? ((List)hashMap.get(resourceID)) : new ArrayList();
    }
}
