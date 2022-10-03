package com.me.mdm.server.customgroup.csv;

import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.DMDataSetWrapper;
import com.adventnet.sym.server.mdm.group.MDMGroupHandler;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.ds.query.SortColumn;
import com.adventnet.ds.query.Range;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import java.util.Iterator;
import com.adventnet.persistence.DataObject;
import java.util.logging.Level;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.me.devicemanagement.framework.server.exception.SyMException;
import com.adventnet.i18n.I18N;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import java.util.ArrayList;
import com.adventnet.persistence.Row;
import com.me.devicemanagement.framework.server.csv.CSVProcessor;
import com.me.devicemanagement.framework.server.csv.CustomerParamsHandler;
import org.json.simple.JSONObject;
import java.util.logging.Logger;
import com.me.devicemanagement.framework.server.csv.CSVTask;

public class AsynDeviceToCustomGroupImportTask extends CSVTask
{
    private static Logger logger;
    private final int range = 50;
    
    protected void performOperation(final JSONObject json) throws Exception {
        this.addDevicesToGroup();
    }
    
    private void addDevicesToGroup() throws Exception {
        try {
            final JSONObject jsonObj = new JSONObject();
            final boolean isIMEIInCSV = Boolean.parseBoolean(CustomerParamsHandler.getInstance().getParameterValue("DeviceToCustomGroupImport_IsIMEIInCSV", (long)this.customerID));
            final boolean isSerialNoInCSV = Boolean.parseBoolean(CustomerParamsHandler.getInstance().getParameterValue("DeviceToCustomGroupImport_IsSerialNoInCSV", (long)this.customerID));
            final boolean isEmailInCSV = Boolean.parseBoolean(CustomerParamsHandler.getInstance().getParameterValue("DeviceToCustomGroupImport_IsEmailInCSV", (long)this.customerID));
            if (isIMEIInCSV) {
                this.addDevicesToGroup("IMEI");
            }
            if (isSerialNoInCSV) {
                this.addDevicesToGroup("SERIAL_NUMBER");
            }
            if (isEmailInCSV) {
                this.addDevicesToGroup("EMAIL_ADDRESS");
            }
            this.handleInvalidEnties(jsonObj);
        }
        catch (final Exception ex) {
            throw ex;
        }
    }
    
