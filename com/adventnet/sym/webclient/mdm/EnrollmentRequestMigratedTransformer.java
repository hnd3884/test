package com.adventnet.sym.webclient.mdm;

import java.net.URLDecoder;
import java.util.Iterator;
import com.me.devicemanagement.framework.server.util.DMIAMEncoder;
import com.adventnet.sym.server.mdm.enroll.MDMEnrollmentUtil;
import java.util.ArrayList;
import org.json.JSONArray;
import com.adventnet.sym.server.mdm.MDMEntrollment;
import com.me.mdm.server.factory.MDMApiFactoryProvider;
import com.me.mdm.server.util.MDMFeatureParamsHandler;
import com.adventnet.sym.server.mdm.core.ManagedDeviceHandler;
import java.net.URLEncoder;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import com.adventnet.sym.server.mdm.util.JSONUtil;
import com.adventnet.sym.server.mdm.util.MDMStringUtils;
import org.json.JSONObject;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import java.util.HashMap;
import java.util.logging.Level;
import com.adventnet.i18n.I18N;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import com.me.devicemanagement.framework.server.customer.CustomerInfoUtil;
import com.adventnet.client.components.web.TransformerContext;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;
import com.adventnet.client.components.table.web.DefaultTransformer;

public class EnrollmentRequestMigratedTransformer extends DefaultTransformer
{
    public Logger logger;
    public List showValueOnlyViews;
    
    public EnrollmentRequestMigratedTransformer() {
        this.logger = Logger.getLogger("MDMLogger");
        this.showValueOnlyViews = Arrays.asList("EnrollmentRequestSearch");
    }
    
    protected boolean checkIfColumnRendererable(final TransformerContext tableContext) throws Exception {
        final String columnalias = tableContext.getPropertyName();
        final String viewName = tableContext.getViewContext().getUniqueId();
        final int reportType = tableContext.getViewContext().getRenderType();
        if (columnalias.equals("UserResource.DOMAIN_NETBIOS_NAME") && CustomerInfoUtil.getInstance().isMSP()) {
            return false;
        }
        if (!columnalias.equalsIgnoreCase("checkbox") && !columnalias.equalsIgnoreCase("Action")) {
            return super.checkIfColumnRendererable(tableContext);
        }
        if (reportType != 4 || viewName.equalsIgnoreCase("EnrollmentRequestSearch")) {
            return false;
        }
        final boolean isUserInRole = ApiFactoryProvider.getAuthUtilAccessAPI().getRoles().contains("MDM_Enrollment_Write");
        final boolean isUserInModernMgmtRole = ApiFactoryProvider.getAuthUtilAccessAPI().getRoles().contains("ModernMgmt_Enrollment_Write");
        return isUserInRole || isUserInModernMgmtRole;
    }
    
    public void renderHeader(final TransformerContext tableContext) {
        super.renderHeader(tableContext);
        final String columnalias = tableContext.getPropertyName();
        final HashMap headerProperties = tableContext.getRenderedAttributes();
        final int reportType = tableContext.getViewContext().getRenderType();
        if (columnalias.equalsIgnoreCase("DeviceEnrollmentRequest.PLATFORM_TYPE") && reportType != 4) {
            try {
                headerProperties.put("VALUE", I18N.getMsg("dc.mdm.device_mgmt.platform", new Object[0]));
            }
            catch (final Exception e) {
                this.logger.log(Level.WARNING, "Excepton in transformer", e);
            }
        }
    }
    
