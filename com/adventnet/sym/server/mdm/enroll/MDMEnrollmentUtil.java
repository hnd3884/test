package com.adventnet.sym.server.mdm.enroll;

import java.util.Hashtable;
import com.me.devicemanagement.framework.server.exception.SyMException;
import com.me.mdm.server.enrollment.deprovision.DeprovisionRequest;
import com.adventnet.ds.query.DeleteQuery;
import com.adventnet.ds.query.DeleteQueryImpl;
import com.me.devicemanagement.framework.webclient.message.MessageProvider;
import com.adventnet.sym.server.mdm.ios.APNSImpl;
import com.adventnet.ds.query.SortColumn;
import com.adventnet.ds.query.DMDataSetWrapper;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.lang.WordUtils;
import com.me.mdm.server.metracker.MEMDMTrackParamManager;
import com.me.devicemanagement.framework.server.alerts.AlertsUtil;
import com.me.mdm.server.util.MDMFeatureParamsHandler;
import java.text.ParseException;
import java.util.TimeZone;
import java.text.SimpleDateFormat;
import java.util.Date;
import com.me.mdm.server.role.RBDAUtil;
import com.me.devicemanagement.framework.server.customer.CustomerInfoThreadLocal;
import com.adventnet.persistence.ReadOnlyPersistence;
import java.util.Map;
import com.me.mdm.server.ios.apns.APNsCertificateHandler;
import com.adventnet.sym.server.mdm.message.MDMMessageUtil;
import com.me.mdm.server.notification.NotificationHandler;
import com.me.mdm.server.notification.PushNotificationHandler;
import com.adventnet.persistence.DataAccess;
import com.me.mdm.server.alerts.MDMAlertConstants;
import org.json.JSONException;
import com.adventnet.persistence.DataAccessException;
import com.adventnet.i18n.I18N;
import org.json.JSONArray;
import com.adventnet.sym.server.mdm.command.DeviceMessage;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import com.me.mdm.server.license.MDMLicenseImplMSP;
import com.me.devicemanagement.framework.server.license.LicenseProvider;
import com.me.mdm.server.factory.MDMApiFactoryProvider;
import com.adventnet.sym.server.mdm.message.MDMMessageHandler;
import com.me.mdm.server.common.MDMEventConstant;
import java.net.URLDecoder;
import java.util.TreeMap;
import com.me.mdm.server.settings.MDMAgentSettingsHandler;
import com.me.mdm.server.enrollment.MDMEnrollmentDeviceHandler;
import com.adventnet.sym.server.mdm.command.DeviceCommandRepository;
import com.me.devicemanagement.framework.server.logger.DMSecurityLogger;
import com.me.devicemanagement.framework.server.util.DBUtil;
import java.util.HashMap;
import com.me.mdm.server.enrollment.MDMEnrollmentRequestHandler;
import com.adventnet.ds.query.UpdateQuery;
import java.util.Iterator;
import java.util.Set;
import com.me.mdm.core.enrollment.DeviceForEnrollmentHandler;
import com.me.mdm.server.onelinelogger.MDMOneLineLogger;
import java.util.List;
import com.adventnet.sym.server.mdm.util.MDMEventLogHandler;
import com.adventnet.ds.query.UpdateQueryImpl;
import com.adventnet.sym.server.mdm.command.DeviceInvCommandHandler;
import com.adventnet.sym.server.mdm.core.DeviceEvent;
import java.util.Collection;
import java.util.HashSet;
import com.adventnet.sym.server.mdm.core.EnrollmentRequestHandler;
import org.json.JSONObject;
import com.adventnet.sym.server.mdm.core.EREvent;
import java.util.ArrayList;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import java.util.logging.Level;
import java.util.Arrays;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.persistence.Row;
import com.adventnet.persistence.DataObject;
import com.adventnet.sym.server.mdm.terms.MDMTermsHandler;
import com.me.mdm.server.privacy.PrivacySettingListener;
import com.adventnet.sym.server.mdm.core.ManagedDeviceHandler;
import com.me.devicemanagement.framework.server.logger.SyMLogger;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.me.mdm.server.enrollment.EnrollmentSettingsHandler;
import com.me.devicemanagement.framework.server.customer.CustomerInfoUtil;
import com.me.idps.core.crud.DMDomainDataHandler;
import java.util.Properties;
import java.util.logging.Logger;

public class MDMEnrollmentUtil
{
    private static MDMEnrollmentUtil enrollmentUtil;
    String sourceClass;
    public Logger logger;
    public static final String SMS_TOOL_TIP_SHOWN_STATUS = "SMS_TOOL_TIP_SHOWN_STATUS";
    
    public MDMEnrollmentUtil() {
        this.sourceClass = "MDMEnrollmentUtil";
        this.logger = Logger.getLogger("MDMEnrollment");
    }
    
    public static MDMEnrollmentUtil getInstance() {
        if (MDMEnrollmentUtil.enrollmentUtil == null) {
            MDMEnrollmentUtil.enrollmentUtil = new MDMEnrollmentUtil();
        }
        return MDMEnrollmentUtil.enrollmentUtil;
    }
    
    public Properties buildEnrollmentProperties(final String sDomainName, final String sUserName, final Long groupID, final String sEmailID, final String sOwnedBy, final Long customerID, final boolean addToExistingUser, final String sPlatform, final boolean isSelfEnroll) {
        final Properties properties = new Properties();
        try {
            ((Hashtable<String, String>)properties).put("DOMAIN_NETBIOS_NAME", sDomainName);
            ((Hashtable<String, String>)properties).put("NAME", sUserName);
            ((Hashtable<String, String>)properties).put("EMAIL_ADDRESS", sEmailID);
            ((Hashtable<String, Long>)properties).put("CUSTOMER_ID", customerID);
            ((Hashtable<String, Integer>)properties).put("PLATFORM_TYPE", Integer.parseInt(sPlatform));
            ((Hashtable<String, Boolean>)properties).put("IS_SELF_ENROLLMENT", isSelfEnroll);
            if (groupID != null) {
                ((Hashtable<String, Long>)properties).put("GROUP_RESOURCE_ID", groupID);
            }
            if (sOwnedBy != null) {
                ((Hashtable<String, Integer>)properties).put("OWNED_BY", new Integer(sOwnedBy));
            }
            if (addToExistingUser) {
                ((Hashtable<String, Boolean>)properties).put("addToExistingUser", addToExistingUser);
            }
            Label_0283: {
                if (isSelfEnroll) {
                    ((Hashtable<String, Integer>)properties).put("AUTH_MODE", 2);
                }
                else {
                    if (!DMDomainDataHandler.getInstance().isADManagedDomain(sDomainName, customerID)) {
                        CustomerInfoUtil.getInstance();
                        if (!CustomerInfoUtil.isSAS()) {
                            ((Hashtable<String, Integer>)properties).put("AUTH_MODE", 1);
                            break Label_0283;
                        }
                    }
                    final int authMode = EnrollmentSettingsHandler.getInstance().getInvitationEnrollmentSettings(customerID).getInt("AUTH_MODE");
                    final Boolean isAppBasedEnrollment = Boolean.valueOf(MDMUtil.getSyMParameter("IsAppBasedEnrollmentForWindowsPhone"));
                    if (Integer.parseInt(sPlatform) == 3 && authMode == 3 && !isAppBasedEnrollment) {
                        CustomerInfoUtil.getInstance();
                        if (!CustomerInfoUtil.isSAS()) {
                            ((Hashtable<String, Integer>)properties).put("AUTH_MODE", 2);
                            break Label_0283;
                        }
                    }
                    ((Hashtable<String, Integer>)properties).put("AUTH_MODE", EnrollmentSettingsHandler.getInstance().getInvitationEnrollmentSettings(customerID).getInt("AUTH_MODE"));
                }
            }
            ((Hashtable<String, Boolean>)properties).put("sendInvitation", true);
        }
        catch (final Exception e) {
            SyMLogger.error(this.logger, this.sourceClass, "buildEnrollmentProperties", "Exception occured while building enrollmentproperties !!", (Throwable)e);
        }
        return properties;
    }
    
    public void updateDeviceOwnedBy(final Long enrollmentRequestID, final int ownedBy) {
        final String sourceMethod = "updateDeviceOwnedBy";
        try {
            final DataObject dataObject = this.getManagedDeviceDO(enrollmentRequestID);
            Long managedDeviceID = null;
            if (!dataObject.isEmpty()) {
                if (dataObject.containsTable("ManagedDevice")) {
                    managedDeviceID = (Long)dataObject.getFirstValue("ManagedDevice", "RESOURCE_ID");
                    final int platformType = (int)dataObject.getFirstValue("ManagedDevice", "PLATFORM_TYPE");
                    if (managedDeviceID != null) {
                        final Properties properties = new Properties();
                        ((Hashtable<String, Long>)properties).put("RESOURCE_ID", managedDeviceID);
                        ((Hashtable<String, Integer>)properties).put("OWNED_BY", new Integer(ownedBy));
                        ((Hashtable<String, Integer>)properties).put("PLATFORM_TYPE", new Integer(platformType));
                        ManagedDeviceHandler.getInstance().updateManagedDeviceDetails(properties);
                    }
                }
                if (dataObject.containsTable("DeviceEnrollmentRequest")) {
                    final Row requestRow = dataObject.getFirstRow("DeviceEnrollmentRequest");
                    requestRow.set("OWNED_BY", (Object)new Integer(ownedBy));
                    dataObject.updateRow(requestRow);
                    MDMUtil.getPersistenceLite().update(dataObject);
                }
                if (managedDeviceID != null) {
                    PrivacySettingListener.getInstance().invokeOwnedByChange(managedDeviceID, ownedBy);
                }
            }
            MDMTermsHandler.getInstance().syncTermsOnOwnedByUpdate(enrollmentRequestID);
        }
        catch (final Exception exp) {
            SyMLogger.error(this.logger, this.sourceClass, sourceMethod, "Exception occured while updating device owner !!", (Throwable)exp);
        }
    }
    
