package com.adventnet.sym.webclient.mdm.enroll.adminenroll;

import java.util.HashMap;
import com.adventnet.i18n.I18N;
import org.json.JSONObject;
import com.adventnet.sym.server.mdm.util.MDMStringUtils;
import com.me.devicemanagement.framework.server.customer.CustomerInfoUtil;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.me.mdm.server.factory.MDMApiFactoryProvider;
import com.adventnet.client.components.web.TransformerContext;
import com.me.devicemanagement.framework.webclient.authorization.RolecheckerTransformer;

public class UnassignedDevicesTransformer extends RolecheckerTransformer
{
    protected boolean checkIfColumnRendererable(final TransformerContext tableContext) throws Exception {
        final String columnalias = tableContext.getPropertyName();
        final String isExport = MDMApiFactoryProvider.getMDMTableViewAPI().getIsExport(tableContext);
        final String viewname = tableContext.getViewContext().getUniqueId();
        if (columnalias.equalsIgnoreCase("DeviceForEnrollment.EAS_DEVICE_IDENTIFIER")) {
            String templateStr = tableContext.getViewContext().getRequest().getParameter("enrollmentTemplate");
            templateStr = ((templateStr == null) ? String.valueOf(-1) : templateStr);
            if ((templateStr != null && !templateStr.isEmpty() && (Integer.valueOf(templateStr) == 11 || Integer.valueOf(templateStr) == 30)) || Integer.valueOf(templateStr) == 31 || Integer.valueOf(templateStr) == 32 || Integer.valueOf(templateStr) == 33 || Integer.valueOf(templateStr) == -1) {
                final String showExchangeColumn = MDMUtil.getSyMParameter("showExchangeColumn");
                return (showExchangeColumn == null || showExchangeColumn.equalsIgnoreCase("false")) ? Boolean.FALSE : Boolean.TRUE;
            }
            return false;
        }
        else {
            if (viewname.equalsIgnoreCase("UnassignedDeviceExportView")) {
                return !columnalias.equals("DOMAIN_NAME") || !CustomerInfoUtil.getInstance().isMSP() || Boolean.FALSE;
            }
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
    }
    
    public void renderCell(final TransformerContext tableContext) throws Exception {
        super.renderCell(tableContext);
        final String viewname = tableContext.getViewContext().getUniqueId();
        final Object data = tableContext.getPropertyValue();
        final HashMap columnProperties = tableContext.getRenderedAttributes();
        final String columnalais = tableContext.getPropertyName();
        final int reportType = tableContext.getViewContext().getRenderType();
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
        final JSONObject payload = new JSONObject();
        if (columnalais.equals("ManagedDevice.MANAGED_STATUS")) {
            final Integer deviceStatus = (Integer)tableContext.getAssociatedPropertyValue("ManagedDevice.MANAGED_STATUS");
            final Integer enrollmentRequestStatus = (Integer)tableContext.getAssociatedPropertyValue("DeviceEnrollmentRequest.REQUEST_STATUS");
            String managedstatus = null;
            String textClass = null;
            if (userId == null && erid == null) {
                textClass = "";
                managedstatus = I18N.getMsg("dc.mdm.enroll.admin.enrollment.status.awiting.both", new Object[0]);
                payload.put("tooltipMessage", (Object)I18N.getMsg("mdm.enrollment.dep.awaitingDeviceActivation.desc", new Object[0]));
            }
            else if (erid == null) {
                textClass = "ucs-table-status-text__in-progress";
                managedstatus = I18N.getMsg("dc.mdm.enroll.admin_enrollment.status.awiting_device_enrollment", new Object[0]);
                payload.put("tooltipMessage", (Object)I18N.getMsg("mdm.enrollment.dep.awaitingDeviceActivation.desc", new Object[0]));
                if (enrollmentTemplate != null && enrollmentTemplate.equals(31)) {
                    final Integer dfeStatus = (Integer)tableContext.getAssociatedPropertyValue("DeviceForEnrollment.STATUS");
                    final JSONObject windowsStatus = this.getDeviceForEnrollmentStatusCompliantWithWebclient(dfeStatus);
                    managedstatus = I18N.getMsg(windowsStatus.getString("statusKey"), new Object[0]);
                    textClass = windowsStatus.getString("textClass");
                }
            }
            else if (userId == null && deviceStatus != null && (deviceStatus == 5 || deviceStatus == 6)) {
                textClass = "ucs-table-status-text__in-progress";
                managedstatus = I18N.getMsg("dc.mdm.enroll.admin_enrollment.status.awiting_user_assignment", new Object[0]);
            }
            else if (enrollmentRequestStatus != null && deviceStatus == null && enrollmentRequestStatus == 1) {
                textClass = "ucs-table-status-text__in-progress";
                managedstatus = I18N.getMsg("mdm.enroll.assign_in_progess", new Object[0]);
            }
            payload.put("messageData", (Object)managedstatus);
            payload.put("textClass", (Object)textClass);
            if (reportType != 4) {
                columnProperties.put("VALUE", managedstatus);
            }
            else {
                columnProperties.put("PAYLOAD", payload);
            }
        }
        if (columnalais.equals("DeviceForEnrollment.SERIAL_NUMBER")) {
            String val = (String)data;
            if (MDMUtil.getInstance().isEmpty(val)) {
                val = (String)tableContext.getAssociatedPropertyValue("MdDeviceInfo.SERIAL_NUMBER");
            }
            columnProperties.put("VALUE", val);
        }
        if (columnalais.equals("ManagedDevice.REMARKS")) {
            final JSONObject remarks = new JSONObject();
            String remarksData = "--";
            if (userId == null && erid != null) {
                final Integer status = (Integer)tableContext.getAssociatedPropertyValue("DeviceEnrollmentRequest.REQUEST_STATUS");
                if (status == 0) {
                    remarksData = (String)tableContext.getAssociatedPropertyValue("DeviceEnrollmentRequest.REMARKS");
                    remarks.put("data", (Object)I18N.getMsg(remarksData, new Object[0]));
                    remarks.put("readKB", (Object)tableContext.getAssociatedPropertyValue("ErrorCodeToKBUrl.KB_URL"));
                }
                else if (status == 1) {
                    remarksData = I18N.getMsg("mdm.enroll.assign_in_progess_remarks", new Object[0]);
                    remarks.put("data", (Object)remarksData);
                }
                else {
                    remarks.put("data", (Object)"--");
                }
            }
            else {
                if (enrollmentTemplate.equals(31) && erid == null) {
                    remarksData = I18N.getMsg((String)tableContext.getAssociatedPropertyValue("DeviceForEnrollment.REMARKS"), new Object[0]);
                }
                remarks.put("data", (Object)remarksData);
            }
            if (reportType != 4) {
                columnProperties.put("VALUE", remarksData);
            }
            else {
                columnProperties.put("PAYLOAD", remarks);
            }
        }
        if (columnalais.equals("AaaUser.FIRST_NAME")) {
            String val = (String)data;
            val = ((val == null || val.equalsIgnoreCase("")) ? "--" : val);
            columnProperties.put("VALUE", val);
        }
        if (columnalais.equals("DeviceForEnrollment.IMEI")) {
            String imei = (String)tableContext.getAssociatedPropertyValue("DeviceForEnrollment.IMEI");
            if (MDMStringUtils.isEmpty(imei)) {
                imei = (String)tableContext.getAssociatedPropertyValue("MdDeviceInfo.IMEI");
                imei = (MDMStringUtils.isEmpty(imei) ? "--" : imei);
                columnProperties.put("VALUE", imei);
            }
        }
        if (columnalais.equals("UserResource.NAME")) {
            String name = (String)tableContext.getAssociatedPropertyValue("UserResource.NAME");
            name = ((name == null || name.equalsIgnoreCase("")) ? "--" : name);
            columnProperties.put("VALUE", name);
        }
        if (columnalais.equals("ManagedUser.EMAIL_ADDRESS")) {
            String email = (String)tableContext.getAssociatedPropertyValue("ManagedUser.EMAIL_ADDRESS");
            email = ((email == null || email.equalsIgnoreCase("")) ? "--" : email);
            columnProperties.put("VALUE", email);
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
            columnProperties.put("VALUE", val);
        }
    }
    
    public JSONObject getDeviceForEnrollmentStatusCompliantWithWebclient(final Integer dfeStatus) {
        String statusKey = "";
        final JSONObject jsonHeaders = new JSONObject();
        String textClass = "";
        switch (dfeStatus) {
            case 0: {
                statusKey = "dc.common.SUCCESS";
                textClass = "ucs-table-status-text__success";
                break;
            }
            case 1:
            case 2:
            case 301:
            case 302: {
                statusKey = "dc.common.status.in_progress";
                textClass = "ucs-table-status-text__in-progress";
                break;
            }
            case 9:
            case 10: {
                statusKey = "dc.db.config.status.failed";
                textClass = "ucs-table-status-text__failed";
                break;
            }
            default: {
                statusKey = "dc.db.config.status.failed";
                textClass = "ucs-table-status-text__failed";
                break;
            }
        }
        jsonHeaders.put("statusKey", (Object)statusKey);
        jsonHeaders.put("textClass", (Object)textClass);
        return jsonHeaders;
    }
}