    public void renderCell(final TransformerContext tableContext) {
        try {
            super.renderCell(tableContext);
            final HashMap columnProperties = tableContext.getRenderedAttributes();
            final String columnalais = tableContext.getPropertyName();
            final String viewName = tableContext.getViewContext().getUniqueId();
            final int reportType = tableContext.getViewContext().getRenderType();
            Object data = tableContext.getPropertyValue();
            if (columnalais.equals("AaaUser.FIRST_NAME")) {
                final Integer enrollType = (Integer)tableContext.getAssociatedPropertyValue("DeviceEnrollmentRequest.ENROLLMENT_TYPE");
                final Integer managedUserStatus = (Integer)tableContext.getAssociatedPropertyValue("ManagedUser.STATUS");
                if (enrollType == 2 && !CustomerInfoUtil.isSAS) {
                    final String userName = (String)tableContext.getAssociatedPropertyValue("UserResource.NAME");
                    if (managedUserStatus != null && managedUserStatus != 11) {
                        columnProperties.put("VALUE", "--");
                    }
                    else {
                        columnProperties.put("VALUE", userName);
                    }
                }
            }
            if (columnalais.equals("ManagedDevice.AGENT_TYPE")) {
                String platformName = "";
                if (data != null) {
                    final Integer platformType = (Integer)data;
                    String isExport = "false";
                    if (reportType != 4) {
                        isExport = "true";
                    }
                    platformName = MDMUtil.getInstance().getAgentValue(platformType, isExport);
                }
                columnProperties.put("VALUE", (data != null) ? platformName : "--");
            }
            if (columnalais.equals("ManagedDevice.MANAGED_STATUS")) {
                final JSONObject payload = new JSONObject();
                String managedstatus = (String)tableContext.getAssociatedPropertyValue("ManagedDeviceStatus.STATUS_LABEL");
                if (data == null || (int)data == 1 || (int)data == 0) {
                    managedstatus = (String)tableContext.getAssociatedPropertyValue("DeviceEnrollmentRequestStatus.STATUS_LABEL");
                    if (MDMStringUtils.isEmpty(managedstatus)) {
                        managedstatus = "--";
                    }
                }
                else if ((int)data == 10) {
                    managedstatus = "dc.mdm.in_stock";
                    payload.put("statusClass", (Object)"ucs-table-status-text__in-progress");
                }
                else if ((int)data == 9) {
                    managedstatus = "mdm.deprovision.in_repair";
                    payload.put("statusClass", (Object)"ucs-table-status-text__ready");
                }
                else if ((int)data == 11) {
                    managedstatus = "dc.mdm.retired";
                    payload.put("statusClass", (Object)"ucs-table-status-text__failed");
                }
                final String value;
                managedstatus = (value = I18N.getMsg(managedstatus, new Object[0]));
                final String actionStr = "";
                if (data == null || (int)data == 1 || (int)data == 0) {
                    final Long reqId = (Long)tableContext.getAssociatedPropertyValue("DeviceEnrollmentRequest.ENROLLMENT_REQUEST_ID");
                    final Long expireTime = (Long)tableContext.getAssociatedPropertyValue("OTPPassword.EXPIRE_TIME");
                    final Integer request_status = (Integer)tableContext.getAssociatedPropertyValue("DeviceEnrollmentRequest.REQUEST_STATUS");
                    if (expireTime != null && expireTime < System.currentTimeMillis()) {
                        payload.put("statusClass", (Object)"ucs-table-status-text__failed");
                        managedstatus = I18N.getMsg("mdm.enroll.request_expired_status", new Object[0]);
                    }
                    else if (request_status == 0) {
                        payload.put("statusClass", (Object)"ucs-table-status-text__failed");
                    }
                    else {
                        if (reqId != null) {
                            final JSONObject json = new JSONObject();
                            json.put("ENROLLMENT_REQUEST_ID", (Object)reqId);
                            json.put("PLATFORM_TYPE", (Object)tableContext.getAssociatedPropertyValue("DeviceEnrollmentRequest.PLATFORM_TYPE"));
                            json.put("IS_INVITED_BY_ADMIN", (Object)tableContext.getAssociatedPropertyValue("InvitationEnrollmentRequest.IS_INVITED_BY_ADMIN"));
                            final Long smsSent = (Long)tableContext.getAssociatedPropertyValue("DEVICEENROLLREQTOSMS.ENROLLMENT_REQUEST_ID");
                            json.put("INVITATION_BY_SMS", smsSent != null);
                            json.put("REGISTRATION_STATUS", (Object)tableContext.getAssociatedPropertyValue("InvitationEnrollmentRequest.REGISTRATION_STATUS"));
                            payload.put("tooltipJSON", (Object)JSONUtil.getInstance().convertLongToString(json));
                        }
                        payload.put("statusClass", (Object)("managed-status-" + String.valueOf(request_status)));
                    }
                }
                else if ((int)data == 2) {
                    final String remarks = (String)tableContext.getAssociatedPropertyValue("ManagedDevice.REMARKS");
                    Long inactiveThreshold = (Long)tableContext.getAssociatedPropertyValue("InactiveDevicePolicyDetails.INACTIVE_THRESHOLD");
                    if (inactiveThreshold == null || inactiveThreshold < 1L) {
                        inactiveThreshold = 604800000L;
                    }
                    Long lastContactTime = (Long)tableContext.getAssociatedPropertyValue("AgentContact.LAST_CONTACT_TIME");
                    final Long currentTime = MDMUtil.getCurrentTimeInMillis();
                    if (lastContactTime == null) {
                        lastContactTime = currentTime;
                    }
                    final Long inactiveTime = currentTime - inactiveThreshold;
                    String srcDC = SyMUtil.getSyMParameter("src_dc");
                    String destDC = SyMUtil.getSyMParameter("dest_dc");
                    if (!SyMUtil.isStringValid(srcDC)) {
                        srcDC = "old";
                    }
                    if (!SyMUtil.isStringValid(destDC)) {
                        destDC = "new";
                    }
                    if (remarks.equalsIgnoreCase("mdm.enroll.assign_in_progess_remarks")) {
                        managedstatus = I18N.getMsg("mdm.enroll.assign_in_progess", new Object[0]);
                        payload.put("statusClass", (Object)"ucs-table-status-text__in-progress");
                    }
                    else if (remarks.equalsIgnoreCase(I18N.getMsg("mdm.agent.yet_to_migrate_remarks_from", new Object[] { destDC })) || remarks.equalsIgnoreCase(I18N.getMsg("mdm.agent.yet_to_migrate_remarks_to", new Object[] { srcDC }))) {
                        managedstatus = I18N.getMsg("mdm.agent.migration_status", new Object[0]);
                        payload.put("statusClass", (Object)"ucs-table-status-text__in-progress");
                    }
                    else if (lastContactTime <= inactiveTime) {
                        payload.put("statusClass", (Object)"ucs-table-status-text__in-progress");
                    }
                    else {
                        payload.put("statusClass", (Object)"ucs-table-status-text__success");
                    }
                }
                else if ((int)data == 4 || (int)data == 9 || (int)data == 10) {
                    payload.put("statusClass", (Object)"ucs-table-status-text__failed");
                }
                else if ((int)data == 11) {
                    final Object templateobj = tableContext.getAssociatedPropertyValue("EnrollmentTemplate.TEMPLATE_TYPE");
                    int templateType = -1;
                    if (templateobj != null) {
                        templateType = (int)templateobj;
                    }
                    if (templateType != 21 && templateType != 10) {
                        payload.put("statusClass", (Object)"ucs-table-status-text__failed");
                    }
                    else {
                        final JSONObject json2 = new JSONObject();
                        json2.put("templateType", templateType);
                        payload.put("tooltipJSON", (Object)json2.toString());
                        payload.put("statusClass", (Object)("managed-status-" + String.valueOf(data)));
                    }
                }
                Boolean statusOnly = false;
                if (viewName.equalsIgnoreCase("ersearch")) {
                    statusOnly = true;
                }
                payload.put("managedstatus", (Object)managedstatus);
                payload.put("statusOnly", (Object)statusOnly);
                if (reportType != 4) {
                    columnProperties.put("VALUE", managedstatus);
                }
                else {
                    columnProperties.put("PAYLOAD", payload);
                }
            }
            if (columnalais.equals("ManagedDevice.UDID")) {
                String udid = (String)data;
                if (udid == null) {
                    udid = (String)tableContext.getAssociatedPropertyValue("DeviceEnrollmentRequest.UDID");
                    if (udid == null) {
                        udid = "--";
                    }
                    columnProperties.put("VALUE", udid);
                }
            }
            if (columnalais.equals("IOSNativeAppStatus.INSTALLATION_STATUS")) {
                final Integer installationStatus = (Integer)data;
                String installationStatusStr = "";
                if (installationStatus == null) {
                    installationStatusStr = "--";
                }
                else if (installationStatus == 0) {
                    installationStatusStr = "dc.db.som.status.yet_to_install";
                }
                else if (installationStatus == 1) {
                    installationStatusStr = "dc.db.som.status.installed_successfully";
                }
                columnProperties.put("VALUE", I18N.getMsg(installationStatusStr, new Object[0]));
            }
            if (columnalais.equals("EnrollmentTemplate.TEMPLATE_TYPE")) {
                final Object object = tableContext.getAssociatedPropertyValue("EnrollmentTemplate.TEMPLATE_TYPE");
                String val = I18N.getMsg("mdm.enroll.by_invite", new Object[0]);
                if (object != null) {
                    final int templateType2 = (int)object;
                    if (templateType2 == 20) {
                        val = I18N.getMsg("dc.mdm.enroll.android_admin_enrollment", new Object[0]);
                    }
                    else if (templateType2 == 11) {
                        val = I18N.getMsg("dc.mdm.enroll.apple_configurator", new Object[0]);
                    }
                    else if (templateType2 == 21) {
                        val = I18N.getMsg("mdm.enroll.knox", new Object[0]);
                    }
                    else if (templateType2 == 30) {
                        val = I18N.getMsg("mdm.common.WINDOWS_10", new Object[0]);
                    }
                    else if (templateType2 == 22) {
                        val = I18N.getMsg("mdm.enroll.emm", new Object[0]);
                    }
                    else if (templateType2 == 31) {
                        val = I18N.getMsg("mdm.enroll.laptop", new Object[0]);
                    }
                    else if (templateType2 == 23) {
                        val = I18N.getMsg("mdm.enroll.zerotouch", new Object[0]);
                    }
                    else if (templateType2 == 10) {
                        val = I18N.getMsg("mdm.enroll.apple_dep", new Object[0]);
                    }
                    else if (templateType2 == 32) {
                        val = I18N.getMsg("mdm.enroll.autopilot", new Object[0]);
                    }
                    else if (templateType2 == 40) {
                        val = I18N.getMsg("mdm.enroll.chrome", new Object[0]);
                    }
                    else if (templateType2 == 12) {
                        val = I18N.getMsg("mdm.enroll.mac.enrolled_by_dc_agent", new Object[0]);
                    }
                }
                else {
                    final int type = (int)tableContext.getAssociatedPropertyValue("DeviceEnrollmentRequest.ENROLLMENT_TYPE");
                    if (type == 2) {
                        val = I18N.getMsg("dc.mdm.enroll.self_enrollment", new Object[0]);
                    }
                }
                columnProperties.put("VALUE", val);
            }
            if (columnalais.equals("Action")) {
                JSONObject payload = new JSONObject();
                final String remarks2 = (String)tableContext.getAssociatedPropertyValue("ManagedDevice.REMARKS");
                if (remarks2 != null && remarks2.equalsIgnoreCase("mdm.enroll.assign_in_progess_remarks")) {
                    payload.put("disabled", true);
                    payload.put("overlibText", (Object)I18N.getMsg("mdm.enrolled.disabled_while_assigning", new Object[0]));
                    columnProperties.put("PAYLOAD", payload);
                }
                else {
                    String userName = (String)tableContext.getAssociatedPropertyValue("UserResource.NAME");
                    userName = URLEncoder.encode(MDMUtil.getInstance().encodeURIComponentEquivalent(userName), "UTF-8");
                    String emailAddress = (String)tableContext.getAssociatedPropertyValue("ManagedUser.EMAIL_ADDRESS");
                    emailAddress = URLEncoder.encode(MDMUtil.getInstance().encodeURIComponentEquivalent(emailAddress), "UTF-8");
                    String deviceName = (String)tableContext.getAssociatedPropertyValue("ManagedDeviceExtn.NAME");
                    deviceName = ((deviceName != null) ? URLEncoder.encode(deviceName, "UTF-8") : deviceName);
                    final Long reqId2 = (Long)tableContext.getAssociatedPropertyValue("DeviceEnrollmentRequest.ENROLLMENT_REQUEST_ID");
                    final String actionStr2 = "";
                    if (reqId2 != null) {
                        final Object udidObj = tableContext.getAssociatedPropertyValue("ManagedDevice.UDID");
                        String udid2 = null;
                        if (udidObj != null) {
                            udid2 = (String)udidObj;
                        }
                        final Object templateobj2 = tableContext.getAssociatedPropertyValue("EnrollmentTemplate.TEMPLATE_TYPE");
                        int templateType3 = -1;
                        if (templateobj2 != null) {
                            templateType3 = (int)templateobj2;
                        }
                        final String easid = (String)tableContext.getAssociatedPropertyValue("MdDeviceInfo.EAS_DEVICE_IDENTIFIER");
                        final String slno = (String)tableContext.getAssociatedPropertyValue("MdDeviceInfo.SERIAL_NUMBER");
                        final String imei = (String)tableContext.getAssociatedPropertyValue("MdDeviceInfo.IMEI");
                        final Integer platformType2 = (Integer)tableContext.getAssociatedPropertyValue("DeviceEnrollmentRequest.PLATFORM_TYPE");
                        final Integer modelType = (Integer)tableContext.getAssociatedPropertyValue("MdModelInfo.MODEL_TYPE");
                        Long inactiveThreshold2 = (Long)tableContext.getAssociatedPropertyValue("InactiveDevicePolicyDetails.INACTIVE_THRESHOLD");
                        if (inactiveThreshold2 == null || inactiveThreshold2 < 1L) {
                            inactiveThreshold2 = 604800000L;
                        }
                        final Long currentTime2 = MDMUtil.getCurrentTimeInMillis();
                        Long lastContactTime2 = (Long)tableContext.getAssociatedPropertyValue("AgentContact.LAST_CONTACT_TIME");
                        if (lastContactTime2 == null) {
                            lastContactTime2 = currentTime2;
                        }
                        final Long inactiveTime2 = currentTime2 - inactiveThreshold2;
                        final JSONObject json3 = new JSONObject();
                        final Boolean isDeviceProvisioningUser = ManagedDeviceHandler.getInstance().isDeviceProvisioningUser();
                        json3.put("isDeviceProvisioningUser", (Object)isDeviceProvisioningUser);
                        json3.put("MANAGED_USER_ID", (Object)String.valueOf(tableContext.getAssociatedPropertyValue("ManagedUser.MANAGED_USER_ID")));
                        json3.put("EMAIL_ADDRESS", (Object)emailAddress);
                        json3.put("NAME", (Object)userName);
                        json3.put("DOMAIN_NETBIOS_NAME", (Object)tableContext.getAssociatedPropertyValue("UserResource.DOMAIN_NETBIOS_NAME"));
                        json3.put("ENROLLMENT_REQUEST_ID", (Object)String.valueOf(reqId2));
                        json3.put("ENROLLMENT_TYPE", tableContext.getAssociatedPropertyValue("DeviceEnrollmentRequest.ENROLLMENT_TYPE"));
                        json3.put("MANAGED_STATUS", (Object)tableContext.getAssociatedPropertyValue("ManagedDevice.MANAGED_STATUS"));
                        json3.put("REQUEST_STATUS", (Object)tableContext.getAssociatedPropertyValue("DeviceEnrollmentRequest.REQUEST_STATUS"));
                        json3.put("ERROR_CODE", (Object)tableContext.getAssociatedPropertyValue("DeviceEnrollReqToErrCode.ERROR_CODE"));
                        json3.put("PLATFORM_TYPE", (Object)platformType2);
                        json3.put("UDID", (Object)tableContext.getAssociatedPropertyValue("DeviceEnrollmentRequest.UDID"));
                        json3.put("DEVICE_NAME", (Object)deviceName);
                        json3.put("BY_ADMIN", (Object)tableContext.getAssociatedPropertyValue("InvitationEnrollmentRequest.IS_INVITED_BY_ADMIN"));
                        json3.put("AUTH_MODE", (Object)tableContext.getAssociatedPropertyValue("DeviceEnrollmentRequest.AUTH_MODE"));
                        json3.put("IS_MSP", !CustomerInfoUtil.getInstance().isMSP());
                        if (lastContactTime2 <= inactiveTime2) {
                            json3.put("isInactive", true);
                        }
                        else {
                            json3.put("isInactive", false);
                        }
                        Integer appInstallationStatus = (Integer)tableContext.getAssociatedPropertyValue("IOSNativeAppStatus.INSTALLATION_STATUS");
                        if (appInstallationStatus == null) {
                            appInstallationStatus = 0;
                        }
                        json3.put("IMEI", (Object)imei);
                        json3.put("SLNO", (Object)slno);
                        json3.put("EASID", (Object)easid);
                        json3.put("udid", (Object)udid2);
                        final Long expireTime2 = (Long)tableContext.getAssociatedPropertyValue("OTPPassword.EXPIRE_TIME");
                        Boolean isExpired = false;
                        if (expireTime2 != null && expireTime2 < System.currentTimeMillis()) {
                            isExpired = true;
                        }
                        json3.put("isExpired", (Object)isExpired);
                        json3.put("INSTALLATION_STATUS", (Object)appInstallationStatus);
                        json3.put("MODEL_TYPE", (Object)modelType);
                        json3.put("TEMPLATE_TYPE", templateType3);
                        if ((templateType3 == 12 || (platformType2.equals(3) && modelType != null && (modelType.equals(3) || modelType.equals(4) || modelType.equals(2)))) && !MDMFeatureParamsHandler.getInstance().isFeatureEnabled("AllowModernDeprovision")) {
                            final JSONObject deviceDetailsJSON = new JSONObject();
                            if (slno != null) {
                                deviceDetailsJSON.put("SERIAL_NUMBER", (Object)slno);
                            }
                            if (udid2 != null) {
                                deviceDetailsJSON.put("UDID", (Object)udid2);
                            }
                            final JSONObject computerDeviceMappingDetails = MDMApiFactoryProvider.getMDMUtilAPI().getComputerDeviceMappingTable(deviceDetailsJSON);
                            if (computerDeviceMappingDetails != null) {
                                final Long dcComputerResourceId = computerDeviceMappingDetails.optLong("RESOURCE_ID", -1L);
                                if (dcComputerResourceId != -1L) {
                                    json3.put("MODERN_MGMT_DEVICE_MANAGED_IN_SOM", true);
                                }
                            }
                        }
                        final int ownedby = Integer.valueOf(tableContext.getAssociatedPropertyValue("DeviceEnrollmentRequest.OWNED_BY").toString());
                        json3.put("OWNEDBY", ownedby);
                        json3.put("ENROLLMENT_TYPE", (Object)Integer.valueOf(tableContext.getAssociatedPropertyValue("DeviceEnrollmentRequest.ENROLLMENT_TYPE").toString()));
                        final String enrollmentRequestRemarks = (String)tableContext.getAssociatedPropertyValue("DeviceEnrollmentRequest.REMARKS");
                        final Integer enrollmentRequestStatus = (Integer)tableContext.getAssociatedPropertyValue("DeviceEnrollmentRequest.REQUEST_STATUS");
                        final Integer otpFailedAttempts = (Integer)tableContext.getAssociatedPropertyValue("OTPPassword.FAILED_ATTEMPTS");
                        if (!MDMUtil.isStringEmpty(enrollmentRequestRemarks) && enrollmentRequestRemarks.equalsIgnoreCase("dc.mdm.enroll.otp_invalidated") && enrollmentRequestStatus != null && enrollmentRequestStatus == 0 && otpFailedAttempts != null && otpFailedAttempts >= 3) {
                            json3.put("regenerate_otp", true);
                        }
                        payload = this.enrollOpenActionList(json3);
                    }
                    columnProperties.put("PAYLOAD", payload);
                }
            }
            if (columnalais.equals("DeviceEnrollmentRequest.OWNED_BY")) {
                final Integer ownedBy = (Integer)data;
                final String sOwnedBy = MDMEntrollment.getInstance().getOwnedByAsString(ownedBy);
                Boolean showValueOnly = false;
                if (this.showValueOnlyViews.contains(viewName)) {
                    showValueOnly = true;
                }
                if (reportType == 4) {
                    final JSONArray ownedByOption = new JSONArray();
                    final String[] optionsList = { "1", "2" };
                    for (int i = 0; i < optionsList.length; ++i) {
                        final JSONObject list = new JSONObject();
                        list.put("id", (Object)optionsList[i]);
                        list.put("displayValue", (Object)MDMEntrollment.getInstance().getOwnedByAsString(Integer.parseInt(optionsList[i])));
                        ownedByOption.put((Object)list);
                    }
                    final JSONObject payload2 = new JSONObject();
                    payload2.put("cellData", (Object)sOwnedBy);
                    payload2.put("ownedByOption", (Object)ownedByOption);
                    payload2.put("showValueOnly", (Object)showValueOnly);
                    columnProperties.put("PAYLOAD", payload2);
                }
                else {
                    columnProperties.put("VALUE", sOwnedBy);
                }
            }
            if (columnalais.equalsIgnoreCase("SCOPE") && data != null) {
                final int installedIn = (int)data;
                columnProperties.put("VALUE", (installedIn == 1) ? "Container" : "Device");
            }
            if (columnalais.equals("GROUP_NAME")) {
                final Long resourceID = (Long)tableContext.getAssociatedPropertyValue("ManagedDevice.RESOURCE_ID");
                final HashMap hashMap = (HashMap)tableContext.getViewContext().getRequest().getAttribute("ASSOCIATED_GROUP_NAMES");
                String groupName = "";
                List groupList = new ArrayList();
                if (hashMap != null && hashMap.get(resourceID) != null) {
                    groupList = hashMap.get(resourceID);
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
            if (columnalais.equals("ManagedDevice.REMARKS")) {
                final JSONObject payload = new JSONObject();
                final Integer request_status2 = (Integer)tableContext.getAssociatedPropertyValue("DeviceEnrollmentRequest.REQUEST_STATUS");
                final Integer device_status = (Integer)tableContext.getAssociatedPropertyValue("ManagedDevice.MANAGED_STATUS");
                String remarks3 = (String)data;
                Long inactiveThreshold3 = (Long)tableContext.getAssociatedPropertyValue("InactiveDevicePolicyDetails.INACTIVE_THRESHOLD");
                if (inactiveThreshold3 == null || inactiveThreshold3 < 1L) {
                    inactiveThreshold3 = 604800000L;
                }
                Long lastContactTime3 = (Long)tableContext.getAssociatedPropertyValue("AgentContact.LAST_CONTACT_TIME");
                final Long currentTime3 = MDMUtil.getCurrentTimeInMillis();
                if (lastContactTime3 == null) {
                    lastContactTime3 = currentTime3;
                }
                final Long inactiveTime3 = currentTime3 - inactiveThreshold3;
                if (device_status != null && device_status == 2 && lastContactTime3 <= inactiveTime3) {
                    remarks3 = "mdm.enroll.inactive_device_remarks";
                }
                else if (data == null || ((request_status2 == 0 || request_status2 == 1) && (device_status == null || (device_status != 4 && device_status != 9 && device_status != 11 && device_status != 10)))) {
                    data = tableContext.getAssociatedPropertyValue("DeviceEnrollmentRequest.REMARKS");
                    remarks3 = (String)data;
                    final Long expireTime3 = (Long)tableContext.getAssociatedPropertyValue("OTPPassword.EXPIRE_TIME");
                    if (request_status2 == 0 && ("dc.mdm.enroll.email_invite_failed".equalsIgnoreCase(remarks3) || "dc.mdm.enroll.sms_invite_failed".equals(remarks3) || "dc.mdm.enroll.both_invite_failed".equals(remarks3))) {
                        String emailInvitationFailureReason = (String)tableContext.getAssociatedPropertyValue("EREmailInvitationFailure.BOUNCE_REASON");
                        final Integer smsErrorCode = (Integer)tableContext.getAssociatedPropertyValue("DEVICEENROLLREQTOSMS.SMS_CODE");
                        final String smsRemarks = (String)tableContext.getAssociatedPropertyValue("DEVICEENROLLREQTOSMS.SMS_REMARKS");
                        if (emailInvitationFailureReason != null && smsErrorCode != null && smsErrorCode != 0) {
                            remarks3 = "dc.mdm.enroll.both_invite_failed";
                        }
                        JSONObject bounceJSON = new JSONObject();
                        if (emailInvitationFailureReason != null) {
                            bounceJSON = new JSONObject(emailInvitationFailureReason);
                            bounceJSON.put("hasEmailHookInfo", true);
                        }
                        if (smsErrorCode != null && smsErrorCode != 0) {
                            MDMEnrollmentUtil.appendSMSWebhookInfoIntoJSON(bounceJSON, smsErrorCode);
                        }
                        emailInvitationFailureReason = DMIAMEncoder.encodeJavaScript(JSONUtil.getInstance().convertLongToString(bounceJSON).toString());
                        payload.put("bounceJSON", (Object)emailInvitationFailureReason);
                        payload.put("status_case", (Object)"sms_invite_failed_with_bounceJSON");
                    }
                    else if (request_status2 == 0) {
                        payload.put("status_case", (Object)"failed_with_readKB");
                        final String readKB = (String)tableContext.getAssociatedPropertyValue("ErrorCodeToKBUrl.KB_URL");
                        if (readKB != null && readKB != "") {
                            payload.put("linkText", (Object)I18N.getMsg("dc.common.READ_KB", new Object[0]));
                            payload.put("readKB", (Object)MDMUtil.replaceProductUrlLoaderValuesinText(readKB, null));
                        }
                    }
                    else if (expireTime3 != null && expireTime3 < System.currentTimeMillis()) {
                        final Boolean byadmin = (Boolean)tableContext.getAssociatedPropertyValue("InvitationEnrollmentRequest.IS_INVITED_BY_ADMIN");
                        final Long erid = (Long)tableContext.getAssociatedPropertyValue("DeviceEnrollmentRequest.ENROLLMENT_REQUEST_ID");
                        final String expireTimeStr = SyMUtil.getSyMParameterFromDB("EXPIRE_TIME");
                        Long expiretime = 604800000L;
                        if (expireTimeStr != null) {
                            expiretime = Long.parseLong(expireTimeStr);
                        }
                        final int days = (int)(expiretime / 86400000L);
                        payload.put("status_case", (Object)"expired");
                        payload.put("remarks", (Object)I18N.getMsg("mdm.enrollment.request_expired_remark", new Object[] { days }));
                        payload.put("expired_days", days);
                        remarks3 = "mdm.enrollment.expired.export.remark";
                    }
                }
                else if (data.equals("mdm.agent.compliance.action.corporatewiped_rooted")) {
                    payload.put("status_case", (Object)"text_with_learnmore");
                    payload.put("linkText", (Object)I18N.getMsg("dc.common.LEARN_MORE", new Object[0]));
                    payload.put("learnmore_link", (Object)"/help/enrollment/customize_me_mdm_app.html");
                }
                else if (device_status == 4) {
                    payload.put("status_case", (Object)"text_with_learnmore");
                    payload.put("linkText", (Object)I18N.getMsg("dc.common.LEARN_MORE", new Object[0]));
                    payload.put("learnmore_link", (Object)"/how-to/mdm-prevent-users-from-revoking-management.html");
                }
                Boolean showValueOnly2 = false;
                if (this.showValueOnlyViews.contains(viewName)) {
                    showValueOnly2 = true;
                }
                payload.put("showValueOnly", (Object)showValueOnly2);
                payload.put("remarks", (Object)I18N.getMsg(remarks3, new Object[0]));
                if (reportType != 4) {
                    columnProperties.put("VALUE", I18N.getMsg(remarks3, new Object[0]));
                }
                else {
                    columnProperties.put("PAYLOAD", payload);
                }
            }
        }
        catch (final Exception ex) {
            this.logger.log(Level.WARNING, "Exception occured while rendering cell value in EnrollmentRequestTransformer ", ex);
            final HashMap columnProperties2 = tableContext.getRenderedAttributes();
            columnProperties2.put("VALUE", "--");
        }
    }
    
    private JSONObject enrollOpenActionList(final JSONObject json) throws Exception {
        try {
            final JSONObject responseJSON = new JSONObject();
            final Long userID = JSONUtil.optLongForUVH(json, "MANAGED_USER_ID", Long.valueOf(-1L));
            final Long reqID = JSONUtil.optLongForUVH(json, "ENROLLMENT_REQUEST_ID", Long.valueOf(-1L));
            String emailID = json.optString("EMAIL_ADDRESS");
            if (!MDMUtil.isStringEmpty(emailID)) {
                emailID = URLDecoder.decode(MDMUtil.getInstance().decodeURIComponentEquivalent(emailID), "UTF-8");
            }
            final String userName = json.getString("NAME");
            final Integer managedStatus = json.optInt("MANAGED_STATUS", -1);
            final int platform = json.getInt("PLATFORM_TYPE");
            final String deviceName = json.optString("DEVICE_NAME", (String)null);
            final int authMode = json.optInt("AUTH_MODE", -1);
            final Boolean byAdmin = json.optBoolean("BY_ADMIN", false);
            final String imei = json.optString("IMEI", (String)null);
            final String slno = json.optString("SLNO", (String)null);
            final String easID = json.optString("EASID", (String)null);
            final Boolean isExpired = json.optBoolean("isExpired", false);
            final Boolean isProvisioningUser = json.optBoolean("isDeviceProvisioningUser", false);
            final int templateType = json.optInt("TEMPLATE_TYPE", -1);
            final int enrollmentType = json.optInt("ENROLLMENT_TYPE", -1);
            final int ownedby = json.getInt("OWNEDBY");
            final int requestStatus = json.optInt("REQUEST_STATUS", -1);
            final int errorCode = json.optInt("ERROR_CODE", -1);
            final String udid = json.optString("UDID", (String)null);
            final boolean showRetryWakeUp = requestStatus == 0 && (managedStatus == -1 || managedStatus != 4) && (errorCode == 51201 || errorCode == 12133) && udid != null && udid.length() != 0;
            final boolean modernMgmtDeviceManagedInSoM = json.optBoolean("MODERN_MGMT_DEVICE_MANAGED_IN_SOM", false);
            final boolean isInactiveDevice = json.optBoolean("isInactive", false);
            final boolean regenerateOTP = json.optBoolean("regenerate_otp", false);
            final JSONArray actionList = new JSONArray();
            if (showRetryWakeUp) {
                final JSONObject action = new JSONObject();
                action.put("remarks", (Object)"dc.mdm.enroll.wake_up.retry_wake_up");
                action.put("action", (Object)"retry_wake_up");
                final JSONObject paramsJSON = new JSONObject();
                paramsJSON.put("erid", (Object)reqID);
                paramsJSON.put("platform", platform);
                action.put("params", (Object)paramsJSON);
                action.put("is_enabled", true);
                actionList.put((Object)action);
            }
            if (regenerateOTP) {
                final JSONObject action = new JSONObject();
                action.put("remarks", (Object)"dc.mdm.enroll.REGENERATE_OTP");
                action.put("action", (Object)"regenerate_otp");
                action.put("is_enabled", true);
                actionList.put((Object)action);
            }
            if (managedStatus != 9 && managedStatus != 10 && managedStatus != 11) {
                if (managedStatus == -1 || (managedStatus != 2 && managedStatus != 4)) {
                    if (MDMUtil.isStringEmpty(emailID) || !MDMUtil.getInstance().isValidEmail(emailID)) {
                        final JSONObject action = new JSONObject();
                        action.put("remarks", (Object)"dc.mdm.enroll.RESEND_ENROLLMENT_REQUEST");
                        action.put("action", (Object)"resend_enrollment_request");
                        action.put("is_enabled", false);
                        actionList.put((Object)action);
                    }
                    else if (byAdmin) {
                        JSONObject action = new JSONObject();
                        action.put("remarks", (Object)"dc.mdm.enroll.enrollment_steps");
                        action.put("action", (Object)"enrollment_steps");
                        final JSONObject paramsJSON = new JSONObject();
                        paramsJSON.put("erid", (Object)reqID);
                        action.put("params", (Object)paramsJSON);
                        action.put("is_enabled", true);
                        actionList.put((Object)action);
                        if (authMode != 2 && isExpired) {
                            action = new JSONObject();
                            action.put("remarks", (Object)"dc.mdm.enroll.REGENERATE_OTP");
                            action.put("action", (Object)"regenerate_otp");
                            action.put("is_enabled", true);
                            actionList.put((Object)action);
                        }
                    }
                    else {
                        final JSONObject action = new JSONObject();
                        action.put("remarks", (Object)"dc.mdm.enroll.RESEND_ENROLLMENT_REQUEST");
                        action.put("action", (Object)"resend_enrollment_request");
                        final JSONObject paramsJSON = new JSONObject();
                        paramsJSON.put("erid", (Object)reqID);
                        paramsJSON.put("by_admin", (Object)byAdmin);
                        action.put("params", (Object)paramsJSON);
                        if (authMode != 4) {
                            action.put("is_enabled", true);
                        }
                        else {
                            action.put("is_enabled", false);
                        }
                        actionList.put((Object)action);
                    }
                }
                else {
                    final JSONObject action = new JSONObject();
                    action.put("remarks", (Object)"dc.mdm.enroll.RESEND_ENROLLMENT_REQUEST");
                    action.put("action", (Object)"resend_enrollment_request");
                    action.put("is_enabled", false);
                    actionList.put((Object)action);
                }
            }
            if (managedStatus != 9 && managedStatus != 10 && managedStatus != 11) {
                if (MDMUtil.isStringEmpty(emailID) || !MDMUtil.getInstance().isValidEmail(emailID)) {
                    final JSONObject action = new JSONObject();
                    action.put("remarks", (Object)"dc.mdm.enroll.SEND_NEW_REQUEST");
                    action.put("action", (Object)"send_new_request");
                    action.put("is_enabled", false);
                    actionList.put((Object)action);
                }
                else if (enrollmentType != 2) {
                    final JSONObject action = new JSONObject();
                    action.put("remarks", (Object)"dc.mdm.enroll.SEND_NEW_REQUEST");
                    action.put("action", (Object)"send_new_request");
                    final JSONObject paramsJSON = new JSONObject();
                    paramsJSON.put("erid", (Object)reqID);
                    paramsJSON.put("email_id", (Object)emailID);
                    paramsJSON.put("user_id", (Object)userID);
                    paramsJSON.put("user_name", (Object)userName);
                    action.put("params", (Object)paramsJSON);
                    action.put("is_enabled", true);
                    actionList.put((Object)action);
                }
            }
            if (managedStatus == 9 || managedStatus == 10) {
                final JSONObject action = new JSONObject();
                action.put("remarks", (Object)"mdm.enroll.re_enroll_device");
                if (templateType == 10 || templateType == 21) {
                    action.put("action", (Object)"assign_user");
                    final JSONObject paramsJSON = new JSONObject();
                    paramsJSON.put("template_type", templateType);
                    paramsJSON.put("imei", (Object)imei);
                    paramsJSON.put("slno", (Object)slno);
                    paramsJSON.put("eas_id", (Object)easID);
                    paramsJSON.put("udid", (Object)udid);
                    paramsJSON.put("managed_status", (Object)managedStatus);
                    action.put("params", (Object)paramsJSON);
                    action.put("is_enabled", true);
                }
                else if (templateType == 11 || templateType == 22 || templateType == 20 || templateType == 30) {
                    action.put("action", (Object)"re_enroll_device_admin");
                    final JSONObject paramsJSON = new JSONObject();
                    paramsJSON.put("template_type", templateType);
                    action.put("params", (Object)paramsJSON);
                    action.put("is_enabled", true);
                }
                else {
                    action.put("action", (Object)"re_enroll_device_invitation");
                    final JSONObject paramsJSON = new JSONObject();
                    paramsJSON.put("erid", (Object)reqID);
                    paramsJSON.put("platform", platform);
                    paramsJSON.put("email_id", (Object)emailID);
                    paramsJSON.put("user_name", (Object)userName);
                    paramsJSON.put("user_id", (Object)userID);
                    action.put("params", (Object)paramsJSON);
                    action.put("is_enabled", true);
                }
                actionList.put((Object)action);
            }
            if (managedStatus == 9) {
                final JSONObject action = new JSONObject();
                action.put("remarks", (Object)"mdm.enroll.move_to_stock");
                action.put("action", (Object)"move_to_stock");
                final JSONObject paramsJSON = new JSONObject();
                paramsJSON.put("erid", (Object)reqID);
                paramsJSON.put("template_type", templateType);
                paramsJSON.put("udid", (Object)udid);
                action.put("params", (Object)paramsJSON);
                action.put("is_enabled", true);
                actionList.put((Object)action);
            }
            if (managedStatus == 9 || managedStatus == 10) {
                final JSONObject action = new JSONObject();
                action.put("remarks", (Object)"mdm.deprovision.retire_device");
                action.put("action", (Object)"retire_device");
                final JSONObject paramsJSON = new JSONObject();
                paramsJSON.put("erid", (Object)reqID);
                paramsJSON.put("template_type", templateType);
                paramsJSON.put("owned_by", ownedby);
                action.put("params", (Object)paramsJSON);
                action.put("is_enabled", true);
                actionList.put((Object)action);
            }
            if ((managedStatus == 11 || managedStatus == 9 || managedStatus == 10 || managedStatus == 4) && !isProvisioningUser) {
                final JSONObject action = new JSONObject();
                action.put("remarks", (Object)"dc.mdm.enroll.REMOVE_DEVICE");
                action.put("action", (Object)"remove_device");
                final JSONObject paramsJSON = new JSONObject();
                paramsJSON.put("erid", (Object)reqID);
                paramsJSON.put("user_name", (Object)userName);
                action.put("is_enabled", true);
                action.put("params", (Object)paramsJSON);
                actionList.put((Object)action);
            }
            if (!MDMUtil.isStringEmpty(deviceName) && managedStatus != 4 && managedStatus != 9 && managedStatus != 10 && managedStatus != 11) {
                final JSONObject action = new JSONObject();
                action.put("remarks", (Object)"dc.mdm.enroll.CHANGE_USER");
                action.put("action", (Object)"change_user");
                final JSONObject paramsJSON = new JSONObject();
                paramsJSON.put("erid", (Object)reqID);
                action.put("params", (Object)paramsJSON);
                action.put("is_enabled", true);
                actionList.put((Object)action);
            }
            if (managedStatus == 2 && !isProvisioningUser) {
                final JSONObject action = new JSONObject();
                action.put("remarks", (Object)"mdm.deprovision.deprovision_device");
                final boolean isDCProductUemEdition = ApiFactoryProvider.getAuthUtilAccessAPI().getRoles().contains("ModernMgmt_Enrollment_Read");
                if (isDCProductUemEdition && modernMgmtDeviceManagedInSoM) {
                    action.put("action", (Object)"re_direct_dc_som");
                    final JSONObject paramsJSON2 = new JSONObject();
                    paramsJSON2.put("device_name", (Object)deviceName);
                    action.put("params", (Object)paramsJSON2);
                    action.put("is_enabled", true);
                }
                else {
                    action.put("action", (Object)"deprovision_page");
                    final JSONObject paramsJSON2 = new JSONObject();
                    paramsJSON2.put("erid", (Object)reqID);
                    paramsJSON2.put("device_name", (Object)deviceName);
                    action.put("params", (Object)paramsJSON2);
                    action.put("is_enabled", true);
                }
                actionList.put((Object)action);
            }
            if (managedStatus != 4 && managedStatus != 9 && managedStatus != 10 && managedStatus != 11 && managedStatus != -1) {
                final Boolean isAppBasedEnrollment = Boolean.valueOf(MDMUtil.getSyMParameter("IsAppBasedEnrollmentForWindowsPhone"));
                if (platform == 3 && !CustomerInfoUtil.isSAS && !isAppBasedEnrollment) {
                    final JSONObject action2 = new JSONObject();
                    action2.put("remarks", (Object)"dc.mdm.enroll.windows_app_enroll_mail");
                    action2.put("action", (Object)"windows_app_enroll_mail");
                    final JSONObject paramsJSON2 = new JSONObject();
                    paramsJSON2.put("platform", platform);
                    paramsJSON2.put("erid", (Object)reqID);
                    paramsJSON2.put("user_name", (Object)userName);
                    action2.put("params", (Object)paramsJSON2);
                    action2.put("is_enabled", true);
                    actionList.put((Object)action2);
                }
            }
            if (managedStatus == 2 && platform == 1 && MDMFeatureParamsHandler.getInstance().isFeatureEnabled("RegainIosDevice")) {
                final JSONObject action = new JSONObject();
                action.put("remarks", (Object)"mdm.apple.appCatalog.reconnect");
                action.put("action", (Object)"regain_ios_device");
                final JSONObject paramsJSON = new JSONObject();
                paramsJSON.put("erid", (Object)reqID);
                action.put("params", (Object)paramsJSON);
                action.put("is_enabled", true);
                actionList.put((Object)action);
            }
            responseJSON.put("actions", (Object)actionList);
            return responseJSON;
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Exception in enrollOpenActionList()", e);
            throw e;
        }
    }
}
