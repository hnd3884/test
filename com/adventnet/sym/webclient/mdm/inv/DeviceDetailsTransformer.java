package com.adventnet.sym.webclient.mdm.inv;

import com.adventnet.sym.server.mdm.util.MDMStringUtils;
import com.adventnet.sym.server.mdm.core.ManagedDeviceHandler;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import java.text.DecimalFormat;
import org.json.JSONObject;
import java.util.HashMap;
import com.adventnet.i18n.I18N;
import javax.servlet.http.HttpServletRequest;
import com.me.mdm.server.util.MDMFeatureParamsHandler;
import com.adventnet.client.components.web.TransformerContext;
import java.util.Arrays;
import java.util.List;
import com.adventnet.client.components.table.web.DefaultTransformer;

public class DeviceDetailsTransformer extends DefaultTransformer
{
    public List showOnlyPlatformIconViews;
    public List platformColumnNames;
    public List yesNoStatusCellColumns;
    
    public DeviceDetailsTransformer() {
        this.showOnlyPlatformIconViews = Arrays.asList("stagedDeviceView", "DevicesAwaitingLicense", "pendingRequestView", "EnrollmentRequestSearch", "retiredView", "EnrollmentRequest", "mdmCertificateProfiles", "mdmCertificateDevices", "mdmDevicesAnnouncements", "LostModeDeviceList", "MDMInventoryAppList", "MDMDeviceToAllAppList");
        this.platformColumnNames = Arrays.asList("ManagedDevice.PLATFORM_TYPE", "DeviceEnrollmentRequest.PLATFORM_TYPE", "Profile.PLATFORM_TYPE", "MdAppDetails.PLATFORM_TYPE", "MdAppGroupDetails.PLATFORM_TYPE");
        this.yesNoStatusCellColumns = Arrays.asList("DMReferrals.REFERRAL_ZAAID", "DMReferrals.DOMAIN_VALID", "MdDeviceInfo.IS_SUPERVISED", "MdDeviceInfo.IS_DEVICE_LOCATOR_ENABLED", "MdDeviceInfo.IS_ACTIVATION_LOCK_ENABLED", "MdDeviceInfo.IS_DND_IN_EFFECT", "MdDeviceInfo.IS_ITUNES_ACCOUNT_ACTIVE", "MdNetworkInfo.DATA_ROAMING_ENABLED", "MdNetworkInfo.VOICE_ROAMING_ENABLED", "MdNetworkInfo.IS_PERSONAL_HOTSPOT_ENABLED", "MdNetworkInfo.ETHERNET_MACS", "MdSecurityInfo.PASSCODE_COMPLAINT", "MdSecurityInfo.PASSCODE_COMPLAINT_PROFILES", "MdSecurityInfo.PASSCODE_PRESENT", "MdSecurityInfo.STORAGE_ENCRYPTION", "MdSIMInfo.IS_ROAMING", "MDMDeviceFileVaultInfo.IS_ENCRYPTION_ENABLED", "MDMDeviceFileVaultInfo.IS_PERSONAL_RECOVERY_KEY", "MDMDeviceFileVaultInfo.IS_INSTITUTION_RECOVERY_KEY", "MDMDeviceFirmwareInfo.IS_FIRMWARE_PASSWORD_EXISTS", "BACKUP_RESTRICTED_IN_DEVICE", "MdDeviceInfo.IS_PROFILEOWNER", "MacBootrapToken.TOKEN", "MdDeviceInfo.IS_MULTIUSER");
    }
    
