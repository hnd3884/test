package com.adventnet.sym.server.mdm.inv;

import com.adventnet.ds.query.SelectQuery;
import java.util.Collection;
import com.me.mdm.server.notification.NotificationHandler;
import com.me.mdm.server.inv.ios.AppleDeviceRenameHandler;
import com.adventnet.sym.server.mdm.command.DeviceCommandRepository;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONException;
import com.adventnet.sym.server.mdm.util.MDMStringUtils;
import com.me.devicemanagement.framework.server.logger.DMSecurityLogger;
import com.me.devicemanagement.framework.server.util.DBUtil;
import com.adventnet.persistence.DataAccessException;
import com.me.mdm.api.error.APIHTTPException;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import java.util.HashMap;
import java.util.Properties;
import com.adventnet.sym.server.mdm.util.MDMEventLogHandler;
import com.adventnet.sym.server.mdm.util.JSONUtil;
import java.util.Iterator;
import java.util.Set;
import com.adventnet.persistence.DataObject;
import java.util.logging.Level;
import com.adventnet.sym.server.mdm.enroll.DeviceManagedDetailsHandler;
import com.me.devicemanagement.framework.server.customer.CustomerInfoUtil;
import com.me.devicemanagement.framework.server.authentication.DMUserHandler;
import java.net.URLDecoder;
import com.adventnet.persistence.Row;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.me.mdm.server.privacy.PrivacyDynamicDeviceNameHandler;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import java.util.Map;
import org.json.simple.JSONObject;
import java.util.logging.Logger;

public class MDCustomDetailsRequestHandler
{
    private static Logger logger;
    private static MDCustomDetailsRequestHandler mdCustomDetailsRequestHandler;
    public static final String CUSTOMER_PARAM_NAME = "CUSTOMER_PARAM_NAME";
    public static final String DATA_TYPE = "DATA_TYPE";
    
    public static MDCustomDetailsRequestHandler getInstance() {
        if (MDCustomDetailsRequestHandler.mdCustomDetailsRequestHandler == null) {
            MDCustomDetailsRequestHandler.mdCustomDetailsRequestHandler = new MDCustomDetailsRequestHandler();
        }
        return MDCustomDetailsRequestHandler.mdCustomDetailsRequestHandler;
    }
    
