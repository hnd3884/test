package com.me.mdm.server.enrollment.adminenroll;

import java.util.Hashtable;
import com.adventnet.i18n.I18N;
import com.adventnet.sym.server.mdm.enroll.MDMEnrollmentUtil;
import com.adventnet.ds.query.DMDataSetWrapper;
import org.json.JSONException;
import com.adventnet.sym.server.mdm.core.ManagedUserHandler;
import java.util.List;
import java.util.Iterator;
import com.adventnet.persistence.DataObject;
import com.adventnet.ds.query.SelectQuery;
import com.me.mdm.core.enrollment.EnrollmentTemplateHandler;
import com.me.devicemanagement.framework.server.customer.CustomerInfoUtil;
import com.adventnet.sym.server.mdm.util.JSONUtil;
import com.me.mdm.core.enrollment.AdminEnrollmentHandler;
import com.adventnet.persistence.Row;
import org.json.JSONArray;
import java.util.ArrayList;
import com.me.devicemanagement.framework.server.csv.CSVProcessor;
import com.me.devicemanagement.framework.server.csv.CustomerParamsHandler;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.adventnet.ds.query.SortColumn;
import com.adventnet.ds.query.Range;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.me.mdm.server.util.MDMFeatureParamsHandler;
import com.me.devicemanagement.framework.server.util.DBUtil;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import java.util.logging.Level;
import org.json.simple.JSONObject;
import java.util.Properties;
import java.util.logging.Logger;
import com.me.devicemanagement.framework.server.csv.CSVTask;

public class AsynAssignUserTask extends CSVTask
{
    private static Logger logger;
    private static Logger assignUserLogger;
    private final int range = 50;
    private int enrollmentTemplate;
    private AssignUserCSVProcessor assignUserCSVProcessor;
    
    public AsynAssignUserTask() {
        this.assignUserCSVProcessor = null;
    }
    
    protected JSONObject getInputs(final Properties taskProps) throws Exception {
        this.customerID = Long.parseLong(((Hashtable<K, String>)taskProps).get("customerID"));
        this.enrollmentTemplate = Integer.parseInt(((Hashtable<K, String>)taskProps).get("EnrollmentTemplate"));
        this.assignUserCSVProcessor = this.getCSVProcessorForTemplate(this.enrollmentTemplate);
        final JSONObject jsonObj = new JSONObject();
        jsonObj.put((Object)"EnrollmentTemplate", (Object)this.enrollmentTemplate);
        AsynAssignUserTask.logger.log(Level.INFO, "In executeTask input json:{0}", jsonObj.toJSONString());
        return jsonObj;
    }
    
