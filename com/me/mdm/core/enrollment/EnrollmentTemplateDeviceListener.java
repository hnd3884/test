package com.me.mdm.core.enrollment;

import java.util.Iterator;
import com.me.mdm.server.adep.AppleDEPProfileHandler;
import com.adventnet.persistence.DataObject;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.me.mdm.server.enrollment.MDMEnrollmentRequestHandler;
import com.adventnet.sym.server.mdm.util.MDMStringUtils;
import com.me.mdm.server.util.MDMFeatureParamsHandler;
import com.me.mdm.server.factory.MDMApiFactoryProvider;
import com.me.devicemanagement.framework.server.license.LicenseProvider;
import com.adventnet.sym.server.mdm.inv.InventoryUtil;
import com.adventnet.sym.server.mdm.command.DeviceCommandRepository;
import com.adventnet.sym.server.mdm.inv.MDCustomDetailsRequestHandler;
import com.me.devicemanagement.framework.server.authentication.DMUserHandler;
import org.json.JSONObject;
import com.adventnet.persistence.Row;
import java.util.logging.Level;
import com.me.devicemanagement.framework.server.util.DBUtil;
import com.adventnet.sym.server.mdm.core.DeviceEvent;
import java.util.logging.Logger;
import com.adventnet.sym.server.mdm.core.ManagedDeviceListener;

public class EnrollmentTemplateDeviceListener extends ManagedDeviceListener
{
    public static Logger logger;
    
    @Override
    public void devicePreDelete(final DeviceEvent deviceEvent) {
        EnrollmentTemplateDeviceListener.mdmlogger.info("Entering EnrollmentTemplateDeviceListener:devicePreDelete");
        try {
            if (deviceEvent.enrollmentRequestId != null) {
                Long deviceID = deviceEvent.resourceJSON.optLong("ENROLLMENT_DEVICE_ID");
                if (deviceID == null) {
                    final Row enrollToRequestRow = DBUtil.getRowFromDB("DeviceEnrollmentToRequest", "ENROLLMENT_REQUEST_ID", (Object)deviceEvent.enrollmentRequestId);
                    if (enrollToRequestRow != null) {
                        deviceID = (Long)enrollToRequestRow.get("ENROLLMENT_DEVICE_ID");
                    }
                }
                if (deviceID != null) {
                    new DeviceForEnrollmentHandler().deleteOnEnrollment(deviceID, "ENROLLMENT_DEVICE_ID");
                    EnrollmentTemplateDeviceListener.logger.log(Level.INFO, "Deleted DeviceForEnrollment entry for enrollment_device_id:{0} enrollment_request_id:{1}", new Object[] { deviceID, deviceEvent.enrollmentRequestId });
                }
            }
        }
        catch (final Exception ex) {
            EnrollmentTemplateDeviceListener.logger.log(Level.SEVERE, null, ex);
        }
        EnrollmentTemplateDeviceListener.mdmlogger.info("Exiting EnrollmentTemplateDeviceListener:devicePreDelete");
    }
    
    @Override
    public void deviceUnmanaged(final DeviceEvent deviceEvent) {
        EnrollmentTemplateDeviceListener.mdmlogger.info("Entering EnrollmentTemplateDeviceListener:deviceUnmanaged");
        try {
            final JSONObject json = new JSONObject();
            json.put("ENROLLMENT_REQUEST_ID", (Object)deviceEvent.enrollmentRequestId);
            if (deviceEvent.enrollmentRequestId != null) {
                final Row r = DBUtil.getRowFromDB("DeviceEnrollmentToRequest", "ENROLLMENT_REQUEST_ID", (Object)deviceEvent.enrollmentRequestId);
                if (r != null) {
                    new DeviceForEnrollmentHandler().deleteOnEnrollment(r.get("ENROLLMENT_DEVICE_ID"), "ENROLLMENT_DEVICE_ID");
                    EnrollmentTemplateDeviceListener.logger.log(Level.INFO, "Deleted DeviceForEnrollment entry for enrollment_device_id:{0} enrollment_request_id:{1}", new Object[] { r.get("ENROLLMENT_DEVICE_ID"), r.get("ENROLLMENT_REQUEST_ID") });
                }
            }
        }
        catch (final Exception ex) {
            EnrollmentTemplateDeviceListener.logger.log(Level.SEVERE, null, ex);
        }
        EnrollmentTemplateDeviceListener.mdmlogger.info("Exiting EnrollmentTemplateDeviceListener:deviceUnmanaged");
    }
    
