package com.me.mdm.chrome.agent.enrollment;

import java.util.Arrays;
import com.me.devicemanagement.framework.server.util.DBUtil;
import com.adventnet.persistence.DataAccessException;
import com.adventnet.ds.query.DeleteQuery;
import com.adventnet.ds.query.DeleteQueryImpl;
import com.google.api.services.directory.model.ChromeOsDevices;
import com.me.mdm.chrome.agent.ChromeDeviceManager;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.adventnet.ds.query.Join;
import com.me.mdm.chrome.agent.Context;
import com.me.mdm.chrome.agent.core.MessageUtil;
import com.me.mdm.chrome.agent.DevicerContext;
import java.util.Iterator;
import com.adventnet.persistence.DataObject;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.persistence.Row;
import com.adventnet.persistence.DataAccess;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.google.chromedevicemanagement.v1.model.BatchDeleteDevicePolicyRequest;
import java.io.IOException;
import java.util.HashSet;
import com.google.api.services.directory.model.ChromeOsDevice;
import java.util.ArrayList;
import java.util.logging.Level;
import com.google.api.client.googleapis.auth.oauth2.GoogleRefreshTokenRequest;
import java.util.Collection;
import com.me.mdm.server.apps.android.afw.GoogleForWorkSettings;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.http.HttpRequestInitializer;
import com.me.mdm.server.apps.android.afw.GoogleAPINetworkManager;
import com.google.api.client.json.jackson2.JacksonFactory;
import org.json.JSONObject;
import com.google.chromedevicemanagement.v1.ChromeDeviceManagement;
import com.google.api.services.directory.Directory;
import java.util.List;
import java.util.logging.Logger;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;

public class ChromeDeviceEnrollmentHandler
{
    private final String APPLICATION_NAME = "ManageEngine EMM/1.0";
    private final JsonFactory JSON_FACTORY;
    private HttpTransport http_Transport;
    private String enterpriseId;
    public static Logger logger;
    public static final String CHROME_SYNC_STATUS_KEY = "ChromeDeviceSyncStatus";
    public static final String QUEUED = "Queued";
    public static final String IN_PROGRESS = "InProgress";
    public static final String COMPLETED = "Completed";
    public static final String FAILED = "Failed";
    public static final String NO_STATUS = "NoStatus";
    public static final String ACCESS_TOKEN = "accessToken";
    private static final List<String> ADMIN_DIRECTORY_SCOPES;
    Directory directory;
    ChromeDeviceManagement chromeDeviceMgmt;
    JSONObject responseJSON;
    JSONObject esaJSON;
    String templateToken;
    
    public ChromeDeviceEnrollmentHandler(final JSONObject integDetails) throws Exception {
        this.JSON_FACTORY = (JsonFactory)JacksonFactory.getDefaultInstance();
        this.http_Transport = null;
        this.enterpriseId = null;
        this.directory = null;
        this.responseJSON = null;
        this.esaJSON = null;
        this.templateToken = null;
        this.http_Transport = GoogleAPINetworkManager.getGoogleAPINetworkManager().getHttpTransportWithProxyConfigured();
        final String accessToken = integDetails.optString("accessToken");
        this.enterpriseId = String.valueOf(integDetails.get("ENTERPRISE_ID"));
        final GoogleCredential credential = this.getGoogleCredential(integDetails);
        if (accessToken != null && !accessToken.isEmpty()) {
            credential.setAccessToken(accessToken);
        }
        this.chromeDeviceMgmt = new ChromeDeviceManagement.Builder(this.http_Transport, this.JSON_FACTORY, (HttpRequestInitializer)credential).setApplicationName("ManageEngine EMM/1.0").build();
        this.directory = new Directory.Builder(this.http_Transport, this.JSON_FACTORY, (HttpRequestInitializer)credential).setApplicationName("ManageEngine EMM/1.0").build();
        this.esaJSON = integDetails;
        this.templateToken = integDetails.optString("TEMPLATE_TOKEN");
    }
    