    public void addOrUpdateCustomDeviceDetails(final JSONObject singleDeviceDetails) throws Exception {
        try {
            final org.json.JSONObject singleDeviceDetailsJson = new org.json.JSONObject((Map)singleDeviceDetails);
            final long deviceId = (long)singleDeviceDetailsJson.get("MANAGED_DEVICE_ID");
            String deviceName = (String)singleDeviceDetailsJson.get("NAME");
            if (deviceName != null && deviceName.length() > 100) {
                deviceName = deviceName.substring(0, 99);
            }
            deviceName = SyMUtil.getInstance().decodeURIComponentEquivalent(deviceName);
            final boolean setByUser = (boolean)singleDeviceDetailsJson.get("IS_MODIFIED");
            if (!setByUser) {
                final long enrollmentId = singleDeviceDetailsJson.optLong("ENROLLMENT_REQUEST_ID", 0L);
                if (enrollmentId != 0L) {
                    final String privacyName = PrivacyDynamicDeviceNameHandler.getInstance().renameDevice(enrollmentId, deviceId);
                    if (privacyName != null && !privacyName.equals("")) {
                        deviceName = privacyName;
                    }
                }
            }
            final Criteria c = new Criteria(Column.getColumn("ManagedDeviceExtn", "MANAGED_DEVICE_ID"), (Object)deviceId, 0);
            final DataObject dobj = MDMUtil.getPersistence().get("ManagedDeviceExtn", c);
            final JSONObject columnList = new MDCustomDetailsCSVProcessor().getCustomColumnDetails();
            String oper;
            if (dobj.isEmpty()) {
                oper = "ADD";
                final Row r = new Row("ManagedDeviceExtn");
                r.set("MANAGED_DEVICE_ID", (Object)deviceId);
                r.set("NAME", (Object)deviceName);
                if (setByUser) {
                    final long currentTime = System.currentTimeMillis();
                    final long userId = (long)singleDeviceDetailsJson.get("USER_ID");
                    String deviceDescription = null;
                    if (singleDeviceDetailsJson.has("DESCRIPTION")) {
                        deviceDescription = (String)singleDeviceDetailsJson.get("DESCRIPTION");
                        deviceDescription = URLDecoder.decode(deviceDescription, "UTF-8");
                        if (deviceDescription != null && !deviceDescription.equals("") && !deviceDescription.equals(" ")) {
                            r.set("DESCRIPTION", (Object)deviceDescription);
                        }
                        else {
                            r.set("DESCRIPTION", (Object)null);
                        }
                    }
                    this.fillCustomColumnValues(r, singleDeviceDetailsJson);
                    r.set("IS_MODIFIED", (Object)true);
                    r.set("LAST_MODIFIED_TIME", (Object)new Long(currentTime));
                    r.set("USER_ID", (Object)userId);
                }
                else {
                    final Set<Map.Entry<String, org.json.JSONObject>> entrySet = columnList.entrySet();
                    for (final Map.Entry<String, org.json.JSONObject> element : entrySet) {
                        final String columnName = element.getKey();
                        r.set(columnName, (Object)null);
                    }
                    r.set("IS_MODIFIED", (Object)false);
                    r.set("LAST_MODIFIED_TIME", (Object)null);
                    r.set("USER_ID", (Object)DMUserHandler.getDCSystemUserId());
                    if (singleDeviceDetailsJson.optString("GENERIC_IDENTIFIER") != null) {
                        r.set("GENERIC_IDENTIFIER", (Object)singleDeviceDetailsJson.getString("GENERIC_IDENTIFIER"));
                    }
                }
                dobj.addRow(r);
            }
            else {
                oper = "UPDATE";
                final Row r = dobj.getRow("ManagedDeviceExtn");
                r.set("NAME", (Object)deviceName);
                Boolean isRowUpdate = false;
                if (setByUser) {
                    String deviceDescription2 = null;
                    if (singleDeviceDetailsJson.has("DESCRIPTION")) {
                        deviceDescription2 = (String)singleDeviceDetailsJson.get("DESCRIPTION");
                        if (deviceDescription2 != null && !deviceDescription2.equals("") && !deviceDescription2.equals(" ")) {
                            deviceDescription2 = URLDecoder.decode(deviceDescription2, "UTF-8");
                            r.set("DESCRIPTION", (Object)deviceDescription2);
                        }
                        else {
                            r.set("DESCRIPTION", (Object)null);
                        }
                    }
                    if (singleDeviceDetailsJson.has("USER_ID")) {
                        r.set("USER_ID", (Object)singleDeviceDetails.get((Object)"USER_ID"));
                    }
                    final long currentTime2 = System.currentTimeMillis();
                    this.fillCustomColumnValues(r, singleDeviceDetailsJson);
                    r.set("IS_MODIFIED", (Object)true);
                    r.set("LAST_MODIFIED_TIME", (Object)new Long(currentTime2));
                    dobj.updateRow(r);
                    isRowUpdate = true;
                }
                else if (!(boolean)r.get("IS_MODIFIED")) {
                    dobj.updateRow(r);
                    isRowUpdate = true;
                }
                if (CustomerInfoUtil.getInstance().isMSP() && isRowUpdate) {
                    final org.json.JSONObject deviceDetails = new org.json.JSONObject();
                    deviceDetails.put("NAME", (Object)deviceName);
                    final Long customerId = CustomerInfoUtil.getInstance().getCustomerIDForResID(Long.valueOf(deviceId));
                    deviceDetails.put("CUSTOMER_ID", (Object)customerId);
                    new DeviceManagedDetailsHandler().addOrUpdateDeviceHandling(deviceId, deviceDetails);
                }
            }
            MDMUtil.getPersistence().update(dobj);
            Logger.getLogger("MDMCustomDetailsLogger").info("Operation: " + oper + " Managed Device Id:" + deviceId + " Custom Device Name:" + deviceName);
        }
        catch (final Exception ex) {
            MDCustomDetailsRequestHandler.logger.log(Level.SEVERE, "Exception in addOrUpdateDetails :{0}", ex);
            throw ex;
        }
    }
    
