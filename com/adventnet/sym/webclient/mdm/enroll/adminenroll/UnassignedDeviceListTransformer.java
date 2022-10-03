package com.adventnet.sym.webclient.mdm.enroll.adminenroll;

import com.me.devicemanagement.framework.webclient.common.SYMClientUtil;
import com.adventnet.sym.server.mdm.util.MDMStringUtils;
import com.adventnet.i18n.I18N;
import java.util.HashMap;
import com.me.devicemanagement.framework.server.customer.CustomerInfoUtil;
import com.me.mdm.core.enrollment.EnrollmentTemplateHandler;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.me.mdm.server.factory.MDMApiFactoryProvider;
import com.adventnet.client.components.web.TransformerContext;
import com.me.devicemanagement.framework.webclient.authorization.RolecheckerTransformer;

public class UnassignedDeviceListTransformer extends RolecheckerTransformer
{
    protected boolean checkIfColumnRendererable(final TransformerContext tableContext) throws Exception {
        final String columnalias = tableContext.getPropertyName();
        final String isExport = MDMApiFactoryProvider.getMDMTableViewAPI().getIsExport(tableContext);
        final String viewname = tableContext.getViewContext().getUniqueId();
        if (columnalias.equalsIgnoreCase("DeviceForEnrollment.EAS_DEVICE_IDENTIFIER")) {
            String templateStr = tableContext.getViewContext().getRequest().getParameter("enrollmentTemplate");
            templateStr = ((templateStr == null) ? String.valueOf(-1) : templateStr);
            if ((templateStr != null && !templateStr.isEmpty() && (Integer.valueOf(templateStr) == 11 || Integer.valueOf(templateStr) == 30)) || Integer.valueOf(templateStr) == 31 || Integer.valueOf(templateStr) == 32 || Integer.valueOf(templateStr) == -1 || Integer.valueOf(templateStr) == 33) {
                final String showExchangeColumn = MDMUtil.getSyMParameter("showExchangeColumn");
                return (showExchangeColumn == null || showExchangeColumn.equalsIgnoreCase("false")) ? Boolean.FALSE : Boolean.TRUE;
            }
            return false;
        }
        else if (viewname.equalsIgnoreCase("UnassignedDeviceExportView")) {
            if (columnalias.equalsIgnoreCase("ManagedDeviceExtn.NAME")) {
                final String templateStr = tableContext.getViewContext().getRequest().getParameter("enrollmentTemplate");
                return templateStr != null && !templateStr.isEmpty() && new EnrollmentTemplateHandler().getPlatformForTemplate(Integer.parseInt(templateStr)) == 2;
            }
            return !columnalias.equals("DOMAIN_NAME") || !CustomerInfoUtil.getInstance().isMSP() || Boolean.FALSE;
        }
        else {
            if (columnalias.equals("USER_NAME")) {
                CustomerInfoUtil.getInstance();
                if (CustomerInfoUtil.isSAS()) {
                    return Boolean.FALSE;
                }
            }
            if (columnalias.equals("DOMAIN_NAME") && CustomerInfoUtil.getInstance().isMSP()) {
                return Boolean.FALSE;
            }
            if (columnalias.equals("Action") || columnalias.equals("DeviceForEnrollment.ENROLLMENT_DEVICE_ID")) {
                return isExport == null || !isExport.equalsIgnoreCase("true");
            }
            return super.checkIfColumnRendererable(tableContext);
        }
    }
    
    public void renderHeader(final TransformerContext tableContext) {
        super.renderHeader(tableContext);
        final HashMap headerProperties = tableContext.getRenderedAttributes();
        final String columnalais = tableContext.getPropertyName();
        if (columnalais.equals("DeviceForEnrollment.ENROLLMENT_DEVICE_ID")) {
            final String checkAll = "<table><tr><td nowrap><input type=\"checkbox\" id=\"selectHead\" value=\"SelectAll\" name=\"selectcheckbox\" onclick=\"javascript:checkAllDevices(this.checked)\"></td></tr></table>";
            headerProperties.put("VALUE", checkAll);
        }
    }
    
