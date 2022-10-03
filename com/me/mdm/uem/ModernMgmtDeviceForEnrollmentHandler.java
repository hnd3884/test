package com.me.mdm.uem;

import java.util.Iterator;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import org.json.JSONException;
import com.adventnet.persistence.DataAccessException;
import com.me.devicemanagement.framework.server.util.DBUtil;
import com.me.devicemanagement.framework.server.csv.CustomerParamsHandler;
import com.me.mdm.core.enrollment.EnrollmentTemplateHandler;
import com.adventnet.sym.server.mdm.core.ManagedUserHandler;
import com.adventnet.persistence.DataObject;
import java.util.List;
import com.adventnet.persistence.DataAccess;
import java.util.ArrayList;
import com.me.mdm.uem.actionconstants.DeviceAction;
import com.me.mdm.server.factory.MDMApiFactoryProvider;
import com.adventnet.sym.server.mdm.enroll.MDMEnrollmentConstants;
import com.adventnet.persistence.Row;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.me.devicemanagement.framework.server.exception.SyMException;
import com.adventnet.sym.server.mdm.util.MDMStringUtils;
import java.util.HashMap;
import java.util.logging.Level;
import com.me.mdm.core.enrollment.settings.UserAssignmentRuleHandler;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.me.mdm.server.util.MDMFeatureParamsHandler;
import org.json.JSONObject;
import java.util.Set;
import org.json.JSONArray;
import com.me.mdm.core.enrollment.DeviceForEnrollmentHandler;

public class ModernMgmtDeviceForEnrollmentHandler extends DeviceForEnrollmentHandler
{
    public JSONObject processWindowsModernMgmtDeviceList(final JSONArray deviceArray, final Set<String> serialNumberList, final Set<String> udidList, final Set<String> genericList) throws Exception {
        final JSONObject apiResponse = new JSONObject();
        this.deviceForEnrollmentChildTableName = "WindowsLaptopDeviceForEnrollment";
        if (genericList.size() > 0 && MDMFeatureParamsHandler.getInstance().isFeatureEnabled("EnableModernWindowsEnrollment")) {
            this.deviceForEnrollmentChildTableName = "WinModernMgmtDeviceForEnrollment";
        }
        (this.existingDO = this.getExistingDO(serialNumberList, "SERIAL_NUMBER", null)).merge(this.getExistingDO(udidList, "UDID", null));
        if (genericList.size() > 0) {
            this.existingDO.merge(this.getExistingDO(genericList, "GENERIC_IDENTIFIER", null));
        }
        this.existingDO.merge(this.getExistingManagedDeviceDOUsingSerialNumberOrUDID(serialNumberList, "SERIAL_NUMBER"));
        this.existingDO.merge(this.getExistingManagedDeviceDOUsingSerialNumberOrUDID(udidList, "UDID"));
        if (genericList.size() > 0) {
            this.existingDO.merge(this.getExistingManagedDeviceDOUsingSerialNumberOrUDID(genericList, "GENERIC_IDENTIFIER"));
        }
        final JSONObject response = this.addModernMgmtDeviceForEnrollment(deviceArray);
        final JSONArray assignUserArray = response.getJSONArray("assignUserArray");
        MDMUtil.getPersistence().update(this.existingDO);
        final JSONArray alreadyEnrolledRes = response.getJSONArray("alreadyEnrolledRes");
        final JSONObject groupDeviceMapping = new UserAssignmentRuleHandler().getAgentParamsForEnrolledDevices(alreadyEnrolledRes);
        apiResponse.put("group_ro_details", (Object)groupDeviceMapping);
        try {
            new UserAssignmentRuleHandler().applyAssignUserRules(assignUserArray, 33);
        }
        catch (final Exception e) {
            ModernMgmtDeviceForEnrollmentHandler.logger.log(Level.WARNING, "Auto user assignment threw an exception  for windows modern mgmt devices: ", e);
        }
        return apiResponse;
    }
    
