package com.adventnet.sym.webclient.mdm.enroll;

import java.util.Map;
import java.util.logging.Level;
import com.me.devicemanagement.framework.server.util.DMIAMEncoder;
import java.net.URLEncoder;
import com.adventnet.sym.webclient.mdm.enroll.adminenroll.UnassignedDeviceListTransformer;
import com.adventnet.i18n.I18N;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import org.json.simple.JSONObject;
import com.me.devicemanagement.framework.server.license.LicenseProvider;
import com.me.mdm.server.factory.MDMApiFactoryProvider;
import java.util.HashMap;
import com.adventnet.client.view.web.ViewContext;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import com.adventnet.client.components.web.TransformerContext;
import java.util.logging.Logger;
import com.adventnet.client.components.table.web.DefaultTransformer;

public class StagedDeviceViewTransformer extends DefaultTransformer
{
    public Logger logger;
    
    public StagedDeviceViewTransformer() {
        this.logger = Logger.getLogger("MDMEnrollment");
    }
    
    protected boolean checkIfColumnRendererable(final TransformerContext tableContext) throws Exception {
        final String columnalias = tableContext.getPropertyName();
        final int reportType = tableContext.getViewContext().getRenderType();
        final ViewContext viewCtx = tableContext.getViewContext();
        final String viewIdName = viewCtx.getUniqueId();
        if (viewIdName.equalsIgnoreCase("stagedDeviceViewSearch") && (columnalias.equalsIgnoreCase("DeviceEnrollmentRequest.ENROLLMENT_REQUEST_ID") || columnalias.equals("Action"))) {
            return false;
        }
        if (!columnalias.equalsIgnoreCase("checkbox") && !columnalias.equalsIgnoreCase("Action")) {
            return super.checkIfColumnRendererable(tableContext);
        }
        if (reportType != 4) {
            return false;
        }
        final boolean enrollWrite = ApiFactoryProvider.getAuthUtilAccessAPI().getRoles().contains("MDM_Enrollment_Write");
        final boolean enrollModernMgmtWrite = ApiFactoryProvider.getAuthUtilAccessAPI().getRoles().contains("ModernMgmt_Enrollment_Write");
        return enrollWrite || enrollModernMgmtWrite;
    }
    
    public void renderHeader(final TransformerContext tableContext) {
        final String columnalias = tableContext.getPropertyName();
        final HashMap headerProperties = tableContext.getRenderedAttributes();
        final int reportType = tableContext.getViewContext().getRenderType();
        if (columnalias.equalsIgnoreCase("DeviceEnrollmentRequest.PLATFORM_TYPE") && reportType == 4) {
            headerProperties.put("VALUE", "");
        }
        super.renderHeader(tableContext);
    }
    