    private void addDevicesToGroup(final String primaryColumn) throws Exception {
        try {
            int completedCount = 0;
            final String countStr = CustomerParamsHandler.getInstance().getParameterValue(CSVProcessor.getProcessedLabel("CustomGroupImport"), (long)this.customerID);
            if (countStr != null) {
                completedCount = Integer.parseInt(countStr);
            }
            DataObject inputdobj = this.getDeviceToCustomGroupImportDetailsDO(primaryColumn);
            if (!inputdobj.isEmpty()) {
                int processed = 0;
                do {
                    Iterator iter = null;
                    if (primaryColumn.equals("IMEI")) {
                        iter = inputdobj.getRows("MdSIMInfo");
                    }
                    else if (primaryColumn.equals("SERIAL_NUMBER")) {
                        iter = inputdobj.getRows("MdDeviceInfo");
                    }
                    else if (primaryColumn.equals("EMAIL_ADDRESS")) {
                        iter = inputdobj.getRows("ManagedUser");
                    }
                    if (iter.hasNext()) {
                        Row r = iter.next();
                        do {
                            ++processed;
                            final ArrayList<Long> resourceIDList = new ArrayList<Long>();
                            Criteria deviceToCustomGroupCriteria = null;
                            if (primaryColumn.equals("IMEI")) {
                                resourceIDList.add((Long)r.get("RESOURCE_ID"));
                                deviceToCustomGroupCriteria = new Criteria(Column.getColumn("DeviceToCustomGroupImportInfo", "IMEI"), r.get("IMEI"), 0, false);
                            }
                            else if (primaryColumn.equals("SERIAL_NUMBER")) {
                                resourceIDList.add((Long)r.get("RESOURCE_ID"));
                                deviceToCustomGroupCriteria = new Criteria(Column.getColumn("DeviceToCustomGroupImportInfo", "SERIAL_NUMBER"), r.get("SERIAL_NUMBER"), 0, false);
                            }
                            else if (primaryColumn.equals("EMAIL_ADDRESS")) {
                                final Long managedUserID = (Long)r.get("MANAGED_USER_ID");
                                final Iterator userToDeviceMappingIter = inputdobj.getRows("ManagedUserToDevice", new Criteria(Column.getColumn("ManagedUserToDevice", "MANAGED_USER_ID"), (Object)managedUserID, 0));
                                while (userToDeviceMappingIter.hasNext()) {
                                    final Row managedUserToDeviceRow = userToDeviceMappingIter.next();
                                    resourceIDList.add((Long)managedUserToDeviceRow.get("MANAGED_DEVICE_ID"));
                                }
                                deviceToCustomGroupCriteria = new Criteria(Column.getColumn("DeviceToCustomGroupImportInfo", "EMAIL_ADDRESS"), r.get("EMAIL_ADDRESS"), 0, false);
                            }
                            final Row csvImportRow = inputdobj.getRow("DeviceToCustomGroupImportInfo", deviceToCustomGroupCriteria);
                            final String groupName = (String)csvImportRow.get("GROUP_NAME");
                            for (final Long resourceID : resourceIDList) {
                                final Row managedDeviceRow = inputdobj.getRow("ManagedDevice", new Criteria(Column.getColumn("ManagedDevice", "RESOURCE_ID"), (Object)resourceID, 0));
                                final Integer platformType = (Integer)managedDeviceRow.get("PLATFORM_TYPE");
                                try {
                                    if (groupName == null || groupName.isEmpty()) {
                                        throw new SyMException(51018, I18N.getMsg("dc.mdm.device_mgmt.error_no_group_identifier", new Object[0]), "dc.mdm.device_mgmt.error_no_group_identifier", (Throwable)null);
                                    }
                                    this.addManagedDeviceToGroup(groupName, resourceID);
                                }
                                catch (final SyMException ex) {
                                    if (primaryColumn.equals("EMAIL_ADDRESS") && ex.getErrorCode() == 14008) {
                                        continue;
                                    }
                                    csvImportRow.set("ERROR_REMARKS", (Object)ex.getErrorKey());
                                    inputdobj.updateRow(csvImportRow);
                                }
                            }
                            r = null;
                            if (iter.hasNext()) {
                                r = iter.next();
                            }
                            if (!primaryColumn.equals("EMAIL_ADDRESS")) {
                                final String errorRemarks = (String)csvImportRow.get("ERROR_REMARKS");
                                if (errorRemarks != null && !errorRemarks.isEmpty()) {
                                    continue;
                                }
                                inputdobj.deleteRow(csvImportRow);
                            }
                            else if (r != null) {
                                final String email = (String)r.get("EMAIL_ADDRESS");
                                final String importEmail = (String)csvImportRow.get("EMAIL_ADDRESS");
                                if (email.equalsIgnoreCase(importEmail)) {
                                    continue;
                                }
                                inputdobj.deleteRow(csvImportRow);
                            }
                            else {
                                final String errorRemarks = (String)csvImportRow.get("ERROR_REMARKS");
                                if (errorRemarks != null && !errorRemarks.isEmpty()) {
                                    continue;
                                }
                                inputdobj.deleteRow(csvImportRow);
                            }
                        } while (r != null);
                    }
                    MDMUtil.getPersistence().update(inputdobj);
                    CustomerParamsHandler.getInstance().addOrUpdateParameter(CSVProcessor.getProcessedLabel("CustomGroupImport"), String.valueOf(completedCount + processed), (long)this.customerID);
                    inputdobj = this.getDeviceToCustomGroupImportDetailsDO(primaryColumn);
                } while (!inputdobj.isEmpty());
            }
        }
        catch (final Exception ex2) {
            AsynDeviceToCustomGroupImportTask.logger.log(Level.SEVERE, "Exception while adding devices to group:{0}", ex2);
            throw ex2;
        }
    }
    
