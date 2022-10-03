package com.me.mdm.server.inv.ios.DeviceAttestation;

import com.adventnet.ds.query.SelectQuery;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.adventnet.sym.server.mdm.util.VersionChecker;
import java.util.HashMap;
import com.adventnet.persistence.Persistence;
import com.adventnet.persistence.DataObject;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import com.adventnet.persistence.Row;
import java.util.List;
import com.me.mdm.server.notification.NotificationHandler;
import java.util.Arrays;
import com.adventnet.sym.server.mdm.command.DeviceCommandRepository;
import java.util.UUID;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import com.dd.plist.NSData;
import com.dd.plist.NSArray;
import com.dd.plist.NSDictionary;
import java.util.logging.Logger;

public class DeviceAttestationHandler
{
    private static Logger logger;
    
    public void deviceAttestationResponseHandler(final NSDictionary nsDict, final Long resourceId) throws Exception {
        final NSArray certificateArray = (NSArray)nsDict.objectForKey("DevicePropertiesAttestation");
        final NSData leafCertificateData = (NSData)certificateArray.objectAtIndex(0);
        final NSData intermediateCertificateData = (NSData)certificateArray.objectAtIndex(1);
        final String leafCertificate = leafCertificateData.getBase64EncodedData();
        final String intermediateCertificate = intermediateCertificateData.getBase64EncodedData();
        final DeviceAttestationModel deviceAttestationDetailsFromCertificate = new DeviceAttestationModel();
        try {
            new DeviceAttestationCertificateHandler().getDeviceAttestationDetails(leafCertificate, intermediateCertificate, deviceAttestationDetailsFromCertificate);
        }
        catch (final Exception ex) {
            DeviceAttestationHandler.logger.log(Level.SEVERE, "Certificate not signed by Apple Enterprise Root CA ");
            throw ex;
        }
        final DeviceAttestationModel deviceAttestationPropertiesFromDB = this.getDeviceAttestationPropertiesFromDB(resourceId);
        if (!deviceAttestationDetailsFromCertificate.getDeviceInformationCommandStatus()) {
            DeviceAttestationHandler.logger.log(Level.SEVERE, "Certificate is not generated properly or Having missing properties");
            throw new Exception("Certificate is not generated Properly or Having missing properties");
        }
        if (!deviceAttestationDetailsFromCertificate.getNonce().equals(deviceAttestationPropertiesFromDB.getNonce())) {
            DeviceAttestationHandler.logger.log(Level.SEVERE, "Certificate nonce is not matching with our records.");
            throw new Exception("Certificate nonce is not matching with our records.");
        }
        this.addOrUpdateDeviceAttestationDetailsInDB(deviceAttestationDetailsFromCertificate, resourceId);
        final boolean isDevicePropertiesMismatched = this.checkDeviceAttestationProperties(deviceAttestationDetailsFromCertificate, resourceId);
        if (isDevicePropertiesMismatched) {
            this.updateDeviceAttestationStatus(2, resourceId);
        }
        else {
            this.updateDeviceAttestationStatus(1, resourceId);
        }
    }
    
    private void sendDeviceAttestationCommand(DeviceAttestationModel deviceAttestationProperties, final Long resourceId) throws Exception {
        Long nonceGeneratedTime = 0L;
        if (deviceAttestationProperties != null) {
            nonceGeneratedTime = deviceAttestationProperties.getNonceGeneratedTime();
        }
        else {
            deviceAttestationProperties = new DeviceAttestationModel();
        }
        final Long nextEligibleTime = nonceGeneratedTime + TimeUnit.DAYS.toMillis(7L);
        boolean isTimeLimitCompleted = true;
        if (nextEligibleTime.compareTo(MDMUtil.getCurrentTimeInMillis()) > 0) {
            isTimeLimitCompleted = false;
        }
        if (isTimeLimitCompleted) {
            try {
                final String nonce = UUID.randomUUID().toString().substring(0, 32);
                this.updateDeviceAttestationNonce(nonce, resourceId);
                deviceAttestationProperties.setNonce(nonce);
                this.pushAppleDeviceAttestationCommand(resourceId);
                DeviceAttestationHandler.logger.log(Level.INFO, "Device attestation command send successfully");
                return;
            }
            catch (final Exception ex) {
                DeviceAttestationHandler.logger.log(Level.SEVERE, "Exception while sending Device attestation command ");
                throw ex;
            }
        }
        if (nonceGeneratedTime.compareTo(deviceAttestationProperties.getUpdatedCommandTime()) > 0) {
            this.pushAppleDeviceAttestationCommand(resourceId);
        }
        else {
            DeviceAttestationHandler.logger.log(Level.SEVERE, "Device attestation failed due to time limit of 7 days is not completed for the resource id -{0}. Device attestation will be eligible after - {1}", new Object[] { resourceId, nextEligibleTime });
        }
    }
    
