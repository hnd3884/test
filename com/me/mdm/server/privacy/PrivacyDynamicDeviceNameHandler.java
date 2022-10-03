package com.me.mdm.server.privacy;

import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.me.devicemanagement.framework.server.csv.CustomerParamsHandler;
import com.adventnet.persistence.DataAccessException;
import java.util.Iterator;
import com.adventnet.persistence.Row;
import com.adventnet.ds.query.Range;
import com.adventnet.ds.query.UpdateQuery;
import com.adventnet.ds.query.UpdateQueryImpl;
import com.adventnet.persistence.DataObject;
import com.adventnet.ds.query.SelectQuery;
import java.util.logging.Level;
import com.me.devicemanagement.framework.server.customer.CustomerInfoUtil;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import java.util.HashMap;
import java.util.logging.Logger;

public class PrivacyDynamicDeviceNameHandler
{
    private static PrivacyDynamicDeviceNameHandler handler;
    Logger logger;
    private static final String CP_DEVICE_NUMBER = "DeviceNumberSufix";
    
    public PrivacyDynamicDeviceNameHandler() {
        this.logger = Logger.getLogger("MDMLogger");
    }
    
    public static PrivacyDynamicDeviceNameHandler getInstance() {
        if (PrivacyDynamicDeviceNameHandler.handler == null) {
            PrivacyDynamicDeviceNameHandler.handler = new PrivacyDynamicDeviceNameHandler();
        }
        return PrivacyDynamicDeviceNameHandler.handler;
    }
    
    public String renameDevice(final Long enrollmentId, final Long resourceId) {
        String deviceName = null;
        try {
            HashMap privacySettings = new HashMap();
            if (enrollmentId != null) {
                privacySettings = new PrivacySettingsHandler().getPrivacySettingsForEnrollmentRquest(enrollmentId);
            }
            else if (resourceId != null) {
                privacySettings = new PrivacySettingsHandler().getPrivacySettingsForMdDevices(resourceId);
            }
            final int fetchDeviceName = Integer.parseInt(privacySettings.get("fetch_device_name").toString());
            if (fetchDeviceName == 0) {
                return deviceName;
            }
            final String devicePattern = privacySettings.get("device_name_pattern");
            if (devicePattern != null && !devicePattern.equals("")) {
                final SelectQuery uQuery = this.getRenameQueryForDevice();
                final Criteria cResId = new Criteria(new Column("ManagedDevice", "RESOURCE_ID"), (Object)resourceId, 0);
                uQuery.setCriteria(cResId);
                final DataObject DO = MDMUtil.getPersistence().get(uQuery);
                if (!DO.isEmpty()) {
                    final Long customerId = CustomerInfoUtil.getInstance().getCustomerIDForResID(resourceId);
                    final Boolean hasDeviceNumber = this.patternHasDeviceNumber(devicePattern);
                    int patternNumner = 0;
                    if (hasDeviceNumber) {
                        patternNumner = this.getDeviceNumber(customerId);
                    }
                    deviceName = this.getDeviceNameFromPattern(DO, resourceId, devicePattern, patternNumner);
                    if (hasDeviceNumber) {
                        ++patternNumner;
                        this.setDeviceNumber(customerId, patternNumner);
                    }
                }
            }
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Caught exception in renameDevice ", e);
        }
        return deviceName;
    }
    
    protected void renameDeviceInExtn(final Long enrollmentId, final Long resourceId) {
        try {
            final String deviceName = this.renameDevice(enrollmentId, resourceId);
            if (deviceName != null && !deviceName.equals("")) {
                final UpdateQuery uQuery = (UpdateQuery)new UpdateQueryImpl("ManagedDeviceExtn");
                final Criteria cModified = new Criteria(new Column("ManagedDeviceExtn", "IS_MODIFIED"), (Object)false, 0);
                final Criteria cRes = new Criteria(new Column("ManagedDeviceExtn", "MANAGED_DEVICE_ID"), (Object)resourceId, 0);
                uQuery.setCriteria(cModified.and(cRes));
                uQuery.setUpdateColumn("NAME", (Object)deviceName);
                MDMUtil.getPersistence().update(uQuery);
            }
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Caught exception in renameDeviceInExtn ", e);
        }
    }
    