    @Override
    public void deviceManaged(final DeviceEvent deviceEvent) {
        EnrollmentTemplateDeviceListener.mdmlogger.info("Entering EnrollmentTemplateDeviceListener:deviceManaged");
        try {
            final String serialNumber = deviceEvent.resourceJSON.optString("SERIAL_NUMBER");
            final String imei = deviceEvent.resourceJSON.optString("IMEI");
            final String easId = deviceEvent.resourceJSON.optString("EAS_DEVICE_IDENTIFIER");
            final String udid = deviceEvent.resourceJSON.optString("UDID");
            final String genericID = deviceEvent.resourceJSON.optString("GENERIC_IDENTIFIER");
            Long erid = null;
            final DeviceForEnrollmentHandler deviceForEnrollmentHandler = new DeviceForEnrollmentHandler();
            final JSONObject jsonObject = new JSONObject();
            jsonObject.put("SerialNumber", (Object)serialNumber);
            jsonObject.put("IMEI", (Object)imei);
            jsonObject.put("UDID", (Object)udid);
            jsonObject.put("EASID", (Object)easId);
            jsonObject.put("GENERIC_ID", (Object)genericID);
            final Long deviceEnrollmentId = deviceForEnrollmentHandler.getDeviceForEnrollmentId(jsonObject);
            try {
                final JSONObject propsJSON = deviceForEnrollmentHandler.getDeviceForEnrollmentProps(deviceEnrollmentId);
                final String deviceName = propsJSON.optString("ASSIGNED_DEVICE_NAME");
                if (deviceName != null && !deviceName.equals("")) {
                    erid = deviceForEnrollmentHandler.getAssociatedEnrollmentRequestid(deviceEnrollmentId);
                    Long userID = null;
                    if (erid != null) {
                        userID = (Long)DBUtil.getValueFromDB("DeviceEnrollmentRequest", "ENROLLMENT_REQUEST_ID", (Object)erid, "USER_ID");
                    }
                    if (userID == null) {
                        userID = DMUserHandler.getDCSystemUserId();
                    }
                    final org.json.simple.JSONObject resourceJSON = new org.json.simple.JSONObject();
                    resourceJSON.put((Object)"MANAGED_DEVICE_ID", (Object)deviceEvent.resourceID);
                    resourceJSON.put((Object)"NAME", (Object)deviceName);
                    resourceJSON.put((Object)"IS_MODIFIED", (Object)true);
                    resourceJSON.put((Object)"USER_ID", (Object)userID);
                    resourceJSON.put((Object)"DESCRIPTION", (Object)"");
                    if (erid != null) {
                        resourceJSON.put((Object)"ENROLLMENT_REQUEST_ID", (Object)erid);
                    }
                    MDCustomDetailsRequestHandler.getInstance().addOrUpdateCustomDeviceDetails(resourceJSON);
                    DeviceCommandRepository.getInstance().addDeviceNameCommand(deviceEvent.resourceID);
                }
                InventoryUtil.getInstance().updateDeviceBasicInfo(jsonObject, deviceEvent.resourceID);
            }
            catch (final Exception ex) {
                Logger.getLogger(EnrollmentTemplateDeviceListener.class.getName()).log(Level.SEVERE, null, ex);
            }
            jsonObject.put("RESOURCE_ID", (Object)deviceEvent.resourceID);
            jsonObject.put("ENROLLMENT_DEVICE_ID", (Object)deviceEnrollmentId);
            jsonObject.put("LicenseVersionString", (Object)LicenseProvider.getInstance().getLicenseVersion());
            jsonObject.put("LicenseTypeString", (Object)LicenseProvider.getInstance().getProductType());
            jsonObject.put("LicenseCostTypeString", (Object)LicenseProvider.getInstance().getLicenseType());
            if (deviceEvent.platformType == 3 || deviceEvent.platformType == 1) {
                final Integer resourceType = (Integer)DBUtil.getValueFromDB("Resource", "RESOURCE_ID", (Object)deviceEvent.resourceID, "RESOURCE_TYPE");
                if (resourceType.equals(121)) {
                    MDMApiFactoryProvider.getMDMUtilAPI().checkLicenseStateAndUpdateManagedDeviceStatus(jsonObject);
                }
            }
            final Boolean allowDuplicateSerialNumber = MDMFeatureParamsHandler.getInstance().isFeatureEnabled("ALLOW_DUPLICATE_SERIAL_NUMBER");
            if (!MDMStringUtils.isEmpty(serialNumber) && !allowDuplicateSerialNumber) {
                deviceForEnrollmentHandler.deleteOnEnrollment(serialNumber, "SERIAL_NUMBER");
            }
            if (!MDMStringUtils.isEmpty(imei)) {
                deviceForEnrollmentHandler.deleteOnEnrollment(imei, "IMEI");
            }
            if (!MDMStringUtils.isEmpty(udid)) {
                deviceForEnrollmentHandler.deleteOnEnrollment(udid, "UDID");
            }
            if (!MDMStringUtils.isEmpty(easId)) {
                deviceForEnrollmentHandler.deleteOnEnrollment(easId, "EAS_DEVICE_IDENTIFIER");
            }
            if (erid != null) {
                MDMEnrollmentRequestHandler.getInstance().updateDeviceRequestStatus(erid, 3, deviceEvent.platformType);
            }
        }
        catch (final Exception ex2) {
            Logger.getLogger(EnrollmentTemplateDeviceListener.class.getName()).log(Level.SEVERE, ex2, () -> "Exception while clearing DFE Entries " + deviceEvent2.resourceJSON.toString());
        }
        EnrollmentTemplateDeviceListener.mdmlogger.info("Exiting EnrollmentTemplateDeviceListener:deviceManaged");
    }
    