    public void pushAppleDeviceAttestationCommand(final Long resourceID) {
        final Long commandID = DeviceCommandRepository.getInstance().addCommand("AppleDeviceAttestation");
        final List<Long> resourceList = Arrays.asList(resourceID);
        try {
            DeviceCommandRepository.getInstance().assignCommandToDevice("AppleDeviceAttestation", resourceID);
            NotificationHandler.getInstance().SendNotification(resourceList, 0);
        }
        catch (final Exception ex) {
            DeviceAttestationHandler.logger.log(Level.SEVERE, "Exception while Sending Device Attestation Command");
        }
    }
    
    private void addOrUpdateDeviceAttestationDetailsInDB(final DeviceAttestationModel deviceAttestationDetails, final Long resourceId) throws Exception {
        final DataObject deviceAttestationDO = this.getDeviceAttestationPropertiesDO(resourceId);
        Row deviceAttestationRow = null;
        if (!deviceAttestationDO.isEmpty()) {
            deviceAttestationRow = deviceAttestationDO.getFirstRow("MdDeviceAttestationInfo");
            deviceAttestationRow.set("SERIAL_NUMBER", (Object)deviceAttestationDetails.getSerialNumber());
            deviceAttestationRow.set("UDID", (Object)deviceAttestationDetails.getUdid());
            deviceAttestationRow.set("OS_VERSION", (Object)deviceAttestationDetails.getOsVersion());
            deviceAttestationRow.set("UPDATED_TIME", (Object)MDMUtil.getCurrentTimeInMillis());
            deviceAttestationDO.updateRow(deviceAttestationRow);
        }
        else {
            final Row safetyNetStatusRow = new Row("SafetyNetStatus");
            safetyNetStatusRow.set("RESOURCE_ID", (Object)resourceId);
            deviceAttestationRow = new Row("MdDeviceAttestationInfo");
            deviceAttestationRow.set("RESOURCE_ID", (Object)resourceId);
            deviceAttestationDO.addRow(deviceAttestationRow);
            deviceAttestationDO.addRow(safetyNetStatusRow);
        }
        SyMUtil.getPersistence().update(deviceAttestationDO);
    }
    
    private void updateDeviceAttestationStatus(final int deviceAttestationStatus, final Long resourceId) throws Exception {
        final DataObject deviceAttestationDO = this.getDeviceAttestationStatusPropertiesDO(resourceId);
        if (!deviceAttestationDO.isEmpty()) {
            final Row deviceAttestationRow = deviceAttestationDO.getFirstRow("MdDeviceAttestationInfo");
            deviceAttestationRow.set("RESOURCE_ID", (Object)resourceId);
            deviceAttestationRow.set("DEVICE_ATTESTATION_STATUS", (Object)deviceAttestationStatus);
            final Row safetyNetRow = deviceAttestationDO.getFirstRow("SafetyNetStatus");
            safetyNetRow.set("RESOURCE_ID", (Object)resourceId);
            if (deviceAttestationStatus == 1) {
                safetyNetRow.set("SAFETYNET_AVAILABIITY", (Object)true);
                safetyNetRow.set("SAFETYNET_BASIC_INTEGRITY", (Object)true);
                safetyNetRow.set("SAFETYNET_ERROR_REASON", (Object)null);
                safetyNetRow.set("SAFETYNET_TIMESTAMP", (Object)MDMUtil.getCurrentTimeInMillis());
            }
            else if (deviceAttestationStatus == 2) {
                safetyNetRow.set("SAFETYNET_AVAILABIITY", (Object)true);
                safetyNetRow.set("SAFETYNET_BASIC_INTEGRITY", (Object)false);
                safetyNetRow.set("SAFETYNET_ERROR_REASON", (Object)null);
                safetyNetRow.set("SAFETYNET_TIMESTAMP", (Object)MDMUtil.getCurrentTimeInMillis());
            }
            deviceAttestationDO.updateRow(deviceAttestationRow);
            deviceAttestationDO.updateRow(safetyNetRow);
        }
        SyMUtil.getPersistence().update(deviceAttestationDO);
    }
    