    public void renderCell(final TransformerContext tableContext) {
        String columnName = null;
        try {
            super.renderCell(tableContext);
            final HashMap columnProperties = tableContext.getRenderedAttributes();
            final String isExport = MDMApiFactoryProvider.getMDMTableViewAPI().getIsExport(tableContext);
            final String columnalais = tableContext.getPropertyName();
            final int reportType = tableContext.getViewContext().getRenderType();
            Boolean isAssignUserEnabled = (Boolean)tableContext.getViewContext().getRequest().getAttribute("IS_ASSIGN_USER_ENABLED");
            Boolean isAssignUserForLaptopEnabled = (Boolean)tableContext.getViewContext().getRequest().getAttribute("IS_ASSIGN_USER_FOR_LAPTOP_ENABLED");
            if (isAssignUserEnabled == null) {
                isAssignUserEnabled = false;
            }
            if (isAssignUserForLaptopEnabled == null) {
                isAssignUserForLaptopEnabled = false;
            }
            final Object data = tableContext.getPropertyValue();
            columnName = columnalais;
            final boolean isProfessionalEdition = LicenseProvider.getInstance().getMDMLicenseAPI().isProfessionalLicenseEdition();
            if (columnalais.equals("DeviceEnrollmentRequest.PLATFORM_TYPE")) {
                Integer platformType = (Integer)data;
                if (platformType == null) {
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
                final JSONObject payload = new JSONObject();
                payload.put((Object)"platformType", (Object)platformType);
                if (reportType != 4) {
                    columnProperties.put("VALUE", MDMUtil.getInstance().getPlatformColumnValue(platformType, "true"));
                }
                else {
                    columnProperties.put("PAYLOAD", payload);
                }
            }
            final int a = 1;
            if (columnalais.equals("ManagedDevice.MANAGED_STATUS")) {
                String managedstatus = "--";
                final Long erid = (Long)tableContext.getAssociatedPropertyValue("DeviceEnrollmentRequest.ENROLLMENT_REQUEST_ID");
                final Long userId = (Long)tableContext.getAssociatedPropertyValue("DeviceEnrollmentToUser.MANAGED_USER_ID");
                final Long macDfeId2 = (Long)tableContext.getAssociatedPropertyValue("MacMgmtDeviceForEnrollment.ENROLLMENT_DEVICE_ID");
                final Long migrationDfeId = (Long)tableContext.getAssociatedPropertyValue("MigrationDeviceForEnrollment.ENROLLMENT_DEVICE_ID");
                final Integer enrollmentRequestStatus = (Integer)tableContext.getAssociatedPropertyValue("DeviceEnrollmentRequest.REQUEST_STATUS");
                final Integer type = (Integer)tableContext.getAssociatedPropertyValue("DeviceEnrollmentRequest.ENROLLMENT_TYPE");
                if (data != null && (int)data == 10) {
                    managedstatus = "dc.mdm.in_stock";
                }
                else if (data != null && (int)data == 9) {
                    managedstatus = "mdm.deprovision.in_repair";
                }
                else if (data != null && (int)data == 11) {
                    managedstatus = "dc.mdm.retired";
                }
                else if (userId == null && erid == null) {
                    if (migrationDfeId != null) {
                        managedstatus = I18N.getMsg("mdm.enroll.awaiting_migration", new Object[0]);
                    }
                    else {
                        managedstatus = I18N.getMsg("mdm.enroll.awaiting_both", new Object[0]);
                    }
                    if (macDfeId2 != null) {
                        managedstatus = I18N.getMsg("dc.mdm.enroll.admin_enrollment.status.awiting_user_assignment", new Object[0]);
                    }
                }
                else if (erid == null) {
                    managedstatus = I18N.getMsg("dc.mdm.enroll.admin_enrollment.status.awiting_device_enrollment", new Object[0]);
                    final Long winLapDfeId2 = (Long)tableContext.getAssociatedPropertyValue("WinLaptopDeviceForEnrollment.ENROLLMENT_DEVICE_ID");
                    if (winLapDfeId2 != null) {
                        final Integer dfeStatus = (Integer)tableContext.getAssociatedPropertyValue("DeviceForEnrollment.STATUS");
                        managedstatus = I18N.getMsg(new UnassignedDeviceListTransformer().getDeviceForEnrollmentStatusText(dfeStatus), new Object[0]);
                    }
                    if (macDfeId2 != null) {
                        managedstatus = I18N.getMsg("mdm.enroll.mac.pending_enrollment_status", new Object[0]);
                    }
                    if (migrationDfeId != null) {
                        managedstatus = I18N.getMsg("mdm.enroll.awaiting_migration", new Object[0]);
                    }
                }
                else if (userId == null && data != null && ((int)data == 5 || (int)data == 6)) {
                    managedstatus = I18N.getMsg("dc.mdm.enroll.admin_enrollment.status.awiting_user_assignment", new Object[0]);
                }
                else if (enrollmentRequestStatus != null && enrollmentRequestStatus == 1 && type == 4) {
                    final Integer dfeStatus2 = (Integer)tableContext.getAssociatedPropertyValue("DeviceForEnrollment.STATUS");
                    managedstatus = I18N.getMsg(new UnassignedDeviceListTransformer().getDeviceForEnrollmentStatusText(dfeStatus2), new Object[0]);
                }
                else if (enrollmentRequestStatus != null && enrollmentRequestStatus == 1) {
                    managedstatus = I18N.getMsg("mdm.enroll.assign_in_progess", new Object[0]);
                }
                else if (enrollmentRequestStatus != null && enrollmentRequestStatus == 0) {
                    managedstatus = I18N.getMsg("dc.mdm.enrollment_failed", new Object[0]);
                }
                final String value;
                managedstatus = (value = I18N.getMsg(managedstatus, new Object[0]));
                if (reportType == 4) {
                    final JSONObject payload2 = new JSONObject();
                    payload2.put((Object)"statusStr", (Object)managedstatus);
                    if (data == null || (int)data == 5 || (int)data == 6) {
                        if (enrollmentRequestStatus != null && enrollmentRequestStatus == 1) {
                            payload2.put((Object)"statusClass", (Object)"ucs-table-status-text__ready");
                        }
                        else if (enrollmentRequestStatus != null && enrollmentRequestStatus == 0 && data == null) {
                            payload2.put((Object)"statusClass", (Object)"ucs-table-status-text__failed");
                        }
                        else {
                            payload2.put((Object)"statusClass", (Object)"ucs-table-status-text__in-progress");
                        }
                    }
                    else {
                        payload2.put((Object)"statusClass", (Object)"ucs-table-status-text__in-progress");
                    }
                    columnProperties.put("PAYLOAD", payload2);
                }
                else {
                    columnProperties.put("VALUE", managedstatus);
                }
            }
            if (columnalais.equalsIgnoreCase("MdDeviceInfo.SERIAL_NUMBER")) {
                String serialNo = (String)tableContext.getAssociatedPropertyValue("MdDeviceInfo.SERIAL_NUMBER");
                if (serialNo == null || serialNo.equalsIgnoreCase("")) {
                    serialNo = (String)tableContext.getAssociatedPropertyValue("SERIAL_NUMBER");
                }
                columnProperties.put("VALUE", serialNo);
            }
            if (columnalais.equals("Action")) {
                final JSONObject payload = new JSONObject();
                final Long enrollmentRequestId = (Long)tableContext.getAssociatedPropertyValue("DeviceEnrollmentRequest.ENROLLMENT_REQUEST_ID");
                final Integer enrollmentRequestStatus2 = (Integer)tableContext.getAssociatedPropertyValue("DeviceEnrollmentRequest.REQUEST_STATUS");
                final Integer deviceStatus = (Integer)tableContext.getAssociatedPropertyValue("ManagedDevice.MANAGED_STATUS");
                int templateType = -1;
                final String assignStr = "";
                String assignUserTxt = null;
                final String imei = (String)tableContext.getAssociatedPropertyValue("MdDeviceInfo.IMEI");
                final String slno = (String)tableContext.getAssociatedPropertyValue("MdDeviceInfo.SERIAL_NUMBER");
                final String udid = (String)tableContext.getAssociatedPropertyValue("DeviceForEnrollment.UDID");
                final String easID = (String)tableContext.getAssociatedPropertyValue("DeviceForEnrollment.EAS_DEVICE_IDENTIFIER");
                if (deviceStatus != null && (deviceStatus == 10 || deviceStatus == 9)) {
                    final Object templateobj = tableContext.getAssociatedPropertyValue("EnrollmentTemplate.TEMPLATE_TYPE");
                    String userName = (String)tableContext.getAssociatedPropertyValue("ManagedUser.DISPLAY_NAME");
                    userName = URLEncoder.encode(MDMUtil.getInstance().encodeURIComponentEquivalent(userName), "UTF-8");
                    String emailAddress = (String)tableContext.getAssociatedPropertyValue("ManagedUser.EMAIL_ADDRESS");
                    emailAddress = URLEncoder.encode(MDMUtil.getInstance().encodeURIComponentEquivalent(emailAddress), "UTF-8");
                    final Long reqId = (Long)tableContext.getAssociatedPropertyValue("DeviceEnrollmentRequest.ENROLLMENT_REQUEST_ID");
                    if (templateobj != null && templateType == -1) {
                        templateType = (int)templateobj;
                    }
                    assignUserTxt = I18N.getMsg("mdm.enroll.re_enroll_device", new Object[0]);
                    if (templateType == 10 && deviceStatus == 9) {
                        payload.put((Object)"action", (Object)"assignDeviceDEP");
                    }
                    else if (templateType == 10 || templateType == 21) {
                        payload.put((Object)"action", (Object)"assignUser");
                    }
                    else if (templateType == 11 || templateType == 22 || templateType == 20 || templateType == 30 || templateType == 12 || templateType == 23 || templateType == 31 || templateType == 32) {
                        payload.put((Object)"action", (Object)"reEnrollDeviceAdmin");
                    }
                    else {
                        payload.put((Object)"action", (Object)"reEnrollDeviceInvitation");
                    }
                    Integer wipeType = (Integer)tableContext.getAssociatedPropertyValue("DeprovisionHistory.DEPROVISION_TYPE");
                    if (wipeType == null) {
                        wipeType = 0;
                    }
                    final String managedDeviceRemarks = (String)tableContext.getAssociatedPropertyValue("ManagedDevice.REMARKS");
                    final Boolean isWipePending = (Boolean)tableContext.getAssociatedPropertyValue("DeprovisionHistory.WIPE_PENDING");
                    final Long deprovisionTime = (Long)tableContext.getAssociatedPropertyValue("DeprovisionHistory.DEPROVISION_TIME");
                    if ((isWipePending != null && deprovisionTime != null && isWipePending && deprovisionTime < System.currentTimeMillis() - 14400000L) || managedDeviceRemarks.equalsIgnoreCase("dc.mdm.actionlog.securitycommands.wipe_failure")) {
                        if (wipeType == 1) {
                            assignUserTxt = I18N.getMsg("mdm.enroll.retry_corporate_wipe", new Object[0]);
                            payload.put((Object)"action", (Object)"retryCorporateWipe");
                        }
                        else if (wipeType == 2) {
                            assignUserTxt = I18N.getMsg("mdm.enroll.retry_corporate_wipe", new Object[0]);
                            payload.put((Object)"action", (Object)"retryCompleteWipe");
                        }
                    }
                }
                else if (deviceStatus == null && enrollmentRequestId != null && enrollmentRequestStatus2 == 1) {
                    assignUserTxt = I18N.getMsg("dc.mdm.dep.assign_device", new Object[0]);
                    payload.put((Object)"disabled", (Object)true);
                    payload.put((Object)"icon", (Object)"admin-enrollment");
                }
                else if (deviceStatus == null || deviceStatus == 5 || deviceStatus == 6) {
                    final Long knoxDfeId2 = (Long)tableContext.getAssociatedPropertyValue("KNOXMobileDeviceForEnrollment.ENROLLMENT_DEVICE_ID");
                    final Long nfcDfeId2 = (Long)tableContext.getAssociatedPropertyValue("AndroidNFCDeviceForEnrollment.ENROLLMENT_DEVICE_ID");
                    final Long appleConfigDfeId2 = (Long)tableContext.getAssociatedPropertyValue("AppleConfgDeviceForEnrollment.ENROLLMENT_DEVICE_ID");
                    final Long winIcdDfeId2 = (Long)tableContext.getAssociatedPropertyValue("WindowsICDDeviceForEnrollment.ENROLLMENT_DEVICE_ID");
                    final Long emmDfeId2 = (Long)tableContext.getAssociatedPropertyValue("AndroidQRDeviceForEnrollment.ENROLLMENT_DEVICE_ID");
                    final Long ztLapDfeId2 = (Long)tableContext.getAssociatedPropertyValue("AndroidZTDeviceForEnrollment.ENROLLMENT_DEVICE_ID");
                    final Long winAzureDfeId2 = (Long)tableContext.getAssociatedPropertyValue("WinAzureADDeviceForEnrollment.ENROLLMENT_DEVICE_ID");
                    final Long depDfeId2 = (Long)tableContext.getAssociatedPropertyValue("AppleDEPDeviceForEnrollment.ENROLLMENT_DEVICE_ID");
                    final Long winLapDfeId3 = (Long)tableContext.getAssociatedPropertyValue("WinLaptopDeviceForEnrollment.ENROLLMENT_DEVICE_ID");
                    final Long chromeBookDfeId2 = (Long)tableContext.getAssociatedPropertyValue("GSChromeDeviceForEnrollment.ENROLLMENT_DEVICE_ID");
                    final Long macDfeId3 = (Long)tableContext.getAssociatedPropertyValue("MacMgmtDeviceForEnrollment.ENROLLMENT_DEVICE_ID");
                    final Long winmmDfeId = (Long)tableContext.getAssociatedPropertyValue("WinModernMgmtDeviceForEnrollment.ENROLLMENT_DEVICE_ID");
                    final Long migrationDfeId2 = (Long)tableContext.getAssociatedPropertyValue("MigrationDeviceForEnrollment.ENROLLMENT_DEVICE_ID");
                    if (knoxDfeId2 != null) {
                        templateType = 21;
                    }
                    else if (nfcDfeId2 != null) {
                        templateType = 20;
                    }
                    else if (appleConfigDfeId2 != null) {
                        templateType = 11;
                    }
                    else if (winIcdDfeId2 != null) {
                        templateType = 30;
                    }
                    else if (emmDfeId2 != null) {
                        templateType = 22;
                    }
                    else if (ztLapDfeId2 != null) {
                        templateType = 23;
                    }
                    else if (winAzureDfeId2 != null) {
                        templateType = 32;
                    }
                    else if (depDfeId2 != null) {
                        templateType = 10;
                    }
                    else if (winLapDfeId3 != null) {
                        templateType = 31;
                    }
                    else if (chromeBookDfeId2 != null) {
                        templateType = 40;
                    }
                    else if (macDfeId3 != null) {
                        templateType = 12;
                    }
                    else if (winmmDfeId != null) {
                        templateType = 33;
                    }
                    else if (migrationDfeId2 != null) {
                        templateType = 50;
                    }
                    final Long userId2 = (Long)tableContext.getAssociatedPropertyValue("DeviceEnrollmentToUser.MANAGED_USER_ID");
                    if (userId2 != null) {
                        assignUserTxt = I18N.getMsg("dc.mdm.enroll.action.reassign_user", new Object[0]);
                    }
                    else {
                        assignUserTxt = I18N.getMsg("dc.mdm.dep.assign_device", new Object[0]);
                    }
                    payload.put((Object)"action", (Object)"assignDevice");
                    payload.put((Object)"templateType", (Object)templateType);
                }
                payload.put((Object)"assignUserTxt", (Object)assignUserTxt);
                final boolean isSelfEnrollEnabledForABMDevice = this.isSelfEnrollEnabledForDepDevice(tableContext);
                boolean isLaptopType = false;
                if (templateType == 12 || templateType == 31 || templateType == 32 || templateType == 30) {
                    isLaptopType = true;
                }
                if (templateType == 12 && !isProfessionalEdition) {
                    payload.put((Object)"disabled", (Object)true);
                    payload.put((Object)"disabledText", (Object)I18N.getMsg("dc.mdm.enroll.UNABLE_TO_ASSIGN_USER_INSUFFICIENT_LICENSE", new Object[0]));
                }
                else if (isLaptopType) {
                    final boolean isDisabled = !isAssignUserForLaptopEnabled;
                    if (isDisabled) {
                        payload.put((Object)"disabled", (Object)true);
                        payload.put((Object)"disabledText", (Object)I18N.getMsg("mdm.modernmgmt.lic_for_non_phone_txt", new Object[0]));
                    }
                }
                else if (enrollmentRequestStatus2 != null && enrollmentRequestStatus2 == 0 && deviceStatus == null) {
                    payload.put((Object)"disabled", (Object)true);
                }
                else if (isSelfEnrollEnabledForABMDevice) {
                    payload.put((Object)"disabled", (Object)true);
                    payload.put((Object)"tooltipMessage", (Object)I18N.getMsg("dc.mdm.msg.abm_self_enroll_enabled", new Object[0]));
                }
                else {
                    Boolean isDisabledAlready = false;
                    if (payload.containsKey((Object)"disabled")) {
                        isDisabledAlready = (Boolean)payload.get((Object)"disabled");
                    }
                    payload.put((Object)"disabled", (Object)(!isAssignUserEnabled || isDisabledAlready));
                }
                if (!isAssignUserEnabled) {
                    payload.put((Object)"tooltipMessage", (Object)I18N.getMsg("dc.mdm.msg.reg_license_reached.title", new Object[0]));
                }
                if (reportType != 4) {
                    columnProperties.put("VALUE", assignUserTxt);
                }
                else {
                    columnProperties.put("PAYLOAD", payload);
                }
            }
            if (columnalais.equalsIgnoreCase("ManagedUser.EMAIL_ADDRESS")) {
                String emailAddress2 = (String)data;
                emailAddress2 = ((emailAddress2 == null || emailAddress2.equalsIgnoreCase("")) ? "--" : emailAddress2);
                String mailAddressText = DMIAMEncoder.encodeHTML(emailAddress2);
                if (emailAddress2.length() > 15) {
                    final Long edid = (Long)tableContext.getAssociatedPropertyValue("DeviceForEnrollment.ENROLLMENT_DEVICE_ID");
                    final String uniqueID = "stagedmailAddr_" + edid;
                    final String trimmedValue = emailAddress2.substring(0, 15) + " ...";
                    mailAddressText = "<div style=\"width: 115px;\" id=\"" + uniqueID + "\"><span onmouseover=\"javascript:customShowCompleteMessage('" + uniqueID + "', '" + DMIAMEncoder.encodeHTML(emailAddress2) + "');return false\" onmouseout=\"hideCustomMessage();return false;\">" + trimmedValue + "</span></div>";
                }
                if (isExport == null || isExport.equalsIgnoreCase("false")) {
                    columnProperties.put("VALUE", mailAddressText);
                }
                else {
                    columnProperties.put("VALUE", emailAddress2);
                }
            }
            if (columnalais.equals("ManagedUser.DISPLAY_NAME")) {
                String val = (String)data;
                val = ((val == null || val.equalsIgnoreCase("")) ? "--" : val);
                columnProperties.put("VALUE", DMIAMEncoder.encodeHTML(val));
            }
            if (columnalais.equals("DeprovisionHistory.COMMENT")) {
                String val = (String)data;
                final Integer wipeType2 = (Integer)tableContext.getAssociatedPropertyValue("DeprovisionHistory.DEPROVISION_TYPE");
                if (val.equalsIgnoreCase("default")) {
                    if (wipeType2 == 1) {
                        val = I18N.getMsg("mdm.inv.corp_wipe_deprovision_comment", new Object[0]);
                    }
                    else {
                        val = I18N.getMsg("mdm.inv.comp_wipe_deprovision_comment", new Object[0]);
                    }
                }
                columnProperties.put("VALUE", DMIAMEncoder.encodeHTML(val));
            }
            if (columnalais.equals("ManagedDevice.REMARKS")) {
                final JSONObject payload = new JSONObject();
                Integer managedStatus = (Integer)tableContext.getAssociatedPropertyValue("ManagedDevice.MANAGED_STATUS");
                final Long macDfeId4 = (Long)tableContext.getAssociatedPropertyValue("MacMgmtDeviceForEnrollment.ENROLLMENT_DEVICE_ID");
                final Long migrationDfeId3 = (Long)tableContext.getAssociatedPropertyValue("MigrationDeviceForEnrollment.ENROLLMENT_DEVICE_ID");
                managedStatus = ((managedStatus != null) ? managedStatus : -1);
                final boolean isSelfEnrollEnabledForABMDevice2 = this.isSelfEnrollEnabledForDepDevice(tableContext);
                String val2 = "";
                if (managedStatus == 10 || managedStatus == 9) {
                    val2 = (String)data;
                    val2 = I18N.getMsg(val2, new Object[0]);
                }
                else {
                    final Long erid2 = (Long)tableContext.getAssociatedPropertyValue("DeviceEnrollmentRequest.ENROLLMENT_REQUEST_ID");
                    final Integer type2 = (Integer)tableContext.getAssociatedPropertyValue("DeviceEnrollmentRequest.ENROLLMENT_TYPE");
                    final Long userId3 = (Long)tableContext.getAssociatedPropertyValue("DeviceEnrollmentToUser.MANAGED_USER_ID");
                    final Integer enrollmentRequestStatus3 = (Integer)tableContext.getAssociatedPropertyValue("DeviceEnrollmentRequest.REQUEST_STATUS");
                    if (val2.equalsIgnoreCase("") && userId3 == null && erid2 == null) {
                        val2 = I18N.getMsg("dc.mdm.enroll.admin.enrollment.status.awiting.both", new Object[0]);
                        if (macDfeId4 != null) {
                            val2 = I18N.getMsg("mdm.enroll.pending_user_remarks", new Object[0]);
                        }
                        if (migrationDfeId3 != null) {
                            val2 = I18N.getMsg("mdm.enroll.awaiting_migration_remarks", new Object[0]);
                        }
                        if (isSelfEnrollEnabledForABMDevice2) {
                            val2 = I18N.getMsg("mdm.enroll.awaiting_activation_remarks", new Object[0]);
                            payload.put((Object)"tooltipMessage", (Object)I18N.getMsg("mdm.enrollment.dep.awaitingDeviceActivation.desc", new Object[0]));
                        }
                    }
                    else if (val2.equalsIgnoreCase("") && erid2 == null) {
                        val2 = I18N.getMsg("mdm.enroll.awaiting_activation_remarks", new Object[0]);
                        if (migrationDfeId3 == null) {
                            payload.put((Object)"tooltipMessage", (Object)I18N.getMsg("mdm.enrollment.dep.awaitingDeviceActivation.desc", new Object[0]));
                        }
                        final Object enrollmentTemplate = tableContext.getAssociatedPropertyValue("EnrollmentTemplate.TEMPLATE_TYPE");
                        if (enrollmentTemplate == null || ((int)enrollmentTemplate != 31 && (int)enrollmentTemplate != 33 && (int)enrollmentTemplate != 12)) {
                            payload.put((Object)"tooltipMessage", (Object)I18N.getMsg("mdm.enrollment.dep.awaitingDeviceActivation.desc", new Object[0]));
                        }
                        final Long winLapDfeId4 = (Long)tableContext.getAssociatedPropertyValue("WinLaptopDeviceForEnrollment.ENROLLMENT_DEVICE_ID");
                        if (winLapDfeId4 != null && erid2 == null) {
                            val2 = I18N.getMsg((String)tableContext.getAssociatedPropertyValue("DeviceForEnrollment.REMARKS"), new Object[0]);
                        }
                        if (macDfeId4 != null) {
                            val2 = I18N.getMsg("mdm.enroll.mac.pending_enrollment_remarks", new Object[0]);
                        }
                        if (migrationDfeId3 != null) {
                            val2 = I18N.getMsg("mdm.enroll.awaiting_migration_remarks", new Object[0]);
                        }
                    }
                    else if (val2.equalsIgnoreCase("") && userId3 == null && (managedStatus == 5 || managedStatus == 6)) {
                        val2 = I18N.getMsg("mdm.enroll.pending_user_remarks", new Object[0]);
                    }
                    else if (val2.equalsIgnoreCase("") && enrollmentRequestStatus3 != null && enrollmentRequestStatus3 == 1 && type2 == 4) {
                        val2 = I18N.getMsg((String)tableContext.getAssociatedPropertyValue("DeviceForEnrollment.REMARKS"), new Object[0]);
                    }
                    else if (val2.equalsIgnoreCase("") && enrollmentRequestStatus3 != null && enrollmentRequestStatus3 == 1) {
                        val2 = I18N.getMsg("mdm.enroll.assign_in_progess_remarks", new Object[0]);
                    }
                    else if (val2.equalsIgnoreCase("") && enrollmentRequestStatus3 != null && enrollmentRequestStatus3 == 0) {
                        val2 = I18N.getMsg((String)tableContext.getAssociatedPropertyValue("DeviceEnrollmentRequest.REMARKS"), new Object[0]);
                    }
                }
                final String actionStr = "";
                payload.put((Object)"messageData", (Object)val2);
                if (reportType != 4) {
                    columnProperties.put("VALUE", val2);
                }
                else {
                    columnProperties.put("PAYLOAD", payload);
                }
            }
            if (columnalais.equals("MdDeviceInfo.IMEI")) {
                String val = (String)data;
                val = ((val == null || val.equalsIgnoreCase("")) ? "--" : val);
                columnProperties.put("VALUE", val);
            }
            if (columnalais.equals("ManagedUser.DISPLAY_NAME")) {
                String val = (String)data;
                val = ((val == null || val.equalsIgnoreCase("")) ? "--" : val);
                columnProperties.put("VALUE", val);
            }
            if (columnalais.equals("EnrollmentTemplate.TEMPLATE_TYPE")) {
                String val = I18N.getMsg("mdm.enroll.by_invite", new Object[0]);
                final Object enrollmentTemplateobject = tableContext.getAssociatedPropertyValue("EnrollmentTemplate.TEMPLATE_TYPE");
                final Long knoxDfeId3 = (Long)tableContext.getAssociatedPropertyValue("KNOXMobileDeviceForEnrollment.ENROLLMENT_DEVICE_ID");
                final Long nfcDfeId3 = (Long)tableContext.getAssociatedPropertyValue("AndroidNFCDeviceForEnrollment.ENROLLMENT_DEVICE_ID");
                final Long appleConfigDfeId3 = (Long)tableContext.getAssociatedPropertyValue("AppleConfgDeviceForEnrollment.ENROLLMENT_DEVICE_ID");
                final Long winIcdDfeId3 = (Long)tableContext.getAssociatedPropertyValue("WindowsICDDeviceForEnrollment.ENROLLMENT_DEVICE_ID");
                final Long emmDfeId3 = (Long)tableContext.getAssociatedPropertyValue("AndroidQRDeviceForEnrollment.ENROLLMENT_DEVICE_ID");
                final Long ztLapDfeId3 = (Long)tableContext.getAssociatedPropertyValue("AndroidZTDeviceForEnrollment.ENROLLMENT_DEVICE_ID");
                final Long winAzureDfeId3 = (Long)tableContext.getAssociatedPropertyValue("WinAzureADDeviceForEnrollment.ENROLLMENT_DEVICE_ID");
                final Long depDfeId3 = (Long)tableContext.getAssociatedPropertyValue("AppleDEPDeviceForEnrollment.ENROLLMENT_DEVICE_ID");
                final Long winLapDfeId5 = (Long)tableContext.getAssociatedPropertyValue("WinLaptopDeviceForEnrollment.ENROLLMENT_DEVICE_ID");
                final Long chromeBookDfeId3 = (Long)tableContext.getAssociatedPropertyValue("GSChromeDeviceForEnrollment.ENROLLMENT_DEVICE_ID");
                final Long macDfeId5 = (Long)tableContext.getAssociatedPropertyValue("MacMgmtDeviceForEnrollment.ENROLLMENT_DEVICE_ID");
                final Long winmmDFEId = (Long)tableContext.getAssociatedPropertyValue("WinModernMgmtDeviceForEnrollment.ENROLLMENT_DEVICE_ID");
                final Long migrationDfeId4 = (Long)tableContext.getAssociatedPropertyValue("MigrationDeviceForEnrollment.ENROLLMENT_DEVICE_ID");
                if (knoxDfeId3 != null || (enrollmentTemplateobject != null && (int)enrollmentTemplateobject == 21)) {
                    val = I18N.getMsg("mdm.enroll.knox", new Object[0]);
                }
                else if (nfcDfeId3 != null || (enrollmentTemplateobject != null && (int)enrollmentTemplateobject == 20)) {
                    val = I18N.getMsg("dc.mdm.enroll.android_admin_enrollment", new Object[0]);
                }
                else if (appleConfigDfeId3 != null || (enrollmentTemplateobject != null && (int)enrollmentTemplateobject == 11)) {
                    val = I18N.getMsg("dc.mdm.enroll.apple_configurator", new Object[0]);
                }
                else if (winIcdDfeId3 != null || (enrollmentTemplateobject != null && (int)enrollmentTemplateobject == 30)) {
                    val = I18N.getMsg("mdm.common.WINDOWS_10", new Object[0]);
                }
                else if (emmDfeId3 != null || (enrollmentTemplateobject != null && (int)enrollmentTemplateobject == 22)) {
                    val = I18N.getMsg("mdm.enroll.emm", new Object[0]);
                }
                else if (ztLapDfeId3 != null || (enrollmentTemplateobject != null && (int)enrollmentTemplateobject == 23)) {
                    val = I18N.getMsg("mdm.enroll.zerotouch", new Object[0]);
                }
                else if (winAzureDfeId3 != null || (enrollmentTemplateobject != null && (int)enrollmentTemplateobject == 32)) {
                    val = I18N.getMsg("mdm.enroll.autopilot", new Object[0]);
                }
                else if (depDfeId3 != null || (enrollmentTemplateobject != null && (int)enrollmentTemplateobject == 10)) {
                    val = this.getDepServerTypeI18N(tableContext);
                }
                else if (winLapDfeId5 != null || (enrollmentTemplateobject != null && (int)enrollmentTemplateobject == 31)) {
                    val = I18N.getMsg("mdm.enroll.laptop", new Object[0]);
                }
                else if (chromeBookDfeId3 != null || (enrollmentTemplateobject != null && (int)enrollmentTemplateobject == 40)) {
                    val = I18N.getMsg("mdm.enroll.chrome", new Object[0]);
                }
                else if (macDfeId5 != null || (enrollmentTemplateobject != null && (int)enrollmentTemplateobject == 12)) {
                    val = I18N.getMsg("mdm.enroll.mac.enrolled_by_dc_agent", new Object[0]);
                }
                else if (winmmDFEId != null || (enrollmentTemplateobject != null && (int)enrollmentTemplateobject == 33)) {
                    val = I18N.getMsg("mdm.enroll.mac.enrolled_by_dc_agent", new Object[0]);
                }
                else if (migrationDfeId4 != null || (enrollmentTemplateobject != null && (int)enrollmentTemplateobject == 33)) {
                    val = I18N.getMsg("mdm.enroll.migration", new Object[0]);
                }
                else {
                    final int type3 = (int)tableContext.getAssociatedPropertyValue("DeviceEnrollmentRequest.ENROLLMENT_TYPE");
                    if (type3 == 2) {
                        val = I18N.getMsg("dc.mdm.enroll.self_enrollment", new Object[0]);
                    }
                }
                columnProperties.put("VALUE", val);
            }
            if (columnalais.equals("MdModelInfo.MODEL_NAME")) {
                String val = (String)data;
                final String actionStr2;
                val = (actionStr2 = ((val == null) ? "--" : val));
                if (isExport == null || isExport.equalsIgnoreCase("false")) {
                    columnProperties.put("VALUE", actionStr2);
                }
                else {
                    columnProperties.put("VALUE", val);
                }
            }
            if (columnalais.equals("AaaUser.FIRST_NAME")) {
                String val = (String)data;
                val = ((val == null || val.trim().equalsIgnoreCase("")) ? "--" : val);
                columnProperties.put("VALUE", DMIAMEncoder.encodeHTML(val));
            }
            if (columnalais.equals("MdModelInfo.MODEL_TYPE")) {
                final Integer type4 = (Integer)data;
                String modelType = "--";
                if (data != null) {
                    if (type4 == 1) {
                        modelType = I18N.getMsg("dc.mdm.actionlog.appmgmt.smartPhone", new Object[0]);
                    }
                    else if (type4 == 2) {
                        modelType = I18N.getMsg("dc.mdm.graphs.tablet", new Object[0]);
                    }
                    else if (type4 == 3) {
                        modelType = I18N.getMsg("dc.common.LAPTOP", new Object[0]);
                    }
                    else if (type4 == 4) {
                        modelType = I18N.getMsg("dc.common.DESKTOP", new Object[0]);
                    }
                    else if (type4 == 5) {
                        modelType = I18N.getMsg("dc.common.TV", new Object[0]);
                    }
                    else {
                        modelType = I18N.getMsg("dc.common.OTHERS", new Object[0]);
                    }
                }
                columnProperties.put("VALUE", modelType);
            }
        }
        catch (final Exception ex) {
            this.logger.log(Level.WARNING, "Exception occured while rendering cell value in StagedDeviceViewTransformer for {0}", columnName);
            this.logger.log(Level.WARNING, "Exception occured while rendering cell value in StagedDeviceViewTransformer", ex);
            final HashMap columnProperties2 = tableContext.getRenderedAttributes();
            columnProperties2.put("VALUE", "--");
        }
    }
    
    private boolean isSelfEnrollEnabledForDepDevice(final TransformerContext tableContext) {
        boolean isSelfEnrollEnabledForABMDevice = false;
        try {
            if (tableContext.getAssociatedPropertyValue("AppleDEPDeviceForEnrollment.DEP_TOKEN_ID") != null) {
                final long depTokenId = (long)tableContext.getAssociatedPropertyValue("AppleDEPDeviceForEnrollment.DEP_TOKEN_ID");
                if (tableContext.getViewContext().getRequest().getAttribute("DEP_TOKEN_SELF_ENROLL_MAP") != null) {
                    final Map<Long, Boolean> depTokenSelfEnrollMap = (Map<Long, Boolean>)tableContext.getViewContext().getRequest().getAttribute("DEP_TOKEN_SELF_ENROLL_MAP");
                    if (depTokenSelfEnrollMap.size() > 0 && depTokenSelfEnrollMap.get(depTokenId) != null) {
                        isSelfEnrollEnabledForABMDevice = depTokenSelfEnrollMap.get(depTokenId);
                    }
                }
            }
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Exception while checking whether Self enrollment is enabled for ABM device", e);
        }
        return isSelfEnrollEnabledForABMDevice;
    }
    
    private String getDepServerTypeI18N(final TransformerContext tableContext) throws Exception {
        String depServerType = I18N.getMsg("mdm.enroll.apple_abm", new Object[0]);
        try {
            if (tableContext.getAssociatedPropertyValue("AppleDEPDeviceForEnrollment.DEP_TOKEN_ID") != null) {
                final long depTokenId = (long)tableContext.getAssociatedPropertyValue("AppleDEPDeviceForEnrollment.DEP_TOKEN_ID");
                if (tableContext.getViewContext().getRequest().getAttribute("DEP_TOKEN_TYPE_MAP") != null) {
                    final Map<Long, Integer> depTokenTypeMap = (Map<Long, Integer>)tableContext.getViewContext().getRequest().getAttribute("DEP_TOKEN_TYPE_MAP");
                    if (depTokenTypeMap.size() > 0 && depTokenTypeMap.get(depTokenId) != null) {
                        final int type = depTokenTypeMap.get(depTokenId);
                        if (type == 2) {
                            depServerType = I18N.getMsg("mdm.enroll.apple_asm", new Object[0]);
                        }
                    }
                }
            }
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Exception while checking whether Self enrollment is enabled for ABM device", e);
        }
        return depServerType;
    }
}