    private GoogleCredential getGoogleCredential(final JSONObject jsonObject) throws Exception {
        if (jsonObject.getInt("OAUTH_TYPE") == GoogleForWorkSettings.OAUTH_TYPE_ESA) {
            final String credentialFilePath = String.valueOf(jsonObject.get("ESA_CREDENTIAL_JSON_PATH"));
            final String esaEMailAddress = String.valueOf(jsonObject.get("ESA_EMAIL_ID"));
            final String serviceAccountUser = String.valueOf(jsonObject.get("DOMAIN_ADMIN_EMAIL_ID"));
            return new GoogleCredential.Builder().setJsonFactory(this.JSON_FACTORY).setServiceAccountId(esaEMailAddress).setServiceAccountPrivateKey(GoogleForWorkSettings.getPrivateKeyFromCredentialJSONFile(credentialFilePath)).setTransport(this.http_Transport).setServiceAccountScopes((Collection)ChromeDeviceEnrollmentHandler.ADMIN_DIRECTORY_SCOPES).setServiceAccountUser(serviceAccountUser).build();
        }
        final JSONObject bearerJSON = jsonObject.getJSONObject("GoogleBearerTokenDetails");
        final String refreshToken = String.valueOf(bearerJSON.get("REFRESH_TOKEN"));
        final String clientId = String.valueOf(bearerJSON.get("CLIENT_ID"));
        final String clientSecret = String.valueOf(bearerJSON.get("CLIENT_SECRET"));
        final GoogleRefreshTokenRequest request = new GoogleRefreshTokenRequest(this.http_Transport, this.JSON_FACTORY, refreshToken, clientId, clientSecret);
        return new GoogleCredential().setAccessToken(request.execute().getAccessToken());
    }
    
    public void resetAllChromeDevices() throws Exception {
        final Long customerId = this.esaJSON.getLong("CUSTOMER_ID");
        ChromeDeviceEnrollmentHandler.logger.log(Level.INFO, "Reset all Chrome devices for {0}", customerId);
        try {
            this.resetAllProfiles(customerId);
        }
        catch (final Exception e) {
            ChromeDeviceEnrollmentHandler.logger.log(Level.WARNING, e, () -> "Cannot reset Chrome profiles when removing for customer " + n);
        }
        this.deleteChromeDeviceStatus(customerId);
    }
    
    public void syncChromeDevices() throws Exception {
        final Long customerId = this.esaJSON.getLong("CUSTOMER_ID");
        final HashSet<ChromeOsDevice> enrolledDeviceList = this.assimilateEnrolledDevices();
        this.handleEnrolledDevices(customerId, new ArrayList<ChromeOsDevice>(enrolledDeviceList));
        final HashSet<ChromeOsDevice> unenrolledDeviceList = this.assimilateUnenrolledDevices();
        this.handleUnmanagedDevices(customerId, new ArrayList<ChromeOsDevice>(unenrolledDeviceList));
    }
    
    private HashSet<ChromeOsDevice> assimilateEnrolledDevices() throws IOException {
        final String[] enrollStatuses = { "ACTIVE", "DISABLED" };
        return this.assimilateChromeDevices(enrollStatuses, "BASIC");
    }
    
    private HashSet<ChromeOsDevice> assimilateUnenrolledDevices() throws IOException {
        final String[] unenrollStatuses = { "DELINQUENT", "DEPROVISIONED", "INACTIVE" };
        return this.assimilateChromeDevices(unenrollStatuses, "BASIC");
    }
    
    private HashSet<ChromeOsDevice> assimilateChromeDevices(final String[] statuses, final String projection) throws IOException {
        final HashSet<ChromeOsDevice> totalList = new HashSet<ChromeOsDevice>();
        for (final String status : statuses) {
            final List<ChromeOsDevice> chromeDeviceList = this.getChromeOsDevices("status:" + status, projection);
            if (chromeDeviceList != null) {
                totalList.addAll((Collection<?>)chromeDeviceList);
            }
            ChromeDeviceEnrollmentHandler.logger.log(Level.INFO, "Chrome device for status {0} : {1}", new Object[] { status, chromeDeviceList });
        }
        return totalList;
    }
    
    private void resetAllProfiles(final Long customerId) throws Exception {
        final List totalist = this.getManagedList(customerId);
        totalist.addAll(this.getUnManagedList(customerId));
        this.removeExistingProfiles(totalist);
    }
    
    private void removeExistingProfiles(final List totalist) throws Exception {
        int fromIndex = 0;
        final int limit = 50;
        for (int toIndex = 0; fromIndex < totalist.size(); fromIndex = toIndex) {
            if (fromIndex + limit < totalist.size()) {
                toIndex = fromIndex + limit;
            }
            else {
                toIndex = fromIndex + (totalist.size() - fromIndex);
            }
            final BatchDeleteDevicePolicyRequest req = new BatchDeleteDevicePolicyRequest().setDeviceIds((List)totalist.subList(fromIndex, toIndex));
            this.chromeDeviceMgmt.enterprises().devices().batchDeleteDevicePolicy("enterprises/" + this.enterpriseId, req).execute();
        }
    }
    
