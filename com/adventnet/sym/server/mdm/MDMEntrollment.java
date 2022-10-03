package com.adventnet.sym.server.mdm;

import java.util.Hashtable;
import java.util.Properties;
import com.me.devicemanagement.framework.server.customer.CustomerInfoUtil;
import java.util.List;
import com.adventnet.persistence.DataObject;
import com.adventnet.ds.query.SelectQuery;
import com.me.devicemanagement.framework.server.util.DBUtil;
import com.adventnet.sym.server.mdm.apps.AppsUtil;
import com.adventnet.sym.server.mdm.config.ProfileAssociateHandler;
import java.util.ArrayList;
import com.adventnet.sym.server.mdm.group.MDMGroupHandler;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import java.util.logging.Level;
import com.adventnet.sym.server.mdm.enroll.MDMEnrollmentUtil;
import com.me.mdm.server.enrollment.MDMEnrollmentDeviceHandler;
import com.me.mdm.server.enrollment.MDMEnrollmentRequestHandler;
import com.adventnet.sym.server.mdm.core.ManagedUserHandler;
import com.adventnet.i18n.I18N;
import com.me.mdm.server.factory.MDMApiFactoryProvider;
import com.adventnet.sym.server.mdm.inv.MDMInvDataPopulator;
import org.json.JSONObject;
import com.me.mdm.server.notification.PushNotificationHandler;
import java.util.HashMap;
import java.util.logging.Logger;

public class MDMEntrollment
{
    public Logger logger;
    public Logger assignUserLogger;
    private static MDMEntrollment mdmEntrollment;
    
    public MDMEntrollment() {
        this.logger = Logger.getLogger("MDMEnrollment");
        this.assignUserLogger = Logger.getLogger("MDMAssignUser");
    }
    
    public static MDMEntrollment getInstance() {
        if (MDMEntrollment.mdmEntrollment == null) {
            MDMEntrollment.mdmEntrollment = new MDMEntrollment();
        }
        return MDMEntrollment.mdmEntrollment;
    }
    