    private JSONObject isActiveDEPDevice(final String serialNumber) throws Exception {
        final JSONObject detailsJSON = new JSONObject();
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("DEPDevicesSyncData"));
        selectQuery.addJoin(new Join("DEPDevicesSyncData", "DEPTokenDetails", new String[] { "DEP_TOKEN_ID" }, new String[] { "DEP_TOKEN_ID" }, 2));
        final Criteria serialNumberCriteria = new Criteria(Column.getColumn("DEPDevicesSyncData", "SERIAL_NUMBER"), (Object)serialNumber, 0);
        final Criteria activeDeviceCriteria = new Criteria(Column.getColumn("DEPDevicesSyncData", "DEVICE_STATUS"), (Object)1, 0);
        final Criteria allCriteria = serialNumberCriteria.and(activeDeviceCriteria);
        selectQuery.setCriteria(allCriteria);
        selectQuery.addSelectColumn(Column.getColumn("DEPDevicesSyncData", "DEVICE_ID"));
        selectQuery.addSelectColumn(Column.getColumn("DEPDevicesSyncData", "DEP_TOKEN_ID"));
        selectQuery.addSelectColumn(Column.getColumn("DEPTokenDetails", "DEP_TOKEN_ID"));
        selectQuery.addSelectColumn(Column.getColumn("DEPTokenDetails", "CUSTOMER_ID"));
        final DataObject dataObject = MDMUtil.getPersistenceLite().get(selectQuery);
        if (dataObject != null && !dataObject.isEmpty()) {
            final Row depDevicesSyncRow = dataObject.getRow("DEPDevicesSyncData");
            final Long depTokenId = (Long)depDevicesSyncRow.get("DEP_TOKEN_ID");
            final Row depTokenRow = dataObject.getRow("DEPTokenDetails", new Criteria(new Column("DEPTokenDetails", "DEP_TOKEN_ID"), (Object)depTokenId, 0));
            final Long customerId = (Long)depTokenRow.get("CUSTOMER_ID");
            detailsJSON.put("DEP_TOKEN_ID", (Object)depTokenId);
            detailsJSON.put("CUSTOMER_ID", (Object)customerId);
        }
        return detailsJSON;
    }
    
    @Override
    public void deviceDeleted(final DeviceEvent deviceEvent) {
        EnrollmentTemplateDeviceListener.mdmlogger.info("Entering EnrollmentTemplateDeviceListener:deviceDeleted");
        try {
            final JSONObject json = new JSONObject();
            json.put("ENROLLMENT_REQUEST_ID", (Object)deviceEvent.enrollmentRequestId);
            if (deviceEvent.platformType == 1) {
                final Long customerID = deviceEvent.customerID;
                final JSONObject resourceJSON = deviceEvent.resourceJSON;
                final SelectQuery deviceQuery = DEPAdminEnrollmentHandler.getDEPTokenToEnrollmentTemplateQuery();
                deviceQuery.addSelectColumn(new Column("DEPTokenDetails", "CUSTOMER_ID"));
                deviceQuery.addSelectColumn(new Column("DEPTokenDetails", "DEP_TOKEN_ID"));
                deviceQuery.setCriteria(new Criteria(new Column("EnrollmentTemplateToRequest", "ENROLLMENT_REQUEST_ID"), (Object)deviceEvent.enrollmentRequestId, 0));
                final DataObject depDeviceRemovedDO = MDMUtil.getPersistence().get(deviceQuery);
                if (!depDeviceRemovedDO.isEmpty()) {
                    EnrollmentTemplateDeviceListener.mdmlogger.info("Device  deleted is a DEP device");
                    final Iterator depTokenDetialsItr = depDeviceRemovedDO.getRows("DEPTokenDetails");
                    if (depTokenDetialsItr.hasNext()) {
                        final Row depTokenRow = depTokenDetialsItr.next();
                        final Long customerId = (Long)depTokenRow.get("CUSTOMER_ID");
                        final Long tokenId = (Long)depTokenRow.get("DEP_TOKEN_ID");
                        EnrollmentTemplateDeviceListener.mdmlogger.info("Going to sync DEP devices in for token id" + tokenId);
                        AppleDEPProfileHandler.getInstance(tokenId, customerId).syncDeviceDetailsOnRemoval(resourceJSON);
                    }
                }
                else {
                    final String serialNumber = resourceJSON.optString("SERIAL_NUMBER");
                    final JSONObject detailsJSON = this.isActiveDEPDevice(serialNumber);
                    if (detailsJSON != null && detailsJSON.length() != 0) {
                        final Long tokenId2 = detailsJSON.optLong("DEP_TOKEN_ID");
                        final Long customerId2 = detailsJSON.optLong("CUSTOMER_ID");
                        EnrollmentTemplateDeviceListener.logger.log(Level.INFO, "Going to sync active DEP device: {0} while deleting this device", new Object[] { serialNumber });
                        AppleDEPProfileHandler.getInstance(tokenId2, customerId2).syncDeviceDetailsOnRemoval(resourceJSON);
                    }
                }
            }
        }
        catch (final Exception ex) {
            Logger.getLogger(EnrollmentTemplateDeviceListener.class.getName()).log(Level.SEVERE, "Exception in EnrollmentTemplateDeviceListener:deviceDeleted", ex);
        }
        EnrollmentTemplateDeviceListener.mdmlogger.info("Exiting EnrollmentTemplateDeviceListener:deviceDeleted");
    }
    
    static {
        EnrollmentTemplateDeviceListener.logger = Logger.getLogger("MDMEnrollment");
    }
}