    protected void performOperation(final JSONObject json) throws Exception {
        try {
            final int totalRowCount = DBUtil.getRecordCount("AssignUserImportInfo", "ASSIGN_USER_ID", new Criteria(Column.getColumn("AssignUserImportInfo", "ERROR_REMARKS"), (Object)null, 0).and(new Criteria(Column.getColumn("AssignUserImportInfo", "CUSTOMER_ID"), (Object)this.customerID, 0)).and(new Criteria(Column.getColumn("AssignUserImportInfo", "TEMPLATE_TYPE"), (Object)this.enrollmentTemplate, 0)));
            final Boolean allowBulkReassignUser = MDMFeatureParamsHandler.getInstance().isFeatureEnabled("BULK_REASSIGN_USER");
            for (int j = 0; j <= totalRowCount / 50; ++j) {
                final SelectQuery sQuery = (SelectQuery)new SelectQueryImpl(new Table("AssignUserImportInfo"));
                sQuery.addSelectColumn(Column.getColumn("AssignUserImportInfo", "ASSIGN_USER_ID"));
                sQuery.addSelectColumn(Column.getColumn("AssignUserImportInfo", "USER_NAME"));
                sQuery.addSelectColumn(Column.getColumn("AssignUserImportInfo", "DOMAIN_NAME"));
                sQuery.addSelectColumn(Column.getColumn("AssignUserImportInfo", "EMAIL_ADDRESS"));
                sQuery.addSelectColumn(Column.getColumn("AssignUserImportInfo", "GROUP_NAME"));
                sQuery.addSelectColumn(Column.getColumn("AssignUserImportInfo", "DEVICE_NAME"));
                sQuery.addSelectColumn(Column.getColumn("AssignUserImportInfo", "IMEI"));
                sQuery.addSelectColumn(Column.getColumn("AssignUserImportInfo", "SERIAL_NUMBER"));
                sQuery.addSelectColumn(Column.getColumn("AssignUserImportInfo", "UDID"));
                sQuery.addSelectColumn(Column.getColumn("AssignUserImportInfo", "EXCHANGE_ID"));
                sQuery.addSelectColumn(Column.getColumn("AssignUserImportInfo", "TEMPLATE_TYPE"));
                sQuery.addSelectColumn(Column.getColumn("AssignUserImportInfo", "CUSTOMER_ID"));
                sQuery.setCriteria(new Criteria(Column.getColumn("AssignUserImportInfo", "ERROR_REMARKS"), (Object)null, 0).and(new Criteria(Column.getColumn("AssignUserImportInfo", "CUSTOMER_ID"), (Object)this.customerID, 0)).and(new Criteria(Column.getColumn("AssignUserImportInfo", "TEMPLATE_TYPE"), (Object)this.enrollmentTemplate, 0)));
                sQuery.setRange(new Range((j * 50 == 0) ? 0 : (j * 50 + 1), 50));
                sQuery.addSortColumn(new SortColumn(Column.getColumn("AssignUserImportInfo", "ASSIGN_USER_ID"), true));
                DataObject dobj = MDMUtil.getPersistence().get(sQuery);
                int processed = 0;
                final String countStr = CustomerParamsHandler.getInstance().getParameterValue(CSVProcessor.getProcessedLabel(this.assignUserCSVProcessor.operationLabel), (long)this.customerID);
                if (countStr != null) {
                    processed = Integer.parseInt(countStr);
                }
                do {
                    final Iterator<Row> iter = dobj.getRows("AssignUserImportInfo", new Criteria(Column.getColumn("AssignUserImportInfo", "ERROR_REMARKS"), (Object)null, 0));
                    final List<org.json.JSONObject> list = new ArrayList<org.json.JSONObject>();
                    JSONArray successList = new JSONArray();
                    JSONArray failedList = new JSONArray();
                    while (iter.hasNext()) {
                        final Row r = iter.next();
                        ++processed;
                        final org.json.JSONObject jsonObj = this.toJSON(r);
                        this.validateAndOpenExchangeColumn(json);
                        final org.json.JSONObject failedJSON = this.validateAssignUserColumns(jsonObj);
                        if (failedJSON.has("ErrorMsg")) {
                            failedList.put((Object)failedJSON);
                        }
                        else {
                            list.add(jsonObj);
                        }
                    }
                    if (allowBulkReassignUser) {
                        final JSONArray reAssignUserJSONArray = AdminEnrollmentHandler.getAlreadyManagedDevicesList(list);
                        final org.json.JSONObject reAssignResponseJSON = AdminEnrollmentHandler.changeUserFromCSV(reAssignUserJSONArray);
                        successList = reAssignResponseJSON.getJSONArray("SuccessList");
                        failedList = JSONUtil.mergeJSONArray(failedList, reAssignResponseJSON.getJSONArray("FailedList"));
                    }
                    final Long customerId = CustomerInfoUtil.getInstance().getCustomerId();
                    final org.json.JSONObject jsonObj = AdminEnrollmentHandler.assignUser(list, this.userID, this.enrollmentTemplate, "ASSIGN_USER_ID", new EnrollmentTemplateHandler().getPlatformForTemplate(this.enrollmentTemplate));
                    final JSONArray tempSuccessList = jsonObj.getJSONArray("SuccessList");
                    final JSONArray tempFailedList = jsonObj.getJSONArray("FailedList");
                    for (int i = 0; i < tempSuccessList.length(); ++i) {
                        successList.put((Object)JSONUtil.optLongForUVH(tempSuccessList, i, -1L));
                    }
                    for (int i = 0; i < tempFailedList.length(); ++i) {
                        failedList.put((Object)tempFailedList.getJSONObject(i));
                    }
                    if (successList.length() != 0) {
                        final Long[] successArray = new Long[successList.length()];
                        for (int k = 0; k < successList.length(); ++k) {
                            successArray[k] = (Long)successList.get(k);
                        }
                        dobj.deleteRows("AssignUserImportInfo", new Criteria(Column.getColumn("AssignUserImportInfo", "ASSIGN_USER_ID"), (Object)successArray, 8));
                    }
                    for (int i = 0; i < failedList.length(); ++i) {
                        final org.json.JSONObject failureRowJSON = failedList.getJSONObject(i);
                        final Long assignUserID = failureRowJSON.getLong("ASSIGN_USER_ID");
                        final String errorMsg = String.valueOf(failureRowJSON.get("ErrorMsg"));
                        final Row assignUserRow = dobj.getRow("AssignUserImportInfo", new Criteria(Column.getColumn("AssignUserImportInfo", "ASSIGN_USER_ID"), (Object)assignUserID, 0));
                        assignUserRow.set("ERROR_REMARKS", (Object)errorMsg);
                        dobj.updateRow(assignUserRow);
                    }
                    MDMUtil.getPersistence().update(dobj);
                    CustomerParamsHandler.getInstance().addOrUpdateParameter(CSVProcessor.getProcessedLabel(this.assignUserCSVProcessor.operationLabel), String.valueOf(processed), (long)this.customerID);
                    dobj = MDMUtil.getPersistence().get(sQuery);
                } while (!dobj.isEmpty());
                this.setFailureCount(this.customerID);
            }
        }
        catch (final Exception ex) {
            AsynAssignUserTask.assignUserLogger.log(Level.SEVERE, "Exception in sendMultipleEnrollmentRequests ", ex);
            throw ex;
        }
    }
    
