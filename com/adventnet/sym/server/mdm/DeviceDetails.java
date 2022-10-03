package com.adventnet.sym.server.mdm;

import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.me.mdm.server.privacy.PrivacySettingsHandler;
import java.util.Iterator;
import com.adventnet.persistence.Row;
import com.adventnet.persistence.DataObject;
import com.adventnet.ds.query.SelectQuery;
import org.json.JSONException;
import com.adventnet.persistence.DataAccessException;
import java.util.logging.Level;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import java.util.ArrayList;
import org.json.JSONObject;
import java.util.logging.Logger;

public class DeviceDetails
{
    public static Logger logger;
    public long resourceId;
    public String name;
    public String udid;
    public String serialNumber;
    public int platform;
    public int agentType;
    public long customerId;
    public String agentVersion;
    public long agentVersionCode;
    public int modelType;
    public int ownedBy;
    public String osVersion;
    public boolean locationTrackingEnabled;
    public JSONObject privacySettingsJSON;
    public boolean nativeAgentInstalled;
    public boolean knoxContainerActive;
    public boolean profileOwner;
    public boolean isSupervised;
    public boolean isMultiUser;
    
    public DeviceDetails() {
        this.osVersion = "-1";
        this.privacySettingsJSON = new JSONObject();
    }
    
    public DeviceDetails(final long resourceId) {
        this.osVersion = "-1";
        this.privacySettingsJSON = new JSONObject();
        this.assignDeviceDetails(this.resourceId = resourceId);
    }
    
    public ArrayList getDeviceDetails(final ArrayList<Long> resourceList) {
        return this.assignDeviceDetails(resourceList);
    }
    
    public DeviceDetails(final String udid) {
        this.osVersion = "-1";
        this.privacySettingsJSON = new JSONObject();
        this.assignDeviceDetails(this.udid = udid);
    }
    
    private void assignDeviceDetails(final long resourceId) {
        this.assignDeviceDetails(new Criteria(Column.getColumn("Resource", "RESOURCE_ID"), (Object)resourceId, 0));
    }
    
    private ArrayList assignDeviceDetails(final ArrayList<Long> resourceList) {
        return this.assignDeviceDetailsList(new Criteria(Column.getColumn("Resource", "RESOURCE_ID"), (Object)resourceList.toArray(), 8));
    }
    
    private void assignDeviceDetails(final String udid) {
        this.assignDeviceDetails(new Criteria(Column.getColumn("ManagedDevice", "UDID"), (Object)udid, 0));
    }
    
    private ArrayList assignDeviceDetailsList(final Criteria criteria) {
        try {
            final SelectQuery sQuery = this.getDeviceDetailsQuery();
            this.addSelectColumnsForDeviceDetailsQuery(sQuery);
            sQuery.setCriteria(criteria);
            final DataObject dobj = MDMUtil.getPersistence().get(sQuery);
            ArrayList deviceDetailsArrayList = new ArrayList();
            if (!dobj.isEmpty()) {
                deviceDetailsArrayList = this.assignDeviceDetailsList(dobj);
            }
            return deviceDetailsArrayList;
        }
        catch (final DataAccessException | JSONException e) {
            DeviceDetails.logger.log(Level.SEVERE, "exception while getting device details", e);
            return new ArrayList();
        }
    }
    
    private ArrayList assignDeviceDetailsList(final DataObject dobj) throws DataAccessException, JSONException {
        final ArrayList<DeviceDetails> deviceList = new ArrayList<DeviceDetails>();
        final Iterator resourceIterator = dobj.getRows("Resource");
        while (resourceIterator.hasNext()) {
            final Row resRow = resourceIterator.next();
            final Long resourceId = (Long)resRow.get("RESOURCE_ID");
            final DeviceDetails deviceDetails = new DeviceDetails();
            this.assignDeviceDetails(dobj, resourceId, deviceDetails);
            deviceList.add(deviceDetails);
        }
        return deviceList;
    }
    
