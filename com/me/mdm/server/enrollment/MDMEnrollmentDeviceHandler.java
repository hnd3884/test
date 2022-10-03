package com.me.mdm.server.enrollment;

import java.util.Hashtable;
import com.me.mdm.server.location.lostmode.LostModeDataHandler;
import com.me.mdm.server.doc.DocMgmtDataHandler;
import org.json.JSONArray;
import com.me.mdm.server.alerts.MDMAlertConstants;
import java.util.Properties;
import com.me.mdm.server.alerts.MDMAlertMailGeneratorUtil;
import com.adventnet.persistence.DataAccessException;
import com.adventnet.ds.query.DeleteQuery;
import com.adventnet.persistence.DataAccess;
import com.adventnet.ds.query.DeleteQueryImpl;
import java.util.Iterator;
import com.adventnet.persistence.DataObject;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.persistence.Row;
import com.adventnet.sym.server.mdm.core.ManagedUserHandler;
import com.me.mdm.core.enrollment.DeviceForEnrollmentHandler;
import com.adventnet.ds.query.SortColumn;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.adventnet.sym.server.mdm.core.DeviceEvent;
import java.util.HashMap;
import com.me.devicemanagement.framework.server.logger.SyMLogger;
import com.adventnet.sym.server.mdm.inv.MDMInvDataPopulator;
import com.adventnet.sym.server.mdm.group.MDMGroupHandler;
import com.adventnet.sym.server.mdm.enroll.MDMEnrollmentUtil;
import com.me.mdm.server.notification.PushNotificationHandler;
import com.me.devicemanagement.framework.server.exception.SyMException;
import org.json.JSONException;
import com.me.mdm.server.onelinelogger.MDMOneLineLogger;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.me.mdm.server.common.MDMEventConstant;
import com.adventnet.sym.server.mdm.util.MDMEventLogHandler;
import com.me.devicemanagement.framework.server.logger.DMSecurityLogger;
import com.me.mdm.server.util.MDMFeatureParamsHandler;
import com.adventnet.sym.server.mdm.core.ManagedDeviceHandler;
import java.util.logging.Level;
import org.json.JSONObject;
import com.me.mdm.server.enrollment.admin.MDMAdminEnrollmentDeviceHandler;
import java.util.logging.Logger;

public class MDMEnrollmentDeviceHandler
{
    public static Logger logger;
    static String sourceClass;
    
    public static MDMEnrollmentDeviceHandler getInstance(final int enrollmentType) {
        if (enrollmentType == 3 || enrollmentType == 4) {
            return new MDMAdminEnrollmentDeviceHandler();
        }
        return new MDMEnrollmentDeviceHandler();
    }
    
    public JSONObject enrollDevice(final JSONObject deviceJSON) throws SyMException {
        final JSONObject responseJson = new JSONObject();
        try {
            MDMEnrollmentDeviceHandler.logger.log(Level.INFO, "Device details {0}", deviceJSON);
            String imei = deviceJSON.optString("IMEI", (String)null);
            if (imei != null) {
                imei = imei.replace(" ", "");
                deviceJSON.remove("IMEI");
                deviceJSON.put("IMEI", (Object)imei);
            }
            final JSONObject oldDeviceProps = this.handlePreEnrollment(deviceJSON);
            deviceJSON.put("UNREGISTERED_TIME", -1);
            ManagedDeviceHandler.getInstance().addOrUpdateManagedDevice(deviceJSON);
            this.addOrUpdateManagedDeviceRelatedTables(deviceJSON);
            final boolean migratedDevice = MDMFeatureParamsHandler.getInstance().isFeatureEnabled("MigrationTarget") && deviceJSON.optBoolean("IsMigrated");
            if (oldDeviceProps != null && oldDeviceProps.length() != 0 && !migratedDevice) {
                deviceJSON.put("DeviceReassignJson", (Object)oldDeviceProps);
            }
            this.invokeManagedDeviceAddedListener(deviceJSON);
            DMSecurityLogger.info(MDMEnrollmentDeviceHandler.logger, MDMEnrollmentDeviceHandler.sourceClass, "enrollDevice", "Enrolled device with details : " + deviceJSON.toString(), (Object)null);
            MDMEventLogHandler.getInstance().MDMEventLogEntry(2001, null, MDMEventConstant.DC_SYSTEM_USER, "dc.mdm.actionlog.enrollment.enroll_success", deviceJSON.optString("NAME"), deviceJSON.optLong("CUSTOMER_ID"));
            final org.json.simple.JSONObject logJSON = new org.json.simple.JSONObject();
            logJSON.put((Object)"UDID", deviceJSON.opt("UDID"));
            logJSON.put((Object)"ENROLLMENT_REQUEST_ID", deviceJSON.opt("ENROLLMENT_REQUEST_ID"));
            logJSON.put((Object)"RESOURCE_ID", deviceJSON.opt("RESOURCE_ID"));
            logJSON.put((Object)"CUSTOMER_ID", deviceJSON.opt("CUSTOMER_ID"));
            logJSON.put((Object)"PLATFORM_TYPE", (Object)MDMUtil.getInstance().getPlatformName(deviceJSON.optInt("PLATFORM_TYPE", -1)));
            logJSON.put((Object)"MANAGED_USER_ID", deviceJSON.opt("MANAGED_USER_ID"));
            logJSON.put((Object)"SERIAL_NUMBER", deviceJSON.opt("SERIAL_NUMBER"));
            logJSON.put((Object)"REMARKS", (Object)"enroll-success");
            MDMOneLineLogger.log(Level.INFO, "DEVICE_ENROLLED", logJSON);
        }
        catch (final JSONException ex) {
            Logger.getLogger(MDMEnrollmentDeviceHandler.class.getName()).log(Level.SEVERE, null, (Throwable)ex);
        }
        return responseJson;
    }
    