    private void handleInvalidEnties(final JSONObject jsonObj) throws Exception {
        try {
            int completedCount = 0;
            final String countStr = CustomerParamsHandler.getInstance().getParameterValue(CSVProcessor.getProcessedLabel("CustomGroupImport"), (long)this.customerID);
            if (countStr != null) {
                completedCount = Integer.parseInt(countStr);
            }
            final SelectQuery sQuery = (SelectQuery)new SelectQueryImpl(new Table("DeviceToCustomGroupImportInfo"));
            sQuery.addSelectColumn(Column.getColumn("DeviceToCustomGroupImportInfo", "DEVICE_IMPORT_ID"));
            sQuery.addSelectColumn(Column.getColumn("DeviceToCustomGroupImportInfo", "IMEI"));
            sQuery.addSelectColumn(Column.getColumn("DeviceToCustomGroupImportInfo", "SERIAL_NUMBER"));
            sQuery.addSelectColumn(Column.getColumn("DeviceToCustomGroupImportInfo", "EMAIL_ADDRESS"));
            sQuery.addSelectColumn(Column.getColumn("DeviceToCustomGroupImportInfo", "GROUP_NAME"));
            sQuery.addSelectColumn(Column.getColumn("DeviceToCustomGroupImportInfo", "ERROR_REMARKS"));
            sQuery.addSelectColumn(Column.getColumn("DeviceToCustomGroupImportInfo", "CUSTOMER_ID"));
            sQuery.setCriteria(new Criteria(Column.getColumn("DeviceToCustomGroupImportInfo", "ERROR_REMARKS"), (Object)null, 0).and(new Criteria(Column.getColumn("DeviceToCustomGroupImportInfo", "CUSTOMER_ID"), (Object)this.customerID, 0)));
            sQuery.setRange(new Range(0, 50));
            sQuery.addSortColumn(new SortColumn(Column.getColumn("DeviceToCustomGroupImportInfo", "DEVICE_IMPORT_ID"), true));
            DataObject dobj = MDMUtil.getPersistence().get(sQuery);
            int processed = 0;
            do {
                final Iterator iter = dobj.getRows("DeviceToCustomGroupImportInfo");
                while (iter.hasNext()) {
                    ++processed;
                    final Row r = iter.next();
                    final String imei = (String)r.get("IMEI");
                    final String serialno = (String)r.get("SERIAL_NUMBER");
                    final String email = (String)r.get("EMAIL_ADDRESS");
                    final String groupName = (String)r.get("GROUP_NAME");
                    if ((imei == null || imei.isEmpty() || imei.equalsIgnoreCase("--")) && (serialno == null || serialno.isEmpty() || serialno.equalsIgnoreCase("--")) && (email == null || email.isEmpty() || email.equalsIgnoreCase("--"))) {
                        r.set("ERROR_REMARKS", (Object)"dc.mdm.device_mgmt.error_no_device_identifier");
                    }
                    else {
                        r.set("ERROR_REMARKS", (Object)"dc.mdm.msg.inv.bulk_edit.no_md_for_imei_serial_email");
                    }
                    dobj.updateRow(r);
                }
                MDMUtil.getPersistence().update(dobj);
                CustomerParamsHandler.getInstance().addOrUpdateParameter(CSVProcessor.getProcessedLabel("CustomGroupImport"), String.valueOf(completedCount + processed), (long)this.customerID);
                dobj = MDMUtil.getPersistence().get(sQuery);
            } while (!dobj.isEmpty());
            this.setFailureCount(this.customerID);
            AsynDeviceToCustomGroupImportTask.logger.info("Invalid Enties are handled");
        }
        catch (final Exception ex) {
            throw ex;
        }
    }
    
    private void addManagedDeviceToGroup(final String groupName, final Long managedDeviceId) throws Exception {
        final JSONObject groupDetails = MDMGroupHandler.getCustomGroupDetails(groupName, this.userID, this.customerID);
        final Long groupId = (Long)groupDetails.get((Object)"RESOURCE_ID");
        final org.json.JSONObject membergroupObjects = new org.json.JSONObject();
        membergroupObjects.put("groupId", (Object)groupId);
        membergroupObjects.put("resourceId", (Object)new Long[] { managedDeviceId });
        membergroupObjects.put("customerId", (Object)this.customerID);
        membergroupObjects.put("isMove", false);
        membergroupObjects.put("userId", (Object)this.userID);
        MDMGroupHandler.getInstance().addMembertoGroup(membergroupObjects);
    }
    