    private DeviceDetails assignDeviceDetails(final DataObject dobj, Long resourceId, final DeviceDetails deviceDetails) {
        try {
            Row resRow = null;
            if (resourceId != null) {
                resRow = dobj.getRow("Resource", new Criteria(Column.getColumn("Resource", "RESOURCE_ID"), (Object)resourceId, 0));
            }
            else {
                resRow = dobj.getRow("Resource");
            }
            deviceDetails.resourceId = (long)resRow.get("RESOURCE_ID");
            deviceDetails.customerId = (long)resRow.get("CUSTOMER_ID");
            resourceId = deviceDetails.resourceId;
            final Row deviceRow = dobj.getRow("ManagedDevice", new Criteria(Column.getColumn("ManagedDevice", "RESOURCE_ID"), (Object)resourceId, 0));
            deviceDetails.udid = (String)deviceRow.get("UDID");
            deviceDetails.resourceId = (long)deviceRow.get("RESOURCE_ID");
            deviceDetails.platform = (int)deviceRow.get("PLATFORM_TYPE");
            deviceDetails.agentType = (int)deviceRow.get("AGENT_TYPE");
            final Row deviceExtnRow = dobj.getRow("ManagedDeviceExtn", new Criteria(Column.getColumn("ManagedDeviceExtn", "MANAGED_DEVICE_ID"), (Object)resourceId, 0));
            deviceDetails.name = (String)deviceExtnRow.get("NAME");
            deviceDetails.agentVersion = (String)deviceRow.get("AGENT_VERSION");
            deviceDetails.agentVersionCode = (long)deviceRow.get("AGENT_VERSION_CODE");
            final Row mdDeviceInfoRow = dobj.getRow("MdDeviceInfo", new Criteria(Column.getColumn("MdDeviceInfo", "RESOURCE_ID"), (Object)resourceId, 0));
            if (mdDeviceInfoRow != null) {
                final Long modelId = (Long)mdDeviceInfoRow.get("MODEL_ID");
                final Row modelInfoRow = dobj.getRow("MdModelInfo", new Criteria(Column.getColumn("MdModelInfo", "MODEL_ID"), (Object)modelId, 0));
                deviceDetails.modelType = (int)modelInfoRow.get("MODEL_TYPE");
                deviceDetails.serialNumber = (String)mdDeviceInfoRow.get("SERIAL_NUMBER");
                final Boolean isProfileOwner = (Boolean)mdDeviceInfoRow.get("IS_PROFILEOWNER");
                if (isProfileOwner != null && isProfileOwner) {
                    deviceDetails.profileOwner = true;
                }
                else {
                    deviceDetails.profileOwner = false;
                }
                deviceDetails.osVersion = (String)mdDeviceInfoRow.get("OS_VERSION");
                deviceDetails.isMultiUser = (mdDeviceInfoRow.get("IS_MULTIUSER") != null && (boolean)mdDeviceInfoRow.get("IS_MULTIUSER"));
                deviceDetails.isSupervised = false;
                final Boolean isSupervised = (Boolean)mdDeviceInfoRow.get("IS_SUPERVISED");
                if (isSupervised != null && isSupervised) {
                    deviceDetails.isSupervised = true;
                }
            }
            if (deviceDetails.osVersion == null) {
                final Row mdOsTempRow = dobj.getRow("MdOSDetailsTemp", new Criteria(Column.getColumn("MdOSDetailsTemp", "RESOURCE_ID"), (Object)resourceId, 0));
                if (mdOsTempRow != null) {
                    deviceDetails.osVersion = (String)mdOsTempRow.get("OS_VERSION");
                }
            }
            final Row enrollmentRequestToDevice = dobj.getRow("EnrollmentRequestToDevice", new Criteria(Column.getColumn("EnrollmentRequestToDevice", "MANAGED_DEVICE_ID"), (Object)resourceId, 0));
            final Long enrollmentRequestId = (Long)enrollmentRequestToDevice.get("ENROLLMENT_REQUEST_ID");
            final Row enrollmentReq = dobj.getRow("DeviceEnrollmentRequest", new Criteria(Column.getColumn("DeviceEnrollmentRequest", "ENROLLMENT_REQUEST_ID"), (Object)enrollmentRequestId, 0));
            deviceDetails.ownedBy = (int)enrollmentReq.get("OWNED_BY");
            final Row iosNativeAppStatusRow = dobj.getRow("IOSNativeAppStatus", new Criteria(Column.getColumn("IOSNativeAppStatus", "RESOURCE_ID"), (Object)resourceId, 0));
            if (iosNativeAppStatusRow != null) {
                final int nativeAppInstallStatus = (int)iosNativeAppStatusRow.get("INSTALLATION_STATUS");
                if (nativeAppInstallStatus == 1) {
                    deviceDetails.nativeAgentInstalled = true;
                }
                else {
                    deviceDetails.nativeAgentInstalled = false;
                }
            }
            else {
                deviceDetails.nativeAgentInstalled = false;
            }
            final Row locationDeviceStatusRow = dobj.getRow("LocationDeviceStatus", new Criteria(Column.getColumn("LocationDeviceStatus", "MANAGED_DEVICE_ID"), (Object)resourceId, 0));
            if (locationDeviceStatusRow != null) {
                deviceDetails.locationTrackingEnabled = (boolean)locationDeviceStatusRow.get("IS_ENABLED");
            }
            else {
                deviceDetails.locationTrackingEnabled = false;
            }
            final Row mdPrivacyToOwnedByRow = dobj.getRow("MDPrivacyToOwnedBy", new Criteria(Column.getColumn("MDPrivacyToOwnedBy", "OWNED_BY"), (Object)deviceDetails.ownedBy, 0).and(new Criteria(Column.getColumn("MDPrivacyToOwnedBy", "CUSTOMER_ID"), (Object)deviceDetails.customerId, 0)));
            if (mdPrivacyToOwnedByRow != null) {
                final Long privacySettingsId = (Long)mdPrivacyToOwnedByRow.get("PRIVACY_SETTINGS_ID");
                final Row mdmPrivacySettingsRow = dobj.getRow("MDMPrivacySettings", new Criteria(Column.getColumn("MDMPrivacySettings", "PRIVACY_SETTINGS_ID"), (Object)privacySettingsId, 0));
                deviceDetails.privacySettingsJSON.put("fetch_device_name", mdmPrivacySettingsRow.get("FETCH_DEVICE_NAME"));
                deviceDetails.privacySettingsJSON.put("fetch_phone_number", mdmPrivacySettingsRow.get("FETCH_PHONE_NUMBER"));
                deviceDetails.privacySettingsJSON.put("fetch_installed_app", mdmPrivacySettingsRow.get("FETCH_INSTALLED_APPS"));
                deviceDetails.privacySettingsJSON.put("fetch_location", mdmPrivacySettingsRow.get("FETCH_LOCATION"));
                deviceDetails.privacySettingsJSON.put("disable_wipe", mdmPrivacySettingsRow.get("DISABLE_WIPE"));
                deviceDetails.privacySettingsJSON.put("disable_remote_control", mdmPrivacySettingsRow.get("DISABLE_REMOTE_CONTROL"));
                deviceDetails.privacySettingsJSON.put("view_privacy_settings", mdmPrivacySettingsRow.get("VIEW_PRIVACY_SETTINGS"));
                deviceDetails.privacySettingsJSON.put("device_name_pattern", mdmPrivacySettingsRow.get("DEVICE_NAME_PATTERN"));
                deviceDetails.privacySettingsJSON.put("device_state_report", mdmPrivacySettingsRow.get("DEVICE_STATE_REPORT"));
                deviceDetails.privacySettingsJSON.put("recent_users_report", mdmPrivacySettingsRow.get("RECENT_USERS_REPORT"));
                deviceDetails.privacySettingsJSON.put("fetch_mac_address", mdmPrivacySettingsRow.get("FETCH_MAC_ADDRESS"));
                deviceDetails.privacySettingsJSON.put("fetch_user_installed_certs", mdmPrivacySettingsRow.get("FETCH_USER_INSTALLED_CERTS"));
                deviceDetails.privacySettingsJSON.put("disable_clear_passcode", mdmPrivacySettingsRow.get("DISABLE_CLEAR_PASSCODE"));
                deviceDetails.privacySettingsJSON.put("fetch_wifi_ssid", mdmPrivacySettingsRow.get("FETCH_WIFI_SSID"));
            }
            else {
                deviceDetails.privacySettingsJSON = new PrivacySettingsHandler().getDefaultPrivacySettings(deviceDetails.ownedBy);
            }
            final Row managedKnoxContainerRow = dobj.getRow("ManagedKNOXContainer");
            if (managedKnoxContainerRow != null) {
                final Integer status = (Integer)managedKnoxContainerRow.get("CONTAINER_STATUS");
                if (status != null && status == 20001) {
                    deviceDetails.knoxContainerActive = true;
                }
                else {
                    deviceDetails.knoxContainerActive = false;
                }
            }
            else {
                deviceDetails.knoxContainerActive = false;
            }
            return deviceDetails;
        }
        catch (final Exception e) {
            DeviceDetails.logger.log(Level.SEVERE, "exception while getting device details", e);
            return null;
        }
    }
    