    public void updateCustomDeviceDetails(final org.json.JSONObject deviceObject) throws Exception {
        try {
            final Long deviceId = deviceObject.optLong("MANAGED_DEVICE_ID");
            final Criteria managedDevice = new Criteria(new Column("ManagedDeviceExtn", "MANAGED_DEVICE_ID"), (Object)deviceId, 0);
            final DataObject dataObject = MDMUtil.getPersistenceLite().get("ManagedDeviceExtn", managedDevice);
            final org.json.JSONObject additionalDataJSON = deviceObject.getJSONObject("additional_data");
            final Long customerId = JSONUtil.optLongForUVH(additionalDataJSON, "customer_id", Long.valueOf(-1L));
            deviceObject.remove("additional_data");
            final Boolean isDeviceNameChanged = additionalDataJSON.optBoolean("is_device_name_changed", false);
            final String userName = additionalDataJSON.optString("user_name");
            String oldDeviceName = null;
            if (!dataObject.isEmpty()) {
                final Row row = dataObject.getFirstRow("ManagedDeviceExtn");
                oldDeviceName = (String)row.get("NAME");
                final Iterator keys = deviceObject.keys();
                while (keys.hasNext()) {
                    final String columnName = keys.next();
                    final Object columnValue = deviceObject.opt(columnName);
                    row.set(columnName, columnValue);
                }
                dataObject.updateRow(row);
                MDMUtil.getPersistenceLite().update(dataObject);
                if (isDeviceNameChanged) {
                    final String name = String.valueOf(deviceObject.get("NAME"));
                    if (CustomerInfoUtil.getInstance().isMSP()) {
                        final org.json.JSONObject deviceDetails = new org.json.JSONObject();
                        deviceDetails.put("NAME", (Object)name);
                        deviceDetails.put("CUSTOMER_ID", (Object)customerId);
                        new DeviceManagedDetailsHandler().addOrUpdateDeviceHandling(deviceId, deviceDetails);
                    }
                    final Object remarksArgs = oldDeviceName + "@@@" + name;
                    MDMEventLogHandler.getInstance().MDMEventLogEntry(29051, deviceId, userName, "mdm.inv.ios.device_name_init", remarksArgs, JSONUtil.optLongForUVH(additionalDataJSON, "customer_id", Long.valueOf(-1L)));
                }
                final Properties taskProps = new Properties();
                deviceObject.put("CUSTOMER_ID", (Object)customerId);
                taskProps.setProperty("params", deviceObject.toString());
                final HashMap taskInfoMap = new HashMap();
                taskInfoMap.put("taskName", "ProcessCustomDeviceDetailsTask");
                taskInfoMap.put("schedulerTime", System.currentTimeMillis());
                taskInfoMap.put("poolName", "asynchThreadPool");
                MDCustomDetailsRequestHandler.logger.log(Level.INFO, "Invoking CustomDeviceDetailsTask from updateCustomDeviceDetails for editing device name with resourceId : {0}", deviceId);
                ApiFactoryProvider.getSchedulerAPI().executeAsynchronously("com.me.mdm.server.inv.CustomDeviceDetailsTask", taskInfoMap, taskProps);
            }
        }
        catch (final DataAccessException ex) {
            throw new APIHTTPException("COM0009", new Object[0]);
        }
    }
    
    public JSONObject getCustomDeviceDetails(final long resourceID) throws Exception {
        try {
            final Row r = DBUtil.getRowFromDB("ManagedDeviceExtn", "MANAGED_DEVICE_ID", (Object)resourceID);
            if (r != null) {
                final JSONObject json = new JSONObject();
                json.put((Object)"MANAGED_DEVICE_ID", (Object)r.get("MANAGED_DEVICE_ID"));
                json.put((Object)"NAME", (Object)r.get("NAME"));
                final JSONObject columnList = new MDCustomDetailsCSVProcessor().getCustomColumnDetails();
                final Set<Map.Entry<String, JSONObject>> entrySet = columnList.entrySet();
                for (final Map.Entry<String, JSONObject> element : entrySet) {
                    final String columnName = element.getKey();
                    json.put((Object)columnName, r.get(columnName));
                }
                DMSecurityLogger.info(MDCustomDetailsRequestHandler.logger, "MDCustomDetailsRequestHandler", "getCustomDeviceDetails", "Getting device details {0}", (Object)json.toJSONString());
                return json;
            }
            return null;
        }
        catch (final Exception ex) {
            throw ex;
        }
    }
    