    private void updateDeviceAttestationNonce(final String nonce, final Long resourceId) throws Exception {
        final Persistence persistence = SyMUtil.getPersistence();
        final DataObject deviceAttestationDO = this.getDeviceAttestationPropertiesDO(resourceId);
        if (!deviceAttestationDO.isEmpty()) {
            final Row deviceAttestationRow = deviceAttestationDO.getFirstRow("MdDeviceAttestationInfo");
            deviceAttestationRow.set("RESOURCE_ID", (Object)resourceId);
            deviceAttestationRow.set("NONCE", (Object)nonce);
            deviceAttestationRow.set("NONCE_GENERATED_TIME", (Object)MDMUtil.getCurrentTimeInMillis());
            deviceAttestationDO.updateRow(deviceAttestationRow);
        }
        persistence.update(deviceAttestationDO);
    }
    
    private boolean checkDeviceAttestationProperties(final DeviceAttestationModel deviceAttestationDetailsFromCertificate, final Long resourceId) throws Exception {
        boolean isDevicePropertiesMismatched;
        try {
            final DeviceAttestationModel deviceInformationPropertiesFromDB = this.getDeviceInformationPropertiesFromDB(resourceId);
            isDevicePropertiesMismatched = this.checkDeviceInformationWithDeviceAttestationProperties(deviceInformationPropertiesFromDB, deviceAttestationDetailsFromCertificate);
        }
        catch (final Exception ex) {
            DeviceAttestationHandler.logger.log(Level.SEVERE, "Exception in UpdateDeviceAttestationStatusInDB while getting deviceInformationProperties, {0}", ex);
            throw ex;
        }
        return isDevicePropertiesMismatched;
    }
    
    public void verifyDeviceAttestation(final HashMap hsmap, final Long resourceId) throws Exception {
        final HashMap deviceMap = MDMUtil.getInstance().getMDMDeviceProperties(resourceId);
        final int modelType = deviceMap.get("MODEL_TYPE");
        if (modelType != 1 && modelType != 2 && modelType != 5) {
            DeviceAttestationHandler.logger.log(Level.INFO, "DeviceAttestation is not supported for this resourceId - {0}", resourceId);
            return;
        }
        final DeviceAttestationModel deviceInformationProperties = new DeviceAttestationModel();
        deviceInformationProperties.setOsVersion(hsmap.get("OSVersion"));
        deviceInformationProperties.setUdid(hsmap.get("UDID"));
        deviceInformationProperties.setSerialNumber(hsmap.get("SerialNumber"));
        DeviceAttestationModel deviceAttestationProperties = null;
        try {
            deviceAttestationProperties = this.getDeviceAttestationPropertiesFromDB(resourceId);
        }
        catch (final Exception ex) {
            DeviceAttestationHandler.logger.log(Level.INFO, "Exception in verifyDeviceAttestation for resourceId - {0} - {1}", new Object[] { resourceId, ex });
            throw new Exception("Exception while accessing DeviceAttestationDetails");
        }
        final VersionChecker versionChecker = new VersionChecker();
        if (deviceAttestationProperties == null) {
            if (versionChecker.isGreaterOrEqual(deviceInformationProperties.getOsVersion(), "16.0")) {
                this.addOrUpdateDeviceAttestationDetailsInDB(null, resourceId);
                this.updateDeviceAttestationStatus(3, resourceId);
                DeviceAttestationHandler.logger.log(Level.INFO, "Device Property is changed. need to re verify attest the device - {0}", resourceId);
                this.sendDeviceAttestationCommand(null, resourceId);
            }
            else {
                DeviceAttestationHandler.logger.log(Level.INFO, "Device Attestation is not applicable for this device - {0}", resourceId);
            }
        }
        else if (versionChecker.isGreaterOrEqual(deviceInformationProperties.getOsVersion(), "16.0")) {
            final boolean isDevicePropertyMismatch = this.checkDeviceInformationWithDeviceAttestationProperties(deviceInformationProperties, deviceAttestationProperties);
            if (isDevicePropertyMismatch) {
                DeviceAttestationHandler.logger.log(Level.INFO, "Device Property is changed. need to re verify attest the device - {0}", resourceId);
                this.updateDeviceAttestationStatus(3, resourceId);
                this.sendDeviceAttestationCommand(deviceAttestationProperties, resourceId);
            }
            else if (deviceAttestationProperties.getOsVersion() == null) {
                this.sendDeviceAttestationCommand(deviceAttestationProperties, resourceId);
            }
        }
        else {
            DeviceAttestationHandler.logger.log(Level.INFO, "Device Attestation is not applicable for this device - {0}", resourceId);
        }
        DeviceAttestationHandler.logger.log(Level.INFO, "Verify DeviceAttestation is completed for resource id - {0}", resourceId);
    }
    