    public JSONObject processMacModernMgmtDeviceForEnrollmentList(final JSONArray deviceArray, final Set<String> serialNumberList, final Set<String> udidList, final Set<String> genericList) throws Exception {
        final JSONObject apiResponse = new JSONObject();
        this.deviceForEnrollmentChildTableName = "AppleDEPDeviceForEnrollment";
        this.existingDO = this.getExistingDO(serialNumberList, "SERIAL_NUMBER", null);
        this.deviceForEnrollmentChildTableName = "MacModernMgmtDeviceForEnrollment";
        this.existingDO.merge(this.getExistingDO(serialNumberList, "SERIAL_NUMBER", null));
        this.existingDO.merge(this.getExistingManagedDeviceDOUsingSerialNumberOrUDID(serialNumberList, "SERIAL_NUMBER"));
        if (genericList.size() > 0) {
            this.existingDO.merge(this.getExistingManagedDeviceDOUsingSerialNumberOrUDID(genericList, "GENERIC_IDENTIFIER"));
        }
        final JSONObject response = this.addModernMgmtDeviceForEnrollment(deviceArray);
        final JSONArray assignUserArray = response.getJSONArray("assignUserArray");
        MDMUtil.getPersistence().update(this.existingDO);
        final JSONArray alreadyEnrolledRes = response.getJSONArray("alreadyEnrolledRes");
        this.deleteDEPCommonDevicesFromMacModernMgmtDO();
        MDMUtil.getPersistence().update(this.existingDO);
        final JSONObject groupDeviceMapping = new UserAssignmentRuleHandler().getAgentParamsForEnrolledDevices(alreadyEnrolledRes);
        apiResponse.put("group_ro_details", (Object)groupDeviceMapping);
        try {
            new UserAssignmentRuleHandler().applyAssignUserRules(assignUserArray, 12);
        }
        catch (final Exception e) {
            ModernMgmtDeviceForEnrollmentHandler.logger.log(Level.WARNING, "Auto user assignment threw an exception for mac devices : ", e);
        }
        return apiResponse;
    }
    