    private void fillRowValue(final Row r, final org.json.JSONObject json, final String columnName, final String type) {
        try {
            if (json.has(columnName)) {
                if (json.isNull(columnName)) {
                    r.set(columnName, (Object)null);
                }
                else if ((type.equalsIgnoreCase("String") || json.get(columnName) instanceof String) && MDMStringUtils.isEmpty((String)json.get(columnName))) {
                    r.set(columnName, (Object)null);
                }
                else {
                    r.set(columnName, json.get(columnName));
                }
            }
        }
        catch (final JSONException ex) {
            MDCustomDetailsRequestHandler.logger.log(Level.WARNING, "Exception while filling Device Details row:", (Throwable)ex);
        }
    }
    
    private void fillCustomColumnValues(final Row r, final org.json.JSONObject singleDeviceDetails) throws Exception {
        final JSONObject columnList = new MDCustomDetailsCSVProcessor().getCustomColumnDetails();
        final Set<Map.Entry<String, JSONObject>> entrySet = columnList.entrySet();
        for (final Map.Entry<String, JSONObject> element : entrySet) {
            final String columnName = element.getKey();
            final JSONObject columnDetails = element.getValue();
            this.fillRowValue(r, singleDeviceDetails, columnName, (String)columnDetails.get((Object)"DATA_TYPE"));
        }
    }
    