    protected void addOrUpdateManagedDeviceRelatedTables(final JSONObject deviceJSON) {
        final String sourceMethod = "addOrUpdateManagedDeviceRelatedTables";
        try {
            final String deviceUDID = deviceJSON.optString("UDID");
            final Long enrollmentRequestID = deviceJSON.optLong("ENROLLMENT_REQUEST_ID");
            final Long customerID = deviceJSON.optLong("CUSTOMER_ID");
            final Long managedDeviceID = deviceJSON.optLong("RESOURCE_ID");
            final Integer platform = deviceJSON.optInt("PLATFORM_TYPE");
            final String deviceName = deviceJSON.optString("NAME");
            final HashMap enrollmentMap = MDMEnrollmentRequestHandler.getInstance().getEnrollmentMap(enrollmentRequestID);
            final Long techUserID = enrollmentMap.get("USER_ID");
            if (techUserID != null) {
                deviceJSON.put("addedUserID", (Object)techUserID);
            }
            final int enrollmentType = enrollmentMap.get("ENROLLMENT_TYPE");
            if (managedDeviceID != null) {
                if (platform == 1) {
                    PushNotificationHandler.getInstance().cloneIOSCommunicationDetails(managedDeviceID, deviceUDID);
                    final String sOSVersion = deviceJSON.optString("OS_VERSION");
                    if (sOSVersion != null) {
                        MDMUtil.getInstance().addOrUpdateOSDetailsInTemp(managedDeviceID, sOSVersion);
                    }
                }
                else {
                    final String registrationId = (String)deviceJSON.opt("REGISTRATION_ID");
                    if (registrationId != null) {
                        this.updateAndroidCommunicationDetails(managedDeviceID, registrationId);
                    }
                }
            }
            if (managedDeviceID != null && enrollmentRequestID != null) {
                this.addOrUpdateEnrollmentRequestToDevice(managedDeviceID, enrollmentRequestID);
            }
            final Long managedUserID = enrollmentMap.get("MANAGED_USER_ID");
            if (managedUserID != null) {
                deviceJSON.put("MANAGED_USER_ID", (Object)managedUserID);
            }
            if (managedDeviceID != null && managedUserID != null) {
                this.addOrUpdateManagedUserToDeviceRel(managedDeviceID, managedUserID);
                final JSONObject selfEnrollmentSettings = EnrollmentSettingsHandler.getInstance().getSelfEnrollmentSettings(customerID);
                Boolean notifyEnabled = selfEnrollmentSettings.getBoolean("NOTIFY_SELF_ENROLLMENT");
                if (notifyEnabled == null) {
                    notifyEnabled = false;
                }
                if (enrollmentType == 2) {
                    final Long updatedBy = selfEnrollmentSettings.optLong("UPDATED_BY");
                    if (updatedBy > 0L) {
                        deviceJSON.put("addedUserID", selfEnrollmentSettings.getLong("UPDATED_BY"));
                    }
                    if (notifyEnabled) {
                        final JSONObject deviceDetails = ManagedDeviceHandler.getInstance().getManagedDeviceDetailsForSelfEnrollMail(managedDeviceID);
                        this.sendSelfEnrollmentMail(deviceDetails);
                    }
                }
            }
            MDMEnrollmentRequestHandler.getInstance().updateDeviceRequestStatus(enrollmentRequestID, 3, platform);
            MDMEnrollmentOTPHandler.getInstance().deleteOTPPassword(enrollmentRequestID);
            final Boolean isAppleConfig = deviceJSON.optBoolean("isAppleConfig", false);
            if (!isAppleConfig && enrollmentRequestID != null && managedDeviceID != null && enrollmentType == 2) {
                final int ownedBy = MDMEnrollmentUtil.getInstance().getOwnedBy(managedDeviceID);
                final Long groupId = MDMGroupHandler.getInstance().getDefaultMDMSelfEnrollGroupId(customerID, platform, ownedBy);
                MDMEnrollmentRequestHandler.getInstance().addEnrollmentGroupTable(enrollmentRequestID, groupId);
            }
            final JSONObject modelandDeviceInfo = deviceJSON.optJSONObject("MdModelInfo");
            if (modelandDeviceInfo != null) {
                MDMInvDataPopulator.getInstance().addInvDetailsOnEnrollment(modelandDeviceInfo, managedDeviceID);
            }
        }
        catch (final Exception exp) {
            SyMLogger.error(MDMEnrollmentDeviceHandler.logger, MDMEnrollmentDeviceHandler.sourceClass, sourceMethod, "Exception occurre during Managed Device Related Tables add or update !!!", (Throwable)exp);
            MDMEnrollmentDeviceHandler.logger.log(Level.INFO, "On exiting addOrUpdateManagedDeviceRelatedTables with device details : {0}", deviceJSON.toString());
        }
    }
    
