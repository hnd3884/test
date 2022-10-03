package com.me.mdm.server.privacy;

import com.adventnet.sym.server.mdm.command.DeviceInvCommandHandler;
import java.util.Collection;
import com.adventnet.sym.server.mdm.core.ManagedDeviceHandler;
import com.adventnet.i18n.I18N;
import com.me.mdm.server.resource.MDMResourceDataPopulator;
import com.adventnet.ds.query.DeleteQuery;
import com.adventnet.persistence.DataAccess;
import com.adventnet.ds.query.DeleteQueryImpl;
import com.adventnet.ds.query.UpdateQuery;
import com.adventnet.ds.query.UpdateQueryImpl;
import com.me.mdm.server.notification.NotificationHandler;
import com.adventnet.sym.server.mdm.command.DeviceCommandRepository;
import com.adventnet.persistence.DataObject;
import com.adventnet.ds.query.SelectQuery;
import com.me.devicemanagement.framework.server.util.DBUtil;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.logging.Level;
import org.json.JSONException;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.logging.Logger;

public class PrivacySettingListener
{
    Logger logger;
    private static PrivacySettingListener listener;
    private static final String[] CHANGE_CHECK;
    
    public PrivacySettingListener() {
        this.logger = Logger.getLogger("MDMLogger");
    }
    
    public static PrivacySettingListener getInstance() {
        if (PrivacySettingListener.listener == null) {
            PrivacySettingListener.listener = new PrivacySettingListener();
        }
        return PrivacySettingListener.listener;
    }
    
    public void invokePrivacySettingsChange(final JSONObject newSettings, final Long customerId) throws JSONException {
        final List removalList = this.getRemovalList(newSettings);
        final JSONArray ownedByArray = (JSONArray)newSettings.get("applicable_for");
        for (int i = 0; i < ownedByArray.length(); ++i) {
            final int ownedBy = (int)ownedByArray.get(i);
            this.applySettingsChange(removalList, ownedBy, customerId, newSettings);
        }
        this.initiateSyncPrivacyCommand(customerId);
    }
    
    public void invokePrivacyCustomMessageChange(final Long customerId) {
        this.initiateSyncPrivacyCommand(customerId);
    }
    
    public void invokeOwnedByChange(final Long resourceId, final int ownedBy) {
        try {
            final JSONObject privacyMap = new PrivacySettingsHandler().getPrivacySettingsJSON(resourceId);
            final List removalList = this.getRemovalList(privacyMap);
            this.applySettingsChange(removalList, resourceId, privacyMap);
            this.initiateSyncPrivacyCommandForDevice(resourceId);
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Exception while invokeOwnedByColumnChange", e);
        }
    }
    
    private List getRemovalList(final JSONObject newSettings) throws JSONException {
        final List removeCorporateList = new ArrayList();
        for (final String columnName : PrivacySettingListener.CHANGE_CHECK) {
            final int fetchNew = newSettings.getInt(columnName);
            if (fetchNew == 2) {
                removeCorporateList.add(columnName);
            }
        }
        return removeCorporateList;
    }
    
    private void applySettingsChange(final List removalList, final Long resourceId, final JSONObject newSettings) {
        if (removalList.isEmpty()) {
            return;
        }
        for (final String s : removalList) {
            final String columnName = s;
            switch (s) {
                case "fetch_phone_number": {
                    this.removePhoneNumber(resourceId);
                    continue;
                }
                case "fetch_installed_app": {
                    this.removeInstalledApp(resourceId);
                    continue;
                }
                case "fetch_device_name": {
                    this.removeDeviceName(resourceId, newSettings);
                    continue;
                }
                case "fetch_location": {
                    this.removeDeviceLocation(resourceId);
                    continue;
                }
                case "recent_users_report": {
                    this.removeDeviceRecentUser(resourceId);
                    continue;
                }
                case "fetch_mac_address": {
                    this.removeDeviceMacAddress(resourceId);
                    continue;
                }
                case "fetch_user_installed_certs": {
                    this.addCertificateInfoCmd(resourceId);
                    continue;
                }
                case "fetch_wifi_ssid": {
                    this.removeDeviceWifiSSID(resourceId);
                    continue;
                }
            }
        }
    }
    
