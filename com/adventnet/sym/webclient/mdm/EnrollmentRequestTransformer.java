package com.adventnet.sym.webclient.mdm;

import com.me.mdm.server.util.MDMFeatureParamsHandler;
import org.json.JSONArray;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.logging.Level;
import com.me.devicemanagement.framework.webclient.common.ProductUrlLoader;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import com.me.devicemanagement.framework.webclient.common.SYMClientUtil;
import com.adventnet.sym.server.mdm.enroll.MDMEnrollmentUtil;
import com.me.devicemanagement.framework.server.util.DMIAMEncoder;
import java.net.URLEncoder;
import com.adventnet.sym.server.mdm.MDMEntrollment;
import com.me.devicemanagement.framework.server.util.DBUtil;
import com.adventnet.sym.server.mdm.util.JSONUtil;
import com.adventnet.i18n.I18N;
import org.json.JSONObject;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.me.devicemanagement.framework.server.util.Utils;
import com.adventnet.client.view.web.ViewContext;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import com.me.devicemanagement.framework.server.customer.CustomerInfoUtil;
import com.me.mdm.server.factory.MDMApiFactoryProvider;
import com.adventnet.client.components.web.TransformerContext;
import java.util.logging.Logger;
import com.adventnet.client.components.table.web.DefaultTransformer;

public class EnrollmentRequestTransformer extends DefaultTransformer
{
    public Logger logger;
    
    public EnrollmentRequestTransformer() {
        this.logger = Logger.getLogger("MDMLogger");
    }
    
    protected boolean checkIfColumnRendererable(final TransformerContext tableContext) throws Exception {
        final String columnalias = tableContext.getPropertyName();
        final String isExport = MDMApiFactoryProvider.getMDMTableViewAPI().getIsExport(tableContext);
        final ViewContext viewCtx = tableContext.getViewContext();
        final String viewIdName = viewCtx.getUniqueId();
        if (columnalias.equals("UserResource.DOMAIN_NETBIOS_NAME") && CustomerInfoUtil.getInstance().isMSP()) {
            return false;
        }
        if (viewIdName.equalsIgnoreCase("EnrollmentRequestSearch") && (columnalias.equalsIgnoreCase("DeviceEnrollmentRequest.ENROLLMENT_REQUEST_ID") || columnalias.equals("Action"))) {
            return false;
        }
        if (!columnalias.equalsIgnoreCase("DeviceEnrollmentRequest.ENROLLMENT_REQUEST_ID") && !columnalias.equalsIgnoreCase("Action")) {
            return super.checkIfColumnRendererable(tableContext);
        }
        if (isExport != null && isExport.equalsIgnoreCase("true")) {
            return false;
        }
        final boolean isUserInRole = ApiFactoryProvider.getAuthUtilAccessAPI().getRoles().contains("MDM_Enrollment_Write");
        final boolean isUserInModernMgmtRole = ApiFactoryProvider.getAuthUtilAccessAPI().getRoles().contains("ModernMgmt_Enrollment_Write");
        return isUserInRole || isUserInModernMgmtRole;
    }
    
    public void renderHeader(final TransformerContext tableContext) {
        super.renderHeader(tableContext);
    }
    