    private void setFailureCount(final long customerID) throws Exception {
        DMDataSetWrapper ds = null;
        try {
            final SelectQuery sQuery = (SelectQuery)new SelectQueryImpl(new Table("DeviceToCustomGroupImportInfo"));
            final Column countColumn = Column.getColumn("DeviceToCustomGroupImportInfo", "DEVICE_IMPORT_ID").count();
            countColumn.setColumnAlias("DEVICE_IMPORT_ID");
            sQuery.addSelectColumn(countColumn);
            sQuery.setCriteria(new Criteria(Column.getColumn("DeviceToCustomGroupImportInfo", "ERROR_REMARKS"), (Object)null, 1).and(new Criteria(Column.getColumn("DeviceToCustomGroupImportInfo", "CUSTOMER_ID"), (Object)customerID, 0)));
            ds = DMDataSetWrapper.executeQuery((Object)sQuery);
            ds.next();
            final JSONObject jsonObj = new JSONObject();
            jsonObj.put((Object)CSVProcessor.getFailedLabel("CustomGroupImport"), (Object)String.valueOf(ds.getValue("DEVICE_IMPORT_ID")));
            jsonObj.put((Object)CSVProcessor.getStatusLabel("CustomGroupImport"), (Object)"COMPLETED");
            CustomerParamsHandler.getInstance().addOrUpdateParameters(jsonObj, customerID);
            AsynDeviceToCustomGroupImportTask.logger.info("Persisted failure count in Customer Params");
        }
        catch (final Exception ex) {
            throw ex;
        }
    }
    