    protected void renameDeviceInExtn(final int ownedBy, final long customerId, final String devicePattern) {
        try {
            int rangeStart = 1;
            final int MAX_DATA = 50;
            final SelectQuery uQuery = this.getDeviceRenameQuery();
            final Criteria cOwnedBy = new Criteria(new Column("DeviceEnrollmentRequest", "OWNED_BY"), (Object)ownedBy, 0);
            final Criteria cCustomerId = new Criteria(new Column("Resource", "CUSTOMER_ID"), (Object)customerId, 0);
            final Criteria cModified = new Criteria(new Column("ManagedDeviceExtn", "IS_MODIFIED"), (Object)false, 0);
            uQuery.setCriteria(cOwnedBy.and(cCustomerId).and(cModified));
            Range range = new Range(rangeStart, MAX_DATA);
            uQuery.setRange(range);
            for (DataObject DO = MDMUtil.getPersistence().get(uQuery); !DO.isEmpty(); DO = MDMUtil.getPersistence().get(uQuery)) {
                DO = this.replaceDeviceNameDynamically(DO, devicePattern, customerId);
                MDMUtil.getPersistence().update(DO);
                rangeStart += MAX_DATA;
                range = new Range(rangeStart, MAX_DATA);
                uQuery.setRange(range);
            }
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Caught exception in removeResourceNameForDevicePrivacy ", e);
        }
    }
    
    private DataObject replaceDeviceNameDynamically(final DataObject currentDO, final String pattern, final Long customerId) throws DataAccessException {
        final Boolean hasDeviceNumber = this.patternHasDeviceNumber(pattern);
        int patternNumner = 0;
        if (hasDeviceNumber) {
            patternNumner = this.getDeviceNumber(customerId);
        }
        final Iterator item = currentDO.getRows("ManagedDeviceExtn");
        while (item.hasNext()) {
            final Row deviceExnt = item.next();
            final Long deviceResid = (Long)deviceExnt.get("MANAGED_DEVICE_ID");
            final String deviceName = this.getDeviceNameFromPattern(currentDO, deviceResid, pattern, patternNumner);
            if (hasDeviceNumber) {
                ++patternNumner;
            }
            deviceExnt.set("NAME", (Object)deviceName);
            currentDO.updateRow(deviceExnt);
        }
        if (hasDeviceNumber) {
            this.setDeviceNumber(customerId, patternNumner);
        }
        return currentDO;
    }
    
    private String getDeviceNameFromPattern(final DataObject currentDO, final Long deviceResid, final String pattern, final int patternNumner) throws DataAccessException {
        String deviceName = new String(pattern);
        if (pattern.contains("%username%")) {
            final Criteria cDeviceResId = new Criteria(new Column("ManagedUserToDevice", "MANAGED_DEVICE_ID"), (Object)deviceResid, 0);
            final Row userRow = currentDO.getRow("ManagedUserToDevice", cDeviceResId);
            final Criteria cUserId = new Criteria(new Column("Resource", "RESOURCE_ID"), (Object)(long)userRow.get("MANAGED_USER_ID"), 0);
            final Row resourceRow = currentDO.getRow("Resource", cUserId);
            deviceName = deviceName.replace("%username%", (CharSequence)resourceRow.get("NAME"));
        }
        if (pattern.contains("%model%")) {
            final Criteria cDeviceResId = new Criteria(new Column("MdDeviceInfo", "RESOURCE_ID"), (Object)deviceResid, 0);
            final Row deviceInfo = currentDO.getRow("MdDeviceInfo", cDeviceResId);
            final Criteria cModelInfo = new Criteria(new Column("MdModelInfo", "MODEL_ID"), (Object)(long)deviceInfo.get("MODEL_ID"), 0);
            final Row modelRow = currentDO.getRow("MdModelInfo", cModelInfo);
            deviceName = deviceName.replace("%model%", (CharSequence)modelRow.get("MODEL"));
        }
        if (pattern.contains("%serialnumber%")) {
            final Criteria cDeviceResId = new Criteria(new Column("MdDeviceInfo", "RESOURCE_ID"), (Object)deviceResid, 0);
            final Row deviceInfo = currentDO.getRow("MdDeviceInfo", cDeviceResId);
            deviceName = deviceName.replace("%serialnumber%", (CharSequence)deviceInfo.get("SERIAL_NUMBER"));
        }
        if (pattern.contains("%dev icenumber%")) {
            deviceName = deviceName.replace("%dev icenumber%", String.valueOf(patternNumner));
        }
        return deviceName;
    }
    