    public boolean checkDeviceInformationWithDeviceAttestationProperties(final DeviceAttestationModel deviceInformationProperties, final DeviceAttestationModel deviceAttestationProperties) {
        boolean isDevicePropertiesMismatch = false;
        if (deviceAttestationProperties.getSerialNumber() != null && !deviceAttestationProperties.getSerialNumber().equals(deviceInformationProperties.getSerialNumber())) {
            DeviceAttestationHandler.logger.log(Level.SEVERE, "Device Serial No is not matching for device {0} - {1} for {2}", new Object[] { deviceAttestationProperties.getSerialNumber(), deviceInformationProperties.getSerialNumber(), deviceInformationProperties.getSerialNumber() });
            isDevicePropertiesMismatch = true;
        }
        if (deviceAttestationProperties.getUdid() != null && !deviceAttestationProperties.getUdid().equals(deviceInformationProperties.getUdid())) {
            DeviceAttestationHandler.logger.log(Level.SEVERE, "Device UDID is not matching for device {0} - {1} for {2}", new Object[] { deviceAttestationProperties.getUdid(), deviceInformationProperties.getUdid(), deviceInformationProperties.getSerialNumber() });
            isDevicePropertiesMismatch = true;
        }
        if (deviceAttestationProperties.getOsVersion() != null && !deviceAttestationProperties.getOsVersion().equals(deviceInformationProperties.getOsVersion())) {
            DeviceAttestationHandler.logger.log(Level.SEVERE, "Device Os Version is not matching for device {0} - {1} for {2}", new Object[] { deviceAttestationProperties.getOsVersion(), deviceInformationProperties.getOsVersion(), deviceInformationProperties.getSerialNumber() });
            isDevicePropertiesMismatch = true;
        }
        return isDevicePropertiesMismatch;
    }
    