    private void handleUnmanagedDevices(final Long customerId, final List<ChromeOsDevice> syncedUnenrolledDevices) throws Exception {
        if (syncedUnenrolledDevices != null && !syncedUnenrolledDevices.isEmpty()) {
            final List<String> udidList = this.getDeviceIdList(syncedUnenrolledDevices);
            final SelectQuery sQuery = (SelectQuery)new SelectQueryImpl(new Table("ChromeDeviceManagedStatus"));
            final Criteria customerCriteria = new Criteria(new Column("ChromeDeviceManagedStatus", "CUSTOMER_ID"), (Object)customerId, 0);
            final Criteria udidsCriteria = new Criteria(new Column("ChromeDeviceManagedStatus", "UDID"), (Object)udidList.toArray(), 8);
            sQuery.addSelectColumn(new Column((String)null, "*"));
            sQuery.setCriteria(customerCriteria.and(udidsCriteria));
            final DataObject dO = DataAccess.get(sQuery);
            final List toBeUnenrolledList = new ArrayList();
            final List toBeDeletedDuplicateEntries = new ArrayList();
            for (ChromeOsDevice syncedUnenrolledDevice : syncedUnenrolledDevices) {
                final String syncedUnenrolledUDID = syncedUnenrolledDevice.getDeviceId();
                final Criteria udidCriteria = new Criteria(new Column("ChromeDeviceManagedStatus", "UDID"), (Object)syncedUnenrolledUDID, 0);
                final Iterator existingDevicesForUDIDItr = dO.getRows("ChromeDeviceManagedStatus", udidCriteria);
                Boolean deviceAlreadyExists = false;
                Boolean tobeUnenrolled = false;
                while (existingDevicesForUDIDItr.hasNext()) {
                    final Row existingDeviceForUDID = existingDevicesForUDIDItr.next();
                    if (deviceAlreadyExists) {
                        final Long toBeDeletedId = (Long)existingDeviceForUDID.get("RESOURCE_ID");
                        toBeDeletedDuplicateEntries.add(toBeDeletedId);
                        ChromeDeviceEnrollmentHandler.logger.log(Level.INFO, "Marking duplicate entry {0} for deletion", existingDeviceForUDID);
                    }
                    deviceAlreadyExists = true;
                    if (existingDeviceForUDID.get("MANAGED_STATUS").equals(ChromeEnrollmentUtil.CHROME_STATUS_ENROLLED)) {
                        tobeUnenrolled = true;
                        ChromeDeviceEnrollmentHandler.logger.log(Level.INFO, "Device {0} in enrolled state previously, unenroll now", existingDeviceForUDID);
                    }
                }
                if (tobeUnenrolled) {
                    syncedUnenrolledDevice = this.prepareMessageForUnenrolledDevice(syncedUnenrolledDevice);
                    toBeUnenrolledList.add(syncedUnenrolledDevice.getDeviceId());
                }
            }
            this.deleteDuplicateChromeDevices(toBeDeletedDuplicateEntries, customerId);
            ChromeDeviceEnrollmentHandler.logger.log(Level.INFO, "To be unenrolled devices {0}", toBeUnenrolledList);
            ChromeEnrollmentUtil.getInstance().addOrUpdateChromeDeviceStatus(customerId, toBeUnenrolledList, ChromeEnrollmentUtil.CHROME_STATUS_UNENROLLED);
        }
    }
    
    private ChromeOsDevice prepareMessageForUnenrolledDevice(final ChromeOsDevice syncedUnenrolledDevice) throws Exception {
        final Context context = new DevicerContext(syncedUnenrolledDevice.getDeviceId(), this.esaJSON);
        final MessageUtil msgUtil = new MessageUtil(context);
        msgUtil.messageType = "RemoveDevice";
        final String enrollRemarks = this.getUnManagedRemarksForStatus(syncedUnenrolledDevice.getStatus());
        msgUtil.setMsgRemarks(enrollRemarks);
        msgUtil.postMessageData();
        return syncedUnenrolledDevice;
    }
    