    private DataObject getDeviceToCustomGroupImportDetailsDO(final String primaryColumn) throws Exception {
        final SelectQuery inputQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("DeviceEnrollmentRequest"));
        inputQuery.addJoin(new Join("DeviceEnrollmentRequest", "EnrollmentRequestToDevice", new String[] { "ENROLLMENT_REQUEST_ID" }, new String[] { "ENROLLMENT_REQUEST_ID" }, 2));
        inputQuery.addJoin(new Join("EnrollmentRequestToDevice", "ManagedDevice", new String[] { "MANAGED_DEVICE_ID" }, new String[] { "RESOURCE_ID" }, 2));
        inputQuery.addJoin(new Join("ManagedDevice", "Resource", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2));
        if (primaryColumn.equals("IMEI")) {
            inputQuery.addJoin(new Join("ManagedDevice", "MdSIMInfo", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2));
            inputQuery.addJoin(new Join("MdSIMInfo", "DeviceToCustomGroupImportInfo", new String[] { "IMEI" }, new String[] { "IMEI" }, 2));
        }
        else if (primaryColumn.equals("SERIAL_NUMBER")) {
            inputQuery.addJoin(new Join("ManagedDevice", "MdDeviceInfo", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2));
            inputQuery.addJoin(new Join("MdDeviceInfo", "DeviceToCustomGroupImportInfo", new String[] { "SERIAL_NUMBER" }, new String[] { "SERIAL_NUMBER" }, 2));
        }
        else if (primaryColumn.equals("EMAIL_ADDRESS")) {
            inputQuery.addJoin(new Join("ManagedDevice", "ManagedUserToDevice", new String[] { "RESOURCE_ID" }, new String[] { "MANAGED_DEVICE_ID" }, 2));
            inputQuery.addJoin(new Join("ManagedUserToDevice", "ManagedUser", new String[] { "MANAGED_USER_ID" }, new String[] { "MANAGED_USER_ID" }, 2));
            inputQuery.addJoin(new Join("ManagedUser", "DeviceToCustomGroupImportInfo", new String[] { "EMAIL_ADDRESS" }, new String[] { "EMAIL_ADDRESS" }, 2));
        }
        inputQuery.addSelectColumn(Column.getColumn("ManagedDevice", "RESOURCE_ID"));
        inputQuery.addSelectColumn(Column.getColumn("ManagedDevice", "PLATFORM_TYPE"));
        inputQuery.addSelectColumn(Column.getColumn("DeviceToCustomGroupImportInfo", "DEVICE_IMPORT_ID"));
        inputQuery.addSelectColumn(Column.getColumn("DeviceToCustomGroupImportInfo", "ERROR_REMARKS"));
        inputQuery.addSelectColumn(Column.getColumn("DeviceToCustomGroupImportInfo", "GROUP_NAME"));
        if (primaryColumn.equals("IMEI")) {
            inputQuery.addSelectColumn(Column.getColumn("MdSIMInfo", "SIM_ID", "MDSIMINFO.SIM_ID"));
            inputQuery.addSelectColumn(Column.getColumn("MdSIMInfo", "RESOURCE_ID", "MDSIMINFO.RESOURCE_ID"));
            inputQuery.addSelectColumn(Column.getColumn("MdSIMInfo", "SLOT", "MDSIMINFO.SLOT"));
            inputQuery.addSelectColumn(Column.getColumn("MdSIMInfo", "IMEI", "MDSIMINFO.IMEI"));
        }
        else if (primaryColumn.equals("SERIAL_NUMBER")) {
            inputQuery.addSelectColumn(Column.getColumn("MdDeviceInfo", "RESOURCE_ID", "MDDEVICEINFO.RESOURCE_ID"));
            inputQuery.addSelectColumn(Column.getColumn("MdDeviceInfo", "SERIAL_NUMBER", "MDDEVICEINFO.SERIAL_NUMBER"));
        }
        else if (primaryColumn.equals("EMAIL_ADDRESS")) {
            inputQuery.addSelectColumn(Column.getColumn("ManagedUserToDevice", "MANAGED_DEVICE_ID", "MANAGEDUSERTODEVICE.MANAGED_DEVICE_ID"));
            inputQuery.addSelectColumn(Column.getColumn("ManagedUserToDevice", "MANAGED_USER_ID", "MANAGEDUSERTODEVICE.MANAGED_USER_ID"));
            inputQuery.addSelectColumn(Column.getColumn("ManagedUser", "MANAGED_USER_ID", "MANAGEDUSER.MANAGED_USER_ID"));
            inputQuery.addSelectColumn(Column.getColumn("ManagedUser", "EMAIL_ADDRESS", "MANAGEDUSER.EMAIL_ADDRESS"));
        }
        if (primaryColumn.equals("IMEI")) {
            inputQuery.addSelectColumn(Column.getColumn("DeviceToCustomGroupImportInfo", "IMEI", "DEVICETOCUSTOMGROUPIMPORTINFO.IMEI"));
        }
        else if (primaryColumn.equals("SERIAL_NUMBER")) {
            inputQuery.addSelectColumn(Column.getColumn("DeviceToCustomGroupImportInfo", "SERIAL_NUMBER", "DEVICETOCUSTOMGROUPIMPORTINFO.SERIAL_NUMBER"));
        }
        else if (primaryColumn.equals("EMAIL_ADDRESS")) {
            inputQuery.addSelectColumn(Column.getColumn("DeviceToCustomGroupImportInfo", "EMAIL_ADDRESS", "DEVICETOCUSTOMGROUPIMPORTINFO.EMAIL_ADDRESS"));
        }
        Criteria baseCriteria = new Criteria(Column.getColumn("DeviceToCustomGroupImportInfo", "CUSTOMER_ID"), (Object)this.customerID, 0).and(new Criteria(Column.getColumn("Resource", "CUSTOMER_ID"), (Object)this.customerID, 0)).and(new Criteria(Column.getColumn("DeviceToCustomGroupImportInfo", "ERROR_REMARKS"), (Object)null, 0));
        if (primaryColumn.equals("IMEI")) {
            baseCriteria = baseCriteria.and(new Criteria(Column.getColumn("MdSIMInfo", "IMEI", "MDSIMINFO.IMEI"), (Object)new String[] { "", "--", null }, 9));
        }
        inputQuery.setCriteria(baseCriteria);
        inputQuery.setRange(new Range(0, 50));
        inputQuery.addSortColumn(new SortColumn(Column.getColumn("DeviceToCustomGroupImportInfo", "DEVICE_IMPORT_ID"), true));
        final DataObject inputdobj = MDMUtil.getPersistence().get(inputQuery);
        return inputdobj;
    }
    
    static {
        AsynDeviceToCustomGroupImportTask.logger = Logger.getLogger("MDMLogger");
    }
}
