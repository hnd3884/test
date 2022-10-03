package com.me.mdm.uem;

import com.adventnet.ds.query.SelectQuery;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.persistence.DataAccessException;
import java.util.Iterator;
import com.adventnet.sym.server.mdm.util.MDMStringUtils;
import com.adventnet.persistence.Row;
import org.json.JSONArray;
import com.adventnet.persistence.DataObject;
import org.json.JSONObject;
import com.me.mdm.server.util.MDMCustomerParamsHandler;
import com.me.mdm.uem.actionconstants.DeviceAction;
import com.me.mdm.server.factory.MDMApiFactoryProvider;
import com.me.devicemanagement.framework.server.customer.CustomerInfoUtil;
import java.util.logging.Level;
import com.me.mdm.server.util.MDMFeatureParamsHandler;
import java.util.logging.Logger;

public class ComputerToDeviceMappingUpdater
{
    private static ComputerToDeviceMappingUpdater updater;
    private static final Logger LOGGER;
    private static final String LAST_UPDATED_TIME_OF_MANAGED_DEVICES_TO_SOM = "LAST_UPDATED_TIME_OF_MANAGED_DEVICES_TO_SOM";
    
    public static ComputerToDeviceMappingUpdater getInstance() {
        if (ComputerToDeviceMappingUpdater.updater == null) {
            ComputerToDeviceMappingUpdater.updater = new ComputerToDeviceMappingUpdater();
        }
        return ComputerToDeviceMappingUpdater.updater;
    }
    
    public void updateEnrolledDevicesToDC() {
        try {
            final boolean forceUpdateAllManagedDeviceDetailsToDC = MDMFeatureParamsHandler.getInstance().isFeatureEnabled("ForceUpdateAllManagedDeviceDetailsToDC");
            ComputerToDeviceMappingUpdater.LOGGER.log(Level.INFO, "ComputerToDeviceMappingUpdater: Going to update the details of devices enrolled since last sync time. ForceUpdate: {0}", new Object[] { forceUpdateAllManagedDeviceDetailsToDC });
            final Long[] customerIdsFromDB;
            final Long[] customers = customerIdsFromDB = CustomerInfoUtil.getInstance().getCustomerIdsFromDB();
            for (final Long customer : customerIdsFromDB) {
                final long lastSyncTime = forceUpdateAllManagedDeviceDetailsToDC ? -1L : this.getLastSyncTimeForCustomer(customer);
                final JSONObject deviceDetails = this.getDetailsOfDevicesEnrolledSinceGivenTime(customer, lastSyncTime);
                if (deviceDetails != null) {
                    ComputerToDeviceMappingUpdater.LOGGER.log(Level.INFO, "ComputerToDeviceMappingUpdater: Contacting DC: {0}", new Object[] { deviceDetails });
                    final JSONObject responseFromDC = MDMApiFactoryProvider.getMDMModernMgmtAPI().deviceListener(DeviceAction.ADDORUPDATE_MAPPINGTABLE, deviceDetails);
                    if (responseFromDC != null && responseFromDC.getBoolean("isSuccessfull")) {
                        ComputerToDeviceMappingUpdater.LOGGER.log(Level.INFO, "ComputerToDeviceMappingUpdater, Successfully updated MDM enrolled devices to DC for customer: {0}", new Object[] { customer });
                    }
                    ComputerToDeviceMappingUpdater.LOGGER.log(Level.INFO, "ComputerToDeviceMappingUpdater: Update Complete for customer: {0} Time: {1}", new Object[] { customer, System.currentTimeMillis() });
                }
                new MDMCustomerParamsHandler().addOrUpdateParameter("LAST_UPDATED_TIME_OF_MANAGED_DEVICES_TO_SOM", String.valueOf(System.currentTimeMillis()), customer);
            }
            if (forceUpdateAllManagedDeviceDetailsToDC) {
                MDMFeatureParamsHandler.updateMDMFeatureParameter("ForceUpdateAllManagedDeviceDetailsToDC", "false");
            }
        }
        catch (final Exception e) {
            ComputerToDeviceMappingUpdater.LOGGER.log(Level.SEVERE, "ComputerToDeviceMappingUpdater: Exception while updating Enrolled device details to DC", e);
        }
    }
    