    public void renderCell(final TransformerContext tableContext) throws Exception {
        super.renderCell(tableContext);
        final String viewname = tableContext.getViewContext().getUniqueId();
        Object data = tableContext.getPropertyValue();
        final HashMap columnProperties = tableContext.getRenderedAttributes();
        final String columnalais = tableContext.getPropertyName();
        final String isExport = MDMApiFactoryProvider.getMDMTableViewAPI().getIsExport(tableContext);
        if (viewname.equalsIgnoreCase("UnassignedDeviceListView") || viewname.equalsIgnoreCase("depManagedDeviceView")) {
            final String dfeUserName = (String)tableContext.getAssociatedPropertyValue("UserResource.NAME");
            final String managedUserName = (String)tableContext.getAssociatedPropertyValue("AssignedUserResource.NAME");
            String userName = null;
            String emailAddress = null;
            if (dfeUserName == null || dfeUserName.isEmpty()) {
                if (managedUserName == null || managedUserName.isEmpty()) {
                    userName = "--";
                    emailAddress = "--";
                }
                else {
                    userName = "--";
                    emailAddress = "--";
                }
            }
            else {
                userName = dfeUserName;
                emailAddress = (String)tableContext.getAssociatedPropertyValue("ManagedUser.EMAIL_ADDRESS");
            }
            if (columnalais.equals("DeviceForEnrollment.SERIAL_NUMBER")) {
                String val = (String)data;
                if (val == null || val.equalsIgnoreCase("")) {
                    val = (String)tableContext.getAssociatedPropertyValue("MdDeviceInfo.SERIAL_NUMBER");
                }
                columnProperties.put("VALUE", val);
            }
            if (columnalais.equals("AaaUser.FIRST_NAME")) {
                String val = (String)data;
                val = ((val == null || val.equalsIgnoreCase("")) ? "--" : val);
                columnProperties.put("VALUE", val);
            }
            if (columnalais.equals("AppleDEPDeviceForEnrollment.DEVICE_MODEL")) {
                String val = String.valueOf(data);
                switch (Integer.parseInt(val)) {
                    case 1: {
                        val = I18N.getMsg("dc.mdm.actionlog.appmgmt.ipad", new Object[0]);
                        break;
                    }
                    case 2: {
                        val = I18N.getMsg("dc.mdm.actionlog.appmgmt.iphone", new Object[0]);
                        break;
                    }
                    case 3: {
                        val = I18N.getMsg("mdm.os.ipod", new Object[0]);
                        break;
                    }
                    case 4: {
                        val = I18N.getMsg("mdm.os.mac", new Object[0]);
                        break;
                    }
                    case 5: {
                        val = I18N.getMsg("mdm.os.tvos", new Object[0]);
                        break;
                    }
                }
                if (isExport != null && isExport.equalsIgnoreCase("true")) {
                    columnProperties.put("VALUE", val);
                }
                else {
                    columnProperties.put("VALUE", "<div style='padding-left:20px !important;'>" + val + "</div>");
                }
            }
            if (columnalais.equals("DeviceForEnrollment.ENROLLMENT_DEVICE_ID")) {
                final Long edid = (Long)tableContext.getAssociatedPropertyValue("DeviceForEnrollment.ENROLLMENT_DEVICE_ID");
                final String actionStr = "<table><tr><td nowrap><input type=\"checkbox\" value=\"" + edid + "\" name=\"selectDevice\" onclick=\"\"></td></tr></table>";
                columnProperties.put("VALUE", actionStr);
            }
            if (columnalais.equals("UserResource.NAME")) {
                columnProperties.put("VALUE", userName);
            }
            if (columnalais.equals("ManagedUser.EMAIL_ADDRESS")) {
                String mailAddressText = emailAddress;
                if (emailAddress.length() > 25) {
                    final Long edid2 = (Long)tableContext.getAssociatedPropertyValue("DeviceForEnrollment.ENROLLMENT_DEVICE_ID");
                    final String uniqueID = "mailAddr_" + edid2;
                    final String trimmedValue = emailAddress.substring(0, 25) + " ...";
                    mailAddressText = "<div id=\"" + uniqueID + "\"><span onmouseover=\"javascript:customShowCompleteMessage('" + uniqueID + "', '" + emailAddress + "');return false\" onmouseout=\"hideCustomMessage();return false;\">" + trimmedValue + "</span></div>";
                }
                if (isExport == null || isExport.equalsIgnoreCase("false")) {
                    columnProperties.put("VALUE", mailAddressText);
                }
                else {
                    columnProperties.put("VALUE", emailAddress);
                }
            }
            if (columnalais.equals("ManagedDeviceExtn.NAME")) {
                final String managedDeviceName = (String)data;
                final String dfeDeviceName = (String)tableContext.getAssociatedPropertyValue("DeviceEnrollmentProps.ASSIGNED_DEVICE_NAME");
                String deviceName = "--";
                if (dfeDeviceName == null || dfeDeviceName.isEmpty()) {
                    if (managedDeviceName != null && !managedDeviceName.isEmpty()) {
                        deviceName = managedDeviceName;
                    }
                }
                else {
                    deviceName = dfeDeviceName;
                }
                columnProperties.put("VALUE", deviceName);
            }
            final Long erid = (Long)tableContext.getAssociatedPropertyValue("DeviceEnrollmentRequest.ENROLLMENT_REQUEST_ID");
            final Long userId = (Long)tableContext.getAssociatedPropertyValue("ManagedUser.MANAGED_USER_ID");
            final String enrollmentTemplateStr = tableContext.getViewContext().getRequest().getParameter("enrollmentTemplate");
            Integer enrollmentTemplate = null;
            if (!MDMStringUtils.isEmpty(enrollmentTemplateStr)) {
                enrollmentTemplate = Integer.valueOf(enrollmentTemplateStr);
            }
            if (columnalais.equals("ManagedDevice.MANAGED_STATUS")) {
                final Integer deviceStatus = (Integer)tableContext.getAssociatedPropertyValue("ManagedDevice.MANAGED_STATUS");
                final Integer enrollmentRequestStatus = (Integer)tableContext.getAssociatedPropertyValue("DeviceEnrollmentRequest.REQUEST_STATUS");
                String managedstatus = null;
                if (userId == null && erid == null) {
                    managedstatus = I18N.getMsg("dc.mdm.enroll.admin.enrollment.status.awiting.both", new Object[0]);
                    managedstatus = managedstatus + "<a class='tool-tip dep-tool-tip' id='awiting_device_enrollment' href=\"#\" onmouseover=\"displayToolTip(this.id, '', 'mdm.enrollment.dep.awaitingDeviceActivation.desc')\" >\n" + "<img width=\"17\" height=\"17\" border=\"0\" align=\"absmiddle\" src=\"/images/help_small.gif\" alt=\"Help\" style=\"cursor:pointer\"/>\n" + "</a>";
                }
                else if (erid == null) {
                    managedstatus = I18N.getMsg("dc.mdm.enroll.admin_enrollment.status.awiting_device_enrollment", new Object[0]);
                    managedstatus = managedstatus + "<a class='tool-tip dep-tool-tip' id='awiting_device_enrollment' href=\"#\" onmouseover=\"displayToolTip(this.id, '', 'mdm.enrollment.dep.awaitingDeviceActivation.desc')\" >\n" + "<img width=\"17\" height=\"17\" border=\"0\" align=\"absmiddle\" src=\"/images/help_small.gif\" alt=\"Help\" style=\"cursor:pointer\"/>\n" + "</a>";
                    if (enrollmentTemplate != null && enrollmentTemplate.equals(31)) {
                        final Integer dfeStatus = (Integer)tableContext.getAssociatedPropertyValue("DeviceForEnrollment.STATUS");
                        managedstatus = I18N.getMsg(this.getDeviceForEnrollmentStatusText(dfeStatus), new Object[0]);
                    }
                }
                else if (userId == null && deviceStatus != null && (deviceStatus == 5 || deviceStatus == 6)) {
                    managedstatus = I18N.getMsg("dc.mdm.enroll.admin_enrollment.status.awiting_user_assignment", new Object[0]);
                }
                else if (enrollmentRequestStatus != null && deviceStatus == null && enrollmentRequestStatus == 1) {
                    managedstatus = I18N.getMsg("mdm.enroll.assign_in_progess", new Object[0]);
                }
                columnProperties.put("VALUE", managedstatus);
            }
            if (columnalais.equals("ManagedDevice.REMARKS")) {
                if (userId == null && erid != null) {
                    final Integer status = (Integer)tableContext.getAssociatedPropertyValue("DeviceEnrollmentRequest.REQUEST_STATUS");
                    if (status == 0) {
                        data = tableContext.getAssociatedPropertyValue("DeviceEnrollmentRequest.REMARKS");
                        SYMClientUtil.renderRemarksWithKB(tableContext, columnProperties, data, (String)null, true);
                    }
                    else if (status == 1) {
                        columnProperties.put("VALUE", "<div style=\"width:170px !important;\">" + I18N.getMsg("mdm.enroll.assign_in_progess_remarks", new Object[0]) + "</div>");
                    }
                    else {
                        final String remarks = "--";
                        columnProperties.put("VALUE", remarks);
                    }
                }
                else {
                    String remarks2 = "--";
                    if (enrollmentTemplate.equals(31) && erid == null) {
                        remarks2 = I18N.getMsg((String)tableContext.getAssociatedPropertyValue("DeviceForEnrollment.REMARKS"), new Object[0]);
                    }
                    columnProperties.put("VALUE", remarks2);
                }
            }
        }
        if (columnalais.equals("DeviceForEnrollment.IMEI")) {
            String imei = (String)tableContext.getAssociatedPropertyValue("DeviceForEnrollment.IMEI");
            if (MDMStringUtils.isEmpty(imei)) {
                imei = (String)tableContext.getAssociatedPropertyValue("MdDeviceInfo.IMEI");
                imei = (MDMStringUtils.isEmpty(imei) ? "--" : imei);
                columnProperties.put("VALUE", imei);
            }
        }
        if (columnalais.equals("USER_NAME")) {
            String name = (String)tableContext.getAssociatedPropertyValue("UserResource.NAME");
            name = ((name == null || name.equalsIgnoreCase("")) ? "--" : name);
            columnProperties.put("VALUE", name);
        }
        if (columnalais.equals("EMAIL_ADDRESS")) {
            String email = (String)tableContext.getAssociatedPropertyValue("ManagedUser.EMAIL_ADDRESS");
            email = ((email == null || email.equalsIgnoreCase("")) ? "--" : email);
            columnProperties.put("VALUE", email);
        }
    }
    
    public String getDeviceForEnrollmentStatusText(final Integer dfeStatus) {
        String statusKey = "";
        switch (dfeStatus) {
            case 0: {
                statusKey = "dc.common.SUCCESS";
                break;
            }
            case 1:
            case 2:
            case 301:
            case 302: {
                statusKey = "dc.common.status.in_progress";
                break;
            }
            case 9:
            case 10: {
                statusKey = "dc.db.config.status.failed";
                break;
            }
            default: {
                statusKey = "dc.db.config.status.failed";
                break;
            }
        }
        return statusKey;
    }
}