    private DataObject getManagedDeviceDO(final Long enrollmentRequestID) {
        final String sourceMethod = "getManagedDeviceDO";
        DataObject dataObject = null;
        try {
            final Criteria criteria = new Criteria(Column.getColumn("DeviceEnrollmentRequest", "ENROLLMENT_REQUEST_ID"), (Object)enrollmentRequestID, 0);
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("DeviceEnrollmentRequest"));
            final Join deviceRequestRelJoin = new Join("DeviceEnrollmentRequest", "EnrollmentRequestToDevice", new String[] { "ENROLLMENT_REQUEST_ID" }, new String[] { "ENROLLMENT_REQUEST_ID" }, 1);
            final Join managedDeviceJoin = new Join("EnrollmentRequestToDevice", "ManagedDevice", new String[] { "MANAGED_DEVICE_ID" }, new String[] { "RESOURCE_ID" }, 1);
            final Join extnJoin = new Join("ManagedDevice", "ManagedDeviceExtn", new String[] { "RESOURCE_ID" }, new String[] { "MANAGED_DEVICE_ID" }, 2);
            selectQuery.addJoin(deviceRequestRelJoin);
            selectQuery.addJoin(managedDeviceJoin);
            selectQuery.addJoin(extnJoin);
            selectQuery.addSelectColumn(Column.getColumn("DeviceEnrollmentRequest", "*"));
            selectQuery.addSelectColumn(Column.getColumn("EnrollmentRequestToDevice", "*"));
            selectQuery.addSelectColumn(Column.getColumn("ManagedDevice", "*"));
            selectQuery.addSelectColumn(Column.getColumn("ManagedDeviceExtn", "MANAGED_DEVICE_ID", "MANAGEDDEVICEEXTN.MANAGED_DEVICE_ID"));
            selectQuery.addSelectColumn(Column.getColumn("ManagedDeviceExtn", "NAME", "MANAGEDDEVICEEXTN.NAME"));
            selectQuery.setCriteria(criteria);
            dataObject = MDMUtil.getPersistence().get(selectQuery);
        }
        catch (final Exception exp) {
            SyMLogger.error(this.logger, this.sourceClass, sourceMethod, "Exception occurred while fetching Managed Device DataObject. ", (Throwable)exp);
        }
        return dataObject;
    }
    
    public Boolean removeDevice(final String enrollmentRequestIDs, final String userName, final Long customerId) throws Exception {
        Boolean successfullyRemoved = false;
        if (enrollmentRequestIDs != null && !enrollmentRequestIDs.isEmpty()) {
            try {
                String[] sArrDeviceID = Arrays.stream(enrollmentRequestIDs.split(",")).filter(s -> s != null && s.length() > 0).toArray(String[]::new);
                sArrDeviceID = Arrays.stream(sArrDeviceID).distinct().toArray(String[]::new);
                int nextStart;
                for (int startRange = 0, valueCount = 500, endRange = Math.min(500, sArrDeviceID.length); startRange < endRange && endRange <= sArrDeviceID.length; startRange = ((nextStart > sArrDeviceID.length) ? endRange : nextStart), endRange = Math.min(endRange + valueCount, sArrDeviceID.length)) {
                    this.sendRemoveDeviceCommand(Arrays.copyOfRange(sArrDeviceID, startRange, endRange), userName, customerId);
                    nextStart = startRange + valueCount;
                }
                successfullyRemoved = true;
            }
            catch (final Exception ex) {
                this.logger.log(Level.SEVERE, "Exception while remove device", ex);
                successfullyRemoved = false;
            }
        }
        return successfullyRemoved;
    }
    
    private DataObject getRemoveDeviceEREventObj(final Long[] enrollmentRequestIDs) {
        DataObject erEventDO = null;
        try {
            final SelectQuery sQuery = (SelectQuery)new SelectQueryImpl(new Table("DeviceEnrollmentRequest"));
            final Join managedUserJoin = new Join("DeviceEnrollmentRequest", "ManagedUser", new String[] { "MANAGED_USER_ID" }, new String[] { "MANAGED_USER_ID" }, 1);
            final Join requestToTinyUrlJoin = new Join("DeviceEnrollmentRequest", "EnrollmentRequestToTinyUrl", new String[] { "ENROLLMENT_REQUEST_ID" }, new String[] { "ENROLLMENT_REQUEST_ID" }, 1);
            final Join tinyUrlJoin = new Join("EnrollmentRequestToTinyUrl", "TinyUrl", new String[] { "TINY_URL_ID" }, new String[] { "TINY_URL_ID" }, 1);
            final Join userResourceJoin = new Join("ManagedUser", "Resource", new String[] { "MANAGED_USER_ID" }, new String[] { "RESOURCE_ID" }, 2);
            sQuery.addJoin(managedUserJoin);
            sQuery.addJoin(userResourceJoin);
            sQuery.addJoin(requestToTinyUrlJoin);
            sQuery.addJoin(tinyUrlJoin);
            sQuery.addSelectColumn(Column.getColumn("EnrollmentRequestToTinyUrl", "ENROLLMENT_REQUEST_ID"));
            sQuery.addSelectColumn(Column.getColumn("EnrollmentRequestToTinyUrl", "TINY_URL_ID"));
            sQuery.addSelectColumn(Column.getColumn("TinyUrl", "TINY_URL_ID"));
            sQuery.addSelectColumn(Column.getColumn("TinyUrl", "TINY_URL"));
            sQuery.addSelectColumn(Column.getColumn("ManagedUser", "MANAGED_USER_ID"));
            sQuery.addSelectColumn(Column.getColumn("ManagedUser", "EMAIL_ADDRESS"));
            sQuery.addSelectColumn(Column.getColumn("DeviceEnrollmentRequest", "ENROLLMENT_REQUEST_ID"));
            sQuery.addSelectColumn(Column.getColumn("DeviceEnrollmentRequest", "MANAGED_USER_ID"));
            sQuery.addSelectColumn(Column.getColumn("Resource", "RESOURCE_ID"));
            sQuery.addSelectColumn(Column.getColumn("Resource", "NAME"));
            final Criteria eridCriteria = new Criteria(Column.getColumn("DeviceEnrollmentRequest", "ENROLLMENT_REQUEST_ID"), (Object)enrollmentRequestIDs, 8);
            final Criteria userNotInTrashCriteria = new Criteria(Column.getColumn("ManagedUser", "STATUS"), (Object)11, 1);
            sQuery.setCriteria(eridCriteria.and(userNotInTrashCriteria));
            erEventDO = SyMUtil.getPersistence().get(sQuery);
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, "Exception in getRemoveDeviceEREventObj", ex);
        }
        return erEventDO;
    }
    
    private DataObject getRemoveDeviceDevEventObj(final Long[] enrollmentRequestIDs) {
        DataObject deviceEventDO = null;
        try {
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("DeviceEnrollmentRequest"));
            final Join deviceRequestRelJoin = new Join("DeviceEnrollmentRequest", "EnrollmentRequestToDevice", new String[] { "ENROLLMENT_REQUEST_ID" }, new String[] { "ENROLLMENT_REQUEST_ID" }, 1);
            final Join managedDeviceJoin = new Join("EnrollmentRequestToDevice", "ManagedDevice", new String[] { "MANAGED_DEVICE_ID" }, new String[] { "RESOURCE_ID" }, 1);
            final Join extnJoin = new Join("ManagedDevice", "ManagedDeviceExtn", new String[] { "RESOURCE_ID" }, new String[] { "MANAGED_DEVICE_ID" }, 1);
            final Join enrollToReqJoin = new Join("DeviceEnrollmentRequest", "DeviceEnrollmentToRequest", new String[] { "ENROLLMENT_REQUEST_ID" }, new String[] { "ENROLLMENT_REQUEST_ID" }, 1);
            selectQuery.addJoin(deviceRequestRelJoin);
            selectQuery.addJoin(managedDeviceJoin);
            selectQuery.addJoin(extnJoin);
            selectQuery.addJoin(enrollToReqJoin);
            selectQuery.addSelectColumn(Column.getColumn("DeviceEnrollmentRequest", "*"));
            selectQuery.addSelectColumn(Column.getColumn("EnrollmentRequestToDevice", "*"));
            selectQuery.addSelectColumn(Column.getColumn("ManagedDevice", "*"));
            selectQuery.addSelectColumn(Column.getColumn("ManagedDeviceExtn", "MANAGED_DEVICE_ID", "MANAGEDDEVICEEXTN.MANAGED_DEVICE_ID"));
            selectQuery.addSelectColumn(Column.getColumn("ManagedDeviceExtn", "NAME", "MANAGEDDEVICEEXTN.NAME"));
            selectQuery.addSelectColumn(Column.getColumn("DeviceEnrollmentToRequest", "*"));
            final Criteria eridCriteria = new Criteria(Column.getColumn("DeviceEnrollmentRequest", "ENROLLMENT_REQUEST_ID"), (Object)enrollmentRequestIDs, 8);
            final Criteria statusCriteria = new Criteria(Column.getColumn("ManagedDevice", "MANAGED_STATUS"), (Object)2, 1);
            selectQuery.setCriteria(eridCriteria.and(statusCriteria));
            deviceEventDO = MDMUtil.getPersistence().get(selectQuery);
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, "Exception in getRemoveDeviceDevEventObj", ex);
        }
        return deviceEventDO;
    }
    
    private void sendRemoveDeviceCommand(final String[] sArrDeviceID, final String userName, final Long customerId) throws Exception {
        final String sourceMethod = "sendRemoveDeviceCommand";
        final Long[] enrollmentRequestIDs = new Long[sArrDeviceID.length];
        for (int i = 0; i < sArrDeviceID.length; ++i) {
            enrollmentRequestIDs[i] = Long.parseLong(sArrDeviceID[i]);
        }
        final ArrayList userNameList = new ArrayList();
        final DataObject erEventDO = this.getRemoveDeviceEREventObj(enrollmentRequestIDs);
        for (int erIndex = 0; erIndex < sArrDeviceID.length; ++erIndex) {
            final EREvent erEvent = new EREvent(sArrDeviceID[erIndex]);
            final JSONObject erEventResourceJSON = new JSONObject();
            String tinyUrl = null;
            String mailAddress = null;
            final Row tinyURLRow = erEventDO.getRow("TinyUrl", new Criteria(Column.getColumn("EnrollmentRequestToTinyUrl", "ENROLLMENT_REQUEST_ID"), (Object)Long.valueOf(erEvent.enrollmentRequestId), 0), new Join("EnrollmentRequestToTinyUrl", "TinyUrl", new String[] { "TINY_URL_ID" }, new String[] { "TINY_URL_ID" }, 2));
            if (tinyURLRow != null) {
                tinyUrl = (String)tinyURLRow.get("TINY_URL");
            }
            erEventResourceJSON.put("TINY_URL", (Object)tinyUrl);
            final Row userRow = erEventDO.getRow("ManagedUser", new Criteria(Column.getColumn("DeviceEnrollmentRequest", "ENROLLMENT_REQUEST_ID"), (Object)Long.valueOf(erEvent.enrollmentRequestId), 0), new Join("DeviceEnrollmentRequest", "ManagedUser", new String[] { "MANAGED_USER_ID" }, new String[] { "MANAGED_USER_ID" }, 2));
            if (userRow != null) {
                mailAddress = (String)userRow.get("EMAIL_ADDRESS");
            }
            erEventResourceJSON.put("EMAIL_ADDRESS", (Object)mailAddress);
            erEvent.resourceJSONString = erEventResourceJSON.toString();
            EnrollmentRequestHandler.getInstance().invokeEnrollmentRequestListeners(erEvent, 1);
            EnrollmentRequestHandler.getInstance().invokeEnrollmentRequestListeners(erEvent, 4);
            final Long userId = (Long)erEventDO.getValue("DeviceEnrollmentRequest", "MANAGED_USER_ID", new Criteria(new Column("DeviceEnrollmentRequest", "ENROLLMENT_REQUEST_ID"), (Object)erEvent.enrollmentRequestId, 0));
            if (userId != null) {
                final String reqRemarks = erEventDO.getValue("Resource", "NAME", new Criteria(new Column("Resource", "RESOURCE_ID"), (Object)userId, 0)) + "(" + erEventDO.getValue("ManagedUser", "EMAIL_ADDRESS", new Criteria(new Column("ManagedUser", "MANAGED_USER_ID"), (Object)userId, 0)) + ")";
                userNameList.add(reqRemarks);
            }
        }
        final Long loggedOnUserID = MDMUtil.getInstance().getCurrentlyLoggedOnUserID();
        final DataObject deviceEventDO = this.getRemoveDeviceDevEventObj(enrollmentRequestIDs);
        final Set<Long> erReqIDSet = new HashSet<Long>();
        final List<Long> stillInServerDeviceIDs = new ArrayList<Long>();
        final List<Long> removeDeviceIDs = new ArrayList<Long>();
        final List<Long> managedDeviceIDList = new ArrayList<Long>();
        final ArrayList deviceNameList = new ArrayList();
        final List<Long> onlyEnrollRequestPresent = new ArrayList<Long>();
        onlyEnrollRequestPresent.addAll(Arrays.asList(enrollmentRequestIDs));
        final Iterator deviceIter = deviceEventDO.getRows("ManagedDevice");
        while (deviceIter.hasNext()) {
            final Row managedDeviceRow = deviceIter.next();
            Long managedDeviceId = null;
            Long deviceForEnrollId = null;
            Integer deviceStatus = null;
            boolean wipecompleted = true;
            final JSONObject deviceEventResourceJSON = new JSONObject();
            managedDeviceId = (Long)managedDeviceRow.get("RESOURCE_ID");
            if (managedDeviceIDList.contains(managedDeviceId)) {
                continue;
            }
            managedDeviceIDList.add(managedDeviceId);
            final String sDeviceName = (String)deviceEventDO.getValue("ManagedDeviceExtn", "NAME", new Criteria(new Column("ManagedDeviceExtn", "MANAGED_DEVICE_ID"), (Object)managedDeviceId, 0));
            deviceNameList.add(sDeviceName);
            final DeviceEvent deviceEvent = new DeviceEvent(managedDeviceId, customerId);
            deviceEvent.udid = (String)managedDeviceRow.get("UDID");
            deviceEvent.platformType = (int)managedDeviceRow.get("PLATFORM_TYPE");
            deviceEvent.enrollmentRequestId = (Long)deviceEventDO.getValue("EnrollmentRequestToDevice", "ENROLLMENT_REQUEST_ID", new Criteria(new Column("EnrollmentRequestToDevice", "MANAGED_DEVICE_ID"), (Object)managedDeviceId, 0));
            deviceStatus = (int)managedDeviceRow.get("MANAGED_STATUS");
            deviceEventResourceJSON.put("MANAGED_STATUS", (Object)deviceStatus);
            deviceForEnrollId = (Long)deviceEventDO.getValue("DeviceEnrollmentToRequest", "ENROLLMENT_DEVICE_ID", new Criteria(Column.getColumn("DeviceEnrollmentToRequest", "ENROLLMENT_REQUEST_ID"), (Object)deviceEvent.enrollmentRequestId, 0));
            deviceEventResourceJSON.put("ENROLLMENT_DEVICE_ID", (Object)deviceForEnrollId);
            deviceEvent.resourceJSON = deviceEventResourceJSON;
            ManagedDeviceHandler.getInstance().invokeDeviceListeners(deviceEvent, 2);
            if (deviceEvent.platformType == 1) {
                this.setSerialNumberInDeviceEvent(deviceEvent);
            }
            final JSONObject json = ManagedDeviceHandler.getInstance().getDeprovisiondetails(deviceEvent.resourceID);
            boolean wipepending = false;
            if (json != null) {
                wipepending = json.optBoolean("WIPE_PENDING", false);
            }
            if (deviceStatus == 2 || deviceStatus == 5 || deviceStatus == 6 || (deviceStatus == 9 && wipepending) || (deviceStatus == 11 && wipepending) || (deviceStatus == 10 && wipepending)) {
                stillInServerDeviceIDs.add(managedDeviceId);
                wipecompleted = false;
                if (deviceStatus == 6) {
                    DeviceInvCommandHandler.getInstance().sendCorporateWipeCommandToDevice(deviceEvent, "CorporateWipe", loggedOnUserID);
                }
            }
            else {
                removeDeviceIDs.add(managedDeviceId);
            }
            ManagedDeviceHandler.getInstance().invokeDeviceListeners(deviceEvent, 4);
            if (deviceStatus != 2 && deviceStatus != 5 && deviceStatus != 6 && ((deviceStatus == 9 && wipecompleted) || (deviceStatus == 11 && wipecompleted) || (deviceStatus == 10 && wipecompleted) || deviceStatus == 4 || deviceStatus == 1)) {
                erReqIDSet.add(deviceEvent.enrollmentRequestId);
            }
            onlyEnrollRequestPresent.remove(deviceEvent.enrollmentRequestId);
        }
        if (onlyEnrollRequestPresent.size() > 0) {
            erReqIDSet.addAll(onlyEnrollRequestPresent);
        }
        if (stillInServerDeviceIDs.size() > 0) {
            final Criteria criteria = new Criteria(new Column("ManagedDevice", "RESOURCE_ID"), (Object)stillInServerDeviceIDs.toArray(), 8);
            final UpdateQuery query = (UpdateQuery)new UpdateQueryImpl("ManagedDevice");
            query.setUpdateColumn("MANAGED_STATUS", (Object)7);
            query.setUpdateColumn("UNREGISTERED_TIME", (Object)System.currentTimeMillis());
            query.setCriteria(criteria);
            MDMUtil.getPersistenceLite().update(query);
        }
        if (removeDeviceIDs.size() > 0) {
            MDMUtil.getPersistenceLite().delete(new Criteria(new Column("MdCommandsToDevice", "RESOURCE_ID"), (Object)removeDeviceIDs.toArray(), 8));
            MDMUtil.getPersistenceLite().delete(new Criteria(new Column("CommandHistory", "RESOURCE_ID"), (Object)removeDeviceIDs.toArray(), 8));
            MDMUtil.getPersistenceLite().delete(new Criteria(new Column("MDMCollnToResErrorCode", "RESOURCE_ID"), (Object)removeDeviceIDs.toArray(), 8));
            MDMUtil.getPersistenceLite().delete(new Criteria(new Column("ResourceToProfileHistory", "RESOURCE_ID"), (Object)removeDeviceIDs.toArray(), 8));
            MDMUtil.getPersistenceLite().delete(new Criteria(new Column("RecentProfileForResource", "RESOURCE_ID"), (Object)removeDeviceIDs.toArray(), 8));
            MDMUtil.getPersistenceLite().delete(new Criteria(new Column("ManagedDevice", "RESOURCE_ID"), (Object)removeDeviceIDs.toArray(), 8));
        }
        final List<Long> allManagedDeviceIds = new ArrayList<Long>();
        allManagedDeviceIds.addAll(stillInServerDeviceIDs);
        allManagedDeviceIds.addAll(removeDeviceIDs);
        final String deviceEventLogRemarks = "dc.mdm.actionlog.enrollment.unmanaged";
        MDMEventLogHandler.getInstance().addEvent(2001, allManagedDeviceIds, userName, deviceEventLogRemarks, deviceNameList, customerId, System.currentTimeMillis());
        final String requestEventLogRemarks = "mdm.actionlog.enrollment.remove_request";
        MDMEventLogHandler.getInstance().addEvent(2001, userName, requestEventLogRemarks, userNameList, customerId, System.currentTimeMillis());
        final org.json.simple.JSONObject logJSON = new org.json.simple.JSONObject();
        logJSON.put((Object)"REMARKS", (Object)"deprovision-success");
        logJSON.put((Object)"MANAGED_DEVICE_ID", (Object)allManagedDeviceIds);
        logJSON.put((Object)"DISPLAY_NAME", (Object)userNameList);
        MDMOneLineLogger.log(Level.INFO, "DEVICE_DEPROVISIONED", logJSON);
        final List<Long> dfeDeviceIDList = new ArrayList<Long>();
        final DataObject dataObject = this.getDFEDetailsforERIDs(enrollmentRequestIDs);
        if (!dataObject.isEmpty()) {
            final Iterator iterator = dataObject.getRows("DeviceEnrollmentToRequest");
            while (iterator.hasNext()) {
                final Row deviceEnrollToReqRow = iterator.next();
                dfeDeviceIDList.add((Long)deviceEnrollToReqRow.get("ENROLLMENT_DEVICE_ID"));
            }
        }
        final Criteria dfeCriteria = new Criteria(new Column("DeviceForEnrollment", "ENROLLMENT_DEVICE_ID"), (Object)dfeDeviceIDList.toArray(), 8);
        new DeviceForEnrollmentHandler().deleteOnEnrollment(dfeCriteria);
        if (erReqIDSet.size() > 0) {
            this.logger.log(Level.INFO, "Calling removeNotExistingEnrollmentRequest from sendRemoveDeviceCommand: {0}", erReqIDSet);
            this.removeNotExistingEnrollmentRequest(erReqIDSet);
        }
    }
    
    public boolean haveEnrollmentRequest(final Long customerId) throws Exception {
        return MDMEnrollmentRequestHandler.getInstance().getAddedEnrollmentRequestCountForCustomer(customerId, null) > 0;
    }
    
    public void updateAndroidResourceDetails(final HashMap<String, String> hsEntrollValues) {
        this.logger.log(Level.INFO, "Inside updateAndroidResourceDetails()..");
        try {
            JSONObject hsDeviceInfoJson = null;
            if (hsEntrollValues.containsKey("DeviceInfo")) {
                hsDeviceInfoJson = new JSONObject((String)hsEntrollValues.get("DeviceInfo"));
            }
            final String sDeviceName = hsEntrollValues.get("DeviceName");
            final String sUDID = hsEntrollValues.get("UDID");
            final String sEmail = hsEntrollValues.get("EmailAddress");
            final String sRegistrationID = hsEntrollValues.get("RegistrationID");
            final Long reqId = Long.parseLong(hsEntrollValues.get("EnrollmentReqID"));
            final Long customerId = Long.parseLong(hsEntrollValues.get("CUSTOMER_ID"));
            final String agentVersion = hsEntrollValues.get("AgentVersion");
            final String versionCode = hsEntrollValues.get("AgentVersionCode");
            final String isMigratedString = hsEntrollValues.getOrDefault("IsMigrated", Boolean.FALSE.toString());
            final boolean isMigrated = Boolean.valueOf(isMigratedString);
            final Long agentVersionCode = (versionCode != null) ? Long.parseLong(versionCode) : -1L;
            final Long userId = (Long)DBUtil.getValueFromDB("DeviceEnrollmentRequest", "ENROLLMENT_REQUEST_ID", (Object)reqId, "MANAGED_USER_ID");
            String imei = hsEntrollValues.get("IMEI");
            String serialNumber = hsEntrollValues.get("SerialNumber");
            String easID = hsEntrollValues.get("EASDeviceIdentifier");
            if (hsDeviceInfoJson != null) {
                imei = ((imei != null) ? imei : hsDeviceInfoJson.optString("IMEI", (String)null));
                serialNumber = ((serialNumber != null) ? serialNumber : hsDeviceInfoJson.optString("SerialNumber", (String)null));
                easID = ((easID != null) ? easID : hsDeviceInfoJson.optString("EASDeviceIdentifier", (String)null));
            }
            this.logger.log(Level.INFO, "updateAndroidResourceDetails : sDeviceName : {0}", sDeviceName);
            this.logger.log(Level.INFO, "updateAndroidResourceDetails : sUDID : {0}", sUDID);
            DMSecurityLogger.info(this.logger, MDMEnrollmentUtil.class.getName(), "updateAndroidResourceDetails", "updateAndroidResourceDetails : {0}", (Object)("email : " + sEmail));
            String remarks = "dc.mdm.db.agent.enroll.agent_enroll_finished";
            if (hsEntrollValues.containsKey("Remarks")) {
                remarks = hsEntrollValues.get("Remarks");
            }
            final Integer managedStatus = 2;
            final Integer enrollmentType = (Integer)DBUtil.getValueFromDB("DeviceEnrollmentRequest", "ENROLLMENT_REQUEST_ID", (Object)reqId, "ENROLLMENT_TYPE");
            final JSONObject properties = new JSONObject();
            properties.put("CUSTOMER_ID", (Object)customerId);
            properties.put("UDID", (Object)sUDID);
            properties.put("NAME", (Object)sDeviceName);
            properties.put("DOMAIN_NETBIOS_NAME", (Object)"MDM");
            properties.put("ENROLLMENT_REQUEST_ID", (Object)reqId);
            properties.put("MANAGED_USER_ID", (Object)userId);
            properties.put("MANAGED_STATUS", (Object)managedStatus);
            properties.put("REMARKS", (Object)remarks);
            properties.put("AGENT_TYPE", Integer.parseInt(hsEntrollValues.get("AGENT_TYPE")));
            properties.put("PLATFORM_TYPE", Integer.parseInt(hsEntrollValues.get("PLATFORM_TYPE")));
            properties.put("REGISTRATION_ID", (Object)sRegistrationID);
            properties.put("AGENT_VERSION", (Object)agentVersion);
            properties.put("AGENT_VERSION_CODE", (Object)agentVersionCode);
            properties.put("SERIAL_NUMBER", (Object)serialNumber);
            properties.put("IMEI", (Object)imei);
            properties.put("EAS_DEVICE_IDENTIFIER", (Object)easID);
            properties.put("IsMigrated", isMigrated);
            if (hsDeviceInfoJson != null) {
                try {
                    final JSONObject modelAndDeviceInfo = new JSONObject();
                    modelAndDeviceInfo.put("MODEL", (Object)String.valueOf(hsDeviceInfoJson.get("Model")));
                    modelAndDeviceInfo.put("MODEL_NAME", (Object)String.valueOf(hsDeviceInfoJson.get("ModelName")));
                    modelAndDeviceInfo.put("MODEL_TYPE", (Object)String.valueOf(hsDeviceInfoJson.get("DeviceType")));
                    modelAndDeviceInfo.put("PRODUCT_NAME", (Object)String.valueOf(hsDeviceInfoJson.get("ProductName")));
                    modelAndDeviceInfo.put("MANUFACTURER", (Object)hsDeviceInfoJson.optString("Manufacture", "--"));
                    modelAndDeviceInfo.put("MODEL_CODE", hsDeviceInfoJson.get("ModelCode"));
                    modelAndDeviceInfo.put("OS_VERSION", (Object)String.valueOf(hsDeviceInfoJson.get("OSVersion")));
                    modelAndDeviceInfo.put("EAS_DEVICE_IDENTIFIER", (Object)hsDeviceInfoJson.optString("EASDeviceIdentifier", (String)null));
                    modelAndDeviceInfo.put("IS_SUPERVISED", (Object)hsDeviceInfoJson.optString("IsDeviceOwner", (String)null));
                    modelAndDeviceInfo.put("IS_PROFILEOWNER", (Object)hsDeviceInfoJson.optString("IsProfileOwner", (String)null));
                    modelAndDeviceInfo.put("GOOGLE_PLAY_SERVICE_ID", (Object)hsDeviceInfoJson.optString("GSFAndroidID", (String)null));
                    modelAndDeviceInfo.put("SERIAL_NUMBER", (Object)serialNumber);
                    modelAndDeviceInfo.put("IMEI", (Object)imei);
                    properties.put("MdModelInfo", (Object)modelAndDeviceInfo);
                }
                catch (final Exception e) {
                    this.logger.log(Level.WARNING, "{0}| {1} | Not updating MDMODELINFO due to missing key :{2}", new Object[] { sUDID, serialNumber, e.getMessage() });
                }
            }
            this.logger.log(Level.INFO, "updateAndroidResourceDetails().. Props populated: {0}", properties.toString());
            final Long resourceID = ManagedDeviceHandler.getInstance().getResourceIDFromUDID(sUDID);
            DeviceCommandRepository.getInstance().removeAllCommandsForResource(resourceID, sUDID);
            MDMEnrollmentDeviceHandler.getInstance(enrollmentType).enrollDevice(properties);
            ManagedDeviceHandler.getInstance().updateDeviceForEnrollmentDetails(resourceID, serialNumber, imei);
            MDMEnrollmentRequestHandler.getInstance().updateDeviceRequestStatus(reqId, 3, 2);
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, "Exception while enrolling andoird device", ex);
        }
    }
    
    public void updateChromeResourceDetails(final HashMap<String, String> hsEntrollValues) {
        this.logger.log(Level.INFO, "Inside updateChromeResourceDetails()..");
        try {
            JSONObject hsDeviceInfoJson = null;
            if (hsEntrollValues.containsKey("DeviceInfo")) {
                hsDeviceInfoJson = new JSONObject((String)hsEntrollValues.get("DeviceInfo"));
            }
            final String sDeviceName = hsEntrollValues.get("DeviceName");
            final String sUDID = hsEntrollValues.get("UDID");
            final String sEmail = hsEntrollValues.get("EmailAddress");
            final String sRegistrationID = "in_server_wakeup";
            final Long reqId = Long.parseLong(hsEntrollValues.get("EnrollmentReqID"));
            final Long customerId = Long.parseLong(hsEntrollValues.get("CUSTOMER_ID"));
            final String agentVersion = hsEntrollValues.get("AgentVersion");
            final String versionCode = hsEntrollValues.get("AgentVersionCode");
            final Long agentVersionCode = (versionCode != null) ? Long.parseLong(versionCode) : -1L;
            final Long userId = (Long)DBUtil.getValueFromDB("DeviceEnrollmentRequest", "ENROLLMENT_REQUEST_ID", (Object)reqId, "MANAGED_USER_ID");
            String imei = hsEntrollValues.get("IMEI");
            String serialNumber = hsEntrollValues.get("SerialNumber");
            String easID = hsEntrollValues.get("EASDeviceIdentifier");
            if (hsDeviceInfoJson != null) {
                imei = ((imei != null) ? imei : hsDeviceInfoJson.optString("IMEI", (String)null));
                serialNumber = ((serialNumber != null) ? serialNumber : hsDeviceInfoJson.optString("SerialNumber", (String)null));
                easID = ((easID != null) ? easID : hsDeviceInfoJson.optString("EASDeviceIdentifier", (String)null));
            }
            this.logger.log(Level.INFO, "updateChromeResourceDetails : sDeviceName : {0}", sDeviceName);
            this.logger.log(Level.INFO, "updateChromeResourceDetails : sUDID : {0}", sUDID);
            this.logger.log(Level.INFO, "updateChromeResourceDetails : email : {0}", sEmail);
            String remarks = "dc.mdm.db.agent.enroll.agent_enroll_finished";
            if (hsEntrollValues.containsKey("Remarks")) {
                remarks = hsEntrollValues.get("Remarks");
            }
            final Integer managedStatus = 2;
            final Integer enrollmentType = (Integer)DBUtil.getValueFromDB("DeviceEnrollmentRequest", "ENROLLMENT_REQUEST_ID", (Object)reqId, "ENROLLMENT_TYPE");
            final JSONObject properties = new JSONObject();
            properties.put("CUSTOMER_ID", (Object)customerId);
            properties.put("UDID", (Object)sUDID);
            properties.put("NAME", (Object)sDeviceName);
            properties.put("DOMAIN_NETBIOS_NAME", (Object)"MDM");
            properties.put("RESOURCE_TYPE", 121);
            properties.put("ENROLLMENT_REQUEST_ID", (Object)reqId);
            properties.put("MANAGED_USER_ID", (Object)userId);
            properties.put("MANAGED_STATUS", (Object)managedStatus);
            properties.put("REMARKS", (Object)remarks);
            properties.put("AGENT_TYPE", Integer.parseInt(hsEntrollValues.get("AGENT_TYPE")));
            properties.put("PLATFORM_TYPE", Integer.parseInt(hsEntrollValues.get("PLATFORM_TYPE")));
            properties.put("REGISTRATION_ID", (Object)sRegistrationID);
            properties.put("AGENT_VERSION", (Object)agentVersion);
            properties.put("AGENT_VERSION_CODE", (Object)agentVersionCode);
            properties.put("SERIAL_NUMBER", (Object)serialNumber);
            properties.put("IMEI", (Object)imei);
            properties.put("EAS_DEVICE_IDENTIFIER", (Object)easID);
            if (hsDeviceInfoJson != null) {
                try {
                    final JSONObject modelAndDeviceInfo = new JSONObject();
                    modelAndDeviceInfo.put("MODEL", (Object)String.valueOf(hsDeviceInfoJson.get("Model")));
                    modelAndDeviceInfo.put("MODEL_NAME", (Object)String.valueOf(hsDeviceInfoJson.get("ModelName")));
                    modelAndDeviceInfo.put("MODEL_TYPE", (Object)String.valueOf(hsDeviceInfoJson.get("DeviceType")));
                    modelAndDeviceInfo.put("PRODUCT_NAME", (Object)String.valueOf(hsDeviceInfoJson.get("ProductName")));
                    modelAndDeviceInfo.put("OS_VERSION", (Object)String.valueOf(hsDeviceInfoJson.get("OSVersion")));
                    modelAndDeviceInfo.put("EAS_DEVICE_IDENTIFIER", (Object)hsDeviceInfoJson.optString("EASDeviceIdentifier", (String)null));
                    modelAndDeviceInfo.put("IS_SUPERVISED", (Object)hsDeviceInfoJson.optString("IsDeviceOwner", (String)null));
                    modelAndDeviceInfo.put("IS_PROFILEOWNER", (Object)hsDeviceInfoJson.optString("IsProfileOwner", (String)null));
                    modelAndDeviceInfo.put("GOOGLE_PLAY_SERVICE_ID", (Object)hsDeviceInfoJson.optString("GSFAndroidID", (String)null));
                    modelAndDeviceInfo.put("SERIAL_NUMBER", (Object)serialNumber);
                    modelAndDeviceInfo.put("IMEI", (Object)imei);
                    properties.put("MdModelInfo", (Object)modelAndDeviceInfo);
                }
                catch (final Exception ex2) {}
            }
            this.logger.log(Level.INFO, "updateChromeResourceDetails().. Props populated: {0}", properties.toString());
            final Long resourceID = ManagedDeviceHandler.getInstance().getResourceIDFromUDID(sUDID);
            DeviceCommandRepository.getInstance().removeAllCommandsForResource(resourceID, sUDID);
            MDMEnrollmentDeviceHandler.getInstance(enrollmentType).enrollDevice(properties);
            MDMEnrollmentRequestHandler.getInstance().updateDeviceRequestStatus(reqId, 3, 4);
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, "Exception while enrolling Chrome device", ex);
        }
    }
    
    public JSONObject getAndroidPushConfigMessageResponse(final Long customerId) throws Exception {
        final JSONObject responseJSON = new JSONObject();
        JSONObject messageResponseJSON = new JSONObject();
        try {
            responseJSON.put("MessageType", (Object)"WakeUpPolicy");
            responseJSON.put("Status", (Object)"Acknowledged");
            messageResponseJSON = MDMAgentSettingsHandler.getInstance().getAndroidPushNotificationConfig(customerId);
        }
        catch (final Exception ex) {
            this.logger.log(Level.WARNING, "Exception occurred while getAndroidPushConfigMessageResponse ", ex);
            responseJSON.put("Status", (Object)"Error");
        }
        responseJSON.put("MessageResponse", (Object)messageResponseJSON);
        return responseJSON;
    }
    
    public boolean isManagedDeviceUDIDExist(final String udid) {
        boolean isManagedDeviceUDID = false;
        try {
            if (udid != null) {
                final SelectQuery reqQuery = this.getManagedDeviceQueryCheck(udid);
                final DataObject dObj = MDMUtil.getPersistence().get(reqQuery);
                if (!dObj.isEmpty()) {
                    isManagedDeviceUDID = true;
                }
            }
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, "Exception in isBulkEnrollUDID", ex);
        }
        return isManagedDeviceUDID;
    }
    
    private SelectQuery getManagedDeviceQueryCheck(final String udid) {
        final SelectQuery reqQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("ManagedDevice"));
        final Join deviceUserJoin = new Join("ManagedDevice", "ManagedUserToDevice", new String[] { "RESOURCE_ID" }, new String[] { "MANAGED_DEVICE_ID" }, 2);
        final Join userRequestJoin = new Join("ManagedUserToDevice", "EnrollmentRequestToDevice", new String[] { "MANAGED_DEVICE_ID" }, new String[] { "MANAGED_DEVICE_ID" }, 2);
        reqQuery.addJoin(deviceUserJoin);
        reqQuery.addJoin(userRequestJoin);
        reqQuery.addSelectColumn(Column.getColumn("ManagedDevice", "RESOURCE_ID"));
        reqQuery.addSelectColumn(Column.getColumn("ManagedUserToDevice", "MANAGED_DEVICE_ID"));
        reqQuery.addSelectColumn(Column.getColumn("ManagedUserToDevice", "MANAGED_USER_ID"));
        final Criteria udidCri = new Criteria(Column.getColumn("ManagedDevice", "UDID"), (Object)udid, 0);
        reqQuery.setCriteria(udidCri);
        return reqQuery;
    }
    
    public TreeMap getDomainListAsTreeMap(final List domainList) {
        try {
            TreeMap tmDomain = null;
            if (!domainList.isEmpty()) {
                tmDomain = new TreeMap();
            }
            for (final Properties domainProps : domainList) {
                final String domainName = domainProps.getProperty("NAME") + "";
                tmDomain.put(domainName, domainName.toUpperCase());
            }
            return tmDomain;
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Error occured in  getDomainList  method", e);
            return null;
        }
    }
    
    public void addSelfEnrollEventLog(final String sEventLogRemarks, String userName, final Long customerId) {
        try {
            Object remarksArgs = null;
            if (userName != null) {
                userName = (String)(remarksArgs = URLDecoder.decode(userName, "UTF-8"));
                final String sLoggedOnUserName = MDMEventConstant.DC_SYSTEM_USER;
                MDMEventLogHandler.getInstance().MDMEventLogEntry(2001, null, sLoggedOnUserName, sEventLogRemarks, remarksArgs, customerId);
                final org.json.simple.JSONObject logJSON = new org.json.simple.JSONObject();
                logJSON.put((Object)"REMARKS", (Object)sEventLogRemarks);
                logJSON.put((Object)"DATA", remarksArgs);
                MDMOneLineLogger.log(Level.INFO, "DEVICE_ENROLLED", logJSON);
                MDMMessageHandler.getInstance().messageAction("NO_DEVICE_ENROLLED", customerId);
            }
        }
        catch (final Exception exp) {
            this.logger.log(Level.WARNING, "Exception in addSelfEnrollEventLog", exp);
        }
    }
    
    public int getOwnedBy(final Long deviceId) {
        int ownedBy = -1;
        try {
            final SelectQuery ownedByQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("DeviceEnrollmentRequest"));
            final Join enrollReqJoin = new Join("DeviceEnrollmentRequest", "EnrollmentRequestToDevice", new String[] { "ENROLLMENT_REQUEST_ID" }, new String[] { "ENROLLMENT_REQUEST_ID" }, 2);
            ownedByQuery.addJoin(enrollReqJoin);
            ownedByQuery.addSelectColumn(Column.getColumn("DeviceEnrollmentRequest", "ENROLLMENT_REQUEST_ID"));
            ownedByQuery.addSelectColumn(Column.getColumn("DeviceEnrollmentRequest", "OWNED_BY"));
            final Criteria deviceCri = new Criteria(Column.getColumn("EnrollmentRequestToDevice", "MANAGED_DEVICE_ID"), (Object)deviceId, 0);
            ownedByQuery.setCriteria(deviceCri);
            final DataObject dObj = MDMUtil.getPersistence().get(ownedByQuery);
            if (!dObj.isEmpty()) {
                ownedBy = (int)dObj.getFirstValue("DeviceEnrollmentRequest", "OWNED_BY");
            }
        }
        catch (final Exception exp) {
            this.logger.log(Level.WARNING, "Exception in getOwnedBy", exp);
        }
        return ownedBy;
    }
    
    public boolean isLicenseLimitReached(final Long customerID) {
        boolean licenseReached = false;
        try {
            final int managedDeviceCount = ManagedDeviceHandler.getInstance().getManagedDeviceCount(MDMApiFactoryProvider.getMDMUtilAPI().getManagedDeviceCountCriteriaForLicenseCheck());
            final int addedEnrollmentRequestCount = MDMEnrollmentRequestHandler.getInstance().getAddedEnrollmentRequestCount();
            if (addedEnrollmentRequestCount > 5) {
                MDMMessageHandler.getInstance().messageAction("NAT_RECOMMENDATION", null);
            }
            if (LicenseProvider.getInstance().isMobileDeviceLicenseReached(managedDeviceCount)) {
                licenseReached = true;
            }
        }
        catch (final Exception exp) {
            this.logger.log(Level.WARNING, "Exception in isEnrollmentAllowed", exp);
        }
        if (!licenseReached && CustomerInfoUtil.getInstance().isMSP() && customerID != null) {
            licenseReached = this.isLicenseAllocationLimitReached(customerID);
        }
        return licenseReached;
    }
    
    public boolean isLicenseAllocationLimitReached(final Long customerID) {
        final MDMLicenseImplMSP mspLicense = new MDMLicenseImplMSP();
        return mspLicense.isMobileDeviceLicenseReached(customerID);
    }
    
    public HashMap getLastEnrollParamMap() {
        HashMap enrollParamMap = null;
        int ownedBy = 2;
        int platformType = 1;
        try {
            final Long loginId = ApiFactoryProvider.getAuthUtilAccessAPI().getLoginID();
            final Criteria loginCri = new Criteria(Column.getColumn("LastUsedEnrollmentParams", "LOGIN_ID"), (Object)loginId, 0);
            final DataObject dObj = MDMUtil.getPersistence().get("LastUsedEnrollmentParams", loginCri);
            if (!dObj.isEmpty()) {
                ownedBy = (int)dObj.getFirstValue("LastUsedEnrollmentParams", "OWNED_BY");
                platformType = (int)dObj.getFirstValue("LastUsedEnrollmentParams", "PLATFORM_TYPE");
            }
            enrollParamMap = new HashMap();
            enrollParamMap.put("OWNED_BY", ownedBy);
            enrollParamMap.put("PLATFORM_TYPE", platformType);
        }
        catch (final Exception exp) {
            this.logger.log(Level.WARNING, "Exception in getLastEnrollParamMap", exp);
        }
        return enrollParamMap;
    }
    
    public HashMap getreEnrollParamMap(final Long erid) {
        HashMap enrollParamMap = null;
        int ownedBy = 2;
        int platformType = 1;
        try {
            final Criteria eridCri = new Criteria(Column.getColumn("DeviceEnrollmentRequest", "ENROLLMENT_REQUEST_ID"), (Object)erid, 0);
            final DataObject dObj = MDMUtil.getPersistence().get("DeviceEnrollmentRequest", eridCri);
            if (!dObj.isEmpty()) {
                ownedBy = (int)dObj.getFirstValue("DeviceEnrollmentRequest", "OWNED_BY");
                platformType = (int)dObj.getFirstValue("DeviceEnrollmentRequest", "PLATFORM_TYPE");
            }
            enrollParamMap = new HashMap();
            enrollParamMap.put("OWNED_BY", ownedBy);
            enrollParamMap.put("PLATFORM_TYPE", platformType);
        }
        catch (final Exception exp) {
            this.logger.log(Level.WARNING, "Exception in getLastEnrollParamMap", exp);
        }
        return enrollParamMap;
    }
    
    public void addOrUpdateGroupSettings(final Long enrollSettingsId, final Long groupId, final int groupIdentifier) {
        try {
            final Criteria enrollSettingsCri = new Criteria(Column.getColumn("DefaultGroupSettings", "ENROLLMENT_SETTINGS_ID"), (Object)enrollSettingsId, 0);
            final Criteria groupIdentifierCri = new Criteria(Column.getColumn("DefaultGroupSettings", "GROUP_IDENTIFIER"), (Object)groupIdentifier, 0);
            final Criteria cri = enrollSettingsCri.and(groupIdentifierCri);
            final DataObject dObj = MDMUtil.getPersistence().get("DefaultGroupSettings", cri);
            Row defaultSettingsRow = null;
            if (dObj.isEmpty()) {
                defaultSettingsRow = new Row("DefaultGroupSettings");
                defaultSettingsRow.set("ENROLLMENT_SETTINGS_ID", (Object)enrollSettingsId);
                defaultSettingsRow.set("GROUP_IDENTIFIER", (Object)groupIdentifier);
                defaultSettingsRow.set("GROUP_ID", (Object)groupId);
                dObj.addRow(defaultSettingsRow);
            }
            else {
                defaultSettingsRow = dObj.getFirstRow("DefaultGroupSettings");
                defaultSettingsRow.set("GROUP_ID", (Object)groupId);
                dObj.updateRow(defaultSettingsRow);
            }
            MDMUtil.getPersistence().update(dObj);
        }
        catch (final Exception exp) {
            this.logger.log(Level.WARNING, "Exception in addOrUpdateGroupSettings", exp);
        }
    }
    
    public String getUDIDFromIOSAgentProperty(final String sBluetoothMAC, final String sWifiMAC) {
        Long resourceId = null;
        String sUDID = null;
        try {
            final Criteria blueToothMacCri = new Criteria(Column.getColumn("MdNetworkInfo", "BLUETOOTH_MAC"), (Object)sBluetoothMAC, 0);
            final Criteria wifiMacCri = new Criteria(Column.getColumn("MdNetworkInfo", "WIFI_MAC"), (Object)sWifiMAC, 0);
            final Criteria criteria = blueToothMacCri.and(wifiMacCri);
            final DataObject dataObj = MDMUtil.getPersistence().get("MdNetworkInfo", criteria);
            if (!dataObj.isEmpty()) {
                resourceId = (Long)dataObj.getFirstValue("MdNetworkInfo", "RESOURCE_ID");
                sUDID = ManagedDeviceHandler.getInstance().getUDIDFromResourceID(resourceId);
            }
        }
        catch (final Exception ex) {
            this.logger.log(Level.WARNING, " Exception thrown in getUDIDFromIOSAgentProperty ", ex);
        }
        return sUDID;
    }
    
    public DeviceMessage processIOSAgentProperty(final HashMap<String, String> hmap) {
        DeviceMessage deviceMsg = null;
        try {
            final JSONObject jsonValues = new JSONObject();
            deviceMsg = new DeviceMessage();
            deviceMsg.messageType = hmap.get("MessageType");
            deviceMsg.status = "Acknowledged";
            deviceMsg.messageResponse = jsonValues;
        }
        catch (final Exception ex) {
            this.logger.log(Level.WARNING, " Exception thrown in processIOSAgentProperty ", ex);
        }
        return deviceMsg;
    }
    
    public DeviceMessage processAuthModeModified(final HashMap<String, String> hmap) throws DataAccessException, JSONException, Exception {
        DeviceMessage deviceMsg = null;
        JSONArray jsonDomainArr = null;
        final String email = hmap.get("EmailAddress");
        final SelectQuery sQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("Resource"));
        final Join userJoin = new Join("Resource", "ManagedUser", new String[] { "RESOURCE_ID" }, new String[] { "MANAGED_USER_ID" }, 1);
        final Join enrolljoin = new Join("ManagedUser", "DeviceEnrollmentRequest", new String[] { "MANAGED_USER_ID" }, new String[] { "MANAGED_USER_ID" }, 1);
        sQuery.addJoin(userJoin);
        sQuery.addJoin(enrolljoin);
        final Criteria cEmail = new Criteria(new Column("ManagedUser", "EMAIL_ADDRESS"), (Object)email, 0, (boolean)Boolean.FALSE);
        final Criteria cStatus = new Criteria(new Column("DeviceEnrollmentRequest", "REQUEST_STATUS"), (Object)3, 0);
        final Criteria platform = new Criteria(new Column("DeviceEnrollmentRequest", "PLATFORM_TYPE"), (Object)1, 0);
        final Criteria userNotInTrashCriteria = new Criteria(Column.getColumn("ManagedUser", "STATUS"), (Object)11, 1).or(new Criteria(Column.getColumn("ManagedUser", "STATUS"), (Object)null, 0));
        sQuery.setCriteria(cEmail.and(cStatus).and(platform).and(userNotInTrashCriteria));
        sQuery.addSelectColumn(Column.getColumn("Resource", "*"));
        sQuery.addSelectColumn(Column.getColumn("DeviceEnrollmentRequest", "*"));
        final Long customerId = CustomerInfoUtil.getInstance().getDefaultCustomer();
        final DataObject DO = MDMUtil.getPersistence().get(sQuery);
        try {
            final List domainNameList = getInstance().getDomainNames(customerId);
            jsonDomainArr = new JSONArray((Collection)domainNameList);
        }
        catch (final Exception je) {
            this.logger.log(Level.INFO, "Exception ocurred while getting the domain names", je);
        }
        if (DO.isEmpty()) {
            deviceMsg = new DeviceMessage();
            JSONObject response = null;
            deviceMsg.messageType = hmap.get("MessageType");
            deviceMsg.status = "Error";
            response = new JSONObject();
            response.put("ErrorMsg", (Object)I18N.getMsg("dc.mdm.actionlog.enrollment.not_authorized", new Object[0]));
            response.put("ErrorCode", (Object)"12001");
            deviceMsg.messageResponse = response;
        }
        else {
            deviceMsg = new DeviceMessage();
            deviceMsg.messageType = hmap.get("MessageType");
            deviceMsg.status = "Acknowledged";
            final JSONObject response = new JSONObject();
            deviceMsg.messageResponse = response;
        }
        return deviceMsg;
    }
    
    public List getEnrolledWindowsDevicesList(final Long appId, final Long customerId) {
        List resourceList = null;
        try {
            final Criteria agentTypecri = new Criteria(Column.getColumn("ManagedDevice", "AGENT_TYPE"), (Object)4, 0);
            final Criteria customercri = new Criteria(Column.getColumn("Resource", "CUSTOMER_ID"), (Object)customerId, 0);
            final Criteria cri = agentTypecri.and(customercri);
            resourceList = ManagedDeviceHandler.getInstance().getManagedDeviceResourceIDs(cri);
        }
        catch (final Exception ex) {
            this.logger.log(Level.WARNING, " Exception in getEnrolledWindowsDevicesList ", ex);
        }
        return resourceList;
    }
    
    public String deleteEnrollmentId(final Long deviceId) {
        final String authPassword = null;
        try {
            final Criteria authCri = new Criteria(Column.getColumn("IOSNativeAppAuthentication", "MANAGED_DEVICE_ID"), (Object)deviceId, 0);
            final DataObject authDO = MDMUtil.getPersistence().get("IOSNativeAppAuthentication", authCri);
            Row authRow = null;
            if (!authDO.isEmpty()) {
                authRow = authDO.getFirstRow("IOSNativeAppAuthentication");
                authDO.deleteRow(authRow);
                MDMUtil.getPersistence().update(authDO);
            }
        }
        catch (final Exception ex) {
            this.logger.log(Level.WARNING, " Exception in deleteEnrollmentId ", ex);
        }
        return authPassword;
    }
    
    public Long getEnrollmentMailTemplateID(final Integer platFormtype, final Integer authMode) {
        Long mailTemplateID = 0L;
        if (platFormtype == 1) {
            if (authMode == 1) {
                mailTemplateID = MDMAlertConstants.IOS_ENROLLMENT_MAIL_TEMPLATE_FOR_PASSCODE;
            }
            else if (authMode == 2) {
                mailTemplateID = MDMAlertConstants.IOS_ENROLLMENT_MAIL_TEMPLATE_FOR_AD;
            }
            else if (authMode == 3) {
                mailTemplateID = MDMAlertConstants.IOS_ENROLLMENT_MAIL_TEMPLATE_FOR_BOTH;
            }
        }
        else if (platFormtype == 2) {
            if (authMode == 1) {
                mailTemplateID = MDMAlertConstants.ANDROID_ENROLLMENT_MAIL_TEMPLATE_FOR_PASSCODE;
            }
            else if (authMode == 2) {
                mailTemplateID = MDMAlertConstants.ANDROID_ENROLLMENT_MAIL_TEMPLATE_FOR_AD;
            }
            else if (authMode == 3) {
                mailTemplateID = MDMAlertConstants.ANDROID_ENROLLMENT_MAIL_TEMPLATE_FOR_BOTH;
            }
        }
        else if (platFormtype == 3) {
            final Boolean isAppBasedEnrollment = Boolean.valueOf(MDMUtil.getSyMParameter("IsAppBasedEnrollmentForWindowsPhone"));
            if (isAppBasedEnrollment) {
                if (authMode == 1) {
                    mailTemplateID = MDMAlertConstants.WINDOWS_APP_BASED_ENROLLMENT_MAIL_TEMPLATE_FOR_PASSCODE;
                }
                else if (authMode == 2) {
                    mailTemplateID = MDMAlertConstants.WINDOWS_APP_BASED_ENROLLMENT_MAIL_TEMPLATE_FOR_AD;
                }
                else if (authMode == 3) {
                    mailTemplateID = MDMAlertConstants.WINDOWS_APP_BASED_ENROLLMENT_MAIL_TEMPLATE_FOR_BOTH;
                }
            }
            else if (authMode == 1) {
                mailTemplateID = MDMAlertConstants.WINDOWS_ENROLLMENT_MAIL_TEMPLATE_FOR_PASSCODE;
            }
            else if (authMode == 2) {
                mailTemplateID = MDMAlertConstants.WINDOWS_ENROLLMENT_MAIL_TEMPLATE_FOR_AD;
            }
            else if (authMode == 3) {
                mailTemplateID = MDMAlertConstants.WINDOWS_ENROLLMENT_MAIL_TEMPLATE_FOR_BOTH;
            }
        }
        else if (platFormtype == 0) {
            if (authMode == 1) {
                mailTemplateID = MDMAlertConstants.PLATFORM_NEUTRAL_ENROLLMENT_MAIL_TEMPLATE_FOR_PASSCODE;
            }
            else if (authMode == 2) {
                mailTemplateID = MDMAlertConstants.PLATFORM_NEUTRAL_ENROLLMENT_MAIL_TEMPLATE_FOR_AD;
            }
            else if (authMode == 3) {
                mailTemplateID = MDMAlertConstants.PLATFORM_NEUTRAL_ENROLLMENT_MAIL_TEMPLATE_FOR_BOTH;
            }
        }
        return mailTemplateID;
    }
    
    public String getServerBaseURL() throws Exception {
        final Properties natProps = ApiFactoryProvider.getServerSettingsAPI().getNATConfigurationProperties();
        if (natProps.size() > 0) {
            final String serverIP = ((Hashtable<K, String>)natProps).get("NAT_ADDRESS");
            final String serverPort = String.valueOf(((Hashtable<K, Object>)natProps).get("NAT_HTTPS_PORT"));
            final StringBuilder baseURLStr = new StringBuilder("https://" + serverIP + ":" + serverPort);
            return baseURLStr.toString();
        }
        final Properties serverProp = MDMUtil.getDCServerInfo();
        final String serverIP2 = ((Hashtable<K, String>)serverProp).get("SERVER_MAC_NAME");
        final String serverPort2 = String.valueOf(((Hashtable<K, Object>)serverProp).get("SERVER_PORT"));
        final StringBuilder baseURLStr2 = new StringBuilder("http://" + serverIP2 + ":" + serverPort2);
        return baseURLStr2.toString();
    }
    
    public Long getEnrollRequestIDFromManagedDeviceID(final Long managedDeviceID) {
        final String sourceMethod = "getManagedDeviceIDFromEnrollRequestID";
        Long enrollRequestID = null;
        try {
            final Criteria criteria = new Criteria(Column.getColumn("EnrollmentRequestToDevice", "MANAGED_DEVICE_ID"), (Object)managedDeviceID, 0);
            final DataObject dataObject = MDMUtil.getPersistence().get("EnrollmentRequestToDevice", criteria);
            if (!dataObject.isEmpty()) {
                enrollRequestID = (Long)dataObject.getFirstValue("EnrollmentRequestToDevice", "ENROLLMENT_REQUEST_ID");
            }
            SyMLogger.info(this.logger, this.sourceClass, sourceMethod, "Managed Device ID : " + enrollRequestID + " from enrollment request id : " + managedDeviceID);
        }
        catch (final Exception exp) {
            SyMLogger.error(this.logger, this.sourceClass, sourceMethod, "Exception occured while geting enrollment request id from managed device id : " + managedDeviceID, (Throwable)exp);
        }
        return enrollRequestID;
    }
    
    public void removeNotExistingEnrollmentRequest(final Set<Long> enrollmentRequestIDSet) {
        final Criteria enrollCri = new Criteria(Column.getColumn("EnrollmentRequestToDevice", "ENROLLMENT_REQUEST_ID"), (Object)enrollmentRequestIDSet.toArray(), 8);
        try {
            final DataObject enrollDO = MDMUtil.getPersistence().get("EnrollmentRequestToDevice", enrollCri);
            if (!enrollDO.isEmpty()) {
                final Iterator reqIDIterator = enrollDO.getRows("EnrollmentRequestToDevice");
                while (reqIDIterator.hasNext()) {
                    final Row row = reqIDIterator.next();
                    final Long erID = (Long)row.get("ENROLLMENT_REQUEST_ID");
                    if (enrollmentRequestIDSet.contains(erID)) {
                        enrollmentRequestIDSet.remove(erID);
                    }
                }
            }
            if (!enrollmentRequestIDSet.isEmpty()) {
                String eridStr = enrollmentRequestIDSet.toString();
                eridStr = eridStr.substring(1, eridStr.length() - 1);
                this.deviceWithoutRequestDebugLog(eridStr.trim().split("\\s*,\\s*"), "removeNotExistingEnrollmentRequest");
                final Criteria erReqCri = new Criteria(Column.getColumn("DeviceEnrollmentRequest", "ENROLLMENT_REQUEST_ID"), (Object)enrollmentRequestIDSet.toArray(), 8);
                DataAccess.delete(erReqCri);
            }
        }
        catch (final Exception ex) {
            this.logger.log(Level.WARNING, ex, () -> "Exception occurred while checking the enrollemnt request is exist. enrollmentRequestID : " + set);
        }
    }
    
    private DataObject getDeviceEnrollmentRequest(final Long enrollRequestID) {
        final String sourceMethod = "getDeviceEnrollmentRequest";
        DataObject dataObject = null;
        try {
            final Criteria criteria = new Criteria(Column.getColumn("DeviceEnrollmentRequest", "ENROLLMENT_REQUEST_ID"), (Object)enrollRequestID, 0);
            dataObject = MDMUtil.getPersistence().get("DeviceEnrollmentRequest", criteria);
        }
        catch (final Exception exp) {
            SyMLogger.error(this.logger, this.sourceClass, sourceMethod, "Exception occured while fetching DeviceEnrollmentRequest table.", (Throwable)exp);
        }
        return dataObject;
    }
    
    public Integer getRequestPlatformType(final Long requestId) {
        int platformType = 1;
        try {
            platformType = (int)DBUtil.getValueFromDB("DeviceEnrollmentRequest", "ENROLLMENT_REQUEST_ID", (Object)requestId, "PLATFORM_TYPE");
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, "Exception while getting Platform Type", ex);
        }
        return platformType;
    }
    
    public Integer getDeviceAuthMode(final Long requestId) {
        Integer authmode = 1;
        try {
            authmode = (Integer)DBUtil.getValueFromDB("DeviceEnrollmentRequest", "ENROLLMENT_REQUEST_ID", (Object)requestId, "AUTH_MODE");
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, "Exception while getting device auth mode", ex);
        }
        return authmode;
    }
    
    public void processAndroidTokenUpdate(final HashMap<String, String> dataMap) throws Exception {
        this.logger.log(Level.INFO, "Android Token update begins");
        final String sUDID = dataMap.get("UDID");
        final Long resourceID = ManagedDeviceHandler.getInstance().getResourceIDFromUDID(sUDID);
        final JSONObject messageData = new JSONObject((String)dataMap.get("Message"));
        final String registrationId = messageData.optString("RegistrationID");
        final Integer repType = Integer.parseInt(dataMap.get("CMDRepType"));
        this.logger.log(Level.INFO, "In processAndroidTokenUpdate -> Resource ID = {0}", resourceID);
        this.logger.log(Level.INFO, "In processAndroidTokenUpdate -> Registration ID = {0}", registrationId);
        if (registrationId != null && !registrationId.isEmpty() && !registrationId.equals("--") && registrationId.length() > 5) {
            final JSONObject hAndroidCommMap = new JSONObject();
            hAndroidCommMap.put("NOTIFICATION_TOKEN_ENCRYPTED", (Object)registrationId);
            try {
                final int notificationType = (repType != null && repType == 2) ? 201 : 2;
                PushNotificationHandler.getInstance().addOrUpdateManagedIdToNotificationRel(resourceID, notificationType, hAndroidCommMap);
                final List resourceList = new ArrayList();
                resourceList.add(resourceID);
                NotificationHandler.getInstance().SendNotification(resourceList, notificationType);
            }
            catch (final Exception ex) {
                this.logger.log(Level.SEVERE, "Exception updating communication details", ex);
            }
            this.logger.log(Level.INFO, "Android Token update Ends successfully");
        }
    }
    
    public void processGCMReregistration(final HashMap<String, String> dataMap) throws JSONException, Exception {
        this.processAndroidTokenUpdate(dataMap);
        final String sUDID = dataMap.get("UDID");
        final Integer status = ManagedDeviceHandler.getInstance().getManagedDeviceStatus(sUDID);
        if (status != null && status == 4) {
            final Long resourceID = ManagedDeviceHandler.getInstance().getResourceIDFromUDID(sUDID);
            final List<Long> devList = new ArrayList<Long>();
            devList.add(resourceID);
            final JSONObject details = new JSONObject();
            details.put("MANAGED_STATUS", 2);
            details.put("REMARKS", (Object)"dc.mdm.db.agent.enroll.user_enabled_admin");
            ManagedDeviceHandler.getInstance().updateManagedDeviceDetails(devList, details);
        }
    }
    
    public boolean isNATConfigured() {
        try {
            boolean isNATConfigured = false;
            isNATConfigured = !MDMMessageUtil.getInstance().isNATNotConfiguredMsgOpen();
            return isNATConfigured;
        }
        catch (final Exception ex) {
            this.logger.log(Level.INFO, "Exception while checking if NAT is configured.. {0}", ex);
            return false;
        }
    }
    
    public boolean isServiceRestarted() {
        try {
            final String isServiceRestarted = MDMUtil.getSyMParameter("Service_Restarted");
            return isServiceRestarted == null || isServiceRestarted.isEmpty() || isServiceRestarted.equalsIgnoreCase("true");
        }
        catch (final Exception ex) {
            this.logger.log(Level.INFO, "Exception while checking if NAT is configured.. {0}", ex);
            return false;
        }
    }
    
    public boolean isMailServerConfigured() {
        try {
            return ApiFactoryProvider.getMailSettingAPI().isMailServerConfigured();
        }
        catch (final Exception ex) {
            this.logger.log(Level.INFO, "Exception while checking if Mail Server is configured.. {0}", ex);
            return false;
        }
    }
    
    public boolean isProxyConfigured() {
        try {
            final String proxy_defined = MDMUtil.getSyMParameter("proxy_defined");
            final String proxy_failed = MDMUtil.getSyMParameter("proxy_failed");
            if (proxy_defined != null && !proxy_defined.isEmpty()) {
                boolean bool = proxy_defined.equalsIgnoreCase("true");
                if (proxy_failed != null && !proxy_failed.isEmpty()) {
                    bool = !proxy_failed.equalsIgnoreCase("true");
                }
                return bool;
            }
        }
        catch (final Exception ex) {
            this.logger.log(Level.INFO, "Exception while checking if Proxy is configured.. {0}", ex);
        }
        return false;
    }
    
    public boolean isAPNsConfigured() {
        try {
            final Map apnsCertificateDetails = APNsCertificateHandler.getAPNSCertificateDetails();
            return apnsCertificateDetails != null && !apnsCertificateDetails.isEmpty();
        }
        catch (final Exception ex) {
            this.logger.log(Level.INFO, "Exception while checking if APNs is configured.. {0}", ex);
            return false;
        }
    }
    
    public void handleDuplicateAndroidDevice(final String imei, final String serialNumber, final String sUDID) {
        try {
            final JSONObject devInf = new JSONObject();
            devInf.put("IMEI", (Object)imei);
            devInf.put("SerialNumber", (Object)serialNumber);
            devInf.put("UDID", (Object)sUDID);
            final ManagedDeviceHandler managedDeviceHandler = ManagedDeviceHandler.getInstance();
            final Long resourceID = managedDeviceHandler.getManagedDeviceID(devInf);
            if (resourceID != null) {
                this.logger.log(Level.INFO, "handleDuplicateAndroidDevice().. Device Information : {0}", devInf);
                this.logger.log(Level.INFO, "Duplciate Device Found! UDID={0} updated for Resource ID={1}", new Object[] { sUDID, resourceID });
                managedDeviceHandler.updateMangedDeviceUDID(resourceID, sUDID);
            }
        }
        catch (final Exception ex) {
            this.logger.log(Level.WARNING, "Exception occurred while converting enrollment data to JSON object into handleDuplicateAndroidDevice", ex);
        }
    }
    
    public String getWindowsAgentDownloadURL() throws Exception {
        String url = "";
        final Long customerID = CustomerInfoUtil.getInstance().getCustomerId();
        final Criteria customerIdCrit = new Criteria(Column.getColumn("MDAgentDownloadInfo", "CUSTOMER_ID"), (Object)customerID, 0);
        final Criteria platformCrit = new Criteria(Column.getColumn("MDAgentDownloadInfo", "PLATFORM_TYPE"), (Object)3, 0);
        final Criteria criteria = customerIdCrit.and(platformCrit);
        final ReadOnlyPersistence cachedPersistence = MDMUtil.getCachedPersistence();
        final DataObject dataObject = cachedPersistence.get("MDAgentDownloadInfo", criteria);
        final Properties natProps = ApiFactoryProvider.getServerSettingsAPI().getNATConfigurationProperties();
        final String serverIP = ((Hashtable<K, String>)natProps).get("NAT_ADDRESS");
        if (dataObject != null && !dataObject.isEmpty()) {
            final Row row = dataObject.getFirstRow("MDAgentDownloadInfo");
            final Integer downloadMode = (Integer)row.get("DOWNLOAD_MODE");
            final String agentUrl = (String)row.get("DOWNLOAD_URL");
            if (downloadMode == 1) {
                final Properties serverProp = MDMUtil.getDCServerInfo();
                final String serverPort = String.valueOf(((Hashtable<K, Object>)serverProp).get("SERVER_PORT"));
                url = "http://" + serverIP + ":" + serverPort + agentUrl;
            }
            else if (downloadMode == 2) {
                final String serverPort2 = String.valueOf(((Hashtable<K, Object>)natProps).get("NAT_HTTPS_PORT"));
                url = "https://" + serverIP + ":" + serverPort2 + agentUrl;
            }
            else {
                url = agentUrl;
            }
        }
        else {
            final String defaultURL = "/mdm/app";
            final String serverPort3 = String.valueOf(((Hashtable<K, Object>)natProps).get("NAT_HTTPS_PORT"));
            url = "https://" + serverIP + ":" + serverPort3 + defaultURL;
        }
        return url;
    }
    
    public int getEnrolledDeviceCount(final Long customerID) throws Exception {
        final String skipCustomerFilter = CustomerInfoThreadLocal.getSkipCustomerFilter();
        boolean flagSet = false;
        try {
            if (skipCustomerFilter != null && skipCustomerFilter.equals("false")) {
                CustomerInfoThreadLocal.setSkipCustomerFilter("true");
                flagSet = true;
            }
            final SelectQuery sQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("ManagedDevice"));
            sQuery.addJoin(new Join("ManagedDevice", "Resource", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2));
            sQuery.addJoin(new Join("ManagedDevice", "EnrollmentRequestToDevice", new String[] { "RESOURCE_ID" }, new String[] { "MANAGED_DEVICE_ID" }, 2));
            sQuery.addJoin(new Join("EnrollmentRequestToDevice", "DeviceEnrollmentRequest", new String[] { "ENROLLMENT_REQUEST_ID" }, new String[] { "ENROLLMENT_REQUEST_ID" }, 2));
            final Criteria enrollStatus = new Criteria(new Column("ManagedDevice", "MANAGED_STATUS"), (Object)2, 0);
            final Criteria customerCriteria = new Criteria(Column.getColumn("Resource", "CUSTOMER_ID"), (Object)customerID, 0);
            sQuery.setCriteria(enrollStatus.and(customerCriteria));
            RBDAUtil.getInstance().getRBDAQuery(sQuery);
            final int enrolledDeviceCount = DBUtil.getRecordCount(sQuery, "ManagedDevice", "RESOURCE_ID");
            return enrolledDeviceCount;
        }
        catch (final DataAccessException ex) {
            MDMUtil.logger.log(Level.WARNING, "Exception occured while getting added Enrollment request count: {0}", (Throwable)ex);
        }
        finally {
            if (flagSet) {
                CustomerInfoThreadLocal.setSkipCustomerFilter("false");
            }
        }
        return 0;
    }
    
    public int getYetToEnrollRequestCount(final Long customerID) throws Exception {
        final boolean flagSet = false;
        try {
            final SelectQuery sQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("DeviceEnrollmentRequest"));
            sQuery.addJoin(new Join("DeviceEnrollmentRequest", "ManagedUser", new String[] { "MANAGED_USER_ID" }, new String[] { "MANAGED_USER_ID" }, 2));
            sQuery.addJoin(new Join("ManagedUser", "Resource", new String[] { "MANAGED_USER_ID" }, new String[] { "RESOURCE_ID" }, 2));
            sQuery.addJoin(new Join("DeviceEnrollmentRequest", "EnrollmentRequestToDevice", new String[] { "ENROLLMENT_REQUEST_ID" }, new String[] { "ENROLLMENT_REQUEST_ID" }, 1));
            sQuery.addJoin(new Join("EnrollmentRequestToDevice", "ManagedDevice", new String[] { "MANAGED_DEVICE_ID" }, new String[] { "RESOURCE_ID" }, 1));
            final Criteria reqCriteria = new Criteria(Column.getColumn("DeviceEnrollmentRequest", "ENROLLMENT_TYPE"), (Object)1, 0).and(new Criteria(Column.getColumn("DeviceEnrollmentRequest", "REQUEST_STATUS"), (Object)1, 0));
            final Criteria notUnmanagedCriteria = new Criteria(Column.getColumn("ManagedDevice", "MANAGED_STATUS"), (Object)null, 0).or(new Criteria(Column.getColumn("ManagedDevice", "MANAGED_STATUS"), (Object)4, 1));
            final Criteria customerCriteria = new Criteria(Column.getColumn("Resource", "CUSTOMER_ID"), (Object)customerID, 0);
            final Criteria userNotInTrashCriteria = new Criteria(Column.getColumn("ManagedUser", "STATUS"), (Object)11, 1);
            final Criteria enrollStatus = reqCriteria.and(notUnmanagedCriteria).and(customerCriteria).and(userNotInTrashCriteria);
            sQuery.setCriteria(enrollStatus);
            final int enrolledDeviceCount = DBUtil.getRecordCount(sQuery, "DeviceEnrollmentRequest", "ENROLLMENT_REQUEST_ID");
            return enrolledDeviceCount;
        }
        catch (final DataAccessException ex) {
            MDMUtil.logger.log(Level.WARNING, "Exception occured while getting added Enrollment request count: {0}", (Throwable)ex);
        }
        finally {
            if (flagSet) {
                CustomerInfoThreadLocal.setSkipCustomerFilter("false");
            }
        }
        return 0;
    }
    
    private void setSerialNumberInDeviceEvent(final DeviceEvent deviceEvent) {
        String serialNum = "";
        try {
            serialNum = (String)DBUtil.getValueFromDB("MdDeviceInfo", "RESOURCE_ID", (Object)deviceEvent.resourceID, "SERIAL_NUMBER");
            final JSONObject deviceJSON = new JSONObject();
            deviceJSON.put("SERIAL_NUMBER", (Object)serialNum);
            deviceEvent.resourceJSON = deviceJSON;
        }
        catch (final Exception ex) {
            Logger.getLogger(MDMEnrollmentUtil.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public Date getDateInStandardFormat(final String dateStr) {
        Date date = null;
        try {
            final SimpleDateFormat utcFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
            utcFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
            date = utcFormat.parse(dateStr);
        }
        catch (final ParseException ex) {
            Logger.getLogger(MDMEnrollmentUtil.class.getName()).log(Level.SEVERE, null, ex);
        }
        return date;
    }
    
    public List<String> getDomainNames(final List domainProps) {
        final List<String> domainNames = new ArrayList<String>();
        for (int i = 0; i < domainProps.size(); ++i) {
            final Properties domainProperty = domainProps.get(i);
            final String domainName = domainProperty.getProperty("NAME");
            domainNames.add(domainName);
        }
        return domainNames;
    }
    
    public List<String> getDomainNames(final Long customerID) {
        final List domainProps = DMDomainDataHandler.getInstance().getAllDMManagedProps(customerID);
        return this.getDomainNames(domainProps);
    }
    
    public List<String> getDomainNamesWithoutGSuite(final Long customerID) {
        final List domainProps = DMDomainDataHandler.getInstance().getAllDMManagedProps(customerID);
        final List<String> domainNames = new ArrayList<String>();
        for (int i = 0; i < domainProps.size(); ++i) {
            final Properties domainProperty = domainProps.get(i);
            if (((Hashtable<K, Integer>)domainProperty).get("CLIENT_ID") != 101) {
                final String domainName = domainProperty.getProperty("NAME");
                domainNames.add(domainName);
            }
        }
        return domainNames;
    }
    
    public List<Properties> getDomainPropsForUserAuthentication(final Long customerID) {
        final List domainProps = DMDomainDataHandler.getInstance().getAllDMManagedProps(customerID);
        final List<Properties> adDomainProps = new ArrayList<Properties>();
        for (int i = 0; i < domainProps.size(); ++i) {
            final Properties domainProperty = domainProps.get(i);
            if (((Hashtable<K, Integer>)domainProperty).get("CLIENT_ID") != 101) {
                adDomainProps.add(domainProperty);
            }
        }
        return adDomainProps;
    }
    
    public void deleteOldTemplateVersion(final Long customerId, final Long alertConstantId) throws DataAccessException {
        final HashMap featureParameters = MDMFeatureParamsHandler.getMDMFeatureParameters();
        if (featureParameters.containsKey("OldVersion-" + alertConstantId)) {
            final String value = String.valueOf(featureParameters.get("OldVersion-" + alertConstantId));
            if (value.equalsIgnoreCase("true")) {
                MDMFeatureParamsHandler.updateMDMFeatureParameter("OldVersion-" + alertConstantId, false);
            }
        }
        final AlertsUtil alertsUtil = new AlertsUtil();
        alertsUtil.deleteAlertTemplate(customerId, alertConstantId);
    }
    
    public boolean previousTemplateVersionsExist(final Long customerId, final Integer platformtype) throws Exception {
        final Integer authMode = EnrollmentSettingsHandler.getInstance().getInvitationEnrollmentSettings(customerId).getInt("AUTH_MODE");
        final long mailTemplateID = getInstance().getEnrollmentMailTemplateID(platformtype, authMode);
        return MDMFeatureParamsHandler.getInstance().isFeatureEnabled("OldVersion-" + mailTemplateID);
    }
    
    public static void addOrIncrementMETrackQRParamManagerValuesForPlatform(final int platform, final Long customerId, final String iOSParamConstant, final String androidParamConstant, final String windowsPramConstant) {
        String paramConst = null;
        switch (platform) {
            case 1: {
                paramConst = iOSParamConstant;
                break;
            }
            case 2: {
                paramConst = androidParamConstant;
                break;
            }
            case 3: {
                paramConst = windowsPramConstant;
                break;
            }
        }
        if (paramConst != null && customerId != null) {
            try {
                MEMDMTrackParamManager.getInstance().incrementTrackValue(customerId, "QR_Enrollment_Module", paramConst);
            }
            catch (final JSONException ex) {
                Logger.getLogger(MDMEnrollmentUtil.class.getName()).log(Level.SEVERE, null, (Throwable)ex);
            }
        }
    }
    
    public String getOrganisationNameForEnrollmentPages() {
        try {
            final JSONObject csrInfo = APNsCertificateHandler.getInstance().getCSRInfo();
            final String orgName = csrInfo.optString("ORGANIZATION_NAME", "ManageEngine");
            return WordUtils.capitalize(orgName);
        }
        catch (final Exception ex) {
            Logger.getLogger("MDMEnrollment").log(Level.INFO, "No Orgname found in CSR INFO , Returning default Orgname:{0}", "ZohoCorp");
            return "ManageEngine";
        }
    }
    
    public String getCustomerLogoPathForEnrollmentMail(final Long customerID) throws Exception {
        String logoPath = "";
        try {
            logoPath = CustomerInfoUtil.getInstance().getRebrandLogoPathForWebConsole();
            if (CustomerInfoUtil.getInstance().isMSP() && customerID != null) {
                final Boolean isNewLogo = (Boolean)DBUtil.getValueFromDB("CustomerInfo", "CUSTOMER_ID", (Object)customerID, "IS_CUSTOM_LOGO_ENABLE");
                if (isNewLogo != null && isNewLogo && CustomerInfoUtil.getInstance().checkIsLogoAvailableInFolder((long)customerID)) {
                    logoPath = CustomerInfoUtil.getInstance().getCustomerLogoPathforWbCl((long)customerID);
                }
            }
        }
        catch (final Exception e) {
            Logger.getLogger(MDMEnrollmentUtil.class.getName()).log(Level.SEVERE, null, e);
        }
        return getInstance().getServerBaseURL() + logoPath;
    }
    
    public int getOwnedByforEnrollmentRequest(final Long erid) {
        int ownedBy = 1;
        try {
            ownedBy = (int)DBUtil.getValueFromDB("DeviceEnrollmentRequest", "ENROLLMENT_REQUEST_ID", (Object)erid, "OWNED_BY");
        }
        catch (final Exception exp) {
            this.logger.log(Level.SEVERE, "Exception while getting ownedby", exp);
        }
        return ownedBy;
    }
    
    public void setEnrollmentInvitationProperties(final Properties enrollmentProperties, final String phoneNumber, final String sendEmail, final String sendSMS) {
        boolean phoneNumberAvailable = false;
        if (phoneNumber != null && !phoneNumber.equals("")) {
            phoneNumberAvailable = true;
            ((Hashtable<String, String>)enrollmentProperties).put("PHONE_NUMBER", SyMUtil.getInstance().decodeURIComponentEquivalent(phoneNumber));
        }
        if (sendSMS != null && !sendSMS.equals("")) {
            if (phoneNumberAvailable) {
                ((Hashtable<String, Boolean>)enrollmentProperties).put("sendSMS", Boolean.parseBoolean(sendSMS));
                this.logger.log(Level.INFO, "MDMEnrollmentUtil: Send SMS Status - {0}", Boolean.parseBoolean(sendSMS));
            }
            else {
                this.logger.log(Level.INFO, "MDMEnrollmentUtil: PhoneNumer is null");
                ((Hashtable<String, Boolean>)enrollmentProperties).put("sendSMS", false);
            }
        }
        else {
            ((Hashtable<String, Boolean>)enrollmentProperties).put("sendSMS", false);
        }
        if (sendEmail != null && !sendEmail.equals("")) {
            final boolean sendEmailStatus = Boolean.parseBoolean(sendEmail);
            if (sendEmailStatus) {
                this.logger.log(Level.INFO, "MDMEnrollmentUtil: Email notification to be sent");
                ((Hashtable<String, Boolean>)enrollmentProperties).put("sendEmail", true);
            }
            else {
                this.logger.log(Level.INFO, "MDMEnrollmentUtil: Email notification need not be sent");
                ((Hashtable<String, Boolean>)enrollmentProperties).put("sendEmail", false);
            }
        }
        else {
            ((Hashtable<String, Boolean>)enrollmentProperties).put("sendEmail", true);
        }
    }
    
    public static boolean isValidPhone(final String phoneNumber) {
        final Pattern pattern = Pattern.compile("[\\+][0-9]{1,4}[\\-][0-9]{6,15}");
        final Matcher matcher = pattern.matcher(phoneNumber);
        return matcher.matches() && phoneNumber.length() >= 10;
    }
    
    public void deviceWithoutRequestDebugLog(final Long[] erid, final String className) {
        try {
            this.deviceWithoutRequestDebugLog(Arrays.asList(erid), className);
        }
        catch (final Exception exp) {
            this.logger.log(Level.SEVERE, "Exception in debug er deletion logging", exp);
        }
    }
    
    public void deviceWithoutRequestDebugLog(final String[] erid, final String className) {
        try {
            final List<Long> eridList = new ArrayList<Long>();
            for (final String eridStr : erid) {
                eridList.add(Long.parseLong(eridStr));
            }
            this.deviceWithoutRequestDebugLog(eridList, className);
        }
        catch (final Exception exp) {
            this.logger.log(Level.SEVERE, "Exception in debug er deletion logging", exp);
        }
    }
    
    public void deviceWithoutRequestDebugLog(final List<Long> erid, final String className) {
        try {
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("DeviceEnrollmentRequest"));
            final Join deviceRequestRelJoin = new Join("DeviceEnrollmentRequest", "EnrollmentRequestToDevice", new String[] { "ENROLLMENT_REQUEST_ID" }, new String[] { "ENROLLMENT_REQUEST_ID" }, 1);
            selectQuery.addJoin(deviceRequestRelJoin);
            selectQuery.addSelectColumn(Column.getColumn("DeviceEnrollmentRequest", "ENROLLMENT_REQUEST_ID"));
            selectQuery.addSelectColumn(Column.getColumn("EnrollmentRequestToDevice", "ENROLLMENT_REQUEST_ID", "ENROLLMENTREQUESTTODEVICE.ENROLLMENT_REQUEST_ID"));
            selectQuery.addSelectColumn(Column.getColumn("EnrollmentRequestToDevice", "MANAGED_DEVICE_ID"));
            selectQuery.setCriteria(new Criteria(Column.getColumn("DeviceEnrollmentRequest", "ENROLLMENT_REQUEST_ID"), (Object)erid.toArray(), 8));
            DMDataSetWrapper ds = null;
            ds = DMDataSetWrapper.executeQuery((Object)selectQuery);
            while (ds.next()) {
                final Long eridFromDeviceEnrollmentrequest = (Long)ds.getValue("ENROLLMENT_REQUEST_ID");
                final Long eridFromenrollmentRequestToDevice = (Long)ds.getValue("ENROLLMENTREQUESTTODEVICE.ENROLLMENT_REQUEST_ID");
                final Long mdidFromenrollmentRequestToDevice = (Long)ds.getValue("MANAGED_DEVICE_ID");
                this.logger.log(Level.INFO, "Debug log for ERID missing... className: {0} || eridFromDeviceEnrollmentrequest: {1} || eridFromenrollmentRequestToDevice: {2} || mdidFromenrollmentRequestToDevice: {3}", new Object[] { className, eridFromDeviceEnrollmentrequest, eridFromenrollmentRequestToDevice, mdidFromenrollmentRequestToDevice });
            }
        }
        catch (final Exception exp) {
            this.logger.log(Level.INFO, "Exception in debug logs for erid missing", exp);
        }
    }
    
    public boolean getToolTipShownStatus() {
        boolean status = false;
        try {
            final Long userID = ApiFactoryProvider.getAuthUtilAccessAPI().getUserID();
            final String closeDB = SyMUtil.getUserParameter(userID, "SMS_TOOL_TIP_SHOWN_STATUS");
            status = Boolean.valueOf(closeDB);
        }
        catch (final Exception e) {
            this.logger.info("MDMEnrollmentUtil : Exception while getting SMS_TOOL_TIP_SHOWN_STATUS value");
        }
        return status;
    }
    
    public void setToolTipShownStatus() {
        try {
            final boolean status = true;
            final Long userID = ApiFactoryProvider.getAuthUtilAccessAPI().getUserID();
            SyMUtil.updateUserParameter(userID, "SMS_TOOL_TIP_SHOWN_STATUS", String.valueOf(status));
        }
        catch (final Exception e) {
            this.logger.info("MDMEnrollmentUtil : Exception while setting SMS_TOOL_TIP_SHOWN_STATUS value");
        }
    }
    
    public DataObject getActiveEnrollmentRequestAvailableForUser(final long managedUserID, final String phoneNumber, final int platformType) {
        DataObject dataObject = null;
        try {
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("DEVICEENROLLREQTOSMS"));
            Join join = new Join("DEVICEENROLLREQTOSMS", "DeviceEnrollmentRequest", new String[] { "ENROLLMENT_REQUEST_ID" }, new String[] { "ENROLLMENT_REQUEST_ID" }, 2);
            selectQuery.addJoin(join);
            join = new Join("DEVICEENROLLREQTOSMS", "InvitationEnrollmentRequest", new String[] { "ENROLLMENT_REQUEST_ID" }, new String[] { "ENROLLMENT_REQUEST_ID" }, 2);
            selectQuery.addJoin(join);
            Criteria criteria = new Criteria(Column.getColumn("DEVICEENROLLREQTOSMS", "PHONE_NUMBER"), (Object)phoneNumber, 0);
            criteria = criteria.and(new Criteria(Column.getColumn("DEVICEENROLLREQTOSMS", "SMS_CODE"), (Object)0, 0));
            criteria = criteria.and(new Criteria(Column.getColumn("DeviceEnrollmentRequest", "MANAGED_USER_ID"), (Object)managedUserID, 0));
            criteria = criteria.and(new Criteria(Column.getColumn("DeviceEnrollmentRequest", "PLATFORM_TYPE"), (Object)platformType, 0));
            criteria = criteria.and(new Criteria(Column.getColumn("DeviceEnrollmentRequest", "REQUEST_STATUS"), (Object)1, 0));
            criteria = criteria.and(new Criteria(Column.getColumn("InvitationEnrollmentRequest", "REGISTRATION_STATUS"), (Object)3, 1));
            selectQuery.setCriteria(criteria);
            selectQuery.addSelectColumn(Column.getColumn("DeviceEnrollmentRequest", "*"));
            selectQuery.addSelectColumn(Column.getColumn("InvitationEnrollmentRequest", "*"));
            selectQuery.addSortColumn(new SortColumn(Column.getColumn("DeviceEnrollmentRequest", "REQUESTED_TIME"), false));
            dataObject = MDMUtil.getPersistence().get(selectQuery);
        }
        catch (final DataAccessException e) {
            this.logger.log(Level.SEVERE, "Exception while fetching active request from db", (Throwable)e);
        }
        return dataObject;
    }
    
    public static String getPlatformString(final int platform) {
        if (platform == 3) {
            return "windows";
        }
        if (platform == 1) {
            return "iOS";
        }
        if (platform == 2) {
            return "android";
        }
        if (platform == 4) {
            return "ChromeOS";
        }
        if (platform == 0) {
            return "neutral";
        }
        return null;
    }
    
    public static void appendSMSWebhookInfoIntoJSON(final JSONObject jsonObject, final int errorCode) throws Exception {
        final JSONArray descriptionJSONArray = new JSONArray();
        final JSONArray suggestionsJSONArray = new JSONArray();
        switch (errorCode) {
            case 10002: {
                jsonObject.put("hasSMSHookInfo", true);
                jsonObject.put("smsErrorTitle", (Object)I18N.getMsg("dc.mdm.sms.unreachable_mobile_title", new Object[0]));
                descriptionJSONArray.put((Object)I18N.getMsg("dc.mdm.sms.error.device_switched_off", new Object[0]));
                descriptionJSONArray.put((Object)I18N.getMsg("dc.mdm.sms.error.carrier_issue", new Object[0]));
                jsonObject.put("smsErrorDescription", (Object)descriptionJSONArray);
                suggestionsJSONArray.put((Object)I18N.getMsg("dc.mdm.sms.suggestion.turn_on_device", new Object[0]));
                suggestionsJSONArray.put((Object)I18N.getMsg("dc.mdm.sms.suggestion.reach_device_locally", new Object[0]));
                jsonObject.put("smsErrorResolvingSuggestions", (Object)suggestionsJSONArray);
                break;
            }
            case 10003: {
                jsonObject.put("hasSMSHookInfo", true);
                jsonObject.put("smsErrorTitle", (Object)I18N.getMsg("dc.mdm.sms.message_blocked_title", new Object[0]));
                descriptionJSONArray.put((Object)I18N.getMsg("dc.mdm.sms.error.number_blocked", new Object[0]));
                descriptionJSONArray.put((Object)I18N.getMsg("dc.mdm.sms.error.number_on_dnc", new Object[0]));
                descriptionJSONArray.put((Object)I18N.getMsg("dc.mdm.sms.error.carrier_issue", new Object[0]));
                jsonObject.put("smsErrorDescription", (Object)descriptionJSONArray);
                suggestionsJSONArray.put((Object)I18N.getMsg("dc.mdm.sms.suggestion.turn_on_device", new Object[0]));
                suggestionsJSONArray.put((Object)I18N.getMsg("dc.mdm.sms.suggestion.reach_device_locally", new Object[0]));
                jsonObject.put("smsErrorResolvingSuggestions", (Object)suggestionsJSONArray);
                break;
            }
            case 10004: {
                jsonObject.put("hasSMSHookInfo", true);
                jsonObject.put("smsErrorTitle", (Object)I18N.getMsg("dc.mdm.sms.unknown_error_title", new Object[0]));
                suggestionsJSONArray.put((Object)I18N.getMsg("dc.mdm.sms.suggestion.turn_on_device", new Object[0]));
                suggestionsJSONArray.put((Object)I18N.getMsg("dc.mdm.sms.suggestion.reach_device_locally", new Object[0]));
                jsonObject.put("smsErrorResolvingSuggestions", (Object)suggestionsJSONArray);
                break;
            }
        }
    }
    
    public JSONArray getDeviceIDForUserId(final JSONObject jsonObject) throws Exception {
        final JSONArray jsonArray = new JSONArray();
        final String email = String.valueOf(jsonObject.get("EMAIL_ADDRESS"));
        final String domain = String.valueOf(jsonObject.get("DOMAIN_NETBIOS_NAME"));
        final Long customerID = Long.valueOf(String.valueOf(jsonObject.get("CUSTOMER_ID")));
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("Resource"));
        selectQuery.addJoin(new Join("Resource", "ManagedUser", new String[] { "RESOURCE_ID" }, new String[] { "MANAGED_USER_ID" }, 2));
        selectQuery.addJoin(new Join("ManagedUser", "ManagedUserToDevice", new String[] { "MANAGED_USER_ID" }, new String[] { "MANAGED_USER_ID" }, 2));
        selectQuery.addJoin(new Join("ManagedUserToDevice", "ManagedDevice", new String[] { "MANAGED_DEVICE_ID" }, new String[] { "RESOURCE_ID" }, 2));
        final Criteria domainCri = new Criteria(Column.getColumn("Resource", "DOMAIN_NETBIOS_NAME"), (Object)domain, 0, false);
        final Criteria customerIDCri = new Criteria(Column.getColumn("Resource", "CUSTOMER_ID"), (Object)customerID, 0);
        final Criteria emailCri = new Criteria(Column.getColumn("ManagedUser", "EMAIL_ADDRESS"), (Object)email, 0);
        final Criteria managedStatusCri = new Criteria(Column.getColumn("ManagedDevice", "MANAGED_STATUS"), (Object)new int[] { 2, 6, 5 }, 8);
        final Criteria userNotInTrashCriteria = new Criteria(Column.getColumn("ManagedUser", "STATUS"), (Object)11, 1);
        selectQuery.setCriteria(domainCri.and(customerIDCri.and(emailCri.and(managedStatusCri))).and(userNotInTrashCriteria));
        selectQuery.addSelectColumn(Column.getColumn("Resource", "RESOURCE_ID", "RESOURCE.RESOURCE_ID"));
        selectQuery.addSelectColumn(Column.getColumn("Resource", "DOMAIN_NETBIOS_NAME"));
        selectQuery.addSelectColumn(Column.getColumn("ManagedUser", "MANAGED_USER_ID", "MANAGEDUSER.MANAGED_USER_ID"));
        selectQuery.addSelectColumn(Column.getColumn("ManagedUserToDevice", "MANAGED_USER_ID"));
        selectQuery.addSelectColumn(Column.getColumn("ManagedUserToDevice", "MANAGED_DEVICE_ID"));
        selectQuery.addSelectColumn(Column.getColumn("ManagedDevice", "RESOURCE_ID"));
        DMDataSetWrapper ds = null;
        ds = DMDataSetWrapper.executeQuery((Object)selectQuery);
        while (ds.next()) {
            jsonArray.put((Object)String.valueOf(ds.getValue("RESOURCE_ID")));
        }
        return jsonArray;
    }
    
    public JSONObject rewakeIosDevices(final Criteria criteria) {
        final JSONObject respJO = new JSONObject();
        boolean apnsResp = false;
        this.logger.log(Level.INFO, "Inside rewakeIosdevices()..");
        try {
            final SelectQuery sq = (SelectQuery)new SelectQueryImpl(new Table("IOSEnrollmentTemp"));
            sq.addSelectColumn(new Column("IOSEnrollmentTemp", "*"));
            Criteria notNullCri = new Criteria(Column.getColumn("IOSEnrollmentTemp", "PUSH_MAGIC"), (Object)null, 1);
            if (criteria != null) {
                notNullCri = notNullCri.and(criteria);
            }
            sq.setCriteria(notNullCri);
            final DataObject dataObject = MDMUtil.getPersistence().get(sq);
            final Iterator it = dataObject.getRows("IOSEnrollmentTemp");
            while (it.hasNext()) {
                final Row r = it.next();
                final String pushMagic = (String)r.get("PUSH_MAGIC");
                final String topic = (String)r.get("TOPIC");
                final String deviceToken = (String)r.get("DEVICE_TOKEN_ENCRYPTED");
                final String udid = (String)r.get("UDID");
                final HashMap hsAdditionalParams = new HashMap();
                hsAdditionalParams.put("ENROLLMENT_REQUEST_ID", null);
                hsAdditionalParams.put("IS_SOURCE_TOKEN_UPDATE", true);
                hsAdditionalParams.put("RESOURCE_ID", ManagedDeviceHandler.getInstance().getResourceIDFromUDID(udid));
                this.logger.log(Level.INFO, "Sending wakeup notification for the device with Udid: {0}", udid);
                apnsResp = APNSImpl.getInstance().wakeUpDeviceWithERID(deviceToken, pushMagic, topic, hsAdditionalParams);
                if (apnsResp) {
                    respJO.put(udid, (Object)"success");
                }
                else {
                    respJO.put(udid, (Object)"failed");
                }
            }
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Exception in rewakeIosDevices() ", e);
        }
        return respJO;
    }
    
    public void hideOrUnHideADMessage(final Long customerId) {
        try {
            final int count = this.getADCount(customerId);
            if (count > 0) {
                MessageProvider.getInstance().hideMessage("MDM_SELF_ENROLLMENT_AD_NOT_CONFIGURED");
            }
            else {
                MessageProvider.getInstance().unhideMessage("MDM_SELF_ENROLLMENT_AD_NOT_CONFIGURED");
            }
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Exception in hideOrUnHideADMessage: ", e);
        }
    }
    
    public int getADCount(final Long customerId) throws Exception {
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("DMDomain"));
        selectQuery.setCriteria(new Criteria(Column.getColumn("DMDomain", "CUSTOMER_ID"), (Object)customerId, 0));
        return DBUtil.getRecordActualCount(selectQuery, "DMDomain", "DOMAIN_ID");
    }
    
    public boolean isValidTemplateType(final int templateType) {
        switch (templateType) {
            case 10:
            case 11:
            case 12:
            case 20:
            case 21:
            case 22:
            case 23:
            case 30:
            case 31:
            case 32:
            case 33:
            case 40: {
                return true;
            }
            default: {
                return false;
            }
        }
    }
    
    public boolean isValidTemplateId(final Long templateId, final Long customerId) {
        try {
            final DataObject dataObject = MDMUtil.getPersistenceLite().get("EnrollmentTemplate", new Criteria(Column.getColumn("EnrollmentTemplate", "TEMPLATE_ID"), (Object)templateId, 0).and(new Criteria(Column.getColumn("EnrollmentTemplate", "CUSTOMER_ID"), (Object)customerId, 0)));
            if (!dataObject.isEmpty()) {
                return true;
            }
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Exception in validating template id");
        }
        return false;
    }
    
    public boolean isDeviceInManagedTab(final Long resourceId, final Long customerId) {
        final Long startTime = MDMUtil.getCurrentTimeInMillis();
        this.logger.log(Level.INFO, "Entering isDeviceInManagedTab in {0}", startTime);
        try {
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("ManagedUser"));
            final Criteria customerCri = new Criteria(Column.getColumn("Resource", "CUSTOMER_ID"), (Object)customerId, 0);
            final Criteria enrollNotSelfReqCri = new Criteria(Column.getColumn("DeviceEnrollmentRequest", "ENROLLMENT_TYPE"), (Object)2, 1);
            final Criteria enrollSelfReqCri = new Criteria(Column.getColumn("DeviceEnrollmentRequest", "ENROLLMENT_TYPE"), (Object)2, 0);
            final Criteria enrollStatusCri = new Criteria(Column.getColumn("DeviceEnrollmentRequest", "REQUEST_STATUS"), (Object)3, 0);
            final Criteria managedDeviceNameCri = new Criteria(Column.getColumn("ManagedDeviceExtn", "NAME"), (Object)null, 1);
            final Criteria selfEnrollDeviceCheckedIn = new Criteria(Column.getColumn("DeviceEnrollmentRequest", "UDID"), (Object)null, 1);
            final Criteria deviceForEnroll = new Criteria(Column.getColumn("DeviceForEnrollment", "ENROLLMENT_DEVICE_ID"), (Object)null, 1);
            final Criteria noDeviceForEnroll = new Criteria(Column.getColumn("DeviceForEnrollment", "ENROLLMENT_DEVICE_ID"), (Object)null, 0);
            final Criteria deviceToUser = new Criteria(Column.getColumn("DeviceEnrollmentToUser", "ENROLLMENT_DEVICE_ID"), (Object)null, 1);
            final Criteria nonTemplateCriteria = new Criteria(Column.getColumn("EnrollmentTemplate", "TEMPLATE_ID"), (Object)null, 0);
            final Criteria templateCriteria = new Criteria(Column.getColumn("EnrollmentTemplate", "TEMPLATE_ID"), (Object)null, 1);
            final Criteria managedStatusCri = new Criteria(Column.getColumn("ManagedDevice", "MANAGED_STATUS"), (Object)new int[] { 2, 4, 5 }, 8);
            final Criteria resourceCri = new Criteria(Column.getColumn("ManagedDevice", "RESOURCE_ID"), (Object)resourceId, 0);
            selectQuery.addJoin(new Join("ManagedUser", "Resource", new String[] { "MANAGED_USER_ID" }, new String[] { "RESOURCE_ID" }, 2));
            selectQuery.addJoin(new Join("ManagedUser", "DeviceEnrollmentRequest", new String[] { "MANAGED_USER_ID" }, new String[] { "MANAGED_USER_ID" }, 2));
            selectQuery.addJoin(new Join("DeviceEnrollmentRequest", "EnrollmentRequestToDevice", new String[] { "ENROLLMENT_REQUEST_ID" }, new String[] { "ENROLLMENT_REQUEST_ID" }, 1));
            selectQuery.addJoin(new Join("EnrollmentRequestToDevice", "ManagedDevice", new String[] { "MANAGED_DEVICE_ID" }, new String[] { "RESOURCE_ID" }, 1));
            selectQuery.addJoin(new Join("ManagedDevice", "ManagedDeviceExtn", new String[] { "RESOURCE_ID" }, new String[] { "MANAGED_DEVICE_ID" }, 1));
            selectQuery.addJoin(new Join("DeviceEnrollmentRequest", "EnrollmentTemplateToRequest", new String[] { "ENROLLMENT_REQUEST_ID" }, new String[] { "ENROLLMENT_REQUEST_ID" }, 1));
            selectQuery.addJoin(new Join("EnrollmentTemplateToRequest", "EnrollmentTemplate", new String[] { "TEMPLATE_ID" }, new String[] { "TEMPLATE_ID" }, 1));
            selectQuery.addJoin(new Join("DeviceEnrollmentRequest", "DeviceEnrollmentToRequest", new String[] { "ENROLLMENT_REQUEST_ID" }, new String[] { "ENROLLMENT_REQUEST_ID" }, 1));
            selectQuery.addJoin(new Join("DeviceEnrollmentToRequest", "DeviceForEnrollment", new String[] { "ENROLLMENT_DEVICE_ID" }, new String[] { "ENROLLMENT_DEVICE_ID" }, 1));
            selectQuery.addJoin(new Join("DeviceForEnrollment", "DeviceEnrollmentToUser", new String[] { "ENROLLMENT_DEVICE_ID" }, new String[] { "ENROLLMENT_DEVICE_ID" }, 1));
            final Criteria allCriteria = customerCri.and(enrollNotSelfReqCri.or(enrollSelfReqCri.and(enrollStatusCri.or(managedDeviceNameCri).or(selfEnrollDeviceCheckedIn)))).and(templateCriteria.and(noDeviceForEnroll.or(deviceForEnroll.and(deviceToUser))).or(nonTemplateCriteria)).and(managedStatusCri).and(resourceCri);
            selectQuery.setCriteria(allCriteria);
            final int deviceCount = DBUtil.getRecordActualCount(selectQuery, "ManagedDevice", "RESOURCE_ID");
            final Long endTime = MDMUtil.getCurrentTimeInMillis();
            this.logger.log(Level.INFO, "Exiting isDeviceInManagedTab in {0}", endTime);
            this.logger.log(Level.INFO, "Time taken for isDeviceInManagedTab is {0} milliseconds", endTime - startTime);
            if (deviceCount > 0) {
                return true;
            }
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Exception in isDeviceInManagedTab ", e);
        }
        return false;
    }
    
    public void clearAssignUserImportInfoDetails(final Long customerId) {
        try {
            MDMUtil.getPersistenceLite().delete(new Criteria(new Column("AssignUserImportInfo", "CUSTOMER_ID"), (Object)customerId, 0));
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, "Exception in clearAssignUserImportInfoDetails ", ex);
        }
    }
    
    private DataObject getDFEDetailsforERIDs(final Long[] enrollRequestIDs) {
        DataObject dfeObject = null;
        try {
            final SelectQuery sQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("DeviceForEnrollment"));
            sQuery.addJoin(new Join("DeviceForEnrollment", "DeviceEnrollmentToRequest", new String[] { "ENROLLMENT_DEVICE_ID" }, new String[] { "ENROLLMENT_DEVICE_ID" }, 2));
            sQuery.addJoin(new Join("DeviceEnrollmentToRequest", "DeviceEnrollmentRequest", new String[] { "ENROLLMENT_REQUEST_ID" }, new String[] { "ENROLLMENT_REQUEST_ID" }, 2));
            sQuery.setCriteria(new Criteria(new Column("DeviceEnrollmentToRequest", "ENROLLMENT_REQUEST_ID"), (Object)enrollRequestIDs, 8));
            sQuery.addSelectColumn(Column.getColumn("DeviceEnrollmentToRequest", "ENROLLMENT_DEVICE_ID"));
            sQuery.addSelectColumn(Column.getColumn("DeviceEnrollmentToRequest", "ENROLLMENT_REQUEST_ID"));
            sQuery.addSelectColumn(Column.getColumn("DeviceEnrollmentRequest", "ENROLLMENT_REQUEST_ID"));
            sQuery.addSelectColumn(Column.getColumn("DeviceForEnrollment", "ENROLLMENT_DEVICE_ID"));
            dfeObject = MDMUtil.getPersistence().get(sQuery);
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, "Exception in getDFEDetailsforERIDs ", ex);
        }
        return dfeObject;
    }
    
    public JSONObject getInactiveDevicePolicyDetailsParam(final Long customerID) {
        JSONObject idpParams = null;
        Long policyID = null;
        Long inactiveThreshold = null;
        Long actionThreshold = null;
        int actionType = 0;
        String actionNameKey = null;
        String actionName = null;
        Long createdBy = null;
        Long addedTime = null;
        Long updatedTime = null;
        try {
            final Criteria customerCri = new Criteria(Column.getColumn("InactiveDevicePolicyDetails", "CUSTOMER_ID"), (Object)customerID, 0);
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("InactiveDevicePolicyDetails"));
            final Join idpActionRelJoin = new Join("InactiveDevicePolicyDetails", "InactiveDevicePolicyActionType", new String[] { "ACTION_TYPE" }, new String[] { "ACTION_TYPE" }, 2);
            selectQuery.addJoin(idpActionRelJoin);
            selectQuery.addSelectColumn(Column.getColumn("InactiveDevicePolicyDetails", "*"));
            selectQuery.addSelectColumn(Column.getColumn("InactiveDevicePolicyActionType", "*"));
            selectQuery.setCriteria(customerCri);
            final DataObject idpDO = MDMUtil.getPersistence().get(selectQuery);
            if (!idpDO.isEmpty()) {
                policyID = (Long)idpDO.getFirstValue("InactiveDevicePolicyDetails", "POLICY_ID");
                inactiveThreshold = (Long)idpDO.getFirstValue("InactiveDevicePolicyDetails", "INACTIVE_THRESHOLD");
                actionThreshold = (Long)idpDO.getFirstValue("InactiveDevicePolicyDetails", "IDP_ACTION_THRESHOLD");
                actionType = (int)idpDO.getFirstValue("InactiveDevicePolicyActionType", "ACTION_TYPE");
                actionNameKey = (String)idpDO.getFirstValue("InactiveDevicePolicyActionType", "ACTION_NAME");
                actionName = I18N.getMsg(actionNameKey, new Object[0]);
                createdBy = (Long)idpDO.getFirstValue("InactiveDevicePolicyDetails", "CREATED_BY");
                addedTime = (Long)idpDO.getFirstValue("InactiveDevicePolicyDetails", "ADDED_TIME");
                updatedTime = (Long)idpDO.getFirstValue("InactiveDevicePolicyDetails", "UPDATED_TIME");
            }
            idpParams = new JSONObject();
            idpParams.put("policy_id", (Object)policyID);
            idpParams.put("inactive_threshold", (Object)inactiveThreshold);
            idpParams.put("action_threshold", (Object)actionThreshold);
            idpParams.put("action_type", actionType);
            idpParams.put("action_name_key", (Object)actionNameKey);
            idpParams.put("action_name", (Object)actionName);
            idpParams.put("created_by", (Object)createdBy);
            idpParams.put("added_time", (Object)addedTime);
            idpParams.put("updated_time", (Object)updatedTime);
        }
        catch (final Exception exp) {
            this.logger.log(Level.WARNING, "Exception in getIDPDetailsParamMap", exp);
        }
        return idpParams;
    }
    
    public void addOrUpdateInactiveDevicePolicySettings(final JSONObject settingsParam) {
        try {
            final Long customerID = (Long)settingsParam.get("customerId");
            final Long userID = (Long)settingsParam.get("userId");
            final Long inactiveThreshold = (Long)settingsParam.get("inactiveThreshold");
            final Long actionThreshold = (Long)settingsParam.get("actionThreshold");
            final int actionType = (int)settingsParam.get("actionType");
            final Long currentTime = (Long)settingsParam.get("currentTime");
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("InactiveDevicePolicyDetails"));
            selectQuery.addSelectColumn(Column.getColumn("InactiveDevicePolicyDetails", "*"));
            final Criteria policyCri = new Criteria(Column.getColumn("InactiveDevicePolicyDetails", "CUSTOMER_ID"), (Object)customerID, 0);
            selectQuery.setCriteria(policyCri);
            final DataObject idpDO = MDMUtil.getPersistence().get(selectQuery);
            if (idpDO.isEmpty()) {
                final Row idpSettingsRow = new Row("InactiveDevicePolicyDetails");
                idpSettingsRow.set("ACTION_TYPE", (Object)actionType);
                idpSettingsRow.set("CUSTOMER_ID", (Object)customerID);
                idpSettingsRow.set("CREATED_BY", (Object)userID);
                idpSettingsRow.set("INACTIVE_THRESHOLD", (Object)inactiveThreshold);
                idpSettingsRow.set("IDP_ACTION_THRESHOLD", (Object)actionThreshold);
                idpSettingsRow.set("ADDED_TIME", (Object)currentTime);
                idpSettingsRow.set("UPDATED_TIME", (Object)currentTime);
                idpDO.addRow(idpSettingsRow);
            }
            else {
                final Row updatePolicyRow = idpDO.getRow("InactiveDevicePolicyDetails");
                updatePolicyRow.set("ACTION_TYPE", (Object)actionType);
                updatePolicyRow.set("INACTIVE_THRESHOLD", (Object)inactiveThreshold);
                updatePolicyRow.set("IDP_ACTION_THRESHOLD", (Object)actionThreshold);
                updatePolicyRow.set("UPDATED_TIME", (Object)currentTime);
                idpDO.updateRow(updatePolicyRow);
            }
            MDMUtil.getPersistence().update(idpDO);
        }
        catch (final Exception exp) {
            this.logger.log(Level.WARNING, "Exception in addOrUpdateIDPSettings", exp);
        }
    }
    
    public void removeInactiveDevicePolicy(final Long customerID) throws Exception {
        try {
            final DeleteQuery delQ = (DeleteQuery)new DeleteQueryImpl("InactiveDevicePolicyDetails");
            final Criteria policyCri = new Criteria(Column.getColumn("InactiveDevicePolicyDetails", "CUSTOMER_ID"), (Object)customerID, 0);
            delQ.setCriteria(policyCri);
            MDMUtil.getPersistence().delete(delQ);
        }
        catch (final Exception ex) {
            this.logger.log(Level.WARNING, " Exception in removeIDP ", ex);
        }
    }
    
    public JSONObject retireInactiveDevices(final List<Long> resourceList, final Long inactiveThreshold, final Long idpActionThreshold, final Long userID, final Long customerID) throws Exception {
        final int wipeReason = 3;
        final long inactiveDayCount = (inactiveThreshold + idpActionThreshold) / 86400000L;
        final String wipeInactiveDeviceReason = I18N.getMsg("mdm.enroll.unassign_inactive_device_remarks", new Object[] { inactiveDayCount });
        final DeprovisionRequest deprovisionRequest = new DeprovisionRequest(customerID, userID, 1, wipeReason, wipeInactiveDeviceReason, resourceList);
        final JSONObject responseJSON = ManagedDeviceHandler.getInstance().deprovisionDevice(deprovisionRequest);
        responseJSON.remove("success");
        return responseJSON;
    }
    
    public boolean isIDPConfigured(final Long customerID) throws Exception {
        boolean isPolicy = false;
        try {
            final Criteria policyCri = new Criteria(Column.getColumn("InactiveDevicePolicyDetails", "CUSTOMER_ID"), (Object)customerID, 0);
            final int policyCount = DBUtil.getRecordCount("InactiveDevicePolicyDetails", "CUSTOMER_ID", policyCri);
            if (policyCount > 0) {
                isPolicy = true;
            }
        }
        catch (final Exception e) {
            this.logger.log(Level.WARNING, " Exception in isIDPConfigured ", e);
        }
        return isPolicy;
    }
    
    public void validateEnrollmentViewSearchText(final String colName, final String colVal) throws SyMException {
        final Pattern safestringregex = Pattern.compile("^[a-zA-Z0-9\\s\\+?!,()@%.\\'\\-:_*\\./\\\\=]+$");
        if (!safestringregex.matcher(colVal).matches()) {
            throw new SyMException(51035, "Invalid search text: Column name:" + colName + " ; Search text : " + colVal, (Throwable)null);
        }
        if (!safestringregex.matcher(colName).matches()) {
            throw new SyMException(51036, "Invalid search column name: Column name:" + colName + " ; Search text : " + colVal, (Throwable)null);
        }
    }
    
    public Boolean isValidInputs(final String value, final String patternType) {
        String validPattern = null;
        switch (patternType) {
            case "IMEI": {
                validPattern = "^-?[\\w]*$";
                break;
            }
            case "SerialNumber":
            case "UDID":
            case "DomainName":
            case "EASID": {
                validPattern = "^[a-zA-Z0-9\\s\\+?!,()@%.\\-:_*\\./\\\\=]+$";
                break;
            }
            case "UserName":
            case "group_name":
            case "DeviceName": {
                validPattern = "[0-9a-zA-Z_\\-\\.\\$@\\,\\:\\&amp;~\\#\\(\\)\\%\\=\\\\^\\+\\?\\'\\/\\|\\!\\P{InBasicLatin}\\s]+";
                break;
            }
            case "EmailAddr": {
                validPattern = "^([\\w-\\$\\+\\'\\!\\#\\%\\*\\-\\/\\=\\?\\^\\_\\.\\{\\|\\}\\~]+(?:\\.[\\w-\\$\\+]+)*)@((?:[\\w-]+\\.)*\\w[\\w-]{0,66})\\.([\\w]{2,12}(?:\\.[\\w]{2})?)$";
                break;
            }
        }
        final Pattern regexPattern = Pattern.compile(validPattern);
        final Matcher matcher = regexPattern.matcher(value);
        return matcher.matches();
    }
    
    public boolean isValuePresentInJSON(final JSONObject jsonObject, final String key) {
        return jsonObject.has(key) && jsonObject.get(key) != null && !((String)jsonObject.get(key)).isEmpty() && !((String)jsonObject.get(key)).equals("--");
    }
    
    static {
        MDMEnrollmentUtil.enrollmentUtil = null;
    }
}
