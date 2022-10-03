package com.adventnet.sym.server.mdm.inv;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import com.adventnet.ds.query.DMDataSetWrapper;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.sym.server.mdm.enroll.DeviceManagedDetailsHandler;
import com.me.devicemanagement.framework.server.customer.CustomerInfoUtil;
import com.me.devicemanagement.framework.server.exception.SyMException;
import com.adventnet.sym.server.mdm.util.MDMStringUtils;
import com.adventnet.persistence.Row;
import java.util.HashMap;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.adventnet.ds.query.Query;
import com.adventnet.db.api.RelationalAPI;
import com.adventnet.ds.query.SortColumn;
import com.adventnet.ds.query.Range;
import com.me.mdm.server.role.RBDAUtil;
import com.me.devicemanagement.framework.server.authentication.DMUserHandler;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.me.devicemanagement.framework.server.csv.CSVProcessor;
import java.util.Iterator;
import java.util.Set;
import java.util.List;
import java.util.logging.Level;
import com.adventnet.sym.server.mdm.core.ManagedDeviceHandler;
import java.util.Collection;
import com.adventnet.persistence.WritableDataObject;
import java.util.ArrayList;
import com.adventnet.persistence.DataObject;
import org.json.JSONArray;
import com.adventnet.sym.server.mdm.util.JSONUtil;
import java.util.Map;
import com.me.devicemanagement.framework.server.csv.CustomerParamsHandler;
import org.json.simple.JSONObject;
import java.util.logging.Logger;
import com.me.devicemanagement.framework.server.csv.CSVTask;

public class AsynMDCustomDetailsUpdateTask extends CSVTask
{
    private static Logger logger;
    private JSONObject columnList;
    private final int range = 50;
    
    protected void performOperation(final JSONObject json) throws Exception {
        this.updateMultipleDetails();
    }
    
    public void updateMultipleDetails() throws Exception {
        try {
            List resourceList = null;
            DataObject updatedDO = null;
            final JSONObject jsonObj = new JSONObject();
            org.json.JSONObject responseJSON = new org.json.JSONObject();
            final boolean isIMEIInCSV = Boolean.parseBoolean(CustomerParamsHandler.getInstance().getParameterValue("IsIMEIInCSV", (long)this.customerID));
            final boolean isSerialNoInCSV = Boolean.parseBoolean(CustomerParamsHandler.getInstance().getParameterValue("isSerialNoInCSV", (long)this.customerID));
            jsonObj.put((Object)"IsIMEIInCSV", (Object)isIMEIInCSV);
            jsonObj.put((Object)"isSerialNoInCSV", (Object)isSerialNoInCSV);
            jsonObj.put((Object)"isDeviceNameInCSV", (Object)Boolean.parseBoolean(CustomerParamsHandler.getInstance().getParameterValue("isDeviceNameInCSV", (long)this.customerID)));
            this.columnList = new MDCustomDetailsCSVProcessor().getCustomColumnDetails();
            final Set<Map.Entry<String, JSONObject>> entrySet = this.columnList.entrySet();
            for (final Map.Entry<String, JSONObject> element : entrySet) {
                final JSONObject columnDetails = element.getValue();
                final String paramName = (String)columnDetails.get((Object)"CUSTOMER_PARAM_NAME");
                jsonObj.put((Object)paramName, (Object)Boolean.parseBoolean(CustomerParamsHandler.getInstance().getParameterValue(paramName, (long)this.customerID)));
            }
            if (isIMEIInCSV) {
                responseJSON = this.updateMultipleDetails(jsonObj, "IMEI");
                resourceList = JSONUtil.getInstance().convertLongJSONArrayTOList((JSONArray)responseJSON.opt("resource_list"));
                updatedDO = (DataObject)responseJSON.opt("data_object");
            }
            if (isSerialNoInCSV) {
                if (resourceList == null) {
                    resourceList = new ArrayList();
                }
                if (updatedDO == null) {
                    updatedDO = (DataObject)new WritableDataObject();
                }
                responseJSON = this.updateMultipleDetails(jsonObj, "SERIAL_NUMBER");
                resourceList.addAll(JSONUtil.getInstance().convertLongJSONArrayTOList((JSONArray)responseJSON.opt("resource_list")));
                updatedDO.merge((DataObject)responseJSON.opt("data_object"));
            }
            this.handleInvalidEnties(jsonObj);
            MDCustomDetailsRequestHandler.getInstance().checkAndSendDeviceNameUpdateCommand(resourceList);
            ManagedDeviceHandler.getInstance().invokeDeviceListenersForBulkDeviceDetailsEdit(updatedDO, this.customerID);
        }
        catch (final Exception ex) {
            AsynMDCustomDetailsUpdateTask.logger.log(Level.SEVERE, "Exception while updating multiple device details via CSV", ex);
            throw ex;
        }
    }
    