    private org.json.JSONObject toJSON(final Row r) throws JSONException {
        final org.json.JSONObject json = new org.json.JSONObject();
        json.put("ASSIGN_USER_ID", r.get("ASSIGN_USER_ID"));
        CustomerInfoUtil.getInstance();
        if (CustomerInfoUtil.isSAS()) {
            if (r.get("EMAIL_ADDRESS") != null && !((String)r.get("EMAIL_ADDRESS")).isEmpty() && !((String)r.get("EMAIL_ADDRESS")).equalsIgnoreCase("--")) {
                final org.json.JSONObject userJSON = new org.json.JSONObject();
                userJSON.put("USER_IDENTIFIER", (Object)"EMAIL_ADDRESS");
                userJSON.put("EMAIL_ADDRESS", (Object)r.get("EMAIL_ADDRESS"));
                userJSON.put("DOMAIN_NETBIOS_NAME", (Object)"MDM");
                userJSON.put("CUSTOMER_ID", (Object)this.customerID);
                final org.json.JSONObject userDetailsJson = ManagedUserHandler.getInstance().getManagedUserDetails(userJSON);
                if (userDetailsJson != null && userDetailsJson.has("NAME")) {
                    this.addToJson(json, "UserName", String.valueOf(userDetailsJson.get("NAME")));
                    this.addToJson(json, "DomainName", String.valueOf(userDetailsJson.get("DOMAIN_NETBIOS_NAME")));
                }
                else if (r.get("USER_NAME") == null || ((String)r.get("USER_NAME")).isEmpty() || ((String)r.get("USER_NAME")).equalsIgnoreCase("--")) {
                    this.addToJson(json, "UserName", ((String)r.get("EMAIL_ADDRESS")).split("@")[0]);
                    this.addToJson(json, "DomainName", (String)r.get("DOMAIN_NAME"));
                }
                else {
                    this.addToJson(json, "UserName", (String)r.get("USER_NAME"));
                    this.addToJson(json, "DomainName", (String)r.get("DOMAIN_NAME"));
                }
            }
            else {
                this.addToJson(json, "UserName", (String)r.get("USER_NAME"));
                this.addToJson(json, "DomainName", (String)r.get("DOMAIN_NAME"));
            }
        }
        else {
            this.addToJson(json, "UserName", (String)r.get("USER_NAME"));
            this.addToJson(json, "DomainName", (String)r.get("DOMAIN_NAME"));
        }
        this.addToJson(json, "DomainName", (String)r.get("DOMAIN_NAME"));
        this.addToJson(json, "EmailAddr", (String)r.get("EMAIL_ADDRESS"));
        this.addToJson(json, "GroupName", (String)r.get("GROUP_NAME"));
        this.addToJson(json, "DeviceName", (String)r.get("DEVICE_NAME"));
        this.addToJson(json, "IMEI", (String)r.get("IMEI"));
        this.addToJson(json, "SerialNumber", (String)r.get("SERIAL_NUMBER"));
        this.addToJson(json, "UDID", (String)r.get("UDID"));
        this.addToJson(json, "EASID", (String)r.get("EXCHANGE_ID"));
        json.put("CustomerId", r.get("CUSTOMER_ID"));
        return json;
    }
    