    private long getLastSyncTimeForCustomer(final long customerId) throws Exception {
        final String lastSyncTime = new MDMCustomerParamsHandler().getParameterValue("LAST_UPDATED_TIME_OF_MANAGED_DEVICES_TO_SOM", customerId);
        ComputerToDeviceMappingUpdater.LOGGER.log(Level.INFO, "ComputerToDeviceMappingUpdater: Last sync time for customer: {0} is {1}", new Object[] { customerId, lastSyncTime });
        return (lastSyncTime == null) ? -1L : Long.parseLong(lastSyncTime);
    }
    
    private JSONObject getDeviceDetails(final DataObject dataObject) throws DataAccessException {
        final JSONArray serialNumberList = new JSONArray();
        final JSONArray deviceUpdateDetails = new JSONArray();
        final Iterator iterator = dataObject.getRows("ManagedDevice");
        while (iterator.hasNext()) {
            final Row row = iterator.next();
            final long resourceId = (long)row.get("RESOURCE_ID");
            ComputerToDeviceMappingUpdater.LOGGER.log(Level.INFO, "ComputerToDeviceMappingUpdater: Getting details for device: {0}", new Object[] { resourceId });
            final String serialNumber = this.getDeviceSerialNumber(dataObject, resourceId);
            if (!MDMStringUtils.isEmpty(serialNumber)) {
                serialNumberList.put((Object)serialNumber);
            }
            final JSONObject deviceDetail = this.getManagedDeviceDetail(dataObject, resourceId);
            deviceUpdateDetails.put((Object)deviceDetail);
        }
        final JSONObject deviceDetailsJson = new JSONObject();
        deviceDetailsJson.put("SerialNumberList", (Object)serialNumberList);
        deviceDetailsJson.put("DeviceUpdateDetails", (Object)deviceUpdateDetails);
        return new JSONObject().put("DeviceDetails", (Object)deviceDetailsJson);
    }
    
    private JSONObject getManagedDeviceDetail(final DataObject dataObject, final long resourceId) throws DataAccessException {
        final Criteria resourceCriteria = new Criteria(new Column("ManagedDevice", "RESOURCE_ID"), (Object)resourceId, 0);
        final Row row = dataObject.getRow("ManagedDevice", resourceCriteria);
        final JSONObject deviceDetail = new JSONObject();
        deviceDetail.put("RESOURCE_ID", row.get("RESOURCE_ID"));
        deviceDetail.put("MANAGED_STATUS", row.get("MANAGED_STATUS"));
        deviceDetail.put("UDID", row.get("UDID"));
        deviceDetail.put("REMARKS", row.get("REMARKS"));
        deviceDetail.put("LAST_CONTACT_TIME", row.get("ADDED_TIME"));
        deviceDetail.put("ADDED_TIME", row.get("ADDED_TIME"));
        deviceDetail.put("SERIAL_NUMBER", (Object)this.getDeviceSerialNumber(dataObject, resourceId));
        deviceDetail.put("ENROLLMENT_TYPE", this.getDeviceEnrollmentTemplateType(dataObject, resourceId));
        return deviceDetail;
    }
    
