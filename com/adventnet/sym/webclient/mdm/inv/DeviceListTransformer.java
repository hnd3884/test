package com.adventnet.sym.webclient.mdm.inv;

import java.util.Iterator;
import com.me.devicemanagement.framework.server.util.DateTimeUtil;
import com.me.mdm.server.settings.location.GeoLocationFacade;
import com.me.devicemanagement.framework.server.util.DMIAMEncoder;
import com.adventnet.sym.server.mdm.apps.AppSettingsDataHandler;
import com.adventnet.sym.server.mdm.inv.MDMMailNotificationHandler;
import org.json.JSONObject;
import com.me.devicemanagement.framework.webclient.common.SYMClientUtil;
import java.util.List;
import java.util.ArrayList;
import java.text.DecimalFormat;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.adventnet.i18n.I18N;
import java.util.HashMap;
import javax.servlet.http.HttpServletRequest;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import com.adventnet.client.components.web.TransformerContext;
import com.adventnet.client.components.table.web.DefaultTransformer;

public class DeviceListTransformer extends DefaultTransformer
{
    private long weekInMilli;
    
    public DeviceListTransformer() {
        this.weekInMilli = 604800000L;
    }
    
    protected boolean checkIfColumnRendererable(final TransformerContext tableContext) throws Exception {
        final String columnalias = tableContext.getPropertyName();
        final String viewname = tableContext.getViewContext().getUniqueId();
        final HttpServletRequest request = tableContext.getViewContext().getRequest();
        final boolean hasInvWritePrivillage = ApiFactoryProvider.getAuthUtilAccessAPI().getRoles().contains("MDM_Inventory_Write") || ApiFactoryProvider.getAuthUtilAccessAPI().getRoles().contains("ModernMgmt_Inventory_Write");
        final boolean hasInvReadPrivilege = ApiFactoryProvider.getAuthUtilAccessAPI().getRoles().contains("MDM_Inventory_Read") || ApiFactoryProvider.getAuthUtilAccessAPI().getRoles().contains("ModernMgmt_Inventory_Read");
        final boolean hasSettingsWritePrivillage = ApiFactoryProvider.getAuthUtilAccessAPI().getRoles().contains("MDM_Settings_Write") || ApiFactoryProvider.getAuthUtilAccessAPI().getRoles().contains("ModernMgmt_Settings_Write");
        final boolean hasSettingsAdminPrivillage = ApiFactoryProvider.getAuthUtilAccessAPI().getRoles().contains("MDM_Settings_Admin") || ApiFactoryProvider.getAuthUtilAccessAPI().getRoles().contains("ModernMgmt_Settings_Admin");
        String isExport = "false";
        final int reportType = tableContext.getViewContext().getRenderType();
        if (reportType != 4) {
            isExport = "true";
        }
        if (columnalias.equalsIgnoreCase("Resource.RESOURCE_ID") || columnalias.equalsIgnoreCase("ManagedDevice.RESOURCE_ID") || columnalias.equalsIgnoreCase("MdAppGroupDetails.APP_GROUP_ID")) {
            final String report = request.getParameter("report");
            if (isExport != null && isExport.equalsIgnoreCase("true")) {
                return false;
            }
            if (viewname.equalsIgnoreCase("ScanDeviceList")) {
                return hasInvReadPrivilege;
            }
            if (viewname.equalsIgnoreCase("PendingDeviceList")) {
                return hasSettingsWritePrivillage;
            }
            if (viewname.equalsIgnoreCase("MDMAppRestrictList") && (report == null || !report.equalsIgnoreCase("true"))) {
                return hasSettingsAdminPrivillage;
            }
            if (viewname.equalsIgnoreCase("")) {
                return hasInvWritePrivillage;
            }
        }
        if (columnalias.equalsIgnoreCase("Actions") || columnalias.equalsIgnoreCase("icon_column") || columnalias.equalsIgnoreCase("icon_columns") || columnalias.equalsIgnoreCase("MdAppGroupDetails.APP_GROUP_ID")) {
            final String userId = request.getParameter("userId");
            if (userId != null && !userId.equalsIgnoreCase("")) {
                return Boolean.FALSE;
            }
            final String report2 = request.getParameter("report");
            if (report2 != null && report2.equalsIgnoreCase("true")) {
                return Boolean.FALSE;
            }
            if (isExport != null && isExport.equalsIgnoreCase("true")) {
                return false;
            }
            if (viewname.equalsIgnoreCase("DeviceList")) {
                return hasInvWritePrivillage;
            }
        }
        if (columnalias.equalsIgnoreCase("Actions") && viewname.equalsIgnoreCase("DeviceListSearch")) {
            return false;
        }
        if ((columnalias.equalsIgnoreCase("Actions") || columnalias.equalsIgnoreCase("MdAppGroupDetails.APP_GROUP_ID") || columnalias.equalsIgnoreCase("Checkbox")) && viewname.equalsIgnoreCase("MDMAppList")) {
            return hasInvWritePrivillage;
        }
        if (columnalias.equalsIgnoreCase("Actions") && (viewname.equalsIgnoreCase("MDMAppRestrictList") || viewname.equalsIgnoreCase("MDMAppRestrictListInv"))) {
            return hasSettingsAdminPrivillage;
        }
        if (columnalias.equalsIgnoreCase("ManagedDevice.AGENT_TYPE")) {
            return false;
        }
        if (columnalias.equalsIgnoreCase("REMARKS")) {
            final Integer agentType = (Integer)tableContext.getAssociatedPropertyValue("ManagedDevice.AGENT_TYPE");
            return agentType != null && agentType == 3;
        }
        if (columnalias.equals("Action") && viewname.equalsIgnoreCase("DeviceLocationList")) {
            return tableContext.getViewContext().getRequest().getParameter("enableHistory").equalsIgnoreCase("true") && !isExport.equalsIgnoreCase("true");
        }
        if (columnalias.equals("MdDeviceLocationDetails.LOCATION_ADDRESS") && (viewname.equalsIgnoreCase("DeviceLocationList") || viewname.equalsIgnoreCase("DeviceLocationHistoryList"))) {
            return tableContext.getViewContext().getRequest().getParameter("includeAddress").equalsIgnoreCase("true") && isExport.equalsIgnoreCase("true");
        }
        if (viewname.equalsIgnoreCase("DeviceLocationHistoryList")) {
            final String isScheduleReportStr = tableContext.getViewContext().getRequest().getParameter("isScheduledReport");
            boolean isScheduleReport = false;
            if (isScheduleReportStr != null) {
                isScheduleReport = Boolean.valueOf(isScheduleReportStr);
            }
            if (columnalias.equalsIgnoreCase("ManagedDeviceExtn.NAME") || columnalias.equalsIgnoreCase("ManagedDevice.PLATFORM_TYPE") || columnalias.equalsIgnoreCase("UserResource.NAME") || columnalias.equalsIgnoreCase("CustomGroup.RESOURCE_ID") || columnalias.equalsIgnoreCase("MdDeviceLocationToErrCode.ERROR_CODE")) {
                return isScheduleReport || isExport.equalsIgnoreCase("true");
            }
        }
        return super.checkIfColumnRendererable(tableContext);
    }
    