    protected boolean checkIfColumnRendererable(final TransformerContext tableContext) throws Exception {
        final String columnalias = tableContext.getPropertyName();
        final String viewName = tableContext.getViewContext().getUniqueId();
        final int reportType = tableContext.getViewContext().getRenderType();
        final HttpServletRequest request = tableContext.getViewContext().getRequest();
        final boolean hasInvWritePrivillage = request.isUserInRole("MDM_Inventory_Write") || request.isUserInRole("ModernMgmt_Inventory_Write");
        if (columnalias.equalsIgnoreCase("Actions")) {
            if (reportType != 4 || viewName.equalsIgnoreCase("DeviceListSearch")) {
                return false;
            }
            final String userId = request.getParameter("userId");
            if (userId != null && !userId.equalsIgnoreCase("")) {
                return Boolean.FALSE;
            }
            return hasInvWritePrivillage;
        }
        else {
            if (columnalias.equalsIgnoreCase("ManagedDevice.AGENT_TYPE")) {
                return false;
            }
            if (columnalias.equalsIgnoreCase("REMARKS")) {
                final Integer agentType = (Integer)tableContext.getAssociatedPropertyValue("ManagedDevice.AGENT_TYPE");
                return agentType != null && agentType == 3;
            }
            return (!columnalias.contains("MdNetworkInfo.WIFI_SSID") || MDMFeatureParamsHandler.getInstance().isFeatureEnabled("EnableFetchWifiSSSID")) && super.checkIfColumnRendererable(tableContext);
        }
    }
    
    public void renderHeader(final TransformerContext tableContext) {
        super.renderHeader(tableContext);
        final String columnalias = tableContext.getPropertyName();
        final String viewname = tableContext.getViewContext().getUniqueId();
        final HashMap headerProperties = tableContext.getRenderedAttributes();
        final int reportType = tableContext.getViewContext().getRenderType();
        try {
            if (this.platformColumnNames.contains(columnalias) && this.showOnlyPlatformIconViews.contains(viewname) && reportType != 4) {
                headerProperties.put("VALUE", I18N.getMsg("dc.mdm.device_mgmt.platform", new Object[0]));
            }
        }
        catch (final Exception ex) {}
    }
    
