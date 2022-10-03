package com.adventnet.sym.webclient.mdm.inv;

import com.adventnet.sym.server.mdm.apps.AppSettingsDataHandler;
import com.adventnet.i18n.I18N;
import org.json.JSONObject;
import java.util.HashMap;
import javax.servlet.http.HttpServletRequest;
import com.adventnet.client.components.web.TransformerContext;
import com.adventnet.client.components.table.web.DefaultTransformer;

public class MDMInventoryAppTransformer extends DefaultTransformer
{
    protected boolean checkIfColumnRendererable(final TransformerContext tableContext) throws Exception {
        final String columnalias = tableContext.getPropertyName();
        final HttpServletRequest request = tableContext.getViewContext().getRequest();
        final boolean hasInvWritePrivillage = request.isUserInRole("MDM_Inventory_Write") || request.isUserInRole("ModernMgmt_Inventory_Write");
        return (!columnalias.equalsIgnoreCase("Actions") && !columnalias.equalsIgnoreCase("MdAppGroupDetails.APP_GROUP_ID") && !columnalias.equalsIgnoreCase("Checkbox")) || hasInvWritePrivillage;
    }
    
    public void renderHeader(final TransformerContext tableContext) {
        super.renderHeader(tableContext);
        final HashMap headerProperties = tableContext.getRenderedAttributes();
        final String columnAlias = tableContext.getPropertyName();
        if (columnAlias.equalsIgnoreCase("MdAppGroupDetails.APP_GROUP_ID")) {
            final String checkAll = "<table><tr><td nowrap><input type=\"checkbox\" id=\"selectAll\" value=\"SelectAll\" name=\"selectcheckbox\" onclick=\"javascript:selectAllObjects(this.checked)\"></td></tr></table>";
            headerProperties.put("PAYLOAD", checkAll);
        }
        if (columnAlias.equalsIgnoreCase("icon_column")) {
            final String checkAll = "&nbsp;";
            headerProperties.put("PAYLOAD", checkAll);
        }
    }
    
    public void renderCell(final TransformerContext tableContext) throws Exception {
        super.renderCell(tableContext);
        final String columnalias = tableContext.getPropertyName();
        final HashMap columnProperties = tableContext.getRenderedAttributes();
        final Object data = tableContext.getPropertyValue();
        String isExport = "false";
        final int reportType = tableContext.getViewContext().getRenderType();
        if (reportType != 4) {
            isExport = "true";
        }
        if (columnalias.equalsIgnoreCase("icon_column") || columnalias.equalsIgnoreCase("Actions")) {
            final String blacklist_type = String.valueOf(tableContext.getAssociatedPropertyValue("IS_BLACKLIST"));
            final JSONObject value = new JSONObject();
            switch (Integer.parseInt(blacklist_type)) {
                case 0: {
                    value.put("blacklist", (Object)"global_blacklist");
                    columnProperties.put("PAYLOAD", value);
                    break;
                }
                case 1: {
                    value.put("blacklist", (Object)"partial_blacklist");
                    columnProperties.put("PAYLOAD", value);
                    break;
                }
                default: {
                    value.put("blacklist", (Object)"non_blacklist");
                    columnProperties.put("PAYLOAD", value);
                    break;
                }
            }
        }
        if (columnalias.equalsIgnoreCase("MdAppGroupDetails.GROUP_DISPLAY_NAME") && (isExport == null || isExport.equalsIgnoreCase("false"))) {
            String appName = data + "";
            if (appName.length() > 25) {
                appName = appName.substring(0, 25) + "...";
                columnProperties.put("PAYLOAD", appName);
            }
            else {
                columnProperties.put("PAYLOAD", appName);
            }
        }
        if (reportType != 4 && columnalias.equals("MdAppGroupDetails.PLATFORM_TYPE")) {
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
            columnProperties.put("PAYLOAD", platformName);
        }
        if (columnalias.equals("INSTALLED_BY")) {
            final Integer appType = (Integer)data;
            final String value2 = AppSettingsDataHandler.getInstance().getAppViewTransformerText(appType);
            columnProperties.put("VALUE", I18N.getMsg(value2, new Object[0]));
        }
        if (columnalias.equals("MdInstalledAppResourceRel.SCOPE")) {
            final Integer platform = (Integer)tableContext.getAssociatedPropertyValue("MdAppGroupDetails.PLATFORM_TYPE");
            final Integer appScope = (Integer)data;
            String sValue = "";
            if (platform == 2) {
                if (appScope == 1) {
                    sValue = I18N.getMsg("dc.mdm.enroll.managedprofile", new Object[0]);
                }
                else {
                    sValue = I18N.getMsg("dc.mdm.enroll.personalprofile", new Object[0]);
                }
            }
            else if (appScope == 1) {
                sValue = I18N.getMsg("dc.mdm.android.knox.container", new Object[0]);
            }
            else {
                sValue = I18N.getMsg("dc.common.DEVICE", new Object[0]);
            }
            columnProperties.put("PAYLOAD", sValue);
        }
        if (columnalias.equals("BlacklistAppCollectionStatus.STATUS")) {
            final JSONObject payloadData = new JSONObject();
            String statusLabel = "";
            String statusStr = "";
            String className = "";
            if (data != null) {
                switch ((int)data) {
                    case 1: {
                        statusLabel = "mdm.blocklist.initiated_status";
                        className = "ucs-table-status-text__ready";
                        break;
                    }
                    case 3: {
                        statusLabel = "mdm.blocklist.inprogress_status";
                        className = "ucs-table-status-text__in-progress";
                        break;
                    }
                    case 9: {
                        statusLabel = "mdm.blocklist.failed_status";
                        className = "ucs-table-status-text__failed";
                        break;
                    }
                }
                statusStr = I18N.getMsg(statusLabel, new Object[0]);
                if (isExport == null || !Boolean.valueOf(isExport)) {
                    payloadData.put("statusLabel", (Object)statusStr);
                    payloadData.put("styleClass", (Object)className);
                    columnProperties.put("PAYLOAD", payloadData);
                }
                else {
                    columnProperties.put("VALUE", statusStr);
                }
            }
        }
        if (columnalias.equalsIgnoreCase("BlacklistAppCollectionStatus.REMARKS")) {
            final Integer status = (Integer)data;
            String sValue2 = String.valueOf(status);
            switch (status) {
                case 1: {
                    sValue2 = I18N.getMsg("mdm.blocklist.initiated_remarks", new Object[0]);
                    break;
                }
                case 3: {
                    sValue2 = I18N.getMsg("mdm.blocklist.inprogress_remarks", new Object[0]);
                    break;
                }
                case 9: {
                    sValue2 = I18N.getMsg("mdm.blocklist.failed_remarks", new Object[0]);
                    break;
                }
            }
            columnProperties.put("VALUE", sValue2);
        }
    }
}