    private void applySettingsChange(final List removalList, final int ownedBy, final Long customerId, final JSONObject newSettings) {
        if (removalList.isEmpty()) {
            return;
        }
        for (final String s : removalList) {
            final String columnName = s;
            switch (s) {
                case "fetch_phone_number": {
                    this.removePhoneNumber(ownedBy, customerId);
                    continue;
                }
                case "fetch_installed_app": {
                    this.removeInstalledApp(ownedBy, customerId);
                    continue;
                }
                case "fetch_device_name": {
                    this.removeDeviceName(ownedBy, customerId, newSettings);
                    continue;
                }
                case "fetch_location": {
                    this.removeDeviceLocation(ownedBy, customerId);
                    continue;
                }
                case "recent_users_report": {
                    this.removeDeviceRecentUser(ownedBy, customerId);
                }
                case "fetch_mac_address": {
                    this.removeDeviceMacAddress(ownedBy, customerId);
                    continue;
                }
                case "fetch_user_installed_certs": {
                    this.addCertificateInfoCmd(ownedBy, customerId);
                    continue;
                }
                case "fetch_wifi_ssid": {
                    this.removeDeviceWifiSSID(ownedBy, customerId);
                    continue;
                }
            }
        }
    }
    
    private void removePhoneNumber(final Long resourceId) {
        this.removePhoneNumber(0, null, resourceId);
    }
    
    private void removePhoneNumber(final int ownedBy, final Long customerId) {
        this.removePhoneNumber(ownedBy, customerId, null);
    }
    
    private void removeInstalledApp(final Long resourceId) {
        this.removeInstalledApp(0, null, resourceId);
    }
    
    private void removeInstalledApp(final int ownedBy, final Long customerId) {
        this.removeInstalledApp(ownedBy, customerId, null);
    }
    
    private void removeDeviceLocation(final int ownedBy, final Long customerId) {
        this.removeDeviceLocation(ownedBy, customerId, null);
    }
    
    private void removeDeviceMacAddress(final int ownedBy, final Long customerId) {
        this.removeDeviceMacAddress(ownedBy, customerId, null);
    }
    
    private void removeDeviceWifiSSID(final int ownedBy, final Long customerId) {
        this.removeDeviceWifiSSID(ownedBy, customerId, null);
    }
    