    private List<String> getDeviceIdList(final List<ChromeOsDevice> chromeDevices) {
        final List<String> deviceIdList = new ArrayList<String>();
        if (chromeDevices != null && !chromeDevices.isEmpty()) {
            for (final ChromeOsDevice device : chromeDevices) {
                deviceIdList.add(device.getDeviceId());
            }
        }
        return deviceIdList;
    }
    
    private void handleEnrolledDevices(final Long customerId, final List<ChromeOsDevice> syncedEnrolledDevicelist) throws Exception {
        if (syncedEnrolledDevicelist != null && !syncedEnrolledDevicelist.isEmpty()) {
            final List<String> udidList = this.getDeviceIdList(syncedEnrolledDevicelist);
            final SelectQuery sQuery = (SelectQuery)new SelectQueryImpl(new Table("ChromeDeviceManagedStatus"));
            final Criteria customerCriteria = new Criteria(new Column("ChromeDeviceManagedStatus", "CUSTOMER_ID"), (Object)customerId, 0);
            final Criteria udidsCriteria = new Criteria(new Column("ChromeDeviceManagedStatus", "UDID"), (Object)udidList.toArray(), 8);
            sQuery.addSelectColumn(new Column((String)null, "*"));
            sQuery.setCriteria(customerCriteria.and(udidsCriteria));
            final DataObject dO = DataAccess.get(sQuery);
            final List toBeEnrolledList = new ArrayList();
            final List toBeDeletedDuplicateEntries = new ArrayList();
            for (ChromeOsDevice syncedEnrolledDevice : syncedEnrolledDevicelist) {
                final String syncedEnrolledUDID = syncedEnrolledDevice.getDeviceId();
                final Criteria udidCriteria = new Criteria(new Column("ChromeDeviceManagedStatus", "UDID"), (Object)syncedEnrolledUDID, 0);
                final Iterator existingDevicesForUDIDItr = dO.getRows("ChromeDeviceManagedStatus", udidCriteria);
                Boolean deviceAlreadyExists = false;
                Boolean tobeEnrolled = false;
                Boolean deviceStuckInEnrollment = false;
                while (existingDevicesForUDIDItr.hasNext()) {
                    final Row existingDeviceForUDID = existingDevicesForUDIDItr.next();
                    final Integer managedStatus = (Integer)existingDeviceForUDID.get("MANAGED_STATUS");
                    if (deviceAlreadyExists) {
                        final Long toBeDeletedId = (Long)existingDeviceForUDID.get("RESOURCE_ID");
                        toBeDeletedDuplicateEntries.add(toBeDeletedId);
                        ChromeDeviceEnrollmentHandler.logger.log(Level.INFO, "Marking duplicate entry {0} for deletion", existingDeviceForUDID);
                    }
                    deviceAlreadyExists = true;
                    if (managedStatus.equals(ChromeEnrollmentUtil.CHROME_STATUS_UNENROLLED)) {
                        tobeEnrolled = true;
                        ChromeDeviceEnrollmentHandler.logger.log(Level.INFO, "Device {0} in unenrolled state previously, enroll now", existingDeviceForUDID);
                    }
                    if (managedStatus.equals(ChromeEnrollmentUtil.CHROME_STATUS_ENROLLED)) {
                        deviceStuckInEnrollment = this.checkIfDeviceStuckInEnrollment((String)existingDeviceForUDID.get("UDID"));
                    }
                }
                if (!deviceAlreadyExists || tobeEnrolled || deviceStuckInEnrollment) {
                    syncedEnrolledDevice = this.prepareMessageForEnrolledDevice(syncedEnrolledDevice, customerId);
                    toBeEnrolledList.add(syncedEnrolledDevice.getDeviceId());
                }
            }
            this.deleteDuplicateChromeDevices(toBeDeletedDuplicateEntries, customerId);
            ChromeDeviceEnrollmentHandler.logger.log(Level.INFO, "To be enrolled devices {0}", toBeEnrolledList);
        }
    }
    