    protected void invokeManagedDeviceAddedListener(final JSONObject deviceJSON) {
        final DeviceEvent deviceEvent = new DeviceEvent(deviceJSON.optLong("RESOURCE_ID"), deviceJSON.optLong("CUSTOMER_ID"));
        deviceEvent.udid = deviceJSON.optString("UDID", (String)null);
        deviceEvent.enrollmentRequestId = deviceJSON.optLong("ENROLLMENT_REQUEST_ID");
        deviceEvent.platformType = deviceJSON.optInt("PLATFORM_TYPE");
        deviceEvent.resourceJSON = deviceJSON;
        ManagedDeviceHandler.getInstance().invokeDeviceListeners(deviceEvent, 5);
        if (ManagedDeviceHandler.getInstance().getManagedDeviceStatus(deviceJSON.optLong("RESOURCE_ID")) == 2) {
            ManagedDeviceHandler.getInstance().invokeDeviceListeners(deviceEvent, 1);
        }
    }
    
    public JSONObject handlePreEnrollment(final JSONObject deviceJSON) {
        JSONObject oldDeviceProps = null;
        try {
            final String udid = deviceJSON.optString("UDID");
            final String serialNo = deviceJSON.optString("SERIAL_NUMBER");
            final String imei = deviceJSON.optString("IMEI");
            MDMEnrollmentUtil.getInstance().handleDuplicateAndroidDevice(imei, serialNo, udid);
            deviceJSON.putOpt("SerialNumber", (Object)deviceJSON.optString("SERIAL_NUMBER"));
            final Long resourceId = ManagedDeviceHandler.getInstance().getManagedDeviceID(deviceJSON);
            if (resourceId != null) {
                final Long enrollRequestID = deviceJSON.getLong("ENROLLMENT_REQUEST_ID");
                final SelectQuery sQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("ManagedDevice"));
                sQuery.addJoin(new Join("ManagedDevice", "EnrollmentRequestToDevice", new String[] { "RESOURCE_ID" }, new String[] { "MANAGED_DEVICE_ID" }, 1));
                sQuery.addJoin(new Join("EnrollmentRequestToDevice", "DeviceEnrollmentRequest", new String[] { "ENROLLMENT_REQUEST_ID" }, new String[] { "ENROLLMENT_REQUEST_ID" }, 1));
                sQuery.addJoin(new Join("DeviceEnrollmentRequest", "ManagedUser", new String[] { "MANAGED_USER_ID" }, new String[] { "MANAGED_USER_ID" }, 1));
                sQuery.addJoin(new Join("ManagedDevice", "MDMCollnToResErrorCode", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 1));
                sQuery.addJoin(new Join("ManagedDevice", "CustomGroupMemberRel", new String[] { "RESOURCE_ID" }, new String[] { "MEMBER_RESOURCE_ID" }, 1));
                sQuery.addJoin(new Join("CustomGroupMemberRel", "RecentProfileForGroup", new String[] { "GROUP_RESOURCE_ID" }, new String[] { "GROUP_ID" }, 1));
                sQuery.addJoin(new Join("ManagedDevice", "ManagedDeviceExtn", new String[] { "RESOURCE_ID" }, new String[] { "MANAGED_DEVICE_ID" }, 1));
                sQuery.addJoin(new Join("ManagedUser", "Resource", new String[] { "MANAGED_USER_ID" }, new String[] { "RESOURCE_ID" }, 1));
                sQuery.addSelectColumn(Column.getColumn("ManagedDevice", "RESOURCE_ID"));
                sQuery.addSelectColumn(Column.getColumn("ManagedDevice", "MANAGED_STATUS"));
                sQuery.addSelectColumn(Column.getColumn("DeviceEnrollmentRequest", "ENROLLMENT_REQUEST_ID"));
                sQuery.addSelectColumn(Column.getColumn("DeviceEnrollmentRequest", "MANAGED_USER_ID"));
                sQuery.addSelectColumn(Column.getColumn("DeviceEnrollmentRequest", "USER_ID"));
                sQuery.addSelectColumn(Column.getColumn("ManagedUser", "MANAGED_USER_ID"));
                sQuery.addSelectColumn(Column.getColumn("ManagedUser", "EMAIL_ADDRESS"));
                sQuery.addSelectColumn(Column.getColumn("ManagedUser", "DISPLAY_NAME"));
                sQuery.addSelectColumn(Column.getColumn("Resource", "DOMAIN_NETBIOS_NAME"));
                sQuery.addSelectColumn(Column.getColumn("ManagedDeviceExtn", "NAME"));
                sQuery.addSelectColumn(Column.getColumn("ManagedDeviceExtn", "USER_ID"));
                sQuery.addSelectColumn(Column.getColumn("ManagedDeviceExtn", "MANAGED_DEVICE_ID"));
                sQuery.addSelectColumn(Column.getColumn("Resource", "RESOURCE_ID"));
                sQuery.addSelectColumn(Column.getColumn("Resource", "NAME"));
                sQuery.addSelectColumn(Column.getColumn("CustomGroupMemberRel", "GROUP_RESOURCE_ID"));
                sQuery.addSelectColumn(Column.getColumn("CustomGroupMemberRel", "MEMBER_RESOURCE_ID"));
                sQuery.addSelectColumn(Column.getColumn("RecentProfileForGroup", "*"));
                final Criteria resCriteria = new Criteria(Column.getColumn("ManagedDevice", "RESOURCE_ID"), (Object)resourceId, 0);
                final Criteria eridCriteria = new Criteria(Column.getColumn("DeviceEnrollmentRequest", "ENROLLMENT_REQUEST_ID"), (Object)enrollRequestID, 1).or(new Criteria(Column.getColumn("DeviceEnrollmentRequest", "ENROLLMENT_REQUEST_ID"), (Object)null, 0));
                sQuery.setCriteria(resCriteria.and(eridCriteria));
                sQuery.addSortColumn(new SortColumn(Column.getColumn("DeviceEnrollmentRequest", "ENROLLMENT_REQUEST_ID"), false));
                final DataObject dobj = MDMUtil.getPersistence().get(sQuery);
                if (!dobj.isEmpty()) {
                    if (new DeviceForEnrollmentHandler().getDeviceForEnrollmentUserId(deviceJSON) == null) {
                        oldDeviceProps = this.populateOldDevicePropsForReassignment(dobj, resourceId);
                    }
                    final Long oldMuid = (Long)dobj.getFirstValue("DeviceEnrollmentRequest", "MANAGED_USER_ID");
                    final Long oldErid = (Long)dobj.getFirstValue("DeviceEnrollmentRequest", "ENROLLMENT_REQUEST_ID");
                    final Long currentReqID = deviceJSON.getLong("ENROLLMENT_REQUEST_ID");
                    int newEnrollType = 3;
                    long newMuid = -1L;
                    if (currentReqID != -1L) {
                        newEnrollType = MDMEnrollmentRequestHandler.getInstance().getEnrollmentType(deviceJSON.getLong("ENROLLMENT_REQUEST_ID"));
                        newMuid = ManagedUserHandler.getInstance().getManagedUserDetailsForRequest(deviceJSON.getLong("ENROLLMENT_REQUEST_ID")).get("MANAGED_USER_ID");
                    }
                    MDMEnrollmentDeviceHandler.logger.log(Level.INFO, "In handlePreEnrollment Old ManagedUserID:{0} , oldErid:{1} , currentErid:{2} , newEnrollmentType:{3} for resourceJSON:{4}", new Object[] { oldMuid, oldErid, currentReqID, newEnrollType, deviceJSON });
                    if (newEnrollType == 3 || newEnrollType == 4 || oldMuid != newMuid) {
                        final DeviceEvent oldDeviceEvent = new DeviceEvent(resourceId, deviceJSON.optLong("CUSTOMER_ID"));
                        oldDeviceEvent.enrollmentRequestId = oldErid;
                        oldDeviceEvent.resourceJSON = new JSONObject();
                        ManagedDeviceHandler.getInstance().invokeDeviceListeners(oldDeviceEvent, 0);
                        MDMEnrollmentDeviceHandler.logger.info("Deleting device for RESOURCE : " + resourceId);
                        this.deleteDeviceCommandsTableEntries(resourceId, currentReqID);
                        this.deleteCommandHistoryTableEntries(resourceId, currentReqID);
                        dobj.deleteRows("MDMCollnToResErrorCode", new Criteria(new Column("MDMCollnToResErrorCode", "RESOURCE_ID"), (Object)resourceId, 0));
                        this.deleteResourceToProfileHistoryEntries(resourceId, currentReqID);
                        this.deleteRecentProfileForResourceEntries(resourceId, currentReqID);
                        dobj.deleteRows("ManagedDevice", new Criteria(Column.getColumn("ManagedDevice", "RESOURCE_ID"), (Object)resourceId, 0));
                        final Iterator<Row> iter = dobj.getRows("DeviceEnrollmentRequest", new Criteria(Column.getColumn("DeviceEnrollmentRequest", "ENROLLMENT_REQUEST_ID"), (Object)currentReqID, 1));
                        while (iter.hasNext()) {
                            final Row enrollRow = iter.next();
                            final Long muid = (Long)enrollRow.get("MANAGED_USER_ID");
                            final Long erid = (Long)enrollRow.get("ENROLLMENT_REQUEST_ID");
                            MDMEnrollmentDeviceHandler.logger.info("Deleting request: " + erid + " for UDID " + udid);
                            dobj.deleteRow(enrollRow);
                        }
                    }
                    else {
                        dobj.deleteRows("MdCommandsToDevice", new Criteria(new Column("MdCommandsToDevice", "RESOURCE_ID"), (Object)resourceId, 0));
                        dobj.deleteRows("CommandHistory", new Criteria(new Column("CommandHistory", "RESOURCE_ID"), (Object)resourceId, 0));
                        dobj.deleteRows("MDMCollnToResErrorCode", new Criteria(new Column("MDMCollnToResErrorCode", "RESOURCE_ID"), (Object)resourceId, 0));
                        dobj.deleteRows("ResourceToProfileHistory", new Criteria(new Column("ResourceToProfileHistory", "RESOURCE_ID"), (Object)resourceId, 0));
                        dobj.deleteRows("RecentProfileForResource", new Criteria(new Column("RecentProfileForResource", "RESOURCE_ID"), (Object)resourceId, 0));
                        dobj.deleteRows("CustomGroupMemberRel", new Criteria(Column.getColumn("CustomGroupMemberRel", "MEMBER_RESOURCE_ID"), (Object)resourceId, 0));
                        dobj.deleteRows("ManagedDevice", new Criteria(Column.getColumn("ManagedDevice", "RESOURCE_ID"), (Object)resourceId, 0));
                        MDMEnrollmentDeviceHandler.logger.log(Level.INFO, "Deleting old ManagedDevice,CommandHistory,MDCommands,Colln Mappings ,RecentProfile etc for resourceID:{0} , UDID:{1}", new Object[] { resourceId, udid });
                    }
                    MDMUtil.getPersistenceLite().update(dobj);
                }
                else {
                    MDMEnrollmentDeviceHandler.logger.log(Level.INFO, "Not handling deletion of existing resourceID as DO is empty for resourceID:{0}", resourceId);
                }
                MDMEnrollmentDeviceHandler.logger.log(Level.INFO, "On Admin Enrollment of given device {0} Duplicate is found as resourced {1} is deleted", new Object[] { deviceJSON, resourceId });
            }
            else {
                MDMEnrollmentDeviceHandler.logger.log(Level.INFO, "ResourceID is null , probably new device in system");
            }
        }
        catch (final Exception ex) {
            MDMEnrollmentDeviceHandler.logger.log(Level.SEVERE, "Exception while handling duplicate device deletion: {0}", ex);
        }
        return oldDeviceProps;
    }
    
    private void deleteCommandHistoryTableEntries(final Long resourceId, final Long currentErid) throws DataAccessException {
        final DeleteQuery cmdHistoryDelQuery = (DeleteQuery)new DeleteQueryImpl("CommandHistory");
        cmdHistoryDelQuery.addJoin(new Join("CommandHistory", "ManagedDevice", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2));
        cmdHistoryDelQuery.addJoin(new Join("ManagedDevice", "EnrollmentRequestToDevice", new String[] { "RESOURCE_ID" }, new String[] { "MANAGED_DEVICE_ID" }, 1));
        final Criteria resIdCriteria = new Criteria(Column.getColumn("CommandHistory", "RESOURCE_ID"), (Object)resourceId, 0);
        final Criteria notCurrentEridCriteria = new Criteria(Column.getColumn("EnrollmentRequestToDevice", "ENROLLMENT_REQUEST_ID"), (Object)currentErid, 1).or(new Criteria(Column.getColumn("EnrollmentRequestToDevice", "ENROLLMENT_REQUEST_ID"), (Object)null, 0));
        cmdHistoryDelQuery.setCriteria(resIdCriteria.and(notCurrentEridCriteria));
        MDMEnrollmentDeviceHandler.logger.log(Level.INFO, "Going to delete CommandHistory table entries for resourceId {0} with an ERID not equal to {1} or ERID as null", new Object[] { resourceId, currentErid });
        DataAccess.delete(cmdHistoryDelQuery);
        MDMEnrollmentDeviceHandler.logger.log(Level.INFO, "Deleted CommandHistory table entries for resourceId {0} with an ERID not equal to {1} or ERID as null", new Object[] { resourceId, currentErid });
    }
    
    private void deleteDeviceCommandsTableEntries(final Long resourceId, final Long currentErid) throws DataAccessException {
        final DeleteQuery deviceCommandsDelQuery = (DeleteQuery)new DeleteQueryImpl("MdCommandsToDevice");
        deviceCommandsDelQuery.addJoin(new Join("MdCommandsToDevice", "ManagedDevice", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2));
        deviceCommandsDelQuery.addJoin(new Join("ManagedDevice", "EnrollmentRequestToDevice", new String[] { "RESOURCE_ID" }, new String[] { "MANAGED_DEVICE_ID" }, 1));
        final Criteria resIdCriteria = new Criteria(Column.getColumn("MdCommandsToDevice", "RESOURCE_ID"), (Object)resourceId, 0);
        final Criteria notCurrentEridCriteria = new Criteria(Column.getColumn("EnrollmentRequestToDevice", "ENROLLMENT_REQUEST_ID"), (Object)currentErid, 1).or(new Criteria(Column.getColumn("EnrollmentRequestToDevice", "ENROLLMENT_REQUEST_ID"), (Object)null, 0));
        deviceCommandsDelQuery.setCriteria(resIdCriteria.and(notCurrentEridCriteria));
        MDMEnrollmentDeviceHandler.logger.log(Level.INFO, "Going to delete MdCommandsToDevice table entries for resourceId {0} with an ERID not equal to {1} or ERID as null", new Object[] { resourceId, currentErid });
        DataAccess.delete(deviceCommandsDelQuery);
        MDMEnrollmentDeviceHandler.logger.log(Level.INFO, "Deleted MdCommandsToDevice table entries for resourceId {0} with an ERID not equal to {1} or ERID as null", new Object[] { resourceId, currentErid });
    }
    
    private void deleteResourceToProfileHistoryEntries(final Long resourceId, final Long currentErid) throws DataAccessException {
        final DeleteQuery resToProfileHistoryDelQuery = (DeleteQuery)new DeleteQueryImpl("ResourceToProfileHistory");
        resToProfileHistoryDelQuery.addJoin(new Join("ResourceToProfileHistory", "ManagedDevice", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2));
        resToProfileHistoryDelQuery.addJoin(new Join("ManagedDevice", "EnrollmentRequestToDevice", new String[] { "RESOURCE_ID" }, new String[] { "MANAGED_DEVICE_ID" }, 1));
        final Criteria resIdCriteria = new Criteria(Column.getColumn("ResourceToProfileHistory", "RESOURCE_ID"), (Object)resourceId, 0);
        final Criteria notCurrentEridCriteria = new Criteria(Column.getColumn("EnrollmentRequestToDevice", "ENROLLMENT_REQUEST_ID"), (Object)currentErid, 1).or(new Criteria(Column.getColumn("EnrollmentRequestToDevice", "ENROLLMENT_REQUEST_ID"), (Object)null, 0));
        resToProfileHistoryDelQuery.setCriteria(resIdCriteria.and(notCurrentEridCriteria));
        MDMEnrollmentDeviceHandler.logger.log(Level.INFO, "Going to delete ResourceToProfileHistory table entries for resourceId {0} with an ERID not equal to {1} or ERID as null", new Object[] { resourceId, currentErid });
        DataAccess.delete(resToProfileHistoryDelQuery);
        MDMEnrollmentDeviceHandler.logger.log(Level.INFO, "Deleted ResourceToProfileHistory table entries for resourceId {0} with an ERID not equal to {1} or ERID as null", new Object[] { resourceId, currentErid });
    }
    
    private void deleteRecentProfileForResourceEntries(final Long resourceId, final Long currentErid) throws DataAccessException {
        final DeleteQuery recentProfileForResDelQuery = (DeleteQuery)new DeleteQueryImpl("RecentProfileForResource");
        recentProfileForResDelQuery.addJoin(new Join("RecentProfileForResource", "ManagedDevice", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2));
        recentProfileForResDelQuery.addJoin(new Join("ManagedDevice", "EnrollmentRequestToDevice", new String[] { "RESOURCE_ID" }, new String[] { "MANAGED_DEVICE_ID" }, 1));
        final Criteria resIdCriteria = new Criteria(Column.getColumn("RecentProfileForResource", "RESOURCE_ID"), (Object)resourceId, 0);
        final Criteria notCurrentEridCriteria = new Criteria(Column.getColumn("EnrollmentRequestToDevice", "ENROLLMENT_REQUEST_ID"), (Object)currentErid, 1).or(new Criteria(Column.getColumn("EnrollmentRequestToDevice", "ENROLLMENT_REQUEST_ID"), (Object)null, 0));
        recentProfileForResDelQuery.setCriteria(resIdCriteria.and(notCurrentEridCriteria));
        MDMEnrollmentDeviceHandler.logger.log(Level.INFO, "Going to delete RecentProfileForResource table entries for resourceId {0} with an ERID not equal to {1} or ERID as null", new Object[] { resourceId, currentErid });
        DataAccess.delete(recentProfileForResDelQuery);
        MDMEnrollmentDeviceHandler.logger.log(Level.INFO, "Deleted RecentProfileForResource table entries for resourceId {0} with an ERID not equal to {1} or ERID as null", new Object[] { resourceId, currentErid });
    }
    
    private void updateAndroidCommunicationDetails(final Long managedDeviceID, final String registrationId) throws JSONException {
        final JSONObject prop = getAndroidDeviceInfo(registrationId);
        try {
            PushNotificationHandler.getInstance().addOrUpdateManagedIdToNotificationRel(managedDeviceID, 2, prop);
        }
        catch (final Exception ex) {
            MDMEnrollmentDeviceHandler.logger.log(Level.SEVERE, "Exception updating communication details", ex);
        }
    }
    
    public void addOrUpdateEnrollmentRequestToDevice(final Long managedDeviceID, final Long enrollRequestID) throws DataAccessException {
        boolean removeLastEnrollmentRequest = false;
        Long lastEnrollRequestID = null;
        final DataObject existingEnrollRequestIDObject = MDMUtil.getPersistence().get("EnrollmentRequestToDevice", new Criteria(Column.getColumn("EnrollmentRequestToDevice", "ENROLLMENT_REQUEST_ID"), (Object)enrollRequestID, 0));
        if (!existingEnrollRequestIDObject.isEmpty()) {
            MDMEnrollmentDeviceHandler.logger.log(Level.INFO, "Duplicate enrollRequestID inside addOrUpdateEnrollmentRequestToDevice with enrollment request {0} to managed device {1} ", new Object[] { enrollRequestID, managedDeviceID });
        }
        final Criteria criteria = new Criteria(Column.getColumn("EnrollmentRequestToDevice", "MANAGED_DEVICE_ID"), (Object)managedDeviceID, 0);
        final DataObject managedUserObject = MDMUtil.getPersistence().get("EnrollmentRequestToDevice", criteria);
        Row managedUserRow = null;
        if (managedUserObject.isEmpty()) {
            managedUserRow = new Row("EnrollmentRequestToDevice");
            managedUserRow.set("ENROLLMENT_REQUEST_ID", (Object)enrollRequestID);
            managedUserRow.set("MANAGED_DEVICE_ID", (Object)managedDeviceID);
            managedUserObject.addRow(managedUserRow);
            MDMEnrollmentDeviceHandler.logger.log(Level.INFO, "On mapping enrollment request {0} to managed device {1} ", new Object[] { enrollRequestID, managedDeviceID });
        }
        else {
            managedUserRow = managedUserObject.getFirstRow("EnrollmentRequestToDevice");
            lastEnrollRequestID = (Long)managedUserObject.getFirstValue("EnrollmentRequestToDevice", "ENROLLMENT_REQUEST_ID");
            removeLastEnrollmentRequest = true;
            managedUserRow.set("ENROLLMENT_REQUEST_ID", (Object)enrollRequestID);
            managedUserObject.updateRow(managedUserRow);
            MDMEnrollmentDeviceHandler.logger.log(Level.INFO, "On updating existing enrollment request {0} with {1} ", new Object[] { lastEnrollRequestID, enrollRequestID });
        }
        MDMUtil.getPersistence().update(managedUserObject);
        if (removeLastEnrollmentRequest && lastEnrollRequestID != null && lastEnrollRequestID != (long)enrollRequestID) {
            MDMEnrollmentUtil.getInstance().deviceWithoutRequestDebugLog(new Long[] { lastEnrollRequestID }, "mdmenrollmentdevicehandler");
            final Criteria removeReqCriteria = new Criteria(new Column("DeviceEnrollmentRequest", "ENROLLMENT_REQUEST_ID"), (Object)lastEnrollRequestID, 0);
            MDMUtil.getPersistence().delete(removeReqCriteria);
        }
    }
    
    public boolean addOrUpdateManagedUserToDeviceRel(final Long managedDeviceID, final Long managedUserID) throws DataAccessException {
        boolean isAdded = false;
        final Criteria criteria = new Criteria(Column.getColumn("ManagedUserToDevice", "MANAGED_DEVICE_ID"), (Object)managedDeviceID, 0);
        final DataObject managedUserObject = MDMUtil.getPersistence().get("ManagedUserToDevice", criteria);
        Row managedUserRow = null;
        if (managedUserObject.isEmpty()) {
            managedUserRow = new Row("ManagedUserToDevice");
            managedUserRow.set("MANAGED_USER_ID", (Object)managedUserID);
            managedUserRow.set("MANAGED_DEVICE_ID", (Object)managedDeviceID);
            managedUserObject.addRow(managedUserRow);
            isAdded = true;
        }
        else {
            managedUserRow = managedUserObject.getFirstRow("ManagedUserToDevice");
            managedUserRow.set("MANAGED_USER_ID", (Object)managedUserID);
            managedUserObject.updateRow(managedUserRow);
        }
        MDMUtil.getPersistence().update(managedUserObject);
        return isAdded;
    }
    
    private void sendSelfEnrollmentMail(final JSONObject deviceDetails) {
        try {
            final MDMAlertMailGeneratorUtil mailGenerator = new MDMAlertMailGeneratorUtil(MDMEnrollmentDeviceHandler.logger);
            final Properties prop = new Properties();
            ((Hashtable<String, String>)prop).put("$user_name$", String.valueOf(deviceDetails.get("DISPLAY_NAME")));
            ((Hashtable<String, String>)prop).put("$device_name$", String.valueOf(deviceDetails.get("NAME")));
            ((Hashtable<String, String>)prop).put("$platform$", String.valueOf(deviceDetails.get("PLATFORM_TYPE")));
            ((Hashtable<String, String>)prop).put("$serial_number$", String.valueOf(deviceDetails.get("SERIAL_NUMBER")));
            ((Hashtable<String, String>)prop).put("$imei$", String.valueOf(deviceDetails.get("UDID")));
            ((Hashtable<String, String>)prop).put("$udid$", String.valueOf(deviceDetails.get("IMEI")));
            ((Hashtable<String, Boolean>)prop).put("appendFooter", true);
            mailGenerator.sendMail(MDMAlertConstants.SELF_ENROLLMENT_MAIL_TEMPLATE, "MDM-SELF-ENROLLMENT", Long.valueOf(String.valueOf(deviceDetails.get("CUSTOMER_ID"))), prop);
        }
        catch (final Exception exp) {
            MDMEnrollmentDeviceHandler.logger.log(Level.WARNING, "Exception in sendSelfEnrollmentMail", exp);
        }
    }
    
    private static JSONObject getAndroidDeviceInfo(final String sRegistrationID) throws JSONException {
        final JSONObject hAndroidCommMap = new JSONObject();
        hAndroidCommMap.put("NOTIFICATION_TOKEN_ENCRYPTED", (Object)sRegistrationID);
        return hAndroidCommMap;
    }
    
    private JSONObject populateOldDevicePropsForReassignment(final DataObject dataObject, final Long resourceID) {
        final JSONObject jsonObject = new JSONObject();
        try {
            Row row = dataObject.getRow("ManagedUser");
            final Row deviceRow = dataObject.getRow("ManagedDevice");
            final Integer managedStatus = (Integer)deviceRow.get("MANAGED_STATUS");
            final Row managedDeviceExtnRow = dataObject.getRow("ManagedDeviceExtn");
            if (row != null) {
                final Row resRow = dataObject.getRow("Resource");
                jsonObject.put("UserName", resRow.get("NAME"));
                jsonObject.put("Email", row.get("EMAIL_ADDRESS"));
                jsonObject.put("Domain", resRow.get("DOMAIN_NETBIOS_NAME"));
                final Row dEnrollrequRow = dataObject.getRow("DeviceEnrollmentRequest");
                Long techUserID = (Long)dEnrollrequRow.get("USER_ID");
                if (techUserID == null) {
                    final Row extnRow = dataObject.getRow("ManagedDeviceExtn");
                    techUserID = (Long)extnRow.get("USER_ID");
                }
                jsonObject.put("TechUserID", (Object)techUserID);
                Iterator iterator = dataObject.getRows("CustomGroupMemberRel");
                final JSONArray groupIds = new JSONArray();
                while (iterator.hasNext()) {
                    final Row cgRow = iterator.next();
                    groupIds.put(cgRow.get("GROUP_RESOURCE_ID"));
                }
                if (groupIds.length() != 0) {
                    jsonObject.put("GroupIDs", (Object)groupIds);
                }
                iterator = dataObject.getRows("RecentProfileForGroup", new Criteria(Column.getColumn("RecentProfileForGroup", "MARKED_FOR_DELETE"), (Object)false, 0));
                final JSONObject profileProps = new JSONObject();
                final JSONObject profileCollectionMap = new JSONObject();
                final HashMap groupProfileList = new HashMap();
                while (iterator.hasNext()) {
                    row = iterator.next();
                    final Long profileID = (Long)row.get("PROFILE_ID");
                    final Long collectionID = (Long)row.get("COLLECTION_ID");
                    groupProfileList.put(profileID, collectionID);
                }
                final SelectQuery recentProfileForResQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("RecentProfileForResource"));
                recentProfileForResQuery.addSelectColumn(Column.getColumn("RecentProfileForResource", "*"));
                final Criteria resIdCri = new Criteria(Column.getColumn("RecentProfileForResource", "RESOURCE_ID"), (Object)resourceID, 0);
                final Criteria notMarkedForDelete = new Criteria(Column.getColumn("RecentProfileForResource", "MARKED_FOR_DELETE"), (Object)false, 0);
                recentProfileForResQuery.setCriteria(resIdCri.and(notMarkedForDelete));
                final DataObject dao = DataAccess.get(recentProfileForResQuery);
                iterator = dao.getRows("RecentProfileForResource");
                while (iterator.hasNext()) {
                    row = iterator.next();
                    final Long profileID2 = (Long)row.get("PROFILE_ID");
                    final Long collectionID2 = (Long)row.get("COLLECTION_ID");
                    if (groupProfileList.get(profileID2) == null || groupProfileList.get(profileID2) == null || !groupProfileList.get(profileID2).equals(collectionID2)) {
                        final DataObject resProfileUserDO = this.getProfileCollectionDOForResource(resourceID, profileID2, collectionID2);
                        final Row profileRow = resProfileUserDO.getRow("Profile", new Criteria(Column.getColumn("Profile", "PROFILE_ID"), (Object)profileID2, 0));
                        final Integer profileType = (Integer)profileRow.get("PROFILE_TYPE");
                        profileCollectionMap.put(profileID2.toString(), (Object)collectionID2);
                        final Row userRow = resProfileUserDO.getRow("AaaUser");
                        final JSONObject curProfileProps = new JSONObject();
                        curProfileProps.put("associatedByUser", userRow.get("USER_ID"));
                        curProfileProps.put("associatedByUserName", userRow.get("FIRST_NAME"));
                        curProfileProps.put("profileType", (Object)profileType);
                        profileProps.put(profileID2.toString(), (Object)curProfileProps);
                    }
                }
                jsonObject.put("ProfileIDs", (Object)profileCollectionMap);
                jsonObject.put("ProfileProps", (Object)profileProps);
                jsonObject.put("DocList", (Object)new DocMgmtDataHandler().getDocsAssociatedToDevice(resourceID));
                final int lostModeStatus = new LostModeDataHandler().getLostModeStatus(resourceID);
                Boolean isDevicehasLostMode = Boolean.FALSE;
                if (lostModeStatus == 1 || lostModeStatus == 2 || lostModeStatus == 3) {
                    isDevicehasLostMode = true;
                }
                if (isDevicehasLostMode) {
                    jsonObject.put("LostModeDetails", (Object)new LostModeDataHandler().getLostModeDeviceInfo(resourceID, 0, false));
                }
                jsonObject.put("ManagedStatus", (Object)managedStatus);
                if (managedDeviceExtnRow != null) {
                    jsonObject.put("DeviceName", managedDeviceExtnRow.get("NAME"));
                }
            }
        }
        catch (final Exception e) {
            MDMEnrollmentDeviceHandler.logger.log(Level.SEVERE, "Couldnt fetch the old device props, profiles data was not fetched. Certain profiles that were associated to the old device might not get assocatied to the device : ", e);
        }
        return jsonObject;
    }
    
    private DataObject getProfileCollectionDOForResource(final Long resourceId, final Long profileId, final Long collectionId) throws DataAccessException {
        final SelectQuery resourceProfileQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("ResourceToProfileHistory"));
        resourceProfileQuery.addJoin(new Join("ResourceToProfileHistory", "Profile", new String[] { "PROFILE_ID" }, new String[] { "PROFILE_ID" }, 2));
        resourceProfileQuery.addJoin(new Join("ResourceToProfileHistory", "AaaUser", new String[] { "ASSOCIATED_BY" }, new String[] { "USER_ID" }, 2));
        resourceProfileQuery.addSelectColumn(Column.getColumn("Profile", "PROFILE_ID"));
        resourceProfileQuery.addSelectColumn(Column.getColumn("Profile", "PROFILE_TYPE"));
        resourceProfileQuery.addSelectColumn(Column.getColumn("AaaUser", "USER_ID"));
        resourceProfileQuery.addSelectColumn(Column.getColumn("AaaUser", "FIRST_NAME"));
        final Criteria resCri = new Criteria(Column.getColumn("ResourceToProfileHistory", "RESOURCE_ID"), (Object)resourceId, 0);
        final Criteria profileCri = new Criteria(Column.getColumn("ResourceToProfileHistory", "PROFILE_ID"), (Object)profileId, 0);
        final Criteria collnCri = new Criteria(Column.getColumn("ResourceToProfileHistory", "COLLECTION_ID"), (Object)collectionId, 0);
        resourceProfileQuery.setCriteria(resCri.and(profileCri).and(collnCri));
        return DataAccess.get(resourceProfileQuery);
    }
    
    static {
        MDMEnrollmentDeviceHandler.logger = Logger.getLogger("MDMEnrollment");
        MDMEnrollmentDeviceHandler.sourceClass = "MDMEnrollmentDeviceHandler";
    }
}