    private JSONObject addModernMgmtDeviceForEnrollment(final JSONArray deviceArray) throws Exception {
        ModernMgmtDeviceForEnrollmentHandler.logger.log(Level.INFO, "ModernMgmtDeviceForEnrollmentHandler: Going to stage {0} device for enrollment in MDM", new Object[] { deviceArray.length() });
        final JSONArray serialNumberListOfDevicesAlreadyManaged = new JSONArray();
        final JSONArray deviceUpdateDetailsOfDevicesAlreadyManaged = new JSONArray();
        final JSONArray mdmResIDofAlreadyEnrolledDevices = new JSONArray();
        final JSONObject response = new JSONObject();
        final JSONArray assignUserArray = new JSONArray();
        final HashMap customerToAdminMap = new HashMap();
        for (int index = 0; index < deviceArray.length(); ++index) {
            final JSONObject deviceJSON = deviceArray.getJSONObject(index);
            final Long customerID = deviceJSON.getLong("customer_id");
            String serialNumber = deviceJSON.getJSONObject("device_unique_props").optString("serial_number", (String)null);
            String udid = deviceJSON.getJSONObject("device_unique_props").optString("udid", (String)null);
            String genericID = deviceJSON.getJSONObject("device_unique_props").optString("generic_id", (String)null);
            final String deviceName = deviceJSON.optString("device_name", (String)null);
            final Boolean forceRegenerateOTP = deviceJSON.optBoolean("force_otp_regeneration", (boolean)Boolean.FALSE);
            ModernMgmtDeviceForEnrollmentHandler.logger.log(Level.INFO, "ModernMgmtDeviceForEnrollmentHandler: DATA-IN: Customer Id - {0}, Serial Number - {1}, Udid - {2}, Generic Id - {3}, Device Name - {5}, Force Regenerate OTP - {6}", new Object[] { customerID, serialNumber, udid, genericID, deviceName, forceRegenerateOTP });
            final JSONObject tokens = deviceJSON.optJSONObject("device_tokens");
            String otp = null;
            if (tokens != null) {
                otp = tokens.optString("otp");
            }
            if (MDMStringUtils.isEmpty(serialNumber) && MDMStringUtils.isEmpty(udid) && MDMStringUtils.isEmpty(genericID)) {
                throw new SyMException(14020, "Either of IMEI or Serial number must be specified", "dc.mdm.msg.inv.bulk_edit.no_imei_slno", new Throwable());
            }
            final Row managedDeviceSerialNumberRow = this.existingDO.getRow("MdDeviceInfo", new Criteria(Column.getColumn("MdDeviceInfo", "SERIAL_NUMBER"), (Object)serialNumber, 0, (boolean)Boolean.FALSE));
            Row managedDeviceUDIDRow = this.existingDO.getRow("ManagedDevice", new Criteria(Column.getColumn("ManagedDevice", "UDID"), (Object)udid, 0, (boolean)Boolean.FALSE));
            Row managedDeviceGenRow = this.existingDO.getRow("ManagedDeviceExtn", new Criteria(Column.getColumn("ManagedDeviceExtn", "GENERIC_IDENTIFIER"), (Object)genericID, 0, (boolean)Boolean.FALSE));
            if (managedDeviceSerialNumberRow == null && managedDeviceUDIDRow == null && managedDeviceGenRow == null) {
                ModernMgmtDeviceForEnrollmentHandler.logger.log(Level.INFO, "ModernMgmtDeviceForEnrollmentHandler: Device is not already managed in MDM. Adding the Device for enrollment. Serial - {0} | Udid - {1} | Generic Id - {2}", new Object[] { serialNumber, udid, genericID });
                Criteria devForEnrollmentCriteria = null;
                if (!MDMStringUtils.isEmpty(serialNumber) && !MDMStringUtils.isEmpty(genericID) && !MDMStringUtils.isEmpty(otp)) {
                    if (this.deviceForEnrollmentChildTableName.equals("MacModernMgmtDeviceForEnrollment")) {
                        ModernMgmtDeviceForEnrollmentHandler.logger.log(Level.INFO, "ModernMgmtDeviceForEnrollmentHandler: Adding device based on serial number for enrollment: {0}", new Object[] { serialNumber });
                        this.addModernMgmtDeviceForEnrollment("SERIAL_NUMBER", serialNumber, deviceJSON);
                        devForEnrollmentCriteria = new Criteria(Column.getColumn("DeviceForEnrollment", "SERIAL_NUMBER"), (Object)serialNumber, 0);
                    }
                    else {
                        ModernMgmtDeviceForEnrollmentHandler.logger.log(Level.INFO, "ModernMgmtDeviceForEnrollmentHandler: Adding device based on generic id for enrollment: {0}", new Object[] { genericID });
                        this.addModernMgmtDeviceForEnrollment("GENERIC_IDENTIFIER", genericID, deviceJSON);
                        devForEnrollmentCriteria = new Criteria(Column.getColumn("DeviceForEnrollment", "GENERIC_IDENTIFIER"), (Object)genericID, 0);
                    }
                }
                else if (!MDMStringUtils.isEmpty(serialNumber)) {
                    ModernMgmtDeviceForEnrollmentHandler.logger.log(Level.INFO, "ModernMgmtDeviceForEnrollmentHandler: Adding device based on sr number for enrollment: {0}", new Object[] { serialNumber });
                    this.addModernMgmtDeviceForEnrollment("SERIAL_NUMBER", serialNumber, deviceJSON);
                    devForEnrollmentCriteria = new Criteria(Column.getColumn("DeviceForEnrollment", "SERIAL_NUMBER"), (Object)serialNumber, 0);
                }
                else if (!MDMStringUtils.isEmpty(udid)) {
                    ModernMgmtDeviceForEnrollmentHandler.logger.log(Level.INFO, "ModernMgmtDeviceForEnrollmentHandler: Adding device based on udid for enrollment: {0}", new Object[] { udid });
                    this.addModernMgmtDeviceForEnrollment("UDID", udid, deviceJSON);
                    devForEnrollmentCriteria = new Criteria(Column.getColumn("DeviceForEnrollment", "UDID"), (Object)udid, 0);
                }
                final Object devForEnrollmentDOValObject = this.existingDO.getRow("DeviceForEnrollment", devForEnrollmentCriteria).get("ENROLLMENT_DEVICE_ID");
                Row deviceForEnrollmentChildTableRow = null;
                Row enrollmentPropsTableRow = null;
                Long deviceForEnrollmentID = null;
                if (devForEnrollmentDOValObject instanceof Long) {
                    deviceForEnrollmentID = (Long)devForEnrollmentDOValObject;
                    final Criteria deviceForEnrollmentChildTableCriteria = new Criteria(Column.getColumn(this.deviceForEnrollmentChildTableName, "ENROLLMENT_DEVICE_ID"), (Object)deviceForEnrollmentID, 0);
                    deviceForEnrollmentChildTableRow = this.existingDO.getRow(this.deviceForEnrollmentChildTableName, deviceForEnrollmentChildTableCriteria);
                    final Criteria enrollmentPropsTableCriteria = new Criteria(Column.getColumn("DeviceEnrollmentProps", "ENROLLMENT_DEVICE_ID"), (Object)deviceForEnrollmentID, 0);
                    enrollmentPropsTableRow = this.existingDO.getRow("DeviceEnrollmentProps", enrollmentPropsTableCriteria);
                }
                if (deviceForEnrollmentChildTableRow == null || (!(devForEnrollmentDOValObject instanceof Long) && devForEnrollmentDOValObject != null)) {
                    deviceForEnrollmentChildTableRow = new Row(this.deviceForEnrollmentChildTableName);
                    deviceForEnrollmentChildTableRow.set("ENROLLMENT_DEVICE_ID", devForEnrollmentDOValObject);
                    this.existingDO.addRow(deviceForEnrollmentChildTableRow);
                }
                if ((enrollmentPropsTableRow == null || (!(devForEnrollmentDOValObject instanceof Long) && devForEnrollmentDOValObject != null)) && !MDMUtil.isStringEmpty(deviceName)) {
                    enrollmentPropsTableRow = new Row("DeviceEnrollmentProps");
                    enrollmentPropsTableRow.set("ENROLLMENT_DEVICE_ID", devForEnrollmentDOValObject);
                    enrollmentPropsTableRow.set("ASSIGNED_DEVICE_NAME", (Object)deviceName);
                    this.existingDO.addRow(enrollmentPropsTableRow);
                }
                if (!MDMStringUtils.isEmpty(otp) && ((!(devForEnrollmentDOValObject instanceof Long) && devForEnrollmentDOValObject != null) || forceRegenerateOTP) && (this.deviceForEnrollmentChildTableName.equals("MacModernMgmtDeviceForEnrollment") || this.deviceForEnrollmentChildTableName.equals("WinModernMgmtDeviceForEnrollment"))) {
                    ModernMgmtDeviceForEnrollmentHandler.logger.log(Level.INFO, "Device is posted with OTP, deviceDetails - slno : {0}, genericid : {1} ", new Object[] { serialNumber, genericID });
                    this.createEnrollRequestWithOTPForModernMgmt(otp, customerToAdminMap, customerID, devForEnrollmentDOValObject);
                }
                final JSONObject resourceJSON = new JSONObject();
                resourceJSON.put("SerialNumber", (Object)serialNumber);
                resourceJSON.put("UDID", (Object)udid);
                resourceJSON.put("customer_id", (Object)customerID);
                this.addModelTypeToDataJSON(resourceJSON, MDMEnrollmentConstants.UserAssignmentRules.DeviceModelRules.MODERN_DEVICE);
                assignUserArray.put((Object)resourceJSON);
            }
            else {
                ModernMgmtDeviceForEnrollmentHandler.logger.log(Level.INFO, "ModernMgmtDeviceForEnrollmentHandler: Device is already managed in MDM. Updating device details: Serial - {0} | Udid - {1} | Generic Id - {2}", new Object[] { serialNumber, udid, genericID });
                Long deviceId = null;
                if (managedDeviceSerialNumberRow != null) {
                    serialNumber = (String)managedDeviceSerialNumberRow.get("SERIAL_NUMBER");
                    deviceId = (Long)managedDeviceSerialNumberRow.get("RESOURCE_ID");
                }
                if (managedDeviceGenRow != null) {
                    genericID = (String)managedDeviceGenRow.get("GENERIC_IDENTIFIER");
                }
                managedDeviceUDIDRow = this.existingDO.getRow("ManagedDevice", new Criteria(Column.getColumn("ManagedDevice", "RESOURCE_ID"), (Object)deviceId, 0));
                if (managedDeviceUDIDRow != null) {
                    udid = (String)managedDeviceUDIDRow.get("UDID");
                }
                mdmResIDofAlreadyEnrolledDevices.put((Object)deviceId);
                serialNumberListOfDevicesAlreadyManaged.put((Object)serialNumber);
                final JSONObject deviceUpdateDetailsJSON = new JSONObject();
                deviceUpdateDetailsJSON.put("RESOURCE_ID", (Object)deviceId);
                deviceUpdateDetailsJSON.put("UDID", (Object)udid);
                deviceUpdateDetailsJSON.put("SERIAL_NUMBER", (Object)serialNumber);
                ModernMgmtDeviceForEnrollmentHandler.logger.log(Level.INFO, "ModernMgmtDeviceForEnrollmentHandler: Adding modern details for device: {0}, Udid - {1}, Serial number: {3}", new Object[] { deviceId, udid, serialNumber });
                addModernDetails(deviceUpdateDetailsJSON, deviceId);
                if (!MDMStringUtils.isEmpty(genericID)) {
                    ModernMgmtDeviceForEnrollmentHandler.logger.log(Level.INFO, "ModernMgmtDeviceForEnrollmentHandler: Generic Id is present for resource: {0}, Generic Id: {1}", new Object[] { deviceId, genericID });
                    deviceUpdateDetailsJSON.put("GENERIC_IDENTIFIER", (Object)genericID);
                    if (managedDeviceGenRow == null) {
                        managedDeviceGenRow = this.existingDO.getRow("ManagedDeviceExtn", new Criteria(Column.getColumn("ManagedDeviceExtn", "MANAGED_DEVICE_ID"), (Object)deviceId, 0));
                        managedDeviceGenRow.set("GENERIC_IDENTIFIER", (Object)genericID);
                        this.existingDO.updateRow(managedDeviceGenRow);
                    }
                }
                deviceUpdateDetailsOfDevicesAlreadyManaged.put((Object)deviceUpdateDetailsJSON);
            }
        }
        if (serialNumberListOfDevicesAlreadyManaged.length() > 0) {
            ModernMgmtDeviceForEnrollmentHandler.logger.log(Level.INFO, "ModernMgmtDeviceForEnrollmentHandler: Going to post the data to DC for updating Computer to device mapping table: No of devices: {0}", new Object[] { serialNumberListOfDevicesAlreadyManaged });
            final JSONObject deviceDetails = new JSONObject();
            deviceDetails.put("SerialNumberList", (Object)serialNumberListOfDevicesAlreadyManaged);
            deviceDetails.put("DeviceUpdateDetails", (Object)deviceUpdateDetailsOfDevicesAlreadyManaged);
            ModernMgmtDeviceForEnrollmentHandler.logger.log(Level.FINE, "Data Being posted to Legacy management for during modern first flow is is {0}", deviceDetails);
            MDMApiFactoryProvider.getMDMModernMgmtAPI().deviceListener(DeviceAction.ADDORUPDATE_MAPPINGTABLE, new JSONObject().put("DeviceDetails", (Object)deviceDetails));
            ModernMgmtDeviceForEnrollmentHandler.logger.log(Level.INFO, "ModernMgmtDeviceForEnrollmentHandler: Successfully posted details to DC for updating Computer to device mapping table: No of devices: {0}", new Object[] { serialNumberListOfDevicesAlreadyManaged });
            final List managedDevIds = new ArrayList();
            for (int index2 = 0; index2 < deviceUpdateDetailsOfDevicesAlreadyManaged.length(); ++index2) {
                final JSONObject deviceDetail = deviceUpdateDetailsOfDevicesAlreadyManaged.getJSONObject(index2);
                final long managedDeviceId = deviceDetail.getLong("RESOURCE_ID");
                managedDevIds.add(managedDeviceId);
            }
            final DataObject dataObject = MDMUtil.getPersistenceLite().get("ManagedDeviceExtn", new Criteria(Column.getColumn("ManagedDeviceExtn", "MANAGED_DEVICE_ID"), (Object)managedDevIds.toArray(), 8));
            for (int index3 = 0; index3 < deviceUpdateDetailsOfDevicesAlreadyManaged.length(); ++index3) {
                final JSONObject deviceDetail2 = deviceUpdateDetailsOfDevicesAlreadyManaged.getJSONObject(index3);
                final String genericID2 = deviceDetail2.optString("GENERIC_IDENTIFIER");
                final long managedDeviceId = deviceDetail2.getLong("RESOURCE_ID");
                if (!MDMStringUtils.isEmpty(genericID2)) {
                    final Row row = dataObject.getRow("ManagedDeviceExtn", new Criteria(Column.getColumn("ManagedDeviceExtn", "MANAGED_DEVICE_ID"), (Object)managedDeviceId, 0));
                    row.set("GENERIC_IDENTIFIER", (Object)genericID2);
                    dataObject.updateRow(row);
                }
            }
            DataAccess.update(dataObject);
        }
        response.put("assignUserArray", (Object)assignUserArray);
        response.put("alreadyEnrolledRes", (Object)mdmResIDofAlreadyEnrolledDevices);
        return response;
    }
    