    private int getDeviceEnrollmentTemplateType(final DataObject dataObject, final long resourceId) throws DataAccessException {
        final Join join = new Join("EnrollmentRequestToDevice", "EnrollmentTemplateToRequest", new String[] { "ENROLLMENT_REQUEST_ID" }, new String[] { "ENROLLMENT_REQUEST_ID" }, 1);
        final Criteria resourceCriteria = new Criteria(new Column("EnrollmentRequestToDevice", "MANAGED_DEVICE_ID"), (Object)resourceId, 0);
        final Row row = dataObject.getRow("EnrollmentTemplateToRequest", resourceCriteria, join);
        int templateType = -1;
        if (row != null) {
            final long templateId = (long)row.get("TEMPLATE_ID");
            final Criteria templateIdCriteria = new Criteria(new Column("EnrollmentTemplate", "TEMPLATE_ID"), (Object)templateId, 0);
            final Row templateRow = dataObject.getRow("EnrollmentTemplate", templateIdCriteria);
            if (templateRow != null) {
                templateType = (int)templateRow.get("TEMPLATE_TYPE");
            }
            else {
                ComputerToDeviceMappingUpdater.LOGGER.log(Level.INFO, "ComputerToDeviceMappingUpdater: No row found in Enrollment template table for the given resource Id: {0}. So, taking it as Enrollment via Invite", new Object[] { resourceId });
                templateType = 1;
            }
        }
        else {
            ComputerToDeviceMappingUpdater.LOGGER.log(Level.INFO, "ComputerToDeviceMappingUpdater: No row found in Enrollment template To request table for the given resource Id: {0}. So, taking it as Enrollment via Invite", new Object[] { resourceId });
            templateType = 1;
        }
        ComputerToDeviceMappingUpdater.LOGGER.log(Level.INFO, "ComputerToDeviceMappingUpdater: Template type for the given resource Id: {0} is {1}", new Object[] { resourceId, templateType });
        return templateType;
    }
    
    private String getDeviceSerialNumber(final DataObject dataObject, final long resourceId) throws DataAccessException {
        final Criteria resourceCriteria = new Criteria(new Column("MdDeviceInfo", "RESOURCE_ID"), (Object)resourceId, 0);
        final Row mdDeviceInfoRow = dataObject.getRow("MdDeviceInfo", resourceCriteria);
        String serialNumber = null;
        if (mdDeviceInfoRow != null) {
            serialNumber = (String)mdDeviceInfoRow.get("SERIAL_NUMBER");
        }
        return serialNumber;
    }
    