    public void enrolliOSDevice(final HashMap hsEntrollValues) throws Exception {
        final String MessageType = hsEntrollValues.get("MessageType");
        final String requestType = hsEntrollValues.get("RequestType");
        if (MessageType != null && MessageType.equalsIgnoreCase("Authenticate")) {
            PushNotificationHandler.getInstance().addOrUpdateiOSEnrollmentTempData(hsEntrollValues);
        }
        else if (MessageType != null && MessageType.equalsIgnoreCase("TokenUpdate")) {
            PushNotificationHandler.getInstance().addOrUpdateiOSEnrollmentTempData(hsEntrollValues);
        }
        else if (requestType != null && requestType.equalsIgnoreCase("DeviceInformation")) {
            final String sUDID = hsEntrollValues.get("UDID");
            final Long customerID = hsEntrollValues.get("CUSTOMER_ID");
            final String sOSVersion = hsEntrollValues.get("OSVersion");
            final String serialNumber = hsEntrollValues.get("SerialNumber");
            final String imei = hsEntrollValues.get("IMEI");
            final String productName = hsEntrollValues.get("ProductName");
            final String model = hsEntrollValues.get("Model");
            final String isTunesAccountActive = hsEntrollValues.get("iTunesStoreAccountIsActive");
            final String isSupervised = hsEntrollValues.get("IsSupervised");
            final String easId = hsEntrollValues.get("EASDeviceIdentifier");
            final boolean isMultiUser = hsEntrollValues.containsKey("IsMultiUser") && Boolean.parseBoolean(hsEntrollValues.get("IsMultiUser"));
            final JSONObject modelAndDeviceInfo = new JSONObject();
            MDMInvDataPopulator.getInstance();
            final int modelType = MDMInvDataPopulator.getModelType(productName);
            final String iOSDeviceSpecificModel = MDMApiFactoryProvider.getMdmModelNameMappingHandlerAPI().getiOSDeviceSpecificModel(productName);
            modelAndDeviceInfo.put("MODEL_NAME", (Object)iOSDeviceSpecificModel);
            modelAndDeviceInfo.put("PRODUCT_NAME", (Object)productName);
            modelAndDeviceInfo.put("MODEL", (Object)model);
            modelAndDeviceInfo.put("MODEL_TYPE", modelType);
            modelAndDeviceInfo.put("SERIAL_NUMBER", (Object)serialNumber);
            modelAndDeviceInfo.put("IMEI", (Object)imei);
            modelAndDeviceInfo.put("OS_VERSION", (Object)sOSVersion);
            modelAndDeviceInfo.put("IS_ITUNES_ACCOUNT_ACTIVE", (Object)isTunesAccountActive);
            modelAndDeviceInfo.put("IS_SUPERVISED", (Object)isSupervised);
            modelAndDeviceInfo.put("EAS_DEVICE_IDENTIFIER", (Object)easId);
            modelAndDeviceInfo.put("IS_MULTIUSER", isMultiUser);
            String sDeviceName = hsEntrollValues.get("DeviceName");
            if (sDeviceName != null && sDeviceName.trim().equals("")) {
                sDeviceName = I18N.getMsg("mdm.common.DEFAULT_DEVICE_NAME", new Object[0]);
            }
            final JSONObject deviceEnrollJSON = new JSONObject();
            deviceEnrollJSON.put("CUSTOMER_ID", (Object)customerID);
            deviceEnrollJSON.put("UDID", (Object)sUDID);
            deviceEnrollJSON.put("NAME", (Object)sDeviceName);
            deviceEnrollJSON.put("DOMAIN_NETBIOS_NAME", (Object)"MDM");
            if (modelType == 3 || modelType == 4) {
                deviceEnrollJSON.put("RESOURCE_TYPE", (Object)new Integer(121));
                modelAndDeviceInfo.put("IS_MULTIUSER", true);
            }
            this.setUserRequestIds(deviceEnrollJSON, hsEntrollValues);
            deviceEnrollJSON.put("REMARKS", (Object)"dc.mdm.db.agent.enroll.agent_enroll_finished");
            deviceEnrollJSON.put("PLATFORM_TYPE", 1);
            if (modelType == 4 || modelType == 3) {
                deviceEnrollJSON.put("AGENT_TYPE", 8);
            }
            else {
                deviceEnrollJSON.put("AGENT_TYPE", 1);
            }
            deviceEnrollJSON.put("OS_VERSION", (Object)sOSVersion);
            deviceEnrollJSON.put("MANAGED_STATUS", (Object)new Integer(2));
            deviceEnrollJSON.put("IMEI", (Object)imei);
            deviceEnrollJSON.put("SERIAL_NUMBER", (Object)serialNumber);
            deviceEnrollJSON.put("EAS_DEVICE_IDENTIFIER", (Object)easId);
            deviceEnrollJSON.put("MANAGED_STATUS", (Object)new Integer(2));
            deviceEnrollJSON.put("MdModelInfo", (Object)modelAndDeviceInfo);
            final Long eriD = deviceEnrollJSON.getLong("ENROLLMENT_REQUEST_ID");
            final HashMap userDetails = ManagedUserHandler.getInstance().getManagedUserDetailsForRequest(eriD);
            if (userDetails != null && userDetails.containsKey("MANAGED_USER_ID")) {
                deviceEnrollJSON.put("MANAGED_USER_ID", userDetails.get("MANAGED_USER_ID"));
            }
            final int enrollmentType = MDMEnrollmentRequestHandler.getInstance().getEnrollmentType(eriD);
            MDMEnrollmentDeviceHandler.getInstance(enrollmentType).enrollDevice(deviceEnrollJSON);
        }
    }
    