    private void assignDeviceDetails(final DataObject dobj) {
        this.assignDeviceDetails(dobj, null, this);
    }
    
    private void assignDeviceDetails(final Criteria criteria) {
        try {
            final SelectQuery sQuery = this.getDeviceDetailsQuery();
            this.addSelectColumnsForDeviceDetailsQuery(sQuery);
            sQuery.setCriteria(criteria);
            final DataObject dobj = MDMUtil.getPersistence().get(sQuery);
            if (!dobj.isEmpty()) {
                this.assignDeviceDetails(dobj);
            }
        }
        catch (final DataAccessException ex) {
            DeviceDetails.logger.log(Level.SEVERE, "exception while getting device details", (Throwable)ex);
        }
    }
    
    private SelectQuery getDeviceDetailsQuery() {
        final Criteria cPrivacy = new Criteria(Column.getColumn("DeviceEnrollmentRequest", "OWNED_BY"), (Object)Column.getColumn("MDPrivacyToOwnedBy", "OWNED_BY"), 0);
        final Criteria cCustomer = new Criteria(Column.getColumn("Resource", "CUSTOMER_ID"), (Object)Column.getColumn("MDPrivacyToOwnedBy", "CUSTOMER_ID"), 0);
        final SelectQuery sQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("Resource"));
        sQuery.addJoin(new Join("Resource", "ManagedDevice", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2));
        sQuery.addJoin(new Join("ManagedDevice", "ManagedDeviceExtn", new String[] { "RESOURCE_ID" }, new String[] { "MANAGED_DEVICE_ID" }, 2));
        sQuery.addJoin(new Join("ManagedDevice", "MdDeviceInfo", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 1));
        sQuery.addJoin(new Join("MdDeviceInfo", "MdModelInfo", new String[] { "MODEL_ID" }, new String[] { "MODEL_ID" }, 1));
        sQuery.addJoin(new Join("ManagedDevice", "EnrollmentRequestToDevice", new String[] { "RESOURCE_ID" }, new String[] { "MANAGED_DEVICE_ID" }, 2));
        sQuery.addJoin(new Join("EnrollmentRequestToDevice", "DeviceEnrollmentRequest", new String[] { "ENROLLMENT_REQUEST_ID" }, new String[] { "ENROLLMENT_REQUEST_ID" }, 2));
        sQuery.addJoin(new Join("ManagedDevice", "LocationDeviceStatus", new String[] { "RESOURCE_ID" }, new String[] { "MANAGED_DEVICE_ID" }, 1));
        sQuery.addJoin(new Join("DeviceEnrollmentRequest", "MDPrivacyToOwnedBy", cPrivacy.and(cCustomer), 1));
        sQuery.addJoin(new Join("MDPrivacyToOwnedBy", "MDMPrivacySettings", new String[] { "PRIVACY_SETTINGS_ID" }, new String[] { "PRIVACY_SETTINGS_ID" }, 1));
        sQuery.addJoin(new Join("ManagedDevice", "IOSNativeAppStatus", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 1));
        sQuery.addJoin(new Join("ManagedDevice", "MdOSDetailsTemp", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 1));
        sQuery.addJoin(new Join("ManagedDevice", "ManagedKNOXContainer", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 1));
        return sQuery;
    }
    
    private void addSelectColumnsForDeviceDetailsQuery(final SelectQuery sQuery) {
        sQuery.addSelectColumn(Column.getColumn("Resource", "CUSTOMER_ID"));
        sQuery.addSelectColumn(Column.getColumn("ManagedDevice", "UDID"));
        sQuery.addSelectColumn(Column.getColumn("MdModelInfo", "MODEL_ID"));
        sQuery.addSelectColumn(Column.getColumn("MdDeviceInfo", "MODEL_ID"));
        sQuery.addSelectColumn(Column.getColumn("MdModelInfo", "MODEL_TYPE"));
        sQuery.addSelectColumn(Column.getColumn("MdDeviceInfo", "OS_VERSION"));
        sQuery.addSelectColumn(Column.getColumn("MDPrivacyToOwnedBy", "*"));
        sQuery.addSelectColumn(Column.getColumn("MDMPrivacySettings", "*"));
        sQuery.addSelectColumn(Column.getColumn("MdDeviceInfo", "RESOURCE_ID"));
        sQuery.addSelectColumn(Column.getColumn("ManagedDevice", "AGENT_TYPE"));
        sQuery.addSelectColumn(Column.getColumn("ManagedDevice", "RESOURCE_ID"));
        sQuery.addSelectColumn(Column.getColumn("MdDeviceInfo", "SERIAL_NUMBER"));
        sQuery.addSelectColumn(Column.getColumn("MdDeviceInfo", "IS_MULTIUSER"));
        sQuery.addSelectColumn(Column.getColumn("MdDeviceInfo", "IS_PROFILEOWNER"));
        sQuery.addSelectColumn(Column.getColumn("ManagedDeviceExtn", "NAME"));
        sQuery.addSelectColumn(Column.getColumn("ManagedDevice", "PLATFORM_TYPE"));
        sQuery.addSelectColumn(Column.getColumn("ManagedDevice", "AGENT_VERSION"));
        sQuery.addSelectColumn(Column.getColumn("MdOSDetailsTemp", "OS_VERSION"));
        sQuery.addSelectColumn(Column.getColumn("MdOSDetailsTemp", "RESOURCE_ID"));
        sQuery.addSelectColumn(Column.getColumn("ManagedDevice", "AGENT_VERSION_CODE"));
        sQuery.addSelectColumn(Column.getColumn("IOSNativeAppStatus", "RESOURCE_ID"));
        sQuery.addSelectColumn(Column.getColumn("LocationDeviceStatus", "IS_ENABLED"));
        sQuery.addSelectColumn(Column.getColumn("ManagedKNOXContainer", "RESOURCE_ID"));
        sQuery.addSelectColumn(Column.getColumn("ManagedDeviceExtn", "MANAGED_DEVICE_ID"));
        sQuery.addSelectColumn(Column.getColumn("DeviceEnrollmentRequest", "OWNED_BY"));
        sQuery.addSelectColumn(Column.getColumn("IOSNativeAppStatus", "INSTALLATION_STATUS"));
        sQuery.addSelectColumn(Column.getColumn("ManagedKNOXContainer", "CONTAINER_STATUS"));
        sQuery.addSelectColumn(Column.getColumn("LocationDeviceStatus", "MANAGED_DEVICE_ID"));
        sQuery.addSelectColumn(Column.getColumn("Resource", "RESOURCE_ID", "RESOURCE.RESOURCE_ID"));
        sQuery.addSelectColumn(Column.getColumn("EnrollmentRequestToDevice", "MANAGED_DEVICE_ID"));
        sQuery.addSelectColumn(Column.getColumn("DeviceEnrollmentRequest", "ENROLLMENT_REQUEST_ID"));
        sQuery.addSelectColumn(Column.getColumn("EnrollmentRequestToDevice", "ENROLLMENT_REQUEST_ID"));
        sQuery.addSelectColumn(Column.getColumn("MdDeviceInfo", "IS_SUPERVISED"));
    }
    
    static {
        DeviceDetails.logger = Logger.getLogger("MDMLogger");
    }
}