    private Boolean checkIfDeviceStuckInEnrollment(final String deviceUDID) {
        Boolean deviceStuckInEnrollment = false;
        try {
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("ChromeDeviceManagedStatus"));
            selectQuery.addJoin(new Join("ChromeDeviceManagedStatus", "ManagedDevice", new String[] { "UDID" }, new String[] { "UDID" }, 2));
            final Criteria udidCriteria = new Criteria(new Column("ChromeDeviceManagedStatus", "UDID"), (Object)deviceUDID, 0);
            final Criteria managedStatusCriteria = new Criteria(new Column("ChromeDeviceManagedStatus", "MANAGED_STATUS"), (Object)ChromeEnrollmentUtil.CHROME_STATUS_ENROLLED, 0);
            selectQuery.addSelectColumn(Column.getColumn("ChromeDeviceManagedStatus", "*"));
            selectQuery.addSelectColumn(Column.getColumn("ManagedDevice", "UDID"));
            selectQuery.addSelectColumn(Column.getColumn("ManagedDevice", "RESOURCE_ID"));
            selectQuery.addSelectColumn(Column.getColumn("ManagedDevice", "MANAGED_STATUS"));
            selectQuery.setCriteria(udidCriteria.and(managedStatusCriteria));
            final DataObject dataObject = MDMUtil.getPersistence().get(selectQuery);
            if (dataObject == null || dataObject.isEmpty()) {
                deviceStuckInEnrollment = true;
            }
            else {
                final Row row = dataObject.getFirstRow("ManagedDevice");
                final int deviceManagedStatus = (int)row.get("MANAGED_STATUS");
                ChromeDeviceEnrollmentHandler.logger.log(Level.INFO, "Managed status of device {0} in table MANAGEDDEVICE is {1}", new Object[] { deviceUDID, deviceManagedStatus });
                if (deviceManagedStatus == 0 || deviceManagedStatus == 1) {
                    deviceStuckInEnrollment = true;
                }
            }
            if (deviceStuckInEnrollment) {
                ChromeDeviceEnrollmentHandler.logger.log(Level.SEVERE, "Device {0} got stuck in Enrollment ,so processing Enrollment again ", deviceUDID);
            }
        }
        catch (final Exception ex) {
            ChromeDeviceEnrollmentHandler.logger.log(Level.SEVERE, "Exception while checking if device stuck in Enrollment ", ex);
        }
        return deviceStuckInEnrollment;
    }
    
    private ChromeOsDevice prepareMessageForEnrolledDevice(ChromeOsDevice syncedEnrolledDevice, final Long customerId) throws Exception {
        syncedEnrolledDevice = (ChromeOsDevice)this.directory.chromeosdevices().get(this.enterpriseId, syncedEnrolledDevice.getDeviceId()).setProjection("FULL").execute();
        ChromeDeviceEnrollmentHandler.logger.log(Level.INFO, "PrepareMessageForEnrolledDevice {0}", syncedEnrolledDevice);
        final JSONObject enrollmentData = new JSONObject();
        enrollmentData.put("UDID", (Object)syncedEnrolledDevice.getDeviceId());
        enrollmentData.put("UserName", (Object)syncedEnrolledDevice.getAnnotatedUser());
        enrollmentData.put("Model", (Object)syncedEnrolledDevice.getModel());
        enrollmentData.put("SerialNumber", (Object)syncedEnrolledDevice.getSerialNumber());
        enrollmentData.put("OSVersion", (Object)syncedEnrolledDevice.getOsVersion());
        enrollmentData.put("CUSTOMER_ID", (Object)customerId);
        enrollmentData.put("TEMPLATE_TOKEN", (Object)this.templateToken);
        final Context context = new DevicerContext(syncedEnrolledDevice.getDeviceId(), this.esaJSON);
        ChromeDeviceManager.getInstance().getEnrollmentProcessor().processEnrollment(context, enrollmentData);
        ChromeDeviceManager.getInstance().getPrivacyManager().getPrivacyStatus(context);
        return syncedEnrolledDevice;
    }
    
    private List<ChromeOsDevice> getChromeOsDevices(final String query, final String projection) throws IOException {
        String nextPageToken = null;
        final List<ChromeOsDevice> deviceList = new ArrayList<ChromeOsDevice>();
        do {
            final Directory.Chromeosdevices.List list = this.directory.chromeosdevices().list(this.enterpriseId).setMaxResults(Integer.valueOf(50)).setProjection(projection);
            list.setQuery(query);
            if (nextPageToken != null) {
                list.setPageToken(nextPageToken);
            }
            final ChromeOsDevices devices = (ChromeOsDevices)list.execute();
            final List<ChromeOsDevice> devicesTmpList = devices.getChromeosdevices();
            if (devicesTmpList != null) {
                deviceList.addAll(devicesTmpList);
            }
            nextPageToken = devices.getNextPageToken();
        } while (nextPageToken != null && !nextPageToken.isEmpty());
        return deviceList;
    }
    
    private void deleteDuplicateChromeDevices(final List toBeDeletedIds, final Long customerId) throws DataAccessException {
        if (!toBeDeletedIds.isEmpty()) {
            final DeleteQuery dQuery = (DeleteQuery)new DeleteQueryImpl("ChromeDeviceManagedStatus");
            final Criteria udidsCriteria = new Criteria(Column.getColumn("ChromeDeviceManagedStatus", "RESOURCE_ID"), (Object)toBeDeletedIds.toArray(), 8);
            final Criteria customerCriteria = new Criteria(Column.getColumn("ChromeDeviceManagedStatus", "CUSTOMER_ID"), (Object)customerId, 0);
            dQuery.setCriteria(udidsCriteria.and(customerCriteria));
            DataAccess.delete(dQuery);
        }
    }
    
    private List<String> getManagedList(final Long customerId) throws Exception {
        List<String> managedList = new ArrayList<String>();
        final SelectQuery sQuery = (SelectQuery)new SelectQueryImpl(new Table("ChromeDeviceManagedStatus"));
        final Criteria customerCriteria = new Criteria(new Column("ChromeDeviceManagedStatus", "CUSTOMER_ID"), (Object)customerId, 0);
        final Criteria statusCriteria = new Criteria(new Column("ChromeDeviceManagedStatus", "MANAGED_STATUS"), (Object)ChromeEnrollmentUtil.CHROME_STATUS_ENROLLED, 0);
        sQuery.addSelectColumn(new Column((String)null, "*"));
        sQuery.setCriteria(statusCriteria.and(customerCriteria));
        final DataObject dO = DataAccess.get(sQuery);
        if (!dO.isEmpty()) {
            final Iterator it = dO.getRows("ChromeDeviceManagedStatus");
            managedList = DBUtil.getColumnValuesAsList(it, "UDID");
        }
        return managedList;
    }
    
    private List<String> getUnManagedList(final Long customerId) throws DataAccessException, Exception {
        List<String> unmanagedList = new ArrayList<String>();
        final SelectQuery sQuery = (SelectQuery)new SelectQueryImpl(new Table("ChromeDeviceManagedStatus"));
        final Criteria customerCriteria = new Criteria(new Column("ChromeDeviceManagedStatus", "CUSTOMER_ID"), (Object)customerId, 0);
        final Criteria statusCriteria = new Criteria(new Column("ChromeDeviceManagedStatus", "MANAGED_STATUS"), (Object)ChromeEnrollmentUtil.CHROME_STATUS_UNENROLLED, 0);
        sQuery.addSelectColumn(new Column((String)null, "*"));
        sQuery.setCriteria(statusCriteria.and(customerCriteria));
        final DataObject dO = DataAccess.get(sQuery);
        if (!dO.isEmpty()) {
            final Iterator it = dO.getRows("ChromeDeviceManagedStatus");
            unmanagedList = DBUtil.getColumnValuesAsList(it, "UDID");
        }
        return unmanagedList;
    }
    
    private String getUnManagedRemarksForStatus(final String status) {
        if (status.equalsIgnoreCase("DEPROVISIONED")) {
            return "Device Deprovisioned from GSuite";
        }
        if (status.equalsIgnoreCase("INACTIVE")) {
            return "Device in Inactive State!";
        }
        if (status.equalsIgnoreCase("DELINQUENT")) {
            return "No Suffecient License to Manage Chrome Devices in GSUite";
        }
        return "Device Unmanaged - Unknown reason !";
    }
    
    private void deleteChromeDeviceStatus(final Long customerId) throws DataAccessException {
        final DeleteQuery dQuery = (DeleteQuery)new DeleteQueryImpl("ChromeDeviceManagedStatus");
        final Criteria customerCriteria = new Criteria(new Column("ChromeDeviceManagedStatus", "CUSTOMER_ID"), (Object)customerId, 0);
        dQuery.setCriteria(customerCriteria);
        DataAccess.delete(dQuery);
    }
    
    static {
        ChromeDeviceEnrollmentHandler.logger = Logger.getLogger("MDMEnrollment");
        ADMIN_DIRECTORY_SCOPES = Arrays.asList("https://www.googleapis.com/auth/admin.directory.user", "https://www.googleapis.com/auth/admin.directory.device.chromeos", "https://www.googleapis.com/auth/chromedevicemanagementapi");
    }
}