    private void setFailureCount(final long customerID) throws Exception {
        DMDataSetWrapper ds = null;
        try {
            final SelectQuery sQuery = (SelectQuery)new SelectQueryImpl(new Table("AssignUserImportInfo"));
            final Column countColumn = Column.getColumn("AssignUserImportInfo", "ASSIGN_USER_ID").count();
            countColumn.setColumnAlias("ASSIGN_USER_ID");
            sQuery.addSelectColumn(countColumn);
            sQuery.setCriteria(new Criteria(Column.getColumn("AssignUserImportInfo", "ERROR_REMARKS"), (Object)null, 1).and(new Criteria(Column.getColumn("AssignUserImportInfo", "CUSTOMER_ID"), (Object)customerID, 0)).and(new Criteria(Column.getColumn("AssignUserImportInfo", "TEMPLATE_TYPE"), (Object)this.enrollmentTemplate, 0)));
            ds = DMDataSetWrapper.executeQuery((Object)sQuery);
            ds.next();
            final JSONObject jsonObj = new JSONObject();
            jsonObj.put((Object)CSVProcessor.getFailedLabel(this.assignUserCSVProcessor.operationLabel), (Object)String.valueOf(ds.getValue("ASSIGN_USER_ID")));
            jsonObj.put((Object)CSVProcessor.getStatusLabel(this.assignUserCSVProcessor.operationLabel), (Object)"COMPLETED");
            CustomerParamsHandler.getInstance().addOrUpdateParameters(jsonObj, customerID);
            AsynAssignUserTask.logger.info("Persisted failure count in Customer Params");
        }
        catch (final Exception ex) {
            throw ex;
        }
    }
    
    private void addToJson(final org.json.JSONObject json, final String columnName, final String columnValue) throws JSONException {
        if (columnValue != null && !columnValue.equals("") && !columnValue.equals(" ") && !columnValue.equals("--")) {
            json.put(columnName, (Object)columnValue);
        }
    }
    
    private AssignUserCSVProcessor getCSVProcessorForTemplate(final int enrollmentTemplate) {
        switch (enrollmentTemplate) {
            case 11: {
                return new AppleConfigAssignUserCSVProcessor();
            }
            case 20: {
                return new AndroidNFCAssignUserCSVProcessor();
            }
            case 21: {
                return new KnoxAssignUserCSVProcessor();
            }
            case 30: {
                return new WindowsWICDAssignUserCSVProcessor();
            }
            case 22: {
                return new AndroidQRAssignUserCSVProcessor();
            }
            case 23: {
                return new AndroidZeroTouchAssignUserCSVProcessor();
            }
            case 31: {
                return new WinLaptopEnrollmentAssignUserCSVProcessor();
            }
            case 40: {
                return new GSuiteChromeAssignUserCSVProcessor();
            }
            case 32: {
                return new WinAzureADAssignUserCSVProcessor();
            }
            case -1: {
                return new MultipleTemplateAssignUserCSVProcessor();
            }
            case 12: {
                return new ModernMacManagementAssignUserCSVProcessor();
            }
            case 33: {
                return new WinModernMgmtAssignUserCSVProcessor();
            }
            default: {
                return null;
            }
        }
    }
    