    private void addCertificateInfoCmd(final int ownedBy, final Long customerId) {
        try {
            final SelectQuery selQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("DeviceEnrollmentRequest"));
            selQuery.addJoin(new Join("DeviceEnrollmentRequest", "EnrollmentRequestToDevice", new String[] { "ENROLLMENT_REQUEST_ID" }, new String[] { "ENROLLMENT_REQUEST_ID" }, 2));
            selQuery.addJoin(new Join("EnrollmentRequestToDevice", "Resource", new String[] { "MANAGED_DEVICE_ID" }, new String[] { "RESOURCE_ID" }, 2));
            final Criteria customerCriteria = new Criteria(new Column("Resource", "CUSTOMER_ID"), (Object)customerId, 0);
            final Criteria ownedByCriteria = new Criteria(new Column("DeviceEnrollmentRequest", "OWNED_BY"), (Object)ownedBy, 0);
            final Criteria iOsCriteria = new Criteria(new Column("DeviceEnrollmentRequest", "PLATFORM_TYPE"), (Object)1, 0);
            selQuery.setCriteria(customerCriteria.and(ownedByCriteria).and(iOsCriteria));
            selQuery.addSelectColumn(Column.getColumn("Resource", "RESOURCE_ID"));
            final DataObject dO = MDMUtil.getPersistence().get(selQuery);
            if (!dO.isEmpty()) {
                final List resourceList = DBUtil.getColumnValuesAsList(dO.getRows("Resource"), "RESOURCE_ID");
                this.addCertificateInfoCmd(resourceList);
            }
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, ex, () -> "Exception while add CertificateInfo Cmd.. for customer: " + n + " ownedBy: " + n2);
        }
    }
    
    private void removeDeviceLocation(final Long resourceId) {
        this.removeDeviceLocation(0, null, resourceId);
    }
    
    private void removeDeviceMacAddress(final Long resourceId) {
        this.removeDeviceMacAddress(0, null, resourceId);
    }
    
    private void removeDeviceWifiSSID(final Long resourceId) {
        this.removeDeviceWifiSSID(0, null, resourceId);
    }
    
    private void addCertificateInfoCmd(final Long resourceId) {
        try {
            final int platformType = (int)DBUtil.getValueFromDB("ManagedDevice", "RESOURCE_ID", (Object)resourceId, "PLATFORM_TYPE");
            if (platformType == 1) {
                final List resourceList = new ArrayList();
                resourceList.add(resourceId);
                this.addCertificateInfoCmd(resourceList);
            }
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, ex, () -> "Exception while add CertificateInfo Cmd.. for resource: " + n);
        }
    }
    
    private void addCertificateInfoCmd(final List resourceList) {
        try {
            final Long commandId = DeviceCommandRepository.getInstance().addCommand("CertificateList");
            DeviceCommandRepository.getInstance().assignCommandToDevices(commandId, resourceList);
            NotificationHandler.getInstance().SendNotification(resourceList);
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, ex, () -> "Exception while add CertificateInfo Cmd.. for resource: " + list.toArray());
        }
    }
    
    private void removeDeviceName(final Long resourceId, final JSONObject newSettings) {
        this.removeDeviceName(0, null, resourceId, newSettings);
    }
    
    private void removeDeviceName(final int ownedBy, final Long customerId, final JSONObject newSettings) {
        this.removeDeviceName(ownedBy, customerId, null, newSettings);
    }
    
    private void removeDeviceRecentUser(final Long resourceId) {
        this.removeDeviceRecentUser(0, null, resourceId);
    }
    
    private void removeDeviceRecentUser(final int ownedBy, final Long customerId) {
        this.removeDeviceRecentUser(ownedBy, customerId, null);
    }
    
    private void removePhoneNumber(final int ownedBy, final Long customerId, final Long resourceId) {
        try {
            final UpdateQuery uQuery = (UpdateQuery)new UpdateQueryImpl("MdSIMInfo");
            uQuery.addJoin(new Join("MdSIMInfo", "ManagedDevice", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2));
            uQuery.addJoin(new Join("ManagedDevice", "Resource", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2));
            uQuery.addJoin(new Join("ManagedDevice", "EnrollmentRequestToDevice", new String[] { "RESOURCE_ID" }, new String[] { "MANAGED_DEVICE_ID" }, 2));
            uQuery.addJoin(new Join("EnrollmentRequestToDevice", "DeviceEnrollmentRequest", new String[] { "ENROLLMENT_REQUEST_ID" }, new String[] { "ENROLLMENT_REQUEST_ID" }, 2));
            Criteria criteria = null;
            if (resourceId == null) {
                final Criteria cOwnedBy = new Criteria(new Column("DeviceEnrollmentRequest", "OWNED_BY"), (Object)ownedBy, 0);
                final Criteria cCustomerId = new Criteria(new Column("Resource", "CUSTOMER_ID"), (Object)customerId, 0);
                criteria = cOwnedBy.and(cCustomerId);
            }
            else {
                criteria = new Criteria(new Column("Resource", "RESOURCE_ID"), (Object)resourceId, 0);
            }
            uQuery.setCriteria(criteria);
            uQuery.setUpdateColumn("PHONE_NUMBER", (Object)"");
            MDMUtil.getPersistence().update(uQuery);
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Exception while removePhoneNumber", e);
        }
    }
    
    private void removeDeviceRecentUser(final int ownedBy, final Long customerId, final Long resourceId) {
        try {
            final DeleteQuery deleteQuery = (DeleteQuery)new DeleteQueryImpl("MdDeviceRecentUsersInfo");
            deleteQuery.addJoin(new Join("MdDeviceRecentUsersInfo", "ManagedDevice", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2));
            deleteQuery.addJoin(new Join("ManagedDevice", "Resource", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2));
            deleteQuery.addJoin(new Join("ManagedDevice", "EnrollmentRequestToDevice", new String[] { "RESOURCE_ID" }, new String[] { "MANAGED_DEVICE_ID" }, 2));
            deleteQuery.addJoin(new Join("EnrollmentRequestToDevice", "DeviceEnrollmentRequest", new String[] { "ENROLLMENT_REQUEST_ID" }, new String[] { "ENROLLMENT_REQUEST_ID" }, 2));
            Criteria criteria = null;
            if (resourceId == null) {
                final Criteria cOwnedBy = new Criteria(new Column("DeviceEnrollmentRequest", "OWNED_BY"), (Object)ownedBy, 0);
                final Criteria cCustomerId = new Criteria(new Column("Resource", "CUSTOMER_ID"), (Object)customerId, 0);
                criteria = cOwnedBy.and(cCustomerId);
            }
            else {
                criteria = new Criteria(new Column("Resource", "RESOURCE_ID"), (Object)resourceId, 0);
            }
            deleteQuery.setCriteria(criteria);
            DataAccess.delete(deleteQuery);
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Exception while removeDeviceRecentUser", e);
        }
    }
    
    private void removeInstalledApp(final int ownedBy, final Long customerId, final Long resourceId) {
        try {
            final DeleteQuery deleteQuery = (DeleteQuery)new DeleteQueryImpl("MdInstalledAppResourceRel");
            deleteQuery.addJoin(new Join("MdInstalledAppResourceRel", "ManagedDevice", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2));
            deleteQuery.addJoin(new Join("ManagedDevice", "Resource", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2));
            deleteQuery.addJoin(new Join("MdInstalledAppResourceRel", "MdAppToGroupRel", new String[] { "APP_ID" }, new String[] { "APP_ID" }, 2));
            deleteQuery.addJoin(new Join("ManagedDevice", "EnrollmentRequestToDevice", new String[] { "RESOURCE_ID" }, new String[] { "MANAGED_DEVICE_ID" }, 2));
            deleteQuery.addJoin(new Join("EnrollmentRequestToDevice", "DeviceEnrollmentRequest", new String[] { "ENROLLMENT_REQUEST_ID" }, new String[] { "ENROLLMENT_REQUEST_ID" }, 2));
            final Criteria appInsCrit = new Criteria(new Column("MdInstalledAppResourceRel", "RESOURCE_ID"), (Object)new Column("MdAppCatalogToResource", "RESOURCE_ID"), 0);
            final Criteria appGrpCrit = new Criteria(new Column("MdAppToGroupRel", "APP_GROUP_ID"), (Object)new Column("MdAppCatalogToResource", "APP_GROUP_ID"), 0);
            final Join appCatJoin = new Join("MdAppToGroupRel", "MdAppCatalogToResource", appGrpCrit.and(appInsCrit), 1);
            deleteQuery.addJoin(appCatJoin);
            final Criteria cManaged = new Criteria(new Column("MdAppCatalogToResource", "APP_GROUP_ID"), (Object)null, 0);
            final Criteria cScopeBy = new Criteria(new Column("MdInstalledAppResourceRel", "SCOPE"), (Object)0, 0);
            Criteria criteria = null;
            if (resourceId == null) {
                final Criteria cOwnedBy = new Criteria(new Column("DeviceEnrollmentRequest", "OWNED_BY"), (Object)ownedBy, 0);
                final Criteria cCustomerId = new Criteria(new Column("Resource", "CUSTOMER_ID"), (Object)customerId, 0);
                criteria = cOwnedBy.and(cCustomerId);
            }
            else {
                criteria = new Criteria(new Column("Resource", "RESOURCE_ID"), (Object)resourceId, 0);
            }
            deleteQuery.setCriteria(criteria.and(cScopeBy).and(cManaged));
            DataAccess.delete(deleteQuery);
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Exception while removeInstalledApp", e);
        }
    }
    
    private void removeDeviceName(final int ownedBy, final Long customerId, final Long resourceId, final JSONObject newSettings) {
        try {
            final String devicePattern = String.valueOf(newSettings.get("DEVICE_NAME_PATTERN"));
            if (resourceId == null) {
                MDMResourceDataPopulator.removeResourceNameForDevicePrivacy(ownedBy, customerId);
                PrivacyDynamicDeviceNameHandler.getInstance().renameDeviceInExtn(ownedBy, customerId, devicePattern);
            }
            else {
                MDMResourceDataPopulator.renameResource(resourceId, I18N.getMsg("mdm.common.DEFAULT_DEVICE_NAME", new Object[0]));
                PrivacyDynamicDeviceNameHandler.getInstance().renameDeviceInExtn(null, resourceId);
            }
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Exception while removeDeviceName", e);
        }
    }
    
    private void removeDeviceLocation(final int ownedBy, final Long customerId, final Long resourceId) {
        try {
            final DeleteQuery deleteQuery = (DeleteQuery)new DeleteQueryImpl("MdDeviceLocationDetails");
            deleteQuery.addJoin(new Join("MdDeviceLocationDetails", "ManagedDevice", new String[] { "DEVICE_ID" }, new String[] { "RESOURCE_ID" }, 2));
            deleteQuery.addJoin(new Join("ManagedDevice", "Resource", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2));
            deleteQuery.addJoin(new Join("ManagedDevice", "EnrollmentRequestToDevice", new String[] { "RESOURCE_ID" }, new String[] { "MANAGED_DEVICE_ID" }, 2));
            deleteQuery.addJoin(new Join("EnrollmentRequestToDevice", "DeviceEnrollmentRequest", new String[] { "ENROLLMENT_REQUEST_ID" }, new String[] { "ENROLLMENT_REQUEST_ID" }, 2));
            Criteria criteria = null;
            if (resourceId == null) {
                final Criteria cOwnedBy = new Criteria(new Column("DeviceEnrollmentRequest", "OWNED_BY"), (Object)ownedBy, 0);
                final Criteria cCustomerId = new Criteria(new Column("Resource", "CUSTOMER_ID"), (Object)customerId, 0);
                criteria = cOwnedBy.and(cCustomerId);
            }
            else {
                criteria = new Criteria(new Column("Resource", "RESOURCE_ID"), (Object)resourceId, 0);
            }
            deleteQuery.setCriteria(criteria);
            DataAccess.delete(deleteQuery);
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Exception while removeDeviceLocation", e);
        }
    }
    
    private void removeDeviceMacAddress(final int ownedBy, final Long customerId, final Long resourceId) {
        try {
            final UpdateQuery updateQuery = (UpdateQuery)new UpdateQueryImpl("MdNetworkInfo");
            updateQuery.addJoin(new Join("MdNetworkInfo", "Resource", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2));
            updateQuery.addJoin(new Join("Resource", "EnrollmentRequestToDevice", new String[] { "RESOURCE_ID" }, new String[] { "MANAGED_DEVICE_ID" }, 2));
            updateQuery.addJoin(new Join("EnrollmentRequestToDevice", "DeviceEnrollmentRequest", new String[] { "ENROLLMENT_REQUEST_ID" }, new String[] { "ENROLLMENT_REQUEST_ID" }, 2));
            Criteria criteria = null;
            if (resourceId == null) {
                final Criteria cOwnedBy = new Criteria(new Column("DeviceEnrollmentRequest", "OWNED_BY"), (Object)ownedBy, 0);
                final Criteria cCustomerId = new Criteria(new Column("Resource", "CUSTOMER_ID"), (Object)customerId, 0);
                criteria = cOwnedBy.and(cCustomerId);
            }
            else {
                criteria = new Criteria(new Column("Resource", "RESOURCE_ID"), (Object)resourceId, 0);
            }
            updateQuery.setCriteria(criteria);
            updateQuery.setUpdateColumn("BLUETOOTH_MAC", (Object)null);
            updateQuery.setUpdateColumn("WIFI_MAC", (Object)null);
            updateQuery.setUpdateColumn("ETHERNET_MACS", (Object)null);
            MDMUtil.getPersistence().update(updateQuery);
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Exception while removeDeviceMacAddress", e);
        }
    }
    
    private void removeDeviceWifiSSID(final int ownedBy, final Long customerId, final Long resourceId) {
        try {
            final UpdateQuery updateQuery = (UpdateQuery)new UpdateQueryImpl("MdNetworkInfo");
            updateQuery.addJoin(new Join("MdNetworkInfo", "Resource", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2));
            updateQuery.addJoin(new Join("Resource", "EnrollmentRequestToDevice", new String[] { "RESOURCE_ID" }, new String[] { "MANAGED_DEVICE_ID" }, 2));
            updateQuery.addJoin(new Join("EnrollmentRequestToDevice", "DeviceEnrollmentRequest", new String[] { "ENROLLMENT_REQUEST_ID" }, new String[] { "ENROLLMENT_REQUEST_ID" }, 2));
            Criteria criteria = null;
            if (resourceId == null) {
                final Criteria cOwnedBy = new Criteria(new Column("DeviceEnrollmentRequest", "OWNED_BY"), (Object)ownedBy, 0);
                final Criteria cCustomerId = new Criteria(new Column("Resource", "CUSTOMER_ID"), (Object)customerId, 0);
                criteria = cOwnedBy.and(cCustomerId);
            }
            else {
                criteria = new Criteria(new Column("Resource", "RESOURCE_ID"), (Object)resourceId, 0);
            }
            updateQuery.setCriteria(criteria);
            updateQuery.setUpdateColumn("WIFI_SSID", (Object)null);
            MDMUtil.getPersistenceLite().update(updateQuery);
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, "Exception in removeDeviceWifiSSID", ex);
        }
    }
    
    private void initiateSyncPrivacyCommand(final long customerId) {
        final List nativecmdDeviceList = new ArrayList();
        final List profilecmdDeviceList = new ArrayList();
        final List iosList = ManagedDeviceHandler.getInstance().getIosManagedDevicesForCustomer(customerId);
        if (!iosList.isEmpty()) {
            nativecmdDeviceList.addAll(iosList);
        }
        final List windowsList = ManagedDeviceHandler.getInstance().getWindowsNativeAppInstalledDeviceResId(customerId);
        if (!windowsList.isEmpty()) {
            nativecmdDeviceList.addAll(windowsList);
        }
        if (!nativecmdDeviceList.isEmpty()) {
            DeviceCommandRepository.getInstance().addSyncPrivacySettingsCommand(iosList, 2);
        }
        final List androidList = ManagedDeviceHandler.getInstance().getAndroidManagedDevicesForCustomer(customerId);
        if (!androidList.isEmpty()) {
            profilecmdDeviceList.addAll(androidList);
        }
        final List chromeList = ManagedDeviceHandler.getInstance().getManagedDevicesForCustomer(customerId, 4);
        if (!chromeList.isEmpty()) {
            profilecmdDeviceList.addAll(chromeList);
        }
        if (!profilecmdDeviceList.isEmpty()) {
            DeviceCommandRepository.getInstance().addSyncPrivacySettingsCommand(profilecmdDeviceList, 1);
        }
        DeviceInvCommandHandler.getInstance().scanAllDevices(customerId, null);
        try {
            NotificationHandler.getInstance().SendNotification(androidList, 2);
            NotificationHandler.getInstance().SendNotification(windowsList, 303);
            NotificationHandler.getInstance().SendNotification(chromeList, 4);
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Exception while send notification", e);
        }
    }
    
    private void initiateSyncPrivacyCommandForDevice(final Long resourceId) {
        final List resList = new ArrayList();
        resList.add(resourceId);
        final int platform = ManagedDeviceHandler.getInstance().getPlatformType(resourceId);
        int commandType = 2;
        if (platform == 2) {
            commandType = 1;
        }
        DeviceCommandRepository.getInstance().addSyncPrivacySettingsCommand(resList, commandType);
        if (platform == 2) {
            try {
                NotificationHandler.getInstance().SendNotification(resList, 2);
            }
            catch (final Exception e) {
                this.logger.log(Level.SEVERE, "Exception while send notification", e);
            }
        }
        else if (platform == 3) {
            try {
                NotificationHandler.getInstance().SendNotification(resList, 303);
            }
            catch (final Exception e) {
                this.logger.log(Level.SEVERE, "Exception while send notification", e);
            }
        }
    }
    
    static {
        PrivacySettingListener.listener = null;
        CHANGE_CHECK = new String[] { "fetch_phone_number", "fetch_installed_app", "fetch_device_name", "fetch_location", "fetch_mac_address", "fetch_user_installed_certs", "recent_users_report", "fetch_wifi_ssid" };
    }
}