    public void renderCell(final TransformerContext tableContext) throws Exception {
        super.renderCell(tableContext);
        final HashMap columnProperties = tableContext.getRenderedAttributes();
        final String columnalais = tableContext.getPropertyName();
        Object data = tableContext.getPropertyValue();
        final Integer platformTypeVal = (Integer)tableContext.getAssociatedPropertyValue("ManagedDevice.PLATFORM_TYPE");
        final Integer deviceModel = (Integer)tableContext.getAssociatedPropertyValue("MdModelInfo.MODEL_TYPE");
        String readKB = (String)tableContext.getAssociatedPropertyValue("ErrorCodeToKBUrl.KB_URL");
        final String viewname = tableContext.getViewContext().getUniqueId();
        Integer scanStatus = (Integer)tableContext.getAssociatedPropertyValue("MdDeviceScanStatus.SCAN_STATUS");
        final Long scanStartTime = (Long)tableContext.getAssociatedPropertyValue("MdDeviceScanStatus.SCAN_START_TIME");
        final int reportType = tableContext.getViewContext().getRenderType();
        if (columnalais.equals("MdSIMInfo.SUBSCRIBER_MCC") && data == "") {
            columnProperties.put("VALUE", "--");
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
        if (this.platformColumnNames.contains(columnalais)) {
            String platformName = I18N.getMsg("dc.common.UNKNOWN", new Object[0]);
            Integer platformType = (Integer)data;
            String svgImage = "";
            if (platformType == null && columnalais.equalsIgnoreCase("DeviceEnrollmentRequest.PLATFORM_TYPE") && (viewname.equalsIgnoreCase("stagedDeviceView") || viewname.equalsIgnoreCase("pendingRequestView"))) {
                final Long knoxDfeId = (Long)tableContext.getAssociatedPropertyValue("KNOXMobileDeviceForEnrollment.ENROLLMENT_DEVICE_ID");
                final Long nfcDfeId = (Long)tableContext.getAssociatedPropertyValue("AndroidNFCDeviceForEnrollment.ENROLLMENT_DEVICE_ID");
                final Long appleConfigDfeId = (Long)tableContext.getAssociatedPropertyValue("AppleConfgDeviceForEnrollment.ENROLLMENT_DEVICE_ID");
                final Long winIcdDfeId = (Long)tableContext.getAssociatedPropertyValue("WindowsICDDeviceForEnrollment.ENROLLMENT_DEVICE_ID");
                final Long emmDfeId = (Long)tableContext.getAssociatedPropertyValue("AndroidQRDeviceForEnrollment.ENROLLMENT_DEVICE_ID");
                final Long ztLapDfeId = (Long)tableContext.getAssociatedPropertyValue("AndroidZTDeviceForEnrollment.ENROLLMENT_DEVICE_ID");
                final Long winAzureDfeId = (Long)tableContext.getAssociatedPropertyValue("WinAzureADDeviceForEnrollment.ENROLLMENT_DEVICE_ID");
                final Long depDfeId = (Long)tableContext.getAssociatedPropertyValue("AppleDEPDeviceForEnrollment.ENROLLMENT_DEVICE_ID");
                final Long winLapDfeId = (Long)tableContext.getAssociatedPropertyValue("WinLaptopDeviceForEnrollment.ENROLLMENT_DEVICE_ID");
                final Long chromeBookDfeId = (Long)tableContext.getAssociatedPropertyValue("GSChromeDeviceForEnrollment.ENROLLMENT_DEVICE_ID");
                final Long macDfeId = (Long)tableContext.getAssociatedPropertyValue("MacMgmtDeviceForEnrollment.ENROLLMENT_DEVICE_ID");
                if (knoxDfeId != null || nfcDfeId != null || emmDfeId != null || ztLapDfeId != null) {
                    platformType = 2;
                }
                else if (appleConfigDfeId != null || depDfeId != null || macDfeId != null) {
                    platformType = 1;
                }
                else if (winAzureDfeId != null || winIcdDfeId != null || winLapDfeId != null) {
                    platformType = 3;
                }
                else if (chromeBookDfeId != null) {
                    platformType = 4;
                }
            }
            if (platformType == 1) {
                svgImage = "ios";
                platformName = I18N.getMsg("mdm.os.Apple", new Object[0]);
                if (viewname.equalsIgnoreCase("ProfileList")) {
                    platformName = I18N.getMsg("mdm.os.ios.ipados", new Object[0]);
                }
            }
            else if (platformType == 2) {
                svgImage = "android";
                platformName = I18N.getMsg("dc.mdm.android", new Object[0]);
                if (viewname.equalsIgnoreCase("ProfileList") || viewname.equalsIgnoreCase("TrashProfileList")) {
                    final Integer profileType = (Integer)tableContext.getAssociatedPropertyValue("Profile.PROFILE_TYPE");
                    if (profileType == 10) {
                        svgImage = "OEM";
                    }
                }
            }
            else if (platformType == 3) {
                svgImage = "windows";
                platformName = I18N.getMsg("dc.common.WINDOWS", new Object[0]);
            }
            else if (platformType == 4) {
                svgImage = "chrome";
                platformName = I18N.getMsg("mdm.common.chrome", new Object[0]);
            }
            else if (platformType == 6) {
                svgImage = "Macos";
                platformName = I18N.getMsg("mdm.os.label_macos", new Object[0]);
            }
            else if (platformType == 7) {
                svgImage = "tvos";
                platformName = I18N.getMsg("mdm.os.label_apple_tvos", new Object[0]);
            }
            else if (platformType == 0) {
                svgImage = "unknown_platform";
                platformName = "--";
            }
            if (reportType == 4) {
                Boolean showIconOnly = false;
                if (this.showOnlyPlatformIconViews.contains(viewname)) {
                    showIconOnly = true;
                }
                final JSONObject payload = new JSONObject();
                payload.put("platformName", (Object)platformName);
                payload.put("platformType", (Object)platformType);
                payload.put("showIconOnly", (Object)showIconOnly);
                payload.put("svgImage", (Object)svgImage);
                columnProperties.put("PAYLOAD", payload);
            }
            else {
                columnProperties.put("VALUE", platformName);
            }
        }
        if (columnalais.equals("MdDeviceScanStatus.SCAN_STATUS")) {
            scanStatus = (Integer)data;
            String sScanStatus = "NA";
            String textClass = "";
            if (scanStatus != null) {
                if (scanStatus == 0) {
                    sScanStatus = I18N.getMsg("dc.db.config.status.failed", new Object[0]);
                    textClass = "ucs-table-status-text__failed";
                }
                else if (scanStatus == 1) {
                    sScanStatus = I18N.getMsg("dc.db.mdm.status.initiated", new Object[0]);
                    textClass = "ucs-table-status-text__ready";
                }
                else if (scanStatus == 4) {
                    sScanStatus = I18N.getMsg("dc.common.status.in_progress", new Object[0]);
                    textClass = "ucs-table-status-text__in-progress";
                }
                else if (scanStatus == 2) {
                    sScanStatus = I18N.getMsg("dc.db.config.status.succeeded", new Object[0]);
                    textClass = "ucs-table-status-text__success";
                }
                else {
                    sScanStatus = I18N.getMsg("dc.common.NOT_SCANNED", new Object[0]);
                    textClass = "orange";
                }
            }
            if (reportType == 4) {
                final JSONObject payload2 = new JSONObject();
                payload2.put("scanStatus", (Object)sScanStatus);
                payload2.put("textClass", (Object)textClass);
                columnProperties.put("PAYLOAD", payload2);
            }
            else {
                columnProperties.put("VALUE", sScanStatus);
            }
        }
        if (columnalais.equals("SafetyNetStatus.SAFETYNET_BASIC_INTEGRITY") || columnalais.equals("SafetyNetStatus.SAFETYNET_CTS")) {
            String value = I18N.getMsg("mdm.agent.efrp.not_applicable", new Object[0]);
            String styleClass = "ucs-table-status-text__not-applicable";
            if (data != null && (boolean)data) {
                value = I18N.getMsg("dc.mdm.reports.safetynet_passed", new Object[0]);
                styleClass = "ucs-table-status-text__success";
            }
            else if (data != null && !(boolean)data) {
                value = I18N.getMsg("dc.mdm.reports.safetynet_failed", new Object[0]);
                styleClass = "ucs-table-status-text__failed";
            }
            if (reportType == 4) {
                final JSONObject payloadData = new JSONObject();
                payloadData.put("statusLabel", (Object)value);
                payloadData.put("styleClass", (Object)styleClass);
                columnProperties.put("PAYLOAD", payloadData);
            }
            else {
                columnProperties.put("VALUE", value);
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
        if (columnalais.equalsIgnoreCase("MdDeviceInfo.BATTERY_LEVEL")) {
            final Float batteryLevel = (Float)tableContext.getAssociatedPropertyValue("MdDeviceInfo.BATTERY_LEVEL");
            if (batteryLevel != null && batteryLevel.equals(-1.0f)) {
                columnProperties.put("VALUE", "--");
            }
        }
        if (columnalais.equals("MdDeviceInfo.USED_DEVICE_SPACE")) {
            final Float usedDeviceSpace = (Float)data;
            if (usedDeviceSpace != null && usedDeviceSpace.equals(0.0f) && (platformTypeVal == null || platformTypeVal.equals(3))) {
                columnProperties.put("VALUE", "--");
            }
        }
        if (columnalais.equalsIgnoreCase("MdSecurityInfo.HARDWARE_ENCRYPTION_CAPS")) {
            if (data != null && Integer.parseInt(String.valueOf(data)) == -1) {
                columnProperties.put("VALUE", "--");
            }
            else if (Integer.parseInt(String.valueOf(data)) == 1) {
                columnProperties.put("VALUE", I18N.getMsg("mdm.encryption.block.level", new Object[0]));
            }
            else if (Integer.parseInt(String.valueOf(data)) == 2) {
                columnProperties.put("VALUE", I18N.getMsg("mdm.encryption.file.level", new Object[0]));
            }
            else if (Integer.parseInt(String.valueOf(data)) == 3) {
                columnProperties.put("VALUE", I18N.getMsg("mdm.encryption.both.block.file.level", new Object[0]));
            }
        }
        if (columnalais.equalsIgnoreCase("MdSecurityInfo.EFRP_STATUS")) {
            Integer efrpStatus = 3;
            if (data != null) {
                efrpStatus = (Integer)data;
            }
            String statusData = "";
            String textClass2 = "";
            switch (efrpStatus) {
                case 0: {
                    statusData = I18N.getMsg("mdm.agent.efrp.not_configured", new Object[0]);
                    textClass2 = "ucs-table-status-text__failed";
                    break;
                }
                case 1: {
                    statusData = I18N.getMsg("dc.mdm.device_mgmt.configured", new Object[0]);
                    textClass2 = "ucs-table-status-text__success";
                    break;
                }
                default: {
                    textClass2 = "ucs-table-status-text__not-applicable";
                    statusData = I18N.getMsg("mdm.agent.efrp.not_applicable", new Object[0]);
                    break;
                }
            }
            if (reportType == 4) {
                final JSONObject payload3 = new JSONObject();
                payload3.put("statusData", (Object)statusData);
                payload3.put("textClass", (Object)textClass2);
                columnProperties.put("PAYLOAD", payload3);
            }
            else {
                columnProperties.put("VALUE", statusData);
            }
        }
        if (columnalais.equalsIgnoreCase("DeviceKioskStateInfo.CURRENT_KIOSK_STATE")) {
            Integer kioskState = 3;
            String kioskStatus = "";
            String textClass2 = "";
            if (data != null) {
                kioskState = (Integer)data;
            }
            if (platformTypeVal == 2) {
                switch (kioskState) {
                    case 1: {
                        kioskStatus = I18N.getMsg("mdm.kiosk.enabled", new Object[0]);
                        textClass2 = "ucs-table-status-text__success";
                        break;
                    }
                    case 2: {
                        kioskStatus = I18N.getMsg("mdm.kiosk.kiosk_paused", new Object[0]);
                        textClass2 = "ucs-table-status-text__in-progress";
                        break;
                    }
                    case 3: {
                        kioskStatus = I18N.getMsg("mdm.lost.mode.lost_mode_not_enabled", new Object[0]);
                        textClass2 = "ucs-table-status-text__stopped";
                        break;
                    }
                }
            }
            else {
                kioskStatus = "--";
            }
            if (reportType == 4) {
                final JSONObject payload3 = new JSONObject();
                payload3.put("scanStatus", (Object)kioskStatus);
                payload3.put("textClass", (Object)textClass2);
                columnProperties.put("PAYLOAD", payload3);
            }
            else {
                columnProperties.put("VALUE", kioskStatus);
            }
        }
        if (columnalais.equals("MdDeviceScanStatus.REMARKS") && data != null) {
            final JSONObject payload4 = new JSONObject();
            final Long timeOutThreshold = Long.valueOf(SyMUtil.getSyMParameter("scanTimeOutThreshold"));
            if (scanStatus != null) {
                if (scanStatus == 4 && MDMUtil.getCurrentTimeInMillis() > scanStartTime + timeOutThreshold) {
                    data = "dc.db.mdm.scan.remarks.scan_time_out";
                    if (platformTypeVal == 2) {
                        data = "dc.db.mdm.scan.remarks.android_agent_delay";
                    }
                }
                else if (scanStatus == 1 && MDMUtil.getCurrentTimeInMillis() > scanStartTime + timeOutThreshold) {
                    data = "dc.db.mdm.scan.remarks.device_network_issue";
                    if (platformTypeVal == 2) {
                        readKB = "$(mdmUrl)/kb/mdm-unable-to-contact-android-device.html?$(did)";
                    }
                    else if (platformTypeVal == 3) {
                        readKB = "$(mdmUrl)/kb/mdm-wns-not-reachable.html";
                    }
                    else if (platformTypeVal == 1) {
                        readKB = "$(mdmUrl)/kb/mdm-unable-to-contact-ios-device.html";
                    }
                }
            }
            payload4.put("remarks", (Object)I18N.getMsg((String)data, new Object[0]));
            payload4.put("scanStatus", (Object)scanStatus.toString());
            if (readKB != null && scanStatus != 2) {
                payload4.put("readKB", (Object)MDMUtil.replaceProductUrlLoaderValuesinText(readKB, null));
                payload4.put("linkText", (Object)I18N.getMsg("dc.common.READ_KB", new Object[0]));
            }
            columnProperties.put("PAYLOAD", payload4);
            columnProperties.put("VALUE", I18N.getMsg((String)data, new Object[0]));
        }
        if (columnalais.equalsIgnoreCase("MdSecurityInfo.HARDWARE_ENCRYPTION_CAPS") && data != null && Integer.parseInt(String.valueOf(data)) == -1) {
            columnProperties.put("VALUE", "--");
        }
        if (columnalais.equals("MDDeviceManagementInfo.MANAGEMENT_TYPE") && data != null) {
            Integer managementType = 3;
            if (data != null) {
                managementType = (Integer)data;
                switch (managementType) {
                    case 1:
                    case 2: {
                        columnProperties.put("VALUE", "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<img class=\"imageWH16\" align=\"absmiddle\" src=\"images/hasDS.gif\"/>&nbsp;" + I18N.getMsg("dc.common.YES", new Object[0]));
                        break;
                    }
                    case -1:
                    case 3: {
                        columnProperties.put("VALUE", "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<img align=\"absmiddle\" src=\"images/noDS.gif\"/>&nbsp;" + I18N.getMsg("dc.common.NO", new Object[0]));
                        break;
                    }
                    default: {
                        columnProperties.put("VALUE", "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;" + I18N.getMsg("mdm.agent.efrp.not_applicable", new Object[0]));
                        break;
                    }
                }
            }
        }
        if (columnalais.equalsIgnoreCase("MdDeviceLocationDetails.LATITUDE")) {
            final String longitude = (String)tableContext.getAssociatedPropertyValue("MdDeviceLocationDetails.LONGITUDE");
            if (longitude != null && !MDMUtil.isStringEmpty(longitude) && data != null && !MDMUtil.isStringEmpty((String)data)) {
                data = data + ", " + longitude;
                columnProperties.put("VALUE", data);
            }
            else {
                columnProperties.put("VALUE", "--");
            }
        }
        if (columnalais.equalsIgnoreCase("MdDeviceInfo.IS_CLOUD_BACKUP_ENABLED")) {
            String cellValue = I18N.getMsg("dc.common.Disabled", new Object[0]);
            String textClass = "ucs-table-status-text__failed";
            if (data != null && data.toString() == "true") {
                textClass = "ucs-table-status-text__success";
                cellValue = I18N.getMsg("dc.common.ENABLED", new Object[0]);
            }
            if (reportType == 4) {
                final JSONObject payload2 = new JSONObject();
                payload2.put("cellData", (Object)cellValue);
                payload2.put("textClass", (Object)textClass);
                columnProperties.put("PAYLOAD", payload2);
            }
            else {
                columnProperties.put("VALUE", cellValue);
            }
        }
        if (columnalais.equalsIgnoreCase("MacBootstrapToken.TOKEN")) {
            final Object value2 = tableContext.getAssociatedPropertyValue(columnalais);
            final boolean isMac = (platformTypeVal == 1 && deviceModel.equals(3)) || deviceModel.equals(4);
            final String osVersion = (String)tableContext.getAssociatedPropertyValue("MdDeviceInfo.OS_VERSION");
            final boolean isBigSur = ManagedDeviceHandler.getInstance().isOsVersionGreaterThan(osVersion, 11.0f);
            final JSONObject payload = new JSONObject();
            payload.put("cellData", (isMac && isBigSur) ? ((value2 != null) ? 1 : 2) : -1);
            columnProperties.put("PAYLOAD", payload);
        }
        if (this.yesNoStatusCellColumns.contains(columnalais)) {
            String value = I18N.getMsg("dc.mdm.enroll.dep.no", new Object[0]);
            String textClass = "ucs-table-status-text__failed";
            Boolean checkOnlyNull = false;
            if (columnalais.equalsIgnoreCase("DMReferrals.REFERRAL_ZAAID")) {
                checkOnlyNull = true;
            }
            if ((data != null && data.toString() == "true" && data != "--") || (checkOnlyNull && data != null && data != "--")) {
                value = I18N.getMsg("dc.mdm.enroll.dep.yes", new Object[0]);
                textClass = "ucs-table-status-text__success";
            }
            if (reportType == 4) {
                final JSONObject payload3 = new JSONObject();
                payload3.put("cellData", (Object)value);
                payload3.put("textClass", (Object)textClass);
                columnProperties.put("PAYLOAD", payload3);
            }
            else {
                columnProperties.put("VALUE", value);
            }
        }
        if (columnalais.equalsIgnoreCase("ManagedKNOXContainer.KNOX_API_LEVEL") && data != null) {
            switch (Integer.parseInt(data.toString())) {
                case 0: {
                    columnProperties.put("VALUE", I18N.getMsg("mdm.yet_to_discover", new Object[0]));
                    break;
                }
                case -1: {
                    columnProperties.put("VALUE", I18N.getMsg("mdm.not_supported", new Object[0]));
                    break;
                }
                default: {
                    columnProperties.put("VALUE", data);
                    break;
                }
            }
        }
        if (columnalais.equalsIgnoreCase("LostModeTrackInfo.TRACKING_STATUS")) {
            String trackingStatus = "";
            String textClass = "";
            if (platformTypeVal == 3) {
                trackingStatus = I18N.getMsg("mdm.agent.efrp.not_applicable", new Object[0]);
                textClass = "ucs-table-status-text__not-applicable";
            }
            else if (data != null && Integer.parseInt(String.valueOf(data)) != -1) {
                final int lostModeStatus = Integer.parseInt(String.valueOf(data));
                switch (lostModeStatus) {
                    case 0: {
                        trackingStatus = I18N.getMsg("mdm.lost.mode.enable_lost_mode_cancelled", new Object[0]);
                        textClass = "ucs-table-status-text__stopped";
                        break;
                    }
                    case 1: {
                        trackingStatus = I18N.getMsg("mdm.lost.mode.enable_lost_mode_initiated", new Object[0]);
                        textClass = "ucs-table-status-text__in-progress";
                        break;
                    }
                    case 2: {
                        trackingStatus = I18N.getMsg("mdm.lost.mode.lost_mode_enabled", new Object[0]);
                        textClass = "ucs-table-status-text__success";
                        break;
                    }
                    case 3: {
                        trackingStatus = I18N.getMsg("mdm.lost.mode.enable_lost_mode_failed", new Object[0]);
                        textClass = "ucs-table-status-text__failed";
                        break;
                    }
                    case 4: {
                        trackingStatus = I18N.getMsg("mdm.lost.mode.disable_lost_mode_initiated", new Object[0]);
                        textClass = "ucs-table-status-text__in-progress";
                        break;
                    }
                    case 5: {
                        trackingStatus = I18N.getMsg("mdm.profile.disabled", new Object[0]);
                        textClass = "ucs-table-status-text__success";
                        break;
                    }
                    case 6: {
                        trackingStatus = I18N.getMsg("mdm.lost.mode.disable_lost_mode_failed", new Object[0]);
                        textClass = "ucs-table-status-text__failed";
                        break;
                    }
                }
            }
            else {
                trackingStatus = I18N.getMsg("mdm.lost.mode.lost_mode_not_enabled", new Object[0]);
                textClass = "ucs-table-status-text__ready";
            }
            if (reportType == 4) {
                final JSONObject payload2 = new JSONObject();
                payload2.put("scanStatus", (Object)trackingStatus);
                payload2.put("textClass", (Object)textClass);
                columnProperties.put("PAYLOAD", payload2);
            }
            else {
                columnProperties.put("VALUE", trackingStatus);
            }
        }
        if (columnalais.equalsIgnoreCase("BlacklistAppCollectionStatus.STATUS")) {
            String blacklistStatusValue = "";
            String textClass = "";
            if (data != null) {
                final Integer blacklistStatus = (Integer)data;
                switch (blacklistStatus) {
                    case 1: {
                        blacklistStatusValue = I18N.getMsg("dc.db.config.status.yet_to_apply", new Object[0]);
                        textClass = "ucs-table-status-text__ready";
                        break;
                    }
                    case 2: {
                        blacklistStatusValue = I18N.getMsg("mdm.blacklist.notified", new Object[0]);
                        textClass = "ucs-table-status-text__acknowledged";
                        break;
                    }
                    case 3: {
                        blacklistStatusValue = I18N.getMsg("mdm.blacklist.inprogress", new Object[0]);
                        textClass = "ucs-table-status-text__in-progress";
                        break;
                    }
                    case 4: {
                        blacklistStatusValue = I18N.getMsg("mdm.blacklist.blacklisted", new Object[0]);
                        textClass = "ucs-table-status-text__success";
                        break;
                    }
                    case 5: {
                        blacklistStatusValue = I18N.getMsg("dc.db.config.status.not_applicable", new Object[0]);
                        textClass = "ucs-table-status-text__not-applicable";
                        break;
                    }
                    case 6: {
                        blacklistStatusValue = I18N.getMsg("mdm.blacklist.yettoremove", new Object[0]);
                        textClass = "ucs-table-status-text__ready";
                        break;
                    }
                    case 7: {
                        blacklistStatusValue = I18N.getMsg("mdm.blacklist.removeinprogress", new Object[0]);
                        textClass = "ucs-table-status-text__in-progress";
                        break;
                    }
                    case 8: {
                        blacklistStatusValue = I18N.getMsg("mdm.blacklist.removed", new Object[0]);
                        textClass = "ucs-table-status-text__success";
                        break;
                    }
                    case 9: {
                        blacklistStatusValue = I18N.getMsg("mdm.blacklist.failed", new Object[0]);
                        textClass = "ucs-table-status-text__failed";
                        break;
                    }
                    case 10: {
                        blacklistStatusValue = I18N.getMsg("mdm.blacklist.removefailed", new Object[0]);
                        textClass = "ucs-table-status-text__failed";
                        break;
                    }
                    case 11: {
                        blacklistStatusValue = I18N.getMsg("mdm.blacklist.disabled", new Object[0]);
                        textClass = "ucs-table-status-text__success";
                        break;
                    }
                    default: {
                        blacklistStatusValue = "--";
                        break;
                    }
                }
            }
            if (reportType == 4) {
                final JSONObject payload2 = new JSONObject();
                payload2.put("scanStatus", (Object)blacklistStatusValue);
                payload2.put("textClass", (Object)textClass);
                columnProperties.put("PAYLOAD", payload2);
            }
            else {
                columnProperties.put("VALUE", blacklistStatusValue);
            }
        }
        if (columnalais.equalsIgnoreCase("MdDeviceInfo.PROCESSOR_CORE_COUNT") && data != null && !MDMStringUtils.isEmpty(data.toString())) {
            if (Integer.parseInt(data.toString()) != 0) {
                columnProperties.put("VALUE", data);
            }
            else {
                columnProperties.put("VALUE", "--");
            }
        }
        if (columnalais.equalsIgnoreCase("MdDeviceInfo.PROCESSOR_NAME")) {
            if (data != null && !MDMStringUtils.isEmpty(data.toString())) {
                columnProperties.put("VALUE", data);
            }
            else {
                columnProperties.put("VALUE", "--");
            }
        }
    }
}