    private void createEnrollRequestWithOTPForModernMgmt(final String otp, final HashMap customerToAdminMap, final Long customerID, final Object devForEnrollmentDOValObject) throws Exception {
        JSONObject userJSON = customerToAdminMap.get(customerID);
        if (userJSON == null) {
            userJSON = ManagedUserHandler.getInstance().getManagedUserIdAndAAAUserIdForAdmin(customerID, Boolean.TRUE);
            final Long userID = (Long)userJSON.get("USER_ID");
            Long templateID = null;
            if (this.deviceForEnrollmentChildTableName.equalsIgnoreCase("WinModernMgmtDeviceForEnrollment")) {
                templateID = EnrollmentTemplateHandler.getModenWindowsEnrollmentTemplateDetailsForCustomer(customerID, userID).getLong("TEMPLATE_ID");
            }
            else {
                templateID = EnrollmentTemplateHandler.getModenMacMgmtEnrollmentTemplateDetailsForCustomer(customerID, userID).getLong("TEMPLATE_ID");
            }
            userJSON.put("TEMPLATE_ID", (Object)templateID);
            customerToAdminMap.put(customerID, userJSON);
        }
        final Long now = System.currentTimeMillis();
        final Row enrollmentRequestRow = new Row("DeviceEnrollmentRequest");
        enrollmentRequestRow.set("ENROLLMENT_TYPE", (Object)4);
        enrollmentRequestRow.set("PLATFORM_TYPE", (Object)(this.deviceForEnrollmentChildTableName.equalsIgnoreCase("WinModernMgmtDeviceForEnrollment") ? 3 : 1));
        enrollmentRequestRow.set("AUTH_MODE", (Object)1);
        enrollmentRequestRow.set("IS_SELF_ENROLLMENT", (Object)Boolean.FALSE);
        enrollmentRequestRow.set("MANAGED_USER_ID", userJSON.get("MANAGED_USER_ID"));
        enrollmentRequestRow.set("REQUESTED_TIME", (Object)now);
        enrollmentRequestRow.set("OWNED_BY", (Object)1);
        enrollmentRequestRow.set("REQUEST_STATUS", (Object)1);
        enrollmentRequestRow.set("REMARKS", (Object)"dc.mdm.enroll.request_created");
        final String otpExpiryPeriod = CustomerParamsHandler.getInstance().getParameterValue("UEM_OTP_EXPIRY", (long)customerID);
        Integer otpExpiry = 21;
        if (!MDMStringUtils.isEmpty(otpExpiryPeriod)) {
            otpExpiry = Integer.parseInt(otpExpiryPeriod);
        }
        final Row otpPassword = new Row("OTPPassword");
        otpPassword.set("ENROLLMENT_REQUEST_ID", enrollmentRequestRow.get("ENROLLMENT_REQUEST_ID"));
        otpPassword.set("OTP_PASSWORD", (Object)otp);
        otpPassword.set("EXPIRE_TIME", (Object)(now + otpExpiry * 24 * 1000 * 60 * 60));
        otpPassword.set("GENERATED_TIME", (Object)now);
        otpPassword.set("FAILED_ATTEMPTS", (Object)0);
        Boolean isUpdate = Boolean.FALSE;
        Row requestToDFE = null;
        if (devForEnrollmentDOValObject instanceof Long) {
            requestToDFE = DBUtil.getRowFromDB("DeviceEnrollmentToRequest", "ENROLLMENT_DEVICE_ID", devForEnrollmentDOValObject);
            if (requestToDFE != null) {
                isUpdate = Boolean.TRUE;
            }
        }
        if (!isUpdate) {
            requestToDFE = new Row("DeviceEnrollmentToRequest");
        }
        requestToDFE.set("ENROLLMENT_REQUEST_ID", enrollmentRequestRow.get("ENROLLMENT_REQUEST_ID"));
        requestToDFE.set("ENROLLMENT_DEVICE_ID", devForEnrollmentDOValObject);
        final Row requestToTemplate = new Row("EnrollmentTemplateToRequest");
        requestToTemplate.set("ENROLLMENT_REQUEST_ID", enrollmentRequestRow.get("ENROLLMENT_REQUEST_ID"));
        requestToTemplate.set("TEMPLATE_ID", userJSON.get("TEMPLATE_ID"));
        this.existingDO.addRow(enrollmentRequestRow);
        this.existingDO.addRow(otpPassword);
        if (!isUpdate) {
            this.existingDO.addRow(requestToDFE);
        }
        else {
            this.existingDO.updateRow(requestToDFE);
        }
        this.existingDO.addRow(requestToTemplate);
        ModernMgmtDeviceForEnrollmentHandler.logger.log(Level.INFO, "Request is added for the given device under the admin user {0}", userJSON);
    }
    