    public void renderHeader(final TransformerContext tableContext) {
        super.renderHeader(tableContext);
        final HashMap headerProperties = tableContext.getRenderedAttributes();
        final String columnalais = tableContext.getPropertyName();
        if (columnalais.equals("Resource.RESOURCE_ID")) {
            final String checkAll = "<table><tr><td nowrap><input type=\"checkbox\" id=\"selectAll\" value=\"SelectAll\" name=\"selectcheckbox\" onclick=\"javascript:selectAllObjects(this.checked)\"></td></tr></table>";
            headerProperties.put("VALUE", checkAll);
        }
        if (columnalais.equalsIgnoreCase("MdAppGroupDetails.APP_GROUP_ID")) {
            final String checkAll = "<table><tr><td nowrap><input type=\"checkbox\" id=\"selectAll\" value=\"SelectAll\" name=\"selectcheckbox\" onclick=\"javascript:selectAllObjects(this.checked)\"></td></tr></table>";
            headerProperties.put("VALUE", checkAll);
        }
        if (columnalais.equalsIgnoreCase("icon_column") || columnalais.equalsIgnoreCase("icon_columns")) {
            final String checkAll = "&nbsp;";
            headerProperties.put("VALUE", checkAll);
        }
        final String viewname = tableContext.getViewContext().getUniqueId();
        if (viewname.equalsIgnoreCase("DeviceList") && columnalais.equalsIgnoreCase("Actions")) {
            headerProperties.put("VALUE", "");
        }
    }
    