    private boolean isEmpty(final JSONObject json, final String key) {
        return !json.containsKey((Object)key) || json.get((Object)key) == null || ((String)json.get((Object)key)).isEmpty() || ((String)json.get((Object)key)).equals("--");
    }
    
    private void validateAndOpenExchangeColumn(final JSONObject json) {
        if ((this.enrollmentTemplate == 11 || this.enrollmentTemplate == -1) && this.isEmpty(json, "IMEI") && this.isEmpty(json, "SerialNumber") && !this.isEmpty(json, "EASID")) {
            MDMUtil.updateSyMParameter("showExchangeColumn", "true");
        }
    }
    
    private org.json.JSONObject validateAssignUserColumns(final org.json.JSONObject inputJson) throws Exception {
        final org.json.JSONObject failureJSON = new org.json.JSONObject();
        final JSONArray invalidEntries = new JSONArray();
        final MDMEnrollmentUtil enrollmentUtil = new MDMEnrollmentUtil();
        if (enrollmentUtil.isValuePresentInJSON(inputJson, "UserName") && !enrollmentUtil.isValidInputs(inputJson.getString("UserName"), "UserName")) {
            invalidEntries.put((Object)"USER_NAME");
        }
        if (enrollmentUtil.isValuePresentInJSON(inputJson, "DomainName") && !enrollmentUtil.isValidInputs(inputJson.getString("DomainName"), "DomainName")) {
            invalidEntries.put((Object)"DOMAIN_NAME");
        }
        if (enrollmentUtil.isValuePresentInJSON(inputJson, "EmailAddr") && !enrollmentUtil.isValidInputs(inputJson.getString("EmailAddr"), "EmailAddr")) {
            invalidEntries.put((Object)"EMAIL_ADDRESS");
        }
        if (enrollmentUtil.isValuePresentInJSON(inputJson, "GroupName") && !enrollmentUtil.isValidInputs(inputJson.getString("GroupName"), "group_name")) {
            invalidEntries.put((Object)"GROUP_NAME");
        }
        if (enrollmentUtil.isValuePresentInJSON(inputJson, "DeviceName") && !enrollmentUtil.isValidInputs(inputJson.getString("DeviceName"), "DeviceName")) {
            invalidEntries.put((Object)"DEVICE_NAME");
        }
        if (enrollmentUtil.isValuePresentInJSON(inputJson, "IMEI") && !enrollmentUtil.isValidInputs(inputJson.getString("IMEI"), "IMEI")) {
            invalidEntries.put((Object)"IMEI");
        }
        if (enrollmentUtil.isValuePresentInJSON(inputJson, "SerialNumber") && !enrollmentUtil.isValidInputs(inputJson.getString("SerialNumber"), "SerialNumber")) {
            invalidEntries.put((Object)"SERIAL_NUMBER");
        }
        if (enrollmentUtil.isValuePresentInJSON(inputJson, "UDID") && !enrollmentUtil.isValidInputs(inputJson.getString("UDID"), "UDID")) {
            invalidEntries.put((Object)"UDID");
        }
        if (enrollmentUtil.isValuePresentInJSON(inputJson, "EASID") && !enrollmentUtil.isValidInputs(inputJson.getString("EASID"), "EASID")) {
            invalidEntries.put((Object)"EXCHANGE_ID");
        }
        final String invalidEntriesString = JSONUtil.getInstance().convertJSONArrayToString(invalidEntries);
        if (invalidEntries.length() > 0) {
            failureJSON.put("ASSIGN_USER_ID", inputJson.get("ASSIGN_USER_ID"));
            failureJSON.put("ErrorMsg", (Object)I18N.getMsg("mdm.csv.invalid_inputs", new Object[] { invalidEntriesString.substring(0, invalidEntriesString.length() - 1) }));
        }
        return failureJSON;
    }
    
    static {
        AsynAssignUserTask.logger = Logger.getLogger("MDMEnrollment");
        AsynAssignUserTask.assignUserLogger = Logger.getLogger("MDMAssignUser");
    }
}
