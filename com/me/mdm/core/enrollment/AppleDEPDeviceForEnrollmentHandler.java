package com.me.mdm.core.enrollment;

import com.adventnet.persistence.Row;
import com.me.mdm.server.adep.DEPEnrollmentUtil;
import com.adventnet.persistence.DataAccessException;
import java.util.Iterator;
import java.util.List;
import com.adventnet.persistence.DataObject;
import com.adventnet.ds.query.SelectQuery;
import java.util.Collection;
import java.util.ArrayList;
import com.me.devicemanagement.framework.server.util.DBUtil;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AppleDEPDeviceForEnrollmentHandler extends DeviceForEnrollmentHandler
{
    public static Logger logger;
    public static final int DEP_SYNC_DATA_DEVICE_STATUS_ACTIVE = 1;
    public static final int DEP_SYNC_DATA_DEVICE_STATUS_INACTIVE = 2;
    
    public void deleteEnrolledDevice(final Long customerId, final Long tokenID, final Long currentTime) throws DataAccessException {
        AppleDEPDeviceForEnrollmentHandler.logger.log(Level.INFO, "Inside deleteEnrolledDevice");
        final Criteria dfeSnoCriteria = new Criteria(Column.getColumn("MdDeviceInfo", "SERIAL_NUMBER"), (Object)Column.getColumn("DeviceForEnrollment", "SERIAL_NUMBER"), 0);
        final Criteria dfeCustomerCriteria = new Criteria(Column.getColumn("Resource", "CUSTOMER_ID"), (Object)Column.getColumn("DeviceForEnrollment", "CUSTOMER_ID"), 0);
        final SelectQuery sQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("MdDeviceInfo"));
        sQuery.addJoin(new Join("MdDeviceInfo", "ManagedDevice", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2));
        sQuery.addJoin(new Join("ManagedDevice", "Resource", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2));
        sQuery.addJoin(new Join("ManagedDevice", "EnrollmentRequestToDevice", new String[] { "RESOURCE_ID" }, new String[] { "MANAGED_DEVICE_ID" }, 2));
        sQuery.addJoin(new Join("EnrollmentRequestToDevice", "DeviceEnrollmentRequest", new String[] { "ENROLLMENT_REQUEST_ID" }, new String[] { "ENROLLMENT_REQUEST_ID" }, 2));
        sQuery.addJoin(new Join("MdDeviceInfo", "DeviceForEnrollment", dfeCustomerCriteria.and(dfeSnoCriteria), 2));
        sQuery.addJoin(new Join("DeviceForEnrollment", "AppleDEPDeviceForEnrollment", new String[] { "ENROLLMENT_DEVICE_ID" }, new String[] { "ENROLLMENT_DEVICE_ID" }, 2));
        final Criteria enrolledStatus = new Criteria(new Column("ManagedDevice", "MANAGED_STATUS"), (Object)new Integer[] { 2, 11, 9, 6, 5, 10 }, 8);
        final Criteria dfeCustIdCriteria = new Criteria(new Column("DeviceForEnrollment", "CUSTOMER_ID"), (Object)customerId, 0);
        final Criteria tokenIDCriteria = new Criteria(new Column("AppleDEPDeviceForEnrollment", "DEP_TOKEN_ID"), (Object)tokenID, 0);
        sQuery.setCriteria(dfeCustIdCriteria.and(tokenIDCriteria).and(enrolledStatus));
        sQuery.addSelectColumn(new Column("DeviceForEnrollment", "*"));
        sQuery.addSelectColumn(new Column("DeviceEnrollmentRequest", "*"));
        sQuery.addSelectColumn(new Column("MdDeviceInfo", "*"));
        sQuery.addSelectColumn(new Column("ManagedDevice", "*"));
        final DataObject enrolledDO = MDMUtil.getPersistenceLite().get(sQuery);
        final List wfuaDfeIdList = DBUtil.getColumnValuesAsList(enrolledDO.getRows("MdDeviceInfo", new Criteria(new Column("ManagedDevice", "MANAGED_STATUS"), (Object)5, 0)), "SERIAL_NUMBER");
        List<String> deletedSerialNos = new ArrayList<String>();
        deletedSerialNos = DBUtil.getColumnValuesAsList(enrolledDO.getRows("MdDeviceInfo", new Criteria(new Column("ManagedDevice", "MANAGED_STATUS"), (Object)5, 1)), "SERIAL_NUMBER");
        final Iterator mapEnrollreqDO = enrolledDO.getRows("DeviceEnrollmentRequest", new Criteria(new Column("DeviceEnrollmentRequest", "ENROLLMENT_TYPE"), (Object)3, 0));
        List<Long> deletedDevicesEnrollmentRequest = new ArrayList<Long>();
        deletedDevicesEnrollmentRequest = DBUtil.getColumnValuesAsList(mapEnrollreqDO, "ENROLLMENT_REQUEST_ID");
        enrolledDO.deleteRows("DeviceForEnrollment", new Criteria(new Column("DeviceForEnrollment", "SERIAL_NUMBER"), (Object)deletedSerialNos.toArray(), 8));
        MDMUtil.getPersistenceLite().update(enrolledDO);
        final SelectQuery deletedDevicesQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("DeviceForEnrollment"));
        deletedDevicesQuery.addJoin(new Join("DeviceForEnrollment", "AppleDEPDeviceForEnrollment", new String[] { "ENROLLMENT_DEVICE_ID" }, new String[] { "ENROLLMENT_DEVICE_ID" }, 2));
        final Criteria updatedTimeCriteria = new Criteria(new Column("DeviceForEnrollment", "UPDATED_TIME"), (Object)currentTime, 7);
        final Criteria notInWfuaCriteria = new Criteria(new Column("DeviceForEnrollment", "SERIAL_NUMBER"), (Object)wfuaDfeIdList.toArray(), 9);
        deletedDevicesQuery.setCriteria(dfeCustIdCriteria.and(tokenIDCriteria).and(updatedTimeCriteria).and(notInWfuaCriteria));
        deletedDevicesQuery.addSelectColumn(new Column("DeviceForEnrollment", "*"));
        final DataObject deletedDevicesDO = MDMUtil.getPersistenceLite().get(deletedDevicesQuery);
        deletedSerialNos.addAll(DBUtil.getColumnValuesAsList(deletedDevicesDO.getRows("DeviceForEnrollment"), "SERIAL_NUMBER"));
        deletedDevicesDO.deleteRows("DeviceForEnrollment", (Criteria)null);
        MDMUtil.getPersistenceLite().update(deletedDevicesDO);
        AppleDEPDeviceForEnrollmentHandler.logger.log(Level.INFO, "Following devices are deleted in DEVICEFORENROLLMENT as they are already Enrolled :  {0}", deletedSerialNos);
        this.addOrUpdateEnrollmentRequestToTemplateForAlreadyEnrolledDEPDevices(deletedDevicesEnrollmentRequest, tokenID);
    }
    
    public void addOrUpdateEnrollmentRequestToTemplateForAlreadyEnrolledDEPDevices(final List<Long> enrollmentRequestIDList, final Long depTokenID) {
        AppleDEPDeviceForEnrollmentHandler.logger.log(Level.INFO, "Going to map these enrollmentRequest [{0}] to DEP tokenID {1}", new Object[] { enrollmentRequestIDList, depTokenID });
        final Long templateTokenID = DEPEnrollmentUtil.getTemplateIDTokenForTokenID(depTokenID);
        if (templateTokenID != null) {
            final EnrollmentTemplateHandler enrollmentTemplateHandler = new EnrollmentTemplateHandler();
            enrollmentTemplateHandler.addOrUpdateEnrollmentRequestToTemplate(enrollmentRequestIDList, templateTokenID);
        }
    }
    
    public void addOrUpdateDEPdevicesSyncData(final Long tokenID, final List devicesList) throws DataAccessException {
        AppleDEPDeviceForEnrollmentHandler.logger.log(Level.INFO, "======= DEP FETCH STATUS FOR TOKEN {0} STARTS ========", tokenID);
        AppleDEPDeviceForEnrollmentHandler.logger.log(Level.INFO, "Total devices in Fetch for token Count:{0}-Devices:{1}", new Object[] { devicesList.size(), devicesList });
        List<String> devicesInDB = new ArrayList<String>();
        Iterator alreadyExistingRows = null;
        final SelectQuery sQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("DEPDevicesSyncData"));
        sQuery.addSelectColumn(new Column((String)null, "*"));
        sQuery.setCriteria(new Criteria(new Column("DEPDevicesSyncData", "DEP_TOKEN_ID"), (Object)tokenID, 0));
        final DataObject allRowsDO = MDMUtil.getPersistence().get(sQuery);
        if (allRowsDO != null && !allRowsDO.isEmpty()) {
            alreadyExistingRows = allRowsDO.getRows("DEPDevicesSyncData");
        }
        if (alreadyExistingRows != null && alreadyExistingRows.hasNext()) {
            devicesInDB = DBUtil.getColumnValuesAsList(alreadyExistingRows, "SERIAL_NUMBER");
        }
        final List<String> deviceExisitnginBothDBandSync = new ArrayList<String>(devicesList);
        deviceExisitnginBothDBandSync.retainAll(devicesInDB);
        final List<String> newDevicesInSync = new ArrayList<String>(devicesList);
        newDevicesInSync.removeAll(deviceExisitnginBothDBandSync);
        final List<String> missingDevicesFromSync = new ArrayList<String>(devicesInDB);
        missingDevicesFromSync.removeAll(devicesList);
        AppleDEPDeviceForEnrollmentHandler.logger.log(Level.INFO, "Devices already in DB and this fetch:Count{0}-Devices{1}:", new Object[] { deviceExisitnginBothDBandSync.size(), deviceExisitnginBothDBandSync });
        AppleDEPDeviceForEnrollmentHandler.logger.log(Level.INFO, "New devices in this Fetch:Count:{0}-Devices{1}", new Object[] { newDevicesInSync.size(), newDevicesInSync });
        AppleDEPDeviceForEnrollmentHandler.logger.log(Level.INFO, "Devices in DB missing in this fetch:Count:{0}-Devices{1}", new Object[] { missingDevicesFromSync.size(), missingDevicesFromSync });
        for (final String newDeviceSNo : newDevicesInSync) {
            final Row newRow = new Row("DEPDevicesSyncData");
            newRow.set("DEP_TOKEN_ID", (Object)tokenID);
            newRow.set("DEVICE_STATUS", (Object)1);
            newRow.set("SERIAL_NUMBER", (Object)newDeviceSNo);
            allRowsDO.addRow(newRow);
        }
        final Iterator rowsOfMissingDevices = allRowsDO.getRows("DEPDevicesSyncData", new Criteria(new Column("DEPDevicesSyncData", "SERIAL_NUMBER"), (Object)missingDevicesFromSync.toArray(new String[0]), 8));
        if (rowsOfMissingDevices != null) {
            while (rowsOfMissingDevices.hasNext()) {
                final Row oldRow = rowsOfMissingDevices.next();
                oldRow.set("DEVICE_STATUS", (Object)2);
                allRowsDO.updateRow(oldRow);
            }
        }
        final Iterator rowsOfdeviceExisitnginBothDBandSync = allRowsDO.getRows("DEPDevicesSyncData", new Criteria(new Column("DEPDevicesSyncData", "SERIAL_NUMBER"), (Object)deviceExisitnginBothDBandSync.toArray(new String[0]), 8));
        if (rowsOfdeviceExisitnginBothDBandSync != null) {
            while (rowsOfdeviceExisitnginBothDBandSync.hasNext()) {
                final Row oldRow2 = rowsOfdeviceExisitnginBothDBandSync.next();
                oldRow2.set("DEVICE_STATUS", (Object)1);
                allRowsDO.updateRow(oldRow2);
            }
        }
        if (!allRowsDO.isEmpty()) {
            MDMUtil.getPersistence().update(allRowsDO);
        }
        AppleDEPDeviceForEnrollmentHandler.logger.log(Level.INFO, "======= DEP FETCH STATUS FOR TOKEN {0} ENDS ========", tokenID);
    }
    
    public void addOrUpdateDEPdevicesSyncData(final Long tokenID, final List<String> addedDevicesList, final List<String> deletedDevicesList) throws DataAccessException {
        AppleDEPDeviceForEnrollmentHandler.logger.log(Level.INFO, "======= DEP SYNC STATUS FOR TOKEN {0} STARTS ========", tokenID);
        AppleDEPDeviceForEnrollmentHandler.logger.log(Level.INFO, "Count of New and Updated devices in this Sync:Count:{0}", new Object[] { addedDevicesList.size() });
        AppleDEPDeviceForEnrollmentHandler.logger.log(Level.INFO, "Count of Deleted devices in this sync:Count:{0}", new Object[] { deletedDevicesList.size() });
        List<String> devicesInDB = new ArrayList<String>();
        Iterator alreadyExistingRows = null;
        final SelectQuery sQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("DEPDevicesSyncData"));
        final Criteria columnCriteria = new Criteria(new Column("DEPDevicesSyncData", "DEP_TOKEN_ID"), (Object)tokenID, 0);
        sQuery.setCriteria(columnCriteria);
        sQuery.addSelectColumn(new Column("DEPDevicesSyncData", "*"));
        final DataObject allRowsDO = MDMUtil.getPersistence().get(sQuery);
        if (allRowsDO != null && !allRowsDO.isEmpty()) {
            alreadyExistingRows = allRowsDO.getRows("DEPDevicesSyncData");
        }
        if (alreadyExistingRows != null && alreadyExistingRows.hasNext()) {
            devicesInDB = DBUtil.getColumnValuesAsList(alreadyExistingRows, "SERIAL_NUMBER");
        }
        final List<String> newlyAddedDevicesList = new ArrayList<String>(addedDevicesList);
        newlyAddedDevicesList.removeAll(devicesInDB);
        final List<String> reAddedDevicesList = new ArrayList<String>(addedDevicesList);
        reAddedDevicesList.removeAll(newlyAddedDevicesList);
        for (final String newDeviceSNo : newlyAddedDevicesList) {
            final Row newRow = new Row("DEPDevicesSyncData");
            newRow.set("DEP_TOKEN_ID", (Object)tokenID);
            newRow.set("DEVICE_STATUS", (Object)1);
            newRow.set("SERIAL_NUMBER", (Object)newDeviceSNo);
            allRowsDO.addRow(newRow);
        }
        final Iterator rowsOfInactiveReAddedDevices = allRowsDO.getRows("DEPDevicesSyncData", new Criteria(new Column("DEPDevicesSyncData", "SERIAL_NUMBER"), (Object)reAddedDevicesList.toArray(new String[0]), 8).and(new Criteria(new Column("DEPDevicesSyncData", "DEVICE_STATUS"), (Object)1, 1)));
        if (rowsOfInactiveReAddedDevices != null) {
            while (rowsOfInactiveReAddedDevices.hasNext()) {
                final Row oldRow = rowsOfInactiveReAddedDevices.next();
                oldRow.set("DEVICE_STATUS", (Object)1);
                allRowsDO.updateRow(oldRow);
            }
        }
        for (final String deviceSerialNo : deletedDevicesList) {
            final Row row = allRowsDO.getRow("DEPDevicesSyncData", new Criteria(new Column("DEPDevicesSyncData", "SERIAL_NUMBER"), (Object)deviceSerialNo, 0));
            if (row != null) {
                row.set("DEVICE_STATUS", (Object)2);
                allRowsDO.updateRow(row);
            }
        }
        if (!allRowsDO.isEmpty()) {
            MDMUtil.getPersistence().update(allRowsDO);
        }
        AppleDEPDeviceForEnrollmentHandler.logger.log(Level.INFO, "======= DEP SYNC STATUS FOR TOKEN {0} ENDS ========", tokenID);
    }
    
    public List<Long> getDEPDeviceForEnrollmentIds(final Long tokenID) {
        List<Long> dfeList = new ArrayList<Long>();
        try {
            final Iterator allRowsItr = DBUtil.getRowsFromDB("AppleDEPDeviceForEnrollment", "DEP_TOKEN_ID", (Object)tokenID);
            if (allRowsItr != null && allRowsItr.hasNext()) {
                dfeList = DBUtil.getColumnValuesAsList(allRowsItr, "ENROLLMENT_DEVICE_ID");
            }
        }
        catch (final Exception ex) {
            AppleDEPDeviceForEnrollmentHandler.logger.log(Level.SEVERE, "Exception while getDEPDeviceForEnrollmentIds", ex);
        }
        return dfeList;
    }
    
    public void addDevicesInDEPTokenToInternalGroup(final Long depTokenID, final Long groupID) {
        final List<Long> adefList = this.getDEPDeviceForEnrollmentIds(depTokenID);
        AppleDEPDeviceForEnrollmentHandler.logger.log(Level.INFO, "Going to add addDevicesInDEPTokenToInternalGroup DeviceForEnrollmentIDs{0},GroupID{1}", new Object[] { adefList, groupID });
        this.addDeviceForEnrollmentToCustomGroup(adefList, groupID);
    }
    
    static {
        AppleDEPDeviceForEnrollmentHandler.logger = Logger.getLogger("MDMEnrollment");
    }
}