    public void renderCell(final TransformerContext tableContext) {
        try {
            super.renderCell(tableContext);
            final HashMap columnProperties = tableContext.getRenderedAttributes();
            final String columnalais = tableContext.getPropertyName();
            final String isExport = MDMApiFactoryProvider.getMDMTableViewAPI().getIsExport(tableContext);
            final String viewName = tableContext.getViewContext().getUniqueId();
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
            if (columnalais.equals("DeviceEnrollmentRequest.ENROLLMENT_REQUEST_ID")) {
                final Long resourceID = (Long)data;
                String checkboxButton = "";
                final String selectedTab = (String)((tableContext.getViewContext().getRequest().getParameter("selectedEnrollTab") == null) ? tableContext.getViewContext().getRequest().getAttribute("selectedTabInput") : tableContext.getViewContext().getRequest().getParameter("selectedEnrollTab"));
                if (selectedTab.equalsIgnoreCase("enrolled")) {
                    checkboxButton = "<input type=\"checkbox\" value=\"" + resourceID + "\" name=\"selectDevice\" onclick=\"setSelectHead(this.checked)\">";
                }
                else if (selectedTab.equalsIgnoreCase("pending")) {
                    checkboxButton = "<input type=\"checkbox\" value=\"" + resourceID + "\" name=\"selectPendingDevice\" onclick=\"setSelectHead(this.checked)\">";
                }
                final Boolean isEnabled = (Boolean)tableContext.getAssociatedPropertyValue("MdDeviceInfo.IS_ACTIVATION_LOCK_ENABLED");
                checkboxButton = checkboxButton + "<input type=\"hidden\" value=\"" + isEnabled + "\" id=\"activationLock_" + resourceID + "\">";
                if (viewName.equalsIgnoreCase("DevicesAwaitingLicense")) {
                    final Integer modelType = (Integer)tableContext.getAssociatedPropertyValue("MdModelInfo.MODEL_TYPE");
                    checkboxButton = checkboxButton + "<input type=\"hidden\" value=\"" + modelType + "\" id=\"modelType_" + resourceID + "\">";
                    final Integer platformType = (Integer)tableContext.getAssociatedPropertyValue("DeviceEnrollmentRequest.PLATFORM_TYPE");
                    checkboxButton = checkboxButton + "<input type=\"hidden\" value=\"" + platformType + "\" id=\"devPlatformType_" + resourceID + "\">";
                }
                columnProperties.put("VALUE", checkboxButton);
            }
            if (columnalais.equals("ManagedDeviceExtn.NAME")) {
                final String deviceName = (String)data;
                if (deviceName == null) {
                    columnProperties.put("VALUE", "--");
                }
                else {
                    columnProperties.put("VALUE", deviceName);
                }
            }
            if (columnalais.equals("DeviceEnrollmentRequest.REQUESTED_TIME")) {
                final Long requestedTime = (Long)tableContext.getAssociatedPropertyValue("DeviceEnrollmentRequest.REQUESTED_TIME");
                String value = "--";
                if (requestedTime != null) {
                    value = Utils.getEventTime(requestedTime);
                }
                if (isExport == null || isExport.equalsIgnoreCase("false")) {
                    value = "<div style=\"width:135px;\">" + value + "</div>";
                }
                columnProperties.put("VALUE", value);
            }
            if (columnalais.equals("ManagedDevice.REGISTERED_TIME")) {
                final Long enrolledTime = (Long)tableContext.getAssociatedPropertyValue("ManagedDevice.REGISTERED_TIME");
                String value = "--";
                if (enrolledTime != null) {
                    value = Utils.getEventTime(enrolledTime);
                }
                if (isExport == null || isExport.equalsIgnoreCase("false")) {
                    value = "<div style=\"width:135px;\">" + value + "</div>";
                }
                columnProperties.put("VALUE", value);
            }
            if (columnalais.equals("DeviceEnrollmentRequest.PLATFORM_TYPE")) {
                final Integer platformType2 = (Integer)data;
                String platformName = "";
                if (platformType2 == 1) {
                    platformName = "<img src=\"/images/appleicon.png\" width=\"25\" height=\"25\" border=\"0\" align=\"top\"/> ";
                }
                else if (platformType2 == 2) {
                    platformName = "<img src=\"/images/androidicon.png\" width=\"25\" height=\"25\" border=\"0\" align=\"top\"/> ";
                }
                else if (platformType2 == 3) {
                    platformName = "<img src=\"/images/windowsicon.png\" width=\"25\" height=\"25\" border=\"0\" align=\"top\"/> ";
                }
                else if (platformType2 == 4) {
                    platformName = "<img src=\"/images/chrome.png\" width=\"20\" height=\"20\" border=\"0\" align=\"top\"/> ";
                }
                if (isExport == null || isExport.equalsIgnoreCase("false")) {
                    columnProperties.put("VALUE", platformName);
                }
                else {
                    platformName = "";
                    platformName = MDMUtil.getInstance().getPlatformColumnValue(platformType2, isExport);
                    columnProperties.put("VALUE", platformName);
                }
            }
            if (columnalais.equals("ManagedDevice.AGENT_TYPE")) {
                String platformName2 = "";
                if (data != null) {
                    final Integer platformType3 = (Integer)data;
                    platformName2 = MDMUtil.getInstance().getAgentValue(platformType3, isExport);
                }
                columnProperties.put("VALUE", (data != null) ? platformName2 : "--");
            }
            final int reportType = tableContext.getViewContext().getRenderType();
            if (viewName.equalsIgnoreCase("SafetyNetDevices") && columnalais.equals("ManagedDevice.MANAGED_STATUS")) {
                final JSONObject payload = new JSONObject();
                String statusClass = "";
                String managedstatus = (String)tableContext.getAssociatedPropertyValue("ManagedDeviceStatus.STATUS_LABEL");
                if (data == null || (int)data == 1 || (int)data == 0) {
                    managedstatus = (String)tableContext.getAssociatedPropertyValue("DeviceEnrollmentRequestStatus.STATUS_LABEL");
                    if (managedstatus == null) {
                        managedstatus = "--";
                    }
                    else {
                        managedstatus = "dc.mdm.yet_to_enroll";
                        statusClass = "ucs-table-status-text__ready";
                    }
                }
                else if ((int)data == 10) {
                    managedstatus = "dc.mdm.in_stock";
                }
                else if ((int)data == 9) {
                    managedstatus = "mdm.deprovision.in_repair";
                }
                else if ((int)data == 11) {
                    managedstatus = "dc.mdm.retired";
                    statusClass = "ucs-table-status-text__failed";
                }
                final String value2;
                managedstatus = (value2 = I18N.getMsg(managedstatus, new Object[0]));
                final String actionStr = "";
                if (data == null || (int)data == 1 || (int)data == 0) {
                    final Long reqId = (Long)tableContext.getAssociatedPropertyValue("DeviceEnrollmentRequest.ENROLLMENT_REQUEST_ID");
                    final Long expireTime = (Long)tableContext.getAssociatedPropertyValue("OTPPassword.EXPIRE_TIME");
                    final Integer request_status = (Integer)tableContext.getAssociatedPropertyValue("DeviceEnrollmentRequest.REQUEST_STATUS");
                    if (expireTime != null && expireTime < System.currentTimeMillis()) {
                        statusClass = "ucs-table-status-text__failed";
                        managedstatus = I18N.getMsg("mdm.enroll.request_expired_status", new Object[0]);
                    }
                    else if (request_status == 0) {
                        statusClass = "ucs-table-status-text__failed";
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
                        statusClass = "ucs-table-status-text__failed";
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
                    if (remarks.equalsIgnoreCase("mdm.enroll.assign_in_progess_remarks")) {
                        managedstatus = I18N.getMsg("mdm.enroll.assign_in_progess", new Object[0]);
                        statusClass = "ucs-table-status-text__in-progress";
                    }
                    else if (lastContactTime <= inactiveTime) {
                        statusClass = "ucs-table-status-text__in-progress";
                    }
                    else {
                        statusClass = "ucs-table-status-text__in-progress";
                    }
                }
                else if ((int)data == 4 || (int)data == 9 || (int)data == 10) {
                    statusClass = "ucs-table-status-text__failed";
                }
                else if ((int)data == 11) {
                    final Object templateobj = tableContext.getAssociatedPropertyValue("EnrollmentTemplate.TEMPLATE_TYPE");
                    int templateType = -1;
                    if (templateobj != null) {
                        templateType = (int)templateobj;
                    }
                    if (templateType != 21 && templateType != 10) {
                        statusClass = "ucs-table-status-text__failed";
                    }
                    else {
                        final JSONObject json2 = new JSONObject();
                        json2.put("templateType", templateType);
                        payload.put("tooltipJSON", (Object)json2.toString());
                        statusClass = "ucs-table-status-text__failed";
                    }
                }
                payload.put("managedstatus", (Object)managedstatus);
                payload.put("statusOnly", true);
                payload.put("statusClass", (Object)statusClass);
                if (reportType != 4) {
                    columnProperties.put("VALUE", managedstatus);
                }
                else {
                    columnProperties.put("PAYLOAD", payload);
                }
            }
            if (!viewName.equalsIgnoreCase("SafetyNetDevices") && columnalais.equals("ManagedDevice.MANAGED_STATUS")) {
                final JSONObject payload = new JSONObject();
                String image = "";
                String managedstatus = (String)tableContext.getAssociatedPropertyValue("ManagedDeviceStatus.STATUS_LABEL");
                String statusClass2 = "";
                if (data == null || (int)data == 1 || (int)data == 0) {
                    managedstatus = (String)tableContext.getAssociatedPropertyValue("DeviceEnrollmentRequestStatus.STATUS_LABEL");
                    if (managedstatus == null) {
                        managedstatus = "--";
                    }
                    else {
                        managedstatus = "dc.mdm.yet_to_enroll";
                        statusClass2 = "ucs-table-status-text__ready";
                    }
                }
                else if ((int)data == 10) {
                    managedstatus = "dc.mdm.in_stock";
                }
                else if ((int)data == 9) {
                    managedstatus = "mdm.deprovision.in_repair";
                }
                else if ((int)data == 11) {
                    managedstatus = "dc.mdm.retired";
                }
                final String value3;
                managedstatus = (value3 = I18N.getMsg(managedstatus, new Object[0]));
                final String actionStr2 = "";
                if (isExport == null || isExport.equalsIgnoreCase("false")) {
                    if (data == null || (int)data == 1 || (int)data == 0) {
                        final Long reqId2 = (Long)tableContext.getAssociatedPropertyValue("DeviceEnrollmentRequest.ENROLLMENT_REQUEST_ID");
                        final Long expireTime2 = (Long)tableContext.getAssociatedPropertyValue("OTPPassword.EXPIRE_TIME");
                        final Integer request_status2 = (Integer)tableContext.getAssociatedPropertyValue("DeviceEnrollmentRequest.REQUEST_STATUS");
                        if (expireTime2 != null && expireTime2 < System.currentTimeMillis()) {
                            managedstatus = I18N.getMsg("mdm.enroll.request_expired_status", new Object[0]);
                            statusClass2 = "ucs-table-status-text__failed";
                        }
                        else if (request_status2 == 0) {
                            managedstatus = managedstatus;
                            statusClass2 = "ucs-table-status-text__failed";
                        }
                        else {
                            if (reqId2 != null) {
                                final JSONObject json3 = new JSONObject();
                                json3.put("ENROLLMENT_REQUEST_ID", (Object)reqId2);
                                json3.put("PLATFORM_TYPE", (Object)tableContext.getAssociatedPropertyValue("DeviceEnrollmentRequest.PLATFORM_TYPE"));
                                json3.put("IS_INVITED_BY_ADMIN", (Object)tableContext.getAssociatedPropertyValue("InvitationEnrollmentRequest.IS_INVITED_BY_ADMIN"));
                                final Long smsSent2 = (Long)tableContext.getAssociatedPropertyValue("DEVICEENROLLREQTOSMS.ENROLLMENT_REQUEST_ID");
                                json3.put("INVITATION_BY_SMS", smsSent2 != null);
                                json3.put("REGISTRATION_STATUS", (Object)tableContext.getAssociatedPropertyValue("InvitationEnrollmentRequest.REGISTRATION_STATUS"));
                                payload.put("tooltipJSON", (Object)JSONUtil.getInstance().convertLongToString(json3));
                                image = "yet-to-enroll";
                            }
                            statusClass2 = "ucs-table-status-text__ready";
                            managedstatus = managedstatus;
                        }
                    }
                    else if ((int)data == 2) {
                        final String remarks2 = (String)tableContext.getAssociatedPropertyValue("ManagedDevice.REMARKS");
                        if (remarks2.equalsIgnoreCase("mdm.enroll.assign_in_progess_remarks")) {
                            managedstatus = I18N.getMsg("mdm.enroll.assign_in_progess", new Object[0]);
                            statusClass2 = "ucs-table-status-text__in-progress";
                        }
                        else {
                            managedstatus = managedstatus;
                        }
                    }
                    else if ((int)data == 4 || (int)data == 9 || (int)data == 10) {
                        managedstatus = managedstatus;
                        statusClass2 = "ucs-table-status-text__not-applicable";
                    }
                    else if ((int)data == 11) {
                        final Object templateobj2 = tableContext.getAssociatedPropertyValue("EnrollmentTemplate.TEMPLATE_TYPE");
                        int templateType2 = -1;
                        if (templateobj2 != null) {
                            templateType2 = (int)templateobj2;
                        }
                        if (templateType2 != 21 && templateType2 != 10) {
                            managedstatus = managedstatus;
                            statusClass2 = "ucs-table-status-text__failed";
                        }
                        else {
                            final JSONObject json = new JSONObject();
                            json.put("templateType", templateType2);
                            payload.put("tooltipJSON", (Object)JSONUtil.getInstance().convertLongToString(json));
                            image = "retire";
                            statusClass2 = "ucs-table-status-text__failed";
                            managedstatus = managedstatus;
                        }
                    }
                }
                if (isExport == null || isExport.equalsIgnoreCase("false")) {
                    payload.put("statusClass", (Object)statusClass2);
                    payload.put("managedstatus", (Object)I18N.getMsg(managedstatus, new Object[0]));
                    payload.put("image", (Object)image);
                    payload.put("statusOnly", true);
                    columnProperties.put("PAYLOAD", payload);
                    columnProperties.put("VALUE", managedstatus);
                }
                else {
                    columnProperties.put("VALUE", value3);
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
            if (columnalais.equals("GROUP_NAME")) {
                String name = (String)tableContext.getAssociatedPropertyValue("GROUP_NAME");
                name = ((name == null) ? "--" : name);
                if (DBUtil.getActiveDBName().equalsIgnoreCase("mssql") && !name.equalsIgnoreCase("--")) {
                    name = name.substring(0, name.lastIndexOf(","));
                }
                String val = "<div style=\"width: 115px;margin-left:7px;\" >" + name + "</div>";
                if (name.length() > 13) {
                    final Long erid = (Long)tableContext.getAssociatedPropertyValue("DeviceEnrollmentRequest.ENROLLMENT_REQUEST_ID");
                    final String uniqueID = "stagedmailAddr_" + erid;
                    final String trimmedValue = name.substring(0, 13) + " ...";
                    val = "<div style=\"width: 115px;margin-left:7px;\" id=\"" + uniqueID + "\"><span onmouseover=\"javascript:customShowCompleteGroupInEnrollMessage('" + uniqueID + "', '" + name + "');return false\" onmouseout=\"hideCustomMessage();return false;\">" + trimmedValue + "</span></div>";
                }
                if (isExport == null || isExport.equalsIgnoreCase("false")) {
                    columnProperties.put("VALUE", val);
                }
                else {
                    columnProperties.put("VALUE", name);
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
            if (columnalais.equals("DeviceEnrollmentRequest.OWNED_BY")) {
                final Integer ownedBy = (Integer)data;
                final Long resourceID2 = (Long)tableContext.getAssociatedPropertyValue("DeviceEnrollmentRequest.ENROLLMENT_REQUEST_ID");
                String sOwnedBy = MDMEntrollment.getInstance().getOwnedByAsString(ownedBy);
                if ((ApiFactoryProvider.getAuthUtilAccessAPI().getRoles().contains("MDM_Enrollment_Write") || ApiFactoryProvider.getAuthUtilAccessAPI().getRoles().contains("ModernMgmt_Enrollment_Write")) && (isExport == null || isExport.equalsIgnoreCase("false"))) {
                    String defaultSelected = "";
                    String corporateSelected = "";
                    String personalSelected = "";
                    if (ownedBy == 0) {
                        defaultSelected = "selected";
                    }
                    else if (ownedBy == 1) {
                        corporateSelected = "selected";
                    }
                    else if (ownedBy == 2) {
                        personalSelected = "selected";
                    }
                    sOwnedBy = "<span id='OWNED_BY_ANCHOR_" + resourceID2 + "' onmouseover='showEditOption(\"" + resourceID2 + "\")' onmouseout='hideEditOption(\"" + resourceID2 + "\")'><span id='OWNED_BY_SPAN_" + resourceID2 + "' class='bodytext'> " + sOwnedBy + "</span>&nbsp;&nbsp;<img id=\"OWNED_BY_EDIT_" + resourceID2 + "\" onclick=\"showDropDownOptions('" + resourceID2 + "')\" class=\"hide\" src=\"/images/modify.png\" border=\"0\" width=\"12\" height=\"12\" align=\"absmiddle\" ></span>";
                    sOwnedBy = sOwnedBy + "<select id='OWNED_BY_SELECT_" + resourceID2 + "' onchange='changeDropDownValue(\"" + resourceID2 + "\")' class='hide' > </option> <option value='1' " + corporateSelected + ">" + I18N.getMsg("dc.mdm.enroll.corporate", new Object[0]) + "</option> <option value='2' " + personalSelected + ">" + I18N.getMsg("dc.mdm.enroll.personal", new Object[0]) + "</option> </select>";
                }
                columnProperties.put("VALUE", sOwnedBy);
            }
            if (columnalais.equals("EnrollmentTemplate.TEMPLATE_TYPE")) {
                final Object object = tableContext.getAssociatedPropertyValue("EnrollmentTemplate.TEMPLATE_TYPE");
                String val = I18N.getMsg("mdm.enroll.by_invite", new Object[0]);
                if (object != null) {
                    final int templateType3 = (int)object;
                    if (templateType3 == 20) {
                        val = I18N.getMsg("dc.mdm.enroll.android_admin_enrollment", new Object[0]);
                    }
                    else if (templateType3 == 11) {
                        val = I18N.getMsg("dc.mdm.enroll.apple_configurator", new Object[0]);
                    }
                    else if (templateType3 == 21) {
                        val = I18N.getMsg("mdm.enroll.knox", new Object[0]);
                    }
                    else if (templateType3 == 30) {
                        val = I18N.getMsg("mdm.common.WINDOWS_10", new Object[0]);
                    }
                    else if (templateType3 == 22) {
                        val = I18N.getMsg("mdm.enroll.emm", new Object[0]);
                    }
                    else if (templateType3 == 31) {
                        val = I18N.getMsg("mdm.enroll.laptop", new Object[0]);
                    }
                    else if (templateType3 == 23) {
                        val = I18N.getMsg("mdm.enroll.zerotouch", new Object[0]);
                    }
                    else if (templateType3 == 10) {
                        val = I18N.getMsg("mdm.enroll.apple_dep", new Object[0]);
                    }
                    else if (templateType3 == 32) {
                        val = I18N.getMsg("mdm.enroll.autopilot", new Object[0]);
                    }
                    else if (templateType3 == 40) {
                        val = I18N.getMsg("mdm.enroll.chrome", new Object[0]);
                    }
                    else if (templateType3 == 12) {
                        val = I18N.getMsg("mdm.enroll.mac.enrolled_by_dc_agent", new Object[0]);
                    }
                    else if (templateType3 == 33) {
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
                final String remarks3 = (String)tableContext.getAssociatedPropertyValue("ManagedDevice.REMARKS");
                if (remarks3 != null && remarks3.equalsIgnoreCase("mdm.enroll.assign_in_progess_remarks")) {
                    final String overlibText = I18N.getMsg("mdm.enrolled.disabled_while_assigning", new Object[0]);
                    final String value4 = "<span  style=\"white-space:nowrap;cursor: not-allowed;\" onmouseover=\"javascript:showOverLib('" + overlibText + "')\" onmouseout=\"return nd();\" ><img src=\"/images/action_dropdown.png\" width=\"20\" height=\"16\" hspace=\"3\" vspace=\"0\" align=\"absmiddle\"></span>";
                    columnProperties.put("VALUE", value4);
                }
                else {
                    String userName = (String)tableContext.getAssociatedPropertyValue("UserResource.NAME");
                    userName = URLEncoder.encode(MDMUtil.getInstance().encodeURIComponentEquivalent(userName), "UTF-8");
                    String emailAddress = (String)tableContext.getAssociatedPropertyValue("ManagedUser.EMAIL_ADDRESS");
                    emailAddress = URLEncoder.encode(MDMUtil.getInstance().encodeURIComponentEquivalent(emailAddress), "UTF-8");
                    String deviceName2 = (String)tableContext.getAssociatedPropertyValue("ManagedDeviceExtn.NAME");
                    deviceName2 = ((deviceName2 != null) ? URLEncoder.encode(deviceName2, "UTF-8") : deviceName2);
                    final Long reqId3 = (Long)tableContext.getAssociatedPropertyValue("DeviceEnrollmentRequest.ENROLLMENT_REQUEST_ID");
                    String actionStr2 = "";
                    if (reqId3 != null) {
                        final Object udidObj = tableContext.getAssociatedPropertyValue("ManagedDevice.UDID");
                        String udid2 = null;
                        if (udidObj != null) {
                            udid2 = (String)udidObj;
                        }
                        final Object templateobj3 = tableContext.getAssociatedPropertyValue("EnrollmentTemplate.TEMPLATE_TYPE");
                        int templateType4 = -1;
                        if (templateobj3 != null) {
                            templateType4 = (int)templateobj3;
                        }
                        final String easid = (String)tableContext.getAssociatedPropertyValue("MdDeviceInfo.EAS_DEVICE_IDENTIFIER");
                        final String slno = (String)tableContext.getAssociatedPropertyValue("MdDeviceInfo.SERIAL_NUMBER");
                        final String imei = (String)tableContext.getAssociatedPropertyValue("MdDeviceInfo.IMEI");
                        final Integer platformType4 = (Integer)tableContext.getAssociatedPropertyValue("DeviceEnrollmentRequest.PLATFORM_TYPE");
                        final Integer modelType2 = (Integer)tableContext.getAssociatedPropertyValue("MdModelInfo.MODEL_TYPE");
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
                        final JSONObject json4 = new JSONObject();
                        final Boolean isDeviceProvisioningUser = (Boolean)tableContext.getViewContext().getRequest().getAttribute("isDeviceProvisioningUser");
                        json4.put("isDeviceProvisioningUser", (Object)isDeviceProvisioningUser);
                        json4.put("MANAGED_USER_ID", (Object)String.valueOf(tableContext.getAssociatedPropertyValue("ManagedUser.MANAGED_USER_ID")));
                        json4.put("EMAIL_ADDRESS", (Object)emailAddress);
                        json4.put("NAME", (Object)userName);
                        json4.put("DOMAIN_NETBIOS_NAME", (Object)tableContext.getAssociatedPropertyValue("UserResource.DOMAIN_NETBIOS_NAME"));
                        json4.put("ENROLLMENT_REQUEST_ID", (Object)String.valueOf(reqId3));
                        json4.put("ENROLLMENT_TYPE", tableContext.getAssociatedPropertyValue("DeviceEnrollmentRequest.ENROLLMENT_TYPE"));
                        json4.put("MANAGED_STATUS", (Object)tableContext.getAssociatedPropertyValue("ManagedDevice.MANAGED_STATUS"));
                        json4.put("REQUEST_STATUS", (Object)tableContext.getAssociatedPropertyValue("DeviceEnrollmentRequest.REQUEST_STATUS"));
                        json4.put("ERROR_CODE", (Object)tableContext.getAssociatedPropertyValue("DeviceEnrollReqToErrCode.ERROR_CODE"));
                        json4.put("PLATFORM_TYPE", (Object)platformType4);
                        json4.put("UDID", (Object)tableContext.getAssociatedPropertyValue("DeviceEnrollmentRequest.UDID"));
                        json4.put("DEVICE_NAME", (Object)deviceName2);
                        json4.put("BY_ADMIN", (Object)tableContext.getAssociatedPropertyValue("InvitationEnrollmentRequest.IS_INVITED_BY_ADMIN"));
                        json4.put("AUTH_MODE", (Object)tableContext.getAssociatedPropertyValue("DeviceEnrollmentRequest.AUTH_MODE"));
                        json4.put("IS_MSP", !CustomerInfoUtil.getInstance().isMSP());
                        if (lastContactTime2 <= inactiveTime2) {
                            json4.put("isInactive", true);
                        }
                        else {
                            json4.put("isInactive", false);
                        }
                        Integer appInstallationStatus = (Integer)tableContext.getAssociatedPropertyValue("IOSNativeAppStatus.INSTALLATION_STATUS");
                        if (appInstallationStatus == null) {
                            appInstallationStatus = 0;
                        }
                        json4.put("IMEI", (Object)imei);
                        json4.put("SLNO", (Object)slno);
                        json4.put("EASID", (Object)easid);
                        json4.put("udid", (Object)udid2);
                        final Long expireTime3 = (Long)tableContext.getAssociatedPropertyValue("OTPPassword.EXPIRE_TIME");
                        Boolean isExpired = false;
                        if (expireTime3 != null && expireTime3 < System.currentTimeMillis()) {
                            isExpired = true;
                        }
                        json4.put("isExpired", (Object)isExpired);
                        json4.put("INSTALLATION_STATUS", (Object)appInstallationStatus);
                        json4.put("MODEL_TYPE", (Object)modelType2);
                        json4.put("TEMPLATE_TYPE", templateType4);
                        if (templateType4 == 12 || (platformType4.equals(3) && modelType2 != null && (modelType2.equals(3) || modelType2.equals(4) || modelType2.equals(2)))) {
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
                                    json4.put("MODERN_MGMT_DEVICE_MANAGED_IN_SOM", true);
                                }
                            }
                        }
                        final int ownedby = Integer.valueOf(tableContext.getAssociatedPropertyValue("DeviceEnrollmentRequest.OWNED_BY").toString());
                        json4.put("OWNEDBY", ownedby);
                        actionStr2 = this.enrollOpenActionList(json4).toString();
                    }
                    columnProperties.put("VALUE", actionStr2);
                }
            }
            if (columnalais.equalsIgnoreCase("ManagedUser.EMAIL_ADDRESS")) {
                if (data == null || ((String)data).isEmpty()) {
                    data = "--";
                    columnProperties.put("VALUE", String.valueOf(data));
                }
                else if ((isExport == null || isExport.equalsIgnoreCase("false")) && (ApiFactoryProvider.getAuthUtilAccessAPI().getRoles().contains("MDM_Enrollment_Write") || ApiFactoryProvider.getAuthUtilAccessAPI().getRoles().contains("MDM_Enrollment_Admin") || ApiFactoryProvider.getAuthUtilAccessAPI().getRoles().contains("ModernMgmt_Enrollment_Admin") || ApiFactoryProvider.getAuthUtilAccessAPI().getRoles().contains("ModernMgmt_Enrollment_Write"))) {
                    final Integer managedStatus = (Integer)tableContext.getAssociatedPropertyValue("ManagedDevice.MANAGED_STATUS");
                    if ((managedStatus == null || (managedStatus != null && managedStatus != 4)) && !CustomerInfoUtil.isSAS) {
                        final Long reqID = (Long)tableContext.getAssociatedPropertyValue("DeviceEnrollmentRequest.ENROLLMENT_REQUEST_ID");
                        final String domainName = (String)tableContext.getAssociatedPropertyValue("UserResource.DOMAIN_NETBIOS_NAME");
                        String email = (String)data;
                        if (email.length() > 20) {
                            email = DMIAMEncoder.encodeHTML(email.substring(0, email.indexOf("@"))) + "<br>" + DMIAMEncoder.encodeHTML(email.substring(email.indexOf("@")));
                        }
                        if (domainName.equalsIgnoreCase("MDM")) {
                            columnProperties.put("VALUE", "<div id=\"ERViewemailComp_" + reqID + "\" style=\"white-space:nowrap;padding-right:10px;display: block;\" onmouseover=\"showEREmailEditIcon('" + reqID + "')\" onmouseout=\"hideEREmailEditIcon('" + reqID + "')\"><span onclick=\"editEREmail('" + reqID + "')\"  id=\"ERViewemailAddress_" + reqID + "\">" + email + "</span> &nbsp;&nbsp;<img style=\"visibility:hidden; cursor: hand;\" id=\"ERViewemailAddressEditIcon_" + reqID + "\" onclick=\"editEREmail('" + reqID + "')\" src=\"/images/modify.png\" border=\"0\" width=\"12\" height=\"12\" align=\"absmiddle\" title=\"" + I18N.getMsg("dc.mdm.enroll.edit_user_email", new Object[0]) + "\"></div>");
                        }
                        else {
                            columnProperties.put("VALUE", "<div id=\"ERViewemailComp_" + reqID + "\" style=\"white-space:nowrap;padding-right:10px;display: block;\" onmouseover=\"showEREmailSyncIcon('" + reqID + "')\" onmouseout=\"hideEREmailSyncIcon('" + reqID + "')\"><span onclick=\"syncEREmail('" + reqID + "')\"  id=\"ERViewemailAddress_" + reqID + "\">" + email + "</span> &nbsp;&nbsp;<img style=\"visibility:hidden; cursor: hand;\" id=\"ERViewemailAddressSyncIcon_" + reqID + "\" onclick=\"syncEREmail('" + reqID + "')\" src=\"/images/syncButton.png\" border=\"0\" width=\"12\" height=\"12\" align=\"absmiddle\" title=\"" + I18N.getMsg("dc.mdm.enroll.sync_user_email", new Object[0]) + "\"></div>");
                        }
                    }
                    else if (managedStatus != null && managedStatus == 4) {
                        String email2 = (String)data;
                        if (email2.length() > 20) {
                            email2 = DMIAMEncoder.encodeHTML(email2.substring(0, email2.indexOf("@"))) + "<br>" + DMIAMEncoder.encodeHTML(email2.substring(email2.indexOf("@")));
                            columnProperties.put("VALUE", "<div style=\"white-space:nowrap;padding-right:10px;display: block;\"><span>" + email2 + "</span></div>");
                        }
                    }
                    else {
                        columnProperties.put("VALUE", DMIAMEncoder.encodeHTML((String)data));
                    }
                }
                else if (isExport == null || isExport.equalsIgnoreCase("false")) {
                    columnProperties.put("VALUE", DMIAMEncoder.encodeHTML((String)data));
                }
            }
            if (columnalais.equalsIgnoreCase("SCOPE") && data != null) {
                final int installedIn = (int)data;
                columnProperties.put("VALUE", (installedIn == 1) ? "Container" : "Device");
            }
            if (columnalais.equals("UserResource.NAME")) {
                final Integer managedUserStatus = (Integer)tableContext.getAssociatedPropertyValue("ManagedUser.STATUS");
                if (isExport == null || isExport.equalsIgnoreCase("false")) {
                    if ((isExport == null || isExport.equalsIgnoreCase("false")) && CustomerInfoUtil.isSAS) {
                        final Long reqID = (Long)tableContext.getAssociatedPropertyValue("DeviceEnrollmentRequest.ENROLLMENT_REQUEST_ID");
                        if (!MDMUtil.isStringEmpty((String)data) && managedUserStatus != null && managedUserStatus != 11) {
                            if (((String)tableContext.getAssociatedPropertyValue("UserResource.DOMAIN_NETBIOS_NAME")).equalsIgnoreCase("MDM")) {
                                columnProperties.put("VALUE", "<div id=\"ERViewUserNameComp_" + reqID + "\" style=\"white-space:nowrap;display: block;\" onmouseover=\"showERUserNameEditIcon('" + String.valueOf(reqID) + "')\" onmouseout=\"hideERUserNameEditIcon('" + String.valueOf(reqID) + "')\"><span onclick=\"editERUserName('" + String.valueOf(reqID) + "')\"  id=\"ERViewUserName_" + reqID + "\">" + data + "</span> &nbsp;&nbsp;<img style=\"visibility:hidden; cursor: hand;\" id=\"ERViewUserNameEditIcon_" + reqID + "\" onclick=\"editERUserName('" + String.valueOf(reqID) + "')\" src=\"/images/modify.png\" border=\"0\" width=\"12\" height=\"12\" align=\"absmiddle\"></div>");
                            }
                            else {
                                columnProperties.put("VALUE", "<div id=\"ERViewUserNameComp_" + reqID + "\" style=\"white-space:nowrap;display: block;\" onmouseover=\"showERUserNameEditIcon('" + String.valueOf(reqID) + "')\" onmouseout=\"hideERUserNameEditIcon('" + String.valueOf(reqID) + "')\"><span onclick=\"cannotEditERUserName('" + String.valueOf(reqID) + "')\"  id=\"ERViewUserName_" + reqID + "\">" + data + "</span> &nbsp;&nbsp;<img style=\"visibility:hidden; cursor: hand;\" id=\"ERViewUserNameEditIcon_" + reqID + "\" onclick=\"cannotEditERUserName('" + String.valueOf(reqID) + "')\" src=\"/images/modify.png\" border=\"0\" width=\"12\" height=\"12\" align=\"absmiddle\"></div>");
                            }
                        }
                        else {
                            columnProperties.put("VALUE", "--");
                        }
                    }
                    else if (managedUserStatus != null && managedUserStatus == 11) {
                        columnProperties.put("VALUE", "--");
                    }
                    else {
                        columnProperties.put("VALUE", DMIAMEncoder.encodeHTML((String)data));
                    }
                }
                else if (managedUserStatus != null && managedUserStatus == 11) {
                    columnProperties.put("VALUE", "--");
                }
                else {
                    columnProperties.put("VALUE", data);
                }
            }
            if (columnalais.equals("ManagedDevice.REMARKS")) {
                final Integer request_status3 = (Integer)tableContext.getAssociatedPropertyValue("DeviceEnrollmentRequest.REQUEST_STATUS");
                final Integer device_status = (Integer)tableContext.getAssociatedPropertyValue("ManagedDevice.MANAGED_STATUS");
                if (data == null || ((request_status3 == 0 || request_status3 == 1) && (device_status == null || (device_status != 4 && device_status != 9 && device_status != 11 && device_status != 10)))) {
                    data = tableContext.getAssociatedPropertyValue("DeviceEnrollmentRequest.REMARKS");
                    String remarks4 = (String)data;
                    final Long expireTime4 = (Long)tableContext.getAssociatedPropertyValue("OTPPassword.EXPIRE_TIME");
                    if (request_status3 == 0 && ("dc.mdm.enroll.email_invite_failed".equalsIgnoreCase(remarks4) || "dc.mdm.enroll.sms_invite_failed".equals(remarks4) || "dc.mdm.enroll.both_invite_failed".equals(remarks4))) {
                        String emailInvitationFailureReason = (String)tableContext.getAssociatedPropertyValue("EREmailInvitationFailure.BOUNCE_REASON");
                        final Integer smsErrorCode = (Integer)tableContext.getAssociatedPropertyValue("DEVICEENROLLREQTOSMS.SMS_CODE");
                        final String smsRemarks = (String)tableContext.getAssociatedPropertyValue("DEVICEENROLLREQTOSMS.SMS_REMARKS");
                        if (emailInvitationFailureReason != null && smsErrorCode != null && smsErrorCode != 0) {
                            remarks4 = "dc.mdm.enroll.both_invite_failed";
                        }
                        String val2 = I18N.getMsg(remarks4, new Object[0]);
                        if (isExport == null || isExport.equalsIgnoreCase("false")) {
                            JSONObject bounceJSON = new JSONObject();
                            if (emailInvitationFailureReason != null) {
                                bounceJSON = new JSONObject(emailInvitationFailureReason);
                                bounceJSON.put("hasEmailHookInfo", true);
                            }
                            if (smsErrorCode != null && smsErrorCode != 0) {
                                MDMEnrollmentUtil.appendSMSWebhookInfoIntoJSON(bounceJSON, smsErrorCode);
                            }
                            emailInvitationFailureReason = DMIAMEncoder.encodeJavaScript(JSONUtil.getInstance().convertLongToString(bounceJSON).toString());
                            val2 = "<div class='REMARKS_" + String.valueOf(request_status3) + "' >" + val2 + "<span class='ACTION_STR' style='vertical-align:middle' onclick=\"displayMailBounceHelpDiv('" + emailInvitationFailureReason + "')\"> <img src=\"/images/help.png\" hspace=\"3\" vspace=\"0\" align=\"absmiddle\"></span></div>";
                        }
                        else {
                            val2 = val2 + " : " + emailInvitationFailureReason;
                        }
                        columnProperties.put("VALUE", val2);
                    }
                    else if (request_status3 == 0 && isExport == null) {
                        SYMClientUtil.renderRemarksWithKB(tableContext, columnProperties, data, (String)null, false);
                    }
                    else if (expireTime4 != null && expireTime4 < System.currentTimeMillis()) {
                        final Boolean byadmin = (Boolean)tableContext.getAssociatedPropertyValue("InvitationEnrollmentRequest.IS_INVITED_BY_ADMIN");
                        final Long erid2 = (Long)tableContext.getAssociatedPropertyValue("DeviceEnrollmentRequest.ENROLLMENT_REQUEST_ID");
                        final boolean isUserInWriteRole = ApiFactoryProvider.getAuthUtilAccessAPI().getRoles().contains("MDM_Enrollment_Write") || ApiFactoryProvider.getAuthUtilAccessAPI().getRoles().contains("ModernMgmt_Enrollment_Write");
                        final String arg1 = isUserInWriteRole ? ("<a href=\"javascript:resendEnrollmentRequest('" + String.valueOf(erid2) + "', '" + String.valueOf(byadmin) + "');\">") : "";
                        final String arg2 = isUserInWriteRole ? "</a>" : "";
                        final String expireTimeStr = SyMUtil.getSyMParameterFromDB("EXPIRE_TIME");
                        Long expiretime = 604800000L;
                        if (expireTimeStr != null) {
                            expiretime = Long.parseLong(expireTimeStr);
                        }
                        final int days = (int)(expiretime / 86400000L);
                        final String actionStr3 = "<div class='REMARKS_" + String.valueOf(0) + "' >" + I18N.getMsg("mdm.enroll.request_expired_remark", new Object[] { days, arg1, arg2 }) + "</div>";
                        columnProperties.put("VALUE", actionStr3);
                    }
                    else {
                        final String val3 = I18N.getMsg((String)data, new Object[0]);
                        String actionStr2 = "";
                        if (device_status == null) {
                            actionStr2 = "<div class='REMARKS_" + String.valueOf(request_status3) + "' >" + val3 + "</div>";
                        }
                        else {
                            actionStr2 = "<div class='REMARKS_" + String.valueOf(device_status) + "' >" + val3 + "</div>";
                        }
                        if (isExport == null || isExport.equalsIgnoreCase("false")) {
                            columnProperties.put("VALUE", actionStr2);
                        }
                        else {
                            columnProperties.put("VALUE", val3);
                        }
                    }
                }
                else {
                    String val4;
                    if (data.equals("mdm.agent.compliance.action.corporatewiped_rooted")) {
                        val4 = I18N.getMsg((String)data, new Object[] { ProductUrlLoader.getInstance().getValue("mdmUrl"), ProductUrlLoader.getInstance().getValue("trackingcode") + "&did=" + SyMUtil.getDIDValue() });
                        val4 = "<span class=\"bodytextred\" >" + val4 + "</span>";
                    }
                    else if (isExport == null || isExport.equalsIgnoreCase("false")) {
                        val4 = I18N.getMsg((String)data, new Object[] { "<a href='" + ProductUrlLoader.getInstance().getValue("mdmUrl") + "/how-to/mdm-prevent-users-from-revoking-management.html?" + ProductUrlLoader.getInstance().getValue("trackingcode") + "&did=" + SyMUtil.getDIDValue() + "' target='_blank' class='blueTxt'>&nbsp; Learn more.</a>" });
                    }
                    else {
                        val4 = I18N.getMsg((String)data, new Object[] { "" });
                    }
                    final String actionStr4 = "<div class='REMARKS_" + String.valueOf(device_status) + "' >" + val4 + "</div>";
                    if (isExport == null || isExport.equalsIgnoreCase("false")) {
                        columnProperties.put("VALUE", actionStr4);
                    }
                    else {
                        columnProperties.put("VALUE", val4);
                    }
                }
            }
            if (columnalais.equalsIgnoreCase("ManagedUser.FIRST_NAME") && data != null) {
                final String firstName = DMIAMEncoder.encodeHTML((String)data);
                columnProperties.put("VALUE", firstName);
            }
            if (columnalais.equalsIgnoreCase("ManagedUser.LAST_NAME") && data != null) {
                final String lastName = DMIAMEncoder.encodeHTML((String)data);
                columnProperties.put("VALUE", lastName);
            }
            if (columnalais.equalsIgnoreCase("ManagedUser.MIDDLE_NAME") && data != null) {
                final String middleName = DMIAMEncoder.encodeHTML((String)data);
                columnProperties.put("VALUE", middleName);
            }
            if (columnalais.equalsIgnoreCase("ManagedUser.DISPLAY_NAME") && data != null) {
                final String displayName = DMIAMEncoder.encodeHTML((String)data);
                columnProperties.put("VALUE", displayName);
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
            final int authMode = json.getInt("AUTH_MODE");
            final Boolean byAdmin = json.optBoolean("BY_ADMIN", false);
            final String imei = json.optString("IMEI", (String)null);
            final String slno = json.optString("SLNO", (String)null);
            final String easID = json.optString("EASID", (String)null);
            final Boolean isExpired = json.optBoolean("isExpired", false);
            final Boolean isProvisioningUser = json.optBoolean("isDeviceProvisioningUser", false);
            final int templateType = json.optInt("TEMPLATE_TYPE", -1);
            final int ownedby = json.getInt("OWNEDBY");
            final int requestStatus = json.optInt("REQUEST_STATUS", -1);
            final int errorCode = json.optInt("ERROR_CODE", -1);
            final String udid = json.optString("UDID", (String)null);
            final boolean showRetryWakeUp = requestStatus == 0 && (managedStatus == -1 || managedStatus != 4) && (errorCode == 51201 || errorCode == 12133) && udid != null && udid.length() != 0;
            final boolean modernMgmtDeviceManagedInSoM = json.optBoolean("MODERN_MGMT_DEVICE_MANAGED_IN_SOM", false);
            final boolean isInactiveDevice = json.optBoolean("isInactive", false);
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
                        action.put("is_enabled", true);
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
                else {
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
            if (managedStatus != 4 && managedStatus != 9 && managedStatus != 10 && managedStatus != 11) {
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