    private void addModernMgmtDeviceForEnrollment(final String primaryKeyColumnName, final String primaryKeyColumnValue, final JSONObject deviceDetails) throws DataAccessException, JSONException {
        final JSONObject deviceUniqueProps = deviceDetails.getJSONObject("device_unique_props");
        final String serialNumber = deviceUniqueProps.optString("serial_number", (String)null);
        final String udid = deviceUniqueProps.optString("udid", (String)null);
        String imei = deviceUniqueProps.optString("imei", (String)null);
        final String genericID = deviceUniqueProps.optString("generic_id", (String)null);
        final Long customerID = deviceDetails.getLong("customer_id");
        final Criteria uniqueCriteria = new Criteria(Column.getColumn("DeviceForEnrollment", primaryKeyColumnName), (Object)primaryKeyColumnValue, 0);
        Row devForEnrollment = this.existingDO.getRow("DeviceForEnrollment", uniqueCriteria);
        if (devForEnrollment == null) {
            devForEnrollment = new Row("DeviceForEnrollment");
            devForEnrollment.set("CUSTOMER_ID", (Object)customerID);
            devForEnrollment.set("ADDED_TIME", (Object)MDMUtil.getCurrentTime());
        }
        if (!MDMStringUtils.isEmpty(serialNumber)) {
            devForEnrollment.set("SERIAL_NUMBER", (Object)serialNumber);
        }
        if (!MDMStringUtils.isEmpty(udid)) {
            devForEnrollment.set("UDID", (Object)udid);
        }
        if (!MDMStringUtils.isEmpty(imei)) {
            imei = imei.replace(" ", "");
            devForEnrollment.set("IMEI", (Object)imei);
        }
        devForEnrollment.set("UPDATED_TIME", (Object)MDMUtil.getCurrentTime());
        devForEnrollment.set("STATUS", (Object)1);
        devForEnrollment.set("REMARKS", (Object)"mdm.enroll.remarks.awaiting_user_assignment");
        if (!MDMStringUtils.isEmpty(genericID)) {
            devForEnrollment.set("GENERIC_IDENTIFIER", (Object)genericID);
        }
        if (this.existingDO.getRow("DeviceForEnrollment", uniqueCriteria) == null) {
            this.existingDO.addRow(devForEnrollment);
        }
        else {
            this.existingDO.updateRow(devForEnrollment);
        }
    }
    