    private JSONObject getDetailsOfDevicesEnrolledSinceGivenTime(final Long customer, final long time) throws DataAccessException {
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("ManagedDevice"));
        this.addNeededColumns(selectQuery);
        this.joinRequiredTables(selectQuery);
        this.setCriteria(customer, time, selectQuery);
        final DataObject dataObject = SyMUtil.getPersistence().get(selectQuery);
        JSONObject deviceDetails = null;
        if (!dataObject.isEmpty()) {
            ComputerToDeviceMappingUpdater.LOGGER.log(Level.INFO, "ComputerToDeviceMappingUpdater: There are devices enrolled since the last sync time: {0}", new Object[] { time });
            deviceDetails = this.getDeviceDetails(dataObject);
        }
        return deviceDetails;
    }
    
    private void addNeededColumns(final SelectQuery selectQuery) {
        selectQuery.addSelectColumn(new Column("ManagedDevice", "ADDED_TIME"));
        selectQuery.addSelectColumn(new Column("ManagedDevice", "MANAGED_STATUS"));
        selectQuery.addSelectColumn(new Column("ManagedDevice", "REMARKS"));
        selectQuery.addSelectColumn(new Column("ManagedDevice", "RESOURCE_ID"));
        selectQuery.addSelectColumn(new Column("ManagedDevice", "UDID"));
        selectQuery.addSelectColumn(new Column("MdDeviceInfo", "RESOURCE_ID"));
        selectQuery.addSelectColumn(new Column("MdDeviceInfo", "SERIAL_NUMBER"));
        selectQuery.addSelectColumn(new Column("MdModelInfo", "MODEL_ID"));
        selectQuery.addSelectColumn(new Column("MdModelInfo", "MODEL_TYPE"));
        selectQuery.addSelectColumn(new Column("EnrollmentTemplateToRequest", "ENROLLMENT_REQUEST_ID", "ENROLLMENTTEMPLATETOREQUEST.ENROLLMENT_REQUEST_ID"));
        selectQuery.addSelectColumn(new Column("EnrollmentTemplateToRequest", "TEMPLATE_ID"));
        selectQuery.addSelectColumn(new Column("EnrollmentRequestToDevice", "MANAGED_DEVICE_ID"));
        selectQuery.addSelectColumn(new Column("EnrollmentRequestToDevice", "ENROLLMENT_REQUEST_ID", "ENROLLMENTREQUESTTODEVICE.ENROLLMENT_REQUEST_ID"));
        selectQuery.addSelectColumn(new Column("EnrollmentTemplate", "TEMPLATE_ID", "ENROLLMENTTEMPLATE.TEMPLATE_ID"));
        selectQuery.addSelectColumn(new Column("EnrollmentTemplate", "TEMPLATE_TYPE"));
    }
    
    private void joinRequiredTables(final SelectQuery selectQuery) {
        selectQuery.addJoin(new Join("ManagedDevice", "Resource", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2));
        selectQuery.addJoin(new Join("Resource", "MdDeviceInfo", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2));
        selectQuery.addJoin(new Join("MdDeviceInfo", "MdModelInfo", new String[] { "MODEL_ID" }, new String[] { "MODEL_ID" }, 2));
        selectQuery.addJoin(new Join("ManagedDevice", "EnrollmentRequestToDevice", new String[] { "RESOURCE_ID" }, new String[] { "MANAGED_DEVICE_ID" }, 2));
        selectQuery.addJoin(new Join("EnrollmentRequestToDevice", "EnrollmentTemplateToRequest", new String[] { "ENROLLMENT_REQUEST_ID" }, new String[] { "ENROLLMENT_REQUEST_ID" }, 1));
        selectQuery.addJoin(new Join("EnrollmentTemplateToRequest", "EnrollmentTemplate", new String[] { "TEMPLATE_ID" }, new String[] { "TEMPLATE_ID" }, 1));
    }
    
    private void setCriteria(final Long customer, final long time, final SelectQuery selectQuery) {
        final Criteria enrolledTimeCriteria = new Criteria(new Column("ManagedDevice", "REGISTERED_TIME"), (Object)time, 4);
        final Criteria enrollSuccessCriteria = this.getManagedDevicesCriteria();
        final Criteria customerIdCriteria = new Criteria(new Column("Resource", "CUSTOMER_ID"), (Object)customer, 0);
        final Criteria deviceTypeCriteria = new Criteria(new Column("MdModelInfo", "MODEL_TYPE"), (Object)new int[] { 3, 4 }, 8);
        final Criteria platformTypeCriteria = new Criteria(new Column("ManagedDevice", "PLATFORM_TYPE"), (Object)new int[] { 1, 3 }, 8);
        final Criteria overallCriteria = customerIdCriteria.and(enrolledTimeCriteria).and(deviceTypeCriteria).and(platformTypeCriteria).and(enrollSuccessCriteria);
        selectQuery.setCriteria(overallCriteria);
    }
    
    private Criteria getManagedDevicesCriteria() {
        final Criteria enrollSuccessCriteria = new Criteria(new Column("ManagedDevice", "MANAGED_STATUS"), (Object)2, 0);
        final Criteria excludeUnmanagedCriteria = new Criteria(new Column("ManagedDevice", "MANAGED_STATUS"), (Object)4, 1);
        final Criteria excludeRepairCriteria = new Criteria(new Column("ManagedDevice", "MANAGED_STATUS"), (Object)9, 1);
        final Criteria excludeRetiredCriteria = new Criteria(new Column("ManagedDevice", "MANAGED_STATUS"), (Object)11, 1);
        final Criteria excludeOldDeviceCrteria = new Criteria(new Column("ManagedDevice", "MANAGED_STATUS"), (Object)10, 1);
        final Criteria deviceStatusCriteria = enrollSuccessCriteria.or(excludeUnmanagedCriteria.and(excludeRepairCriteria).and(excludeRetiredCriteria).and(excludeOldDeviceCrteria));
        return deviceStatusCriteria;
    }
    
    static {
        LOGGER = Logger.getLogger("MDMModernMgmtLogger");
    }
}