    public void renderCell(final TransformerContext tableContext) throws Exception {
        super.renderCell(tableContext);
        final HashMap columnProperties = tableContext.getRenderedAttributes();
        final String columnalais = tableContext.getPropertyName();
        String isExport = "false";
        final int reportType = tableContext.getViewContext().getRenderType();
        if (reportType != 4) {
            isExport = "true";
        }
        final String viewname = tableContext.getViewContext().getUniqueId();
        Object data = tableContext.getPropertyValue();
        final Integer platformTypeVal = (Integer)tableContext.getAssociatedPropertyValue("ManagedDevice.PLATFORM_TYPE");
        final boolean hasInvReadPrivillage = ApiFactoryProvider.getAuthUtilAccessAPI().getRoles().contains("MDM_Inventory_Read") || ApiFactoryProvider.getAuthUtilAccessAPI().getRoles().contains("ModernMgmt_Inventory_Read");
        if (columnalais.equals("ManagedDeviceExtn.NAME") && (isExport == null || isExport.equalsIgnoreCase("false")) && hasInvReadPrivillage) {
            final Long resourceID = (Long)tableContext.getAssociatedPropertyValue("Resource.RESOURCE_ID");
            final String deviceName = (String)data;
            if (deviceName != null) {
                final String actionStr = "<a ignorequickload=\"true\" href=\"#/uems/mdm/inventory/devicesList/" + resourceID + "/summary\" >" + deviceName + "</a>";
                columnProperties.put("VALUE", actionStr);
            }
        }
        Boolean status = true;
        if (tableContext.getAssociatedPropertyValue("BlacklistAppCollectionStatus.STATUS") instanceof Integer && (int)tableContext.getAssociatedPropertyValue("BlacklistAppCollectionStatus.STATUS") != 8) {
            status = false;
        }
        if (columnalais.equals("MdDeviceLocationDetails.LATITUDE") && isExport.equalsIgnoreCase("true")) {
            final String longitute = (String)tableContext.getAssociatedPropertyValue("MdDeviceLocationDetails.LONGITUDE");
            columnProperties.put("VALUE", data + "," + longitute);
        }
        if (columnalais.equals("Resource.RESOURCE_ID")) {
            final Long resourceID2 = (Long)data;
            final String actionStr = "<table><tr><td nowrap><input type=\"checkbox\" value=\"" + resourceID2 + "\" name=\"object_list\" onclick=\"setSelectHead(this.checked)\"></td></tr></table>";
            columnProperties.put("VALUE", actionStr);
        }
        if (columnalais.equals("MdAppGroupDetails.APP_GROUP_ID")) {
            final Long resourceID2 = (Long)data;
            String actionStr = "<table><tr><td nowrap><input type=\"checkbox\" value=\"" + resourceID2 + "\" name=\"object_list\" onclick=\"setSelectHead(this.checked)\"></td></tr></table>";
            if ((int)tableContext.getAssociatedPropertyValue("MdAppGroupDetails.PLATFORM_TYPE") == 4) {
                final String toolTip = I18N.getMsg("dc.mdm.inv.wp_blacklist_whitelist_not_supported", new Object[0]);
                actionStr = "<table><tr><td nowrap><input disabled type=\"checkbox\" value=\"" + resourceID2 + "\" name=\"object_list\"><span style=\"position:relative;left:-14px;top:-3px;cursor:not-allowed\" onmouseover=\"return overlib('<div class=\\'overlib\\'>" + toolTip + "</div>', FULLHTML);\" onmouseout=\"return nd();\">&nbsp;&nbsp;&nbsp;&nbsp;</span></input></td></tr></table>";
            }
            else if (!(boolean)tableContext.getAssociatedPropertyValue("MdAppGroupDetails.IS_MODERN_APP")) {
                final String toolTip = I18N.getMsg("mdm.inv.blacklist_not_supported_msi", new Object[0]);
                actionStr = "<table><tr><td nowrap><input disabled type=\"checkbox\" value=\"" + resourceID2 + "\" name=\"object_list\"><span style=\"position:relative;left:-14px;top:-3px;cursor:not-allowed\" onmouseover=\"return overlib('<div class=\\'overlib\\'>" + toolTip + "</div>', FULLHTML);\" onmouseout=\"return nd();\">&nbsp;&nbsp;&nbsp;&nbsp;</span></input></td></tr></table>";
            }
            else if (((String)tableContext.getAssociatedPropertyValue("MdAppGroupDetails.IDENTIFIER")).equals("com.manageengine.mdm.iosagent") || ((String)tableContext.getAssociatedPropertyValue("MdAppGroupDetails.IDENTIFIER")).equals("d73a6956-c81b-4bcb-ba8d-fe8718735ad7") || ((String)tableContext.getAssociatedPropertyValue("MdAppGroupDetails.IDENTIFIER")).equals("com.manageengine.mdm.android") || ((String)tableContext.getAssociatedPropertyValue("MdAppGroupDetails.IDENTIFIER")).equals("ZohoCorp.ManageEngineMDM_hfrrf6a1akhx2")) {
                final String toolTip = I18N.getMsg("mdm.inv.memdm_not_blacklisted", new Object[0]);
                actionStr = "<table><tr><td nowrap><input disabled type=\"checkbox\" value=\"" + resourceID2 + "\" name=\"object_list\"><span style=\"position:relative;left:-14px;top:-3px;cursor:not-allowed\" onmouseover=\"return overlib('<div class=\\'overlib\\'>" + toolTip + "</div>', FULLHTML);\" onmouseout=\"return nd();\">&nbsp;&nbsp;&nbsp;&nbsp;</span></input></td></tr></table>";
            }
            else if ((int)tableContext.getAssociatedPropertyValue("MdAppGroupDetails.PLATFORM_TYPE") == 1 && ((String)tableContext.getAssociatedPropertyValue("MdAppGroupDetails.IDENTIFIER")).equals("com.apple.mobilephone")) {
                final String toolTip = I18N.getMsg("mdm.inv.ios.phone_not_blacklisted", new Object[0]);
                actionStr = "<table><tr><td nowrap><input disabled type=\"checkbox\" value=\"" + resourceID2 + "\" name=\"object_list\"><span style=\"position:relative;left:-14px;top:-3px;cursor:not-allowed\" onmouseover=\"return overlib('<div class=\\'overlib\\'>" + toolTip + "</div>', FULLHTML);\" onmouseout=\"return nd();\">&nbsp;&nbsp;&nbsp;&nbsp;</span></input></td></tr></table>";
            }
            columnProperties.put("VALUE", actionStr);
        }
        if (columnalais.equals("ManagedDevice.PLATFORM_TYPE") && !viewname.equalsIgnoreCase("MDMDeviceToAllApp") && !viewname.equalsIgnoreCase("DevicesByApplication") && !viewname.equalsIgnoreCase("BlacklistedDevicesByApplication")) {
            String platformName = I18N.getMsg("dc.common.UNKNOWN", new Object[0]);
            final Integer platformType = (Integer)data;
            platformName = MDMUtil.getInstance().getPlatformColumnValue(platformType, isExport);
            columnProperties.put("VALUE", platformName);
        }
        if ((columnalais.equals("MdAppDetails.APP_NAME") || columnalais.equals("MdAppGroupDetails.GROUP_DISPLAY_NAME")) && (isExport == null || isExport.equalsIgnoreCase("false"))) {
            String appName = data + "";
            if (appName.length() > 25) {
                appName = appName.substring(0, 25) + "...";
                columnProperties.put("TRIMMED_VALUE", appName);
            }
            else {
                columnProperties.put("VALUE", appName);
            }
        }
        if (columnalais.equalsIgnoreCase("ACTIONS") && !viewname.equalsIgnoreCase("MDMAppList")) {
            if (viewname.equalsIgnoreCase("DeviceList")) {
                String actionStr2 = "";
                final Long resourceId = (Long)tableContext.getAssociatedPropertyValue("Resource.RESOURCE_ID");
                actionStr2 = "<a href=\"#\" onclick=\"editCustomDetails('" + resourceId + "')\"><img src=\"images/edit_icon.gif\" width=\"18\" height=\"15\" alt=\"" + I18N.getMsg("dc.mdm.device_mgmt.device.edit_details", new Object[0]) + "\" title=\"" + I18N.getMsg("dc.mdm.device_mgmt.device.edit_details", new Object[0]) + "\" height=\"17\" hspace=\"3\" vspace=\"0\" border=\"0\" align=\"absmiddle\" ></a>";
                columnProperties.put("VALUE", actionStr2);
            }
            else {
                String blacklist_count = "" + tableContext.getAssociatedPropertyValue("BLACKLIST_COUNT");
                String total_count = "" + tableContext.getAssociatedPropertyValue("INSTALLATION_COUNT");
                if (blacklist_count == null || blacklist_count.equals("null")) {
                    blacklist_count = "0";
                }
                if (total_count == null || total_count.equals("null")) {
                    total_count = "0";
                }
                String wcount = "" + tableContext.getAssociatedPropertyValue("WHITELIST_COUNT");
                if (wcount == null || wcount.equals("null")) {
                    wcount = "0";
                }
                final Long groupId = (Long)tableContext.getAssociatedPropertyValue("MdAppGroupDetails.APP_GROUP_ID");
                final String appName2 = (String)tableContext.getAssociatedPropertyValue("MdAppGroupDetails.GROUP_DISPLAY_NAME");
                String action = "<a href=\"javascript:markAppAction('" + groupId + "','" + appName2 + "','" + blacklist_count + "','" + wcount + "','" + total_count + "')\"  nowrap><img src=\"/images/action_dropdown.png\"  width=\"20\" height=\"16\" hspace=\"3\" vspace=\"0\" align=\"absmiddle\" ></a>";
                final Integer platformType2 = (Integer)tableContext.getAssociatedPropertyValue("MdAppGroupDetails.PLATFORM_TYPE");
                final Boolean isModernApp = (Boolean)tableContext.getAssociatedPropertyValue("MdAppGroupDetails.IS_MODERN_APP");
                if (platformType2 == 4) {
                    final String toolTip2 = I18N.getMsg("dc.mdm.inv.wp_blacklist_whitelist_not_supported", new Object[0]);
                    action = "<a onmouseover=\"return overlib('<div class=\\'overlib\\'>" + toolTip2 + "</div>', FULLHTML);\" onmouseout=\"return nd();\" style=\"cursor:not-allowed !important\"   nowrap><img style=\"cursor:not-allowed !important\" src=\"/images/action_dropdown_disabled.png\"  width=\"20\" height=\"16\" hspace=\"3\" vspace=\"0\" align=\"absmiddle\"></a>";
                }
                else if (!isModernApp) {
                    final String toolTip2 = I18N.getMsg("mdm.inv.blacklist_not_supported_msi", new Object[0]);
                    action = "<a onmouseover=\"return overlib('<div class=\\'overlib\\'>" + toolTip2 + "</div>', FULLHTML);\" onmouseout=\"return nd();\" style=\"cursor:not-allowed !important\"   nowrap><img style=\"cursor:not-allowed !important\" src=\"/images/action_dropdown_disabled.png\"  width=\"20\" height=\"16\" hspace=\"3\" vspace=\"0\" align=\"absmiddle\"></a>";
                }
                else if (((String)tableContext.getAssociatedPropertyValue("MdAppGroupDetails.IDENTIFIER")).equals("com.manageengine.mdm.iosagent") || ((String)tableContext.getAssociatedPropertyValue("MdAppGroupDetails.IDENTIFIER")).equals("d73a6956-c81b-4bcb-ba8d-fe8718735ad7") || ((String)tableContext.getAssociatedPropertyValue("MdAppGroupDetails.IDENTIFIER")).equals("com.manageengine.mdm.android") || ((String)tableContext.getAssociatedPropertyValue("MdAppGroupDetails.IDENTIFIER")).equals("ZohoCorp.ManageEngineMDM_hfrrf6a1akhx2")) {
                    final String toolTip2 = I18N.getMsg("mdm.inv.memdm_not_blacklisted", new Object[0]);
                    action = "<a onmouseover=\"return overlib('<div class=\\'overlib\\'>" + toolTip2 + "</div>', FULLHTML);\" onmouseout=\"return nd();\" style=\"cursor:not-allowed !important\"   nowrap><img style=\"cursor:not-allowed !important\" src=\"/images/action_dropdown_disabled.png\"  width=\"20\" height=\"16\" hspace=\"3\" vspace=\"0\" align=\"absmiddle\"></a>";
                }
                columnProperties.put("VALUE", action);
            }
        }
        if (columnalais.equals("MdDeviceInfo.AVAILABLE_DEVICE_CAPACITY")) {
            final Float freeSpace = (Float)data;
            if (freeSpace != null) {
                final DecimalFormat decimalFormat = new DecimalFormat("####0.00");
                columnProperties.put("VALUE", decimalFormat.format(freeSpace));
            }
            if (platformTypeVal == null || platformTypeVal.equals(3)) {
                columnProperties.put("VALUE", "--");
            }
        }
        if (columnalais.equals("MdDeviceInfo.USED_DEVICE_SPACE")) {
            final Float usedDeviceSpace = (Float)data;
            if (usedDeviceSpace != null && usedDeviceSpace.equals(0.0f) && (platformTypeVal == null || platformTypeVal.equals(3))) {
                columnProperties.put("VALUE", "--");
            }
        }
        if (columnalais.equalsIgnoreCase("MdDeviceInfo.BATTERY_LEVEL")) {
            final Float batteryLevel = (Float)tableContext.getAssociatedPropertyValue("MdDeviceInfo.BATTERY_LEVEL");
            if (batteryLevel != null && batteryLevel.equals(-1.0f)) {
                columnProperties.put("VALUE", "--");
            }
        }
        if (columnalais.equalsIgnoreCase("MdDeviceBatteryDetails.BATTERY_LEVEL")) {
            final Float batteryLevel = Float.parseFloat(String.valueOf(tableContext.getAssociatedPropertyValue("MdDeviceBatteryDetails.BATTERY_LEVEL")));
            if (batteryLevel != null && batteryLevel.equals(-1.0f)) {
                columnProperties.put("VALUE", "--");
            }
            else {
                int value = Math.round(batteryLevel);
                if (value > 100) {
                    value = 100;
                }
                String barcolor = "green";
                if (value < 20) {
                    barcolor = "#fc0303";
                }
                else if (value < 50) {
                    barcolor = "#fc8803";
                }
                else if (value < 80) {
                    barcolor = "#a1fc03";
                }
                if (isExport != null && isExport.equalsIgnoreCase("true")) {
                    columnProperties.put("VALUE", value + "%");
                }
                else {
                    columnProperties.put("VALUE", "<div class='battery-level-outer'><div class='battery-level' style='width:" + value + "%;background-color:" + barcolor + "'></div></div>" + value + "%");
                }
            }
        }
        if (columnalais.equalsIgnoreCase("MdDeviceBatteryDetails.BATTERY_STATE")) {
            final String batteryLevel2 = String.valueOf(tableContext.getAssociatedPropertyValue("MdDeviceBatteryDetails.BATTERY_STATE"));
            if (isExport != null && isExport.equalsIgnoreCase("true")) {
                String value2 = I18N.getMsg("mdm.battery.unknown", new Object[0]);
                if (batteryLevel2 != null && batteryLevel2.equals("1")) {
                    value2 = I18N.getMsg("mdm.battery.full", new Object[0]);
                }
                else if (batteryLevel2 != null && batteryLevel2.equals("2")) {
                    value2 = I18N.getMsg("mdm.battery.plugged", new Object[0]);
                }
                else if (batteryLevel2 != null && batteryLevel2.equals("3")) {
                    value2 = I18N.getMsg("mdm.battery.unplugged", new Object[0]);
                }
                columnProperties.put("VALUE", value2);
            }
            else if (batteryLevel2 != null && batteryLevel2.equals("1")) {
                columnProperties.put("VALUE", "<span color:'green'>" + I18N.getMsg("mdm.battery.full", new Object[0]) + "</span>");
            }
            else if (batteryLevel2 != null && batteryLevel2.equals("2")) {
                columnProperties.put("VALUE", "<span color:'#EF8F09'>" + I18N.getMsg("mdm.battery.plugged", new Object[0]) + "</span>");
            }
            else if (batteryLevel2 != null && batteryLevel2.equals("3")) {
                columnProperties.put("VALUE", "<span color:'green'>" + I18N.getMsg("mdm.battery.unplugged", new Object[0]) + "</span>");
            }
            else {
                columnProperties.put("VALUE", I18N.getMsg("mdm.battery.unknown", new Object[0]) + "<a class='tool-tip dep-tool-tip' + id=\"battery_status_" + tableContext.getRowIndex() + "\" href=\"#\" onmouseover=\"displayToolTip(this.id, '', 'mdm.battery.status_not_available')\" >\n" + "<img width=\"17\" height=\"17\" border=\"0\" align=\"absmiddle\" src=\"/images/help_small.gif\" alt=\"Help\" style=\"cursor:pointer\"/>\n" + "</a>");
            }
        }
        if (columnalais.equals("CustomGroup.RESOURCE_ID") && viewname.equalsIgnoreCase("DeviceList")) {
            final Long resourceID2 = (Long)tableContext.getAssociatedPropertyValue("ManagedDevice.RESOURCE_ID");
            final HashMap hashMap = (HashMap)tableContext.getViewContext().getRequest().getAttribute("ASSOCIATED_GROUP_NAMES");
            String groupName = "";
            List groupList = new ArrayList();
            if (hashMap != null && hashMap.get(resourceID2) != null) {
                groupList = hashMap.get(resourceID2);
            }
            if (groupList.size() != 0) {
                final Iterator item = groupList.iterator();
                groupName = item.next();
                while (item.hasNext()) {
                    groupName = groupName + " , " + item.next();
                }
            }
            else {
                groupName = "--";
            }
            columnProperties.put("VALUE", groupName);
        }
        if (columnalais.equals("MdInstalledAppResourceRel.SCOPE")) {
            final Integer appScope = (Integer)tableContext.getAssociatedPropertyValue("MdInstalledAppResourceRel.SCOPE");
            String sValue = "";
            if (viewname.equalsIgnoreCase("InstalledAndroidAppList")) {
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
            columnProperties.put("VALUE", sValue);
        }
        if ((columnalais.equals("MdNetworkInfo.DATA_ROAMING_ENABLED") || columnalais.equals("MdNetworkInfo.VOICE_ROAMING_ENABLED") || columnalais.equals("MdSecurityInfo.PASSCODE_COMPLAINT") || columnalais.equals("MdSecurityInfo.PASSCODE_COMPLAINT_PROFILES") || columnalais.equals("MdSecurityInfo.PASSCODE_PRESENT") || columnalais.equals("MdSecurityInfo.STORAGE_ENCRYPTION") || columnalais.equals("MdDeviceInfo.IS_SUPERVISED") || columnalais.equals("MdDeviceInfo.IS_PROFILEOWNER") || columnalais.equals("MdDeviceInfo.IS_DEVICE_LOCATOR_ENABLED") || columnalais.equals("MdDeviceInfo.IS_ACTIVATION_LOCK_ENABLED") || columnalais.equals("MdDeviceInfo.IS_DND_IN_EFFECT") || columnalais.equals("MdDeviceInfo.IS_ITUNES_ACCOUNT_ACTIVE") || columnalais.equals("MdNetworkInfo.IS_PERSONAL_HOTSPOT_ENABLED") || columnalais.equals("MDMDeviceFirmwareInfo.IS_FIRMWARE_PASSWORD_EXISTS") || columnalais.equals("MDMDeviceFileVaultInfo.IS_ENCRYPTION_ENABLED") || columnalais.equals("MDMDeviceFileVaultInfo.IS_PERSONAL_RECOVERY_KEY") || columnalais.equals("MDMDeviceFileVaultInfo.IS_INSTITUTION_RECOVERY_KEY")) && (isExport == null || isExport.equalsIgnoreCase("false")) && data != null) {
            final Boolean isEnabled = (Boolean)data;
            String statusImage = "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<img class=\"imageWH16\" align=\"absmiddle\" src=\"images/hasDS.gif\"/>&nbsp;" + I18N.getMsg("desktopcentral.common.Yes", new Object[0]);
            if (!isEnabled) {
                statusImage = "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<img align=\"absmiddle\" src=\"images/noDS.gif\"/>&nbsp;" + I18N.getMsg("desktopcentral.common.No", new Object[0]);
            }
            columnProperties.put("VALUE", statusImage);
        }
        if (columnalais.equals("MdDeviceInfo.IS_CLOUD_BACKUP_ENABLED") && data != null) {
            final Boolean isEnabled = (Boolean)data;
            String statusImage;
            if (isExport != null && isExport.equalsIgnoreCase("true")) {
                statusImage = I18N.getMsg("dc.common.Enabled", new Object[0]);
                if (!isEnabled) {
                    statusImage = I18N.getMsg("dc.common.Disabled", new Object[0]);
                }
            }
            else {
                statusImage = "<img align=\"absmiddle\" src=\"images/hasDS.gif\"/>&nbsp;" + I18N.getMsg("dc.common.Enabled", new Object[0]);
                if (!isEnabled) {
                    statusImage = "<img align=\"absmiddle\" src=\"images/noDS.gif\"/>&nbsp;" + I18N.getMsg("dc.common.Disabled", new Object[0]);
                }
            }
            columnProperties.put("VALUE", statusImage);
        }
        if (columnalais.equals("BACKUP_RESTRICTED_IN_DEVICE")) {
            final Integer platformType3 = (Integer)tableContext.getAssociatedPropertyValue("ManagedDevice.PLATFORM_TYPE");
            Integer isEnabled2 = 1;
            if (platformType3 == 1) {
                isEnabled2 = (Integer)tableContext.getAssociatedPropertyValue("BACKUP_RESTRICTED_IN_IOS_DEVICE");
            }
            String statusImage2 = "";
            if (isExport != null && isExport.equalsIgnoreCase("true")) {
                if (isEnabled2 == 1) {
                    statusImage2 = I18N.getMsg("desktopcentral.common.No", new Object[0]);
                }
                else {
                    statusImage2 = I18N.getMsg("desktopcentral.common.Yes", new Object[0]);
                }
                columnProperties.put("VALUE", statusImage2);
            }
            else if (isEnabled2 == 1) {
                columnProperties.put("VALUE", true);
            }
            else {
                columnProperties.put("VALUE", false);
            }
        }
        if (columnalais.equals("MdAppDetails.BUNDLE_SIZE") || columnalais.equals("MdInstalledAppResourceRel.DYNAMIC_SIZE")) {
            final Number dataVal = (Number)data;
            if (dataVal != null && dataVal.longValue() != 0L) {
                final long iBundledSize = dataVal.longValue();
                double dBundledSize = iBundledSize / 1048576.0;
                final long lBundledSize = Math.round(dBundledSize * 100.0);
                dBundledSize = lBundledSize / 100.0;
                columnProperties.put("VALUE", dBundledSize);
            }
            else {
                columnProperties.put("VALUE", "--");
            }
        }
        if (columnalais.equals("MdDeviceScanStatus.REMARKS") && data != null) {
            final Integer scanStatus = (Integer)tableContext.getAssociatedPropertyValue("MdDeviceScanStatus.SCAN_STATUS");
            Boolean isErr = Boolean.FALSE;
            if (scanStatus == 0) {
                isErr = Boolean.TRUE;
            }
            if (isExport == null && data != null && !data.equals("") && !data.equals("--") && isErr) {
                final Integer errorCode = (Integer)tableContext.getAssociatedPropertyValue("ErrorCodeToKBUrl.ERROR_CODE");
                if (errorCode != null) {
                    if (errorCode == 12202 || errorCode == 12203) {
                        SYMClientUtil.renderRemarksWithKB(tableContext, columnProperties, data, (String)null, (boolean)isErr, "dc.common.LEARN_MORE");
                    }
                    else {
                        SYMClientUtil.renderRemarksWithKB(tableContext, columnProperties, data, (String)null, (boolean)isErr);
                    }
                }
                else {
                    SYMClientUtil.renderRemarksWithKB(tableContext, columnProperties, data, (String)null, (boolean)isErr);
                }
            }
            else {
                columnProperties.put("VALUE", I18N.getMsg((String)data, new Object[0]));
            }
        }
        if (columnalais.equals("MdAppDetails.PLATFORM_TYPE") || (columnalais.equals("MdAppGroupDetails.PLATFORM_TYPE") && !viewname.equalsIgnoreCase("MDMAppList"))) {
            String platformName = I18N.getMsg("dc.common.UNKNOWN", new Object[0]);
            final Integer platformType = (Integer)data;
            platformName = MDMUtil.getInstance().getPlatformColumnValue(platformType, isExport);
            columnProperties.put("VALUE", platformName);
        }
        if (reportType != 4 && (columnalais.equals("ManagedDevice.PLATFORM_TYPE") || columnalais.equals("MdAppGroupDetails.PLATFORM_TYPE") || columnalais.equals("MdAppDetails.PLATFORM_TYPE"))) {
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
        if (columnalais.equalsIgnoreCase("INSTALLED_APP_COUNT")) {
            final Long resId = (Long)tableContext.getAssociatedPropertyValue("Resource.RESOURCE_ID");
            final String deviceName2 = (String)tableContext.getAssociatedPropertyValue("ManagedDeviceExtn.NAME");
            if (data == null) {
                data = 0;
            }
            if (isExport == null || isExport.equalsIgnoreCase("false")) {
                final JSONObject payloadData = new JSONObject();
                payloadData.put("value", data);
                payloadData.put("resId", (Object)resId.toString());
                payloadData.put("deviceName", (Object)deviceName2);
                columnProperties.put("PAYLOAD", payloadData);
            }
            else {
                columnProperties.put("VALUE", data);
            }
        }
        if (columnalais.equals("MdModelInfo.MODEL_TYPE") && data != null) {
            final int type = (int)data;
            String modelType = "--";
            if (data != null) {
                if (type == 1) {
                    modelType = I18N.getMsg("dc.mdm.actionlog.appmgmt.smartPhone", new Object[0]);
                }
                else if (type == 2) {
                    modelType = I18N.getMsg("dc.mdm.graphs.tablet", new Object[0]);
                }
                else if (type == 3) {
                    modelType = I18N.getMsg("dc.common.LAPTOP", new Object[0]);
                }
                else if (type == 4) {
                    modelType = I18N.getMsg("dc.common.DESKTOP", new Object[0]);
                }
                else if (type == 5) {
                    modelType = I18N.getMsg("dc.common.TV", new Object[0]);
                }
                else {
                    modelType = I18N.getMsg("dc.common.OTHERS", new Object[0]);
                }
            }
            columnProperties.put("VALUE", modelType);
        }
        if ((columnalais.equalsIgnoreCase("icon_column") || columnalais.equalsIgnoreCase("icon_columns")) && viewname.equalsIgnoreCase("MDMAppList")) {
            final String blacklist_type = String.valueOf(tableContext.getAssociatedPropertyValue("IS_BLACKLIST"));
            final JSONObject value3 = new JSONObject();
            switch (Integer.parseInt(blacklist_type)) {
                case 0: {
                    value3.put("blacklist", (Object)"global_blacklist");
                    columnProperties.put("PAYLOAD", value3);
                    break;
                }
                case 1: {
                    value3.put("blacklist", (Object)"partial_blacklist");
                    columnProperties.put("PAYLOAD", value3);
                    break;
                }
                default: {
                    value3.put("blacklist", (Object)"non_blacklist");
                    columnProperties.put("PAYLOAD", value3);
                    break;
                }
            }
        }
        if (columnalais.equalsIgnoreCase("WHITELIST_COUNT") && (isExport == null || isExport.equalsIgnoreCase("false"))) {
            final Long resId = (Long)tableContext.getAssociatedPropertyValue("Resource.RESOURCE_ID");
            String total = "" + tableContext.getAssociatedPropertyValue("TOTAL_APP_COUNT");
            if (total == null || total.equals("null")) {
                total = "0";
            }
            String blacklist = "" + tableContext.getAssociatedPropertyValue("BLACKLIST_COUNT");
            if (blacklist == null || blacklist.equals("null")) {
                blacklist = "0";
            }
            String whitelist = "" + tableContext.getAssociatedPropertyValue("WHITELIST_COUNT");
            if (whitelist == null || whitelist.equals("null")) {
                whitelist = "0";
            }
            final Integer platform = (Integer)tableContext.getAssociatedPropertyValue("ManagedDevice.PLATFORM_TYPE");
            if (data != null) {
                final String count = "<a href=\"javascript:showAppToDevice('" + resId + "'," + total + "," + whitelist + "," + blacklist + "," + platform + ",2)\">" + data + "</a>";
                columnProperties.put("VALUE", count);
            }
            else {
                columnProperties.put("VALUE", 0);
            }
        }
        if (columnalais.equalsIgnoreCase("BLACKLIST_COUNT") && (isExport == null || isExport.equalsIgnoreCase("false"))) {
            final Long resId = (Long)tableContext.getAssociatedPropertyValue("Resource.RESOURCE_ID");
            String total = "" + tableContext.getAssociatedPropertyValue("TOTAL_APP_COUNT");
            if (total == null || total.equals("null")) {
                total = "0";
            }
            String blacklist = "" + tableContext.getAssociatedPropertyValue("BLACKLIST_COUNT");
            if (blacklist == null || blacklist.equals("null")) {
                blacklist = "0";
            }
            String whitelist = "" + tableContext.getAssociatedPropertyValue("WHITELIST_COUNT");
            if (whitelist == null || whitelist.equals("null")) {
                whitelist = "0";
            }
            final Integer platform = (Integer)tableContext.getAssociatedPropertyValue("ManagedDevice.PLATFORM_TYPE");
            if (data != null) {
                final String count = "<a href=\"javascript:showAppToDevice('" + resId + "'," + total + "," + whitelist + "," + blacklist + "," + platform + ",1)\">" + data + "</a>";
                columnProperties.put("VALUE", count);
            }
            else {
                columnProperties.put("VALUE", 0);
            }
        }
        if ((columnalais.equalsIgnoreCase("TOTAL_APP_COUNT") || (columnalais.equalsIgnoreCase("ManagedDeviceExtn.NAME") && viewname.equalsIgnoreCase("MDMDeviceToAllApp"))) && (isExport == null || isExport.equalsIgnoreCase("false"))) {
            final Long resId = (Long)tableContext.getAssociatedPropertyValue("Resource.RESOURCE_ID");
            String total = "" + tableContext.getAssociatedPropertyValue("TOTAL_APP_COUNT");
            if (total == null || total.equals("null")) {
                total = "0";
            }
            String blacklist = "" + tableContext.getAssociatedPropertyValue("BLACKLIST_COUNT");
            if (blacklist == null || blacklist.equals("null")) {
                blacklist = "0";
            }
            String whitelist = "" + tableContext.getAssociatedPropertyValue("WHITELIST_COUNT");
            if (whitelist == null || whitelist.equals("null")) {
                whitelist = "0";
            }
            final Integer platform = (Integer)tableContext.getAssociatedPropertyValue("ManagedDevice.PLATFORM_TYPE");
            if (data != null) {
                final String count = "<a href=\"javascript:showAppToDevice('" + resId + "'," + total + "," + whitelist + "," + blacklist + "," + platform + ",0)\">" + data + "</a>";
                columnProperties.put("VALUE", count);
            }
            else {
                columnProperties.put("VALUE", 0);
            }
        }
        else if (columnalais.equalsIgnoreCase("DEVICE_NAME")) {
            columnProperties.put("VALUE", data);
        }
        if (columnalais.equalsIgnoreCase("INSTALLATION_COUNT") && (viewname.equalsIgnoreCase("ApplicationByDevice") || viewname.equalsIgnoreCase("ApplicationByDeviceSearch"))) {
            final Long appID = (Long)tableContext.getAssociatedPropertyValue("MdAppDetails.APP_ID");
            final String appName3 = (String)tableContext.getAssociatedPropertyValue("MdAppDetails.APP_NAME");
            final String appVersion = (String)tableContext.getAssociatedPropertyValue("MdAppDetails.APP_VERSION");
            if (data == null) {
                data = 0;
            }
            if (isExport == null || isExport.equalsIgnoreCase("false")) {
                final JSONObject payloadData2 = new JSONObject();
                payloadData2.put("value", data);
                payloadData2.put("appId", (Object)appID.toString());
                payloadData2.put("appName", (Object)appName3);
                payloadData2.put("appVersion", (Object)appVersion);
                columnProperties.put("PAYLOAD", payloadData2);
            }
            else {
                columnProperties.put("VALUE", data);
            }
        }
        if ((columnalais.equalsIgnoreCase("BLACKLIST_COUNT") || columnalais.equalsIgnoreCase("INSTALLATION_COUNT")) && viewname.equalsIgnoreCase("MDMAppRestrictList")) {
            final Long appGroupID = (Long)tableContext.getAssociatedPropertyValue("MdAppGroupDetails.APP_GROUP_ID");
            final String appName3 = (String)tableContext.getAssociatedPropertyValue("MdAppGroupDetails.GROUP_DISPLAY_NAME");
            String viewContent = "blacklistCount";
            if (columnalais.equalsIgnoreCase("INSTALLATION_COUNT")) {
                viewContent = "installationCount";
            }
            if (data == null) {
                data = 0;
            }
            if (isExport == null || isExport.equalsIgnoreCase("false")) {
                final JSONObject payloadData2 = new JSONObject();
                payloadData2.put("value", data);
                payloadData2.put("appName", (Object)appName3);
                payloadData2.put("appGroupId", (Object)appGroupID.toString());
                payloadData2.put("viewContent", (Object)viewContent);
                columnProperties.put("PAYLOAD", payloadData2);
            }
            else {
                columnProperties.put("VALUE", data);
            }
        }
        if (columnalais.equalsIgnoreCase("WHITELIST_COUNT") && viewname.equalsIgnoreCase("MDMAppList")) {
            final Long appGroupID = (Long)tableContext.getAssociatedPropertyValue("MdAppGroupDetails.APP_GROUP_ID");
            String blacklist_count2 = "" + tableContext.getAssociatedPropertyValue("BLACKLIST_COUNT");
            String installation_count = "" + tableContext.getAssociatedPropertyValue("INSTALLATION_COUNT");
            String wcount2 = "" + data;
            if (blacklist_count2 == null || blacklist_count2.equals("null")) {
                blacklist_count2 = "0";
            }
            if (installation_count == null || installation_count.equals("null")) {
                installation_count = "0";
            }
            if (wcount2 == null || wcount2.equals("null")) {
                wcount2 = "0";
            }
            final String appName2 = "" + tableContext.getAssociatedPropertyValue("MdAppGroupDetails.GROUP_DISPLAY_NAME");
            if (isExport != null && !isExport.equalsIgnoreCase("false")) {
                columnProperties.put("VALUE", wcount2);
            }
            else if (!wcount2.equals("0")) {
                columnProperties.put("VALUE", wcount2);
            }
            else {
                columnProperties.put("VALUE", 0);
            }
        }
        if (columnalais.equalsIgnoreCase("BlacklistAppCollectionStatus.NOTIFIED_COUNT") && (status == null || !status)) {
            final Long resID = (Long)tableContext.getAssociatedPropertyValue("MdBlackListAppInResource.RESOURCE_ID");
            final Integer maxNotification = MDMMailNotificationHandler.getInstance().getNotificationCount(resID);
            String value4 = "--";
            if (data != null) {
                try {
                    final Integer currentCount = (Integer)data;
                    if (currentCount == 0) {
                        value4 = I18N.getMsg("dc.mdm.blwl.email.blacklist_notification_yet_to_start", new Object[0]);
                    }
                    else if (currentCount >= maxNotification) {
                        value4 = I18N.getMsg("dc.mdm.blwl.email.blacklist_notification_expire", new Object[0]);
                    }
                    else {
                        value4 = data + "/" + maxNotification;
                    }
                }
                catch (final Exception ex) {
                    ex.printStackTrace();
                    value4 = data + "/" + maxNotification;
                }
            }
            else {
                value4 = I18N.getMsg("dc.mdm.blwl.email.blacklist_notification_expire", new Object[0]);
            }
            columnProperties.put("VALUE", value4);
        }
        if (columnalais.equalsIgnoreCase("ManagedDeviceExtn.PURCHASE_PRICE")) {
            String value5 = "--";
            if (data != null) {
                value5 = data + "$";
            }
            columnProperties.put("VALUE", value5);
        }
        if (columnalais.equalsIgnoreCase("MdInstalledAppResourceRel.USER_INSTALLED_APPS")) {
            String value5 = "--";
            if (data != null) {
                Integer appType = (Integer)data;
                final Long resID2 = (Long)tableContext.getAssociatedPropertyValue("RESOURCE_ID");
                if (resID2 != null) {
                    appType = 3;
                }
                value5 = AppSettingsDataHandler.getInstance().getAppViewLocalTransformerText(appType);
            }
            columnProperties.put("VALUE", value5);
        }
        if (columnalais.equals("MdDeviceInfo.EAS_DEVICE_IDENTIFIER") && data != null) {
            String easDeviceIdentifiershorten;
            final String easDeviceIdentifier = easDeviceIdentifiershorten = (String)data;
            if (easDeviceIdentifier.length() > 15) {
                easDeviceIdentifiershorten = "<span  onmouseover=\"showOverLib('" + easDeviceIdentifier + "');\" onmouseout=\"return nd();\">" + easDeviceIdentifier.substring(0, 15) + "..." + "</span> ";
            }
            final String easDeviceIdentifierWithCopy = "<span><span id='" + data + "' class=\"hide\" >" + easDeviceIdentifier + "</span>" + easDeviceIdentifiershorten + "<img src=\"/images/copy.png\" id='" + easDeviceIdentifier + "img" + "' hspace=\"5\" vspace=\"0\" width=\"12px\" style=\"cursor:pointer;\"  onmouseover=\"showOverLib('" + I18N.getMsg("mdm.enroll.zt.copy_json", new Object[0]) + "');\" onmouseout=\"return nd();\" onClick=\"copyTextFunction(this.id,'" + data + "');\"/></span>";
            if (isExport != null && isExport.equalsIgnoreCase("true")) {
                columnProperties.put("VALUE", easDeviceIdentifier);
            }
            else {
                columnProperties.put("VALUE", easDeviceIdentifierWithCopy);
            }
        }
        if (columnalais.equals("MdDeviceInfo.EAS_DEVICE_ID") && data != null) {
            if (reportType == 4) {
                columnProperties.put("PAYLOAD", data);
            }
            else {
                columnProperties.put("VALUE", data);
            }
        }
        if (columnalais.equalsIgnoreCase("DeviceKioskStateInfo.CURRENT_KIOSK_STATE")) {
            Integer kioskState = 3;
            if (data != null) {
                kioskState = (Integer)data;
            }
            if (platformTypeVal == 2) {
                switch (kioskState) {
                    case 1: {
                        columnProperties.put("VALUE", I18N.getMsg("mdm.kiosk.in_kiosk", new Object[0]));
                        break;
                    }
                    case 2: {
                        columnProperties.put("VALUE", I18N.getMsg("mdm.kiosk.kiosk_paused", new Object[0]));
                        break;
                    }
                    case 3: {
                        columnProperties.put("VALUE", I18N.getMsg("mdm.kiosk.not_in_kiosk", new Object[0]));
                        break;
                    }
                }
            }
            else {
                columnProperties.put("VALUE", "--");
            }
        }
        if (columnalais.equals("Checkbox")) {
            final JSONObject value6 = new JSONObject();
            final String bundleIdentifier = (String)tableContext.getAssociatedPropertyValue("MdAppGroupDetails.IDENTIFIER");
            if ((int)tableContext.getAssociatedPropertyValue("MdAppGroupDetails.PLATFORM_TYPE") == 4) {
                value6.put("toolTipMsg", (Object)"dc.mdm.inv.wp_blacklist_whitelist_not_supported");
                value6.put("isDisabled", true);
            }
            else if (!(boolean)tableContext.getAssociatedPropertyValue("MdAppGroupDetails.IS_MODERN_APP")) {
                value6.put("toolTipMsg", (Object)"mdm.inv.blacklist_not_supported_msi");
                value6.put("isDisabled", true);
            }
            else if (bundleIdentifier.equals("com.manageengine.mdm.iosagent") || bundleIdentifier.equals("d73a6956-c81b-4bcb-ba8d-fe8718735ad7") || bundleIdentifier.equals("com.manageengine.mdm.android") || bundleIdentifier.equals("ZohoCorp.ManageEngineMDM_hfrrf6a1akhx2") || bundleIdentifier.equals("com.manageengine.mdm.mac")) {
                value6.put("toolTipMsg", (Object)"mdm.inv.memdm_not_blacklisted");
                value6.put("isDisabled", true);
            }
            else if (bundleIdentifier.equals("com.apple.mobilephone")) {
                value6.put("toolTipMsg", (Object)"mdm.inv.ios.phone_not_blacklisted");
                value6.put("isDisabled", true);
            }
            else if (bundleIdentifier.equals("com.apple.Preferences")) {
                value6.put("isDisabled", true);
            }
            columnProperties.put("PAYLOAD", value6);
        }
        if (columnalais.equalsIgnoreCase("MdSecurityInfo.HARDWARE_ENCRYPTION_CAPS") && data != null && Integer.parseInt(String.valueOf(data)) == -1) {
            columnProperties.put("VALUE", "--");
        }
        if (columnalais.equalsIgnoreCase("MdDeviceLocationDetails.LATITUDE")) {
            final String longitude = (String)tableContext.getAssociatedPropertyValue("MdDeviceLocationDetails.LONGITUDE");
            if (data != null && !MDMUtil.isStringEmpty((String)data)) {
                final Long resourceID3 = (Long)tableContext.getAssociatedPropertyValue("Resource.RESOURCE_ID");
                data = data + ", " + longitude;
                if (MDMUtil.isStringEmpty(isExport)) {
                    isExport = "false";
                }
                if (isExport.equalsIgnoreCase("false")) {
                    final String actionStr3 = "<a ignorequickload=\"true\" href=\"#/uems/mdm/inventory/devicesList/" + resourceID3 + "/geo-tracking\" >" + data + "</a>";
                    columnProperties.put("VALUE", actionStr3);
                }
                else {
                    columnProperties.put("VALUE", data);
                }
            }
        }
        if ((columnalais.equalsIgnoreCase("SafetyNetStatus.SAFETYNET_BASIC_INTEGRITY") || columnalais.equalsIgnoreCase("SafetyNetStatus.SAFETYNET_CTS")) && (isExport == null || isExport.equalsIgnoreCase("false"))) {
            if (data != null) {
                final Boolean isEnabled = (Boolean)data;
                String statusImage = "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<img class=\"imageWH16\" align=\"absmiddle\" src=\"images/hasDS.gif\"/>&nbsp;" + I18N.getMsg("dc.mdm.reports.safetynet_passed", new Object[0]);
                if (!isEnabled) {
                    statusImage = "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<img align=\"absmiddle\" src=\"images/noDS.gif\"/>&nbsp;" + I18N.getMsg("dc.mdm.reports.safetynet_failed", new Object[0]);
                }
                columnProperties.put("VALUE", statusImage);
            }
            else {
                columnProperties.put("VALUE", "Not available");
            }
        }
        if (viewname.equalsIgnoreCase("ApplicationByDeviceSearch")) {
            if (columnalais.equalsIgnoreCase("MdAppDetails.APP_VERSION") && data != null) {
                final String appVersion2 = DMIAMEncoder.encodeHTML((String)data);
                columnProperties.put("VALUE", appVersion2);
            }
            if (columnalais.equalsIgnoreCase("MdAppDetails.IDENTIFIER") && data != null) {
                final String bundleIdentifier2 = DMIAMEncoder.encodeHTML((String)data);
                columnProperties.put("VALUE", bundleIdentifier2);
            }
            if (columnalais.equalsIgnoreCase("MdAppDetails.APP_NAME_SHORT_VERSION") && data != null) {
                final String appShortVersion = DMIAMEncoder.encodeHTML((String)data);
                columnProperties.put("VALUE", appShortVersion);
            }
            if (columnalais.equals("MdAppDetails.APP_NAME") && (isExport == null || isExport.equalsIgnoreCase("false"))) {
                String appName = data + "";
                if (appName.length() > 25) {
                    appName = appName.substring(0, 25) + "...";
                    columnProperties.put("TRIMMED_VALUE", DMIAMEncoder.encodeHTML(appName));
                }
                else {
                    columnProperties.put("VALUE", DMIAMEncoder.encodeHTML(appName));
                }
            }
        }
        if (columnalais.equals("MdDeviceLocationToErrCode.ERROR_CODE")) {
            boolean isGeoTrackingEnabled = (boolean)tableContext.getAssociatedPropertyValue("LocationDeviceStatus.IS_ENABLED");
            final Object lostStatusString = tableContext.getAssociatedPropertyValue("LostModeTrackInfo.TRACKING_STATUS");
            boolean isLost = false;
            if (lostStatusString != null) {
                final int lostStatus = Integer.parseInt(lostStatusString.toString());
                isLost = (lostStatus != 0 && lostStatus != 5 && lostStatus != 3);
            }
            isGeoTrackingEnabled = (isGeoTrackingEnabled || isLost);
            long lastLocatedTimeInMilli = 0L;
            if (viewname.equalsIgnoreCase("DeviceLocationHistoryList")) {
                if (tableContext.getAssociatedPropertyValue("RecentLocationDetails.LOCATED_TIME") != null) {
                    lastLocatedTimeInMilli = (long)tableContext.getAssociatedPropertyValue("RecentLocationDetails.LOCATED_TIME");
                }
            }
            else if (tableContext.getAssociatedPropertyValue("MdDeviceLocationDetails.LOCATED_TIME") != null) {
                lastLocatedTimeInMilli = (long)tableContext.getAssociatedPropertyValue("MdDeviceLocationDetails.LOCATED_TIME");
            }
            String errorCode2 = null;
            if (data != null) {
                errorCode2 = data.toString();
            }
            final Long resourceID4 = (Long)tableContext.getAssociatedPropertyValue("Resource.RESOURCE_ID");
            final String statusData = new GeoLocationFacade().getGeoStatus(errorCode2, isGeoTrackingEnabled, resourceID4, platformTypeVal, lastLocatedTimeInMilli);
            if (reportType == 4) {
                final JSONObject statusPayload = new JSONObject();
                statusPayload.put("data", (Object)statusData);
                if (isGeoTrackingEnabled && (errorCode2 != null || lastLocatedTimeInMilli <= DateTimeUtil.determine_From_To_Times("today").get("date2") - this.weekInMilli)) {
                    final String readKb = MDMUtil.replaceProductUrlLoaderValuesinText("$(mdmUrl)/help/security_management/location_tracking.html#location_tracking_reports", null);
                    statusPayload.put("readKB", (Object)readKb);
                }
                columnProperties.put("PAYLOAD", statusPayload);
            }
            else {
                columnProperties.put("VALUE", statusData);
            }
        }
    }
}