    public static void addModernDetails(final JSONObject jsonObject, final Long resourceID) throws DataAccessException {
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("ManagedDevice"));
        selectQuery.addJoin(new Join("ManagedDevice", "EnrollmentRequestToDevice", new String[] { "RESOURCE_ID" }, new String[] { "MANAGED_DEVICE_ID" }, 2));
        selectQuery.addJoin(new Join("ManagedDevice", "AgentContact", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 1));
        selectQuery.addJoin(new Join("EnrollmentRequestToDevice", "EnrollmentTemplateToRequest", new String[] { "ENROLLMENT_REQUEST_ID" }, new String[] { "ENROLLMENT_REQUEST_ID" }, 1));
        selectQuery.addJoin(new Join("EnrollmentTemplateToRequest", "EnrollmentTemplate", new String[] { "TEMPLATE_ID" }, new String[] { "TEMPLATE_ID" }, 1));
        selectQuery.addSelectColumn(Column.getColumn("ManagedDevice", "RESOURCE_ID"));
        selectQuery.addSelectColumn(Column.getColumn("ManagedDevice", "MANAGED_STATUS"));
        selectQuery.addSelectColumn(Column.getColumn("ManagedDevice", "ADDED_TIME"));
        selectQuery.addSelectColumn(Column.getColumn("ManagedDevice", "REMARKS"));
        selectQuery.addSelectColumn(Column.getColumn("AgentContact", "RESOURCE_ID"));
        selectQuery.addSelectColumn(Column.getColumn("AgentContact", "LAST_CONTACT_TIME"));
        selectQuery.addSelectColumn(Column.getColumn("EnrollmentTemplate", "TEMPLATE_ID"));
        selectQuery.addSelectColumn(Column.getColumn("EnrollmentTemplate", "TEMPLATE_TYPE"));
        selectQuery.addSelectColumn(Column.getColumn("EnrollmentTemplateToRequest", "TEMPLATE_ID"));
        selectQuery.addSelectColumn(Column.getColumn("EnrollmentTemplateToRequest", "ENROLLMENT_REQUEST_ID"));
        selectQuery.setCriteria(new Criteria(Column.getColumn("ManagedDevice", "RESOURCE_ID"), (Object)resourceID, 0));
        final DataObject dataObject = MDMUtil.getPersistenceLite().get(selectQuery);
        if (!dataObject.isEmpty()) {
            Row row = dataObject.getRow("ManagedDevice");
            jsonObject.put("MANAGED_STATUS", row.get("MANAGED_STATUS"));
            jsonObject.put("REMARKS", row.get("REMARKS"));
            jsonObject.put("LAST_CONTACT_TIME", row.get("ADDED_TIME"));
            jsonObject.put("ADDED_TIME", row.get("ADDED_TIME"));
            row = dataObject.getRow("EnrollmentTemplate");
            if (row == null) {
                jsonObject.put("ENROLLMENT_TYPE", 1);
            }
            else {
                jsonObject.put("ENROLLMENT_TYPE", row.get("TEMPLATE_TYPE"));
            }
        }
    }
    
    public static JSONArray getModernDetails(final JSONArray jsonArray) throws DataAccessException {
        final List list = new ArrayList();
        for (int i = 0; i < jsonArray.length(); ++i) {
            list.add(jsonArray.get(i));
        }
        final JSONArray response = new JSONArray();
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("ManagedDevice"));
        selectQuery.addJoin(new Join("ManagedDevice", "EnrollmentRequestToDevice", new String[] { "RESOURCE_ID" }, new String[] { "MANAGED_DEVICE_ID" }, 2));
        selectQuery.addJoin(new Join("ManagedDevice", "AgentContact", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2));
        selectQuery.addJoin(new Join("EnrollmentRequestToDevice", "EnrollmentTemplateToRequest", new String[] { "ENROLLMENT_REQUEST_ID" }, new String[] { "ENROLLMENT_REQUEST_ID" }, 1));
        selectQuery.addJoin(new Join("EnrollmentTemplateToRequest", "EnrollmentTemplate", new String[] { "TEMPLATE_ID" }, new String[] { "TEMPLATE_ID" }, 1));
        selectQuery.addSelectColumn(Column.getColumn("ManagedDevice", "RESOURCE_ID"));
        selectQuery.addSelectColumn(Column.getColumn("ManagedDevice", "MANAGED_STATUS"));
        selectQuery.addSelectColumn(Column.getColumn("ManagedDevice", "ADDED_TIME"));
        selectQuery.addSelectColumn(Column.getColumn("ManagedDevice", "REMARKS"));
        selectQuery.addSelectColumn(Column.getColumn("AgentContact", "RESOURCE_ID"));
        selectQuery.addSelectColumn(Column.getColumn("AgentContact", "LAST_CONTACT_TIME"));
        selectQuery.addSelectColumn(Column.getColumn("EnrollmentTemplate", "TEMPLATE_ID"));
        selectQuery.addSelectColumn(Column.getColumn("EnrollmentTemplate", "TEMPLATE_TYPE"));
        selectQuery.addSelectColumn(Column.getColumn("EnrollmentTemplateToRequest", "TEMPLATE_ID"));
        selectQuery.addSelectColumn(Column.getColumn("EnrollmentTemplateToRequest", "ENROLLMENT_REQUEST_ID"));
        selectQuery.setCriteria(new Criteria(Column.getColumn("ManagedDevice", "RESOURCE_ID"), (Object)list.toArray(), 8));
        final DataObject dataObject = MDMUtil.getPersistenceLite().get(selectQuery);
        final Iterator iterator = dataObject.getRows("ManagedDevice");
        while (iterator.hasNext()) {
            final JSONObject jsonObject = new JSONObject();
            final Row row = iterator.next();
            jsonObject.put("MANAGED_STATUS", row.get("MANAGED_STATUS"));
            jsonObject.put("REMARKS", row.get("REMARKS"));
            jsonObject.put("ADDED_TIME", row.get("ADDED_TIME"));
            jsonObject.put("DEVICE_ID", row.get("RESOURCE_ID"));
            final Long resID = (Long)row.get("RESOURCE_ID");
            final Row contactRow = dataObject.getRow("AgentContact", new Criteria(Column.getColumn("AgentContact", "RESOURCE_ID"), (Object)resID, 0));
            jsonObject.put("LAST_CONTACT_TIME", contactRow.get("LAST_CONTACT_TIME"));
            final Row enrollmentrequestToDevice = dataObject.getRow("EnrollmentRequestToDevice", new Criteria(Column.getColumn("EnrollmentRequestToDevice", "MANAGED_DEVICE_ID"), (Object)resID, 0));
            if (enrollmentrequestToDevice != null) {
                final Row enrollmentrequestToTemplate = dataObject.getRow("EnrollmentTemplateToRequest", new Criteria(Column.getColumn("EnrollmentTemplateToRequest", "ENROLLMENT_REQUEST_ID"), enrollmentrequestToDevice.get("ENROLLMENT_REQUEST_ID"), 0));
                if (enrollmentrequestToTemplate != null) {
                    final Row templateRow = dataObject.getRow("EnrollmentTemplate", new Criteria(Column.getColumn("EnrollmentTemplate", "TEMPLATE_ID"), enrollmentrequestToTemplate.get("TEMPLATE_ID"), 0));
                    if (templateRow != null) {
                        jsonObject.put("ENROLLMENT_TYPE", row.get("TEMPLATE_TYPE"));
                    }
                }
            }
            if (!jsonObject.has("ENROLLMENT_TYPE")) {
                jsonObject.put("ENROLLMENT_TYPE", 1);
            }
            response.put((Object)jsonObject);
        }
        return response;
    }
}