    private Boolean patternHasDeviceNumber(final String pattern) {
        if (pattern.contains("%dev icenumber%")) {
            return true;
        }
        return false;
    }
    
    private int getDeviceNumber(final Long customerId) {
        int number = 1;
        try {
            final String countStr = CustomerParamsHandler.getInstance().getParameterValue("DeviceNumberSufix", (long)customerId);
            if (countStr != null) {
                number = Integer.parseInt(countStr);
            }
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, "Exception while getting device numbering", ex);
        }
        return number;
    }
    
    private void setDeviceNumber(final Long customerId, final int deviceNumber) {
        try {
            CustomerParamsHandler.getInstance().addOrUpdateParameter("DeviceNumberSufix", String.valueOf(deviceNumber), (long)customerId);
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, null, ex);
        }
    }
    
    private SelectQuery getRenameQueryForDevice() {
        final SelectQuery uQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("ManagedDevice"));
        uQuery.addJoin(new Join("ManagedDevice", "ManagedUserToDevice", new String[] { "RESOURCE_ID" }, new String[] { "MANAGED_DEVICE_ID" }, 2));
        uQuery.addJoin(new Join("ManagedUserToDevice", "Resource", new String[] { "MANAGED_USER_ID" }, new String[] { "RESOURCE_ID" }, 2));
        uQuery.addJoin(new Join("ManagedDevice", "MdDeviceInfo", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2));
        uQuery.addJoin(new Join("MdDeviceInfo", "MdModelInfo", new String[] { "MODEL_ID" }, new String[] { "MODEL_ID" }, 2));
        uQuery.addSelectColumn(new Column("ManagedDevice", "*"));
        uQuery.addSelectColumn(new Column("ManagedUserToDevice", "*"));
        uQuery.addSelectColumn(new Column("Resource", "*"));
        uQuery.addSelectColumn(new Column("MdDeviceInfo", "*"));
        uQuery.addSelectColumn(new Column("MdModelInfo", "*"));
        return uQuery;
    }
    
    private SelectQuery getDeviceRenameQuery() {
        final SelectQuery uQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("ManagedDeviceExtn"));
        uQuery.addJoin(new Join("ManagedDeviceExtn", "ManagedUserToDevice", new String[] { "MANAGED_DEVICE_ID" }, new String[] { "MANAGED_DEVICE_ID" }, 2));
        uQuery.addJoin(new Join("ManagedDeviceExtn", "EnrollmentRequestToDevice", new String[] { "MANAGED_DEVICE_ID" }, new String[] { "MANAGED_DEVICE_ID" }, 2));
        uQuery.addJoin(new Join("EnrollmentRequestToDevice", "DeviceEnrollmentRequest", new String[] { "ENROLLMENT_REQUEST_ID" }, new String[] { "ENROLLMENT_REQUEST_ID" }, 2));
        uQuery.addJoin(new Join("ManagedUserToDevice", "Resource", new String[] { "MANAGED_USER_ID" }, new String[] { "RESOURCE_ID" }, 2));
        uQuery.addJoin(new Join("ManagedDeviceExtn", "MdDeviceInfo", new String[] { "MANAGED_DEVICE_ID" }, new String[] { "RESOURCE_ID" }, 2));
        uQuery.addJoin(new Join("MdDeviceInfo", "MdModelInfo", new String[] { "MODEL_ID" }, new String[] { "MODEL_ID" }, 2));
        uQuery.addSelectColumn(new Column("ManagedDeviceExtn", "*"));
        uQuery.addSelectColumn(new Column("ManagedUserToDevice", "*"));
        uQuery.addSelectColumn(new Column("Resource", "*"));
        uQuery.addSelectColumn(new Column("DeviceEnrollmentRequest", "*"));
        uQuery.addSelectColumn(new Column("MdDeviceInfo", "*"));
        uQuery.addSelectColumn(new Column("MdModelInfo", "*"));
        return uQuery;
    }
    
    static {
        PrivacyDynamicDeviceNameHandler.handler = null;
    }
}