    private void setUserRequestIds(final JSONObject deviceEnrollJSON, final HashMap hsEntrollValues) {
        boolean isAppleConfig = false;
        Long appleConfigId = null;
        try {
            final Long enrollmentRequestID = hsEntrollValues.get("ENROLLMENT_REQUEST_ID");
            final Long managedUserID = hsEntrollValues.get("MANAGED_USER_ID");
            final String isAppleConfigStr = hsEntrollValues.get("isAppleConfig");
            final String appleConfigIdStr = hsEntrollValues.get("appleConfigId");
            if (appleConfigIdStr != null) {
                appleConfigId = Long.valueOf(appleConfigIdStr);
                deviceEnrollJSON.put("appleConfigId", (Object)appleConfigId);
            }
            final String udid = hsEntrollValues.get("UDID");
            isAppleConfig = Boolean.parseBoolean(isAppleConfigStr);
            deviceEnrollJSON.put("isAppleConfig", isAppleConfig);
            final boolean isManagedDeviceUDID = MDMEnrollmentUtil.getInstance().isManagedDeviceUDIDExist(udid);
            final boolean isBulkEnrollUDID = MDMEnrollmentRequestHandler.getInstance().isBulkEnrollUDIDExist(udid);
            if (isAppleConfig && isManagedDeviceUDID) {
                this.setManagedDeviceUserRequest(deviceEnrollJSON, hsEntrollValues);
            }
            else if (isAppleConfig && isBulkEnrollUDID) {
                this.setbulkEnrollmentUserRequest(deviceEnrollJSON, hsEntrollValues);
            }
            else if (isAppleConfig && !isBulkEnrollUDID) {
                this.createUnknownUserRequest(deviceEnrollJSON);
            }
            else {
                deviceEnrollJSON.put("ENROLLMENT_REQUEST_ID", (Object)enrollmentRequestID);
            }
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, "Exception in setUserRequestIds", ex);
        }
    }
    
    private void setManagedDeviceUserRequest(final JSONObject deviceEnrollJSON, final HashMap hsEntrollValues) {
        try {
            final String udid = hsEntrollValues.get("UDID");
            Long enrollmentRequestID = null;
            Long managedUserID = null;
            Long managedDeviceID = null;
            Integer managedStatus = null;
            if (udid != null) {
                final SelectQuery reqQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("ManagedDevice"));
                final Join deviceUserJoin = new Join("ManagedDevice", "ManagedUserToDevice", new String[] { "RESOURCE_ID" }, new String[] { "MANAGED_DEVICE_ID" }, 2);
                final Join userRequestJoin = new Join("ManagedUserToDevice", "EnrollmentRequestToDevice", new String[] { "MANAGED_DEVICE_ID" }, new String[] { "MANAGED_DEVICE_ID" }, 2);
                final Join requestJoin = new Join("EnrollmentRequestToDevice", "DeviceEnrollmentRequest", new String[] { "ENROLLMENT_REQUEST_ID" }, new String[] { "ENROLLMENT_REQUEST_ID" }, 2);
                reqQuery.addJoin(deviceUserJoin);
                reqQuery.addJoin(userRequestJoin);
                reqQuery.addJoin(requestJoin);
                reqQuery.addSelectColumn(Column.getColumn("ManagedDevice", "RESOURCE_ID"));
                reqQuery.addSelectColumn(Column.getColumn("ManagedDevice", "MANAGED_STATUS"));
                reqQuery.addSelectColumn(Column.getColumn("ManagedUserToDevice", "MANAGED_DEVICE_ID"));
                reqQuery.addSelectColumn(Column.getColumn("ManagedUserToDevice", "MANAGED_USER_ID"));
                reqQuery.addSelectColumn(Column.getColumn("EnrollmentRequestToDevice", "ENROLLMENT_REQUEST_ID"));
                reqQuery.addSelectColumn(Column.getColumn("EnrollmentRequestToDevice", "MANAGED_DEVICE_ID"));
                reqQuery.addSelectColumn(Column.getColumn("DeviceEnrollmentRequest", "ENROLLMENT_REQUEST_ID"));
                final Criteria udidCri = new Criteria(Column.getColumn("ManagedDevice", "UDID"), (Object)udid, 0);
                reqQuery.setCriteria(udidCri);
                final DataObject dObj = MDMUtil.getPersistence().get(reqQuery);
                if (!dObj.isEmpty()) {
                    enrollmentRequestID = (Long)dObj.getFirstValue("DeviceEnrollmentRequest", "ENROLLMENT_REQUEST_ID");
                    managedUserID = (Long)dObj.getFirstValue("ManagedUserToDevice", "MANAGED_USER_ID");
                    managedDeviceID = (Long)dObj.getFirstValue("ManagedUserToDevice", "MANAGED_DEVICE_ID");
                    managedStatus = (Integer)dObj.getFirstValue("ManagedDevice", "MANAGED_STATUS");
                    MDMGroupHandler.getInstance().deleteMemberFromAllGroups(managedDeviceID);
                    final List resourceList = new ArrayList();
                    resourceList.add(managedDeviceID);
                    ProfileAssociateHandler.getInstance().deleteRecentProfileForResource(managedDeviceID);
                    ProfileAssociateHandler.getInstance().updateDeviceProfileSummary();
                    AppsUtil.getInstance().deleteAppResourceRel(managedDeviceID);
                    deviceEnrollJSON.put("MANAGED_USER_ID", (Object)managedUserID);
                    if (managedStatus == 4) {
                        final Long appleConfigId = (Long)deviceEnrollJSON.get("appleConfigId");
                        final Long sharedUserId = (Long)DBUtil.getValueFromDB("AppleConfigRequest", "APPLE_CONFIG_REQUEST_ID", (Object)appleConfigId, "SHARED_USER_ID");
                        final Long[] enrollReqIds = { enrollmentRequestID };
                        ManagedUserHandler.getInstance().changeUser(sharedUserId, enrollReqIds);
                        deviceEnrollJSON.put("MANAGED_USER_ID", (Object)sharedUserId);
                    }
                }
                deviceEnrollJSON.put("ENROLLMENT_REQUEST_ID", (Object)enrollmentRequestID);
            }
        }
        catch (final Exception ex) {
            this.assignUserLogger.log(Level.SEVERE, "Exception in setManagedDeviceUserRequest", ex);
        }
    }
    
    private void setbulkEnrollmentUserRequest(final JSONObject deviceEnrollJSON, final HashMap hsEntrollValues) {
        try {
            final String udid = hsEntrollValues.get("UDID");
            Long enrollmentRequestID = null;
            Long managedUserID = null;
            if (udid != null) {
                final Criteria udidCri = new Criteria(Column.getColumn("DeviceEnrollmentRequest", "UDID"), (Object)udid, 0);
                final DataObject dObj = MDMUtil.getPersistence().get("DeviceEnrollmentRequest", udidCri);
                if (!dObj.isEmpty()) {
                    enrollmentRequestID = (Long)dObj.getValue("DeviceEnrollmentRequest", "ENROLLMENT_REQUEST_ID", udidCri);
                    managedUserID = (Long)dObj.getValue("DeviceEnrollmentRequest", "MANAGED_USER_ID", udidCri);
                    deviceEnrollJSON.put("ENROLLMENT_REQUEST_ID", (Object)enrollmentRequestID);
                    deviceEnrollJSON.put("MANAGED_USER_ID", (Object)managedUserID);
                }
            }
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, "Exception in setManagedDeviceUserRequest", ex);
        }
    }
    
    private void createUnknownUserRequest(final JSONObject properties) {
        try {
            final Long customerId = CustomerInfoUtil.getInstance().getDefaultCustomer();
            final Long appleConfigId = (Long)properties.get("appleConfigId");
            final Long sharedUserId = (Long)DBUtil.getValueFromDB("AppleConfigRequest", "APPLE_CONFIG_REQUEST_ID", (Object)appleConfigId, "SHARED_USER_ID");
            final Criteria cri = new Criteria(Column.getColumn("Resource", "RESOURCE_ID"), (Object)sharedUserId, 0);
            final DataObject userObj = MDMUtil.getPersistence().get("Resource", cri);
            final String domainName = (String)userObj.getValue("Resource", "DOMAIN_NETBIOS_NAME", cri);
            final String userName = (String)userObj.getValue("Resource", "NAME", cri);
            final String email = "--";
            final String ownedByStr = String.valueOf(1);
            final Long defaultMDMGroupId = MDMGroupHandler.getInstance().getDefaultMDMSelfEnrollGroupId(customerId, 1, 1);
            final Properties enrollProp = MDMEnrollmentUtil.getInstance().buildEnrollmentProperties(domainName, userName, defaultMDMGroupId, email, ownedByStr, customerId, true, String.valueOf(1), false);
            ((Hashtable<String, Boolean>)enrollProp).put("isSelfEnroll", true);
            ((Hashtable<String, Integer>)enrollProp).put("ENROLLMENT_TYPE", 2);
            String sEventLogRemarks = "dc.mdm.actionlog.enrollment.self_enroll_request";
            final int enrollStatus = ((Hashtable<K, Integer>)MDMEnrollmentRequestHandler.getInstance().sendEnrollmentRequest(enrollProp)).get("ENROLL_STATUS");
            properties.put("ENROLLMENT_REQUEST_ID", ((Hashtable<K, Object>)enrollProp).get("ENROLLMENT_REQUEST_ID"));
            properties.put("MANAGED_USER_ID", ((Hashtable<K, Object>)enrollProp).get("MANAGED_USER_ID"));
            if (enrollStatus == 1) {
                sEventLogRemarks = "dc.mdm.actionlog.enrollment.apple_config_request_success";
                MDMEnrollmentUtil.getInstance().addSelfEnrollEventLog(sEventLogRemarks, userName, customerId);
            }
            else {
                sEventLogRemarks = "dc.mdm.actionlog.enrollment.apple_config_request_failed";
                MDMEnrollmentUtil.getInstance().addSelfEnrollEventLog(sEventLogRemarks, userName, customerId);
            }
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, "Exception in setUserRequestIds", ex);
        }
    }
    
    public void updateEnrollmentDetails(final Long resourceID, final HashMap hsEntrollValues) throws Exception {
        final String MessageType = hsEntrollValues.get("MessageType");
        if (MessageType != null && MessageType.equalsIgnoreCase("TokenUpdate")) {
            final JSONObject map = new JSONObject();
            map.put("TOPIC", hsEntrollValues.get("Topic"));
            map.put("NOTIFICATION_TOKEN_ENCRYPTED", hsEntrollValues.get("DeviceToken"));
            map.put("PUSH_MAGIC_ENCRYPTED", hsEntrollValues.get("PushMagic"));
            map.put("UNLOCK_TOKEN_ENCRYPTED", hsEntrollValues.get("UnlockToken"));
            PushNotificationHandler.getInstance().addOrUpdateManagedIdToNotificationRel(resourceID, 1, map);
        }
    }
    
    public String getOwnedByAsString(final Integer iOwnedBy) throws Exception {
        String sOwnedBy = I18N.getMsg("dc.mdm.enroll.not_specified", new Object[0]);
        if (iOwnedBy != null) {
            try {
                if (iOwnedBy == 1) {
                    sOwnedBy = I18N.getMsg("dc.mdm.enroll.corporate", new Object[0]);
                }
                else if (iOwnedBy == 2) {
                    sOwnedBy = I18N.getMsg("dc.mdm.enroll.personal", new Object[0]);
                }
            }
            catch (final Exception ex) {
                this.logger.log(Level.WARNING, "Exception while getting Owned By Value as String ", ex);
            }
        }
        return sOwnedBy;
    }
    
    static {
        MDMEntrollment.mdmEntrollment = null;
    }
}