    private org.json.JSONObject updateMultipleDetails(final JSONObject jsonObj, final String joinBy) throws Exception {
        final org.json.JSONObject responseJSON = new org.json.JSONObject();
        final DataObject updatedDO = (DataObject)new WritableDataObject();
        List resourceList = null;
        try {
            resourceList = new ArrayList();
            final boolean isDeviceNameInCSV = (boolean)jsonObj.get((Object)"isDeviceNameInCSV");
            int completedCount = 0;
            final String countStr = CustomerParamsHandler.getInstance().getParameterValue(CSVProcessor.getProcessedLabel("MDCustomDetails"), (long)this.customerID);
            if (countStr != null) {
                completedCount = Integer.parseInt(countStr);
            }
            final SelectQuery sQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("Resource"));
            sQuery.addJoin(new Join("Resource", "ManagedDevice", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2));
            if (joinBy.equals("IMEI")) {
                sQuery.addJoin(new Join(Table.getTable("Resource"), Table.getTable("MdSIMInfo"), new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2));
                sQuery.addJoin(new Join("MdSIMInfo", "ManagedDeviceExtn", new String[] { "RESOURCE_ID" }, new String[] { "MANAGED_DEVICE_ID" }, 2));
                sQuery.addJoin(new Join(Table.getTable("MdSIMInfo"), Table.getTable("MDCustomDetailsImportInfo"), new String[] { "IMEI" }, new String[] { "IMEI" }, 2));
            }
            else if (joinBy.equals("SERIAL_NUMBER")) {
                sQuery.addJoin(new Join(Table.getTable("Resource"), Table.getTable("MdDeviceInfo"), new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2));
                sQuery.addJoin(new Join("MdDeviceInfo", "ManagedDeviceExtn", new String[] { "RESOURCE_ID" }, new String[] { "MANAGED_DEVICE_ID" }, 2));
                sQuery.addJoin(new Join(Table.getTable("MdDeviceInfo"), Table.getTable("MDCustomDetailsImportInfo"), new String[] { "SERIAL_NUMBER" }, new String[] { "SERIAL_NUMBER" }, 2));
            }
            sQuery.addSelectColumn(Column.getColumn("ManagedDeviceExtn", "MANAGED_DEVICE_ID", "ManagedDeviceExtn.MANAGED_DEVICE_ID"));
            sQuery.addSelectColumn(Column.getColumn("ManagedDeviceExtn", "NAME", "ManagedDeviceExtn.NAME"));
            final Set<Map.Entry<String, JSONObject>> entrySet = this.columnList.entrySet();
            for (final Map.Entry<String, JSONObject> element : entrySet) {
                final String columnName = element.getKey();
                sQuery.addSelectColumn(Column.getColumn("ManagedDeviceExtn", columnName, "ManagedDeviceExtn." + columnName));
            }
            sQuery.addSelectColumn(Column.getColumn("ManagedDeviceExtn", "IS_MODIFIED", "ManagedDeviceExtn.IS_MODIFIED"));
            sQuery.addSelectColumn(Column.getColumn("ManagedDeviceExtn", "USER_ID", "ManagedDeviceExtn.USER_ID"));
            sQuery.addSelectColumn(Column.getColumn("ManagedDeviceExtn", "LAST_MODIFIED_TIME", "ManagedDeviceExtn.LAST_MODIFIED_TIME"));
            sQuery.addSelectColumn(Column.getColumn("ManagedDevice", "RESOURCE_ID", "ManagedDevice.RESOURCE_ID"));
            sQuery.addSelectColumn(Column.getColumn("ManagedDevice", "UDID", "ManagedDevice.UDID"));
            if (joinBy.equals("IMEI")) {
                sQuery.addSelectColumn(Column.getColumn("MdSIMInfo", "SIM_ID", "MdSIMInfo.SIM_ID"));
                sQuery.addSelectColumn(Column.getColumn("MdSIMInfo", "RESOURCE_ID", "MdSIMInfo.RESOURCE_ID"));
                sQuery.addSelectColumn(Column.getColumn("MdSIMInfo", "SLOT", "MdSIMInfo.SLOT"));
                sQuery.addSelectColumn(Column.getColumn("MdSIMInfo", "IMEI", "MdSIMInfo.IMEI"));
            }
            else if (joinBy.equals("SERIAL_NUMBER")) {
                sQuery.addSelectColumn(Column.getColumn("MdDeviceInfo", "RESOURCE_ID", "MdDeviceInfo.RESOURCE_ID"));
                sQuery.addSelectColumn(Column.getColumn("MdDeviceInfo", "SERIAL_NUMBER", "MdDeviceInfo.SERIAL_NUMBER"));
            }
            sQuery.addSelectColumn(Column.getColumn("MDCustomDetailsImportInfo", "DEVICE_CUSTOM_ID", "MDCustomDetailsImportInfo.DEVICE_CUSTOM_ID"));
            sQuery.addSelectColumn(Column.getColumn("MDCustomDetailsImportInfo", "DEVICE_NAME", "MDCustomDetailsImportInfo.DEVICE_NAME"));
            for (final Map.Entry<String, JSONObject> element : entrySet) {
                final String columnName = element.getKey();
                sQuery.addSelectColumn(Column.getColumn("MDCustomDetailsImportInfo", columnName, "MDCustomDetailsImportInfo." + columnName));
            }
            if (joinBy.equals("IMEI")) {
                sQuery.addSelectColumn(Column.getColumn("MDCustomDetailsImportInfo", "IMEI", "MDCustomDetailsImportInfo.IMEI"));
            }
            else if (joinBy.equals("SERIAL_NUMBER")) {
                sQuery.addSelectColumn(Column.getColumn("MDCustomDetailsImportInfo", "SERIAL_NUMBER", "MDCustomDetailsImportInfo.SERIAL_NUMBER"));
            }
            sQuery.addSelectColumn(Column.getColumn("MDCustomDetailsImportInfo", "ERROR_REMARKS", "MDCustomDetailsImportInfo.ERROR_REMARKS"));
            Criteria baseCriteria = new Criteria(Column.getColumn("MDCustomDetailsImportInfo", "CUSTOMER_ID"), (Object)this.customerID, 0).and(new Criteria(Column.getColumn("Resource", "CUSTOMER_ID"), (Object)this.customerID, 0)).and(new Criteria(Column.getColumn("MDCustomDetailsImportInfo", "ERROR_REMARKS"), (Object)null, 0));
            if (joinBy.equals("IMEI")) {
                baseCriteria = baseCriteria.and(new Criteria(Column.getColumn("MdSIMInfo", "IMEI", "MdSIMInfo.IMEI"), (Object)new String[] { "", "--", null }, 9));
            }
            sQuery.setCriteria(baseCriteria);
            final Long loginId = DMUserHandler.getLoginIdForUserId(this.userID);
            final boolean isMDMAdmin = DMUserHandler.isUserInRole(loginId, "All_Managed_Mobile_Devices");
            if (!isMDMAdmin) {
                sQuery.addJoin(RBDAUtil.getInstance().getUserDeviceMappingJoin("ManagedDevice", "RESOURCE_ID"));
                final Criteria cgCri = RBDAUtil.getInstance().getUserDeviceMappingCriteria(loginId);
                final Criteria cri = sQuery.getCriteria().and(cgCri);
                sQuery.setCriteria(cri);
            }
            sQuery.setRange(new Range(0, 50));
            sQuery.addSortColumn(new SortColumn(Column.getColumn("MDCustomDetailsImportInfo", "DEVICE_CUSTOM_ID"), true));
            final String sql = RelationalAPI.getInstance().getSelectSQL((Query)sQuery);
            DataObject dobj = MDMUtil.getPersistence().get(sQuery);
            int processed = 0;
            final ArrayList<Long> deviceNameUpdatedResourceList = new ArrayList<Long>();
            final HashMap<Long, String> resourceDeviceNameMap = new HashMap<Long, String>();
            do {
                Iterator iter;
                if (joinBy.equals("IMEI")) {
                    iter = dobj.getRows("MdSIMInfo");
                }
                else {
                    iter = dobj.getRows("MdDeviceInfo");
                }
                final List<Long> deleteRowList = new ArrayList<Long>();
                while (iter.hasNext()) {
                    ++processed;
                    final Row r = iter.next();
                    long resourceID;
                    if (joinBy.equals("IMEI")) {
                        resourceID = (long)r.get("RESOURCE_ID");
                    }
                    else {
                        resourceID = (long)r.get("RESOURCE_ID");
                    }
                    final Row updateRow = dobj.getRow("ManagedDeviceExtn", new Criteria(Column.getColumn("ManagedDeviceExtn", "MANAGED_DEVICE_ID"), (Object)resourceID, 0));
                    final String udid = (String)dobj.getValue("ManagedDevice", "UDID", new Criteria(Column.getColumn("ManagedDevice", "RESOURCE_ID"), (Object)resourceID, 0));
                    Row inputRow = null;
                    if (joinBy.equals("IMEI")) {
                        inputRow = dobj.getRow("MDCustomDetailsImportInfo", new Criteria(Column.getColumn("MDCustomDetailsImportInfo", "IMEI"), r.get("IMEI"), 0));
                    }
                    else if (joinBy.equals("SERIAL_NUMBER")) {
                        inputRow = dobj.getRow("MDCustomDetailsImportInfo", new Criteria(Column.getColumn("MDCustomDetailsImportInfo", "SERIAL_NUMBER"), r.get("SERIAL_NUMBER"), 0));
                    }
                    try {
                        final String deviceName = (String)inputRow.get("DEVICE_NAME");
                        if (!MDMStringUtils.isEmpty(deviceName) && deviceName.matches("^[0-9a-zA-Z_\\-\\.\\$@\\,\\:\\&amp;~\\#\\(\\)\\[\\]\\%\\=\\\\^\\+\\?\\'\\/\\|\\!\\P{InBasicLatin}\\s]+$")) {
                            resourceList.add(resourceID);
                            updateRow.set("NAME", (Object)deviceName);
                        }
                        else if (isDeviceNameInCSV) {
                            throw new SyMException(52000, "Device name is invalid", "dc.mdm.msg.inv.bulk.edit.invalid.device.name", (Throwable)null);
                        }
                        for (final Map.Entry<String, JSONObject> element2 : entrySet) {
                            final String columnName2 = element2.getKey();
                            final JSONObject columnDetails = element2.getValue();
                            final String paramName = (String)columnDetails.get((Object)"CUSTOMER_PARAM_NAME");
                            final String type = (String)columnDetails.get((Object)"DATA_TYPE");
                            this.updateRowIfPresent(updateRow, inputRow, jsonObj, columnName2, paramName, type);
                        }
                        updateRow.set("IS_MODIFIED", (Object)true);
                        updateRow.set("USER_ID", (Object)this.userID);
                        updateRow.set("LAST_MODIFIED_TIME", (Object)System.currentTimeMillis());
                        updateRow.validate();
                        dobj.updateRow(updateRow);
                        updatedDO.addRow(updateRow);
                        deviceNameUpdatedResourceList.add(resourceID);
                        resourceDeviceNameMap.put(resourceID, deviceName);
                        deleteRowList.add((Long)inputRow.get("DEVICE_CUSTOM_ID"));
                        Logger.getLogger("MDMCustomDetailsLogger").info("Operation: UPDATE (CSV) Managed Device Id:" + updateRow.get("MANAGED_DEVICE_ID") + " UDID:" + udid + " Custom Device Name:" + updateRow.get("NAME"));
                    }
                    catch (final SyMException ex) {
                        AsynMDCustomDetailsUpdateTask.logger.log(Level.SEVERE, "Exception while updating row :{0}", (Throwable)ex);
                        if (ex.getErrorCode() == 52000 || ex.getErrorCode() == 52001 || ex.getErrorCode() == 52002) {
                            inputRow.set("ERROR_REMARKS", (Object)ex.getErrorKey());
                        }
                        else {
                            inputRow.set("ERROR_REMARKS", (Object)"dc.mdm.msg.inv.bulk_edit.update_error");
                        }
                        dobj.updateRow(inputRow);
                    }
                    catch (final Exception ex2) {
                        AsynMDCustomDetailsUpdateTask.logger.log(Level.SEVERE, "Exception while updating row :{0}", ex2);
                        inputRow.set("ERROR_REMARKS", (Object)"dc.mdm.msg.inv.bulk_edit.update_error");
                        dobj.updateRow(inputRow);
                    }
                }
                dobj.deleteRows("MDCustomDetailsImportInfo", new Criteria(Column.getColumn("MDCustomDetailsImportInfo", "DEVICE_CUSTOM_ID"), (Object)deleteRowList.toArray(), 8));
                MDMUtil.getPersistence().update(dobj);
                CustomerParamsHandler.getInstance().addOrUpdateParameter(CSVProcessor.getProcessedLabel("MDCustomDetails"), String.valueOf(completedCount + processed), (long)this.customerID);
                if (CustomerInfoUtil.getInstance().isMSP() && deviceNameUpdatedResourceList.size() > 0) {
                    new DeviceManagedDetailsHandler().updateDeviceNameInHistoryTable(this.customerID, deviceNameUpdatedResourceList, resourceDeviceNameMap);
                }
                dobj = MDMUtil.getPersistence().get(sQuery);
            } while (!dobj.isEmpty());
        }
        catch (final Exception ex3) {
            AsynMDCustomDetailsUpdateTask.logger.log(Level.SEVERE, "Exception while updating multiple device details :{0}", ex3);
            throw ex3;
        }
        responseJSON.put("resource_list", (Collection)resourceList);
        responseJSON.put("data_object", (Object)updatedDO);
        return responseJSON;
    }
    
    private void handleInvalidEnties(final JSONObject jsonObj) throws Exception {
        try {
            final boolean isIMEIInCSVHdr = (boolean)jsonObj.get((Object)"IsIMEIInCSV");
            final boolean isSerialNoInCSVHdr = (boolean)jsonObj.get((Object)"isSerialNoInCSV");
            int completedCount = 0;
            final String countStr = CustomerParamsHandler.getInstance().getParameterValue(CSVProcessor.getProcessedLabel("MDCustomDetails"), (long)this.customerID);
            if (countStr != null) {
                completedCount = Integer.parseInt(countStr);
            }
            final SelectQuery sQuery = (SelectQuery)new SelectQueryImpl(new Table("MDCustomDetailsImportInfo"));
            sQuery.addSelectColumn(Column.getColumn("MDCustomDetailsImportInfo", "DEVICE_CUSTOM_ID"));
            sQuery.addSelectColumn(Column.getColumn("MDCustomDetailsImportInfo", "DEVICE_NAME"));
            final Set<Map.Entry<String, JSONObject>> entrySet = this.columnList.entrySet();
            for (final Map.Entry<String, JSONObject> element : entrySet) {
                final String columnName = element.getKey();
                sQuery.addSelectColumn(Column.getColumn("MDCustomDetailsImportInfo", columnName, "MDCustomDetailsImportInfo." + columnName));
            }
            sQuery.addSelectColumn(Column.getColumn("MDCustomDetailsImportInfo", "IMEI", "MDCustomDetailsImportInfo.IMEI"));
            sQuery.addSelectColumn(Column.getColumn("MDCustomDetailsImportInfo", "SERIAL_NUMBER", "MDCustomDetailsImportInfo.SERIAL_NUMBER"));
            sQuery.addSelectColumn(Column.getColumn("MDCustomDetailsImportInfo", "ERROR_REMARKS", "MDCustomDetailsImportInfo.ERROR_REMARKS"));
            sQuery.addSelectColumn(Column.getColumn("MDCustomDetailsImportInfo", "CUSTOMER_ID", "MDCustomDetailsImportInfo.CUSTOMER_ID"));
            sQuery.setCriteria(new Criteria(Column.getColumn("MDCustomDetailsImportInfo", "ERROR_REMARKS"), (Object)null, 0).and(new Criteria(Column.getColumn("MDCustomDetailsImportInfo", "CUSTOMER_ID"), (Object)this.customerID, 0)));
            sQuery.setRange(new Range(0, 50));
            sQuery.addSortColumn(new SortColumn(Column.getColumn("MDCustomDetailsImportInfo", "DEVICE_CUSTOM_ID"), true));
            DataObject dobj = MDMUtil.getPersistence().get(sQuery);
            int processed = 0;
            do {
                final Iterator iter = dobj.getRows("MDCustomDetailsImportInfo");
                while (iter.hasNext()) {
                    ++processed;
                    final Row r = iter.next();
                    final String imei = (String)r.get("IMEI");
                    final String serialno = (String)r.get("SERIAL_NUMBER");
                    if (imei != null && serialno != null) {
                        r.set("ERROR_REMARKS", (Object)"dc.mdm.msg.inv.bulk_edit.no_md_for_both");
                    }
                    else if (imei != null) {
                        r.set("ERROR_REMARKS", (Object)"dc.mdm.msg.inv.bulk_edit.no_md_for_imei");
                    }
                    else if (serialno != null) {
                        r.set("ERROR_REMARKS", (Object)"dc.mdm.msg.inv.bulk_edit.no_md_for_slno");
                    }
                    else if (isIMEIInCSVHdr && isSerialNoInCSVHdr) {
                        r.set("ERROR_REMARKS", (Object)"dc.mdm.msg.inv.bulk_edit.no_imei_slno");
                    }
                    else if (isIMEIInCSVHdr) {
                        r.set("ERROR_REMARKS", (Object)"dc.mdm.msg.inv.bulk.edit.no.imei");
                    }
                    else if (isSerialNoInCSVHdr) {
                        r.set("ERROR_REMARKS", (Object)"dc.mdm.msg.inv.bulk.edit.no.slno");
                    }
                    dobj.updateRow(r);
                }
                MDMUtil.getPersistence().update(dobj);
                CustomerParamsHandler.getInstance().addOrUpdateParameter(CSVProcessor.getProcessedLabel("MDCustomDetails"), String.valueOf(completedCount + processed), (long)this.customerID);
                dobj = MDMUtil.getPersistence().get(sQuery);
            } while (!dobj.isEmpty());
            this.setFailureCount(this.customerID);
            AsynMDCustomDetailsUpdateTask.logger.info("Invalid Enties are handled");
        }
        catch (final Exception ex) {
            throw ex;
        }
    }
    
    private void setFailureCount(final long customerID) throws Exception {
        DMDataSetWrapper ds = null;
        try {
            final SelectQuery sQuery = (SelectQuery)new SelectQueryImpl(new Table("MDCustomDetailsImportInfo"));
            final Column countColumn = Column.getColumn("MDCustomDetailsImportInfo", "DEVICE_CUSTOM_ID").count();
            countColumn.setColumnAlias("DEVICE_CUSTOM_ID");
            sQuery.addSelectColumn(countColumn);
            sQuery.setCriteria(new Criteria(Column.getColumn("MDCustomDetailsImportInfo", "ERROR_REMARKS"), (Object)null, 1).and(new Criteria(Column.getColumn("MDCustomDetailsImportInfo", "CUSTOMER_ID"), (Object)customerID, 0)));
            ds = DMDataSetWrapper.executeQuery((Object)sQuery);
            ds.next();
            final JSONObject jsonObj = new JSONObject();
            jsonObj.put((Object)CSVProcessor.getFailedLabel("MDCustomDetails"), (Object)String.valueOf(ds.getValue("DEVICE_CUSTOM_ID")));
            jsonObj.put((Object)CSVProcessor.getStatusLabel("MDCustomDetails"), (Object)"COMPLETED");
            CustomerParamsHandler.getInstance().addOrUpdateParameters(jsonObj, customerID);
            AsynMDCustomDetailsUpdateTask.logger.info("Persisted failure count in Customer Params");
        }
        catch (final Exception ex) {
            throw ex;
        }
    }
    
    private void updateRowIfPresent(final Row updateRow, final Row inputRow, final JSONObject jsonObj, final String columnName, final String paramName, final String type) throws SyMException {
        final boolean isParamNameInCSV = (boolean)jsonObj.get((Object)paramName);
        if (isParamNameInCSV) {
            if (inputRow.get(columnName) == null) {
                updateRow.set(columnName, (Object)null);
            }
            else if (type.equalsIgnoreCase("String")) {
                updateRow.set(columnName, (Object)inputRow.get(columnName));
            }
            else {
                if (type.equalsIgnoreCase("Date")) {
                    try {
                        if (((String)inputRow.get(columnName)).matches("^(0[1-9]|1[0-2])\\/(0[1-9]|1\\d{1}|2\\d{1}|3[01])\\/\\d{4}$")) {
                            final SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
                            updateRow.set(columnName, (Object)sdf.parse((String)inputRow.get(columnName)).getTime());
                            return;
                        }
                        throw new ParseException("Not in MM/dd/yyyy format", 0);
                    }
                    catch (final ParseException ex) {
                        AsynMDCustomDetailsUpdateTask.logger.log(Level.SEVERE, "Exception while parsing date", ex);
                        throw new SyMException(52001, "Date must be in MM/dd/yyyy format", "dc.mdm.device_mgmt.device.validate_date_format", (Throwable)null);
                    }
                }
                if (type.equalsIgnoreCase("Float")) {
                    try {
                        updateRow.set(columnName, (Object)Float.parseFloat((String)inputRow.get(columnName)));
                    }
                    catch (final NumberFormatException ex2) {
                        AsynMDCustomDetailsUpdateTask.logger.log(Level.SEVERE, "Exception while parsing price", ex2);
                        throw new SyMException(52002, "Purchase Price must be a valid number", "dc.mdm.device_mgmt.device.validate_decimal_format_price", (Throwable)null);
                    }
                }
            }
        }
    }
    
    static {
        AsynMDCustomDetailsUpdateTask.logger = Logger.getLogger("MDMEnrollment");
    }
}