    public void checkAndSendDeviceNameUpdateCommand(final List resourceList) {
        try {
            final List finalResourceList = new ArrayList();
            final HashMap<Integer, ArrayList<Long>> notificationTypeToResource = new HashMap<Integer, ArrayList<Long>>();
            final SelectQuery sQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("ManagedDevice"));
            sQuery.addJoin(new Join("ManagedDevice", "MdDeviceInfo", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2));
            sQuery.addJoin(new Join("MdDeviceInfo", "MdModelInfo", new String[] { "MODEL_ID" }, new String[] { "MODEL_ID" }, 2));
            Criteria resourceCriteria = new Criteria(Column.getColumn("ManagedDevice", "RESOURCE_ID"), (Object)resourceList.toArray(), 8);
            final Criteria platformCriteria = new Criteria(Column.getColumn("ManagedDevice", "PLATFORM_TYPE"), (Object)1, 0);
            final Criteria modelTypeCriteria = new Criteria(Column.getColumn("MdModelInfo", "MODEL_TYPE"), (Object)new int[] { 3, 4 }, 8);
            final Criteria isSupervisedCriteria = new Criteria(Column.getColumn("MdDeviceInfo", "IS_SUPERVISED"), (Object)Boolean.TRUE, 0);
            final Criteria winPlatformCriteria = new Criteria(Column.getColumn("ManagedDevice", "PLATFORM_TYPE"), (Object)3, 0);
            final Criteria androidPlatformCriteria = new Criteria(Column.getColumn("ManagedDevice", "PLATFORM_TYPE"), (Object)2, 0);
            final Criteria winOSVersionCriteria = new Criteria(Column.getColumn("MdDeviceInfo", "OS_VERSION"), (Object)"10.0*", 10, (boolean)Boolean.FALSE);
            sQuery.addSelectColumn(Column.getColumn("ManagedDevice", "RESOURCE_ID"));
            sQuery.addSelectColumn(Column.getColumn("MdDeviceInfo", "RESOURCE_ID"));
            sQuery.addSelectColumn(Column.getColumn("MdDeviceInfo", "MODEL_ID"));
            sQuery.addSelectColumn(Column.getColumn("ManagedDevice", "PLATFORM_TYPE"));
            sQuery.addSelectColumn(Column.getColumn("MdModelInfo", "MODEL_ID"));
            sQuery.addSelectColumn(Column.getColumn("MdModelInfo", "MODEL_TYPE"));
            sQuery.setCriteria(resourceCriteria.and(platformCriteria.and(isSupervisedCriteria.or(modelTypeCriteria)).or(winPlatformCriteria.and(winOSVersionCriteria)).or(androidPlatformCriteria)));
            final DataObject dataObject = MDMUtil.getPersistence().get(sQuery);
            if (!dataObject.isEmpty()) {
                final Iterator iterator = dataObject.getRows("ManagedDevice");
                while (iterator.hasNext()) {
                    final Row row = iterator.next();
                    final Long resourceID = (Long)row.get("RESOURCE_ID");
                    final Integer platformType = (Integer)row.get("PLATFORM_TYPE");
                    if (platformType == 2) {
                        DeviceCommandRepository.getInstance().addSecurityCommand(resourceID, "UpdateUserInfo");
                    }
                    else if (platformType == 1) {
                        final Criteria modelCriteria = new Criteria(new Column("MdDeviceInfo", "RESOURCE_ID"), (Object)row.get("RESOURCE_ID"), 0);
                        final Row modelRow = dataObject.getRow("MdModelInfo", modelCriteria);
                        final Integer modelType = (Integer)modelRow.get("MODEL_TYPE");
                        final AppleDeviceRenameHandler appleDeviceRenameHandler = AppleDeviceRenameHandler.getDeviceRenameHandler(modelType);
                        appleDeviceRenameHandler.addDeviceRenameCommand(resourceID);
                    }
                    else {
                        DeviceCommandRepository.getInstance().addDeviceNameCommand(resourceID);
                    }
                    finalResourceList.add(resourceID);
                    if (notificationTypeToResource.get(platformType) == null) {
                        notificationTypeToResource.put(platformType, new ArrayList<Long>());
                    }
                    notificationTypeToResource.get(platformType).add(resourceID);
                }
            }
            for (final Integer notificationtype : notificationTypeToResource.keySet()) {
                NotificationHandler.getInstance().SendNotification(notificationTypeToResource.get(notificationtype), notificationtype);
            }
            resourceList.removeAll(finalResourceList);
            if (!resourceList.isEmpty() || resourceList != null) {
                resourceCriteria = new Criteria(Column.getColumn("ManagedDevice", "RESOURCE_ID"), (Object)resourceList.toArray(), 8);
                final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("ManagedDevice"));
                selectQuery.addJoin(new Join("ManagedDevice", "MdDeviceInfo", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2));
                selectQuery.addSelectColumn(Column.getColumn("ManagedDevice", "RESOURCE_ID"));
                selectQuery.addSelectColumn(Column.getColumn("MdDeviceInfo", "RESOURCE_ID"));
                selectQuery.addSelectColumn(Column.getColumn("MdDeviceInfo", "SERIAL_NUMBER"));
                selectQuery.setCriteria(resourceCriteria.and(platformCriteria));
                final DataObject dObject = MDMUtil.getPersistence().get(selectQuery);
                if (!dObject.isEmpty()) {
                    final Iterator iterator2 = dObject.getRows("MdDeviceInfo");
                    while (iterator2.hasNext()) {
                        final Row row2 = iterator2.next();
                        final Long resourceID2 = (Long)row2.get("RESOURCE_ID");
                        final String sNo = (String)row2.get("SERIAL_NUMBER");
                        final long userID = (long)DBUtil.getValueFromDB("ManagedDeviceExtn", "MANAGED_DEVICE_ID", (Object)resourceID2, "USER_ID");
                        final String userName = DMUserHandler.getDCUser(DMUserHandler.getLoginIdForUserId(Long.valueOf(userID)));
                        final long customerId = (long)DBUtil.getValueFromDB("Resource", "RESOURCE_ID", (Object)resourceID2, "CUSTOMER_ID");
                        MDMEventLogHandler.getInstance().MDMEventLogEntry(29050, resourceID2, userName, "mdm.inv.unsupervised.device_name_update", sNo, customerId);
                    }
                }
            }
        }
        catch (final Exception ex) {
            MDCustomDetailsRequestHandler.logger.log(Level.SEVERE, "Exception in checkAndSendDeviceNameUpdateCommand :{0}", ex);
        }
    }
    
    static {
        MDCustomDetailsRequestHandler.logger = Logger.getLogger("MDMEnrollment");
        MDCustomDetailsRequestHandler.mdCustomDetailsRequestHandler = null;
    }
}
