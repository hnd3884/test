package com.adventnet.sym.server.mdm.core;

import java.util.Hashtable;
import com.me.devicemanagement.framework.server.license.LicenseProvider;
import com.me.mdm.server.license.MDMLicenseImplMSP;
import com.me.mdm.server.customer.MDMCustomerInfoUtil;
import org.json.JSONException;
import com.me.mdm.server.enrollment.MDMEnrollmentDeviceHandler;
import com.me.mdm.core.auth.MDMDeviceTokenGenerator;
import com.me.mdm.server.adep.DEPEnrollmentUtil;
import com.adventnet.sym.server.mdm.group.MDMGroupHandler;
import java.util.Arrays;
import com.me.mdm.server.util.MDMCheckSumProvider;
import com.me.mdm.server.command.kiosk.KioskPauseResumeHandler;
import com.me.mdm.server.command.kiosk.KioskPauseResumeManager;
import com.adventnet.ds.query.CaseExpression;
import com.adventnet.ds.query.Query;
import com.adventnet.ds.query.DerivedTable;
import org.apache.commons.collections.MultiMap;
import com.me.mdm.server.adep.AppleDEPWebServicetHandler;
import org.apache.commons.collections.MultiHashMap;
import com.adventnet.ds.query.DeleteQuery;
import com.adventnet.sym.server.mdm.config.ProfileAssociateHandler;
import com.adventnet.sym.server.mdm.apps.ios.AppleAppLicenseMgmtHandler;
import com.adventnet.ds.query.DeleteQueryImpl;
import com.me.mdm.server.enrollment.MDMEnrollmentRequestHandler;
import com.me.mdm.server.status.ManagedUserCollectionStatusSummary;
import com.me.mdm.server.status.GroupCollectionStatusSummary;
import com.me.mdm.core.enrollment.DEPAdminEnrollmentHandler;
import com.me.mdm.server.conditionalaccess.AzureWinCEAListener;
import com.me.mdm.server.notification.NotificationHandler;
import com.adventnet.sym.server.mdm.command.DeviceInvCommandHandler;
import com.me.mdm.server.onelinelogger.MDMOneLineLogger;
import com.me.devicemanagement.framework.server.customer.CustomerInfoUtil;
import com.adventnet.sym.server.mdm.util.MDMEventLogHandler;
import com.adventnet.sym.server.mdm.security.RemoteWipeHandler;
import com.me.mdm.server.ios.apns.APNsCertificateHandler;
import com.adventnet.sym.server.mdm.DeviceDetails;
import com.me.mdm.server.enrollment.deprovision.DeprovisionRequest;
import com.me.mdm.server.factory.MDMApiFactoryProvider;
import java.util.HashSet;
import java.util.Set;
import com.adventnet.sym.server.mdm.util.MDMCommonConstants;
import com.adventnet.sym.server.mdm.iosnativeapp.IosNativeAppHandler;
import com.adventnet.sym.server.mdm.inv.InventoryUtil;
import java.util.StringTokenizer;
import com.adventnet.sym.server.mdm.util.MDMStringUtils;
import com.me.devicemanagement.framework.server.logger.DMSecurityLogger;
import com.me.mdm.server.apps.android.afw.usermgmt.GoogleManagedAccountHandler;
import com.me.devicemanagement.framework.server.util.Utils;
import java.util.Date;
import java.util.Calendar;
import com.me.mdm.server.settings.MDMAgentSettingsHandler;
import com.adventnet.ds.query.Range;
import com.adventnet.ds.query.SortColumn;
import com.adventnet.persistence.DataAccess;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import com.adventnet.sym.server.mdm.util.VersionChecker;
import com.adventnet.ds.query.GroupByClause;
import com.me.devicemanagement.framework.server.customer.CustomerInfoThreadLocal;
import com.me.mdm.server.util.MDMFeatureParamsHandler;
import com.adventnet.persistence.DataAccessException;
import com.adventnet.ds.query.DMDataSetWrapper;
import com.me.mdm.server.role.RBDAUtil;
import com.me.devicemanagement.framework.server.authentication.DMUserHandler;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import org.json.JSONArray;
import java.util.List;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.ds.query.UpdateQuery;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.adventnet.sym.server.mdm.command.DeviceCommandRepository;
import com.me.devicemanagement.framework.server.util.DBUtil;
import com.adventnet.ds.query.UpdateQueryImpl;
import com.adventnet.sym.server.mdm.inv.MDCustomDetailsRequestHandler;
import com.adventnet.persistence.Row;
import com.adventnet.persistence.DataObject;
import com.me.devicemanagement.framework.server.exception.SyMException;
import com.me.devicemanagement.framework.server.logger.SyMLogger;
import com.adventnet.sym.server.mdm.enroll.MDMEnrollmentUtil;
import com.me.mdm.server.resource.MDMResourceDataPopulator;
import java.util.Properties;
import com.adventnet.sym.server.mdm.util.JSONUtil;
import com.adventnet.i18n.I18N;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import java.util.logging.Level;
import org.json.JSONObject;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.logging.Logger;

public class ManagedDeviceHandler
{
    Logger logger;
    Logger enrollmentLogger;
    String sourceClass;
    private static ManagedDeviceHandler deviceHandler;
    public static final int DEVICE_PRE_REGISTER = 0;
    public static final int DEVICE_MANAGED = 1;
    public static final int DEVICE_PRE_REMOVE = 2;
    public static final int DEVICE_UNMANAGED = 3;
    public static final int DEVICE_REMOVED = 4;
    public static final int DEVICE_REGISTERED = 5;
    public static final int DEVICE_USER_CHANGED = 6;
    public static final int DEVICE_DETAIL_CHANGE = 7;
    public static final int DEVICE_PRE_USER_ASSIGNED = 8;
    public static final int DEVICE_DEPROVISIONED = 9;
    public static final int DEVICE_POST_SCAN = 10;
    private ArrayList<ManagedDeviceListener> deviceListenerList;
    
    private ManagedDeviceHandler() {
        this.logger = Logger.getLogger("MDMLogger");
        this.enrollmentLogger = Logger.getLogger("MDMEnrollment");
        this.sourceClass = ManagedDeviceHandler.class.getName();
        this.deviceListenerList = null;
        this.deviceListenerList = new ArrayList<ManagedDeviceListener>();
    }
    
    public static ManagedDeviceHandler getInstance() {
        if (ManagedDeviceHandler.deviceHandler == null) {
            ManagedDeviceHandler.deviceHandler = new ManagedDeviceHandler();
        }
        return ManagedDeviceHandler.deviceHandler;
    }
    
    public void addManagedDeviceListener(final ManagedDeviceListener deviceListener) {
        this.deviceListenerList.add(deviceListener);
    }
    
    public void invokeDeviceListeners(final DeviceEvent deviceEvent, final int operation) {
        final int l = this.deviceListenerList.size();
        if (operation == 0) {
            for (int s = 0; s < l; ++s) {
                final ManagedDeviceListener listener = this.deviceListenerList.get(s);
                listener.devicePreRegister(deviceEvent);
            }
        }
        else if (operation == 5) {
            for (int s = 0; s < l; ++s) {
                final ManagedDeviceListener listener = this.deviceListenerList.get(s);
                listener.deviceRegistered(deviceEvent);
            }
        }
        else if (operation == 1) {
            for (int s = 0; s < l; ++s) {
                final ManagedDeviceListener listener = this.deviceListenerList.get(s);
                listener.deviceManaged(deviceEvent);
            }
        }
        else if (operation == 2) {
            for (int s = 0; s < l; ++s) {
                final ManagedDeviceListener listener = this.deviceListenerList.get(s);
                listener.devicePreDelete(deviceEvent);
            }
        }
        else if (operation == 4) {
            for (int s = 0; s < l; ++s) {
                final ManagedDeviceListener listener = this.deviceListenerList.get(s);
                listener.deviceDeleted(deviceEvent);
            }
        }
        else if (operation == 3) {
            for (int s = 0; s < l; ++s) {
                final ManagedDeviceListener listener = this.deviceListenerList.get(s);
                listener.deviceUnmanaged(deviceEvent);
            }
        }
        else if (operation == 6) {
            for (int s = 0; s < l; ++s) {
                final ManagedDeviceListener listener = this.deviceListenerList.get(s);
                listener.userAssigned(deviceEvent);
            }
        }
        else if (operation == 7) {
            for (int s = 0; s < l; ++s) {
                final ManagedDeviceListener listener = this.deviceListenerList.get(s);
                listener.deviceDetailChanged(deviceEvent);
            }
        }
        else if (operation == 8) {
            for (int s = 0; s < l; ++s) {
                final ManagedDeviceListener listener = this.deviceListenerList.get(s);
                listener.devicePreUserAssigned(deviceEvent);
            }
        }
        else if (operation == 9) {
            for (final ManagedDeviceListener listener : this.deviceListenerList) {
                listener.deviceDeprovisioned(deviceEvent);
            }
        }
        else if (operation == 10) {
            for (int s = 0; s < l; ++s) {
                final ManagedDeviceListener listener = this.deviceListenerList.get(s);
                listener.devicePostScan(deviceEvent);
            }
        }
    }
    
    public void addOrUpdateManagedDevice(final JSONObject deviceJson) throws SyMException {
        this.enrollmentLogger.log(Level.INFO, "ManagedDevice Properties : {0}", deviceJson.toString());
        final String sourceMethod = "addOrUpdateManagedDevice";
        try {
            final String deviceUDID = String.valueOf(deviceJson.get("UDID"));
            final Criteria criteria = new Criteria(Column.getColumn("ManagedDevice", "UDID"), (Object)deviceUDID, 0, false);
            final DataObject managedDeviceDO = MDMUtil.getPersistence().get("ManagedDevice", criteria);
            if (managedDeviceDO.isEmpty()) {
                final String deviceName = deviceJson.optString("NAME", I18N.getMsg("mdm.common.DEFAULT_DEVICE_NAME", new Object[0]));
                final String domainNetbiosName = String.valueOf(deviceJson.get("DOMAIN_NETBIOS_NAME"));
                final String resourceType = deviceJson.optString("RESOURCE_TYPE", String.valueOf(120));
                final Long customerID = JSONUtil.optLongForUVH(deviceJson, "CUSTOMER_ID", (Long)null);
                Long managedDeviceID = null;
                if (deviceName != null && domainNetbiosName != null && customerID != null) {
                    final Properties properties = new Properties();
                    ((Hashtable<String, String>)properties).put("UDID", deviceUDID);
                    ((Hashtable<String, String>)properties).put("NAME", deviceName);
                    ((Hashtable<String, String>)properties).put("DOMAIN_NETBIOS_NAME", domainNetbiosName);
                    properties.setProperty("RESOURCE_TYPE", resourceType);
                    ((Hashtable<String, Long>)properties).put("CUSTOMER_ID", customerID);
                    final DataObject dataObject = MDMResourceDataPopulator.addOrUpdateResourceForMDMDevice(properties);
                    if (!dataObject.isEmpty()) {
                        managedDeviceID = (Long)dataObject.getFirstValue("Resource", "RESOURCE_ID");
                        deviceJson.put("RESOURCE_ID", (Object)managedDeviceID);
                        this.addOrUpdateManagedDeviceRow(deviceJson);
                    }
                }
            }
            else {
                final Long managedDeviceID2 = (Long)managedDeviceDO.getFirstValue("ManagedDevice", "RESOURCE_ID");
                deviceJson.put("RESOURCE_ID", (Object)managedDeviceID2);
                final Integer managedStatusFromDB = (Integer)managedDeviceDO.getFirstValue("ManagedDevice", "MANAGED_STATUS");
                final int managedStatusFromDevice = deviceJson.getInt("MANAGED_STATUS");
                if (managedStatusFromDB != null && managedStatusFromDB == managedStatusFromDevice) {
                    deviceJson.remove("REMARKS");
                }
                final Long customerID = deviceJson.getLong("CUSTOMER_ID");
                if (MDMEnrollmentUtil.getInstance().isLicenseLimitReached(customerID)) {
                    deviceJson.put("MANAGED_STATUS", 6);
                }
                MDMResourceDataPopulator.renameResource(managedDeviceID2, deviceJson.optString("NAME", I18N.getMsg("mdm.common.DEFAULT_DEVICE_NAME", new Object[0])));
                this.addOrUpdateManagedDeviceRow(deviceJson);
            }
        }
        catch (final Exception exp) {
            SyMLogger.error(this.logger, this.sourceClass, sourceMethod, "Exception occurred during Managed Device add or update !!!", (Throwable)exp);
            throw new SyMException(1001, (Throwable)exp);
        }
    }
    
    private void addOrUpdateManagedDeviceRow(final JSONObject deviceJSON) throws Exception {
        this.enrollmentLogger.log(Level.INFO, "ManagedDevice Properties while entering addOrUpdateManagedDeviceRow: {0}", deviceJSON.toString());
        final long registeredTime = System.currentTimeMillis();
        final long managedDeviceID = JSONUtil.optLongForUVH(deviceJSON, "RESOURCE_ID", (Long)null);
        final long erid = deviceJSON.optLong("ENROLLMENT_REQUEST_ID");
        final int ownedBy = MDMEnrollmentUtil.getInstance().getOwnedByforEnrollmentRequest(erid);
        final Criteria criteria = new Criteria(Column.getColumn("ManagedDevice", "RESOURCE_ID"), (Object)managedDeviceID, 0, false);
        DataObject managedDeviceDO = MDMUtil.getPersistence().get("ManagedDevice", criteria);
        Row managedDeviceRow = null;
        if (managedDeviceDO.isEmpty()) {
            managedDeviceRow = new Row("ManagedDevice");
            managedDeviceRow.set("RESOURCE_ID", (Object)managedDeviceID);
            managedDeviceRow.set("UDID", (Object)String.valueOf(deviceJSON.get("UDID")));
            managedDeviceRow.set("PLATFORM_TYPE", (Object)deviceJSON.optInt("PLATFORM_TYPE", 1));
            managedDeviceRow.set("MANAGED_STATUS", (Object)deviceJSON.optInt("MANAGED_STATUS", 2));
            managedDeviceRow.set("OWNED_BY", (Object)deviceJSON.optInt("OWNED_BY", ownedBy));
            managedDeviceRow.set("REGISTERED_TIME", (Object)registeredTime);
            managedDeviceRow.set("ADDED_TIME", (Object)registeredTime);
            managedDeviceRow.set("AGENT_TYPE", (Object)deviceJSON.optInt("AGENT_TYPE"));
            managedDeviceRow.set("REMARKS", (Object)deviceJSON.optString("REMARKS"));
            managedDeviceRow.set("AGENT_VERSION", (Object)deviceJSON.optString("AGENT_VERSION", "--"));
            managedDeviceRow.set("AGENT_VERSION_CODE", (Object)deviceJSON.optLong("AGENT_VERSION_CODE", -1L));
            managedDeviceDO.addRow(managedDeviceRow);
        }
        else {
            managedDeviceRow = managedDeviceDO.getFirstRow("ManagedDevice");
            if (deviceJSON.optInt("OWNED_BY", 0) != 0 && (int)managedDeviceRow.get("OWNED_BY") != deviceJSON.optInt("OWNED_BY", 0)) {
                managedDeviceRow.set("OWNED_BY", (Object)deviceJSON.optInt("OWNED_BY", ownedBy));
            }
            if (deviceJSON.optInt("PLATFORM_TYPE", 0) != 0) {
                managedDeviceRow.set("PLATFORM_TYPE", (Object)deviceJSON.getInt("PLATFORM_TYPE"));
            }
            final Integer lastAgentType = (Integer)managedDeviceDO.getFirstValue("ManagedDevice", "AGENT_TYPE");
            if (lastAgentType != null && lastAgentType != deviceJSON.optInt("AGENT_TYPE", (int)lastAgentType)) {
                managedDeviceRow.set("AGENT_TYPE", (Object)deviceJSON.optInt("AGENT_TYPE"));
            }
            if (deviceJSON.optInt("MANAGED_STATUS", 0) == 2) {
                managedDeviceRow.set("REGISTERED_TIME", (Object)registeredTime);
                managedDeviceRow.set("ADDED_TIME", (Object)registeredTime);
            }
            if (deviceJSON.optInt("MANAGED_STATUS", 0) != 0 && (int)managedDeviceRow.get("MANAGED_STATUS") != deviceJSON.optInt("MANAGED_STATUS", 0)) {
                managedDeviceRow.set("MANAGED_STATUS", (Object)deviceJSON.optInt("MANAGED_STATUS"));
            }
            if (deviceJSON.optLong("UNREGISTERED_TIME", 0L) != 0L) {
                managedDeviceRow.set("UNREGISTERED_TIME", (Object)deviceJSON.optLong("UNREGISTERED_TIME"));
            }
            if (!deviceJSON.optString("REMARKS", "--").equals("--")) {
                managedDeviceRow.set("REMARKS", (Object)deviceJSON.optString("REMARKS"));
            }
            managedDeviceRow.set("AGENT_VERSION", (Object)deviceJSON.optString("AGENT_VERSION", "--"));
            managedDeviceRow.set("AGENT_VERSION_CODE", (Object)deviceJSON.optLong("AGENT_VERSION_CODE", -1L));
            managedDeviceDO.updateRow(managedDeviceRow);
        }
        managedDeviceDO = MDMUtil.getPersistence().update(managedDeviceDO);
        final org.json.simple.JSONObject singleDeviceDetails = new org.json.simple.JSONObject();
        singleDeviceDetails.put((Object)"MANAGED_DEVICE_ID", (Object)Long.parseLong(deviceJSON.get("RESOURCE_ID").toString()));
        singleDeviceDetails.put((Object)"NAME", (Object)deviceJSON.optString("NAME", I18N.getMsg("mdm.common.DEFAULT_DEVICE_NAME", new Object[0])));
        singleDeviceDetails.put((Object)"IS_MODIFIED", (Object)false);
        singleDeviceDetails.put((Object)"ENROLLMENT_REQUEST_ID", (Object)deviceJSON.optLong("ENROLLMENT_REQUEST_ID", 0L));
        final String genericID = deviceJSON.optString("GENERIC_IDENTIFIER");
        if (genericID != null) {
            singleDeviceDetails.put((Object)"GENERIC_IDENTIFIER", (Object)genericID);
        }
        MDCustomDetailsRequestHandler.getInstance().addOrUpdateCustomDeviceDetails(singleDeviceDetails);
        deviceJSON.put("RESOURCE_ID", managedDeviceDO.getValue("ManagedDevice", "RESOURCE_ID", (Criteria)null));
        this.enrollmentLogger.log(Level.INFO, "ManagedDevice Properties while exiting addOrUpdateManagedDeviceRow: {0}", deviceJSON.toString());
    }
    
    public void updateManagedDeviceDetails(final JSONObject jsonObject) throws Exception {
        final Iterator<String> keyset = jsonObject.keys();
        final Properties properties = new Properties();
        while (keyset.hasNext()) {
            final String key = keyset.next();
            ((Hashtable<String, Object>)properties).put(key, jsonObject.get(key));
        }
        this.updateManagedDeviceDetails(properties);
    }
    
    public void updateManagedDeviceDetails(final Properties prop) throws Exception {
        final UpdateQuery uQuery = (UpdateQuery)new UpdateQueryImpl("ManagedDevice");
        String UDID = ((Hashtable<K, String>)prop).get("UDID");
        Long managedDeviceId = ((Hashtable<K, Long>)prop).get("RESOURCE_ID");
        this.logger.log(Level.INFO, "Going to update the managed device details:{0}", new Object[] { managedDeviceId });
        if (managedDeviceId != null) {
            final Criteria cManagedDeviceId = new Criteria(new Column("ManagedDevice", "RESOURCE_ID"), (Object)managedDeviceId, 0);
            uQuery.setCriteria(cManagedDeviceId);
            if (UDID == null) {
                UDID = (String)DBUtil.getValueFromDB("ManagedDevice", "RESOURCE_ID", (Object)managedDeviceId, "UDID");
            }
        }
        else {
            final Criteria cUdid = new Criteria(new Column("ManagedDevice", "UDID"), (Object)UDID, 0);
            uQuery.setCriteria(cUdid);
            managedDeviceId = (Long)DBUtil.getValueFromDB("ManagedDevice", "UDID", (Object)UDID, "RESOURCE_ID");
        }
        Boolean updateStatus = false;
        Boolean wipeInitiated = false;
        final int existingStatus = (int)DBUtil.getValueFromDB("ManagedDevice", "RESOURCE_ID", (Object)managedDeviceId, "MANAGED_STATUS");
        final Integer managedStatus = ((Hashtable<K, Integer>)prop).get("MANAGED_STATUS");
        Label_0349: {
            if (managedStatus != null) {
                if ((managedStatus == 4 || managedStatus == 9 || managedStatus == 11 || managedStatus == 10) && (existingStatus == 4 || existingStatus == 7)) {
                    if (prop.get("WipeCmdFromServer") == null) {
                        break Label_0349;
                    }
                    if (!((Hashtable<K, Boolean>)prop).get("WipeCmdFromServer")) {
                        break Label_0349;
                    }
                }
                uQuery.setUpdateColumn("MANAGED_STATUS", (Object)managedStatus);
                final String remarks = ((Hashtable<K, String>)prop).get("REMARKS");
                if (remarks != null) {
                    uQuery.setUpdateColumn("REMARKS", (Object)remarks);
                }
                updateStatus = true;
                if (prop.get("wipeInitiated") != null) {
                    wipeInitiated = ((Hashtable<K, Boolean>)prop).get("wipeInitiated");
                }
            }
        }
        final Long unregisteredTime = ((Hashtable<K, Long>)prop).get("UNREGISTERED_TIME");
        if (unregisteredTime != null) {
            uQuery.setUpdateColumn("UNREGISTERED_TIME", (Object)unregisteredTime);
        }
        final Integer ownedBy = ((Hashtable<K, Integer>)prop).get("OWNED_BY");
        if (ownedBy != null) {
            uQuery.setUpdateColumn("OWNED_BY", (Object)ownedBy);
        }
        final Long agentVersionCode = ((Hashtable<K, Long>)prop).get("AGENT_VERSION_CODE");
        if (agentVersionCode != null) {
            uQuery.setUpdateColumn("AGENT_VERSION_CODE", (Object)agentVersionCode);
        }
        final String agentVersion = ((Hashtable<K, String>)prop).get("AGENT_VERSION");
        if (agentVersion != null) {
            uQuery.setUpdateColumn("AGENT_VERSION", (Object)agentVersion);
        }
        final String notifiedVersion = prop.getProperty("NOTIFIED_AGENT_VERSION", null);
        if (notifiedVersion != null) {
            uQuery.setUpdateColumn("NOTIFIED_AGENT_VERSION", (Object)notifiedVersion);
        }
        if (managedStatus != null && (managedStatus == 4 || ((managedStatus == 9 || managedStatus == 11 || managedStatus == 10) && !wipeInitiated)) && !DeviceCommandRepository.getInstance().hasDeviceCommandInCacheOrRepo(UDID) && this.removeDeviceInTrash(getInstance().getResourceIDFromUDID(UDID))) {
            return;
        }
        this.updateManagedDeviceExtnUpdatedTime(managedDeviceId);
        MDMUtil.getPersistence().update(uQuery);
        if (managedStatus != null && (managedStatus == 4 || ((managedStatus == 9 || managedStatus == 11 || managedStatus == 10) && !wipeInitiated))) {
            if (prop.get("WipeCmdFromServer") != null) {
                if (((Hashtable<K, Boolean>)prop).get("WipeCmdFromServer")) {
                    return;
                }
            }
            if (updateStatus) {
                final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("Resource"));
                selectQuery.addJoin(new Join("Resource", "ManagedDevice", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2));
                selectQuery.addJoin(new Join("ManagedDevice", "EnrollmentRequestToDevice", new String[] { "RESOURCE_ID" }, new String[] { "MANAGED_DEVICE_ID" }, 2));
                selectQuery.setCriteria(new Criteria(Column.getColumn("ManagedDevice", "RESOURCE_ID"), (Object)managedDeviceId, 0));
                selectQuery.addSelectColumn(Column.getColumn((String)null, "*"));
                final DataObject DO = MDMUtil.getPersistence().get(selectQuery);
                final Long erid = (Long)DO.getFirstValue("EnrollmentRequestToDevice", "ENROLLMENT_REQUEST_ID");
                final DeviceEvent deviceEvent = new DeviceEvent(managedDeviceId);
                if (erid != null) {
                    deviceEvent.enrollmentRequestId = erid;
                }
                final Long customerID = (Long)DO.getFirstValue("Resource", "CUSTOMER_ID");
                deviceEvent.customerID = customerID;
                if (UDID != null) {
                    deviceEvent.udid = UDID;
                }
                deviceEvent.platformType = ((Hashtable<K, Integer>)prop).get("PLATFORM_TYPE");
                final Boolean isMigrated = ((Hashtable<K, Boolean>)prop).getOrDefault("IsMigrated", false);
                final JSONObject deviceEventResourceJSON = new JSONObject();
                deviceEventResourceJSON.put("IsMigrated", (Object)isMigrated);
                deviceEvent.resourceJSON = deviceEventResourceJSON;
                getInstance().invokeDeviceListeners(deviceEvent, 3);
            }
        }
    }
    
    public boolean bulkUpdateManagedDeviceDetails(final JSONObject details) throws Exception {
        final UpdateQuery uQuery = (UpdateQuery)new UpdateQueryImpl("ManagedDevice");
        JSONArray arr = details.optJSONArray("resourceIds");
        final List<Long> resourceIds = new ArrayList<Long>();
        for (int i = 0; i < arr.length(); ++i) {
            resourceIds.add(arr.optLong(i));
        }
        if (details.opt("resourceIds") != null) {
            final Criteria criteria = new Criteria(new Column("ManagedDevice", "RESOURCE_ID"), (Object)resourceIds.toArray(), 8);
            uQuery.setCriteria(criteria);
        }
        if (details.has("MANAGED_STATUS")) {
            uQuery.setUpdateColumn("MANAGED_STATUS", (Object)details.optInt("MANAGED_STATUS"));
        }
        if (details.has("REMARKS")) {
            uQuery.setUpdateColumn("REMARKS", (Object)details.optString("REMARKS"));
        }
        if (details.has("OWNED_BY")) {
            uQuery.setUpdateColumn("OWNED_BY", (Object)details.optInt("OWNED_BY"));
        }
        if (details.has("AGENT_VERSION_CODE")) {
            uQuery.setUpdateColumn("AGENT_VERSION_CODE", (Object)details.optInt("AGENT_VERSION_CODE"));
        }
        if (details.has("AGENT_VERSION")) {
            uQuery.setUpdateColumn("AGENT_VERSION", (Object)details.optInt("AGENT_VERSION"));
        }
        if (details.has("NOTIFIED_AGENT_VERSION")) {
            uQuery.setUpdateColumn("NOTIFIED_AGENT_VERSION", (Object)details.optInt("NOTIFIED_AGENT_VERSION"));
        }
        MDMUtil.getPersistence().update(uQuery);
        if (details.has("MANAGED_STATUS") && details.optInt("MANAGED_STATUS") == 4) {
            arr = details.optJSONArray("requestIds");
            final List<Long> erids = new ArrayList<Long>();
            for (int j = 0; j < arr.length(); ++j) {
                erids.add(arr.optLong(j));
            }
            final List udids = this.getManagedDeviceIdFromErids(erids, "UDID");
            final Long customerID = details.optLong("customer_id");
            for (int k = 0; k < erids.size(); ++k) {
                final Long managedDeviceId = Long.parseLong(resourceIds.get(k).toString());
                final Long erid = Long.parseLong(erids.get(k).toString());
                final String UDID = udids.get(k).toString();
                final DeviceEvent deviceEvent = new DeviceEvent(managedDeviceId);
                deviceEvent.enrollmentRequestId = erid;
                deviceEvent.customerID = customerID;
                if (UDID != null) {
                    deviceEvent.udid = UDID;
                }
                deviceEvent.platformType = details.optInt("PLATFORM_TYPE");
                getInstance().invokeDeviceListeners(deviceEvent, 3);
            }
        }
        return true;
    }
    
    public ArrayList getManagedDeviceResourceIDs() {
        return this.getManagedDeviceResourceIDs(null);
    }
    
    public ArrayList getAndroidManagedDeviceResourceIDs() {
        final Column deviceTypeColumn = Column.getColumn("ManagedDevice", "PLATFORM_TYPE");
        final Criteria deviceTypeCriteria = new Criteria(deviceTypeColumn, (Object)2, 0);
        return this.getManagedDeviceResourceIDs(deviceTypeCriteria);
    }
    
    public ArrayList getAndroidUnManagedDeviceResourceIDs() {
        final Column deviceTypeColumn = Column.getColumn("ManagedDevice", "PLATFORM_TYPE");
        final Criteria deviceTypeCriteria = new Criteria(deviceTypeColumn, (Object)2, 0);
        return this.getUnManagedDeviceResourceIDs(deviceTypeCriteria);
    }
    
    public ArrayList getAndroidApkSilentInstallResources(List deviceList) {
        if (deviceList == null) {
            deviceList = this.getAndroidManagedDeviceResourceIDs();
        }
        final Criteria samsungCriteria = new Criteria(new Column("ManagedDevice", "AGENT_TYPE"), (Object)3, 0);
        final Criteria osVersionNot5Criteria = new Criteria(new Column("MdDeviceInfo", "OS_VERSION"), (Object)"5.", 10).negate();
        final Criteria deviceOwnerCriteria = new Criteria(new Column("MdDeviceInfo", "IS_SUPERVISED"), (Object)true, 0);
        final Criteria resourceIdCriteria = new Criteria(new Column("ManagedDevice", "RESOURCE_ID"), (Object)deviceList.toArray(), 8);
        final Criteria cEnterpriseSilentInstall = resourceIdCriteria.and(samsungCriteria.or(deviceOwnerCriteria.and(osVersionNot5Criteria)));
        return this.getManagedResourcesWithDeviceInfo(cEnterpriseSilentInstall);
    }
    
    public ArrayList getIOSManagedDeviceResourceIDs() {
        final Column deviceTypeColumn = Column.getColumn("ManagedDevice", "PLATFORM_TYPE");
        final Criteria deviceTypeCriteria = new Criteria(deviceTypeColumn, (Object)1, 0);
        return this.getManagedDeviceResourceIDs(deviceTypeCriteria);
    }
    
    public ArrayList getWindowsManagedDeviceResourceIDs(final List resourceList) {
        final Column deviceTypeColumn = Column.getColumn("ManagedDevice", "PLATFORM_TYPE");
        Criteria deviceTypeCriteria = new Criteria(deviceTypeColumn, (Object)3, 0);
        if (resourceList != null) {
            final Criteria resourceIDCriteria = new Criteria(Column.getColumn("ManagedDevice", "RESOURCE_ID"), (Object)resourceList.toArray(), 8);
            deviceTypeCriteria = deviceTypeCriteria.and(resourceIDCriteria);
        }
        return this.getManagedDeviceResourceIDs(deviceTypeCriteria);
    }
    
    public List getWindows81AboveManagedDeviceResourceIDs(List deviceList) {
        if (deviceList == null) {
            deviceList = this.getWindowsManagedDeviceResourceIDs(null);
        }
        List windows81AboveDeviceResourceIDs = new ArrayList();
        final Criteria osVersion8AndAboveCriteria = new Criteria(new Column("MdDeviceInfo", "OS_VERSION"), (Object)"8.0*", 10).negate();
        final Criteria resourceIdCriteria = new Criteria(new Column("ManagedDevice", "RESOURCE_ID"), (Object)deviceList.toArray(), 8);
        windows81AboveDeviceResourceIDs = this.getManagedDeviceIdBasedOnOSVersion(osVersion8AndAboveCriteria.and(resourceIdCriteria));
        return windows81AboveDeviceResourceIDs;
    }
    
    public List getWindows81AboveWNSAllowedDeviceResourceIDs() {
        final List windows81AboveWNSAllowedDeviceResourceIDs = new ArrayList();
        final Criteria nativeserviceCriteria = new Criteria(new Column("MDCommunicationMode", "SERVICE_TYPE"), (Object)1, 0);
        final Criteria osVersion8AndAboveCriteria = new Criteria(new Column("MdDeviceInfo", "OS_VERSION"), (Object)"8.0*", 10).negate();
        final Criteria windowsPlatformCriteria = new Criteria(new Column("MDCommunicationMode", "PLATFORM_TYPE"), (Object)3, 0);
        final Criteria devicePlatformCriteria = new Criteria(new Column("ManagedDevice", "PLATFORM_TYPE"), (Object)3, 0);
        try {
            final SelectQuery sQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("ManagedDevice"));
            sQuery.addJoin(new Join("ManagedDevice", "MdDeviceInfo", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2));
            sQuery.addJoin(new Join("ManagedDevice", "Resource", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2));
            sQuery.addJoin(new Join("Resource", "MDCommunicationMode", new String[] { "CUSTOMER_ID" }, new String[] { "CUSTOMER_ID" }, 2));
            sQuery.setCriteria(devicePlatformCriteria.and(osVersion8AndAboveCriteria.and(nativeserviceCriteria.and(windowsPlatformCriteria))));
            sQuery.addSelectColumn(Column.getColumn("ManagedDevice", "*"));
            final DataObject DO = MDMUtil.getPersistence().get(sQuery);
            if (!DO.isEmpty()) {
                final Iterator item = DO.getRows("ManagedDevice");
                while (item.hasNext()) {
                    final Row mdRow = item.next();
                    windows81AboveWNSAllowedDeviceResourceIDs.add(mdRow.get("RESOURCE_ID"));
                }
            }
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Exception in getWindows81AboveWNSAllowedDeviceResourceIDs", e);
        }
        return windows81AboveWNSAllowedDeviceResourceIDs;
    }
    
    public List getWindows81AboveWNSAllowedAgentInstalledResourceIDs() {
        final List windows81AboveWNSAllowedDeviceResourceIDs = new ArrayList();
        final Criteria nativeserviceCriteria = new Criteria(new Column("MDCommunicationMode", "SERVICE_TYPE"), (Object)1, 0);
        final Criteria osVersion8AndAboveCriteria = new Criteria(new Column("MdDeviceInfo", "OS_VERSION"), (Object)"8.0*", 10).negate();
        final Criteria windowsPlatformCriteria = new Criteria(new Column("MDCommunicationMode", "PLATFORM_TYPE"), (Object)3, 0);
        final Criteria devicePlatformCriteria = new Criteria(new Column("ManagedDevice", "PLATFORM_TYPE"), (Object)3, 0);
        final Criteria agentVersionNotAsHyphensCri = new Criteria(new Column("ManagedDevice", "AGENT_VERSION"), (Object)"--", 0).negate();
        final Criteria nonEmptyAgentVersionCri = new Criteria(new Column("ManagedDevice", "AGENT_VERSION"), (Object)"", 0).negate();
        final Criteria agentInstalledCri = agentVersionNotAsHyphensCri.and(nonEmptyAgentVersionCri);
        try {
            final SelectQuery sQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("ManagedDevice"));
            sQuery.addJoin(new Join("ManagedDevice", "MdDeviceInfo", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2));
            sQuery.addJoin(new Join("ManagedDevice", "Resource", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2));
            sQuery.addJoin(new Join("Resource", "MDCommunicationMode", new String[] { "CUSTOMER_ID" }, new String[] { "CUSTOMER_ID" }, 2));
            sQuery.setCriteria(devicePlatformCriteria.and(osVersion8AndAboveCriteria.and(nativeserviceCriteria.and(windowsPlatformCriteria.and(agentInstalledCri)))));
            sQuery.addSelectColumn(Column.getColumn("ManagedDevice", "*"));
            final DataObject DO = MDMUtil.getPersistence().get(sQuery);
            if (!DO.isEmpty()) {
                final Iterator item = DO.getRows("ManagedDevice");
                while (item.hasNext()) {
                    final Row mdRow = item.next();
                    windows81AboveWNSAllowedDeviceResourceIDs.add(mdRow.get("RESOURCE_ID"));
                }
            }
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Exception in getWindows81AboveWNSAllowedAgentInstalledResourceIDs", e);
        }
        return windows81AboveWNSAllowedDeviceResourceIDs;
    }
    
    public Map<String, List> getWindows8AndWindows81DevicesAsSeparateList(final List resourceList) {
        final HashMap<String, List> devicesListMap = new HashMap<String, List>();
        final List windows81AboveDevices = this.getWindows81AboveManagedDeviceResourceIDs(resourceList);
        devicesListMap.put("windows81AboveDevices", windows81AboveDevices);
        final List windows8Devices = this.getWindowsManagedDeviceResourceIDs(resourceList);
        windows8Devices.removeAll(windows81AboveDevices);
        devicesListMap.put("windows8Devices", windows8Devices);
        return devicesListMap;
    }
    
    public List getWindows81AndBelowManagedDeviceResourceIDs(List deviceList) {
        if (deviceList == null) {
            deviceList = this.getWindowsManagedDeviceResourceIDs(null);
        }
        List windows81BelowDeviceResourceIDs = new ArrayList();
        final Criteria osVersion81AndBelowCriteria = new Criteria(new Column("MdDeviceInfo", "OS_VERSION"), (Object)"8.*", 10);
        final Criteria resourceIdCriteria = new Criteria(new Column("ManagedDevice", "RESOURCE_ID"), (Object)deviceList.toArray(), 8);
        windows81BelowDeviceResourceIDs = this.getManagedDeviceIdBasedOnOSVersion(osVersion81AndBelowCriteria.and(resourceIdCriteria));
        return windows81BelowDeviceResourceIDs;
    }
    
    public ArrayList getManagedDeviceResourceIDs(final Criteria criteria) {
        Criteria mdCriteria = this.getSuccessfullyEnrolledCriteria();
        if (criteria != null) {
            mdCriteria = mdCriteria.and(criteria);
        }
        return this.getDeviceResourceIDs(mdCriteria);
    }
    
    public ArrayList getDeviceResourceIDs(final Criteria criteria) {
        final ArrayList enrolledResourceIDs = new ArrayList();
        try {
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("ManagedDevice"));
            final Join managedDeviceJoin = new Join("ManagedDevice", "Resource", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2);
            selectQuery.addJoin(managedDeviceJoin);
            selectQuery.addSelectColumn(Column.getColumn("ManagedDevice", "RESOURCE_ID"));
            selectQuery.setCriteria(criteria);
            final DataObject dataObject = MDMUtil.getPersistence().get(selectQuery);
            if (!dataObject.isEmpty()) {
                final Iterator iterator = dataObject.getRows("ManagedDevice");
                while (iterator.hasNext()) {
                    final Row managedDeviceRow = iterator.next();
                    enrolledResourceIDs.add(managedDeviceRow.get("RESOURCE_ID"));
                }
            }
        }
        catch (final Exception ex) {
            this.logger.log(Level.WARNING, "Exception in getAllEnrolledResourceIDs method : {0}", ex);
        }
        return enrolledResourceIDs;
    }
    
    public ArrayList getResourceIDsForUpdateIfRequired(final Criteria criteria) {
        final ArrayList enrolledResourceIDs = new ArrayList();
        try {
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("ManagedDevice"));
            final Join managedDeviceJoin = new Join("ManagedDevice", "Resource", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2);
            final Join agentSettingsJoin = new Join("Resource", "AndroidAgentSettings", new String[] { "CUSTOMER_ID" }, new String[] { "CUSTOMER_ID" }, 2);
            selectQuery.addJoin(managedDeviceJoin);
            selectQuery.addJoin(agentSettingsJoin);
            selectQuery.addSelectColumn(Column.getColumn("ManagedDevice", "RESOURCE_ID"));
            final Criteria autoUpdateCriteria = new Criteria(new Column("AndroidAgentSettings", "UPDATE_TYPE"), (Object)1, 0);
            selectQuery.setCriteria(criteria.and(autoUpdateCriteria));
            final DataObject dataObject = MDMUtil.getPersistence().get(selectQuery);
            if (!dataObject.isEmpty()) {
                final Iterator iterator = dataObject.getRows("ManagedDevice");
                while (iterator.hasNext()) {
                    final Row managedDeviceRow = iterator.next();
                    enrolledResourceIDs.add(managedDeviceRow.get("RESOURCE_ID"));
                }
            }
        }
        catch (final Exception ex) {
            this.logger.log(Level.WARNING, "Exception in getResourceIDsForUpdate method : {0}", ex);
        }
        return enrolledResourceIDs;
    }
    
    public ArrayList getManagedResourcesWithDeviceInfo(final Criteria criteria) {
        Criteria mdCriteria = this.getSuccessfullyEnrolledCriteria();
        if (criteria != null) {
            mdCriteria = mdCriteria.and(criteria);
        }
        return this.getResourcesWithDeviceInfo(mdCriteria);
    }
    
    private ArrayList getResourcesWithDeviceInfo(final Criteria criteria) {
        final ArrayList enrolledResourceIDs = new ArrayList();
        try {
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("ManagedDevice"));
            final Join managedDeviceJoin = new Join("ManagedDevice", "Resource", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2);
            selectQuery.addJoin(managedDeviceJoin);
            selectQuery.addJoin(new Join("ManagedDevice", "MdDeviceInfo", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2));
            selectQuery.addSelectColumn(Column.getColumn("ManagedDevice", "RESOURCE_ID"));
            selectQuery.setCriteria(criteria);
            final DataObject dataObject = MDMUtil.getPersistence().get(selectQuery);
            if (!dataObject.isEmpty()) {
                final Iterator iterator = dataObject.getRows("ManagedDevice");
                while (iterator.hasNext()) {
                    final Row managedDeviceRow = iterator.next();
                    enrolledResourceIDs.add(managedDeviceRow.get("RESOURCE_ID"));
                }
            }
        }
        catch (final Exception ex) {
            this.logger.log(Level.WARNING, "Exception in getDeviceResourceIDsWithDeviceInfo method : {0}", ex);
        }
        return enrolledResourceIDs;
    }
    
    public ArrayList getManagedResourcesWithSIMInfo(final Criteria criteria) {
        Criteria mdCriteria = this.getSuccessfullyEnrolledCriteria();
        if (criteria != null) {
            mdCriteria = mdCriteria.and(criteria);
        }
        final ArrayList enrolledResourceIDs = new ArrayList();
        try {
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("ManagedDevice"));
            final Join managedDeviceJoin = new Join("ManagedDevice", "Resource", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2);
            selectQuery.addJoin(managedDeviceJoin);
            selectQuery.addJoin(new Join("ManagedDevice", "MdSIMInfo", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2));
            selectQuery.addSelectColumn(Column.getColumn("ManagedDevice", "RESOURCE_ID"));
            selectQuery.setCriteria(mdCriteria);
            final DataObject dataObject = MDMUtil.getPersistence().get(selectQuery);
            if (!dataObject.isEmpty()) {
                final Iterator iterator = dataObject.getRows("ManagedDevice");
                while (iterator.hasNext()) {
                    final Row managedDeviceRow = iterator.next();
                    enrolledResourceIDs.add(managedDeviceRow.get("RESOURCE_ID"));
                }
            }
        }
        catch (final Exception ex) {
            this.logger.log(Level.WARNING, "Exception in getDeviceResourceIDsWithDeviceInfo method : {0}", ex);
        }
        return enrolledResourceIDs;
    }
    
    public ArrayList getUnManagedDeviceResourceIDs(final Criteria criteria) {
        final ArrayList enrolledResourceIDs = new ArrayList();
        try {
            Criteria mdCriteria = this.getUnEnrolledCriteria();
            if (criteria != null) {
                mdCriteria = mdCriteria.and(criteria);
            }
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("ManagedDevice"));
            final Join managedDeviceJoin = new Join("ManagedDevice", "Resource", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2);
            selectQuery.addJoin(managedDeviceJoin);
            selectQuery.addSelectColumn(Column.getColumn("ManagedDevice", "RESOURCE_ID"));
            selectQuery.setCriteria(mdCriteria);
            final DataObject dataObject = MDMUtil.getPersistence().get(selectQuery);
            if (!dataObject.isEmpty()) {
                final Iterator iterator = dataObject.getRows("ManagedDevice");
                while (iterator.hasNext()) {
                    final Row managedDeviceRow = iterator.next();
                    enrolledResourceIDs.add(managedDeviceRow.get("RESOURCE_ID"));
                }
            }
        }
        catch (final Exception ex) {
            this.logger.log(Level.WARNING, "Exception in getAllEnrolledResourceIDs method : {0}", ex);
        }
        return enrolledResourceIDs;
    }
    
    private ArrayList getLoggedUserResourceIDs(final Criteria criteria) {
        final ArrayList enrolledResourceIDs = new ArrayList();
        try {
            Criteria mdCriteria = this.getSuccessfullyEnrolledCriteria();
            if (criteria != null) {
                mdCriteria = mdCriteria.and(criteria);
            }
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("ManagedDevice"));
            final Join managedDeviceJoin = new Join("ManagedDevice", "Resource", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2);
            selectQuery.addJoin(managedDeviceJoin);
            selectQuery.addSelectColumn(Column.getColumn("ManagedDevice", "RESOURCE_ID"));
            final Long loginID = ApiFactoryProvider.getAuthUtilAccessAPI().getLoginID();
            final boolean isMDMAdmin = DMUserHandler.isUserInRole(loginID, "All_Managed_Mobile_Devices");
            if (!isMDMAdmin) {
                selectQuery.addJoin(RBDAUtil.getInstance().getUserDeviceMappingJoin("ManagedDevice", "RESOURCE_ID"));
                final Criteria cgCri = RBDAUtil.getInstance().getUserDeviceMappingCriteria(loginID);
                mdCriteria = mdCriteria.and(cgCri);
            }
            selectQuery.setCriteria(mdCriteria);
            final DataObject dataObject = MDMUtil.getPersistence().get(selectQuery);
            if (!dataObject.isEmpty()) {
                final Iterator iterator = dataObject.getRows("ManagedDevice");
                while (iterator.hasNext()) {
                    final Row managedDeviceRow = iterator.next();
                    enrolledResourceIDs.add(managedDeviceRow.get("RESOURCE_ID"));
                }
            }
        }
        catch (final Exception ex) {
            this.logger.log(Level.WARNING, "Exception in getLoggedUserResourceIDs method : {0}", ex);
        }
        return enrolledResourceIDs;
    }
    
    public ArrayList getAllManagedDeviceDetailslist(final Long customerID) {
        return this.getManagedDeviceDetailslist(customerID, null);
    }
    
    public SelectQuery getManagedDeviceQuery(final Long customerID, final Criteria searchCri) {
        SelectQuery selectQuery = null;
        try {
            final Column custCol = Column.getColumn("Resource", "CUSTOMER_ID");
            final Criteria customerCriteria = new Criteria(custCol, (Object)customerID, 0);
            final Criteria mdCriteria = this.getSuccessfullyEnrolledCriteria();
            Criteria cri = customerCriteria.and(mdCriteria);
            selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("ManagedDevice"));
            final Join managedDeviceJoin = new Join("ManagedDevice", "Resource", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2);
            final Join managedDeviceExtnJoin = new Join("ManagedDevice", "ManagedDeviceExtn", new String[] { "RESOURCE_ID" }, new String[] { "MANAGED_DEVICE_ID" }, 2);
            final Join managedUserJoin = new Join("ManagedDevice", "ManagedUserToDevice", new String[] { "RESOURCE_ID" }, new String[] { "MANAGED_DEVICE_ID" }, 2);
            final Join muToResourceJoin = new Join("ManagedUserToDevice", "Resource", new String[] { "MANAGED_USER_ID" }, new String[] { "RESOURCE_ID" }, "ManagedUserToDevice", "USER_RESOURCE", 2);
            selectQuery.addJoin(managedDeviceJoin);
            selectQuery.addJoin(managedDeviceExtnJoin);
            selectQuery.addJoin(managedUserJoin);
            selectQuery.addJoin(muToResourceJoin);
            selectQuery.addSelectColumn(Column.getColumn("ManagedDevice", "RESOURCE_ID"));
            selectQuery.addSelectColumn(Column.getColumn("ManagedDevice", "PLATFORM_TYPE"));
            selectQuery.addSelectColumn(Column.getColumn("ManagedDeviceExtn", "NAME"));
            selectQuery.addSelectColumn(Column.getColumn("USER_RESOURCE", "RESOURCE_ID", "USER_RESOURCE_ID"));
            selectQuery.addSelectColumn(Column.getColumn("USER_RESOURCE", "NAME", "USER_RESOURCE_NAME"));
            if (searchCri != null) {
                cri = cri.and(searchCri);
            }
            selectQuery.setCriteria(cri);
        }
        catch (final Exception ex) {
            this.logger.log(Level.WARNING, "Exception in getManagedDeviceQuery method : {0}", ex);
        }
        return selectQuery;
    }
    
    public ArrayList getManagedDeviceDetailslist(final Long customerID, final Criteria searchCri) {
        final SelectQuery selectQuery = this.getManagedDeviceQuery(customerID, searchCri);
        return this.getManagedDeviceDetailslist(selectQuery);
    }
    
    public ArrayList getManagedDeviceDetailslist(final SelectQuery selectQuery) {
        final ArrayList managedDeviceDetailsList = new ArrayList();
        DMDataSetWrapper ds = null;
        try {
            ds = null;
            ds = DMDataSetWrapper.executeQuery((Object)selectQuery);
            while (ds.next()) {
                final HashMap managedDeviceDetails = new HashMap();
                managedDeviceDetails.put("RESOURCE_ID", ds.getValue("RESOURCE_ID"));
                managedDeviceDetails.put("PLATFORM_TYPE", ds.getValue("PLATFORM_TYPE"));
                managedDeviceDetails.put("NAME", ds.getValue("NAME"));
                managedDeviceDetails.put("USER_RESOURCE_NAME", ds.getValue("USER_RESOURCE_NAME"));
                managedDeviceDetailsList.add(managedDeviceDetails);
            }
        }
        catch (final Exception ex) {
            this.logger.log(Level.WARNING, "Exception in getAllManagedDeviceDetailslist method : {0}", ex);
        }
        return managedDeviceDetailsList;
    }
    
    public JSONObject getResourceIDFromErid(final List<Long> reqID) {
        final String sourceMethod = "getResourceIDFromErid";
        final JSONObject json = new JSONObject();
        final JSONArray managedDeviceID = new JSONArray();
        try {
            final Criteria criteria = new Criteria(Column.getColumn("EnrollmentRequestToDevice", "ENROLLMENT_REQUEST_ID"), (Object)reqID.toArray(), 8);
            final DataObject dataObject = MDMUtil.getPersistence().get("EnrollmentRequestToDevice", criteria);
            if (!dataObject.isEmpty()) {
                final Iterator iterator = dataObject.getRows("EnrollmentRequestToDevice");
                while (iterator.hasNext()) {
                    final Row row = iterator.next();
                    managedDeviceID.put((Object)row.get("MANAGED_DEVICE_ID"));
                }
            }
            json.put("ReqID", (Collection)reqID);
            json.put("ResourceID", (Object)managedDeviceID);
            SyMLogger.info(this.logger, this.sourceClass, sourceMethod, "Managed Device ID : " + managedDeviceID + " from enrollment request id : " + reqID);
        }
        catch (final Exception exp) {
            SyMLogger.error(this.logger, this.sourceClass, sourceMethod, "Exception occured while geting managed device id from enrollment request id : " + reqID, (Throwable)exp);
        }
        return json;
    }
    
    public Long getManagedDeviceIDFromEnrollRequestID(final Long enrollRequestID) {
        final String sourceMethod = "getManagedDeviceIDFromEnrollRequestID";
        Long managedDeviceID = null;
        try {
            final Criteria criteria = new Criteria(Column.getColumn("EnrollmentRequestToDevice", "ENROLLMENT_REQUEST_ID"), (Object)enrollRequestID, 0);
            final DataObject dataObject = MDMUtil.getPersistence().get("EnrollmentRequestToDevice", criteria);
            if (!dataObject.isEmpty()) {
                managedDeviceID = (Long)dataObject.getFirstValue("EnrollmentRequestToDevice", "MANAGED_DEVICE_ID");
            }
            SyMLogger.info(this.logger, this.sourceClass, sourceMethod, "Managed Device ID : " + managedDeviceID + " from enrollment request id : " + enrollRequestID);
        }
        catch (final Exception exp) {
            SyMLogger.error(this.logger, this.sourceClass, sourceMethod, "Exception occured while geting managed device id from enrollment request id : " + enrollRequestID, (Throwable)exp);
        }
        return managedDeviceID;
    }
    
    public String getUDIDFromEnrollmentRequestID(final Long enrollRequestID) {
        String udid = null;
        final SelectQuery sql = (SelectQuery)new SelectQueryImpl(new Table("ManagedDevice"));
        sql.addJoin(new Join("ManagedDevice", "EnrollmentRequestToDevice", new String[] { "RESOURCE_ID" }, new String[] { "MANAGED_DEVICE_ID" }, 2));
        sql.setCriteria(new Criteria(new Column("EnrollmentRequestToDevice", "ENROLLMENT_REQUEST_ID"), (Object)enrollRequestID, 0));
        sql.addSelectColumn(Column.getColumn("ManagedDevice", "UDID"));
        sql.addSelectColumn(Column.getColumn("ManagedDevice", "RESOURCE_ID"));
        try {
            final DataObject dao = MDMUtil.getPersistence().get(sql);
            if (!dao.isEmpty()) {
                udid = dao.getFirstRow("ManagedDevice").get("UDID").toString();
            }
        }
        catch (final DataAccessException ex) {
            this.logger.log(Level.SEVERE, "Exception in getUDIDFromEnrollmentRequestID", (Throwable)ex);
        }
        return udid;
    }
    
    public Criteria getSuccessfullyEnrolledCriteria() {
        final Criteria cri = new Criteria(Column.getColumn("ManagedDevice", "MANAGED_STATUS"), (Object)new Integer(2), 0);
        return cri;
    }
    
    public Criteria getUnEnrolledCriteria() {
        return new Criteria(Column.getColumn("ManagedDevice", "MANAGED_STATUS"), (Object)new Integer(4), 0);
    }
    
    public ArrayList getManagedDevicesListForCustomer(final Long customerID) {
        final Column custCol = Column.getColumn("Resource", "CUSTOMER_ID");
        final Criteria customerCriteria = new Criteria(custCol, (Object)customerID, 0);
        return this.getManagedDeviceResourceIDs(customerCriteria);
    }
    
    public ArrayList getManagedDevicesListForLoggedUser(final Long customerID) {
        final Column custCol = Column.getColumn("Resource", "CUSTOMER_ID");
        final Criteria customerCriteria = new Criteria(custCol, (Object)customerID, 0);
        return this.getLoggedUserResourceIDs(customerCriteria);
    }
    
    public ArrayList getAndroidManagedDevicesForCustomer(final Long customerID) {
        final Criteria cPlatform = new Criteria(new Column("ManagedDevice", "PLATFORM_TYPE"), (Object)2, 0);
        final Criteria cCust = new Criteria(new Column("Resource", "CUSTOMER_ID"), (Object)customerID, 0);
        final Criteria criteria = cPlatform.and(cCust);
        return this.getManagedDeviceResourceIDs(criteria);
    }
    
    public ArrayList getManagedDevicesForCustomer(final Long customerID, final int platform) {
        final Criteria cPlatform = new Criteria(new Column("ManagedDevice", "PLATFORM_TYPE"), (Object)platform, 0);
        final Criteria cCust = new Criteria(new Column("Resource", "CUSTOMER_ID"), (Object)customerID, 0);
        final Criteria criteria = cPlatform.and(cCust);
        return this.getManagedDeviceResourceIDs(criteria);
    }
    
    public ArrayList getIosManagedDevicesForCustomer(final Long customerID) {
        final Criteria cPlatform = new Criteria(new Column("ManagedDevice", "PLATFORM_TYPE"), (Object)1, 0);
        final Criteria cCust = new Criteria(new Column("Resource", "CUSTOMER_ID"), (Object)customerID, 0);
        final Criteria criteria = cPlatform.and(cCust);
        return this.getManagedDeviceResourceIDs(criteria);
    }
    
    public ArrayList getWindowsPhoneManagedDevicesForCustomer(final Long customerID) {
        final Criteria cPlatform = new Criteria(new Column("ManagedDevice", "PLATFORM_TYPE"), (Object)3, 0);
        final Criteria cCust = new Criteria(new Column("Resource", "CUSTOMER_ID"), (Object)customerID, 0);
        final Criteria criteria = cPlatform.and(cCust);
        return this.getManagedDeviceResourceIDs(criteria);
    }
    
    public List getWindowsPhone81AboveDevices(final Long customerID) {
        List deviceList = null;
        final Criteria os80Criteria = new Criteria(new Column("MdDeviceInfo", "OS_VERSION"), (Object)"8.0*", 10);
        final List resList = this.getWindowsPhoneManagedDevicesForCustomer(customerID);
        if (resList != null && !resList.isEmpty()) {
            final Criteria resListCri = new Criteria(new Column("ManagedDevice", "RESOURCE_ID"), (Object)resList.toArray(), 8);
            deviceList = this.getManagedDeviceResourceIDs(resListCri);
            final Criteria osCri = os80Criteria.and(resListCri);
            final List device80List = this.getManagedDeviceIdBasedOnOSVersion(osCri);
            deviceList.removeAll(device80List);
        }
        return deviceList;
    }
    
    public ArrayList getYetToUpgradeAndroidManagedDevicesForCustomer(final Long customerID) {
        final String androidAgent = MDMUtil.getProductProperty("androidagentversion");
        final Criteria cPlatform = new Criteria(new Column("ManagedDevice", "PLATFORM_TYPE"), (Object)2, 0);
        final Criteria cAgentVersion = new Criteria(new Column("ManagedDevice", "AGENT_VERSION"), (Object)androidAgent, 1);
        final Criteria cAgentNotVersion = new Criteria(new Column("ManagedDevice", "NOTIFIED_AGENT_VERSION"), (Object)androidAgent, 1);
        final Criteria criteria = cPlatform.and(cAgentVersion.and(cAgentNotVersion));
        return this.getManagedDeviceResourceIDs(criteria);
    }
    
    public ArrayList getYetToUpgradeAndroidManagedDevices() {
        ArrayList androidList = new ArrayList();
        final Properties prop = MDMUtil.getMDMServerInfo();
        if (prop != null) {
            final String androidAgentVersion = ((Hashtable<K, String>)prop).get("ANDROID_AGENT_VERSION");
            final Long androidVersionCode = ((Hashtable<K, Long>)prop).get("ANDROID_AGENT_VERSION_CODE");
            androidList = this.getDevicesYetToNotifyForUpgrade(androidAgentVersion, androidVersionCode, 2);
            final ArrayList safeList = this.getSAFEDevicesYetToNotifyForUpgrade(prop);
            this.logger.log(Level.INFO, "Fetched latest version details - version{0},version code{1},safe version{2}", new Object[] { androidAgentVersion, androidVersionCode, prop });
            androidList.addAll(safeList);
        }
        return androidList;
    }
    
    public ArrayList getYetToUpgradeIOSManagedDevices() {
        final Properties prop = MDMUtil.getMDMServerInfo();
        final String iosAgentVersion = ((Hashtable<K, String>)prop).get("IOS_AGENT_VERSION");
        final Long iosVersionCode = ((Hashtable<K, Long>)prop).get("IOS_AGENT_VERSION_CODE");
        final ArrayList androidList = this.getDevicesYetToNotifyForUpgrade(iosAgentVersion, iosVersionCode, 1);
        return androidList;
    }
    
    public Criteria getYetToUpgradeMacOSManagedDevicesCriteria() {
        final Properties prop = MDMUtil.getMDMServerInfo();
        final String iosAgentVersion = ((Hashtable<K, String>)prop).get("MAC_AGENT_VERSION");
        final Long iosVersionCode = ((Hashtable<K, Long>)prop).get("MAC_AGENT_VERSION_CODE");
        final Criteria cPlatform = new Criteria(new Column("ManagedDevice", "AGENT_TYPE"), (Object)8, 0);
        final Criteria cAgentVersion = new Criteria(new Column("ManagedDevice", "AGENT_VERSION"), (Object)iosAgentVersion, 1);
        final Criteria cEmptyVersion = new Criteria(new Column("ManagedDevice", "AGENT_VERSION"), (Object)"--", 1);
        final Criteria cAgentNotVersion = new Criteria(new Column("ManagedDevice", "NOTIFIED_AGENT_VERSION"), (Object)iosAgentVersion, 1);
        final Criteria cAgentVersionCode = new Criteria(new Column("ManagedDevice", "AGENT_VERSION_CODE"), (Object)iosVersionCode, 7);
        return cPlatform.and(cAgentNotVersion).and(cAgentVersion).and(cEmptyVersion).and(cAgentVersionCode);
    }
    
    public ArrayList getYetToUpgradeWindowsManagedDevices() {
        final Properties prop = MDMUtil.getMDMServerInfo();
        final String wpAgentVersion = ((Hashtable<K, String>)prop).get("WINDOWS_AGENT_VERSION");
        final Long wpVersionCode = ((Hashtable<K, Long>)prop).get("WINDOWS_AGENT_VERSION_CODE");
        final ArrayList windowsList = this.getYetToUpgradeDevices(wpAgentVersion, wpVersionCode, 4);
        return windowsList;
    }
    
    private ArrayList getDevicesYetToNotifyForUpgrade(final String agentVersion, final Long agentVersionCode, final int agentType) {
        final Criteria cPlatform = new Criteria(new Column("ManagedDevice", "AGENT_TYPE"), (Object)agentType, 0);
        final Criteria cAgentVersion = new Criteria(new Column("ManagedDevice", "AGENT_VERSION"), (Object)agentVersion, 1);
        final Criteria cEmptyVersion = new Criteria(new Column("ManagedDevice", "AGENT_VERSION"), (Object)"--", 1);
        final Criteria cAgentNotVersion = new Criteria(new Column("ManagedDevice", "NOTIFIED_AGENT_VERSION"), (Object)agentVersion, 1);
        final Criteria cAgentVersionCode = new Criteria(new Column("ManagedDevice", "AGENT_VERSION_CODE"), (Object)agentVersionCode, 7);
        Criteria criteria = cAgentVersionCode.and(cPlatform).and(cAgentVersion).and(cAgentNotVersion).and(cEmptyVersion);
        final Criteria deviceCriteria = new Criteria(Column.getColumn("ManagedDevice", "MANAGED_STATUS"), (Object)new int[] { new Integer(2), new Integer(5), new Integer(6) }, 8);
        criteria = criteria.and(deviceCriteria);
        if (MDMFeatureParamsHandler.getInstance().isFeatureEnabled("AndroidAgentUpdateControl")) {
            return this.getResourceIDsForUpdateIfRequired(criteria);
        }
        return this.getDeviceResourceIDs(criteria);
    }
    
    public ArrayList getNavtiveAppInstallediOSDevices() {
        final Criteria iosPlatform = new Criteria(new Column("ManagedDevice", "PLATFORM_TYPE"), (Object)1, 0);
        return this.getDevicesWithDefaultMEMDMAppInstalled(iosPlatform);
    }
    
    public ArrayList getDevicesWithDefaultMEMDMAppInstalled(final Criteria iosPlatform) {
        this.logger.log(Level.INFO, "Getting getDevicesWithDefaultMEMDMAppInstalled...");
        final Criteria isAppInstalled = new Criteria(new Column("ManagedDevice", "AGENT_VERSION"), (Object)"--", 1);
        return this.getManagedDeviceResourceIDs(isAppInstalled.and(iosPlatform));
    }
    
    private ArrayList getDevicesYetToNotifyForUpgrade(final String agentVersion, final Long[] agentVersionCode, final int agentType) {
        final Criteria cPlatform = new Criteria(new Column("ManagedDevice", "AGENT_TYPE"), (Object)agentType, 0);
        final Criteria cAgentVersion = new Criteria(new Column("ManagedDevice", "AGENT_VERSION"), (Object)agentVersion, 1);
        final Criteria cEmptyVersion = new Criteria(new Column("ManagedDevice", "AGENT_VERSION"), (Object)"--", 1);
        final Criteria cAgentNotVersion = new Criteria(new Column("ManagedDevice", "NOTIFIED_AGENT_VERSION"), (Object)agentVersion, 1);
        final Criteria cAgentVersionCode = new Criteria(new Column("ManagedDevice", "AGENT_VERSION_CODE"), (Object)agentVersionCode, 14);
        final Criteria criteria = cAgentVersionCode.and(cPlatform).and(cAgentVersion).and(cAgentNotVersion).and(cEmptyVersion);
        return this.getManagedDeviceResourceIDs(criteria);
    }
    
    private ArrayList getYetToUpgradeDevices(final String agentVersion, final Long agentVersionCode, final int agentType) {
        final Criteria cPlatform = new Criteria(new Column("ManagedDevice", "AGENT_TYPE"), (Object)agentType, 0);
        final Criteria cAgentVersion = new Criteria(new Column("ManagedDevice", "AGENT_VERSION"), (Object)agentVersion, 1);
        final Criteria cEmptyVersion = new Criteria(new Column("ManagedDevice", "AGENT_VERSION"), (Object)"--", 1);
        final Criteria cAgentVersionCode = new Criteria(new Column("ManagedDevice", "AGENT_VERSION_CODE"), (Object)agentVersionCode, 7);
        final Criteria criteria = cAgentVersionCode.and(cPlatform).and(cAgentVersion).and(cEmptyVersion);
        return this.getManagedDeviceResourceIDs(criteria);
    }
    
    private ArrayList getSAFEDevicesYetToNotifyForUpgrade(final Properties mdmServerInfoProp) {
        final String androidAgentVersion = ((Hashtable<K, String>)mdmServerInfoProp).get("ANDROID_AGENT_VERSION");
        final String safeAgentVersion = ((Hashtable<K, String>)mdmServerInfoProp).get("SAFE_AGENT_VERSION");
        final String knoxAgentVersion = ((Hashtable<K, String>)mdmServerInfoProp).get("KNOX_AGENT_VERSION");
        final Long androidAgentVersionCode = ((Hashtable<K, Long>)mdmServerInfoProp).get("ANDROID_AGENT_VERSION_CODE");
        final Long safeAgentVersionCode = ((Hashtable<K, Long>)mdmServerInfoProp).get("SAFE_AGENT_VERSION_CODE");
        final Long knoxAgentVersionCode = ((Hashtable<K, Long>)mdmServerInfoProp).get("KNOX_AGENT_VERSION_CODE");
        final Criteria cPlatform = new Criteria(new Column("ManagedDevice", "AGENT_TYPE"), (Object)3, 0);
        final Criteria cEmptyVersion = new Criteria(new Column("ManagedDevice", "AGENT_VERSION"), (Object)"--", 1);
        final Criteria cSafeAgentVersion = new Criteria(new Column("ManagedDevice", "AGENT_VERSION"), (Object)safeAgentVersion, 1);
        final Criteria cSafeVersion = new Criteria(new Column("ManagedDevice", "AGENT_VERSION"), (Object)"S", 12, false);
        final Criteria cSafeAgentNotVersion = new Criteria(new Column("ManagedDevice", "NOTIFIED_AGENT_VERSION"), (Object)safeAgentVersion, 1);
        final Criteria cSafeAgentVersionCode = new Criteria(new Column("ManagedDevice", "AGENT_VERSION_CODE"), (Object)safeAgentVersionCode, 7);
        final Criteria safeCriteria = cSafeAgentVersionCode.and(cPlatform).and(cSafeAgentVersion).and(cSafeAgentNotVersion).and(cEmptyVersion).and(cSafeVersion);
        final Criteria cKnoxAgentVersion = new Criteria(new Column("ManagedDevice", "AGENT_VERSION"), (Object)knoxAgentVersion, 1);
        final Criteria cKnoxVersion = new Criteria(new Column("ManagedDevice", "AGENT_VERSION"), (Object)"K", 12, false);
        final Criteria cKnoxAgentNotVersion = new Criteria(new Column("ManagedDevice", "NOTIFIED_AGENT_VERSION"), (Object)knoxAgentVersion, 1);
        final Criteria cKnoxAgentVersionCode = new Criteria(new Column("ManagedDevice", "AGENT_VERSION_CODE"), (Object)knoxAgentVersionCode, 7);
        final Criteria knoxCriteria = cKnoxAgentVersionCode.and(cPlatform).and(cKnoxAgentVersion).and(cKnoxAgentNotVersion).and(cEmptyVersion).and(cKnoxVersion);
        final Criteria cAndroidAgentVersion = new Criteria(new Column("ManagedDevice", "AGENT_VERSION"), (Object)androidAgentVersion, 1);
        final Criteria cAndroidVersion = new Criteria(new Column("ManagedDevice", "AGENT_VERSION"), (Object)"A", 12, false);
        final Criteria cAndroidNewFormatVersion = cSafeVersion.or(cKnoxVersion).or(cAndroidVersion).negate();
        final Criteria cAndroidAgentNotVersion = new Criteria(new Column("ManagedDevice", "NOTIFIED_AGENT_VERSION"), (Object)androidAgentVersion, 1);
        final Criteria cAndroidAgentVersionCode = new Criteria(new Column("ManagedDevice", "AGENT_VERSION_CODE"), (Object)androidAgentVersionCode, 7);
        final Criteria androidCriteria = cAndroidAgentVersionCode.and(cPlatform).and(cAndroidAgentVersion).and(cAndroidAgentNotVersion).and(cEmptyVersion).and(cAndroidVersion.or(cAndroidNewFormatVersion));
        final Criteria mdCriteria = this.getSuccessfullyEnrolledCriteria();
        if (MDMFeatureParamsHandler.getInstance().isFeatureEnabled("AndroidAgentUpdateControl")) {
            return this.getResourceIDsForUpdateIfRequired(safeCriteria.or(knoxCriteria).or(androidCriteria).and(mdCriteria));
        }
        return this.getManagedDeviceResourceIDs(safeCriteria.or(knoxCriteria).or(androidCriteria));
    }
    
    private ArrayList getSAFEDevicesYetToUpgrade(final String safeAgentVersion, final Long safeAgentVersionCode, final String knoxAgentVersion, final Long knoxAgentVersionCode) {
        final Criteria cPlatform = new Criteria(new Column("ManagedDevice", "AGENT_TYPE"), (Object)3, 0);
        final Criteria cSafeAgentVersion = new Criteria(new Column("ManagedDevice", "AGENT_VERSION"), (Object)safeAgentVersion, 1);
        final Criteria cSafeVersion = new Criteria(new Column("ManagedDevice", "AGENT_VERSION"), (Object)"S", 12, false);
        final Criteria cSafeEmptyVersion = new Criteria(new Column("ManagedDevice", "AGENT_VERSION"), (Object)"--", 1);
        final Criteria cSafeAgentVersionCode = new Criteria(new Column("ManagedDevice", "AGENT_VERSION_CODE"), (Object)safeAgentVersionCode, 7);
        final Criteria safeCriteria = cSafeAgentVersionCode.and(cPlatform).and(cSafeAgentVersion).and(cSafeEmptyVersion).and(cSafeVersion);
        final Criteria cKnoxAgentVersion = new Criteria(new Column("ManagedDevice", "AGENT_VERSION"), (Object)knoxAgentVersion, 1);
        final Criteria cKnoxVersion = new Criteria(new Column("ManagedDevice", "AGENT_VERSION"), (Object)"K", 12, false);
        final Criteria cKnoxEmptyVersion = new Criteria(new Column("ManagedDevice", "AGENT_VERSION"), (Object)"--", 1);
        final Criteria cKnoxAgentVersionCode = new Criteria(new Column("ManagedDevice", "AGENT_VERSION_CODE"), (Object)knoxAgentVersionCode, 7);
        final Criteria knoxCriteria = cKnoxAgentVersionCode.and(cPlatform).and(cKnoxAgentVersion).and(cKnoxEmptyVersion).and(cKnoxVersion);
        return this.getManagedDeviceResourceIDs(safeCriteria.or(knoxCriteria));
    }
    
    public int getManagedDeviceCount(final Criteria criteria) {
        return this.getManagedDeviceCount(criteria, true);
    }
    
    public int getManagedDeviceCount(final Criteria criteria, final Boolean skipRBDA) {
        int managedDeviceCount = 0;
        final String skipCustomerFilter = CustomerInfoThreadLocal.getSkipCustomerFilter();
        boolean flagSet = false;
        try {
            if (skipCustomerFilter != null && skipCustomerFilter.equals("false")) {
                CustomerInfoThreadLocal.setSkipCustomerFilter("true");
                flagSet = true;
            }
            Criteria mdCriteria = getInstance().getSuccessfullyEnrolledCriteria();
            if (criteria != null) {
                mdCriteria = mdCriteria.and(criteria);
            }
            SelectQuery managedDeviceCountQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("ManagedDevice"));
            managedDeviceCountQuery.addJoin(new Join("ManagedDevice", "Resource", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2));
            managedDeviceCountQuery.addJoin(new Join("ManagedDevice", "EnrollmentRequestToDevice", new String[] { "RESOURCE_ID" }, new String[] { "MANAGED_DEVICE_ID" }, 2));
            managedDeviceCountQuery.addJoin(new Join("ManagedDevice", "MdDeviceInfo", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2));
            managedDeviceCountQuery.addJoin(new Join("MdDeviceInfo", "MdModelInfo", new String[] { "MODEL_ID" }, new String[] { "MODEL_ID" }, 2));
            managedDeviceCountQuery.setCriteria(mdCriteria);
            if (!skipRBDA) {
                managedDeviceCountQuery = RBDAUtil.getInstance().getRBDAQuery(managedDeviceCountQuery);
            }
            managedDeviceCount = DBUtil.getRecordCount(managedDeviceCountQuery, "ManagedDevice", "RESOURCE_ID");
        }
        catch (final Exception exp) {
            this.logger.log(Level.SEVERE, "Exception in getManagedDeviceCount", exp);
        }
        finally {
            if (flagSet) {
                CustomerInfoThreadLocal.setSkipCustomerFilter("false");
            }
        }
        return managedDeviceCount;
    }
    
    public int getManagedDeviceCount() {
        return this.getManagedDeviceCount(null);
    }
    
    public int getDeviceCountAllowedToDeprovision() {
        int managedDeviceCount = this.getManagedDeviceCount();
        if (managedDeviceCount >= 1000) {
            return 100;
        }
        managedDeviceCount /= 10;
        if (managedDeviceCount <= 100) {
            return 10;
        }
        return managedDeviceCount;
    }
    
    public int getWindows81ManagedDeviceCount() {
        final Criteria osVersion8_1Criteria = new Criteria(new Column("MdDeviceInfo", "OS_VERSION"), (Object)"8.1*", 10);
        final Criteria deviceType = new Criteria(new Column("ManagedDevice", "PLATFORM_TYPE"), (Object)3, 0);
        final int windows81DeviceCount = this.getManagedDeviceCountBasedOnOSVersion(osVersion8_1Criteria.and(deviceType));
        return windows81DeviceCount;
    }
    
    public int getWindows10ManagedDeviceCount() {
        final Criteria osVersion10Criteria = new Criteria(new Column("MdDeviceInfo", "OS_VERSION"), (Object)"10.*", 10);
        final Criteria deviceType = new Criteria(new Column("ManagedDevice", "PLATFORM_TYPE"), (Object)3, 0);
        final int windows10DeviceCount = this.getManagedDeviceCountBasedOnOSVersion(osVersion10Criteria.and(deviceType));
        return windows10DeviceCount;
    }
    
    public int getWindowsManagedDeviceCount() {
        final Column deviceTypeColumn = Column.getColumn("ManagedDevice", "PLATFORM_TYPE");
        final Criteria deviceTypeCriteria = new Criteria(deviceTypeColumn, (Object)3, 0);
        return this.getManagedDeviceCount(deviceTypeCriteria);
    }
    
    public int getAppleManagedDeviceCount() {
        final Column deviceTypeColumn = Column.getColumn("ManagedDevice", "PLATFORM_TYPE");
        final Criteria deviceTypeCriteria = new Criteria(deviceTypeColumn, (Object)1, 0);
        return this.getManagedDeviceCount(deviceTypeCriteria);
    }
    
    public int getAppleManagedDeviceAndWaitingForUserAssignCount() {
        Integer count = -1;
        try {
            final SelectQuery managedDeviceAndWFUA = (SelectQuery)new SelectQueryImpl(Table.getTable("ManagedDevice"));
            final Criteria checkPlatformType = new Criteria(Column.getColumn("ManagedDevice", "PLATFORM_TYPE"), (Object)1, 0);
            final Criteria checkManagedStatus = new Criteria(Column.getColumn("ManagedDevice", "MANAGED_STATUS"), (Object)new Integer[] { 2, 5 }, 8);
            managedDeviceAndWFUA.setCriteria(checkPlatformType.and(checkManagedStatus));
            count = DBUtil.getRecordCount(managedDeviceAndWFUA, "ManagedDevice", "RESOURCE_ID");
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Exception in getAppleManagedDeviceAndWaitingForUserAssignCount", e);
        }
        return count;
    }
    
    public JSONObject getModelWiseManagedDeviceEnrollmentTypeCount() throws Exception {
        try {
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("ManagedDevice"));
            selectQuery.addJoin(new Join("ManagedDevice", "MdDeviceInfo", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2));
            selectQuery.addJoin(new Join("MdDeviceInfo", "MdModelInfo", new String[] { "MODEL_ID" }, new String[] { "MODEL_ID" }, 2));
            selectQuery.addJoin(new Join("ManagedDevice", "EnrollmentRequestToDevice", new String[] { "RESOURCE_ID" }, new String[] { "MANAGED_DEVICE_ID" }, 2));
            selectQuery.addJoin(new Join("EnrollmentRequestToDevice", "DeviceEnrollmentRequest", new String[] { "ENROLLMENT_REQUEST_ID" }, new String[] { "ENROLLMENT_REQUEST_ID" }, 2));
            selectQuery.setCriteria(getInstance().getSuccessfullyEnrolledCriteria());
            selectQuery.addSelectColumn(Column.getColumn("ManagedDevice", "PLATFORM_TYPE"));
            selectQuery.addSelectColumn(Column.getColumn("MdModelInfo", "MODEL_TYPE"));
            selectQuery.addSelectColumn(Column.getColumn("DeviceEnrollmentRequest", "ENROLLMENT_TYPE"));
            final Column count = Column.getColumn("DeviceEnrollmentRequest", "ENROLLMENT_TYPE").count();
            count.setColumnAlias("count");
            selectQuery.addSelectColumn(count);
            final List groupByList = new ArrayList();
            final Column groupByPlatform = Column.getColumn("ManagedDevice", "PLATFORM_TYPE");
            final Column groupByModel = Column.getColumn("MdModelInfo", "MODEL_TYPE");
            final Column groupByEnrollType = Column.getColumn("DeviceEnrollmentRequest", "ENROLLMENT_TYPE");
            groupByList.add(groupByPlatform);
            groupByList.add(groupByModel);
            groupByList.add(groupByEnrollType);
            final GroupByClause groupBy = new GroupByClause(groupByList);
            selectQuery.setGroupByClause(groupBy);
            final JSONObject platFormJson = new JSONObject();
            final DMDataSetWrapper ds = DMDataSetWrapper.executeQuery((Object)selectQuery);
            while (ds.next()) {
                final Object platForm = ds.getValue("PLATFORM_TYPE");
                final Object model = ds.getValue("MODEL_TYPE");
                final Object enrollmentType = ds.getValue("ENROLLMENT_TYPE");
                final Object enrollmentTypeCount = ds.getValue("count");
                if (platForm != null && model != null && enrollmentType != null) {
                    final JSONObject enrollTypeJson = new JSONObject();
                    enrollTypeJson.put(enrollmentType.toString(), enrollmentTypeCount);
                    final JSONObject innerPlatFormJson = platFormJson.optJSONObject(platForm.toString());
                    platFormJson.put(platForm.toString(), (Object)((innerPlatFormJson == null) ? new JSONObject().put(model.toString(), (Object)enrollTypeJson) : innerPlatFormJson.put(model.toString(), (Object)enrollTypeJson)));
                }
            }
            return platFormJson;
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Exception in getModelWiseManagedDeviceEnrollmentTypeCount", e);
            throw e;
        }
    }
    
    public int getAndroidManagedDeviceCount() {
        final Column deviceTypeColumn = Column.getColumn("ManagedDevice", "PLATFORM_TYPE");
        final Criteria deviceTypeCriteria = new Criteria(deviceTypeColumn, (Object)2, 0);
        return this.getManagedDeviceCount(deviceTypeCriteria);
    }
    
    public int getChromeManagedDeviceCount() {
        final Column deviceTypeColumn = Column.getColumn("ManagedDevice", "PLATFORM_TYPE");
        final Criteria deviceTypeCriteria = new Criteria(deviceTypeColumn, (Object)4, 0);
        return this.getManagedDeviceCount(deviceTypeCriteria);
    }
    
    public int getMacOSManagedDeviceCount() {
        final Column deviceTypeColumn = Column.getColumn("ManagedDevice", "PLATFORM_TYPE");
        final Column modelColumn = Column.getColumn("MdModelInfo", "MODEL_TYPE");
        final Criteria modelCriteria = new Criteria(modelColumn, (Object)new Integer[] { 3, 4 }, 8);
        Criteria macOSTypeCriteria = new Criteria(deviceTypeColumn, (Object)1, 0);
        macOSTypeCriteria = macOSTypeCriteria.and(modelCriteria);
        return this.getManagedDeviceCount(macOSTypeCriteria);
    }
    
    public int getSharedIpadManagedDeviceCount() {
        final Criteria modelCriteria = new Criteria(new Column("MdModelInfo", "MODEL_TYPE"), (Object)2, 0);
        final Criteria iOSPlatFormCriteria = new Criteria(new Column("ManagedDevice", "PLATFORM_TYPE"), (Object)1, 0);
        Criteria multiUserCriteria = new Criteria(new Column("MdDeviceInfo", "IS_MULTIUSER"), (Object)true, 0);
        multiUserCriteria = multiUserCriteria.and(modelCriteria).and(iOSPlatFormCriteria);
        return this.getManagedDeviceCount(multiUserCriteria);
    }
    
    public int getSAFEDeviceCount() {
        final Column deviceTypeColumn = Column.getColumn("ManagedDevice", "AGENT_TYPE");
        final Criteria deviceTypeCriteria = new Criteria(deviceTypeColumn, (Object)3, 0);
        return this.getManagedDeviceCount(deviceTypeCriteria);
    }
    
    public int getAgentType(final Long resourceID) {
        int agentType = -1;
        final SelectQuery sQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("ManagedDevice"));
        sQuery.addSelectColumn(Column.getColumn("ManagedDevice", "*"));
        final Criteria safeCriteria = new Criteria(Column.getColumn("ManagedDevice", "RESOURCE_ID"), (Object)resourceID, 0);
        sQuery.setCriteria(safeCriteria);
        try {
            final DataObject dataObject = MDMUtil.getPersistence().get(sQuery);
            if (!dataObject.isEmpty()) {
                final Row row = dataObject.getRow("ManagedDevice");
                agentType = (int)row.get("AGENT_TYPE");
            }
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, "Exception in getAgentType", ex);
        }
        return agentType;
    }
    
    public int getDeviceOwnership(final Long resourceID) {
        final SelectQuery sQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("DeviceEnrollmentRequest"));
        final Join enrolReqDevice = new Join("DeviceEnrollmentRequest", "EnrollmentRequestToDevice", new String[] { "ENROLLMENT_REQUEST_ID" }, new String[] { "ENROLLMENT_REQUEST_ID" }, 2);
        final Criteria resCriteria = new Criteria(Column.getColumn("EnrollmentRequestToDevice", "MANAGED_DEVICE_ID"), (Object)resourceID, 0);
        sQuery.addJoin(enrolReqDevice);
        sQuery.setCriteria(resCriteria);
        sQuery.addSelectColumn(Column.getColumn("DeviceEnrollmentRequest", "*"));
        int ownerShipType = -1;
        try {
            final DataObject dataObject = MDMUtil.getPersistence().get(sQuery);
            if (!dataObject.isEmpty()) {
                final Row row = dataObject.getRow("DeviceEnrollmentRequest");
                ownerShipType = (int)row.get("OWNED_BY");
            }
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, "Exception in getDeviceOwnership", ex);
        }
        return ownerShipType;
    }
    
    public int getManagedSelfEnrolledDeviceCount() {
        int selfEnrolledCount = 0;
        try {
            final SelectQuery sQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("DeviceEnrollmentRequest"));
            final Criteria cSelfEnroll = new Criteria(new Column("DeviceEnrollmentRequest", "ENROLLMENT_TYPE"), (Object)2, 0);
            final Criteria managedDeviceCriteria = new Criteria(new Column("ManagedDevice", "MANAGED_STATUS"), (Object)2, 0);
            final Join enrollReqToDeviceJoin = new Join("DeviceEnrollmentRequest", "EnrollmentRequestToDevice", new String[] { "ENROLLMENT_REQUEST_ID" }, new String[] { "ENROLLMENT_REQUEST_ID" }, 2);
            final Join enrollReqToMgdDeviceJoin = new Join("EnrollmentRequestToDevice", "ManagedDevice", new String[] { "MANAGED_DEVICE_ID" }, new String[] { "RESOURCE_ID" }, 2);
            sQuery.addJoin(enrollReqToDeviceJoin);
            sQuery.addJoin(enrollReqToMgdDeviceJoin);
            sQuery.setCriteria(cSelfEnroll.and(managedDeviceCriteria));
            selfEnrolledCount = DBUtil.getRecordCount(sQuery, "DeviceEnrollmentRequest", "ENROLLMENT_REQUEST_ID");
        }
        catch (final Exception ex) {
            Logger.getLogger(ManagedDeviceHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
        return selfEnrolledCount;
    }
    
    public int getAdminEnrollmentCount() {
        int selfEnrolledCount = 0;
        try {
            final SelectQuery sQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("DeviceEnrollmentRequest"));
            final Criteria cSelfEnroll = new Criteria(new Column("DeviceEnrollmentRequest", "ENROLLMENT_TYPE"), (Object)3, 0);
            final Criteria managedDeviceCriteria = new Criteria(new Column("ManagedDevice", "MANAGED_STATUS"), (Object)2, 0);
            final Join enrollReqToDeviceJoin = new Join("DeviceEnrollmentRequest", "EnrollmentRequestToDevice", new String[] { "ENROLLMENT_REQUEST_ID" }, new String[] { "ENROLLMENT_REQUEST_ID" }, 2);
            final Join enrollReqToMgdDeviceJoin = new Join("EnrollmentRequestToDevice", "ManagedDevice", new String[] { "MANAGED_DEVICE_ID" }, new String[] { "RESOURCE_ID" }, 2);
            sQuery.addJoin(enrollReqToDeviceJoin);
            sQuery.addJoin(enrollReqToMgdDeviceJoin);
            sQuery.setCriteria(cSelfEnroll.and(managedDeviceCriteria));
            selfEnrolledCount = DBUtil.getRecordCount(sQuery, "DeviceEnrollmentRequest", "ENROLLMENT_REQUEST_ID");
        }
        catch (final Exception ex) {
            Logger.getLogger(ManagedDeviceHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
        return selfEnrolledCount;
    }
    
    public List getSupervisedIosDevicelist(final List deviceIdList) {
        final List superVisedList = this.getSupervisedIosDevicelist(deviceIdList, null);
        return superVisedList;
    }
    
    private List getSupervisedIosDevicelist(final List deviceIdList, final Criteria criteria) {
        final List superVisedList = new ArrayList();
        try {
            final SelectQuery sQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("ManagedDevice"));
            sQuery.addJoin(new Join("ManagedDevice", "MdDeviceInfo", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2));
            Criteria cSuperVisedDevice = new Criteria(new Column("MdDeviceInfo", "IS_SUPERVISED"), (Object)Boolean.TRUE, 0);
            if (deviceIdList != null && !deviceIdList.isEmpty()) {
                final Criteria cDeviceList = new Criteria(new Column("ManagedDevice", "RESOURCE_ID"), (Object)deviceIdList.toArray(), 8);
                cSuperVisedDevice = cSuperVisedDevice.and(cDeviceList).and(criteria);
            }
            sQuery.setCriteria(cSuperVisedDevice);
            sQuery.addSelectColumn(Column.getColumn("ManagedDevice", "*"));
            final DataObject DO = MDMUtil.getPersistence().get(sQuery);
            if (!DO.isEmpty()) {
                final Iterator item = DO.getRows("ManagedDevice");
                while (item.hasNext()) {
                    final Row mdRow = item.next();
                    superVisedList.add(mdRow.get("RESOURCE_ID"));
                }
            }
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Exception in getSupervisedIosDevicelist", e);
        }
        return superVisedList;
    }
    
    public ArrayList<Long> getiOS9AndAboveDevices(final ArrayList<Long> resourceIDs) {
        final ArrayList<Long> list = new ArrayList<Long>();
        try {
            final SelectQuery sq = (SelectQuery)new SelectQueryImpl(new Table("MdDeviceInfo"));
            final Criteria resC = new Criteria(new Column("MdDeviceInfo", "RESOURCE_ID"), (Object)resourceIDs.toArray(), 8);
            final Criteria osC = this.getMDDeviceiOS9AndAboveCriteria();
            sq.addSelectColumn(Column.getColumn((String)null, "*"));
            sq.setCriteria(resC.and(osC));
            final DataObject dataObj = MDMUtil.getPersistence().get(sq);
            final Iterator<Row> iter = dataObj.getRows("MdDeviceInfo");
            while (iter.hasNext()) {
                list.add((Long)iter.next().get("RESOURCE_ID"));
            }
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "ManagedDeviceHandler: Exception in getiOS9AndAboveDevices() ", e);
        }
        return list;
    }
    
    public Criteria getMDDeviceiOS9AndAboveCriteria() {
        final Criteria cOSVersion4 = new Criteria(new Column("MdDeviceInfo", "OS_VERSION"), (Object)"4.*", 3);
        final Criteria cOSVersion5 = new Criteria(new Column("MdDeviceInfo", "OS_VERSION"), (Object)"5.*", 3);
        final Criteria cOSVersion6 = new Criteria(new Column("MdDeviceInfo", "OS_VERSION"), (Object)"6.*", 3);
        final Criteria cOSVersion7 = new Criteria(new Column("MdDeviceInfo", "OS_VERSION"), (Object)"7.*", 3);
        final Criteria cOSVersion8 = new Criteria(new Column("MdDeviceInfo", "OS_VERSION"), (Object)"8.*", 3);
        final Criteria cOSVersion9 = cOSVersion4.and(cOSVersion5).and(cOSVersion6).and(cOSVersion7).and(cOSVersion8);
        return cOSVersion9;
    }
    
    public List getSupervisedIos9AndAboveDevicelist(final List deviceIdList) {
        final Criteria cOSVersion = this.getMDDeviceiOS9AndAboveCriteria();
        final List superVisedList = this.getSupervisedIosDevicelist(deviceIdList, cOSVersion);
        return superVisedList;
    }
    
    public boolean isSupervisedAnd11_2Above(final Long resourceId) throws Exception {
        final Row row = DBUtil.getRowFromDB("MdDeviceInfo", "RESOURCE_ID", (Object)resourceId);
        final String osVersion = (String)row.get("OS_VERSION");
        final boolean isSupervised = row.get("IS_SUPERVISED") != null && (boolean)row.get("IS_SUPERVISED");
        final boolean isHigher = osVersion.equals("11.2") || new VersionChecker().isGreater(osVersion, "11.2");
        return isSupervised && isHigher;
    }
    
    public List<Long> getManagedDeviceIdForUserId(final Long userId, final Integer platform, final boolean managedOnly) {
        final List<Long> deviceId = new ArrayList<Long>();
        try {
            final SelectQuery sQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("ManagedUserToDevice"));
            sQuery.addJoin(new Join("ManagedUserToDevice", "ManagedDevice", new String[] { "MANAGED_DEVICE_ID" }, new String[] { "RESOURCE_ID" }, 2));
            Criteria cUser = new Criteria(new Column("ManagedUserToDevice", "MANAGED_USER_ID"), (Object)userId, 0);
            if (platform != null) {
                final Criteria cPlatform = new Criteria(new Column("ManagedDevice", "PLATFORM_TYPE"), (Object)platform, 0);
                cUser = cUser.and(cPlatform);
            }
            sQuery.setCriteria(cUser);
            sQuery.addSelectColumn(Column.getColumn("ManagedDevice", "*"));
            if (managedOnly) {
                final Criteria managedDeviceCri = new Criteria(Column.getColumn("ManagedDevice", "MANAGED_STATUS"), (Object)2, 0);
                sQuery.setCriteria(sQuery.getCriteria().and(managedDeviceCri));
            }
            final DataObject DO = MDMUtil.getPersistence().get(sQuery);
            if (!DO.isEmpty()) {
                final Iterator item = DO.getRows("ManagedDevice");
                while (item.hasNext()) {
                    final Row mdRow = item.next();
                    deviceId.add((Long)mdRow.get("RESOURCE_ID"));
                }
            }
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Exception in getManagedDeviceIdForUserId", e);
        }
        return deviceId;
    }
    
    public List getIOS6andBelowDeviceId(final List resourceId) {
        Criteria criteira = new Criteria(new Column("ManagedDevice", "PLATFORM_TYPE"), (Object)1, 0);
        if (resourceId != null && !resourceId.isEmpty()) {
            criteira = criteira.and(new Criteria(new Column("ManagedDevice", "RESOURCE_ID"), (Object)resourceId.toArray(), 8));
        }
        final Criteria os4Criteria = new Criteria(new Column("MdDeviceInfo", "OS_VERSION"), (Object)"4.*", 2);
        final Criteria os5Criteria = new Criteria(new Column("MdDeviceInfo", "OS_VERSION"), (Object)"5.*", 2);
        final Criteria os6Criteria = new Criteria(new Column("MdDeviceInfo", "OS_VERSION"), (Object)"6.*", 2);
        final Criteria oscriteria = os4Criteria.or(os5Criteria).or(os6Criteria);
        criteira = criteira.and(oscriteria);
        final List deviceList = this.getManagedDeviceIdBasedOnOSVersion(criteira);
        return deviceList;
    }
    
    private List getManagedDeviceIdBasedOnOSVersion(final Criteria osCriteria) {
        final List deviceIdList = new ArrayList();
        try {
            final SelectQuery sQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("ManagedDevice"));
            sQuery.addJoin(new Join("ManagedDevice", "MdDeviceInfo", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2));
            if (osCriteria != null) {
                sQuery.setCriteria(osCriteria);
            }
            sQuery.addSelectColumn(Column.getColumn("ManagedDevice", "*"));
            final DataObject DO = MDMUtil.getPersistence().get(sQuery);
            if (!DO.isEmpty()) {
                final Iterator item = DO.getRows("ManagedDevice");
                while (item.hasNext()) {
                    final Row mdRow = item.next();
                    deviceIdList.add(mdRow.get("RESOURCE_ID"));
                }
            }
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Exception in getManagedDeviceIdBasedOnOSVersion", e);
        }
        return deviceIdList;
    }
    
    private Integer getManagedDeviceCountBasedOnOSVersion(final Criteria osCriteria) {
        int deviceCount = 0;
        try {
            final SelectQuery sQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("ManagedDevice"));
            sQuery.addJoin(new Join("ManagedDevice", "MdDeviceInfo", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2));
            if (osCriteria != null) {
                sQuery.setCriteria(osCriteria);
            }
            deviceCount = DBUtil.getRecordActualCount(sQuery, "ManagedDevice", "RESOURCE_ID");
        }
        catch (final Exception ex) {
            this.logger.severe("Error in ManagedDeviceHandler.getManagedDeviceCountBasedOnOSVersion : " + ex.getStackTrace());
        }
        return deviceCount;
    }
    
    public Integer getManagedDeviceStatus(final String deviceUDID) throws Exception {
        int deviceManagedStatus = -1;
        final Criteria criteria = new Criteria(Column.getColumn("ManagedDevice", "UDID"), (Object)deviceUDID, 0);
        final DataObject dataObject = MDMUtil.getPersistence().get("ManagedDevice", criteria);
        if (!dataObject.isEmpty()) {
            deviceManagedStatus = (int)dataObject.getFirstValue("ManagedDevice", "MANAGED_STATUS");
        }
        return deviceManagedStatus;
    }
    
    public Integer getManagedDeviceStatus(final Long resourceID) {
        Integer managedDeviceStatus = null;
        try {
            final Criteria criteria = new Criteria(Column.getColumn("ManagedDevice", "RESOURCE_ID"), (Object)resourceID, 0);
            final DataObject managedDeviceDO = MDMUtil.getPersistence().get("ManagedDevice", criteria);
            Row managedDeviceRow = null;
            if (!managedDeviceDO.isEmpty()) {
                managedDeviceRow = managedDeviceDO.getFirstRow("ManagedDevice");
                managedDeviceStatus = (Integer)managedDeviceRow.get("MANAGED_STATUS");
            }
        }
        catch (final Exception ex) {
            this.logger.log(Level.WARNING, "Exception while getting Managed Device Status ", ex);
        }
        return managedDeviceStatus;
    }
    
    public String getUDIDFromResourceID(final Long resourceID) {
        String deviceUDID = null;
        try {
            final Criteria criteria = new Criteria(Column.getColumn("ManagedDevice", "RESOURCE_ID"), (Object)resourceID, 0);
            final DataObject dataObj = MDMUtil.getPersistence().get("ManagedDevice", criteria);
            if (!dataObj.isEmpty()) {
                deviceUDID = (String)dataObj.getFirstValue("ManagedDevice", "UDID");
            }
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, "Exception in getUDIDFromResourceID", ex);
        }
        return deviceUDID;
    }
    
    public Map<Long, String> getResourceID_UDID(final List<Long> resourceIDList) {
        final Map<Long, String> deviceResIDUDIDMap = new HashMap<Long, String>();
        try {
            final Criteria criteria = new Criteria(Column.getColumn("ManagedDevice", "RESOURCE_ID"), (Object)resourceIDList.toArray(), 8);
            final DataObject dataObj = MDMUtil.getPersistence().get("ManagedDevice", criteria);
            if (!dataObj.isEmpty()) {
                final Iterator deviceIterator = dataObj.getRows("ManagedDevice");
                while (deviceIterator.hasNext()) {
                    final Row row = deviceIterator.next();
                    deviceResIDUDIDMap.put((Long)row.get("RESOURCE_ID"), (String)row.get("UDID"));
                }
            }
        }
        catch (final Exception ex) {
            ex.printStackTrace();
        }
        return deviceResIDUDIDMap;
    }
    
    public Long getResourceIDFromUDID(final String strUDID) {
        Long resourceID = null;
        try {
            final Criteria criteria = new Criteria(Column.getColumn("ManagedDevice", "UDID"), (Object)strUDID, 0);
            final DataObject dataObj = MDMUtil.getPersistence().get("ManagedDevice", criteria);
            if (!dataObj.isEmpty()) {
                resourceID = (Long)dataObj.getFirstValue("ManagedDevice", "RESOURCE_ID");
            }
        }
        catch (final Exception ex) {
            this.logger.log(Level.WARNING, "Exception occurred in getResourceIDFromUDID(){0}", ex);
        }
        this.logger.log(Level.FINE, "getResourceIDFromUDID() UDID {1} -> returning resourceID {0}", new Object[] { resourceID, strUDID });
        return resourceID;
    }
    
    public Long getResourceIDFromUDID(final String udid, final Long customerId) throws DataAccessException {
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("ManagedDevice"));
        selectQuery.addJoin(new Join("ManagedDevice", "Resource", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2));
        selectQuery.setCriteria(new Criteria(Column.getColumn("Resource", "CUSTOMER_ID"), (Object)customerId, 0).and(new Criteria(Column.getColumn("ManagedDevice", "UDID"), (Object)udid, 0)));
        selectQuery.addSelectColumn(Column.getColumn("Resource", "RESOURCE_ID"));
        selectQuery.addSelectColumn(Column.getColumn("ManagedDevice", "RESOURCE_ID"));
        final DataObject dO = MDMUtil.getPersistence().get(selectQuery);
        if (dO.isEmpty()) {
            return null;
        }
        final Row row = dO.getRow("ManagedDevice");
        return (Long)row.get("RESOURCE_ID");
    }
    
    public List getCustomDeviceNameListFromDB(final Long[] resourceIds) {
        final List customDeviceNameList = new ArrayList();
        try {
            final Criteria resIdCriteria = new Criteria(Column.getColumn("ManagedDeviceExtn", "MANAGED_DEVICE_ID"), (Object)resourceIds, 8);
            final DataObject availableDO = MDMUtil.getPersistence().get("ManagedDeviceExtn", resIdCriteria);
            final Iterator doIterator = availableDO.getRows("ManagedDeviceExtn");
            while (doIterator.hasNext()) {
                final Row r = doIterator.next();
                customDeviceNameList.add(r.get("NAME"));
            }
            this.logger.log(Level.INFO, "getCustomDeviceNamelist obtained from db : {0}", customDeviceNameList);
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Exception occurred in getCustomDeviceNameListFromDB : {0}", e);
        }
        return customDeviceNameList;
    }
    
    public String getDeviceNameFromResource(final long resourceId) throws Exception {
        try {
            this.logger.log(Level.INFO, "In getDeviceName input : {0}", resourceId);
            return (String)DBUtil.getValueFromDB("Resource", "RESOURCE_ID", (Object)resourceId, "NAME");
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, "Exception in getDeviceName :{0}", ex);
            throw ex;
        }
    }
    
    public ArrayList getIOSNativeAppInstalledDeviceResId(final Long customerId) {
        return this.getNativeAppInstalledDeviceResId(customerId, 1);
    }
    
    public ArrayList getWindowsNativeAppInstalledDeviceResId(final Long customerId) {
        return this.getNativeAppInstalledDeviceResId(customerId, 3);
    }
    
    private ArrayList getNativeAppInstalledDeviceResId(final Long customerId, final int platform) {
        final ArrayList resourceList = new ArrayList();
        try {
            final SelectQuery sQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("Resource"));
            sQuery.addJoin(new Join("Resource", "ManagedDevice", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2));
            sQuery.addJoin(new Join("ManagedDevice", "IOSNativeAppStatus", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2));
            final Criteria cPlatform = new Criteria(Column.getColumn("ManagedDevice", "PLATFORM_TYPE"), (Object)platform, 0);
            final Criteria cStatus = new Criteria(Column.getColumn("ManagedDevice", "MANAGED_STATUS"), (Object)2, 0);
            final Criteria cAppStatus = new Criteria(Column.getColumn("IOSNativeAppStatus", "INSTALLATION_STATUS"), (Object)1, 0);
            Criteria criteria = cPlatform.and(cStatus).and(cAppStatus);
            if (customerId != null) {
                criteria = criteria.and(new Criteria(Column.getColumn("Resource", "CUSTOMER_ID"), (Object)customerId, 0));
            }
            final DataObject DO = MDMUtil.getPersistence().get("ManagedDevice", criteria);
            if (!DO.isEmpty()) {
                final Iterator item = DO.getRows("ManagedDevice");
                while (item.hasNext()) {
                    final Row mdRow = item.next();
                    resourceList.add(mdRow.get("RESOURCE_ID"));
                }
            }
        }
        catch (final Exception ex) {
            this.logger.log(Level.WARNING, "Exception occurred in getResourceIDFromUDID(){0}", ex);
        }
        return resourceList;
    }
    
    public String getDeviceName(final long resourceId) throws Exception {
        try {
            this.logger.log(Level.INFO, "In getCustomDeviceName input : {0}", resourceId);
            return (String)DBUtil.getValueFromDB("ManagedDeviceExtn", "MANAGED_DEVICE_ID", (Object)resourceId, "NAME");
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, "Exception in getCustomDeviceName :{0}", ex);
            throw ex;
        }
    }
    
    public boolean isCustomDetailsModified(final Long resourceId) throws Exception {
        boolean isCustomDetailsModified = false;
        try {
            isCustomDetailsModified = (boolean)DBUtil.getValueFromDB("ManagedDeviceExtn", "MANAGED_DEVICE_ID", (Object)resourceId, "IS_MODIFIED");
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Exception in isCustomDetailsModified()", e);
        }
        return isCustomDetailsModified;
    }
    
    public HashMap getManagedDeviceIdNameMap(final Long customerId) throws DataAccessException {
        try {
            final HashMap responseMap = new HashMap();
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("ManagedDevice"));
            final Join managedDeviceExtnJoin = new Join("ManagedDevice", "ManagedDeviceExtn", new String[] { "RESOURCE_ID" }, new String[] { "MANAGED_DEVICE_ID" }, 2);
            final Join resourceJoin = new Join("ManagedDevice", "Resource", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2);
            selectQuery.addJoin(managedDeviceExtnJoin);
            selectQuery.addJoin(resourceJoin);
            final Criteria managedDeviceCriteria = new Criteria(Column.getColumn("ManagedDevice", "MANAGED_STATUS"), (Object)2, 0);
            final Criteria customerCriteria = new Criteria(Column.getColumn("Resource", "CUSTOMER_ID"), (Object)customerId, 0);
            selectQuery.setCriteria(managedDeviceCriteria.and(customerCriteria));
            selectQuery.addSelectColumn(Column.getColumn("ManagedDeviceExtn", "MANAGED_DEVICE_ID"));
            selectQuery.addSelectColumn(Column.getColumn("ManagedDeviceExtn", "NAME"));
            selectQuery.addSelectColumn(Column.getColumn("ManagedDevice", "RESOURCE_ID"));
            selectQuery.addSelectColumn(Column.getColumn("Resource", "RESOURCE_ID"));
            final DataObject dataObject = MDMUtil.getPersistenceLite().get(selectQuery);
            if (!dataObject.isEmpty()) {
                final Iterator iterator = dataObject.getRows("ManagedDeviceExtn");
                while (iterator.hasNext()) {
                    final Row managedDeviceExtnRow = iterator.next();
                    final Long resourceId = (Long)managedDeviceExtnRow.get("MANAGED_DEVICE_ID");
                    final String deviceName = (String)managedDeviceExtnRow.get("NAME");
                    responseMap.put(resourceId, deviceName);
                }
            }
            return responseMap;
        }
        catch (final DataAccessException e) {
            this.logger.log(Level.SEVERE, "Exception in getDeviceIdNameMap", (Throwable)e);
            throw e;
        }
    }
    
    public HashMap getDeviceNames(final List resourceIds) throws Exception {
        final HashMap<Long, String> resourceVsName = new HashMap<Long, String>();
        try {
            this.logger.log(Level.INFO, "In getCustomDeviceName input : {0}", resourceIds);
            final Criteria resourceCriteria = new Criteria(new Column("ManagedDeviceExtn", "MANAGED_DEVICE_ID"), (Object)resourceIds.toArray(), 8);
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("ManagedDeviceExtn"));
            selectQuery.addSelectColumn(Column.getColumn("ManagedDeviceExtn", "MANAGED_DEVICE_ID"));
            selectQuery.addSelectColumn(Column.getColumn("ManagedDeviceExtn", "NAME"));
            selectQuery.setCriteria(resourceCriteria);
            final DataObject dObj = SyMUtil.getPersistence().get(selectQuery);
            if (dObj.size("ManagedDeviceExtn") > 0) {
                final Iterator<Row> rows = dObj.getRows("ManagedDeviceExtn");
                while (rows.hasNext()) {
                    final Row row = rows.next();
                    resourceVsName.put((Long)row.get("MANAGED_DEVICE_ID"), row.get("NAME").toString());
                }
            }
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, "Exception in getCustomDeviceName :{0}", ex);
            throw ex;
        }
        return resourceVsName;
    }
    
    public boolean isWindows81OrAboveDevice(final Long resourceID) {
        return this.isOsVersionGreaterThanForResource(resourceID, 8.1f);
    }
    
    public boolean isOsVersionGreaterThanForResource(final Long resourceID, final Float osVersionToCompare) {
        final HashMap deviceMap = MDMUtil.getInstance().getMDMDeviceProperties(resourceID);
        String osVersion = null;
        if (deviceMap != null) {
            osVersion = deviceMap.get("OS_VERSION");
            if (osVersion != null) {
                if (!osVersion.trim().equals("")) {
                    return this.isOsVersionGreaterThan(osVersion, osVersionToCompare);
                }
            }
            try {
                osVersion = (String)DBUtil.getValueFromDB("MdOSDetailsTemp", "RESOURCE_ID", (Object)resourceID, "OS_VERSION");
            }
            catch (final Exception ex) {
                this.logger.log(Level.SEVERE, "Exception in obtaining os details from temp table {0}", ex);
            }
        }
        return this.isOsVersionGreaterThan(osVersion, osVersionToCompare);
    }
    
    public int getPlatformForErid(final Long erid) {
        int platform = 1;
        try {
            platform = (int)DBUtil.getValueFromDB("DeviceEnrollmentRequest", "ENROLLMENT_REQUEST_ID", (Object)erid, "PLATFORM_TYPE");
        }
        catch (final Exception ex) {
            Logger.getLogger(ManagedDeviceHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
        return platform;
    }
    
    private int getPlatformTypeFromCriteria(final Criteria cri) {
        int platformType = -1;
        try {
            final DataObject dObj = DataAccess.get("ManagedDevice", cri);
            if (!dObj.isEmpty()) {
                platformType = (int)dObj.getValue("ManagedDevice", "PLATFORM_TYPE", cri);
            }
        }
        catch (final DataAccessException ex) {
            this.logger.log(Level.SEVERE, null, (Throwable)ex);
        }
        return platformType;
    }
    
    public List<JSONObject> getPlatformTypeForListOfDeviceIDs(final List<Long> resourceIDs) {
        final List<JSONObject> deviceDetails = new ArrayList<JSONObject>();
        try {
            final DataObject dObj = SyMUtil.getPersistence().get("ManagedDevice", new Criteria(Column.getColumn("ManagedDevice", "RESOURCE_ID"), (Object)resourceIDs.toArray(), 8).and(new Criteria(Column.getColumn("ManagedDevice", "MANAGED_STATUS"), (Object)2, 0)));
            if (dObj != null && !dObj.isEmpty()) {
                final Iterator iterator = dObj.getRows("ManagedDevice");
                while (iterator != null && iterator.hasNext()) {
                    final Row managedDeviceRow = iterator.next();
                    final JSONObject curDeviceDetails = new JSONObject();
                    curDeviceDetails.put("RESOURCE_ID", (Object)managedDeviceRow.get("RESOURCE_ID"));
                    curDeviceDetails.put("PLATFORM_TYPE", (Object)managedDeviceRow.get("PLATFORM_TYPE"));
                    deviceDetails.add(curDeviceDetails);
                }
            }
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, null, ex);
        }
        return deviceDetails;
    }
    
    public boolean isWindows81OrAboveDevice(final String osVersion) {
        return this.isOsVersionGreaterThan(osVersion, 8.1f);
    }
    
    public boolean isOsVersionGreaterThan(final String osVersionString, final Float comparisonValue) {
        boolean retVal;
        if (osVersionString != null) {
            final String[] osVersionSplit = osVersionString.split("\\.");
            Float fOsVersion;
            if (osVersionSplit.length > 1) {
                fOsVersion = Float.parseFloat(osVersionSplit[0] + "." + osVersionSplit[1]);
            }
            else {
                fOsVersion = Float.parseFloat(osVersionSplit[0]);
            }
            if (fOsVersion >= comparisonValue) {
                retVal = Boolean.TRUE;
            }
            else {
                retVal = Boolean.FALSE;
            }
        }
        else {
            retVal = Boolean.FALSE;
        }
        return retVal;
    }
    
    public ArrayList getManagedDeviceIDBasedOnVersionCode(final int platformType, final int versionCode) {
        final Criteria above_AgentVerCri = new Criteria(Column.getColumn("ManagedDevice", "AGENT_VERSION_CODE"), (Object)versionCode, 4);
        final Criteria platformCri = new Criteria(Column.getColumn("ManagedDevice", "PLATFORM_TYPE"), (Object)platformType, 0);
        return this.getManagedDeviceResourceIDs(platformCri.and(above_AgentVerCri));
    }
    
    public int getPlatformType(final Long resourceId) {
        final int platformType = -1;
        final Criteria cri = new Criteria(Column.getColumn("ManagedDevice", "RESOURCE_ID"), (Object)resourceId, 0);
        return this.getPlatformTypeFromCriteria(cri);
    }
    
    public int getPlatformType(final String udid) {
        final Criteria cri = new Criteria(Column.getColumn("ManagedDevice", "UDID"), (Object)udid, 0);
        return this.getPlatformTypeFromCriteria(cri);
    }
    
    public List getManagedDeviceList() throws Exception {
        return this.getManagedDeviceList(null);
    }
    
    public List getManagedDeviceList(final Criteria criteria) throws Exception {
        final String skipCustomerFilter = CustomerInfoThreadLocal.getSkipCustomerFilter();
        boolean flagSet = false;
        try {
            if (skipCustomerFilter != null && skipCustomerFilter.equals("false")) {
                CustomerInfoThreadLocal.setSkipCustomerFilter("true");
                flagSet = true;
            }
            final List managedDeviceList = new ArrayList();
            final SelectQueryImpl query = new SelectQueryImpl(Table.getTable("DeviceEnrollmentRequest"));
            query.addJoin(new Join("DeviceEnrollmentRequest", "ManagedUser", new String[] { "MANAGED_USER_ID" }, new String[] { "MANAGED_USER_ID" }, 2));
            query.addJoin(new Join("ManagedUser", "Resource", new String[] { "MANAGED_USER_ID" }, new String[] { "RESOURCE_ID" }, 2));
            query.addJoin(new Join("DeviceEnrollmentRequest", "EnrollmentRequestToDevice", new String[] { "ENROLLMENT_REQUEST_ID" }, new String[] { "ENROLLMENT_REQUEST_ID" }, 2));
            query.addJoin(new Join("EnrollmentRequestToDevice", "ManagedDevice", new String[] { "MANAGED_DEVICE_ID" }, new String[] { "RESOURCE_ID" }, 2));
            query.addJoin(new Join("ManagedDevice", "ManagedDeviceExtn", new String[] { "RESOURCE_ID" }, new String[] { "MANAGED_DEVICE_ID" }, 2));
            query.addJoin(new Join(new Table("ManagedDevice"), new Table("Resource", "DeviceResource"), new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2));
            query.addSelectColumn(Column.getColumn("DeviceEnrollmentRequest", "ENROLLMENT_REQUEST_ID"));
            query.addSelectColumn(Column.getColumn("DeviceEnrollmentRequest", "MANAGED_USER_ID"));
            query.addSelectColumn(Column.getColumn("EnrollmentRequestToDevice", "ENROLLMENT_REQUEST_ID"));
            query.addSelectColumn(Column.getColumn("EnrollmentRequestToDevice", "MANAGED_DEVICE_ID", "ENROLLMENTREQUESTTODEVICE.MANAGED_DEVICE_ID"));
            query.addSelectColumn(Column.getColumn("ManagedDeviceExtn", "MANAGED_DEVICE_ID", "MANAGEDDEVICEEXTN.MANAGED_DEVICE_ID"));
            query.addSelectColumn(Column.getColumn("ManagedDeviceExtn", "NAME"));
            query.addSelectColumn(Column.getColumn("Resource", "RESOURCE_ID"));
            query.addSelectColumn(Column.getColumn("Resource", "NAME"));
            final Criteria enrollDeviceCriteria = new Criteria(Column.getColumn("ManagedDevice", "MANAGED_STATUS"), (Object)2, 0);
            final Criteria userNotInTrashCriteria = new Criteria(Column.getColumn("ManagedUser", "STATUS"), (Object)11, 1);
            if (criteria != null) {
                query.setCriteria(enrollDeviceCriteria.and(criteria).and(userNotInTrashCriteria));
            }
            else {
                query.setCriteria(userNotInTrashCriteria);
            }
            final DataObject dataObject = MDMUtil.getPersistence().get((SelectQuery)query);
            if (!dataObject.isEmpty()) {
                final Iterator iterator = dataObject.getRows("ManagedDeviceExtn");
                while (iterator.hasNext()) {
                    final Row deviceRow = iterator.next();
                    final HashMap deviceInfo = new HashMap();
                    final Row requestRow = dataObject.getRow("EnrollmentRequestToDevice", new Criteria(Column.getColumn("EnrollmentRequestToDevice", "MANAGED_DEVICE_ID"), deviceRow.get("MANAGED_DEVICE_ID"), 0));
                    final Row enrollReqRow = dataObject.getRow("DeviceEnrollmentRequest", new Criteria(Column.getColumn("DeviceEnrollmentRequest", "ENROLLMENT_REQUEST_ID"), requestRow.get("ENROLLMENT_REQUEST_ID"), 0));
                    final Row managedUserRow = dataObject.getRow("Resource", new Criteria(Column.getColumn("Resource", "RESOURCE_ID"), enrollReqRow.get("MANAGED_USER_ID"), 0));
                    deviceInfo.put("ENROLLMENT_REQUEST_ID", requestRow.get("ENROLLMENT_REQUEST_ID"));
                    deviceInfo.put("NAME", managedUserRow.get("NAME") + "(" + deviceRow.get("NAME") + ")");
                    managedDeviceList.add(deviceInfo);
                }
            }
            return managedDeviceList;
        }
        catch (final DataAccessException ex) {
            this.logger.log(Level.WARNING, "Exception occured while getting enrolled device list : {0}", (Throwable)ex);
            throw new SyMException(1001, (Throwable)ex);
        }
        finally {
            if (flagSet) {
                CustomerInfoThreadLocal.setSkipCustomerFilter("false");
            }
        }
    }
    
    public org.json.simple.JSONArray getManagedDeviceList(final Long customerID, final Long loginId, final String spiceDeviceSearchVal, final int startIndex, final int noOfObj) {
        final org.json.simple.JSONArray managedDeviceArr = new org.json.simple.JSONArray();
        Criteria spiceSearchCri = null;
        boolean isAndroid5AndAbove = false;
        String osVersion = null;
        if (spiceDeviceSearchVal != null && !spiceDeviceSearchVal.equalsIgnoreCase("")) {
            final Criteria spiceDeviceSearchCri = new Criteria(Column.getColumn("ManagedDeviceExtn", "NAME"), (Object)spiceDeviceSearchVal, 12, false);
            final Criteria spiceUserSearchCri = new Criteria(Column.getColumn("USER_RESOURCE", "NAME", "USER_RESOURCE_NAME"), (Object)spiceDeviceSearchVal, 12, false);
            spiceSearchCri = spiceDeviceSearchCri.or(spiceUserSearchCri);
        }
        final boolean isMDMAdmin = DMUserHandler.isUserInRole(loginId, "All_Managed_Mobile_Devices");
        final SelectQuery deviceQuery = getInstance().getManagedDeviceQuery(customerID, spiceSearchCri);
        final Join deviceInfoJoin = new Join("ManagedDevice", "MdDeviceInfo", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 1);
        final Join modelInfoJoin = new Join("MdDeviceInfo", "MdModelInfo", new String[] { "MODEL_ID" }, new String[] { "MODEL_ID" }, 1);
        final Join securityInfoJoin = new Join("MdDeviceInfo", "MdSecurityInfo", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 1);
        deviceQuery.addJoin(deviceInfoJoin);
        deviceQuery.addJoin(modelInfoJoin);
        deviceQuery.addJoin(securityInfoJoin);
        if (!isMDMAdmin) {
            deviceQuery.addJoin(RBDAUtil.getInstance().getUserDeviceMappingJoin("ManagedDevice", "RESOURCE_ID"));
            final Criteria cgCri = RBDAUtil.getInstance().getUserDeviceMappingCriteria(loginId);
            deviceQuery.setCriteria(deviceQuery.getCriteria().and(cgCri));
        }
        deviceQuery.addSelectColumn(Column.getColumn("MdDeviceInfo", "IMEI"));
        deviceQuery.addSelectColumn(Column.getColumn("MdDeviceInfo", "OS_NAME"));
        deviceQuery.addSelectColumn(Column.getColumn("MdDeviceInfo", "OS_VERSION"));
        deviceQuery.addSelectColumn(Column.getColumn("MdDeviceInfo", "SERIAL_NUMBER"));
        deviceQuery.addSelectColumn(Column.getColumn("MdModelInfo", "MODEL_TYPE"));
        deviceQuery.addSelectColumn(Column.getColumn("MdModelInfo", "MODEL_NAME"));
        deviceQuery.addSelectColumn(Column.getColumn("MdModelInfo", "PRODUCT_NAME"));
        deviceQuery.addSelectColumn(Column.getColumn("MdSecurityInfo", "PASSCODE_PRESENT"));
        DMDataSetWrapper ds = null;
        try {
            deviceQuery.addSortColumn(new SortColumn("ManagedDeviceExtn", "NAME", true));
            final Range deviceRange = new Range(startIndex, noOfObj);
            deviceQuery.setRange(deviceRange);
            ds = DMDataSetWrapper.executeQuery((Object)deviceQuery);
            String deviceType = I18N.getMsg("dc.common.OTHERS", new Object[0]);
            Long deviceId = -1L;
            final int androidNotificationService = MDMAgentSettingsHandler.getInstance().getNotificaitonServiceType(2, customerID);
            while (ds.next()) {
                final org.json.simple.JSONObject managedDeviceJSON = new org.json.simple.JSONObject();
                deviceId = (Long)ds.getValue("RESOURCE_ID");
                osVersion = (String)ds.getValue("OS_VERSION");
                managedDeviceJSON.put((Object)"RESOURCE_ID", (Object)deviceId);
                final int modelType = (int)ds.getValue("MODEL_TYPE");
                if (modelType == 1) {
                    deviceType = I18N.getMsg("dc.mdm.actionlog.appmgmt.smartPhone", new Object[0]);
                }
                else if (modelType == 2) {
                    deviceType = I18N.getMsg("dc.mdm.graphs.tablet", new Object[0]);
                }
                managedDeviceJSON.put((Object)"DEVICE_TYPE", (Object)deviceType);
                managedDeviceJSON.put((Object)"NAME", ds.getValue("NAME"));
                managedDeviceJSON.put((Object)"USER_NAME", ds.getValue("USER_RESOURCE_NAME"));
                managedDeviceJSON.put((Object)"IMEI", ds.getValue("IMEI"));
                String os = "";
                final int platformType = (int)ds.getValue("PLATFORM_TYPE");
                switch (platformType) {
                    case 1: {
                        os = I18N.getMsg("dc.mdm.ios", new Object[0]);
                        break;
                    }
                    case 2: {
                        os = I18N.getMsg("dc.mdm.android", new Object[0]);
                        try {
                            if (androidNotificationService == 2) {
                                final JSONObject pollingConfig = MDMAgentSettingsHandler.getInstance().getScheduledPollingConfig();
                                final Long lastContactTime = (Long)DBUtil.getValueFromDB("AgentContact", "RESOURCE_ID", (Object)deviceId, "LAST_CONTACT_TIME");
                                String nextCntTxt = "(N/A)";
                                if (lastContactTime != null) {
                                    final Integer pollingInterval = pollingConfig.optInt("POLLING_INTERVAL", 60);
                                    final Calendar cal = Calendar.getInstance();
                                    cal.setTime(new Date(lastContactTime));
                                    cal.add(12, pollingInterval);
                                    final Long curTm = System.currentTimeMillis();
                                    if (curTm >= cal.getTimeInMillis()) {
                                        nextCntTxt = "deviceNotReacheable";
                                    }
                                    else {
                                        nextCntTxt = Utils.getEventTime(Long.valueOf(cal.getTimeInMillis())) + "";
                                    }
                                }
                                managedDeviceJSON.put((Object)"androidNextPoll", (Object)nextCntTxt);
                            }
                            if (!osVersion.matches("2.*") && !osVersion.matches("3.*") && !osVersion.matches("4.*")) {
                                isAndroid5AndAbove = true;
                                managedDeviceJSON.put((Object)"isAndroid5AndAbove", (Object)isAndroid5AndAbove);
                            }
                        }
                        catch (final Exception ex) {
                            this.logger.log(Level.WARNING, "Exception in fetching Android poll: {0}", ex);
                        }
                        break;
                    }
                    case 3: {
                        os = I18N.getMsg("dc.common.WINDOWS", new Object[0]);
                        break;
                    }
                }
                managedDeviceJSON.put((Object)"OS", (Object)os);
                managedDeviceJSON.put((Object)"PLATFORM_TYPE", ds.getValue("PLATFORM_TYPE"));
                managedDeviceJSON.put((Object)"OS_VERSION", ds.getValue("OS_VERSION"));
                managedDeviceJSON.put((Object)"SERIAL_NUMBER", ds.getValue("SERIAL_NUMBER"));
                managedDeviceJSON.put((Object)"MODEL_NAME", ds.getValue("MODEL_NAME"));
                managedDeviceJSON.put((Object)"PRODUCT_NAME", ds.getValue("PRODUCT_NAME"));
                managedDeviceJSON.put((Object)"PASSCODE_PRESENT", ds.getValue("PASSCODE_PRESENT"));
                managedDeviceArr.add((Object)managedDeviceJSON);
            }
        }
        catch (final Exception ex2) {
            this.logger.log(Level.WARNING, "Exception in getManagedDeviceList method : {0}", ex2);
        }
        return managedDeviceArr;
    }
    
    public List getAFWEligibleDevices(final Long customerId) {
        final List afwDevicesList = new ArrayList();
        try {
            final SelectQuery sQuery = new GoogleManagedAccountHandler().getAFWDeviceSelectQuery();
            final Criteria androidCri = new Criteria(new Column("ManagedDevice", "PLATFORM_TYPE"), (Object)2, 0);
            final Criteria customerCriteria = new Criteria(new Column("Resource", "CUSTOMER_ID"), (Object)customerId, 0);
            sQuery.setCriteria(androidCri.and(customerCriteria));
            final DataObject dO = MDMUtil.getPersistence().get(sQuery);
            if (dO != null && !dO.isEmpty()) {
                final Iterator managedDeviceItr = dO.getRows("ManagedDevice");
                final Iterator mdDeviceInfoItr = dO.getRows("MdDeviceInfo");
                while (managedDeviceItr.hasNext() && mdDeviceInfoItr.hasNext()) {
                    final Row managedDeviceRow = managedDeviceItr.next();
                    final Row mdDeviceInfoRow = mdDeviceInfoItr.next();
                    if (this.isDeviceAFWCompatible(managedDeviceRow, mdDeviceInfoRow)) {
                        final Properties deviceProps = new Properties();
                        ((Hashtable<String, Object>)deviceProps).put("RESOURCE_ID", managedDeviceRow.get("RESOURCE_ID"));
                        ((Hashtable<String, Object>)deviceProps).put("UDID", managedDeviceRow.get("UDID"));
                        afwDevicesList.add(deviceProps);
                    }
                }
            }
            return afwDevicesList;
        }
        catch (final DataAccessException ex) {
            this.logger.log(Level.SEVERE, "Exception in getAFWEligibleDevices", (Throwable)ex);
            return null;
        }
    }
    
    public boolean isDeviceAFWCompatible(final Row managedDeviceRow, final Row mdDeviceInfoRow) {
        return new GoogleManagedAccountHandler().isAndWhyDeviceNotAFWCompatible(managedDeviceRow, mdDeviceInfoRow) == 0;
    }
    
    public boolean isDeviceAFWCompatible(final Long resourceId) {
        return new GoogleManagedAccountHandler().isAndWhyDeviceNotAFWCompatible(resourceId) == 0;
    }
    
    public JSONObject getEnrollmentDetailsForDevice(final String udid) {
        final JSONObject json = new JSONObject();
        try {
            final SelectQuery sQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("ManagedDevice"));
            sQuery.addJoin(new Join("ManagedDevice", "EnrollmentRequestToDevice", new String[] { "RESOURCE_ID" }, new String[] { "MANAGED_DEVICE_ID" }, 2));
            sQuery.addJoin(new Join("EnrollmentRequestToDevice", "DeviceEnrollmentRequest", new String[] { "ENROLLMENT_REQUEST_ID" }, new String[] { "ENROLLMENT_REQUEST_ID" }, 2));
            sQuery.addJoin(new Join("DeviceEnrollmentRequest", "Resource", new String[] { "MANAGED_USER_ID" }, new String[] { "RESOURCE_ID" }, 2));
            sQuery.addSelectColumn(Column.getColumn("DeviceEnrollmentRequest", "ENROLLMENT_REQUEST_ID"));
            sQuery.addSelectColumn(Column.getColumn("Resource", "RESOURCE_ID"));
            sQuery.addSelectColumn(Column.getColumn("Resource", "CUSTOMER_ID"));
            sQuery.setCriteria(new Criteria(Column.getColumn("ManagedDevice", "UDID"), (Object)udid, 0));
            final DataObject dobj = MDMUtil.getPersistence().get(sQuery);
            if (!dobj.isEmpty()) {
                json.put("ENROLLMENT_REQUEST_ID", (Object)dobj.getFirstValue("DeviceEnrollmentRequest", "ENROLLMENT_REQUEST_ID"));
                json.put("CUSTOMER_ID", (Object)dobj.getFirstValue("Resource", "CUSTOMER_ID"));
                return json;
            }
        }
        catch (final Exception ex) {
            this.logger.severe("Exception occurred while getting customer ID and erid: " + ex);
        }
        return json;
    }
    
    public String getInstalledAgentVersion(final Long resourceId) throws DataAccessException {
        String installedAgentVersion = null;
        final SelectQuery sql = (SelectQuery)new SelectQueryImpl(new Table("ManagedDevice"));
        sql.addSelectColumn(new Column("ManagedDevice", "*"));
        sql.setCriteria(new Criteria(Column.getColumn("ManagedDevice", "RESOURCE_ID"), (Object)resourceId, 0, (boolean)Boolean.FALSE));
        final DataObject dataObject = MDMUtil.getPersistence().get(sql);
        final Row row = dataObject.getFirstRow("ManagedDevice");
        if (row != null) {
            installedAgentVersion = row.get("AGENT_VERSION").toString();
        }
        return installedAgentVersion;
    }
    
    public Long getInstalledAgentVersionCode(final Long resourceId) throws DataAccessException {
        Long installedAgentVersion = null;
        final SelectQuery sql = (SelectQuery)new SelectQueryImpl(new Table("ManagedDevice"));
        sql.addSelectColumn(new Column("ManagedDevice", "*"));
        sql.setCriteria(new Criteria(Column.getColumn("ManagedDevice", "RESOURCE_ID"), (Object)resourceId, 0, (boolean)Boolean.FALSE));
        final DataObject dataObject = MDMUtil.getPersistence().get(sql);
        final Row row = dataObject.getFirstRow("ManagedDevice");
        if (row != null) {
            installedAgentVersion = (Long)row.get("AGENT_VERSION_CODE");
        }
        return installedAgentVersion;
    }
    
    public Boolean isAgentVersionNotNull(final Long resourceId) {
        try {
            final String installedAgentVersion = getInstance().getInstalledAgentVersion(resourceId);
            if (installedAgentVersion != null && !installedAgentVersion.trim().equalsIgnoreCase("")) {
                return Boolean.TRUE;
            }
        }
        catch (final DataAccessException ex) {
            this.logger.log(Level.SEVERE, "Exception in isAgentVersionNotNull", (Throwable)ex);
        }
        return Boolean.FALSE;
    }
    
    public List<String> getUDIDListForResourceIDList(final List resourceList) throws DataAccessException {
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("ManagedDevice"));
        final Criteria resourceCriteria = new Criteria(Column.getColumn("ManagedDevice", "RESOURCE_ID"), (Object)resourceList.toArray(), 8);
        selectQuery.setCriteria(resourceCriteria);
        selectQuery.addSelectColumn(Column.getColumn("ManagedDevice", "UDID"));
        selectQuery.addSelectColumn(Column.getColumn("ManagedDevice", "RESOURCE_ID"));
        final DataObject dao = MDMUtil.getPersistence().get(selectQuery);
        final Iterator rowIter = dao.getRows("ManagedDevice");
        final List<String> udidList = new ArrayList<String>();
        while (rowIter.hasNext()) {
            final Row row = rowIter.next();
            udidList.add((String)row.get("UDID"));
        }
        return udidList;
    }
    
    public List<Long> getInActiveDevice(final Integer platform, final int days) {
        final List<Long> resourceList = new ArrayList<Long>();
        final long currentTime = System.currentTimeMillis();
        final long elapsedTime = currentTime - days * 24 * 60 * 60 * 1000L;
        try {
            Criteria criteria;
            final Criteria elapsedCrit = criteria = new Criteria(Column.getColumn("AgentContact", "LAST_CONTACT_TIME"), (Object)new Long(elapsedTime), 6);
            final SelectQuery sQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("AgentContact"));
            final Join managedDeviceJoin = new Join("AgentContact", "ManagedDevice", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2);
            sQuery.addSelectColumn(Column.getColumn("AgentContact", "RESOURCE_ID"));
            sQuery.addSelectColumn(Column.getColumn("AgentContact", "LAST_CONTACT_TIME"));
            sQuery.addJoin(managedDeviceJoin);
            if (platform != null) {
                final Criteria platformCri = new Criteria(Column.getColumn("ManagedDevice", "PLATFORM_TYPE"), (Object)platform, 0);
                criteria = criteria.and(platformCri);
            }
            sQuery.setCriteria(criteria);
            final DataObject inactiveDO = MDMUtil.getPersistence().get(sQuery);
            if (!inactiveDO.isEmpty()) {
                final Iterator iterator = inactiveDO.getRows("AgentContact");
                while (iterator.hasNext()) {
                    final Row row = iterator.next();
                    resourceList.add((Long)row.get("RESOURCE_ID"));
                }
            }
        }
        catch (final Exception ex) {
            this.logger.log(Level.WARNING, "Exception occurred while getInActiveDevice", ex);
        }
        return resourceList;
    }
    
    public List<Long> getInActiveDevicesByCommand(final Integer platform, final int days, final String commandName) {
        final List<Long> resourceList = new ArrayList<Long>();
        final long currentTime = System.currentTimeMillis();
        final long elapsedTime = currentTime - days * 24 * 60 * 60 * 1000L;
        try {
            final Criteria elapsedCrit = new Criteria(Column.getColumn("AgentContact", "LAST_CONTACT_TIME"), (Object)new Long(elapsedTime), 6);
            final Criteria cmdCrit = new Criteria(Column.getColumn("MdCommands", "COMMAND_UUID"), (Object)commandName, 0);
            final Criteria cmdAddedCrit = new Criteria(Column.getColumn("MdCommandsToDevice", "UPDATED_AT"), (Object)elapsedTime, 6);
            final Criteria cmdStatusCrit = new Criteria(Column.getColumn("MdCommandsToDevice", "RESOURCE_COMMAND_STATUS"), (Object)12, 0);
            Criteria criteria = elapsedCrit.and(cmdCrit).and(cmdAddedCrit).and(cmdStatusCrit);
            final SelectQuery sQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("AgentContact"));
            final Join managedDeviceJoin = new Join("AgentContact", "ManagedDevice", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2);
            final Join mdCommandsToDevJoin = new Join("ManagedDevice", "MdCommandsToDevice", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2);
            final Join mdCommandsJoin = new Join("MdCommandsToDevice", "MdCommands", new String[] { "COMMAND_ID" }, new String[] { "COMMAND_ID" }, 2);
            sQuery.addSelectColumn(Column.getColumn("AgentContact", "RESOURCE_ID"));
            sQuery.addSelectColumn(Column.getColumn("AgentContact", "LAST_CONTACT_TIME"));
            sQuery.addJoin(managedDeviceJoin);
            sQuery.addJoin(mdCommandsToDevJoin);
            sQuery.addJoin(mdCommandsJoin);
            if (platform != null) {
                final Criteria platformCri = new Criteria(Column.getColumn("ManagedDevice", "PLATFORM_TYPE"), (Object)platform, 0);
                criteria = criteria.and(platformCri);
            }
            sQuery.setCriteria(criteria);
            final DataObject inactiveDO = MDMUtil.getPersistence().get(sQuery);
            if (!inactiveDO.isEmpty()) {
                final Iterator iterator = inactiveDO.getRows("AgentContact");
                while (iterator.hasNext()) {
                    final Row row = iterator.next();
                    resourceList.add((Long)row.get("RESOURCE_ID"));
                }
            }
        }
        catch (final Exception ex) {
            this.logger.log(Level.WARNING, "Exception occurred while getInActiveDevice", ex);
        }
        return resourceList;
    }
    
    public void updateManagedDeviceDetails(final List<Long> resources, final JSONObject details) {
        try {
            final Integer status = details.optInt("MANAGED_STATUS");
            final String remarks = details.optString("REMARKS", "");
            final Criteria resourceListCri = new Criteria(Column.getColumn("ManagedDevice", "RESOURCE_ID"), (Object)resources.toArray(), 8);
            final UpdateQuery uQuery = (UpdateQuery)new UpdateQueryImpl("ManagedDevice");
            uQuery.setUpdateColumn("MANAGED_STATUS", (Object)status);
            if (remarks != null && !remarks.isEmpty() && !remarks.equals("")) {
                uQuery.setUpdateColumn("REMARKS", (Object)remarks);
            }
            this.updateManagedDeviceExtnUpdatedTime(resources);
            uQuery.setCriteria(resourceListCri);
            MDMUtil.getPersistence().update(uQuery);
        }
        catch (final Exception ex) {
            this.logger.log(Level.WARNING, "Exception occurred while updateInActiveDevices", ex);
        }
    }
    
    public void updateMangedDeviceUDID(final Long resourceID, final String udid) {
        try {
            final Criteria resourceIdCri = new Criteria(Column.getColumn("ManagedDevice", "RESOURCE_ID"), (Object)resourceID, 0);
            final UpdateQuery query = (UpdateQuery)new UpdateQueryImpl("ManagedDevice");
            query.setCriteria(resourceIdCri);
            query.setUpdateColumn("UDID", (Object)udid);
            MDMUtil.getPersistence().update(query);
        }
        catch (final Exception ex) {
            this.logger.log(Level.WARNING, "Exception occurred while updateMangedDeviceUDID", ex);
        }
    }
    
    public Long getManagedDeviceID(final JSONObject deviceInfo) {
        DMSecurityLogger.info(this.logger, "ManagedDeviceHandler", "getManagedDeviceID", "Duplicate Device Handling Begins. Data : {0}", (Object)deviceInfo);
        Long resourceID = null;
        String imei = deviceInfo.optString("IMEI", (String)null);
        if (imei != null) {
            imei = imei.replace(" ", "");
        }
        final String serialNumber = deviceInfo.optString("SerialNumber", (String)null);
        final String udid = deviceInfo.optString("UDID", (String)null);
        Criteria criteria = null;
        try {
            final Boolean allowDuplicateSerialNumber = MDMFeatureParamsHandler.getInstance().isFeatureEnabled("ALLOW_DUPLICATE_SERIAL_NUMBER");
            if (allowDuplicateSerialNumber) {
                if (MDMStringUtils.isValidDeviceIdentifier(imei)) {
                    criteria = new Criteria(Column.getColumn("MdSIMInfo", "IMEI"), (Object)imei, 0);
                }
                else if (MDMStringUtils.isValidDeviceIdentifier(udid)) {
                    criteria = new Criteria(Column.getColumn("ManagedDevice", "UDID"), (Object)udid, 0);
                }
            }
            else if (MDMStringUtils.isValidDeviceIdentifier(imei)) {
                criteria = new Criteria(Column.getColumn("MdDeviceInfo", "IMEI"), (Object)imei, 0);
            }
            else if (MDMStringUtils.isValidDeviceIdentifier(serialNumber)) {
                criteria = new Criteria(Column.getColumn("MdDeviceInfo", "SERIAL_NUMBER"), (Object)serialNumber, 0);
            }
            else if (MDMStringUtils.isValidDeviceIdentifier(udid)) {
                criteria = new Criteria(Column.getColumn("ManagedDevice", "UDID"), (Object)udid, 0);
            }
            if (criteria != null) {
                final SelectQuery sql = (SelectQuery)new SelectQueryImpl(Table.getTable("ManagedDevice"));
                sql.addJoin(new Join("ManagedDevice", "MdDeviceInfo", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 1));
                sql.addJoin(new Join("MdDeviceInfo", "MdSIMInfo", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 1));
                sql.addSelectColumn(new Column("ManagedDevice", "RESOURCE_ID"));
                sql.setCriteria(criteria);
                final DataObject deviceDO = MDMUtil.getPersistence().get(sql);
                if (deviceDO != null && !deviceDO.isEmpty()) {
                    final Row row = deviceDO.getFirstRow("ManagedDevice");
                    resourceID = (Long)row.get("RESOURCE_ID");
                    this.logger.log(Level.INFO, "Duplicate Device Found : Resource ID : {0}", resourceID);
                }
            }
        }
        catch (final Exception ex) {
            this.logger.log(Level.WARNING, "Exception occurred while getDuplicateDeviceID", ex);
        }
        this.logger.log(Level.INFO, "Duplicate Device Handling Completed");
        return resourceID;
    }
    
    public ArrayList<Long> getDevicesEqualOrAboveOsVersion(final List resourceIdsList, final String osVersionToBeChecked) throws DataAccessException {
        final ArrayList<Long> resourceListWithGreaterOsVersions = new ArrayList<Long>();
        final Criteria resourceListCriteria = new Criteria(new Column("MdDeviceInfo", "RESOURCE_ID"), (Object)resourceIdsList.toArray(), 8);
        final DataObject deviceInfoDo = MDMUtil.getPersistence().get("MdDeviceInfo", resourceListCriteria);
        if (!deviceInfoDo.isEmpty()) {
            final Iterator deviceInfoIter = deviceInfoDo.getRows("MdDeviceInfo");
            while (deviceInfoIter.hasNext()) {
                final Row deviceInfoRow = deviceInfoIter.next();
                if (deviceInfoRow != null) {
                    final String resOsVersion = (String)deviceInfoRow.get("OS_VERSION");
                    final Long resourceID = (Long)deviceInfoRow.get("RESOURCE_ID");
                    final int isVersionGreater = this.checkGreaterOrEqual(resOsVersion, osVersionToBeChecked);
                    if (isVersionGreater != 0) {
                        continue;
                    }
                    resourceListWithGreaterOsVersions.add(resourceID);
                }
            }
        }
        return resourceListWithGreaterOsVersions;
    }
    
    private int checkGreater(final String version1, final String version2) {
        final StringTokenizer stoken = new StringTokenizer(version1, ".");
        final int tokencount = stoken.countTokens();
        for (int i = 0; i < tokencount; ++i) {
            final String tokenString = stoken.nextToken();
            String sec = this.getString(version2, i);
            if (sec.indexOf(".") != -1) {
                sec = sec.substring(0, sec.indexOf("."));
            }
            int fir = 0;
            int secon = 0;
            if (tokenString != null) {
                fir = Integer.parseInt(tokenString);
            }
            if (sec != null) {
                secon = Integer.parseInt(sec);
            }
            if (fir > secon) {
                return 0;
            }
            if (fir < secon) {
                return 1;
            }
        }
        final StringTokenizer ss = new StringTokenizer(version2, ".");
        final int count = ss.countTokens();
        if (tokencount > count) {
            return 0;
        }
        return 1;
    }
    
    private int checkGreaterOrEqual(final String version1, final String version2) {
        final StringTokenizer stoken = new StringTokenizer(version1, ".");
        final int tokencount = stoken.countTokens();
        for (int i = 0; i < tokencount; ++i) {
            final String tokenString = stoken.nextToken();
            String sec = this.getString(version2, i);
            if (sec.indexOf(".") != -1) {
                sec = sec.substring(0, sec.indexOf("."));
            }
            int fir = 0;
            int secon = 0;
            if (tokenString != null) {
                fir = Integer.parseInt(tokenString);
            }
            if (sec != null) {
                secon = Integer.parseInt(sec);
            }
            if (fir < secon) {
                return 1;
            }
        }
        final StringTokenizer ss = new StringTokenizer(version2, ".");
        final int count = ss.countTokens();
        if (tokencount < count) {
            return 1;
        }
        return 0;
    }
    
    private String getString(final String str, final int len) {
        String s1 = str;
        final int i1;
        if (len == 0 && (i1 = s1.indexOf(".")) != -1) {
            final String s2 = s1 = s1.substring(0, i1);
        }
        for (int j = 0; j < len; ++j) {
            final int i2 = s1.indexOf(".");
            String s3;
            s1 = ((i2 != -1) ? (s3 = s1.substring(i2 + 1)) : "0");
        }
        return s1;
    }
    
    public int getManagedDeviceCountForResources(final List resoureceIds, final Criteria osVersionCriteria) {
        try {
            final SelectQuery managedDeviceCount = (SelectQuery)new SelectQueryImpl(Table.getTable("ManagedDevice"));
            final Criteria enrolledCriteria = new Criteria(Column.getColumn("ManagedDevice", "MANAGED_STATUS"), (Object)2, 0);
            final Criteria resourceIDCriteria = new Criteria(Column.getColumn("ManagedDevice", "RESOURCE_ID"), (Object)resoureceIds.toArray(), 8);
            if (osVersionCriteria != null) {
                managedDeviceCount.addJoin(new Join("ManagedDevice", "MdDeviceInfo", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2));
            }
            managedDeviceCount.setCriteria(enrolledCriteria.and(resourceIDCriteria).and(osVersionCriteria));
            final Column distinctManagedCount = Column.getColumn("ManagedDevice", "RESOURCE_ID").count();
            managedDeviceCount.addSelectColumn(distinctManagedCount);
            return DBUtil.getRecordCount(managedDeviceCount);
        }
        catch (final Exception ex) {
            this.logger.log(Level.WARNING, "Exception occured in getManagedDeviceCountInGroups: {0}", ex);
            return 0;
        }
    }
    
    public int getManagedUserCountForResources(final List resoureceIds, final Criteria osVersionCriteria) {
        try {
            final SelectQuery managedUserCount = (SelectQuery)new SelectQueryImpl(Table.getTable("ManagedUserToDevice"));
            managedUserCount.addJoin(new Join("ManagedUserToDevice", "ManagedDevice", new String[] { "MANAGED_DEVICE_ID" }, new String[] { "RESOURCE_ID" }, 2));
            final Criteria enrolledCriteria = new Criteria(Column.getColumn("ManagedDevice", "MANAGED_STATUS"), (Object)2, 0);
            final Criteria resourceIDCriteria = new Criteria(Column.getColumn("ManagedDevice", "RESOURCE_ID"), (Object)resoureceIds.toArray(), 8);
            if (osVersionCriteria != null) {
                managedUserCount.addJoin(new Join("ManagedDevice", "MdDeviceInfo", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2));
            }
            managedUserCount.setCriteria(enrolledCriteria.and(resourceIDCriteria).and(osVersionCriteria));
            final Column distinctManagedUserCount = Column.getColumn("ManagedUserToDevice", "MANAGED_USER_ID").distinct().count();
            managedUserCount.addSelectColumn(distinctManagedUserCount);
            return DBUtil.getRecordCount(managedUserCount);
        }
        catch (final Exception ex) {
            this.logger.log(Level.WARNING, "Exception occured in getUserCountInGroups: {0}", ex);
            return 0;
        }
    }
    
    public int getManagedDeviceCountInGroups(final List groupResourceIds) {
        return this.getManagedDeviceCountInGroups(groupResourceIds, null, -1);
    }
    
    public int getManagedDeviceCountInGroups(final List groupResourceIds, final Criteria osVersionCriteria, final int platformType) {
        try {
            final SelectQuery managedDeviceCount = (SelectQuery)new SelectQueryImpl(Table.getTable("CustomGroupMemberRel"));
            managedDeviceCount.addJoin(new Join("CustomGroupMemberRel", "ManagedDevice", new String[] { "MEMBER_RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2));
            final Criteria enrolledCriteria = new Criteria(Column.getColumn("ManagedDevice", "MANAGED_STATUS"), (Object)2, 0);
            final Criteria groupCriteria = new Criteria(Column.getColumn("CustomGroupMemberRel", "GROUP_RESOURCE_ID"), (Object)groupResourceIds.toArray(), 8);
            Criteria criteria = enrolledCriteria.and(groupCriteria).and(osVersionCriteria);
            if (osVersionCriteria != null) {
                managedDeviceCount.addJoin(new Join("ManagedDevice", "MdDeviceInfo", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2));
            }
            if (platformType != -1) {
                criteria = criteria.and(new Criteria(Column.getColumn("ManagedDevice", "PLATFORM_TYPE"), (Object)platformType, 0));
            }
            managedDeviceCount.setCriteria(criteria);
            final Column distinctManagedDeviceCount = Column.getColumn("CustomGroupMemberRel", "MEMBER_RESOURCE_ID").distinct().count();
            managedDeviceCount.addSelectColumn(distinctManagedDeviceCount);
            return DBUtil.getRecordCount(managedDeviceCount);
        }
        catch (final Exception ex) {
            this.logger.log(Level.WARNING, "Exception occured in getManagedDeviceCountInGroups: {0}", ex);
            return 0;
        }
    }
    
    public int getManagedUserCountInGroups(final List groupResourceIds) {
        return this.getManagedUserCountInGroups(groupResourceIds, null, -1);
    }
    
    public int getManagedUserCountInGroups(final List groupResourceIds, final Criteria osVersionCriteria, final int platformType) {
        try {
            final SelectQuery managedUserCount = (SelectQuery)new SelectQueryImpl(Table.getTable("CustomGroupMemberRel"));
            managedUserCount.addJoin(new Join("CustomGroupMemberRel", "ManagedUserToDevice", new String[] { "MEMBER_RESOURCE_ID" }, new String[] { "MANAGED_DEVICE_ID" }, 2));
            managedUserCount.addJoin(new Join("CustomGroupMemberRel", "ManagedDevice", new String[] { "MEMBER_RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2));
            final Criteria enrolledCriteria = new Criteria(Column.getColumn("ManagedDevice", "MANAGED_STATUS"), (Object)2, 0);
            final Criteria groupCriteria = new Criteria(Column.getColumn("CustomGroupMemberRel", "GROUP_RESOURCE_ID"), (Object)groupResourceIds.toArray(), 8);
            Criteria criteria = enrolledCriteria.and(groupCriteria).and(osVersionCriteria);
            if (osVersionCriteria != null) {
                managedUserCount.addJoin(new Join("ManagedDevice", "MdDeviceInfo", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2));
            }
            if (platformType != -1) {
                criteria = criteria.and(new Criteria(Column.getColumn("ManagedDevice", "PLATFORM_TYPE"), (Object)1, 0));
            }
            managedUserCount.setCriteria(criteria);
            final Column distinctManagedUserCount = Column.getColumn("ManagedUserToDevice", "MANAGED_USER_ID").distinct().count();
            managedUserCount.addSelectColumn(distinctManagedUserCount);
            return DBUtil.getRecordCount(managedUserCount);
        }
        catch (final Exception ex) {
            this.logger.log(Level.WARNING, "Exception occured in getManagedUserCountInGroups: {0}", ex);
            return 0;
        }
    }
    
    public Boolean isDeviceOwner(final Long resourceId) {
        return InventoryUtil.getInstance().isSupervisedDevice(resourceId);
    }
    
    public Boolean isProfileOwner(final Long resourceId) {
        Boolean isProfileOwner = false;
        try {
            final SelectQuery sQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("MdDeviceInfo"));
            final Criteria cResourceId = new Criteria(new Column("MdDeviceInfo", "RESOURCE_ID"), (Object)resourceId, 0);
            final Criteria cSuperVisedDevice = new Criteria(new Column("MdDeviceInfo", "IS_PROFILEOWNER"), (Object)Boolean.TRUE, 0);
            sQuery.setCriteria(cResourceId.and(cSuperVisedDevice));
            sQuery.addSelectColumn(Column.getColumn("MdDeviceInfo", "RESOURCE_ID"));
            final DataObject DO = MDMUtil.getPersistence().get(sQuery);
            if (!DO.isEmpty()) {
                isProfileOwner = true;
            }
        }
        catch (final Exception e) {
            e.printStackTrace();
        }
        return isProfileOwner;
    }
    
    public List filterProfileOwnerResource(final List resourceList) {
        final List filteredList = new ArrayList();
        for (final Long resourceId : resourceList) {
            if (this.isProfileOwner(resourceId)) {
                filteredList.add(resourceId);
            }
        }
        return filteredList;
    }
    
    public boolean isPersonalProfileManaged(final Long resourceId) {
        return this.isProfileOwner(resourceId) && IosNativeAppHandler.getInstance().isIOSNativeAgentInstalled(resourceId);
    }
    
    public Boolean isWin10RedstoneOrAboveOSVersion(final Long resourceID) {
        Boolean retVal = Boolean.FALSE;
        try {
            String osVersion = (String)DBUtil.getValueFromDB("MdDeviceInfo", "RESOURCE_ID", (Object)resourceID, "OS_VERSION");
            if (osVersion == null || osVersion.trim().equalsIgnoreCase("")) {
                osVersion = (String)DBUtil.getValueFromDB("MdOSDetailsTemp", "RESOURCE_ID", (Object)resourceID, "OS_VERSION");
            }
            retVal = this.isWin10RedstoneOrAboveOSVersion(osVersion);
        }
        catch (final Exception e) {
            Logger.getLogger("MDMLogger").log(Level.SEVERE, "Exception in isWin10RedstoneOrAboveResource {0}", e);
        }
        return retVal;
    }
    
    public Boolean isWin10RedstoneOrAboveOSVersion(final String osVersion) {
        return this.compareWindowsOSVersionWithBuildNumber(osVersion, MDMCommonConstants.WIN_10_REDSTONE_BUILD_NUMBER);
    }
    
    public Boolean isWin10Redstone4OrAboveOSVersion(final String osVersion) {
        return this.compareWindowsOSVersionWithBuildNumber(osVersion, MDMCommonConstants.WIN_10_REDSTONE_4_BUILD_NUMBER);
    }
    
    public Boolean isWin10Redstone2OrAboveOSVersion(final String osVersion) {
        return this.compareWindowsOSVersionWithBuildNumber(osVersion, MDMCommonConstants.WIN_10_REDSTONE_2_BUILD_NUMBER);
    }
    
    public Boolean isWin10Redstone5OrAboveOSVersion(final String osVersion) {
        return this.compareWindowsOSVersionWithBuildNumber(osVersion, MDMCommonConstants.WIN_10_REDSTONE_5_BUILD_NUMBER);
    }
    
    public Boolean isWin1020H2OrAboveOSVersion(final String osVersion) {
        return this.compareWindowsOSVersionWithBuildNumber(osVersion, MDMCommonConstants.WIN_10_20H2_BUILD_NUMBER);
    }
    
    public Boolean isWin11OrAboveOSVersion(final String osVersion) {
        return this.compareWindowsOSVersionWithBuildNumber(osVersion, MDMCommonConstants.WIN_11_BUILD_NUMBER);
    }
    
    private Boolean compareWindowsOSVersionWithBuildNumber(final String osVersion, final Long targetBuildNumber) {
        final String[] osVersionSplit = osVersion.split("\\.");
        if (osVersionSplit.length == 4) {
            final long majorVersion = Long.parseLong(osVersionSplit[0]);
            final long minorVersion = Long.parseLong(osVersionSplit[1]);
            final long buildNumber = Long.parseLong(osVersionSplit[2]);
            if (majorVersion > 10L) {
                return Boolean.TRUE;
            }
            if (majorVersion == 10L) {
                if (minorVersion > 0L) {
                    return Boolean.TRUE;
                }
                if (minorVersion == 0L && buildNumber >= targetBuildNumber) {
                    return Boolean.TRUE;
                }
            }
        }
        return Boolean.FALSE;
    }
    
    public int getManagedDeviceCountForUser(final Long managedUserId, final Integer managedStatus, final Criteria c) {
        int count = 0;
        DMDataSetWrapper ds = null;
        SelectQuery sQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("ManagedDevice"));
        if (managedUserId != null && managedStatus != null) {
            final Join managedUserIdJoin = new Join("ManagedDevice", "ManagedUserToDevice", new String[] { "RESOURCE_ID" }, new String[] { "MANAGED_DEVICE_ID" }, 2);
            final Criteria managedUserIdCriteria = new Criteria(new Column("ManagedUserToDevice", "MANAGED_USER_ID"), (Object)managedUserId, 0);
            sQuery.addJoin(managedUserIdJoin);
            sQuery.setCriteria(managedUserIdCriteria);
            final Criteria managedStatusCriteria = new Criteria(new Column("ManagedDevice", "MANAGED_STATUS"), (Object)managedStatus, 0);
            sQuery.setCriteria(managedStatusCriteria);
            if (c != null) {
                sQuery.setCriteria(managedUserIdCriteria.and(managedStatusCriteria.and(c)));
            }
            else {
                sQuery.setCriteria(managedUserIdCriteria.and(managedStatusCriteria.and(c)));
            }
            final Column countColumn = new Column("ManagedDevice", "RESOURCE_ID").count();
            countColumn.setColumnAlias("RESOURCE_ID");
            sQuery.addSelectColumn(countColumn);
            sQuery = RBDAUtil.getInstance().getRBDAQuery(sQuery);
            try {
                ds = DMDataSetWrapper.executeQuery((Object)sQuery);
                while (ds.next()) {
                    count = (int)ds.getValue("RESOURCE_ID");
                }
            }
            catch (final Exception ex) {
                this.logger.log(Level.SEVERE, "Unable to get managed device status and count", ex);
            }
        }
        else {
            this.logger.log(Level.WARNING, "Cannot find user enrollment request status for null values");
        }
        return count;
    }
    
    public boolean isSupervisedAnd9_3Above(final Long resourceId) throws Exception {
        final Row row = DBUtil.getRowFromDB("MdDeviceInfo", "RESOURCE_ID", (Object)resourceId);
        final String osVersion = (String)row.get("OS_VERSION");
        final boolean isSupervised = row.get("IS_SUPERVISED") != null && (boolean)row.get("IS_SUPERVISED");
        final boolean isHigher = osVersion.equals("9.3") || new VersionChecker().isGreater(osVersion, "9.3");
        return isSupervised && isHigher;
    }
    
    public Set<Long> fetchSupervisedAnd9_3Above(final List<Long> resourceList) {
        final Set<Long> applicableSet = new HashSet<Long>();
        try {
            final SelectQuery query = (SelectQuery)new SelectQueryImpl(Table.getTable("ManagedDevice"));
            query.addJoin(new Join("ManagedDevice", "Resource", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 1));
            query.addJoin(new Join("ManagedDevice", "MdDeviceInfo", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 1));
            final Criteria resourceCriteria = new Criteria(new Column("Resource", "RESOURCE_ID"), (Object)resourceList.toArray(), 8);
            final Criteria supervisedCriteria = new Criteria(new Column("MdDeviceInfo", "IS_SUPERVISED"), (Object)true, 0);
            final Criteria ios9_3AboveDevicesCriteria = this.getIos9_3AboveDevicesCriteria();
            query.setCriteria(resourceCriteria.and(ios9_3AboveDevicesCriteria).and(supervisedCriteria));
            query.addSelectColumn(Column.getColumn("MdDeviceInfo", "RESOURCE_ID"));
            final DataObject dataObject = MDMUtil.getPersistence().get(query);
            if (!dataObject.isEmpty()) {
                final Iterator<Row> iterator = dataObject.getRows("MdDeviceInfo");
                while (iterator.hasNext()) {
                    final Row resRow = iterator.next();
                    applicableSet.add(Long.valueOf(resRow.get("RESOURCE_ID").toString()));
                }
            }
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Exception in fetchSupervisedAnd9_3Above", e);
        }
        return applicableSet;
    }
    
    public Criteria getIos9_3AboveDevicesCriteria() {
        final Criteria osVersion4Criteria = new Criteria(Column.getColumn("MdDeviceInfo", "OS_VERSION"), (Object)"4.*", 3);
        final Criteria osVersion5Criteria = new Criteria(Column.getColumn("MdDeviceInfo", "OS_VERSION"), (Object)"5.*", 3);
        final Criteria osVersion6Criteria = new Criteria(Column.getColumn("MdDeviceInfo", "OS_VERSION"), (Object)"6.*", 3);
        final Criteria osVersion7Criteria = new Criteria(Column.getColumn("MdDeviceInfo", "OS_VERSION"), (Object)"7.*", 3);
        final Criteria osVersion8Criteria = new Criteria(Column.getColumn("MdDeviceInfo", "OS_VERSION"), (Object)"8.*", 3);
        final Criteria osVersion9Criteria = new Criteria(Column.getColumn("MdDeviceInfo", "OS_VERSION"), (Object)"9.0", 3);
        final Criteria osVersion9X1Criteria = new Criteria(Column.getColumn("MdDeviceInfo", "OS_VERSION"), (Object)"9.1", 3);
        final Criteria osVersion9X2Criteria = new Criteria(Column.getColumn("MdDeviceInfo", "OS_VERSION"), (Object)"9.2", 3);
        final Criteria osVersionCriteria = osVersion4Criteria.and(osVersion5Criteria).and(osVersion6Criteria).and(osVersion7Criteria).and(osVersion8Criteria).and(osVersion9Criteria).and(osVersion9X1Criteria).and(osVersion9X2Criteria);
        return osVersionCriteria;
    }
    
    public boolean isSupervisedAndEqualOrAboveVersion(final Long resourceId, final String osVersionGiven) throws Exception {
        final Row row = DBUtil.getRowFromDB("MdDeviceInfo", "RESOURCE_ID", (Object)resourceId);
        final String osVersion = (String)row.get("OS_VERSION");
        final boolean isSupervised = row.get("IS_SUPERVISED") != null && (boolean)row.get("IS_SUPERVISED");
        final boolean isHigher = osVersion.equals(osVersionGiven) || new VersionChecker().isGreater(osVersion, osVersionGiven);
        return isSupervised && isHigher;
    }
    
    public boolean isEqualOrAboveOSVersion(final Long resourceId, final String osVersionGiven) throws Exception {
        final Row row = DBUtil.getRowFromDB("MdDeviceInfo", "RESOURCE_ID", (Object)resourceId);
        final String osVersion = (String)row.get("OS_VERSION");
        final boolean isHigher = osVersion.equals(osVersionGiven) || new VersionChecker().isGreater(osVersion, osVersionGiven);
        return isHigher;
    }
    
    public HashMap getManagedDeviceDetails(final String deviceUDID) {
        final HashMap deviceDetails = new HashMap();
        try {
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("Resource"));
            selectQuery.addJoin(new Join("Resource", "ManagedUser", new String[] { "RESOURCE_ID" }, new String[] { "MANAGED_USER_ID" }, 2));
            selectQuery.addJoin(new Join("ManagedUser", "ManagedUserToDevice", new String[] { "MANAGED_USER_ID" }, new String[] { "MANAGED_USER_ID" }, 2));
            selectQuery.addJoin(new Join("ManagedUserToDevice", "ManagedDevice", new String[] { "MANAGED_DEVICE_ID" }, new String[] { "RESOURCE_ID" }, 2));
            selectQuery.addJoin(new Join("ManagedDevice", "ManagedDeviceExtn", new String[] { "RESOURCE_ID" }, new String[] { "MANAGED_DEVICE_ID" }, 2));
            selectQuery.addJoin(new Join("ManagedDevice", "EnrollmentRequestToDevice", new String[] { "RESOURCE_ID" }, new String[] { "MANAGED_DEVICE_ID" }, 2));
            selectQuery.addJoin(new Join("ManagedDevice", "MdDeviceInfo", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2));
            selectQuery.addJoin(new Join("MdDeviceInfo", "MdModelInfo", new String[] { "MODEL_ID" }, new String[] { "MODEL_ID" }, 2));
            selectQuery.addJoin(new Join("ManagedDevice", "MdNetworkInfo", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 1));
            final Criteria userNotInTrashCriteria = new Criteria(Column.getColumn("ManagedUser", "STATUS"), (Object)11, 1).or(new Criteria(Column.getColumn("ManagedUser", "STATUS"), (Object)null, 0));
            selectQuery.setCriteria(new Criteria(Column.getColumn("ManagedDevice", "UDID"), (Object)deviceUDID, 0, false).and(userNotInTrashCriteria));
            selectQuery.addSelectColumn(Column.getColumn((String)null, "*"));
            final DataObject dataObject = MDMUtil.getPersistence().get(selectQuery);
            if (!dataObject.isEmpty()) {
                deviceDetails.put("NAME", dataObject.getValue("Resource", "NAME", (Criteria)null));
                deviceDetails.put("DOMAIN_NETBIOS_NAME", dataObject.getValue("Resource", "DOMAIN_NETBIOS_NAME", (Criteria)null));
                deviceDetails.put("CUSTOMER_ID", dataObject.getValue("Resource", "CUSTOMER_ID", (Criteria)null));
                deviceDetails.put("RESOURCE_ID", dataObject.getValue("ManagedDevice", "RESOURCE_ID", (Criteria)null));
                deviceDetails.put("EMAIL_ADDRESS", dataObject.getValue("ManagedUser", "EMAIL_ADDRESS", (Criteria)null));
                deviceDetails.put("FIRST_NAME", dataObject.getValue("ManagedUser", "FIRST_NAME", (Criteria)null));
                deviceDetails.put("MIDDLE_NAME", dataObject.getValue("ManagedUser", "MIDDLE_NAME", (Criteria)null));
                deviceDetails.put("LAST_NAME", dataObject.getValue("ManagedUser", "LAST_NAME", (Criteria)null));
                deviceDetails.put("DISPLAY_NAME", dataObject.getValue("ManagedUser", "DISPLAY_NAME", (Criteria)null));
                deviceDetails.put("MANAGED_USER_ID", dataObject.getValue("ManagedUser", "MANAGED_USER_ID", (Criteria)null));
                deviceDetails.put("ENROLLMENT_REQUEST_ID", dataObject.getValue("EnrollmentRequestToDevice", "ENROLLMENT_REQUEST_ID", (Criteria)null));
                deviceDetails.put("CUSTOMDEVICENAME", dataObject.getValue("ManagedDeviceExtn", "NAME", (Criteria)null));
                deviceDetails.put("SERIAL_NUMBER", dataObject.getValue("MdDeviceInfo", "SERIAL_NUMBER", (Criteria)null));
                deviceDetails.put("IMEI", dataObject.getValue("MdDeviceInfo", "IMEI", (Criteria)null));
                deviceDetails.put("EAS_DEVICE_IDENTIFIER", dataObject.getValue("MdDeviceInfo", "EAS_DEVICE_IDENTIFIER", (Criteria)null));
                deviceDetails.put("APN_USER_NAME", dataObject.getValue("ManagedDeviceExtn", "APN_USER_NAME", (Criteria)null));
                deviceDetails.put("APN_PASSWORD", dataObject.getValue("ManagedDeviceExtn", "APN_PASSWORD", (Criteria)null));
                deviceDetails.put("MODEL_NAME", dataObject.getValue("MdModelInfo", "MODEL_NAME", (Criteria)null));
                deviceDetails.put("PRODUCT_NAME", dataObject.getValue("MdModelInfo", "PRODUCT_NAME", (Criteria)null));
                deviceDetails.put("ASSET_TAG", dataObject.getValue("ManagedDeviceExtn", "ASSET_TAG", (Criteria)null));
                deviceDetails.put("ASSET_OWNER", dataObject.getValue("ManagedDeviceExtn", "ASSET_OWNER", (Criteria)null));
                deviceDetails.put("OFFICE", dataObject.getValue("ManagedDeviceExtn", "OFFICE", (Criteria)null));
                deviceDetails.put("BRANCH", dataObject.getValue("ManagedDeviceExtn", "BRANCH", (Criteria)null));
                deviceDetails.put("LOCATION", dataObject.getValue("ManagedDeviceExtn", "LOCATION", (Criteria)null));
                deviceDetails.put("AREA_MANAGER", dataObject.getValue("ManagedDeviceExtn", "AREA_MANAGER", (Criteria)null));
                deviceDetails.put("DEVICE_NAME", dataObject.getValue("ManagedDeviceExtn", "NAME", (Criteria)null));
                deviceDetails.put("BLUETOOTH_MAC", dataObject.getValue("MdNetworkInfo", "BLUETOOTH_MAC", (Criteria)null));
                deviceDetails.put("WIFI_MAC", dataObject.getValue("MdNetworkInfo", "WIFI_MAC", (Criteria)null));
                deviceDetails.put("ETHERNET_MACS", dataObject.getValue("MdNetworkInfo", "ETHERNET_MACS", (Criteria)null));
            }
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Exception in getManagedDeviceDetails", e);
        }
        return deviceDetails;
    }
    
    public boolean isDeviceRemoved(final Long managedDeviceID) throws DataAccessException, Exception {
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("Resource"));
        selectQuery.addJoin(new Join("Resource", "ManagedDevice", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2));
        selectQuery.addJoin(new Join("ManagedDevice", "EnrollmentRequestToDevice", new String[] { "RESOURCE_ID" }, new String[] { "MANAGED_DEVICE_ID" }, 2));
        selectQuery.addSelectColumn(Column.getColumn((String)null, "*"));
        selectQuery.setCriteria(new Criteria(Column.getColumn("ManagedDevice", "RESOURCE_ID"), (Object)managedDeviceID, 0));
        final DataObject DO = MDMUtil.getPersistence().get(selectQuery);
        return DO.isEmpty() || DO.getRows("ManagedDevice", new Criteria(Column.getColumn("ManagedDevice", "MANAGED_STATUS"), (Object)7, 0)).hasNext();
    }
    
    public boolean removeDeviceInTrash(final Long managedDeviceID) throws DataAccessException, Exception {
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("Resource"));
        selectQuery.addJoin(new Join("Resource", "ManagedDevice", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2));
        selectQuery.addJoin(new Join("ManagedDevice", "EnrollmentRequestToDevice", new String[] { "RESOURCE_ID" }, new String[] { "MANAGED_DEVICE_ID" }, 2));
        selectQuery.addSelectColumn(Column.getColumn((String)null, "*"));
        selectQuery.setCriteria(new Criteria(Column.getColumn("ManagedDevice", "RESOURCE_ID"), (Object)managedDeviceID, 0));
        final DataObject DO = MDMUtil.getPersistence().get(selectQuery);
        if (DO.isEmpty()) {
            return true;
        }
        if (DO.getRows("ManagedDevice", new Criteria(Column.getColumn("ManagedDevice", "MANAGED_STATUS"), (Object)7, 0)).hasNext()) {
            this.logger.log(Level.INFO, "Removing device from trash...{0}", managedDeviceID);
            final Long enrollmentRequestID = (Long)DO.getFirstValue("EnrollmentRequestToDevice", "ENROLLMENT_REQUEST_ID");
            MDMUtil.getPersistenceLite().delete(new Criteria(new Column("MdCommandsToDevice", "RESOURCE_ID"), (Object)managedDeviceID, 0));
            MDMUtil.getPersistenceLite().delete(new Criteria(new Column("CommandHistory", "RESOURCE_ID"), (Object)managedDeviceID, 0));
            MDMUtil.getPersistenceLite().delete(new Criteria(new Column("MDMCollnToResErrorCode", "RESOURCE_ID"), (Object)managedDeviceID, 0));
            MDMUtil.getPersistenceLite().delete(new Criteria(new Column("ResourceToProfileHistory", "RESOURCE_ID"), (Object)managedDeviceID, 0));
            MDMUtil.getPersistenceLite().delete(new Criteria(new Column("RecentProfileForResource", "RESOURCE_ID"), (Object)managedDeviceID, 0));
            MDMUtil.getPersistence().delete(new Criteria(Column.getColumn("ManagedDevice", "RESOURCE_ID"), (Object)managedDeviceID, 0));
            final DataObject dataObject = MDMUtil.getPersistence().get("DeviceEnrollmentRequest", new Criteria(Column.getColumn("DeviceEnrollmentRequest", "ENROLLMENT_REQUEST_ID"), (Object)enrollmentRequestID, 0));
            if (!dataObject.isEmpty()) {
                final Set<Long> erReqIDSet = new HashSet<Long>();
                erReqIDSet.add(enrollmentRequestID);
                Logger.getLogger("MDMEnrollment").log(Level.INFO, "calling removeNotExistingEnrollmentRequest from remove device in trash");
                MDMEnrollmentUtil.getInstance().removeNotExistingEnrollmentRequest(erReqIDSet);
            }
            return true;
        }
        return false;
    }
    
    public List getManagedDeviceIdFromErids(final List erids, final String columnName) {
        List values = null;
        try {
            final SelectQuery deviceQuery = (SelectQuery)new SelectQueryImpl(new Table("DeviceEnrollmentRequest"));
            final Join requestJoin = new Join("DeviceEnrollmentRequest", "EnrollmentRequestToDevice", new String[] { "ENROLLMENT_REQUEST_ID" }, new String[] { "ENROLLMENT_REQUEST_ID" }, 2);
            final Join deviceJoin = new Join("EnrollmentRequestToDevice", "ManagedDevice", new String[] { "MANAGED_DEVICE_ID" }, new String[] { "RESOURCE_ID" }, 2);
            deviceQuery.addJoin(requestJoin);
            deviceQuery.addJoin(deviceJoin);
            deviceQuery.addSelectColumn(Column.getColumn((String)null, "*"));
            final Criteria eridCriteria = new Criteria(Column.getColumn("DeviceEnrollmentRequest", "ENROLLMENT_REQUEST_ID"), (Object)erids.toArray(), 8);
            deviceQuery.setCriteria(eridCriteria);
            final DataObject dobj = MDMUtil.getPersistence().get(deviceQuery);
            final Iterator deviceItr = dobj.getRows("ManagedDevice");
            values = DBUtil.getColumnValuesAsList(deviceItr, columnName);
        }
        catch (final Exception ex) {
            this.logger.log(Level.WARNING, " Exception thrown in getManagedDeviceIdFromErids ", ex);
        }
        return values;
    }
    
    public Boolean getIsDeviceAFWCompatible(final List resourceList) {
        Boolean isDeviceAFWCompatible = false;
        try {
            final Criteria resListCri = new Criteria(new Column("BSUsersToManagedDevices", "MANAGED_DEVICE_ID"), (Object)resourceList.toArray(), 8);
            final DataObject Do = MDMUtil.getPersistence().get("BSUsersToManagedDevices", resListCri);
            if (!Do.isEmpty()) {
                isDeviceAFWCompatible = true;
            }
        }
        catch (final Exception ex) {
            this.logger.log(Level.WARNING, "Exception occoured in getIsDeviceAFWCompatible....", ex);
        }
        return isDeviceAFWCompatible;
    }
    
    public String getLeastOSVersionOfDevices(final List resourceList) {
        String osVersion = null;
        try {
            final Criteria resListCri = new Criteria(new Column("MdDeviceInfo", "RESOURCE_ID"), (Object)resourceList.toArray(), 8);
            final DataObject Do = MDMUtil.getPersistence().get("MdDeviceInfo", resListCri);
            if (!Do.isEmpty()) {
                final SortColumn sortColumn = new SortColumn(Column.getColumn("MdDeviceInfo", "OS_VERSION"), true);
                Do.sortRows("MdDeviceInfo", new SortColumn[] { sortColumn });
                final Row mdDeviceInfoRow = Do.getFirstRow("MdDeviceInfo");
                osVersion = (String)mdDeviceInfoRow.get("OS_VERSION");
            }
        }
        catch (final Exception ex) {
            this.logger.log(Level.WARNING, "Exception occoured in getLeastOSVersionOfDevices....", ex);
        }
        return osVersion;
    }
    
    public Boolean checkIfTheseDevicesAreNotSupervised(final List resourceList) {
        Boolean isDeviceListContainsNonSupervisedDevice = false;
        try {
            final Criteria resListCri = new Criteria(new Column("MdDeviceInfo", "RESOURCE_ID"), (Object)resourceList.toArray(), 8);
            final Criteria nonSupervisedCri = new Criteria(new Column("MdDeviceInfo", "IS_SUPERVISED"), (Object)false, 0);
            final DataObject Do = MDMUtil.getPersistence().get("MdDeviceInfo", resListCri.and(nonSupervisedCri));
            if (!Do.isEmpty()) {
                isDeviceListContainsNonSupervisedDevice = true;
            }
        }
        catch (final Exception ex) {
            this.logger.log(Level.WARNING, "Exception occoured in getLeastOSVersionOfDevices....", ex);
        }
        return isDeviceListContainsNonSupervisedDevice;
    }
    
    public int getCustomerManagedDeviceCount(final Criteria criteria) {
        int manageddevices = 0;
        CustomerInfoThreadLocal.setSkipCustomerFilter("true");
        final SelectQuery query = (SelectQuery)new SelectQueryImpl(Table.getTable("Resource"));
        final Join resourceJoin = new Join("Resource", "ManagedDevice", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2);
        final Column resIdColumn = new Column("ManagedDevice", "RESOURCE_ID");
        final Column managedDeviceCountColumn = resIdColumn.count();
        managedDeviceCountColumn.setColumnAlias("RESOURCE_ID");
        Criteria mdCriteria = getInstance().getSuccessfullyEnrolledCriteria();
        query.addJoin(resourceJoin);
        query.addSelectColumn(managedDeviceCountColumn);
        DMDataSetWrapper ds = null;
        if (criteria != null) {
            mdCriteria = mdCriteria.and(criteria);
        }
        query.setCriteria(mdCriteria);
        try {
            ds = DMDataSetWrapper.executeQuery((Object)query);
            while (ds.next()) {
                manageddevices = (int)ds.getValue("RESOURCE_ID");
            }
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, "Exception occured in getCustomerManagedDeviceCount: {0}", ex);
        }
        return manageddevices;
    }
    
    public int getManagedDeviceCountForCustomer(final long customerID) {
        int manageddevices = 0;
        Criteria customerIDCriteria = new Criteria(new Column("Resource", "CUSTOMER_ID"), (Object)customerID, 0);
        final Criteria resourceTypeCriteria = MDMApiFactoryProvider.getMDMUtilAPI().getManagedDeviceCountCriteriaForLicenseCheck();
        if (resourceTypeCriteria != null) {
            customerIDCriteria = customerIDCriteria.and(resourceTypeCriteria);
        }
        manageddevices = this.getCustomerManagedDeviceCount(customerIDCriteria);
        return manageddevices;
    }
    
    public int getManagedDeviceCountForCustomers(final List customerIDs) {
        int manageddevices = 0;
        Criteria customerIDCriteria = new Criteria(new Column("Resource", "CUSTOMER_ID"), (Object)customerIDs.toArray(), 8);
        final Criteria resourceTypeCriteria = MDMApiFactoryProvider.getMDMUtilAPI().getManagedDeviceCountCriteriaForLicenseCheck();
        if (resourceTypeCriteria != null) {
            customerIDCriteria = customerIDCriteria.and(resourceTypeCriteria);
        }
        manageddevices = this.getCustomerManagedDeviceCount(customerIDCriteria);
        return manageddevices;
    }
    
    public void addOrUpdateManagedDeviceUniqueIdsRow(final Long resourceID, final JSONObject deviceIdsJSON) throws Exception {
        final DataObject mdDeviceUniqueIDDO = MDMUtil.getPersistence().get("MdDeviceUniqueIds", new Criteria(Column.getColumn("MdDeviceUniqueIds", "RESOURCE_ID"), (Object)resourceID, 0));
        Row mdDeviceUniqueIDRow = null;
        if (mdDeviceUniqueIDDO.isEmpty()) {
            mdDeviceUniqueIDRow = new Row("MdDeviceUniqueIds");
            deviceIdsJSON.get("UDID");
        }
        else {
            mdDeviceUniqueIDRow = mdDeviceUniqueIDDO.getFirstRow("MdDeviceUniqueIds");
        }
        mdDeviceUniqueIDRow.set("RESOURCE_ID", (Object)resourceID);
        final List columnNames = mdDeviceUniqueIDRow.getColumns();
        for (final Object columnName : columnNames) {
            final String colName = String.valueOf(columnName);
            if (!colName.equalsIgnoreCase("RESOURCE_ID")) {
                final String newValue = deviceIdsJSON.optString(colName, (String)null);
                if (MDMStringUtils.isEmpty(newValue)) {
                    continue;
                }
                mdDeviceUniqueIDRow.set(colName, (Object)newValue);
            }
        }
        if (mdDeviceUniqueIDDO.isEmpty()) {
            mdDeviceUniqueIDDO.addRow(mdDeviceUniqueIDRow);
        }
        else {
            mdDeviceUniqueIDDO.updateRow(mdDeviceUniqueIDRow);
        }
        this.logger.log(Level.FINE, "DeviceID JSON obtained is {0}", deviceIdsJSON.toString());
        this.logger.log(Level.FINE, "Updating MdDeviceUniqueIds row for resource ID {0} with the Row {1}", new Object[] { resourceID, mdDeviceUniqueIDRow });
        MDMUtil.getPersistence().update(mdDeviceUniqueIDDO);
        this.logger.log(Level.FINE, "Updated MdDeviceUniqueIds row for resourceID {0}", new Object[] { resourceID });
    }
    
    public Boolean isWindowsDesktopOSDevice(final int modelType) {
        return modelType == 3 || modelType == 2 || modelType == 4;
    }
    
    public void areAvailable(final List<Long> resList, final boolean onlyIfEnrolled) throws DataAccessException, SyMException {
        final Criteria resourceCriteria = new Criteria(Column.getColumn("ManagedDevice", "RESOURCE_ID"), (Object)resList.toArray(new Long[0]), 8);
        final Criteria enrolledCriteria = new Criteria(Column.getColumn("ManagedDevice", "MANAGED_STATUS"), (Object)2, 0);
        Criteria finalCriteria = resourceCriteria;
        if (onlyIfEnrolled) {
            finalCriteria = finalCriteria.and(enrolledCriteria);
        }
        final DataObject dobj = MDMUtil.getPersistence().get("ManagedDevice", finalCriteria);
        final int devicesCount = DBUtil.getIteratorSize(dobj.getRows("ManagedDevice"));
        if (devicesCount < resList.size()) {
            throw new SyMException(404, ((resList.size() == 1) ? "Device is" : "Some devices are") + " not managed", (Throwable)null);
        }
    }
    
    public HashMap getPlatformBasedMemberId(final List resourceList) {
        final HashMap map = new HashMap();
        try {
            final Criteria criteria = new Criteria(Column.getColumn("ManagedDevice", "RESOURCE_ID"), (Object)resourceList.toArray(), 8);
            final DataObject DO = MDMUtil.getPersistence().get("ManagedDevice", criteria);
            final Set ios = new HashSet();
            final Set android = new HashSet();
            final Set windows = new HashSet();
            final Set chrome = new HashSet();
            if (!DO.isEmpty()) {
                final Iterator item = DO.getRows("ManagedDevice");
                while (item.hasNext()) {
                    final Row mdRow = item.next();
                    final int platform = (int)mdRow.get("PLATFORM_TYPE");
                    final Long resourceId = (Long)mdRow.get("RESOURCE_ID");
                    switch (platform) {
                        case 1: {
                            ios.add(resourceId);
                            continue;
                        }
                        case 2: {
                            android.add(resourceId);
                            continue;
                        }
                        case 3: {
                            windows.add(resourceId);
                            continue;
                        }
                        case 4: {
                            chrome.add(resourceId);
                            continue;
                        }
                    }
                }
            }
            map.put(1, ios);
            map.put(2, android);
            map.put(3, windows);
            map.put(4, chrome);
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, "Exception in getMemberGroupsId", ex);
        }
        return map;
    }
    
    public JSONObject deprovisionDevice(final DeprovisionRequest request) throws Exception {
        boolean showRetiredTab = false;
        Logger.getLogger("MDMEnrollment").log(Level.INFO, "Deprovision device: {0}", request.toString());
        final List<Long> appleDeviceList = new ArrayList<Long>();
        final List<Long> failureList = new ArrayList<Long>();
        final List<Long> successList = new ArrayList<Long>();
        final JSONArray successArray = new JSONArray();
        final JSONArray failedArray = new JSONArray();
        final JSONObject ownedByJson = getInstance().getDeviceOwnershipForResourceIDs(request.getManagedDeviceIDList());
        final List<DeviceDetails> corporateWipeList = new ArrayList<DeviceDetails>();
        final List<DeviceDetails> completeWipeList = new ArrayList<DeviceDetails>();
        final List<DeviceDetails> deprovisionDeviceList = new ArrayList<DeviceDetails>();
        final DataObject dataObject = MDMUtil.getPersistence().get("DeprovisionHistory", new Criteria(Column.getColumn("DeprovisionHistory", "RESOURCE_ID"), (Object)request.getManagedDeviceIDList().toArray(), 8));
        final SelectQuery managedDeviceTableSelectQuery = this.getManagedDeviceSelectQuery(request.getManagedDeviceIDList());
        final DataObject managedDeviceDataObject = MDMUtil.getPersistenceLite().get(managedDeviceTableSelectQuery);
        final Integer apnsErrorCode = APNsCertificateHandler.getInstance().getApnsErrorCode();
        final String reason = request.getWipeComment();
        for (final Long managedDeviceID : request.getManagedDeviceIDList()) {
            final DeviceDetails device = new DeviceDetails(managedDeviceID);
            String commandDisplayName = "";
            final int ownedby = ownedByJson.getInt(String.valueOf(managedDeviceID));
            final String udid = (String)managedDeviceDataObject.getValue("ManagedDevice", "UDID", new Criteria(Column.getColumn("ManagedDevice", "RESOURCE_ID"), (Object)managedDeviceID, 0));
            final String serialNumber = (String)managedDeviceDataObject.getValue("MdDeviceInfo", "SERIAL_NUMBER", new Criteria(Column.getColumn("MdDeviceInfo", "RESOURCE_ID"), (Object)managedDeviceID, 0));
            final Boolean profileOwner = (Boolean)managedDeviceDataObject.getValue("MdDeviceInfo", "IS_PROFILEOWNER", new Criteria(Column.getColumn("MdDeviceInfo", "RESOURCE_ID"), (Object)managedDeviceID, 0));
            final Row managedDeviceRow = managedDeviceDataObject.getRow("ManagedDevice", new Criteria(Column.getColumn("ManagedDevice", "RESOURCE_ID"), (Object)managedDeviceID, 0));
            final Integer platformType = (Integer)managedDeviceRow.get("PLATFORM_TYPE");
            if (profileOwner != null && profileOwner) {
                request.setWipeType(1);
            }
            else if (request.getWipeReason() != 4) {
                if (ownedby == 1 && (request.getWipeReason() == 3 || request.getWipeReason() == 1 || request.getWipeReason() == 2)) {
                    request.setWipeType(2);
                    if (request.getWipeReason() == 2 && platformType.equals(3)) {
                        new RemoteWipeHandler().addOrUpdateDeviceWipeOptions(new JSONObject().put("WIPE_BUT_RETAIN_MDM", (Object)Boolean.TRUE).put("RESOURCE_ID", (Object)managedDeviceID));
                    }
                }
                else {
                    request.setWipeType(1);
                }
            }
            Row deprovisionRow = dataObject.getRow("DeprovisionHistory", new Criteria(Column.getColumn("DeprovisionHistory", "RESOURCE_ID"), (Object)managedDeviceID, 0));
            boolean newRow = false;
            if (deprovisionRow == null) {
                deprovisionRow = new Row("DeprovisionHistory");
                newRow = true;
            }
            deprovisionRow.set("RESOURCE_ID", (Object)managedDeviceID);
            deprovisionRow.set("DEPROVISION_TYPE", (Object)request.getWipeType());
            deprovisionRow.set("DEPROVISION_REASON", (Object)request.getWipeReason());
            deprovisionRow.set("DEPROVISION_TIME", (Object)System.currentTimeMillis());
            deprovisionRow.set("USER_ID", (Object)DMUserHandler.getUserIdForLoginId(DMUserHandler.getLoginId()));
            deprovisionRow.set("WIPE_PENDING", (Object)Boolean.TRUE);
            deprovisionRow.set("COMMENT", (Object)reason);
            if (newRow) {
                dataObject.addRow(deprovisionRow);
            }
            else {
                dataObject.updateRow(deprovisionRow);
            }
            String managedStatus = null;
            switch (request.getWipeReason()) {
                case 1: {
                    if (ownedby == 1) {
                        managedDeviceRow.set("REMARKS", (Object)"mdm.deprovision.complete_wipe_init");
                        managedDeviceRow.set("MANAGED_STATUS", (Object)9);
                        managedStatus = I18N.getMsg("mdm.actionlog.deprovision.repaired", new Object[0]);
                        break;
                    }
                    managedDeviceRow.set("REMARKS", (Object)"mdm.deprovision.corporate_wipe_init");
                    managedDeviceRow.set("MANAGED_STATUS", (Object)11);
                    managedDeviceRow.set("UNREGISTERED_TIME", (Object)System.currentTimeMillis());
                    managedStatus = I18N.getMsg("mdm.actionlog.deprovision.retired", new Object[0]);
                    showRetiredTab = true;
                    break;
                }
                case 2: {
                    if (ownedby == 2) {
                        managedDeviceRow.set("REMARKS", (Object)"mdm.deprovision.corporate_wipe_init");
                        managedDeviceRow.set("MANAGED_STATUS", (Object)11);
                        managedDeviceRow.set("UNREGISTERED_TIME", (Object)System.currentTimeMillis());
                        managedStatus = I18N.getMsg("mdm.actionlog.deprovision.retired", new Object[0]);
                        showRetiredTab = true;
                        break;
                    }
                    managedDeviceRow.set("REMARKS", (Object)"mdm.deprovision.complete_wipe_init");
                    managedDeviceRow.set("MANAGED_STATUS", (Object)10);
                    managedStatus = I18N.getMsg("mdm.actionlog.deprovision.stock", new Object[0]);
                    break;
                }
                case 3: {
                    if (ownedby == 1) {
                        managedDeviceRow.set("REMARKS", (Object)"mdm.deprovision.complete_wipe_init");
                    }
                    else {
                        managedDeviceRow.set("REMARKS", (Object)"mdm.deprovision.corporate_wipe_init");
                    }
                    managedDeviceRow.set("MANAGED_STATUS", (Object)11);
                    managedDeviceRow.set("UNREGISTERED_TIME", (Object)System.currentTimeMillis());
                    managedStatus = I18N.getMsg("mdm.actionlog.deprovision.retired", new Object[0]);
                    showRetiredTab = true;
                    break;
                }
                case 4: {
                    if (request.getWipeType() == 1) {
                        managedDeviceRow.set("REMARKS", (Object)"mdm.deprovision.corporate_wipe_init");
                    }
                    else {
                        managedDeviceRow.set("REMARKS", (Object)"mdm.deprovision.complete_wipe_init");
                    }
                    if (ownedby == 2) {
                        managedDeviceRow.set("MANAGED_STATUS", (Object)11);
                        managedDeviceRow.set("UNREGISTERED_TIME", (Object)System.currentTimeMillis());
                        managedStatus = I18N.getMsg("mdm.actionlog.deprovision.retired", new Object[0]);
                        showRetiredTab = true;
                        break;
                    }
                    managedDeviceRow.set("MANAGED_STATUS", (Object)10);
                    managedStatus = I18N.getMsg("mdm.actionlog.deprovision.stock", new Object[0]);
                    break;
                }
            }
            if (apnsErrorCode != null && apnsErrorCode != -1 && platformType == 1) {
                appleDeviceList.add(managedDeviceID);
                managedDeviceRow.set("REMARKS", (Object)"mdm.deprovision.apns_expired_deprovision_ios_device");
                this.enrollmentLogger.log(Level.INFO, "APNS is not valid (expired or revoked). So profile will not be romoved for resource {0}", managedDeviceID);
            }
            managedDeviceDataObject.updateRow(managedDeviceRow);
            if (request.getWipeType() == 2) {
                completeWipeList.add(device);
                deprovisionDeviceList.add(device);
                commandDisplayName = I18N.getMsg("dc.mdm.inv.remote_wipe", new Object[0]);
            }
            else if (request.getWipeType() == 1) {
                corporateWipeList.add(device);
                deprovisionDeviceList.add(device);
                commandDisplayName = I18N.getMsg("dc.mdm.inv.corporate_wipe", new Object[0]);
            }
            final String loginUser = ApiFactoryProvider.getAuthUtilAccessAPI().getLoginName();
            final Object remarkArgs = loginUser + "@@@" + commandDisplayName + "@@@" + device.name + "@@@" + serialNumber + "@@@" + udid + "@@@" + managedStatus + "@@@" + reason;
            MDMEventLogHandler.getInstance().MDMEventLogEntry(2001, managedDeviceID, loginUser, "mdm.actionlog.deprovision.success_action_log", remarkArgs, CustomerInfoUtil.getInstance().getCustomerId());
            final org.json.simple.JSONObject logJSON = new org.json.simple.JSONObject();
            logJSON.put((Object)"RESOURCE_ID", (Object)managedDeviceID);
            logJSON.put((Object)"REMARKS", (Object)"deprovision-success");
            MDMOneLineLogger.log(Level.INFO, "DEVICE_DEPROVISIONED", logJSON);
        }
        MDMUtil.getPersistenceLite().update(dataObject);
        MDMUtil.getPersistenceLite().update(managedDeviceDataObject);
        for (final DeviceDetails device2 : completeWipeList) {
            try {
                DeviceInvCommandHandler.getInstance().sendCommandToDevice(device2, "EraseDevice", request.getUserId());
                successList.add(device2.resourceId);
                successArray.put(device2.resourceId);
            }
            catch (final Exception exp) {
                this.enrollmentLogger.log(Level.SEVERE, "Exception while sending deprovision command", exp);
                final JSONObject failureJson = new JSONObject();
                failureJson.put("RESOURCE_ID", device2.resourceId);
                if (exp instanceof SyMException) {
                    failureJson.put("ErrorMsg", (Object)I18N.getMsg("mdm.api.error.deprovision_failed_personal_device", new Object[0]));
                }
                else {
                    failureJson.put("ErrorMsg", (Object)I18N.getMsg("mdm.api.error.internal_server_error", new Object[0]));
                }
                final String dName = (String)managedDeviceDataObject.getValue("ManagedDeviceExtn", "NAME", new Criteria(Column.getColumn("ManagedDeviceExtn", "MANAGED_DEVICE_ID"), (Object)device2.resourceId, 0));
                failureJson.put("device_name", (Object)dName);
                failedArray.put((Object)failureJson);
                failureList.add(device2.resourceId);
                appleDeviceList.remove(device2.resourceId);
            }
        }
        for (final DeviceDetails device2 : corporateWipeList) {
            try {
                DeviceInvCommandHandler.getInstance().sendCommandToDevice(device2, "CorporateWipe", request.getUserId());
                successList.add(device2.resourceId);
                successArray.put(device2.resourceId);
            }
            catch (final Exception exp) {
                this.enrollmentLogger.log(Level.SEVERE, "Exception while sending deprovision command", exp);
                final JSONObject failureJson = new JSONObject();
                failureJson.put("RESOURCE_ID", device2.resourceId);
                failureJson.put("ErrorMsg", (Object)I18N.getMsg("mdm.api.error.internal_server_error", new Object[0]));
                final String dName = (String)managedDeviceDataObject.getValue("ManagedDeviceExtn", "NAME", new Criteria(Column.getColumn("ManagedDeviceExtn", "MANAGED_DEVICE_ID"), (Object)device2.resourceId, 0));
                failureJson.put("device_name", (Object)dName);
                failedArray.put((Object)failureJson);
                failureList.add(device2.resourceId);
                appleDeviceList.remove(device2.resourceId);
            }
        }
        for (int i = 0; i < successArray.length(); ++i) {
            final Long resourceId = JSONUtil.optLongForUVH(successArray, i, -1L);
            for (final DeviceDetails deviceDetails : deprovisionDeviceList) {
                if (deviceDetails.resourceId == resourceId) {
                    final DeviceEvent deviceEvent = new DeviceEvent();
                    deviceEvent.resourceID = deviceDetails.resourceId;
                    deviceEvent.platformType = deviceDetails.platform;
                    deviceEvent.udid = deviceDetails.udid;
                    deviceEvent.customerID = request.getCustomerId();
                    this.invokeDeviceListeners(deviceEvent, 9);
                }
            }
        }
        this.updateManagedStatusToEnrolled(failureList);
        this.removeMemberfromGroupsOnDeprovision(successList, request.getUserId());
        NotificationHandler.getInstance().SendNotification(successList);
        this.removeDeviceForEnrollmentEntires(successList, request.getCustomerId());
        AzureWinCEAListener.markDeviceStatusUnmanaged(successList);
        final int success = showRetiredTab ? 2 : 1;
        final JSONObject depovisionReturnJson = new JSONObject();
        depovisionReturnJson.put("success", success);
        depovisionReturnJson.put("SuccessList", (Object)successArray);
        depovisionReturnJson.put("FailureList", (Object)failedArray);
        if (request.isForceDeprovision() && apnsErrorCode != null && apnsErrorCode != -1) {
            depovisionReturnJson.put("IOSDeviceCount", appleDeviceList.size());
        }
        this.enrollmentLogger.log(Level.INFO, "Deprovision device process ends with: {0}", depovisionReturnJson.toString());
        return depovisionReturnJson;
    }
    
    public void addMDMEventLogForBulkDeprovision(final String loginUser, final int deviceCount) throws Exception {
        if (deviceCount > 0) {
            final Object remarksArgs = loginUser + "@@@" + deviceCount;
            MDMEventLogHandler.getInstance().MDMEventLogEntry(2001, null, loginUser, "mdm.deprovision.bulk_deprovision_remarks", remarksArgs, CustomerInfoUtil.getInstance().getCustomerId());
        }
    }
    
    public JSONObject getDeprovisiondetails(final Long resourceID) throws Exception {
        JSONObject json = new JSONObject();
        int managedStatus = -1;
        try {
            final Criteria resourceIdCri = new Criteria(Column.getColumn("ManagedDevice", "RESOURCE_ID"), (Object)resourceID, 0);
            final DataObject dObj = MDMUtil.getPersistence().get("ManagedDevice", resourceIdCri);
            if (!dObj.isEmpty()) {
                managedStatus = (int)dObj.getValue("ManagedDevice", "MANAGED_STATUS", resourceIdCri);
            }
            if (managedStatus == 2) {
                return null;
            }
            json = this.getDeprovisionRemarks(managedStatus, resourceID);
        }
        catch (final Exception exp) {
            this.logger.log(Level.SEVERE, "Exception while getting managedstatus", exp);
            return null;
        }
        return json;
    }
    
    private JSONObject getDeprovisionRemarks(final int managedStatus, final Long resourceID) {
        int wipeReason = -1;
        String remarks = null;
        boolean wipepending = false;
        final JSONObject json = new JSONObject();
        try {
            final SelectQuery depovisionQuery = this.getDeprovisionQuery();
            depovisionQuery.setCriteria(new Criteria(Column.getColumn("DeprovisionHistory", "RESOURCE_ID"), (Object)resourceID, 0));
            final DataObject DO = MDMUtil.getPersistenceLite().get(depovisionQuery);
            if (!DO.isEmpty()) {
                final Row row = DO.getFirstRow("DeprovisionHistory");
                wipeReason = (int)row.get("DEPROVISION_REASON");
                wipepending = (boolean)row.get("WIPE_PENDING");
                if (managedStatus == 9) {
                    remarks = "mdm.deprovision.repair_remark";
                }
                else if (managedStatus == 11) {
                    if (wipeReason == 5) {
                        remarks = "mdm.enrollment.jailbroken.remarks";
                    }
                    else {
                        remarks = "mdm.deprovision.retire_remark";
                    }
                }
                else if (managedStatus == 10) {
                    switch (wipeReason) {
                        case 2: {
                            remarks = "mdm.deprovision.emp_left_remark";
                            break;
                        }
                        case 4: {
                            remarks = "mdm.deprovision.old_remark";
                            break;
                        }
                    }
                }
                json.put("REMARKS", (Object)remarks);
                json.put("MANAGED_STATUS", managedStatus);
                json.put("WIPE_PENDING", wipepending);
            }
        }
        catch (final Exception exp) {
            this.logger.log(Level.SEVERE, "Exception while getting deprovision details", exp);
            return null;
        }
        return json;
    }
    
    public void updatedeprovisionhistory(final JSONObject json) {
        try {
            final Long resourceID = json.getLong("RESOURCE_ID");
            final Boolean wipepending = json.getBoolean("WIPE_PENDING");
            Criteria cri = new Criteria(Column.getColumn("DeprovisionHistory", "RESOURCE_ID"), (Object)resourceID, 0);
            cri = cri.and(new Criteria(Column.getColumn("DeprovisionHistory", "WIPE_PENDING"), (Object)true, 0));
            final SelectQuery depovisionQuery = this.getDeprovisionQuery();
            depovisionQuery.setCriteria(cri);
            final DataObject DO = MDMUtil.getPersistenceLite().get(depovisionQuery);
            if (!DO.isEmpty()) {
                final Row row = DO.getFirstRow("DeprovisionHistory");
                row.set("WIPE_PENDING", (Object)wipepending);
                DO.updateRow(row);
                MDMUtil.getPersistenceLite().update(DO);
            }
        }
        catch (final Exception exp) {
            this.logger.log(Level.SEVERE, "Exception while updating deprovision details", exp);
        }
    }
    
    public JSONArray getDEPDeviceSerialNumber(final Long[] resId) {
        final JSONArray deviceListArray = new JSONArray();
        final SelectQuery sQuery = DEPAdminEnrollmentHandler.getManagedResourceToDEPTokenQuery();
        sQuery.addSelectColumn(Column.getColumn("DEPTokenDetails", "DEP_TOKEN_ID"));
        sQuery.addSelectColumn(Column.getColumn("MdDeviceInfo", "SERIAL_NUMBER"));
        sQuery.addSelectColumn(Column.getColumn("MdDeviceInfo", "RESOURCE_ID"));
        sQuery.setCriteria(new Criteria(new Column("MdDeviceInfo", "RESOURCE_ID"), (Object)resId, 8).and(new Criteria(new Column("EnrollmentTemplate", "TEMPLATE_TYPE"), (Object)10, 0)));
        DMDataSetWrapper ds = null;
        try {
            ds = DMDataSetWrapper.executeQuery((Object)sQuery);
            while (ds.next()) {
                final JSONObject devicePropJSON = new JSONObject();
                devicePropJSON.put("SERIAL_NUMBER", ds.getValue("SERIAL_NUMBER"));
                devicePropJSON.put("RESOURCE_ID", ds.getValue("RESOURCE_ID"));
                devicePropJSON.put("DEP_TOKEN_ID", ds.getValue("DEP_TOKEN_ID"));
                deviceListArray.put((Object)devicePropJSON);
            }
        }
        catch (final Exception exp) {
            this.logger.log(Level.SEVERE, "Exception while getting serial number", exp);
            return null;
        }
        return deviceListArray;
    }
    
    public int getOwnedByForDevice(final Criteria cCriteria) {
        int ownedby = 0;
        try {
            final SelectQuery sQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("DeviceEnrollmentRequest"));
            sQuery.addJoin(new Join("DeviceEnrollmentRequest", "EnrollmentRequestToDevice", new String[] { "ENROLLMENT_REQUEST_ID" }, new String[] { "ENROLLMENT_REQUEST_ID" }, 1));
            sQuery.addJoin(new Join("EnrollmentRequestToDevice", "ManagedDevice", new String[] { "MANAGED_DEVICE_ID" }, new String[] { "RESOURCE_ID" }, 1));
            sQuery.setCriteria(cCriteria);
            sQuery.addSelectColumn(Column.getColumn("DeviceEnrollmentRequest", "*"));
            final DataObject Do = MDMUtil.getPersistence().get(sQuery);
            if (!Do.isEmpty()) {
                final Row requestRow = Do.getFirstRow("DeviceEnrollmentRequest");
                ownedby = (int)requestRow.get("OWNED_BY");
            }
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Exception in getOwnedByForDevice", e);
        }
        return ownedby;
    }
    
    public void removeResourceAssociationsOnUnmanage(final Long resourceId) {
        try {
            this.logger.log(Level.INFO, "Removing associated table on device unmanage : {0}", resourceId);
            final List groupList = GroupCollectionStatusSummary.getInstance().getGroupIdsCollectionListForResource(resourceId);
            final List mUserIdsList = ManagedUserCollectionStatusSummary.getInstance().getManagedUserIdsCollectionListForResource(resourceId);
            if (MDMFeatureParamsHandler.getInstance().isFeatureEnabled("DoNotReassignOldDeviceUser") || MDMEnrollmentRequestHandler.getInstance().getEnrollmentType(MDMEnrollmentRequestHandler.getInstance().getEnrollmentRequestIdFromManagedDeviceID(resourceId)) != 3) {
                final DeleteQuery recentProfileToResource = (DeleteQuery)new DeleteQueryImpl("RecentProfileForResource");
                recentProfileToResource.setCriteria(new Criteria(Column.getColumn("RecentProfileForResource", "RESOURCE_ID"), (Object)resourceId, 0));
                MDMUtil.getPersistenceLite().delete(recentProfileToResource);
                final DeleteQuery resourceToProfileHistory = (DeleteQuery)new DeleteQueryImpl("ResourceToProfileHistory");
                resourceToProfileHistory.setCriteria(new Criteria(Column.getColumn("ResourceToProfileHistory", "RESOURCE_ID"), (Object)resourceId, 0));
                MDMUtil.getPersistenceLite().delete(resourceToProfileHistory);
                final DeleteQuery groupToResource = (DeleteQuery)new DeleteQueryImpl("CustomGroupMemberRel");
                groupToResource.setCriteria(new Criteria(Column.getColumn("CustomGroupMemberRel", "MEMBER_RESOURCE_ID"), (Object)resourceId, 0));
                MDMUtil.getPersistenceLite().delete(groupToResource);
                final DeleteQuery docToResource = (DeleteQuery)new DeleteQueryImpl("DocumentManagedDeviceRel");
                docToResource.setCriteria(new Criteria(Column.getColumn("DocumentManagedDeviceRel", "MANAGEDDEVICE_ID"), (Object)resourceId, 0));
                MDMUtil.getPersistenceLite().delete(docToResource);
            }
            if (MDMUtil.getInstance().getPlatformType(resourceId) == 1) {
                new AppleAppLicenseMgmtHandler().revokeAllAppLicensesForResource(resourceId, CustomerInfoUtil.getInstance().getCustomerIDForResID(resourceId));
            }
            final DeleteQuery appToResource = (DeleteQuery)new DeleteQueryImpl("MdAppCatalogToResource");
            appToResource.setCriteria(new Criteria(Column.getColumn("MdAppCatalogToResource", "RESOURCE_ID"), (Object)resourceId, 0));
            MDMUtil.getPersistenceLite().delete(appToResource);
            final DeleteQuery collnToResource = (DeleteQuery)new DeleteQueryImpl("CollnToResources");
            collnToResource.setCriteria(new Criteria(Column.getColumn("CollnToResources", "RESOURCE_ID"), (Object)resourceId, 0));
            MDMUtil.getPersistenceLite().delete(collnToResource);
            final DeleteQuery profileToResourceSummary = (DeleteQuery)new DeleteQueryImpl("ResourceToProfileSummary");
            profileToResourceSummary.setCriteria(new Criteria(Column.getColumn("ResourceToProfileSummary", "RESOURCE_ID"), (Object)resourceId, 0));
            MDMUtil.getPersistenceLite().delete(profileToResourceSummary);
            final Criteria resourceCriteria = new Criteria(new Column("ComplianceToResource", "RESOURCE_ID"), (Object)resourceId, 0);
            final DeleteQuery complianceToResourceDeleteQuery = (DeleteQuery)new DeleteQueryImpl("ComplianceToResource");
            complianceToResourceDeleteQuery.setCriteria(resourceCriteria);
            MDMUtil.getPersistence().delete(complianceToResourceDeleteQuery);
            final DeleteQuery blackListAppCollectionStatus = (DeleteQuery)new DeleteQueryImpl("BlacklistAppCollectionStatus");
            blackListAppCollectionStatus.setCriteria(new Criteria(Column.getColumn("BlacklistAppCollectionStatus", "RESOURCE_ID"), (Object)resourceId, 0));
            MDMUtil.getPersistenceLite().delete(blackListAppCollectionStatus);
            final DeleteQuery announcementResourceQuery = (DeleteQuery)new DeleteQueryImpl("AnnouncementToResources");
            announcementResourceQuery.setCriteria(new Criteria(new Column("AnnouncementToResources", "RESOURCE_ID"), (Object)resourceId, 0));
            MDMUtil.getPersistenceLite().delete(announcementResourceQuery);
            ProfileAssociateHandler.getInstance().updateDeviceProfileSummary();
            DeviceCommandRepository.getInstance().removeAllMdCommandToDeviceForResource(resourceId);
            GroupCollectionStatusSummary.getInstance().updateGroupCollectionStatusSummary(groupList);
            ManagedUserCollectionStatusSummary.getInstance().updateManagedUserCollectionStatusSummary(mUserIdsList);
        }
        catch (final Exception exp) {
            this.logger.log(Level.SEVERE, "Exception while removing profile association on device unmanage", exp);
        }
    }
    
    public void unmanageAllDevices(final Long customerId, final int platform) throws Exception {
        if (platform == 4) {
            final UpdateQuery uQuery = (UpdateQuery)new UpdateQueryImpl("ManagedDevice");
            final Join resourceJoin = new Join("ManagedDevice", "Resource", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2);
            uQuery.addJoin(resourceJoin);
            final Criteria platformType = new Criteria(Column.getColumn("ManagedDevice", "PLATFORM_TYPE"), (Object)platform, 0);
            final Criteria customerCriteria = new Criteria(Column.getColumn("ManagedDevice", "PLATFORM_TYPE"), (Object)platform, 0);
            uQuery.setCriteria(platformType.and(customerCriteria));
            uQuery.setUpdateColumn("MANAGED_STATUS", (Object)4);
            uQuery.setUpdateColumn("REMARKS", (Object)I18N.getMsg("mdm.profile.removal.g_suite_integration", new Object[0]));
            MDMUtil.getPersistence().update(uQuery);
            return;
        }
        throw new UnsupportedOperationException("Not supported yet for platform type " + platform);
    }
    
    public boolean isDepDeviceAssignedInPortal(final Long[] resourceID, final Long customerid) {
        boolean dep_unassigned = true;
        final MultiMap depTokenToDeviceSerialNumbersMap = (MultiMap)new MultiHashMap();
        final MultiMap depTokenToDeviceResourceIDMap = (MultiMap)new MultiHashMap();
        final JSONArray depDeviceDetailsJArray = getInstance().getDEPDeviceSerialNumber(resourceID);
        if (depDeviceDetailsJArray != null) {
            for (int i = 0; i < depDeviceDetailsJArray.length(); ++i) {
                final JSONObject deviceDetailsJSON = depDeviceDetailsJArray.optJSONObject(i);
                depTokenToDeviceSerialNumbersMap.put((Object)deviceDetailsJSON.optLong("DEP_TOKEN_ID"), (Object)deviceDetailsJSON.optString("SERIAL_NUMBER"));
                depTokenToDeviceResourceIDMap.put((Object)deviceDetailsJSON.optLong("DEP_TOKEN_ID"), (Object)deviceDetailsJSON.optString("RESOURCE_ID"));
            }
        }
        JSONObject specificDeviceObject = new JSONObject();
        JSONObject deviceObject = new JSONObject();
        try {
            final Set<Long> depTokenSet = depTokenToDeviceSerialNumbersMap.keySet();
            for (final Long depTokenID : depTokenSet) {
                final JSONObject deviceJSON = new JSONObject();
                final ArrayList deviceSerialNumbersInToken = (ArrayList)depTokenToDeviceSerialNumbersMap.get((Object)depTokenID);
                deviceJSON.put("devices", (Collection)deviceSerialNumbersInToken);
                deviceObject = AppleDEPWebServicetHandler.getInstance(depTokenID, customerid).getDeviceDetails(deviceJSON);
                for (int j = 0; j < deviceSerialNumbersInToken.size(); ++j) {
                    specificDeviceObject = deviceObject.optJSONObject("devices").optJSONObject(String.valueOf(deviceSerialNumbersInToken.get(j)));
                    if (specificDeviceObject.optString("response_status").equalsIgnoreCase("SUCCESS")) {
                        dep_unassigned = false;
                        break;
                    }
                }
            }
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, "Exception in isDepDeviceAssignedInPortal(): ", ex);
        }
        return dep_unassigned;
    }
    
    public boolean isSerialNumberPresentInDEPPortal(final String serialno, final Long customerid) {
        boolean un_assigned = true;
        JSONObject specificDeviceObject = new JSONObject();
        JSONObject deviceObject = new JSONObject();
        final ArrayList<Long> depTokenSet = new ArrayList<Long>();
        try {
            final SelectQuery sq = (SelectQuery)new SelectQueryImpl(new Table("DEPTokenDetails"));
            sq.addSelectColumn(Column.getColumn("DEPTokenDetails", "DEP_TOKEN_ID"));
            final DataObject DO = MDMUtil.getPersistenceLite().get(sq);
            if (!DO.isEmpty()) {
                final Iterator itr = DO.getRows("DEPTokenDetails");
                while (itr.hasNext()) {
                    final Row row = itr.next();
                    depTokenSet.add((Long)row.get("DEP_TOKEN_ID"));
                }
            }
            for (final Long depTokenID : depTokenSet) {
                final JSONObject deviceJSON = new JSONObject();
                deviceJSON.put("devices", (Object)new String[] { serialno });
                deviceObject = AppleDEPWebServicetHandler.getInstance(depTokenID, customerid).getDeviceDetails(deviceJSON);
                specificDeviceObject = deviceObject.optJSONObject("devices").optJSONObject(serialno);
                if (specificDeviceObject.optString("response_status").equalsIgnoreCase("SUCCESS")) {
                    un_assigned = false;
                    break;
                }
            }
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, "Exception in syncDeviceDetailsOnRemoval(): ", ex);
        }
        return un_assigned;
    }
    
    public SelectQuery getDeprovisionQuery() {
        final SelectQuery deprovisionQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("DeprovisionHistory"));
        final Column maxTimeColumn = new Column("DeprovisionHistory", "DEPROVISION_TIME");
        final Column resourceColumn = new Column("DeprovisionHistory", "RESOURCE_ID");
        final Column idColumn = new Column("DeprovisionHistory", "DEPROVISION_ID");
        final Column wipePendingColumn = new Column("DeprovisionHistory", "WIPE_PENDING");
        final Column wipeReasonColumn = new Column("DeprovisionHistory", "DEPROVISION_REASON");
        final Column wipeTypeColumn = new Column("DeprovisionHistory", "DEPROVISION_TYPE");
        deprovisionQuery.addSelectColumn(maxTimeColumn);
        deprovisionQuery.addSelectColumn(idColumn);
        deprovisionQuery.addSelectColumn(resourceColumn);
        deprovisionQuery.addSelectColumn(wipePendingColumn);
        deprovisionQuery.addSelectColumn(wipeReasonColumn);
        deprovisionQuery.addSelectColumn(wipeTypeColumn);
        final SortColumn sortColumn = new SortColumn(Column.getColumn("DeprovisionHistory", "DEPROVISION_TIME"), false);
        deprovisionQuery.addSortColumn(sortColumn);
        return deprovisionQuery;
    }
    
    public int getWipePendingCount() {
        int removedDeviceCount = 0;
        DMDataSetWrapper dataSet = null;
        final Criteria removedCriteria = new Criteria(new Column("DeprovisionHistory", "WIPE_PENDING"), (Object)true, 0);
        final SelectQuery deprovisionHistory = this.getDeprovisionHistorySelectQuery(removedCriteria);
        final DerivedTable dtab = new DerivedTable("DeprovisionHistory", (Query)deprovisionHistory);
        final SelectQuery mainQuery = (SelectQuery)new SelectQueryImpl((Table)dtab);
        final Column countColumn = new Column("DeprovisionHistory", "RESOURCE_ID").count();
        countColumn.setColumnAlias("RESOURCE_ID");
        mainQuery.addSelectColumn(countColumn);
        try {
            dataSet = DMDataSetWrapper.executeQuery((Object)mainQuery);
            if (dataSet.next()) {
                removedDeviceCount = (int)dataSet.getValue("RESOURCE_ID");
            }
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Error in getting removed count ", e);
        }
        return removedDeviceCount;
    }
    
    public ArrayList<Long> getWipePendingResList() {
        final Set resList = new HashSet();
        final Criteria removedCriteria = new Criteria(new Column("DeprovisionHistory", "WIPE_PENDING"), (Object)true, 0);
        final SelectQuery deprovisionHistory = (SelectQuery)new SelectQueryImpl(Table.getTable("DeprovisionHistory"));
        final Column uvhColumn = new Column("DeprovisionHistory", "DEPROVISION_ID");
        final Column resourceColumn = new Column("DeprovisionHistory", "RESOURCE_ID");
        final Column wipePending = new Column("DeprovisionHistory", "WIPE_PENDING");
        deprovisionHistory.addSelectColumn(resourceColumn);
        deprovisionHistory.addSelectColumn(uvhColumn);
        deprovisionHistory.addSelectColumn(wipePending);
        deprovisionHistory.setCriteria(removedCriteria);
        try {
            final DataObject dataObject = MDMUtil.getPersistenceLite().get(deprovisionHistory);
            if (dataObject != null && !dataObject.isEmpty()) {
                final Iterator iterator = dataObject.getRows("DeprovisionHistory");
                while (iterator.hasNext()) {
                    final Row row = iterator.next();
                    final Long resId = (Long)row.get("RESOURCE_ID");
                    resList.add(resId);
                }
            }
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Error in getting list ", e);
        }
        return new ArrayList<Long>(resList);
    }
    
    public SelectQuery getDeprovisionHistorySelectQuery(final Criteria criteria) {
        final SelectQuery deprovisionHistory = (SelectQuery)new SelectQueryImpl(Table.getTable("DeprovisionHistory"));
        final Column maxTimeColumn = new Column("DeprovisionHistory", "DEPROVISION_TIME").maximum();
        final Column resourceColumn = new Column("DeprovisionHistory", "RESOURCE_ID");
        deprovisionHistory.addSelectColumn(maxTimeColumn);
        deprovisionHistory.addSelectColumn(resourceColumn);
        final ArrayList<Column> groupByColumnsList = new ArrayList<Column>();
        groupByColumnsList.add(resourceColumn);
        final GroupByClause groupByColumn = new GroupByClause((List)groupByColumnsList);
        deprovisionHistory.setGroupByClause(groupByColumn);
        if (criteria != null) {
            deprovisionHistory.setCriteria(criteria);
        }
        return deprovisionHistory;
    }
    
    public JSONObject getDeviceCountForEnrollmentTabs(final Long customerid) throws Exception {
        final JSONObject json = new JSONObject();
        try {
            final SelectQuery managedDeviceSelectQuery = (SelectQuery)new SelectQueryImpl(new Table("DeviceEnrollmentRequest"));
            managedDeviceSelectQuery.addJoin(new Join("DeviceEnrollmentRequest", "ManagedUser", new String[] { "MANAGED_USER_ID" }, new String[] { "MANAGED_USER_ID" }, 1));
            managedDeviceSelectQuery.addJoin(new Join("ManagedUser", "Resource", new String[] { "MANAGED_USER_ID" }, new String[] { "RESOURCE_ID" }, 2));
            managedDeviceSelectQuery.addJoin(new Join("DeviceEnrollmentRequest", "EnrollmentRequestToDevice", new String[] { "ENROLLMENT_REQUEST_ID" }, new String[] { "ENROLLMENT_REQUEST_ID" }, 1));
            managedDeviceSelectQuery.addJoin(new Join("EnrollmentRequestToDevice", "ManagedDevice", new String[] { "MANAGED_DEVICE_ID" }, new String[] { "RESOURCE_ID" }, 1));
            managedDeviceSelectQuery.addJoin(new Join("DeviceEnrollmentRequest", "InvitationEnrollmentRequest", new String[] { "ENROLLMENT_REQUEST_ID" }, new String[] { "ENROLLMENT_REQUEST_ID" }, 1));
            managedDeviceSelectQuery.addJoin(new Join("DeviceEnrollmentRequest", "DeviceEnrollmentToRequest", new String[] { "ENROLLMENT_REQUEST_ID" }, new String[] { "ENROLLMENT_REQUEST_ID" }, 1));
            managedDeviceSelectQuery.addJoin(new Join("DeviceEnrollmentToRequest", "DeviceForEnrollment", new String[] { "ENROLLMENT_DEVICE_ID" }, new String[] { "ENROLLMENT_DEVICE_ID" }, 1));
            Criteria rbdacriteria = null;
            final Long loginID = ApiFactoryProvider.getAuthUtilAccessAPI().getLoginID();
            final Long userID = ApiFactoryProvider.getAuthUtilAccessAPI().getUserID();
            if (loginID != null) {
                final Boolean isMDMAdmin = DMUserHandler.isUserInRole(loginID, "All_Managed_Mobile_Devices");
                if (!isMDMAdmin) {
                    managedDeviceSelectQuery.addJoin(new Join("ManagedDevice", "CustomGroupMemberRel", new String[] { "RESOURCE_ID" }, new String[] { "MEMBER_RESOURCE_ID" }, 1));
                    managedDeviceSelectQuery.addJoin(new Join("CustomGroupMemberRel", "UserCustomGroupMapping", new String[] { "GROUP_RESOURCE_ID" }, new String[] { "GROUP_RESOURCE_ID" }, "CustomGroupMemberRel", "DeviceGroups", 1));
                    final Criteria cgCriteria = new Criteria(Column.getColumn("DeviceGroups", "GROUP_RESOURCE_ID", "DeviceGroups.GROUP_RESOURCE_ID"), (Object)null, 1);
                    final Criteria cgReqCriteria = new Criteria(Column.getColumn("DeviceEnrollmentRequest", "USER_ID"), (Object)userID, 0);
                    final Criteria mdCriteria = new Criteria(Column.getColumn("ManagedDevice", "RESOURCE_ID"), (Object)null, 1);
                    final Criteria nomdCriteria = new Criteria(Column.getColumn("ManagedDevice", "RESOURCE_ID"), (Object)null, 0);
                    final Criteria userCustomGroupCriteria = new Criteria(Column.getColumn("DeviceGroups", "LOGIN_ID", "DeviceGroups.LOGIN_ID"), (Object)loginID, 0);
                    Criteria criteria = managedDeviceSelectQuery.getCriteria();
                    if (criteria == null) {
                        criteria = userCustomGroupCriteria.and(mdCriteria.and(cgCriteria)).or(nomdCriteria.and(cgReqCriteria));
                    }
                    else {
                        criteria = criteria.and(userCustomGroupCriteria.and(mdCriteria.and(cgCriteria)).or(nomdCriteria.and(cgReqCriteria)));
                    }
                    rbdacriteria = criteria;
                }
            }
            final Criteria custIdCriteria = new Criteria(new Column("Resource", "CUSTOMER_ID"), (Object)customerid, 0);
            final Criteria userNotInTrashCriteria = new Criteria(Column.getColumn("ManagedUser", "STATUS"), (Object)11, 1).or(new Criteria(Column.getColumn("ManagedUser", "STATUS"), (Object)null, 0));
            managedDeviceSelectQuery.setCriteria(custIdCriteria);
            Criteria enrolledCri = new Criteria(new Column("ManagedDevice", "MANAGED_STATUS"), (Object)new int[] { 2, 4 }, 8);
            Criteria retiredCri = new Criteria(new Column("ManagedDevice", "MANAGED_STATUS"), (Object)11, 0);
            final Criteria instockCri = new Criteria(new Column("ManagedDevice", "MANAGED_STATUS"), (Object)new int[] { 10, 9 }, 8);
            final Criteria waitingForUserCri = new Criteria(new Column("DeviceForEnrollment", "ENROLLMENT_DEVICE_ID"), (Object)null, 1).and(new Criteria(new Column("ManagedDevice", "MANAGED_STATUS"), (Object)new int[] { 5, 6 }, 8).or(new Criteria(new Column("ManagedDevice", "MANAGED_STATUS"), (Object)null, 0).and(new Criteria(Column.getColumn("DeviceEnrollmentRequest", "REQUEST_STATUS"), (Object)0, 1))));
            Criteria pendingRequestCri = new Criteria(new Column("DeviceEnrollmentRequest", "REQUEST_STATUS"), (Object)0, 0).or(new Criteria(new Column("DeviceEnrollmentRequest", "REQUEST_STATUS"), (Object)1, 0).and(new Criteria(new Column("DeviceEnrollmentToRequest", "ENROLLMENT_REQUEST_ID"), (Object)null, 0))).and(new Criteria(new Column("ManagedDevice", "RESOURCE_ID"), (Object)null, 0).or(new Criteria(new Column("ManagedDevice", "MANAGED_STATUS"), (Object)1, 0)));
            pendingRequestCri = pendingRequestCri.and(new Criteria(new Column("DeviceEnrollmentRequest", "AUTH_MODE"), (Object)4, 1));
            if (rbdacriteria != null) {
                enrolledCri = enrolledCri.and(rbdacriteria);
                retiredCri = retiredCri.and(rbdacriteria);
                pendingRequestCri = pendingRequestCri.and(rbdacriteria);
            }
            final CaseExpression enrolledExp = new CaseExpression("enrolledCount");
            enrolledExp.addWhen(enrolledCri.and(userNotInTrashCriteria), (Object)Column.getColumn("DeviceEnrollmentRequest", "ENROLLMENT_REQUEST_ID"));
            final Column enrolledCountColumn = MDMUtil.getInstance().getDistinctCountCaseExpressionColumn(enrolledExp, 4, "enrolledCount");
            final CaseExpression retiredExp = new CaseExpression("retiredCount");
            retiredExp.addWhen(retiredCri, (Object)Column.getColumn("DeviceEnrollmentRequest", "ENROLLMENT_REQUEST_ID"));
            final Column retiredCountColumn = MDMUtil.getInstance().getDistinctCountCaseExpressionColumn(retiredExp, 4, "retiredCount");
            final CaseExpression instockExp = new CaseExpression("instockCount");
            instockExp.addWhen(instockCri, (Object)Column.getColumn("DeviceEnrollmentRequest", "ENROLLMENT_REQUEST_ID"));
            final Column instockCountColumn = MDMUtil.getInstance().getDistinctCountCaseExpressionColumn(instockExp, 4, "instockCount");
            final CaseExpression pendingRequestExp = new CaseExpression("pendingRequestCount");
            pendingRequestExp.addWhen(pendingRequestCri, (Object)Column.getColumn("DeviceEnrollmentRequest", "ENROLLMENT_REQUEST_ID"));
            final Column pendingRequestCountColumn = MDMUtil.getInstance().getDistinctCountCaseExpressionColumn(pendingRequestExp, 4, "pendingRequestCount");
            final CaseExpression waitingForUserAssignExp = new CaseExpression("waitingForUserAssignCount");
            waitingForUserAssignExp.addWhen(waitingForUserCri.and(userNotInTrashCriteria), (Object)Column.getColumn("DeviceEnrollmentRequest", "ENROLLMENT_REQUEST_ID"));
            final Column waitingForUserAssignColumn = MDMUtil.getInstance().getDistinctCountCaseExpressionColumn(waitingForUserAssignExp, 4, "waitingForUserAssignCount");
            managedDeviceSelectQuery.addSelectColumn(enrolledCountColumn);
            managedDeviceSelectQuery.addSelectColumn(retiredCountColumn);
            managedDeviceSelectQuery.addSelectColumn(instockCountColumn);
            managedDeviceSelectQuery.addSelectColumn(pendingRequestCountColumn);
            managedDeviceSelectQuery.addSelectColumn(waitingForUserAssignColumn);
            final org.json.simple.JSONArray res = MDMUtil.executeSelectQuery(managedDeviceSelectQuery);
            org.json.simple.JSONObject resultJson = new org.json.simple.JSONObject();
            if (res.size() > 0) {
                resultJson = (org.json.simple.JSONObject)res.get(0);
            }
            final SelectQuery sq = (SelectQuery)new SelectQueryImpl(Table.getTable("DeviceForEnrollment"));
            sq.addJoin(new Join("DeviceForEnrollment", "DeviceEnrollmentToRequest", new String[] { "ENROLLMENT_DEVICE_ID" }, new String[] { "ENROLLMENT_DEVICE_ID" }, 1));
            sq.setCriteria(new Criteria(Column.getColumn("DeviceEnrollmentToRequest", "ENROLLMENT_DEVICE_ID"), (Object)null, 0).and(new Criteria(Column.getColumn("DeviceForEnrollment", "CUSTOMER_ID"), (Object)customerid, 0)));
            final SelectQuery enrollFailedDeviceQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("DeviceEnrollmentRequest"));
            enrollFailedDeviceQuery.addJoin(new Join("DeviceEnrollmentRequest", "DeviceEnrollmentToRequest", new String[] { "ENROLLMENT_REQUEST_ID" }, new String[] { "ENROLLMENT_REQUEST_ID" }, 2));
            enrollFailedDeviceQuery.addJoin(new Join("DeviceEnrollmentRequest", "EnrollmentRequestToDevice", new String[] { "ENROLLMENT_REQUEST_ID" }, new String[] { "ENROLLMENT_REQUEST_ID" }, 1));
            enrollFailedDeviceQuery.addJoin(new Join("EnrollmentRequestToDevice", "ManagedDevice", new String[] { "MANAGED_DEVICE_ID" }, new String[] { "RESOURCE_ID" }, 1));
            enrollFailedDeviceQuery.setCriteria(new Criteria(Column.getColumn("DeviceEnrollmentRequest", "ENROLLMENT_TYPE"), (Object)new Integer[] { 3, 4 }, 8).and(new Criteria(Column.getColumn("DeviceEnrollmentRequest", "REQUEST_STATUS"), (Object)0, 0)));
            enrollFailedDeviceQuery.setCriteria(enrollFailedDeviceQuery.getCriteria().and(new Criteria(Column.getColumn("ManagedDevice", "MANAGED_STATUS"), (Object)null, 0)));
            json.put("managed_count", (Object)MDMUtil.getInstance().getIntVal(resultJson.get((Object)"enrolledCount")));
            json.put("retired_count", (Object)MDMUtil.getInstance().getIntVal(resultJson.get((Object)"retiredCount")));
            json.put("staged_count", MDMUtil.getInstance().getIntVal(resultJson.get((Object)"instockCount")) + MDMUtil.getInstance().getIntVal(resultJson.get((Object)"waitingForUserAssignCount")) + DBUtil.getRecordActualCount(sq, "DeviceForEnrollment", "ENROLLMENT_DEVICE_ID") + DBUtil.getRecordActualCount(enrollFailedDeviceQuery, "DeviceEnrollmentRequest", "ENROLLMENT_REQUEST_ID"));
            json.put("pending_request_count", (Object)MDMUtil.getInstance().getIntVal(resultJson.get((Object)"pendingRequestCount")));
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "", e);
        }
        return json;
    }
    
    public List getSupervisedAnd9_3AboveMobileDevices(final List resList) throws Exception {
        final List applicableList = new ArrayList();
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("MdDeviceInfo"));
        selectQuery.addJoin(new Join("MdDeviceInfo", "MdModelInfo", new String[] { "MODEL_ID" }, new String[] { "MODEL_ID" }, 2));
        final Criteria resCriteria = new Criteria(Column.getColumn("MdDeviceInfo", "RESOURCE_ID"), (Object)resList.toArray(), 8);
        final Criteria modelCriteria = new Criteria(Column.getColumn("MdModelInfo", "MODEL_TYPE"), (Object)new Integer[] { 4, 3 }, 9);
        selectQuery.addSelectColumn(Column.getColumn("MdDeviceInfo", "RESOURCE_ID"));
        selectQuery.addSelectColumn(Column.getColumn("MdDeviceInfo", "OS_VERSION"));
        selectQuery.addSelectColumn(Column.getColumn("MdDeviceInfo", "IS_SUPERVISED"));
        selectQuery.setCriteria(resCriteria.and(modelCriteria));
        final DataObject dataObject = MDMUtil.getPersistence().get(selectQuery);
        final Iterator iterator = dataObject.getRows("MdDeviceInfo");
        while (iterator.hasNext()) {
            final Row row = iterator.next();
            final String osVersion = (String)row.get("OS_VERSION");
            final boolean isSupervised = row.get("IS_SUPERVISED") != null && (boolean)row.get("IS_SUPERVISED");
            final boolean isHigher = osVersion.equals("9.3") || new VersionChecker().isGreater(osVersion, "9.3");
            final Long resID = (Long)row.get("RESOURCE_ID");
            if (isSupervised && isHigher) {
                applicableList.add(resID);
            }
        }
        return applicableList;
    }
    
    public void updateManagedDeviceExtnUpdatedTime(final List<Long> managedDeviceIds) throws DataAccessException {
        final UpdateQuery updateTimeQuery = (UpdateQuery)new UpdateQueryImpl("ManagedDeviceExtn");
        updateTimeQuery.setUpdateColumn("LAST_MODIFIED_TIME", (Object)System.currentTimeMillis());
        updateTimeQuery.setCriteria(new Criteria(Column.getColumn("ManagedDeviceExtn", "MANAGED_DEVICE_ID"), (Object)managedDeviceIds.toArray(), 8));
        MDMUtil.getPersistence().update(updateTimeQuery);
    }
    
    public void updateManagedDeviceExtnUpdatedTime(final Long managedDeviceId) throws DataAccessException {
        final UpdateQuery updateTimeQuery = (UpdateQuery)new UpdateQueryImpl("ManagedDeviceExtn");
        updateTimeQuery.setUpdateColumn("LAST_MODIFIED_TIME", (Object)System.currentTimeMillis());
        updateTimeQuery.setCriteria(new Criteria(Column.getColumn("ManagedDeviceExtn", "MANAGED_DEVICE_ID"), (Object)managedDeviceId, 0));
        MDMUtil.getPersistence().update(updateTimeQuery);
    }
    
    public void removeKioskStateForUnmanagedDevice(final Long resourceID) {
        final JSONObject deviceState = new JSONObject();
        try {
            deviceState.put("RESOURCE_ID", (Object)resourceID);
            deviceState.put("CURRENT_KIOSK_STATE", (Object)KioskPauseResumeManager.NOT_IN_KIOSK);
            deviceState.put("REMARKS", (Object)"device_unmanaged");
            deviceState.put("LAST_CHANGED_TIME", System.currentTimeMillis());
            new KioskPauseResumeHandler().addOrUpdateKioskStateForDevice(deviceState);
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Exception while changing device kiosk state", e);
        }
    }
    
    public String deviceKioskOrRevokeAdminPassword(String udidString, final String mode, Long agentVersionCode) throws Exception {
        final String time = String.valueOf(System.currentTimeMillis() / 10000000L);
        agentVersionCode %= 100000L;
        udidString = "MEMDM" + mode + udidString + "ManageEngine" + time;
        String password = null;
        if (agentVersionCode >= 536L) {
            password = MDMCheckSumProvider.getInstance().getSHA256HashFromString(udidString);
        }
        else {
            password = MDMCheckSumProvider.getInstance().GetMD5hashFromString(udidString);
        }
        return password;
    }
    
    public int getDeviceCountForStagedview(final JSONObject json) {
        int count = 0;
        try {
            final Long customerid = json.getLong("customerid");
            final int selectedStatus = json.getInt("selectedStatus");
            final int templateType = json.getInt("templateType");
            final int platformType = json.getInt("platformType");
            final SelectQuery leftselectQuery = (SelectQuery)new SelectQueryImpl(new Table("Resource"));
            leftselectQuery.addJoin(new Join("Resource", "ManagedDevice", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2));
            leftselectQuery.addJoin(new Join("ManagedDevice", "EnrollmentRequestToDevice", new String[] { "RESOURCE_ID" }, new String[] { "MANAGED_DEVICE_ID" }, 2));
            leftselectQuery.addJoin(new Join("EnrollmentRequestToDevice", "DeviceEnrollmentRequest", new String[] { "ENROLLMENT_REQUEST_ID" }, new String[] { "ENROLLMENT_REQUEST_ID" }, 2));
            leftselectQuery.addJoin(new Join("DeviceEnrollmentRequest", "EnrollmentTemplateToRequest", new String[] { "ENROLLMENT_REQUEST_ID" }, new String[] { "ENROLLMENT_REQUEST_ID" }, 1));
            leftselectQuery.addJoin(new Join("EnrollmentTemplateToRequest", "EnrollmentTemplate", new String[] { "TEMPLATE_ID" }, new String[] { "TEMPLATE_ID" }, 1));
            leftselectQuery.addJoin(new Join("ManagedDevice", "MdDeviceInfo", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 1));
            leftselectQuery.addJoin(new Join("MdDeviceInfo", "MdModelInfo", new String[] { "MODEL_ID" }, new String[] { "MODEL_ID" }, 1));
            Criteria leftQuerycriteria = new Criteria(new Column("Resource", "CUSTOMER_ID"), (Object)customerid, 0);
            if (platformType != -1) {
                if (platformType != 5 && platformType != 1) {
                    final Criteria platformcriteria = new Criteria(new Column("DeviceEnrollmentRequest", "PLATFORM_TYPE"), (Object)platformType, 0);
                    leftQuerycriteria = leftQuerycriteria.and(platformcriteria);
                }
                else if (platformType == 1) {
                    final Criteria platformcriteria = new Criteria(new Column("DeviceEnrollmentRequest", "PLATFORM_TYPE"), (Object)platformType, 0).and(new Criteria(new Column("MdModelInfo", "MODEL_TYPE"), (Object)new int[] { 3, 4 }, 9));
                    leftQuerycriteria = leftQuerycriteria.and(platformcriteria);
                }
                else if (platformType == 5) {
                    final Criteria platformcriteria = new Criteria(new Column("DeviceEnrollmentRequest", "PLATFORM_TYPE"), (Object)platformType, 0).and(new Criteria(new Column("MdModelInfo", "MODEL_TYPE"), (Object)new int[] { 3, 4 }, 8));
                    leftQuerycriteria = leftQuerycriteria.and(platformcriteria);
                }
                else if (platformType == 0) {
                    final Criteria platformCriteria = new Criteria(new Column("DeviceEnrollmentRequest", "PLATFORM_TYPE"), (Object)platformType, 0);
                    leftQuerycriteria = leftQuerycriteria.and(platformCriteria);
                }
            }
            if (templateType != -1) {
                leftQuerycriteria = leftQuerycriteria.and(new Criteria(Column.getColumn("EnrollmentTemplate", "TEMPLATE_TYPE"), (Object)templateType, 0));
            }
            if (selectedStatus != -1) {
                leftQuerycriteria = leftQuerycriteria.and(new Criteria(new Column("ManagedDevice", "MANAGED_STATUS"), (Object)selectedStatus, 0));
            }
            else {
                leftQuerycriteria = leftQuerycriteria.and(new Criteria(new Column("ManagedDevice", "MANAGED_STATUS"), (Object)new int[] { 10, 9 }, 8));
            }
            leftselectQuery.setCriteria(leftQuerycriteria);
            count = DBUtil.getRecordActualCount(leftselectQuery, "Resource", "RESOURCE_ID");
            final SelectQuery rightselectQuery = (SelectQuery)new SelectQueryImpl(new Table("DeviceForEnrollment"));
            rightselectQuery.addJoin(new Join("DeviceForEnrollment", "AppleDEPDeviceForEnrollment", new String[] { "ENROLLMENT_DEVICE_ID" }, new String[] { "ENROLLMENT_DEVICE_ID" }, 1));
            rightselectQuery.addJoin(new Join("DeviceForEnrollment", "AppleConfigDeviceForEnrollment", new String[] { "ENROLLMENT_DEVICE_ID" }, new String[] { "ENROLLMENT_DEVICE_ID" }, 1));
            rightselectQuery.addJoin(new Join("DeviceForEnrollment", "WinAzureADDeviceForEnrollment", new String[] { "ENROLLMENT_DEVICE_ID" }, new String[] { "ENROLLMENT_DEVICE_ID" }, 1));
            rightselectQuery.addJoin(new Join("DeviceForEnrollment", "WindowsLaptopDeviceForEnrollment", new String[] { "ENROLLMENT_DEVICE_ID" }, new String[] { "ENROLLMENT_DEVICE_ID" }, 1));
            rightselectQuery.addJoin(new Join("DeviceForEnrollment", "WinModernMgmtDeviceForEnrollment", new String[] { "ENROLLMENT_DEVICE_ID" }, new String[] { "ENROLLMENT_DEVICE_ID" }, 1));
            rightselectQuery.addJoin(new Join("DeviceForEnrollment", "WindowsICDDeviceForEnrollment", new String[] { "ENROLLMENT_DEVICE_ID" }, new String[] { "ENROLLMENT_DEVICE_ID" }, 1));
            rightselectQuery.addJoin(new Join("DeviceForEnrollment", "KNOXMobileDeviceForEnrollment", new String[] { "ENROLLMENT_DEVICE_ID" }, new String[] { "ENROLLMENT_DEVICE_ID" }, 1));
            rightselectQuery.addJoin(new Join("DeviceForEnrollment", "AndroidQRDeviceForEnrollment", new String[] { "ENROLLMENT_DEVICE_ID" }, new String[] { "ENROLLMENT_DEVICE_ID" }, 1));
            rightselectQuery.addJoin(new Join("DeviceForEnrollment", "AndroidNFCDeviceForEnrollment", new String[] { "ENROLLMENT_DEVICE_ID" }, new String[] { "ENROLLMENT_DEVICE_ID" }, 1));
            rightselectQuery.addJoin(new Join("DeviceForEnrollment", "AndroidZTDeviceForEnrollment", new String[] { "ENROLLMENT_DEVICE_ID" }, new String[] { "ENROLLMENT_DEVICE_ID" }, 1));
            rightselectQuery.addJoin(new Join("DeviceForEnrollment", "GSChromeDeviceForEnrollment", new String[] { "ENROLLMENT_DEVICE_ID" }, new String[] { "ENROLLMENT_DEVICE_ID" }, 1));
            rightselectQuery.addJoin(new Join("DeviceForEnrollment", "MacModernMgmtDeviceForEnrollment", new String[] { "ENROLLMENT_DEVICE_ID" }, new String[] { "ENROLLMENT_DEVICE_ID" }, 1));
            rightselectQuery.addJoin(new Join("DeviceForEnrollment", "DeviceEnrollmentToRequest", new String[] { "ENROLLMENT_DEVICE_ID" }, new String[] { "ENROLLMENT_DEVICE_ID" }, 1));
            rightselectQuery.addJoin(new Join("DeviceEnrollmentToRequest", "DeviceEnrollmentRequest", new String[] { "ENROLLMENT_REQUEST_ID" }, new String[] { "ENROLLMENT_REQUEST_ID" }, 1));
            rightselectQuery.addJoin(new Join("DeviceEnrollmentRequest", "EnrollmentRequestToDevice", new String[] { "ENROLLMENT_REQUEST_ID" }, new String[] { "ENROLLMENT_REQUEST_ID" }, 1));
            rightselectQuery.addJoin(new Join("EnrollmentRequestToDevice", "ManagedDevice", new String[] { "ENROLLMENT_REQUEST_ID" }, new String[] { "RESOURCE_ID" }, 1));
            rightselectQuery.addJoin(new Join("DeviceForEnrollment", "DeviceEnrollmentToUser", new String[] { "ENROLLMENT_DEVICE_ID" }, new String[] { "ENROLLMENT_DEVICE_ID" }, 1));
            Criteria rightQuerycriteria = new Criteria(new Column("DeviceForEnrollment", "CUSTOMER_ID"), (Object)customerid, 0).and(new Criteria(new Column("ManagedDevice", "MANAGED_STATUS"), (Object)2, 1).or(new Criteria(new Column("ManagedDevice", "MANAGED_STATUS"), (Object)null, 0)));
            if (platformType != -1) {
                Criteria platformcriteria2 = null;
                switch (platformType) {
                    case 1: {
                        platformcriteria2 = new Criteria(new Column("AppleDEPDeviceForEnrollment", "ENROLLMENT_DEVICE_ID"), (Object)null, 1).and(new Criteria(new Column("AppleDEPDeviceForEnrollment", "DEVICE_MODEL"), (Object)4, 1)).or(new Criteria(new Column("AppleConfigDeviceForEnrollment", "ENROLLMENT_DEVICE_ID"), (Object)null, 1));
                        break;
                    }
                    case 2: {
                        platformcriteria2 = new Criteria(new Column("KNOXMobileDeviceForEnrollment", "ENROLLMENT_DEVICE_ID"), (Object)null, 1).or(new Criteria(new Column("AndroidQRDeviceForEnrollment", "ENROLLMENT_DEVICE_ID"), (Object)null, 1)).or(new Criteria(new Column("AndroidNFCDeviceForEnrollment", "ENROLLMENT_DEVICE_ID"), (Object)null, 1).or(new Criteria(new Column("AndroidZTDeviceForEnrollment", "ENROLLMENT_DEVICE_ID"), (Object)null, 1)));
                        break;
                    }
                    case 3: {
                        platformcriteria2 = new Criteria(new Column("WinAzureADDeviceForEnrollment", "ENROLLMENT_DEVICE_ID"), (Object)null, 1).or(new Criteria(new Column("WindowsLaptopDeviceForEnrollment", "ENROLLMENT_DEVICE_ID"), (Object)null, 1)).or(new Criteria(new Column("WindowsICDDeviceForEnrollment", "ENROLLMENT_DEVICE_ID"), (Object)null, 1)).or(new Criteria(new Column("WinModernMgmtDeviceForEnrollment", "ENROLLMENT_DEVICE_ID"), (Object)null, 1));
                        break;
                    }
                    case 5: {
                        platformcriteria2 = new Criteria(new Column("MacModernMgmtDeviceForEnrollment", "ENROLLMENT_DEVICE_ID"), (Object)null, 1).or(new Criteria(new Column("AppleDEPDeviceForEnrollment", "ENROLLMENT_DEVICE_ID"), (Object)null, 1).and(new Criteria(new Column("AppleDEPDeviceForEnrollment", "DEVICE_MODEL"), (Object)4, 0)));
                        break;
                    }
                }
                rightQuerycriteria = rightQuerycriteria.and(platformcriteria2);
            }
            if (templateType != -1) {
                Criteria templatecriteria = null;
                switch (templateType) {
                    case 20: {
                        templatecriteria = new Criteria(new Column("AndroidNFCDeviceForEnrollment", "ENROLLMENT_DEVICE_ID"), (Object)null, 1);
                        break;
                    }
                    case 11: {
                        templatecriteria = new Criteria(new Column("AppleConfigDeviceForEnrollment", "ENROLLMENT_DEVICE_ID"), (Object)null, 1);
                        break;
                    }
                    case 21: {
                        templatecriteria = new Criteria(new Column("KNOXMobileDeviceForEnrollment", "ENROLLMENT_DEVICE_ID"), (Object)null, 1);
                        break;
                    }
                    case 30: {
                        templatecriteria = new Criteria(new Column("WindowsICDDeviceForEnrollment", "ENROLLMENT_DEVICE_ID"), (Object)null, 1);
                        break;
                    }
                    case 22: {
                        templatecriteria = new Criteria(new Column("AndroidQRDeviceForEnrollment", "ENROLLMENT_DEVICE_ID"), (Object)null, 1);
                        break;
                    }
                    case 31: {
                        templatecriteria = new Criteria(new Column("WindowsLaptopDeviceForEnrollment", "ENROLLMENT_DEVICE_ID"), (Object)null, 1);
                        break;
                    }
                    case 23: {
                        templatecriteria = new Criteria(new Column("AndroidZTDeviceForEnrollment", "ENROLLMENT_DEVICE_ID"), (Object)null, 1);
                        break;
                    }
                    case 32: {
                        templatecriteria = new Criteria(new Column("WinAzureADDeviceForEnrollment", "ENROLLMENT_DEVICE_ID"), (Object)null, 1);
                        break;
                    }
                    case 10: {
                        templatecriteria = new Criteria(new Column("AppleDEPDeviceForEnrollment", "ENROLLMENT_DEVICE_ID"), (Object)null, 1);
                        break;
                    }
                    case 40: {
                        templatecriteria = new Criteria(new Column("GSChromeDeviceForEnrollment", "ENROLLMENT_DEVICE_ID"), (Object)null, 1);
                        break;
                    }
                    case 12: {
                        templatecriteria = new Criteria(new Column("MacModernMgmtDeviceForEnrollment", "ENROLLMENT_DEVICE_ID"), (Object)null, 1);
                        break;
                    }
                    case 33: {
                        templatecriteria = new Criteria(new Column("WinModernMgmtDeviceForEnrollment", "ENROLLMENT_DEVICE_ID"), (Object)null, 1);
                        break;
                    }
                }
                if (templatecriteria != null) {
                    rightQuerycriteria = rightQuerycriteria.and(templatecriteria);
                }
            }
            if (selectedStatus != -1) {
                switch (selectedStatus) {
                    case 12: {
                        rightQuerycriteria = rightQuerycriteria.and(new Criteria(new Column("DeviceEnrollmentRequest", "ENROLLMENT_REQUEST_ID"), (Object)null, 0).and(new Criteria(new Column("DeviceEnrollmentToUser", "MANAGED_USER_ID"), (Object)null, 0)));
                        break;
                    }
                    case 5: {
                        rightQuerycriteria = rightQuerycriteria.and(new Criteria(new Column("ManagedDevice", "MANAGED_STATUS"), (Object)5, 0));
                        break;
                    }
                    case 0: {
                        rightQuerycriteria = rightQuerycriteria.and(new Criteria(new Column("DeviceEnrollmentRequest", "ENROLLMENT_REQUEST_ID"), (Object)null, 0).and(new Criteria(new Column("DeviceEnrollmentToUser", "MANAGED_USER_ID"), (Object)null, 1)));
                        break;
                    }
                }
            }
            rightselectQuery.setCriteria(rightQuerycriteria);
            count += DBUtil.getRecordActualCount(rightselectQuery, "DeviceForEnrollment", "ENROLLMENT_DEVICE_ID");
        }
        catch (final Exception exp) {
            this.logger.log(Level.SEVERE, "Exception while getting count for staged device view!!!", exp);
        }
        return count;
    }
    
    public int isIosPresent(final String reqIds) throws Exception {
        final String[] reqIdArray = reqIds.split(",");
        final Long[] reqId = new Long[reqIdArray.length];
        for (int i = 0; i < reqIdArray.length; ++i) {
            reqId[i] = Long.parseLong(reqIdArray[i]);
        }
        final SelectQuery sq = (SelectQuery)new SelectQueryImpl(new Table("DeviceEnrollmentRequest"));
        final Criteria cri = new Criteria(new Column("DeviceEnrollmentRequest", "PLATFORM_TYPE"), (Object)1, 0).and(new Criteria(new Column("DeviceEnrollmentRequest", "ENROLLMENT_REQUEST_ID"), (Object)reqId, 8));
        sq.setCriteria(cri);
        int count = DBUtil.getRecordActualCount(sq, "DeviceEnrollmentRequest", "ENROLLMENT_REQUEST_ID");
        count = ((count > 0) ? 1 : 0);
        return count;
    }
    
    public int isPendingForActivation(final String serialNo) {
        int templateType = -1;
        DMDataSetWrapper ds = null;
        try {
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("DeviceForEnrollment"));
            selectQuery.addJoin(new Join("DeviceForEnrollment", "AppleDEPDeviceForEnrollment", new String[] { "ENROLLMENT_DEVICE_ID" }, new String[] { "ENROLLMENT_DEVICE_ID" }, 1));
            selectQuery.addJoin(new Join("DeviceForEnrollment", "WinAzureADDeviceForEnrollment", new String[] { "ENROLLMENT_DEVICE_ID" }, new String[] { "ENROLLMENT_DEVICE_ID" }, 1));
            selectQuery.addJoin(new Join("DeviceForEnrollment", "AppleConfigDeviceForEnrollment", new String[] { "ENROLLMENT_DEVICE_ID" }, new String[] { "ENROLLMENT_DEVICE_ID" }, 1));
            selectQuery.addJoin(new Join("DeviceForEnrollment", "WindowsLaptopDeviceForEnrollment", new String[] { "ENROLLMENT_DEVICE_ID" }, new String[] { "ENROLLMENT_DEVICE_ID" }, 1));
            selectQuery.addJoin(new Join("DeviceForEnrollment", "WinModernMgmtDeviceForEnrollment", new String[] { "ENROLLMENT_DEVICE_ID" }, new String[] { "ENROLLMENT_DEVICE_ID" }, 1));
            selectQuery.addJoin(new Join("DeviceForEnrollment", "WindowsICDDeviceForEnrollment", new String[] { "ENROLLMENT_DEVICE_ID" }, new String[] { "ENROLLMENT_DEVICE_ID" }, 1));
            selectQuery.addJoin(new Join("DeviceForEnrollment", "KNOXMobileDeviceForEnrollment", new String[] { "ENROLLMENT_DEVICE_ID" }, new String[] { "ENROLLMENT_DEVICE_ID" }, 1));
            selectQuery.addJoin(new Join("DeviceForEnrollment", "AndroidQRDeviceForEnrollment", new String[] { "ENROLLMENT_DEVICE_ID" }, new String[] { "ENROLLMENT_DEVICE_ID" }, 1));
            selectQuery.addJoin(new Join("DeviceForEnrollment", "AndroidNFCDeviceForEnrollment", new String[] { "ENROLLMENT_DEVICE_ID" }, new String[] { "ENROLLMENT_DEVICE_ID" }, 1));
            selectQuery.addJoin(new Join("DeviceForEnrollment", "AndroidZTDeviceForEnrollment", new String[] { "ENROLLMENT_DEVICE_ID" }, new String[] { "ENROLLMENT_DEVICE_ID" }, 1));
            selectQuery.addJoin(new Join("DeviceForEnrollment", "GSChromeDeviceForEnrollment", new String[] { "ENROLLMENT_DEVICE_ID" }, new String[] { "ENROLLMENT_DEVICE_ID" }, 1));
            selectQuery.addJoin(new Join("DeviceForEnrollment", "MacModernMgmtDeviceForEnrollment", new String[] { "ENROLLMENT_DEVICE_ID" }, new String[] { "ENROLLMENT_DEVICE_ID" }, 1));
            selectQuery.setCriteria(new Criteria(new Column("DeviceForEnrollment", "SERIAL_NUMBER"), (Object)serialNo, 0));
            selectQuery.addSelectColumn(Column.getColumn("DeviceForEnrollment", "ENROLLMENT_DEVICE_ID"));
            selectQuery.addSelectColumn(Column.getColumn("AppleDEPDeviceForEnrollment", "ENROLLMENT_DEVICE_ID", "ENROLLMENT_DEVICE_ID"));
            selectQuery.addSelectColumn(Column.getColumn("AppleDEPDeviceForEnrollment", "ENROLLMENT_DEVICE_ID", "DEP_TOKEN_ID"));
            selectQuery.addSelectColumn(Column.getColumn("WinAzureADDeviceForEnrollment", "ENROLLMENT_DEVICE_ID", "ENROLLMENT_DEVICE_ID"));
            selectQuery.addSelectColumn(Column.getColumn("AppleConfigDeviceForEnrollment", "ENROLLMENT_DEVICE_ID", "ENROLLMENT_DEVICE_ID"));
            selectQuery.addSelectColumn(Column.getColumn("WindowsLaptopDeviceForEnrollment", "ENROLLMENT_DEVICE_ID", "ENROLLMENT_DEVICE_ID"));
            selectQuery.addSelectColumn(Column.getColumn("WinModernMgmtDeviceForEnrollment", "ENROLLMENT_DEVICE_ID", "ENROLLMENT_DEVICE_ID"));
            selectQuery.addSelectColumn(Column.getColumn("WindowsICDDeviceForEnrollment", "ENROLLMENT_DEVICE_ID", "ENROLLMENT_DEVICE_ID"));
            selectQuery.addSelectColumn(Column.getColumn("KNOXMobileDeviceForEnrollment", "ENROLLMENT_DEVICE_ID", "ENROLLMENT_DEVICE_ID"));
            selectQuery.addSelectColumn(Column.getColumn("AndroidNFCDeviceForEnrollment", "ENROLLMENT_DEVICE_ID", "ENROLLMENT_DEVICE_ID"));
            selectQuery.addSelectColumn(Column.getColumn("AndroidQRDeviceForEnrollment", "ENROLLMENT_DEVICE_ID", "ENROLLMENT_DEVICE_ID"));
            selectQuery.addSelectColumn(Column.getColumn("AndroidZTDeviceForEnrollment", "ENROLLMENT_DEVICE_ID", "ENROLLMENT_DEVICE_ID"));
            selectQuery.addSelectColumn(Column.getColumn("GSChromeDeviceForEnrollment", "ENROLLMENT_DEVICE_ID", "ENROLLMENT_DEVICE_ID"));
            selectQuery.addSelectColumn(Column.getColumn("MacModernMgmtDeviceForEnrollment", "ENROLLMENT_DEVICE_ID", "ENROLLMENT_DEVICE_ID"));
            final CaseExpression templateTypeCaseExpression = new CaseExpression("TEMPLATE_TYPE");
            templateTypeCaseExpression.addWhen(new Criteria(Column.getColumn("AppleDEPDeviceForEnrollment", "ENROLLMENT_DEVICE_ID"), (Object)null, 1), (Object)10);
            templateTypeCaseExpression.addWhen(new Criteria(Column.getColumn("AppleConfigDeviceForEnrollment", "ENROLLMENT_DEVICE_ID"), (Object)null, 1), (Object)11);
            templateTypeCaseExpression.addWhen(new Criteria(Column.getColumn("KNOXMobileDeviceForEnrollment", "ENROLLMENT_DEVICE_ID"), (Object)null, 1), (Object)21);
            templateTypeCaseExpression.addWhen(new Criteria(Column.getColumn("AndroidNFCDeviceForEnrollment", "ENROLLMENT_DEVICE_ID"), (Object)null, 1), (Object)20);
            templateTypeCaseExpression.addWhen(new Criteria(Column.getColumn("AndroidQRDeviceForEnrollment", "ENROLLMENT_DEVICE_ID"), (Object)null, 1), (Object)22);
            templateTypeCaseExpression.addWhen(new Criteria(Column.getColumn("AndroidZTDeviceForEnrollment", "ENROLLMENT_DEVICE_ID"), (Object)null, 1), (Object)23);
            templateTypeCaseExpression.addWhen(new Criteria(Column.getColumn("WindowsLaptopDeviceForEnrollment", "ENROLLMENT_DEVICE_ID"), (Object)null, 1), (Object)31);
            templateTypeCaseExpression.addWhen(new Criteria(Column.getColumn("WindowsICDDeviceForEnrollment", "ENROLLMENT_DEVICE_ID"), (Object)null, 1), (Object)30);
            templateTypeCaseExpression.addWhen(new Criteria(Column.getColumn("WinAzureADDeviceForEnrollment", "ENROLLMENT_DEVICE_ID"), (Object)null, 1), (Object)32);
            templateTypeCaseExpression.addWhen(new Criteria(Column.getColumn("WinModernMgmtDeviceForEnrollment", "ENROLLMENT_DEVICE_ID"), (Object)null, 1), (Object)33);
            templateTypeCaseExpression.addWhen(new Criteria(Column.getColumn("GSChromeDeviceForEnrollment", "ENROLLMENT_DEVICE_ID"), (Object)null, 1), (Object)40);
            templateTypeCaseExpression.addWhen(new Criteria(Column.getColumn("MacModernMgmtDeviceForEnrollment", "ENROLLMENT_DEVICE_ID"), (Object)null, 1), (Object)12);
            selectQuery.addSelectColumn((Column)templateTypeCaseExpression);
            ds = DMDataSetWrapper.executeQuery((Object)selectQuery);
            if (ds != null && ds.next()) {
                templateType = (int)ds.getValue("TEMPLATE_TYPE");
            }
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, "Exception in isPendingForActivation() ", ex);
        }
        return templateType;
    }
    
    public MultiMap removeProfileForDeevices(final String reqIdsStr, final Long customerID) throws Exception {
        JSONArray depDeviceDetailsJArray = null;
        final MultiHashMap depTokenToDeviceSerialNumbersMap = new MultiHashMap();
        final MultiHashMap depTokenToDeviceResourceIDMap = new MultiHashMap();
        final List<Long> reqID = new ArrayList<Long>();
        final String[] reqIds = reqIdsStr.split(",");
        for (int i = 0; i < reqIds.length; ++i) {
            reqID.add(Long.parseLong(reqIds[i]));
        }
        final JSONObject resourceIDjson = this.getResourceIDFromErid(reqID);
        final JSONArray resID = (JSONArray)resourceIDjson.get("ResourceID");
        final Long[] resourceIDArr = new Long[reqIds.length];
        for (int j = 0; j < resID.length(); ++j) {
            resourceIDArr[j] = resID.getLong(j);
        }
        depDeviceDetailsJArray = this.getDEPDeviceSerialNumber(resourceIDArr);
        if (depDeviceDetailsJArray != null) {
            for (int j = 0; j < depDeviceDetailsJArray.length(); ++j) {
                final JSONObject deviceDetailsJSON = depDeviceDetailsJArray.optJSONObject(j);
                depTokenToDeviceSerialNumbersMap.put((Object)deviceDetailsJSON.optLong("DEP_TOKEN_ID"), (Object)deviceDetailsJSON.optString("SERIAL_NUMBER"));
                depTokenToDeviceResourceIDMap.put((Object)deviceDetailsJSON.optLong("DEP_TOKEN_ID"), (Object)deviceDetailsJSON.optString("RESOURCE_ID"));
            }
            final Set<Long> depTokenSet = depTokenToDeviceSerialNumbersMap.keySet();
            for (final Long depTokenID : depTokenSet) {
                final JSONObject deviceJSON = new JSONObject();
                final Collection<String> serialNumberList = depTokenToDeviceSerialNumbersMap.getCollection((Object)depTokenID);
                final JSONArray deviceList = new JSONArray((Collection)serialNumberList);
                deviceJSON.put("devices", (Object)deviceList);
                AppleDEPWebServicetHandler.getInstance(depTokenID, customerID).removeDEPProfile(deviceJSON);
            }
        }
        return (MultiMap)depTokenToDeviceResourceIDMap;
    }
    
    public void getSelfEnrolledDeviceCountForUser(final JSONObject jsonObject) throws SyMException {
        int count = 0;
        try {
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("ManagedDevice"));
            selectQuery.addJoin(new Join("ManagedDevice", "EnrollmentRequestToDevice", new String[] { "RESOURCE_ID" }, new String[] { "MANAGED_DEVICE_ID" }, 2));
            selectQuery.addJoin(new Join("EnrollmentRequestToDevice", "DeviceEnrollmentRequest", new String[] { "ENROLLMENT_REQUEST_ID" }, new String[] { "ENROLLMENT_REQUEST_ID" }, 2));
            selectQuery.addJoin(new Join("DeviceEnrollmentRequest", "ManagedUser", new String[] { "MANAGED_USER_ID" }, new String[] { "MANAGED_USER_ID" }, 2));
            selectQuery.addJoin(new Join("ManagedUser", "Resource", new String[] { "MANAGED_USER_ID" }, new String[] { "RESOURCE_ID" }, 2));
            final Criteria enrollmentReqCri = new Criteria(Column.getColumn("DeviceEnrollmentRequest", "ENROLLMENT_TYPE"), (Object)2, 0).and(new Criteria(Column.getColumn("ManagedUser", "EMAIL_ADDRESS"), (Object)String.valueOf(jsonObject.get("EMAIL_ADDRESS")), 0)).and(new Criteria(Column.getColumn("Resource", "DOMAIN_NETBIOS_NAME"), (Object)String.valueOf(jsonObject.get("DOMAIN_NETBIOS_NAME")), 0));
            final Criteria managedStatusCri = new Criteria(Column.getColumn("ManagedDevice", "MANAGED_STATUS"), (Object)2, 0);
            selectQuery.setCriteria(enrollmentReqCri.and(managedStatusCri));
            count = DBUtil.getRecordActualCount(selectQuery, "DeviceEnrollmentRequest", "ENROLLMENT_REQUEST_ID");
            if (count >= jsonObject.getInt("COUNT")) {
                throw new SyMException();
            }
        }
        catch (final SyMException exp) {
            this.logger.log(Level.SEVERE, "self enrollment count exceeded", (Throwable)exp);
            throw exp;
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Exception while getting count for self enrollment", e);
        }
    }
    
    public void removeDeviceForEnrollmentEntires(final List<Long> managedDeviceIdList, final Long customerid) {
        try {
            final List<String> udidList = this.getUDIDListForResourceIDList(managedDeviceIdList);
            this.removeDeviceForEnrollmentEntiresUsingUdid(udidList, customerid);
        }
        catch (final Exception exp) {
            this.logger.log(Level.SEVERE, "Exception while removeDeviceForEnrollmentEntires", exp);
        }
    }
    
    public void removeDeviceForEnrollmentEntiresUsingUdid(final List<String> udid, final Long customerid) {
        try {
            final DeleteQuery deleteQuery = (DeleteQuery)new DeleteQueryImpl("DeviceForEnrollment");
            deleteQuery.setCriteria(new Criteria(Column.getColumn("DeviceForEnrollment", "UDID"), (Object)udid.toArray(), 8).and(new Criteria(Column.getColumn("DeviceForEnrollment", "CUSTOMER_ID"), (Object)customerid, 0)));
            MDMUtil.getPersistenceLite().delete(deleteQuery);
        }
        catch (final Exception exp) {
            this.logger.log(Level.SEVERE, "Exception while removing device for enrollment entries for deprovision of waiting for user assignment devices", exp);
        }
    }
    
    public JSONObject getDeprovisionWipeStatus(final Long managedDeviceId) throws Exception {
        int wipetype = -1;
        final SelectQuery depovisionQuery = this.getDeprovisionQuery();
        depovisionQuery.setCriteria(new Criteria(Column.getColumn("DeprovisionHistory", "RESOURCE_ID"), (Object)managedDeviceId, 0));
        final DataObject DO = MDMUtil.getPersistenceLite().get(depovisionQuery);
        if (!DO.isEmpty()) {
            final Row row = DO.getFirstRow("DeprovisionHistory");
            wipetype = (int)row.get("DEPROVISION_TYPE");
        }
        String wipeStr = null;
        switch (wipetype) {
            case 1: {
                wipeStr = "CorporateWipe";
                break;
            }
            case 2: {
                wipeStr = "EraseDevice";
                break;
            }
            case -1: {
                throw new SyMException();
            }
        }
        final JSONObject jsonObject = DeviceInvCommandHandler.getInstance().getDeviceCommandStatus(Arrays.asList(managedDeviceId), wipeStr).getJSONObject(0);
        jsonObject.put("wipe_type", wipetype);
        return jsonObject;
    }
    
    public JSONObject getDeviceOwnershipForResourceIDs(final List<Long> resourceID) {
        final JSONObject jsonObject = new JSONObject();
        final SelectQuery sQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("DeviceEnrollmentRequest"));
        final Join enrolReqDevice = new Join("DeviceEnrollmentRequest", "EnrollmentRequestToDevice", new String[] { "ENROLLMENT_REQUEST_ID" }, new String[] { "ENROLLMENT_REQUEST_ID" }, 2);
        final Criteria resCriteria = new Criteria(Column.getColumn("EnrollmentRequestToDevice", "MANAGED_DEVICE_ID"), (Object)resourceID.toArray(), 8);
        sQuery.addJoin(enrolReqDevice);
        sQuery.setCriteria(resCriteria);
        sQuery.addSelectColumn(Column.getColumn("DeviceEnrollmentRequest", "ENROLLMENT_REQUEST_ID"));
        sQuery.addSelectColumn(Column.getColumn("DeviceEnrollmentRequest", "OWNED_BY"));
        sQuery.addSelectColumn(Column.getColumn("EnrollmentRequestToDevice", "MANAGED_DEVICE_ID"));
        sQuery.addSelectColumn(Column.getColumn("EnrollmentRequestToDevice", "ENROLLMENT_REQUEST_ID"));
        final int ownerShipType = -1;
        try {
            MDMUtil.getInstance();
            final org.json.simple.JSONArray dsJSArray = MDMUtil.executeSelectQuery(sQuery);
            if (!dsJSArray.isEmpty()) {
                for (int i = 0; i < dsJSArray.size(); ++i) {
                    final org.json.simple.JSONObject jsObject = (org.json.simple.JSONObject)dsJSArray.get(i);
                    jsonObject.put(String.valueOf(jsObject.get((Object)"MANAGED_DEVICE_ID")), (Object)jsObject.get((Object)"OWNED_BY"));
                }
            }
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, "Exception in getDeviceOwnership", ex);
        }
        return jsonObject;
    }
    
    public void removeMemberfromGroupsOnDeprovision(final List<Long> resourceId, final Long userId) {
        Logger.getLogger("MDMEnrollment").log(Level.INFO, "Removing devices from groups");
        try {
            final MultiMap multiMap = MDMGroupHandler.getInstance().getGroupsForResourceIdList(resourceId);
            final Set keyset = multiMap.keySet();
            for (final Long memberResourceId : keyset) {
                final List<Long> groupResourceId = (List<Long>)multiMap.get((Object)memberResourceId);
                final Long[] groupIdArray = new Long[groupResourceId.size()];
                int i = 0;
                for (final Long gid : groupResourceId) {
                    groupIdArray[i++] = gid;
                }
                Logger.getLogger("MDMEnrollment").log(Level.INFO, "Removing device from groups on deprovision! Member Resource id: {0}Group Resource id: {1}", new Object[] { memberResourceId, groupResourceId.toString() });
                MDMGroupHandler.getInstance().removeMemberfromGroups(memberResourceId, groupIdArray, userId);
            }
        }
        catch (final Exception exp) {
            this.logger.log(Level.SEVERE, "Exception while removing devices from groups!", exp);
        }
    }
    
    public Map getCustomDeviceNameMapFromDB(final Long[] resourceIds) {
        final Map<Long, String> customDeviceNameMap = new HashMap<Long, String>();
        try {
            final Criteria resIdCriteria = new Criteria(Column.getColumn("ManagedDeviceExtn", "MANAGED_DEVICE_ID"), (Object)resourceIds, 8);
            final DataObject availableDO = MDMUtil.getPersistence().get("ManagedDeviceExtn", resIdCriteria);
            final Iterator doIterator = availableDO.getRows("ManagedDeviceExtn");
            while (doIterator.hasNext()) {
                final Row r = doIterator.next();
                customDeviceNameMap.put((Long)r.get("MANAGED_DEVICE_ID"), (String)r.get("NAME"));
            }
            this.logger.log(Level.INFO, "customDeviceNameMap obtained from db : {0}", customDeviceNameMap);
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Exception occurred in getCustomDeviceNameListFromDB :", e);
        }
        return customDeviceNameMap;
    }
    
    public SelectQuery getManagedDeviceSelectQuery(final List resourceIdList) {
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("ManagedDevice"));
        selectQuery.addJoin(new Join("ManagedDevice", "MdDeviceInfo", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2));
        selectQuery.addJoin(new Join("ManagedDevice", "ManagedDeviceExtn", new String[] { "RESOURCE_ID" }, new String[] { "MANAGED_DEVICE_ID" }, 2));
        selectQuery.addSelectColumn(Column.getColumn("ManagedDevice", "RESOURCE_ID"));
        selectQuery.addSelectColumn(Column.getColumn("ManagedDevice", "UDID"));
        selectQuery.addSelectColumn(Column.getColumn("ManagedDevice", "MANAGED_STATUS"));
        selectQuery.addSelectColumn(Column.getColumn("ManagedDevice", "REMARKS"));
        selectQuery.addSelectColumn(Column.getColumn("ManagedDevice", "UNREGISTERED_TIME"));
        selectQuery.addSelectColumn(Column.getColumn("ManagedDevice", "PLATFORM_TYPE"));
        selectQuery.addSelectColumn(Column.getColumn("MdDeviceInfo", "RESOURCE_ID"));
        selectQuery.addSelectColumn(Column.getColumn("MdDeviceInfo", "SERIAL_NUMBER"));
        selectQuery.addSelectColumn(Column.getColumn("MdDeviceInfo", "IS_PROFILEOWNER"));
        selectQuery.addSelectColumn(Column.getColumn("ManagedDeviceExtn", "MANAGED_DEVICE_ID"));
        selectQuery.addSelectColumn(Column.getColumn("ManagedDeviceExtn", "NAME"));
        selectQuery.setCriteria(new Criteria(Column.getColumn("ManagedDevice", "RESOURCE_ID"), (Object)resourceIdList.toArray(), 8));
        return selectQuery;
    }
    
    public Long getResourceIdForDeprovision(final String slno, final String imei, final String udid) throws SyMException {
        try {
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("ManagedDevice"));
            selectQuery.addJoin(new Join("ManagedDevice", "MdDeviceInfo", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2));
            selectQuery.addSelectColumn(Column.getColumn("ManagedDevice", "RESOURCE_ID"));
            selectQuery.addSelectColumn(Column.getColumn("ManagedDevice", "MANAGED_STATUS"));
            selectQuery.addSelectColumn(Column.getColumn("MdDeviceInfo", "RESOURCE_ID"));
            Criteria criteria = null;
            if (this.isStringEmpty(slno) && this.isStringEmpty(imei) && this.isStringEmpty(udid)) {
                throw new SyMException(51031, "Device is not enrolled with MDM!!", "mdm.api.error.deprovision_failed_not_enrolled_device", (Throwable)null);
            }
            if (slno != null) {
                final Criteria slnoCri = new Criteria(Column.getColumn("MdDeviceInfo", "SERIAL_NUMBER"), (Object)slno, 0);
                criteria = ((criteria == null) ? slnoCri : criteria.and(slnoCri));
            }
            if (imei != null) {
                final Criteria imeiCri = new Criteria(Column.getColumn("MdDeviceInfo", "IMEI"), (Object)imei, 0);
                criteria = ((criteria == null) ? imeiCri : criteria.and(imeiCri));
            }
            if (udid != null) {
                final Criteria udidCri = new Criteria(Column.getColumn("ManagedDevice", "UDID"), (Object)udid, 0);
                criteria = ((criteria == null) ? udidCri : criteria.and(udidCri));
            }
            selectQuery.setCriteria(criteria);
            final DataObject dataObject = MDMUtil.getPersistenceLite().get(selectQuery);
            if (dataObject.isEmpty()) {
                throw new SyMException(51031, "Device is not enrolled with MDM!!", "mdm.api.error.deprovision_failed_not_enrolled_device", (Throwable)null);
            }
            if (dataObject.size("ManagedDevice") > 1) {
                throw new SyMException(51032, "More than 1 device is present for the given details!", "mdm.deprovision.error.more_than_one", (Throwable)null);
            }
            if (dataObject.isEmpty()) {
                throw new SyMException();
            }
            final Row notenrolledrow = dataObject.getRow("ManagedDevice", new Criteria(Column.getColumn("ManagedDevice", "MANAGED_STATUS"), (Object)2, 1));
            if (notenrolledrow != null) {
                throw new SyMException(51033, "Device is not in enrolled state!", "mdm.deprovision.error.not_in_enrolled_state", (Throwable)null);
            }
            final Row row = dataObject.getFirstRow("ManagedDevice");
            return (Long)row.get("RESOURCE_ID");
        }
        catch (final DataAccessException e) {
            this.logger.log(Level.SEVERE, "Exception while getting resource id!", (Throwable)e);
            return -1L;
        }
    }
    
    public void updateManagedStatusToEnrolled(final List<Long> resoureId) throws DataAccessException {
        final UpdateQuery updateQuery = (UpdateQuery)new UpdateQueryImpl("ManagedDevice");
        updateQuery.setCriteria(new Criteria(Column.getColumn("ManagedDevice", "RESOURCE_ID"), (Object)resoureId.toArray(), 8));
        updateQuery.setUpdateColumn("UNREGISTERED_TIME", (Object)(-1L));
        updateQuery.setUpdateColumn("MANAGED_STATUS", (Object)2);
        updateQuery.setUpdateColumn("REMARKS", (Object)"dc.mdm.db.agent.enroll.agent_enroll_finished");
        MDMUtil.getPersistenceLite().update(updateQuery);
    }
    
    public boolean isStringEmpty(String str) {
        if (str != null) {
            str = str.trim();
            return str.isEmpty() || str.equalsIgnoreCase("--") || str.equalsIgnoreCase("-") || str.equalsIgnoreCase("null");
        }
        return true;
    }
    
    public Boolean isDeviceProvisioningUser() {
        try {
            Boolean isProvisioningUser = MDMFeatureParamsHandler.getInstance().isFeatureEnabled("ENABLE_PROVISIONING_USER");
            if (isProvisioningUser) {
                final Boolean isUserInWriteRole = DMUserHandler.isUserInRole(ApiFactoryProvider.getAuthUtilAccessAPI().getLoginID(), "MDM_Enrollment_Write") || DMUserHandler.isUserInRole(ApiFactoryProvider.getAuthUtilAccessAPI().getLoginID(), "ModernMgmt_Enrollment_Write");
                final Boolean isUserInAdminRole = DMUserHandler.isUserInRole(ApiFactoryProvider.getAuthUtilAccessAPI().getLoginID(), "MDM_Enrollment_Admin") || DMUserHandler.isUserInRole(ApiFactoryProvider.getAuthUtilAccessAPI().getLoginID(), "ModernMgmt_Enrollment_Admin");
                if (!isUserInAdminRole && isUserInWriteRole) {
                    isProvisioningUser = true;
                }
                else {
                    isProvisioningUser = false;
                }
            }
            return isProvisioningUser;
        }
        catch (final Exception exp) {
            this.logger.log(Level.SEVERE, "Exception in isDeviceProvisioningUser: ", exp);
            return false;
        }
    }
    
    public void updateRemoveDEPProfileOnRemove(final Long[] resourceID) throws Exception {
        final SelectQuery sQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("DeprovisionHistory"));
        sQuery.addSelectColumn(Column.getColumn("DeprovisionHistory", "DEPROVISION_ID"));
        sQuery.addSelectColumn(Column.getColumn("DeprovisionHistory", "RESOURCE_ID"));
        sQuery.addSelectColumn(Column.getColumn("DeprovisionHistory", "DEP_PROFILE_REMOVED"));
        sQuery.setCriteria(new Criteria(Column.getColumn("DeprovisionHistory", "RESOURCE_ID"), (Object)resourceID, 8));
        final DataObject DO = MDMUtil.getPersistence().get(sQuery);
        final Iterator iterator = DO.getRows("DeprovisionHistory");
        while (iterator.hasNext()) {
            final Row row = iterator.next();
            row.set("DEP_PROFILE_REMOVED", (Object)false);
            DO.updateRow(row);
        }
        MDMUtil.getPersistenceLite().update(DO);
    }
    
    public void reAssignDepProfile(final Long resourceId, final Long customerId) throws Exception {
        final SelectQuery sQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("DeprovisionHistory"));
        sQuery.addSelectColumn(Column.getColumn("DeprovisionHistory", "DEPROVISION_ID"));
        sQuery.addSelectColumn(Column.getColumn("DeprovisionHistory", "RESOURCE_ID"));
        sQuery.addSelectColumn(Column.getColumn("DeprovisionHistory", "DEP_PROFILE_REMOVED"));
        sQuery.setCriteria(new Criteria(Column.getColumn("DeprovisionHistory", "RESOURCE_ID"), (Object)resourceId, 0));
        final DataObject DO = MDMUtil.getPersistence().get(sQuery);
        if (!DO.isEmpty()) {
            final Iterator itr = DO.getRows("DeprovisionHistory");
            while (itr.hasNext()) {
                final Row row = itr.next();
                row.set("DEP_PROFILE_REMOVED", (Object)false);
                DO.updateRow(row);
            }
            MDMUtil.getPersistenceLite().update(DO);
        }
        final Properties properties = new Properties();
        ((Hashtable<String, Long>)properties).put("RESOURCE_ID", resourceId);
        ((Hashtable<String, Integer>)properties).put("MANAGED_STATUS", 10);
        ((Hashtable<String, String>)properties).put("REMARKS", "mdm.deprovision.assign_user_to_enroll");
        ((Hashtable<String, Integer>)properties).put("PLATFORM_TYPE", 1);
        getInstance().updateManagedDeviceDetails(properties);
        DEPEnrollmentUtil.syncDEPTokensForCustomer(customerId);
    }
    
    public List<String> getDomainList(final Long customerID) {
        final List<String> domainList = new ArrayList<String>();
        try {
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("DMDomain"));
            selectQuery.setCriteria(new Criteria(Column.getColumn("DMDomain", "CUSTOMER_ID"), (Object)customerID, 0));
            selectQuery.addSelectColumn(Column.getColumn("DMDomain", "DOMAIN_ID"));
            selectQuery.addSelectColumn(Column.getColumn("DMDomain", "NAME"));
            final DataObject dataObject = MDMUtil.getPersistenceLite().get(selectQuery);
            if (!dataObject.isEmpty()) {
                final Iterator itr = dataObject.getRows("DMDomain");
                while (itr.hasNext()) {
                    final Row row = itr.next();
                    domainList.add((String)row.get("NAME"));
                }
            }
        }
        catch (final Exception exp) {
            this.logger.log(Level.SEVERE, "Exception in getting domains", exp);
        }
        return domainList;
    }
    
    public JSONObject getManagedDeviceDetailsForSelfEnrollMail(final Long managedDeviceID) throws Exception {
        final JSONObject jsonObject = new JSONObject();
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("ManagedDevice"));
        selectQuery.addJoin(new Join("ManagedDevice", "MdDeviceInfo", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 1));
        selectQuery.addJoin(new Join("ManagedDevice", "ManagedDeviceExtn", new String[] { "RESOURCE_ID" }, new String[] { "MANAGED_DEVICE_ID" }, 1));
        selectQuery.addJoin(new Join("ManagedDevice", "ManagedUserToDevice", new String[] { "RESOURCE_ID" }, new String[] { "MANAGED_DEVICE_ID" }, 1));
        selectQuery.addJoin(new Join("ManagedUserToDevice", "ManagedUser", new String[] { "MANAGED_USER_ID" }, new String[] { "MANAGED_USER_ID" }, 1));
        selectQuery.addJoin(new Join("ManagedUser", "Resource", new String[] { "MANAGED_USER_ID" }, new String[] { "RESOURCE_ID" }, 1));
        final Criteria userNotInTrashCriteria = new Criteria(Column.getColumn("ManagedUser", "STATUS"), (Object)11, 1).or(new Criteria(Column.getColumn("ManagedUser", "STATUS"), (Object)null, 0));
        selectQuery.setCriteria(new Criteria(Column.getColumn("ManagedDevice", "RESOURCE_ID"), (Object)managedDeviceID, 0).and(userNotInTrashCriteria));
        selectQuery.addSelectColumn(Column.getColumn("ManagedDevice", "RESOURCE_ID"));
        selectQuery.addSelectColumn(Column.getColumn("ManagedDevice", "PLATFORM_TYPE"));
        selectQuery.addSelectColumn(Column.getColumn("ManagedDevice", "UDID"));
        selectQuery.addSelectColumn(Column.getColumn("MdDeviceInfo", "RESOURCE_ID", "MDDEVICEINFO.RESOURCE_ID"));
        selectQuery.addSelectColumn(Column.getColumn("MdDeviceInfo", "SERIAL_NUMBER"));
        selectQuery.addSelectColumn(Column.getColumn("MdDeviceInfo", "IMEI"));
        selectQuery.addSelectColumn(Column.getColumn("ManagedDeviceExtn", "MANAGED_DEVICE_ID"));
        selectQuery.addSelectColumn(Column.getColumn("ManagedDeviceExtn", "NAME"));
        selectQuery.addSelectColumn(Column.getColumn("ManagedUser", "MANAGED_USER_ID"));
        selectQuery.addSelectColumn(Column.getColumn("ManagedUserToDevice", "MANAGED_USER_ID", "MANAGEDUSERTODEVICE.MANAGED_USER_ID"));
        selectQuery.addSelectColumn(Column.getColumn("ManagedUserToDevice", "MANAGED_DEVICE_ID", "MANAGEDUSERTODEVICE.MANAGED_DEVICE_ID"));
        selectQuery.addSelectColumn(Column.getColumn("Resource", "NAME", "RESOURCE.NAME"));
        selectQuery.addSelectColumn(Column.getColumn("Resource", "RESOURCE_ID", "RESOURCE.RESOURCE_ID"));
        selectQuery.addSelectColumn(Column.getColumn("Resource", "CUSTOMER_ID"));
        DMDataSetWrapper ds = null;
        ds = DMDataSetWrapper.executeQuery((Object)selectQuery);
        if (ds.next()) {
            jsonObject.put("UDID", ds.getValue("UDID"));
            jsonObject.put("PLATFORM_TYPE", (Object)MDMEnrollmentUtil.getPlatformString((int)ds.getValue("PLATFORM_TYPE")));
            String slNo = (String)ds.getValue("SERIAL_NUMBER");
            if (MDMUtil.isStringEmpty(slNo)) {
                slNo = "--";
            }
            String imei = (String)ds.getValue("IMEI");
            if (MDMUtil.isStringEmpty(imei)) {
                imei = "--";
            }
            jsonObject.put("SERIAL_NUMBER", (Object)slNo);
            jsonObject.put("IMEI", (Object)imei);
            jsonObject.put("NAME", ds.getValue("NAME"));
            jsonObject.put("DISPLAY_NAME", ds.getValue("RESOURCE.NAME"));
            jsonObject.put("CUSTOMER_ID", ds.getValue("CUSTOMER_ID"));
        }
        return jsonObject;
    }
    
    private JSONObject checkifDeviceEnrolledWithoutRequestAndgetDetails(final JSONObject json) {
        final JSONObject jsonObject = new JSONObject();
        boolean isDeviceEnrolledWithoutRequest = false;
        try {
            final String udid = String.valueOf(json.get("UDID"));
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("ManagedDevice"));
            selectQuery.addJoin(new Join("ManagedDevice", "EnrollmentRequestToDevice", new String[] { "RESOURCE_ID" }, new String[] { "MANAGED_DEVICE_ID" }, 1));
            selectQuery.addJoin(new Join("ManagedDevice", "Resource", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2));
            selectQuery.setCriteria(new Criteria(Column.getColumn("ManagedDevice", "UDID"), (Object)udid, 0).and(new Criteria(Column.getColumn("ManagedDevice", "MANAGED_STATUS"), (Object)new int[] { 2, 5 }, 8)));
            selectQuery.addSelectColumn(Column.getColumn("ManagedDevice", "UDID"));
            selectQuery.addSelectColumn(Column.getColumn("ManagedDevice", "RESOURCE_ID"));
            selectQuery.addSelectColumn(Column.getColumn("ManagedDevice", "ADDED_TIME"));
            selectQuery.addSelectColumn(Column.getColumn("ManagedDevice", "PLATFORM_TYPE"));
            selectQuery.addSelectColumn(Column.getColumn("ManagedDevice", "OWNED_BY"));
            selectQuery.addSelectColumn(Column.getColumn("ManagedDevice", "UDID"));
            selectQuery.addSelectColumn(Column.getColumn("EnrollmentRequestToDevice", "MANAGED_DEVICE_ID"));
            selectQuery.addSelectColumn(Column.getColumn("EnrollmentRequestToDevice", "ENROLLMENT_REQUEST_ID"));
            selectQuery.addSelectColumn(Column.getColumn("Resource", "RESOURCE_ID", "RESOURCE.RESOURCE_ID"));
            selectQuery.addSelectColumn(Column.getColumn("Resource", "CUSTOMER_ID"));
            DMDataSetWrapper ds = null;
            ds = DMDataSetWrapper.executeQuery((Object)selectQuery);
            if (ds.next()) {
                final Long resourceID = (Long)ds.getValue("RESOURCE_ID");
                final Long requestId = (Long)ds.getValue("ENROLLMENT_REQUEST_ID");
                if (resourceID != null && requestId == null) {
                    isDeviceEnrolledWithoutRequest = true;
                }
                jsonObject.put("RESOURCE_ID", (Object)resourceID);
                jsonObject.put("CUSTOMER_ID", (Object)ds.getValue("CUSTOMER_ID"));
                jsonObject.put("PLATFORM_TYPE", (Object)ds.getValue("PLATFORM_TYPE"));
                jsonObject.put("OWNED_BY", (Object)ds.getValue("OWNED_BY"));
                jsonObject.put("ADDED_TIME", (Object)ds.getValue("ADDED_TIME"));
                jsonObject.put("UDID", (Object)ds.getValue("UDID"));
            }
            jsonObject.put("isDeviceEnrolledWithoutRequest", isDeviceEnrolledWithoutRequest);
        }
        catch (final Exception exp) {
            this.enrollmentLogger.log(Level.SEVERE, "Exception while checking if device is enrolled without a request: ", exp);
        }
        return jsonObject;
    }
    
    public void checkAndAssignDeviceToAdminForDevicesWithoutRequest(final JSONObject json) {
        try {
            final JSONObject deviceDetailsJsonObject = this.checkifDeviceEnrolledWithoutRequestAndgetDetails(json);
            final boolean isDeviceEnrolledWithoutRequest = deviceDetailsJsonObject.getBoolean("isDeviceEnrolledWithoutRequest");
            if (isDeviceEnrolledWithoutRequest) {
                final String deviceToken = json.optString("encapiKey");
                final String udidOfDevice = String.valueOf(json.get("UDID"));
                final Properties enrollmentRequestProperties = this.getCreateEnrollmentRequestPropsForAdminUser(deviceDetailsJsonObject);
                if (!enrollmentRequestProperties.isEmpty()) {
                    final Long resourceId = ((Hashtable<K, Long>)enrollmentRequestProperties).get("RESOURCE_ID");
                    final Long enrollmentRequestId = MDMEnrollmentRequestHandler.getInstance().addOrUpdateEnrollmentRequest(enrollmentRequestProperties);
                    final Long managedUserId = ((Hashtable<K, Long>)enrollmentRequestProperties).get("MANAGED_USER_ID");
                    MDMDeviceTokenGenerator.getInstance().addOrUpdateDeviceToken(enrollmentRequestId, deviceToken);
                    MDMEnrollmentDeviceHandler.getInstance(1).addOrUpdateEnrollmentRequestToDevice(resourceId, enrollmentRequestId);
                    MDMEnrollmentDeviceHandler.getInstance(1).addOrUpdateManagedUserToDeviceRel(resourceId, managedUserId);
                    this.enrollmentLogger.log(Level.INFO, "*** DEVICE WITHOUT REQUEST FOUND.. ASSIGNED TO ADMIN.. MANAGED_USER_ID:{0} UDID :{1} ***", new Object[] { String.valueOf(managedUserId), udidOfDevice });
                    this.updateRemarksForDevice(resourceId, "mdm.enroll.device_recovered_remarks");
                    if (deviceDetailsJsonObject.getInt("PLATFORM_TYPE") == 2) {
                        DeviceCommandRepository.getInstance().addSecurityCommand(resourceId, "UpdateUserInfo");
                    }
                }
            }
        }
        catch (final Exception exp) {
            this.enrollmentLogger.log(Level.SEVERE, "Exception while adding enrollment request.. ", exp);
        }
    }
    
    private Properties getCreateEnrollmentRequestPropsForAdminUser(final JSONObject jsonObject) throws Exception {
        final Properties properties = new Properties();
        final JSONObject userIdJson = ManagedUserHandler.getInstance().getManagedUserIdAndAAAUserIdForAdmin(Long.valueOf(String.valueOf(jsonObject.get("CUSTOMER_ID"))), Boolean.FALSE);
        if (userIdJson.length() != 0) {
            final Long managedUserID = userIdJson.getLong("MANAGED_USER_ID");
            final Long loggedInUserID = userIdJson.getLong("USER_ID");
            final Integer ownedBy = (jsonObject.getInt("OWNED_BY") == 2) ? 2 : 1;
            ((Hashtable<String, Long>)properties).put("MANAGED_USER_ID", managedUserID);
            ((Hashtable<String, Integer>)properties).put("OWNED_BY", ownedBy);
            ((Hashtable<String, Integer>)properties).put("PLATFORM_TYPE", jsonObject.getInt("PLATFORM_TYPE"));
            ((Hashtable<String, Long>)properties).put("REQUESTED_TIME", Long.valueOf(String.valueOf(jsonObject.get("ADDED_TIME"))));
            ((Hashtable<String, Integer>)properties).put("REQUEST_STATUS", 3);
            ((Hashtable<String, Integer>)properties).put("ENROLLMENT_TYPE", 1);
            ((Hashtable<String, Boolean>)properties).put("IS_SELF_ENROLLMENT", false);
            ((Hashtable<String, String>)properties).put("UDID", String.valueOf(jsonObject.get("UDID")));
            ((Hashtable<String, Boolean>)properties).put("byAdmin", true);
            ((Hashtable<String, String>)properties).put("REMARKS", "dc.mdm.db.agent.enroll.agent_enroll_finished");
            ((Hashtable<String, Long>)properties).put("RESOURCE_ID", jsonObject.getLong("RESOURCE_ID"));
            ((Hashtable<String, Long>)properties).put("USER_ID", loggedInUserID);
            ((Hashtable<String, Integer>)properties).put("AUTH_MODE", 1);
            ((Hashtable<String, Long>)properties).put("CUSTOMER_ID", userIdJson.getLong("CUSTOMER_ID"));
        }
        return properties;
    }
    
    private void updateRemarksForDevice(final Long resourceID, final String remarks) throws Exception {
        final UpdateQuery updateQuery = (UpdateQuery)new UpdateQueryImpl("ManagedDevice");
        updateQuery.setUpdateColumn("REMARKS", (Object)remarks);
        updateQuery.setCriteria(new Criteria(Column.getColumn("ManagedDevice", "RESOURCE_ID"), (Object)resourceID, 0));
        MDMUtil.getPersistenceLite().update(updateQuery);
    }
    
    private ArrayList getDeviceResourceIDsWithDeviceInfo(final Criteria criteria) {
        final ArrayList enrolledResourceIDs = new ArrayList();
        try {
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("ManagedDevice"));
            final Join managedDeviceJoin = new Join("ManagedDevice", "Resource", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2);
            selectQuery.addJoin(managedDeviceJoin);
            selectQuery.addJoin(new Join("ManagedDevice", "MdDeviceInfo", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2));
            selectQuery.addSelectColumn(Column.getColumn("ManagedDevice", "RESOURCE_ID"));
            selectQuery.setCriteria(criteria);
            final DataObject dataObject = MDMUtil.getPersistence().get(selectQuery);
            if (!dataObject.isEmpty()) {
                final Iterator iterator = dataObject.getRows("ManagedDevice");
                while (iterator.hasNext()) {
                    final Row managedDeviceRow = iterator.next();
                    enrolledResourceIDs.add(managedDeviceRow.get("RESOURCE_ID"));
                }
            }
        }
        catch (final Exception ex) {
            this.logger.log(Level.WARNING, "Exception in getDeviceResourceIDsWithDeviceInfo method : {0}", ex);
        }
        return enrolledResourceIDs;
    }
    
    private ArrayList getManagedDeviceResourceIDsWithDeviceInfo(final Criteria criteria) {
        Criteria mdCriteria = this.getSuccessfullyEnrolledCriteria();
        if (criteria != null) {
            mdCriteria = mdCriteria.and(criteria);
        }
        return this.getDeviceResourceIDsWithDeviceInfo(mdCriteria);
    }
    
    public void updateDeviceForEnrollmentDetails(final Long resourceID, final String serialNumber, String imei) {
        try {
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("DeviceForEnrollment"));
            selectQuery.addJoin(new Join("DeviceForEnrollment", "DeviceEnrollmentToRequest", new String[] { "ENROLLMENT_DEVICE_ID" }, new String[] { "ENROLLMENT_DEVICE_ID" }, 2));
            selectQuery.addJoin(new Join("DeviceEnrollmentToRequest", "DeviceEnrollmentRequest", new String[] { "ENROLLMENT_REQUEST_ID" }, new String[] { "ENROLLMENT_REQUEST_ID" }, 2));
            selectQuery.addJoin(new Join("DeviceEnrollmentRequest", "EnrollmentRequestToDevice", new String[] { "ENROLLMENT_REQUEST_ID" }, new String[] { "ENROLLMENT_REQUEST_ID" }, 2));
            final Criteria resIdCri = new Criteria(Column.getColumn("EnrollmentRequestToDevice", "MANAGED_DEVICE_ID"), (Object)resourceID, 0);
            final Criteria emptySerialNoCri = new Criteria(Column.getColumn("DeviceForEnrollment", "SERIAL_NUMBER"), (Object)null, 0).or(new Criteria(Column.getColumn("DeviceForEnrollment", "SERIAL_NUMBER"), (Object)"", 0, false));
            final Criteria imeiCri = new Criteria(Column.getColumn("DeviceForEnrollment", "IMEI"), (Object)null, 0).or(new Criteria(Column.getColumn("DeviceForEnrollment", "IMEI"), (Object)"", 0, false));
            selectQuery.setCriteria(resIdCri.and(emptySerialNoCri.or(imeiCri)));
            selectQuery.addSelectColumn(Column.getColumn("DeviceForEnrollment", "ENROLLMENT_DEVICE_ID"));
            selectQuery.addSelectColumn(Column.getColumn("DeviceForEnrollment", "IMEI"));
            selectQuery.addSelectColumn(Column.getColumn("DeviceForEnrollment", "SERIAL_NUMBER"));
            selectQuery.addSelectColumn(Column.getColumn("EnrollmentRequestToDevice", "MANAGED_DEVICE_ID"));
            selectQuery.addSelectColumn(Column.getColumn("EnrollmentRequestToDevice", "ENROLLMENT_REQUEST_ID"));
            selectQuery.addSelectColumn(Column.getColumn("DeviceEnrollmentRequest", "ENROLLMENT_REQUEST_ID", "DEVICEENROLLMENTREQUEST.ENROLLMENT_REQUEST_ID"));
            selectQuery.addSelectColumn(Column.getColumn("DeviceEnrollmentToRequest", "ENROLLMENT_REQUEST_ID", "DEVICEENROLLMENTTOREQUEST.ENROLLMENT_REQUEST_ID"));
            selectQuery.addSelectColumn(Column.getColumn("DeviceEnrollmentToRequest", "ENROLLMENT_DEVICE_ID", "DEVICEENROLLMENTTOREQUEST.ENROLLMENT_DEVICE_ID"));
            final DataObject dataObject = MDMUtil.getPersistenceLite().get(selectQuery);
            if (dataObject != null && !dataObject.isEmpty()) {
                final Row row = dataObject.getFirstRow("DeviceForEnrollment");
                row.set("SERIAL_NUMBER", (Object)serialNumber);
                if (!MDMStringUtils.isEmpty(imei)) {
                    imei = imei.replace(" ", "");
                }
                row.set("IMEI", (Object)imei);
                dataObject.updateRow(row);
                MDMUtil.getPersistenceLite().update(dataObject);
            }
        }
        catch (final Exception exp) {
            this.logger.log(Level.SEVERE, "Exception while updating device for enrollment table.. ", exp);
        }
    }
    
    public void addReasonForCommand(final Long resourceId, final int commandReason, final String commandReasonI18, final String commandReasonText, final Long commandId) {
        try {
            this.logger.log(Level.INFO, "Adding wipe command to {0} due to reason {1}", new Object[] { String.valueOf(resourceId), String.valueOf(commandReason) });
            final SelectQuery reasonForWipeQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("ReasonForCommand"));
            reasonForWipeQuery.addSelectColumn(Column.getColumn("ReasonForCommand", "*"));
            reasonForWipeQuery.setCriteria(new Criteria(Column.getColumn("ReasonForCommand", "RESOURCE_ID"), (Object)resourceId, 0));
            final DataObject dObj = DataAccess.get(reasonForWipeQuery);
            Row resourceRow = null;
            if (!dObj.isEmpty()) {
                resourceRow = dObj.getFirstRow("ReasonForCommand");
            }
            if (resourceRow == null) {
                resourceRow = new Row("ReasonForCommand");
                resourceRow.set("RESOURCE_ID", (Object)resourceId);
            }
            resourceRow.set("REASON", (Object)commandReason);
            resourceRow.set("REASON_I18", (Object)commandReasonI18);
            resourceRow.set("REASON_I18_FALLBACK", (Object)commandReasonText);
            resourceRow.set("COMMAND_ID", (Object)commandId);
            if (dObj.isEmpty()) {
                dObj.addRow(resourceRow);
            }
            else {
                dObj.updateRow(resourceRow);
            }
            DataAccess.update(dObj);
        }
        catch (final Exception exp) {
            this.logger.log(Level.SEVERE, " Cannot write the reason for wiping the device ", exp);
        }
    }
    
    public int getReasonFoCommandCode(final Long resId) {
        int reason = 0;
        try {
            final SelectQuery reasonForWipeQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("ReasonForCommand"));
            reasonForWipeQuery.addSelectColumn(Column.getColumn("ReasonForCommand", "RESOURCE_ID"));
            reasonForWipeQuery.addSelectColumn(Column.getColumn("ReasonForCommand", "REASON"));
            reasonForWipeQuery.setCriteria(new Criteria(Column.getColumn("ReasonForCommand", "RESOURCE_ID"), (Object)resId, 0));
            final DataObject dObj = DataAccess.get(reasonForWipeQuery);
            reason = (int)dObj.getFirstRow("ReasonForCommand").get("REASON");
        }
        catch (final DataAccessException exp) {
            this.logger.log(Level.INFO, " Exception while getting the reason for wipe ", (Throwable)exp);
            reason = 0;
        }
        return reason;
    }
    
    public int getReasonFoCommandCode(final String udid) {
        return this.getReasonFoCommandCode(this.getResourceIDFromUDID(udid));
    }
    
    public JSONObject getCommandReasonJson(final Long resId, final Long cmdId) {
        final JSONObject reason = new JSONObject();
        try {
            final SelectQuery reasonForWipeQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("ReasonForCommand"));
            reasonForWipeQuery.addSelectColumn(new Column((String)null, "*"));
            reasonForWipeQuery.setCriteria(new Criteria(Column.getColumn("ReasonForCommand", "RESOURCE_ID"), (Object)resId, 0));
            reasonForWipeQuery.setCriteria(new Criteria(Column.getColumn("ReasonForCommand", "COMMAND_ID"), (Object)cmdId, 0));
            final DataObject wipeObj = DataAccess.get(reasonForWipeQuery);
            if (!wipeObj.isEmpty()) {
                final Row wipeRow = wipeObj.getFirstRow("ReasonForCommand");
                if (wipeRow != null) {
                    reason.put("WipeReason", (int)wipeRow.get("REASON"));
                    reason.put("WipeReasonI18Key", (Object)wipeRow.get("REASON_I18"));
                    reason.put("WipeReasonFallback", (Object)wipeRow.get("REASON_I18_FALLBACK"));
                }
            }
            else {
                this.logger.log(Level.INFO, " Wipe reason not set for resource ID ", resId);
            }
        }
        catch (final DataAccessException exp) {
            this.logger.log(Level.INFO, " Exception while getting the reason for wipe ", (Throwable)exp);
        }
        catch (final JSONException exp2) {
            this.logger.log(Level.INFO, " Exception while forming the json for wipe reason", (Throwable)exp2);
        }
        return reason;
    }
    
    public JSONObject getCommandReasonJson(final String udid, final Long cmdId) {
        return this.getCommandReasonJson(this.getResourceIDFromUDID(udid), cmdId);
    }
    
    public HashMap getManagedDeviceDetailsFromResourceID(final Long resourceId) {
        final HashMap deviceDetails = new HashMap();
        try {
            final SelectQuery sQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("ManagedDevice"));
            sQuery.addSelectColumn(Column.getColumn("ManagedDevice", "*"));
            final Criteria safeCriteria = new Criteria(Column.getColumn("ManagedDevice", "RESOURCE_ID"), (Object)resourceId, 0);
            sQuery.setCriteria(safeCriteria);
            final DataObject dataObject = MDMUtil.getPersistence().get(sQuery);
            if (!dataObject.isEmpty()) {
                final Row row = dataObject.getRow("ManagedDevice");
                deviceDetails.put("AGENT_VERSION", row.get("AGENT_VERSION"));
                deviceDetails.put("AGENT_TYPE", row.get("AGENT_TYPE"));
                deviceDetails.put("AGENT_VERSION_CODE", row.get("AGENT_VERSION_CODE"));
                deviceDetails.put("MANAGED_STATUS", row.get("MANAGED_STATUS"));
                deviceDetails.put("ADDED_TIME", row.get("ADDED_TIME"));
                deviceDetails.put("NOTIFIED_AGENT_VERSION", row.get("NOTIFIED_AGENT_VERSION"));
                deviceDetails.put("OWNED_BY", row.get("OWNED_BY"));
                deviceDetails.put("UDID", row.get("UDID"));
                deviceDetails.put("REGISTERED_TIME", row.get("REGISTERED_TIME"));
                deviceDetails.put("UNREGISTERED_TIME", row.get("UNREGISTERED_TIME"));
                deviceDetails.put("REMARKS", row.get("REMARKS"));
                deviceDetails.put("PLATFORM_TYPE", row.get("PLATFORM_TYPE"));
            }
        }
        catch (final Exception exception) {
            this.logger.log(Level.SEVERE, "Exception while getting ManagedDevice Details from resource id ", exception);
        }
        return deviceDetails;
    }
    
    public static HashMap getUniquePropsFromResource(final List resourceIDs) throws Exception {
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("ManagedDevice"));
        selectQuery.addJoin(new Join("ManagedDevice", "MdDeviceInfo", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2));
        selectQuery.addJoin(new Join("ManagedDevice", "MdNetworkInfo", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 1));
        selectQuery.addSelectColumn(Column.getColumn("MdDeviceInfo", "RESOURCE_ID"));
        selectQuery.addSelectColumn(Column.getColumn("MdDeviceInfo", "SERIAL_NUMBER"));
        selectQuery.addSelectColumn(Column.getColumn("MdNetworkInfo", "RESOURCE_ID"));
        selectQuery.addSelectColumn(Column.getColumn("MdNetworkInfo", "WIFI_MAC"));
        selectQuery.addSelectColumn(Column.getColumn("MdNetworkInfo", "ETHERNET_MACS"));
        selectQuery.addSelectColumn(Column.getColumn("ManagedDevice", "RESOURCE_ID"));
        final Criteria resCriteria = new Criteria(Column.getColumn("ManagedDevice", "RESOURCE_ID"), (Object)resourceIDs.toArray(), 8);
        final Criteria managedCriteria = new Criteria(Column.getColumn("ManagedDevice", "MANAGED_STATUS"), (Object)2, 0);
        selectQuery.setCriteria(resCriteria.and(managedCriteria));
        final DataObject dataObject = MDMUtil.getPersistenceLite().get(selectQuery);
        final HashMap response = new HashMap();
        final Iterator iterator = dataObject.getRows("ManagedDevice");
        while (iterator.hasNext()) {
            final JSONObject deviceProps = new JSONObject();
            final Row row = iterator.next();
            final Long resID = (Long)row.get("RESOURCE_ID");
            if (row != null) {
                final Row infoRow = dataObject.getRow("MdDeviceInfo", new Criteria(Column.getColumn("MdDeviceInfo", "RESOURCE_ID"), (Object)resID, 0));
                final Row netRow = dataObject.getRow("MdNetworkInfo", new Criteria(Column.getColumn("MdNetworkInfo", "RESOURCE_ID"), (Object)resID, 0));
                if (infoRow != null) {
                    deviceProps.put("SerialNumber", infoRow.get("SERIAL_NUMBER"));
                }
                if (netRow != null) {
                    final String eMac = (String)netRow.get("ETHERNET_MACS");
                    final String wMac = (String)netRow.get("WIFI_MAC");
                    String allMacs = "";
                    String appendString = "";
                    if (!MDMStringUtils.isEmpty(eMac)) {
                        allMacs += eMac;
                        appendString = ",";
                    }
                    if (!MDMStringUtils.isEmpty(wMac)) {
                        allMacs = allMacs + appendString + wMac;
                    }
                    deviceProps.put("MacList", (Object)allMacs);
                }
                else {
                    Logger.getLogger("MDMEnrollment").log(Level.INFO, "No rows found for resID {0} in MDNETWORKINFO , so not adding WiFi,Bluetooth MAC criteria", resID);
                }
            }
            response.put(resID, deviceProps);
        }
        return response;
    }
    
    public void updateManagedDeviceStatus(final List resourceIDs, final int status) throws DataAccessException {
        final UpdateQuery updateQuery = (UpdateQuery)new UpdateQueryImpl("ManagedDevice");
        updateQuery.setCriteria(new Criteria(Column.getColumn("ManagedDevice", "RESOURCE_ID"), (Object)resourceIDs.toArray(), 8));
        updateQuery.setUpdateColumn("MANAGED_STATUS", (Object)status);
        MDMUtil.getPersistenceLite().update(updateQuery);
    }
    
    public List<Long> getProfileOwnerList(final List<Long> resourceList) {
        final List<Long> profileOwnerList = new ArrayList<Long>();
        if (resourceList == null || resourceList.isEmpty()) {
            return profileOwnerList;
        }
        try {
            final SelectQuery sQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("MdDeviceInfo"));
            final Criteria cResourceId = new Criteria(new Column("MdDeviceInfo", "RESOURCE_ID"), (Object)resourceList.toArray(), 8);
            final Criteria profileOwnerCriteria = new Criteria(new Column("MdDeviceInfo", "IS_PROFILEOWNER"), (Object)Boolean.TRUE, 0);
            sQuery.setCriteria(cResourceId.and(profileOwnerCriteria));
            sQuery.addSelectColumn(Column.getColumn("MdDeviceInfo", "RESOURCE_ID"));
            final DataObject DO = MDMUtil.getPersistence().get(sQuery);
            if (!DO.isEmpty()) {
                final Iterator iterator = DO.getRows("MdDeviceInfo");
                while (iterator.hasNext()) {
                    final Row row = iterator.next();
                    profileOwnerList.add((Long)row.get("RESOURCE_ID"));
                }
            }
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Issue in getting list of Profile Owner devices");
        }
        return profileOwnerList;
    }
    
    public void invokeDeviceListenersForBulkDeviceDetailsEdit(final DataObject updatedDO, final Long customerId) throws Exception {
        try {
            if (updatedDO != null && !updatedDO.isEmpty()) {
                final Iterator iterator = updatedDO.getRows("ManagedDeviceExtn");
                while (iterator.hasNext()) {
                    final Row managedDeviceExtnRow = iterator.next();
                    final ArrayList columnList = (ArrayList)managedDeviceExtnRow.getColumns();
                    final JSONObject deviceJSON = new JSONObject();
                    for (final Object columnName : columnList) {
                        deviceJSON.put((String)columnName, managedDeviceExtnRow.get((String)columnName));
                    }
                    deviceJSON.put("DEVICE_NAME", (Object)deviceJSON.optString("NAME"));
                    deviceJSON.remove("NAME");
                    final DeviceEvent deviceEvent = new DeviceEvent((Long)managedDeviceExtnRow.get("MANAGED_DEVICE_ID"));
                    deviceEvent.resourceJSON = deviceJSON;
                    deviceEvent.customerID = customerId;
                    getInstance().invokeDeviceListeners(deviceEvent, 7);
                }
            }
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Exception in invokeDeviceListenersForBulkDeviceDetailsEdit", e);
            throw e;
        }
    }
    
    public List getSuccessfullyEnrolledDevicesForPlatformModels(final int platform, final List modelList, final Long customerID) {
        final List resList = new ArrayList();
        try {
            final SelectQuery managedDeviceQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("ManagedDevice"));
            managedDeviceQuery.addJoin(new Join("ManagedDevice", "Resource", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2));
            managedDeviceQuery.addJoin(new Join("ManagedDevice", "MdDeviceInfo", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2));
            managedDeviceQuery.addJoin(new Join("MdDeviceInfo", "MdModelInfo", new String[] { "MODEL_ID" }, new String[] { "MODEL_ID" }, 2));
            final Criteria platformCriteria = new Criteria(Column.getColumn("ManagedDevice", "PLATFORM_TYPE"), (Object)platform, 0);
            final Criteria modelTypeCriteria = new Criteria(Column.getColumn("MdModelInfo", "MODEL_TYPE"), (Object)modelList.toArray(), 8);
            final Criteria customerCriteria = new Criteria(Column.getColumn("Resource", "CUSTOMER_ID"), (Object)customerID, 0);
            final Criteria mdCriteria = this.getSuccessfullyEnrolledCriteria();
            managedDeviceQuery.setCriteria(mdCriteria.and(platformCriteria).and(modelTypeCriteria).and(customerCriteria));
            managedDeviceQuery.addSelectColumn(Column.getColumn("ManagedDevice", "RESOURCE_ID"));
            final DataObject managedDeviceDo = MDMUtil.getPersistence().get(managedDeviceQuery);
            final Iterator deviceItr = managedDeviceDo.getRows("ManagedDevice");
            while (deviceItr.hasNext()) {
                final Row deviceInfoRow = deviceItr.next();
                final Long resID = (Long)deviceInfoRow.get("RESOURCE_ID");
                if (!resList.contains(resID)) {
                    resList.add(resID);
                }
            }
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Exception in getModelTypeToManagedDeviceMap", e);
        }
        return resList;
    }
    
    public int getPurchasedMobileDeviceCount(final Long customerId) {
        int licenseCount = -1;
        if (MDMCustomerInfoUtil.getInstance().isMSP()) {
            final String licenseCountStr = new MDMLicenseImplMSP().getNoOfMobileDevicesAllocated(customerId);
            if (!MDMStringUtils.isEmpty(licenseCountStr) && !licenseCountStr.equalsIgnoreCase("unlimited")) {
                licenseCount = Integer.parseInt(licenseCountStr);
            }
            else {
                licenseCount = -1;
            }
        }
        else {
            final String licenseCountStr = LicenseProvider.getInstance().getNoOfMobileDevicesManaged();
            if (!MDMStringUtils.isEmpty(licenseCountStr) && !licenseCountStr.equalsIgnoreCase("unlimited")) {
                licenseCount = Integer.parseInt(licenseCountStr);
            }
        }
        return licenseCount;
    }
    
    public Boolean isSupervisedOr10Above(final Long resourceId) throws Exception {
        final Boolean isSupervisedOr10Above = null;
        final Row row = DBUtil.getRowFromDB("MdDeviceInfo", "RESOURCE_ID", (Object)resourceId);
        if (row != null) {
            final String osVersion = (String)row.get("OS_VERSION");
            final boolean isSupervised = row.get("IS_SUPERVISED") != null && (boolean)row.get("IS_SUPERVISED");
            final boolean isHigher = new VersionChecker().isGreaterOrEqual(osVersion, "10");
            return isSupervised || isHigher;
        }
        return isSupervisedOr10Above;
    }
    
    public Long getEnrollmentReqIdForResourceId(final Long resourceID) throws Exception {
        this.enrollmentLogger.log(Level.INFO, "getEnrollmentReqIdForResourceId() :- for resourceID={0}", new Object[] { resourceID });
        try {
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("EnrollmentRequestToDevice"));
            selectQuery.setCriteria(new Criteria(Column.getColumn("EnrollmentRequestToDevice", "MANAGED_DEVICE_ID"), (Object)resourceID, 0));
            selectQuery.addSelectColumn(Column.getColumn("EnrollmentRequestToDevice", "*"));
            final DataObject dataObject = MDMUtil.getPersistence().get(selectQuery);
            return dataObject.isEmpty() ? null : ((Long)dataObject.getFirstRow("EnrollmentRequestToDevice").get("ENROLLMENT_REQUEST_ID"));
        }
        catch (final Exception e) {
            this.enrollmentLogger.log(Level.SEVERE, "Exception in getEnrollmentReqIdForResourceId() :-", e);
            throw e;
        }
    }
    
    public void handleDeprovisionFailure(final Long resourceId) {
        try {
            this.logger.log(Level.INFO, "Updating managed device remarks as deprovision failure for resource ID: {0}", resourceId);
            final DataObject dataObject = MDMUtil.getPersistence().get("ManagedDevice", new Criteria(Column.getColumn("ManagedDevice", "RESOURCE_ID"), (Object)resourceId, 0));
            final Row managedDeviceRow = dataObject.getRow("ManagedDevice");
            managedDeviceRow.set("REMARKS", (Object)"dc.mdm.actionlog.securitycommands.wipe_failure");
            dataObject.updateRow(managedDeviceRow);
            MDMUtil.getPersistence().update(dataObject);
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Exception while handling deprovision failure", e);
        }
    }
    
    public Map<Integer, List<Long>> getAndroidModelDeviceSplit(final List<Long> resourceList) {
        final Map<Integer, List<Long>> resourceToModelInfo = new HashMap<Integer, List<Long>>();
        final SelectQuery sQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("MdDeviceInfo"));
        sQuery.addJoin(new Join("MdDeviceInfo", "MdModelInfo", new String[] { "MODEL_ID" }, new String[] { "MODEL_ID" }, 2));
        final Criteria resCriteria = new Criteria(new Column("MdDeviceInfo", "RESOURCE_ID"), (Object)resourceList.toArray(), 8);
        sQuery.setCriteria(resCriteria);
        final Criteria modelTypeTabletCriteria = new Criteria(new Column("MdModelInfo", "MODEL_TYPE"), (Object)2, 0);
        final CaseExpression modelTypeExpression = new CaseExpression("ANDROID_MODEL_TYPE");
        modelTypeExpression.addWhen(modelTypeTabletCriteria, (Object)3);
        modelTypeExpression.elseVal((Object)2);
        sQuery.addSelectColumn(Column.getColumn("MdDeviceInfo", "RESOURCE_ID"));
        sQuery.addSelectColumn((Column)modelTypeExpression);
        try {
            final DMDataSetWrapper ds = DMDataSetWrapper.executeQuery((Object)sQuery);
            if (ds != null) {
                while (ds.next()) {
                    final Integer androidModel = (Integer)ds.getValue("ANDROID_MODEL_TYPE");
                    if (!resourceToModelInfo.containsKey(androidModel)) {
                        resourceToModelInfo.put(androidModel, new ArrayList<Long>());
                    }
                    resourceToModelInfo.get(androidModel).add((Object)ds.getValue("RESOURCE_ID"));
                }
            }
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Cannot fetch model Type for Devices", e);
        }
        return resourceToModelInfo;
    }
    
    public static void addOrUpdateDeprovisionHistory(final JSONObject deprovisionJson, final Long userId) throws Exception {
        final Long resourceId = deprovisionJson.getLong("RESOURCE_ID");
        final DataObject dataObject = MDMUtil.getPersistence().get("DeprovisionHistory", new Criteria(Column.getColumn("DeprovisionHistory", "RESOURCE_ID"), (Object)resourceId, 0));
        Row deprovisionRow;
        if (!dataObject.isEmpty()) {
            deprovisionRow = dataObject.getFirstRow("DeprovisionHistory");
        }
        else {
            deprovisionRow = new Row("DeprovisionHistory");
        }
        deprovisionRow.set("RESOURCE_ID", (Object)resourceId);
        deprovisionRow.set("DEPROVISION_TYPE", deprovisionJson.get("DEPROVISION_TYPE"));
        deprovisionRow.set("DEPROVISION_REASON", (Object)4);
        deprovisionRow.set("DEPROVISION_TIME", (Object)System.currentTimeMillis());
        deprovisionRow.set("USER_ID", (Object)userId);
        deprovisionRow.set("WIPE_PENDING", (Object)Boolean.TRUE);
        deprovisionRow.set("COMMENT", deprovisionJson.get("COMMENT"));
        if (!dataObject.isEmpty()) {
            dataObject.updateRow(deprovisionRow);
        }
        else {
            dataObject.addRow(deprovisionRow);
        }
        MDMUtil.getPersistence().update(dataObject);
    }
    
    static {
        ManagedDeviceHandler.deviceHandler = null;
    }
}