    private DeviceAttestationModel getDeviceInformationPropertiesFromDB(final Long resourceId) throws Exception {
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("ManagedDevice"));
        final Criteria resourceCriteria = new Criteria(Column.getColumn("ManagedDevice", "RESOURCE_ID"), (Object)resourceId, 0);
        selectQuery.setCriteria(resourceCriteria);
        final Join mdDeviceInfoJoin = new Join("ManagedDevice", "MdDeviceInfo", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2);
        selectQuery.addSelectColumn(Column.getColumn("ManagedDevice", "*"));
        selectQuery.addSelectColumn(Column.getColumn("MdDeviceInfo", "*"));
        selectQuery.addJoin(mdDeviceInfoJoin);
        final DeviceAttestationModel deviceInformationDetailsFromDB = new DeviceAttestationModel();
        try {
            final DataObject deviceInformationDO = SyMUtil.getPersistence().get(selectQuery);
            if (!deviceInformationDO.isEmpty()) {
                final Row managedDeviceRow = deviceInformationDO.getFirstRow("ManagedDevice");
                deviceInformationDetailsFromDB.setUdid((String)managedDeviceRow.get("UDID"));
                final Row mdDeviceRow = deviceInformationDO.getFirstRow("MdDeviceInfo");
                deviceInformationDetailsFromDB.setSerialNumber((String)mdDeviceRow.get("SERIAL_NUMBER"));
                deviceInformationDetailsFromDB.setOsVersion((String)mdDeviceRow.get("OS_VERSION"));
            }
        }
        catch (final Exception ex) {
            DeviceAttestationHandler.logger.log(Level.SEVERE, "Exception in updateDeviceAttestationStatusInDB while access DB {0}", ex);
            throw new Exception("Exception in updateDeviceAttestationStatusInDB while access DB");
        }
        return deviceInformationDetailsFromDB;
    }
    
    public DeviceAttestationModel getDeviceAttestationPropertiesFromDB(final Long resourceId) throws Exception {
        DeviceAttestationModel deviceAttestationProperties = null;
        final DataObject deviceAttestationDO = this.getDeviceAttestationPropertiesDO(resourceId);
        Row deviceAttestationRow = null;
        if (!deviceAttestationDO.isEmpty()) {
            deviceAttestationProperties = new DeviceAttestationModel();
            deviceAttestationRow = deviceAttestationDO.getFirstRow("MdDeviceAttestationInfo");
            deviceAttestationProperties.setSerialNumber((String)deviceAttestationRow.get("SERIAL_NUMBER"));
            deviceAttestationProperties.setUdid((String)deviceAttestationRow.get("UDID"));
            deviceAttestationProperties.setOsVersion((String)deviceAttestationRow.get("OS_VERSION"));
            deviceAttestationProperties.setNonce((String)deviceAttestationRow.get("NONCE"));
            deviceAttestationProperties.setNonceGeneratedTime((Long)deviceAttestationRow.get("NONCE_GENERATED_TIME"));
            deviceAttestationProperties.setUpdatedCommandTime((Long)deviceAttestationRow.get("UPDATED_TIME"));
        }
        else {
            DeviceAttestationHandler.logger.log(Level.SEVERE, "DeviceAttestation Properties Not available for this resource id - {0}", resourceId);
        }
        return deviceAttestationProperties;
    }
    
    public DataObject getDeviceAttestationPropertiesDO(final Long resourceId) throws Exception {
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("MdDeviceAttestationInfo"));
        final Criteria resourceCriteria = new Criteria(Column.getColumn("MdDeviceAttestationInfo", "RESOURCE_ID"), (Object)resourceId, 0);
        selectQuery.setCriteria(resourceCriteria);
        selectQuery.addSelectColumn(Column.getColumn("MdDeviceAttestationInfo", "*"));
        DataObject deviceAttestationDO = null;
        try {
            deviceAttestationDO = SyMUtil.getPersistence().get(selectQuery);
        }
        catch (final Exception ex) {
            DeviceAttestationHandler.logger.log(Level.SEVERE, "Exception while access MdDeviceAttestationInfo {0}", ex);
            throw new Exception("Exception while access MdDeviceAttestationInfo");
        }
        return deviceAttestationDO;
    }
    
    private DataObject getDeviceAttestationStatusPropertiesDO(final Long resourceId) throws Exception {
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("MdDeviceAttestationInfo"));
        final Criteria resourceCriteria = new Criteria(Column.getColumn("MdDeviceAttestationInfo", "RESOURCE_ID"), (Object)resourceId, 0);
        final Join safetyNetStatusJoin = new Join("MdDeviceAttestationInfo", "SafetyNetStatus", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2);
        selectQuery.addJoin(safetyNetStatusJoin);
        selectQuery.setCriteria(resourceCriteria);
        selectQuery.addSelectColumn(Column.getColumn("MdDeviceAttestationInfo", "*"));
        selectQuery.addSelectColumn(Column.getColumn("SafetyNetStatus", "*"));
        DataObject deviceAttestationDO = null;
        try {
            deviceAttestationDO = SyMUtil.getPersistence().get(selectQuery);
        }
        catch (final Exception ex) {
            DeviceAttestationHandler.logger.log(Level.SEVERE, "Exception while access MdDeviceAttestationInfo {0}", ex);
            throw new Exception("Exception while access MdDeviceAttestationInfo");
        }
        return deviceAttestationDO;
    }
    
    static {
        DeviceAttestationHandler.logger = Logger.getLogger("MDMLogger");
    }
}
